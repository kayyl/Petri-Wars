package petriwars;

import static petriwars.types.Task.TASK_ATTACKMOVE;
import static petriwars.types.Task.TASK_DIVIDE;
import static petriwars.types.Task.TASK_MOVE;
import static petriwars.types.Task.TASK_UPGRADE_CELLWALL;
import static petriwars.types.Task.TASK_UPGRADE_CHLOROPLAST;
import static petriwars.types.Task.TASK_UPGRADE_CILIA;
import static petriwars.types.Task.TASK_UPGRADE_FLAGELLA;
import static petriwars.types.Task.TASK_UPGRADE_LYSOSOME;
import static petriwars.types.Unit.STATE_ATTACKING;
import static petriwars.types.Unit.STATE_IDLE;
import static petriwars.types.Unit.STATE_MOVING;
import gameserver.AbstractGame;
import gameserver.ClientManager;
import gameserver.ServerGram;
import gameserver.ids.ClientID;
import gameserver.ids.GameID;
import gameserver.util.ByteBreaker;
import gameserver.util.ByteBuilder;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Logger;

import petriwars.types.Map;
import petriwars.types.Task;
import petriwars.types.Unit;

public class PetriWarsGame extends AbstractGame {
	private static final Logger LOG = Logger.getLogger("PetriWarsGame"); 
	//USE THIS LOGGER INSTEAD OF System.out!!!!
	
	public static final int MSGTYPE_COMMAND = 51;
	public static final int MSGTYPE_MULTICAST = 60;
	
	public static final int MULTICASTPORT = 12184;
	private static final int MAX_SELECT_UNITS = 12;
	private static final int MAX_UNITS = 130; //Supply max for map
	
	private ArrayList<ClientManager> playerlist;
	private ArrayList<InetAddress> addresses;
	private DatagramSocket dsock;

	private List<Task> task_list;	//List of tasks
	private PathManager path_list;	//List of moves
	private UnitManager unit_list;	//List of units
	
	private int attack_table[]={0, 11, 15, 19, 22, 25, 28, 31, 35, 39};
	
	private Timer timer;
	private long tick;			//time in ticks, is constantly increasing
	
	
	
	
	public PetriWarsGame(ClientID creator) {
		super(creator);
		playerlist = new ArrayList<ClientManager>(2);
		addresses = new ArrayList<InetAddress>(2);
	}
	
	@Override public String getGameName() { return "Petri Wars v0.1"; }
	
	@Override public GameID getGameID() {
		return null;
	}
	
	@Override public int getMaxNumPlayers() {
		return 2;
	}
	
	@Override public boolean canAddPlayer() {
		return false;
	}
	
	@Override public void addPlayer(ClientManager client) {
		playerlist.add(client);
		client.setGame(this);
	}
	
	@Override public void removePlayer(ClientManager client) {
		int index = playerlist.indexOf(client);
		if (index == -1) return;
		
		playerlist.remove(index);
		addresses.remove(index);
		LOG.fine("Removing player from game. There are now "+playerlist.size()+" players in the game.");
		if (playerlist.isEmpty()) disband();
	}
	
	@Override public void pushMessage(ClientManager client, ServerGram gram) {
		if (gram.getType() == MSGTYPE_COMMAND){
			ByteBreaker bb = new ByteBreaker((byte[]) gram.getBody());
			MsgData_Command cmd = new MsgData_Command();
			{
				cmd.tick = bb.parseLong();
				cmd.command = bb.parseByte();
				cmd.unitnums = bb.parseByte();
				int[] unit = new int[MAX_SELECT_UNITS];
				for(int i = 0; i < unit.length; i++){
					unit[i] = bb.parseShort();
				}
				cmd.uid = Arrays.copyOfRange(unit, 0, cmd.unitnums);
				cmd.destx = bb.parseShort()*0.01f;
				cmd.desty = bb.parseShort()*0.01f;
				cmd.targetid = bb.parseShort();
			}
			task_list.add(new Task(cmd.uid, cmd.command, new float[]{cmd.desty, cmd.destx}));
			LOG.fine("Task List now has "+task_list.size()+" tasks to do.");
		}
	}
	
