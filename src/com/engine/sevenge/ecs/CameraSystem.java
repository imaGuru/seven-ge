
package com.engine.sevenge.ecs;

import static android.opengl.Matrix.invertM;
import static android.opengl.Matrix.multiplyMM;
import static android.opengl.Matrix.setLookAtM;

import java.util.List;

import android.view.MotionEvent;

import com.engine.sevenge.SevenGE;
import com.engine.sevenge.graphics.Camera2D;
import com.engine.sevenge.input.GestureProcessor;

public class CameraSystem extends System implements GestureProcessor {
	private static int SYSTEM_MASK = CameraComponent.MASK | PositionComponent.MASK;
	private float[] coords = new float[4];

	boolean scrolled = false;
	float me2x;
	float me2y;
	float distX;
	float distY;

	public CameraSystem () {
		SevenGE.input.setGestureProcessor(this);
	}

	@Override
	public void process (List<Entity> entities) {
		for (Entity entity : entities) {
			if ((SYSTEM_MASK & entity.mask) == SYSTEM_MASK) {
				if (scrolled) {
					CameraComponent cc = (CameraComponent)entity.components.get(8);
					Camera2D.unProject(me2x + distX, me2y + distY, cc.width, cc.height, cc.invertedVPMatrix, coords);
					float x1 = coords[0];
					float y1 = coords[1];
					Camera2D.unProject(me2x, me2y, cc.width, cc.height, cc.invertedVPMatrix, coords);
					float x2 = coords[0];
					float y2 = coords[1];
					PositionComponent cp = (PositionComponent)entity.components.get(1);
					cp.x -= x2 - x1;
					cp.y -= y2 - y1;

					setLookAtM(cc.viewMatrix, 0, cp.x, cp.y, 1f, cp.x, cp.y, 0f, 0f, 1.0f, 0.0f);
					multiplyMM(cc.viewProjectionMatrix, 0, cc.projectionMatrix, 0, cc.viewMatrix, 0);
					invertM(cc.invertedVPMatrix, 0, cc.viewProjectionMatrix, 0);

					scrolled = false;
				}
			}
		}

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

		scrolled = true;

		this.me2x = me2.getX();
		this.me2y = me2.getY();
		this.distX = distX;
		this.distY = distY;

		return false;
	}
}
