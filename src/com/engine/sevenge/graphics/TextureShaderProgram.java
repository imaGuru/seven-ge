
package com.engine.sevenge.graphics;

import static android.opengl.GLES20.GL_TEXTURE0;
import static android.opengl.GLES20.glGetAttribLocation;
import static android.opengl.GLES20.glGetUniformLocation;
import static android.opengl.GLES20.glUniform1i;
import static android.opengl.GLES20.glUniformMatrix4fv;

import com.engine.sevenge.assets.Asset;

public final class TextureShaderProgram extends Asset {

	private final int matrixLocation;
	private final int textureUnitLocation;
	// Attribute locations
	public final int attributePositionLocation;
	public final int attributeTextureCoordinatesLocation;

	public final int glID;

	/** Texture shader program for drawing textured objects in OpenGL
	 * @param vs vertex shader
	 * @param fs fragment shader */
	public TextureShaderProgram (int vs, int fs) {
		glID = ShaderUtils.linkShaderProgram(vs, fs);

		matrixLocation = glGetUniformLocation(glID, "u_Matrix");
		textureUnitLocation = glGetUniformLocation(glID, "u_TextureUnit");
		// Retrieve attribute locations for the shader program.
		attributePositionLocation = glGetAttribLocation(glID, "a_Position");
		attributeTextureCoordinatesLocation = glGetAttribLocation(glID, "a_TextureCoordinates");
	}

	/** Set texture and matrix to be used with the shader program
	 * @param matrix
	 * @param texture */
	public void setUniforms (float[] matrix, int texture) {
		// Pass the matrix into the shader program.
		glUniformMatrix4fv(matrixLocation, 1, false, matrix, 0);
		TextureUtils.bindTexture(GL_TEXTURE0, texture);
		glUniform1i(textureUnitLocation, 0);
	}
}