	@Override public void handleChat(ClientManager client, ServerGram gram) {
		for (ClientManager cm : playerlist){
			cm.sendMessage(gram);
		}
	}
	
	@Override public void shutdownNow() {
		timer.cancel(); timer = null;
		playerlist.clear(); playerlist = null;
		dsock.close();
	}
	
	@Override public void disband() {
		dsock.close();
//		GameServer.releaseMulticastAddress(multicastaddress);
		timer.cancel();
	}
	
	@Override public void setupGame() {
		try {
			LOG.fine("Setting up Game");
			dsock = new DatagramSocket();
			
			for (int i = 0; i < playerlist.size(); i++){
				addresses.add(playerlist.get(i).getSocket().getInetAddress());
				
			}
			
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {}
		
		//TODO get map from client here
		Map map = new Map("map.pwm");
		
		path_list = new PathManager(map);
		unit_list = new UnitManager(MAX_UNITS);
		task_list = new ArrayList<Task>();
		
		unit_list.set_up_new_map(map, 2);
		tick = 0;
	}
	
	@Override public void run() {
		LOG.info("Game now starting");
		timer = new Timer();
		timer.scheduleAtFixedRate(new perform_tasks(), 0, 100);
	}
	
	private void sendUpdate() {
		LOG.finest("Sending update!");
		//package units up into byte array
		ByteBuilder bb = new ByteBuilder();
		bb.append(tick); //game tick is sent first
		for (int i = 0; i < unit_list.unit_manager.length; i++){
			if (unit_list.unit_manager[i] != null){
				bb.append(unit_list.unit_manager[i].toByteStructure());
			}
		}
		
		//prepend the size
		ByteBuilder db = new ByteBuilder(bb.size()+4);
		db.append(bb.size()).append(bb);
		
		for (InetAddress in : addresses){
			try {
				dsock.send(new DatagramPacket(db.toByteArray(), db.size(), in, MULTICASTPORT));
			} catch (IOException e) {
				LOG.severe("Error sending udp packet to client at address "+in);
			}
		}
	}
	
	class perform_tasks extends TimerTask{
		public void run() {
//			LOG.finest("Performing Task");
			
			//increment time
			tick += 1;
			
			/*Task Steps:
			 * 
			 * check unit status
			 * 		attack
			 * 		move
			 * 		idle
			 * modify unit status
			 * perform tasks in task list
			 */
			
			//check each unit's status and perform the appropriate action if the timing is right
			for(int i=0; i<MAX_UNITS; i++){
				Unit cur_unit = unit_list.unit_manager[i];
				if (cur_unit == null) continue;
				if(cur_unit.cur_state == STATE_ATTACKING){//if unit is attacking
					if(cur_unit.player!=0)attack(cur_unit);
				}
				if(cur_unit.cur_state == STATE_MOVING){//if unit is moving
					if(cur_unit.player!=0)aquire_target_move(cur_unit);
				}
				if(cur_unit.cur_state == STATE_IDLE){//if unit is idle
					if(cur_unit.player!=0)aquire_target(cur_unit);
				}
				do_special(cur_unit);
			}
			//do all unit's next move
			move();
			//now do tasks in the task list
			for(int i=0; i<task_list.size(); i++){
				do_task(task_list.remove(i));
			}
			
			//send update to all clients
			sendUpdate();
			//System.out.println("update: " + new Date().getTime());
		}

	}
	
	private void attack(Unit unit){
		//check if there is a target
		if(unit.target_id>=0){
			//check if target exists
			//POSSIBLE BUG - unit dies at same time as a unit is made and this.attacks the new unit
			if(unit_list.unit_manager[unit.target_id]!=null){
				//check if target is within range
				if(Math.pow(Math.pow(Math.abs(unit_list.unit_manager[unit.target_id].cur_pos[0]-unit.cur_pos[0]), 2) + 
						Math.pow(Math.abs(unit_list.unit_manager[unit.target_id].cur_pos[0]-unit.cur_pos[0]), 2), .5)<=unit.rng){
					//check if unit can attack (once per second)
					if((unit.attk_time + 10) <= tick){
						//attack formula: ((att-def)*10)^.5 * 10 up to 5 and then opposite of that to 10
						int attk=unit.dam - unit_list.unit_manager[unit.target_id].def;
						unit_list.unit_manager[unit.target_id].cur_energy-=attack_table[attk];
						//self heal from feeding on opponent
						unit.cur_energy += (int)(attk*1.6);
						System.out.println("Unit " + unit.id + " (" + unit.cur_energy + ") attacks enemy " + unit.target_id + " (" + unit_list.unit_manager[unit.target_id].cur_energy + ")");
						//reset attack timer
						unit.attk_time=tick;
						//target death check
						if(unit_list.unit_manager[unit.target_id].cur_energy<1){
							path_list.clear_unit_path(unit.target_id);//delete target moves
							unit_list.unit_manager[unit.target_id]=null;//delete target
							unit.target_id=-1;//set no target
							unit.cur_state = STATE_IDLE;//set to idle
						}
						//tell unit to stop moving
						path_list.clear_unit_path(unit.id);
						return;
					}
				}
			}
			//else change target to -1 (cause target unit is dead)
			else{
				unit.target_id=-1;
			}
		}
		//else set to idle
		unit.cur_state = STATE_IDLE;
		unit.target_id=-1;
	}
	
	private void aquire_target(Unit unit){
		float[] pos = unit.cur_pos;
		int target=-1;
		double closest=unit.rng*2;
		
		for(int i=0; i<unit_list.unit_manager.length-1; i++){//acquire closest target
			//if not an ally unit (right now there are no forces)
			if(unit_list.unit_manager[i]!=null && unit_list.unit_manager[i].player!=unit.player){
				//if within range see if its better than current best closest unit
				if(Math.abs(pos[0]-unit_list.unit_manager[i].cur_pos[0])<=unit.rng && Math.abs(pos[1]-unit_list.unit_manager[i].cur_pos[1])<=unit.rng){
					if(Math.abs(pos[0]-unit_list.unit_manager[i].cur_pos[0])+Math.abs(pos[1]-unit_list.unit_manager[i].cur_pos[1])<closest){
						closest=Math.abs(pos[0]-unit_list.unit_manager[i].cur_pos[0])+Math.abs(pos[1]-unit_list.unit_manager[i].cur_pos[1]);
						target=unit_list.unit_manager[i].id;
					}
				}
			}
		}
		unit.target_id=target;//set target
		if(unit.target_id!=-1){unit.cur_state = STATE_ATTACKING;}//set if attacking
	}
	
	private void aquire_target_move(Unit unit){
		float[] pos = unit.cur_pos;
		int target=-1;
		double closest=1;
		
		for(int i=0; i<unit_list.unit_manager.length-1; i++){//acquire closest target
			//if not an ally unit (right now there are no forces)
			if(unit_list.unit_manager[i]!=null && unit_list.unit_manager[i].player!=unit.player){
				//if within range see if its better than current best closest unit
				if(Math.abs(pos[0]-unit_list.unit_manager[i].cur_pos[0])<=1 && Math.abs(pos[1]-unit_list.unit_manager[i].cur_pos[1])<=1){
					if(Math.abs(pos[0]-unit_list.unit_manager[i].cur_pos[0])+Math.abs(pos[1]-unit_list.unit_manager[i].cur_pos[1])<closest){
						closest=Math.abs(pos[0]-unit_list.unit_manager[i].cur_pos[0])+Math.abs(pos[1]-unit_list.unit_manager[i].cur_pos[1]);
						target=unit_list.unit_manager[i].id;
					}
				}
			}
		}
		unit.target_id=target;//set target
		if(unit.target_id!=-1){unit.cur_state = STATE_ATTACKING;}//set if attacking
	}
	
	private void move(){
		ArrayList<PathManager.Unit_Move> moves=path_list.get_next_move_phase(unit_list);
		if (moves == null) return;
		for(int i=0; i<moves.size(); i++){
			//calculate unit offset (t_coor offset + movement step)
			double step =  1 / (double)moves.get(i).age;
			double[] offset = {moves.get(i).next_coor_t[0]-moves.get(i).cur_coor_t[0], moves.get(i).next_coor_t[1]-moves.get(i).cur_coor_t[1]};
			if(moves.get(i).final_dest!=null){//if this move is the final move, then change offset
				offset[0] = moves.get(i).final_dest[0]-moves.get(i).cur_coor_t[0];
				offset[1] = moves.get(i).final_dest[1]-moves.get(i).cur_coor_t[1];
			}
			offset[0]*=step;
			offset[1]*=step;
			//add offset to cur_pos of unit
			unit_list.unit_manager[moves.get(i).u_id].cur_pos[0]+=offset[0];
			unit_list.unit_manager[moves.get(i).u_id].cur_pos[1]+=offset[1];
			//change t_pos of unit
			unit_list.unit_manager[moves.get(i).u_id].t_pos[0]=(int)unit_list.unit_manager[moves.get(i).u_id].cur_pos[0];
			unit_list.unit_manager[moves.get(i).u_id].t_pos[1]=(int)unit_list.unit_manager[moves.get(i).u_id].cur_pos[1];
		}
	}
	
	private void do_special(Unit unit){
		//do chloroplasts
		if(tick > (unit.spec_time+10)){
			if(unit.upgrades[12]==true){
				unit.cur_energy+=5;
				unit.spec_time=tick;
			}
			if(unit.upgrades[13]==true){
				unit.cur_energy+=3;
				unit.spec_time=tick;
			}
			if(unit.upgrades[14]==true){
				unit.cur_energy+=4;
				unit.spec_time=tick;
			}
		}
		//but set unit energy to max if its exceeded
		if(unit.cur_energy>300){
			unit.cur_energy=300;
		}
	}
	
	private void do_task(Task task){
		//if (1 == 1) return;
		switch (task.action) {
			case TASK_MOVE:{ //if task is move, add group to path manager
				int[] t_destination={(int)task.loc[0], (int)task.loc[1]};
				for(int i=0; i<task.id.length; i++){
					if(unit_list.unit_manager[task.id[i]]!=null){
						unit_list.unit_manager[task.id[i]].cur_state = STATE_MOVING;
						unit_list.unit_manager[task.id[i]].move_time = tick;
						path_list.add_path(unit_list.unit_manager[task.id[i]].t_pos, t_destination, task.id[i], unit_list.unit_manager[task.id[i]].spd, tick, task.loc, unit_list.unit_manager[task.id[i]].cur_pos);
						//System.out.println("command recieved: " + new Date().getTime());
					}
				}
			} break;
			case TASK_ATTACKMOVE:{ //if task is attack move, add group to path manager and set status to attack
				int[] t_destination={(int)task.loc[0], (int)task.loc[1]};
				for(int i=0; i<task.id.length; i++){
					if(unit_list.unit_manager[task.id[i]]!=null){
						unit_list.unit_manager[task.id[i]].cur_state = STATE_ATTACKING;
						path_list.add_path(unit_list.unit_manager[task.id[i]].t_pos, t_destination, task.id[i], unit_list.unit_manager[task.id[i]].spd, tick, task.loc, unit_list.unit_manager[task.id[i]].cur_pos);
					}
				}
			} break;
			case TASK_UPGRADE_FLAGELLA:{//flagella upgrade
				for(int i=0; i<task.id.length; i++){
					if(unit_list.unit_manager[task.id[i]]!=null){
						if(unit_list.unit_manager[task.id[i]].up_count<3){
							if(unit_list.unit_manager[task.id[i]].upgrades[0]==false){
								if(unit_list.unit_manager[task.id[i]].cur_energy>=130){
									unit_list.unit_manager[task.id[i]].upgrades[0]=true;
									unit_list.unit_manager[task.id[i]].spd+=2;
									unit_list.unit_manager[task.id[i]].cur_energy-=30;
									unit_list.unit_manager[task.id[i]].up_count+=1;
								}
							}
							else if(unit_list.unit_manager[task.id[i]].upgrades[1]==false){
								if(unit_list.unit_manager[task.id[i]].cur_energy>=150){
									unit_list.unit_manager[task.id[i]].upgrades[1]=true;
									unit_list.unit_manager[task.id[i]].spd+=1;
									unit_list.unit_manager[task.id[i]].cur_energy-=50;
									unit_list.unit_manager[task.id[i]].up_count+=1;
								}
							}
							else if(unit_list.unit_manager[task.id[i]].upgrades[2]==false){
								if(unit_list.unit_manager[task.id[i]].cur_energy>=180){
									unit_list.unit_manager[task.id[i]].upgrades[2]=true;
									unit_list.unit_manager[task.id[i]].spd+=2;
									unit_list.unit_manager[task.id[i]].cur_energy-=80;
									unit_list.unit_manager[task.id[i]].up_count+=1;
								}
							}
						}
					}
				}
			} break;
			case TASK_UPGRADE_CELLWALL:{//cell wall upgrade
				for(int i=0; i<task.id.length; i++){
					if(unit_list.unit_manager[task.id[i]]!=null){
						if(unit_list.unit_manager[task.id[i]].up_count<3){
							if(unit_list.unit_manager[task.id[i]].upgrades[3]==false){
								if(unit_list.unit_manager[task.id[i]].cur_energy>=130){
									unit_list.unit_manager[task.id[i]].upgrades[3]=true;
									unit_list.unit_manager[task.id[i]].def+=1;
									unit_list.unit_manager[task.id[i]].spd-=1;
									unit_list.unit_manager[task.id[i]].cur_energy-=30;
									unit_list.unit_manager[task.id[i]].up_count+=1;
								}
							}
							else if(unit_list.unit_manager[task.id[i]].upgrades[4]==false){
								if(unit_list.unit_manager[task.id[i]].cur_energy>=150){
									unit_list.unit_manager[task.id[i]].upgrades[4]=true;
									unit_list.unit_manager[task.id[i]].spd-=2;
									unit_list.unit_manager[task.id[i]].def+=2;
									unit_list.unit_manager[task.id[i]].cur_energy-=50;
									unit_list.unit_manager[task.id[i]].up_count+=1;
								}
							}
							else if(unit_list.unit_manager[task.id[i]].upgrades[5]==false){
								if(unit_list.unit_manager[task.id[i]].cur_energy>=180){
									unit_list.unit_manager[task.id[i]].upgrades[5]=true;
									unit_list.unit_manager[task.id[i]].spd-=3;
									unit_list.unit_manager[task.id[i]].def+=4;
									unit_list.unit_manager[task.id[i]].cur_energy-=80;
									unit_list.unit_manager[task.id[i]].up_count+=1;
								}
							}
						}
					}
				}
			} break;
			case TASK_UPGRADE_CILIA:{//cilia upgrade
				for(int i=0; i<task.id.length; i++){
					if(unit_list.unit_manager[task.id[i]]!=null){
						if(unit_list.unit_manager[task.id[i]].up_count<3){
							if(unit_list.unit_manager[task.id[i]].upgrades[6]==false){
								if(unit_list.unit_manager[task.id[i]].cur_energy>=140){
									unit_list.unit_manager[task.id[i]].upgrades[6]=true;
									unit_list.unit_manager[task.id[i]].dam+=1;
									unit_list.unit_manager[task.id[i]].cur_energy-=40;
									unit_list.unit_manager[task.id[i]].up_count+=1;
								}
							}
							else if(unit_list.unit_manager[task.id[i]].upgrades[7]==false){
								if(unit_list.unit_manager[task.id[i]].cur_energy>=160){
									unit_list.unit_manager[task.id[i]].upgrades[7]=true;
									unit_list.unit_manager[task.id[i]].dam+=1;
									unit_list.unit_manager[task.id[i]].spd+=1;
									unit_list.unit_manager[task.id[i]].cur_energy-=60;
									unit_list.unit_manager[task.id[i]].up_count+=1;
								}
							}
							else if(unit_list.unit_manager[task.id[i]].upgrades[8]==false){
								if(unit_list.unit_manager[task.id[i]].cur_energy>=200){
									unit_list.unit_manager[task.id[i]].upgrades[8]=true;
									unit_list.unit_manager[task.id[i]].dam+=1;
									unit_list.unit_manager[task.id[i]].spd+=1;
									unit_list.unit_manager[task.id[i]].cur_energy-=100;
									unit_list.unit_manager[task.id[i]].up_count+=1;
								}
							}
						}
					}
				}
			} break;
			case TASK_UPGRADE_LYSOSOME:{//lysosome ejection upgrade
				for(int i=0; i<task.id.length; i++){
					if(unit_list.unit_manager[task.id[i]]!=null){
						if(unit_list.unit_manager[task.id[i]].up_count<3){
							if(unit_list.unit_manager[task.id[i]].upgrades[9]==false){
								if(unit_list.unit_manager[task.id[i]].cur_energy>=140){
									unit_list.unit_manager[task.id[i]].upgrades[9]=true;
									unit_list.unit_manager[task.id[i]].rng+=1;
									unit_list.unit_manager[task.id[i]].cur_energy-=40;
									unit_list.unit_manager[task.id[i]].up_count+=1;
								}
							}
							else if(unit_list.unit_manager[task.id[i]].upgrades[10]==false){
								if(unit_list.unit_manager[task.id[i]].cur_energy>=160){
									unit_list.unit_manager[task.id[i]].upgrades[10]=true;
									unit_list.unit_manager[task.id[i]].rng+=1;
									unit_list.unit_manager[task.id[i]].cur_energy-=60;
									unit_list.unit_manager[task.id[i]].up_count+=1;
								}
							}
							else if(unit_list.unit_manager[task.id[i]].upgrades[11]==false){
								if(unit_list.unit_manager[task.id[i]].cur_energy>=200){
									unit_list.unit_manager[task.id[i]].upgrades[11]=true;
									unit_list.unit_manager[task.id[i]].rng+=1;
									unit_list.unit_manager[task.id[i]].cur_energy-=100;
									unit_list.unit_manager[task.id[i]].up_count+=1;
								}
							}
						}
					}
				}
			} break;
			case TASK_UPGRADE_CHLOROPLAST:{ //chloroplast upgrade
				for(int i=0; i<task.id.length; i++){
					if(unit_list.unit_manager[task.id[i]]!=null){
						if(unit_list.unit_manager[task.id[i]].up_count<3){
							if(unit_list.unit_manager[task.id[i]].upgrades[12]==false){
								if(unit_list.unit_manager[task.id[i]].cur_energy>=150){
									unit_list.unit_manager[task.id[i]].upgrades[12]=true;
									unit_list.unit_manager[task.id[i]].spd-=2;
									unit_list.unit_manager[task.id[i]].def-=1;
									unit_list.unit_manager[task.id[i]].cur_energy-=50;
									unit_list.unit_manager[task.id[i]].up_count+=1;
								}
							}
							else if(unit_list.unit_manager[task.id[i]].upgrades[13]==false){
								if(unit_list.unit_manager[task.id[i]].cur_energy>=220){
									unit_list.unit_manager[task.id[i]].upgrades[13]=true;
									unit_list.unit_manager[task.id[i]].def-=1;
									unit_list.unit_manager[task.id[i]].cur_energy-=120;
									unit_list.unit_manager[task.id[i]].up_count+=1;
								}
							}
							else if(unit_list.unit_manager[task.id[i]].upgrades[14]==false){
								if(unit_list.unit_manager[task.id[i]].cur_energy>=300){
									unit_list.unit_manager[task.id[i]].upgrades[14]=true;
									unit_list.unit_manager[task.id[i]].def-=1;
									unit_list.unit_manager[task.id[i]].cur_energy-=200;
									unit_list.unit_manager[task.id[i]].up_count+=1;
								}
							}
						}
					}
				}
			} break;
			case TASK_DIVIDE: {
				for(int i=0; i<task.id.length; i++){
					if(unit_list.unit_manager[task.id[i]]!=null){
						if(unit_list.unit_manager[task.id[i]].cur_energy>=200){
							unit_list.new_unit_bud(unit_list.unit_manager[task.id[i]].id, unit_list.unit_manager[task.id[i]].player);
							unit_list.unit_manager[task.id[i]].cur_energy-=100;
						}
					}
				}
			} break;
		}
	}
	
	private class MsgData_Command {
		long tick;
		byte command;
		byte unitnums;
		int uid[];
		float destx;
		float desty;
		short targetid;
	}
	
}
