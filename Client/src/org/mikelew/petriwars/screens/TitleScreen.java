package org.mikelew.petriwars.screens;

import java.awt.Font;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.jagatoo.input.devices.components.Key;
import org.jagatoo.input.events.KeyPressedEvent;
import org.jagatoo.input.events.KeyReleasedEvent;
import org.jagatoo.input.events.KeyTypedEvent;
import org.jagatoo.util.errorhandling.IncorrectFormatException;
import org.jagatoo.util.errorhandling.ParsingException;
import org.mikelew.petriwars.PetriClient;
import org.openmali.vecmath2.Colorf;
import org.xith3d.effects.EffectFactory;
import org.xith3d.effects.celshading.CelShadingFactory;
import org.xith3d.effects.celshading.SimpleCelShadingFactory;
import org.xith3d.loaders.models.Model;
import org.xith3d.loaders.models.ModelLoader;
import org.xith3d.resources.ResourceLocator;
import org.xith3d.scenegraph.Group;
import org.xith3d.scenegraph.Shape3D;
import org.xith3d.scenegraph.TransformGroup;
import org.xith3d.scenegraph.Geometry.Optimization;
import org.xith3d.scenegraph.primitives.Rectangle;
import org.xith3d.ui.hud.widgets.Button;
import org.xith3d.ui.text2d.Text2D;
import org.xith3d.ui.text2d.TextAlignment;

public class TitleScreen extends GameScreen {
	
	private Rectangle logo;
	private TransformGroup logotg;
	private TransformGroup screen1, screen2;
	private Text2D screentext;
	
	private Button play_go;
	
	private int wavecount = 0;
	private int alphaTimer = -1, alphaTimerTotal = -1;
	private float destAlpha = 0.0f, startAlpha = 0.0f;
	
	private StringBuffer connectScreenText = new StringBuffer();
	
	private static enum ScreenInputDirection {
		None, //input goes nowhere
		ConnectScreen //input goes to connection screen
	}
	private ScreenInputDirection inputDirection = ScreenInputDirection.ConnectScreen;
	
