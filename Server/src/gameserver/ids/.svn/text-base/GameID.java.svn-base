package gameserver.ids;

import java.io.Serializable;

public class GameID implements Serializable {
	private static final long serialVersionUID = -6320345660004272277L;
	private String gamename;
	private String instancename;
	private int id;
	
	protected GameID(){}
	public GameID(String name, String instance, int id){
		this.gamename = name;
		this.instancename = instance;
		this.id = id;
	}
	
	public void setGameName(String name){this.gamename = name;}
	public void setInstanceName(String name){this.instancename = name;}
	public void setID(int id){this.id = id;}
	
	public String getGameName(){return gamename;}
	public String getInstanceName(){return instancename;}
	public int getID(){return id;}
}
