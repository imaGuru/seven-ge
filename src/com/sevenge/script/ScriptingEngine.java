
package com.sevenge.script;

import java.io.IOException;

import android.util.Log;

import com.naef.jnlua.LuaRuntimeException;
import com.naef.jnlua.LuaState;
import com.naef.jnlua.LuaSyntaxException;
import com.naef.jnlua.NamedJavaFunction;
import com.sevenge.IO;
import com.sevenge.utils.FixedSizeArray;

public class ScriptingEngine {
	private LuaState mLuaState;
	private FixedSizeArray<String> scriptsBuffer = new FixedSizeArray<String>(20, null);
	private FixedSizeArray<String> scripts = new FixedSizeArray<String>(20, null);

	public ScriptingEngine (EngineHandles eh) {
		mLuaState = new LuaState();
		mLuaState.openLibs();
		mLuaState.register("SevenGE", new NamedJavaFunction[] {new SetFrameTime(), new ScriptLog(), new CreateEntity(eh),
			new RemoveEntity(eh), new GetEntity(eh), new GetCamera(eh), new SetCameraPosition(eh), new SetCameraRotation(eh),
			new SetCameraZoom(eh), new SetAudioVolume(eh), new PlaySound(eh), new PlayMusic(eh)}, true);
		try {
			mLuaState.load(IO.openAsset("Scripts/EngineAPI.lua"), "=EngineAPI", "t");
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
		synchronized (this) {
			scriptsBuffer.add(script);
		}
	}

	public void run (double delta) {
		// Prepare a function call
		synchronized (this) {
			for (int i = 0; i < scriptsBuffer.getCount(); i++)
				scripts.add(scriptsBuffer.removeLast());
		}
		try {
			for (int i = 0; i < scripts.getCount(); i++) {
				mLuaState.load(scripts.removeLast(), "=runtimeScript");
				mLuaState.call(0, 0);
			}
			mLuaState.getGlobal("wakeUpWaitingThreads"); // Push the function on the stack //
			mLuaState.pushNumber(delta);
			mLuaState.call(1, 0); // 1 arguments, 0 return
		} catch (LuaRuntimeException e) {
			Log.e("SCRIPTS", e.getLocalizedMessage());
			e.printLuaStackTrace();
		}
	}
}
