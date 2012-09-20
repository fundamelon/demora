package util;

import java.util.ArrayList;

import main.ControlManager;
import main.GameBase;
import main.entity.EntityManager;

import org.lwjgl.input.Keyboard;
import org.newdawn.slick.Color;
import org.newdawn.slick.Font;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.TrueTypeFont;

public class Lagmeter {
	private static boolean inView = false;
	private static boolean toggle = false;
	private static ArrayList<java.lang.Float> deltaSet =  new ArrayList<java.lang.Float>(256);
	private static Font meterFont = new TrueTypeFont(new java.awt.Font("Courier", 10, 10), true);
	
	//Colors
	private static Color red = new Color(0.6f, 0f, 0f, 0.3f);
	private static Color yellow = new Color(0.6f, 0.6f, 0f, 0.3f);
	private static Color green = new Color(0f, 0.6f, 0f, 0.3f);
	
	//Lightest to darkest
	private static Color gray = new Color(0.4f, 0.4f, 0.4f, 0.3f);
	private static Color gray2 = new Color(0.3f, 0.3f, 0.3f, 0.6f);
	private static Color gray3 = new Color(0.2f, 0.2f, 0.2f, 0.6f);
	private static Color gray4 = new Color(0f, 0f, 0f, 0.6f);
	
	private static Color white = new Color(0.9f, 0.9f, 0.9f, 0.6f);
	
	public static int offsetY = 0;	
	private static float deltaSetAvg = 0;

	
	public static Transition slider = new Transition(200, 0, 300);
	
	public static void update() {
		if(ControlManager.keyPressed(Keyboard.KEY_8)) {
			if(toggle) show();
			else hide();
			toggle = !toggle;
		}
		
		setOffsetY((int)slider.getCurVal());
		
		//Store delta data
		deltaSet.add(0, ControlManager.getDelta());
		
		if(deltaSet.size() > 255)
			deltaSet.subList(255, Math.max(255, deltaSet.size())).clear();
		
		if(offsetY == 200) inView = false;
		else inView = true;

		if(!inView) return;
		
		//Render the graph		
		Graphics g = new Graphics();
		float w = GameBase.getWidth();
		float h = GameBase.getHeight();
		
		g.translate(0, offsetY);
		
		g.setColor(gray);
		g.fillRect(w - 255, h - 200, w, h);
		g.setColor(gray2);
		for(int i = 0; i < deltaSet.size(); i++) {
			float x = deltaSet.get(i);
			if(x > 50) g.setColor(red);
			g.drawLine(w-i, h, w-i, h-x*4);
			if(x > 50) g.setColor(gray2);
		}
		
		g.setColor(gray3);
		for(int y = 0; y < 11; y++) {
			g.drawLine(w - 255, h - y * 20, w - 250, h - y * 20);
			g.drawLine(w - 255, h + 10 - y * 20, w - 253, h + 10 - y * 20);
			g.drawLine(w, h - y * 10, w - 5, h - y * 10);
		}
		
		g.setColor(red);
		g.drawLine(w - 255, h-200, w, h - 200);	
		
		g.setColor(yellow);
		g.drawLine(w - 255, h-160, w, h - 160);	
		
		g.setColor(green);
		g.drawLine(w - 255, h-120, w, h - 120);
		
		deltaSetAvg = 0;
		for(float a : deltaSet) {
			deltaSetAvg += a;
		}
		deltaSetAvg /= deltaSet.size();
		
		g.setColor(white);
		g.drawLine(w - 260, h - deltaSetAvg * 4, w - 255, h - deltaSetAvg * 4);
		
		g.setFont(meterFont);
		g.setColor(gray4);
		g.drawString("delta: "+ControlManager.getDelta(), w-250, h-195);
		g.drawString("  avg: "+Math.round(deltaSetAvg), w-250, h - 185);
		g.drawString("  fps: "+GameBase.getFPS(), w-250, h-175);
		
		g.translate(0, -offsetY);
	}
	
	public static void show() {
		inView = true;
		slider.setToForward();
		slider.start();
	}
	
	public static void hide() {
		slider.setToReverse();
		slider.start();
	}
	
	public static void setOffsetY(int a) {
		offsetY = a;
	}
	
	public static int getOffsetY() {
		return offsetY;
	}
}
