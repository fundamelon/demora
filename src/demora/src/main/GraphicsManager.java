package main;

import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.ByteBuffer;
import java.util.*;

import javax.imageio.ImageIO;

import main.ai.*;
import main.entity.*;
import main.map.*;
import main.particles.*;

import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.Sys;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.*;
import org.lwjgl.util.vector.*;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Color;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Circle;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Shape;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.particles.*;

import util.Util;

public class GraphicsManager {
	
	static Image[] texture;
	static GameBase panel;
	public static org.newdawn.slick.particles.ParticleSystem particle_system_fire;
	public static org.newdawn.slick.particles.ParticleSystem particle_system_smoke;
	public static org.newdawn.slick.particles.ParticleSystem particle_system_magic;
	private static int sparkct = 0, particle_count = 0;
	public static boolean first_run = true;
	
	private static Color fadeCol = new Color(0, 0, 0);
	private static Color overlayCol = new Color(fadeCol.getRed(), fadeCol.getGreen(), fadeCol.getBlue(), 0);
	//Vars with preceding underscore are to be values for render options.  :O
	private static boolean fadeMode = false, helperText = false, shake = false;
	
	static Pathfinder_AStar pathfinderTest = new Pathfinder_AStar(AIManager.getNodeMap());
	
	private static boolean debug = false;
	
	private static SparkEmitter sparktest = new SparkEmitter();
	
	public static Image grassblade_tex0, grassblade_tex1;
	/**
	 * Initialize state
	 */
	public static void init() {		
		try {
			particle_system_fire = new ParticleSystem(new Image("lib/img/particle/flamelrg_02.tga"));
			particle_system_smoke = new ParticleSystem(new Image("lib/img/particle/smoke_02.tga"));
			particle_system_magic = new ParticleSystem(new Image("lib/img/particle/smoke_02.tga"));
		} catch (SlickException e) {
			e.printStackTrace();
		}
		
		particle_system_smoke.setBlendingMode(ParticleSystem.BLEND_COMBINE);
		particle_system_fire.setBlendingMode(ParticleSystem.BLEND_ADDITIVE);
		particle_system_magic.setBlendingMode(ParticleSystem.BLEND_ADDITIVE);
		
		
		try {
			grassblade_tex0 = new Image("lib/img/tilesets/individual/grass_blade0a_tint1.png");
			grassblade_tex1 = new Image("lib/img/tilesets/individual/grass_blade1.png");
		} catch(Exception e) {
			e.printStackTrace();
		}

	//	Debug grass
	//	GameBase.getMap().createTallGrass(0, new Rectangle(0, 0, 3000, 2000), 15000);
	//	GameBase.getMap().createTallGrass(0, new Rectangle(0, 0, 3000, 2000), 15000);
		
		TiledMap map = GameBase.getMap().getData();
		int groupID = map.getObjectGroupID("grass");
		for(int i = 0; i < map.getObjectCount(groupID); i++) {
			GameBase.getMap().createTallGrass(0, map.getObjectX(groupID, i), map.getObjectY(groupID, i));
		}
		
		sparktest.init(5f, 90, 45, 10, "Fire");
		Detail_fog_overlay.init();
	}
	
