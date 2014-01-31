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

public class GLRenderer {
	private List<Drawable> RenderQueue;
	private int i;

	public GLRenderer() {
		RenderQueue = new ArrayList<Drawable>(50);
		glEnable(GL_CULL_FACE);
		glEnable(GL_BLEND);
		glBlendFunc(GL_ONE, GL_ONE_MINUS_SRC_ALPHA);
		glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
	}

	public void render() {
		glClear(GL_COLOR_BUFFER_BIT);
		for (i = 0; i < RenderQueue.size(); i++)
			RenderQueue.get(i).draw();
		RenderQueue.clear();
	}

	public void addToRender(Drawable d) {
		RenderQueue.add(d);
	}

	public void addToRender(List<Drawable> ds) {
		RenderQueue.addAll(ds);
	}
}