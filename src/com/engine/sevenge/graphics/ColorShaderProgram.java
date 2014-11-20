
package com.engine.sevenge.graphics;

import static android.opengl.GLES20.glGetAttribLocation;
import static android.opengl.GLES20.glGetUniformLocation;
import static android.opengl.GLES20.glUniformMatrix4fv;

/** Class providing easy way of colouring vertices and faces in opengl */
public final class ColorShaderProgram extends ShaderProgram {
	// Uniform locations
	private final int uniformMatrixLocation;
	// Attribute locations
	private final int attributePositionLocation;
	private final int atributeColorLocation;

	/** Creates a shader program with specified shaders and retrives attribute locations
	 * @param vs vertex shader object suitable for coloring
	 * @param fs fragment shader object suitable for coloring */
	public ColorShaderProgram (Shader vs, Shader fs) {
		super(vs, fs);
		// Retrieve uniform locations for the shader program.
		uniformMatrixLocation = glGetUniformLocation(programID, U_MATRIX);
		// Retrieve attribute locations for the shader program.
		attributePositionLocation = glGetAttribLocation(programID, A_POSITION);
		atributeColorLocation = glGetAttribLocation(programID, A_COLOR);
	}

	/** Set matrix uniform for shader program
	 * @param matrix location matrix */
	public void setUniforms (float[] matrix) {
		// Pass the matrix into the shader program.
		glUniformMatrix4fv(uniformMatrixLocation, 1, false, matrix, 0);
	}

	/** Retrieves the position attribute location
	 * @return position attribute location */
	public int getPositionAttributeLocation () {
		return attributePositionLocation;
	}

	/** Retrieves the color attribute location
	 * @return color attribute location */
	public int getColorAttributeLocation () {
		return atributeColorLocation;
	}
}
