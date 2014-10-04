package com.engine.sevenge.assets;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.engine.sevenge.SevenGE;
import com.engine.sevenge.audio.Audio;
import com.engine.sevenge.graphics.Shader;
import com.engine.sevenge.graphics.SubTexture2D;
import com.engine.sevenge.graphics.Texture2D;
import com.engine.sevenge.graphics.TextureShaderProgram;
import com.engine.sevenge.io.FileHandle;

public class SpriteSheetLoader extends AssetLoader {

	protected SpriteSheetLoader(AssetManager as) {
		super(as);
	}

	@Override
	public void load(String content) {
		String textureId;
		try {
			FileHandle fileh;
			JSONArray jarr = new JSONArray(content);
			Log.d("CHEEECK   :  ","IN LOAD FUNCTION" );
			Log.d("CHEEECK   :  ",Integer.toString(jarr.length()) );
			Log.d("CHEEECK   :  ",content );
			for (int i = 0; i < jarr.length(); i++) {
				JSONObject jSprite = jarr.getJSONObject(i);
				fileh = SevenGE.io.asset(jSprite.get("path").toString());
				Log.d("CHEEECK",jSprite.get("path").toString() );
				String subtextureContent = fileh.readString();
				JSONObject jSubtexture = new JSONObject(subtextureContent);
				textureId = jSubtexture.getString("textureID");
				Log.d("CHEEECK",jSubtexture.getString("textureID") );
				JSONArray jSubtextureArr = jSubtexture.getJSONArray("subtextures");
				Log.d("CHEEECK   :  ",Integer.toString(jSubtextureArr.length()) );
				for (int j = 0; j < jSubtextureArr.length(); j++) {
						JSONObject jSub = jSubtextureArr.getJSONObject(j);
						Log.d("CHEEECK   :  ",Integer.toString(j)+jSubtextureArr.getJSONObject(j).toString() );
						assetManager.registerAsset(
							jSub.getString("id"),
							new SubTexture2D(jSub.getString("id"), jSub.getInt("width"), jSub.getInt("height"), jSub.getInt("x"), jSub
								.getInt("y"), (Texture2D)assetManager.getAsset(textureId)));
					}
			}
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

}
