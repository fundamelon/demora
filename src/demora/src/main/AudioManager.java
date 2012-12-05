package main;

import org.lwjgl.openal.AL;
import org.newdawn.slick.openal.*;
import org.newdawn.slick.util.ResourceLoader;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.Sound;

public class AudioManager {
	public static Audio testEffect;
	
	public static void test() {
		
	}
	
	public static void main(String[] args) {
		try {
			new Sound("lib/sound/sfx/test/door_latch_open.ogg").play();
		} catch (SlickException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
