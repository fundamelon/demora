package main;

import main.entity.*;
import main.map.Detail_fog_overlay;

import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.*;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import util.Lagmeter;
import util.Util;


@SuppressWarnings("all")
public class ControlManager {
	
	//declaration of characters to watch for 
	private static char KEY_MOVE_N = "W".charAt(0);
	private static char KEY_MOVE_S = "S".charAt(0);
	private static char KEY_MOVE_E  = "D".charAt(0);
	private static char KEY_MOVE_W  = "A".charAt(0);
	private static float smoothTickX = 0, smoothTickY = 0;
	private static boolean anyKeysPressed = false;
	
	static boolean[] keyStatus = new boolean[Keyboard.getKeyCount()];
	static boolean[] keyToggle = new boolean[keyStatus.length];
	static boolean[] keyPressed =  new boolean[keyStatus.length];
	static boolean[] keyReleased = new boolean[keyStatus.length];
	
	private static float[] mouseTraceX = new float[64];
	private static float[] mouseTraceY = new float[64];
	private static int traceCount;
	
	private static int mousePrevX = 0, mousePrevY = 0;
	
	public static Vector2f mouseDX;
	
	private static boolean[] mouseButtonStatus = new boolean[16];
	private static boolean[] mouseButtonClicked = new boolean[16];
	private static boolean[] mouseButtonReleased = new boolean[16];
	
	
	public static int keyToggleMapRendering = Keyboard.KEY_M;
	
	
	public static int mousePrimary = 0;
	public static int mouseSecondary = 1;
	
	public static int shake_time = 0;
	
	public static float delta;
	
	public static Entity currentEntity;
		
	
	public static void init() {
		traceCount = 0;
		
		setKeyToggle(keyToggleMapRendering);
	}
	
	/**Run through input devices and update status*/
	public static void update(float delta2) {
		ControlManager.delta = delta2;

		
		if(keyPressed(Keyboard.KEY_F)) {
		 	GameBase.setDisplayMode(800, 600, !Display.isFullscreen());
		}
		if(keyPressed(Keyboard.KEY_V)) {
		  GameBase.vsync = !GameBase.vsync;
		  Display.setVSyncEnabled(GameBase.vsync);
		}

		if(keyStatus(Keyboard.KEY_O)) {
			EntityManager.getPlayer().followPath();
		}
		if(keyStatus(Keyboard.KEY_P)) {
			int tileX = GameBase.getMap().getTileAtX(EntityManager.getPlayer().getBounds().getCenterX());
			int tileY = GameBase.getMap().getTileAtY(EntityManager.getPlayer().getBounds().getCenterY());
			main.ai.Node target = AIManager.getNodeMap().getNodeAt(
					GameBase.getMap().getTileAtX(Util.toWorldX(ControlManager.getMouseX())), 
					GameBase.getMap().getTileAtY(Util.toWorldY(ControlManager.getMouseY())));
			if(target != null && !target.isBlocked())
				EntityManager.getPlayer().setPath(target);
		}

		
		if(keyPressed(Keyboard.KEY_ESCAPE)) {
			GameBase.toggleIngameMenu();
		//	System.exit(0);
		}

		if(keyPressed(Keyboard.KEY_5)){
			//refer to GraphicsManager
		}
		
		if(keyPressed(Keyboard.KEY_6)){
			Camera.toggleShake();
		}
		
		if(keyPressed(Keyboard.KEY_7)) {
			GraphicsManager.fadeToggle();
		}
		if(keyPressed(Keyboard.KEY_8)) {
			Lagmeter.toggle();
		}
		if(keyPressed(Keyboard.KEY_9)) {
			Detail_fog_overlay.toggle();
		}
		
		updatePlayerCtrls();
		updateMouseButtons();
		updateKeys();
		
		mouseTraceX[traceCount] = Mouse.getX();
		mouseTraceY[traceCount] = Mouse.getY();
		traceCount++;
		traceCount %= mouseTraceX.length;
		
		mouseDX = new Vector2f(Mouse.getDX(), Mouse.getDY());
		
	}
	
	public static float[] getMouseTraceXArr() {
		return mouseTraceX;
	}
	public static float[] getMouseTraceYArr() {
		return mouseTraceY;
	}
	public static int getMouseTraceLength() {
		return traceCount;
	}
	
	public static float getDelta() {
		return delta;
	}

	/**
	 * Manually set mouse position
	 * @param nx - new pos x
	 * @param ny - new pos y
	 */
	
	/** @return Mouse x position */
	public static int getMouseX() {
		return Mouse.getX();
	}
	
	
	/** @return Mouse y position */
	public static int getMouseY() {
		return GameBase.getHeight() - Mouse.getY();
	}
	
	public static Vector getMouseDX() {
		return mouseDX;
	}
	
