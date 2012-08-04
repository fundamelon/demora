package main.pathfinding;

import java.util.ArrayList;
import main.AIManager;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;

public class Pathfinder_AStar {
	private Path output;
	private NodeMap nodes = AIManager.getNodeMap();
	
	private Node start;
	private Node goal;
	
	private ArrayList<Node> open = new ArrayList<Node>();
	private ArrayList<Node> closed = new ArrayList<Node>();
	private ArrayList<Node> visited = new ArrayList<Node>();
	
	private int length;
	
	public Pathfinder_AStar(NodeMap initialSet) {
		output = new Path();
		nodes = initialSet;
	}
	
	public void pathfind(Node nstart, Node ngoal) {
		goal = ngoal;
		start = nstart;
		if(start.isBlocked()) {
			if(AIManager.reporting) 
				System.out.println("PF: Invalid start position, aborting");
			return;
		}
		start.setParent(null);
		
		nodes = AIManager.getNodeMap();
		
		open.clear();
		closed.clear();
		visited.clear();
		length = 0;
		
		open.add(start);
		goal.setParent(null);
		
		int maxDepth = 1000;
		Node current = start, candidate = null;
		
		Graphics g = new org.newdawn.slick.Graphics();
		
	//	System.out.println("AI: Pathfinding from "+start.toString()+" to "+goal.toString()+", max steps: "+maxDepth);
		
		while(length <= maxDepth && !goal.equals(current)) {
			int x = current.getX(), y = current.getY();
			if(AIManager.reporting)
				System.out.println("\tPF: Analyzing... ("+length+") "+nodes.getNodeAt(x, y));
			
			for(int ny = -1; ny <= 1; ny++) {
				for(int nx = -1; nx <= 1; nx++) {
					if(nx == 0 && ny == 0) continue;
					
					//Disable diagonal movement
				//	if((nx != 0) && (ny != 0)) continue;
					
					int cx = x + nx, cy = y + ny;
					
					//Skip if it's going to cut a corner
					if(!isValidLocation(cx - nx, cy) || !isValidLocation(cx, cy-ny)) continue;
					
					//If it's not blocked
					if(isValidLocation(cx, cy)) {
						Node cn = nodes.getNodeAt(cx, cy);
						
						//If it's not already a part of the path and hasn't been visited 
						if(	!closed.contains(cn) && !open.contains(cn)) {
							open.add(cn);
							
							g.setColor(new Color(0f, 1f, 0f, (150-length)/150f-0.8f));
							g.drawRect(current.getX()*32+12, current.getY()*32+12, 8, 8);
							
							cn.setParent(nodes.getNodeAt(x, y));
							g.setColor(new Color(100, 100, 100, 100));
							g.drawLine(cx*32+16, cy*32+16, x*32+16, y*32+16);
							
							if(AIManager.reporting)
								System.out.println("\t\t\tNODE: Added "+cn.toString());
							
						} else if(AIManager.reporting) {
							System.out.print("\t\t\tNODE: Rejected "+cn.toString()+": ");
							if(!closed.contains(cn)) {
								System.out.print("closed; ");
							}
							if(!open.contains(cn)) {
								System.out.print("open; ");
							}
							if(!visited.contains(cn)) {
								System.out.print("visited; ");
							}
							if(cn.getParent() == null) {
								System.out.print("parented; ");
							}
							System.out.print("\n");
						}
					}
				}
			}
			
			candidate = getClosest(current, goal, open);
			
			if(AIManager.reporting)
				System.out.println("\t\tHEU: Choosing "+candidate.toString());
			
			
			current = candidate;
			closed.add(current);
			g.setColor(new Color(1f, 1f, 1f, 0.2f));
			g.fillRect(current.getX()*32+12, current.getY()*32+12, 8, 8);
			open.remove(current);
			length++;
		}
		
		if(goal.getParent() == null) {
			if(AIManager.reporting)
				System.out.println("\tPF: Pathfinding failed");
		} else {
			if(AIManager.reporting)
				System.out.println("\tPF: Pathfinding successful");
		}
		
		for(int i = 0; i < nodes.getSize(); i++)
			nodes.getNodeByID(i).setVisited(false);
	}
	
	/**Does a node exist at this location, and is it clear?
	 * 
	 * @param x
	 * @param y
	 * @return true if node is a valid location
	 */
	public boolean isValidLocation(int x, int y) {
		
		return (
			x < nodes.getWidth() && x >= 0 && 
			y < nodes.getHeight() && y >= 0 && 
			!nodes.getNodeAt(x, y).isBlocked()
		);
	}
	
	public boolean isAdjacent(Node a, Node b) {
		return (
			Math.abs(a.getX() - b.getX()) <= 1 &&
			Math.abs(a.getY() - b.getY()) <= 1
		);
	}
	
	public float getDistance(Node origin, Node goal) {
		float ox = origin.getX();
		float oy = origin.getY();
		float gx = goal.getX();
		float gy = goal.getY();
	
		return (float)Math.sqrt((gx-ox)*(gx-ox) + (gy-oy)*(gy-oy));
	}
	
	public Node getClosest(Node start, Node goal, ArrayList<Node> set) {
		Node closest = set.get(0);
		for(int i = 1; i < set.size(); i++) {
			float distanceFromClosest = getDistance(closest, goal);
			float distanceFromCurrent = getDistance(set.get(i), goal);
			if((distanceFromCurrent <= distanceFromClosest) && isAdjacent(set.get(i), closest)) {
				
				if(AIManager.reporting)
					System.out.println("\t\tHEU: Swap ["+closest.toString()+", distance: "+distanceFromClosest+"] \tto ["+set.get(i).toString()+", distance: "+distanceFromCurrent+"]");
				
				closest = set.get(i);
			}
		}
		return closest;
	}
	
	public Path createPath() {
		output = new Path();
		int maxLength = 1000, length = 0;
		if(goal != null) {
			Node current = goal;
			while(current.getParent() != null) {
				if(length > maxLength) {
					if(AIManager.reporting)
						System.out.println("AI: [WARNING] Path creator overflow");
					break;
				}
				length++;
				output.addStep(current);
				current = current.getParent();
			}
		}
		return output.getReversedCopy();
	}
}
