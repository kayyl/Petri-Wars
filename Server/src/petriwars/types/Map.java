package petriwars.types;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class Map {
	private char[][] map;
	private String map_name="";
	private int map_size=0;
	public Map(String url){
		try {
			if(get_map_from_file(url)==false){}
		} catch (IOException e) {
			e.printStackTrace();
		}											//something is wrong with the map
		
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
		List<String> map_raw = readFileAsListOfStrings(url);
		String line;
		//CHECK FOR CORRECT FORMAT
		line=map_raw.remove(0);
		if(line.compareTo("#Petri Wars Map")>0){return false;}//bad map header
		line=map_raw.remove(0);
		if(line.startsWith("#size ")==false){return false;}//bad map header
		map_size=Integer.parseInt(line.substring(6));
		line=map_raw.remove(0);
		if(line.startsWith("#name ")==false){return false;}//bad map header
		map_name=line.substring(6);
		line=map_raw.remove(0);
		if(line.startsWith("#content")==false){return false;}//bad map header
		
		map = new char[map_size][map_size];
		
		//Now, read in map
		for(int i=0; i<map_size; i++){
			for(int j=0; j<map_size; j++){
				map[i][j]=map_raw.get(i).charAt(j);
			}
		}
		return true;
	}
	
	private static List<String> readFileAsListOfStrings(String url) throws IOException
	  {
	    List<String> records = new ArrayList<String>();
	    BufferedReader reader = new BufferedReader(new FileReader(url));
	    String line;
	    while ((line = reader.readLine()) != null)
	    {
	      records.add(line);
	    }
	    reader.close();
	    return records;
	  }
}
