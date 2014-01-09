package com.engine.sevenge.graphics;

import static android.opengl.GLES20.glGetAttribLocation;
import static android.opengl.GLES20.glGetUniformLocation;
import static android.opengl.GLES20.glUniformMatrix4fv;

public class ColorShaderProgram extends ShaderProgram {
	// Uniform locations
	private final int uMatrixLocation;
	// Attribute locations
	private final int aPositionLocation;
	private final int aColorLocation;

	public ColorShaderProgram(int vertexShaderID, int fragmentShaderID) {
		super(vertexShaderID, fragmentShaderID);
		// Retrieve uniform locations for the shader program.
		uMatrixLocation = glGetUniformLocation(programID, U_MATRIX);
		// Retrieve attribute locations for the shader program.
		aPositionLocation = glGetAttribLocation(programID, A_POSITION);
		aColorLocation = glGetAttribLocation(programID, A_COLOR);
	}

	public void setUniforms(float[] matrix) {
		// Pass the matrix into the shader program.
		glUniformMatrix4fv(uMatrixLocation, 1, false, matrix, 0);
	}

	public int getPositionAttributeLocation() {
		return aPositionLocation;
	}

	public int getColorAttributeLocation() {
		return aColorLocation;
	}
}
