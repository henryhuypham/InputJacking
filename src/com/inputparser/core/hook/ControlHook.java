package com.inputparser.core.hook;

public interface ControlHook {
	void onPreStart();

	void onPostStart();

	void onPreStop();

	void onPostStop();

	void onPreReplicate();

	void onPostReplicate();
}
