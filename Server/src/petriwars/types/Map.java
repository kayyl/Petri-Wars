package petriwars.types;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class Map {
	private Square[][] map;
	private String map_name;
	private int height;
private int width;
	public Map(String url){
		try {
			if(get_map_from_file(url)==false){}
		} catch (IOException e) {
			e.printStackTrace();
		}											//something is wrong with the map
		
	}

        public ArrayList<Unit> getUnitsAt(int x, int y)
{
return map[y][x].getUnits();
}

        public Obstacle getObstacleAt(int x, int y)
{
return map[y][x].getObstacle();
}
	
	public char[][] get_map(){
		return map;
	}
	
	public int get_map_size(){
		return map_size;
	}
	
	public char get_map_loc(int row, int col){
		return map[col][row];
	}
	
	public void set_map_to_normal(int col, int row){
		map[col][row]='1';
	}
	
	private boolean get_map_from_file(String url) throws IOException{
File map_file = new File(url);
map_name = map_file.getName();
byte[][] map_raw = FileParser.getByteMap(map_file);

height = map_raw.length;
width = map_raw[0].length;
map = new Square[y][x];
for (int y = 0; y < height; y++) 
for (int x = 0; x < width; x++) 
map[y][x] = new Square(map_raw[y][x]);

for (int y = 0; y < height; y++) 
for (int x = 0; x < width; x++) 
if (map_raw[y][x] == OBSTACLE && map[y][x].obstacle == NULL)
map[y][x].obstacle = new Obstacle(map_raw, map, x, y);

return true;
	}
}
