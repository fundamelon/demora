package main.particles;
import org.newdawn.slick.Image;
import org.newdawn.slick.particles.*;
import org.newdawn.slick.particles.ParticleSystem;

public abstract interface ParticleEmitter extends org.newdawn.slick.particles.ParticleEmitter {
	public void setPos(float x, float y);
}
