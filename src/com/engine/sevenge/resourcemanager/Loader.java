package com.engine.sevenge.resourcemanager;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.engine.sevenge.R;

import android.content.Context;

public class Loader {

	public void loadTextures(Context ctx, ResourceManager parent)
			throws JSONException {
		String textureList = readTxt(ctx, R.raw.texture_list);
		JSONArray jArr = new JSONArray(textureList);

		JSONObject jObj;
		for (int i = 0; i < jArr.length(); i++) {
			jObj = jArr.getJSONObject(i);
			if(jObj!=null){
			String id = jObj.getString("id");
			String path = jObj.getString("path");
			// create texture than save it in hashmap
			// parent.putInMap(id, texture);
			}
			else{
				
			}
		}

	}

	@SuppressWarnings("null")
	public void loadSpirtes(Context ctx, ResourceManager parent)
			throws JSONException {
		String textureList = readTxt(ctx, R.raw.sprite_list);
		JSONArray jArr = new JSONArray(textureList);

		JSONObject jObj = null;
		for (int i = 0; i < jArr.length(); i++) {
			jObj = jArr.getJSONObject(i);
			if(jObj!=null){
			int[] coord = null;
			
			String id = jObj.getString("id");
			int height = jObj.getInt("height");
			int width = jObj.getInt("width");
			coord[1] = jObj.getInt("x");
			coord[2] = jObj.getInt("y");
			coord[3] = jObj.getInt("z");
			coord[4] = jObj.getInt("w");

			// create texture than save it in hashmap
			Resource newSprite = null;
			//use sprite constructor here
			
			parent.putInMap(id, newSprite);
			}
		}

	}

	public void loadAudio(Context ctx) throws JSONException {
		String audioList = readTxt(ctx, R.raw.audio_list);
		JSONArray jArr = new JSONArray(audioList);

		JSONObject jObj;
		for (int i = 0; i < jArr.length(); i++) {
			jObj = jArr.getJSONObject(i);
			// String id = jObj.getString("id");
			// String path = jObj.getString("path");
			// create audio than save it in hashmap
			// resourceMap.put(id, resource)
		}

	}

	private String readTxt(Context ctx, int id) {

		InputStream is;
		String content = "";
		try {
			is = ctx.getResources().openRawResource(id);

			byte[] input = new byte[is.available()];
			while (is.read(input) != -1) {
			}
			content += new String(input);
			is.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return content;

	}
}
