package com.inputparser.touchEvent;

public class FingerTouch extends BaseTouchEvent {
	private int	x, y;

	public FingerTouch(int X, int Y) {
		x = X;
		y = Y;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	@Override
	public TouchEventType getType() {
		return TouchEventType.FINGER_TOUCH;
	}

}
