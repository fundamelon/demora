package main.particles;

import java.util.ArrayList;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.geom.Rectangle;

import util.Util;

import main.ControlManager;
import main.GameBase;
import main.Physics;

import org.lwjgl.util.vector.*;

public class SparkEmitter {
	protected ArrayList<Spark> sparks = new ArrayList<Spark>();
	protected float life, startAng, angWidth;
	protected String type;
	protected Color col = Color.white;
	protected float repeatTimeout = 100, repeatCurTime = 0;
	protected boolean repeat = false;
	protected Vector2f pos;
	protected float vel = 10;
	protected int eamt;
	protected int trailLength;
	
	public void init(float vel, float startAng, float angWidth, float life, String type) {
		this.vel = vel;
		this.startAng = startAng;
		this.angWidth = angWidth;
		this.life = life;
		this.type = type;
		createSparksAt(pos, eamt, trailLength);
	}
	
	public void init(float vel, float life, String type) {
		init(vel, 90, 90, life, type);
	}
	
	public void init(float life, String type) {
		init(this.vel, 90, 90, life, type);
	}
	
	public void init(String type) {
		init(this.vel, 90, 90, this.life, type);
	}
	
	public void setVel(float newvel) { this.vel = newvel; }
	public void setLife(float newlife) { this.life = newlife; }
	public void setType(String newtype) { this.type = newtype; }
	public void setTrails(int length) { this.trailLength = length; }
	
	public void createSparksAt(Vector2f newpos, int amt, int trailLength) {
		eamt = amt;
		pos = newpos;
		for(int i = 0; i < amt; i++) {
			float randVelFactor = (float)Math.random()/2 + 0.5f;
			float ang = (float)((Math.random() * angWidth) - angWidth/2) + startAng;
			Vector2f velVec = new Vector2f((float)Math.cos(ang), (float)Math.sin(ang));
			velVec.scale(vel * randVelFactor);
			sparks.add(new Spark(pos, velVec, life, type));
			sparks.get(i).setColor(col);
			sparks.get(i).setTrails(trailLength);
		}
	}
	
	public void createSparksAt(Vector2f newpos, int amt) {
		createSparksAt(newpos, amt, 100);
	}
	
	public void setRepeat(float time) {
		repeat = true;
		repeatTimeout = time;
	}
	
	public void render() {
		Graphics g = main.GameBase.g;
		Color oldCol = g.getColor();
		for(int i = 0; i < sparks.size(); i++) {
			Spark e = sparks.get(i);
			if(!e.alive()) sparks.remove(e);
			else {
				g.setColor(e.getColor());
				g.drawRect(Util.toScreenX(e.getX()), Util.toScreenY(e.getY()), 0, 0);
				
				if(true) {
					int trailCount = Math.min(e.trailLength-1, e.getTrace().size()-1);
					for(int c = trailCount; c >= 0; c--) {
						g.setColor(new Color(g.getColor().r, g.getColor().g, g.getColor().b, 1));
					//	System.out.println(e.getTrace().get(c) + ", "+ e.pos);
						if(c == trailCount) {
							g.drawLine(	e.pos.x, e.pos.y,
										Util.toScreenX(e.getTrace().get(c).x),
										Util.toScreenY(e.getTrace().get(c).y));
						} else {
							g.drawLine(	Util.toScreenX(e.getTrace().get(c).x), 
										Util.toScreenY(e.getTrace().get(c).y),
										Util.toScreenX(e.getTrace().get(c+1).x), 
										Util.toScreenY(e.getTrace().get(c+1).y));
						}
					}
				}
			}
		}
		g.setColor(oldCol);
	}
	
	public void update() {
		if(repeat) {
			repeatCurTime += ControlManager.getDelta();
			if(repeatCurTime > repeatTimeout) {
				for(int c = 0; c < repeatCurTime%repeatTimeout; c++)
					createSparksAt(pos, eamt, trailLength);
				repeatCurTime = 0;
			}
		}
		for(Spark s : sparks) {
			s.life -= ControlManager.getDelta()/1000f;
			s.update();
		}
	}
	
	abstract class SparkBase {
		public Vector2f pos, vel;
		public float life = 100;
		protected boolean alive = true;
		protected String type = "";
		protected Color col = Color.white;
		protected int randAng = 0;
		protected ArrayList<Vector2f> trace = new ArrayList<Vector2f>();
		protected int trailLength = 10;
		protected int trailClk = 0;
		protected float rot;

		public void update() {
			this.vel.scale(0.8f);
		//	this.vel.x += Math.cos(life * 10) * ControlManager.getDelta() * 0.001f;
		//	this.vel.y += Math.sin(life * 10) * ControlManager.getDelta() * 0.001f;
			
			if(!this.alive) return;
			if(this.life >= 0) {
				trailClk += 1;
				if(trailClk >= 0.5) {
					this.trace.add(new Vector2f(this.pos.x, this.pos.y));
					trailClk = 0;
					if(this.trace.size() >= trailLength) {
						this.trace.remove(0);
					}
				}
				move();
			} else alive = false;
		}
		
		public void move() {
			this.pos.x += vel.x * ControlManager.getDelta();
			this.pos.y += vel.y * ControlManager.getDelta();
		}
		
		public void setColor(Color newCol) {col = newCol;}
		public void setTrails(int length) {trailLength = length;}
		public void setAlive(boolean alive) {this.alive = alive;}
		
		public float getX() {return pos.x;}
		public float getY() {return pos.y;}
		public ArrayList<Vector2f> getTrace() {return trace;}
		public boolean alive() {return alive;}
		public Color getColor() {return col;}
	}
	
	class Spark extends SparkBase {
		public Spark(Vector2f pos, Vector2f vel, float life, String type) {
			this.pos = new Vector2f(pos.x, pos.y);
			this.vel = new Vector2f(vel.x, vel.y);
			this.life = life;
			this.type = type;
		}
	}
}