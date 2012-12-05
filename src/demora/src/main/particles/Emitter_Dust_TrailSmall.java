package main.particles;
import main.*;

import org.newdawn.slick.Image;
import org.newdawn.slick.particles.*;

public class Emitter_Dust_TrailSmall implements ParticleEmitter {
	/** The x coordinate of the center of the effect */
	private int x;
	/** The y coordinate of the center of the effect */
	private int y;
	
	/** The particle emission rate */
	private int interval = 80;
	/** Time til the next particle */
	private int timer;
	/** The size of the initial particles */
	private float size = 10f;
	
	/** Timer controlling particle variation */
	private int variation_timer = 0;
	
	private boolean enabled = false;
	
	/**
	 * Create a dust effect at 0,0
	 */
	public Emitter_Dust_TrailSmall() {
	}

	/**
	 * Create a dust effect at x,y
	 * 
	 * @param x2 The x coordinate of the effect
	 * @param y2 The y coordinate of the effect
	 */
	public Emitter_Dust_TrailSmall(int x2, int y2) {
		this.x = x2;
		this.y = y2;
	}
	
	public Emitter_Dust_TrailSmall(float x, float y) {
		this.x = (int)x;
		this.y = (int)y;
	}

	/**
	 * Create a dust effect at x,y
	 * 
	 * @param x The x coordinate of the effect
	 * @param y The y coordinate of the effect
	 * @param size The size of the particle being pumped out
	 */
	public Emitter_Dust_TrailSmall(int x, int y, float size) {
		this.x = x;
		this.y = y;
		this.size = size;
	}
	
	/**
	 * @see org.newdawn.slick.particles.ParticleEmitter#update(org.newdawn.slick.particles.ParticleSystem, int)
	 */
	public void update(ParticleSystem system, int delta) {
		timer -= delta;
		variation_timer += delta;
		if (timer <= 0 && isEnabled()) {
			timer = interval;
			Particle p = system.getNewParticle(this, 4000);
			p.setColor(0.8f, 0.6f, 0.6f, 0.4f);
			p.setPosition(x + (float)(Math.random() * 2f), y);
			p.setSize(size*3f);
			float vx = (float) (-0.005f + (Math.random() * 0.01f)) + (float)Math.sin(variation_timer * 0.0003 + Math.sin(variation_timer*0.001))*0.007f;
			float vy = (float) (-(Math.random() * 0.05f)-0.03f) * 0.15f;
			
			
			p.setVelocity(vx/2, vy/2, 2f);
		}
	}

	/**
	 * @see org.newdawn.slick.particles.ParticleEmitter#updateParticle(org.newdawn.slick.particles.Particle, int)
	 */
	public void updateParticle(Particle particle, int delta) {
		if (particle.getLife() > 1000) {
			particle.adjustSize(0.06f * delta);
			particle.adjustColor(0, 0, 0, delta*-0.0002f);
			
		} else {
			particle.adjustSize(0.02f * delta * (size / 40.0f));
			particle.adjustColor(0,  0, 0, delta*-0.08f);
		}
		float c = 0.002f * delta;
		float vx = (float)Math.sin(variation_timer * 0.0003 + Math.sin(variation_timer*0.001))*0.000001f;
		float vy = 0;
		
		particle.adjustVelocity(vx, vy);
		
	}
	
	public void setPos(float nx, float ny) {
		x = (int)nx;
		y = (int)ny;
	}

	/**
	 * @see org.newdawn.slick.particles.ParticleEmitter#isEnabled()
	 */
	public boolean isEnabled() {
		return enabled;
	}

	/**
	 * @see org.newdawn.slick.particles.ParticleEmitter#setEnabled(boolean)
	 */
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	/**
	 * @see org.newdawn.slick.particles.ParticleEmitter#completed()
	 */
	public boolean completed() {
		return false;
	}

	/**
	 * @see org.newdawn.slick.particles.ParticleEmitter#useAdditive()
	 */
	public boolean useAdditive() {
		return false;
	}

	/**
	 * @see org.newdawn.slick.particles.ParticleEmitter#getImage()
	 */
	public Image getImage() {
		return null;
	}

	/**
	 * @see org.newdawn.slick.particles.ParticleEmitter#usePoints(org.newdawn.slick.particles.ParticleSystem)
	 */
	public boolean usePoints(ParticleSystem system) {
		return false;
	}

	/**
	 * @see org.newdawn.slick.particles.ParticleEmitter#isOriented()
	 */
	public boolean isOriented() {
		return false;
	}

	/**
	 * @see org.newdawn.slick.particles.ParticleEmitter#wrapUp()
	 */
	public void wrapUp() {
	}

	/**
	 * @see org.newdawn.slick.particles.ParticleEmitter#resetState()
	 */
	public void resetState() {
	}

	public float getBrightness() {
		return 0.4f + (float)((Math.sin(variation_timer * 0.003f) + Math.cos(variation_timer * 0.01f)) * 0.01f);
	}
}
