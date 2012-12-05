package main.entity;

import java.awt.Point;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.lwjgl.util.vector.Vector2f;
import org.newdawn.slick.Animation;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Circle;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.particles.ParticleSystem;


import main.*;
import main.ai.Node;
import main.ai.Path;
import main.ai.Pathfinder_AStar;
import main.particles.Emitter_Dust_TrailSmall;

@SuppressWarnings("all")
public class Entity_player extends Entity_mobile implements Entity {

	public static org.newdawn.slick.particles.ParticleSystem ps_sprintdust;
	
	public Entity_player() throws SlickException {
		
		ps_sprintdust = new ParticleSystem(new Image("lib/img/particle/smoke_01.tga"));
		ps_sprintdust.setPosition(0, 0);
		ps_sprintdust.setBlendingMode(ParticleSystem.BLEND_COMBINE);
		ps_sprintdust.addEmitter(new Emitter_Dust_TrailSmall(this.pos.x, this.pos.y));
		ps_sprintdust.getEmitter(0).setEnabled(false);
	
		//Load up all textures and animations!
		
		TEX_FRONT = new Image("lib/img/char/plr_front_static.png");
		TEX_BACK  = new Image("lib/img/char/plr_back_static.png");
		TEX_LEFT  = new Image("lib/img/char/plr_left_static.png");
		TEX_RIGHT = new Image("lib/img/char/plr_right_static.png");
		
		TEX_JUMP_FRONT = new Image("lib/img/char/plr_front_run02.png");
		TEX_JUMP_BACK  = new Image("lib/img/char/plr_back_run03.png");
		TEX_JUMP_LEFT  = new Image("lib/img/char/plr_side_run02.png");
		TEX_JUMP_RIGHT = new Image("lib/img/char/plr_side_run04.png");
		
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
		
		velMult = 0.15f;
		
		bounds = new Rectangle(0, 0, 30, 30);
	
	}
	
	/**
	 * Re-load the player at a position
	 * @param nx - start pos x
	 * @param ny - start pos y
	 * @param tilewise - True means tiles, false means pixels.
	 */
	public void init(int nx, int ny, boolean tilewise) {
		
		pos.x = nx * (tilewise ? 1 : 32);
		pos.y = ny * (tilewise ? 1 : 32);
		pos.x = 64;
		pos.y = 64;
		super.init();
		
		System.out.println("ENT: player initialized");
	}

	public void move(Vector2f newDir) {
		if(!isJumping() && !isSprinting()) {
			dir.x += newDir.x * 0.05f * ControlManager.getDelta();
			dir.y += newDir.y * 0.05f * ControlManager.getDelta();
			if(newDir.x == 0) dir.x = 0;
			if(newDir.y == 0) dir.y = 0;
		} else if(isSprinting()) {
			dir.x += newDir.x * 0.005f * ControlManager.getDelta();
			dir.y += newDir.y * 0.005f * ControlManager.getDelta();
		} else {
			dir.x += newDir.x * 0.003f * ControlManager.getDelta();
			dir.y += newDir.y * 0.003f * ControlManager.getDelta();
		}
		
		if(dir.length() != 0)
			dir.normalise();
		
		if(isSprinting()) {
			vel.x += dir.x * velMult * 1.8f;
			vel.y += dir.y * velMult * 1.8f;
		} else {
			vel.x += dir.x * velMult;
			vel.y += dir.y * velMult;
		}
	}
	
	public void init() {
		init(1, 1, true);
	}
	
	public void update() {
		ps_sprintdust.getEmitter(0).setEnabled(isMoving() && isSprinting() && !isJumping());
		
		if(ps_sprintdust.getEmitterCount() != 0) {
			((Emitter_Dust_TrailSmall)ps_sprintdust.getEmitter(0)).setPos(this.getBounds().getCenterX(), this.getBounds().getCenterY() + 10);
			ps_sprintdust.update((int)ControlManager.getDelta());
		}
		super.update();
		super.updateDirection();
		finalize();
	}
	
	public void finalize() {
		if(!isJumping()) {
			vel.x *= 0;
			vel.y *= 0;
		}
		super.finalize();
	}

	
	
	public void setPath(Node target) {
		pathfinder.pathfind(AIManager.getNodeMap().getNodeAt(GameBase.getMap().getTileAt(pos.x, pos.y)), target);
		currentPath = pathfinder.createPath();
	}
	
	public Image getImg() {
		img_offset_x = 0;
		img_offset_y = 0;
		if(isJumping()) {
			img_offset_y -= pos.z;
		}
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
	
	public void jump() {
		if(!isJumping()) {
			this.vel.x *= 1.5f;
			this.vel.y *= 1.5f;
			this.vel.z += jumpForce;
		}
	}

	@Override
	public float getAng() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	public void drawFgEffects() {
		ps_sprintdust.render();
	}
	
	public void drawBgEffects() {
		
	}
	

	@Override
	public boolean castShadows() {
		return true;
	}
}
