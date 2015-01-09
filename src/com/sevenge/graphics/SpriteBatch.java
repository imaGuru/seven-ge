
package com.sevenge.graphics;

import static android.opengl.GLES20.GL_ARRAY_BUFFER;
import static android.opengl.GLES20.GL_ELEMENT_ARRAY_BUFFER;
import static android.opengl.GLES20.GL_TEXTURE0;
import static android.opengl.GLES20.GL_TEXTURE_2D;
import static android.opengl.GLES20.GL_TRIANGLES;
import static android.opengl.GLES20.GL_UNSIGNED_SHORT;
import static android.opengl.GLES20.glActiveTexture;
import static android.opengl.GLES20.glBindBuffer;
import static android.opengl.GLES20.glBindTexture;
import static android.opengl.GLES20.glDrawElements;
import static android.opengl.GLES20.glUseProgram;

public class SpriteBatch {

	private static final short INDICES_PER_SPRITE = 6;
	private static final int VERTICES_PER_SPRITE = 4;
	private static final int POSITION_COMPONENT_COUNT = 2;
	private static final int TEXTURE_COORDINATES_COMPONENT_COUNT = 2;
	private static final int BYTES_PER_FLOAT = 4;
	private static final int STRIDE = (POSITION_COMPONENT_COUNT + TEXTURE_COORDINATES_COMPONENT_COUNT) * BYTES_PER_FLOAT;

	private final VertexBuffer mVertexBuffer;
	private final IndexBuffer mIndexBuffer;
	public final TextureShaderProgram shader;
	private final int mTextureID;
	private int mSpriteCount = 0, mUploadedSprites = 0;
	private float[] mProjectionMatrix;
	private float[] mSpriteData;

	public SpriteBatch (int size, int texture, int type) {
		mTextureID = texture;
		mVertexBuffer = new VertexBuffer(size, type);
		shader = ShaderUtils.TEXTURE_SHADER;
		mSpriteData = new float[size * VERTICES_PER_SPRITE];
		short[] indices = new short[size * INDICES_PER_SPRITE];
		for (int i = 0; i < size; i++) {
			int mIndexOffset = i * INDICES_PER_SPRITE;
			int mVertexOffset = i * VERTICES_PER_SPRITE;
			indices[mIndexOffset] = (short)mVertexOffset;
			indices[mIndexOffset + 1] = (short)(mVertexOffset + 1);
			indices[mIndexOffset + 2] = (short)(mVertexOffset + 2);
			indices[mIndexOffset + 3] = (short)(mVertexOffset);
			indices[mIndexOffset + 4] = (short)(mVertexOffset + 2);
			indices[mIndexOffset + 5] = (short)(mVertexOffset + 3);
		}
		mIndexBuffer = new IndexBuffer(indices);
	}

	public void addSprite (Sprite sprite) {
		if (mTextureID != sprite.texture) throw new RuntimeException("Adding sprite with different texture is forbidden!");
		System.arraycopy(sprite.getVertices(), 0, mSpriteData, mSpriteCount * VERTICES_PER_SPRITE
			* (POSITION_COMPONENT_COUNT + TEXTURE_COORDINATES_COMPONENT_COUNT), 16);
		mSpriteCount++;
	}

	public void addSprite (float x, float y, float rotation, float scaleX, float scaleY, TextureRegion sprite) {
		if (mTextureID != sprite.texture) throw new RuntimeException("Adding sprite with different texture is forbidden!");
		int offset = mSpriteCount * 16;
		float[] uvs = sprite.UVs;
		float hw = sprite.width / 2;
		float hh = sprite.height / 2;
		if (rotation != 0) {
			rotation = (float)Math.toRadians(-rotation);
			float cos = (float)Math.cos(rotation);
			float sin = (float)Math.sin(rotation);
			float vx = hw * scaleX;
			float vy = hh * scaleY;
			mSpriteData[offset] = vx * cos - vy * sin + x;
			mSpriteData[offset + 1] = vx * sin + vy * cos + y;
			mSpriteData[offset + 2] = uvs[0];
			mSpriteData[offset + 3] = uvs[1];

			vx = -hw;
			vy = hh * scaleY;
			mSpriteData[offset + 4] = vx * cos - vy * sin + x;
			mSpriteData[offset + 5] = vx * sin + vy * cos + y;
			mSpriteData[offset + 6] = uvs[2];
			mSpriteData[offset + 7] = uvs[3];

			vx = -hw;
			vy = -hh;
			mSpriteData[offset + 8] = vx * cos - vy * sin + x;
			mSpriteData[offset + 9] = vx * sin + vy * cos + y;
			mSpriteData[offset + 10] = uvs[4];
			mSpriteData[offset + 11] = uvs[5];

			vx = hw * scaleX;
			vy = -hh;
			mSpriteData[offset + 12] = vx * cos - vy * sin + x;
			mSpriteData[offset + 13] = vx * sin + vy * cos + y;
			mSpriteData[offset + 14] = uvs[6];
			mSpriteData[offset + 15] = uvs[7];
		} else {
			float vx = hw * scaleX;
			float vy = hh * scaleY;
			mSpriteData[offset] = vx + x;
			mSpriteData[offset + 1] = vy + y;
			mSpriteData[offset + 2] = uvs[0];
			mSpriteData[offset + 3] = uvs[1];

			vx = -hw;
			vy = hh * scaleY;
			mSpriteData[offset + 4] = vx + x;
			mSpriteData[offset + 5] = vy + y;
			mSpriteData[offset + 6] = uvs[2];
			mSpriteData[offset + 7] = uvs[3];

			vx = -hw;
			vy = -hh;
			mSpriteData[offset + 8] = vx + x;
			mSpriteData[offset + 9] = vy + y;
			mSpriteData[offset + 10] = uvs[4];
			mSpriteData[offset + 11] = uvs[5];

			vx = hw * scaleX;
			vy = -hh;
			mSpriteData[offset + 12] = vx + x;
			mSpriteData[offset + 13] = vy + y;
			mSpriteData[offset + 14] = uvs[6];
			mSpriteData[offset + 15] = uvs[7];
		}
		mSpriteCount++;
	}

