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
		
		bounds = new Rectangle(0, 0, 32, 32);
	}
	
	public void init(int nx, int ny, boolean tilewise) {
		pos.x = nx;
		pos.y = ny;
		updateBounds();
		
		velMult = 0.5f;
		
		
		System.out.println("ENT: umbrin test initialized");
	}
	
	public void init() {
		init(200, 200, false);
	}
	
	public void update() {
		super.pos.z = (float)Math.sin((System.currentTimeMillis()) *0.002)*8 - 25;
		
		vel.x = (EntityManager.getPlayer().getX() - pos.x) * 0.01f;
		vel.y = (EntityManager.getPlayer().getY() - pos.y) * 0.01f;
		
		super.update();
		
		if(vel.x > 0 && vel.x > Math.abs(vel.y)) {
			cur_img = TEX_RIGHT.copy();
		}
		else if(vel.x < 0 && -vel.x > Math.abs(vel.y)) {
			cur_img = TEX_LEFT.copy();
		}
		else if(vel.y < 0 && -vel.y > Math.abs(vel.x)) {
			cur_img = TEX_BACK.copy();
		} else {
			cur_img = TEX_FRONT.copy();
		}
		super.finalize();
		super.updateBounds();
		
	//	move(pos.x, pos.y);
	}
	
	public void updateBounds() {
		bounds.setX(pos.x - getBounds().getWidth()/2);
		bounds.setY(pos.y + getBounds().getHeight()/2);
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
		return pos.x;
	}

	public float getY() {
		return pos.y;
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
