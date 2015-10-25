
package com.sevenge.assets;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.sevenge.SevenGE;
/** Loads Audio instances defined in JSON **/
public class AudioLoader extends AssetLoader {

	public AudioLoader (AssetManager as) {
		super(as);
	}

	@Override
	public void load (String content) {
		try {
			JSONArray jarr = new JSONArray(content);
			for (int i = 0; i < jarr.length(); i++) {
				JSONObject jAudio = jarr.getJSONObject(i);

				if (jAudio.getString("type").equals("music")) {
					assetManager.registerAsset(jAudio.getString("id"), SevenGE.getAudio().getMusic(jAudio.getString("path")));
				} else if (jAudio.getString("type").equals("sound")) {
					assetManager.registerAsset(jAudio.getString("id"), SevenGE.getAudio().getSound(jAudio.getString("path")));
				}

			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
}
