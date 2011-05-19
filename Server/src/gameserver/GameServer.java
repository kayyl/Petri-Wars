package gameserver;

import gameserver.exceptions.ServerShuttingDownException;
import gameserver.logging.GSFormatter;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GameServer implements IGameServer {
	private static final Logger LOG = Logger.getLogger("GameServer");
	private static final String SERVER_VERSION = "0.00 alpha";
	//////UDP config//////
	private static final int NUMSUBNETS_MULTICAST = 1;
	private static final int SUBNETOFFSET_MULTICAST = 21;
	
	public static void main(String[] args) {
		LinkedList<String> argbuffer = new LinkedList<String>();
		Collections.addAll(argbuffer, args);
		
		boolean matMode = false;
		
		try {
			while(!argbuffer.isEmpty()){
				String arg = argbuffer.pop();
				if (arg.equalsIgnoreCase("--help") || arg.equalsIgnoreCase("-?")){
					System.out.println(
					"Format: java gameserver.GameServer [args]\n" +
					"\n" +
					"Options:\n" +
					"\t--internal -i		Start into maintainence mode, connecting to a running server.\n"
					);
					return;
					
				} else if (arg.equalsIgnoreCase("--internal") || arg.equalsIgnoreCase("-i")){
					matMode = true;
				} 
			}
		}
		catch (NumberFormatException e) {
			System.out.printf("Invalid number argument");
			return;
		}
		
		if (matMode){
			
		} else {
			initializeLoggers();
			startServer();
		}
	}
	
	/////////////////////////////////////////////////////////////////////////////
	//                           Server Bookkeeping                            //
	/////////////////////////////////////////////////////////////////////////////
	private static long starttime = 0;
	
	private static volatile boolean running = true;
	public static boolean isRunning(){
		return running;
	}
	
	private static int unusedClientID = 100;
	protected static int getNewClientNumber(){
		return ++unusedClientID;
	}
	
	private static boolean unusedMulticast[] = new boolean[256*NUMSUBNETS_MULTICAST];
	public static InetAddress getUnusedMulticastAddress(){
		byte a = (byte) 231;
		byte b = 21;
		byte c = 0;
		byte d = 0;
		for (int i = 0; i < unusedMulticast.length; i++){
			if (unusedMulticast[i]) continue;
			
			c = (byte) (SUBNETOFFSET_MULTICAST + Math.floor(i / 256));
			d = (byte) (i % 256);
			unusedMulticast[i] = true;
			try {
				return InetAddress.getByAddress(new byte[]{a, b, c, d});
			} catch (UnknownHostException e) {
				LOG.severe("Could not get a Multicast Address! "+e.getMessage());
				return null;
			}
		}
		LOG.severe("Could not get a free Multicast Address!");
		return null;
	}
	public static void releaseMulticastAddress(InetAddress add){
		int usedaddress = (add.getAddress()[2] - SUBNETOFFSET_MULTICAST) * add.getAddress()[3];
		if (usedaddress < 0  || usedaddress > unusedMulticast.length ){
			LOG.severe("Tried to free unreserved address: "+add.getHostAddress());
		}
		unusedMulticast[usedaddress] = false;
	}
	
	private static HashSet<ClientManager> clientlist = new HashSet<ClientManager>();
	public static boolean addClient(ClientManager c) throws ServerShuttingDownException{
		synchronized (clientlist){ 
			/* Synchronize on the clientlist, since that's what we're modifying.
			 * This also allows other threads to access other methods during this. */
			
			if (!running) throw new ServerShuttingDownException(); 
			/* We throw this AFTER we get into the synchronized block so that any
			 * threads waiting on this block will find it as soon as they get in.
			 * Putting it before the block would not affect waiting threads.*/
			
			if (clientlist.add(c)){
				if (c.getClientID().getID() == -1){
					c.getClientID().setID(getNewClientNumber());
				}
				return true;
			} else {
				return false;
			}
		}
	}
	public static List<ClientManager> getAllFreeClients() throws ServerShuttingDownException{
		synchronized (clientlist){
			if (!running) throw new ServerShuttingDownException();
			ArrayList<ClientManager> lst = new ArrayList<ClientManager>();
			for (ClientManager cm: clientlist){
				if (cm.getGame() == null){
					lst.add(cm);
				}
			}
			return lst;
		}
	}
	public static boolean removeClient(ClientManager c) throws ServerShuttingDownException{
		synchronized (clientlist){
			if (!running) throw new ServerShuttingDownException();
			
			if (clientlist.remove(c)){
				return true;
			} else {
				return false;
			}
		}
	}
	
	
	private static int unusedGameID = 10;
	protected static int getNewGameNumber(){
		return ++unusedGameID;
	}
	
	private static HashSet<AbstractGame> gamelist;
	public static boolean addGame(AbstractGame g) throws ServerShuttingDownException{
		synchronized (gamelist){
			/* Synchronize on the gamelist, since that's what we're modifying.
			 * This also allows other threads to access other methods during this. */
			
			if (!running) throw new ServerShuttingDownException();
			/* We throw this AFTER we get into the synchronized block so that any
			 * threads waiting on this block will find it as soon as they get in.
			 * Putting it before the block would not affect waiting threads.*/
			
			if (gamelist.add(g)){
				if (g.getGameID().getID() == -1){
					g.getGameID().setID(getNewGameNumber());
				}
				return true;
			} else {
				return false;
			}
		}
	}
	
	/////////////////////////////////////////////////////////////////////////////
	//                            Front-end Server                             //
	/////////////////////////////////////////////////////////////////////////////
	private static ServerSocket serve;
	
	private static void startServer(){
		try {
			LOG.info("Starting GameServer, version "+SERVER_VERSION);
			internal = new ServerSocket(INTERNAL_PORT);
			LOG.info("Server has opened the maintenance port "+INTERNAL_PORT+" successfully.");
			serve = new ServerSocket(SERVERPORT);
			LOG.info("Server Started with "+serve.toString());
		} catch (IOException e) {
			LOG.severe("Could not open internal or serve port; either may already be in use " +
					"or a second instance of the Game Server may already be running. " +
					"Shutting down.");
			try {
				if (internal != null) internal.close();
				if (serve != null) serve.close();
			} catch (IOException e1) {}
			System.exit(-1);
		}
		AbstractGameManager.loadGameManagers();
		Runtime.getRuntime().addShutdownHook(new ShutdownThread());
		starttime = System.currentTimeMillis();
		new AccepterThread().start();
		new MaintainenceThread().start();
		LOG.info("Main method has completed. Server is now running.");
	}
	
	private static void shutdownServer(){
		System.exit(0);
	}

	private static void initializeLoggers(){
		Logger rootlog = Logger.getLogger("");
		
		rootlog.setLevel(Level.ALL);
		
		for (Handler h : rootlog.getHandlers()){ //remove all handlers
			rootlog.removeHandler(h);
		}
		//setup console log
		ConsoleHandler ch = new ConsoleHandler();
		ch.setFormatter(new GSFormatter());
		ch.setLevel(Level.ALL);
		rootlog.addHandler(ch);
		
		//setup logfile log
		try {
			FileHandler fh = new FileHandler("/logs/server.%g.%u.log", 1000*1000, 5, true);
			fh.setFormatter(new GSFormatter());
			rootlog.addHandler(fh);
		} catch (SecurityException e) {
			LOG.throwing("GameServer", "initilizeLoggers", e);
		} catch (IOException e) {
			LOG.throwing("GameServer", "initilizeLoggers", e);
		}
	}
	
	public static class ShutdownThread extends Thread {
		@Override public void run() {
			LOG.severe("Shutdown process initiated. Server is now not running.");
			running = false;
			LOG.info("Notifying all clients to the shutdown.");
			for (ClientManager cm: clientlist){
				try {
					cm.shutdownNow();
				} catch (Exception e) {}
			}
			LOG.info("Shutting down all games now.");
			for (AbstractGame g: gamelist){
				try {
					g.shutdownNow();
				} catch (Exception e){}
			}
			LOG.info("Shutting down game managers.");
			AbstractGameManager.unloadGameManagers();
			LOG.info("Clearing all resources.");
			try {serve.close();} catch (IOException e) {}
			try {internal.close();} catch (IOException e) {}
			serve = null; internal = null;
			clientlist.clear(); clientlist = null;
			gamelist.clear(); gamelist = null;
			LOG.info("Final uptime report: "+getUptime());
			LOG.info("Running garbage collector. Final shutdown.");
			System.gc();
		}
	}
	
	private static class AccepterThread extends Thread{
		@Override public void run() {
			try {
				while(running){
					try {
						Socket tempSocket = serve.accept();  // accept connection
						//give connecting client the server info
						LOG.info("Accepted connection from "+tempSocket.getInetAddress().getHostAddress());
						if (!running){
							LOG.info("Accepted connection dropped because server is shutting down!");
							tempSocket.close();
						} else {
							new SorterThread(tempSocket).start();
						}
					} catch(Exception e) {
						
					}
				} //while
/*			} catch(ThreadDeath t){
				LOG.info("Caught ThreadDeath Exception. Closing all active games and shutting down.");
				throw t;
*/			} finally {
				//closing the accepter thread means closing the server down. Shutdown!
				shutdownServer();
			}
		}		
	}
	
	private static class SorterThread extends Thread {
		private Socket tempSocket;
		public SorterThread(Socket newsock) {
			tempSocket = newsock;
		}
		
		@Override public void run() {
			try {
				//give connecting client the server info
				PrintWriter tempWriter =
					new PrintWriter(
					new BufferedWriter(
					new OutputStreamWriter(
						tempSocket.getOutputStream())), true);
				tempWriter.println("GameServer "+SERVER_VERSION+" by Tustin2121");
				
				BufferedReader tempReader = 
					new BufferedReader(
					new InputStreamReader(tempSocket.getInputStream()));
				String str = tempReader.readLine();
				//Expects a response in this exact format: 
				//[GAMENAME] [VERSION] (Java|[OTHER]) [OS] (32|64)
				LOG.finer("Got str: "+str);
				StringTokenizer st = new StringTokenizer(str, " \n\r");
				
				String gamename = st.nextToken();
				String clientver = st.nextToken();
				String clientlang = st.nextToken();
				String osver = st.nextToken();
				String arch = st.nextToken();
				//if it doesn't return a format string of that exact type, we discard it
				if (st.hasMoreTokens()) throw new NoSuchElementException();
				
				ClientManager cm;
				if (clientlang.equalsIgnoreCase("java")){
					cm = new ClientManager.JavaCM(tempSocket);
				} else {
					cm = new ClientManager.GenericCM(tempSocket);
				}
				LOG.finer("Asking for ClientID");
				if (cm.retrieveClientID() == null){
					//client has not responded with properid
					return;
				}
				
				AbstractGameManager gm = AbstractGameManager.getGameManager(gamename);
				if(gm != null) {
					gm.addClient(cm);
				} else {
					cm.sendMessage(new ServerGram(IGameServer.MSGTYPE_DISCONNECT, "No Such Game!"));
					cm.getSocket().close();
					return;
				}
				cm.start();
				GameServer.addClient(cm);
			} catch (NoSuchElementException e){
				try {tempSocket.close();} catch (IOException e1) {}
			} catch (IOException e) {
				try {tempSocket.close();} catch (IOException e1) {}
			} catch (ServerShuttingDownException e) {
				e.printStackTrace();
			} finally {}
		}
	}
	
	/////////////////////////////////////////////////////////////////////////////
	//                          Back-end Maintenance                           //
	/////////////////////////////////////////////////////////////////////////////
	static final int INTERNAL_PORT = 12122;
	
	private static final String MAT_STATUS = "STATUS"; 
	
	private static ServerSocket internal;
	
	/**
	 * This method opens a socket to the INTERNAL_PORT and asks the Game Server running
	 * on it a question, given in the parameter.
	 */
	private static void pingRunningServer(String msg){
		try {
			Socket s = new Socket("localhost", INTERNAL_PORT);
			new PrintWriter(s.getOutputStream()).println(msg);
			
			try {
				int size;
				byte[] b = new byte[100];
				while (true){
					size = s.getInputStream().read(b);
					if (size < 0) break;
					System.out.write(b, 0, size);
				}
			} catch (IOException e){}//Socket Finished
		} catch (UnknownHostException e) { 
			e.printStackTrace(); //never happens - this is LOCALHOST!
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private static class MaintainenceThread extends Thread{
		@Override public void run() {
			while(running){
				try {
					Socket s = internal.accept();
					new ManagerHandlerThread(s).start();
					
				} catch (IOException e){
					
				}
			}
		}
	}
	
	private static class ManagerHandlerThread extends Thread {
		private Socket sock; 
		public ManagerHandlerThread(Socket s) {
			sock = s;
		}
		
		@Override public void run() {
			BufferedReader read;
			try {
				read = new BufferedReader(new InputStreamReader(sock.getInputStream()));
				PrintWriter out = new PrintWriter(sock.getOutputStream());
				
				while (!sock.isClosed()){
					String input = read.readLine();
					
					if (input.matches("(?i)help|\\?")){
						out.println("Accepted commands:\n" +
								"	logout\n" +
								"	status\n" +
								"	shutdown\n");
					} else if (input.matches("(?i)logout|quit|exit")){
						sock.close();
					} else if (input.matches("(?i)status|st|uptime")){
						out.println(
								"Digiplex GameServer, version "+SERVER_VERSION+"\n" +
								"Uptime: "+ getUptime() + "\n" +
								"Clients Connected: "+clientlist.size() + 
								"Games Running: "+(0) //TODO
						);
					} else if (input.matches("(?i)shutdown")){
						GameServer.shutdownServer();
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	private static String getUptime(){
		//This code gotten from http://www.coderanch.com/t/378404/Java-General/java/Convert-milliseconds-time
		//Thanks JavaRanch! :D
		long elapsedTime = System.currentTimeMillis() - starttime;
	    String format = String.format("%%0%dd", 2);
	    elapsedTime = elapsedTime / 1000;
	    String seconds = String.format(format, elapsedTime % 60);
	    String minutes = String.format(format, (elapsedTime % 3600) / 60);
	    String hours = String.format(format, elapsedTime / 3600);
	    return hours +":"+ minutes +":"+ seconds;
	}
}
