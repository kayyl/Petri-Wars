package gameserver.ids;

import java.io.Serializable;

/**
 * This is the Server version of ClientID. The Server version is not completely
 * immutable as the Client version is. The Server can assign a Server User ID to
 * this Client, and then return the ClientID to the client. Note that this ClientID
 * is the same as Client's ClientID in terms of serialization.
 * @author Tim
 *
 */
public class ClientID implements Serializable {
	private static final long serialVersionUID = -7806882876379944335L;
	private String name;
	private int id;
	
	protected ClientID(){} //constructor for serialization
	
	public ClientID(String name){
		this.name = name;
		this.id = -1;
	}
	public ClientID(String name, int id){
		this.name = name;
		this.id = id;
	}
	public ClientID(int id){
		this.name = null;
		this.id = id;
	}
	
	public void setName(String str){this.name = str;}
	public void setID(int id){this.id = id;}
	
	public String getName(){return name;}
	public int getID(){return id;}
}
