package util;

import java.util.ArrayList;

public class TransitionManager {
	public static ArrayList<Transition> transitionTable = new ArrayList<Transition>();
	
	public static void addToTable(Transition e) {
		transitionTable.add(e);
	}
	
	public static ArrayList<Transition> getTable() {
		return transitionTable;
	}
	
	public static void updateAll() {
		for(Transition e : transitionTable)
			e.update(main.ControlManager.getDelta());
	}
}
