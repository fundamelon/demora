package util;

public class Timer {
	private String name = null;
	private long startTime = 0;
	private long curTime = 0;
	private long limit = 0;
	private boolean running = true;
	private boolean repeat = false;
	public Timer(String name) {
		this.name = name;
		startTime = System.nanoTime();
	}
	
	public Timer(String name, long time) {
		this(name);
		this.limit = time;
	}
	
	public Timer(String name, long time, boolean repeat) {
		this(name, time);
		this.repeat = repeat;
	}
	
	public void update() {
		if(running) curTime = System.nanoTime();
	}	
	
	public void start() {
		running = true;
	}
	
	public void restart() {
		reset();
		start();
	}
	
	public void stop() {
		running = false;
	}
	
	public void reset() {
		startTime = curTime;
	}
	
	public boolean isRunning() {
		return running;
	}
	
	public void setRepeat(boolean a) {
		repeat = a;
	}
	
	public long timeElapsed() {
		return curTime - startTime;
	}
	
	public String getName() {
		return name;
	}
	
	public boolean completed() {
		if(!repeat) {
			running = false;
			return curTime >= startTime + limit;
		} else {
			reset();
			return true;
		}
	}
}