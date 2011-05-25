package org.mikelew.petriwars;

import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;

import org.xith3d.base.Xith3DEnvironment;
import org.xith3d.loop.InputAdapterRenderLoop;
import org.xith3d.loop.RenderLoop;
import org.xith3d.loop.RenderLoopListener;
import org.xith3d.render.Canvas3D;
import org.xith3d.render.Canvas3DFactory;
import org.xith3d.render.config.CanvasConstructionInfo;
import org.xith3d.render.config.DisplayMode;
import org.xith3d.render.config.DisplayModeSelector;
import org.xith3d.render.config.OpenGLLayer;
import org.xith3d.render.config.DisplayMode.FullscreenMode;


public class PetriClient extends InputAdapterRenderLoop{
	/*
	 * Xith3D demos and tests to reference:
	 * 
	 * /Xith3DTets/xith-test-source/org/xith3d/test/ui/HUDConsoleTest1.java - Cheat Console
	 * /Xith3DTets/xith-test-source/org/xith3d/test/selection/SelectionTest.java - Unit Selection
	 * /Xith3DTets/xith-test-source/org/xith3d/test/input/PointAndClickTest.java - Unit Selection
	 * 
	 * /Xith3DTets/xith-test-source/org/xith3d/demos/Text2DDemo.java - 2D Text
	 * /Xith3DTets/xith-test-source/org/xith3d/test/particles/ParticleSystemTest.java - Particle Systems
	 * /Xith3DTets/xith-test-source/org/xith3d/test/texture/RadarTest.java - Radar?
	 * /Xith3DTets/xith-test-source/org/xith3d/test/loaders/OBJLoaderTest.java - Object Loader
	 * 
	 * /Xith3DTets/xith-test-source/org/xith3d/test/display/FullscreenSwitchTest.java - Full Screen
	 */
	private static final OpenGLLayer FALLBACK_DEFAULT_OGLLAYER = OpenGLLayer.LWJGL;
	public static OpenGLLayer DEFAULT_OGL_LAYER = OpenGLLayer.getDefault();
	static
	{
		try
		{
			String defaultOGLLayer = System.getProperty( "org.xith3d.test.OpenGLLayer.default", FALLBACK_DEFAULT_OGLLAYER.name() );

			DEFAULT_OGL_LAYER = OpenGLLayer.valueOf( defaultOGLLayer );

			if (DEFAULT_OGL_LAYER != null)
				DEFAULT_OGL_LAYER = FALLBACK_DEFAULT_OGLLAYER;
		}
		catch (Throwable t)
		{
			DEFAULT_OGL_LAYER = FALLBACK_DEFAULT_OGLLAYER;
		}
	}

	public static final DisplayMode DEFAULT_DISPLAY_MODE = DisplayModeSelector.getImplementation( DEFAULT_OGL_LAYER ).getBestMode( 800, 600, 32 );
	public static final FullscreenMode DEFAULT_FULLSCREEN = DisplayMode.WINDOWED;
	public static final boolean DEFAULT_VSYNC = DisplayMode.VSYNC_ENABLED;

	////////////////////////////////////////////////////////////////////////////

	protected Canvas3D canvas;

	public PetriClient() {
		super(60.0f);
		
		setupXithEnvironment();
	}

	private void setupXithEnvironment() {
		final int wwidth = 640, wheight = 480; //TODO make these programmable

		// Create a new Window
		Xith3DEnvironment env = new Xith3DEnvironment(this);

		CanvasConstructionInfo cci = new CanvasConstructionInfo();
		cci.setFullscreenMode(FullscreenMode.WINDOWED);
		cci.setTitle("Petri Wars!!");
		cci.setVSyncEnabled(true);

		final JFrame frame = new JFrame("Petri Wars");
		frame.setLayout(null);
		frame.setSize(wwidth, wheight); 
		frame.addWindowListener(new WindowAdapter(){
			@Override public void windowClosing(WindowEvent e){
				//TODO close properly
				System.exit(0);
			}
		});

		canvas = Canvas3DFactory.create(cci, frame.getContentPane());
		env.addCanvas(canvas);

		frame.setVisible(true);

		Thread.yield(); //yeild to show that we are still alive

		Insets insets = frame.getInsets();
		Dimension frameSize = new Dimension(wwidth + insets.left + insets.right, wheight + insets.top + insets.bottom);
		frame.setSize(frameSize);

		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

		Point upperLeft = new Point((screenSize.width - frameSize.width)/2, (screenSize.height - frameSize.height)/2);
		frame.setLocation(upperLeft);
		frame.setResizable(false);

		java.awt.Dimension size = frame.getSize();
		frame.setSize(size.width - 2, size.height - 2);

		Thread.yield();

		if (frame.getLocation().x == 0 && frame.getLocation().y == 0)
			frame.setLocation(upperLeft);

		this.addRenderLoopListener(new RenderLoopListener(){
			public void onRenderLoopStarted(RenderLoop rl){}
			public void onRenderLoopPaused(RenderLoop rl, long gameTime, TimingMode timingMode, int pauseMode){}
			public void onRenderLoopResumed(RenderLoop rl, long gameTime, TimingMode timingMode, int pauseMode){}
			public void onRenderLoopStopped(RenderLoop rl, long gameTime, TimingMode timingMode, float averageFPS){
				frame.setVisible(false);
				frame.dispose();
			}
		});
	}

}
