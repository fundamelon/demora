package util;

public class Transition {
	float a, b, d, t; //start, end, duration, tension (curves only)
	float curVal;
	float timeElapsed, timeLeft;
	String curveType = "linear";
	
	String[] validCurveTypes = new String[]{
		"linear", "single", "double", "hold"
	};
	
	public Transition(float a, float b, float d, float t, String c) {
		this.a = a;
		this.b = b;
		this.t = t;
		
		this.curveType = c;
	}
	
	public void update(float delta) {
		
	}
	
	public float getCurVal() {
		return curVal;
	}
	
	public boolean isValidCurveType(String c) {
		return false;
	}
}
