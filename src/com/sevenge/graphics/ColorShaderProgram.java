
package com.sevenge.graphics;

import static android.opengl.GLES20.glGetAttribLocation;
import static android.opengl.GLES20.glGetUniformLocation;
import static android.opengl.GLES20.glUniformMatrix4fv;

import com.sevenge.assets.Asset;

/** Class providing easy way of colouring vertices and faces in opengl */
public final class ColorShaderProgram extends Asset {
	// Uniform locations
	private final int mUniformMatrixLocation;
	// Attribute locations
	public final int mAttributePositionLocation;
	public final int mAtributeColorLocation;

	public final int glID;

	/** Creates a shader program with specified shaders and retrives attribute locations
	 * @param vs vertex shader object suitable for coloring
	 * @param fs fragment shader object suitable for coloring */
	public ColorShaderProgram (int vs, int fs) {
		glID = ShaderUtils.linkShaderProgram(vs, fs);
		// Retrieve uniform locations for the shader program.
		mUniformMatrixLocation = glGetUniformLocation(glID, "u_Matrix");
		// Retrieve attribute locations for the shader program.
		mAttributePositionLocation = glGetAttribLocation(glID, "a_Position");
		mAtributeColorLocation = glGetAttribLocation(glID, "a_Color");
	}

	/** Set matrix uniform for shader program
	 * @param matrix location matrix */
	public void setUniforms (float[] matrix) {
		// Pass the matrix into the shader program.
		glUniformMatrix4fv(mUniformMatrixLocation, 1, false, matrix, 0);
	}
}
