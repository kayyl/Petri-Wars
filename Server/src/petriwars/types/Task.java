package petriwars.types;


public class Task {
	/*TASKS:
	 * 
	 * Move -> m
	 * Attack Move -> a
	 * Get Upgrade -> (whatever the upgrade hotkey is)
	 * 		flagella - >f
	 * 		cell wall ->c
	 * 		cilia	 - >e
	 * 		lysosome - >r
	 * 		chrolplast->v
	 * 		divide   - >d
	 */
	
	public static final int 
		TASK_STOP = 0,
		TASK_MOVE = 1,
		TASK_ATTACKMOVE = 2,
		TASK_UPGRADE_FLAGELLA = 3,
		TASK_UPGRADE_CELLWALL = 4,
		TASK_UPGRADE_CILIA = 5,
		TASK_UPGRADE_LYSOSOME = 6,
		TASK_UPGRADE_CHLOROPLAST = 7,
		TASK_DIVIDE = 8;
	
	public int id[];
	public int action;
	public float loc[];
	
	public Task(int u_id[], int a, float l[]){
		id=u_id;
		action=a;
		loc=l;
	}
}
