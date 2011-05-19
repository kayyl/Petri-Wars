package petriwars;

import java.util.ArrayList;
import java.util.Random;

import petriwars.types.Map;
import petriwars.types.Unit;


public class UnitManager {
	Unit unit_manager[];
	Random rand = new Random();
	
	public UnitManager(int MAX_UNITS){
		unit_manager = new Unit[MAX_UNITS];
	}
	
	public void set_up_new_map(Map map, int players){
		ArrayList<int[]> spawn_locs = new ArrayList<int[]>();
		char[][] temp_map = map.get_map();
		for(int i=0; i< map.get_map_size(); i++){
			for(int j=0; j< map.get_map_size(); j++){
				if(temp_map[i][j]=='4'){//potential spawn location
					int[] temp_loc = new int[2];
					temp_loc[0]=i;
					temp_loc[1]=j;
					spawn_locs.add(temp_loc);
					//now change map to '1' so units can move there
					map.set_map_to_normal(i, j);
				}
				else if(temp_map[i][j]=='3'){//neutral unit location
					int n_id = get_unused_slot();
					float[] temp_loc = new float[2];
					temp_loc[0] = i+.5f;
					temp_loc[1] = j+.5f;
					unit_manager[n_id]=new Unit(n_id, temp_loc, 0);
					unit_manager[n_id].upgrades[14]=true;
					//now change map to '1' so units can move there
					map.set_map_to_normal(i, j);
				}
			}
		}
		//now give players their spawn units
		
		//player 1
		int r = rand.nextInt(spawn_locs.size());
		int r2 = rand.nextInt(spawn_locs.size());
		int n_id = get_unused_slot();
		float spawn_unit[] = {(float)spawn_locs.get(r)[0], (float)spawn_locs.get(r)[1]};
		unit_manager[n_id]=new Unit(n_id, spawn_unit, 1);
		spawn_unit[0]+=.5;
		n_id = get_unused_slot();
		unit_manager[n_id]=new Unit(n_id, spawn_unit, 1);
		spawn_unit[1]+=.5;
		n_id = get_unused_slot();
		unit_manager[n_id]=new Unit(n_id, spawn_unit, 1);
		spawn_unit[0]-=.5;
		n_id = get_unused_slot();
		unit_manager[n_id]=new Unit(n_id, spawn_unit, 1);
		//player 2
		while(r==r2){r2 = rand.nextInt(spawn_locs.size());}
		n_id = get_unused_slot();
		float spawn_unit2[] = {(float)spawn_locs.get(r2)[0], (float)spawn_locs.get(r2)[1]};
		unit_manager[n_id]=new Unit(n_id, spawn_unit2, 2);
		spawn_unit2[0]+=.5;
		n_id = get_unused_slot();
		unit_manager[n_id]=new Unit(n_id, spawn_unit2, 2);
		spawn_unit2[1]+=.5;
		n_id = get_unused_slot();
		unit_manager[n_id]=new Unit(n_id, spawn_unit2, 2);
		spawn_unit2[0]-=.5;
		n_id = get_unused_slot();
		unit_manager[n_id]=new Unit(n_id, spawn_unit2, 2);
		
	}
	
	public boolean new_unit_bud(int parent_id, int player){
		int id=get_unused_slot();//get unused id within max unit count
		if(id==-1){return false;}//no slots available
		float[] offset = new float[2];
		offset[0] = rand.nextFloat();//generate random spawn pt
		offset[1] = (float) Math.pow(1 - Math.pow(offset[0], 2), .5);
		//make offset randomly positive or negative
			int pos_neg = rand.nextInt(1);
			if(pos_neg==0) pos_neg=-1;
			offset[0] *= pos_neg;
			pos_neg = rand.nextInt(1);
			if(pos_neg==0) pos_neg=-1;
			offset[1] *= pos_neg;
		offset[0] += unit_manager[parent_id].cur_pos[0];
		offset[1] += unit_manager[parent_id].cur_pos[1];
		unit_manager[id]=new Unit(id, offset, player);
		return true;
	}
	
	public int get_unused_slot(){
		for(int i=0; i<unit_manager.length; i++){
			if(unit_manager[i] == null){
				return i;
			}
		}
		return -1;
	}
}
