package com.inputparser.main;

import wei.mark.standout.StandOutWindow;
import android.app.Activity;
import android.os.Bundle;

public class FloatActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		StandOutWindow.closeAll(this, SimpleWindow.class);
		StandOutWindow.show(this, SimpleWindow.class, StandOutWindow.DEFAULT_ID);
	}

}
