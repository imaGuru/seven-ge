
package com.sevenge.ecs;

import android.opengl.Matrix;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;

import com.sevenge.SevenGE;
import com.sevenge.graphics.Camera2D;
import com.sevenge.input.GestureProcessor;

public class CameraSystem extends System implements GestureProcessor {

	private float[] coords = new float[4];
	private float[] devCoords = new float[4];

	boolean scrolled = false;
	float me2x;
	float me2y;
	float distX;
	float distY;
	Entity camera = null;

	public CameraSystem (Entity camera) {
		super(CameraComponent.MASK, 0);
		this.camera = camera;
		SevenGE.input.addGestureProcessor(this);
	}

	public void setCamera (Entity camera) {
		this.camera = camera;
	}

	public void process () {
		PositionComponent cp = (PositionComponent)camera.components[0];
		cp.px = cp.x;
		cp.py = cp.y;
		CameraComponent cc = (CameraComponent)camera.components[2];
		Matrix.invertM(cc.invertedVPMatrix, 0, cc.viewProjectionMatrix, 0);
	}

	@Override
	public void handleMessage (Message m, Entity e) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean onDoubleTap (MotionEvent arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onSingleTapConfirmed (MotionEvent arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onFling (MotionEvent arg0, MotionEvent arg1, float arg2, float arg3) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onLongPress (MotionEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean onScroll (MotionEvent me1, MotionEvent me2, float distX, float distY) {

		this.me2x = me2.getX();
		this.me2y = me2.getY();
		this.distX = distX;
		this.distY = distY;

		CameraComponent cc = (CameraComponent)camera.components[2];
		Camera2D.unproject((int)(me2x + distX), (int)(me2y + distY), cc.width, cc.height, devCoords, cc.invertedVPMatrix, coords);
		float x1 = coords[0];
		float y1 = coords[1];
		Camera2D.unproject((int)me2x, (int)me2y, cc.width, cc.height, devCoords, cc.invertedVPMatrix, coords);
		float x2 = coords[0];
		float y2 = coords[1];
		PositionComponent cp = (PositionComponent)camera.components[0];
		cp.px = cp.x;
		cp.py = cp.y;
		cp.x -= x2 - x1;
		cp.y -= y2 - y1;

		return false;
	}

	@Override
	public boolean onScale (ScaleGestureDetector detector) {
		CameraComponent cc = (CameraComponent)camera.components[2];
		float scale;
		float fx = detector.getCurrentSpanX();
		float fy = detector.getCurrentSpanY();
		Camera2D.unproject((int)(fx), (int)(fy), cc.width, cc.height, devCoords, cc.invertedVPMatrix, coords);
		float x1 = coords[0];
		float y1 = coords[1];
		fx = detector.getPreviousSpanX();
		fy = detector.getPreviousSpanY();
		Camera2D.unproject((int)(fx), (int)(fy), cc.width, cc.height, devCoords, cc.invertedVPMatrix, coords);
		float x2 = coords[0];
		float y2 = coords[1];
		scale = (float)(Math.sqrt(x1 * x1 + y1 * y1) / Math.sqrt(x2 * x2 + y2 * y2));
		// Don't let the object get too small or too large.
		cc.scale = Math.max(0.1f, Math.min(cc.scale * detector.getScaleFactor(), 10.0f));
		// Log.d("PINCH", "pinch detected " + cc.scale);
		Camera2D.setOrthoProjection(cc.width / cc.scale, cc.height / cc.scale, cc.projectionMatrix);
		Camera2D.getVPM(cc.viewProjectionMatrix, cc.projectionMatrix, cc.viewMatrix);
		Matrix.invertM(cc.invertedVPMatrix, 0, cc.viewProjectionMatrix, 0);
		return false;
	}
}
