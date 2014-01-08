package com.inputparser.touchEvent;

public class FingerUp extends BaseTouchEvent {

	@Override
	public TouchEventType getType() {
		return TouchEventType.FINGER_UP;
	}

}
