
package com.sevenge.graphics;

import static android.opengl.GLES20.GL_ARRAY_BUFFER;
import static android.opengl.GLES20.GL_FLOAT;
import static android.opengl.GLES20.glBindBuffer;
import static android.opengl.GLES20.glBufferData;
import static android.opengl.GLES20.glBufferSubData;
import static android.opengl.GLES20.glDeleteBuffers;
import static android.opengl.GLES20.glEnableVertexAttribArray;
import static android.opengl.GLES20.glGenBuffers;
import static android.opengl.GLES20.glVertexAttribPointer;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class VertexBuffer {
	public final int bufferId;
	private FloatBuffer mFloatBuffer;
	private static final int BYTES_PER_FLOAT = 4;
	private int mActualSize = 0;
	private int mType;
	private final int buffers[];

	/** Creates new vertex array object with specified data
	 * @param vertices */
	public VertexBuffer (int size, int type) {
		// Allocate a buffer.
		mType = type;
		buffers = new int[1];
		glGenBuffers(buffers.length, buffers, 0);
		if (buffers[0] == 0) {
			throw new RuntimeException("Could not create a new vertex buffer object.");
		}
		bufferId = buffers[0];
		mFloatBuffer = ByteBuffer.allocateDirect(size * BYTES_PER_FLOAT).order(ByteOrder.nativeOrder()).asFloatBuffer();
	}

	public void dispose () {
		glDeleteBuffers(1, buffers, 0);
		mActualSize = 0;
	}

	/** Put data at the end of vertex array
	 * @param vertexData */
	public void put (float[] vertexData, int length) {
		mFloatBuffer.position(mActualSize);
		mFloatBuffer.put(vertexData, 0, length);
		mActualSize += length;
	}

	public void put (float[] vertexData, int start, int length) {
		mFloatBuffer.position(mActualSize);
		mFloatBuffer.put(vertexData, start, length);
		mActualSize += length;
	}

	public void upload (int start) {
		glBindBuffer(GL_ARRAY_BUFFER, bufferId);
		glBufferSubData(GL_ARRAY_BUFFER, start * BYTES_PER_FLOAT, mActualSize * BYTES_PER_FLOAT, mFloatBuffer);
		glBindBuffer(GL_ARRAY_BUFFER, 0);
		mFloatBuffer.position(0);
		mActualSize = 0;
	}

	public void upload () {
		// Bind to the buffer.
		glBindBuffer(GL_ARRAY_BUFFER, bufferId);
		// Transfer data from native memory to the GPU buffer.
		glBufferData(GL_ARRAY_BUFFER, mActualSize * BYTES_PER_FLOAT, mFloatBuffer, mType);
		// IMPORTANT: Unbind from the buffer when we're done with it.
		glBindBuffer(GL_ARRAY_BUFFER, 0);
		mFloatBuffer.position(0);
		mActualSize = 0;
	}

	public int size () {
		return mActualSize;
	}

	/** Attribute location setting
	 * @param dataOffset start location of attribute data in the buffer
	 * @param attributeLocation attribute we want to use
	 * @param componentCount size of the single data
	 * @param stride number of indices to skip for next part of data */
	public void setVertexAttribPointer (int dataOffset, int attributeLocation, int componentCount, int stride) {
		glVertexAttribPointer(attributeLocation, componentCount, GL_FLOAT, false, stride, dataOffset);
		glEnableVertexAttribArray(attributeLocation);
	}
}
