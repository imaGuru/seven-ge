package com.sevenge.script;

import com.naef.jnlua.LuaState;
import com.naef.jnlua.NamedJavaFunction;

public class RemoveEntity implements NamedJavaFunction {
	EngineHandles engine;

	public RemoveEntity(EngineHandles engine) {
		this.engine = engine;
	}

	@Override
	public int invoke(LuaState luaState) {
		engine.EM.removeEntity(luaState.checkInteger(1));
		luaState.pop(1);
		return 0;
	}

	@Override
	public String getName() {
		return "removeEntity";
	}
}
