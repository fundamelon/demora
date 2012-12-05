package main.entity;

import java.awt.Point;
import java.util.ArrayList;

import static java.lang.Math.*;

import main.AIManager;
import main.ControlManager;
import main.GameBase;
import main.Physics;
import main.ai.Path;
import main.ai.Pathfinder_AStar;

import org.newdawn.slick.Animation;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.TrueTypeFont;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Shape;

import org.lwjgl.util.vector.*;

import util.Util;


public abstract class Entity_mobile implements Entity{
	

	protected Image TEX_FRONT;
	protected Image TEX_BACK;
	protected Image TEX_LEFT;
	protected Image TEX_RIGHT;
	
	protected Image TEX_JUMP_FRONT;
	protected Image TEX_JUMP_BACK;
	protected Image TEX_JUMP_LEFT;
	protected Image TEX_JUMP_RIGHT;
	
	protected Image[] TEX_RUN_FRONT;
	protected Image[] TEX_RUN_SIDE;
	protected Image[] TEX_RUN_BACK;
	
	protected Animation ANIM_RUN_FRONT;
	protected Animation ANIM_RUN_LEFT;
	protected Animation ANIM_RUN_RIGHT;
	protected Animation ANIM_RUN_BACK;
	
	protected Image cur_img = null;
	protected Animation cur_anim = null;
	
	protected boolean flipCurImg = false;
	
	protected int blockSize, dustCount = 0;
	
	protected boolean isMoving = false;
	protected boolean isColliding = false;
	protected boolean followingPath = false;
	protected boolean sprinting = false;
	
	protected String moveAxis = "x";
	protected String action = "idle", direction = "front";
	
	protected Rectangle bounds = new Rectangle(0, 0, 32, 32);
	
	public Vector3f pos = new Vector3f(0, 0, 0);
	public Vector3f vel = new Vector3f(0, 0, 0);
	public Vector2f dir = new Vector2f(0, 0);
	public Vector2f imgPos = new Vector2f(0, 0);
	public Vector2f tilepos = new Vector2f(0, 0);

	public float velMult;
	public float ang;
	public float jumpForce = 0.3f;
	public float stamina = 100f;
	
	public float health = 100f;
	public float maxHealth = 100f;
	
	public float sprintCost = 0.03f;
	
	public float img_offset_x=0, img_offset_y=0;
	public String type = null, name = null;
	public String currentImage = "lib/img/girl_front_nobg.png";
	protected Animation[] anims;
	protected Path currentPath;
	protected Pathfinder_AStar pathfinder;
	
	public void setAnimation() {}
	
	
	public int[] itemTable = new int[128];
	
	public void init() {
		updateBounds();
		pathfinder = new Pathfinder_AStar(AIManager.getNodeMap());
	};
	
	public void draw() {};
	
	public void debugDraw(Graphics g) {
	//	g.setFont(new TrueTypeFont(new java.awt.Font("Helvetica", 10, 10), true));
		g.setColor(org.newdawn.slick.Color.black);
		g.drawString("moving:    "+isMoving, pos.x + bounds.getWidth(), pos.y - 40);
		g.drawString("colliding: "+isColliding, pos.x + bounds.getWidth(), pos.y - 30);
		g.drawString("following: "+followingPath, pos.x + bounds.getWidth(), pos.y - 20);
	}
	
	public void move(Vector2f newDir) {
		if(!isJumping()) {
			dir = newDir;
		} else {
			dir.x += newDir.x * 0.003f * ControlManager.getDelta();
			dir.y += newDir.y * 0.003f * ControlManager.getDelta();
		}
		
		if(dir.length() != 0)
			dir.normalise();
		
		vel.x += dir.x * velMult;
		vel.y += dir.y * velMult;
	}
	
	public void setPath(Path in) {
		currentPath = in;
	}
	
	public void setFollowPath(boolean a) {
		followingPath = a;
	}
	
	public void followPath(Path in) {
		if(in.nextStep() == null) {
			System.out.println("Path completed");
			return;
		}
		Vector2f target = new Vector2f((in.nextStep().getX()*32 - pos.x), (in.nextStep().getY()*32 - pos.y));
		move(target);
		target = new Vector2f((in.nextStep().getX()*32 - pos.x), (in.nextStep().getY()*32 - pos.y));
		float radius = 8;
		if(target.length() <= radius) {
			in.nextStep().setVisited(true);
		}
	}
	
	public void followPath() {
		followPath(currentPath);
	}
	

