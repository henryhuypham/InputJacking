package com.inputparser.floatWindow;

import wei.mark.standout.StandOutWindow;
import wei.mark.standout.constants.StandOutFlags;
import wei.mark.standout.ui.Window;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import com.inputparser.R;
import com.inputparser.touchParser.advParser.RecordAgent;

public class SimpleWindow extends StandOutWindow {
	private RecordAgent	agent;
	private View		mVIew;

	@Override
	public String getAppName() {
		return "SimpleWindow";
	}

	@Override
	public int getAppIcon() {
		return android.R.drawable.ic_menu_close_clear_cancel;
	}

	@Override
	public void createAndAttachView(int id, FrameLayout frame) {
		// create a new layout from body.xml
		LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
		mVIew = inflater.inflate(R.layout.simple, frame, true);

		agent = new RecordAgent();
		setButtonFunction();
	}

	private void setButtonFunction() {
		mVIew.findViewById(R.id.startCapture).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				agent.startMonitorWithDelay();
			}
		});
		mVIew.findViewById(R.id.stopCapture).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				agent.stopMonitor();
			}
		});
		mVIew.findViewById(R.id.replicateCapture).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				agent.rawReplicateCapture();
			}
		});
	}

	// the window will be centered
	@Override
	public StandOutLayoutParams getParams(int id, Window window) {
		return new StandOutLayoutParams(id, 250, 300, StandOutLayoutParams.CENTER, StandOutLayoutParams.CENTER);
	}

	// move the window by dragging the view
	@Override
	public int getFlags(int id) {
		return super.getFlags(id) | StandOutFlags.FLAG_BODY_MOVE_ENABLE | StandOutFlags.FLAG_WINDOW_EDGE_LIMITS_ENABLE
				| StandOutFlags.FLAG_WINDOW_HIDE_ENABLE;
	}

	@Override
	public String getPersistentNotificationMessage(int id) {
		return "Click to close the SimpleWindow";
	}

	@Override
	public Intent getPersistentNotificationIntent(int id) {
		return StandOutWindow.getCloseIntent(this, SimpleWindow.class, id);
	}
}
