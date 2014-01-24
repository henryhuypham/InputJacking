package com.inputparser.floatWindow;

import wei.mark.standout.StandOutWindow;
import wei.mark.standout.constants.StandOutFlags;
import wei.mark.standout.ui.Window;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.Toast;
import com.inputparser.R;
import com.inputparser.actionHook.ControlHook;
import com.inputparser.touchParser.advParser.RecordAgent;
import com.inputparser.touchParser.phoneSpecific.FakeS4;
import com.inputparser.touchParser.phoneSpecific.HtcOneAgent;

public class SimpleWindow extends StandOutWindow implements ControlHook {
	private RecordAgent	agent;
	private View		mVIew;
	private int			myId;

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
		myId = id;

		agent = new FakeS4(this);
		setButtonFunction();
	}

	private void setButtonFunction() {
		mVIew.findViewById(R.id.startCapture).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				agent.startMonitorWithDelay();
				agent.disableRecording();

				new Handler().postDelayed(new Runnable() {
					@Override
					public void run() {
						agent.enableRecording();
					}
				}, 500);
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

	@Override
	public void onPreStart() {
		Toast.makeText(getApplicationContext(), "Start Recording", Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onPostStart() {
	}

	@Override
	public void onPreStop() {
	}

	@Override
	public void onPostStop() {
		Toast.makeText(getApplicationContext(), "Stop Recording", Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onPreReplicate() {
//		new AsyncTask<Void, Void, Void>() {
//			@Override
//			protected Void doInBackground(Void... params) {
//				SimpleWindow.this.hide(myId);
//				return null;
//			}
//		}.execute();
		Toast.makeText(getApplicationContext(), "Start Playback", Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onPostReplicate() {
//		new Handler().postDelayed(new Runnable() {
//			@Override
//			public void run() {
//				SimpleWindow.this.show(myId);
//			}
//		}, 2000);
		Toast.makeText(getApplicationContext(), "Done Playback", Toast.LENGTH_SHORT).show();
	}
}
