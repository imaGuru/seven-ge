
package com.sevenge.sample;

import java.util.Random;

import android.opengl.Matrix;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.sevenge.GameState;
import com.sevenge.IO;
import com.sevenge.SevenGE;
import com.sevenge.assets.AudioLoader;
import com.sevenge.assets.ShaderLoader;
import com.sevenge.assets.SpriteSheetLoader;
import com.sevenge.assets.TextureLoader;
import com.sevenge.assets.TextureRegion;
import com.sevenge.assets.TextureShaderProgramLoader;
import com.sevenge.audio.Music;
import com.sevenge.ecs.AnimationComponent;
import com.sevenge.ecs.AnimationSystem;
import com.sevenge.ecs.CameraComponent;
import com.sevenge.ecs.CameraSystem;
import com.sevenge.ecs.Component;
import com.sevenge.ecs.Entity;
import com.sevenge.ecs.EntityManager;
import com.sevenge.ecs.PhysicsComponent;
import com.sevenge.ecs.PhysicsSystem;
import com.sevenge.ecs.PositionComponent;
import com.sevenge.ecs.RendererSystem;
import com.sevenge.ecs.ScriptingSystem;
import com.sevenge.ecs.SpriteComponent;
import com.sevenge.graphics.Camera2D;
import com.sevenge.graphics.FontUtils;
import com.sevenge.input.GestureProcessor;
import com.sevenge.input.InputProcessor;

public class SampleGameState extends GameState implements InputProcessor, GestureProcessor {

	private static final String TAG = "SampleGameState";

	private EntityManager mEM;
	private Component[] components = new Component[10];

	private Music music;

	private RendererSystem rendererSystem;

	private AnimationSystem animationSystem;

	private CameraSystem cameraSystem;

	private ScriptingSystem scriptingSystem;

	private PhysicsSystem physicsSystem;

	private int counter = 0;

	public SampleGameState () {
		SevenGE.input.addInputProcessor(this);
		SevenGE.input.addGestureProcessor(this);

		SevenGE.assetManager.addLoader("spriteSheet", new SpriteSheetLoader(SevenGE.assetManager));
		SevenGE.assetManager.addLoader("texture", new TextureLoader(SevenGE.assetManager));
		SevenGE.assetManager.addLoader("program", new TextureShaderProgramLoader(SevenGE.assetManager));
		SevenGE.assetManager.addLoader("shader", new ShaderLoader(SevenGE.assetManager));
		SevenGE.assetManager.addLoader("audio", new AudioLoader(SevenGE.assetManager));
		
	
		SevenGE.assetManager.loadAssets("sample.pkg");
		
		SevenGE.assetManager.registerAsset("font", FontUtils.load(IO.getAssetManager(), "Fonts/OpenSansBold.ttf", 20, 2, 0));
		
		
		rendererSystem = new RendererSystem(200);
		animationSystem = new AnimationSystem(200);
		cameraSystem = new CameraSystem(null);
		scriptingSystem = new ScriptingSystem();
		physicsSystem = new PhysicsSystem(200);

		mEM = new EntityManager(300, 10);
		mEM.registerSystem(rendererSystem);
		mEM.registerSystem(animationSystem);
		mEM.registerSystem(cameraSystem);
		mEM.registerSystem(scriptingSystem);
		mEM.registerSystem(physicsSystem);

		Random rng = new Random();
		for (int i = 0; i < 150; i++) {
			Entity entity = mEM.createEntity(components, 0);
			SpriteComponent cs = new SpriteComponent();
			PositionComponent cp = new PositionComponent();
			cp.rotation = rng.nextFloat() * 360.0f;
			cp.x = rng.nextFloat() * 1400f;
			cp.y = rng.nextFloat() * 1400f;
			cs.scale = 1.0f;
			float rnd = rng.nextFloat();
			if (rnd < 0.5f) {
				cs.textureRegion = (TextureRegion)SevenGE.assetManager.getAsset("meteorBrown_big1");
				cs.scale = 1f;
			} else if (rnd < 0.7f)
				cs.textureRegion = (TextureRegion)SevenGE.assetManager.getAsset("meteorBrown_small2");
			else if (rnd < 0.75f)
				cs.textureRegion = (TextureRegion)SevenGE.assetManager.getAsset("meteorBrown_tiny2");
			else {
				cs.textureRegion = (TextureRegion)SevenGE.assetManager.getAsset("enemyBlack1");
				AnimationComponent ca = new AnimationComponent();
				PhysicsComponent physicsComponent = new PhysicsComponent();

				//

				BodyDef bodyDef = new BodyDef();
				bodyDef.type = BodyDef.BodyType.DynamicBody;
				bodyDef.position.set(PhysicsSystem.WORLD_TO_BOX * cp.x, PhysicsSystem.WORLD_TO_BOX * cp.y);
				Body body = physicsSystem.getWorld().createBody(bodyDef);
				CircleShape dynamicCircle = new CircleShape();
				dynamicCircle.setRadius(cs.textureRegion.height / 2 * PhysicsSystem.WORLD_TO_BOX);
				FixtureDef fixtureDef = new FixtureDef();
				fixtureDef.shape = dynamicCircle;
				fixtureDef.density = 2.0f;
				fixtureDef.friction = 0.5f;
				fixtureDef.restitution = 0.5f;
				body.createFixture(fixtureDef);
				physicsComponent.setBody(body);

				//

				ca.frameList = new TextureRegion[] {(TextureRegion)SevenGE.assetManager.getAsset("enemyBlack1"),
					(TextureRegion)SevenGE.assetManager.getAsset("enemyBlack2"),
					(TextureRegion)SevenGE.assetManager.getAsset("enemyBlack3"),
					(TextureRegion)SevenGE.assetManager.getAsset("enemyBlack4"),
					(TextureRegion)SevenGE.assetManager.getAsset("enemyBlack5")};
				ca.durations = new int[] {500, 1000, 2000, 234, 666};
				ca.isPlaying = true;
				entity.addComponent(ca, 3);
				entity.addComponent(physicsComponent, 4);
			}

			entity.addComponent(cp, 0);
			entity.addComponent(cs, 1);

		}
		Entity e = mEM.createEntity(components, 0);
		PhysicsComponent physicsComponent = new PhysicsComponent();
		PositionComponent positionComponent = new PositionComponent();

		int groundWidth = 1100;
		int groundHeight = 1;
		int groundX = groundWidth / 2 + 140;
		int groundY = groundHeight / 2;

		positionComponent.x = groundX;
		positionComponent.y = groundY;

		groundX *= PhysicsSystem.WORLD_TO_BOX;
		groundY *= PhysicsSystem.WORLD_TO_BOX;
		groundWidth *= PhysicsSystem.WORLD_TO_BOX;
		groundHeight *= PhysicsSystem.WORLD_TO_BOX;

		BodyDef groundBodyDef = new BodyDef();
		groundBodyDef.position.set(new Vector2(groundX, groundY));
		Body groundBody = physicsSystem.getWorld().createBody(groundBodyDef);
		PolygonShape groundBox = new PolygonShape();
		groundBox.setAsBox(groundWidth, groundHeight);
		groundBody.createFixture(groundBox, 0.0f);

		physicsComponent.setBody(groundBody);

		e.addComponent(positionComponent, 0);
		// e.addComponent(spriteComponent, 1);
		e.addComponent(physicsComponent, 4);

		mEM.assignEntities();

		music = (Music)SevenGE.assetManager.getAsset("music1");
		music.setLooping(true);
		music.play();

	}

