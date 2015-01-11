
package com.sevenge.sample;

import static android.opengl.GLES20.GL_BLEND;
import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES20.GL_ONE;
import static android.opengl.GLES20.GL_ONE_MINUS_SRC_ALPHA;
import static android.opengl.GLES20.glBlendFunc;
import static android.opengl.GLES20.glClear;
import static android.opengl.GLES20.glEnable;
import static android.opengl.Matrix.orthoM;

import java.util.Random;

import android.graphics.Color;
import android.view.MotionEvent;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.sevenge.ActionManager;
import com.sevenge.GameState;
import com.sevenge.SevenGE;
import com.sevenge.assets.AssetManager;
import com.sevenge.assets.AudioLoader;
import com.sevenge.assets.ShaderLoader;
import com.sevenge.assets.SpriteSheetFTLoader;
import com.sevenge.assets.Texture;
import com.sevenge.assets.TextureLoader;
import com.sevenge.assets.TextureShaderProgramLoader;
import com.sevenge.audio.Music;
import com.sevenge.audio.Sound;
import com.sevenge.ecs.AnimationSystem;
import com.sevenge.ecs.Entity;
import com.sevenge.ecs.EntityManager;
import com.sevenge.ecs.PhysicsComponent;
import com.sevenge.ecs.PhysicsSystem;
import com.sevenge.ecs.PositionComponent;
import com.sevenge.ecs.RendererSystem;
import com.sevenge.ecs.ScriptingSystem;
import com.sevenge.ecs.SpriteComponent;
import com.sevenge.graphics.Camera;
import com.sevenge.graphics.Emitter;
import com.sevenge.graphics.ParticleSystem;
import com.sevenge.graphics.ShaderUtils;
import com.sevenge.graphics.Sprite;
import com.sevenge.graphics.SpriteBatch;
import com.sevenge.graphics.SpriteBatcher;
import com.sevenge.graphics.TextureRegion;
import com.sevenge.input.GestureProcessor;
import com.sevenge.input.Input;
import com.sevenge.input.InputProcessor;
import com.sevenge.utils.DebugLog;
import com.sevenge.utils.FixedSizeArray;
import com.sevenge.utils.Vector3;

public class SampleGameState extends GameState implements InputProcessor, GestureProcessor {

	private static final String TAG = "SampleGameState";

	private EntityManager mEM;
	private Music music;
	private Music engineMusic;
	private Sound laserSfx;

	private RendererSystem rendererSystem;
	private AnimationSystem animationSystem;
	private ScriptingSystem scriptingSystem;
	private PhysicsSystem physicsSystem;
	private SceneManager sceneManager;

	private Entity eSpaceShip;
	private SpriteComponent scSpaceShip;
	private PositionComponent pcSpaceShip;

	private int counter = 0;
	private Camera camera;
	private float lastScale = 1;
	private float firstSpan;
	private AssetManager assetManager;
	private Input input;
	private Sprite[] controls;
	private boolean isAccelerating = false;
	private int acceleratingPointer = -1;
	private boolean isRotating = false;
	private int rotatingPointer = -1;
	private int rotationX;
	private int rotationY;
	private ActionManager actionManager;
	private PhysicsComponent fcSpaceShip;
	private float spaceShipAngle;
	private Vector3 position = new Vector3(0, 0, 0);;
	private Vector3 direction = new Vector3(0, 0, 0);

	SpriteBatcher mSpriteBatch = new SpriteBatcher(500);

	private float[] projectionMatrix = new float[16];
	private FixedSizeArray<Layer> maplayers;

	private float[] matrix = new float[16];

	private ParticleSystem particleSystem;

	private Emitter emiter;

	private long globalStartTime;

	@Override
	public void load () {
		globalStartTime = System.nanoTime();

		actionManager = new ActionManager();
		assetManager = SevenGE.getAssetManager();
		registerInput();
		createSystems();
		createLoaders();
		loadPlayer();
		createCamera();
		loadAssets();
		loadControls();
		generateRandomPlanets();

		Texture tex = (Texture)SevenGE.getAssetManager().getAsset("particle");
		particleSystem = new ParticleSystem(400, ShaderUtils.PARTICLE_SHADER, tex.glID);
		emiter = new Emitter(particleSystem, Color.rgb(5, 50, 255), 30, 2);

		mEM.assignEntities();

	}

