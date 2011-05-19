package gameserver.exceptions;

/**
 * This exception is thrown (usually by the ClientManager over the socket at the client via
 * a CANNOT message) whenever the client requests to do something before the initial 
 * credential swap. The ClientManager will continue to throw this until the client exchanges
 * credentials. 
 * @author Tim
 */
public class CredentialsRequiredException extends GameServerException {
	private static final long serialVersionUID = -9168914421377103565L;
	public CredentialsRequiredException() {super();}
	public CredentialsRequiredException(String message, Throwable cause) {super(message, cause);}
	public CredentialsRequiredException(String message) {super(message);}
	public CredentialsRequiredException(Throwable cause) {super(cause);}
	
}
