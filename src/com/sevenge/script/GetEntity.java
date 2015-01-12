
package com.sevenge.script;

import com.naef.jnlua.LuaState;
import com.naef.jnlua.NamedJavaFunction;

public class GetEntity implements NamedJavaFunction {

	public GetEntity (EngineHandles eh) {
		// TODO Auto-generated constructor stub
	}

	@Override
	public int invoke (LuaState luaState) {
		return 0;
	}

	@Override
	public String getName () {
		return "getEntity";
	}

}
