package com.engine.sevenge.graphics;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import static android.opengl.GLES20.GL_FLOAT;
import static android.opengl.GLES20.glVertexAttribPointer;
import static android.opengl.GLES20.glEnableVertexAttribArray;

public class VertexArray {
	private final FloatBuffer floatBuffer;
	private static final int BYTES_PER_FLOAT = 4;

	VertexArray(float[] vertexData) {
		floatBuffer = ByteBuffer
				.allocateDirect(vertexData.length * BYTES_PER_FLOAT)
				.order(ByteOrder.nativeOrder()).asFloatBuffer();
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
