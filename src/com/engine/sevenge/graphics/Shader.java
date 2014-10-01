
package com.engine.sevenge.graphics;

import static android.opengl.GLES20.GL_COMPILE_STATUS;
import static android.opengl.GLES20.glCompileShader;
import static android.opengl.GLES20.glCreateShader;
import static android.opengl.GLES20.glDeleteShader;
import static android.opengl.GLES20.glGetShaderInfoLog;
import static android.opengl.GLES20.glGetShaderiv;
import static android.opengl.GLES20.glShaderSource;

import com.engine.sevenge.assets.Asset;
import com.engine.sevenge.utils.Log;

public class Shader extends Asset {
	private static final String TAG = "Shader";
	private final int shaderObjectId;

	public Shader (String shaderCode, int type) {
		shaderObjectId = glCreateShader(type);
		if (shaderObjectId == 0) {
			Log.w(TAG, "Could not create new shader.");
			return;
		}
		glShaderSource(shaderObjectId, shaderCode);
		glCompileShader(shaderObjectId);

		final int[] compileStatus = new int[1];
		glGetShaderiv(shaderObjectId, GL_COMPILE_STATUS, compileStatus, 0);
		Log.v(TAG, "Results of compiling source:" + "\n" + shaderCode + "\n:" + glGetShaderInfoLog(shaderObjectId));
		if (compileStatus[0] == 0) {
			// If it failed, delete the shader object.
			glDeleteShader(shaderObjectId);
			Log.w(TAG, "Compilation of shader failed.");
			return;
		}
	}

	public int getGLID () {
		return shaderObjectId;
	}

	@Override
	public void dispose () {
		// TODO Auto-generated method stub

	}
}
