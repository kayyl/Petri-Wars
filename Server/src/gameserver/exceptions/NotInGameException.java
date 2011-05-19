package gameserver.exceptions;

/**
 * This exception is thrown (usually by the ClientManager over the socket at the client via
 * a CANNOT message) in the event where the ClientManager receives a custom message type
 * (types over 32) when the client is not in a game to pass the message to.
 * @author Tim
 */
public class NotInGameException extends GameServerException {
	private static final long serialVersionUID = 2263881862438190843L;
	public NotInGameException() {super();}
	public NotInGameException(String message, Throwable cause) {super(message, cause);}
	public NotInGameException(String message) {super(message);}
	public NotInGameException(Throwable cause) {super(cause);}
	
}
