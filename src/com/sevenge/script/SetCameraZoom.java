
package com.sevenge.script;

import com.naef.jnlua.LuaState;
import com.naef.jnlua.NamedJavaFunction;

public class SetCameraZoom implements NamedJavaFunction {

	EngineHandles engine;

	public SetCameraZoom (EngineHandles eh) {
		engine = eh;
	}

	@Override
	public int invoke (LuaState luaState) {
		engine.camera.setZoom((float)luaState.checkNumber(-1));
		luaState.pop(1);
		return 0;
	}

	@Override
	public String getName () {
		return "setCameraZoom";
	}

}
