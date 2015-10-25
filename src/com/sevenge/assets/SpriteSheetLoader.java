
package com.sevenge.assets;

import java.io.IOException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.sevenge.IO;
import com.sevenge.graphics.TextureRegion;
/** Loads SpriteSheetLoader instances defined in JSON **/
public class SpriteSheetLoader extends AssetLoader {

	public SpriteSheetLoader (AssetManager as) {
		super(as);
	}

	@Override
	public void load (String content) {
		try {
			JSONArray jarr = new JSONArray(content);
			for (int i = 0; i < jarr.length(); i++) {
				JSONObject jSpriteSheet = jarr.getJSONObject(i);
				String spriteSheetData = IO.readToString(IO.openAsset(jSpriteSheet.getString("path")));
				JSONObject jSpriteSheetData = new JSONObject(spriteSheetData);
				String textureId = jSpriteSheetData.getString("textureID");
				JSONArray jTextureRegions = jSpriteSheetData.getJSONArray("textureRegions");
				for (int j = 0; j < jTextureRegions.length(); j++) {
					JSONObject jRegion = jTextureRegions.getJSONObject(j);
					assetManager.registerAsset(jRegion.getString("id"),
						new TextureRegion(jRegion.getInt("width"), jRegion.getInt("height"), jRegion.getInt("x"), jRegion.getInt("y"),
							(Texture)assetManager.getAsset(textureId)));
				}
			}

		} catch (JSONException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
