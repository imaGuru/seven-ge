package com.engine.sevenge.resourcemanager;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.opengl.GLES20;

import com.engine.sevenge.SevenGE;
import com.engine.sevenge.graphics.Shader;
import com.engine.sevenge.graphics.Sprite;
import com.engine.sevenge.graphics.Texture2D;
import com.engine.sevenge.graphics.TextureShaderProgram;
import com.engine.sevenge.io.FileHandle;
import com.engine.sevenge.io.IO;

public class ResourceManager {
	private Map<String, Resource> resourceMap = new HashMap<String, Resource>();
	private IO io;
	private int i;

	public ResourceManager() {
		io = SevenGE.io;
	}

	public void loadResources(FileHandle packageFile) {
		String content = packageFile.readString();
		try {
			JSONObject pkg = new JSONObject(content);
			JSONArray jarr = pkg.getJSONArray("textures");
			for (i = 0; i < jarr.length(); i++) {
				JSONObject jTexture = jarr.getJSONObject(i);
				resourceMap.put(jTexture.getString("id"),
						new Texture2D(io.asset(jTexture.getString("path"))));
			}
			jarr = pkg.getJSONArray("sprites");
			for (i = 0; i < jarr.length(); i++) {
				JSONObject jSprite = jarr.getJSONObject(i);
				JSONArray uvs = jSprite.getJSONArray("uvs");
				float[] uv = new float[8];
				for (int j = 0; j < uvs.length(); j++)
					uv[j] = (float) uvs.getDouble(j);
				resourceMap.put(
						jSprite.getString("id"),
						new Sprite((float) jSprite.getDouble("width"),
								(float) jSprite.getDouble("height"), uv,
								(Texture2D) getResource(jSprite
										.getString("textureID")),
								(TextureShaderProgram) getResource(jSprite
										.getString("programID"))));
			}
			jarr = pkg.getJSONArray("shaders");
			for (i = 0; i < jarr.length(); i++) {
				JSONObject jShader = jarr.getJSONObject(i);
				int type = jShader.getString("type").equals("vertex") ? GLES20.GL_VERTEX_SHADER
						: GLES20.GL_FRAGMENT_SHADER;
				resourceMap.put(jShader.getString("id"),
						new Shader(io.asset(jShader.getString("path"))
								.readString(), type));
			}
			jarr = pkg.getJSONArray("programs");
			for (i = 0; i < jarr.length(); i++) {
				JSONObject jProgram = jarr.getJSONObject(i);
				resourceMap.put(
						jProgram.getString("id"),
						new TextureShaderProgram((Shader) getResource(jProgram
								.getString("vertexShader")),
								(Shader) getResource(jProgram
										.getString("fragmentShader"))));
			}
			System.gc();

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void putInMap(String key, Resource res) {
		resourceMap.put(key, res);

	}

	public void clearMap() {
		resourceMap.clear();
	}

	public Resource getResource(String id) {
		Resource res;
		res = resourceMap.get(id);
		return res;
	}
}
