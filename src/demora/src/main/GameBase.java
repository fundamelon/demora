package main;

import main.entity.*;
import main.gui.*;
import main.map.*;
import util.*;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.Sys;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.*;
import org.lwjgl.util.glu.GLU;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.EXTFramebufferObject.*;

import org.newdawn.slick.*;
import org.newdawn.slick.opengl.*;
import org.newdawn.slick.openal.*;

import util.Lagmeter;

import Dario.*;

@SuppressWarnings("all")
public class GameBase {
	

	private static Tilemap currentMap;

	/** Current game render mode.
	 * 0: menu,
	 * 1: world,
	 * 2: console
	 */
	public static int viewMode = 0;
	
	public static final int VIEW_MENU = 0;
	public static final int VIEW_WORLD = 1;
	public static final int VIEW_CONS = 2;
	
	//Debug switches, toggle console reporting and/or display modes.
	public static boolean debug_keyboard = 	false;
	public static boolean debug_mouse = 	false;
	public static boolean debug_graphics = 	true;
	public static boolean debug_menu = 		false;
	public static boolean debug_tileUtil = 	false;
	
	private static int colorTextureID;
	private static int framebufferID;
	private static int depthRenderBufferID;
	
	
	/** time at last frame */
	static long lastFrame;
	
	/** frames per second */
	static int fps;
	/** last fps */
	static long lastFPS;
	/** current fps */
	static int thisFPS;
	
	static util.Console console;
	
	static boolean vsync;
	
	public static boolean menuVisible;
	public static boolean paused = true;
	
	private static boolean disableFBO = true;
	

	//debug option for easy map toggle
	public static boolean mapRendering = true;
	
	public static void main(String[] args) {
		GameBase.start();
	}
	
	@SuppressWarnings("all")
	public static void start() {
		System.out.println("//--------------------");
		try {
			Display.setDisplayMode(new DisplayMode(800, 600));
			Display.create();
		} catch (LWJGLException e) {
			e.printStackTrace();
			System.exit(0);
		}

		
		System.out.println(glGetString(GL_VENDOR));
		System.out.println(glGetString(GL11.GL_RENDERER));
		System.out.println("OpenGL "+glGetString(GL_VERSION));
		System.out.println("//--------------------");
		
		Graphics g = new Graphics();
		
		//Temporary stub to load map
		loadMap(new Tilemap("lib/map/pf_testbed.tmx"));
		System.out.println("Current map: "+getMap().getFilepath());
		AIManager.generateNodeMap(32, 256, 256);
		
		GraphicsManager.init();
		ControlManager.init();
		EntityManager.init();
		AIManager.init();
		GUIManager.init();
		
		g.setFont(new TrueTypeFont(new java.awt.Font("Helvetica", 10, 10), true));

		initGL();
		
		Lagmeter.show();
		
		GraphicsManager.setDebugMode(debug_graphics);
		
		
		
		getDelta(); // call once before loop to initialise lastFrame
		lastFPS = getTime(); // call before loop to initialise fps timer

		while (!Display.isCloseRequested()) {
			float delta = getDelta();

			Display.setTitle("Demora (Testbed) FPS: " + thisFPS + "  Particles: "+GraphicsManager.getParticleCount());
			
			update(delta);
			render(g, delta);

			Display.update();
			g.clear();
			Display.sync(60); // cap fps
		}

		Display.destroy();
	}
	
	public static void update(float delta) {
		
		//Keyboard Event Handlers!!
		//TODO: Move to ControlManager
		
		while (Keyboard.next()) {
		    if (Keyboard.getEventKeyState()) {
		        if (Keyboard.getEventKey() == Keyboard.KEY_F) {
		        	setDisplayMode(800, 600, !Display.isFullscreen());
		        }
		        else if (Keyboard.getEventKey() == Keyboard.KEY_V) {
		        	vsync = !vsync;
		        	Display.setVSyncEnabled(vsync);
		        }
		    }
		}
		
		if(Keyboard.isKeyDown(Keyboard.KEY_ESCAPE))
			System.exit(0);

		ControlManager.update(delta);
		TransitionManager.updateAll();
		
		if(viewMode == VIEW_MENU) {
			GUIManager.update();
		} else if(viewMode == VIEW_WORLD) {
			EntityManager.update();
		} 
		
		updateFPS(); // update FPS Counter
	}

