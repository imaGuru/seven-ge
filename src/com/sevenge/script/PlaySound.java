package com.sevenge.script;

import com.naef.jnlua.LuaState;
import com.naef.jnlua.NamedJavaFunction;
import com.sevenge.SevenGE;
import com.sevenge.audio.Sound;

public class PlaySound implements NamedJavaFunction {

	public PlaySound(EngineHandles eh) {
		// TODO Auto-generated constructor stub
	}

	@Override
	public int invoke(LuaState luaState) {
		Sound sound = (Sound) SevenGE.getAssetManager().getAsset(
				luaState.checkString(-1));
		sound.play(0.5f);
		luaState.pop(1);
		return 0;
	}

	@Override
	public String getName() {
		return "playSound";
	}

}
