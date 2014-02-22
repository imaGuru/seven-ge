package com.engine.sevenge.graphics;

import static android.opengl.GLES20.GL_BLEND;
import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES20.GL_CULL_FACE;
import static android.opengl.GLES20.GL_ONE;
import static android.opengl.GLES20.GL_ONE_MINUS_SRC_ALPHA;
import static android.opengl.GLES20.glBlendFunc;
import static android.opengl.GLES20.glClear;
import static android.opengl.GLES20.glClearColor;
import static android.opengl.GLES20.glEnable;

import java.util.ArrayList;
import java.util.List;

public class RenderQueue {
	private List<Drawable> queue;
	private int i;

	public RenderQueue() {
		queue = new ArrayList<Drawable>(50);
		// glEnable(GL_CULL_FACE);
		glEnable(GL_BLEND);
		glBlendFunc(GL_ONE, GL_ONE_MINUS_SRC_ALPHA);
		glClearColor(0.0f, 1.0f, 0.0f, 0.0f);
	}

	public void render() {
		glClear(GL_COLOR_BUFFER_BIT);
		for (Drawable currentDrawable : queue)
			currentDrawable.draw();
		queue.clear();
	}

	public void add(Drawable d) {
		queue.add(d);
	}

	public void add(List<Drawable> ds) {
		queue.addAll(ds);
	}
}