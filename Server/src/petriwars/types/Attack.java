package petriwars.types;

public class Attack implements Task {
	private Unit attacker, defender;
	private Mode mode;
	
	enum Mode {
		SOFT,
		HARD
	}
	
	// Constructor for Mode.SOFT
	public Attack (Unit a) {
		attacker = a;
		mode = Mode.SOFT;
	}
	
	// Constructor for Mode.HARD
	public Attack (Unit a, Unit d) {
		attacker = a;
		defender = d;
		mode = Mode.HARD;
	}
	
	public void exec () {
		if (mode = Mode.HARD) {			
			// Check if defender is in range of attacker
			if (defender.inRange(a.currentPosition)) {
				// Attack
				d.health -= a.att;
				if (d.health < 0) {
					d.alive = false;
				}
			} else {
				// Move
				
			}
		}
	}
}