import java.util.Timer;

public class ServerManager {
    Map map;
    WaypointManager waypointManager;
    //Player[] players;
    
    public static void main(String[] args) {
        map = new Map("test3.bmp");
        waypointManager = new WaypointManager();
        waypointManager.path_list.add(new Path(0.0, 10.0, 10.0, 0.0));
        
        map.testMap();
        System.out.println();
        
        String[][] output = map.getTestRepresentation(false);
        ArrayList<WP> waypoints = waypointManager.path_list.get(0).waypt_list;
        for (WP w : waypoints)
            output[(int)w.loc.y][(int)w.loc.x] = "x";
        map.printRepresentation(output);
    }
}