
package com.sevenge.graphics;

import static android.opengl.GLES20.GL_BLEND;
import static android.opengl.GLES20.GL_ELEMENT_ARRAY_BUFFER;
import static android.opengl.GLES20.GL_FRAGMENT_SHADER;
import static android.opengl.GLES20.GL_TEXTURE0;
import static android.opengl.GLES20.GL_TEXTURE_2D;
import static android.opengl.GLES20.GL_TRIANGLES;
import static android.opengl.GLES20.GL_UNSIGNED_SHORT;
import static android.opengl.GLES20.GL_VERTEX_SHADER;
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

/** Drawable batch of sprites using a single texture */
public class SpriteBatch {

	/** Texture with sprites */
	public int mTexture = 0;
	/** VertexArray Object containing sprite locations in the world */
	private final VertexArray mVertexArray;
	/** IndexBuffer Object containing face definitions */
	private final IndexBuffer indexBuffer;
	/** Shader program used to texture the sprites */
	public final TextureShaderProgram mProgram;

	private final float[] mSprites;
	private final float[] vdata;

	private final int mSize;
	private final float[] matrix;
	private int mSpriteCount = 0;
	public int spriteCountPeak = 0;

	private boolean blendingChanged;
	private boolean blendingEnabled;
	private int sfactor;
	private int dfactor;

	private static final int POSITION_COMPONENT_COUNT = 2;
	private static final int TEXTURE_COORDINATES_COMPONENT_COUNT = 2;
	private static final int BYTES_PER_FLOAT = 4;
	private static final int STRIDE = (POSITION_COMPONENT_COUNT + TEXTURE_COORDINATES_COMPONENT_COUNT) * BYTES_PER_FLOAT;
	private static final short INDICES_PER_SPRITE = 6;
	private static final int VERTICES_PER_SPRITE = 4;

	/** Creates a new sprite batch
	 * @param tex2D texture to be used
	 * @param spriteShader shader to be used
	 * @param size hint number of sprites */
	public SpriteBatch (int maxSpriteCount) {
		mProgram = new TextureShaderProgram(ShaderUtils.compileShader(ShaderUtils.textureVertexShader, GL_VERTEX_SHADER),
			ShaderUtils.compileShader(ShaderUtils.textureFragmentShader, GL_FRAGMENT_SHADER));
		mSize = maxSpriteCount;

		vdata = new float[16];
		matrix = new float[16];

		short[] indices = new short[maxSpriteCount * INDICES_PER_SPRITE];
		mSprites = new float[maxSpriteCount * VERTICES_PER_SPRITE
			* (POSITION_COMPONENT_COUNT + TEXTURE_COORDINATES_COMPONENT_COUNT)];
		mVertexArray = new VertexArray(maxSpriteCount * (POSITION_COMPONENT_COUNT + TEXTURE_COORDINATES_COMPONENT_COUNT)
			* VERTICES_PER_SPRITE);

		for (int i = 0; i < maxSpriteCount; i++) {
			int mIndexOffset = i * INDICES_PER_SPRITE;
			int mVertexOffset = i * VERTICES_PER_SPRITE;
			indices[mIndexOffset] = (short)mVertexOffset;
			indices[mIndexOffset + 1] = (short)(mVertexOffset + 1);
			indices[mIndexOffset + 2] = (short)(mVertexOffset + 2);
			indices[mIndexOffset + 3] = (short)(mVertexOffset);
			indices[mIndexOffset + 4] = (short)(mVertexOffset + 2);
			indices[mIndexOffset + 5] = (short)(mVertexOffset + 3);
		}
		indexBuffer = new IndexBuffer(indices);
	}