	public static void render(Graphics g, float delta) {	
		if(!disableFBO && GLContext.getCapabilities().GL_EXT_framebuffer_object) {
			glViewport(0, 0, Display.getWidth(), Display.getHeight());
			glBindTexture(GL_TEXTURE_2D, 0);
			glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, framebufferID);
		}
			
		glClearColor (0.0f, 0.0f, 0.0f, 0.1f);
		glClear (GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
		
		switch(viewMode) {
		case VIEW_MENU:
			menuVisible = true;
			break;
			
		case VIEW_WORLD:
	        g.setDrawMode(Graphics.MODE_NORMAL);
			GraphicsManager.renderGame(g, delta);
			break;
			
		case VIEW_CONS:
			break;
			
		}
		
		if(menuVisible) {
			GUIManager.render(g, delta);
		}
		
		Lagmeter.update();
		
		if(!disableFBO && GLContext.getCapabilities().GL_EXT_framebuffer_object) {
			glEnable(GL_TEXTURE_2D);
			glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, 0);
			glClearColor (0.0f, 1.0f, 0.0f, 0.5f);
			glClear (GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
			glBindTexture(GL_TEXTURE_2D, colorTextureID); 
			
			glDisable(GL_TEXTURE_2D);
			glFlush();
		}
		
	}
	
	public static void setDisplayMode(int width, int height, boolean fullscreen) {

	    // return if requested DisplayMode is already set
	    if ((Display.getDisplayMode().getWidth() == width) && 
	        (Display.getDisplayMode().getHeight() == height) && 
		(Display.isFullscreen() == fullscreen)) {
		    return;
	    }

	    try {
	        DisplayMode targetDisplayMode = null;
			
		if (fullscreen) {
		    DisplayMode[] modes = Display.getAvailableDisplayModes();
		    int freq = 0;
					
		    for (int i=0;i<modes.length;i++) {
		        DisplayMode current = modes[i];
						
			if ((current.getWidth() == width) && (current.getHeight() == height)) {
			    if ((targetDisplayMode == null) || (current.getFrequency() >= freq)) {
			        if ((targetDisplayMode == null) || (current.getBitsPerPixel() > targetDisplayMode.getBitsPerPixel())) {
				    targetDisplayMode = current;
				    freq = targetDisplayMode.getFrequency();
	                        }
	                    }

			    // if we've found a match for bpp and frequence against the 
			    // original display mode then it's probably best to go for this one
			    // since it's most likely compatible with the monitor
			    if ((current.getBitsPerPixel() == Display.getDesktopDisplayMode().getBitsPerPixel()) &&
	                        (current.getFrequency() == Display.getDesktopDisplayMode().getFrequency())) {
	                            targetDisplayMode = current;
	                            break;
	                    }
	                }
	            }
	        } else {
	            targetDisplayMode = new DisplayMode(width,height);
	        }

	        if (targetDisplayMode == null) {
	            System.out.println("Failed to find value mode: "+width+"x"+height+" fs="+fullscreen);
	            return;
	        }

	        Display.setDisplayMode(targetDisplayMode);
	        Display.setFullscreen(fullscreen);
	        
				
	    } catch (LWJGLException e) {
	        System.out.println("Unable to setup mode "+width+"x"+height+" fullscreen="+fullscreen + e);
	    }
	}
	
	/** 
	 * Calculate how many milliseconds have passed 
	 * since last frame.
	 * 
	 * @return milliseconds passed since last frame 
	 */
	public static float getDelta() {
	    long time = System.nanoTime();
	    long delta = (time - lastFrame);
	    lastFrame = time;
	    return (delta/1000000f);
	}

	/**
	 * Open a simple console
	 */
	public static void openConsole() {
		if(console == null) {
			try {
				util.Console console = new util.Console();
			}catch (Exception e) {}
		}
	}
	
	/**
	 * Get the accurate system time
	 * 
	 * @return The system time in milliseconds
	 */
	public static long getTime() {
	    return (Sys.getTime() * 1000) / Sys.getTimerResolution();
	}
	