	/**
	 * Main function that redraws all graphics on a GameBase.
	 * @param g - graphics context
	 * @param delta - update delta
	 */
	public static void renderGame(Graphics g, float delta) {
		Color oldCol;
		oldCol = g.getColor();
		g.setColor(Color.white);
		
		int width = GameBase.getWidth();
		int height = GameBase.getHeight();

	//	Camera.followPlayer();
		Camera.update();
		if(ControlManager.keyStatus(Keyboard.KEY_5)) {
			Camera.moveToPos(
					Util.toWorldX(ControlManager.getMouseX()), 
					Util.toWorldY(ControlManager.getMouseY()), 0.01f);
		} else {
			Camera.followPlayer();
		}
		
		if(GameBase.mapRendering)
			renderMap(g);

		
		
		//Set the camera to follow the player with the players coordinates, then update the camera.
		
		
		g.translate(-Camera.getAnchorX(), -Camera.getAnchorY());

		if(debug) {
			for(Shape r : GameBase.getMap().getNearbyObstacles(
					EntityManager.getPlayer().tilepos.x, EntityManager.getPlayer().tilepos.y)) {
				if(r != null) {
					g.draw(r);
				}
			}
			
		//	AIManager.renderNodeMap(g);		
			for(Entity e : EntityManager.entityTable)
				if(e.getCurrentPath() != null) {
					e.getCurrentPath().render(g);		
				}
		}
		
		
		
		if(debug) {
			Entity_player player = EntityManager.getPlayer();
			g.draw(new Circle(player.getBounds().getCenterX(), player.getBounds().getCenterY(), player.getBounds().getBoundingCircleRadius()+4));
			
			if(ControlManager.mouseButtonStatus(ControlManager.mousePrimary)) {
				g.setColor(Color.red);
				
			//	for(int i = 0; i < particle_system_fire.getEmitterCount(); i++) {
			//		particle_system_fire.getEmitter(i).setPos(toWorldX(ControlManager.getMouseX()), toWorldY(ControlManager.getMouseY()));
			//	}
			}
			
			if(ControlManager.mouseButtonStatus(ControlManager.mouseSecondary)) {
				int tileX = GameBase.getMap().getTileAtX(Util.toWorldX(ControlManager.getMouseX()));
				int tileY = GameBase.getMap().getTileAtY(Util.toWorldY(ControlManager.getMouseY()));
				Color oldColor = g.getColor();
				g.setColor(Color.black);
				g.drawRect(Util.toScreenX(tileX * 32), Util.toScreenY(tileY * 32), 32, 32);
				g.setColor(oldColor);
			//	System.out.println("ID: "+(GameBase.getZone().getData().getTileId(tileX, tileY, 2)));
			}
			
		//	g.drawRect(Util.toWorldX(ControlManager.getMouseX() - 8),  Util.toWorldY(ControlManager.getMouseY() - 8), 16, 16);

			if(ControlManager.mouseButtonClicked(ControlManager.mousePrimary)) {
				/*
				for(int i = 0; i < 5; i++) {
					particle_system_smoke.addEmitter(new Emitter_Smoke_ContinuousMed());
					((main.particles.ParticleEmitter)particle_system_smoke.getEmitter(particle_system_smoke.getEmitterCount()-1)).setPos(Util.toWorldX(ControlManager.getMouseX()), Util.toWorldY(ControlManager.getMouseY()));
				}
				for(int i = 0; i < 5; i++) {
					particle_system_fire.addEmitter(new Emitter_FireMed());
					((main.particles.ParticleEmitter)particle_system_fire.getEmitter(particle_system_fire.getEmitterCount()-1)).setPos(Util.toWorldX(ControlManager.getMouseX()), Util.toWorldY(ControlManager.getMouseY()));
				}
				*/

				sparktest.createSparksAt(new Vector2f(Util.toWorldX(ControlManager.getMouseX()), Util.toWorldY(ControlManager.getMouseY())), 100);
			}
			
			if(ControlManager.mouseButtonClicked(ControlManager.mouseSecondary)) {
				System.out.println("clicky");
				for(int i = 0; i < 5; i++) {
					particle_system_magic.addEmitter(new Emitter_Magic_BurstSmall());
					((main.particles.ParticleEmitter)particle_system_magic.getEmitter(particle_system_magic.getEmitterCount()-1)).setPos(Util.toWorldX(ControlManager.getMouseX()), Util.toWorldY(ControlManager.getMouseY()));
				}
			}
		}
		
	//	for(int i = 0; i < particle_system_magic.getEmitterCount(); i++) {
	//		((main.particles.ParticleEmitter)particle_system_magic.getEmitter(i)).setPos(ControlManager.getMouseX(), ControlManager.getMouseY());
	//	}
		
		if(GameBase.mapRendering) {
			for(ArrayList<Detail_grassblade_med> grass_group : GameBase.getMap().getTallGrass()) {
				for(Detail_grassblade_med grass_blade : grass_group)
					if(grass_blade.y <= EntityManager.getPlayer().getY()) 
						grass_blade.draw();
			}
		}

		renderEntities(g);

		if(GameBase.mapRendering) { 
			for(ArrayList<Detail_grassblade_med> grass_group : GameBase.getMap().getTallGrass()) {
				for(Detail_grassblade_med grass_blade : grass_group)
					if(grass_blade.y >= EntityManager.getPlayer().getY()) 
						grass_blade.draw();
			}
		}

		
		sparktest.update();
		particle_system_smoke.update((int)delta);
		particle_system_fire.update((int)delta);
		particle_system_magic.update((int)delta);

		sparktest.render();
		particle_system_smoke.render();
		particle_system_fire.render();
		particle_system_magic.render();
		
		

		g.translate(Camera.getAnchorX(), Camera.getAnchorY());

		Detail_fog_overlay.update();
		Detail_fog_overlay.render();

		//Stamina bar
		g.setColor(Color.white);
		g.drawRect(18, 498, 204, 14);
		g.drawRect(20, 500, 200 * EntityManager.getPlayer().getStaminaFraction(), 10);
		g.setColor(new Color(0f, 0.5f, 0f, 0.5f));
		g.fillRect(21, 501, 198 * EntityManager.getPlayer().getStaminaFraction(), 8);
		
		//Health bar
		float healthFraction = EntityManager.getPlayer().getHealth()/EntityManager.getPlayer().getTotalHealth();
		g.setColor(Color.white);
		g.drawRect(18, 528, 204, 14);
		g.drawRect(20, 530, 200 * healthFraction, 10);
		g.setColor(new Color(0.5f, 0f, 0f, 0.5f));
		g.fillRect(21, 531, 198 * healthFraction, 8);
		
		g.setColor(oldCol);
		
		if(ControlManager.keyPressed(Keyboard.KEY_F2)) {
			
			ByteBuffer buffer = BufferUtils.createByteBuffer(width * height * 4);
			GL11.glReadPixels(0, 0, width, height, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer );
			
			BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
			 
			for(int x = 0; x < width; x++)
				for(int y = 0; y < height; y++)
				{
					int i = (x + (width * y)) * 4;
					int r = buffer.get(i) & 0xFF;
					int g1 = buffer.get(i + 1) & 0xFF;
					int b = buffer.get(i + 2) & 0xFF;
					image.setRGB(x, height - (y + 1), (0xFF << 24) | (r << 16) | (g1 << 8) | b);
				}
			try {
				String date = GameBase.getDateTime().replace("/", "-").replace(":", "-").replace(" ", "_");
				ImageIO.write(image, "PNG", new File("lib/img/screenshots/"+date+".png"));
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			
			System.out.println("Saved screenshot");
		}
	}
	
	public static void renderMainMenu(Graphics g) {	}
	
	public static ParticleSystem getParticleSystemFire() {
		return particle_system_fire;
	}
	public static void renderEntities(Graphics g) {
		for(int i = 0; i < EntityManager.getTableLength(); i++) {
			Entity curEnt = EntityManager.getByIndex(i);
			if(curEnt.getImg() == null) continue;
			
			if(curEnt instanceof Entity_player) {
				curEnt = (Entity_player)curEnt;
			} else if(curEnt instanceof Entity_mobile) {
				curEnt = (Entity_mobile)curEnt;
			}
			
			float z;
			
			if(curEnt instanceof Entity_mobile) {
				z = ((Entity_mobile) curEnt).getZ();
			} else z = 0;
			
			if(debug) 
				g.draw(curEnt.getBounds());
			
			float x = curEnt.getX() - curEnt.getImg().getWidth()/2, y = curEnt.getY() - curEnt.getImg().getHeight()/2;
			
			g.setColor(new Color(0, 0, 0, 0.4f));
			g.fillOval(curEnt.getBounds().getCenterX() -15 , curEnt.getBounds().getCenterY() + curEnt.getImgOffsetY() + z + 10, 30, 8);
			
			curEnt.drawFgEffects();
			g.drawImage(curEnt.getImg(), (int)x + curEnt.getImgOffsetX(), (int)y + curEnt.getImgOffsetY());
			curEnt.drawBgEffects();
			
		}
	}
	
	
	public static void renderMap(Graphics g) {
		GameBase.getMap().render(-(int)Camera.getAnchorX(), -(int)Camera.getAnchorY());
	}
	
	
	/**
	 * Simple method to display strings.
	 * @param g2 - Graphics2D context
	 * @param text - String in question
	 * @param x - Pos in pixels
	 * @param y - Pos in pizels
	 * @param local - True means it's local to the camera, false means it's static on the map surface.
	 */
	public static void print(Graphics g, String text, float x, float y, boolean local) {
		if(local) 
			g.drawString(text, (int)Util.toScreenX(x), (int)Util.toScreenY(y));
		else
			g.drawString(text, (int)x, (int)y);
	}
	
	/**Tally up all the particles
	 * 
	 */
	public static int getParticleCount() {
		return particle_system_smoke.getParticleCount() + particle_system_fire.getParticleCount();
	}
	
	/**
	 * Simple grid drawing algorithm
	 * @param g - Graphics context
	 */
	public static void renderGrid(Graphics g) {
		
		int k=0;
		Color oldColor = g.getColor();
		g.setColor(new Color(200, 200, 200));
		int htOfRow = GameBase.getHeight() / 15;
		for (k = 0; k <= 15; k++)
			g.drawLine(0, k * htOfRow , GameBase.getWidth(), k * htOfRow );
		
		int wdOfRow = GameBase.getWidth() / 20;
		for (k = 0; k <= 20; k++) 
			g.drawLine(k*wdOfRow , 0, k*wdOfRow , GameBase.getHeight());
		
		g.setColor(oldColor);
	}
	
	public static void setDebugMode(boolean mode) {
		debug = mode;
	}
	
	/**
	 * Mutator to update boolean fade variable.
	 * @param mode - true/false to toggle if its fading in or out respectively
	 */
	public static void setFade(boolean mode) {
		fadeMode = mode;
	}
	
	/**
	 * Update overlay alpha to obscure screen
	 */
	public static void fade() {
		float fadeIncrement = ControlManager.getDelta() * 0.1f;
		if(fadeMode) {
			if(overlayCol.getAlpha() > 0 - fadeIncrement)
				overlayCol = new Color(overlayCol.getRed(), overlayCol.getGreen(), overlayCol.getBlue(), overlayCol.getAlpha() - fadeIncrement);
			else
				overlayCol = new Color(overlayCol.getRed(), overlayCol.getGreen(), overlayCol.getBlue(), 0);
				
		}
		else {
			if(overlayCol.getAlpha() < 255 + fadeIncrement)
				overlayCol = new Color(overlayCol.getRed(), overlayCol.getGreen(), overlayCol.getBlue(), overlayCol.getAlpha() + fadeIncrement);
			else
				overlayCol = new Color(overlayCol.getRed(), overlayCol.getGreen(), overlayCol.getBlue(), 255);
		}
	}
	
	public static void fadeToggle() {
		fadeMode = !fadeMode;
		fade();
		System.out.println("Fade: "+fadeMode);
	}
	
	/**
	 * Updated every tick to show helper text (will reset after each tick)
	 * @param mode - true/false to show/hide helper text respectively
	 */
	public static void showHelperText(boolean mode) {
		helperText = mode;
	}
	
	
	
	/**
	 * Set camera shaking to true (is reset to false after each tick)
	 */
	public static void shake() {
		shake = true;
	}

	public static void renderMenuOverlay(Graphics g) {
		Color oldCol = g.getColor();
		g.setColor(new Color(0.2f, 0.2f, 0.2f, 0.4f));
		g.fillRect(0, 0, GameBase.getWidth(), GameBase.getHeight());
		g.setColor(oldCol);
	}
}
