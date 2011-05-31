package org.mikelew.petriwars.screens;

import org.mikelew.petriwars.PetriClient;
import org.xith3d.render.BackgroundRenderPass;
import org.xith3d.render.RenderPass;
import org.xith3d.scenegraph.BranchGroup;
import org.xith3d.ui.hud.HUD;

public abstract class GameScreen {
	private final RenderPass foreground;
	protected final BranchGroup scene;
	protected final HUD hud;
	private BackgroundRenderPass background;
	
	private final boolean prespectiveview;
	
	public GameScreen(boolean perspective){
		this.prespectiveview = perspective;
		
		if (prespectiveview){
			foreground = RenderPass.createPerspective();
		} else {
			foreground = RenderPass.createParallel();
		}
		background = BackgroundRenderPass.createParallel(); //default background
		scene = foreground.getBranchGroup();
		
		hud = new HUD(0, 0);
	}
	public GameScreen() {this(true);}
	
	public final boolean isPerspective(){return prespectiveview;}
	
	public RenderPass getRenderPass() {return foreground;}
	public BackgroundRenderPass getBackgroundRenderPass() {return background;}
	protected void setBackgroundRenderPass(BackgroundRenderPass bgrp) {
		if (scene.getSceneGraph() != null) scene.getSceneGraph().removeRenderPass(background);
		background = bgrp;
		if (scene.getSceneGraph() != null) scene.getSceneGraph().addRenderPass(background);
	}
	
	public HUD getHud() {return hud;}
	
	public void init(){
		hud.setSize(
				PetriClient.getInstance().getCanvasWidth(), 
				PetriClient.getInstance().getCanvasHeight());
		this.initScreen();
	}
	
	/**
	 * Called when this screen is added to the client engine, before the first render pass.
	 * Do any first frame initialization here.
	 */
	protected abstract void initScreen();
	
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
