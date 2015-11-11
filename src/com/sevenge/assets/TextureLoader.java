package com.sevenge.assets;

import java.io.IOException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.sevenge.IO;
import com.sevenge.graphics.TextureUtils;

/** Loads Texture instances defined in JSON **/
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
				assetManager.registerAsset(jTexture.getString("id"),
						TextureUtils.createTexture(IO.openAsset(jTexture
								.getString("path"))));
				Log.d("assets",
						jTexture.getString("id") + " "
								+ jTexture.getString("path"));
			}
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
