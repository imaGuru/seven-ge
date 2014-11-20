
package com.engine.sevenge.graphics;

import static android.opengl.GLES20.GL_TRIANGLES;
import static android.opengl.GLES20.GL_UNSIGNED_SHORT;
import static android.opengl.GLES20.glDrawElements;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;

/** Drawable batch of sprites using a single texture */
public class SpriteBatch implements Drawable {

	/** Texture with sprites */
	private Texture texture;
	/** VertexArray Object containing sprite locations in the world */
	private VertexArray vertexArray;
	/** IndexBuffer Object containing face definitions */
	private ShortBuffer indexBuffer;
	/** Shader program used to texture the sprites */
	private TextureShaderProgram program;
	/** Sprite location data */
	private float[] spriteData;
	/** Face indices data */
	private short[] indices;
	/** VertexArrayObject initialization state */
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

	/** Create a spriteBatch with the specified size hint
	 * @param size hint number of sprites */
	public SpriteBatch (int size) {
		this(null, null, size);
	}

	/** Creates a new sprite batch
	 * @param tex2D texture to be used
	 * @param spriteShader shader to be used
	 * @param size hint number of sprites */
	public SpriteBatch (Texture tex2D, TextureShaderProgram spriteShader, int size) {
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

	/** Set new shader program
	 * @param program Texture shader program */
	public void setProgram (TextureShaderProgram program) {
		this.program = program;
	}

	/** Set new texture
	 * @param tex texture to be used */
	public void setTexture (Texture tex) {
		this.texture = tex;
	}

	/** Add sprite opengl data
	 * @param vertexData locations of 4 vertices with uv coordinates */
	public void add (float[] vertexData) {
		if (spriteCount > size) {
			// TODO
			return;
		}
		for (i = 0; i < vertexData.length; i++)
			spriteData[offset + i] = vertexData[i];
		offset = ++spriteCount * 4 * (POSITION_COMPONENT_COUNT + TEXTURE_COORDINATES_COMPONENT_COUNT);
	}

	/** Upload data to the graphics card. Should be executed after any change to the spritebatch */
	public void upload () {
		if (!VAOinitialized) {
			vertexArray = new VertexArray(spriteData);
			VAOinitialized = true;
		} else
			vertexArray.reuploadData(spriteData);
	}

	/** Remove every sprite from the batch */
	public void clear () {
		spriteCount = 0;
		offset = 0;
	}

	/** Draw this spritebatch using specified view projection matrix */
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
