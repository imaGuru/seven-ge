
package com.sevenge.graphics;

import static android.opengl.GLES20.GL_ARRAY_BUFFER;
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
import static android.opengl.GLES20.glDrawElements;
import static android.opengl.GLES20.glUseProgram;

public class SpriteBatch {

	private static final short INDICES_PER_SPRITE = 6;
	private static final int VERTICES_PER_SPRITE = 4;
	private static final int POSITION_COMPONENT_COUNT = 2;
	private static final int TEXTURE_COORDINATES_COMPONENT_COUNT = 2;
	private static final int BYTES_PER_FLOAT = 4;
	private static final int STRIDE = (POSITION_COMPONENT_COUNT + TEXTURE_COORDINATES_COMPONENT_COUNT) * BYTES_PER_FLOAT;

	private final VertexBuffer vertexBuffer;
	private final IndexBuffer indexBuffer;
	public final TextureShaderProgram mProgram;
	private final int textureID;
	private int spriteCount = 0;
	private float[] matrix;
	private float[] spriteData;

	public SpriteBatch (int size, int texture, int type) {
		textureID = texture;
		vertexBuffer = new VertexBuffer(size, type);
		mProgram = new TextureShaderProgram(ShaderUtils.compileShader(ShaderUtils.textureVertexShader, GL_VERTEX_SHADER),
			ShaderUtils.compileShader(ShaderUtils.textureFragmentShader, GL_FRAGMENT_SHADER));
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
		indexBuffer = new IndexBuffer(indices);
	}

	public void addSprite (Sprite sprite) {
		if (textureID != sprite.texture) throw new RuntimeException("Adding sprite with different texture is forbidden!");
		System.arraycopy(sprite.getVertices(), 0, spriteData, spriteCount * VERTICES_PER_SPRITE
			* (POSITION_COMPONENT_COUNT + TEXTURE_COORDINATES_COMPONENT_COUNT), 16);
		spriteCount++;
	}

	public void addSprite (float x, float y, float rotation, float scaleX, float scaleY, TextureRegion sprite) {
		if (textureID != sprite.texture) throw new RuntimeException("Adding sprite with different texture is forbidden!");
		int offset = spriteCount * 16;
		float[] uvs = sprite.UVs;
		float hw = sprite.width / 2;
		float hh = sprite.height / 2;
		if (rotation != 0) {
			rotation = (float)Math.toRadians(-rotation);
			float cos = (float)Math.cos(rotation);
			float sin = (float)Math.sin(rotation);
			float vx = hw * scaleX;
			float vy = hh * scaleY;
			spriteData[offset] = vx * cos - vy * sin + x;
			spriteData[offset + 1] = vx * sin + vy * cos + y;
			spriteData[offset + 2] = uvs[0];
			spriteData[offset + 3] = uvs[1];

			vx = -hw;
			vy = hh * scaleY;
			spriteData[offset + 4] = vx * cos - vy * sin + x;
			spriteData[offset + 5] = vx * sin + vy * cos + y;
			spriteData[offset + 6] = uvs[2];
			spriteData[offset + 7] = uvs[3];

			vx = -hw;
			vy = -hh;
			spriteData[offset + 8] = vx * cos - vy * sin + x;
			spriteData[offset + 9] = vx * sin + vy * cos + y;
			spriteData[offset + 10] = uvs[4];
			spriteData[offset + 11] = uvs[5];

			vx = hw * scaleX;
			vy = -hh;
			spriteData[offset + 12] = vx * cos - vy * sin + x;
			spriteData[offset + 13] = vx * sin + vy * cos + y;
			spriteData[offset + 14] = uvs[6];
			spriteData[offset + 15] = uvs[7];
		} else {
			float vx = hw * scaleX;
			float vy = hh * scaleY;
			spriteData[offset] = vx + x;
			spriteData[offset + 1] = vy + y;
			spriteData[offset + 2] = uvs[0];
			spriteData[offset + 3] = uvs[1];

			vx = -hw;
			vy = hh * scaleY;
			spriteData[offset + 4] = vx + x;
			spriteData[offset + 5] = vy + y;
			spriteData[offset + 6] = uvs[2];
			spriteData[offset + 7] = uvs[3];

			vx = -hw;
			vy = -hh;
			spriteData[offset + 8] = vx + x;
			spriteData[offset + 9] = vy + y;
			spriteData[offset + 10] = uvs[4];
			spriteData[offset + 11] = uvs[5];

			vx = hw * scaleX;
			vy = -hh;
			spriteData[offset + 12] = vx + x;
			spriteData[offset + 13] = vy + y;
			spriteData[offset + 14] = uvs[6];
			spriteData[offset + 15] = uvs[7];
		}
		spriteCount++;
	}

	public void updateSprite (int index, Sprite sprite) {
		if (index >= spriteCount) throw new RuntimeException("Cannot modify a sprite that doesnt exist");
	}

	public void updateSprite (int index, float x, float y, float rotation, float scaleX, float scaleY, TextureRegion sprite) {
		if (index >= spriteCount) throw new RuntimeException("Cannot modify a sprite that doesnt exist");
	}

	public void update () {

	}

	public void setProjection (float[] matrix) {
		System.arraycopy(matrix, 0, this.matrix, 0, 16);
	}

	public void draw () {
		glUseProgram(mProgram.mGlID);
		mProgram.setTextureUniform(textureID);
		glBindBuffer(GL_ARRAY_BUFFER, vertexBuffer.bufferId);
		vertexBuffer.setVertexAttribPointer(0, mProgram.mAttributePositionLocation, POSITION_COMPONENT_COUNT, STRIDE);
		vertexBuffer.setVertexAttribPointer(POSITION_COMPONENT_COUNT, mProgram.mAttributeTextureCoordinatesLocation,
			TEXTURE_COORDINATES_COMPONENT_COUNT, STRIDE);
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, indexBuffer.bufferId);
		mProgram.setMatrixUniform(matrix);
		glActiveTexture(GL_TEXTURE0);
		glBindTexture(GL_TEXTURE_2D, textureID);
		glDrawElements(GL_TRIANGLES, spriteCount * INDICES_PER_SPRITE, GL_UNSIGNED_SHORT, 0);
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
		glBindBuffer(GL_ARRAY_BUFFER, 0);
	}
}
