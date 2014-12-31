
package com.sevenge.graphics;

import static android.opengl.GLES20.GL_LINES;
import static android.opengl.GLES20.GL_UNSIGNED_SHORT;
import static android.opengl.GLES20.glDrawElements;
import static android.opengl.GLES20.glUseProgram;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;

public class PrimitiveBatch extends Batch {
	private VertexArray mVertexArray;
	/** IndexBuffer Object containing face definitions */
	private ShortBuffer mIndexBuffer;
	/** Shader program used to texture the sprites */
	public ColorShaderProgram mProgram;
	/** Face indices data */
	private short[] mIndices;
	private float[] mPrimitives;

	private int mVertexCount = 0;
	private int mSize = 0;
	private boolean mUpdated = false;

	private static final int POSITION_COMPONENT_COUNT = 2;
	private static final int COLOR_COMPONENT_COUNT = 3;
	private static final int BYTES_PER_FLOAT = 4;
	private static final int BYTES_PER_SHORT = 2;
	private static final int STRIDE = (POSITION_COMPONENT_COUNT + COLOR_COMPONENT_COUNT) * BYTES_PER_FLOAT;

	private PrimitiveBatchPool mPool;

	/** Creates a new sprite batch
	 * @param tex2D texture to be used
	 * @param spriteShader shader to be used
	 * @param size hint number of sprites */
	public PrimitiveBatch (int type, ColorShaderProgram colorShader, int size, PrimitiveBatchPool pool) {
		mPool = pool;
		mProgram = colorShader;
		mSize = size;
		mIndices = new short[mSize * 2];
		mPrimitives = new float[mSize * (POSITION_COMPONENT_COUNT + COLOR_COMPONENT_COUNT)];
		mVertexArray = new VertexArray(mSize * (POSITION_COMPONENT_COUNT + COLOR_COMPONENT_COUNT));
		mIndexBuffer = ByteBuffer.allocateDirect(mSize * 2 * BYTES_PER_SHORT).order(ByteOrder.nativeOrder()).asShortBuffer();
	}

	/** Add sprite opengl data
	 * @param vertexData locations of 4 vertices with uv coordinates */
	@Override
	public void add (float[] vertexData, int segments) {
		System.arraycopy(vertexData, 0, mPrimitives, mVertexCount * (POSITION_COMPONENT_COUNT + COLOR_COMPONENT_COUNT),
			vertexData.length);
		short offset = (short)(mVertexCount * 2);
		for (short i = 0; i < segments; i++) {
			mIndices[offset + i * 2] = (short)(mVertexCount + i);
			mIndices[offset + i * 2 + 1] = (short)(mVertexCount + (i + 1) % segments);
		}
		mVertexCount += vertexData.length / 5;
		mUpdated = true;
	}

	/** Remove every sprite from the batch */
	@Override
	public void clear () {
		mVertexArray.clear();
		mVertexCount = 0;
		mUpdated = false;
	}

	@Override
	public void draw (float[] vpMatrix) {
		if (mUpdated) {
			mVertexArray.put(mPrimitives, mVertexCount * (POSITION_COMPONENT_COUNT + COLOR_COMPONENT_COUNT));
			mIndexBuffer.put(mIndices, 0, mVertexCount * 2);
			mIndexBuffer.position(0);
			mUpdated = false;
		}
		glUseProgram(mProgram.mGlID);
		mProgram.setUniforms(vpMatrix);
		mVertexArray.setVertexAttribPointer(0, mProgram.mAttributePositionLocation, POSITION_COMPONENT_COUNT, STRIDE);
		mVertexArray.setVertexAttribPointer(POSITION_COMPONENT_COUNT, mProgram.mAtributeColorLocation, COLOR_COMPONENT_COUNT,
			STRIDE);
		glDrawElements(GL_LINES, mVertexCount * 2, GL_UNSIGNED_SHORT, mIndexBuffer);
	}

	@Override
	void release () {
		mPool.release(this);
	}

	@Override
	int getRemaining () {
		return mSize - mVertexCount;
	}
}
