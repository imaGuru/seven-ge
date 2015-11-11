package com.sevenge.graphics;

import static android.opengl.GLES20.GL_ELEMENT_ARRAY_BUFFER;
import static android.opengl.GLES20.GL_STATIC_DRAW;
import static android.opengl.GLES20.glBindBuffer;
import static android.opengl.GLES20.glBufferData;
import static android.opengl.GLES20.glDeleteBuffers;
import static android.opengl.GLES20.glGenBuffers;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;

public class IndexBuffer {
	private static final int BYTES_PER_SHORT = 2;
	public final int bufferId;
	private final int[] buffers;

	/**
	 * Creates new index buffer object with specified data
	 * 
	 * @param indices
	 */
	public IndexBuffer(short[] indices) {
		// Allocate a buffer.
		buffers = new int[1];
		glGenBuffers(buffers.length, buffers, 0);
		if (buffers[0] == 0) {
			throw new RuntimeException(
					"Could not create a new vertex buffer object.");
		}
		bufferId = buffers[0];
		ShortBuffer mShortBuffer = ByteBuffer
				.allocateDirect(indices.length * BYTES_PER_SHORT)
				.order(ByteOrder.nativeOrder()).asShortBuffer().put(indices);
		mShortBuffer.position(0);
		// Bind to the buffer.
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, bufferId);
		// Transfer data from native memory to the GPU buffer.
		glBufferData(GL_ELEMENT_ARRAY_BUFFER, mShortBuffer.capacity()
				* BYTES_PER_SHORT, mShortBuffer, GL_STATIC_DRAW);
		// IMPORTANT: Unbind from the buffer when we're done with it.
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
	}

	/**
	 * Should be called before garbage collection of this object to avoid memory
	 * leaks in VRAM
	 */
	public void dispose() {
		glDeleteBuffers(1, buffers, 0);
	}
}
