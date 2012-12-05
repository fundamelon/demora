package main;

import java.awt.Point;
import java.awt.geom.Point2D;

import org.lwjgl.util.vector.Vector2f;
import org.newdawn.slick.geom.Rectangle;

import util.Util;

import main.entity.EntityManager;

@SuppressWarnings("all")
public class Camera {
	public static Vector2f pos = new Vector2f(GameBase.getWidth()/2, GameBase.getHeight()/2);
	public static Vector2f origin = pos;
	public static Vector2f newPos = new Vector2f(0, 0);
	public static boolean moving = false;
	public static int time, tick;
	public static float velMult;
	public static float moveDist, curDist;
	public static boolean lockToPlayer = false;
	public static boolean shaking = false;
	
	public static Rectangle visibleArea = new Rectangle(pos.x, pos.y, 864, 664);
	
	public static void setX(float nx) {pos.x = nx;}
	public static void setY(float ny) {pos.y = ny;}
	public static void setPoint(Point npos) {pos.x = npos.x; pos.y = npos.y;}
	
	public static float getX() {return pos.x;}
	public static float getY() {return pos.y;}
	public static float getAnchorX() {return (pos.x - GameBase.getWidth()/2);}
	public static float getAnchorY() {return (pos.y - GameBase.getHeight()/2);}
	public static String getString() {return "X: "+pos.x+" Y: "+pos.y;}
	
	/**
	 * Move the camera to certain position
	 * @param x - new pos x
	 * @param y - new pos y
	 * @param ntime - movement interval
	 */
	public static void moveToPos(float x, float y, float mult) {
		moving = true;
		origin = pos;
		newPos = new Vector2f(x, y);
		velMult = mult;
	}
	
	/**
	 * Move the camera to certain position
	 * @param npos - new Point
	 * @param ntime - movement interval
	 */
	public static void moveToPos(Vector2f npos, float mult) {
		moveToPos(npos.x, npos.y, mult);
	}
	
	
	/**
	 * Move the camera if it's not at the desired coordinates yet
	 */
	public static void update() {
		if(true) {
			try {
				if(lockToPlayer) {
					newPos.x = EntityManager.getPlayer().getX();
					newPos.y = EntityManager.getPlayer().getY();
					velMult = 0.01f;
				}
				float nx = (newPos.x - pos.x) * velMult * ControlManager.getDelta();
				float ny= (newPos.y - pos.y) * velMult * ControlManager.getDelta();
				if(shaking) {
					nx += 5 * (Math.random() - 0.5f);
					ny += 5 * (Math.random() - 0.5f);
				}
				pos.x = Util.clamp(pos.x + nx, GameBase.getWidth()/2, (GameBase.getMap().getWidth()) * 32 - GameBase.getWidth()/2);
				pos.y = Util.clamp(pos.y + ny, GameBase.getHeight()/2, (GameBase.getMap().getHeight()) * 32 - GameBase.getHeight()/2);
			} catch(ArithmeticException e){}
		}
		lockToPlayer = false;
		visibleArea.setLocation(getAnchorX()-32, getAnchorY()-32);
	}
	
	public static Rectangle getVisibleArea() {
		return visibleArea;
	}
	
	public static void setShake(boolean val) {
		shaking = val;
	}
	
	public static void toggleShake() {
		shaking = !shaking;
	}
	
	public static void followPlayer() {
		lockToPlayer = true;
	}
}
