
package com.sevenge.graphics;

import java.util.Random;

import android.opengl.Matrix;

import com.sevenge.utils.Vector3;

public class ParticleShooter {
	private final Vector3 position;
	private final int color;

	private final float angleVariance;
	private final float speedVariance;
	private final Random random = new Random();
	private float[] rotationMatrix = new float[16];
	private float[] directionVector = new float[4];
	private float[] resultVector = new float[4];

	public ParticleShooter (Vector3 position, Vector3 direction, int color, float angleVarianceInDegrees, float speedVariance) {
		this.position = position;
		this.color = color;
		this.angleVariance = angleVarianceInDegrees;
		this.speedVariance = speedVariance;

		directionVector[0] = direction.x;
		directionVector[1] = direction.y;
		directionVector[2] = direction.z;
	}

	public void addParticles (ParticleSystem particleSystem, float currentTime, int count) {
		for (int i = 0; i < count; i++) {
			Matrix.setRotateEulerM(rotationMatrix, 0, (random.nextFloat() - 0.5f) * angleVariance, (random.nextFloat() - 0.5f)
				* angleVariance, (random.nextFloat() - 0.5f) * angleVariance);

			Matrix.multiplyMV(resultVector, 0, rotationMatrix, 0, directionVector, 0);

			float speedAdjustment = 1f + random.nextFloat() * speedVariance;

			Vector3 thisDirection = new Vector3(resultVector[0] * speedAdjustment, resultVector[1] * speedAdjustment, 0.0f);
			particleSystem.addParticle(position, color, thisDirection, currentTime);
		}
	}
}
