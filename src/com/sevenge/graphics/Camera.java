
package com.sevenge.graphics;

import static android.opengl.Matrix.invertM;
import static android.opengl.Matrix.multiplyMM;
import static android.opengl.Matrix.multiplyMV;
import static android.opengl.Matrix.orthoM;
import static android.opengl.Matrix.setLookAtM;

import com.sevenge.utils.Vector2;
import com.sevenge.utils.Vector3;

public class Camera {
	private final float[] mCoords = new float[4];
	private final float[] mDevCoords = new float[4];
	private final float[] projectionMatrix = new float[16];
	private final float[] viewMatrix = new float[16];
	private final float[] invertedVPMatrix = new float[16];
	private float[] viewProjectionMatrix = new float[16];
	private final int height;
	private final int width;
	private final Vector2 position = new Vector2(0, 0);
	private final Vector2 lastPosition = new Vector2(0, 0);
	private final Vector3 up = new Vector3(0, 1, 0);
	private float rotation;
	private float zoom = 1f;
	private float[] projectionMatrixParallax = new float[16];

	public Camera (int w, int h) {
		height = h;
		width = w;
	}

	public void setZoom (float z) {
		zoom = z;
	}

	public float getZoom () {
		return zoom;
	}

	public void setPostion (float x, float y) {
		lastPosition.x = position.x;
		lastPosition.y = position.y;
		position.x = x;
		position.y = y;
	}

	public Vector2 getPosition () {
		return position;
	}

	public void setRotation (float angle) {
		rotation = angle;
		up.x = (float)Math.sin(rotation);
		up.y = (float)Math.cos(rotation);
	}

	public float getRotation () {
		return rotation;
	}

	public float[] getCameraMatrix () {
		float hw = width / 2 * zoom;
		float hh = height / 2 * zoom;
		orthoM(projectionMatrix, 0, -hw, hw, -hh, hh, -1f, 1f);
		float x = position.x;
		float y = position.y;
		setLookAtM(viewMatrix, 0, x, y, 1f, x, y, 0f, up.x, up.y, up.z);
		multiplyMM(viewProjectionMatrix, 0, projectionMatrix, 0, viewMatrix, 0);
		return viewProjectionMatrix;
	}

	public float[] getCameraMatrix (float parallax, float[] outMatrix) {
		float hw = width / 2 * (1 + (zoom - 1) * parallax);
		float hh = height / 2 * (1 + (zoom - 1) * parallax);
		orthoM(projectionMatrixParallax, 0, -hw, hw, -hh, hh, -1f, 1f);
		float x = position.x * parallax;
		float y = position.y * parallax;
		setLookAtM(viewMatrix, 0, x, y, 1f, x, y, 0f, up.x, up.y, up.z);
		multiplyMM(outMatrix, 0, projectionMatrixParallax, 0, viewMatrix, 0);
		return outMatrix;
	}

	public float[] getProjectionMatrix () {
		float hw = width / 2;
		float hh = height / 2;
		orthoM(projectionMatrix, 0, -hw, hw, -hh, hh, -1f, 1f);
		return projectionMatrix;
	}

	/** Unprojects x y screen coordinates into world coordinates
	 * @param x coordinate in device coordinates
	 * @param y coordinate in device coordinates
	 * @param w width of the screen
	 * @param h height of the screen
	 * @param devCoords temporary array of length 4 to avoid constant allocation in the method
	 * @param invertedVPM inverted viewProjection matrix
	 * @param unpCoords output in world coordinates. Array has to be of length 4
	 * @return */
	public float[] unproject (int x, int y, int wScreen, int hScreen, float[] cameraMatrix) {
		mDevCoords[0] = 1.0f * x / wScreen * 2 - 1;
		mDevCoords[1] = -1.0f * y / hScreen * 2 + 1;
		mDevCoords[2] = 0;
		mDevCoords[3] = 1;
		invertM(invertedVPMatrix, 0, cameraMatrix, 0);
		multiplyMV(mCoords, 0, invertedVPMatrix, 0, mDevCoords, 0);
		return mCoords;
	}

	public Vector2 getLastPosition () {
		return lastPosition;
	}
}