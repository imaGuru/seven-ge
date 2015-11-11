package com.sevenge.assets;

import java.io.IOException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.sevenge.IO;
import com.sevenge.graphics.TextureRegion;
import com.sevenge.utils.DebugLog;

/** Loads SpriteSheetFTLoader instances defined in JSON **/
public class SpriteSheetFTLoader extends AssetLoader {

	public SpriteSheetFTLoader(AssetManager as) {
		super(as);
	}

	@Override
	public void load(String content) {
		try {
			JSONArray jarr = new JSONArray(content);
			for (int i = 0; i < jarr.length(); i++) {
				JSONObject jSpriteSheet = jarr.getJSONObject(i);
				String spriteSheetData = IO.readToString(IO
						.openAsset(jSpriteSheet.getString("path")));
				JSONObject jSpriteSheetData = new JSONObject(spriteSheetData);
				String textureId = jSpriteSheetData.getString("textureID");
				JSONArray jFrames = jSpriteSheetData.getJSONArray("frames");
				for (int j = 0; j < jFrames.length(); j++) {
					JSONObject jRegion = jFrames.getJSONObject(j);
					JSONObject prop = jRegion.getJSONObject("frame");
					assetManager.registerAsset(
							jRegion.getString("filename"),
							new TextureRegion(prop.getInt("w"), prop
									.getInt("h"), prop.getInt("x"), prop
									.getInt("y"), (Texture) assetManager
									.getAsset(textureId)));
					DebugLog.d("assets", jRegion.getString("filename")
							+ " loaded ");

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
