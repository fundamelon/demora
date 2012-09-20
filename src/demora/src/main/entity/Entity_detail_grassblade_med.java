package main.entity;

import main.GraphicsManager;

import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.Graphics;

@SuppressWarnings("all")
public class Entity_detail_grassblade_med {
	
	public float x, y;
	public int type;
	public float offset = 0;
	public Image texture;
	boolean debugLines = false;
	float scaleFactor = 1f;
	int offsetDir = 0;
	
	//Affects wind sway - more chaos means more random movement. 0 - 10.
	public float chaos = 15;
	
	float randOffset = (float)(Math.random()*0.3);
	
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
			scaleFactor = 0.5f;
		} else if(type == 1) {
			texture = GraphicsManager.grassblade_tex1.copy();
			scaleFactor = 0.6f;
		} else {
			debugLines = true;
		}
		float t = System.nanoTime()/2000000000f;
		float swayAmt = 5+(float)Math.cos(t + (Math.sin(x*0.0001) + Math.cos(y*0.001) + (randOffset * Math.sin(x * 0.001)))*chaos)*8;
		swayAmt += Math.sin(t*0.003)*Math.cos(t*x*0.004)*Math.cos(t * 8)*0.1f*swayAmt;
		float dist = (float)Math.sqrt(
				Math.pow(EntityManager.getPlayer().getBounds().getCenterY() - (y + texture.getHeight() * scaleFactor * 0.5), 2) +
				Math.pow(EntityManager.getPlayer().getBounds().getCenterX() - (x + texture.getWidth() * scaleFactor * 0.5), 2)
			);
		if(dist < 20){
			if(Math.abs(offset) <= 20 && offsetDir == 0) {
				if(EntityManager.getPlayer().getX()-16 < x) {
					offsetDir = -1;
				} else {
					offsetDir = 1;
				}
			}
			
			offset += 0.1 * offsetDir * main.ControlManager.getDelta();
			
			if(offset > 13) offset = 13;
			if(offset < -13) offset = -13;
		} else {
			offset *= 0.97;
			offsetDir = 0;
		}
		
		swayAmt = (float)Math.sin(swayAmt * 0.7 * Math.cos(swayAmt*-0.2)) + swayAmt;
		swayAmt = (float)(swayAmt * Math.cos(windDir)) + offset;
		
		swayAmt *= 1.2f;
		
		if(!debugLines) {
			int vertOffset = (int)Math.abs(offset * 0.2f * (dist<20 ? 20-dist : 1));
			float height = texture.getHeight();
			texture = texture.getScaledCopy(texture.getWidth(), texture.getHeight() - vertOffset);
			texture.getScaledCopy(scaleFactor).drawSheared(x - swayAmt, y + vertOffset*scaleFactor, swayAmt, 0);
		} else {
			float nx = x + texture.getWidth()/4;
			float ny = y + texture.getHeight()/4;
			
			new Graphics().drawLine(nx, ny, nx - swayAmt, ny - 20);
		}
	}
}
