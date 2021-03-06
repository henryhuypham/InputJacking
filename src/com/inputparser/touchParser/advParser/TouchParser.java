package com.inputparser.touchParser.advParser;

import java.util.ArrayList;
import java.util.List;
import android.util.Log;
import com.inputparser.touchEvent.RawEvent;

public class TouchParser {
	private List<EventChunk>	rawRecords;
	private List<RawEvent>		eventBuffer;
	private boolean				recording;
	private static final int	CHUNK_THRESHOLD	= 200;

	public TouchParser() {
		rawRecords = new ArrayList<EventChunk>();
		eventBuffer = new ArrayList<RawEvent>();
		recording = true;
	}

	public List<EventChunk> getRawRecords() {
		return rawRecords;
	}

	public void reset() {
		recording = true;
		rawRecords.clear();
	}

	public void rawRecord(int type, int code, int value, long delay) {
		if (recording) {
			if (delay >= CHUNK_THRESHOLD) {
				rawRecords.add(new EventChunk(eventBuffer));
				eventBuffer = new ArrayList<RawEvent>();
				Log.d("inputParser", "CHUNK " + rawRecords.size());
			}
			eventBuffer.add(new RawEvent(type, code, value, delay));
		}
	}

	public void packageRecord() {
		rawRecords.remove(0);
	}

	public void disableRecording() {
		recording = false;
	}

	public void enableRecording() {
		recording = true;
	}

}
