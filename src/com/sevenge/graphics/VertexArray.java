package com.sevenge.graphics;

import static android.opengl.GLES20.GL_FLOAT;
import static android.opengl.GLES20.glEnableVertexAttribArray;
import static android.opengl.GLES20.glVertexAttribPointer;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/** Responsible for holding vertex data and exposing it to the GPU */
public class VertexArray {
	private FloatBuffer mFloatBuffer;
	private static final int BYTES_PER_FLOAT = 4;
	private int mActualSize = 0;

	/**
	 * Creates new vertex array object with specified data
	 * 
	 * @param vertices
	 */
	public VertexArray(int size) {
		mFloatBuffer = ByteBuffer.allocateDirect(size * BYTES_PER_FLOAT)
				.order(ByteOrder.nativeOrder()).asFloatBuffer();
	}

	public void clear() {
		mFloatBuffer.rewind();
		mActualSize = 0;
	}

	/**
	 * Put data at the end of vertex array
	 * 
	 * @param vertexData
	 */
	public void put(float[] vertexData, int length) {
		mFloatBuffer.position(mActualSize);
		mFloatBuffer.put(vertexData, 0, length);
		mActualSize += length;
	}

	/**
	 * Put data at the start position
	 * 
	 * @param vertexData
	 *            data to input
	 * @param start
	 *            index to put data at
	 * @param length
	 *            of the data
	 */
	public void put(float[] vertexData, int start, int length) {
		mFloatBuffer.position(start);
		mFloatBuffer.put(vertexData, start, length);
		int size = start + length;
		if (size > mActualSize)
			mActualSize = size;
	}

	/**
	 * Returns the number of floats stored by this array
	 * 
	 * @return number of floats held by this VAO
	 */
	public int size() {
		return mActualSize;
	}

	/**
	 * Attribute location setting
	 * 
	 * @param dataOffset
	 *            start location of attribute data in the buffer
	 * @param attributeLocation
	 *            attribute we want to use
	 * @param componentCount
	 *            size of the single data
	 * @param stride
	 *            number of indices to skip for next part of data
	 */
	public void setVertexAttribPointer(int dataOffset, int attributeLocation,
			int componentCount, int stride) {
		mFloatBuffer.position(dataOffset);
		glVertexAttribPointer(attributeLocation, componentCount, GL_FLOAT,
				false, stride, mFloatBuffer);
		glEnableVertexAttribArray(attributeLocation);
	}
}
