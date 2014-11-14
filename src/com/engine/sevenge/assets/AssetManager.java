
package com.engine.sevenge.assets;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.engine.sevenge.io.IO;

public class AssetManager {
	private final String TAG = "AssetManager";

	private Map<String, Asset> assets = new HashMap<String, Asset>();
	private Map<String, AssetLoader> loaders = new HashMap<String, AssetLoader>();

	public AssetManager () {
		loaders.put("texture", new TextureLoader(this));
		loaders.put("textureRegion", new SpriteSheetLoader(this));
		loaders.put("shaderProgram", new TextureShaderProgramLoader(this));
		loaders.put("shader", new ShaderLoader(this));
		loaders.put("audio", new AudioLoader(this));
		loaders.put("animation", new AnimationLoader(this));
		loaders.put("spriteSheet", new SpriteSheetLoader(this));
	}

	public void loadAssets (String path) {
		String content;
		try {
			content = IO.readToString(IO.openAsset(path));
			JSONObject pkg = new JSONObject(content);
			Iterator<?> keys = pkg.keys();
			while (keys.hasNext()) {
				String key = (String)keys.next();
				Log.d("ASSETMANAGER", "Loading " + key);
				loaders.get(key).load(pkg.getJSONArray(key).toString()); // TODO handle missing loaders
			}
			System.gc();
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	public void registerAsset (String key, Asset res) {
		assets.put(key, res);
	}

	public void addLoader (String key, AssetLoader l) {
		loaders.put(key, l);
	}

	public Asset getAsset (String id) {
		return assets.get(id);// TODO handle missing assets
	}

	public void clearAssets () {
		Collection<Asset> cl = assets.values();
		Iterator<Asset> it = cl.iterator();
		while (it.hasNext())
			it.next().dispose();
		assets.clear();
	}
}
