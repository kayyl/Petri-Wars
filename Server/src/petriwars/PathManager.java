package petriwars;

import java.util.ArrayList;

import petriwars.types.Map;

public class PathManager {
	//list of paths - which are linked lists themselves
	private ArrayList<ArrayList<Pathfinder.Node>> manager;
	private final char[][] map;
	
	//the yet-to-be-completed steps
	ArrayList<Unit_Move> list = new ArrayList<Unit_Move>();//to be returned
	
	public PathManager(Map map){
		/*
		 * The manager provides a list of phases.  Each phases allows certain paths in the current group selection
		 * (which is sent to the manager) to move at a time.  This prevents group-selected units from colliding
		 * into each other which moving. See proj.java for a demo.
		 * 
		 * How to Use:
		 * 1) make new pathmanager
		 * 2) add paths to pathmanager
		 * 3) get steps until pathmanager is empty
		 */
		this.map = map.get_map();
		manager  = new ArrayList<ArrayList<Pathfinder.Node>>();
	}
	
	public void add_path(int[] start, int[] dest, int unit_id, int spd, long tick, float[] f_dest, float[] cur_pos){
		//check if another path with the same unit_id exists
		clear_unit_path(unit_id);
		//add new path to manager
		ArrayList<Pathfinder.Node> path = new Pathfinder(unit_id, spd, tick, f_dest, cur_pos).findPath(start, dest);
		if(path!=null)manager.add(path);
	}
	
	public void clear_unit_path(int unit_id){
		for(int i=manager.size()-1; i>0; i--){
			if(manager.get(i).get(0).u_id==unit_id)
				manager.remove(i);
		}
	}
	
	public boolean isEmpty(){
		return manager.isEmpty();
	}
	
