
public class Unit {
	int id;
	int player;
	int force;
	double cur_pos[];
	int t_pos[];
	int n_pos[];
	int cur_energy;
	char cur_state;//idle,move,attack
	int target_id;
	double direction;
	boolean upgrades[];//f1,f2,f3,cw1,cw2,cw3,ci1,ci2,ci3,l1,l2,l3,ch1,ch2,ch3,pe(unused),pi(unused)
	int up_count;
	int dam;
	int def;
	int spd;
	int rng;
	double attk_time;
	double move_time;
	
	public Unit(int id_num, double pos[], int p){
		id=id_num;
		player=p;
		cur_pos=pos;
		t_pos=new int[2];
			t_pos[0]=(int)cur_pos[0];
			t_pos[1]=(int)cur_pos[1];
		n_pos=t_pos;
		cur_energy=100;
		cur_state=0;
		target_id=-1;
		upgrades = new boolean[16];
		up_count=0;
		dam=5;
		def=0;
		spd=5;
		rng=1;
		attk_time=0;
		move_time=0;
	}
	
}
