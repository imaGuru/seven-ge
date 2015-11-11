package com.sevenge.graphics;

import static android.opengl.GLES20.GL_BLEND;
import static android.opengl.GLES20.GL_ELEMENT_ARRAY_BUFFER;
import static android.opengl.GLES20.GL_TEXTURE0;
import static android.opengl.GLES20.GL_TEXTURE_2D;
import static android.opengl.GLES20.GL_TRIANGLES;
import static android.opengl.GLES20.GL_UNSIGNED_SHORT;
import static android.opengl.GLES20.glActiveTexture;
import static android.opengl.GLES20.glBindBuffer;
import static android.opengl.GLES20.glBindTexture;
import static android.opengl.GLES20.glBlendFunc;
import static android.opengl.GLES20.glDisable;
import static android.opengl.GLES20.glDisableVertexAttribArray;
import static android.opengl.GLES20.glDrawElements;
import static android.opengl.GLES20.glEnable;
import static android.opengl.GLES20.glUseProgram;

import com.sevenge.assets.Font;

/** Class enabling the user to efficiently draw sprites by batching OpenGL calls */
public class SpriteBatcher {

	/** Texture with sprites */
	private int mTexture = 0;
	/** VertexArray Object containing sprite locations in the world */
	private final VertexArray mVertexArray;
	/** IndexBuffer Object containing face definitions */
	private final IndexBuffer mIndexBuffer;
	/** Shader program used to texture the sprites */
	public final TextureShaderProgram shader;

	private final float[] mSprites;
	private final float[] mVData;

	private final int mSize;
	private final float[] mProjectionMatrix;
	private int mSpriteCount = 0;
	public int spriteCountPeak = 0;

	private boolean mBlendingChanged;
	private boolean mBlendingEnabled;
	private int mSFactor;
	private int mDFactor;

	private static final int POSITION_COMPONENT_COUNT = 2;
	private static final int TEXTURE_COORDINATES_COMPONENT_COUNT = 2;
	private static final int BYTES_PER_FLOAT = 4;
	private static final int STRIDE = (POSITION_COMPONENT_COUNT + TEXTURE_COORDINATES_COMPONENT_COUNT)
			* BYTES_PER_FLOAT;
	private static final short INDICES_PER_SPRITE = 6;
	private static final int VERTICES_PER_SPRITE = 4;

	/**
	 * Creates a new SpriteBatcher
	 * 
	 * @param maxSpriteCount
	 *            maximum number of sprites in one batch call
	 */
	public SpriteBatcher(int maxSpriteCount) {
		shader = ShaderUtils.TEXTURE_SHADER;
		mSize = maxSpriteCount;

		mVData = new float[16];
		mProjectionMatrix = new float[16];

		short[] indices = new short[maxSpriteCount * INDICES_PER_SPRITE];
		mSprites = new float[maxSpriteCount
				* VERTICES_PER_SPRITE
				* (POSITION_COMPONENT_COUNT + TEXTURE_COORDINATES_COMPONENT_COUNT)];
		mVertexArray = new VertexArray(
				maxSpriteCount
						* (POSITION_COMPONENT_COUNT + TEXTURE_COORDINATES_COMPONENT_COUNT)
						* VERTICES_PER_SPRITE);

		for (int i = 0; i < maxSpriteCount; i++) {
			int mIndexOffset = i * INDICES_PER_SPRITE;
			int mVertexOffset = i * VERTICES_PER_SPRITE;
			indices[mIndexOffset] = (short) mVertexOffset;
			indices[mIndexOffset + 1] = (short) (mVertexOffset + 1);
			indices[mIndexOffset + 2] = (short) (mVertexOffset + 2);
			indices[mIndexOffset + 3] = (short) (mVertexOffset);
			indices[mIndexOffset + 4] = (short) (mVertexOffset + 2);
			indices[mIndexOffset + 5] = (short) (mVertexOffset + 3);
		}
		mIndexBuffer = new IndexBuffer(indices);
	}

