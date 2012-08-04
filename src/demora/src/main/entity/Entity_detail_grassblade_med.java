package main.entity;

import main.GraphicsManager;

import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Rectangle;

@SuppressWarnings("all")
public class Entity_detail_grassblade_med {
	
	public float x, y;
	public int type;
	public float offset = 0;
	public Image texture;
	
	//Affects wind sway - more chaos means more random movement. 0 - 10.
	public float chaos = 15;
	
	//Wind direction in degrees
	public float windDir = 0;
	
	public Entity_detail_grassblade_med(float x, float y) {
		this(0, x, y);
	}
	
	public Entity_detail_grassblade_med(int type, float x, float y) {
		this.x = x;
		this.y = y;
		this.type = type;
	}
	
	public void draw() {
		if(!main.Camera.getVisibleArea().contains(x, y)) return;
		if(type == 0) {
			texture = GraphicsManager.grassblade_tex0.copy();
		} else {
			texture = GraphicsManager.grassblade_tex1.copy();
		}
		
		float swayAmt = (float)Math.cos(System.nanoTime()/2000000000f + (Math.sin(x*0.001) + Math.cos(y*0.001))*chaos)*10;
		if(EntityManager.getPlayer().getBounds().intersects(new Rectangle(x+5, y+10, 20, 40))) {
			if(Math.abs(offset) <= 13)
				if(EntityManager.getPlayer().getX()-16 < x) {
					offset += -0.04 * main.ControlManager.getDelta();
				} else {
					offset += 0.04 * main.ControlManager.getDelta();
				}
		} else {
			offset *= 0.97;
		}
		
		swayAmt = (float)Math.sin(swayAmt * 0.7 * Math.cos(swayAmt*-0.2)) + swayAmt;
		swayAmt = (float)(swayAmt * Math.cos(windDir)) + offset;
		
		texture.getScaledCopy(0.5f).drawSheared(x - swayAmt, y, swayAmt, 0);
	}
}
