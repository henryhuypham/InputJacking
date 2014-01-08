package com.inputparser.touchEvent;

public abstract class BaseTouchEvent {
	public static enum TouchEventType {
		FINGER_DOWN, FINGER_UP, FINGER_TOUCH;
	};
	
	public abstract TouchEventType getType();
}
