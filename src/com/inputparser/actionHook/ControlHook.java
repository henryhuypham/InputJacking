package com.inputparser.actionHook;

public interface ControlHook {
	void onPreStart();

	void onPostStart();

	void onPreStop();

	void onPostStop();

	void onPreReplicate();

	void onPostReplicate();
}
