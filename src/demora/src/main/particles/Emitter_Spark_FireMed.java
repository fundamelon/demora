package main.particles;

import java.util.ArrayList;

import main.ControlManager;
import main.particles.SparkEmitter.Spark;

import org.newdawn.slick.Color;

public class Emitter_Spark_FireMed extends SparkEmitter{
	class Spark extends SparkEmitter.Spark{

		public Spark(float x, float y, float life, float velX, float velY, String type) {
			super.x = x;
			super.y = y;
			super.life = 100;
			super.velX = velX * (float)(Math.random()-0.5) * 0.01f;
			super.velY = velY * (float)Math.random();
			super.type = type;
			col = Color.darkGray;
		}
		
		public void update() {
			super.setColor(new Color(
					col.r * (life/30),
					col.g * (life/40),
					col.b * (life/70),
					col.a * (life/35)*(float)(1-Math.random()*0.01)));
			super.update();
		}
		
		public void move() {
			super.randAng += ControlManager.getDelta() * 0.5f;
			super.velX += Math.cos(randAng) * 0.001f;
			
			super.velY += (Math.sin(randAng) < 0)? Math.sin(randAng)*0.0001f : 0;

			super.move();
		}
	}

	public void createSparksAt(int amt, float x, float y) {
		eamt = amt;
		ex = x;
		ey = y;
		for(int i = 0; i < amt; i++) {
			float ang = (float)((startAng + (Math.random()-0.5)*2*angWidth)*Math.PI/180);
			float randVelFactor = (float)Math.random();
			sparks.add(new Spark(
					x+5, y, life, 
					1,
					-Math.abs(0.03f * (float)Math.sin(ang)) - 0.01f,
					type));
			sparks.get(i).setColor(col);
		//	System.out.println(vel);
		}
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
		super.update();
	}
}
