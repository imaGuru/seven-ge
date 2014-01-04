package resourcemanager;


import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.engine.sevenge.R;

import android.content.Context;

public class ResourceManager {

	public void loadResources(Context ctx) throws JSONException {
		String textureList = readTxt(ctx,R.raw.texture_list);
		JSONArray jArr = new JSONArray(textureList);
		JSONObject jObj;
		for (int i = 0; i < jArr.length(); i++) {
			jObj = jArr.getJSONObject(i);
			String id = jObj.getString("id");
			String path = jObj.getString("path");
			//create texture save it in hashmap
		}
	}

	private String readTxt(Context ctx,int id) {

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
