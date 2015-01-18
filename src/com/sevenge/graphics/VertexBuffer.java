
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

	/** Should be called before garbage collection of this object to avoid memory leaks in VRAM */
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

	/** Put data at the start position
	 * @param vertexData data to input
	 * @param start index to put data at
	 * @param length of the data */
	public void put (float[] vertexData, int start, int length) {
		mFloatBuffer.position(mActualSize);
		mFloatBuffer.put(vertexData, start, length);
		mActualSize += length;
	}

	/** Uploads data to part of the VBO starting at start
	 * @param start */
	public void upload (int start) {
		mFloatBuffer.position(0);
		glBindBuffer(GL_ARRAY_BUFFER, bufferId);
		glBufferSubData(GL_ARRAY_BUFFER, start * BYTES_PER_FLOAT, mActualSize * BYTES_PER_FLOAT, mFloatBuffer);
		glBindBuffer(GL_ARRAY_BUFFER, 0);
		mActualSize = 0;
	}

	/** Uploads data input to this buffer to VRAM */
	public void upload () {
		mFloatBuffer.position(0);
		glBindBuffer(GL_ARRAY_BUFFER, bufferId);
		glBufferData(GL_ARRAY_BUFFER, mActualSize * BYTES_PER_FLOAT, mFloatBuffer, mType);
		glBindBuffer(GL_ARRAY_BUFFER, 0);
		mActualSize = 0;
	}

	/** Returns the number of floats stored by this array
	 * @return number of floats held by this VBO */
	public int size () {
		return mActualSize;
	}

	/** Attribute location setting
	 * @param dataOffset start location of attribute data in the buffer
	 * @param attributeLocation attribute we want to use
	 * @param componentCount size of the single data
	 * @param stride number of indices to skip for next part of data */
	public void setVertexAttribPointer (int dataOffset, int attributeLocation, int componentCount, int stride) {
		glVertexAttribPointer(attributeLocation, componentCount, GL_FLOAT, false, stride, dataOffset * BYTES_PER_FLOAT);
		glEnableVertexAttribArray(attributeLocation);
	}
}
