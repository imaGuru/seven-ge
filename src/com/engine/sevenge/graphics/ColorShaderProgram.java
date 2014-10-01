
package com.engine.sevenge.graphics;

import static android.opengl.GLES20.glGetAttribLocation;
import static android.opengl.GLES20.glGetUniformLocation;
import static android.opengl.GLES20.glUniformMatrix4fv;

public class ColorShaderProgram extends ShaderProgram {
	// Uniform locations
	private final int uniformMatrixLocation;
	// Attribute locations
	private final int attributePositionLocation;
	private final int atributeColorLocation;

	public ColorShaderProgram (Shader vs, Shader fs) {
		super(vs, fs);
		// Retrieve uniform locations for the shader program.
		uniformMatrixLocation = glGetUniformLocation(programID, U_MATRIX);
		// Retrieve attribute locations for the shader program.
		attributePositionLocation = glGetAttribLocation(programID, A_POSITION);
		atributeColorLocation = glGetAttribLocation(programID, A_COLOR);
	}

	public void setUniforms (float[] matrix) {
		// Pass the matrix into the shader program.
		glUniformMatrix4fv(uniformMatrixLocation, 1, false, matrix, 0);
	}

	public int getPositionAttributeLocation () {
		return attributePositionLocation;
	}

	public int getColorAttributeLocation () {
		return atributeColorLocation;
	}
}
