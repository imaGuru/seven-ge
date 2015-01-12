
package com.sevenge.script;

import com.naef.jnlua.LuaState;
import com.naef.jnlua.NamedJavaFunction;
import com.sevenge.utils.Vector2;

public class GetCamera implements NamedJavaFunction {
	EngineHandles engine;

	public GetCamera (EngineHandles eh) {
		engine = eh;
	}

	@Override
	public int invoke (LuaState luaState) {
		Vector2 position = engine.camera.getPosition();
		luaState.pushNumber(position.x);
		luaState.pushNumber(position.y);
		luaState.pushNumber(engine.camera.getZoom());
		luaState.pushNumber(engine.camera.getRotation());
		return 4;
	}

	@Override
	public String getName () {
		return "getCamera";
	}

}
