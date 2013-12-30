package com.engine.sevenge.graphics;

import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES20.glClear;

import java.util.ArrayList;
import java.util.List;

public class LRenderer {
	private List<Drawable> RenderQueue;
	private int i;

	public LRenderer() {
		RenderQueue = new ArrayList<Drawable>(50);
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
