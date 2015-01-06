
package com.sevenge.graphics;

import static android.opengl.GLES20.GL_TRIANGLES;
import static android.opengl.GLES20.GL_UNSIGNED_SHORT;
import static android.opengl.GLES20.glDrawElements;
import static android.opengl.GLES20.glUseProgram;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;

import android.opengl.Matrix;

import com.sevenge.assets.TextureRegion;

/** Drawable batch of sprites using a single texture */
public class SpriteBatch extends Batch {

	/** Texture with sprites */
	public int mTexture;
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

	private boolean mUpdated = false;
	private SpriteBatchPool mPool;

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
	public SpriteBatch (TextureShaderProgram spriteShader, int maxSpriteCount) {
		mProgram = spriteShader;
		mSize = maxSpriteCount;
		temp = new float[4];
		tempr = new float[4];
		vdata = new float[16];
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

	public void addSprite (float x, float y, float rotation, float scaleX, float scaleY, TextureRegion sprite) {
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
		add(vdata);
	}

	/** Add sprite opengl data
	 * @param vertexData locations of 4 vertices with uv coordinates */
	@Override
	public void add (float[] vertexData) {
		System.arraycopy(vertexData, 0, mSprites, mSpriteCount * VERTICES_PER_SPRITE
			* (POSITION_COMPONENT_COUNT + TEXTURE_COORDINATES_COMPONENT_COUNT), vertexData.length);
		mSpriteCount++;
		mUpdated = true;
	}

	/** Remove every sprite from the batch */
	@Override
	public void clear () {
		mVertexArray.clear();
		mSpriteCount = 0;
		mUpdated = false;
	}

	/** Draw this spritebatch using specified view projection matrix */
	@Override
	public void draw (float[] vpMatrix) {
		if (mUpdated) {
			mVertexArray.put(mSprites, mSpriteCount * VERTICES_PER_SPRITE
				* (POSITION_COMPONENT_COUNT + TEXTURE_COORDINATES_COMPONENT_COUNT));
			mUpdated = false;
		}
		glUseProgram(mProgram.mGlID);
		mProgram.setUniforms(vpMatrix, mTexture);
		mVertexArray.setVertexAttribPointer(0, mProgram.mAttributePositionLocation, POSITION_COMPONENT_COUNT, STRIDE);
		mVertexArray.setVertexAttribPointer(POSITION_COMPONENT_COUNT, mProgram.mAttributeTextureCoordinatesLocation,
			TEXTURE_COORDINATES_COMPONENT_COUNT, STRIDE);
		glDrawElements(GL_TRIANGLES, mSpriteCount * INDICES_PER_SPRITE, GL_UNSIGNED_SHORT, mIndexBuffer);
	}

	@Override
	public int getRemaining () {
		return mSize - mSpriteCount;
	}
}