	public ArrayList<Unit_Move>get_next_move_phase(UnitManager unit_list){
		boolean in_list = false;
		boolean conflict = false;
		//populate list
		for(int i=(manager.size()-1); i>=0; i--){//for each path (manager is a list of paths) (in reverse order for safety)
			//first check for an empty path (or size 1)
			//System.out.println(manager.size() + " " + i);
			if(manager.get(i).size()>1){
				if(unit_list.unit_manager[manager.get(i).get(0).u_id]==null){manager.remove(i); continue;}
			//then check if list has that unit moving already (since this function is called for each step of a move)
				in_list=false;
				for(int j=0; j<list.size(); j++){
					if(list.get(j).u_id==manager.get(i).get(0).u_id){//if list u_id == a path u_id
						if(list.get(j).time<=manager.get(i).get(0).get_tick()){
							//then path is already in list and has not been updated
							in_list=true;
							break;
						}
					}
				}
				//check for movement conflicts
				conflict = false;
				for(int j=0; j<list.size(); j++){
					//if there is a t_square conflict
					if(manager.get(i).get(1)!=null && list.get(j).next_coor_t[0]==manager.get(i).get(1).coor[0] && list.get(j).next_coor_t[1]==manager.get(i).get(1).coor[1]){
						//if players are allied
						if(unit_list.unit_manager[manager.get(i).get(0).u_id].player == unit_list.unit_manager[list.get(j).u_id].player){
							//TODO if there is an offset conflict
							conflict=true;
							break;
						}
					}
				}
				if(conflict == true){
					if(manager.get(i).size()>2){//there is a next next step
						ArrayList<int[]> alt = get_pos_alternate_route(manager.get(i).get(0), manager.get(i).get(2).coor);
						//see if there is a single position in alt that does not share coords with anything in list
						for(int j=0; j<alt.size(); j++){
							boolean free=true;
							int alt_route=0;
							for(int k=0; k<list.size(); k++){
								if(alt.get(j)[0]==list.get(k).next_coor_t[0] && alt.get(j)[1]==list.get(k).next_coor_t[1]){
									//TODO if there is an offset conflict
									free=false;
									alt_route=k;
									break;
								}
							}
							if(free==true){//this spot is free, take it
								manager.get(i).get(1).coor[0]=alt.get(alt_route)[0];
								manager.get(i).get(1).coor[1]=alt.get(alt_route)[1];
							}
						}
					}
				}
				if(in_list==false && conflict==false){
					list.add(new Unit_Move(manager.get(i).get(0).u_id, manager.get(i).get(0).coor,manager.get(i).get(1).coor, manager.get(i).get(0).age, manager.get(0).get(0).get_tick()));
					if(manager.get(i).get(1).last_step==true)list.get(list.size()-1).set_fdest(manager.get(i).get(1).get_dest());//add final destination to the last step
					//we also have to tell the unit that it has since moved so that its moves count!
					unit_list.unit_manager[list.get(list.size()-1).u_id].t_pos[0]=list.get(list.size()-1).cur_coor_t[0];
					unit_list.unit_manager[list.get(list.size()-1).u_id].t_pos[1]=list.get(list.size()-1).cur_coor_t[1];
					unit_list.unit_manager[list.get(list.size()-1).u_id].n_pos[0]=list.get(list.size()-1).next_coor_t[0];
					unit_list.unit_manager[list.get(list.size()-1).u_id].n_pos[1]=list.get(list.size()-1).next_coor_t[1];
				}
			}
			else{//remember when we checked to see if a path was empty or not?  well if it is, kill that path
				manager.remove(i);
			}
		}
		//now the list is populated with unit moves that want to be made
		
		//we want to age everything in the list here and remove old items (in reverse order for safety issues)
		for(int i=list.size()-1; i>=0; i--){//list is aged based on unit speed :)
			//System.out.println("list size: " + list.size() + " list i: " + i + " unit_list id: " + list.get(i).u_id + " unit_list mem: " + unit_list.unit_manager[list.get(i).u_id]);
			list.get(i).life -= unit_list.unit_manager[list.get(i).u_id].spd * .2;
			//now we have to check to see if the life of a move got below 0 and then retire that move
			if(list.get(i).life<=0){
				for(int j=0; j<manager.size(); j++){ //remove this move from path in the list of paths
					if(manager.get(j).get(0).u_id==list.get(i).u_id){
						manager.get(j).remove(0);
						break;//for safety and speed
					}
				}
				list.remove(i);		//remove aged move from list
			}
		}
		return list;
	}
	
//	public ArrayList<Unit_Move> get_next_move_phase(UnitManager unit_list){//after phase is generated, moves are deleted from selected paths
//		//in case pathmanager is empty
//		if(isEmpty()){return null;}
//		//VARS
//		
//		ArrayList<int[]> destinations = new ArrayList<int[]>();//list of different destinations
//		ArrayList<int[]> mdist = new ArrayList<int[]>();//list of destination's manhattan distances and who's got them
//		//mdist: u_id, distance value
//		boolean unique_dest=true;
//		int[] md_temp;
//
//		//PROCEDURE
//		for(int i=0; i<manager.size(); i++){//generate destinations
//			//if destinations is not empty, then add to destinations list
//			unique_dest=true;
//			for(int j=0; j<destinations.size(); j++){
//				if(manager.get(i).get(1).coor[0]==destinations.get(j)[0] && manager.get(i).get(1).coor[1]==destinations.get(j)[1]){//must be unique destination
//					unique_dest=false;
//				}
//			}
//			if(unique_dest){ destinations.add(manager.get(i).get(0).coor);}//add destination
//		}//now we have a list of unique destinations
//		for(int i=0; i<destinations.size(); i++){//now to generate manhattan distances of destinations
//			md_temp = new int[2];
//			md_temp[0] = -1;
//			md_temp[1] = -1;
//			mdist.add(md_temp);
//			for(int j=0; j<manager.size(); j++){
//				if(manager.get(j).get(0).coor[0]==destinations.get(i)[0] && manager.get(j).get(0).coor[1]==destinations.get(i)[1]){
//					if(mdist.get(i)[0]==-1){md_temp[0]=j; md_temp[1]=manager.get(j).get(0).dist;}//populate empty list
//					//check if distance is better
//					else if(mdist.get(i)[1]>manager.get(j).get(0).dist){
//						md_temp[0]=j;
//						md_temp[1]=manager.get(j).get(0).dist;
//					}
//				}
//			}
//		}//now we have a list of best manhattan distances and which path has them
//		//generate list of who is moving to what destination
//		for(int i=0; i<destinations.size(); i++){
//			boolean same_id=false;
//			for(int j=0; j<list.size(); j++){//check if unit is already in list, if so dont add it
//				if(manager.get(mdist.get(i)[0]).get(0).u_id==list.get(j).u_id){
//					same_id=true;
//				}
//			}
//			if(same_id==false){//unit is not already in transit
//				list.add(new Unit_Move(manager.get(mdist.get(i)[0]).get(0).u_id, manager.get(mdist.get(i)[0]).get(0).coor, manager.get(mdist.get(i)[0]).get(1).coor, manager.get(mdist.get(i)[0]).get(0).age));
//				unit_list.unit_manager[list.get(list.size()-1).u_id].t_pos=list.get(list.size()-1).cur_coor_t;
//				unit_list.unit_manager[list.get(list.size()-1).u_id].n_pos=list.get(list.size()-1).next_coor_t;
//			}
//			same_id=false;
//		}
//		//now age each step
//		for(int i=list.size()-1; i>0; i--){
//			//age the step  EVENTUALLY USE UNIT SPEED
//			list.get(i).life-=1;
//			//CLEANUP: 1)delete all cur steps in list if its old	2)delete paths who reach their final destination
//			if(list.get(i).life<=1){
//				for(int j=0; j<manager.size(); j++){
//					if(list.get(i).u_id == manager.get(j).get(0).u_id){
//						manager.get(j).remove(0);//delete cur step
//					}
//				}
//				list.remove(i);
//			}
//		}
//		//CLEANUP PART 2
//		for(int i=manager.size()-1; i>=0; i--){
//			if(manager.get(i).size()<=1){manager.remove(manager.get(i));}//delete path
//		}
//		//return list! :)
//		return list;
//	}
	public ArrayList<int[]> get_pos_alternate_route(Pathfinder.Node cur, int[] step_dest){
		ArrayList<int[]> alts = new ArrayList<int[]>();
		for(int i=-1; i<=1; i++){
			for(int j=-1; j<=1; j++){
				if((cur.coor[0]+i)>=0 && (cur.coor[1]+j)>=0 //in bounds upper left
				&&(cur.coor[0]+i)<map.length && (cur.coor[1]+j)<map.length//in bounds bottom right
		 		&& (map[cur.coor[0]+i][cur.coor[1]+j]=='1')){ //go to free space
					if(Math.abs(i)!=Math.abs(j) || //non-diagonal move or...
							((!(i==1 && j==1)  || (map[cur.coor[0]+1][cur.coor[1]]=='1' && map[cur.coor[0]][cur.coor[1]+1]=='1')) &&//1,1 and no 1 or 1
							(!(i==-1 && j==-1)  || (map[cur.coor[0]-1][cur.coor[1]]=='1' && map[cur.coor[0]][cur.coor[1]-1]=='1')) &&//-1,-1 and no -1 or -1
							(!(i==-1 && j==1)  || (map[cur.coor[0]-1][cur.coor[1]]=='1' && map[cur.coor[0]][cur.coor[1]+1]=='1')) &&//-1,1 and no -1 or 1
							(!(i==1 && j==-1)  || (map[cur.coor[0]+1][cur.coor[1]]=='1' && map[cur.coor[0]][cur.coor[1]-1]=='1'))//1,-1 and no 1 or -1
									)){
						if(Math.abs((cur.coor[0]+i)-step_dest[0])<=1 && Math.abs((cur.coor[1]+j)-step_dest[1])<=1)//make sure its within one space of the next move
							alts.add(new int[]{cur.coor[0]+i, cur.coor[1]+j});
					}
				}
			}
		}
		return alts;
	}
	
