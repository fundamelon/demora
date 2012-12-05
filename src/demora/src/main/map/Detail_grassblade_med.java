package main.map;

import main.GraphicsManager;
import main.entity.Entity;
import main.entity.EntityManager;
import main.entity.Entity_mobile;

import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.Graphics;

@SuppressWarnings("all")
public class Detail_grassblade_med {
	
	public float x, y;
	public int type;
	public float offset = 0;
	public Image texture;
	public Image drawtex;
	boolean debugLines = false;
	float scaleFactor = 1f;
	int offsetDir = 0;
	
	//Affects wind sway - more chaos means more random movement. 0 - 10.
	public float chaos = 15;
	
	float randOffset = (float)(Math.random()*0.3);
	
	//Wind direction in degrees
	public float windDir = 0;
	
	public Detail_grassblade_med(float x, float y) {
		this(0, x, y);
	}
	
	public Detail_grassblade_med(int type, float x, float y) {
		this.x = x;
		this.y = y;
		this.type = type;
		
		if(type == 0) {
			texture = GraphicsManager.grassblade_tex0.copy();
			scaleFactor = 0.5f;
		} else if(type == 1) {
			texture = GraphicsManager.grassblade_tex1.copy();
			scaleFactor = 0.6f;
		} else {
			debugLines = true;
		}
		
		this.x -= texture.getWidth() * 0.5f * scaleFactor;
		this.y -= texture.getHeight() * scaleFactor;
	}
	
	public void draw() {
		Rectangle clipRect = main.Camera.getVisibleArea();
		clipRect.scaleGrow(1.05f, 1.1f);
		if(!clipRect.contains(x, y)) return;
		float t = System.nanoTime()/2000000000f;
		float swayAmt = (float)Math.cos(t + (Math.sin(x*0.0001) + (randOffset * Math.sin(t + x * 0.001)))*chaos)*8 + (float)Math.cos(t * 0.08 + y*0.001);
		swayAmt += Math.sin(t*0.003)*Math.cos(t*0.004 + x)*Math.cos(t * 0.08)*0.1f*swayAmt;
		float dist = 20;
		for(Entity e : EntityManager.entityTable){ 
			
			dist = (float)Math.sqrt(
					Math.pow(e.getBounds().getCenterY() - (y + texture.getHeight() * scaleFactor * 0.5), 2) +
					Math.pow(e.getBounds().getCenterX() - (x + texture.getWidth() * scaleFactor * 0.5), 2)
				);
			if(dist < 20 && !e.isJumping()){
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
				offset *= 1 - (0.0005 * main.ControlManager.getDelta());
				offsetDir = 0;
			}
		}
		
		swayAmt = (float)Math.sin(swayAmt * 0.08 * Math.cos((1/swayAmt) * (1/swayAmt) *-0.1)) * (float)Math.pow(Math.cos(t * 10f + x), 3) + swayAmt;
		swayAmt = (float)(swayAmt * Math.cos(windDir)) + offset;
		
		swayAmt *= 1.2f;
		
		if(!debugLines) {
			int vertOffset = (int)Math.abs(offset * 0.2f * (dist<20 ? 20-dist : 1));
			float height = texture.getHeight();
			drawtex = texture.getScaledCopy(texture.getWidth(), texture.getHeight() - vertOffset);
			drawtex = drawtex.getSubImage(0, 0, drawtex.getWidth(), drawtex.getHeight() - 1);
			drawtex.getScaledCopy(scaleFactor).drawSheared(x - swayAmt, y + vertOffset*scaleFactor, swayAmt, 0);
		} else {
			float nx = x + texture.getWidth()/4;
			float ny = y + texture.getHeight()/4;
			
			new Graphics().drawLine(nx, ny, nx - swayAmt, ny - 20);
		}
	}
}
