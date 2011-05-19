package gameserver;

import gameserver.util.ByteBuilder;

import java.io.Serializable;

public interface IGameServer {
	public static final int SERVERPORT = 12121;
	
	/**************************** Server ********************************/
	
	public static final int MSGTYPE_SERVERERROR = -2;
	
	public static final int MSGTYPE_DISCONNECT = -1;
	
	/**<p>Message indicates that the other party is acknowledging, with no indication
	 * or parameters as to what they are acknowledging. Basically, this message type is 
	 * a NOOP. If a client sends this message to the server, the server will ignore it.</p>
	 */
	public static final int MSGTYPE_ACK = 0;
	
	public static final int MSGTYPE_TIME = 1;
	
	/**
	 * <b>Client to Server</b>: "I'm logging in now"
	 * <b>Server to Client</b>: "A client has logged in"
	 * @param body (from Server) A ClientID identifying the client that is logging in.
	 */
	public static final int MSGTYPE_LOGIN = 2;
	
	/**
	 * <b>Client to Server</b>: "I'm logging out now"
	 * <b>Server to Client</b>: "A client has logged out"
	 * @param body (from Server) A ClientID identifying the client that is logging out.
	 */
	public static final int MSGTYPE_LOGOUT = 3;
	
	public static final int MSGTYPE_DENY = 4;
	
	public static final int MSGTYPE_CANNOT = 5;
	
	public static final int MSGTYPE_SERVERSHUTDOWN = 6;
	
//	public static final int MSGTYPE_RESERVED = 7;
	
//	public static final int MSGTYPE_RESERVED = 8;
	
//	public static final int MSGTYPE_RESERVED = 9;
	
	/**<b>Server to Client</b>: "Your name, please?"
	 * <b>Client to Server</b>: "Hello, my name is..."
	 * <p>Message is sent to the server with the client's constructed ClientID. If this
	 * message is sent from the Server, it is requesting the Client's identification, 
	 * and the client must supply it as soon as possible. This request could happen at
	 * any time. 
	 * <p>Note that this message is never sent from the server; the server, upon first 
	 * connection, sends a String with which it identifies itself. The string will tell 
	 * the client the server type and version number, after which it will send general 
	 * information in a simple paragraph.</p>
	 * @param body (from Client) A ClientID representing the sending client.
	 */
	public static final int MSGTYPE_CREDENTIALS = 10;
	
//	public static final int MSGTYPE_RESERVED = 11;	
	
//	public static final int MSGTYPE_RESERVED = 12;
	
//	public static final int MSGTYPE_RESERVED = 13;
	
//	public static final int MSGTYPE_RESERVED = 14;
	
//	public static final int MSGTYPE_RESERVED = 15;
	
//	public static final int MSGTYPE_RESERVED = 16;
	
//	public static final int MSGTYPE_RESERVED = 17;
	
//	public static final int MSGTYPE_RESERVED = 18;
	
//	public static final int MSGTYPE_RESERVED = 19;
	
//	public static final int MSGTYPE_RESERVED = 20;	
	
//	public static final int MSGTYPE_RESERVED = 21;
	
//	public static final int MSGTYPE_RESERVED = 22;
	
//	public static final int MSGTYPE_RESERVED = 23;
	
//	public static final int MSGTYPE_RESERVED = 24;
	
//	public static final int MSGTYPE_RESERVED = 25;
	
//	public static final int MSGTYPE_RESERVED = 26;
	
//	public static final int MSGTYPE_RESERVED = 27;
	
//	public static final int MSGTYPE_RESERVED = 28;
	
	/**
	 * <b>Client to Server</b>: "I'd like a list of connected users, please?"
	 * <b>Server to Client</b>: "Here is a list of connected users."
	 * <p>This message is part of the series of messages that the GameServer does not
	 * directly handle. This message is handled be the game manager. The recommendation
	 * is that this message is sent from the client when a user joins, a user leaves,
	 * or the client requests the list.
	 * @param body (from Server) A game-dependent type that lists the connected users
	 */
	public static final int MSGTYPE_LISTUSERS = 29;
	
	/**
	 * <b>Client to Server</b>: "I'd like a list of available games, please?"
	 * <b>Server to Client</b>: "Here is a list of available games."
	 * <p>This message is part of the series of messages that the GameServer does not
	 * directly handle. This message is handled be the game manager. The recommendation
	 * is that this message is sent to the client when a new game is created, a game 
	 * is destroyed, or the client requests the list.
	 * @param body (from Server) A game-dependent type that lists the available games. 
	 */
	public static final int MSGTYPE_LISTGAMES = 30;	
	
	/**
	 * <b>Client to Server</b>: "I'd like to create a game with these settings."
	 * <b>Server to Client</b>: "A new game has been created"
	 * <p>This message is part of the series of messages that the GameServer does not
	 * directly handle. This message is handled be the game manager. The recommendation 
	 * is that this message is sent from the client that wishes to create a game. The 
	 * server could send this message to all clients (in lieu of the LISTGAMES message)
	 * to indicate a new game.
	 * @param body 
	 * (from Client) A game-dependent type that specifies game creation parameters.<br>
	 * (from Server) A GameID that identifies the game created.
	 */
	public static final int MSGTYPE_CREATEGAME = 31;
	