	private void loadAssets () {
		music = (Music)assetManager.getAsset("music1");
		engineMusic = (Music)assetManager.getAsset("engine");
		laserSfx = (Sound)assetManager.getAsset("laser");
		MapLoader maploader = new MapLoader();
		maploader.load("map.json");
		maplayers = maploader.getLayers();

		music.setLooping(true);
		engineMusic.setLooping(true);
	}

	private void loadPlayer () {
		eSpaceShip = mEM.createEntity(10);
		scSpaceShip = new SpriteComponent();
		pcSpaceShip = new PositionComponent();
		fcSpaceShip = new PhysicsComponent();
		pcSpaceShip.x = 0;
		pcSpaceShip.y = 0;

		scSpaceShip.scale = 1.0f;
		scSpaceShip.textureRegion = (TextureRegion)assetManager.getAsset("Hull4.png");

		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyDef.BodyType.DynamicBody;
		bodyDef.position.set(PhysicsSystem.WORLD_TO_BOX * pcSpaceShip.x, PhysicsSystem.WORLD_TO_BOX * pcSpaceShip.y);
		Body body = physicsSystem.getWorld().createBody(bodyDef);
		CircleShape dynamicCircle = new CircleShape();
		dynamicCircle.setRadius(scSpaceShip.textureRegion.height / 2 * PhysicsSystem.WORLD_TO_BOX);
		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = dynamicCircle;
		fixtureDef.density = 100.0f;
		fixtureDef.friction = 0.5f;
		fixtureDef.restitution = 0.5f;
		body.createFixture(fixtureDef);
		fcSpaceShip.setBody(body);
		body.setLinearDamping((float)0.1);
		body.setAngularDamping((float)0.2);
		eSpaceShip.addComponent(fcSpaceShip, 4);

		eSpaceShip.addComponent(pcSpaceShip, 0);
		eSpaceShip.addComponent(scSpaceShip, 1);

	}

	private void loadControls () {
		controls = new Sprite[3];

		Sprite sMoveButton = new Sprite((TextureRegion)assetManager.getAsset("shadedLight07.png"));
		sMoveButton.setPosition(40, 40);
		sMoveButton.setScale(1, 1);

		Sprite sAccelerateButton = new Sprite((TextureRegion)assetManager.getAsset("shadedLight00.png"));
		sAccelerateButton.setPosition(SevenGE.getWidth() - 140, 40);
		sAccelerateButton.setScale(1, 1);

		Sprite sFireButton = new Sprite((TextureRegion)assetManager.getAsset("shadedLight46.png"));
		sFireButton.setPosition(SevenGE.getWidth() - 100, 160);
		sFireButton.setScale(1, 1);

		controls[0] = sFireButton;
		controls[1] = sMoveButton;
		controls[2] = sAccelerateButton;

	}

	private void registerInput () {

		input = SevenGE.getInput();
		input.addInputProcessor(this);
		input.addGestureProcessor(this);
	}

	private void createLoaders () {

		assetManager.addLoader("spriteSheet", new SpriteSheetFTLoader(assetManager));
		assetManager.addLoader("texture", new TextureLoader(assetManager));
		assetManager.addLoader("program", new TextureShaderProgramLoader(assetManager));
		assetManager.addLoader("shader", new ShaderLoader(assetManager));
		assetManager.addLoader("audio", new AudioLoader(assetManager));
		assetManager.loadAssets("package.pkg");

	}

	private void createSystems () {

		rendererSystem = new RendererSystem(500);
		animationSystem = new AnimationSystem(200);
		scriptingSystem = new ScriptingSystem();
		physicsSystem = new PhysicsSystem(500);
		sceneManager = new SceneManager();

		mEM = new EntityManager(500, 10);
		mEM.registerSystem(rendererSystem);
		mEM.registerSystem(animationSystem);
		mEM.registerSystem(scriptingSystem);
		mEM.registerSystem(physicsSystem);

	}

	private void createCamera () {

		camera = new Camera(SevenGE.getWidth(), SevenGE.getHeight());
		camera.setPostion(pcSpaceShip.x, pcSpaceShip.y);
		camera.setRotation(0.0f);
		camera.setZoom(0.5f);
		rendererSystem.setCamera(camera);
	}

