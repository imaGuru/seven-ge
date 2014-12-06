package com.sevenge.sample;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import android.opengl.Matrix;
import android.view.MotionEvent;

import com.sevenge.GameActivity;
import com.sevenge.GameState;
import com.sevenge.SevenGE;
import com.sevenge.assets.TextureRegion;
import com.sevenge.audio.Music;
import com.sevenge.ecs.AnimationComponent;
import com.sevenge.ecs.AnimationSystem;
import com.sevenge.ecs.CameraComponent;
import com.sevenge.ecs.CameraSystem;
import com.sevenge.ecs.Component;
import com.sevenge.ecs.Entity;
import com.sevenge.ecs.PositionComponent;
import com.sevenge.ecs.RendererSystem;
import com.sevenge.ecs.ScriptingSystem;
import com.sevenge.ecs.SpriteComponent;
import com.sevenge.input.GestureProcessor;
import com.sevenge.input.InputProcessor;
import com.sevenge.utils.Log;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

public class SampleGameState extends GameState implements InputProcessor, GestureProcessor {

  private final String TAG = "InputTest";
  static final float BOX_STEP = 1 / 60f;
  static final int BOX_VELOCITY_ITERATIONS = 6;
  static final int BOX_POSITION_ITERATIONS = 2;
  static final float WORLD_TO_BOX = 0.01f;
  static final float BOX_WORLD_TO = 100f;
  


  private RendererSystem rendererSystem;
  private CameraSystem cameraSystem;
  private List<Entity> entities;
  private int changeme = 0;
  private Component[] optimizedEntites = new Component[10000];
  private int oec = 0;
  private CameraComponent cc;
  private ScriptingSystem scriptingSystem;

  private float angle;

  private float cameraX;

  private float cameraY;

  World world;
  Body apple;
  Entity appleEntity;

  PositionComponent positionComponent;
  SpriteComponent spriteComponent;


  public SampleGameState(GameActivity gameActivity) {
    super(gameActivity);

    SevenGE.input.addInputProcessor(this);
    SevenGE.input.addGestureProcessor(this);

    SevenGE.assetManager.loadAssets("sample.pkg");

    rendererSystem = new RendererSystem();
    cameraSystem = new CameraSystem();
    scriptingSystem = new ScriptingSystem();

    world = new World(new Vector2(0, -10), true);
    

    entities = new ArrayList<Entity>();
    Random rng = new Random();

    // paseczek
    Entity bar = new Entity();
    Entity cam = new Entity();

    spriteComponent = new SpriteComponent();
    spriteComponent.scale = 1.0f;
    spriteComponent.textureRegion = (TextureRegion) SevenGE.assetManager.getAsset("buttonRed");
    positionComponent = new PositionComponent();
    positionComponent.x = 0;
    positionComponent.y = -400;

    Matrix.setIdentityM(positionComponent.scaleMatrix, 0);
    Matrix.scaleM(positionComponent.scaleMatrix, 0, spriteComponent.scale, spriteComponent.scale,
        1.0f);

    Matrix.setIdentityM(positionComponent.transform, 0);
    Matrix.translateM(positionComponent.transform, 0, spriteComponent.textureRegion.width / 2,
        spriteComponent.textureRegion.height / 2, 0f);
    Matrix.rotateM(positionComponent.transform, 0, positionComponent.rotation, 0f, 0f, 1.0f);
    Matrix.translateM(positionComponent.transform, 0, positionComponent.x, positionComponent.y, 0f);

    bar.add(positionComponent, 1);
    bar.add(spriteComponent, 2);
    
    entities.add(cam);

    optimizedEntites[oec++] = positionComponent;
    optimizedEntites[oec++] = spriteComponent;

    // ground body

    BodyDef groundBodyDef = new BodyDef();
    groundBodyDef.position.set(new Vector2(WORLD_TO_BOX * positionComponent.x, WORLD_TO_BOX * positionComponent.y));
    Body groundBody = world.createBody(groundBodyDef);
    PolygonShape groundBox = new PolygonShape();
    groundBox.setAsBox(WORLD_TO_BOX * spriteComponent.textureRegion.width / 2,
        WORLD_TO_BOX * spriteComponent.textureRegion.height / 2);
    groundBody.createFixture(groundBox, 0.0f);

    // apple

    appleEntity = new Entity();
    positionComponent = new PositionComponent();
    positionComponent.x = 0;
    positionComponent.y = 0;
    spriteComponent = new SpriteComponent();
    spriteComponent.scale = 1.0f;
    spriteComponent.textureRegion = (TextureRegion) SevenGE.assetManager.getAsset("enemyRed5");


    Matrix.setIdentityM(positionComponent.scaleMatrix, 0);
    Matrix.scaleM(positionComponent.scaleMatrix, 0, spriteComponent.scale, spriteComponent.scale,
        1.0f);

    Matrix.setIdentityM(positionComponent.transform, 0);
    Matrix.translateM(positionComponent.transform, 0, spriteComponent.textureRegion.width / 2,
        spriteComponent.textureRegion.height / 2, 0f);
    Matrix.rotateM(positionComponent.transform, 0, positionComponent.rotation, 0f, 0f, 1.0f);
    Matrix.translateM(positionComponent.transform, 0, positionComponent.x, positionComponent.y, 0f);

    appleEntity.add(positionComponent, 1);
    appleEntity.add(spriteComponent, 2);

    optimizedEntites[oec++] = positionComponent;
    optimizedEntites[oec++] = spriteComponent;

    // dynamic body

    BodyDef bodyDef = new BodyDef();
    bodyDef.type = BodyDef.BodyType.DynamicBody;
    bodyDef.position.set(WORLD_TO_BOX * positionComponent.x,WORLD_TO_BOX * positionComponent.y);
    apple = world.createBody(bodyDef);

    CircleShape dynamicCircle = new CircleShape();
    dynamicCircle.setRadius(WORLD_TO_BOX * spriteComponent.textureRegion.height / 2);
    FixtureDef fixtureDef = new FixtureDef();
    fixtureDef.shape = dynamicCircle;
    fixtureDef.density = 5.0f;
    fixtureDef.friction = 0f;
    fixtureDef.restitution = 0.5f;
    apple.createFixture(fixtureDef);
    


  }

