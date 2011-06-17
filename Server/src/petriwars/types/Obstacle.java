package petriwars.types;

import java.util.ArrayList;

public class Obstacle {
    private final byte OBSTACLE = 0x00;
    private ArrayList<Square> squares;
    private ArrayList<Point> corners;
    
    public Obstacle(byte[][] map_raw, Square[][] map, int x, int y)
    {
        squares = new ArrayList<Square>();
        corners = new ArrayList<Point>();
        grow(map_raw, map, x, y);
    }
    
    public void grow(byte[][] map_raw, Square[][] map, int x, int y)
    {
        squares.add(map[y][x]);
        map[y][x].setObstacle(this);
        int w = map[0].length;
        int h = map.length;
        int nw = 0, wn = 0, ne = 0, en = 0, se = 0, es = 0, sw = 0, ws = 0;

        if (isEmpty(x - 1, y - 1, w, h, map_raw, map))
        {
            nw++;
            ne++;
            wn++;
            ws++;
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
            nw++;
            en++;
            es++;
        }
        if (isEmpty(x - 1, y, w, h, map_raw, map))
        {
            sw++;
            ws++;
            nw++;
            wn++;
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
            se++;
            ws++;
            wn++;
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
            sw++;
            es++;
            en++;
        }

        if ((nw == 4 || wn == 4) && notHasCorner(x - 0.5, y - 0.5))
            corners.add(new Point(x - 0.5, y - 0.5));
        if ((ne == 4 || en == 4) && notHasCorner(x - 0.5, y - 0.5))
            corners.add(new Point(x + 1.5, y - 0.5));
        if ((sw == 4 || ws == 4) && notHasCorner(x - 0.5, y - 0.5))
            corners.add(new Point(x - 0.5, y + 1.5));
        if ((se == 4 || es == 4) && notHasCorner(x - 0.5, y - 0.5))
            corners.add(new Point(x + 1.5, y + 1.5));
    }
    
    private boolean isEmpty(int x, int y, int w, int h, byte[][] m, Square[][] m2)
    {
        if (x < 0 || y < 0 || x >= w || y >= h)
            return false;
        if (m[y][x] == OBSTACLE)
        {
            if (m2[y][x].nullObstacle())
                grow(m, m2, x, y);
            return false;
        }
        return true;
    }


    public boolean notHasCorner(int x, int y)
    {
        for (Point p : points)
            if ((int)p.x == x && (int)p.y == y)
                return false;
        return true;
    }
    
    public ArrayList<Point> getCorners()
    {
        return corners;
    }
}