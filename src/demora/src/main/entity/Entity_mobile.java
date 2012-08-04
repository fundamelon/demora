package main.entity;

import java.awt.Point;

import main.ControlManager;
import main.GameBase;
import main.PhysUtil;
import main.pathfinding.Path;

import org.newdawn.slick.Animation;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Rectangle;


public abstract class Entity_mobile implements Entity{
	

	protected Image TEX_FRONT;
	protected Image TEX_BACK;
	protected Image TEX_LEFT;
	protected Image TEX_RIGHT;
	
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
	
	protected float vel_z;
	protected int blockSize, dustCount = 0;
	protected float dist;
	
	protected boolean isMoving = false;
	protected boolean isColliding = false;
	
	protected String action = "idle", direction = "front";
	
	protected Rectangle bounds = new Rectangle(0, 0, 32, 32);
	
	public float moveSpeed = 0.15f, x, y, z, ang;
	public float img_offset_x=0, img_offset_y=0;
	public String type = null, name = null;
	public String currentImage = "lib/img/girl_front_nobg.png";
	protected Animation[] anims;
	protected Path currentPath;
	
	public void setAnimation() {}
	
	public float imgX, imgY;
	public float dx, dy;
	
	public int[] itemTable = new int[128];
	
	public abstract void init();
	
	public void draw() {};
	
	public void move(float ndx, float ndy, float speed) {
		//Slow the entity down if diagonal
		if(ndx != 0 && ndy != 0) {
			ndx *= 0.7;
			ndy *= 0.7;
		}
		dx += ndx*speed*ControlManager.getDelta();
		dy += ndy*speed*ControlManager.getDelta();
	}

	public void move(double ndx, double ndy, float speed) {
		move((float)ndx, (float)ndy, speed);	
	}
	
	public void move(double ndx, double ndy) {
		move(ndx, ndy, moveSpeed);
	}
	
	public void setPath(Path in) {
		currentPath = in;
	}
	
	public void followPath(Path in) {
		if(in.nextStep() == null) {
			System.out.println("Path completed");
			return;
		}
		float targetAng = (float)Math.atan((in.nextStep().getY() - y)/(in.nextStep().getX() - x));
		move(Math.cos(targetAng), Math.sin(targetAng), moveSpeed);
	}
	

	public void update() {
		//	System.out.println("Moving player, dx: "+dx+", dy:"+dy);
			if(cur_anim != null)
				cur_anim.update((long)(Math.ceil(ControlManager.getDelta())));
			
			//skip if not moving
			if(dx == 0 && dy == 0) {
				if(isMoving) {
					isMoving = false; 
					setAction("idle");
				}
				return;
			} else isMoving = true;
			
			float ndx = dx, ndy = dy;
			isColliding = false;
			
			Rectangle attempted_bounds = getBounds();
			attempted_bounds.setX(getBounds().getX() + dx);
			attempted_bounds.setY(getBounds().getY() + dy);
			Rectangle[] collisionArray = GameBase.getZone().getCollisionArray();
			
			if(PhysUtil.collisions) {
				for(int i = 0; i < collisionArray.length; i++) {
				//	Rectangle temp_obs = new Rectangle(100, 100, 100, 100);
					Rectangle temp_obs = collisionArray[i];
					
					int cushion; //Keep below 8 or it will cause problems
					if(GameBase.getZone().collisionType(i) == 2) {
						cushion = 8;
					} else {
						cushion = 8;
					}
					if(temp_obs != null && Point.distance(getBounds().getCenterX(), getBounds().getCenterY(), temp_obs.getCenterX(), temp_obs.getCenterY()) < bounds.getBoundingCircleRadius()*2-cushion) {
						if(PhysUtil.collision(attempted_bounds, temp_obs)) {
							isColliding = true;
							
							temp_obs.setY(temp_obs.getY() + dy);
							
							if(PhysUtil.collision(getBounds(), temp_obs)) {
								ndx = 0;
							}
				
							temp_obs.setX(temp_obs.getX() + dx);
							temp_obs.setY(temp_obs.getY() - dy);
							
							if(PhysUtil.collision(getBounds(), temp_obs)) {
								ndy = 0;
							}
							
							temp_obs.setX(temp_obs.getX() - dx);
							
							//If it's within 24 pixels of corner, x movement is blocked,
							//	and y movement is not zero, then nudge it over
							//Left edge
							if(		!GameBase.getZone().blocked(i-1) &&
									getBounds().getX() + getBounds().getWidth()-24 < temp_obs.getX() && 
									getBounds().getX() + getBounds().getWidth()    > temp_obs.getX() && 
									dy != 0 && ndx == 0 && dx <= 0) {
								ndx -= 0.05 * ControlManager.getDelta();
							}
							//Right edge
							if(		!GameBase.getZone().blocked(i+1) &&
									getBounds().getX() > temp_obs.getX() + temp_obs.getWidth()-24 && 
									getBounds().getX() < temp_obs.getMaxX() + temp_obs.getWidth() && 
									dy != 0 && ndx == 0 && dx >= 0)	{
								ndx += 0.05 * ControlManager.getDelta();
							}
							
							//Top edge
							if(		!GameBase.getZone().blocked(i - GameBase.getZone().getWidth()) &&
									getBounds().getY() + getBounds().getHeight()-24 < temp_obs.getY() && 
									getBounds().getY() + getBounds().getHeight() > temp_obs.getY()&& 
									dx != 0 && ndy == 0 && dy >= 0)	{
								ndy -= 0.05 * ControlManager.getDelta();
							}
							//Bottom edge
							if(		!GameBase.getZone().blocked(i + GameBase.getZone().getWidth()) &&
									getBounds().getY() > temp_obs.getY() + temp_obs.getHeight()-24 && 
									getBounds().getY() < temp_obs.getMaxY() + temp_obs.getHeight() && 
									dx != 0 && ndy == 0 && dy >= 0)	{
								ndy += 0.05 * ControlManager.getDelta();
							}
						}
					}
				}
			}
			
			dist = Math.abs(ndx) + Math.abs(ndy);
			
			x += ndx;
			y += ndy;
			x = ControlManager.clamp(x, 18, GameBase.getZone().getWidth() * 32 + blockSize + 18);
			y = ControlManager.clamp(y, 18, GameBase.getZone().getHeight() * 32 + blockSize - 18);
			
			//TODO: Jump function goes here
			
			// direction controller
			if(dist == 0) {
				setAction("idle");
			//	System.out.println("Zero distance");
			}
			else if(dx * dy == 0) {
				String newDirection;
				if(dx < 0)
					newDirection = "left";
				else if(dx > 0)
					newDirection = "right";
				else if(dy < 0)
					newDirection = "back";
				else
					newDirection = "front";
				
				setAction("move", newDirection);
			}
					
			updateBounds();
			
			dx = 0;
			dy = 0;
		}

