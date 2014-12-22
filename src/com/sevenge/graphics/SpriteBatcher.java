
package com.sevenge.graphics;

import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES20.glClear;
import android.opengl.Matrix;

import com.sevenge.SevenGE;
import com.sevenge.assets.Font;
import com.sevenge.assets.TextureRegion;

/** Class responsible for creating sprite batches and puting sprites in the correct batches for fast and easy drawing */
public class SpriteBatcher {
	private SpriteBatch[] mBatches;
	private int mUsedBatches;
	private int mCurrentGlid = -1;

	private float[] uvs;
	private float[] v;
	private float[] transform = new float[16];
	private float[] r = new float[8], temp = new float[4], tempr = new float[4];
	private float[] t = new float[16];

	/** Creates a new spriteBatcher
	 * @param size number of batches
	 * @param batchSizeHint hint number of sprites in a batch */
	public SpriteBatcher (int sizeHint, int batchSizeHint) {
		mUsedBatches = -1;
		mBatches = new SpriteBatch[sizeHint];
		TextureShaderProgram tsp = (TextureShaderProgram)SevenGE.assetManager.getAsset("spriteShader");
		for (int i = 0; i < sizeHint; i++) {
			mBatches[i] = new SpriteBatch(batchSizeHint);
			mBatches[i].mProgram = tsp;
		}
	}

	/** Draw the created batches using specified view projection matrix
	 * @param vpm view projection matrix */
	public void flush (float[] vpm) {
		glClear(GL_COLOR_BUFFER_BIT);
		for (int i = 0; i < mUsedBatches + 1; i++) {
			mBatches[i].toNative();
			mBatches[i].draw(vpm);
			mBatches[i].clear();
		}
		mUsedBatches = -1;
		mCurrentGlid = -1;
	}

	/** Removes content of all batches */
	public void clear () {
		for (int i = 0; i < mUsedBatches + 1; i++) {
			mBatches[i].clear();
		}
		mUsedBatches = -1;
		mCurrentGlid = -1;
	}

	public void drawSprite (float[] transform, TextureRegion sprite) {
		uvs = sprite.UVs;
		v = sprite.vertices;

		for (int i = 0; i < 4; i++) {
			temp[0] = v[i * 2];
			temp[1] = v[i * 2 + 1];
			temp[2] = 0;
			temp[3] = 1;
			Matrix.multiplyMV(tempr, 0, transform, 0, temp, 0);
			r[i * 2] = tempr[0];
			r[i * 2 + 1] = tempr[1];
		}

		t[0] = r[0];
		t[1] = r[1];
		t[2] = uvs[0];
		t[3] = uvs[1];
		t[4] = r[2];
		t[5] = r[3];
		t[6] = uvs[2];
		t[7] = uvs[3];
		t[8] = r[4];
		t[9] = r[5];
		t[10] = uvs[4];
		t[11] = uvs[5];
		t[12] = r[6];
		t[13] = r[7];
		t[14] = uvs[6];
		t[15] = uvs[7];

		int tex = sprite.texture;

		if (tex == mCurrentGlid)
			mBatches[mUsedBatches].add(t);
		else {
			mUsedBatches++;
			mBatches[mUsedBatches].mTexture = tex;
			mBatches[mUsedBatches].add(t);
			mCurrentGlid = tex;
		}

	}

	/** Adds the sprite in the specified world x, y location and with specified rotation and scale
	 * @param x world coordinate
	 * @param y world coordinate
	 * @param rotation of the sprite
	 * @param scale of the sprite
	 * @param sprite to draw */
	public void drawSprite (float x, float y, float rotation, float scaleX, float scaleY, TextureRegion sprite) {
		uvs = sprite.UVs;
		v = sprite.vertices;

		Matrix.setIdentityM(transform, 0);
		Matrix.translateM(transform, 0, x, y, 0f);
		Matrix.rotateM(transform, 0, rotation, 0f, 0f, -1.0f);
		Matrix.scaleM(transform, 0, scaleX, scaleY, 1);
		for (int i = 0; i < 4; i++) {
			temp[0] = v[i * 2];
			temp[1] = v[i * 2 + 1];
			temp[2] = 0;
			temp[3] = 1;
			Matrix.multiplyMV(tempr, 0, transform, 0, temp, 0);
			r[i * 2] = tempr[0];
			r[i * 2 + 1] = tempr[1];
		}

		t[0] = r[0];
		t[1] = r[1];
		t[2] = uvs[0];
		t[3] = uvs[1];
		t[4] = r[2];
		t[5] = r[3];
		t[6] = uvs[2];
		t[7] = uvs[3];
		t[8] = r[4];
		t[9] = r[5];
		t[10] = uvs[4];
		t[11] = uvs[5];
		t[12] = r[6];
		t[13] = r[7];
		t[14] = uvs[6];
		t[15] = uvs[7];

		int tex = sprite.texture;

		if (tex == mCurrentGlid)
			mBatches[mUsedBatches].add(t);
		else {
			mUsedBatches++;
			mBatches[mUsedBatches].mTexture = tex;
			mBatches[mUsedBatches].add(t);
			mCurrentGlid = tex;
		}
	}

	public void drawText (String text, float x, float y, Font font) {
		float chrHeight = font.cellHeight * font.scaleY; // Calculate Scaled Character Height
		float chrWidth = font.cellWidth * font.scaleX; // Calculate Scaled Character Width
		int len = text.length(); // Get String Length
		x += (chrWidth / 2.0f) - (font.fontPadX * font.scaleX); // Adjust Start X
		y += (chrHeight / 2.0f) - (font.fontPadY * font.scaleY); // Adjust Start Y
		for (int i = 0; i < len; i++) { // FOR Each Character in String
			int c = (int)text.charAt(i) - FontUtils.CHAR_START; // Calculate Character Index (Offset by First Char in Font)
			if (c < 0 || c >= FontUtils.CHAR_CNT) // IF Character Not In Font
				c = FontUtils.CHAR_UNKNOWN; // Set to Unknown Character Index
			drawSprite(x, y, 0, font.scaleX, font.scaleY, font.charRgn[c]); // Draw the Character
			x += (font.charWidths[c] + font.spaceX) * font.scaleX; // Advance X Position by Scaled Character Width
		}
	}
}
