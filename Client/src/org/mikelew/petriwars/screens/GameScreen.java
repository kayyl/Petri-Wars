package org.mikelew.petriwars.screens;

import org.jagatoo.input.InputSystem;
import org.jagatoo.input.devices.components.ControllerAxis;
import org.jagatoo.input.devices.components.ControllerButton;
import org.jagatoo.input.devices.components.Key;
import org.jagatoo.input.devices.components.MouseButton;
import org.jagatoo.input.events.ControllerAxisChangedEvent;
import org.jagatoo.input.events.ControllerButtonEvent;
import org.jagatoo.input.events.ControllerButtonPressedEvent;
import org.jagatoo.input.events.ControllerButtonReleasedEvent;
import org.jagatoo.input.events.KeyPressedEvent;
import org.jagatoo.input.events.KeyReleasedEvent;
import org.jagatoo.input.events.KeyStateEvent;
import org.jagatoo.input.events.KeyTypedEvent;
import org.jagatoo.input.events.MouseButtonClickedEvent;
import org.jagatoo.input.events.MouseButtonEvent;
import org.jagatoo.input.events.MouseButtonPressedEvent;
import org.jagatoo.input.events.MouseButtonReleasedEvent;
import org.jagatoo.input.events.MouseMovedEvent;
import org.jagatoo.input.events.MouseStoppedEvent;
import org.jagatoo.input.events.MouseWheelEvent;
import org.jagatoo.input.listeners.InputListener;
import org.mikelew.petriwars.PetriClient;
import org.mikelew.petriwars.util.Camera;
import org.xith3d.render.BackgroundRenderPass;
import org.xith3d.render.RenderPass;
import org.xith3d.scenegraph.BranchGroup;
import org.xith3d.ui.hud.HUD;

public abstract class GameScreen implements InputListener {
	private final RenderPass foreground;
	protected final BranchGroup scene;
	protected final HUD hud;
	private BackgroundRenderPass background;
	protected final Camera camera;
	
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
		camera = new Camera();
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
	public Camera getCamera() {return camera;}
	
	public final void init(){
		hud.setSize(
				PetriClient.getInstance().getCanvasWidth(), 
				PetriClient.getInstance().getCanvasHeight());
		InputSystem.getInstance().addInputListener(this);
		this.initScreen();
	}
	
	public final void deinit(){
		InputSystem.getInstance().removeInputListener(this);
		this.deinitScreen();
	}
	
	/**
	 * Called when this screen is added to the client engine, before the first render pass.
	 * Do any first frame initialization here.
	 */
	protected abstract void initScreen();
	
	/**
	 * Called when this screen is removed from the client engine, after its final render
	 * pass. Do any cleanup needed here.
	 */
	protected abstract void deinitScreen();
	
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
	
	// We HAVE to implement these, but we'll never use them ever
	@Override public final void onControllerAxisChanged(ControllerAxisChangedEvent e, ControllerAxis axis, float axisDelta) {}
	@Override public final void onControllerButtonPressed(ControllerButtonPressedEvent e, ControllerButton button) {}
	@Override public final void onControllerButtonReleased(ControllerButtonReleasedEvent e, ControllerButton button) {}
	@Override public final void onControllerButtonStateChanged(ControllerButtonEvent e, ControllerButton button, boolean state) {}
	
	@Override public void onKeyPressed(KeyPressedEvent e, Key key) {}
	@Override public void onKeyReleased(KeyReleasedEvent e, Key key) {}
	@Override public void onKeyStateChanged(KeyStateEvent e, Key key, boolean state) {}
	@Override public void onKeyTyped(KeyTypedEvent e, char keyChar) {}
	
	@Override public final long getMouseStopDelay() {return 500000000L;}
	@Override public void onMouseStopped(MouseStoppedEvent e, int x, int y) {}
	
	@Override public void onMouseButtonPressed(MouseButtonPressedEvent e, MouseButton button) {}
	@Override public void onMouseButtonReleased(MouseButtonReleasedEvent e, MouseButton button) {}
	@Override public void onMouseButtonClicked(MouseButtonClickedEvent e, MouseButton button, int clickCount) {}
	@Override public void onMouseButtonStateChanged(MouseButtonEvent e, MouseButton button, boolean state) {}
	@Override public void onMouseMoved(MouseMovedEvent e, int x, int y, int dx, int dy) {}
	@Override public void onMouseWheelMoved(MouseWheelEvent e, int wheelDelta) {}
}
