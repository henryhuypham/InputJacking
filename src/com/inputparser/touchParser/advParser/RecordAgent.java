package com.inputparser.touchParser.advParser;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import net.pocketmagic.android.eventinjector.Events;
import net.pocketmagic.android.eventinjector.Events.InputDevice;
import android.os.AsyncTask;
import android.util.Log;
import com.inputparser.actionHook.ControlHook;
import com.inputparser.touchEvent.RawEvent;

public class RecordAgent {
	private static final long	REPLICATE_DELAY	= 500;
	private Events				events;
	private String				logTag;
	private AtomicBoolean		isMonitorOn;
	private TouchParser			parser;
	private ControlHook			host;
	private Set<Integer>		blackList;
	protected String[]			blackListDevName;

	public RecordAgent(ControlHook host, String... blkList) {
		events = new Events();
		logTag = "InputParser";
		isMonitorOn = new AtomicBoolean();
		parser = new TouchParser();

		this.host = host;
		this.blackList = new HashSet<Integer>();
		this.blackListDevName = blkList;

		init();
	}

	private void init() {
		scanDevices();
		openAllDevices();

		for (int i = 0; i < events.m_Devs.size(); i++) {
			for (String blk : blackListDevName) {
				if (events.m_Devs.get(i).getPath().contains(blk)) {
					blackList.add(i);
					break;
				}
			}
		}
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
		host.onPreStart();

		isMonitorOn.set(true);
		parser.reset();

		Thread monitorThread = new Thread(new Runnable() {
			public void run() {
				long oldTimeMajor = -1, oldTimeMinor = -1;

				while (isMonitorOn.get()) {
					for (int i = 0; i < events.m_Devs.size(); i++) {
						if (blackList.contains(i))
							continue;

						InputDevice idev = events.m_Devs.get(i);

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
							
							if (dt >= 200)
								Log.d(logTag, "---------------------------");
							String line = idev.getName() + ":" + type + " " + code + " " + value + " " + timeMajor + " " + timeMinor
									+ " - " + dt;
							Log.d(logTag, "Event:" + line);
							
							oldTimeMajor = timeMajor;
							oldTimeMinor = timeMinor;

							rawRecordTouchEvent(i, type, code, value, dt);
						}
					}
				}
				packageRecord();
				Log.d(logTag, "Stopped!!!!!");

				host.onPostStart();
			}
		});
		monitorThread.start();
	}

	public void stopMonitor() {
		host.onPreStop();

		isMonitorOn.set(false);
		parser.disableRecording();

		host.onPostStop();
	}

	public void resetParser() {
		parser.reset();
	}

	public void enableRecording() {
		parser.enableRecording();
	}

	public void disableRecording() {
		parser.disableRecording();
	}

	private void delay(long delay) {
		try {
			Thread.sleep(delay);
		}
		catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private void rawRecordTouchEvent(int devNum, int type, int code, int value, long delay) {
		parser.rawRecord(devNum, type, code, value, delay);
	}

	private void packageRecord() {
		parser.packageRecord();
	}

	public void rawReplicateCapture() {
		delay(REPLICATE_DELAY);
		spawEvent(0);
	}

	private void spawEvent(final int index) {
		final EventChunk chunk = parser.getRawRecords().get(index);
		new AsyncTask<Void, Void, Void>() {
			@Override
			protected void onPreExecute() {
				if (index == 0) {
					host.onPreReplicate();
				}
			}

			@Override
			protected Void doInBackground(Void... params) {
				List<RawEvent> records = chunk.getRawRecords();
				Log.d(logTag, "-- CHUNK " + index);
				for (RawEvent event : records) {
					delay(event.delay);
					// Log.d(logTag, "-- Delay: " + event.delay);
					rawEventReplay(event);
				}
				return null;
			}

			@Override
			protected void onPostExecute(Void result) {
				if (index < parser.getRawRecords().size() - 1) {
					Log.d(logTag, "-- Spawn new event " + (index + 1));
					spawEvent(index + 1);
				}
				else {
					host.onPostReplicate();
				}
			}
		}.execute();
	}

	private void rawEventReplay(RawEvent event) {
		InputDevice device = events.m_Devs.get(event.devNum);
		events.rawSendEvent(device.getId(), event.type, event.code, event.value);

		String line = event.type + " " + event.code + " " + event.value;
		Log.d(logTag, line);
	}
}
