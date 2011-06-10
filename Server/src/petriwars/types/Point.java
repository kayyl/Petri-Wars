package petriwars.types;

public class Point {
    double x, y;
    
    public Point(double X, double Y){
        x = X; 
        y = Y;
    }
    
    public double distance(Point p) {
        return Math.sqrt((x - p.x) * (x - p.x) + (y - p.y) * (y - p.y));
    }
}
