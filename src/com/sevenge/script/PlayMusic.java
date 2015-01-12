
package com.sevenge.script;

import com.naef.jnlua.LuaState;
import com.naef.jnlua.NamedJavaFunction;
import com.sevenge.SevenGE;
import com.sevenge.audio.Music;

public class PlayMusic implements NamedJavaFunction {

	public PlayMusic (EngineHandles eh) {
		// TODO Auto-generated constructor stub
	}

	@Override
	public int invoke (LuaState luaState) {
		String asset = luaState.checkString(-1);
		Music music = (Music)SevenGE.getAssetManager().getAsset(asset);
		music.play();
		luaState.pop(1);
		return 0;
	}

	@Override
	public String getName () {
		return "playMusic";
	}

}