	public void update() {
			if(cur_anim != null) {
				if(isSprinting()) {
					cur_anim.update((long)(ControlManager.getDelta() * 1.5f));
				} else {
					cur_anim.update((long)(ControlManager.getDelta()));
				}
			}
			
			if(isJumping()) {
				vel.z -= Physics.gravity * ControlManager.getDelta();
				if(pos.z + (vel.z) <= 0 && vel.z <= 0) {
					pos.z = 0;
					vel.z = 0;
					if(cur_anim != null && cur_anim.isStopped()) {
						cur_anim.start();
						cur_anim.setCurrentFrame((cur_anim.getFrame()+1) % cur_anim.getFrameCount());
					}
				} else {
					setAction("jump");
				}
			}

			pos.z += vel.z * ControlManager.getDelta();
			
			
			//skip if not moving
			if(vel.x == 0 && vel.y == 0 && !isJumping()) {
				if(isMoving) {
					isMoving = false; 
					setAction("idle");
				}
			//	return;
			} else if(!isJumping()) {
				isMoving = true;
			//	System.out.println("Moving player, vel.x: "+vel.x+", vel.y:"+vel.y);
			}

			if(isSprinting() && isMoving()) {
				float cost = sprintCost * ControlManager.getDelta();
				if(stamina - cost > 0) {
					if(!isJumping()) {
						stamina -= cost;
					}
				} else {
					stamina = 0;
					stopSprint();
				}
			} else {
				float gain = 0.03f * ControlManager.getDelta();
				if(stamina + gain < 100) {
					stamina += gain;
				} else {
					stamina = 100;
				}
			}
			
		//	System.out.println("Velocity x="+vel.x+" y="+vel.y+" z="+vel.z);
			
			Vector2f newvel = new Vector2f(vel.x, vel.y);
			isColliding = false;
			
			
			if(Physics.collisions) {
				ArrayList<Shape>collisionArray = GameBase.getMap().getNearbyObstacles(this.tilepos.x, this.tilepos.y);
				for(int i = 0; i < collisionArray.size(); i++) {
					if(	collisionArray.get(i) == null )
						continue;
					
					Shape temp_obs = collisionArray.get(i);

					Shape attempted_bounds = getBounds();
					attempted_bounds.setX(getBounds().getX() + newvel.x * ControlManager.getDelta());
					attempted_bounds.setY(getBounds().getY() + newvel.y * ControlManager.getDelta());
					
					int cushion = 0;
					if(GameBase.getMap().collisionType(i) == 2) {
						cushion = 1;
					}
			
				//	System.out.println("- - - -");
					Vector2f impulse = Physics.splitAxisCollision(temp_obs, attempted_bounds);						
					if(impulse.length() > 0) {
						isColliding = true;
				//		System.out.println("Collision detected");
				//		impulse.scale(1f);
				//		System.out.println("impulse x="+impulse.x+" y="+impulse.y);
						newvel.x += impulse.x / ControlManager.getDelta();
						newvel.y += impulse.y / ControlManager.getDelta();
					}
				}
			}
			
			
			pos.x += newvel.x * ControlManager.getDelta();
			pos.y += newvel.y * ControlManager.getDelta();
			
			pos.x = Util.clamp(pos.x, 18, GameBase.getMap().getWidth() * 32 + blockSize + 18);
			pos.y = Util.clamp(pos.y, 18, GameBase.getMap().getHeight() * 32 + blockSize - 18);
			
			updateBounds();
		}
	
	
	public void updateDirection() {
		// direction controller
		if(isJumping()) {
			setAction("jump");
			return;
		}
		String newDirection = "";
		float velDiff = abs(vel.x) - abs(vel.y);
		if(velDiff > 0.001) {
			moveAxis = "x";
		} else if(velDiff < 0) {
			moveAxis = "y";
		}
		
		if(vel.length() != 0) {
			if(moveAxis.equals("x")) {
				if(vel.x < 0)
					newDirection = "left";
				else 
					newDirection = "right";
			} else if(moveAxis.equals("y")) {
				if(vel.y < 0)
					newDirection = "back";
				else
					newDirection = "front";
			}
			direction = newDirection;
			setAction("move");
		} else {
			setAction("idle");
		}
	}
	
	public void finalize() {
		vel.x = 0;
		vel.y = 0;
	}
	
