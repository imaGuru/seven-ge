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

import com.engine.sevenge.utils.Log;

public class ShaderProgram {
	// Uniform constants
	protected static final String U_MATRIX = "u_Matrix";
	protected static final String U_TEXTURE_UNIT = "u_TextureUnit";
	// Attribute constants
	protected static final String A_POSITION = "a_Position";
	protected static final String A_COLOR = "a_Color";
	protected static final String A_TEXTURE_COORDINATES = "a_TextureCoordinates";
	protected static final String TAG = "ShaderProgram";

	protected final int programID;

	public ShaderProgram(int vertexShaderID, int fragmentShaderID) {
		programID = glCreateProgram();
		if (programID == 0) {
			Log.w(TAG, "Could not create new program");
		}
		glAttachShader(programID, vertexShaderID);
		glAttachShader(programID, fragmentShaderID);
		glLinkProgram(programID);
		final int[] linkStatus = new int[1];
		glGetProgramiv(programID, GL_LINK_STATUS, linkStatus, 0);
		Log.v(TAG, "Results of linking program:\n"
				+ glGetProgramInfoLog(programID));
		if (linkStatus[0] == 0) {
			// If it failed, delete the program object.
			glDeleteProgram(programID);
			Log.w(TAG, "Linking of program failed.");
		}
	}

	public void use() {
		glUseProgram(programID);
	}

	public int getGLID() {
		return programID;
	}

	public boolean validateProgram() {
		glValidateProgram(programID);
		final int[] validateStatus = new int[1];
		glGetProgramiv(programID, GL_VALIDATE_STATUS, validateStatus, 0);
		Log.v(TAG, "Results of validating program: " + validateStatus[0]
				+ "\nLog:" + glGetProgramInfoLog(programID));
		return validateStatus[0] != 0;
	}
}
