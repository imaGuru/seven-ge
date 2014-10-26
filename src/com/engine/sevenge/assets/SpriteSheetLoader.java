
package com.engine.sevenge.assets;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.engine.sevenge.SevenGE;
import com.engine.sevenge.graphics.TextureRegion;
import com.engine.sevenge.graphics.Texture;
import com.engine.sevenge.io.FileHandle;

public class SpriteSheetLoader extends AssetLoader {

	protected SpriteSheetLoader (AssetManager as) {
		super(as);
	}

	@Override
	public void load (String content) {
		String textureId;
		try {
			FileHandle fileh;
			JSONArray jarr = new JSONArray(content);
			for (int i = 0; i < jarr.length(); i++) {
				JSONObject jSprite = jarr.getJSONObject(i);
				fileh = SevenGE.io.asset(jSprite.get("path").toString());
				String subtextureContent = fileh.readString();
				JSONObject jSubtexture = new JSONObject(subtextureContent);
				textureId = jSubtexture.getString("textureID");
				JSONArray jSubtextureArr = jSubtexture.getJSONArray("subtextures");
				for (int j = 0; j < jSubtextureArr.length(); j++) {
					JSONObject jSub = jSubtextureArr.getJSONObject(j);
					assetManager.registerAsset(
						jSub.getString("id"),
						new TextureRegion(jSub.getString("id"), jSub.getInt("width"), jSub.getInt("height"), jSub.getInt("x"), jSub
							.getInt("y"), (Texture)assetManager.getAsset(textureId)));
				}
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

}
