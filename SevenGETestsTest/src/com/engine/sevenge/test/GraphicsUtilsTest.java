
package com.engine.sevenge.test;

import junit.framework.Assert;
import android.opengl.Matrix;

import com.sevenge.graphics.Camera2D;

public class GraphicsUtilsTest extends BaseOpenGLES20UnitTest {

	public GraphicsUtilsTest () {

	}

	public void testCameraUnprojectDeviceCoordinates () throws Throwable {
		runOnGLThread(new TestWrapper() {
			@Override
			public void executeTest () throws Throwable {
				float[] viewMatrix = new float[16];
				float[] projectionMatrix = new float[16];
				float[] VPM = new float[16];
				float[] invVPM = new float[16];
				float[] devCoords = new float[4];
				float[] unpCoords = new float[4];
				Matrix.invertM(invVPM, 0, VPM, 0);
				Camera2D.lookAt(0, 0, viewMatrix);
				Camera2D.setOrthoProjection(1024, 640, projectionMatrix);
				Camera2D.getVPM(VPM, projectionMatrix, viewMatrix);
				Camera2D.unproject(100, 100, 1024, 640, devCoords, invVPM, unpCoords);
				if (devCoords[0] != -0.8046875f || devCoords[1] != 0.6875) Assert.fail("Bad device coordinates");

			}
		});
	}
}
