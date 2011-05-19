package gameserver;

import gameserver.exceptions.CredentialsRequiredException;
import gameserver.exceptions.ServerShuttingDownException;
import gameserver.ids.ClientID;
import gameserver.util.ByteBreaker;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.Arrays;
import java.util.logging.Logger;

public abstract class ClientManager extends Thread implements IGameServer {
	private static final Logger LOG = Logger.getLogger("server.ClientManager");
	
	protected ClientID cid;
	protected Socket csocket;
	protected AbstractGame game;
	protected AbstractGameManager gmanager;
	
	protected String getLoggableID(){
		if (cid != null) return Integer.toString(cid.getID()) + ":"+cid.getName();
		return super.toString();
	}
	
	private boolean running;
	public boolean isRunning() {return running;}
	
	public ClientManager() {
		cid = new ClientID(null, GameServer.getNewClientNumber());
	}
	
	public void shutdownNow() throws IOException{
		running = false;
		sendMessage(new ServerGram(MSGTYPE_SERVERSHUTDOWN, null));
		csocket.close();
	}
	
	//public void setClientID(ClientID cid) {this.cid = cid;}
	public ClientID getClientID() {return cid;}
	
	//public void setSocket(Socket csocket) {this.csocket = csocket;}
	public Socket getSocket() {return csocket;}
	
	public void setGame(AbstractGame game) {
		if (game != null && this.game != null) throw new IllegalStateException(
				"Cannot set game on client when client is already in a game.");
		this.game = game;
	}
	public AbstractGame getGame() {return game;}
	
	public void setGameManager(AbstractGameManager gmanager) {this.gmanager = gmanager;}
	public AbstractGameManager getGameManager() {return gmanager;}
	
		
	public abstract boolean sendMessage(ServerGram message);
	public abstract ServerGram receiveMessage() throws EOFException;
	
	public ServerGram receiveTimedMessage(int mswait) throws EOFException {
		try {
			int orgtimeout = csocket.getSoTimeout();
			csocket.setSoTimeout(mswait);
			ServerGram sg = this.receiveMessage();
			csocket.setSoTimeout(orgtimeout);
			return sg;
		} catch (SocketException e) {
			LOG.severe("["+getLoggableID()+"] SocketException: "+e.getMessage());
			return null;
		}
		
	}
	
	public ClientID retrieveClientID(){
		// get clientID from client
		int attemptCount = 3;
		while (true){
			ServerGram sg = new ServerGram(MSGTYPE_CREDENTIALS, null);
			this.sendMessage(sg);
			try {
				sg = this.receiveTimedMessage(15000);
			} catch (EOFException e1) {
				return null;
			}
			
			if (sg == null || sg.getBodyClass() != ClientID.class){
				LOG.finer("Message does not have ClientID body");
				attemptCount--;
				if (attemptCount > 0) continue;
				LOG.finer("Sending DISCONNECT!");
				this.sendMessage(new ServerGram(MSGTYPE_DISCONNECT, "No Credentials"));
				try { csocket.close(); } catch (IOException e) {}
				return null;
			}
			ClientID newid = (ClientID) sg.getBody();
			cid.setName(newid.getName());
			return cid;
		}
	}

	public boolean sendException(Exception e){
		LOG.fine("["+getLoggableID()+"] Sending Exception: "+e.getClass().getName());
		return sendMessage(new ServerGram(MSGTYPE_CANNOT, e));
	}

	@Override
	public void run() {
		running = true;
		LOG.finest("ClientManager entering run loop");
		try {
			while (running && this.csocket.isConnected()){
				try {
					ServerGram gram = this.receiveMessage();
					if (gram == null) break;
					
					//If we don't have the ClientID yet, throw an exception at them
	//				if (cid == null && gram.getType() != MSGTYPE_CREDENTIALS){
	//					sendException(new CredentialsRequiredException());
	//					continue;
	//				}
					//else, handle depending on the type
					if (gram.getType() > 49){
						if (game != null){
							game.pushMessage(this, gram);
						} else {
							//sendException(new NotInGameException());
							gmanager.pushMessage(this, gram);
						}
					} else if (gram.getType() == MSGTYPE_CHAT){ //MSGTYPE_CHAT = 49
						IGameServer.ChatData cd = (ChatData) gram.getBody();
						cd.name = cid.getName();
						gram = new ServerGram(MSGTYPE_CHAT, cd);
						if (game != null){
							game.handleChat(this, gram);
						} else {
							gmanager.pushChat(this, gram);
							//handleMessage(gram);
						}
					} else {
						if (!this.handleMessage(gram)){
							gmanager.pushMessage(this, gram);
						}
					}
				} finally {}
			}
		} catch (EOFException e){
			LOG.warning("["+getLoggableID()+"] Client threw EOF, closing out.");
		}
		if (game != null) game.removePlayer(this);
		if (gmanager != null) gmanager.removeClient(this);
		try {GameServer.removeClient(this);} catch (ServerShuttingDownException e) {}
	}
	
