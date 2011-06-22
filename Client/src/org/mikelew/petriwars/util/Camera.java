package org.mikelew.petriwars.util;

import org.mikelew.petriwars.PetriClient;
import org.openmali.vecmath2.Tuple3f;
import org.xith3d.scenegraph.View;

public class Camera {
	public Camera() {
		eyePosition = new Tuple3f(0, 0, -5);
		lookAt = new Tuple3f(0, 0, 0);
		upVector = new Tuple3f(0, 1, 0);
	}
	public Camera(View attachTo) {
		this();
		attachedView = attachTo;
	}
	
	public void update(){
		if (attachedView == null) return;
		attachedView.lookAt(eyePosition, lookAt, upVector);
	}
	
	private View attachedView;
	
	public void setAttachedView(View attachedView) {
		this.attachedView = attachedView;
	}
	
	private Tuple3f eyePosition;
	private Tuple3f lookAt;
	private Tuple3f upVector;
	
	public Tuple3f getEyePosition() { return eyePosition; }
	public Tuple3f getLookAtPosition() { return lookAt; }
	public Tuple3f getUpVector() { return upVector; }
	
	public void setEyePosition(Tuple3f eyePosition) {
		this.eyePosition = eyePosition;
	}
	public void setLookAtPosition(Tuple3f lookAt) {
		this.lookAt = lookAt;
	}
	public void setUpVector(Tuple3f upVector) {
		this.upVector = upVector;
	}
}
