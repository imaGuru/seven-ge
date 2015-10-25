
package com.sevenge.assets;

import java.io.IOException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.opengl.GLES20;

import com.sevenge.IO;
import com.sevenge.graphics.ShaderUtils;
/** Loads Shader instances defined in JSON **/
public class ShaderLoader extends AssetLoader {

	public ShaderLoader (AssetManager as) {
		super(as);
	}

	@Override
	public void load (String content) {
		try {
			JSONArray jarr = new JSONArray(content);
			for (int i = 0; i < jarr.length(); i++) {
				JSONObject jShader = jarr.getJSONObject(i);
				int type = jShader.getString("type").equals("vertex") ? GLES20.GL_VERTEX_SHADER : GLES20.GL_FRAGMENT_SHADER;
				assetManager.registerAsset(jShader.getString("id"),
					new Shader(ShaderUtils.compileShader(IO.readToString(IO.openAsset(jShader.getString("path"))), type), type));
			}
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
