
package com.sevenge;

import android.app.Activity;
import android.content.res.Configuration;
import android.opengl.GLSurfaceView;
import android.os.Bundle;

import com.sevenge.utils.DebugLog;

/** Class responsible for setting up and running the game engine It contains framelimited game loop and necessary handles for
 * android lifecycle support */
public class GameActivity extends Activity {
	private static final String TAG = "GameActivity";
	private SevenGE gameEngine;
	private GLSurfaceView glSurfaceView;

	/** Function required by android lifecycle, responsible for initialization of the game engine and setting up the OpenGL surface */
	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		DebugLog.d(TAG, "onCreate");
		setContentView(R.layout.activity_main);
		glSurfaceView = (GLSurfaceView)findViewById(R.id.glSurfaceView);
		gameEngine = new SevenGE(this, glSurfaceView);
	}

	/** Handles pausing the game engine */
	@Override
	protected void onPause () {
		super.onPause();
		gameEngine.onPause();
	}

	/** Handles unpausing the game engine */
	@Override
	protected void onResume () {
		super.onResume();
		gameEngine.onResume();
	}

	@Override
	public void onConfigurationChanged (Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

}
