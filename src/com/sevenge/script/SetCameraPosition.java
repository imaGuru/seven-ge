package com.sevenge.script;

import com.naef.jnlua.LuaState;
import com.naef.jnlua.NamedJavaFunction;

public class SetCameraPosition implements NamedJavaFunction {
	EngineHandles engine;

	public SetCameraPosition(EngineHandles eh) {
		engine = eh;
	}

	@Override
	public int invoke(LuaState luaState) {
		engine.camera.setPostion((float) luaState.checkNumber(-1),
				(float) luaState.checkNumber(-2));
		luaState.pop(2);
		return 0;
	}

	@Override
	public String getName() {
		return "setCameraPosition";
	}

}
