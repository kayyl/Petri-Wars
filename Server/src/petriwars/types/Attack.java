package petriwars.types;

public class SoftAttack implements Task {
 private Unit attacker, defender;
 private Mode mode;

 // Constructor for Mode.HARD
 public Attack (Unit a, Unit d) {
  attacker = a;
  defender = d;
  mode = Mode.HARD;
 }
 
 public void exec () {

   // Check if defender is in range of attacker
   if (defender.inRange(attacker.currentPosition)) {
    // Attack
    defender.health -= attacker.att;
    if (defender.health < 0) {
     defender.alive = false;
    }
   }
   else{
       attacker.cancelAttack();
   }
   
   
  
 }
}

public class HardAttack implements Task {
 private Unit attacker, defender;
 private Mode mode;
 
 
 // Constructor for Mode.HARD
 public Attack (Unit a, Unit d) {
  attacker = a;
  defender = d;
  mode = Mode.HARD;
 }
 
 public void exec () {

   // Check if defender is in range of attacker
   if (defender.inRange(attacker.currentPosition)) {
    // Attack
    defender.health -= attacker.att;
    if (defender.health < 0) {
     defender.alive = false;
    }
   } else {
    // Move
    attacker.move(defender.currentPosition);
   }
  
 }
}