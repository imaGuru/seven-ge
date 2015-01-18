
package com.sevenge.graphics;

import static android.opengl.GLES20.glGetAttribLocation;
import static android.opengl.GLES20.glGetUniformLocation;
import static android.opengl.GLES20.glUniform1f;
import static android.opengl.GLES20.glUniformMatrix4fv;

public class ParticleShaderProgram {

// Uniform locations
	private final int uMatrixLocation;
	private final int uTimeLocation;
	// Attribute locations
	public final int aPositionLocation;
	public final int aColorLocation;
	public final int aDirectionVectorLocation;
	public final int aParticleStartTimeLocation;
	public final int mGlID;

	/** Creates a shader program with specified shaders and retrives attribute locations
	 * @param vs vertex shader object suitable for particle rendering
	 * @param fs fragment shader object suitable for particle rendering */
	public ParticleShaderProgram (int vs, int fs) {
		mGlID = ShaderUtils.linkShaderProgram(vs, fs);
		// Retrieve uniform locations for the shader program.
		uMatrixLocation = glGetUniformLocation(mGlID, "u_Matrix");
		uTimeLocation = glGetUniformLocation(mGlID, "u_Time");
		// Retrieve attribute locations for the shader program.
		aPositionLocation = glGetAttribLocation(mGlID, "a_Position");
		aColorLocation = glGetAttribLocation(mGlID, "a_Color");
		aDirectionVectorLocation = glGetAttribLocation(mGlID, "a_DirectionVector");
		aParticleStartTimeLocation = glGetAttribLocation(mGlID, "a_ParticleStartTime");
	}

	/** Set uniforms for shader program
	 * @param matrix location matrix
	 * @param elapsedTime time since the start of the particle system in seconds */
	public void setUniforms (float[] matrix, float elapsedTime) {
		glUniformMatrix4fv(uMatrixLocation, 1, false, matrix, 0);
		glUniform1f(uTimeLocation, elapsedTime);
	}
}
