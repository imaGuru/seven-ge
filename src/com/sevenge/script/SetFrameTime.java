
package com.sevenge.script;

import com.naef.jnlua.LuaState;
import com.naef.jnlua.NamedJavaFunction;
import com.sevenge.SevenGE;

public class SetFrameTime implements NamedJavaFunction {

	@Override
	public int invoke (LuaState luaState) {
		int frametime = luaState.checkInteger(1);
		SevenGE.FRAME_TIME = frametime;
		return 0;
	}

	@Override
	public String getName () {
		return "setFrameTime";
	}
}
