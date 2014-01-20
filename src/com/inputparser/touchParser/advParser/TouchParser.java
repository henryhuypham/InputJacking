package com.inputparser.touchParser.advParser;

import java.util.ArrayList;
import java.util.List;

import com.inputparser.touchEvent.RawEvent;

public class TouchParser {
	private List<EventChunk>	rawRecords;
	private List<RawEvent>		eventBuffer;
	private static final int	CHUNK_THRESHOLD	= 200;

	public TouchParser() {
		rawRecords = new ArrayList<EventChunk>();
		eventBuffer = new ArrayList<RawEvent>();
	}

	public List<EventChunk> getRawRecords() {
		return rawRecords;
	}

	public void reset() {
		rawRecords.clear();
	}

	public void rawRecord(int type, int code, int value, long delay) {
		if (delay >= CHUNK_THRESHOLD) {
			rawRecords.add(new EventChunk(eventBuffer));
			eventBuffer = new ArrayList<RawEvent>();
		}
		eventBuffer.add(new RawEvent(type, code, value, delay));
	}

	public void packageRecord() {
		rawRecords.add(new EventChunk(eventBuffer));
	}

}
