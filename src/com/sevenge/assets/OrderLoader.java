
package com.sevenge.assets;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.sevenge.SevenGE;

public class OrderLoader extends AssetLoader {

	protected OrderLoader (AssetManager as) {
		super(as);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void load (String content) {
		JSONArray jarr;
		try {
			jarr = new JSONArray(content);
			for (int i = 0; i < jarr.length(); i++) {
				JSONObject jAsset = jarr.getJSONObject(i);
				SevenGE.getAssetManager().addLoaderToList(jAsset.getString("key"));
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
}