	public void begin () {
		glUseProgram(mProgram.mGlID);
		mProgram.setTextureUniform(mTexture);
		mVertexArray.setVertexAttribPointer(0, mProgram.mAttributePositionLocation, POSITION_COMPONENT_COUNT, STRIDE);
		mVertexArray.setVertexAttribPointer(POSITION_COMPONENT_COUNT, mProgram.mAttributeTextureCoordinatesLocation,
			TEXTURE_COORDINATES_COMPONENT_COUNT, STRIDE);
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, indexBuffer.bufferId);
	}

	public void flush () {
		mVertexArray.put(mSprites, mSpriteCount * VERTICES_PER_SPRITE
			* (POSITION_COMPONENT_COUNT + TEXTURE_COORDINATES_COMPONENT_COUNT));
		if (blendingChanged) {
			if (blendingEnabled) {
				glEnable(GL_BLEND);
				glBlendFunc(sfactor, dfactor);
			} else {
				glDisable(GL_BLEND);
			}
			blendingChanged = false;
		}
		mProgram.setMatrixUniform(matrix);
		glActiveTexture(GL_TEXTURE0);
		glBindTexture(GL_TEXTURE_2D, mTexture);
		glDrawElements(GL_TRIANGLES, mSpriteCount * INDICES_PER_SPRITE, GL_UNSIGNED_SHORT, 0);
		if (spriteCountPeak < mSpriteCount) spriteCountPeak = mSpriteCount;
		mSpriteCount = 0;
		mVertexArray.clear();
	}

	public void end () {
		if (mSpriteCount > 0) flush();
		glDisableVertexAttribArray(mProgram.mAttributePositionLocation);
		glDisableVertexAttribArray(mProgram.mAttributeTextureCoordinatesLocation);
		glUseProgram(0);
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
		glBindTexture(GL_TEXTURE_2D, 0);
	}

	public void enableBlending () {
		if (blendingEnabled) return;
		if (mSpriteCount > 0) flush();
		blendingEnabled = true;
		blendingChanged = true;
	}

	public void disableBlending () {
		if (!blendingEnabled) return;
		if (mSpriteCount > 0) flush();
		blendingEnabled = false;
		blendingChanged = true;
	}

	public void setBlendFunc (int sfactor, int dfactor) {
		if (sfactor == this.sfactor && dfactor == this.dfactor) return;
		if (mSpriteCount > 0) flush();
		this.sfactor = sfactor;
		this.dfactor = dfactor;
		blendingChanged = true;
	}

	public void setProjection (float[] matrix) {
		if (mSpriteCount > 0) flush();
		System.arraycopy(matrix, 0, this.matrix, 0, 16);
	}

	public void drawSprite (float x, float y, float rotation, float scaleX, float scaleY, TextureRegion sprite) {
		if (mSpriteCount == mSize) flush();
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
			rotation = (float)Math.toRadians(-rotation);
			float cos = (float)Math.cos(rotation);
			float sin = (float)Math.sin(rotation);
			float vx = hw * scaleX;
			float vy = hh * scaleY;
			vdata[0] = vx * cos - vy * sin + x;
			vdata[1] = vx * sin + vy * cos + y;
			vdata[2] = uvs[0];
			vdata[3] = uvs[1];

			vx = -hw;
			vy = hh * scaleY;
			vdata[4] = vx * cos - vy * sin + x;
			vdata[5] = vx * sin + vy * cos + y;
			vdata[6] = uvs[2];
			vdata[7] = uvs[3];

			vx = -hw;
			vy = -hh;
			vdata[8] = vx * cos - vy * sin + x;
			vdata[9] = vx * sin + vy * cos + y;
			vdata[10] = uvs[4];
			vdata[11] = uvs[5];

			vx = hw * scaleX;
			vy = -hh;
			vdata[12] = vx * cos - vy * sin + x;
			vdata[13] = vx * sin + vy * cos + y;
			vdata[14] = uvs[6];
			vdata[15] = uvs[7];
		} else {
			float vx = hw * scaleX;
			float vy = hh * scaleY;
			vdata[0] = vx + x;
			vdata[1] = vy + y;
			vdata[2] = uvs[0];
			vdata[3] = uvs[1];

			vx = -hw;
			vy = hh * scaleY;
			vdata[4] = vx + x;
			vdata[5] = vy + y;
			vdata[6] = uvs[2];
			vdata[7] = uvs[3];

			vx = -hw;
			vy = -hh;
			vdata[8] = vx + x;
			vdata[9] = vy + y;
			vdata[10] = uvs[4];
			vdata[11] = uvs[5];

			vx = hw * scaleX;
			vy = -hh;
			vdata[12] = vx + x;
			vdata[13] = vy + y;
			vdata[14] = uvs[6];
			vdata[15] = uvs[7];
		}

		System.arraycopy(vdata, 0, mSprites, mSpriteCount * VERTICES_PER_SPRITE
			* (POSITION_COMPONENT_COUNT + TEXTURE_COORDINATES_COMPONENT_COUNT), vdata.length);
		mSpriteCount++;
	}

	public void drawSprite (Sprite sprite) {
		if (mSpriteCount == mSize) flush();
		if (mTexture == 0)
			mTexture = sprite.texture;
		else if (mTexture != sprite.texture) {
			flush();
			mTexture = sprite.texture;
		}
		System.arraycopy(sprite.getVertices(), 0, mSprites, mSpriteCount * VERTICES_PER_SPRITE
			* (POSITION_COMPONENT_COUNT + TEXTURE_COORDINATES_COMPONENT_COUNT), vdata.length);
		mSpriteCount++;
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
