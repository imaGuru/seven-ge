package com.engine.sevenge.graphics;

import static android.opengl.GLES20.GL_FLOAT;
import static android.opengl.GLES20.glEnableVertexAttribArray;
import static android.opengl.GLES20.glVertexAttribPointer;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class VertexArray {
	private FloatBuffer floatBuffer;
	private static final int BYTES_PER_FLOAT = 4;
	private int memoryAllocated = 0;

	public VertexArray(float[] vertexData) {
		floatBuffer = ByteBuffer
				.allocateDirect(vertexData.length * BYTES_PER_FLOAT)
				.order(ByteOrder.nativeOrder()).asFloatBuffer().put(vertexData);
		memoryAllocated = vertexData.length * BYTES_PER_FLOAT;
	}

	public void reuploadData(float[] vertexData) {
		floatBuffer.rewind();
		if (vertexData.length * BYTES_PER_FLOAT <= memoryAllocated)
			floatBuffer.put(vertexData);
		else {
			floatBuffer = ByteBuffer
					.allocateDirect(vertexData.length * BYTES_PER_FLOAT)
					.order(ByteOrder.nativeOrder()).asFloatBuffer()
					.put(vertexData);
			memoryAllocated = vertexData.length * BYTES_PER_FLOAT;
		}
	}

	public void setVertexAttribPointer(int dataOffset, int attributeLocation,
			int componentCount, int stride) {
		floatBuffer.position(dataOffset);
		glVertexAttribPointer(attributeLocation, componentCount, GL_FLOAT,
				false, stride, floatBuffer);
		glEnableVertexAttribArray(attributeLocation);
		floatBuffer.position(0);
	}
}
