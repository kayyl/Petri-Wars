package gameserver;

import gameserver.ids.ClientID;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InvalidClassException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Logger;

/**
 * This class is the base class for any GameManager. There is one GameManager for every
 * type of game on the server. The GameManager gets clients immedeately after they log
 * into the server. It is their job to manage clients not in a game and to arrange games
 * between clients (ie, handle the server room). 
 * @author tpittman
 */
public abstract class AbstractGameManager {
	private static final Logger LOG = Logger.getLogger("AbstractGameManager");
	
	private static HashMap<String, AbstractGameManager> managerlist = new HashMap<String, AbstractGameManager>();
	public static AbstractGameManager getGameManager(String id){
		return managerlist.get(id);
	}
	
	public static void loadGameManagers(){
		LOG.fine("Loading Game Manager List");
		try {
			ClassLoader cl = ClassLoader.getSystemClassLoader();
			BufferedReader list = new BufferedReader(new FileReader("managerlist.txt"));
			String linein;
			while ((linein = list.readLine()) != null){
				if (linein.charAt(0) == '#') continue;
				if (linein.isEmpty()) continue;
				try {
					Class<?> mng = cl.loadClass(linein);
					if (mng.getSuperclass() != AbstractGameManager.class) 
						throw new InvalidClassException("Specified class is not subclass of AbstractGameManager!");
					AbstractGameManager agm = (AbstractGameManager) mng.newInstance();
					managerlist.put(agm.getGameManagerName(), agm);
					LOG.info("Game Manager "+linein+" Successfully Loaded");
				} catch (ClassNotFoundException e) {
					LOG.severe("Could not load class \""+linein+"\": " + e.getMessage());
				} catch (InvalidClassException e) {
					LOG.severe("Could not load class \""+linein+"\": " + e.getMessage());
				} catch (InstantiationException e) {
					LOG.severe("Could not load class \""+linein+"\": " + e.getMessage());
				} catch (IllegalAccessException e) {
					LOG.severe("Could not load class \""+linein+"\": " + e.getMessage());
				}
			}
		} catch (IOException e){
			LOG.severe("Could not open managerlist file: " + e.getMessage());
		}
		
	}
	public static void unloadGameManagers(){
		LOG.fine("Unloading Game Manager List");
		managerlist.clear(); 
	}
	
	/////////////////////////////////////////////////////
	/**
	 * A master list of clients. 
	 */
	protected ArrayList<ClientManager> clientlist;
	/**
	 * A list of clients that are not in a game at the moment.
	 */
	protected ArrayList<ClientManager> freelist;
	
	public AbstractGameManager(){
		clientlist = new ArrayList<ClientManager>();
		freelist = new ArrayList<ClientManager>();
	}
	
	/**
	 * Gets the name which the GameServer uses to match game types to GameManagers. The
	 * client, with its initial connection, sends this name to the manager. The GameServer
	 * then sorts the client into the proper manager.
	 * @return The name of the manager
	 */
	public abstract String getGameManagerName();
	
	/**
	 * Tells the manager that it needs to shutdown now. The manager should NOT tell the clients,
	 * as by the time this method is called, the Clients have been shutdown and disconnected. The
	 * manager should do any cleanup it needs as soon as possible. 
	 */
	public abstract void shutdownManager();
	
	/**
	 * This function is called by the ClientManager when the client sends it a message and
	 * a) the client is not in a game and b) the ClientManager does not handle the message. 
	 * @param sg The ServerGram to be handled
	 */
	public abstract void pushMessage(ClientManager cm, ServerGram sg);
	
	/**
	 * This function is a convenience function to handle chat messages from the client. The
	 * default implementation sends the message to all clients connected and, if the sending
	 * client is the only one in the manager, sends a second message stating "No one hears you." 
	 * @param cm The ClientManager sending the chat message
	 * @param sg The message sent
	 */
	public void pushChat(ClientManager cm, ServerGram sg){
		for (ClientManager othercm : freelist){
			othercm.sendMessage(sg);
		}
		if (clientlist.size() == 1){ //the only person here is the talking person
			clientlist.get(0).sendMessage(new ServerGram(
					IGameServer.MSGTYPE_CHAT,
					new IGameServer.ChatData("", "No one hears you...")
			));
		}
	}
	
	public void addClient(ClientManager cm){
		clientlist.add(cm);
		freelist.add(cm);
		cm.setGameManager(this);
		ClientID addingcid = cm.getClientID();
		for(ClientManager i : clientlist){
			i.sendMessage(new ServerGram(IGameServer.MSGTYPE_LOGIN, addingcid));
		}
	}
	
	public void removeClient(ClientManager cm){
		if (!clientlist.remove(cm)) return; //if this cm was never part of this, then return
		freelist.remove(cm);
		if (cm.getGameManager() == this) cm.setGameManager(null);
		ClientID leavingcid = cm.getClientID();
		for(ClientManager i : clientlist){
			i.sendMessage(new ServerGram(IGameServer.MSGTYPE_LOGOUT, leavingcid));
		}
	}
	
}
