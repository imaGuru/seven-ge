package com.engine.sevenge.graphics;

import static android.opengl.GLES20.glGetUniformLocation;
import static android.opengl.GLES20.glGetAttribLocation;
import static android.opengl.GLES20.glUniformMatrix4fv;
import static android.opengl.GLES20.glActiveTexture;
import static android.opengl.GLES20.glBindTexture;
import static android.opengl.GLES20.glUniform1i;
import static android.opengl.GLES20.GL_TEXTURE0;
import static android.opengl.GLES20.GL_TEXTURE_2D;

public class TextureShaderProgram extends ShaderProgram {
	private final int uMatrixLocation;
	private final int uTextureUnitLocation;
	// Attribute locations
	private final int aPositionLocation;
	private final int aTextureCoordinatesLocation;

	public TextureShaderProgram(int vertexShaderID, int fragmentShaderID) {
		super(vertexShaderID, fragmentShaderID);

		uMatrixLocation = glGetUniformLocation(programID, U_MATRIX);
		uTextureUnitLocation = glGetUniformLocation(programID, U_TEXTURE_UNIT);
		// Retrieve attribute locations for the shader program.
		aPositionLocation = glGetAttribLocation(programID, A_POSITION);
		aTextureCoordinatesLocation = glGetAttribLocation(programID,
				A_TEXTURE_COORDINATES);
	}

	public void setUniforms(float[] matrix, int textureId) {
		// Pass the matrix into the shader program.
		glUniformMatrix4fv(uMatrixLocation, 1, false, matrix, 0);
		// Set the active texture unit to texture unit 0.
		glActiveTexture(GL_TEXTURE0);
		// Bind the texture to this unit.
		glBindTexture(GL_TEXTURE_2D, textureId);
		// Tell the texture uniform sampler to use this texture in the shader by
		// telling it to read from texture unit 0.
		glUniform1i(uTextureUnitLocation, 0);
	}

	public int getPositionAttributeLocation() {
		return aPositionLocation;
	}

	public int getTextureCoordinatesAttributeLocation() {
		return aTextureCoordinatesLocation;
	}
}
