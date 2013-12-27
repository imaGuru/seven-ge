package com.engine.sevenge.graphics;

import java.util.ArrayList;
import java.util.List;

public class Renderer {
	private List<Drawable> RenderQueue;
	private int i;
	Renderer()
	{
		RenderQueue = new ArrayList<Drawable>(50);
	}
	public void render()
	{
		for(i=0;i<RenderQueue.size();i++)
			RenderQueue.get(i).draw();
		RenderQueue.clear();
	}
	public void addToRender(Drawable d)
	{
		RenderQueue.add(d);
	}
	public void addToRender(List<Drawable> ds)
	{
		RenderQueue.addAll(ds);
	}
}
