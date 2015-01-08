
package com.sevenge.graphics;

import static android.opengl.GLES20.glGetAttribLocation;
import static android.opengl.GLES20.glGetUniformLocation;
import static android.opengl.GLES20.glUniform1i;
import static android.opengl.GLES20.glUniformMatrix4fv;

import com.sevenge.assets.Asset;

public final class TextureShaderProgram extends Asset {

	private final int mMatrixLocation;
	private final int mTextureUnitLocation;
	// Attribute locations
	public final int mAttributePositionLocation;
	public final int mAttributeTextureCoordinatesLocation;

	public final int mGlID;

	/** Texture shader program for drawing textured objects in OpenGL
	 * @param vs vertex shader
	 * @param fs fragment shader */
	public TextureShaderProgram (int vs, int fs) {
		mGlID = ShaderUtils.linkShaderProgram(vs, fs);

		mMatrixLocation = glGetUniformLocation(mGlID, "u_Matrix");
		mTextureUnitLocation = glGetUniformLocation(mGlID, "u_TextureUnit");
		// Retrieve attribute locations for the shader program.
		mAttributePositionLocation = glGetAttribLocation(mGlID, "a_Position");
		mAttributeTextureCoordinatesLocation = glGetAttribLocation(mGlID, "a_TextureCoordinates");
	}

	/** Set texture and matrix to be used with the shader program
	 * @param matrix
	 * @param texture */
	public void setMatrixUniform (float[] matrix) {
		// Pass the matrix into the shader program.
		glUniformMatrix4fv(mMatrixLocation, 1, false, matrix, 0);
	}

	public void setTextureUniform (int texture) {
		glUniform1i(mTextureUnitLocation, 0);
	}
}
