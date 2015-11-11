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

public class TexturedParticleShaderProgram extends Asset {

	// Uniform locations
	private final int uMatrixLocation;
	private final int uTimeLocation;
	private final int uGravityFactorLocation;
	public final int uTextureUnitLocation;
	// Attribute locations
	public final int aPositionLocation;
	public final int aColorLocation;
	public final int aDirectionVectorLocation;
	public final int aParticleStartTimeLocation;
	public final int mGlID;

	/**
	 * Creates this shader program and sets shader locations
	 * 
	 * @param vs
	 *            vertex shader glID
	 * @param fs
	 *            fragment shader glID
	 */
	public TexturedParticleShaderProgram(int vs, int fs) {
		mGlID = ShaderUtils.linkShaderProgram(vs, fs);
		// Retrieve uniform locations for the shader program.
		uMatrixLocation = glGetUniformLocation(mGlID, "u_Matrix");
		uTimeLocation = glGetUniformLocation(mGlID, "u_Time");
		uGravityFactorLocation = glGetUniformLocation(mGlID, "u_GravityFactor");
		uTextureUnitLocation = glGetUniformLocation(mGlID, "u_TextureUnit");
		// Retrieve attribute locations for the shader program.
		aPositionLocation = glGetAttribLocation(mGlID, "a_Position");
		aColorLocation = glGetAttribLocation(mGlID, "a_Color");
		aDirectionVectorLocation = glGetAttribLocation(mGlID,
				"a_DirectionVector");
		aParticleStartTimeLocation = glGetAttribLocation(mGlID,
				"a_ParticleStartTime");
	}

	/**
	 * Sets the uniforms for this shaderprogram
	 * 
	 * @param matrix
	 *            projection matrix
	 * @param elapsedTime
	 *            time since the start of the particle system
	 * @param textureId
	 *            texture to draw the point sprites with
	 * @param gravityFactor
	 *            not used
	 */
	public void setUniforms(float[] matrix, float elapsedTime, int textureId,
			float gravityFactor) {
		glUniformMatrix4fv(uMatrixLocation, 1, false, matrix, 0);
		glUniform1f(uTimeLocation, elapsedTime);
		glUniform1f(uGravityFactorLocation, gravityFactor);
		glActiveTexture(GL_TEXTURE0);
		glBindTexture(GL_TEXTURE_2D, textureId);
		glUniform1i(uTextureUnitLocation, 0);
	}
}
