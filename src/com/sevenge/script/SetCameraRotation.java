package com.sevenge.script;

import com.naef.jnlua.LuaState;
import com.naef.jnlua.NamedJavaFunction;

public class SetCameraRotation implements NamedJavaFunction {
	EngineHandles engine;

	public SetCameraRotation(EngineHandles eh) {
		engine = eh;
	}

	@Override
	public int invoke(LuaState luaState) {
		engine.camera.setRotation((float) luaState.checkNumber(-1));
		luaState.pop(1);
		return 0;
	}

	@Override
	public String getName() {
		return "setCameraRotation";
	}

}
