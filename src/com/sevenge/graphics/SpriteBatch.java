
package com.sevenge.graphics;

import static android.opengl.GLES20.GL_TRIANGLES;
import static android.opengl.GLES20.GL_UNSIGNED_SHORT;
import static android.opengl.GLES20.glDrawElements;
import static android.opengl.GLES20.glUseProgram;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;

/** Drawable batch of sprites using a single texture */
public class SpriteBatch {

	/** Texture with sprites */
	public int mTexture;
	/** VertexArray Object containing sprite locations in the world */
	private VertexArray mVertexArray;
	/** IndexBuffer Object containing face definitions */
	private ShortBuffer mIndexBuffer;
	/** Shader program used to texture the sprites */
	public TextureShaderProgram mProgram;
	/** Face indices data */
	private short[] mIndices;
	private float[] mSprites;

	private int mSpriteCount = 0, mSize = 0;
	private int mIndexOffset, mVertexOffset;

	private static final int POSITION_COMPONENT_COUNT = 2;
	private static final int TEXTURE_COORDINATES_COMPONENT_COUNT = 2;
	private static final int BYTES_PER_FLOAT = 4;
	private static final int BYTES_PER_SHORT = 2;
	private static final int STRIDE = (POSITION_COMPONENT_COUNT + TEXTURE_COORDINATES_COMPONENT_COUNT) * BYTES_PER_FLOAT;
	private static final short INDICES_PER_SPRITE = 6;
	private static final int VERTICES_PER_SPRITE = 4;

	/** Create a spriteBatch with the specified size hint
	 * @param size hint number of sprites */
	public SpriteBatch (int size) {
		this(0, null, size);
	}

	/** Creates a new sprite batch
	 * @param tex2D texture to be used
	 * @param spriteShader shader to be used
	 * @param size hint number of sprites */
	public SpriteBatch (int tex2D, TextureShaderProgram spriteShader, int size) {
		mTexture = tex2D;
		mProgram = spriteShader;
		this.mSize = size;
		mIndices = new short[size * INDICES_PER_SPRITE];
		mSprites = new float[size * VERTICES_PER_SPRITE * (POSITION_COMPONENT_COUNT + TEXTURE_COORDINATES_COMPONENT_COUNT)];
		mVertexArray = new VertexArray(size * (POSITION_COMPONENT_COUNT + TEXTURE_COORDINATES_COMPONENT_COUNT)
			* VERTICES_PER_SPRITE);
		generateIndices();

	}

	private void generateIndices () {
		for (int i = 0; i < mSize; i++) {
			mIndexOffset = i * INDICES_PER_SPRITE;
			mVertexOffset = i * VERTICES_PER_SPRITE;
			mIndices[mIndexOffset] = (short)mVertexOffset;
			mIndices[mIndexOffset + 1] = (short)(mVertexOffset + 1);
			mIndices[mIndexOffset + 2] = (short)(mVertexOffset + 2);
			mIndices[mIndexOffset + 3] = (short)(mVertexOffset);
			mIndices[mIndexOffset + 4] = (short)(mVertexOffset + 2);
			mIndices[mIndexOffset + 5] = (short)(mVertexOffset + 3);
			mIndexBuffer = ByteBuffer.allocateDirect(mSize * INDICES_PER_SPRITE * BYTES_PER_SHORT).order(ByteOrder.nativeOrder())
				.asShortBuffer();
			mIndexBuffer.put(mIndices, 0, mSize * INDICES_PER_SPRITE);
			mIndexBuffer.position(0);
		}
	}

	/** Add sprite opengl data
	 * @param vertexData locations of 4 vertices with uv coordinates */
	public void add (float[] vertexData) {
		System.arraycopy(vertexData, 0, mSprites, mSpriteCount * VERTICES_PER_SPRITE
			* (POSITION_COMPONENT_COUNT + TEXTURE_COORDINATES_COMPONENT_COUNT), vertexData.length);
		mSpriteCount++;
	}

	/** Remove every sprite from the batch */
	public void clear () {
		mVertexArray.clear();
		mSpriteCount = 0;
	}

	public void toNative () {
		mVertexArray.put(mSprites, mSpriteCount * VERTICES_PER_SPRITE
			* (POSITION_COMPONENT_COUNT + TEXTURE_COORDINATES_COMPONENT_COUNT));
	}

	/** Draw this spritebatch using specified view projection matrix */
	public void draw (float[] vpMatrix) {
		glUseProgram(mProgram.mGlID);
		mProgram.setUniforms(vpMatrix, mTexture);
		mVertexArray.setVertexAttribPointer(0, mProgram.mAttributePositionLocation, POSITION_COMPONENT_COUNT, STRIDE);
		mVertexArray.setVertexAttribPointer(POSITION_COMPONENT_COUNT, mProgram.mAttributeTextureCoordinatesLocation,
			TEXTURE_COORDINATES_COMPONENT_COUNT, STRIDE);
		glDrawElements(GL_TRIANGLES, mSpriteCount * INDICES_PER_SPRITE, GL_UNSIGNED_SHORT, mIndexBuffer);
	}
}
