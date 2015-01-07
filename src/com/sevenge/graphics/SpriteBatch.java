
package com.sevenge.graphics;

import static android.opengl.GLES20.GL_BLEND;
import static android.opengl.GLES20.GL_FRAGMENT_SHADER;
import static android.opengl.GLES20.GL_TRIANGLES;
import static android.opengl.GLES20.GL_UNSIGNED_SHORT;
import static android.opengl.GLES20.GL_VERTEX_SHADER;
import static android.opengl.GLES20.glBlendFunc;
import static android.opengl.GLES20.glDisable;
import static android.opengl.GLES20.glDisableVertexAttribArray;
import static android.opengl.GLES20.glDrawElements;
import static android.opengl.GLES20.glEnable;
import static android.opengl.GLES20.glUseProgram;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;

import android.opengl.Matrix;

import com.sevenge.assets.Font;
import com.sevenge.assets.TextureRegion;

/** Drawable batch of sprites using a single texture */
public class SpriteBatch {

	/** Texture with sprites */
	public int mTexture = 0;
	/** VertexArray Object containing sprite locations in the world */
	private VertexArray mVertexArray;
	/** IndexBuffer Object containing face definitions */
	private ShortBuffer mIndexBuffer;
	/** Shader program used to texture the sprites */
	public TextureShaderProgram mProgram;

	private float[] mSprites;
	private float[] temp, tempr, vdata, transform;

	private int mSpriteCount = 0;
	private int mSize = 0;
	public int spriteCountPeak = 0;
	private boolean blendingEnabled;
	private int sfactor;
	private int dfactor;
	private float[] matrix;
	private boolean blendingChanged;

	private static final int POSITION_COMPONENT_COUNT = 2;
	private static final int TEXTURE_COORDINATES_COMPONENT_COUNT = 2;
	private static final int BYTES_PER_FLOAT = 4;
	private static final int BYTES_PER_SHORT = 2;
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

		temp = new float[4];
		tempr = new float[4];
		vdata = new float[16];
		transform = new float[16];

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
			mIndexBuffer = ByteBuffer.allocateDirect(mSize * INDICES_PER_SPRITE * BYTES_PER_SHORT).order(ByteOrder.nativeOrder())
				.asShortBuffer().put(indices, 0, mSize * INDICES_PER_SPRITE);
			mIndexBuffer.position(0);
		}
	}

	public void begin () {
		glUseProgram(mProgram.mGlID);
		mVertexArray.setVertexAttribPointer(0, mProgram.mAttributePositionLocation, POSITION_COMPONENT_COUNT, STRIDE);
		mVertexArray.setVertexAttribPointer(POSITION_COMPONENT_COUNT, mProgram.mAttributeTextureCoordinatesLocation,
			TEXTURE_COORDINATES_COMPONENT_COUNT, STRIDE);
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
		mProgram.setTextureUniform(mTexture);
		glDrawElements(GL_TRIANGLES, mSpriteCount * INDICES_PER_SPRITE, GL_UNSIGNED_SHORT, mIndexBuffer);
		if (spriteCountPeak < mSpriteCount) spriteCountPeak = mSpriteCount;
		mSpriteCount = 0;
		mVertexArray.clear();
	}

	public void end () {
		if (mSpriteCount > 0) flush();
		glDisableVertexAttribArray(mProgram.mAttributePositionLocation);
		glDisableVertexAttribArray(mProgram.mAttributeTextureCoordinatesLocation);
	}

	public void enableBlending () {
		if (mSpriteCount > 0) flush();
		blendingEnabled = true;
		blendingChanged = true;
	}

	public void disableBlending () {
		if (mSpriteCount > 0) flush();
		blendingEnabled = false;
		blendingChanged = true;
	}

	public void setBlendFunc (int sfactor, int dfactor) {
		if (mSpriteCount > 0) flush();
		this.sfactor = sfactor;
		this.dfactor = dfactor;
		blendingChanged = true;
	}

	public void setProjection (float[] matrix) {
		if (mSpriteCount > 0) flush();
		this.matrix = matrix;
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
		float[] v = sprite.vertices;
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
			vdata[i * 4] = tempr[0];
			vdata[i * 4 + 1] = tempr[1];
			vdata[i * 4 + 2] = uvs[i * 2];
			vdata[i * 4 + 3] = uvs[i * 2 + 1];
		}
		System.arraycopy(vdata, 0, mSprites, mSpriteCount * VERTICES_PER_SPRITE
			* (POSITION_COMPONENT_COUNT + TEXTURE_COORDINATES_COMPONENT_COUNT), vdata.length);
		mSpriteCount++;
	}

	public int drawText (String text, float x, float y, Font font) {
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
		return text.length();
	}
}
