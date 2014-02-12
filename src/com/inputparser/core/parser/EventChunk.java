package com.inputparser.core.parser;

import java.util.ArrayList;
import java.util.List;

import com.inputparser.core.event.RawEvent;

public class EventChunk {
	private List<RawEvent>	rawRecords;

	public EventChunk() {
		rawRecords = new ArrayList<RawEvent>();
	}

	public EventChunk(List<RawEvent> ev) {
		rawRecords = ev;
	}

	public List<RawEvent> getRawRecords() {
		return rawRecords;
	}

	public void setRawRecords(List<RawEvent> events) {
		rawRecords = events;
	}

	public void reset() {
		rawRecords.clear();
	}
}