	/**
	 * Function fired regardless of keys pressed; keys are additively combined
	 */
	public static void updatePlayerCtrls() {
		Entity_player player = EntityManager.getPlayer();
		Vector2f newVel = new Vector2f(player.vel.x, player.vel.y);
		boolean anyKeysPressed = false;
		
		//Get keys pressed.
		if(Keyboard.isKeyDown(Keyboard.KEY_W)){
			newVel.y = newVel.y - 1;
			anyKeysPressed = true;
		}
		if(Keyboard.isKeyDown(Keyboard.KEY_S)){
			newVel.y = newVel.y + 1;
			anyKeysPressed = true;
		}
		if(Keyboard.isKeyDown(Keyboard.KEY_D)){
			newVel.x = newVel.x + 1;
			anyKeysPressed = true;
		}
		if(Keyboard.isKeyDown(Keyboard.KEY_A)){
			newVel.x = newVel.x - 1;
			anyKeysPressed = true;
		}
		if(Keyboard.isKeyDown(Keyboard.KEY_SPACE)){
			player.jump();
		}
		if(keyPressed(Keyboard.KEY_LSHIFT)) {
			player.startSprint();
		}
		if(keyReleased(Keyboard.KEY_LSHIFT)) {
			player.stopSprint();
		}

		//movement and movement speed controls.
		/*
		if(newVel.x == 0 && smoothTickX > 0)
			smoothTickX-=0.001 * delta;
		else if(newVel.x != 0 && smoothTickX<1)
			smoothTickX+=0.001 * delta;
		
		if(newVel.y == 0 && smoothTickY > 0) 
			smoothTickY-=0.001 * delta;
		else if(newVel.y != 0 && smoothTickY<1)
			smoothTickY+=0.001 * delta;
		
		//Reduce dx and dy by smoothing factors.
	//	newVel = new Vector2f(newVel.x * smoothTickX, newVel.y * smoothTickY);
		
		//Reset smoothing if you're at a standstill.
		if(!anyKeysPressed) {
			smoothTickX = 0;
			smoothTickY = 0;
		}
	//	DEBUG: System.out.println("Player asked to move by "+newVel.x+", "+newVel.y);
		
		if(!anyKeysPressed) {
		//	System.out.println("No keys pressed to move player.");
		}
		
		*/
		
		player.move(newVel);
	}
	
	
	
	
	public static void updateKeys() {
		for(int i = 0; i < Keyboard.getKeyCount(); i++) {
			if(Keyboard.isKeyDown(i)) {
				if(keyStatus[i]) {
					keyPressed[i] = false;
				} else {
					keyPressed[i] = true;
					
					if(GameBase.debug_keyboard) {
						System.out.println("Key "+Keyboard.getKeyName(i)+" pressed");
					}
				}
				
				if(!keyToggle[i]) {
					keyStatus[i] = true;
				} else if(Keyboard.isKeyDown(i) != keyStatus[i]) {
					keyStatus[i] = !keyStatus[i];
				}
			} else {
				keyPressed[i] = false;
				
				if(!keyStatus[i] || keyReleased[i]) {
					keyReleased[i] = false;
				} else {
					keyReleased[i] = true;
					
					if(GameBase.debug_keyboard) {
						System.out.println("Key "+Keyboard.getKeyName(i)+" released");
					}
				}
				
				if(!keyToggle[i]) {
					keyStatus[i] = false;
				}
			}
		}
	}
	
	public static void updateMouseButtons() {
		for(int i = 0; i < 16; i++) {
			if(Mouse.isButtonDown(i)) {
				if(mouseButtonStatus[i]) {
					mouseButtonClicked[i] = false;
				} else {
					mouseButtonClicked[i] = true;
					
					if(GameBase.debug_mouse){
						System.out.println("Mouse "+Mouse.getButtonName(i)+" pressed");
					}
				}
				
				mouseButtonStatus[i] = true;
			} else {
				if(!mouseButtonStatus[i] || mouseButtonReleased[i]) {
					mouseButtonReleased[i] = false;
				} else {
					mouseButtonReleased[i] = true;

					if(GameBase.debug_mouse) {
						System.out.println("Mouse "+Mouse.getButtonName(i)+" released");
					}
				}
				
				mouseButtonStatus[i] = false;
			}
		}
	}
	
	/**Status of a key on the keyboard*/
	public static boolean keyStatus(int i) {
		return keyStatus[i];
	}
	
	/**If key was just pressed*/
	public static boolean keyPressed(int i) {
		return keyPressed[i];
	}
	
	/**If key was just released*/
	public static boolean keyReleased(int i) {
		return keyReleased[i];
	}
	
	/**If the key is in toggle mode*/
	public static boolean keyToggleable(int i) {
		return keyToggle[i];
	}
	
	/**Set a key to toggle mode*/
	public static void setKeyToggle(int i) {
		keyToggle[i] = true;
	}
	
	/**Remove key toggle mode*/
	public static void resetKeyToggle(int i) {
		keyToggle[i] = false;
	}
	
	/**Status of a mouse button*/
	public static boolean mouseButtonStatus(int i) {
		return mouseButtonStatus[i];
	}
	
	/**If mouse button was just clicked*/
	public static boolean mouseButtonClicked(int i) {
		return mouseButtonClicked[i] && mouseButtonStatus[i];
	}
	
	/**If mouse button was just released*/
	public static boolean mouseButtonReleased(int i) {
		return mouseButtonReleased[i];
	}
	
	public static char getKeyN() {return KEY_MOVE_N;}
	public static char getKeyS() {return KEY_MOVE_S;}
	public static char getKeyE() {return KEY_MOVE_E;}
	public static char getKeyW() {return KEY_MOVE_W;}
}
