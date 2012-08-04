package main.pathfinding;

import java.util.ArrayList;

import org.newdawn.slick.Color;
import org.newdawn.slick.Font;
import org.newdawn.slick.TrueTypeFont;

import main.AIManager;
import main.GameBase;

public class Path {
	public ArrayList<Node> steps = new ArrayList<Node>(1);
	
	public Path(Node[] nodeArr) {
		for(Node n : nodeArr)
			steps.add(n);
	}
	
	public Path() {
		
	}
	
	public void addStep(Node newStep) {
		steps.add(newStep);
	}
	
	public void removeStep(Node targetStep) {
		steps.remove(targetStep);
	}
	
	public Node getFirst() {
		return steps.get(0);
	}
	
	public void setNodeVisited(Node n) {
		n.setVisited(true);
	}
	
	public Node getNext() {
		for(Node n : steps) {
			if(!n.isVisited()) return n;
		}
		return null;
	}
	
	public Node getLast() {
		return steps.get(steps.size()-1);
	}
	
	public Node[] getNodesArr() {
		return (Node[]) steps.toArray();
	}
	
	public ArrayList<Node> getNodes() {
		return steps;
	}
	
	public void setNodes(ArrayList<Node> in) {
		steps = in;
	}
	
	public void render(org.newdawn.slick.Graphics g) {
		int interval = AIManager.getNodeMap().getInterval();
		//if(AIManager.reporting)
		//	System.out.println("AI: Rendering path of length "+steps.size());
		for(int i = 1; i < steps.size(); i++) {
			
			g.drawLine(	
				steps.get(i).getX()*interval + 16, 
				steps.get(i).getY()*interval + 16, 
				steps.get(i-1).getX()*interval + 16, 
				steps.get(i-1).getY()*interval + 16
			);
			
			if(i == steps.size()-1) {
				Color oldColor = g.getColor();
				g.setColor(Color.red);
				g.fillRect(
					steps.get(i).getX()*interval + 14, 
					steps.get(i).getY()*interval + 14,  
					5, 5
				);
				g.setColor(oldColor);
			}else if(i == 1) {
				Color oldColor = g.getColor();
				g.setColor(Color.green);
				g.fillRect(
					steps.get(i-1).getX()*interval + 14, 
					steps.get(i-1).getY()*interval + 14,  
					5, 5
				);
				g.setColor(oldColor);
			}
			g.drawString(""+i, steps.get(i).getX()*32, steps.get(i).getY()*32);
		}
	}

	public Path getReversedCopy() {
		Path temp = new Path();
		temp.setNodes(steps);
		java.util.Collections.reverse(temp.getNodes());
		return temp;
	}

	public Node nextStep() {
		for(Node n : steps) {
			if(!n.isVisited()) return n;
		}
		return null;
	}
}
