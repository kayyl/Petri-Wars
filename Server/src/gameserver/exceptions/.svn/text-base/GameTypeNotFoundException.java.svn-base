package gameserver.exceptions;

/**
 * This exception is thrown (usually by the ClientManager over the socket at the client via
 * a CANNOT message) when the creation of a game is requested and the server cannot
 * find or cannot determine the AbstractGame subclass to initialize. Note that this is a 
 * subclass of ClassNotFoundException (instead of GameServerException) because the two are
 * essentially the same thing.
 * @author Tim
 */
public class GameTypeNotFoundException extends ClassNotFoundException {
	private static final long serialVersionUID = 2062423493109679017L;
	public GameTypeNotFoundException() {super();}
	public GameTypeNotFoundException(String message, Throwable cause) {super(message, cause);}
	public GameTypeNotFoundException(String message) {super(message);}
	//public GameTypeNotFoundException(Throwable cause) {super(cause);}
	
}
