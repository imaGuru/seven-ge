
package com.engine.sevenge.graphics;

import static android.opengl.Matrix.multiplyMM;
import static android.opengl.Matrix.multiplyMV;
import static android.opengl.Matrix.orthoM;
import static android.opengl.Matrix.setLookAtM;

public class Camera2D {

	public static void setOrthoProjection (float width, float height, float[] projectionMatrix) {
		orthoM(projectionMatrix, 0, -width / 2, width / 2, -height / 2, height / 2, 0f, 1f);
	}

	public static void lookAt (float x, float y, float[] viewMatrix) {
		setLookAtM(viewMatrix, 0, x, y, 1f, x, y, 0f, 0f, 1.0f, 0.0f);
	}

	public static void unProject (int x, int y, int w, int h, float[] devCoords, float[] invertedVPM, float[] unpCoords) {
		devCoords[0] = x / w * 2 - 1;
		devCoords[1] = -y / h * 2 + 1;
		devCoords[2] = 1;
		devCoords[3] = 1;
		multiplyMV(unpCoords, 0, invertedVPM, 0, devCoords, 0);
	}

	public static void getVPM (float[] VPM, float[] PM, float[] VM) {
		multiplyMM(VPM, 0, PM, 0, VM, 0);
	}
}