	private void generateRandomPlanets () {

		Random rng = new Random();
		for (int i = 0; i < 400; i++) {
			Entity entity = mEM.createEntity(10);
			SpriteComponent cs = new SpriteComponent();
			PositionComponent cp = new PositionComponent();
			cp.rotation = rng.nextFloat() * 360.0f;
			cp.x = rng.nextFloat() * 8000f - 4000f;
			cp.y = rng.nextFloat() * 8000f - 4000f;
			cs.scale = 1.0f;
			int rnd = rng.nextInt(24) + 1;

			int count = 0;

			cs.textureRegion = (TextureRegion)assetManager.getAsset("a" + rnd);
			if (cs.textureRegion == null) {
				DebugLog.d("POOL", rnd + ", count :" + count);
				count += 1;
			}

			PhysicsComponent physicsComponent = new PhysicsComponent();
			BodyDef bodyDef = new BodyDef();
			bodyDef.type = BodyDef.BodyType.DynamicBody;
			bodyDef.position.set(PhysicsSystem.WORLD_TO_BOX * cp.x, PhysicsSystem.WORLD_TO_BOX * cp.y);
			Body body = physicsSystem.getWorld().createBody(bodyDef);
			body.setAngularDamping(0.1f);
			body.setLinearDamping(0.1f);
			CircleShape dynamicCircle = new CircleShape();
			dynamicCircle.setRadius(cs.textureRegion.height / 2 * PhysicsSystem.WORLD_TO_BOX);
			FixtureDef fixtureDef = new FixtureDef();
			fixtureDef.shape = dynamicCircle;
			fixtureDef.density = cs.textureRegion.height * 0.8f;
			fixtureDef.friction = 0.5f;
			fixtureDef.restitution = 0.5f;
			body.createFixture(fixtureDef);
			physicsComponent.setBody(body);

			entity.addComponent(physicsComponent, 4);
			entity.addComponent(cp, 0);
			entity.addComponent(cs, 1);

		}
	}

	@Override
	public void update () {
		physicsSystem.process();
		input.process();
		actionManager.process();

		if (isRotating) {
			float angle = (float)Math.toRadians(spaceShipAngle);
			fcSpaceShip.getBody().setTransform(fcSpaceShip.getBody().getPosition(), angle);
		}

		if (isAccelerating) {

			Vector2 force = new Vector2(10000, 0);
			float angle = fcSpaceShip.getBody().getAngle();// (float)Math.toRadians(spaceShipAngle);
			float X = (float)(force.x * Math.cos(angle) - force.y * Math.sin(angle));
			float Y = (float)(force.y * Math.cos(angle) + force.x * Math.sin(angle));

			fcSpaceShip.getBody().applyForce(new Vector2(X, Y), fcSpaceShip.getBody().getPosition());
			float length = (float)Math.sqrt((X * X) + (Y * Y));
			direction.x = -X / length * 90;
			direction.y = -Y / length * 90;
			direction.z = 0;
			position.x = pcSpaceShip.x + direction.x;
			position.y = pcSpaceShip.y + direction.y;
			position.z = 0;
			float currentTime = (System.nanoTime() - globalStartTime) / 1000000000f;
			emiter.addParticles(position, direction, currentTime, 5);
		}

		animationSystem.process();
		if (counter == 33) {
			counter = 0;
			scriptingSystem.process();
		}
		counter++;
		mEM.assignEntities();
		sceneManager.update();
		camera.setPostion(pcSpaceShip.x, pcSpaceShip.y);
	}

	@Override
	public void draw (float interpolationAlpha) {
		glClear(GL_COLOR_BUFFER_BIT);
		glEnable(GL_BLEND);
		glBlendFunc(GL_ONE, GL_ONE_MINUS_SRC_ALPHA);
		for (int i = 0; i < maplayers.getCount(); i++) {
			Layer layer = maplayers.get(i);
			for (int j = 0; j < layer.batches.getCount(); j++) {
				SpriteBatch sb = layer.batches.get(j);
				sb.setProjection(camera.getCameraMatrix(layer.parallaxFactor, matrix));
				sb.draw();
			}
		}
		float currentTime = (System.nanoTime() - globalStartTime) / 1000000000f;
		rendererSystem.process(interpolationAlpha);
		particleSystem.draw(camera.getCameraMatrix(), currentTime);
		orthoM(projectionMatrix, 0, 0, SevenGE.getWidth(), 0, SevenGE.getHeight(), -1f, 1f);
		mSpriteBatch.begin();
		mSpriteBatch.setProjection(projectionMatrix);

		mSpriteBatch.drawSprite(controls[0]);
		if (isRotating) mSpriteBatch.drawSprite(controls[1]);
		mSpriteBatch.drawSprite(controls[2]);
		mSpriteBatch.setProjection(camera.getCameraMatrix());
		mSpriteBatch.end();

	}

