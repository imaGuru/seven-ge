package com.engine.sevenge.resourcemanager;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;

import android.content.Context;

public class ResourceManager {
	private Map<String, Resource> resourceMap = new HashMap<String, Resource>();

	public void loadResources(Context ctx,ResourceManager parent) throws JSONException {
		Loader loader = new Loader();
		loader.loadTextures(ctx,parent);
		loader.loadSpirtes(ctx, parent);
		
		loader.loadAudio(ctx);

	}

	public void putInMap(String key, Resource res) {
		resourceMap.put(key, res);

	}

	public void clearMap() {
		resourceMap.clear();
	}

	public Resource getResource(String id) {
		Resource res;
		res = resourceMap.get(id);
		return res;
	}
}