	@Override
	public void onSurfaceChange (int width, int height) {

		CameraComponent cc = new CameraComponent();
		cc.height = height;
		cc.width = width;
		cc.scaledHeight = height;
		cc.scaledWidth = width;
		cc.scale = 0.7f;

		PositionComponent cp = new PositionComponent();
		cp.x = 700;
		cp.y = 650;

		Camera2D.lookAt(cp.x, cp.y, cc.viewMatrix);
		Camera2D.setOrthoProjection(width / cc.scale, height / cc.scale, cc.projectionMatrix);
		Camera2D.getVPM(cc.viewProjectionMatrix, cc.projectionMatrix, cc.viewMatrix);
		Matrix.invertM(cc.invertedVPMatrix, 0, cc.viewProjectionMatrix, 0);

		Entity camera = mEM.createEntity(components, 0);
		camera.addComponent(cc, 2);
		camera.addComponent(cp, 0);

		cameraSystem.setCamera(camera);
		rendererSystem.setCamera(camera);
	}

	@Override
	public void update () {
		physicsSystem.process();
		cameraSystem.process();
		SevenGE.input.process();
		animationSystem.process();
		if (counter == 33) {
			counter = 0;
			scriptingSystem.process();
		}
		counter++;
		mEM.assignEntities();
	}

	@Override
	public void draw (float interpolationAlpha) {
		rendererSystem.process(interpolationAlpha);
	}

	@Override
	public void dispose () {

	}

	@Override
	public void pause () {
		music.pause();

	}

	@Override
	public void resume () {
		music.play();

	}

	@Override
	public boolean onDoubleTap (MotionEvent me) {
		// Log.d(TAG, "onDoubleTap");
		return false;
	}

	@Override
	public boolean onSingleTapConfirmed (MotionEvent arg0) {
		// Log.d(TAG, "onSingleTapConfirmed");
		return false;
	}

	@Override
	public boolean onFling (MotionEvent arg0, MotionEvent arg1, float arg2, float arg3) {
		// Log.d(TAG, "onFling");
		return false;
	}

	@Override
	public void onLongPress (MotionEvent arg0) {
		// Log.d(TAG, "onLongPress");

	}

	@Override
	public boolean onScroll (MotionEvent arg0, MotionEvent arg1, float arg2, float arg3) {
		// Log.d(TAG, "onScroll");
		return false;
	}

	@Override
	public boolean touchDown (int x, int y, int pointer, int button) {
		// Log.d(TAG, "touchDown" + " x : " + x + " , y : " + y + " , pointerid : " + pointer);
		return false;
	}

	@Override
	public boolean touchUp (int x, int y, int pointer, int button) {
		// Log.d(TAG, "touchUp" + " x : " + x + " , y : " + y + " , pointerid : " + pointer);
		return false;
	}

	@Override
	public boolean touchMove (int x, int y, int pointer) {
		// Log.d(TAG, "touchMove" + " x : " + x + " , y : " + y + " , pointerid : " + pointer);
		return false;
	}

	@Override
	public boolean onScale (ScaleGestureDetector detector) {
		// TODO Auto-generated method stub
		return false;
	}

}
