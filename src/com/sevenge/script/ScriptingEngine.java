
package com.sevenge.script;

import java.io.IOException;

import android.util.Log;

import com.naef.jnlua.LuaRuntimeException;
import com.naef.jnlua.LuaState;
import com.naef.jnlua.LuaSyntaxException;
import com.naef.jnlua.NamedJavaFunction;
import com.sevenge.IO;

public class ScriptingEngine {
	private LuaState mLuaState;

	public ScriptingEngine (EngineHandles eh) {
		mLuaState = new LuaState();
		mLuaState.openLibs();
		mLuaState.register("SevenGE", new NamedJavaFunction[] {new SetFrameTime(), new ScriptLog(), new CreateEntity(eh),
			new RemoveEntity(eh)}, true);
		try {
			mLuaState.load(IO.openAsset("Scripts/EngineAPI.lua"), "=EngineAPI", "t");
			// Evaluate the chunk, thus defining the function
			mLuaState.call(0, 0); // No arguments, no returns
			mLuaState.load(IO.openAsset("Scripts/Threading.lua"), "=Threading", "t");
			mLuaState.call(0, 0); // No arguments, no returns
		} catch (LuaSyntaxException e) {
			Log.e("SCRIPTS", e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void executeScript (String script) {
		mLuaState.load(script, "=runtimeScript");
		mLuaState.call(0, 0);
	}

	public void run (double delta) {
		// Prepare a function call
		try {
			mLuaState.getGlobal("wakeUpWaitingThreads"); // Push the function on the stack //
			mLuaState.pushNumber(delta);
			mLuaState.call(1, 0); // 1 arguments, 0 return
		} catch (LuaRuntimeException e) {
			Log.e("SCRIPTS", e.getLocalizedMessage());
			e.printLuaStackTrace();
		}
	}
}
