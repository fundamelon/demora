package main.particles;

import java.util.ArrayList;

import main.entity.Entity;

import org.newdawn.slick.Image;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.particles.*;

public class Container implements Entity {
	private ArrayList<ParticleEmitter> emitters;
	
	public Container(ParticleEmitter[] emitters) {
		for(ParticleEmitter e : emitters) 
			this.emitters.add(e);
	}
	
	public Container(ArrayList<ParticleEmitter> emitters) {
		this.emitters.addAll(emitters);
	}
	
	public void addEmitter(ParticleEmitter e) {
		this.emitters.add(e);
	}
	
	public void addEmitter(ParticleEmitter e, int count) {
		for(int i = 0; i < count; i++) {
			addEmitter(e);
		}
	}
	
	public ParticleEmitter getEmitterByIndex(int i) {
		return emitters.get(i);
	}
	
	public ParticleEmitter[] getEmittersArray() {
		return (ParticleEmitter[])emitters.toArray();
	}
	
	public ArrayList<ParticleEmitter> getEmitters() {
		return emitters;
	}
	
	public void clearEmitterType(Class<? extends ParticleEmitter> type) {
		for(ParticleEmitter e : emitters) 
			if(e.getClass() == type)
				emitters.remove(e);
	}

	@Override
	public void draw() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Rectangle getBounds() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Image getImg() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Image getShadowCasterImg() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setImg(String path) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public float getX() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public float getY() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public float getAng() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void init() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void update() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public float getImgOffsetX() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public float getImgOffsetY() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void init(int nx, int ny, boolean tilewise) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean castShadows() {
		// TODO Auto-generated method stub
		return false;
	}
}
