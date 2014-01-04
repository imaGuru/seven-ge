package resourcemanager;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;


import android.content.Context;

public class ResourceManager {
	private Map<String, Resource> resourceMap = new HashMap<String, Resource>();

	public void loadResources(Context ctx) throws JSONException{
		Loader loader = new Loader();
		loader.loadTextures(ctx);
		loader.loadAudio(ctx);

	}

	public void putInMap(String key, Resource res) {
		resourceMap.put(key, res);

	}
}
