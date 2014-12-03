
package com.sevenge.assets;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.sevenge.graphics.TextureShaderProgram;

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
				Shader v = (Shader)assetManager.getAsset(jProgram.getString("vertexShader"));
				Shader f = (Shader)assetManager.getAsset(jProgram.getString("fragmentShader"));
				assetManager.registerAsset(jProgram.getString("id"), new TextureShaderProgram(v.glID, f.glID));
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

}
