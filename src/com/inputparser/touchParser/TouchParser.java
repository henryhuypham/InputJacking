package com.inputparser.touchParser;

import java.util.ArrayList;
import java.util.List;
import com.inputparser.touchEvent.BaseTouchEvent;
import com.inputparser.touchEvent.FingerDown;
import com.inputparser.touchEvent.FingerTouch;
import com.inputparser.touchEvent.FingerUp;
import com.inputparser.touchEvent.RawEvent;

public class TouchParser {
	private static final int		UP_DOWN_TYPE_CODEX			= 1;
	private static final int		UP_DOWN_CODE_CODEX			= 330;
	private static final int		UP_DOWN_VALUE_DOWN_CODEX	= 1;
	private static final int		UP_DOWN_VALUE_UP_CODEX		= 0;

	private static final int		TOUCH_TYPE_CODEX			= 3;
	private static final int		TOUCH_X_CODE_CODEX			= 53;
	private static final int		TOUCH_Y_CODE_CODEX			= 54;

	private static boolean			waitingForX, waitingForY;
	private static int				oldTouchPos;

	private List<BaseTouchEvent>	touchRecords;
	private List<RawEvent>			rawRecords;

	public TouchParser() {
		touchRecords = new ArrayList<BaseTouchEvent>();
		rawRecords = new ArrayList<RawEvent>();
		waitingForX = false;
		waitingForY = false;
	}

	public List<BaseTouchEvent> getTouchRecords() {
		return touchRecords;
	}
	
	public List<RawEvent> getRawRecords() {
		return rawRecords;
	}

	public void reset() {
		touchRecords.clear();
		rawRecords.clear();
	}

	public void parse(int type, int code, int value) {
		switch (type) {
			case UP_DOWN_TYPE_CODEX:
				if (code == UP_DOWN_CODE_CODEX) {
					if (value == UP_DOWN_VALUE_DOWN_CODEX) {
						touchRecords.add(new FingerDown());
					}
					else if (value == UP_DOWN_VALUE_UP_CODEX) {
						touchRecords.add(new FingerUp());
					}
				}
				break;
			case TOUCH_TYPE_CODEX:
				switch (code) {
					case TOUCH_X_CODE_CODEX:
						if (waitingForX) {
							touchRecords.add(new FingerTouch(value, oldTouchPos));
							waitingForX = false;
						}
						else {
							oldTouchPos = value;
							waitingForY = true;
						}
						break;
					case TOUCH_Y_CODE_CODEX:
						if (waitingForY) {
							touchRecords.add(new FingerTouch(oldTouchPos, value));
							waitingForY = false;
						}
						else {
							oldTouchPos = value;
							waitingForX = true;
						}
						break;
				}
				break;
		}
	}
	
	public void rawRecord(int type, int code, int value, long delay) {
		rawRecords.add(new RawEvent(type, code, value, delay));
	}

}
