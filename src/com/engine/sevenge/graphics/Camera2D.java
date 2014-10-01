
package com.engine.sevenge.graphics;

import static android.opengl.Matrix.invertM;
import static android.opengl.Matrix.multiplyMM;
import static android.opengl.Matrix.multiplyMV;
import static android.opengl.Matrix.orthoM;
import static android.opengl.Matrix.setLookAtM;
import android.graphics.PointF;

public class Camera2D {

	private final float[] projectionMatrix = new float[16];

	private final float[] viewMatrix = new float[16];

	private final float[] viewProjectionMatrix = new float[16];
	/** Used for unprojecting the coordinates into world coordinates */
	private final float[] invertedViewProjectionMatrix = new float[16];
	/** Container for unprojected coordinates */
	private final float[] unprojectedCoords = new float[4];

	private final float[] deviceCoords = new float[4];
	/** Position of the camera in world coordinates */
	private PointF cameraPosition = new PointF();

	private boolean isChangedView = true;

	private boolean isChangedProjection = true;

	private boolean isChangedInvertedViewProjection = true;

	private float height;

	private float width;

	private float scale = 1f;

	public Camera2D () {

	}

	public void setProjectionOrtho (float width, float height) {
		this.height = height;
		this.width = width;
		orthoM(projectionMatrix, 0, -width / 2, width / 2, -height / 2, height / 2, 0f, 1f);
		isChangedProjection = true;
		isChangedInvertedViewProjection = true;
	}

	public void lookAt (float x, float y) {
		setLookAtM(viewMatrix, 0, x, y, 1f, x, y, 0f, 0f, 1.0f, 0.0f);
		cameraPosition.set(x, y);
		isChangedView = true;
		isChangedInvertedViewProjection = true;
	}

	public void zoom (float scale) {
		this.scale = scale;
		isChangedProjection = true;
		isChangedInvertedViewProjection = true;
	}

	public float[] unProject (float x, float y) {
		if (isChangedInvertedViewProjection) {
			orthoM(projectionMatrix, 0, -width / scale / 2, width / scale / 2, -height / scale / 2, height / scale / 2, 0f, 1f);
			multiplyMM(viewProjectionMatrix, 0, projectionMatrix, 0, viewMatrix, 0);
			invertM(invertedViewProjectionMatrix, 0, viewProjectionMatrix, 0);
			isChangedInvertedViewProjection = false;
		}
		deviceCoords[0] = x / width * 2 - 1;
		deviceCoords[1] = -y / height * 2 + 1;
		deviceCoords[2] = 1;
		deviceCoords[3] = 1;
		multiplyMV(unprojectedCoords, 0, invertedViewProjectionMatrix, 0, deviceCoords, 0);
		return unprojectedCoords;
	}

	public float[] getViewProjectionMatrix () {
		if (isChangedProjection) {
			orthoM(projectionMatrix, 0, -width / scale / 2, width / scale / 2, -height / scale / 2, height / scale / 2, 0f, 1f);
		}
		if (isChangedView || isChangedProjection) {
			multiplyMM(viewProjectionMatrix, 0, projectionMatrix, 0, viewMatrix, 0);
			isChangedView = false;
			isChangedProjection = false;
		}
		return viewProjectionMatrix;
	}

	public PointF getCameraPosition () {
		return cameraPosition;
	}
}