	private void setAction(String newAction) {
		setAction(newAction, direction);
	}
	
	protected void setAction(String newAction, String newDirection) {
		if(newAction == "move"){
			if(newDirection == "left") {
				if(cur_anim == null || !cur_anim.equals(ANIM_RUN_LEFT)) {
					cur_anim = ANIM_RUN_LEFT;
					cur_anim.setPingPong(false);
					cur_anim.start();
				}
				
				flipCurImg = false;
			}
			
			else if(newDirection == "right") {
				if(cur_anim == null || !cur_anim.equals(ANIM_RUN_RIGHT)) {
					cur_anim = ANIM_RUN_RIGHT;
					cur_anim.setPingPong(false);
					cur_anim.start();
				}
				
				flipCurImg = true;
			}
			
			else if(newDirection == "back") {
				if(cur_anim == null || !cur_anim.equals(ANIM_RUN_BACK)) {
					cur_anim = ANIM_RUN_BACK;
					cur_anim.setPingPong(false);
					cur_anim.start();
				}
				
				flipCurImg = false;
			}
			
			else if(newDirection == "front") {
				if(cur_anim == null || !cur_anim.equals(ANIM_RUN_FRONT)) {
					cur_anim = ANIM_RUN_FRONT;
					cur_anim.setPingPong(true);
					cur_anim.start();
				}
				
				flipCurImg = false;
			}
			
			else {
				System.out.println("Invalid direction: \""+newDirection+"\"");
				return;
			}
			direction = newDirection;
		}
		
		else if(newAction == "idle") {
			if(cur_anim != null && !cur_anim.isStopped()) cur_anim.stop();
			if(newDirection == "left") {
				cur_anim = null;
				cur_img = TEX_LEFT;
				
				flipCurImg = false;
			}
			
			else if(newDirection == "right") {
				cur_anim = null;
				cur_img = TEX_RIGHT;
				
				flipCurImg = false;
			}
			
			else if(newDirection == "back") {
				cur_anim = null;
				cur_img = TEX_BACK;
				
				flipCurImg = false;
			}
			
			else if(newDirection == "front") {
				cur_anim = null;
				cur_img = TEX_FRONT;
				
				flipCurImg = false;
			}
			
			else {
				System.out.println("Invalid direction: \""+newDirection+"\"");
				return;
			}
		//	System.out.println("Set action to idle");
		}
		else
			System.out.println("Invalid action: \""+newAction+"\"");
		
		direction = newDirection;
	}

	public void resizeBounds(float newWidth, float newHeight) {
		bounds.setWidth(newWidth);
		bounds.setHeight(newHeight);
	}

	public void updateBounds() {
		bounds.setX(x - getBounds().getWidth()/2);
		bounds.setY(y + getBounds().getHeight()/2);
	}
	
	public void damage() {}

	
	public String getType() {
		return type;
	}
	public String getName() {
		return name;
	}

	public float getX() {return x;}
	public float getY() {return y;}
	public float getZ() {return z;}
	
	public int getImgX() {
		return (int)imgX;
	}
	
	public int getImgY() {
		return (int)imgY;
	}
	
	public float getDX() {
		return dx;
	}
	
	public float getDY() {
		return dy;
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

	public void setVelZ(float newVel) {
		vel_z = newVel;
	}
	
	public boolean isJumping() {
		return z == 0 ? false : true;
	}

	public void jump() {
		this.vel_z += 300;
	}

}
