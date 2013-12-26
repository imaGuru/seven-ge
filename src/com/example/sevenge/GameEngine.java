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
	private static final long FRAME_TIME=32;
	private static final int MAX_FRAME_SKIPS=5;
	private int framesSkipped=0;
	
	GameEngine(Context context)
	{
		this.context = context;
	}
	@Override
	public void onDrawFrame(GL10 arg0) {
	    dt = (System.currentTimeMillis() - startTime);
	    sleepTime = FRAME_TIME-dt;
	    if (sleepTime>0)
	    {
			try {
				Thread.sleep(sleepTime);
			} catch (InterruptedException e) {}
	    }
	    framesSkipped = 0;
	    while (sleepTime < 0 && framesSkipped < MAX_FRAME_SKIPS) {
	    	sleepTime += FRAME_TIME;
	    	//gamestate.update
	    	framesSkipped++;
	    }
	    Log.v(TAG, "FramesSkipped: "+framesSkipped+" FPS: "+ (double)1/(System.currentTimeMillis()-startTime)*1000);
	    startTime = System.currentTimeMillis();
	    //gamestate.update
	    //gamestate.draw
	    glClear(GL_COLOR_BUFFER_BIT);
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
