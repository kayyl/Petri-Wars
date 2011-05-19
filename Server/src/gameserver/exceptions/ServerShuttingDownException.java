package gameserver.exceptions;

/**
 * This exception is thrown by any server bookkeeping methods whenever they cannot perform
 * the task they are to do because the server is shutting down. Games that run into this
 * exception are to handle it gracefully (which is why it is NOT a RuntimeException). 
 * @author Tim
 */
public class ServerShuttingDownException extends GameServerException {
	private static final long serialVersionUID = -3171576739751366720L;
	public ServerShuttingDownException() {super();}
	public ServerShuttingDownException(String message, Throwable cause) {super(message, cause);}
	public ServerShuttingDownException(String message) {super(message);}
	public ServerShuttingDownException(Throwable cause) {super(cause);}
	
}
