package com.engine.sevenge.assets;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.engine.sevenge.graphics.Sprite;
import com.engine.sevenge.graphics.Texture2D;
import com.engine.sevenge.graphics.TextureShaderProgram;

public class SpriteLoader extends AssetLoader {

	public SpriteLoader(AssetManager as) {
		super(as);
	}

	@Override
	public void load(String content) {
		try {
			JSONArray jarr = new JSONArray(content);
			for (int i = 0; i < jarr.length(); i++) {
				JSONObject jSprite = jarr.getJSONObject(i);
				JSONArray uvs = jSprite.getJSONArray("uvs");
				float[] uv = new float[8];
				for (int j = 0; j < uvs.length(); j++)
					uv[j] = (float) uvs.getDouble(j);
				assetManager.registerAsset(
						jSprite.getString("id"),
						new Sprite((float) jSprite.getDouble("width"),
								(float) jSprite.getDouble("height"), uv,
								(Texture2D) assetManager.getAsset(jSprite
										.getString("textureID")),
								(TextureShaderProgram) assetManager
										.getAsset(jSprite
												.getString("programID"))));
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

}
