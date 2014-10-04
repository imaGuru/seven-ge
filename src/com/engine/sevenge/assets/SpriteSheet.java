package com.engine.sevenge.assets;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.engine.sevenge.audio.Audio;
import com.engine.sevenge.graphics.Shader;
import com.engine.sevenge.graphics.SubTexture2D;
import com.engine.sevenge.graphics.Texture2D;
import com.engine.sevenge.graphics.TextureShaderProgram;
import com.engine.sevenge.io.FileHandle;

public class SpriteSheet extends AssetLoader {

	protected SpriteSheet(AssetManager as) {
		super(as);
	}

	@Override
	public void load(String content) {
		String textureId;
		try {
			FileHandle fh;
			JSONArray jarr = new JSONArray(content);
			for (int i = 0; i < jarr.length(); i++) {
				JSONObject jSprite = jarr.getJSONObject(i);
				fh = new FileHandle(jSprite.getString("path"));
				String subtextureContent = fh.readString();
				JSONObject jSubtexture = new JSONObject(subtextureContent);
				textureId = jSubtexture.getString("textureID");
				JSONArray jSubtextureArr = jSubtexture.getJSONArray("subtextures");
				
				for (int j = 0; j < jSubtextureArr.length(); j++) {
						JSONObject jSub = jSubtextureArr.getJSONObject(i);
						assetManager.registerAsset(
							jSub.getString("id"),
							new SubTexture2D(jSub.getString("id"), jSub.getInt("width"), jSub.getInt("height"), jSub.getInt("x"), jSub
								.getInt("y"), (Texture2D)assetManager.getAsset(textureId)));
					}
			}
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

}