	/** Updates the entity's animation or image based on input action and existing direction. */
	protected void setAction(String newAction) {
		if(GameBase.paused) return; 
		if(newAction == "move"){
			if(direction.equals("left")) {
				if(cur_anim == null || !cur_anim.equals(ANIM_RUN_LEFT)) {
					cur_anim = ANIM_RUN_LEFT;
					cur_anim.setPingPong(false);
					cur_anim.start();
				}
				
				flipCurImg = false;
			} else if(direction.equals("right")) {
				if(cur_anim == null || !cur_anim.equals(ANIM_RUN_RIGHT)) {
					cur_anim = ANIM_RUN_RIGHT;
					cur_anim.setPingPong(false);
					cur_anim.start();
				}
				
				flipCurImg = true;
			} else if(direction.equals("back")) {
				if(cur_anim == null || !cur_anim.equals(ANIM_RUN_BACK)) {
					cur_anim = ANIM_RUN_BACK;
					cur_anim.setPingPong(false);
					cur_anim.start();
				}
				
				flipCurImg = false;
			} else if(direction.equals("front")) {
				if(cur_anim == null || !cur_anim.equals(ANIM_RUN_FRONT)) {
					cur_anim = ANIM_RUN_FRONT;
					cur_anim.setPingPong(true);
					cur_anim.start();
				}
				
				flipCurImg = false;
			}
		} else if(newAction == "jump"){
			if(cur_anim != null && !cur_anim.isStopped()) cur_anim.stop();
		} else if(newAction == "idle") {
			if(cur_anim != null && !cur_anim.isStopped()) cur_anim.stop();
			if(cur_anim == ANIM_RUN_LEFT) {
				cur_anim = null;
				cur_img = TEX_LEFT;
				
				flipCurImg = false;
			} else if(cur_anim == ANIM_RUN_RIGHT) {
				cur_anim = null;
				cur_img = TEX_RIGHT;
				
				flipCurImg = false;
			} else if(cur_anim == ANIM_RUN_BACK) {
				cur_anim = null;
				cur_img = TEX_BACK;
				
				flipCurImg = false;
			} else if(cur_anim == ANIM_RUN_FRONT) {
				cur_anim = null;
				cur_img = TEX_FRONT;
				
				flipCurImg = false;
			}
		//	System.out.println("Set action to idle");
		} else
			System.out.println("Invalid action: \""+newAction+"\"");
		
		if(GameBase.debug_animation)
			if(cur_anim == null)
				System.out.println(cur_img.toString());
			else
				System.out.println(cur_anim.toString());
	}

	public void resizeBounds(float newWidth, float newHeight) {
		bounds.setWidth(newWidth);
		bounds.setHeight(newHeight);
	}

	public void updateBounds() {
		bounds.setLocation(pos.x - getBounds().getWidth()/2, pos.y + getBounds().getHeight()/2);
		tilepos.x = GameBase.getMap().getTileAtX(pos.x);
		tilepos.y = GameBase.getMap().getTileAtY(pos.y);
	}
	
	public void damage() {}
	
	public String getType() {
		return type;
	}
	
	public String getName() {
		return name;
	}
	
	
	public Vector pos() {
		return pos;
	}

	public float getX() {return pos.x;}
	public float getY() {return pos.y;}
	public float getZ() {return pos.z;}
	
	public Vector getImgPos() {
		return imgPos;
	}
	
	public Vector vel() {
		return vel;
	}
	
	public float getDX() {
		return vel.x;
	}
	
	public float getDY() {
		return vel.y;
	}
	
	public Image getImg() {
		try {
			return new Image(currentImage);
		} catch (SlickException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public float getImgOffsetX() {
		return img_offset_x;
	}
	
	public float getImgOffsetY() {
		return img_offset_y;
	}
	
	public void setImg(String newPath) {
		currentImage = newPath;
	}
	
	public Path getCurrentPath() {
		return currentPath;
	}

	public void setVelZ(float newVel) {
		vel.z = newVel;
	}
	
	public boolean isJumping() {
		return (pos.z == 0 && vel.z == 0) ? false : true;
	}

	
	public void startSprint() {
		if(stamina > 0 && !isJumping()) {
			sprinting = true;
		} else {
			sprinting = false;
		}
	}
	public void stopSprint() {
		sprinting = false;
	}
	public boolean isSprinting() {
		return sprinting;
	}
	
	public float getStaminaFraction() {
		return stamina / 100;
	}
	
	public boolean hasCollisions() {
		return true;
	}
	
	public boolean isMoving() {
		return isMoving;
	}
	
	public void jump() {
		if(!isJumping()) {
			this.vel.z += jumpForce;
		}
	}
	
	public float getHealth() {
		return health;
	}
	
	public float getTotalHealth() {
		return maxHealth;
	}
	
	public void drawFgEffects() {}
	public void drawBgEffects() {}

}
