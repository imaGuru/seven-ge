package com.engine.sevenge.assets;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.engine.sevenge.graphics.Sprite;
import com.engine.sevenge.graphics.SubTexture2D;
import com.engine.sevenge.graphics.Texture2D;
import com.engine.sevenge.graphics.TextureShaderProgram;

public class SubTextureLoader extends AssetLoader {

	public SubTextureLoader(AssetManager as) {
		super(as);
	}

	@Override
	public void load(String content) {
		try {
			JSONArray jarr = new JSONArray(content);
			for (int i = 0; i < jarr.length(); i++) {
				JSONObject jSub = jarr.getJSONObject(i);
				assetManager.registerAsset(
						jSub.getString("id"),
						new SubTexture2D(jSub.getString("id"), jSub
								.getInt("width"), jSub.getInt("height"), jSub
								.getInt("x"), jSub.getInt("y"),
								(Texture2D) assetManager.getAsset(jSub
										.getString("textureID"))));
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
}
