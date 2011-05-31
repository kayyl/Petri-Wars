package org.mikelew.petriwars.screens;

import org.xith3d.scenegraph.BranchGroup;

public abstract class GameScreen extends BranchGroup {
	private final boolean prespectiveview;
	
	public GameScreen() {
		prespectiveview = true;
	}
	
	public GameScreen(boolean perspective){
		this.prespectiveview = perspective;
	}
	
	public final boolean isPerspective(){
		return prespectiveview;
	}
	
	/**
	 * Called when this screen is added to the client engine, before the first render pass.
	 */
	public abstract void setupScreen();
	
	/**
	 * Called by the client engine every frame. Logic should be run here.
	 */
	public abstract void runFrame();
	
	/**
	 * Called by the client engine before the frame is rendered. DO NOT RUN LOGIC HERE!
	 */
	public void preRender(){}
	
	/**
	 * Called by the client engine after the frame is rendered. DO NOT RUN LOGIC HERE!
	 */
	public void postRender(){}
}
