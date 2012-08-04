package main.entity;

import java.util.ArrayList;
import java.util.Vector;

import org.newdawn.slick.SlickException;

import main.entity.*;

@SuppressWarnings("all")
public class EntityManager {
	public static ArrayList<Entity> entityTable = new ArrayList<Entity>();
	public static ArrayList<Entity> tile_ents = new ArrayList<Entity>();
	
	
	public static void init() {
		try {
			addToTable(new Entity_player());
			addToTable(new Entity_umbrin_test());
		} catch (SlickException e) {
			e.printStackTrace();
		}
		
		for(Entity e : entityTable) {
			e.init();
		}
	}
	
	public static int getIndex(Entity ent) {
		return entityTable.indexOf(ent);
	}
	
	public static Entity getByIndex(int i) {
		return entityTable.get(i);
	}
	
	public static Entity getByName(String name) {
		for(int i = 0; i < entityTable.size(); i++) {
			if(entityTable.get(i).getName().equals(name))
				return entityTable.get(i);
		}
		return null;
	}
	
	public static int getTableLength() {
		return entityTable.size();
	}
	
	public static ArrayList<Entity> getByType(String entType) {
		ArrayList<Entity> outTable = new ArrayList<Entity>();
		for(int i = 0; i < entityTable.size(); i++) {
			if(entityTable.get(i).getType().equals(entType))
				outTable.add(entityTable.get(i));				
		}
		return outTable;
	}
	
	public static boolean addToTable(Entity ent) {
		if(entityTable.indexOf(ent) == -1) {
			entityTable.add(ent);
			return true;
		} else return false;
	}
	
	public static void addToTable(Vector<Entity> oldTable) {
		for(int i = 0; i < oldTable.size(); i++) {
			if(entityTable.indexOf(oldTable.get(i)) != -1)
				entityTable.add(oldTable.get(i));
		}
	}
	
	public static Entity_player getPlayer() {
		return (Entity_player)getByIndex(0);
	}
	
	
	public static void draw(String setname) {
		for(int i = 0; i < entityTable.size(); i++) {
			entityTable.get(i).draw();
		}
	}
	
	public static void update() {
		for(int i = 0; i < entityTable.size(); i++) {
			entityTable.get(i).update();
		}
	}
	
	
}