	public TitleScreen() {
		super(false);
		
		this.getRenderPass().getConfig().setBackClipDistance(1000);
		this.getRenderPass().getConfig().setFrontClipDistance(-1000);
		
		Rectangle title = new Rectangle(768, 448, "menu_title.png");
		TransformGroup titletg = new TransformGroup(0, 0, -100);
		titletg.addChild(title);
		scene.addChild(titletg);
		
		logo = new Rectangle(434, 270, "logo.png");
		logo.setAlpha(1.0f);
		logotg = new TransformGroup(0, 120, -99);
		logotg.addChild(logo);
		scene.addChild(logotg);
		
		try {
			EffectFactory.getInstance().registerCelShadingFactory( new SimpleCelShadingFactory() );
			//EffectFactory.getInstance().registerCelShadingFactory( new AssemblyCelShadingFactory() );
			
			CelShadingFactory.setOutlineLineWidth(3.0f);
			
			URL url = ResourceLocator.getInstance().getResource("model/connect_screen.obj");
			Model m = ModelLoader.getInstance().loadModel(url);
			m.dump();
			
			screen1 = new TransformGroup();
			//screen2 = new TransformGroup(-100, 0, 200);
			
			for (int tgi = 0; tgi < 1; tgi++){
				TransformGroup screenN = (tgi == 0)?screen1:screen2;
				
				TransformGroup screentg = new TransformGroup();
				//screentg.getTransform().setScale(100.0f);
				
				final Group topg = (Group) m.getChild(0);
				for (int i = 0; i < topg.numChildren(); i++){
					Shape3D shape = ((Shape3D)topg.getChild(i));
					shape.getGeometry().setOptimization( Optimization.AUTO );
					EffectFactory.getInstance().getCelShadingFactory().prepareForCelShading( shape.getGeometry(), screentg );
					
					if (shape.getName().startsWith("Screen")){
						shape = (Shape3D) screentg.getChild((i*2));
						shape.getAppearance(true).getMaterial(true).setAmbientColor(3.6f, 3.6f, 3.6f);
					} else {
						shape = (Shape3D) screentg.getChild((i*2));
						shape.getAppearance(true).getMaterial(true).setAmbientColor(2.0f, 2.0f, 2.0f);
					}
				}
				{
					screentext = new Text2D(
							"Connect to Server:\n \n", 
							Colorf.BLACK, 
							new Font("Courier New", Font.BOLD, 32), 
							TextAlignment.TOP_CENTER);
					//screentext.getTransform().setScale(0.01f);
					//screentext.getTransform().setTranslation(-1.0f, 96, 0.5f);
					TransformGroup texttg = new TransformGroup();
					texttg.getTransform().setTranslation(-1.0f, 96, 0.5f);
					texttg.addChild(screentext);
					screentg.addChild(texttg);
					
					
				}
				
				
				screenN.addChild(screentg);
				scene.addChild(screenN);
			}
			
			
			screen1.getTransform().rotXYZ(0.11f, 0.45f, 0);
			screen1.getTransform().setTranslation(0, 0, 200);
			screen1.updateTransform();
			
			System.out.println("Done");
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IncorrectFormatException e) {
			e.printStackTrace();
		} catch (ParsingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	@Override public void initScreen() {
		this.getRenderPass().getConfig().setScreenScale(PetriClient.getInstance().getCanvasWidth()/2);
		wavecount = -150;
	}
	
	@Override protected void deinitScreen() {}

	@Override public void runFrame() {
		/* 
		 if (splashVars[0] < 400) splashVars[0]++;
		splashVars[4]++; splashVars[4] %= 1000;
		
		float sinwave = 0.1f*cos((splashVars[4]/1000.0f)*(2*MY_PI));
		splashUI[1]->LocalTransform.SetTranslate(APoint(0.0f, 120.0f, 0.0f));
		splashUI[1]->LocalTransform.SetScale(APoint(
			1.0f-sinwave, //makes the logo "breathe"
			1.0f-sinwave, 
			1.0f));
		splashLogoMat->Diffuse = Float4(0.0f, 0.0f, 0.0f, 
			(splashVars[0] < 0)?0.0f:((float)splashVars[0])/400.0f); //fade in the logo
		splashUI[1]->Update(0, false);
		*/
		wavecount++; wavecount %= 1000;
		
		if (wavecount == -1){
			changeLogoAlpha(0.0f, 400);
		}
		
		float sinewave = (float) (0.1 * Math.cos(((double)wavecount / 1000.0) * (2*Math.PI)));
		logotg.getLocalTransform().setScale(1.0f-sinewave, 1.0f-sinewave, 1.0f);
		
	//	screen1.getTransform().rotY(sinewave*10);
	//	screen1.updateTransform();
		
	//	screen2.getTransform().rotY(-sinewave*10);
	//	screen2.updateTransform();
		
		if (this.alphaTimer >= 0){
			float alpha = logo.getAppearance().getTransparencyAttributes().getTransparency();
			alphaTimer--;
			float astep = (1.0f - ((float)alphaTimer / (float)alphaTimerTotal)) * (destAlpha - startAlpha);
//			System.out.println(alpha + "; "+ alphaTimer + "; "+astep + " over "+startAlpha+" -> "+destAlpha);
			
			alpha = startAlpha + astep;
			
			logo.getAppearance().getTransparencyAttributes().setTransparency(alpha);
		}
		
		logotg.updateTransform();
	}
	
	protected void changeLogoAlpha(float destAlpha, int time){
		this.startAlpha = logo.getAlpha();
		this.destAlpha = destAlpha;
		this.alphaTimer = this.alphaTimerTotal = time;
	}
	
	private char prevKeyChar = 0; //hacky fix to the repeating key problem
	@Override public void onKeyTyped(KeyTypedEvent e, char keyChar) {
		if (prevKeyChar == keyChar) return; //hacky fix to the repeating key problem
		prevKeyChar = keyChar;
		
		if (inputDirection == ScreenInputDirection.ConnectScreen){
			switch (keyChar){
			case '\n':
				//TODO connect to server
				break;
			case '\b':
				if (connectScreenText.length() > 0)
					connectScreenText.setLength(connectScreenText.length()-1);
				break;
			default:
				connectScreenText.append(keyChar);
			}
			screentext.setText("Connect to Server:\n \n"+connectScreenText.toString());
		}
	}
	
	@Override public void onKeyPressed(KeyPressedEvent e, Key key) {
		prevKeyChar = 0; //hacky fix to the repeating key problem
	}
	
	@Override public void onKeyReleased(KeyReleasedEvent e, Key key) {}

}
