package com.inputparser.touchParser.advParser;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import net.pocketmagic.android.eventinjector.Events;
import net.pocketmagic.android.eventinjector.Events.InputDevice;
import android.os.AsyncTask;
import android.util.Log;
import com.inputparser.touchEvent.RawEvent;

public class RecordAgent {
	private static final int	DEVICE_NUM		= 3;
	private static final long	REPLICATE_DELAY	= 500;
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
	 * Raw Record
	 */
	private void rawRecordTouchEvent(int type, int code, int value, long delay) {
		parser.rawRecord(type, code, value, delay);
	}

	public void rawReplicateCapture() {
		delay(REPLICATE_DELAY);
		spawEvent(0);
	}

	private void spawEvent(final int index) {
		final EventChunk chunk = parser.getRawRecords().get(index);
		new AsyncTask<Void, Void, Void>() {
			@Override
			protected Void doInBackground(Void... params) {
				List<RawEvent> records = chunk.getRawRecords();
				for (RawEvent event : records) {
					delay(event.delay);
					Log.d(logTag, "-- Delay: " + event.delay);
					rawTouchAt(event.type, event.code, event.value);
				}
				return null;
			}

			@Override
			protected void onPostExecute(Void result) {
				if (index < parser.getRawRecords().size() - 1) {
					Log.d(logTag, "-- Spawn new event " + (index + 1));
					spawEvent(index + 1);
				}
			}
		}.execute();
	}

	private void rawTouchAt(int type, int code, int value) {
		InputDevice device = events.m_Devs.get(DEVICE_NUM);
		events.rawSendEvent(device.getId(), type, code, value);

		String line = type + " " + code + " " + value;
		Log.d(logTag, line);
	}
}
