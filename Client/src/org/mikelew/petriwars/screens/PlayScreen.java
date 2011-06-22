package org.mikelew.petriwars.screens;

import org.mikelew.petriwars.PetriClient;
import org.mikelew.petriwars.terrain.PetriTerrain;

public class PlayScreen extends GameScreen {
	private PetriTerrain terrain;
	
	public PlayScreen() {
		super(true);
		
		terrain = new PetriTerrain(new int[10][10]);
		scene.addChild(terrain);
		
	}
	@Override protected void initScreen() {
		camera.getLookAtPosition().setY(-10);
		camera.getEyePosition().setZ(-100);
		camera.update();
	}
	@Override protected void deinitScreen() {}
	@Override public void runFrame() {}
}
