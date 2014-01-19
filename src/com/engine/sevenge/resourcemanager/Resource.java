package com.engine.sevenge.resourcemanager;

public abstract class Resource {
	public final String ID;

	public Resource(String id) {
		ID = id;
	}

	public abstract void dispose();
}
