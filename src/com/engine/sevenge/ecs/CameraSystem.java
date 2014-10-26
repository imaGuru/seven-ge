
package com.engine.sevenge.ecs;

import static android.opengl.Matrix.invertM;
import static android.opengl.Matrix.multiplyMM;
import static android.opengl.Matrix.setLookAtM;

import java.util.List;

import com.engine.sevenge.SevenGE;
import com.engine.sevenge.graphics.Camera2D;
import com.engine.sevenge.input.InputEvent;

public class CameraSystem extends System {
	private static int SYSTEM_MASK = CameraComponent.MASK | PositionComponent.MASK;
	private float[] coords = new float[4];
	private float[] devCoords = new float[4];

	@Override
	public void process (List<Entity> entities) {
		for (Entity entity : entities) {
			if ((SYSTEM_MASK & entity.mask) == SYSTEM_MASK) {
				List<InputEvent> inputEvents = SevenGE.input.getInputEvents();
				for (InputEvent curIE : inputEvents) {
					if (curIE.type == InputEvent.Type.SCROLL) {
						CameraComponent cc = (CameraComponent)entity.components.get(8);
						Camera2D.unProject((int)(curIE.motionEvent2.getX() + curIE.distX),
							(int)(curIE.motionEvent2.getY() + curIE.distY), cc.width, cc.height, devCoords, cc.invertedVPMatrix, coords);
						float x1 = coords[0];
						float y1 = coords[1];
						Camera2D.unProject((int)curIE.motionEvent2.getX(), (int)curIE.motionEvent2.getY(), cc.width, cc.height,
							devCoords, cc.invertedVPMatrix, coords);
						float x2 = coords[0];
						float y2 = coords[1];
						PositionComponent cp = (PositionComponent)entity.components.get(1);
						cp.x -= x2 - x1;
						cp.y -= y2 - y1;

						setLookAtM(cc.viewMatrix, 0, cp.x, cp.y, 1f, cp.x, cp.y, 0f, 0f, 1.0f, 0.0f);
						multiplyMM(cc.viewProjectionMatrix, 0, cc.projectionMatrix, 0, cc.viewMatrix, 0);
						invertM(cc.invertedVPMatrix, 0, cc.viewProjectionMatrix, 0);
					}
				}
			}
		}

	}

	@Override
	public void handleMessage (Message m, Entity e) {
		// TODO Auto-generated method stub

	}

}
