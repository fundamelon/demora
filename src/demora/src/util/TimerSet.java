package util;

import java.util.ArrayList;

public class TimerSet {
	private static ArrayList<Timer> timers = new ArrayList<Timer>();
	private static ArrayList<Boolean> statuses;
	
	public static void updateAll() {
		for(Timer t : timers) 
			t.update();
	}
	
	public static void addTimer(Timer t) {
		timers.add(t);
	}
	
	public static void addTimer(String name, long time) {
		addTimer(new Timer(name, time));
	}
	
	public static void addTimer(String name, long time, boolean repeat) {
		addTimer(new Timer(name, time, repeat));
	}
	
	public static Timer getTimerByName(String name) {
		for(Timer t : timers)
			if(t.getName().equals(name))
				return t;
		return null;
	}
	
	public static void stopAll() {
		statuses = new ArrayList<Boolean>(timers.size());
		for(int i = 0; i < timers.size(); i++) {
			statuses.add(i, timers.get(i).isRunning());
			timers.get(i).stop();
		}
	}
	
	public static void resumeAll() {
		for(int i = 0; i < timers.size(); i++) {
			if(statuses.get(i)) {
				timers.get(i).start();
			}
		}
	}
	
	public static void destroyAll() {
		timers = new ArrayList<Timer>();
	}
}