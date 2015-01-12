
package com.sevenge.script;

import com.naef.jnlua.LuaState;
import com.naef.jnlua.NamedJavaFunction;
import com.sevenge.utils.DebugLog;

class ScriptLog implements NamedJavaFunction {
	@Override
	public int invoke (LuaState luaState) {
// Get arguments using the check APIs; these throw exceptions with
// meaningful error messages if there is a mismatch
		String msg = luaState.checkString(2);
		String TAG = luaState.checkString(1);
		DebugLog.d(TAG, msg);
		luaState.pop(2);
		return 0;
	}

	@Override
	public String getName () {
		return "log";
	}
}
