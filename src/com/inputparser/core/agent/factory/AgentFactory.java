package com.inputparser.core.agent.factory;

import com.inputparser.core.parser.RecordAgent;

public class AgentFactory {
	public static RecordAgent getAgent(DeviceAgent agent) {
		return agent.createAgent();
	}
}
