
package com.sevenge.graphics;

import static android.opengl.GLES20.GL_COMPILE_STATUS;
import static android.opengl.GLES20.GL_LINK_STATUS;
import static android.opengl.GLES20.GL_VALIDATE_STATUS;
import static android.opengl.GLES20.glAttachShader;
import static android.opengl.GLES20.glCompileShader;
import static android.opengl.GLES20.glCreateProgram;
import static android.opengl.GLES20.glCreateShader;
import static android.opengl.GLES20.glDeleteProgram;
import static android.opengl.GLES20.glDeleteShader;
import static android.opengl.GLES20.glGetProgramInfoLog;
import static android.opengl.GLES20.glGetProgramiv;
import static android.opengl.GLES20.glGetShaderInfoLog;
import static android.opengl.GLES20.glGetShaderiv;
import static android.opengl.GLES20.glLinkProgram;
import static android.opengl.GLES20.glShaderSource;
import static android.opengl.GLES20.glValidateProgram;

import com.sevenge.utils.Log;

/** Class creating opengl shader and holding reference to opengl object */
public class ShaderUtils {
	private static final String TAG = "Shader";

	/** Creates shader of specified type (vertex, fragment)
	 * @param shaderCode string with shader code to compile
	 * @param type of the shader (vertex, fragment) */
	public static int compileShader (String shaderCode, int type) {
		int shaderObjectId = glCreateShader(type);
		if (shaderObjectId == 0) {
			Log.w(TAG, "Could not create new shader.");
			return shaderObjectId;
		}
		glShaderSource(shaderObjectId, shaderCode);
		glCompileShader(shaderObjectId);

		final int[] compileStatus = new int[1];
		glGetShaderiv(shaderObjectId, GL_COMPILE_STATUS, compileStatus, 0);
		Log.v(TAG, "Results of compiling source:" + "\n" + shaderCode + "\n:" + glGetShaderInfoLog(shaderObjectId));
		if (compileStatus[0] == 0) {
			// If it failed, delete the shader object.
			glDeleteShader(shaderObjectId);
			shaderObjectId = 0;
			Log.w(TAG, "Compilation of shader failed.");
			return shaderObjectId;
		}
		return shaderObjectId;
	}

	public static int linkShaderProgram (int vs, int fs) {
		int programID = glCreateProgram();
		if (programID == 0) {
			Log.w(TAG, "Could not create new program");
		}
		glAttachShader(programID, vs);
		glAttachShader(programID, fs);
		glLinkProgram(programID);
		final int[] linkStatus = new int[1];
		glGetProgramiv(programID, GL_LINK_STATUS, linkStatus, 0);
		Log.v(TAG, "Results of linking program:\n" + glGetProgramInfoLog(programID));
		if (linkStatus[0] == 0) {
			// If it failed, delete the program object.
			glDeleteProgram(programID);
			Log.w(TAG, "Linking of program failed.");
		}
		return programID;
	}

	public static boolean validateProgram (int programID) {
		glValidateProgram(programID);
		final int[] validateStatus = new int[1];
		glGetProgramiv(programID, GL_VALIDATE_STATUS, validateStatus, 0);
		Log.v(TAG, "Results of validating program: " + validateStatus[0] + "\nLog:" + glGetProgramInfoLog(programID));
		return validateStatus[0] != 0;
	}
}
