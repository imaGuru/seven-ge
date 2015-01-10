
package com.sevenge.sample;

import static android.opengl.GLES20.GL_STATIC_DRAW;

import java.io.IOException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.sevenge.IO;
import com.sevenge.SevenGE;
import com.sevenge.graphics.Sprite;
import com.sevenge.graphics.SpriteBatch;
import com.sevenge.graphics.TextureRegion;
import com.sevenge.utils.FixedSizeArray;

public class MapLoader {

	private final FixedSizeArray<Layer> layers;

	public MapLoader () {
		layers = new FixedSizeArray<Layer>(10, Layer.Sorter);
	}

	public void load (String path) {
		try {
			String map = IO.readToString(IO.openAsset("map.json"));
			JSONArray maparr = new JSONArray(map);
			for (int i = 0; i < maparr.length(); i++) {
				JSONObject jlayer = maparr.getJSONObject(i);
				JSONArray jsprites = jlayer.getJSONArray("sprites");
				Layer layer = new Layer(jlayer.getInt("id"), (float)jlayer.getDouble("parallax"), jsprites.length());
				layers.add(layer);
				for (int j = 0; j < jsprites.length(); j++) {
					JSONObject jsprite = jsprites.getJSONObject(j);
					TextureRegion tex = (TextureRegion)SevenGE.getAssetManager().getAsset(jsprite.getString("textureRegion"));
					Sprite sprite = new Sprite(tex);
					sprite.setScale((float)jsprite.getDouble("scaleX"), (float)jsprite.getDouble("scaleY"));
					sprite.setRotation((float)Math.toRadians(jsprite.getDouble("rotation")));
					sprite.setCenter((float)jsprite.getDouble("x"), (float)jsprite.getDouble("y"));
					layer.addSprite(sprite);
				}
				layer.sprites.sort(false);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public FixedSizeArray<Layer> getLayers () {
		for (int i = 0; i < layers.getCount(); i++) {
			Layer layer = layers.get(i);
			layer.batches = new FixedSizeArray<SpriteBatch>(10, null);
			int texture = layer.sprites.get(0).texture;
			SpriteBatch batch = new SpriteBatch(layer.sprites.getCount(), GL_STATIC_DRAW);
			layer.batches.add(batch);
			for (int j = 0; j < layer.sprites.getCount(); j++) {
				Sprite sprite = layer.sprites.get(j);
				if (texture != sprite.texture) {
					batch = new SpriteBatch(layer.sprites.getCount() - j, GL_STATIC_DRAW);
					layer.batches.add(batch);
				}
				batch.addSprite(sprite);
			}
		}
		layers.sort(false);
		return layers;
	}
}
