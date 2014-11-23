
package com.engine.sevenge.graphics;

import static android.opengl.GLES20.GL_FLOAT;
import static android.opengl.GLES20.glEnableVertexAttribArray;
import static android.opengl.GLES20.glVertexAttribPointer;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/** Responsible for holding vertex data and exposing it to the GPU */
public class VertexArray {
	private FloatBuffer floatBuffer;
	private static final int BYTES_PER_FLOAT = 4;
	private int memoryAllocated = 0;

	/** Creates new vertex array object with specified data
	 * @param vertexData */
	public VertexArray (float[] vertexData, int length) {
		floatBuffer = ByteBuffer.allocateDirect(length * BYTES_PER_FLOAT).order(ByteOrder.nativeOrder()).asFloatBuffer()
			.put(vertexData, 0, length);
		memoryAllocated = length * BYTES_PER_FLOAT;
	}

	/** Reupload the data to GPU
	 * @param vertexData */
	public void reuploadData (float[] vertexData, int length) {
		floatBuffer.rewind();
		if (length * BYTES_PER_FLOAT <= memoryAllocated)
			floatBuffer.put(vertexData, 0, length);
		else {
			floatBuffer = ByteBuffer.allocateDirect(length * BYTES_PER_FLOAT).order(ByteOrder.nativeOrder()).asFloatBuffer()
				.put(vertexData, 0, length);
		}
		memoryAllocated = length * BYTES_PER_FLOAT;
	}

	/** Attribute location setting
	 * @param dataOffset start location of attribute data in the buffer
	 * @param attributeLocation attribute we want to use
	 * @param componentCount size of the single data
	 * @param stride number of indices to skip for next part of data */
	public void setVertexAttribPointer (int dataOffset, int attributeLocation, int componentCount, int stride) {
		floatBuffer.position(dataOffset);
		glVertexAttribPointer(attributeLocation, componentCount, GL_FLOAT, false, stride, floatBuffer);
		glEnableVertexAttribArray(attributeLocation);
		floatBuffer.position(0);
	}
}
