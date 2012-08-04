package main.entity;

import main.GameBase;

import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Rectangle;

@SuppressWarnings("all")
public class Entity_umbrin_test extends Entity_mobile implements Entity {

	public Entity_umbrin_test() throws SlickException {
		TEX_FRONT = new Image("lib/img/char/umbrin_0/umbrin_front_static.png");
		TEX_BACK = new Image("lib/img/char/umbrin_0/umbrin_back_static.png");
		TEX_LEFT = new Image("lib/img/char/umbrin_0/umbrin_left_static.png");
		TEX_RIGHT = new Image("lib/img/char/umbrin_0/umbrin_right_static.png");
		
		cur_img = TEX_FRONT;
	}
	
	public void init(int nx, int ny, boolean tilewise) {
		x = nx;
		y = ny;
		updateBounds();
		
		moveSpeed = 0.5f;
		
		bounds = new Rectangle(0, 0, 32, 32);
		
		System.out.println("ENT: umbrin test initialized");
	}
	
	public void init() {
		init(200, 200, false);
	}
	
	public void update() {
		super.img_offset_y = (float)Math.sin((System.currentTimeMillis()) *0.002)*5 - 20;
		
		dx = (EntityManager.getPlayer().getX() - x) * 0.01f;
		dy = (EntityManager.getPlayer().getY() - y) * 0.01f;
		
		x += dx;
		y += dy;
		
		if(dx > 0 && dx > Math.abs(dy)) {
			cur_img = TEX_RIGHT.copy();
		}
		else if(dx < 0 && -dx > Math.abs(dy)) {
			cur_img = TEX_LEFT.copy();
		}
		else if(dy < 0 && -dy > Math.abs(dx)) {
			cur_img = TEX_BACK.copy();
		} else {
			cur_img = TEX_FRONT.copy();
		}
	}
	
	public void updateBounds() {
		bounds.setX(x - getBounds().getWidth()/2);
		bounds.setY(y + getBounds().getHeight()/2);
	}
	
	@Override
	public void draw() {
		// TODO Auto-generated method stub
		
	}
	
	public Rectangle getBounds() {
		return bounds;
	}

	@Override
	public Image getShadowCasterImg() {
		return cur_img;
	}

	public Image getImg() {
		return cur_img;
	}
	
	public float getX() {
		return x;
	}

	public float getY() {
		return y;
	}

	@Override
	public float getAng() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean castShadows() {
		return true;
	}

}
