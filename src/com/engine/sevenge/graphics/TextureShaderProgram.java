
package com.engine.sevenge.graphics;

import static android.opengl.GLES20.GL_TEXTURE0;
import static android.opengl.GLES20.glGetAttribLocation;
import static android.opengl.GLES20.glGetUniformLocation;
import static android.opengl.GLES20.glUniform1i;
import static android.opengl.GLES20.glUniformMatrix4fv;

public final class TextureShaderProgram extends ShaderProgram {
	private final int matrixLocation;
	private final int textureUnitLocation;
	// Attribute locations
	private final int attributePositionLocation;
	private final int attributeTextureCoordinatesLocation;

	/** Texture shader program for drawing textured objects in OpenGL
	 * @param vs vertex shader
	 * @param fs fragment shader */
	public TextureShaderProgram (Shader vs, Shader fs) {
		super(vs, fs);

		matrixLocation = glGetUniformLocation(programID, U_MATRIX);
		textureUnitLocation = glGetUniformLocation(programID, U_TEXTURE_UNIT);
		// Retrieve attribute locations for the shader program.
		attributePositionLocation = glGetAttribLocation(programID, A_POSITION);
		attributeTextureCoordinatesLocation = glGetAttribLocation(programID, A_TEXTURE_COORDINATES);
	}

	/** Set texture and matrix to be used with the shader program
	 * @param matrix
	 * @param texture */
	public void setUniforms (float[] matrix, Texture texture) {
		// Pass the matrix into the shader program.
		glUniformMatrix4fv(matrixLocation, 1, false, matrix, 0);
		texture.bindTexture(GL_TEXTURE0);
		glUniform1i(textureUnitLocation, 0);
	}

	/** Return position attribute location
	 * @return position attribute location */
	public int getPositionAttributeLocation () {
		return attributePositionLocation;
	}

	/** Return texturecoordinates attribute location
	 * @return texturecoordinates attribute location */
	public int getTextureCoordinatesAttributeLocation () {
		return attributeTextureCoordinatesLocation;
	}
}
