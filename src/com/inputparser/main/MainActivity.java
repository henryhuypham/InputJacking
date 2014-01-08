package com.inputparser.main;

import java.util.concurrent.atomic.AtomicBoolean;
import net.pocketmagic.android.eventinjector.Events;
import net.pocketmagic.android.eventinjector.Events.InputDevice;
import android.app.Activity;
import android.os.Bundle;
import android.text.format.Time;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import com.inputparser.R;
import com.inputparser.touchEvent.BaseTouchEvent;
import com.inputparser.touchEvent.FingerTouch;
import com.inputparser.touchEvent.RawEvent;
import com.inputparser.touchParser.TouchParser;

public class MainActivity extends Activity {
	private Events			events		= new Events();
	private String			logTag		= "InputParser";
	private AtomicBoolean	isMonitorOn	= new AtomicBoolean();
	private TouchParser		parser		= new TouchParser();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		scanDevices();
		openAllDevices();
		setButtonFunction();
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

	private void setButtonFunction() {
		findViewById(R.id.startCapture).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startMonitor();
			}
		});

		findViewById(R.id.stopCapture).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				stopMonitor();
			}
		});

		findViewById(R.id.replicateCapture).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// replicateCapture();
				rawReplicateCapture();
			}
		});
	}

	private void startMonitor() {
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
							final String line = idev.getName() + ":" + type + " " + code + " " + value;
							Log.d(logTag, "Event:" + line);

							// recordTouchEvent(type, code, value);
							now.setToNow();
							long currentTime = now.toMillis(true);
							rawRecordTouchEvent(type, code, value, currentTime - lastTime);
							Log.d(logTag, "Event time delay:" + currentTime + " - " + lastTime);
							lastTime = currentTime;
						}

					}
				}
				Log.d(logTag, "Stopped!!!!!");
			}
		});
		monitorThread.start();
	}

	private void stopMonitor() {
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
	private void replicateCapture() {
		delay(5000);
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

	private void rawReplicateCapture() {
		Log.d(logTag, "Event COunt: " + parser.getRawRecords().size());
		delay(5000);
		for (RawEvent event : parser.getRawRecords()) {
			rawTouchAt(event.type, event.code, event.value);
			delay(5);
		}
		Log.d(logTag, "DONE!!!!!!");
	}

	private void rawTouchAt(int type, int code, int value) {
		InputDevice device = events.m_Devs.get(3);
		events.rawSendEvent(device.getId(), type, code, value);

		String line = type + " " + code + " " + value;
		Log.d(logTag, line);
	}
}
