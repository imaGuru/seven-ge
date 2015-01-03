
package com.sevenge.graphics;

import android.opengl.Matrix;

import com.sevenge.SevenGE;
import com.sevenge.assets.Font;
import com.sevenge.assets.Shader;
import com.sevenge.assets.TextureRegion;

/** Class responsible for creating sprite batches and puting sprites in the correct batches for fast and easy drawing */
public class SpriteBatcher {
	private Batch[] mBatches;
	private int mUsedBatches;
	private int mCurrentGlid = -1;
	private float DOUBLEPI = (float)(Math.PI * 2.0f);
	private TextureShaderProgram mTSP;
	private ColorShaderProgram mCSP;
	private SpriteBatchPool spriteBatchPool;
	private PrimitiveBatchPool primitiveBatchPool;

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
		mBatches = new Batch[sizeHint];
		mTSP = (TextureShaderProgram)SevenGE.assetManager.getAsset("spriteShader");
		Shader v = (Shader)SevenGE.assetManager.getAsset("shader3");
		Shader f = (Shader)SevenGE.assetManager.getAsset("shader4");
		mCSP = new ColorShaderProgram(v.glID, f.glID);
		spriteBatchPool = new SpriteBatchPool(sizeHint, mTSP, batchSizeHint);
		primitiveBatchPool = new PrimitiveBatchPool(sizeHint, mCSP, batchSizeHint * 2);
	}

	/** Draw the created batches using specified view projection matrix
	 * @param vpm view projection matrix */
	public void flush (float[] vpm) {
		for (int i = 0; i < mUsedBatches + 1; i++) {
			mBatches[i].draw(vpm);
			mBatches[i].clear();
			mBatches[i].release();
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

		if (tex == mCurrentGlid && mBatches[mUsedBatches].getRemaining() > 0)
			mBatches[mUsedBatches].add(t);
		else {
			mUsedBatches++;
			SpriteBatch sb = spriteBatchPool.get();
			sb.mTexture = tex;
			sb.add(t);
			mBatches[mUsedBatches] = sb;
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

		if (tex == mCurrentGlid && mBatches[mUsedBatches].getRemaining() > 0)
			mBatches[mUsedBatches].add(t);
		else {
			mUsedBatches++;
			SpriteBatch sb = spriteBatchPool.get();
			sb.mTexture = tex;
			sb.add(t);
			mBatches[mUsedBatches] = sb;
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

	public void drawCircle (float cx, float cy, float r, float rotation, int nsegments, float red, float green, float blue) {
		float[] vertices = new float[nsegments * 5];
		float t, x = (float)Math.cos(-rotation) * r, y = (float)Math.sin(-rotation) * r;
		float c = (float)Math.cos(DOUBLEPI / nsegments);
		float s = (float)Math.sin(DOUBLEPI / nsegments);
		for (short i = 0; i < nsegments; i++) {
			vertices[i * 5] = x + cx;
			vertices[i * 5 + 1] = y + cy;
			vertices[i * 5 + 2] = red;
			vertices[i * 5 + 3] = green;
			vertices[i * 5 + 4] = blue;
			t = x;
			x = c * x - s * y;
			y = s * t + c * y;
		}
		if (mCurrentGlid == -2 && mBatches[mUsedBatches].getRemaining() >= nsegments)
			mBatches[mUsedBatches].add(vertices, nsegments);
		else {
			mUsedBatches++;
			PrimitiveBatch pb = primitiveBatchPool.get();
			pb.add(vertices, nsegments);
			mBatches[mUsedBatches] = pb;
			mCurrentGlid = -2;
		}
	}

	public void drawRectangle (float cx, float cy, float w, float h, float rotation, float red, float green, float blue) {
		float[] vertices = new float[20];
		float c = (float)Math.cos(-rotation);
		float s = (float)Math.sin(-rotation);
		float x = w / 2, y = h / 2;
		vertices[0] = x * c - s * y + cx;
		vertices[1] = s * x + c * y + cy;
		x = -w / 2;
		vertices[5] = x * c - s * y + cx;
		vertices[6] = s * x + c * y + cy;
		y = -h / 2;
		vertices[10] = x * c - s * y + cx;
		vertices[11] = s * x + c * y + cy;
		x = w / 2;
		vertices[15] = x * c - s * y + cx;
		vertices[16] = s * x + c * y + cy;
		for (int i = 0; i < 4; i++) {
			vertices[i * 5 + 2] = red;
			vertices[i * 5 + 3] = green;
			vertices[i * 5 + 4] = blue;
		}
		if (mCurrentGlid == -2 && mBatches[mUsedBatches].getRemaining() >= 4)
			mBatches[mUsedBatches].add(vertices, 4);
		else {
			mUsedBatches++;
			PrimitiveBatch pb = primitiveBatchPool.get();
			pb.add(vertices, 4);
			mBatches[mUsedBatches] = pb;
			mCurrentGlid = -2;
		}
	}
}
