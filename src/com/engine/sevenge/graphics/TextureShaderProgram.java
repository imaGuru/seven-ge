
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

	public TextureShaderProgram (Shader vs, Shader fs) {
		super(vs, fs);

		matrixLocation = glGetUniformLocation(programID, U_MATRIX);
		textureUnitLocation = glGetUniformLocation(programID, U_TEXTURE_UNIT);
		// Retrieve attribute locations for the shader program.
		attributePositionLocation = glGetAttribLocation(programID, A_POSITION);
		attributeTextureCoordinatesLocation = glGetAttribLocation(programID, A_TEXTURE_COORDINATES);
	}

	public void setUniforms (float[] matrix, Texture texture) {
		// Pass the matrix into the shader program.
		glUniformMatrix4fv(matrixLocation, 1, false, matrix, 0);
		texture.bindTexture(GL_TEXTURE0);
		glUniform1i(textureUnitLocation, 0);
	}

	public int getPositionAttributeLocation () {
		return attributePositionLocation;
	}

	public int getTextureCoordinatesAttributeLocation () {
		return attributeTextureCoordinatesLocation;
	}
}
