package com.engine.sevenge.graphics;

import static android.opengl.Matrix.invertM;
import static android.opengl.Matrix.multiplyMM;
import static android.opengl.Matrix.multiplyMV;
import static android.opengl.Matrix.orthoM;
import static android.opengl.Matrix.setLookAtM;
import android.graphics.PointF;

public class Camera2D {

	private final float[] mProjectionMatrix = new float[16];

	private final float[] mViewMatrix = new float[16];

	private final float[] mViewProjectionMatrix = new float[16];
	/**
	 * Used for unprojecting the coordinates into world coordinates
	 */
	private final float[] mInvertedViewProjectionMatrix = new float[16];
	/**
	 * Container for unprojected coordinates
	 */
	private final float[] mUnprojectedCoords = new float[4];

	private final float[] mDeviceCoords = new float[4];
	/**
	 * Position of the camera in world coordinates
	 */
	private PointF mCameraPosition = new PointF();

	private boolean isChangedView = true;

	private boolean isChangedProjection = true;

	private boolean isChangedInvertedViewProjection = true;

	private float mHeight;

	private float mWidth;

	private float mScale = 1f;

	public Camera2D() {

	}

	public void setProjectionOrtho(float width, float height) {
		mHeight = height;
		mWidth = width;
		orthoM(mProjectionMatrix, 0, -width / 2, width / 2, -height / 2,
				height / 2, 0f, 1f);
		isChangedProjection = true;
		isChangedInvertedViewProjection = true;
	}

	public void lookAt(float x, float y) {
		setLookAtM(mViewMatrix, 0, x, y, 1f, x, y, 0f, 0f, 1.0f, 0.0f);
		mCameraPosition.set(x, y);
		isChangedView = true;
		isChangedInvertedViewProjection = true;
	}

	public void zoom(float scale) {
		mScale = scale;
		isChangedProjection = true;
		isChangedInvertedViewProjection = true;
	}

	public float[] unProject(float x, float y) {
		if (isChangedInvertedViewProjection) {
			orthoM(mProjectionMatrix, 0, -mWidth / mScale / 2, mWidth / mScale
					/ 2, -mHeight / mScale / 2, mHeight / mScale / 2, 0f, 1f);
			multiplyMM(mViewProjectionMatrix, 0, mProjectionMatrix, 0,
					mViewMatrix, 0);
			invertM(mInvertedViewProjectionMatrix, 0, mViewProjectionMatrix, 0);
			isChangedInvertedViewProjection = false;
		}
		mDeviceCoords[0] = x / mWidth * 2 - 1;
		mDeviceCoords[1] = -y / mHeight * 2 + 1;
		mDeviceCoords[2] = 1;
		mDeviceCoords[3] = 1;
		multiplyMV(mUnprojectedCoords, 0, mInvertedViewProjectionMatrix, 0,
				mDeviceCoords, 0);
		return mUnprojectedCoords;
	}

	public float[] getViewProjectionMatrix() {
		if (isChangedProjection) {
			orthoM(mProjectionMatrix, 0, -mWidth / mScale / 2, mWidth / mScale
					/ 2, -mHeight / mScale / 2, mHeight / mScale / 2, 0f, 1f);
		}
		if (isChangedView || isChangedProjection) {
			multiplyMM(mViewProjectionMatrix, 0, mProjectionMatrix, 0,
					mViewMatrix, 0);
			isChangedView = false;
			isChangedProjection = false;
		}
		return mViewProjectionMatrix;
	}

	public PointF getCameraPosition() {
		return mCameraPosition;
	}
}
