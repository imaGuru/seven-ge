
package com.engine.sevenge.ecs;

import java.io.IOException;
import java.util.List;

import android.util.Log;

import com.engine.sevenge.SevenGE;
import com.naef.jnlua.LuaRuntimeException;
import com.naef.jnlua.LuaState;
import com.naef.jnlua.LuaSyntaxException;
import com.naef.jnlua.LuaType;
import com.naef.jnlua.NamedJavaFunction;

public class ScriptingSystem extends System {
	private LuaState luaState;

	public ScriptingSystem () {
		luaState = new LuaState();
		luaState.openLibs();
		luaState.register("simplelol", new NamedJavaFunction[] {new DebugLog()}, true);
		try {
			luaState.load(SevenGE.io.asset("Scripts/main.lua").getInputStream(), "=main", "t");

			// Evaluate the chunk, thus defining the function
			luaState.call(0, 0); // No arguments, no returns

		} catch (LuaSyntaxException e) {
			Log.e("SCRIPTS", e.getMessage());
			e.printStackTrace();
		} catch (IOException e) { // TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	public void process (List<Entity> entities) {
		// Prepare a function call
		try {
			luaState.getGlobal("wakeUpWaitingThreads"); // Push the function on the stack //
			luaState.pushInteger(1);
			luaState.call(1, 0); // 1 arguments, 0 return
		} catch (LuaRuntimeException e) {
			Log.e("SCRIPTS", e.getLocalizedMessage());
			e.printLuaStackTrace();
		}
	}

	@Override
	public void handleMessage (Message m, Entity e) {
		// TODO Auto-generated method stub

	}

	private void stackDump () {
		int i;
		int top = luaState.getTop();
		for (i = 1; i <= top; i++) {
			LuaType t = luaState.type(i);
			Log.d("SCRIPT", t.displayText());
		}
	}

}

class DebugLog implements NamedJavaFunction {
	@Override
	public int invoke (LuaState luaState) {
// Get arguments using the check APIs; these throw exceptions with
// meaningful error messages if there is a mismatch
		String msg = luaState.checkString(2);
		String TAG = luaState.checkString(1);
		Log.d(TAG, msg);
		return 0;
	}

	@Override
	public String getName () {
		return "log";
	}
}
