
package com.sevenge.graphics;

import static android.opengl.GLES20.GL_ONE;
import static android.opengl.GLES20.GL_POINTS;
import static android.opengl.GLES20.glBlendFunc;
import static android.opengl.GLES20.glDrawArrays;
import static android.opengl.GLES20.glUseProgram;
import android.graphics.Color;

import com.sevenge.utils.Vector3;

public class ParticleSystem {
	private static final int POSITION_COMPONENT_COUNT = 3;
	private static final int COLOR_COMPONENT_COUNT = 3;
	private static final int VECTOR_COMPONENT_COUNT = 3;
	private static final int PARTICLE_START_TIME_COMPONENT_COUNT = 1;
	private static final int TOTAL_COMPONENT_COUNT = POSITION_COMPONENT_COUNT + COLOR_COMPONENT_COUNT + VECTOR_COMPONENT_COUNT
		+ PARTICLE_START_TIME_COMPONENT_COUNT;
	private static final int BYTES_PER_FLOAT = 4;
	private static final int STRIDE = TOTAL_COMPONENT_COUNT * BYTES_PER_FLOAT;

	private float[] mParticles;
	private VertexArray mVertexArray;
	private int mMaxParticleCount;
	private int mCurrentParticleCount = 0;
	private int mNextParticle = 0;
	private TexturedParticleShaderProgram mProgram;
	private int mTextureID;

	/** Creates a new particle System
	 * @param maxParticleCount maximum number of particles in the system
	 * @param psp particle shader program
	 * @param texture to use for rendering particles */
	public ParticleSystem (int maxParticleCount, TexturedParticleShaderProgram psp, int texture) {
		mParticles = new float[maxParticleCount * TOTAL_COMPONENT_COUNT];
		mVertexArray = new VertexArray(maxParticleCount * TOTAL_COMPONENT_COUNT);
		this.mMaxParticleCount = maxParticleCount;
		mProgram = psp;
		mTextureID = texture;
	}

	/** Adds a particle to the system. If the number of particles is exceeded the old particles are overwritten
	 * @param position of the particle
	 * @param color of the particle
	 * @param direction of the particle
	 * @param particleStartTime start time of the particle in seconds */
	public void addParticle (Vector3 position, int color, Vector3 direction, float particleStartTime) {
		final int particleOffset = mNextParticle * TOTAL_COMPONENT_COUNT;
		int currentOffset = particleOffset;
		mNextParticle++;
		if (mCurrentParticleCount < mMaxParticleCount) {
			mCurrentParticleCount++;
		}
		if (mNextParticle == mMaxParticleCount) {
			mNextParticle = 0;
		}
		mParticles[currentOffset++] = position.x;
		mParticles[currentOffset++] = position.y;
		mParticles[currentOffset++] = position.z;
		mParticles[currentOffset++] = Color.red(color) / 255f;
		mParticles[currentOffset++] = Color.green(color) / 255f;
		mParticles[currentOffset++] = Color.blue(color) / 255f;
		mParticles[currentOffset++] = direction.x;
		mParticles[currentOffset++] = direction.y;
		mParticles[currentOffset++] = direction.z;
		mParticles[currentOffset++] = particleStartTime;
		mVertexArray.put(mParticles, particleOffset, TOTAL_COMPONENT_COUNT);
	}

	/** Draws this particle system
	 * @param vpMatrix combined camera matrix
	 * @param time elapsedTime since the start of this particle system */
	public void draw (float[] vpMatrix, float time) {
		glUseProgram(mProgram.mGlID);
		glBlendFunc(GL_ONE, GL_ONE);
		mProgram.setUniforms(vpMatrix, time, mTextureID, 25.0f);
		int dataOffset = 0;
		mVertexArray.setVertexAttribPointer(dataOffset, mProgram.aPositionLocation, POSITION_COMPONENT_COUNT, STRIDE);
		dataOffset += POSITION_COMPONENT_COUNT;
		mVertexArray.setVertexAttribPointer(dataOffset, mProgram.aColorLocation, COLOR_COMPONENT_COUNT, STRIDE);
		dataOffset += COLOR_COMPONENT_COUNT;
		mVertexArray.setVertexAttribPointer(dataOffset, mProgram.aDirectionVectorLocation, VECTOR_COMPONENT_COUNT, STRIDE);
		dataOffset += VECTOR_COMPONENT_COUNT;
		mVertexArray.setVertexAttribPointer(dataOffset, mProgram.aParticleStartTimeLocation, PARTICLE_START_TIME_COMPONENT_COUNT,
			STRIDE);
		glDrawArrays(GL_POINTS, 0, mCurrentParticleCount);
	}
}
