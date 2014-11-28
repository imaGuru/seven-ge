
package com.engine.sevenge.graphics;

import static android.opengl.GLES20.GL_TRIANGLES;
import static android.opengl.GLES20.GL_UNSIGNED_SHORT;
import static android.opengl.GLES20.glDrawElements;
import static android.opengl.GLES20.glUseProgram;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;

import com.engine.sevenge.assets.TextureRegion;

import android.opengl.Matrix;

/** Drawable batch of sprites using a single texture */
public class SpriteBatch {

	/** Texture with sprites */
	public int texture;
	/** VertexArray Object containing sprite locations in the world */
	private VertexArray vertexArray;
	/** IndexBuffer Object containing face definitions */
	private ShortBuffer indexBuffer;
	/** Shader program used to texture the sprites */
	public TextureShaderProgram program;
	/** Face indices data */
	private short[] indices;
	private float[] sprites;

	private int spriteCount = 0, i = 0, size = 0;
	private int indexOffset, vertexOffset;

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
		texture = tex2D;
		program = spriteShader;
		this.size = size;
		indices = new short[size * INDICES_PER_SPRITE];
		sprites = new float[size * VERTICES_PER_SPRITE * (POSITION_COMPONENT_COUNT + TEXTURE_COORDINATES_COMPONENT_COUNT)];
		vertexArray = new VertexArray(size * (POSITION_COMPONENT_COUNT + TEXTURE_COORDINATES_COMPONENT_COUNT) * VERTICES_PER_SPRITE);
		generateIndices();

	}

	private void generateIndices () {
		for (i = 0; i < size; i++) {
			indexOffset = i * INDICES_PER_SPRITE;
			vertexOffset = i * VERTICES_PER_SPRITE;
			indices[indexOffset] = (short)vertexOffset;
			indices[indexOffset + 1] = (short)(vertexOffset + 1);
			indices[indexOffset + 2] = (short)(vertexOffset + 2);
			indices[indexOffset + 3] = (short)(vertexOffset);
			indices[indexOffset + 4] = (short)(vertexOffset + 2);
			indices[indexOffset + 5] = (short)(vertexOffset + 3);
			indexBuffer = ByteBuffer.allocateDirect(size * INDICES_PER_SPRITE * BYTES_PER_SHORT).order(ByteOrder.nativeOrder())
				.asShortBuffer();
			indexBuffer.put(indices, 0, size * INDICES_PER_SPRITE);
			indexBuffer.position(0);
		}
	}

	/** Add sprite opengl data
	 * @param vertexData locations of 4 vertices with uv coordinates */
	public void add (float[] vertexData) {
		System.arraycopy(vertexData, 0, sprites, spriteCount * VERTICES_PER_SPRITE
			* (POSITION_COMPONENT_COUNT + TEXTURE_COORDINATES_COMPONENT_COUNT), vertexData.length);
		spriteCount++;
	}

	/** Remove every sprite from the batch */
	public void clear () {
		vertexArray.clear();
		spriteCount = 0;
	}

	/** Draw this spritebatch using specified view projection matrix */
	public void draw (float[] vpMatrix) {
		vertexArray.put(sprites, spriteCount * VERTICES_PER_SPRITE
			* (POSITION_COMPONENT_COUNT + TEXTURE_COORDINATES_COMPONENT_COUNT));

		glUseProgram(program.glID);
		program.setUniforms(vpMatrix, texture);
		vertexArray.setVertexAttribPointer(0, program.attributePositionLocation, POSITION_COMPONENT_COUNT, STRIDE);
		vertexArray.setVertexAttribPointer(POSITION_COMPONENT_COUNT, program.attributeTextureCoordinatesLocation,
			TEXTURE_COORDINATES_COMPONENT_COUNT, STRIDE);
		glDrawElements(GL_TRIANGLES, spriteCount * INDICES_PER_SPRITE, GL_UNSIGNED_SHORT, indexBuffer);
	}

	public void add (TextureRegion sprite, float scale, float rotation, int x, int y) {

		int i;
		float[] uvs;
		float[] v;
		float[] r = new float[8], temp = new float[4], tempr = new float[4];
		float[] t = new float[16];
		float[] transform = new float[16], scaleMatrix = new float[16];
		int hw, hh;

		hw = sprite.width / 2;
		hh = sprite.height / 2;
		uvs = sprite.UVs;
		v = sprite.vertices;

		Matrix.setIdentityM(scaleMatrix, 0);
		Matrix.scaleM(scaleMatrix, 0, scale, scale, 1.0f);

		Matrix.setIdentityM(transform, 0);
		Matrix.translateM(transform, 0, hw, hh, 0f);
		Matrix.rotateM(transform, 0, rotation, 0f, 0f, 1.0f);
		Matrix.translateM(transform, 0, x, y, 0f);
		for (i = 0; i < 4; i++) {
			temp[0] = v[i * 2];
			temp[1] = v[i * 2 + 1];
			temp[2] = 0;
			temp[3] = 1;
			Matrix.multiplyMV(tempr, 0, scaleMatrix, 0, temp, 0);
			Matrix.multiplyMV(temp, 0, transform, 0, tempr, 0);
			r[i * 2] = temp[0];
			r[i * 2 + 1] = temp[1];
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

		add(t);

	}
}
