package com.engine.sevenge;

import static android.opengl.GLES20.glViewport;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.opengl.GLSurfaceView.Renderer;
import android.view.MotionEvent;

import com.engine.sevenge.audio.Music;
import com.engine.sevenge.graphics.Camera2D;
import com.engine.sevenge.graphics.GLRenderer;
import com.engine.sevenge.graphics.Sprite;
import com.engine.sevenge.graphics.SpriteBatch;
import com.engine.sevenge.graphics.SubTexture2D;
import com.engine.sevenge.graphics.Texture2D;
import com.engine.sevenge.graphics.TextureShaderProgram;
import com.engine.sevenge.input.SingleTouchDetector;
import com.engine.sevenge.input.SingleTouchDetector.onGestureListener;
import com.engine.sevenge.utils.Log;

public class GameRenderer implements Renderer {

	private static final String TAG = "GameEngine";

	private long startTime = 0;
	private long dt = 0, sleepTime = 0;
	private int framesSkipped = 0;

	private static final long FRAME_TIME = 32;
	private static final int MAX_FRAME_SKIPS = 5;

	private GLRenderer renderer;

	private SpriteBatch spriteBatch;
	private Camera2D camera;
	private InputProcessor inputProcessor;

	GameRenderer(Context context) {
		// init gamestates
		inputProcessor = new InputProcessor();
		SevenGE.input.addDetector(new SingleTouchDetector(context,
				new SampleGestureListener(inputProcessor)));
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
		// Log.v(TAG, "FramesSkipped: " + framesSkipped + " FPS: " + (double) 1
		// / (System.currentTimeMillis() - startTime) * 1000);
		startTime = System.currentTimeMillis();

		// gamestate.update
		inputProcessor.process(camera);
		// gamestate.draw
		spriteBatch.setVPMatrix(camera.getViewProjectionMatrix());
		renderer.addToRender(spriteBatch);
		renderer.render();
	}

	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height) {
		glViewport(0, 0, width, height);
		camera.setProjectionOrtho(width, height);
	}

	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		SevenGE.assetManager.loadAssets(SevenGE.io.asset("sample.pkg"));
		Music music = (Music) SevenGE.assetManager.getAsset("music1");
		music.setLooping(true);
		music.play();
		renderer = new GLRenderer();
		camera = new Camera2D();
		camera.lookAt(0, 0);
		camera.zoom(1.2f);
		TextureShaderProgram tsp = (TextureShaderProgram) SevenGE.assetManager
				.getAsset("spriteShader");
		Texture2D tex = (Texture2D) SevenGE.assetManager.getAsset("spaceSheet");
		spriteBatch = new SpriteBatch(tex, tsp, 1000);
		Random rng = new Random();
		for (int i = 0; i < 350; i++) {
			Sprite sprite;
			if (rng.nextInt(10) < 3)
				sprite = new Sprite(
						(SubTexture2D) SevenGE.assetManager
								.getAsset("meteorBrown_big1"));
			else if (rng.nextInt(10) < 6)
				sprite = new Sprite(
						(SubTexture2D) SevenGE.assetManager
								.getAsset("meteorBrown_small2"));
			else if (rng.nextInt(10) < 9)
				sprite = new Sprite(
						(SubTexture2D) SevenGE.assetManager
								.getAsset("meteorBrown_tiny2"));
			else
				sprite = new Sprite(
						(SubTexture2D) SevenGE.assetManager
								.getAsset("enemyRed1"));

			sprite.rotate(rng.nextFloat() * 6.28f);
			sprite.translate(rng.nextFloat() * 3000f,
					rng.nextFloat() * 3000f);
			spriteBatch.add(sprite.getTransformedVertices());
		}
		spriteBatch.upload();
	}
}

class SampleGestureListener implements onGestureListener {
	private static final String TAG = "Gesture";
	private final InputProcessor ip;

	public SampleGestureListener(InputProcessor ip) {
		this.ip = ip;
	}

	@Override
	public boolean onDown(MotionEvent e) {
		Log.i(TAG, "Down");
		InputEvent ie = new InputEvent("Down");
		ie.events.add(e);
		ip.addEvent(ie);
		return true;
	}

	@Override
	public boolean onUp(MotionEvent e) {
		Log.i(TAG, "Up");
		InputEvent ie = new InputEvent("Up");
		ie.events.add(e);
		ip.addEvent(ie);
		return true;
	}

	@Override
	public boolean onLongPress(MotionEvent e) {
		Log.i(TAG, "Long Press");
		InputEvent ie = new InputEvent("LongPress");
		ie.events.add(e);
		ip.addEvent(ie);
		return true;
	}

	@Override
	public boolean onDrag(float x1, float y1, float x2, float y2) {
		Log.i(TAG, "Drag");
		InputEvent ie = new InputEvent("Drag");
		ie.x2 = x2;
		ie.y2 = y2;
		ie.y1 = y1;
		ie.x1 = x1;
		ip.addEvent(ie);
		return true;
	}

	@Override
	public boolean onTap(MotionEvent e) {
		Log.i(TAG, "Tap");
		InputEvent ie = new InputEvent("Tap");
		ie.events.add(e);
		ip.addEvent(ie);
		return true;
	}

	@Override
	public boolean onDoubleTap(MotionEvent e) {
		Log.i(TAG, "Double Tap");
		InputEvent ie = new InputEvent("DoubleTap");
		ie.events.add(e);
		ip.addEvent(ie);
		return true;
	}

}

class InputEvent {
	String type;
	float x1,x2,y1,y2;
	public InputEvent(String type) {
		this.type = type;
	}

	Queue<MotionEvent> events = new LinkedList<MotionEvent>();
}

class InputProcessor {
	Queue<InputEvent> EventQueue;
	float[] coords1 = new float[4];
	float[] coords2 = new float[4];
	public InputProcessor() {
		EventQueue = new ConcurrentLinkedQueue<InputEvent>();
	}

	public void addEvent(InputEvent ie) {
		EventQueue.add(ie);
	}

	public void process(Camera2D cam) {
		InputEvent ie = EventQueue.poll();
		while (ie != null) {
			if(ie.type.equals("Drag"))
			{
				coords1 = cam.unProject(ie.x1, ie.y1);
				float x1 = coords1[0];
				float y1 = coords1[1];
				coords2 = cam.unProject(ie.x2, ie.y2);
				float x2 = coords2[0];
				float y2 = coords2[1];
				float[] cameraxy = cam.getCameraXY();
				cam.lookAt(cameraxy[0]-(x2-x1), cameraxy[1]-(y2-y1));
				float deltay = ie.y2-ie.y1;
				float deltax = ie.x2-ie.x1;
				//Log.v("WTF", "x "+deltax+" y "+deltay);
				//cam.lookAt(cameraxy[0]+deltax, cameraxy[1]-deltay);
			}
			ie = EventQueue.poll();
		}
	}
}