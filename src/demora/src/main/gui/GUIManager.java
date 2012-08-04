package main.gui;

import java.util.ArrayList;
import main.*;


import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;

public class GUIManager {
	private static ArrayList<Panel> panels = new ArrayList<Panel>();
	public static int screenMidX = (int)(GameBase.getWidth() / 2);
	public static int screenMidY = (int)(GameBase.getHeight() / 2);
	
	public static int curPanel = 0;
	
	public static void init() {
		panels.add(new Panel(Panel.PRESET_MAIN));
	}
	
	public static void update() {
		for(Panel p : panels)
			p.update();
	}

	public static void render(Graphics g, float delta) {
		for(Panel p : panels) {
			for(int i = 0; i < p.getItems().size(); i++) {
				Clickable item = p.getItem(i);
				if(item.isVisible()) {
					if(main.GameBase.debug_menu) {
						if(item.mouseClick()) {
							System.out.println(item.getName() + ": clicked");
						}
						
						if(item.mouseExit()) {
							System.out.println(item.getName() + ": exited");
						} else if(item.mouseEnter()) {
							System.out.println(item.getName() + ": entered");
						}
					}
					
					
					
					Color oldColor = g.getColor();
					if(item.mouseDown()) {
						g.setColor(Color.darkGray);
						Event.fire(Event.GAME_START);
					} else if(item.mouseHover()) {
						g.setColor(Color.gray);
					} else {
						g.setColor(Color.white);
					}
					g.fill(item.getBounds());
					g.setColor(oldColor);
				}
			}
		}
	}
}
