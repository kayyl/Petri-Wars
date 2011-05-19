package gameserver;

import gameserver.ids.ClientID;
import gameserver.ids.GameID;

public abstract class AbstractGame extends Thread {
	protected String instancename;
	private ClientID creator;
	public final ClientID getGameCreator(){return creator;}
	
	public AbstractGame(ClientID creator){
		this.creator = creator;
	}
	
	public abstract GameID getGameID();
	
	public String getInstanceName(){return instancename;}
	
	/**
	 * The server calls this method whenever a client asks for a filtered list of available
	 * games. The server takes the string the client asked for (usually a regex pattern) and
	 * matches it against this string to see if the client might be looking for this game.
	 * Usually, returning the same string as getGameName() (the default implementation) is 
	 * enough, but in certain cases where this game has specialized settings or this game 
	 * is in a certain mode, this string may be different to reflect that.
	 * @return A string to identify this game apart from other game types and games of the
	 * same type but different settings.
	 */
	public String getFilterName() {return getGameName();}
	
	/**
	 * The server calls this method when it wants the name of this game type, possibly 
	 * different from the class name. Returning the class name is the default behavior.
	 * @return A string to identify this game apart from other game types.
	 */
	public String getGameName(){return this.getClass().getName();}
	
	public abstract int getMaxNumPlayers();
	
	public abstract boolean canAddPlayer();
	public abstract void addPlayer(ClientManager client);
	public abstract void removePlayer(ClientManager client);
	
	/**
	 * <p>Pushes a message from a client onto the message stack. <b>This method should
	 * not do any message processing.</b> If this method does do such processing, and
	 * the message requires a lot of time to process, it may
	 * keep the ClientManager that called this method from receiving any further messages
	 * from the client.
	 * <p>This message should put messages into a queue for the game thread to handle. 
	 * @param client
	 * @param gram
	 */
	public abstract void pushMessage(ClientManager client, ServerGram gram);
	
	/**
	 * Handles a chat message for a particular game. Games can do whatever they want
	 * with the chat message, including ignore it.
	 * @param client
	 * @param gram
	 */
	public abstract void handleChat(ClientManager client, ServerGram gram);
	
	/**
	 * A call to this method means that the server is shutting down immediately. 
	 * The game should clear all messages out of the queue without addressing them and
	 * dispose of all resources as quickly as possible. 
	 */
	public abstract void shutdownNow();
	
	/**
	 * A call to this method means that the game is being shutdown and all of its players
	 * freed. This call is not as immediate as {@code shutdownNow()}. The game should 
	 * gracefully handle all remaining messages with an aim to clear them out. Usually
	 * all players will have been removed already, however this does not have to be the case.  
	 */
	public abstract void disband();
	
	/**
	 * The game server calls this function just before the game thread is started, to process 
	 * any setup the game needs to run. This could include getting data from the clients
	 * or sending data to the clients. By the time this function is called, all the clients
	 * should be added to the game (given that all clients must be present to start the game).
	 */
	public abstract void setupGame();
	
	@Override public abstract void run();
}