	class Unit_Move{
		int u_id;//unit id
		int[] cur_coor_t;//cur terrain square
		int[] next_coor_t;//destination terrain square
		float life;//lifetime of move
		int age;//length of move
		long time;//tick of move
		float[] final_dest;
		
		public Unit_Move(int u, int[] c, int[] d, int a, long t){
			u_id=u;
			cur_coor_t=c;
			next_coor_t=d;
			life=(float)a;
			age=a;
			time=t;
			final_dest=null;
		}
		public float[] get_fdest(){
			return final_dest;
		}
		public void set_fdest(float[] fd){
			final_dest=fd.clone();
		}
	}
	
	public class Pathfinder {
		/*	_ = empty
		 * 	X = wall
		 *  O = corner spot
		 * 	' = path
		*/	//char[][] map;
//			char[][] map = currmap.get_map();
			ArrayList<Node> path;
			
			ArrayList<Node> p = new ArrayList<Node>();
			ArrayList<int[]> v = new ArrayList<int[]>();
			Node start;
			Node cur;
			int[] dir_temp;
			boolean visited=false;
			
			int u_id;
			int speed;
			long time;
			float[] dest;
			float[] curpos;
			float[] offset;
			
			public Pathfinder(int unit_id, int spd, long tick, float[] f_dest, float[] cur_pos){
				//map = m <- import this
				
				this.u_id=unit_id;
				this.speed=spd;
				time=tick;
				dest=f_dest;
				curpos=cur_pos;
				offset=new float[2];
				offset[0]=0;
				offset[1]=0;
				
				path = new ArrayList<Node>();
				p = new ArrayList<Node>();
			}
			public void compute_offset(int[] s){
				offset[0]=curpos[0]-(float)s[0];
				offset[1]=curpos[1]-(float)s[1];
			}
			
			class Node{
				Node prev;
//				ArrayList<Node> next = new ArrayList<Node>();
				int[] coor = new int[2];
				int dist;
				double moves=0;
				int u_id;
				int age;
				boolean diagonal_move=false;
				boolean last_step;
				int speed;
				public Node(Node p, int[] c, int d, int unit_id, int spd){
					this.u_id=unit_id;
					this.speed=spd;
					prev=p;
					coor=c;
					dist=d;
					age=10;
					last_step=false;
					if(prev!=null)moves=prev.moves+1;
				}
//				public void enqueue(Node n){
//					this.next.add(n);
//				}
//				public Node dequeue(Node n){
//					Node temp = n.prev;
//					temp.next.remove(n);
//					return temp;
//				}
				public long get_tick(){
					return Pathfinder.this.time;
				}
				public float[] get_dest(){
					return Pathfinder.this.dest;
				}
			}
			
