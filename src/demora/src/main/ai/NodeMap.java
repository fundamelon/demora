package main.ai;

import java.util.ArrayList;

import main.GameBase;

import org.lwjgl.util.vector.Vector2f;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.geom.Circle;
import org.newdawn.slick.geom.Rectangle;

public class NodeMap implements Cloneable {

	private int width, height, interval;
	private Node[][] map;
	
	public Object clone() {
		try {
			return super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public NodeMap(int interval, int width, int height) {
		this.width = width;
		this.height = height;
		this.interval = interval;
		generate();
	}
	
	public void generate() {
		if(width == 0 || height == 0 || interval == 0) {
			System.out.println("Invalid arguments");
			return;
		}
		
		map = new Node[width][height];
		
		for(int x = 0; x < width; x++) {
			for(int y = 0; y < height; y++) {
				map[x][y] = new Node(x, y, false);
			}
		}
		
		for(int x = 0; x < Math.min(width, GameBase.getMap().getWidth()); x++) {
			for(int y = 0; y < Math.min(height, GameBase.getMap().getHeight()); y++) {
				if(GameBase.getMap().blockedAtPixel(x * (interval)+(interval/2), y * (interval)+(interval/2))) {
					if(x < width && y < height) {
						map[x][y].setBlocked(true);
					}
					
//					if(x+2 < width && y+2 < height) {
//						map[x+2][y+1].setBlocked(true);
//						map[x+2][y+2].setBlocked(true);
//						map[x+1][y+2].setBlocked(true);
//					}
				}
			}
		}
	}
	
	public void render(Graphics g) {
		for(int x = 0; x < width; x++) {
			for(int y = 0; y < height; y++) {
				//Only render if visible
				if(!main.Camera.getVisibleArea().contains((x) * interval + 16, (y) * interval + 16)) continue;
				
				Color oldColor = g.getColor();
				g.setColor(map[x][y].isBlocked() ? Color.red : Color.white);
				g.draw(new Rectangle((x) * interval + 16, (y) * interval + 16, 3, 3));
				g.setColor(oldColor);
			}
		}
	}
	
	public int getWidth() {return width;}
	public int getHeight() {return height;}
	public int getInterval() {return interval;}
	public int getSize() {return width*height;}
	
	public Node getNodeAt(int x, int y) {
		if(x < width && x >= 0 && y < width && y >= 0)
			return map[x][y];
		else return null;
	}
	
	public ArrayList<Node> getNeighbors(int x, int y) {
		return getNeighbors(getNodeAt(x, y)); 
	}
	
	public ArrayList<Node> getNeighbors(Node n) {
		ArrayList<Node> neighbors = new ArrayList<Node>(8);
		neighbors.add(getNodeAt(n.getX(),   n.getY()+1));
		neighbors.add(getNodeAt(n.getX()+1, n.getY()+1));
		neighbors.add(getNodeAt(n.getX()+1, n.getY()));
		neighbors.add(getNodeAt(n.getX()+1, n.getY()-1));
		neighbors.add(getNodeAt(n.getX(),   n.getY()-1));
		neighbors.add(getNodeAt(n.getX()-1, n.getY()-1));
		neighbors.add(getNodeAt(n.getX()-1, n.getY()));
		neighbors.add(getNodeAt(n.getX()-1, n.getY()+1));
		return neighbors;
	}
	
	public Node getNodeByID(int id) {
		return map[(id - id%width)/width][id%width];
	}
	
	public Node[][] toArray() {
		return map;
	}
	
	public void setNode(Node newNode) {
		map[newNode.getX()][newNode.getY()] = newNode;
	}
	
	public void setNode(int x, int y, boolean block) {
		setNode(new Node(x, y, block));
	}

	public Node getNodeAt(Vector2f pos) {
		return getNodeAt((int)pos.x, (int)pos.y);
	}
}
