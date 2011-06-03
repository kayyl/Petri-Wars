package petriwars;

import java.util.ArrayList;
import petriwars.Point;

public class WaypointManager {
	public ArrayList<Path> path_list;
	public WaypointManager(){
		path_list = new ArrayList<Path>();
	}

	public static class Path{
		ArrayList<WP> waypt_list;
		public Path(float start_x, float start_y, float end_x, float end_y){
			
			waypt_list = new ArrayList<WP>();
			waypt_list.add(new WP(new Point(start_x, start_y), new Point(end_x, end_y), null));
			waypt_list.add(new WP(new Point(end_x, end_y), new Point(end_x, end_y), waypt_list.get(0)));
			expand_path();
		}
		
		public class WP{
			Point loc;
			WP prev;
			double dist_left, dist_travelled;
			public WP(Point loc, Point dest, WP prev){
				this.loc = loc;
				this.prev = prev;
				dist_left = (Math.abs(dest.x-loc.x)) + (Math.abs(dest.y-loc.y));
				dist_travelled = prev.dist_travelled + Math.abs(prev.loc.x-loc.x) + Math.abs(prev.loc.y-loc.y);
			}
		}
		
		public void expand_path(){//add all of the way-points to the list
			//necessary bool :(
			boolean path_found=false;
			Point dest = waypt_list.get(waypt_list.size()-1).loc;
			WP cur;
			ArrayList<WP> path_tree = new ArrayList<WP>();
			path_tree.add((WP) waypt_list.get(0));
			
			//figure out which squares the unit path hits
			ArrayList<Point> intersects;
			ArrayList<Point> corners; // = andrew's corner finder
			//corners.sort(); //and also shave
			while(path_found==false){
				intersects = get_square_intersects(waypt_list.get(0).loc, dest);
				if(intersects.isEmpty()){path_found=true; break;}//done searching for waypoints
				corners = getCorners();
				print_intersects(intersects);
				//dont remove so that u can stil ref previous onesss
				cur = path_tree.get(next_path_pick(path_tree));
				for(int i=0; i<corners.size(); i++){
					path_tree.add(new WP(corners.get(i).loc, dest, cur));
				}
			}
			//reassemble the path
			while(cur.loc.x != waypt_list.get(0).loc.x && cur.loc.y != waypt_list.get(0).loc.y){
				waypt_list.add(1, cur);
				cur = cur.prev;
			}
		}
		
