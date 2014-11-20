
package com.engine.sevenge.graphics;

import static android.opengl.GLES20.GL_LINK_STATUS;
import static android.opengl.GLES20.GL_VALIDATE_STATUS;
import static android.opengl.GLES20.glAttachShader;
import static android.opengl.GLES20.glCreateProgram;
import static android.opengl.GLES20.glDeleteProgram;
import static android.opengl.GLES20.glGetProgramInfoLog;
import static android.opengl.GLES20.glGetProgramiv;
import static android.opengl.GLES20.glLinkProgram;
import static android.opengl.GLES20.glUseProgram;
import static android.opengl.GLES20.glValidateProgram;

import com.engine.sevenge.assets.Asset;
import com.engine.sevenge.utils.Log;

/** Class providing the base for every shaderProgram */
class ShaderProgram extends Asset {
	// Uniform constants
	protected static final String U_MATRIX = "u_Matrix";
	protected static final String U_TEXTURE_UNIT = "u_TextureUnit";
	// Attribute constants
	protected static final String A_POSITION = "a_Position";
	protected static final String A_COLOR = "a_Color";
	protected static final String A_TEXTURE_COORDINATES = "a_TextureCoordinates";
	protected static final String TAG = "ShaderProgram";

	protected final int programID;

	/** Creates an opengl shader program
	 * @param vs vertex shader
	 * @param fs fragment shader */
	public ShaderProgram (Shader vs, Shader fs) {
		programID = glCreateProgram();
		if (programID == 0) {
			Log.w(TAG, "Could not create new program");
		}
		glAttachShader(programID, vs.getGLID());
		glAttachShader(programID, fs.getGLID());
		glLinkProgram(programID);
		final int[] linkStatus = new int[1];
		glGetProgramiv(programID, GL_LINK_STATUS, linkStatus, 0);
		Log.v(TAG, "Results of linking program:\n" + glGetProgramInfoLog(programID));
		if (linkStatus[0] == 0) {
			// If it failed, delete the program object.
			glDeleteProgram(programID);
			Log.w(TAG, "Linking of program failed.");
		}
	}

	/** Use this shader program in opengl drawing */
	public void use () {
		glUseProgram(programID);
	}

	/** Returns opengl id of this shader program
	 * @return Opengl ID */
	public int getGLID () {
		return programID;
	}

	/** Validate the compilation of this shader program
	 * @return true if program successfully validated else false */
	public boolean validateProgram () {
		glValidateProgram(programID);
		final int[] validateStatus = new int[1];
		glGetProgramiv(programID, GL_VALIDATE_STATUS, validateStatus, 0);
		Log.v(TAG, "Results of validating program: " + validateStatus[0] + "\nLog:" + glGetProgramInfoLog(programID));
		return validateStatus[0] != 0;
	}

	@Override
	public void dispose () {
		glDeleteProgram(programID);
	}
}
