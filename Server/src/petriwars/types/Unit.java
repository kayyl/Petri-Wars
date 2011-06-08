package petriwars.types;

public class Unit {
	
	public int id;
	
	public double cur_x, cur_y;
	public double momentum[];
	public double impulse;
	public double direction[];
	
	public float att;
	public float def;
	public float spd;
	public float rng;
	
	public Unit(double x, double y){
		cur_x = x;
		cur_y =y;
	}
}