	@Override
	public void dispose () {
		input.removeInputProcessor(this);
		input.removeGestureProcessor(this);
	}

	@Override
	public void pause () {
		music.pause();

	}

	@Override
	public void resume () {
		// music.play();

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
		// SevenGE.getStateManager().setCurrentState(new SampleGameState());

// float[] coords = camera.unproject((int)arg0.getX(), (int)arg0.getY(), SevenGE.getWidth(), SevenGE.getHeight(),
// camera.getCameraMatrix());
//
// actionManager.add(new Move(eSpaceShip, new Vector2(coords[0], coords[1])));

	}

	@Override
	public boolean onScroll (MotionEvent me1, MotionEvent me2, float distX, float distY) {
// float me2x = me2.getX();
// float me2y = me2.getY();
//
// float[] coords = camera.unproject((int)(me2x + distX), (int)(me2y + distY), SevenGE.getWidth(), SevenGE.getHeight(),
// camera.getCameraMatrix());
// float x1 = coords[0];
// float y1 = coords[1];
// coords = camera.unproject((int)me2x, (int)me2y, SevenGE.getWidth(), SevenGE.getHeight(), camera.getCameraMatrix());
// float x2 = coords[0];
// float y2 = coords[1];
//
// com.sevenge.utils.Vector2 camPos = camera.getPosition();
// camPos.x -= x2 - x1;
// camPos.y -= y2 - y1;
// camera.setPostion(camPos.x, camPos.y);
		return false;
	}

	public boolean isButtonClicked (Sprite button, int x, int y) {

		boolean isClicked = false;

		float buttonX = button.getAxisAlignedBoundingBox().x;
		float buttonY = button.getAxisAlignedBoundingBox().y;
		float buttonWidth = button.getAxisAlignedBoundingBox().width;
		float buttonHeight = button.getAxisAlignedBoundingBox().height;

		if (x > buttonX && x < buttonX + buttonWidth && y > buttonY && y < buttonY + buttonHeight) {
			isClicked = true;
		}

		return isClicked;

	}

	@Override
	public boolean touchDown (int x, int y, int pointer, int button) {

		y = SevenGE.getHeight() - y;

		if (x > 0 && x < SevenGE.getWidth() / 2 && y > 0 && y < SevenGE.getHeight()) {
			controls[1].setCenter(x, y);
			isRotating = true;
			rotatingPointer = pointer;
			rotationX = x;
			rotationY = y;
		}

		if (isButtonClicked(controls[0], x, y)) {
			DebugLog.d("Controls", "clicked");
			laserSfx.play(1f);
		}
		if (isButtonClicked(controls[2], x, y)) {
			isAccelerating = true;
			acceleratingPointer = pointer;
			engineMusic.play();
		}

		return false;
	}

	@Override
	public boolean touchUp (int x, int y, int pointer, int button) {

		if (pointer == acceleratingPointer) {
			isAccelerating = false;
			engineMusic.stop();
			acceleratingPointer = -1;
		}
		if (pointer == rotatingPointer) {
			isRotating = false;
			rotatingPointer = -1;

		}

		return false;
	}

	@Override
	public boolean touchMove (int x, int y, int pointer) {

		y = SevenGE.getHeight() - y;

		if (isRotating && rotatingPointer == pointer) {

			float buttonY = controls[1].getY() + controls[1].getAxisAlignedBoundingBox().height / 2;
			float buttonX = controls[1].getX() + controls[1].getAxisAlignedBoundingBox().width / 2;

			double angle = Math.toDegrees(Math.atan2(y - buttonY, x - buttonX));
// angle = (angle + 360) % 360;
			// DebugLog.d("touchDown", "x : " + x + " y : " + y);
			// DebugLog.d("touchDown", "angle : " + angle);

			spaceShipAngle = (float)(angle);

		}

		return false;
	}

	@Override
	public boolean onScale (float currentSpan) {
// float scale = lastScale * firstSpan / currentSpan;
// camera.setZoom(Math.min(5.0f, Math.max(0.1f, scale)));
		return true;
	}

	@Override
	public void onScaleEnd (float currentSpan) {
	}

	@Override
	public void onScaleBegin (float currentSpan) {
		lastScale = camera.getZoom();
		firstSpan = currentSpan;
	}

}
