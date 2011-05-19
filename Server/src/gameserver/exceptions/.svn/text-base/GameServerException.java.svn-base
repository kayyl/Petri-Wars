package gameserver.exceptions;

public class GameServerException extends Exception {
	private static final long serialVersionUID = 3493097541222736711L;
	public GameServerException() {}
	public GameServerException(String message) {super(message);}
	public GameServerException(Throwable cause) {super(cause);}
	public GameServerException(String message, Throwable cause) {super(message, cause);}
	
	public byte[] toByteArray(){
		int l = this.getMessage().length();
		byte[] ex = new byte[l+4];
		byte[] msg = this.getMessage().getBytes();
		for (int i = 4; i < msg.length; i++){
			
		}
		return ex;
	}
}
