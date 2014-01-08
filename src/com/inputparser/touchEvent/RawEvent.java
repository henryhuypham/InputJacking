package com.inputparser.touchEvent;

public class RawEvent {
	public int	type, code, value;
	public long	delay;

	public RawEvent(int t, int c, int v, long d) {
		type = t;
		code = c;
		value = v;
		delay = d;
	}
}
