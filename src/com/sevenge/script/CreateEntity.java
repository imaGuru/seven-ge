
package com.sevenge.script;

import com.naef.jnlua.LuaState;
import com.naef.jnlua.NamedJavaFunction;
import com.sevenge.SevenGE;
import com.sevenge.ecs.Entity;
import com.sevenge.ecs.PositionComponent;
import com.sevenge.ecs.SpriteComponent;
import com.sevenge.graphics.TextureRegion;

public class CreateEntity implements NamedJavaFunction {
	EngineHandles engine;

	public CreateEntity (EngineHandles eh) {
		engine = eh;
	}

	@Override
	public int invoke (LuaState luaState) {
		Entity e = engine.EM.createEntity(10);
		luaState.pushNumber(e.mId);
		PositionComponent pc = new PositionComponent();
		pc.x = 0;
		pc.y = 0;
		SpriteComponent sc = new SpriteComponent();
		sc.scale = 1;
		sc.textureRegion = (TextureRegion)SevenGE.getAssetManager().getAsset("Hull4.png");
		e.addComponent(pc, 0);
		e.addComponent(sc, 1);
		return 1;
	}

	@Override
	public String getName () {
		return "createEntity";
	}
}
