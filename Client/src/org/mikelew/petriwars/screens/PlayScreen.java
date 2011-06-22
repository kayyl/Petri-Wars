package org.mikelew.petriwars.screens;

import static org.mikelew.petriwars.PetriClient.PW_SCALE;

import org.mikelew.petriwars.PetriClient;
import org.mikelew.petriwars.terrain.PetriTerrain;
import org.openmali.vecmath2.Colorf;
import org.xith3d.scenegraph.primitives.Rectangle;

public class PlayScreen extends GameScreen {
	private PetriTerrain terrain;
	
	public PlayScreen() {
		super(true);
		
		//this.getRenderPass().getConfig().setBackClipDistance(1000);
		//this.getRenderPass().getConfig().setFrontClipDistance(-1000);
		
		
		terrain = new PetriTerrain(new int[10][10]);
		scene.addChild(terrain);
		
		scene.addChild(new Rectangle(PW_SCALE, PW_SCALE, Colorf.BLUE));
		
	}
	@Override protected void initScreen() {
		//camera.getLookAtPosition().setY(-10);
		camera.getEyePosition().setZ(100);
		camera.getEyePosition().setY(100);
		camera.getEyePosition().setX(50);
		
		camera.update();
	}
	@Override protected void deinitScreen() {}
	@Override public void runFrame() {}
}
