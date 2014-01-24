package com.engine.sevenge.graphics;

import static android.opengl.GLES20.GL_TEXTURE0;
import static android.opengl.GLES20.glGetAttribLocation;
import static android.opengl.GLES20.glGetUniformLocation;
import static android.opengl.GLES20.glUniform1i;
import static android.opengl.GLES20.glUniformMatrix4fv;

public class TextureShaderProgram extends ShaderProgram {
	private final int uMatrixLocation;
	private final int uTextureUnitLocation;
	// Attribute locations
	private final int aPositionLocation;
	private final int aTextureCoordinatesLocation;

	public TextureShaderProgram(Shader vs, Shader fs) {
		super(vs, fs);

		uMatrixLocation = glGetUniformLocation(programID, U_MATRIX);
		uTextureUnitLocation = glGetUniformLocation(programID, U_TEXTURE_UNIT);
		// Retrieve attribute locations for the shader program.
		aPositionLocation = glGetAttribLocation(programID, A_POSITION);
		aTextureCoordinatesLocation = glGetAttribLocation(programID,
				A_TEXTURE_COORDINATES);
	}

	public void setUniforms(float[] matrix, Texture2D texture) {
		// Pass the matrix into the shader program.
		glUniformMatrix4fv(uMatrixLocation, 1, false, matrix, 0);
		texture.bindTexture(GL_TEXTURE0);
		glUniform1i(uTextureUnitLocation, 0);
	}

	public int getPositionAttributeLocation() {
		return aPositionLocation;
	}

	public int getTextureCoordinatesAttributeLocation() {
		return aTextureCoordinatesLocation;
	}
}
