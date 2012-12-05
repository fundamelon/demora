package main.entity;

import main.ControlManager;

import org.newdawn.slick.Image;
import org.newdawn.slick.geom.*;

import util.Util;

public class Entity_cursor extends Entity_mobile implements Entity {

	public Entity_cursor() {
		bounds = new Rectangle(0, 0, 16, 16);
		pos.x = Util.toWorldX(ControlManager.getMouseX());
		pos.y = Util.toWorldY(ControlManager.getMouseY());
	}
	@Override
	public float getAng() {
		return 0;
	}
	
	public void update() {
		this.vel.x = Util.toWorldX(ControlManager.getMouseX() - this.pos.x);
		this.vel.y = Util.toWorldY(ControlManager.getMouseY() - this.pos.y);
		super.update();
	}

	@Override
	public void init(int nx, int ny, boolean tilewise) {
		update();
	}

	@Override
	public Shape getBounds() {
		return bounds;
	}
	
	@Override
	public Image getImg() {
		return null;
	}

	@Override
	public Image getShadowCasterImg() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean castShadows() {
		// TODO Auto-generated method stub
		return false;
	}

}
