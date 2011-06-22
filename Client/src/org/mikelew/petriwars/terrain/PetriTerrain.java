package org.mikelew.petriwars.terrain;

import org.openmali.vecmath2.Colorf;
import org.xith3d.scenegraph.Group;
import org.xith3d.scenegraph.Node;
import org.xith3d.scenegraph.TransformGroup;
import org.xith3d.scenegraph.primitives.Rectangle;

import static org.mikelew.petriwars.PetriClient.PW_SCALE;

public class PetriTerrain extends Group {
	private TransformGroup[][] tile;
	
	public PetriTerrain(int map[][]){
		tile = new TransformGroup[map.length][map[0].length];
		
		for (int x = 0; x < map.length; x++){
			for (int y = 0; y < map[x].length; y++){
				TransformGroup tg = new TransformGroup();
				tg.getTransform().rotX((float) -(Math.PI/2));
				tg.getTransform().setTranslation(x*PW_SCALE, 0, y*PW_SCALE);
				Node n = getNodeForType(map[x][y]);
				tg.addChild(n);
				this.addChild(tg);
			}
		}
	}
	
	public Node getTileAt(int x, int y){
		if (x < 0 || x > tile.length) return null;
		if (y < 0 || y > tile[x].length) return null;
		return tile[x][y];
	}
	
	protected Node getNodeForType(int type){
		return new Rectangle(PW_SCALE, PW_SCALE, Colorf.RED);
	}
}