	/** Begins this rendering. Sets OpenGL state for drawing sprites */
	public void begin() {
		glUseProgram(shader.mGlID);
		shader.setTextureUniform(mTexture);
		mVertexArray.setVertexAttribPointer(0,
				shader.mAttributePositionLocation, POSITION_COMPONENT_COUNT,
				STRIDE);
		mVertexArray.setVertexAttribPointer(POSITION_COMPONENT_COUNT,
				shader.mAttributeTextureCoordinatesLocation,
				TEXTURE_COORDINATES_COMPONENT_COUNT, STRIDE);
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, mIndexBuffer.bufferId);
	}

	/** Draw the created batch using specified view projection matrix */
	public void flush() {
		mVertexArray
				.put(mSprites,
						mSpriteCount
								* VERTICES_PER_SPRITE
								* (POSITION_COMPONENT_COUNT + TEXTURE_COORDINATES_COMPONENT_COUNT));
		if (mBlendingChanged) {
			if (mBlendingEnabled) {
				glEnable(GL_BLEND);
				glBlendFunc(mSFactor, mDFactor);
			} else {
				glDisable(GL_BLEND);
			}
			mBlendingChanged = false;
		}
		shader.setMatrixUniform(mProjectionMatrix);
		glActiveTexture(GL_TEXTURE0);
		glBindTexture(GL_TEXTURE_2D, mTexture);
		glDrawElements(GL_TRIANGLES, mSpriteCount * INDICES_PER_SPRITE,
				GL_UNSIGNED_SHORT, 0);
		if (spriteCountPeak < mSpriteCount)
			spriteCountPeak = mSpriteCount;
		mSpriteCount = 0;
		mVertexArray.clear();
	}

	/** Finishes this rendering, reverting OpenGL state changes */
	public void end() {
		if (mSpriteCount > 0)
			flush();
		glDisableVertexAttribArray(shader.mAttributePositionLocation);
		glDisableVertexAttribArray(shader.mAttributeTextureCoordinatesLocation);
		glUseProgram(0);
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
		glBindTexture(GL_TEXTURE_2D, 0);
	}

	/**
	 * Enables blending for the next batch. If there were any added sprites
	 * before this call the buffer is flushed
	 */
	public void enableBlending() {
		if (mBlendingEnabled)
			return;
		if (mSpriteCount > 0)
			flush();
		mBlendingEnabled = true;
		mBlendingChanged = true;
	}

	/**
	 * Disables blending for the next batch. If there were any added sprites
	 * before this call the buffer is flushed
	 */
	public void disableBlending() {
		if (!mBlendingEnabled)
			return;
		if (mSpriteCount > 0)
			flush();
		mBlendingEnabled = false;
		mBlendingChanged = true;
	}

	public void setBlendFunc(int sfactor, int dfactor) {
		if (sfactor == this.mSFactor && dfactor == this.mDFactor)
			return;
		if (mSpriteCount > 0)
			flush();
		this.mSFactor = sfactor;
		this.mDFactor = dfactor;
		mBlendingChanged = true;
	}

	/**
	 * Sets the view projection matrix for the current batch
	 * 
	 * @param matrix
	 *            with projection
	 */
	public void setProjection(float[] matrix) {
		if (mSpriteCount > 0)
			flush();
		System.arraycopy(matrix, 0, this.mProjectionMatrix, 0, 16);
	}

	/**
	 * Draws a new sprite with the given textureRegion.
	 * 
	 * @param x
	 *            coordinate of the left bottom corner of this sprite
	 * @param y
	 *            coordinate of the left bottom corner of this sprite
	 * @param rotation
	 *            angle in degrees
	 * @param scaleX
	 *            scaling factor in x
	 * @param scaleY
	 *            scaling factor in y
	 * @param sprite
	 *            textureRegion to use for the sprite
	 */
	public void drawSprite(float x, float y, float rotation, float scaleX,
			float scaleY, TextureRegion sprite) {
		if (mSpriteCount == mSize)
			flush();
		if (mTexture == 0)
			mTexture = sprite.texture;
		else if (mTexture != sprite.texture) {
			flush();
			mTexture = sprite.texture;
		}
		float[] uvs = sprite.UVs;
		float hw = sprite.width / 2;
		float hh = sprite.height / 2;
		if (rotation != 0) {
			rotation = (float) Math.toRadians(-rotation);
			float cos = (float) Math.cos(rotation);
			float sin = (float) Math.sin(rotation);
			float vx = hw * scaleX;
			float vy = hh * scaleY;
			mVData[0] = vx * cos - vy * sin + x;
			mVData[1] = vx * sin + vy * cos + y;
			mVData[2] = uvs[0];
			mVData[3] = uvs[1];

			vx = -hw;
			vy = hh * scaleY;
			mVData[4] = vx * cos - vy * sin + x;
			mVData[5] = vx * sin + vy * cos + y;
			mVData[6] = uvs[2];
			mVData[7] = uvs[3];

			vx = -hw;
			vy = -hh;
			mVData[8] = vx * cos - vy * sin + x;
			mVData[9] = vx * sin + vy * cos + y;
			mVData[10] = uvs[4];
			mVData[11] = uvs[5];

			vx = hw * scaleX;
			vy = -hh;
			mVData[12] = vx * cos - vy * sin + x;
			mVData[13] = vx * sin + vy * cos + y;
			mVData[14] = uvs[6];
			mVData[15] = uvs[7];
		} else {
			float vx = hw * scaleX;
			float vy = hh * scaleY;
			mVData[0] = vx + x;
			mVData[1] = vy + y;
			mVData[2] = uvs[0];
			mVData[3] = uvs[1];

			vx = -hw;
			vy = hh * scaleY;
			mVData[4] = vx + x;
			mVData[5] = vy + y;
			mVData[6] = uvs[2];
			mVData[7] = uvs[3];

			vx = -hw;
			vy = -hh;
			mVData[8] = vx + x;
			mVData[9] = vy + y;
			mVData[10] = uvs[4];
			mVData[11] = uvs[5];

			vx = hw * scaleX;
			vy = -hh;
			mVData[12] = vx + x;
			mVData[13] = vy + y;
			mVData[14] = uvs[6];
			mVData[15] = uvs[7];
		}

		System.arraycopy(
				mVData,
				0,
				mSprites,
				mSpriteCount
						* VERTICES_PER_SPRITE
						* (POSITION_COMPONENT_COUNT + TEXTURE_COORDINATES_COMPONENT_COUNT),
				mVData.length);
		mSpriteCount++;
	}

	/**
	 * Draws the given sprite
	 * 
	 * @param sprite
	 *            to draw
	 */
	public void drawSprite(Sprite sprite) {
		if (mSpriteCount == mSize)
			flush();
		if (mTexture == 0)
			mTexture = sprite.texture;
		else if (mTexture != sprite.texture) {
			flush();
			mTexture = sprite.texture;
		}
		System.arraycopy(
				sprite.getVertices(),
				0,
				mSprites,
				mSpriteCount
						* VERTICES_PER_SPRITE
						* (POSITION_COMPONENT_COUNT + TEXTURE_COORDINATES_COMPONENT_COUNT),
				mVData.length);
		mSpriteCount++;
	}

	/**
	 * Draws a series of sprites representing text using the loaded font
	 * 
	 * @param text
	 *            to render
	 * @param x
	 *            coordinate of the first letters left bottom corner
	 * @param y
	 *            coordinate of the first letters left bottom corner
	 * @param font
	 *            to draw the text with
	 */
	public void drawText(String text, float x, float y, Font font) {
		float chrHeight = font.cellHeight * font.scaleY; // Calculate Scaled
															// Character Height
		float chrWidth = font.cellWidth * font.scaleX; // Calculate Scaled
														// Character Width
		int len = text.length(); // Get String Length
		x += (chrWidth / 2.0f) - (font.fontPadX * font.scaleX); // Adjust Start
																// X
		y += (chrHeight / 2.0f) - (font.fontPadY * font.scaleY); // Adjust Start
																	// Y
		for (int i = 0; i < len; i++) { // FOR Each Character in String
			int c = text.charAt(i) - FontUtils.CHAR_START; // Calculate
																	// Character
																	// Index
																	// (Offset
																	// by First
																	// Char in
																	// Font)
			if (c < 0 || c >= FontUtils.CHAR_CNT) // IF Character Not In Font
				c = FontUtils.CHAR_UNKNOWN; // Set to Unknown Character Index
			drawSprite(x, y, 0, font.scaleX, font.scaleY, font.charRgn[c]); // Draw
																			// the
																			// Character
			x += (font.charWidths[c] + font.spaceX) * font.scaleX; // Advance X
																	// Position
																	// by Scaled
																	// Character
																	// Width
		}
	}
}
