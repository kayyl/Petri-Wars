package org.mikelew.petriwars;

import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.sql.Date;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.StringTokenizer;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.regex.Matcher;

import org.jagatoo.input.InputSystem;
import org.jagatoo.input.InputSystemException;
import org.jagatoo.input.devices.components.ControllerAxis;
import org.jagatoo.input.devices.components.ControllerButton;
import org.jagatoo.input.devices.components.Key;
import org.jagatoo.input.devices.components.KeyID;
import org.jagatoo.input.devices.components.Keys;
import org.jagatoo.input.devices.components.MouseButton;
import org.jagatoo.input.events.ControllerAxisChangedEvent;
import org.jagatoo.input.events.ControllerButtonEvent;
import org.jagatoo.input.events.ControllerButtonPressedEvent;
import org.jagatoo.input.events.ControllerButtonReleasedEvent;
import org.jagatoo.input.events.KeyPressedEvent;
import org.jagatoo.input.events.KeyReleasedEvent;
import org.jagatoo.input.events.KeyStateEvent;
import org.jagatoo.input.events.KeyTypedEvent;
import org.jagatoo.input.events.KeyboardEvent;
import org.jagatoo.input.events.MouseButtonClickedEvent;
import org.jagatoo.input.events.MouseButtonEvent;
import org.jagatoo.input.events.MouseButtonPressedEvent;
import org.jagatoo.input.events.MouseButtonReleasedEvent;
import org.jagatoo.input.events.MouseMovedEvent;
import org.jagatoo.input.events.MouseStoppedEvent;
import org.jagatoo.input.events.MouseWheelEvent;
import org.jagatoo.input.listeners.InputAdapter;
import org.jagatoo.input.listeners.InputListener;
import org.jagatoo.logging.Log;
import org.jagatoo.logging.LogChannel;
import org.jagatoo.logging.LogLevel;
import org.jagatoo.logging.LogManager;
import org.mikelew.petriwars.annotations.TestingCheat;
import org.mikelew.petriwars.hud.CheatConsole;
import org.mikelew.petriwars.hud.ConsoleListener;
import org.mikelew.petriwars.screens.GameScreen;
import org.mikelew.petriwars.screens.TitleScreen;
import org.xith3d.base.Xith3DEnvironment;
import org.xith3d.loaders.texture.TextureLoader;
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
import org.xith3d.render.util.WindowClosingListener;
import org.xith3d.resources.ResourceLocator;
import org.xith3d.ui.hud.HUD;

import com.sun.servicetag.UnauthorizedAccessException;

public class PetriClient extends InputAdapterRenderLoop {
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
	
	private static PetriClient instance;
	public static PetriClient getInstance(){
		if (instance == null) instance = new PetriClient();
		return instance;
	}

	////////////////////////////////////////////////////////////////////////////
	
	private static final LogChannel logchannel = new LogChannel("PetriWars");
	private static final Logger LOG = Logger.getLogger("PetriClient");
	
	private final PetriClient myself = this;
	
	protected Xith3DEnvironment env;
	protected Canvas3D canvas;
	protected HUD hud;
	protected CheatConsole cheatconsole;
	
	//////////////////////////// Client Start-up ///////////////////////////////

	private PetriClient() {
		super(60.0f);
		
		setupLogging();
		setupXithEnvironment();
		setupCheatConsole();
		
		//Insert first screen to be run here!
		this.setScreen(new TitleScreen());
	}
	
	private void setupLogging(){
		Logger root = Logger.getLogger("");
		Handler xithloghandler = new Handler() {
			@Override public void publish(LogRecord record) {
				String str = this.getFormatter().format(record);
				Log.println(logchannel, record.getLevel().intValue(), str);
			}
			@Override public void flush() {}
			@Override public void close() throws SecurityException {}
		};
		xithloghandler.setFormatter(new Formatter() {
			@Override public String format(LogRecord record) {
				StringBuffer sb = new StringBuffer();
				sb
					.append(new SimpleDateFormat("HH:mm:ss").format(new Date(record.getMillis())) )
					.append(" ").append(record.getLoggerName())
					.append(" [").append(record.getLevel().getName()).append("] ")
					.append(record.getMessage()).append('\n');
				
				return sb.toString();
			}
		});
		
		root.addHandler(xithloghandler);
	}

	private void setupXithEnvironment(){
		try {
			env = new Xith3DEnvironment( this );
			
			// Create Canvas
			canvas = Canvas3DFactory.create(
						800, 600, 
						32, 
						FullscreenMode.WINDOWED, 
						"Petri Wars!");
			InputSystem.getInstance().registerNewKeyboardAndMouse(canvas.getPeer());
			env.addCanvas(canvas);
			
			canvas.addWindowClosingListener(new WindowClosingListener() {
				@Override public void onWindowCloseRequested(Canvas3D canvas) {
					end();
				}
			});
			
			// Create Heads-Up Display
			hud = new HUD(canvas.getWidth(), canvas.getHeight());
			env.addHUD(hud);
			
			// Create Resource Locators
			ResourceLocator rl = ResourceLocator.create("org/mikelew/petriwars/resource");
			rl.createAndAddTSL("texture");
			rl.createAndAddTSL("bgimg");
			rl.createAndAddTSL("sprite");
			ResourceLocator.setSingletonInstance(rl);
			
			// Add loop listener
			this.addRenderLoopListener(new RenderLoopListener(){
				public void onRenderLoopStarted(RenderLoop rl){}
				public void onRenderLoopPaused(RenderLoop rl, long gameTime, TimingMode timingMode, int pauseMode){}
				public void onRenderLoopResumed(RenderLoop rl, long gameTime, TimingMode timingMode, int pauseMode){}
				public void onRenderLoopStopped(RenderLoop rl, long gameTime, TimingMode timingMode, float averageFPS){}
			});
			
		} catch (InputSystemException ex){
			LOG.severe("Exception with Input System while setting up Xith Environment:"+ex.getMessage());
		} catch (MalformedURLException e) {
			LOG.severe("MalformedURLException with Resource Loader setup:"+e.getMessage());
		}
	}
	////////////////////// Game Management //////////////////////////
	