	/**
	 * <b>Client to Server</b>: "Please destroy this game."
	 * <b>Server to Client</b>: "A new game has been removed"
	 * <p>This message is part of the series of messages that the GameServer does not
	 * directly handle. This message is handled be the game manager. The recommendation
	 * is that this message sent from the client that wishes to destroy a game. The
	 * server could send this message to all clients (in lieu of the LISTGAMES message)
	 * to indicate a removed game.
	 * @param body (from Client) A GameID that identifies the game to be removed. 
	 */
	public static final int MSGTYPE_DESTROYGAME = 32;
	
	/**<b>Client to Server</b>: "I'd like to join this game."
	 * <b>Server to Client</b>: "A new client has joined the game."
	 * <p>This message is part of the series of messages that the GameServer does not
	 * directly handle. This message is handled be the game manager. The recommendation
	 * is that this message is sent from the client to request joining a game. The server
	 * then sends to all in the game already that the client is entering. Depending on the
	 * game, this message may or may not be used.
	 * @param body <br>
	 * (from Client) A GameID identifying the game that the client wants to join.<br>
	 * (from Server) A ClientID identifying the client that has been added.
	 */
	public static final int MSGTYPE_JOINGAME = 33;
	
	/**
	 * <b>Client to Server</b>: "Ask this other user to join the game."
	 * <b>Server to Client</b>: "Another user wants you to join a game."
	 * <p>This message is part of the series of messages that the GameServer does not
	 * directly handle. This message is handled be the game manager. The recommendation
	 * is that this message is sent from a client to the server to request that another
	 * client join an existing game. The requested client can respond with a JOINGAME or
	 * with a DENY. Depending on the game, this message may or may not be used.
	 * @param body <br>
	 * (from Client) A ClientID and GameID (or a game-dependent replacement) identifying
	 * the client to ask and game to join, respectfully.
	 * (from Server) A ClientID and GameID (or a game-dependent replacement) identifying
	 * the client who asked you to join and game to join, respectfully.
	 */
	public static final int MSGTYPE_REQUESTJOIN = 34;
	
	/**<b>Client to Server</b>: "I'm leaving this game."<br>
	 * <b>Server to Client</b>: "A client is leaving the game." 
	 * <p>This message is part of the series of messages that the GameServer does not
	 * directly handle. This message is handled be the game manager. The recommendation
	 * is that the client send this to leave the game and return to the game manager.
	 * The server sends all the clients in the game still this message to identify
	 * the leaving party.
	 * @param body (from Server) A ClientID identifying the client that is closing.
	 */
	public static final int MSGTYPE_LEAVEGAME = 35;
	
	/**<b>Client to Server</b>: "I'd like to move to a sub-manager"<br>
	 * <b>Server to Client</b>: "A client has joined a sub-manager"
	 */
	public static final int MSGTYPE_JOINCHANNEL = 36;
	
	/**<b>Client to Server</b>: "I'd like to leave to the main manager"<br>
	 * <b>Server to Client</b>: "A client has left the sub-manager"
	 */
	public static final int MSGTYPE_LEAVECHANNEL = 37;
	
//	public static final int MSGTYPE_RESERVED = 38;
	
//	public static final int MSGTYPE_RESERVED = 39;
	
	
//	public static final int MSGTYPE_RESERVED = 40;	
	
//	public static final int MSGTYPE_RESERVED = 41;
	
//	public static final int MSGTYPE_RESERVED = 42;
	
//	public static final int MSGTYPE_RESERVED = 43;
	
//	public static final int MSGTYPE_RESERVED = 44;
	
//	public static final int MSGTYPE_RESERVED = 45;
	
//	public static final int MSGTYPE_RESERVED = 46;
	
//	public static final int MSGTYPE_RESERVED = 47;
	
//	public static final int MSGTYPE_RESERVED = 48;
	
	/**
	 * <b>Server to Client</b>: "Tell everyone this: ..."
	 * <b>Client to Server</b>: "Hey, this client said this: ..."
	 * <p>This message type is used to send messages to other clients. If the sending
	 * client is "free" (it is not attached to any game) then the server will propagate
	 * the message sent to all other "free" clients on the server, including the sender.
	 * <p>A client receiving this message from the server should notify its user about 
	 * the message. The message will be prepended with the username of the sending client
	 * (in the server; clients should not prepend their username themselves).
	 * <p>If the sending client is not free, then the game he is a part of must handle 
	 * the request. Games are not required to use this message type for chats, but are 
	 * encouraged to do so for standardization. (Games are given message type numbers
	 * 50 and above to use for their own purposes.) 
	 * @param body A string containing the message.
	 */
	public static final int MSGTYPE_CHAT = 49;
	
	////////////////////////////////////////////////////////////////
	
	public static class ChatData implements Serializable {
		private static final long serialVersionUID = -4556918659157773525L;
		public ChatData(String name, String message){
			this.name = name; this.message = message;
		}
		public String name;
		public String message;
		
		public byte[] toByteStructure() {
			ByteBuilder bb = new ByteBuilder();
			bb.append(name);
			bb.append(message);
			return bb.toByteArray();
		}
	}
}
