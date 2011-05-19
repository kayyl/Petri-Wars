package petriwars.types;


public class Player {
	public int u_id_list[];//list if unit id's
	public int spawn[];//spawn point coordinates
	public int force;//what team player is on
	
	public Player(int s[], int f, int MAX_SUPPLY){
		spawn=s;
		force=f;
		u_id_list = new int[MAX_SUPPLY];
	}
	
	public void update_unit_list(){
		//TODO this
	}
}
