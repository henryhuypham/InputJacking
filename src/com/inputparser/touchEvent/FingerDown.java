package com.inputparser.touchEvent;

public class FingerDown extends BaseTouchEvent {

	@Override
	public TouchEventType getType() {
		return TouchEventType.FINGER_DOWN;
	}

}
