package main.map;

import java.util.ArrayList;
import java.util.Map;
import java.util.Random;

import main.GameBase;
import main.ai.NodeMap;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Shape;

import org.lwjgl.util.Point;
import org.lwjgl.util.vector.Vector;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

@SuppressWarnings("all")
public class Tilemap {
	private TiledMap mapData;
	private Map<Point, Rectangle> collisionMap;
	private ArrayList<ArrayList<Detail_grassblade_med>> grasses = new ArrayList<ArrayList<Detail_grassblade_med>>();
	private ArrayList<Detail_grassblade_med> indies = new ArrayList<Detail_grassblade_med>();
	Rectangle[] collisionArray;
	private String path;
	
	public Tilemap(String newPath) {
		readFromFile(newPath);
		init();
	}
	
	public void readFromFile(String newPath) {
		path = newPath;
		try {
			mapData = new TiledMap(path);
		} catch (Exception e) {	e.printStackTrace(); }
	}
	
	public void readFromFile() {
		readFromFile("lib/map/test.tmx");
	}
	
	public void init() {
		buildCollisionArray();
		grasses.add(indies);
	}
	
	public TiledMap getData() {
		return mapData;
	}
	
	public int getWidth() {
		return mapData.getWidth();
	}
	
	public int getHeight() {
		return mapData.getHeight();
	}
	
	public int getWidthPixels() {
		return mapData.getWidth() * mapData.getTileWidth();
	}
	
	public int getHeightPixels() {
		return mapData.getHeight() * mapData.getTileHeight();
	}
	
	public void render(int x, int y) {
		for(int i = 0; i < mapData.getLayerCount(); i++)
			if(!(i == mapData.getLayerIndex("util") && !GameBase.debug_tileUtil))
				mapData.render(x, y, i);
	}
	
	public int getTileAtX(float ox) {
		return (int)Math.floor(ox / mapData.getTileWidth());
	}
	
	public int getTileAtY(float oy) {
		return (int)Math.floor(oy / mapData.getTileHeight());
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
	//	System.out.println(collisionMap.keySet().toString());
	}
	
	public ArrayList<Shape> getNearbyObstacles(int x, int y) {
		ArrayList<Shape> tempArray = new ArrayList<Shape>();
	//	tempArray.add(collisionMap.get(new Point(x, y)));
		for(int r = 2; r > -1; r--) {
			for(int c = 1; c > -2; c--) {
				if(true) {
					if(blocked(x+c, y+r)) {
						tempArray.add(new Rectangle((x+c) * 32, (y+r) * 32, 32, 32));
					} else {
						tempArray.add(null);
					}
				}
			}
		}
		return tempArray;
	}
	
	public ArrayList<Shape> getNearbyObstacles(Shape s) {
		ArrayList<Shape> tempArray = new ArrayList<Shape>();
		
		return tempArray;
	}

	public ArrayList<Shape> getNearbyObstacles(float x, float y) {
		return getNearbyObstacles((int)x, (int)y);
	}
	
	public void getCollisionNeighbors(int x, int y) {
		int i = y*getWidth() + x;
		System.out.println(collisionMap.keySet().toString());
	}
	
	//TODO: Broken, add support for map polygon regions
	public boolean isWithinRegion(String name) {
		int gid = mapData.getObjectGroupID(name);
		for(int i = 0; i < mapData.getObjectCount(gid); i++) {
			System.out.println(mapData.getObjectType(gid, i));
		}
		return false;
	}
	
	public void createTallGrass(int type, Rectangle bounds, int count) {
		ArrayList<Detail_grassblade_med> group = new ArrayList<Detail_grassblade_med>();
		grasses.add(group);
		
		Random gen = new Random();
		for(int i = 0; i < count; i++) {
			group.add(new Detail_grassblade_med(type, bounds.getX() + gen.nextInt((int)bounds.getWidth()), bounds.getY() + gen.nextInt((int)bounds.getHeight())));
		}
		
		group = sortTallGrass(group);
	}
	
	public ArrayList<Detail_grassblade_med> sortTallGrass(ArrayList<Detail_grassblade_med> group) {

		for(int i = 0; i < group.size(); i++) {
			float min = group.get(i).y;
			int minIndex = i;
			for(int c = i+1; c < group.size(); c++) {
				if(group.get(c).y < min) {
					min = group.get(c).y;
					minIndex = c;
				}
			}
			Detail_grassblade_med trans = group.get(i);
			group.set(i, group.get(minIndex));
			group.set(minIndex, trans);
		}
		
		return group;
	}
	
	public ArrayList<ArrayList<Detail_grassblade_med>> getTallGrass() {
		return grasses;
	}
	
	public ArrayList<Detail_grassblade_med> getTallGrassGroup(int id) {
		return grasses.get(id);
	}
	
	public Detail_grassblade_med getOneTallGrass(int id, int i) {
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
	
	public String getFilepath() {
		return path;
	}
	
	public Map<Point, Rectangle> getCollisionMap() {
		return collisionMap;
	}
	
	public boolean blockedAtPixel(float x, float y) {
		return 1 == collisionType(getTileAtX(x), getTileAtY(y));
	}
	
	public boolean blocked(int x, int y) {
		if(x >= 0 && y >= 0 && x < mapData.width && y < mapData.height)
			return 1 == collisionType(x, y);
		else return true;
	}
	
	public boolean blockedAtTile(Vector pos) {
		Vector2f tpos = (Vector2f)pos;
		return blocked((int)tpos.x, (int)tpos.y);
	}
	
	public boolean blockedAtTile(float x, float y) {
		return blockedAtTile(new Vector2f(x, y));
	}
	
	public boolean blocked(Vector pos) {
		Vector2f tpos = (Vector2f)pos;
		return blockedAtPixel(tpos.x, tpos.y);
	}
	
	public boolean blocked(int i) {
		return blocked(idToTileX(i), idToTileY(i));
	}
	
	public int collisionType(int x, int y) {
		return Integer.parseInt(mapData.getTileProperty(getData().getTileId(x, y, mapData.getLayerIndex("util")), "collision", "0"));
	}
	
	public int collisionType(int i) {
		return collisionType(idToTileX(i), idToTileY(i));
	}

	public void createTallGrass(int type, int x, int y) {
		indies.add(new Detail_grassblade_med(type, x, y));
		indies = sortTallGrass(indies);
	}

	public Vector2f getTileAt(Vector pos) {
		Vector2f tpos = (Vector2f)pos;
		return new Vector2f(getTileAtX(tpos.x), getTileAtY(tpos.y));
	}

	public Vector2f getTileAt(float x, float y) {
		return getTileAt(new Vector2f(x, y));
	}

}