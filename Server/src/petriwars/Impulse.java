package petriwars;

import java.util.ArrayList;

public class Impulse {
	double mass, velocity;
	double impulse;
	public Impulse(Unit unit){
		//get the mass and velocity from the unit upgrades
		velocity = unit.spd;
		mass = 1;//TODO get mass based on upgrades
		impulse = mass * velocity;
		unit.impulse *= impulse;
	}
	
	public double[] normalize(double[] vec){
		double mag = Math.sqrt(vec[0] * vec[0] + vec[1] * vec[1]);
		double sign_x=1, sign_y=1;
		if(vec[0]<0) sign_x=-1;if(vec[1]<0) sign_y=-1;
		vec[0]/=mag*sign_x;
		vec[1]/=mag*sign_y;
		return vec;
	}
	
	public ArrayList<Unit> get_collisions(Unit unit){
		double coors[] = {unit.cur_x, unit.cur_y};
		ArrayList<Unit> cols = new ArrayList<Unit>();
		
		//TODO need list of all units (aka a unit manager)
		
		return null;
	}
	
	public void compute_momentum(Unit unit){
		ArrayList<Unit> cols = get_collisions(unit);
		double[][] imps = new double[cols.size()][2];
		double[] momentum = {0,0};
		//standard "push" = norm(sum((norm(col.dir + (col.loc-unit.loc)) * col.imp)))
		//get list of impulses
		for(int i=0; i<cols.size(); i++){
			//get norm impulse vector
			imps[i][0] = cols.get(i).direction[0]+(cols.get(i).cur_x - unit.cur_x);
			imps[i][1] = cols.get(i).direction[1]+(cols.get(i).cur_y - unit.cur_y);
			imps[i] = normalize(imps[i]);
			//mult by momentum
			imps[i][0]*=cols.get(i).impulse;
			imps[i][1]*=cols.get(i).impulse;
		}
		//add all of the impulses together
		for(int i=0; i<imps.length; i++){
			momentum[0] += imps[i][0];
			momentum[1] += imps[i][1];
		}
		//add the original unit's impulse
		//DONE!
		unit.momentum[0] = unit.direction[0]*unit.impulse + momentum[0];
		unit.momentum[1] = unit.direction[1]*unit.impulse + momentum[1];
	}
}
