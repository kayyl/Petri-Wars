package org.mikelew.petriwars.screens;

import org.mikelew.petriwars.PetriClient;
import org.xith3d.scenegraph.TransformGroup;
import org.xith3d.scenegraph.primitives.Rectangle;

public class TitleScreen extends GameScreen {
	
	private Rectangle logo;
	private TransformGroup logotg;
	private int wavecount = 0;
	private int alphaTimer = -1, alphaTimerTotal = -1;
	private float destAlpha = 0.0f, startAlpha = 0.0f;
	
	public TitleScreen() {
		super(false);
		
		Rectangle title = new Rectangle(768, 448, "menu_title.png");
		scene.addChild(title);
		
		logo = new Rectangle(434, 270, "logo.png");
		logo.setAlpha(1.0f);
		logotg = new TransformGroup(0, 120, 1.0f);
		logotg.addChild(logo);
		scene.addChild(logotg);
	}

	@Override public void initScreen() {
		this.getRenderPass().getConfig().setScreenScale(PetriClient.getInstance().getCanvasWidth()/2);
		wavecount = -150;
	}

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

}