	public static int getFPS() {
		return thisFPS;
	}
	
	/**
	 * Calculate the FPS and set it in the title bar
	 */
	public static void updateFPS() {
		if (getTime() - lastFPS > 1000) {
			thisFPS = fps;
			fps = 0;
			lastFPS += 1000;
		}
		fps++;
	}
	
	public static void initGL() {
		glMatrixMode(GL11.GL_PROJECTION);
		glLoadIdentity();
		glOrtho(0, 800, 600, 0, 1, -1);
		glMatrixMode(GL11.GL_MODELVIEW);
		
		
		//Fix transparent pixels being black
		glEnable(GL11.GL_BLEND);
		glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ZERO);
		
		if (GLContext.getCapabilities().GL_ARB_vertex_buffer_object) System.out.println("VBO available :D");
		
		//override, disabled for now
		if(disableFBO && !GLContext.getCapabilities().GL_EXT_framebuffer_object) {
		//	System.out.println("\nFBO not supported :C");
		} else {
			System.out.println("FBO supported :D");
			framebufferID = glGenFramebuffersEXT();
			colorTextureID = glGenTextures();
			depthRenderBufferID = glGenRenderbuffersEXT();
			
			glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, framebufferID);
			glBindTexture(GL_TEXTURE_2D, colorTextureID);
			glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR); 
			
			glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, 512, 512, 0,GL_RGBA, GL_INT, (java.nio.ByteBuffer) null);
			glFramebufferTexture2DEXT(GL_FRAMEBUFFER_EXT,GL_COLOR_ATTACHMENT0_EXT,GL_TEXTURE_2D, colorTextureID, 0); 
			
			glBindRenderbufferEXT(GL_RENDERBUFFER_EXT, depthRenderBufferID); 
			glRenderbufferStorageEXT(GL_RENDERBUFFER_EXT, GL14.GL_DEPTH_COMPONENT24, 512, 512);
			glFramebufferRenderbufferEXT(GL_FRAMEBUFFER_EXT,GL_DEPTH_ATTACHMENT_EXT,GL_RENDERBUFFER_EXT, depthRenderBufferID);
			
			glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, 0);
		}
	}
	
	public static int getWidth() {
		return Display.getWidth();
	}
	
	public static int getHeight() {
		return Display.getHeight();
	}
	
	public static void loadMap(Tilemap newMap) {
		currentMap = newMap;
		currentMap.init();
	}
	
	public static int createVBOID() {
		  if (GLContext.getCapabilities().GL_ARB_vertex_buffer_object) {
		    IntBuffer buffer = BufferUtils.createIntBuffer(1);
		    ARBVertexBufferObject.glGenBuffersARB(buffer);
		    return buffer.get(0);
		  }
		  return 0;
	}
	
	public static void bufferData(int id, FloatBuffer buffer) {
		if (GLContext.getCapabilities().GL_ARB_vertex_buffer_object) {
		    ARBVertexBufferObject.glBindBufferARB(ARBVertexBufferObject.GL_ARRAY_BUFFER_ARB, id);
		    ARBVertexBufferObject.glBufferDataARB(ARBVertexBufferObject.GL_ARRAY_BUFFER_ARB, buffer, ARBVertexBufferObject.GL_STATIC_DRAW_ARB);
		  }
	}
	
	public static void bufferElementData(int id, IntBuffer buffer) {
	  if (GLContext.getCapabilities().GL_ARB_vertex_buffer_object) {
		    ARBVertexBufferObject.glBindBufferARB(ARBVertexBufferObject.GL_ELEMENT_ARRAY_BUFFER_ARB, id);
		    ARBVertexBufferObject.glBufferDataARB(ARBVertexBufferObject.GL_ELEMENT_ARRAY_BUFFER_ARB, buffer, ARBVertexBufferObject.GL_STATIC_DRAW_ARB);
		  }
	}
	
	public static Tilemap getMap() {
		return currentMap;
	}
	
	public static String getDateTime() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        return dateFormat.format(date);
    }

	public static void quit() {
		Display.destroy();
	}
}