			public  ArrayList<Node> findPath(int[] s, int[] end){
				start = new Node(null, s, (Math.abs(s[0]-end[0]) + Math.abs(s[1]-end[1])), u_id, speed);
				cur=start;
				while(cur.coor[0]!=end[0] || cur.coor[1]!=end[1]){
					//add all possible directions
					for(int i=-1; i<=1; i++){
						for(int j=-1; j<=1; j++){
							/* min bounds	&&
							 * max bounds	&&
							 * is a '_'		&&
							 * if abs[i]=abs[j] (if diagonal move)
							 * 	then	1,1		no 1 or 1
							 * 			-1,-1	no -1 or -1
							 * 			-1, 1	no -1 or 1
							 * 			1,-1	no 1 or -1
							 */
							if((cur.coor[0]+i)>=0 && (cur.coor[1]+j)>=0 //in bounds upper left
							&&(cur.coor[0]+i)<map.length && (cur.coor[1]+j)<map.length//in bounds bottom right
					 		&& (map[cur.coor[0]+i][cur.coor[1]+j]=='1')){ //go to free space
								if(Math.abs(i)!=Math.abs(j) || //non-diagonal move or...
										((!(i==1 && j==1)  || (map[cur.coor[0]+1][cur.coor[1]]=='1' && map[cur.coor[0]][cur.coor[1]+1]=='1')) &&//1,1 and no 1 or 1
										(!(i==-1 && j==-1)  || (map[cur.coor[0]-1][cur.coor[1]]=='1' && map[cur.coor[0]][cur.coor[1]-1]=='1')) &&//-1,-1 and no -1 or -1
										(!(i==-1 && j==1)  || (map[cur.coor[0]-1][cur.coor[1]]=='1' && map[cur.coor[0]][cur.coor[1]+1]=='1')) &&//-1,1 and no -1 or 1
										(!(i==1 && j==-1)  || (map[cur.coor[0]+1][cur.coor[1]]=='1' && map[cur.coor[0]][cur.coor[1]-1]=='1'))//1,-1 and no 1 or -1
												)){
									//eliminate previously visited spaces
									visited=false;
									dir_temp = new int[2];
									dir_temp[0]=cur.coor[0]+i;
									dir_temp[1]=cur.coor[1]+j;
									for(int a=0; a<v.size(); a++){
										if(dir_temp[0]==v.get(a)[0] && dir_temp[1]==v.get(a)[1]){
											visited = true;
											break;
										}
									}
									if(visited==false){//add to queue
										v.add(dir_temp);
										path.add(new Node(cur, dir_temp, (Math.abs(dir_temp[0]-end[0]) + Math.abs(dir_temp[1]-end[1])), u_id, speed));
										//SOMEWHAT OUT OF PLACE - add .4 to a diagonal move and 4 to age
										if(Math.abs(i)==Math.abs(j)){
											path.get(path.size()-1).moves+=.4;
											path.get(path.size()-1).age+=4;
											path.get(path.size()-1).diagonal_move=true;
										}
										if(dir_temp[0]==end[0] && dir_temp[1]==end[1]){//check if it reached the end and then reassemble already
											cur=path.get(path.size()-1);
											reassemble(s);
											return p;
										}
									}
								}
							}
						}
					}
					//remove cur from path
					path.remove(cur);
					//pick next location
					if(path.isEmpty()){//if path is empty (aka nowhere to go)
						return null;
					}
					double best = path.get(0).moves + path.get(0).dist;
					double temp_dist;
					int best_num=0;
					for(int i=1; i<path.size(); i++){
						temp_dist = path.get(i).moves + path.get(i).dist;
						if(temp_dist < best){
							best=temp_dist;
							best_num=i;
						}
						//else if(temp_dist == best)
					}
					cur = path.get(best_num);
				}
				return reassemble(s);
			}
			
			public ArrayList<Node> reassemble(int[] s){
				//reassemble the path backwards
				while(cur.coor[0]!=start.coor[0] || cur.coor[1]!=start.coor[1]){
					p.add(0, new Node(cur.prev, cur.coor, cur.dist, u_id, speed));
					cur=cur.prev;
				}
				//tack on start position
				p.add(0, start);
				//add last node id to this one
				p.get(p.size()-1).last_step=true;
				//compute offset
				Pathfinder.this.compute_offset(s);
				return p;
			}
			
		}

}
