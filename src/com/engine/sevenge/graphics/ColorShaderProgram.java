
package com.engine.sevenge.graphics;

import static android.opengl.GLES20.glGetAttribLocation;
import static android.opengl.GLES20.glGetUniformLocation;
import static android.opengl.GLES20.glUniformMatrix4fv;

import com.engine.sevenge.assets.Asset;

/** Class providing easy way of colouring vertices and faces in opengl */
public final class ColorShaderProgram extends Asset {
	// Uniform locations
	private final int uniformMatrixLocation;
	// Attribute locations
	public final int attributePositionLocation;
	public final int atributeColorLocation;

	public final int glID;

	/** Creates a shader program with specified shaders and retrives attribute locations
	 * @param vs vertex shader object suitable for coloring
	 * @param fs fragment shader object suitable for coloring */
	public ColorShaderProgram (int vs, int fs) {
		glID = ShaderUtils.linkShaderProgram(vs, fs);
		// Retrieve uniform locations for the shader program.
		uniformMatrixLocation = glGetUniformLocation(glID, "u_Matrix");
		// Retrieve attribute locations for the shader program.
		attributePositionLocation = glGetAttribLocation(glID, "a_Position");
		atributeColorLocation = glGetAttribLocation(glID, "a_Color");
	}

	/** Set matrix uniform for shader program
	 * @param matrix location matrix */
	public void setUniforms (float[] matrix) {
		// Pass the matrix into the shader program.
		glUniformMatrix4fv(uniformMatrixLocation, 1, false, matrix, 0);
	}
}
