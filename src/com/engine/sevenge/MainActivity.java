package com.engine.sevenge;

import com.engine.sevenge.audio.AndroidAudio;
import com.engine.sevenge.input.AndroidInput;
import com.engine.sevenge.input.Input;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.opengl.GLSurfaceView;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Toast;

public class MainActivity extends Activity {

	private GLSurfaceView glSurfaceView;
	private boolean rendererSet = false;
	private Input input;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		glSurfaceView = new GLSurfaceView(this);
		input =  new AndroidInput();
		glSurfaceView.setOnTouchListener( (OnTouchListener) input);
		
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
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
		;

		if (supportsEs2) {
			glSurfaceView.setEGLContextClientVersion(2);
			glSurfaceView.setEGLConfigChooser(8, 8, 8, 8, 16, 0);
			glSurfaceView.setRenderer(new GameEngine(this,input));
			glSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
			rendererSet = true;
			/*
			 * Toast.makeText(this, "OpenGL ES working fine! Renderer set.",
			 * Toast.LENGTH_LONG).show();
			 */
			setContentView(glSurfaceView);
		} else {
			Toast.makeText(this, "This device does not support OpenGL ES 2.0",
					Toast.LENGTH_LONG).show();
			return;
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (rendererSet) {
			glSurfaceView.onPause();
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (rendererSet) {
			glSurfaceView.onResume();
		}
	}

}
