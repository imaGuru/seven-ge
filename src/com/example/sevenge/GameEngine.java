package com.example.sevenge;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import static android.opengl.GLES20.*;
import android.opengl.GLSurfaceView.Renderer;
import android.util.Log;
import static android.opengl.Matrix.*;

public class GameEngine implements Renderer {

	private Context context;
	private static final String TAG = "GameEngine"; 
	private final float[] projectionMatrix = new float[16];
	private long startTime=0;
	private long dt=0, sleepTime=0;
	
	GameEngine(Context context)
	{
		this.context = context;
	}
	@Override
	public void onDrawFrame(GL10 arg0) {
	    dt = (System.currentTimeMillis() - startTime);
	    sleepTime = 32-dt;
	    if (sleepTime>0)
	    {
			try {
				Thread.sleep(sleepTime);
			} catch (InterruptedException e) {}
	    }
	    Log.v(TAG, "FPS: "+ (double)1/(System.currentTimeMillis()-startTime)*1000);
	    startTime = System.currentTimeMillis();
	    glClear(GL_COLOR_BUFFER_BIT);
	    //gamestate.update
	    //gamestate.draw
	}

	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height) {
		glViewport(0, 0, width, height);
		final float aspectRatio = width > height ?
				(float) width / (float) height :
				(float) height / (float) width;
				if (width > height) {
				// Landscape
					orthoM(projectionMatrix, 0, -aspectRatio, aspectRatio, -1f, 1f, -1f, 1f);
				} else {
				// Portrait or square
					orthoM(projectionMatrix, 0, -1f, 1f, -aspectRatio, aspectRatio, -1f, 1f);
				}
	}

	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		glClearColor(0.0f,0.0f,0.0f,0.0f);
	}

}
