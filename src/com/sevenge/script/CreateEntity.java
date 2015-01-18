
package com.sevenge.script;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.naef.jnlua.LuaState;
import com.naef.jnlua.NamedJavaFunction;
import com.sevenge.SevenGE;
import com.sevenge.ecs.Entity;
import com.sevenge.ecs.PhysicsComponent;
import com.sevenge.ecs.PhysicsSystem;
import com.sevenge.ecs.PositionComponent;
import com.sevenge.ecs.SpriteComponent;
import com.sevenge.graphics.TextureRegion;
import com.sevenge.utils.DebugLog;

public class CreateEntity implements NamedJavaFunction {
	EngineHandles engine;

	public CreateEntity (EngineHandles eh) {
		engine = eh;
	}

	@Override
	public int invoke (LuaState luaState) {
		Entity e = engine.EM.createEntity(10);
		luaState.pushNil();
		float x = 0, y = 0, rotation = 0, radius = 0, scale = 1;
		while (luaState.next(-2)) {
			int position = luaState.checkInteger(-2) - 1;
			switch (position) {
			case 0:
				PositionComponent pc = new PositionComponent();
				luaState.pushNil();
				luaState.next(-2);
				pc.x = (float)luaState.checkNumber(-1);
				x = pc.x;
				luaState.pop(1);
				luaState.next(-2);
				pc.y = (float)luaState.checkNumber(-1);
				y = pc.y;
				luaState.pop(1);
				luaState.next(-2);
				pc.rotation = (float)luaState.checkNumber(-1);
				rotation = pc.rotation;
				luaState.pop(1);
				luaState.next(-2);
				e.addComponent(pc, position);
				break;
			case 1:
				SpriteComponent sc = new SpriteComponent();
				luaState.pushNil();
				luaState.next(-2);
				sc.scale = (float)luaState.checkNumber(-1);
				scale = sc.scale;
				luaState.pop(1);
				luaState.next(-2);
				String textureRegion = luaState.checkString(-1);
				sc.textureRegion = (TextureRegion)SevenGE.getAssetManager().getAsset(textureRegion);
				if (sc.textureRegion != null) {
					radius = sc.textureRegion.height / 2 * scale;
					luaState.pop(1);
					luaState.next(-2);
					e.addComponent(sc, position);
				}
				break;
			case 4:
				BodyDef bodyDef = new BodyDef();
				bodyDef.type = BodyDef.BodyType.DynamicBody;
				bodyDef.position.set(PhysicsSystem.WORLD_TO_BOX * x, PhysicsSystem.WORLD_TO_BOX * y);
				Body body = engine.ps.getWorld().createBody(bodyDef);
				CircleShape dynamicCircle = new CircleShape();
				dynamicCircle.setRadius(radius * PhysicsSystem.WORLD_TO_BOX);
				body.setTransform(body.getPosition(), 90 - rotation);
				FixtureDef fixtureDef = new FixtureDef();
				fixtureDef.shape = dynamicCircle;
				luaState.pushNil();
				luaState.next(-2);
				fixtureDef.density = (float)luaState.checkNumber(-1);
				DebugLog.d("SCRIPTS", "" + fixtureDef.density);
				luaState.pop(1);
				luaState.next(-2);
				fixtureDef.friction = (float)luaState.checkNumber(-1);
				DebugLog.d("SCRIPTS", "" + fixtureDef.friction);
				luaState.pop(1);
				luaState.next(-2);
				fixtureDef.restitution = (float)luaState.checkNumber(-1);
				DebugLog.d("SCRIPTS", "" + fixtureDef.restitution);
				luaState.pop(1);
				luaState.next(-2);
				body.createFixture(fixtureDef);
				PhysicsComponent phc = new PhysicsComponent();
				phc.setBody(body);
				body.setLinearDamping((float)0.1);
				body.setAngularDamping((float)0.2);
				body.setUserData(e);
				e.addComponent(phc, 4);
				break;
			}
			luaState.pop(1);
		}
		luaState.pop(1);
		luaState.pushNumber(e.mId);
		return 1;
	}

	@Override
	public String getName () {
		return "createEntity";
	}
}
