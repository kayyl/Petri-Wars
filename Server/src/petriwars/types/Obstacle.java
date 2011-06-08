package petriwars.types;

import java.util.ArrayList;

public class Obstacle {
    private final byte OBSTACLE = 0x00;
    private ArrayList<Square> squares;
    private ArrayList<Points> corners;
    
    public Obstacle(byte[][] map_raw, Square[][] map, int x, int y)
    {
        squares = new ArrayList<Square>();
        corners = new ArrayList<Points>();
        grow(map_raw, map, x, y);
    }
    
    public void grow(byte[][] map_raw, Square[][] map, int x, int y)
    {
        squares.add(map[y][x]);
        map[y][x].addObstacle(this);
        int w = map[0].length;
        int h = map.length;
        int nw, wn, ne, en, se, es, sw, ws;

        if (isEmpty(x - 1, y - 1, w, h, map_raw, map))
        {
            nw++;
            wn++;
        }
        if (isEmpty(x, y - 1, w, h, map_raw, map))
        {
            nw++;
            wn++; 
            ne++;
            en++;
        }
        if (isEmpty(x + 1, y - 1, w, h, map_raw, map))
        {
            ne++;
            en++;
        }
        if (isEmpty(x - 1, y, w, h, map_raw, map))
        {
            sw++;
            ws++;
            ne++;
            en++;
        }
        if (isEmpty(x + 1, y, w, h, map_raw, map))
        {
            se++;
            es++;
            ne++;
            en++;
        }
        if (isEmpty(x - 1, y + 1, w, h, map_raw, map))
        {
            sw++;
            ws++;
        }
        if (isEmpty(x, y + 1, w, h, map_raw, map))
        {
            se++;
            es++;
            sw++;
            ws++;
        }
        if (isEmpty(x + 1, y + 1, w, h, map_raw, map))
        {
            se++;
            es++;
        }
        
        if (nw == 4 || wn == 4)
            corners.add(new Point(x - 0.5, y - 0.5));
        if (ne == 4 || en == 4)
            corners.add(new Point(x + 0.5, y - 0.5));
        if (sw == 4 || ws == 4)
            corners.add(new Point(x - 0.5, y + 0.5));
        if (se == 4 || es == 4)
            corners.add(new Point(x + 0.5, y + 0.5));
    }
    
    private boolean isEmpty(int x, int y, int w, int h, byte[][] m, Square[][] m2)
    {
        if (x < 0 || y < 0 || x >= w || y >= h)
            return false;
        if ([y][x] == OBSTACLE)
        {
            grow(m, m2, x, y);
            return false;
        }
        return true;
    }
}