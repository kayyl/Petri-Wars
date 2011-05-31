public class Square
{
    private Obstacle wall;
    private ArrayList<Unit> units;
    private int x;
    private int y;
    
    public Square(int x1, int y1)
    {
        x = x1;
        y = y1;
        wall = null;
        units = new ArrayList<Unit>();
    }
    
    public Obstacle getWall()
    {
        return wall;
    }
    
    public boolean addWall(Obstacle w)
    {
        if (wall == null)
        {
            wall = w;
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
}