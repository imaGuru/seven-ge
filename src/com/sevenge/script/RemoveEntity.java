
package com.sevenge.script;

import com.naef.jnlua.LuaState;
import com.naef.jnlua.NamedJavaFunction;

public class RemoveEntity implements NamedJavaFunction {
	EngineHandles engine;

	public RemoveEntity (EngineHandles engine) {
		this.engine = engine;
	}

	@Override
	public int invoke (LuaState luaState) {
		int i = luaState.checkInteger(1);
		engine.EM.removeEntity(i);
		return 0;
	}

	@Override
	public String getName () {
		return "removeEntity";
	}
}
