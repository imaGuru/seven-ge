
package com.sevenge.assets;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
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

	private List<String> order = new ArrayList<String>();

	public AssetManager () {
		loaders.put("assets", new OrderLoader(this));
	}

	/** Reads the package file specified by the path and loads the specified assets
	 * @param path leading to asset package file */
	public void loadAssets (String path) {
		String content;
		try {
			content = IO.readToString(IO.openAsset(path));

			JSONObject pkg = new JSONObject(content);

			AssetLoader al = loaders.get("assets");

			al.load(pkg.getJSONArray("assets").toString());

			for (String temp : order) {

				al = loaders.get(temp);

				al.load(pkg.getJSONArray(temp).toString());
			}

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

		if (assets.get(id) == null) // TODO handle missing assets
			return null; // this is for tests only
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

	/** Add loader key to order list */
	public void addLoaderToList (String key) {
		order.add(key);
	}

}
