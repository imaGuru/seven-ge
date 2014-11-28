
package com.engine.sevenge.assets;

import java.io.IOException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.engine.sevenge.graphics.TextureUtils;
import com.engine.sevenge.io.IO;

public class TextureLoader extends AssetLoader {

	public TextureLoader (AssetManager as) {
		super(as);
	}

	@Override
	public void load (String content) {
		JSONArray jarr;
		try {
			jarr = new JSONArray(content);
			for (int i = 0; i < jarr.length(); i++) {
				JSONObject jTexture = jarr.getJSONObject(i);
				assetManager.registerAsset(jTexture.getString("id"),
					TextureUtils.createTexture(IO.openAsset(jTexture.getString("path"))));
			}
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
