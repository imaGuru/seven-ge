
package com.sevenge.sample;

import java.util.Comparator;

import com.sevenge.graphics.Sprite;
import com.sevenge.graphics.SpriteBatch;
import com.sevenge.utils.FixedSizeArray;

/** Class responsible for holding layers */
public class Layer {
	public final int id;
	public final float parallaxFactor;
	public final FixedSizeArray<Sprite> sprites;
	public FixedSizeArray<SpriteBatch> batches;

	public Layer (int id, float parallaxFactor, int capacity) {
		this.id = id;
		this.parallaxFactor = parallaxFactor;
		sprites = new FixedSizeArray<Sprite>(capacity, Sprite.SortByTexture);
	}

	public void addSprite (Sprite sprite) {
		sprites.add(sprite);
	}

	public static Comparator<Layer> Sorter = new Comparator<Layer>() {
		@Override
		public int compare (Layer lhs, Layer rhs) {
			return lhs.id - rhs.id;
		}
	};
}
