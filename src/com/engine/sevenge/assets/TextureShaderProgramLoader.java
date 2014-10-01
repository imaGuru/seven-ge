
package com.engine.sevenge.assets;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.engine.sevenge.graphics.Shader;
import com.engine.sevenge.graphics.TextureShaderProgram;

public class TextureShaderProgramLoader extends AssetLoader {

	protected TextureShaderProgramLoader (AssetManager as) {
		super(as);
	}

	@Override
	public void load (String content) {
		try {
			JSONArray jarr = new JSONArray(content);
			for (int i = 0; i < jarr.length(); i++) {
				JSONObject jProgram = jarr.getJSONObject(i);
				assetManager.registerAsset(
					jProgram.getString("id"),
					new TextureShaderProgram((Shader)assetManager.getAsset(jProgram.getString("vertexShader")), (Shader)assetManager
						.getAsset(jProgram.getString("fragmentShader"))));
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

}
