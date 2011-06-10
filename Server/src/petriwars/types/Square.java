package petriwars.types;

import java.util.ArrayList;

import petriwars.types.Unit;

public class Square
{
    private Obstacle obstacle;
    private ArrayList<Unit> units;
    private int x;
    private int y;
    private byte type;
    
    public Square(byte t, int x1, int y1)
    {
        type = t;
        x = x1;
        y = y1;
        obstacle = null;
        units = new ArrayList<Unit>();
    }
    
    public int getX() {return x;}
    public int getY() {return y;}
    
    
    public Obstacle getObstacle()
    {
        return obstacle;
    }
    
    public boolean setObstacle(Obstacle o)
    {
        if (obstacle == null)
        {
            obstacle = o;
            return true;
        }
        return false;
    }
    
    public boolean addUnit(Unit u)
    {
        return units.add(u);
    }
    
    public boolean removeUnit(Unit u)
    {
        return units.remove(u);
    }
    
    public ArrayList<Unit> getUnits()
    {
     return units;
    }
    
    public boolean nullObstacle()
    {
        return obstacle == null;
    }
}