
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
	private int actualSize = 0;

	/** Creates new vertex array object with specified data
	 * @param vertices */
	public VertexArray (int size) {
		floatBuffer = ByteBuffer.allocateDirect(size * BYTES_PER_FLOAT).order(ByteOrder.nativeOrder()).asFloatBuffer();
	}

	public void clear () {
		floatBuffer.rewind();
		actualSize = 0;
	}

	/** Put data at the end of vertex array
	 * @param vertexData */
	public void put (float[] vertexData, int length) {
		floatBuffer.position(actualSize);
		floatBuffer.put(vertexData, 0, length);
		actualSize += length;
	}

	public void put (FloatBuffer fb) {
		floatBuffer.position(actualSize);
		floatBuffer.put(fb);
		actualSize = floatBuffer.position();
	}

	public int size () {
		return actualSize;
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
	}
}
