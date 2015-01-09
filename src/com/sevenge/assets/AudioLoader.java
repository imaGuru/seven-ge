
package com.sevenge.assets;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.sevenge.SevenGE;

public class AudioLoader extends AssetLoader {

	public AudioLoader (AssetManager as) {
		super(as);
	}

	@Override
	public void load (String content) {
		try {
			JSONArray jarr = new JSONArray(content);
			for (int i = 0; i < jarr.length(); i++) {
				JSONObject jMusic = jarr.getJSONObject(i);
				assetManager.registerAsset(jMusic.getString("id"), SevenGE.getAudio().getMusic(jMusic.getString("path")));
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

}
