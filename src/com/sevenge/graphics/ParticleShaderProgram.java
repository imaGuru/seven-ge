
package com.sevenge.graphics;

import static android.opengl.GLES20.GL_TEXTURE0;
import static android.opengl.GLES20.GL_TEXTURE_2D;
import static android.opengl.GLES20.glActiveTexture;
import static android.opengl.GLES20.glBindTexture;
import static android.opengl.GLES20.glGetAttribLocation;
import static android.opengl.GLES20.glGetUniformLocation;
import static android.opengl.GLES20.glUniform1f;
import static android.opengl.GLES20.glUniform1i;
import static android.opengl.GLES20.glUniformMatrix4fv;

import com.sevenge.assets.Asset;

public class ParticleShaderProgram extends Asset {

	// Uniform locations
	private final int uMatrixLocation;
	private final int uTimeLocation;
	public final int uTextureUnitLocation;
	// Attribute locations
	public final int aPositionLocation;
	public final int aColorLocation;
	public final int aDirectionVectorLocation;
	public final int aParticleStartTimeLocation;
	public final int mGlID;

	public ParticleShaderProgram (int vs, int fs) {
		mGlID = ShaderUtils.linkShaderProgram(vs, fs);
		// Retrieve uniform locations for the shader program.
		uMatrixLocation = glGetUniformLocation(mGlID, "u_Matrix");
		uTimeLocation = glGetUniformLocation(mGlID, "u_Time");
		uTextureUnitLocation = glGetUniformLocation(mGlID, "u_TextureUnit");
		// Retrieve attribute locations for the shader program.
		aPositionLocation = glGetAttribLocation(mGlID, "a_Position");
		aColorLocation = glGetAttribLocation(mGlID, "a_Color");
		aDirectionVectorLocation = glGetAttribLocation(mGlID, "a_DirectionVector");
		aParticleStartTimeLocation = glGetAttribLocation(mGlID, "a_ParticleStartTime");
	}

	public void setUniforms (float[] matrix, float elapsedTime, int textureId) {
		glUniformMatrix4fv(uMatrixLocation, 1, false, matrix, 0);
		glUniform1f(uTimeLocation, elapsedTime);
		glActiveTexture(GL_TEXTURE0);
		glBindTexture(GL_TEXTURE_2D, textureId);
		glUniform1i(uTextureUnitLocation, 0);
	}
}
