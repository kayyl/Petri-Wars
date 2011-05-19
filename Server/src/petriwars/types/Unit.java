package petriwars.types;

import gameserver.util.ByteBuilder;


public class Unit {
	public static final int STATE_NOSTATE = 0;
	public static final int STATE_IDLE = 1;
	public static final int STATE_ATTACKING = 2;
	public static final int STATE_MOVING = 3;
	
	public int id;
	public int player;
	public int force;
	public float cur_pos[];
	public int t_pos[];
	public int n_pos[];
	public int cur_energy;
	public byte cur_state;//idle,move,attack
	public int target_id;
	public byte direction; //0->180 (convert to 0 to 360 on client)
	public boolean upgrades[];//f1,f2,f3,cw1,cw2,cw3,ci1,ci2,ci3,l1,l2,l3,ch1,ch2,ch3,pe(unused),pi(unused)
	public int up_count;
	public int dam;
	public int def;
	public int spd;
	public int rng;
	public long attk_time;
	public long move_time;
	public long spec_time;
	
	public Unit(int id_num, float pos[], int p){
		id=id_num;
		player=p;
		cur_pos=pos.clone();
		t_pos=new int[2];
			t_pos[0]=(int)cur_pos[0];
			t_pos[1]=(int)cur_pos[1];
		n_pos=t_pos;
		cur_energy=100;
		cur_state = STATE_NOSTATE;
		target_id=-1;
		upgrades = new boolean[16];
		up_count=0;
		dam=5;
		def=0;
		spd=5;
		rng=1;
		attk_time=0;
		move_time=0;
		spec_time=0;
	}
	
	
	/**
	 * A special method that will be called when passing this object to a
	 * ByteBuilder for printing.  
	 */
	public byte[] toByteStructure(){
		ByteBuilder bb = new ByteBuilder();
		bb
			.appendSpecial(id, 2, false) //uid is u16 bit
			.appendSpecial((short)(cur_pos[1]*100.0f), 2, false) //posx is s16
			.appendSpecial((short)(cur_pos[0]*100.0f), 2, false) //posy is s16
			.appendSpecial(cur_energy, 2, false) //u16
			.appendSpecial(player, 1, false) //u8
			.appendSpecial(force, 1, false) //u8
			.append(direction)
			.append(cur_state); 
		
		int upgrademap = 0;
		for (int i = 0; i < upgrades.length; i++){
			if (upgrades[i]) upgrademap |= 1 << i; 
		}
		bb.appendSpecial(upgrademap, 4, false); //u32
		
		return bb.toByteArray();
	}
}
