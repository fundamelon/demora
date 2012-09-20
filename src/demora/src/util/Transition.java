package util;

public class Transition {
	float a, b, t; //start, end, duration
	float curVal;
	float curTime, startTime;
	String curveType = "linear";
	boolean paused = true;
	boolean reverse = false;
	
	String[] validCurveTypes = new String[]{
		"linear", "single", "double", "hold"
	};
	
	/**
	 * Create a new transition.
	 * @param a origin
	 * @param b target
	 * @param t time
	 */
	public Transition(float a, float b, float t) {
		if(a < b) {
			this.a = a;
			this.b = b;
		} else {
			this.a = b;
			this.b = a;
		}
		this.t = t;
		curVal = a;
		startTime = System.nanoTime();
		TransitionManager.addToTable(this);
	}
	
	/**
	 * Updates the transition. Pauses once target value is reached
	 * @param delta
	 */
	public void update(float delta) {
		if(paused) return;
		curTime = System.nanoTime();
		
		//Exponential, smooth transition.
		if(curveType == "single") {
			if(!reverse)
				curVal = Math.min(((curTime - startTime) / t) * (b - a), b);
			else
				curVal = Math.max(((curTime - startTime) / t) * (a - b), a);
		} 
		//Linear transition
		else if(curveType == "linear") {
			if(!reverse)
				curVal = Math.min(curVal + (delta / t) * (b-a), b);
			else 
				curVal = Math.max(curVal - (delta / t) * (b-a), a);
		}
		curVal = Math.round(curVal * 10000) / 10000;
		
		if(curVal == (float)b) paused = true;
	//	System.out.println("a: "+a+" b: "+b+" curVal: "+curVal);
	}
	
	/**
	 * Get current value
	 * @return transition value
	 */
	public float getCurVal() {
		return curVal;
	}
	
	/**	Start animation	*/
	public void start() {
		paused = false;
	}
	
	/**	Pause animation	*/
	public void pause() {
		paused = true;
	}
	
	/**	Is it at the target value?	*/
	public boolean finished() {
		if(curVal == (float)b) return true;
		else return false;
	}
	
	/**	Set direction of animation */
	public void setToForward() {
		reverse = false;
	}
	
	/**	Set direction of animation */
	public void setToReverse() {
		reverse = true;
	}
}
