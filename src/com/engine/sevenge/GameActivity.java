package com.engine.sevenge;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.opengl.GLSurfaceView;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.widget.Toast;

import com.engine.sevenge.assets.AssetManager;
import com.engine.sevenge.audio.Audio;
import com.engine.sevenge.input.InputListener;
import com.engine.sevenge.io.IO;

public abstract class GameActivity extends Activity
{

	private GLSurfaceView glSurfaceView;
	private boolean rendererSet = false;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		Bundle extras = getIntent().getExtras();
		if (extras != null)
		{
			int demo = extras.getInt("demo");
			Toast.makeText(this, "Demo " + demo, Toast.LENGTH_LONG).show();
		}

		final ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
		final ConfigurationInfo configurationInfo = activityManager
				.getDeviceConfigurationInfo();
		final boolean supportsEs2 = configurationInfo.reqGlEsVersion >= 0x20000
				|| (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1 && (Build.FINGERPRINT
						.startsWith("generic")
						|| Build.FINGERPRINT.startsWith("unknown")
						|| Build.MODEL.contains("google_sdk")
						|| Build.MODEL.contains("Emulator") || Build.MODEL
							.contains("Android SDK built for x86")));

		SevenGE.input = new InputListener();
		SevenGE.io = new IO(this);
		SevenGE.renderer = new GameRenderer(this);
		SevenGE.audio = new Audio(this);
		SevenGE.assetManager = new AssetManager();
		SevenGE.stateManager = new GameStateManager();

		if (supportsEs2)
		{
			glSurfaceView = new GLSurfaceView(this);
			glSurfaceView.setEGLContextClientVersion(2);
			glSurfaceView.setEGLConfigChooser(8, 8, 8, 8, 16, 0);
			glSurfaceView.setPreserveEGLContextOnPause(true);
			glSurfaceView.setRenderer(SevenGE.renderer);
			glSurfaceView.setOnTouchListener(SevenGE.input);
			glSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
			rendererSet = true;
			SevenGE.stateManager.setCurrentState(getStartStage());
			setContentView(glSurfaceView);

		}
		else
		{
			Toast.makeText(this, "This device does not support OpenGL ES 2.0",
					Toast.LENGTH_LONG).show();
			return;
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	protected void onPause()
	{
		super.onPause();

		SevenGE.stateManager.pause();

		glSurfaceView.onPause();

	}

	@Override
	protected void onResume()
	{
		super.onResume();

		SevenGE.stateManager.resume();

		glSurfaceView.onResume();

	}

	public abstract GameState getStartStage();
}
