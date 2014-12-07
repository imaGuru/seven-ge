package com.sevenge.ecs;

import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;

public class PhysicsComponent extends Component {

  public static final int MASK = 1 << 4;
  static final float WORLD_TO_BOX = 0.01f;
  static final float BOX_TO_WORLD = 100f;

  private float x;
  private float y;
  // private float rotation;
  private float width;
  private float height;
  private float radius;

  public FixtureDef fixtureDef;
  public BodyDef bodyDef;

  public float getX() {
    return x * BOX_TO_WORLD;
  }

  public void setX(float x) {
    this.x = x * WORLD_TO_BOX;
  }

  public float getY() {
    return y * BOX_TO_WORLD;
  }

  public void setY(float y) {
    this.y = y * WORLD_TO_BOX;
  }

  public float getWidth() {
    return width * BOX_TO_WORLD;
  }

  public void setWidth(float width) {
    this.width = width * WORLD_TO_BOX;
  }

  public float getHeight() {
    return height * BOX_TO_WORLD;
  }

  public void setHeight(float height) {
    this.height = height * WORLD_TO_BOX;
  }

  public float getRadius() {
    return radius * BOX_TO_WORLD;
  }

  public void setRadius(float radius) {
    this.radius = radius * WORLD_TO_BOX;
  }



}
