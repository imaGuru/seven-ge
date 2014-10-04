
package com.engine.sevenge.assets;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.engine.sevenge.audio.Audio;
import com.engine.sevenge.graphics.Shader;
import com.engine.sevenge.graphics.SubTexture2D;
import com.engine.sevenge.graphics.Texture2D;
import com.engine.sevenge.graphics.TextureShaderProgram;
import com.engine.sevenge.io.FileHandle;

public class AssetManager {
	private Map<String, Asset> assets = new HashMap<String, Asset>();
	private Map<String, AssetLoader> loaders = new HashMap<String, AssetLoader>();

	private final String TAG = "AssetManager";

	public AssetManager () {
		loaders.put(Texture2D.class.getName(), new TextureLoader(this));
		//loaders.put(SubTexture2D.class.getName(), new SubTextureLoader(this));
		loaders.put(TextureShaderProgram.class.getName(), new TextureShaderProgramLoader(this));
		loaders.put(Shader.class.getName(), new ShaderLoader(this));
		loaders.put(Audio.class.getName(), new AudioLoader(this));
		loaders.put(Animation.class.getName(), new AnimationLoader(this));
	}

	public void loadAssets (FileHandle packageFile) {
		String content = packageFile.readString();
		try {
			JSONObject pkg = new JSONObject(content);
			AssetLoader al = loaders.get(Texture2D.class.getName());
			al.load(pkg.getJSONArray("textures").toString());
			//
			al = loaders.get(SpriteSheet.class.getName());
			al.load(pkg.getJSONArray("spritesheet").toString());
			//
			/*
			al = loaders.get(SubTexture2D.class.getName());
			al.load(pkg.getJSONArray("subtextures").toString());
			*/
			al = loaders.get(Shader.class.getName());
			al.load(pkg.getJSONArray("shaders").toString());
			al = loaders.get(TextureShaderProgram.class.getName());
			al.load(pkg.getJSONArray("programs").toString());
			al = loaders.get(Audio.class.getName());
			al.load(pkg.getJSONArray("audio").toString());
			System.gc();
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public void registerAsset (String key, Asset res) {
		assets.put(key, res);
	}

	public void clearAssets () {
		Collection<Asset> cl = assets.values();
		Iterator<Asset> it = cl.iterator();
		while (it.hasNext())
			it.next().dispose();
		assets.clear();
	}

	public Asset getAsset (String id) {
		return assets.get(id);
	}
}
