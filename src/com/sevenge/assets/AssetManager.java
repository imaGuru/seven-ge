
package com.sevenge.assets;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.sevenge.IO;

public class AssetManager {
	private final String TAG = "AssetManager";

	/** Holds assets using id string as the key and asset as the value of a hashmap */
	private Map<String, Asset> assets = new HashMap<String, Asset>();
	/** Holds asset loaders using type of loaded asset as key and asset as the value */
	private Map<String, AssetLoader> loaders = new HashMap<String, AssetLoader>();

	public AssetManager () {
		loaders.put("texture", new TextureLoader(this));
		loaders.put("program", new TextureShaderProgramLoader(this));
		loaders.put("shader", new ShaderLoader(this));
		loaders.put("audio", new AudioLoader(this));
		// loaders.put("animation", new AnimationLoader(this));
		loaders.put("spriteSheet", new SpriteSheetLoader(this));
	}

	/** Reads the package file specified by the path and loads the specified assets
	 * @param path leading to asset package file */
	public void loadAssets (String path) {
		/*
		 * String content; try { content = IO.readToString(IO.openAsset(path)); JSONObject pkg = new JSONObject(content);
		 * Iterator<?> keys = pkg.keys(); while (keys.hasNext()) { String key = (String)keys.next(); Log.d("ASSETMANAGER",
		 * "Loading " + key); loaders.get(key).load(pkg.getJSONArray(key).toString()); // TODO handle missing loaders } System.gc();
		 * } catch (JSONException e) { e.printStackTrace(); } catch (IOException e1) { // TODO Auto-generated catch block
		 * e1.printStackTrace(); }
		 */
		String content;
		try {
			content = IO.readToString(IO.openAsset(path));
			JSONObject pkg = new JSONObject(content);
			AssetLoader al = loaders.get("texture");
			al.load(pkg.getJSONArray("texture").toString());
			al = loaders.get("spriteSheet");
			al.load(pkg.getJSONArray("spriteSheet").toString());
			al = loaders.get("shader");
			al.load(pkg.getJSONArray("shader").toString());
			al = loaders.get("program");
			al.load(pkg.getJSONArray("program").toString());
			al = loaders.get("audio");
			al.load(pkg.getJSONArray("audio").toString());
			System.gc();
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/** Register the assets in the hashmap
	 * @param key asset id
	 * @param res asset to be stored */
	public void registerAsset (String key, Asset res) {
		assets.put(key, res);
	}

	/** Add a loader to the hashmap
	 * @param key asset type string
	 * @param l loader handling loading of some asset type */
	public void addLoader (String key, AssetLoader l) {
		loaders.put(key, l);
	}

	/** Retrieve assets by their id string
	 * @param id string identifying uniquely an asset
	 * @return requested asset */
	public Asset getAsset (String id) {
		
		if(assets.get(id)==null)// TODO handle missing assets 
			return null;		//this is for tests only
		return assets.get(id);
	}

	/** Remove all loaded assets */
	public void clearAssets () {
		Collection<Asset> cl = assets.values();
		Iterator<Asset> it = cl.iterator();
		while (it.hasNext())
			it.next().dispose();
		assets.clear();
	}
}
