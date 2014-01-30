package com.engine.sevenge;

import static android.opengl.GLES20.glViewport;

import java.util.List;
import java.util.Random;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.opengl.GLSurfaceView.Renderer;
import android.util.FloatMath;

import com.engine.sevenge.audio.Music;
import com.engine.sevenge.graphics.Camera2D;
import com.engine.sevenge.graphics.LRenderer;
import com.engine.sevenge.graphics.Sprite;
import com.engine.sevenge.graphics.SpriteBatch;
import com.engine.sevenge.graphics.Texture2D;
import com.engine.sevenge.graphics.TextureShaderProgram;
import com.engine.sevenge.input.Input;
import com.engine.sevenge.input.Input.TouchEvent;
import com.engine.sevenge.io.IO;
import com.engine.sevenge.utils.Log;

public class GameRenderer implements Renderer {

	private static final String TAG = "GameEngine";

	private Context context;
	private long startTime = 0;
	private long dt = 0, sleepTime = 0;
	private int framesSkipped = 0;

	private static final long FRAME_TIME = 32;
	private static final int MAX_FRAME_SKIPS = 5;

	private LRenderer renderer;

	private SpriteBatch spriteBatch;
	private Camera2D camera;
	Input input;
	IO io;

	// game related
	private float camX = 0, camY = 0, scale = 0.0f;
	private float mHeight = 0, mWidth = 0;

	enum Mode {
		NONE, DRAG, ZOOM
	}

	private Mode mode = Mode.NONE;
	private float dragStartX;
	private float dragStartY;
	private float newDistance;
	private float oldDistance;
	private int[] pointersX = new int[20];
	private int[] pointersY = new int[20];
	private int midPointX;
	private int midPointY;

	GameRenderer(Context context) {
		this.context = context;
		// init gamestates
		input = SevenGE.input;
		io = SevenGE.io;
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
		update();

		// gamestate.draw
		spriteBatch.setVPMatrix(camera.getViewProjectionMatrix());
		renderer.addToRender(spriteBatch);
		renderer.render();
	}

	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height) {
		glViewport(0, 0, width, height);
		mWidth = width;
		mHeight = height;
		camX = -width / 2;
		camY = -height / 2;
		camera.setProjectionOrtho(width, height);
	}

	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		SevenGE.resourceManager.loadAssets(SevenGE.io.asset("sample.pkg"));
		Music music = (Music) SevenGE.resourceManager.getAsset("music1");
		music.setLooping(true);
		music.play();
		renderer = new LRenderer();
		camera = new Camera2D();
		camera.lookAt(camX, camY);
		TextureShaderProgram tsp = (TextureShaderProgram) SevenGE.resourceManager
				.getAsset("spriteShader");
		Texture2D tex = (Texture2D) SevenGE.resourceManager
				.getAsset("apple");
		spriteBatch = new SpriteBatch(tex, tsp, 1000);
		Random rng = new Random();
		for (int i = 0; i < 500; i++) {
			Sprite sprite = (Sprite) SevenGE.resourceManager
					.getAsset("appleSprite");
			sprite.rotate(rng.nextFloat() * 6.28f);
			sprite.translate(rng.nextFloat() * 2000f - 1000f,
					rng.nextFloat() * 2000f - 1000f);
			spriteBatch.add(sprite.getTransformedVertices());
		}
		spriteBatch.upload();
	}
	

	public void update() {

		List<TouchEvent> touchEvents = input.getTouchEvents();

		for (TouchEvent touchEvent : touchEvents) {
			switch (touchEvent.type) {
			case DOWN:
				
				if(touchEvent.pointerID == 0){

				mode = Mode.DRAG;
				dragStartX = touchEvent.x;
				dragStartY = touchEvent.y;
				float[] coords = camera.unProject(touchEvent.x, touchEvent.y);
				camera.lookAt(coords[0], coords[1]);
				pointersX[touchEvent.pointerID] = touchEvent.x;
				pointersY[touchEvent.pointerID] = touchEvent.y;

				}
				
				// second finger
				if (touchEvent.pointerID == 1) {

					oldDistance = getSpacing();
					midPointX = getMidpointX();
					midPointY = getMidpointY();
					if (oldDistance > 10f) {
						mode = Mode.ZOOM;
					}

				}

				break;
			case UP:

				if (mode == Mode.ZOOM && touchEvent.pointerID < 2) {
					oldDistance = 0;
					mode = Mode.NONE;
				} else if (mode == Mode.DRAG) {
					mode = Mode.NONE;
				}

				break;
			case MOVE:
				if (mode == Mode.DRAG && touchEvent.pointerID < 2) {
					float distanceX = touchEvent.x - dragStartX;
					float distanceY = touchEvent.y - dragStartY;
					// float newX = (camX - distanceX);
					// float newY = (camY - distanceY);

					// camX = newX;
					// camY = newY;

					// dragStartX = touchEvent.x;
					// dragStartY = touchEvent.y;

				} else if (mode == Mode.ZOOM && touchEvent.pointerID < 2) {
					pointersX[touchEvent.pointerID] = touchEvent.x;
					pointersY[touchEvent.pointerID] = touchEvent.y;

					newDistance = getSpacing();

					if (newDistance > 10f) {

						float offset =  oldDistance/newDistance;
						Log.d("TEST", offset + "");

						camera.zoom(offset);
						newDistance = oldDistance;
					}

				}
				break;
			default:
				break;
			}
		}
	}

	private float getSpacing() {
		float x = pointersX[0] - pointersX[1];
		float y = pointersY[0] - pointersY[1];
		return FloatMath.sqrt(x * x + y * y);
	}

	private int getMidpointX() {
		float x = pointersX[0] + pointersX[1];
		return (int) (x / 2);
	}

	private int getMidpointY() {
		float y = pointersY[0] + pointersY[1];
		return (int) (y / 2);
	}

}
