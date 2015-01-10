
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

	private float[] particles;
	private VertexArray vertexArray;
	private int maxParticleCount;
	private int currentParticleCount = 0;
	private int nextParticle = 0;
	private TexturedParticleShaderProgram mProgram;
	private int textureID;

	public ParticleSystem (int maxParticleCount, TexturedParticleShaderProgram psp, int texture) {
		particles = new float[maxParticleCount * TOTAL_COMPONENT_COUNT];
		vertexArray = new VertexArray(maxParticleCount * TOTAL_COMPONENT_COUNT);
		this.maxParticleCount = maxParticleCount;
		mProgram = psp;
		textureID = texture;
	}

	public void addParticle (Vector3 position, int color, Vector3 direction, float particleStartTime) {
		final int particleOffset = nextParticle * TOTAL_COMPONENT_COUNT;
		int currentOffset = particleOffset;
		nextParticle++;
		if (currentParticleCount < maxParticleCount) {
			currentParticleCount++;
		}
		if (nextParticle == maxParticleCount) {
			nextParticle = 0;
		}
		particles[currentOffset++] = position.x;
		particles[currentOffset++] = position.y;
		particles[currentOffset++] = position.z;
		particles[currentOffset++] = Color.red(color) / 255f;
		particles[currentOffset++] = Color.green(color) / 255f;
		particles[currentOffset++] = Color.blue(color) / 255f;
		particles[currentOffset++] = direction.x;
		particles[currentOffset++] = direction.y;
		particles[currentOffset++] = direction.z;
		particles[currentOffset++] = particleStartTime;
		vertexArray.put(particles, particleOffset, TOTAL_COMPONENT_COUNT);
	}

	public void draw (float[] vpMatrix, float time) {
		glUseProgram(mProgram.mGlID);
		glBlendFunc(GL_ONE, GL_ONE);
		mProgram.setUniforms(vpMatrix, time, textureID, 25.0f);
		int dataOffset = 0;
		vertexArray.setVertexAttribPointer(dataOffset, mProgram.aPositionLocation, POSITION_COMPONENT_COUNT, STRIDE);
		dataOffset += POSITION_COMPONENT_COUNT;
		vertexArray.setVertexAttribPointer(dataOffset, mProgram.aColorLocation, COLOR_COMPONENT_COUNT, STRIDE);
		dataOffset += COLOR_COMPONENT_COUNT;
		vertexArray.setVertexAttribPointer(dataOffset, mProgram.aDirectionVectorLocation, VECTOR_COMPONENT_COUNT, STRIDE);
		dataOffset += VECTOR_COMPONENT_COUNT;
		vertexArray.setVertexAttribPointer(dataOffset, mProgram.aParticleStartTimeLocation, PARTICLE_START_TIME_COMPONENT_COUNT,
			STRIDE);
		glDrawArrays(GL_POINTS, 0, currentParticleCount);
	}
}
