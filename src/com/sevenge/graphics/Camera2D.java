
package com.sevenge.graphics;

import static android.opengl.Matrix.multiplyMM;
import static android.opengl.Matrix.multiplyMV;
import static android.opengl.Matrix.orthoM;
import static android.opengl.Matrix.setLookAtM;

public class Camera2D {

	/** Creates a new orthographic projection matrix in the last parameter
	 * @param width of the visible area
	 * @param height of the visible area
	 * @param projectionMatrix OUT parameter with the orthographic projection matrix */
	public static void setOrthoProjection (float width, float height, float[] projectionMatrix) {
		orthoM(projectionMatrix, 0, -width / 2, width / 2, -height / 2, height / 2, 0f, 1f);
	}

	/** Creates a view matrix looking at point (x,y) in the last parameter
	 * @param x coordinate of the point we are looking at
	 * @param y coordinate of the point we are looking at
	 * @param viewMatrix */
	public static void lookAt (float x, float y, float[] viewMatrix) {
		setLookAtM(viewMatrix, 0, x, y, 1f, x, y, 0f, 0f, 1.0f, 0.0f);
	}

	/** Unprojects x y screen coordinates into world coordinates
	 * @param x coordinate in device coordinates
	 * @param y coordinate in device coordinates
	 * @param w width of the screen
	 * @param h height of the screen
	 * @param devCoords temporary array of length 4 to avoid constant allocation in the method
	 * @param invertedVPM inverted viewProjection matrix
	 * @param unpCoords output in world coordinates. Array has to be of length 4 */
	public static void unproject (int x, int y, int w, int h, float[] devCoords, float[] invertedVPM, float[] unpCoords) {
		devCoords[0] = 1.0f * x / w * 2 - 1;
		devCoords[1] = -1.0f * y / h * 2 + 1;
		devCoords[2] = 0;
		devCoords[3] = 1;
		multiplyMV(unpCoords, 0, invertedVPM, 0, devCoords, 0);
	}

	/** Combines view and projection matrices into single view projection matrix
	 * @param VPM output view projection matrix
	 * @param PM projection matrix
	 * @param VM view matrix */
	public static void getVPM (float[] VPM, float[] PM, float[] VM) {
		multiplyMM(VPM, 0, PM, 0, VM, 0);
	}
}