	public void updateSprite (int index, Sprite sprite) {
		if (index >= mSpriteCount) throw new RuntimeException("Cannot modify a sprite that doesnt exist");
		mVertexBuffer.put(sprite.getVertices(), 16);
		mVertexBuffer.upload(index * 16);
	}

	public void updateSprite (int index, float x, float y, float rotation, float scaleX, float scaleY, TextureRegion sprite) {
		if (index >= mSpriteCount) throw new RuntimeException("Cannot modify a sprite that doesnt exist");
		int offset = index * 16;
		float[] uvs = sprite.UVs;
		float hw = sprite.width / 2;
		float hh = sprite.height / 2;
		if (rotation != 0) {
			rotation = (float)Math.toRadians(-rotation);
			float cos = (float)Math.cos(rotation);
			float sin = (float)Math.sin(rotation);
			float vx = hw * scaleX;
			float vy = hh * scaleY;
			mSpriteData[offset] = vx * cos - vy * sin + x;
			mSpriteData[offset + 1] = vx * sin + vy * cos + y;
			mSpriteData[offset + 2] = uvs[0];
			mSpriteData[offset + 3] = uvs[1];

			vx = -hw;
			vy = hh * scaleY;
			mSpriteData[offset + 4] = vx * cos - vy * sin + x;
			mSpriteData[offset + 5] = vx * sin + vy * cos + y;
			mSpriteData[offset + 6] = uvs[2];
			mSpriteData[offset + 7] = uvs[3];

			vx = -hw;
			vy = -hh;
			mSpriteData[offset + 8] = vx * cos - vy * sin + x;
			mSpriteData[offset + 9] = vx * sin + vy * cos + y;
			mSpriteData[offset + 10] = uvs[4];
			mSpriteData[offset + 11] = uvs[5];

			vx = hw * scaleX;
			vy = -hh;
			mSpriteData[offset + 12] = vx * cos - vy * sin + x;
			mSpriteData[offset + 13] = vx * sin + vy * cos + y;
			mSpriteData[offset + 14] = uvs[6];
			mSpriteData[offset + 15] = uvs[7];
		} else {
			float vx = hw * scaleX;
			float vy = hh * scaleY;
			mSpriteData[offset] = vx + x;
			mSpriteData[offset + 1] = vy + y;
			mSpriteData[offset + 2] = uvs[0];
			mSpriteData[offset + 3] = uvs[1];

			vx = -hw;
			vy = hh * scaleY;
			mSpriteData[offset + 4] = vx + x;
			mSpriteData[offset + 5] = vy + y;
			mSpriteData[offset + 6] = uvs[2];
			mSpriteData[offset + 7] = uvs[3];

			vx = -hw;
			vy = -hh;
			mSpriteData[offset + 8] = vx + x;
			mSpriteData[offset + 9] = vy + y;
			mSpriteData[offset + 10] = uvs[4];
			mSpriteData[offset + 11] = uvs[5];

			vx = hw * scaleX;
			vy = -hh;
			mSpriteData[offset + 12] = vx + x;
			mSpriteData[offset + 13] = vy + y;
			mSpriteData[offset + 14] = uvs[6];
			mSpriteData[offset + 15] = uvs[7];
		}
		mVertexBuffer.put(mSpriteData, offset, 16);
		mVertexBuffer.upload(offset);
	}

	public void setProjection (float[] matrix) {
		System.arraycopy(matrix, 0, this.mProjectionMatrix, 0, 16);
	}

	public void draw () {
		if (mUploadedSprites != mSpriteCount) {
			if (mUploadedSprites == 0)
				mVertexBuffer.upload();
			else {
				mVertexBuffer.put(mSpriteData, mUploadedSprites * 16, mSpriteCount * 16);
				mVertexBuffer.upload(mUploadedSprites * 16);
			}
			mUploadedSprites = mSpriteCount;
		}
		glUseProgram(shader.mGlID);
		shader.setTextureUniform(mTextureID);
		glBindBuffer(GL_ARRAY_BUFFER, mVertexBuffer.bufferId);
		mVertexBuffer.setVertexAttribPointer(0, shader.mAttributePositionLocation, POSITION_COMPONENT_COUNT, STRIDE);
		mVertexBuffer.setVertexAttribPointer(POSITION_COMPONENT_COUNT, shader.mAttributeTextureCoordinatesLocation,
			TEXTURE_COORDINATES_COMPONENT_COUNT, STRIDE);
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, mIndexBuffer.bufferId);
		shader.setMatrixUniform(mProjectionMatrix);
		glActiveTexture(GL_TEXTURE0);
		glBindTexture(GL_TEXTURE_2D, mTextureID);
		glDrawElements(GL_TRIANGLES, mSpriteCount * INDICES_PER_SPRITE, GL_UNSIGNED_SHORT, 0);
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
		glBindBuffer(GL_ARRAY_BUFFER, 0);
	}
}
