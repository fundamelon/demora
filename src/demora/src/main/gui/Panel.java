package main.gui;

import java.util.ArrayList;

public class Panel {
	public static final int PRESET_MAIN = 0;
	public static final int PRESET_OPTIONS = 1;
	private int preset;
	
	private ArrayList<Clickable> items = new ArrayList<Clickable>();
	
	public Panel(int preset) {
		switch(preset)
		{
		case PRESET_MAIN:
			items.add(	
				new Button(	
					items.size(), 
					"START GAME",
					GUIManager.screenMidX - 100, 
					GUIManager.screenMidY - 25, 
					200, 
					50,
					Event.GAME_START
				));
			
			items.add(
				new Button(
					items.size(),
					"OPTIONS",
					GUIManager.screenMidX - 100,
					GUIManager.screenMidY + 50,
					200,
					50,
					Event.OPEN_OPTIONS
			));
			
			items.add(	
				new Button(	
					items.size(), 
					"QUIT",
					GUIManager.screenMidX - 100, 
					GUIManager.screenMidY + 125, 
					200, 
					50,
					Event.GAME_QUIT
			));
			
			break;
		}
		this.preset = preset;
	}
	
	public void update() {
		for(Clickable e : items)
			e.update();
	}
	
	public ArrayList<Clickable> getItems() {
		return items;
	}
	
	public Clickable getItem(int id) {
		for(Clickable e : items)
			if(e.getID() == id)
				return e;
		
		return null;
	}

	public int getPreset() {
		return preset;
	}
}
