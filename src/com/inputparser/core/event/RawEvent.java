package com.inputparser.core.event;

public class RawEvent {
	public int	devNum, type, code, value;
	public long	delay;

	public RawEvent(int n, int t, int c, int v, long d) {
		devNum = n;
		type = t;
		code = c;
		value = v;
		delay = d;
	}
}
