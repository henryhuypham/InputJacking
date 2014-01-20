package com.inputparser.touchParser.rawParser;

import java.util.concurrent.atomic.AtomicBoolean;
import com.inputparser.touchEvent.BaseTouchEvent;
import com.inputparser.touchEvent.FingerTouch;
import com.inputparser.touchEvent.RawEvent;
import android.text.format.Time;
import android.util.Log;
import net.pocketmagic.android.eventinjector.Events;
import net.pocketmagic.android.eventinjector.Events.InputDevice;

public class RecordAgent {
	private static final int	DEVICE_NUM		= 3;
	private static final long	REPLICATE_DELAY	= 2000;
	private Events				events;
	private String				logTag;
	private AtomicBoolean		isMonitorOn;
	private TouchParser			parser;

	public RecordAgent() {
		events = new Events();
		logTag = "InputParser";
		isMonitorOn = new AtomicBoolean();
		parser = new TouchParser();

		init();
	}

	private void init() {
		scanDevices();
		openAllDevices();
	}

	private void openAllDevices() {
		for (InputDevice dev : events.m_Devs) {
			if (dev.Open(true)) {
				Log.d(logTag, dev.getPath() + " open succesful!");
			}
			else {
				Log.d(logTag, dev.getPath() + " open failed!");
			}
		}
	}

	private void scanDevices() {
		Events.intEnableDebug(1);
		Log.d(logTag, "Scanning for input dev files.");
		int res = events.Init();
		Log.d(logTag, "Event files:" + res);
	}

	public void startMonitor() {
		isMonitorOn.set(true);
		parser.reset();
		final Time now = new Time();
		now.setToNow();

		Thread monitorThread = new Thread(new Runnable() {
			public void run() {
				while (isMonitorOn.get()) {
					long lastTime = now.toMillis(true);
					for (InputDevice idev : events.m_Devs) {
						if (idev.getOpen() && (0 == idev.getPollingEvent())) {
							final int type = idev.getSuccessfulPollingType();
							final int code = idev.getSuccessfulPollingCode();
							final int value = idev.getSuccessfulPollingValue();
							final String line = idev.getName() + ":" + type + " " + code + " " + value + " "
									+ idev.getSuccessfulTimeMajor() + " " + idev.getSuccessfulTimeMinor();
							Log.d(logTag, "Event:" + line);

							now.setToNow();
							long currentTime = now.toMillis(true);
							rawRecordTouchEvent(type, code, value, currentTime - lastTime);
							Log.d(logTag, "Event time delay: " + (currentTime - lastTime));
							lastTime = currentTime;
						}

					}
				}
				Log.d(logTag, "Stopped!!!!!");
			}
		});
		monitorThread.start();
	}

	public void startMonitorWithDelay() {
		isMonitorOn.set(true);
		parser.reset();

		Thread monitorThread = new Thread(new Runnable() {
			public void run() {
				long oldTimeMajor = -1, oldTimeMinor = -1;
				while (isMonitorOn.get()) {
					for (InputDevice idev : events.m_Devs) {
						if (idev.getOpen() && (0 == idev.getPollingEvent())) {
							int type = idev.getSuccessfulPollingType();
							int code = idev.getSuccessfulPollingCode();
							int value = idev.getSuccessfulPollingValue();
							long timeMajor = idev.getSuccessfulTimeMajor();
							long timeMinor = idev.getSuccessfulTimeMinor();

							long dMt = oldTimeMajor == -1 ? 0 : timeMajor - oldTimeMajor;
							long dmt = oldTimeMinor == -1 ? 0 : timeMinor - oldTimeMinor;
							long dt = dMt * 1000 + dmt / 1000;
							dt = (dt >= 1 ? dt : 1);

							String line = idev.getName() + ":" + type + " " + code + " " + value + " " + timeMajor + " " + timeMinor
									+ " - " + dt;
							Log.d(logTag, "Event:" + line);

							oldTimeMajor = timeMajor;
							oldTimeMinor = timeMinor;

							rawRecordTouchEvent(type, code, value, dt);
						}

					}
				}
				Log.d(logTag, "Stopped!!!!!");
			}
		});
		monitorThread.start();
	}

	public void stopMonitor() {
		isMonitorOn.set(false);
	}

	private void delay(long delay) {
		try {
			Thread.sleep(delay);
		}
		catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	/*
	 * Record & parsing
	 */
	public void replicateCapture() {
		delay(REPLICATE_DELAY);
		for (BaseTouchEvent event : parser.getTouchRecords()) {
			switch (event.getType()) {
			case FINGER_DOWN:
				Log.d(logTag, "Finger down");
				break;
			case FINGER_UP:
				Log.d(logTag, "Finger up");
				break;
			case FINGER_TOUCH:
				FingerTouch touch = (FingerTouch) event;
				Log.d(logTag, "Finger touch: " + touch.getX() + " - " + touch.getY());
				delay(100);
				touchAt(touch.getX(), touch.getY());
				break;
			default:
				break;
			}
		}
	}

	private void recordTouchEvent(int type, int code, int value) {
		parser.parse(type, code, value);
	}

	private void touchAt(int x, int y) {
		events.m_Devs.get(3).SendTouchDownAbs(x, y);
	}

	/*
	 * Raw Record
	 */
	private void rawRecordTouchEvent(int type, int code, int value, long delay) {
		parser.rawRecord(type, code, value, delay);
	}

	public void rawReplicateCapture() {
		Log.d(logTag, "Event COunt: " + parser.getRawRecords().size());
		delay(REPLICATE_DELAY);
		for (RawEvent event : parser.getRawRecords()) {
			delay(event.delay);
			Log.d(logTag, "-- Delay: " + event.delay);
			rawTouchAt(event.type, event.code, event.value);
		}
		Log.d(logTag, "DONE!!!!!!");
	}

	private void rawTouchAt(int type, int code, int value) {
		InputDevice device = events.m_Devs.get(DEVICE_NUM);
		events.rawSendEvent(device.getId(), type, code, value);

		String line = type + " " + code + " " + value;
		Log.d(logTag, line);
	}
}