	protected GameScreen currScreen;
	private GameScreen pendingScreen;
	
	public GameScreen getCurrentScreen() {return currScreen;}
	/** Sets the next screen, which will be switched to on the next loop iteration */
	public void setScreen(GameScreen newscreen) {
		pendingScreen = newscreen;
	}
	public boolean isScreenSwitchPending(){return pendingScreen != null;}
	
	@Override protected void loopIteration(long gameTime, long frameTime, TimingMode timingMode) {
		//switch screens if a screen update is pending
		if (pendingScreen != null){
			if (currScreen != null){
				env.removeRenderPass(currScreen.getRenderPass());
				env.removeRenderPass(currScreen.getBackgroundRenderPass());
				env.removeHUD(currScreen.getHud());
			}
			
			this.currScreen = pendingScreen;
			pendingScreen = null;
			
			env.addRenderPass(currScreen.getRenderPass());
			env.addRenderPass(currScreen.getBackgroundRenderPass());
			env.addHUD(currScreen.getHud());
			currScreen.init();
		}
		
		prepareNextFrame(gameTime, frameTime, timingMode);
		if (currScreen != null) currScreen.runFrame();

		if ((getPauseMode() & PAUSE_RENDERING) == 0) {
			if (currScreen != null) currScreen.preRender();
			renderNextFrame(gameTime, frameTime, timingMode);
			if (currScreen != null) currScreen.postRender();
		}
	}
	
	public int getCanvasWidth() {return canvas.getWidth();}
	public int getCanvasHeight() {return canvas.getHeight();}
	
	/////////////////////// Game Globals ////////////////////////////
	//placing global boolean and integer properties here allows us to test them by using the
	//"boolprop" and "intprop" cheat commands
	
	/** The testingcheats boolean property signifies that certain properties are allowed to be
	 * switched and certain commands allowed to be called. */
	public boolean testingcheats = false;
	@TestingCheat public int screensize = 2;
	
	/////////////////////// Cheat Console ///////////////////////////
	
	private void setupCheatConsole(){
		cheatconsole = new CheatConsole(canvas.getWidth(), 150, logchannel.getID(), false);
		cheatconsole.addConsoleListener(new CheatConsoleListener());
		
		hud.getContentPane().addWidget(cheatconsole);
		
		LogManager.getInstance().registerLog(cheatconsole);
		
		InputSystem.getInstance().addInputListener(new InputAdapter() {
			@Override public void onKeyPressed(KeyPressedEvent e, Key key) {
				if (	((e.getModifierMask() & Keys.MODIFIER_CONTROL) != 0) && 
						((e.getModifierMask() & Keys.MODIFIER_ALT) != 0) &&
						(key.getKeyID() == KeyID.HOME) ){
					cheatconsole.popUp(!cheatconsole.isPoppedUp());
				}
			}
		});
		
	}
	
	private class CheatConsoleListener implements ConsoleListener {
		@Override public void onCommandEntered(CheatConsole console, String commandLine) {
			try {
				StringTokenizer tk = new StringTokenizer(commandLine, " ");
				String cmd = tk.nextToken();
				
				//TODO insert cheat command logic here!
				if (cmd.matches("(?i)wireframe(mode)?")){
					boolean b = !canvas.isWireframeMode();
					if (tk.hasMoreTokens())
						b = parseBoolean(tk.nextToken());
					canvas.setWireframeMode(b);
					
				} else if (cmd.matches("(?i)boolprop")){
					String name = tk.nextToken();
					boolean b = parseBoolean(tk.nextToken());
					if (name == null) throw new NullPointerException();
					
					Field f = PetriClient.class.getDeclaredField(name);
					if (f.getGenericType() != Boolean.TYPE) throw new ClassCastException();
					
					if (f.isAnnotationPresent(TestingCheat.class) && !testingcheats)
						throw new UnauthorizedAccessException();
					
					f.setBoolean(myself, b);
					Log.println(logchannel, f.getName()+" = "+b);
				
				} else if (cmd.matches("(?i)intprop")){
					String name = tk.nextToken();
					int b = Integer.parseInt(tk.nextToken());
					if (name == null) throw new NullPointerException();
					
					Field f = PetriClient.class.getDeclaredField(name);
					if (f.getGenericType() != Integer.TYPE) throw new ClassCastException();
					
					if (f.isAnnotationPresent(TestingCheat.class) && !testingcheats)
						throw new UnauthorizedAccessException();
					
					f.setInt(myself, b);
					Log.println(logchannel, f.getName()+" = "+b);
					
				} else {
					Log.println(logchannel, "Unknown Command");
				}
			} catch (Exception ex){
				Log.println(logchannel, "Bad command");
			}
		}
		
		public boolean parseBoolean(String s) throws ParseException {
			if (s.matches("(?i)true|t|yes|on|enabled|1")) return true;
			if (s.matches("(?i)false|f|no|off|disabled|0")) return false;
			throw new ParseException("", 0);
		}
	}
	
}
