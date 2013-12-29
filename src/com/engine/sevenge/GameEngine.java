package com.engine.sevenge;

import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES20.GL_VERTEX_SHADER;
import static android.opengl.GLES20.GL_FRAGMENT_SHADER;
import static android.opengl.GLES20.glClear;
import static android.opengl.GLES20.glClearColor;
import static android.opengl.GLES20.glViewport;
import static android.opengl.Matrix.orthoM;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.R;
import android.content.Context;
import android.graphics.Shader;
import android.opengl.GLSurfaceView.Renderer;

import com.engine.sevenge.graphics.ShaderProgram;
import com.engine.sevenge.utils.Helper;
import com.engine.sevenge.utils.Log;

public class GameEngine implements Renderer {

	private Context context;
	private static final String TAG = "GameEngine";
	private long startTime = 0;
	private long dt = 0, sleepTime = 0;
	private static final long FRAME_TIME = 32;
	private static final int MAX_FRAME_SKIPS = 5;
	private int framesSkipped = 0;

	private static final String A_COLOR = "a_Color";
	private static final int COLOR_COMPONENT_COUNT = 3;
	private static final int BYTES_PER_FLOAT = 4;
	private static final int STRIDE = (POSITION_COMPONENT_COUNT + COLOR_COMPONENT_COUNT)
			* BYTES_PER_FLOAT;

	private int aColorLocation;

	private static final String U_COLOR = "u_Color";
	private int uColorLocation;

	private static final String A_POSITION = "a_Position";
	private int aPositionLocation;

	private static final String U_MATRIX = "u_Matrix";
	private final float[] projectionMatrix = new float[16];
	private int uMatrixLocation;

	GameEngine(Context context) {
		this.context = context;
		// init gamestates
	}

	@Override
	public void onDrawFrame(GL10 arg0) {
		dt = (System.currentTimeMillis() - startTime);
		sleepTime = FRAME_TIME - dt;
		if (sleepTime > 0) {
			try {
				Thread.sleep(sleepTime);
			} catch (InterruptedException e) {
			}
		}
		framesSkipped = 0;
		while (sleepTime < 0 && framesSkipped < MAX_FRAME_SKIPS) {
			sleepTime += FRAME_TIME;
			// gamestate.update
			framesSkipped++;
		}
		Log.v(TAG, "FramesSkipped: " + framesSkipped + " FPS: " + (double) 1
				/ (System.currentTimeMillis() - startTime) * 1000);
		startTime = System.currentTimeMillis();
		// gamestate.update
		// gamestate.draw
		glClear(GL_COLOR_BUFFER_BIT);
	}

	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height) {
		glViewport(0, 0, width, height);
		final float aspectRatio = width > height ? (float) width
				/ (float) height : (float) height / (float) width;
		if (width > height) {
			// Landscape
			orthoM(projectionMatrix, 0, -aspectRatio, aspectRatio, -1f, 1f,
					-1f, 1f);
		} else {
			// Portrait or square
			orthoM(projectionMatrix, 0, -1f, 1f, -aspectRatio, aspectRatio,
					-1f, 1f);
		}
	}

	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
		Shader vertexShader = new Shader(Helper.readRawTextFile(context, R.raw.simple_vertex_shader),GL_VERTEX_SHADER);
		
		ShaderProgram program = new ShaderProgram(vertexShaderID, fragmentShaderID)
		uColorLocation = glGetUniformLocation(program, U_COLOR);
		aPositionLocation = glGetAttribLocation(program, A_POSITION);
		uMatrixLocation = glGetUniformLocation(program, U_MATRIX);
	}
}
