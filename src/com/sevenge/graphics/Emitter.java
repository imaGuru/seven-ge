
package com.sevenge.graphics;

import java.util.Random;

import android.opengl.Matrix;

import com.sevenge.utils.Vector3;

public class Emitter {
	private final int color;

	private ParticleSystem particleSystem;
	private final float angleVariance;
	private final float speedVariance;
	private final Random random = new Random();
	private float[] rotationMatrix = new float[16];
	private float[] directionVector = new float[4];
	private float[] resultVector = new float[4];

	/** Creates a particle emitter using specified ParticleSystem
	 * @param particleSystem to be used by the emitter
	 * @param color of the emitted particles
	 * @param angleVarianceInDegrees variance in particle angle
	 * @param speedVariance variance in particle speed */
	public Emitter (ParticleSystem particleSystem, int color, float angleVarianceInDegrees, float speedVariance) {
		this.particleSystem = particleSystem;
		this.color = color;
		this.angleVariance = angleVarianceInDegrees;
		this.speedVariance = speedVariance;
	}

	/** Emits particles
	 * @param position starting position of the particle
	 * @param direction direction of the particle
	 * @param currentTime start time of the particle
	 * @param count number of particles to spawn */
	public void addParticles (Vector3 position, Vector3 direction, float currentTime, int count) {
		for (int i = 0; i < count; i++) {
			Matrix.setRotateEulerM(rotationMatrix, 0, (random.nextFloat() - 0.5f) * angleVariance, (random.nextFloat() - 0.5f)
				* angleVariance, (random.nextFloat() - 0.5f) * angleVariance);

			directionVector[0] = direction.x;
			directionVector[1] = direction.y;
			directionVector[2] = direction.z;

			Matrix.multiplyMV(resultVector, 0, rotationMatrix, 0, directionVector, 0);

			float speedAdjustment = 1f + random.nextFloat() * speedVariance;

			Vector3 thisDirection = new Vector3(resultVector[0] * speedAdjustment, resultVector[1] * speedAdjustment, 0.0f);
			particleSystem.addParticle(position, color, thisDirection, currentTime);
		}
	}
}
