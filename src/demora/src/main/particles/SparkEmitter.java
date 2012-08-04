package main.particles;

import java.util.ArrayList;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.geom.Rectangle;

import main.ControlManager;
import main.GameBase;
import main.GraphicsManager;
import main.PhysUtil;

public class SparkEmitter {
	abstract class Spark {

		public float x = 0, y = 0, life = 100, velX = 10, velY = 10;
		boolean alive = true;
		String type = "";
		Color col = Color.white;
		int randAng = 0;
		ArrayList<Float> xTrace = new ArrayList<Float>();
		ArrayList<Float> yTrace = new ArrayList<Float>();

		public void update() {
			if(!this.alive) return;
			if(this.life >= 0) {
				this.life--;
				move();
			} else alive = false;
		}
		
		public void move() {
			this.x += velX * ControlManager.getDelta();
			this.y += velY * ControlManager.getDelta();
		}
		
		public void setColor(Color newCol) {col = newCol;}
		
		public float getX() {return x;}
		public float getY() {return y;}
		public ArrayList<Float> getTraceX() {return this.xTrace;}
		public ArrayList<Float> getTraceY() {return this.yTrace;}
		public boolean alive() {return alive;}
		public Color getColor() {return col;}
	}
	
	class Spark_Default extends Spark {
		public Spark_Default(float x, float y, float life, float velX, float velY, String type) {
			this.x = x;
			this.y = y;
			this.life = life;
			this.velX = velX;
			this.velY = velY;
			this.type = type;
		}
	}

	protected ArrayList<Spark> sparks = new ArrayList<Spark>(1);
	protected float life, vel, startAng, angWidth;
	protected String type;
	protected Color col = Color.white;
	protected int repeatTimeout = 500, repeatCurTime = 0;
	protected boolean repeat = true;
	protected float ex, ey;
	protected int eamt;
	
	public void init(String type, float life, float vel, float startAng, float angWidth) {
		this.life = life;
		this.vel = vel;
		this.startAng = startAng;
		this.angWidth = angWidth;
	}
	
	public void createSparksAt(int amt, float x, float y) {
		eamt = amt;
		ex = x;
		ey = y;
	//	System.out.println(x + "... "+ex);
		for(int i = 0; i < amt; i++) {
			float ang = (float)((startAng + (Math.random()-0.5)*2*angWidth)*Math.PI/180);
			float randVelFactor = (float)Math.random();
			sparks.add(new Spark_Default(
					x, y, life, 
					vel * (float)Math.cos(ang) * randVelFactor,
					vel * (float)Math.sin(ang) * randVelFactor,
					type));
			sparks.get(i).setColor(col);
		//	System.out.println(vel);
		}
	}
	
	public void setRepeat(int time) {
		repeat = true;
		repeatTimeout = time;
	}
	
	public void render(Graphics g) {
		Color oldCol = g.getColor();
		for(int i = 0; i < sparks.size(); i++) {
			Spark e = sparks.get(i);
		//	if(!e.alive()) sparks.remove(e);
		//	else {
				g.setColor(e.getColor());
				g.drawRect(GraphicsManager.toLocalX(e.getX()), GraphicsManager.toLocalY(e.getY()), 0, 0);
				
				if(true) {
					for(int c = e.getTraceX().size()-1; c > 0; c--) {
						g.drawLine(	GraphicsManager.toLocalX(e.getTraceX().get(c)),
									GraphicsManager.toLocalY(e.getTraceY().get(c)), 
									GraphicsManager.toLocalX(e.getTraceX().get(c-1)), 
									GraphicsManager.toLocalY(e.getTraceY().get(c-1)));
					}
				}
		//	}
		}
		g.setColor(oldCol);
	}
	
	public void update() {
		if(repeat) {
			repeatCurTime += ControlManager.getDelta();
			if(repeatCurTime > repeatTimeout) {
				repeatCurTime = 0;
				createSparksAt(eamt, ex, ey);
			//	System.out.println(ex);
			}
		}
		for(Spark e : sparks) {
			e.update();
		}
	}
}