		public ArrayList<Point> get_square_intersects(Point s, Point e){//TODO fix this to use diff start AND end locations (also used by corner sorter)
			ArrayList<Point> intersects = new ArrayList<Point>();
			WP start = new WP(s, e, null);
			WP end = new WP(e, e, null);
			float slope;//slope of the line
			int left_right;//if ray is moving left or right (-1, or 1)
			Point end_square;//square map coor of path destination
			float xpos;//x pos on map
			float ypos;//y pos on map
			//get important variables
			slope = (end.loc.y - start.loc.y) / (end.loc.x - start.loc.x);
			left_right = (int) Math.signum(end.loc.x - start.loc.x);
			end_square = new Point((int)end.loc.x, (int)end.loc.y);
			//find each square on map that path hits
			ypos=start.loc.y;
			xpos=start.loc.x;
			if(left_right>0){//check if you are moving right
				if(slope==1){
					xpos-=1; ypos-=1;
					while((int)xpos!=(int)end_square.x || (int)ypos!=(int)end_square.y){
						xpos+=1;ypos+=1;
						intersects.add(new Point((int)(xpos), (int) ypos));//add to intersects
					}
				}
				if(slope>1){
					xpos-=1; ypos-=slope;
					while((int)xpos!=(int)end_square.x || (int)ypos!=(int)end_square.y){//until you reach the destination square
						//increment xpos and ypos
						xpos+=1; ypos+=slope;
						for(int i=(int)ypos; i<=(int)(ypos+slope); i++){//get all int values of y incurred in this int x
							intersects.add(new Point((int)(xpos), i));//add to intersects
							if((int)xpos==(int)end_square.x && i==(int)end_square.y)break;
						}
					}
				}
				else if (slope<1 && slope>0){
					slope=(float) Math.pow(slope, -1);//get slope inverse
					ypos-=1; xpos-=slope;
					while((int)xpos!=(int)end_square.x || (int)ypos!=(int)end_square.y){//until you reach the destination square
						//increment xpos and ypos
						ypos+=1; xpos+=slope;
						for(int i=(int)xpos; i<=(int)(xpos+slope); i++){//get all int values of y incurred in this int x
							intersects.add(new Point(i, (int)ypos));//add to intersects
							if(i==(int)end_square.x && (int)ypos==(int)end_square.y)break;
						}
						System.out.println("rounding? :" + xpos + " " + ypos);
					}
				}
				else if (slope==0){
					while((int)xpos!=(int)end_square.x || (int)ypos!=(int)end_square.y){//until you reach the destination square
						xpos++;
						intersects.add(new Point((int)xpos, (int)ypos));
					}
				}
				else if (slope<0 && slope>-1){
					slope=(float) Math.pow(slope, -1);//get slope inverse
					ypos+=1; xpos+=slope;
					while((int)xpos!=(int)end_square.x || (int)ypos!=(int)end_square.y){//until you reach the destination square
						//increment xpos and ypos
						ypos-=1; xpos-=slope;
						for(int i=(int)xpos; i<=(int)(xpos-slope); i++){//get all int values of y incurred in this int x
							intersects.add(new Point(i, (int)ypos));//add to intersects
							if(i==(int)end_square.x && (int)ypos==(int)end_square.y)break;
						}
						System.out.println("rounding? :" + xpos + " " + ypos);
					}
				}
				else if (slope<-1){
					xpos-=1; ypos-=slope;
					while((int)xpos!=(int)end_square.x || (int)ypos!=(int)end_square.y){//until you reach the destination square
						//increment xpos and ypos
						xpos+=1; ypos+=slope;
						for(int i=(int)ypos; i>=(int)(ypos+slope); i--){//get all int values of y incurred in this int x
							intersects.add(new Point((int)(xpos), i));//add to intersects
							if((int)xpos==(int)end_square.x && i==(int)end_square.y)break;
						}
						System.out.println("rounding? :" + xpos + " " + ypos);
					}
				}
			}
			else if (left_right<0){//you may be moving left
				if(slope>=1){
					while((int)xpos!=(int)end_square.x || (int)ypos!=(int)end_square.y){//until you reach the destination square
						for(int i=(int)ypos; i<=(int)(ypos+slope); i++){//get all int values of y incurred in this int x
							intersects.add(new Point((int)(xpos), i));//add to intersects
						}
						//increment xpos and ypos
						xpos-=1; ypos+=slope;
					}
				}
				else if (slope<1 && slope>0){
					slope=(float) Math.pow(slope, -1);//get slope inverse
					while((int)xpos!=(int)end_square.x || (int)ypos!=(int)end_square.y){//until you reach the destination square
						for(int i=(int)xpos; i>=(int)(xpos+slope); i--){//get all int values of y incurred in this int x
							intersects.add(new Point(i, (int)ypos));//add to intersects
						}
						//increment xpos and ypos
						ypos+=1; xpos-=slope;
					}
				}
				else if (slope==0){
					while((int)xpos!=(int)end_square.x || (int)ypos!=(int)end_square.y){//until you reach the destination square
						xpos--;
						intersects.add(new Point((int)xpos, (int)ypos));
					}
				}
				else if (slope<0 && slope>-1){
					slope=(float) Math.pow(slope, -1);//get slope inverse
					while((int)xpos!=(int)end_square.x || (int)ypos!=(int)end_square.y){//until you reach the destination square
						for(int i=(int)xpos; i>=(int)(xpos+slope); i--){//get all int values of y incurred in this int x
							intersects.add(new Point(i, (int)ypos));//add to intersects
						}
						//increment xpos and ypos
						ypos-=1; xpos-=slope;
					}
				}
				else if (slope<-1){
					while((int)xpos!=(int)end_square.x || (int)ypos!=(int)end_square.y){//until you reach the destination square
						for(int i=(int)ypos; i>=(int)(ypos+slope); i--){//get all int values of y incurred in this int x
							intersects.add(new Point((int)(xpos), i));//add to intersects
						}
						//increment xpos and ypos
						xpos-=1; ypos-=slope;
					}
				}
			}
			else{//you're moving up/down
				if(end.loc.y-ypos>0){//moving up
					while((int)xpos!=(int)end_square.x || (int)ypos!=(int)end_square.y){//until you reach the destination square
						ypos++;
						intersects.add(new Point((int)xpos, (int)ypos));
					}
				}
				else{//moving down
					while((int)xpos!=(int)end_square.x || (int)ypos!=(int)end_square.y){//until you reach the destination square
						ypos--;
						intersects.add(new Point((int)xpos, (int)ypos));
					}
				}
			}
			
			return intersects;
		}
		
		public void print_intersects(ArrayList<Point> intersects){
			System.out.println("Line from point: ( " + this.waypt_list.get(0).loc.x + ", " + this.waypt_list.get(0).loc.y + 
					") to point: (" + this.waypt_list.get(this.waypt_list.size()-1).loc.x + ", " + this.waypt_list.get(this.waypt_list.size()-1).loc.y + ")...");
			for(int i=0; i<intersects.size(); i++){
				System.out.println("(" + intersects.get(i).x + ", " + intersects.get(i).y + ")");
			}
		}
		
		public int next_path_pick (ArrayList<WP> list){
			double best_dist = list.get(0).dist_left + list.get(0).dist_travelled;;
			double cur_dist;
			int best_index = 0;
			for(int i=0; i<list.size(); i++){
				cur_dist = list.get(i).dist_left + list.get(i).dist_travelled;
				if(cur_dist < best_dist){best_dist = cur_dist; best_index = i;}
			}
			return best_index;
		}
		
		public ArrayList<Point> pick_corners (ArrayList<Point> corners, Point start){
			ArrayList<Point> ints;
			for(int i=0; i<corners.size(); i++){
				
			}
		}
	}
	
}
