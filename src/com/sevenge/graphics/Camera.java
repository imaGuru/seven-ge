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
	private float[] projectionMatrixParallax = new float[16];
	private float[] viewMatrixParallax = new float[16];
	private final int height;
	private final int width;
	private final Vector2 position = new Vector2(0, 0);
	private final Vector3 up = new Vector3(0, 1, 0);
	private float rotation;
	private float zoom = 1f;
	private boolean mUpdated = true;

	/**
	 * Creates camera with given width and height
	 * 
	 * @param w
	 *            width
	 * @param h
	 *            height
	 */
	public Camera(int w, int h) {
		height = h;
		width = w;
	}

	/**
	 * Sets the zoom of this camera. Values above 1 zoom out. Values below 1
	 * zoom in
	 * 
	 * @param z
	 *            zoom factor
	 */
	public void setZoom(float z) {
		zoom = z;
		mUpdated = true;
	}

	/**
	 * Returns current camera zoom
	 * 
	 * @return zoom
	 */
	public float getZoom() {
		return zoom;
	}

	/**
	 * Sets position of the camera to x,y
	 * 
	 * @param x
	 *            new camera x coordinate
	 * @param y
	 *            new camera y coordinate
	 */
	public void setPostion(float x, float y) {
		position.x = x;
		position.y = y;
		mUpdated = true;
	}

	/**
	 * Returns current camera position
	 * 
	 * @return vector with camera location
	 */
	public Vector2 getPosition() {
		return position;
	}

	/**
	 * Sets the rotation of the camera
	 * 
	 * @param angle
	 *            in radians
	 */
	public void setRotation(float angle) {
		rotation = angle;
		up.x = (float) Math.sin(rotation);
		up.y = (float) Math.cos(rotation);
		mUpdated = true;
	}

	/**
	 * Returns the current rotation of the camera
	 * 
	 * @return camera rotation in radians
	 */
	public float getRotation() {
		return rotation;
	}

	/**
	 * Returns the combined camera matrix for the focal layer
	 * 
	 * @return combined viewProjectionMatrix
	 */
	public float[] getCameraMatrix() {
		if (mUpdated) {
			float hw = width / 2 * zoom;
			float hh = height / 2 * zoom;
			orthoM(projectionMatrix, 0, -hw, hw, -hh, hh, -1f, 1f);
			float x = position.x;
			float y = position.y;
			setLookAtM(viewMatrix, 0, x, y, 1f, x, y, 0f, up.x, up.y, up.z);
			multiplyMM(viewProjectionMatrix, 0, projectionMatrix, 0,
					viewMatrix, 0);
		}
		return viewProjectionMatrix;
	}

	/**
	 * Returns the combined camera matrix with specified parallax factor.
	 * Parallax factor larger than one means the layer scrolls faster than the
	 * focal layer. Parallax factor less than one means the layer scrolls slower
	 * than the focal layer
	 * 
	 * @param parallax
	 *            positive float
	 * @param outMatrix
	 *            float array of size 16
	 * @return combined camera matrix
	 */
	public float[] getCameraMatrix(float parallax, float[] outMatrix) {
		float hw = width / 2 * (1 + (zoom - 1) * parallax);
		float hh = height / 2 * (1 + (zoom - 1) * parallax);
		orthoM(projectionMatrixParallax, 0, -hw, hw, -hh, hh, -1f, 1f);
		float x = position.x * parallax;
		float y = position.y * parallax;
		setLookAtM(viewMatrixParallax, 0, x, y, 1f, x, y, 0f, up.x, up.y, up.z);
		multiplyMM(outMatrix, 0, projectionMatrixParallax, 0,
				viewMatrixParallax, 0);
		return outMatrix;
	}

	/**
	 * Unprojects x y screen coordinates into world coordinates
	 * 
	 * @param x
	 *            coordinate in device coordinates
	 * @param y
	 *            coordinate in device coordinates
	 * @param wScreen
	 *            width of the screen
	 * @param hScreen
	 *            height of the screen
	 * @param cameraMatrix
	 *            combined camera matrix
	 * @return
	 */
	public float[] unproject(int x, int y, int wScreen, int hScreen,
			float[] cameraMatrix) {
		mDevCoords[0] = 1.0f * x / wScreen * 2 - 1;
		mDevCoords[1] = -1.0f * y / hScreen * 2 + 1;
		mDevCoords[2] = 0;
		mDevCoords[3] = 1;
		invertM(invertedVPMatrix, 0, cameraMatrix, 0);
		multiplyMV(mCoords, 0, invertedVPMatrix, 0, mDevCoords, 0);
		return mCoords;
	}
}
