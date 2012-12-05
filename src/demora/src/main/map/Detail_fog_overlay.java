package main.map;

import main.Camera;
import main.ControlManager;

import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;

public class Detail_fog_overlay {
	public static float offsetX = 0, offsetY = 0;
	public static Image fog_overlay;
	private static float alpha = 0;
	private static boolean fadingIn = false;

	public static void init() {
		try {
			fog_overlay = new Image("lib/img/misc/fog_overlay.png");
		} catch (SlickException e1) {
			e1.printStackTrace();
		}
	}
	
	public static void render() {
		fog_overlay.setAlpha(alpha);
		for(int x = 0; x < 2; x++) {
			for(int y = 0; y < 2; y++) {
				fog_overlay.draw(
						(fog_overlay.getWidth() * 7 * x) + offsetX + Camera.getAnchorX() * -1.1f, 
						(fog_overlay.getHeight() * 7 * y) + offsetY + Camera.getAnchorY() * -1.1f, 7);
				fog_overlay.draw(
						(fog_overlay.getWidth() * 7 * x) + offsetX + -200 + Camera.getAnchorX() * -1.5f, 
						(fog_overlay.getHeight() * 7 * y) + offsetY + -200 + Camera.getAnchorY() * -1.5f, 7);
			}
		}
	}
	
	public static void update() {
		offsetX -= ControlManager.getDelta() * 0.01f;
		offsetY -= ControlManager.getDelta() * 0.005f;

		if(fadingIn) {
			if(alpha < 0.5f) alpha += ControlManager.getDelta() * 0.0001f;
			else alpha = 0.5f;
		} else {
			if(alpha > 0) alpha -= ControlManager.getDelta() * 0.0001f;
			else alpha = 0;
		}
	}
	
	public static void fadeIn() {fadingIn = true;}
	public static void fadeOut() {fadingIn = false;}
	public static void toggle() {fadingIn = !fadingIn;}
}
