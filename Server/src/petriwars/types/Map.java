package petriwars.types;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import petriwars.FileParser;

public class Map {
    private final byte OBSTACLE = 0x00;
    private Square[][] map;
    private String map_name;
    private int height;
    private int width;
    private ArrayList<Obstacle> obstacles;
    
    public Map(String url) {
        File map_file = new File(url);
        map_name = map_file.getName();
        byte[][] map_raw = FileParser.getByteMap(map_file);
        
        height = map_raw.length;
        width = map_raw[0].length;
        map = new Square[y][x];
        obstacles = new ArrayList<Obstacle>();
        for (int y = 0; y < height; y++)
            for (int x = 0; x < width; x++)
            map[y][x] = new Square(map_raw[y][x]);
        
        for (int y = 0; y < height; y++)
            for (int x = 0; x < width; x++)
            if (map_raw[y][x] == OBSTACLE && map[y][x].obstacle == NULL)
            map[y][x].obstacle = new Obstacle(map_raw, map, x, y);
        obstacles.add(map[y][x].obstacle);
        
    }
    
    public ArrayList<Unit> getUnitsAt(int x, int y) {
        return map[y][x].getUnits();
    }
    
    public Obstacle getObstacleAt(int x, int y) {
        return map[y][x].getObstacle();
    }
    
    public String getName() {
        return map_name;
    }
    
    public int getHeight() {
        return height;
    }
    
    public int getWidth() {
        return width;
    }
    
    public Square getSquare(int x, int y) {
        return map[y][x];
    }
    
}