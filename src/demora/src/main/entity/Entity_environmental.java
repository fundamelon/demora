package main.entity;

import main.ai.Path;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.geom.Shape;

public class Entity_environmental implements Entity {

	@Override
	public void draw() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void debugDraw(Graphics g) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Shape getBounds() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Image getImg() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Image getShadowCasterImg() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setImg(String path) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public float getX() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public float getY() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public float getAng() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void init() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void update() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public float getImgOffsetX() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public float getImgOffsetY() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void init(int nx, int ny, boolean tilewise) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean castShadows() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Path getCurrentPath() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean hasCollisions() {
		// TODO Auto-generated method stub
		return false;
	}
	
	public boolean isMoving() {
		return false;
	}

	@Override
	public boolean isJumping() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void drawFgEffects() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void drawBgEffects() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public float getHealth() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public float getTotalHealth() {
		// TODO Auto-generated method stub
		return 0;
	}

}
