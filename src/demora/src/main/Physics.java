package main;

import org.newdawn.slick.geom.*;
import org.lwjgl.util.vector.*;
import org.lwjgl.util.vector.Vector2f;

import static java.lang.Math.*;

public class Physics {
	public static float gravity = 0.0010f;
	
	public static boolean collisions = true;
	
	public static boolean collidedFromLeft(Shape a, Shape b) {
		System.out.println("Left");
		return a.getX() < b.getX() && a.intersects(b);
	}

	public static boolean collidedFromRight(Shape a, Shape b) {
		System.out.println("Right");
		return a.getX() > b.getX() && a.intersects(b);
	}

	public static boolean collidedFromTop(Shape a, Shape b) {
		System.out.println("Top");
		return a.getY() > b.getY() && a.intersects(b);
	}

	public static boolean collidedFromBottom(Shape a, Shape b) {
		System.out.println("Bottom");
		return a.getY() < b.getY() && a.intersects(b);
	}
	
	/**
	 * Detect collision between two rectangular objects.
	 * @param a
	 * @param b
	 * @return Projection vector for shape b
	 */
	public static Vector2f splitAxisCollision(Shape a, Shape b) {
		Vector2f projection = new Vector2f(0, 0);
		Vector2f apos = new Vector2f(a.getCenterX(), a.getCenterY());
		Vector2f bpos = new Vector2f(b.getCenterX(), b.getCenterY());
		Vector2f diff = new Vector2f(bpos.x - apos.x, bpos.y - apos.y);
		Vector2f aw = new Vector2f(a.getWidth()/2, a.getHeight()/2);
		Vector2f bw = new Vector2f(b.getWidth()/2, b.getHeight()/2);
		
		Vector2f overlap = new Vector2f(0, 0);
		
		
		//Handle corners of collision
		if(true) {
			if(diff.x < 0 && diff.y < 0) {
				//Top left
			//	System.out.println("Top left");
				aw.x *= -1;
				aw.y *= -1;
			} else if(apos.x < bpos.x && apos.y >= bpos.y) {
				//Top right
			//	System.out.println("Top right");
				bw.x *= -1;
				aw.y *= -1;
			} else if(diff.x < 0 && diff.y >= 0) {
				//Bottom left
			//	System.out.println("Bottom left");
				bw.y *= -1;
				aw.x *= -1;
			} else if(diff.x >= 0 && diff.y >= 0) {
				//Bottom right 
			//	System.out.println("Bottom right");
				bw.y *= -1;
				bw.x *= -1;
			}
			overlap = new Vector2f((bpos.x + bw.x) - (apos.x + aw.x), (bpos.y + bw.y) - (apos.y + aw.y));

			if(abs(diff.x) > abs(aw.x) + abs(bw.x)) overlap.x = 0;
			if(abs(diff.y) > abs(aw.y) + abs(bw.y)) overlap.y = 0;

			if(overlap.x == 0 || overlap.y == 0) {
				overlap.scale(0f);
			}
			
			if(abs(overlap.x) < abs(overlap.y)){
				overlap.y = 0;
			} else {
				overlap.x = 0;
			}
		}
		

		

	//	System.out.println("Overlap x= "+overlap.x+" y= "+overlap.y);
		
		projection = overlap;
		projection.scale(-1f);
		
		return projection;
	}
}
