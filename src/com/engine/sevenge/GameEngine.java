package com.engine.sevenge;

import static android.opengl.GLES20.GL_FRAGMENT_SHADER;
import static android.opengl.GLES20.GL_VERTEX_SHADER;
import static android.opengl.GLES20.glViewport;
import static android.opengl.Matrix.multiplyMM;
import static android.opengl.Matrix.orthoM;
import static android.opengl.Matrix.setLookAtM;

import java.util.Random;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.opengl.GLSurfaceView.Renderer;

import com.engine.sevenge.graphics.LRenderer;
import com.engine.sevenge.graphics.Shader;
import com.engine.sevenge.graphics.Sprite;
import com.engine.sevenge.graphics.SpriteBatch;
import com.engine.sevenge.graphics.Texture2D;
import com.engine.sevenge.graphics.TextureShaderProgram;
import com.engine.sevenge.utils.Helper;
import com.engine.sevenge.utils.Log;

public class GameEngine implements Renderer {

	private static final String TAG = "GameEngine";

	private Context context;
	private float x = 0, y = 0;
	private float mHeight = 0, mWidth = 0;
	private long startTime = 0;
	private long dt = 0, sleepTime = 0;
	private int framesSkipped = 0;

	private static final long FRAME_TIME = 32;
	private static final int MAX_FRAME_SKIPS = 5;

	private float[] projectionMatrix = new float[16];
	private float[] viewMatrix = new float[16];
	private float[] viewProjectionMatrix = new float[16];

	private LRenderer renderer;

	private SpriteBatch spriteBatch;

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
		x += 1f;
		y += 1f;
		setLookAtM(viewMatrix, 0, x, y, 1f, x, y, 0f, 0f, 1.0f, 0.0f);
		multiplyMM(viewProjectionMatrix, 0, projectionMatrix, 0, viewMatrix, 0);
		spriteBatch.setVPMatrix(viewProjectionMatrix);
		renderer.addToRender(spriteBatch);
		renderer.render();
	}

	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height) {
		glViewport(0, 0, width, height);
		for (int i = 0; i < 16; i++) {
			projectionMatrix[i] = 0.0f;
			viewMatrix[i] = 0.0f;
			viewProjectionMatrix[i] = 0.0f;
		}
		orthoM(projectionMatrix, 0, 0, width, 0, height, 0, 1f);
		mWidth = width;
		mHeight = height;
		x = -width / 2;
		y = -height / 2;
	}

	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		renderer = new LRenderer();
		Texture2D tex = new Texture2D(context, R.drawable.apple);
		Shader vs = new Shader(Helper.readRawTextFile(context,
				R.raw.texture_vertex_shader), GL_VERTEX_SHADER);
		Shader fs = new Shader(Helper.readRawTextFile(context,
				R.raw.texture_fragment_shader), GL_FRAGMENT_SHADER);
		TextureShaderProgram spriteShader = new TextureShaderProgram(
				vs.getGLID(), fs.getGLID());
		spriteBatch = new SpriteBatch(tex, spriteShader, 1000);
		Random rng = new Random();
		for (int i = 0; i < 1000; i++) {
			Sprite sprite = new Sprite(100f, 100f, new float[] { 0.0f, 0.0f,
					0.0f, 1.0f, 1.0f, 1.0f, 1.0f, 0.0f }, tex);
			sprite.rotate(rng.nextFloat() * 6.28f);
			sprite.translate(rng.nextFloat() * 3000f, rng.nextFloat() * 3000f);
			spriteBatch.add(sprite.getTransformedVertices());
		}
		spriteBatch.upload();
	}
}
