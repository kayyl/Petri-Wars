package org.mikelew.petriwars.screens;

import static org.mikelew.petriwars.PetriClient.PW_SCALE;
import static org.mikelew.petriwars.PetriClient.PW_CAMERA_EDGEBUFFER;
import static org.mikelew.petriwars.PetriClient.PW_CAMERA_MOVESPD;

import org.jagatoo.input.events.MouseMovedEvent;
import org.mikelew.petriwars.PetriClient;
import org.mikelew.petriwars.terrain.PetriTerrain;
import org.openmali.vecmath2.Colorf;
import org.openmali.vecmath2.Vector2f;
import org.xith3d.scenegraph.primitives.Rectangle;

public class PlayScreen extends GameScreen {
	private PetriTerrain terrain;
	
	private Vector2f cameraMovement;
	
	public PlayScreen() {
		super(true);
		
		//this.getRenderPass().getConfig().setBackClipDistance(1000);
		//this.getRenderPass().getConfig().setFrontClipDistance(-1000);
		
		
		terrain = new PetriTerrain(new int[10][10]);
		scene.addChild(terrain);
		
		scene.addChild(new Rectangle(PW_SCALE, PW_SCALE, Colorf.BLUE));
		
		cameraMovement = new Vector2f(0, 0);
	}
	@Override protected void initScreen() {
		//camera.getLookAtPosition().setY(-10);
		camera.getEyePosition().setZ(100);
		camera.getEyePosition().setY(100);
		//camera.getEyePosition().setX(50);
		
		camera.update();
	}
	@Override protected void deinitScreen() {}
	@Override public void runFrame() {
		
		camera.getEyePosition().addX(cameraMovement.getX());
		camera.getLookAtPosition().addX(cameraMovement.getX());
		camera.getEyePosition().addZ(cameraMovement.getY());
		camera.getLookAtPosition().addZ(cameraMovement.getY());
		camera.update();
	}
	
	@Override public void onMouseMoved(MouseMovedEvent e, int x, int y, int dx, int dy) {
		System.out.println("[Mouse Moved: x="+x+" y="+y+" dx="+dx+" dy="+dy+"]");
		if (x < PW_CAMERA_EDGEBUFFER) {
			cameraMovement.setX(-PW_CAMERA_MOVESPD);
		} else if (x > PetriClient.getInstance().getCanvasWidth() - PW_CAMERA_EDGEBUFFER){
			cameraMovement.setX(PW_CAMERA_MOVESPD);
		} else {
			cameraMovement.setX(0);
		}
		
		if (y < PW_CAMERA_EDGEBUFFER) {
			cameraMovement.setY(-PW_CAMERA_MOVESPD);
		} else if (y > PetriClient.getInstance().getCanvasHeight() - PW_CAMERA_EDGEBUFFER){
			cameraMovement.setY(PW_CAMERA_MOVESPD);
		} else {
			cameraMovement.setY(0);
		}
	}
	
}
