package com.engine.sevenge.graphics;

import static android.opengl.Matrix.multiplyMM;
import static android.opengl.Matrix.orthoM;
import static android.opengl.Matrix.setLookAtM;

public class Camera2D {

	private float[] projectionMatrix = new float[16];
	private float[] viewMatrix = new float[16];
	private float[] viewProjectionMatrix = new float[16];
	private boolean isChanged = true;
	private float vHeight, vWidth, vScale;

	public Camera2D() {

	}

	public void setProjection(float width, float height) {
		orthoM(projectionMatrix, 0, 0, width, 0, height, 0, 1f);
		vHeight = height;
		vWidth = width;
		isChanged = true;
	}

	public void lookAt(float x, float y) {
		setLookAtM(viewMatrix, 0, x, y, 1f, x, y, 0f, 0f, 1.0f, 0.0f);
		isChanged = true;
	}

	public void zoom(float scale) {
		orthoM(projectionMatrix, 0, 0, vWidth * scale, 0, vHeight * scale, 0,
				1f);
		vScale = scale;
		isChanged = true;
	}

	public float[] getViewProjectionMatrix() {
		if (isChanged) {
			multiplyMM(viewProjectionMatrix, 0, projectionMatrix, 0,
					viewMatrix, 0);
			isChanged = false;
		}
		return viewProjectionMatrix;
	}
}
