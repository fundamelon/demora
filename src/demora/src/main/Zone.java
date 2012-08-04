package main;

import java.util.ArrayList;
import java.util.Map;
import java.util.Random;

import main.entity.Entity_detail_grassblade_med;
import main.pathfinding.NodeMap;
import org.newdawn.slick.geom.Rectangle;

import org.lwjgl.util.Point;
import org.newdawn.slick.tiled.*;

@SuppressWarnings("all")
public class Zone {
	private TiledMap currentZone;
	private Map<Point, Rectangle> collisionMap;
	private ArrayList<ArrayList<Entity_detail_grassblade_med>> grasses = new ArrayList<ArrayList<Entity_detail_grassblade_med>>();
	Rectangle[] collisionArray;
	
	public Zone(String path) {
		readFromFile(path);
		init();
	}
	
	public void readFromFile(String path) {
		try {
			currentZone = new TiledMap(path);
		} catch (Exception e) {	e.printStackTrace(); }
	}
	
	public void readFromFile() {
		readFromFile("lib/map/test.tmx");
	}
	
	public void init() {
		buildCollisionArray();
		ArrayList torchPositions;
	}
	
	public TiledMap getData() {
		return currentZone;
	}
	
	public int getWidth() {
		return currentZone.getWidth();
	}
	
	public int getHeight() {
		return currentZone.getHeight();
	}
	
	public int getWidthPixels() {
		return currentZone.getWidth() * currentZone.getTileWidth();
	}
	
	public int getHeightPixels() {
		return currentZone.getHeight() * currentZone.getTileHeight();
	}
	
	public void render(int x, int y) {
		for(int i = 0; i < currentZone.getLayerCount(); i++)
			if(!(i == currentZone.getLayerIndex("util") && !GameBase.debug_tileUtil))
				currentZone.render(x, y, i);
	}
	
	public int getTileAtX(float ox) {
		return (int)Math.floor(ox / currentZone.getTileWidth());
	}
	
	public int getTileAtY(float oy) {
		return (int)Math.floor(oy / currentZone.getTileHeight());
	}
	
	public void buildCollisionArray() {
		collisionArray = new Rectangle[getWidth() * getHeight()];
		for(int x = 0; x < getWidth(); x++) {
			for(int y = 0; y < getHeight(); y++) {
				if(blocked(x, y)) {
					collisionArray[y*getWidth() + x] = new Rectangle(x*32, y*32, 32, 32);
				//	System.out.println(y*getWidth() + x);
				}
			}
		}
	}
	
	public void createTallGrass(Rectangle bounds, int count) {
		ArrayList<Entity_detail_grassblade_med> group = new ArrayList<Entity_detail_grassblade_med>();
		grasses.add(group);
		
		Random gen = new Random();
		for(int i = 0; i < count; i++) {
			group.add(new Entity_detail_grassblade_med(0, bounds.getX() + gen.nextInt((int)bounds.getWidth()), bounds.getY() + gen.nextInt((int)bounds.getHeight())));
		}
		
		for(int i = 0; i < group.size(); i++) {
			float min = group.get(i).y;
			int minIndex = i;
			for(int c = i+1; c < group.size(); c++) {
				if(group.get(c).y < min) {
					min = group.get(c).y;
					minIndex = c;
				}
			}
			Entity_detail_grassblade_med trans = group.get(i);
			group.set(i, group.get(minIndex));
			group.set(minIndex, trans);
		}
	}
	
	public ArrayList<ArrayList<Entity_detail_grassblade_med>> getTallGrass() {
		return grasses;
	}
	
	public ArrayList<Entity_detail_grassblade_med> getTallGrassGroup(int id) {
		return grasses.get(id);
	}
	
	public Entity_detail_grassblade_med getOneTallGrass(int id, int i) {
		return grasses.get(id).get(i);
	}
	
	public int idToTileX(int id) {
		return id == 0 ? 0 : id%getWidth();
	}
	
	public int idToTileY(int id) {
		return id == 0 ? 0 : (int)(id/getHeight());
	}
	
	public Rectangle[] getCollisionArray() {
		return collisionArray;
	}
	
	public Map<Point, Rectangle> getCollisionMap() {
		return collisionMap;
	}
	
	public boolean blockedByPixel(int x, int y) {
		return 1 == collisionType(getTileAtX(x), getTileAtY(y));
	}
	
	public boolean blocked(int x, int y) {
		return 1 == collisionType(x, y);
	}
	
	public boolean blocked(int i) {
		return blocked(idToTileX(i), idToTileY(i));
	}
	
	public int collisionType(int x, int y) {
		return Integer.parseInt(currentZone.getTileProperty(getData().getTileId(x, y, currentZone.getLayerIndex("util")), "collision", "0"));
	}
	
	public int collisionType(int i) {
		return collisionType(idToTileX(i), idToTileY(i));
	}
}