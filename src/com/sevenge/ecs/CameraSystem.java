
package com.sevenge.ecs;

import android.opengl.Matrix;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;

import com.sevenge.SevenGE;
import com.sevenge.graphics.Camera2D;
import com.sevenge.input.GestureProcessor;

public class CameraSystem extends SubSystem implements GestureProcessor {

	private float[] mCoords = new float[4];
	private float[] mDevCoords = new float[4];
	Entity mCamera = null;

	public CameraSystem (Entity camera) {
		super(CameraComponent.MASK, 0);
		this.mCamera = camera;
		SevenGE.input.addGestureProcessor(this);
	}

	public void setCamera (Entity camera) {
		this.mCamera = camera;
	}

	public void process () {
		PositionComponent cp = (PositionComponent)mCamera.mComponents[0];
		cp.px = cp.x;
		cp.py = cp.y;
		CameraComponent cc = (CameraComponent)mCamera.mComponents[2];
		Matrix.invertM(cc.invertedVPMatrix, 0, cc.viewProjectionMatrix, 0);
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

		float me2x = me2.getX();
		float me2y = me2.getY();

		CameraComponent cc = (CameraComponent)mCamera.mComponents[2];
		Camera2D.unproject((int)(me2x + distX), (int)(me2y + distY), cc.width, cc.height, mDevCoords, cc.invertedVPMatrix, mCoords);
		float x1 = mCoords[0];
		float y1 = mCoords[1];
		Camera2D.unproject((int)me2x, (int)me2y, cc.width, cc.height, mDevCoords, cc.invertedVPMatrix, mCoords);
		float x2 = mCoords[0];
		float y2 = mCoords[1];
		PositionComponent cp = (PositionComponent)mCamera.mComponents[0];
		cp.px = cp.x;
		cp.py = cp.y;
		cp.x -= x2 - x1;
		cp.y -= y2 - y1;

		return false;
	}

	@Override
	public boolean onScale (ScaleGestureDetector detector) {
		CameraComponent cc = (CameraComponent)mCamera.mComponents[2];
		cc.scaledWidth = (int)(cc.scaledWidth / detector.getScaleFactor());
		cc.scaledHeight = (int)(cc.scaledHeight / detector.getScaleFactor());
		Camera2D.setOrthoProjection(cc.scaledWidth, cc.scaledHeight, cc.projectionMatrix);
		Camera2D.getVPM(cc.viewProjectionMatrix, cc.projectionMatrix, cc.viewMatrix);
		Matrix.invertM(cc.invertedVPMatrix, 0, cc.viewProjectionMatrix, 0);
		return false;
	}

	@Override
	public void onScaleEnd (ScaleGestureDetector detector) {

	}

	@Override
	public void onScaleBegin (ScaleGestureDetector detector) {

	}
}