	/**
	 * 
	 * @param gram
	 * @return True if message has been handled, false otherwise
	 */
	protected boolean handleMessage(ServerGram gram){
		switch (gram.getType()){
		case MSGTYPE_TIME:
			sendMessage(new ServerGram(MSGTYPE_TIME, new Long(System.currentTimeMillis())));
			return true;
		case MSGTYPE_CREDENTIALS:
			if (!(gram.getBody() instanceof ClientID)){
				sendException(new CredentialsRequiredException());
			}
			ClientID c = (ClientID)gram.getBody();
			return true;
		default: return false; //ignore
		}
	}

	////////////////////Implementations///////////////////////
	
	public static class JavaCM extends ClientManager {
		protected ObjectInputStream oin;
		protected ObjectOutputStream oout;
		
		public JavaCM(Socket socket){
			this.csocket = socket;
			try {
				oin = new ObjectInputStream(socket.getInputStream());
				oout = new ObjectOutputStream(socket.getOutputStream());
			} catch (IOException e) {e.printStackTrace();}
			
			
		}
		
		@Override public boolean sendMessage(ServerGram message){
			try {
				oout.writeObject(message);
				return true;
			} catch (IOException e) {
				LOG.fine("["+getLoggableID()+"] IOException while sending message: "+e.getMessage());
				return false;
			}
		}
		
		@Override public ServerGram receiveMessage() throws EOFException {
			try {
				return (ServerGram)oin.readObject();
			} catch (SocketTimeoutException e){
				LOG.finest("["+getLoggableID()+"] SocketTimeoutException: "+e.getMessage());
			} catch (SocketException e){
				LOG.finer("["+getLoggableID()+"] SocketException: "+e.getMessage());
			} catch (EOFException e){
				LOG.info("["+getLoggableID()+"] EOFException: end of stream");
				throw e;
			} catch (IOException e){
				LOG.fine("["+getLoggableID()+"] IOException: "+e.getMessage());
			} catch (ClassNotFoundException e){
				LOG.severe("["+getLoggableID()+"] ClassNotFoundException: "+e.getMessage());
			}
			return null;
		}
	}
	
	public static class GenericCM extends ClientManager {
		protected BufferedInputStream oin;
		protected BufferedOutputStream oout;
		
		public GenericCM(Socket socket){
			this.csocket = socket;
			try {
				oin = new BufferedInputStream(socket.getInputStream());
				oout = new BufferedOutputStream(socket.getOutputStream());
			} catch (IOException e) {e.printStackTrace();}
			
			//TODO get clientID from client
		}
		
		@Override public boolean sendMessage(ServerGram message){
			try {
				LOG.finest("["+getLoggableID()+"] Sending message: "+message.getType());
				byte bstruct[] = message.toByteStructure();
				oout.write(bstruct);
				oout.flush();
				return true;
			} catch (IOException e) {
				LOG.warning("["+getLoggableID()+"] IOException while sending message: "+e.getMessage());
				return false;
			}
		}

		@Override public ServerGram receiveMessage() throws EOFException {
			try {
				byte[] data = new byte[1024];
				LOG.finest("Receiving message...");
				int res = oin.read(data);
				if (res == -1) throw new EOFException("Socket reached end of stream.");
				ByteBreaker bb = new ByteBreaker(data);
				byte type = bb.parseByte();
				Object body = convertToObject(type, bb);
				if (body == null){
					byte[] ba = bb.toByteArray();
					body = Arrays.copyOfRange(ba, 1, ba.length-1);
				}
				ServerGram sg = new ServerGram(type, (Serializable)body);
				
				LOG.finest("Received message with type: "+sg.getType());
				return sg;
			} catch (SocketTimeoutException e){
				LOG.finest("["+getLoggableID()+"] SocketTimeoutException: "+e.getMessage());
			} catch (SocketException e){
				LOG.warning("["+getLoggableID()+"] SocketException: "+e.getMessage());
			} catch (EOFException e){
				LOG.info("["+getLoggableID()+"] EOFException: end of stream");
				throw e;
			} catch (IOException e){
				LOG.info("["+getLoggableID()+"] IOException: "+e.getMessage());
			}
			LOG.finer("receiveMessage is returning null.");
			return null;
		}
		
		private Object convertToObject(int msgtype, ByteBreaker bb){
			switch(msgtype){
			case IGameServer.MSGTYPE_CREDENTIALS:{ //turn body into ClientID
				int id = bb.parseInt();
				String name = bb.parseString();
				ClientID cid = new ClientID(name, id);
				return cid;
			}
			case IGameServer.MSGTYPE_CHAT:{
				String name = bb.parseString();
				String message = bb.parseString();
				
				IGameServer.ChatData cd = new IGameServer.ChatData(name, message);
				return cd;
			}
			default:
				return null;
			}
		}
	}
}
