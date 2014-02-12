package com.inputparser.core.agent.factory;

import com.inputparser.core.agent.deviceAgent.FakeS4;
import com.inputparser.core.agent.deviceAgent.HtcOneAgent;
import com.inputparser.core.parser.RecordAgent;

public enum DeviceAgent {
	FakeS4 {
		@Override
		public RecordAgent createAgent() {
			return new FakeS4();
		}
	},
	
	HtcOne {
		@Override
		public RecordAgent createAgent() {
			return new HtcOneAgent();
		}
	};
	
	public abstract RecordAgent createAgent();
}
