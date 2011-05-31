public class Square
{
    private Obstacle obstacle;
    private ArrayList<Unit> units;
    private int x;
    private int y;
    
    public Square(int x1, int y1)
    {
        x = x1;
        y = y1;
        obstacle = null;
        units = new ArrayList<Unit>();
    }
    
    public Obstacle getObstacle()
    {
        return obstacle;
    }
    
    public boolean addObstacle(Obstacle o)
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
}