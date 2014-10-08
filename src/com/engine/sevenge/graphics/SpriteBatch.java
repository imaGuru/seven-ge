
package com.engine.sevenge.graphics;

import static android.opengl.GLES20.GL_TRIANGLES;
import static android.opengl.GLES20.GL_UNSIGNED_SHORT;
import static android.opengl.GLES20.glDrawElements;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;

public class SpriteBatch implements Drawable {

	private Texture2D texture;
	private VertexArray vertexArray;
	private ShortBuffer indexBuffer;
	private TextureShaderProgram program;
	private float[] spriteData;
	private short[] indices;
	private boolean VAOinitialized = false;

	private int spriteCount = 0, i = 0, size = 0, offset = 0;
	private int indexOffset, vertexOffset;

	private static final int POSITION_COMPONENT_COUNT = 2;
	private static final int TEXTURE_COORDINATES_COMPONENT_COUNT = 2;
	private static final int BYTES_PER_FLOAT = 4;
	private static final int BYTES_PER_SHORT = 2;
	private static final int STRIDE = (POSITION_COMPONENT_COUNT + TEXTURE_COORDINATES_COMPONENT_COUNT) * BYTES_PER_FLOAT;
	private static final short INDICES_PER_SPRITE = 6;
	private static final int VERTICES_PER_SPRITE = 4;

	public SpriteBatch (int size) {
		this(null, null, size);
	}

	public SpriteBatch (Texture2D tex2D, TextureShaderProgram spriteShader, int size) {
		texture = tex2D;
		program = spriteShader;
		this.size = size;
		spriteData = new float[size * (POSITION_COMPONENT_COUNT + TEXTURE_COORDINATES_COMPONENT_COUNT) * 4];
		indices = new short[size * INDICES_PER_SPRITE];
		for (i = 0; i < size; i++) {
			indexOffset = i * INDICES_PER_SPRITE;
			vertexOffset = i * VERTICES_PER_SPRITE;
			indices[indexOffset] = (short)vertexOffset;
			indices[indexOffset + 1] = (short)(vertexOffset + 1);
			indices[indexOffset + 2] = (short)(vertexOffset + 2);
			indices[indexOffset + 3] = (short)(vertexOffset);
			indices[indexOffset + 4] = (short)(vertexOffset + 2);
			indices[indexOffset + 5] = (short)(vertexOffset + 3);
		}
		indexBuffer = ByteBuffer.allocateDirect(indices.length * BYTES_PER_SHORT).order(ByteOrder.nativeOrder()).asShortBuffer()
			.put(indices);
		indexBuffer.position(0);
	}

	public void setProgram (TextureShaderProgram program) {
		this.program = program;
	}

	public void setTexture (Texture2D tex) {
		this.texture = tex;
	}

	public void add (float[] vertexData) {
		if (spriteCount > size) {
			// TODO
		}
		for (i = 0; i < vertexData.length; i++)
			spriteData[offset + i] = vertexData[i];
		offset = ++spriteCount * 4 * (POSITION_COMPONENT_COUNT + TEXTURE_COORDINATES_COMPONENT_COUNT);
	}

	public void upload () {
		if (!VAOinitialized) {
			vertexArray = new VertexArray(spriteData);
			VAOinitialized = true;
		} else
			vertexArray.reuploadData(spriteData);
	}

	public void clear () {
		spriteCount = 0;
		offset = 0;
	}

	@Override
	public void draw (float[] vpMatrix) {
		program.use();
		program.setUniforms(vpMatrix, texture);
		vertexArray.setVertexAttribPointer(0, program.getPositionAttributeLocation(), POSITION_COMPONENT_COUNT, STRIDE);
		vertexArray.setVertexAttribPointer(POSITION_COMPONENT_COUNT, program.getTextureCoordinatesAttributeLocation(),
			TEXTURE_COORDINATES_COMPONENT_COUNT, STRIDE);
		glDrawElements(GL_TRIANGLES, indices.length, GL_UNSIGNED_SHORT, indexBuffer);
	}
}
