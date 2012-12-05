package main.particles;

import java.util.ArrayList;

import main.ControlManager;
import main.particles.SparkEmitter.Spark;

import org.newdawn.slick.Color;

import org.lwjgl.util.vector.*;

public class SparkEmitter_FireMed extends SparkEmitter{
	class FireSpark extends SparkEmitter.SparkBase{

		public FireSpark(Vector2f pos, Vector2f vel, float life, String type) {
			super.pos = pos;
			super.vel = vel;
			super.life = 3000;
			super.vel.x *= (float)(Math.random()-0.5) * 0.01f;
			super.vel.y *= (float)Math.random();
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
			super.vel.x += ControlManager.getDelta() * Math.cos(randAng) * 0.001f;
			super.vel.y += ControlManager.getDelta() * ((Math.sin(randAng) < 0)? Math.sin(randAng)*0.0001f : 0);

			super.move();
		}
	}

	public void createSparksAt(Vector2f newpos, int amt) {
		eamt = amt;
		this.pos = newpos;
		for(int i = 0; i < amt; i++) {
			float ang = (float)((startAng + (Math.random()-0.5)*2*angWidth)*Math.PI/180);
			float randVelFactor = (float)Math.random();
			Vector2f velVec = new Vector2f((float)Math.cos(ang), (float)Math.sin(ang));
			velVec.scale(vel * randVelFactor);
			sparks.add(new Spark(pos, velVec, -Math.abs(0.03f * (float)Math.sin(ang)) - 0.01f, type));
			sparks.get(i).setColor(col);
		}
	}
	
	public void update() {
		super.update();
	}
}
