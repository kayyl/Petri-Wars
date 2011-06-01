package petriwars.types;

import java.util.ArrayList;


public class Obstacle {
	private final byte OBSTACLE = 0x00;
	private ArrayList<Square> squares;
	private ArrayList<Square> corners;

	public Obstacle(byte[][] map_raw, Square[][] map, int x, int y)
	{
		squares = new ArrayList<Square>();
		corners = new ArrayList<Square>();
		grow(map_raw, map, x, y);
	}

	public void grow(byte[][] map_raw, Square[][] map, int x, int y)
	{
		squares.add(map[y][x]);
		map[y][x].addObstacle(this);
		if (x < map[0].length - 1 && map_raw[y][x + 1] == OBSTACLE)
			grow(map_raw, map, x + 1, y);


		if (y < map.length - 1)
		{
			if (map_raw[y + 1][x] == OBSTACLE)
				grow(map_raw, map, x, y + 1);
			if (x > 0 && map_raw[y + 1][x - 1] == OBSTACLE)
				grow(map_raw, map, x - 1, y + 1);
			if (x < map[0].length - 1 && map_raw[y + 1][x + 1] == OBSTACLE)
				grow(map_raw, map, x + 1, y + 1);
		}

	}