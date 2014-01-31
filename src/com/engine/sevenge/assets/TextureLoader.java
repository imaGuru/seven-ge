package com.engine.sevenge.assets;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.engine.sevenge.SevenGE;
import com.engine.sevenge.graphics.Texture2D;

public class TextureLoader extends AssetLoader {

	public TextureLoader(AssetManager as) {
		super(as);
	}

	@Override
	public void load(String content) {
		JSONArray jarr;
		try {
			jarr = new JSONArray(content);
			for (int i = 0; i < jarr.length(); i++) {
				JSONObject jTexture = jarr.getJSONObject(i);
				assetManager.registerAsset(
						jTexture.getString("id"),
						new Texture2D(SevenGE.io.asset(jTexture
								.getString("path"))));
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

}
