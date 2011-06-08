package petriwars.types;

public class Unit {
	
	public int id;
	
	public Point currentPosition;
	public Impulse impulse;
	public double direction[];
	
	public int health;
	public double mass;
	public double att;
	public double def;
	public double spd;
	public double rng;
	
	public boolean alive;
	
	public Unit(double x, double y, double d[]){
		currentPosition = new Point(x, y);
		impulse = new Impulse();
		direction = d[];
		
		health = 100;
		mass = 1;
		att = 5;
		def = 0;
		spd = 5;
		rng = 1;	
		
		alive = true;
	}
	
	public boolean inRange(Point enemyPosition) {
		double distance = currentPosition.distance(enemyPosition);
		
		if (distance < rng) {
			return true;
		} else {
			return false;
		}
	}
}