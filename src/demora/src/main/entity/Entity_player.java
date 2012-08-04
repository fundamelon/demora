package main.entity;

import java.awt.Point;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.newdawn.slick.Animation;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Circle;
import org.newdawn.slick.geom.Rectangle;


import main.*;
import main.pathfinding.Path;

@SuppressWarnings("all")
public class Entity_player extends Entity_mobile implements Entity {
	
	
	
	
	public Entity_player() throws SlickException {
	
		//Load up all of mein textures and animations!
		
		TEX_FRONT = new Image("lib/img/char/plr_front_static.png");
		TEX_BACK =  new Image("lib/img/char/plr_back_static.png");
		TEX_LEFT =  new Image("lib/img/char/plr_left_static.png");
		TEX_RIGHT = new Image("lib/img/char/plr_right_static.png");
		
		TEX_RUN_FRONT = new Image[] {
			new Image("lib/img/char/plr_front_run02.png"),
			new Image("lib/img/char/plr_front_run01.png"),
			new Image("lib/img/char/plr_front_run03.png")
		};
		
		TEX_RUN_SIDE = new Image[] {
			new Image("lib/img/char/plr_side_run01.png"),
			new Image("lib/img/char/plr_side_run02.png"),
			new Image("lib/img/char/plr_side_run03.png"),
			new Image("lib/img/char/plr_side_run04.png")
		};
		
		TEX_RUN_BACK = new Image[] {
			new Image("lib/img/char/plr_back_run01.png"),
			new Image("lib/img/char/plr_back_run02.png"),
			new Image("lib/img/char/plr_back_run03.png"),
			new Image("lib/img/char/plr_back_run04.png")
		};
		
		ANIM_RUN_FRONT = new Animation(TEX_RUN_FRONT, new int[] {200, 120, 200}, true);
		ANIM_RUN_LEFT = new Animation(TEX_RUN_SIDE, new int[] {120, 200, 120, 200}, true);
		ANIM_RUN_RIGHT = new Animation(TEX_RUN_SIDE, new int[] {120, 200, 120, 200}, true);
		ANIM_RUN_BACK = new Animation(TEX_RUN_BACK, new int[] {200, 120, 200, 120}, true);
		
		cur_img = TEX_FRONT;
		
		action = "idle";
		direction = "front";
		
		moveSpeed = 0.15f;
		
		bounds = new Rectangle(0, 0, 32, 32);
	
	}
	
	/**
	 * Re-load the player at a position
	 * @param nx - start pos x
	 * @param ny - start pos y
	 * @param tilewise - True means tiles, false means pixels.
	 */
	@Override
	public void init(int nx, int ny, boolean tilewise) {
		
		x = nx * (tilewise ? 1 : 32);
		y = ny * (tilewise ? 1 : 32);
		updateBounds();
		
		System.out.println("ENT: player initialized");
	}
	
	public void init() {
		init(1, 1, true);
	}

	public void followPath(Path in) {
		if(in.nextStep() == null) {
			System.out.println("Path completed");
			return;
		}
		float xOffset = -16, yOffset = 16;
		float targetAng = (float)((Math.atan2((in.nextStep().getY()*32 + xOffset - y), (in.nextStep().getX()*32 + yOffset - x))+Math.PI));
		move((float)-Math.cos(targetAng), (float)-Math.sin(targetAng), moveSpeed);
		if(Math.round((this.x + xOffset)/32) == in.nextStep().getX() && Math.round((this.y + yOffset)/32) == in.nextStep().getY()) {
			in.nextStep().setVisited(true);
		}
	}
	
	public Image getImg() {
		img_offset_x = 0;
		img_offset_y = 0;
		if(cur_anim != null) {
			cur_img = cur_anim.getCurrentFrame();
			
			//Set of images that will offset player upward
			
			if( cur_img.getResourceReference().equals("lib/img/char/plr_front_run01.png")) {
				img_offset_y -= 3;
			}
			if(	cur_img.getResourceReference().equals("lib/img/char/plr_back_run02.png") ||
				cur_img.getResourceReference().equals("lib/img/char/plr_back_run04.png")) {
				img_offset_y += 3;
			}
			if(	cur_img.getResourceReference().equals("lib/img/char/plr_back_run01.png") ||
				cur_img.getResourceReference().equals("lib/img/char/plr_front_run02.png")) {
				img_offset_x += 2;
			}
			if(	cur_img.getResourceReference().equals("lib/img/char/plr_back_run03.png") ||
				cur_img.getResourceReference().equals("lib/img/char/plr_front_run04.png")) {
				img_offset_x -= 2;
			}
			if(	cur_img.getResourceReference().equals("lib/img/char/plr_side_run01.png") || 
				cur_img.getResourceReference().equals("lib/img/char/plr_side_run03.png")) 
			{
				if(flipCurImg)
					img_offset_x -= 2;
				else
					img_offset_x += 2;
				
				img_offset_y -= 2;
			}
		}
		if(flipCurImg) {
			cur_img = cur_img.getFlippedCopy(true, false);
		}
		return cur_img;
	}
	
	public Image getShadowCasterImg() {
		if(cur_img.equals(TEX_FRONT))
			return TEX_BACK;
		
		else if(cur_img.equals(TEX_BACK))
			return TEX_FRONT;
		
		else return cur_img;
	}

	public Rectangle getBounds() {
		return bounds;
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
