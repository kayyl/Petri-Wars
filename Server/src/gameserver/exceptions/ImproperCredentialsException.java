package gameserver.exceptions;

/**
 * This exception is usually thrown when the client has sent a DISBAND message when the
 * requesting client is not the creator of the game. This is usually thrown by the 
 * ClientManager over the socket at the client via a CANNOT message.
 * @author Tim
 */
public class ImproperCredentialsException extends GameServerException {
	private static final long serialVersionUID = -5211616778524114737L;
	public ImproperCredentialsException() {super();}
	public ImproperCredentialsException(String message, Throwable cause) {super(message, cause);}
	public ImproperCredentialsException(String message) {super(message);}
	public ImproperCredentialsException(Throwable cause) {super(cause);}
	
}
