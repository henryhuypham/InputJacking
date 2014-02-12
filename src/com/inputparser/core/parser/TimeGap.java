package com.inputparser.core.parser;

public class TimeGap {
	private long	timeMajor, timeMinor;
	private long timeDiff;

	public TimeGap() {
		timeMajor = -1;
		timeMinor = -1;
	}
	
	public long getTimeDiff(long timeMajor, long timeMinor) {
		long dMt = this.timeMajor == -1 ? 0 : timeMajor - this.timeMajor;
		long dmt = this.timeMinor == -1 ? 0 : timeMinor - this.timeMinor;
		timeDiff = dMt * 1000 + dmt / 1000;
		timeDiff = (timeDiff >= 1 ? timeDiff : 1);
		
		this.timeMajor = timeMajor;
		this.timeMinor = timeMinor;
		
		return timeDiff;
	}
}