  @Override
  public void onSurfaceChange(int width, int height) {
    Entity e = entities.get(0);
    cc = new CameraComponent();
    cc.height = height;
    cc.width = width;
    cc.scale = 0.7f;
    PositionComponent cp = new PositionComponent();
    cp.x = 0;
    cp.y = 0;
    Matrix.setLookAtM(cc.viewMatrix, 0, cp.x, cp.y, 1f, cp.x, cp.y, 0f, 0f, 1.0f, 0.0f);
    Matrix.orthoM(cc.projectionMatrix, 0, -width / cc.scale / 2, width / cc.scale / 2, -height
        / cc.scale / 2, height / cc.scale / 2, 0f, 1f);
    Matrix.multiplyMM(cc.viewProjectionMatrix, 0, cc.projectionMatrix, 0, cc.viewMatrix, 0);
    Matrix.invertM(cc.invertedVPMatrix, 0, cc.viewProjectionMatrix, 0);
    e.add(cp, 1);
    e.add(cc, 8);
    entities.add(e);
  }

  @Override
  public void update() {
    SevenGE.input.process();

    angle = (float) ((angle + 0.05f) % (Math.PI * 2));
    cameraX = (float) (Math.cos(angle) * 500);
    cameraY = (float) (Math.sin(angle) * 200);

    cameraSystem.process(entities);
    if (changeme >= 30) {
      changeme = 0;
      scriptingSystem.process(entities);
    }
    changeme++;

    world.step(BOX_STEP, BOX_VELOCITY_ITERATIONS, BOX_POSITION_ITERATIONS);

     positionComponent.x = BOX_WORLD_TO * apple.getPosition().x;
     positionComponent.y = BOX_WORLD_TO * apple.getPosition().y;

    Matrix.setIdentityM(positionComponent.scaleMatrix, 0);
    Matrix.scaleM(positionComponent.scaleMatrix, 0, spriteComponent.scale, spriteComponent.scale,
        1.0f);

    Matrix.setIdentityM(positionComponent.transform, 0);
    Matrix.translateM(positionComponent.transform, 0, spriteComponent.textureRegion.width / 2,
        spriteComponent.textureRegion.height / 2, 0f);
    Matrix.rotateM(positionComponent.transform, 0, positionComponent.rotation, 0f, 0f, 1.0f);
    Matrix.translateM(positionComponent.transform, 0, positionComponent.x, positionComponent.y, 0f);


  }

  @Override
  public void draw(float a, boolean updated) {
    rendererSystem.process(optimizedEntites, cc, oec, a, updated);
  }

  @Override
  public void dispose() {

  }

  @Override
  public void pause() {
    // music.pause();

  }

  @Override
  public void resume() {
    // music.play();

  }

  @Override
  public boolean onDoubleTap(MotionEvent me) {
    Log.d(TAG, "onDoubleTap");
    return false;
  }

  @Override
  public boolean onSingleTapConfirmed(MotionEvent arg0) {
    Log.d(TAG, "onSingleTapConfirmed");
    return false;
  }

  @Override
  public boolean onFling(MotionEvent arg0, MotionEvent arg1, float arg2, float arg3) {
    Log.d(TAG, "onFling");
    return false;
  }

  @Override
  public void onLongPress(MotionEvent arg0) {
    Log.d(TAG, "onLongPress");

  }

  @Override
  public boolean onScroll(MotionEvent arg0, MotionEvent arg1, float arg2, float arg3) {
    Log.d(TAG, "onScroll");
    return false;
  }

  @Override
  public boolean touchDown(int x, int y, int pointer, int button) {
    Log.d(TAG, "touchDown" + " x : " + x + " , y : " + y + " , pointerid : " + pointer);
    return false;
  }

  @Override
  public boolean touchUp(int x, int y, int pointer, int button) {
    Log.d(TAG, "touchUp" + " x : " + x + " , y : " + y + " , pointerid : " + pointer);
    return false;
  }

  @Override
  public boolean touchMove(int x, int y, int pointer) {
    Log.d(TAG, "touchMove" + " x : " + x + " , y : " + y + " , pointerid : " + pointer);
    return false;
  }

}
