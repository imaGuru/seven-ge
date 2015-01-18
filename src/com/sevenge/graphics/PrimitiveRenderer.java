
package com.sevenge.graphics;

import static android.opengl.GLES20.GL_LINES;
import static android.opengl.GLES20.GL_TRIANGLES;
import static android.opengl.GLES20.GL_UNSIGNED_SHORT;
import static android.opengl.GLES20.glDisableVertexAttribArray;
import static android.opengl.GLES20.glDrawElements;
import static android.opengl.GLES20.glUseProgram;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;

/** Class enabling the user to draw circles, rectangles, lines and arrows */
public class PrimitiveRenderer {
	private static final float DOUBLEPI = (float)(Math.PI * 2.0f);
	private final VertexArray mVertexArray;
	/** IndexBuffer Object containing face definitions */
	private final ShortBuffer mIndexBuffer;
	/** Shader program used to color primitives */
	public final ColorShaderProgram shader;
	/** Indices data */
	private final short[] mIndices;
	private final float[] mPrimitives;
	private final float[] mProjectionMatrix;

	private int mPrimitiveCount = 0;
	private final int mSize;
	private int mVertexCountPeak;
	private boolean mFillEnabled = false;
	private static final int POSITION_COMPONENT_COUNT = 2;
	private static final int COLOR_COMPONENT_COUNT = 3;
	private static final int BYTES_PER_FLOAT = 4;
	private static final int BYTES_PER_SHORT = 2;
	private static final int STRIDE = (POSITION_COMPONENT_COUNT + COLOR_COMPONENT_COUNT) * BYTES_PER_FLOAT;

	/** Creates a new PrimitiveRenderer
	 * @param size maximum vertices in a batch call */
	public PrimitiveRenderer (int size) {
		mSize = size;
		mIndices = new short[mSize * 2];
		mProjectionMatrix = new float[16];
		shader = ShaderUtils.COLOR_SHADER;
		mPrimitives = new float[mSize * (POSITION_COMPONENT_COUNT + COLOR_COMPONENT_COUNT)];
		mVertexArray = new VertexArray(mSize * (POSITION_COMPONENT_COUNT + COLOR_COMPONENT_COUNT));
		mIndexBuffer = ByteBuffer.allocateDirect(mSize * 2 * BYTES_PER_SHORT).order(ByteOrder.nativeOrder()).asShortBuffer();
	}

	/** Begins this rendering. Sets OpenGL state for drawing primitives */
	public void begin () {
		glUseProgram(shader.glID);
		mVertexArray.setVertexAttribPointer(0, shader.attributePositionLocation, POSITION_COMPONENT_COUNT, STRIDE);
		mVertexArray.setVertexAttribPointer(POSITION_COMPONENT_COUNT, shader.atributeColorLocation, COLOR_COMPONENT_COUNT, STRIDE);
	}

	/** Finishes this rendering, reverting OpenGL state changes */
	public void end () {
		if (mPrimitiveCount > 0) flush();
		glDisableVertexAttribArray(shader.attributePositionLocation);
		glDisableVertexAttribArray(shader.atributeColorLocation);
		glUseProgram(0);
	}

	/** Sets the view projection matrix for the current batch
	 * @param matrix with projection */
	public void setProjection (float[] matrix) {
		if (mPrimitiveCount > 0) flush();
		System.arraycopy(matrix, 0, this.mProjectionMatrix, 0, 16);
	}

	/** Enables primitive filling. Not used */
	public void enableFill () {
		if (mFillEnabled) return;
		if (mPrimitiveCount > 0) flush();
		mFillEnabled = true;
	}

	/** Disables primitive filling. Not used */
	public void disableFill () {
		if (!mFillEnabled) return;
		if (mPrimitiveCount > 0) flush();
		mFillEnabled = false;
	}

	/** Draw the created batch using specified view projection matrix */
	public void flush () {
		shader.setUniforms(mProjectionMatrix);
		mVertexArray.put(mPrimitives, mPrimitiveCount * (POSITION_COMPONENT_COUNT + COLOR_COMPONENT_COUNT));
		if (mFillEnabled) {
			mIndexBuffer.put(mIndices, 0, mPrimitiveCount * 3);
			mIndexBuffer.position(0);
			glDrawElements(GL_TRIANGLES, mPrimitiveCount * 3, GL_UNSIGNED_SHORT, mIndexBuffer);
		} else {
			mIndexBuffer.put(mIndices, 0, mPrimitiveCount * 2);
			mIndexBuffer.position(0);
			glDrawElements(GL_LINES, mPrimitiveCount * 2, GL_UNSIGNED_SHORT, mIndexBuffer);
		}
		if (mVertexCountPeak < mPrimitiveCount) mVertexCountPeak = mPrimitiveCount;
		mPrimitiveCount = 0;
		mVertexArray.clear();
	}

	/** @param cx x center of the circle
	 * @param cy y center of the circle
	 * @param r radius of the circle
	 * @param rotation angle in radians
	 * @param nsegments number of vertices approximating circle
	 * @param red component of color
	 * @param green component of color
	 * @param blue component of color */
	public void drawCircle (float cx, float cy, float r, float rotation, int nsegments, float red, float green, float blue) {
		if (mPrimitiveCount == mSize) flush();
		float t, x = (float)Math.cos(-rotation) * r, y = (float)Math.sin(-rotation) * r;
		float c = (float)Math.cos(DOUBLEPI / nsegments);
		float s = (float)Math.sin(DOUBLEPI / nsegments);
		for (int i = mPrimitiveCount; i < mPrimitiveCount + nsegments; i++) {
			mPrimitives[i * 5] = x + cx;
			mPrimitives[i * 5 + 1] = y + cy;
			mPrimitives[i * 5 + 2] = red;
			mPrimitives[i * 5 + 3] = green;
			mPrimitives[i * 5 + 4] = blue;
			t = x;
			x = c * x - s * y;
			y = s * t + c * y;
		}
		short offset = (short)(mPrimitiveCount * 2);
		for (short i = 0; i < nsegments; i++) {
			mIndices[offset + i * 2] = (short)(mPrimitiveCount + i);
			mIndices[offset + i * 2 + 1] = (short)(mPrimitiveCount + (i + 1) % nsegments);
		}
		mPrimitiveCount += nsegments;
	}

	/** Draws a rectangle
	 * @param cx x center of rectangle
	 * @param cy y center of rectangle
	 * @param w width of the rectangle
	 * @param h height of the rectangle
	 * @param rotation angle in radians
	 * @param red component of color
	 * @param green component of color
	 * @param blue component of color */
	public void drawRectangle (float cx, float cy, float w, float h, float rotation, float red, float green, float blue) {
		if (mPrimitiveCount == mSize) flush();
		float c = (float)Math.cos(-rotation);
		float s = (float)Math.sin(-rotation);
		float x = w / 2, y = h / 2;
		int offset = mPrimitiveCount * 5;
		mPrimitives[offset] = x * c - s * y + cx;
		mPrimitives[offset + 1] = s * x + c * y + cy;
		mPrimitives[offset + 2] = red;
		mPrimitives[offset + 3] = green;
		mPrimitives[offset + 4] = blue;
		x = -w / 2;
		mPrimitives[offset + 5] = x * c - s * y + cx;
		mPrimitives[offset + 6] = s * x + c * y + cy;
		mPrimitives[offset + 7] = red;
		mPrimitives[offset + 8] = green;
		mPrimitives[offset + 9] = blue;
		y = -h / 2;
		mPrimitives[offset + 10] = x * c - s * y + cx;
		mPrimitives[offset + 11] = s * x + c * y + cy;
		mPrimitives[offset + 12] = red;
		mPrimitives[offset + 13] = green;
		mPrimitives[offset + 14] = blue;
		x = w / 2;
		mPrimitives[offset + 15] = x * c - s * y + cx;
		mPrimitives[offset + 16] = s * x + c * y + cy;
		mPrimitives[offset + 17] = red;
		mPrimitives[offset + 18] = green;
		mPrimitives[offset + 19] = blue;

		offset = mPrimitiveCount * 2;
		for (short i = 0; i < 4; i++) {
			mIndices[offset + i * 2] = (short)(mPrimitiveCount + i);
			mIndices[offset + i * 2 + 1] = (short)(mPrimitiveCount + (i + 1) % 4);
		}
		mPrimitiveCount += 4;
	}

	/** @param x origin x coordinate
	 * @param y origin y coordinate
	 * @param x2 end x coordinate
	 * @param y2 end y coordinate
	 * @param red component of color
	 * @param green component of color
	 * @param blue component of color */
	public void drawLine (float x, float y, float x2, float y2, float red, float green, float blue) {
		if (mPrimitiveCount == mSize) flush();
		int offset = mPrimitiveCount * 5;
		mPrimitives[offset] = x;
		mPrimitives[offset + 1] = y;
		mPrimitives[offset + 2] = red;
		mPrimitives[offset + 3] = green;
		mPrimitives[offset + 4] = blue;

		mPrimitives[offset + 5] = x2;
		mPrimitives[offset + 6] = y2;
		mPrimitives[offset + 7] = red;
		mPrimitives[offset + 8] = green;
		mPrimitives[offset + 9] = blue;

		offset = mPrimitiveCount * 2;
		mIndices[offset] = (short)mPrimitiveCount;
		mIndices[offset + 1] = (short)(mPrimitiveCount + 1);
		mPrimitiveCount += 2;
	}

	/** Draws a line in specified direction
	 * @param x origin x coordinate
	 * @param y origin y coordinate
	 * @param angle in radians
	 * @param length of the line
	 * @param red component of color
	 * @param green component of color
	 * @param blue component of color */
	public void drawAngleLine (float x, float y, float angle, float length, float red, float green, float blue) {
		float x2 = (float)(Math.sin(angle) * length + x);
		float y2 = (float)(Math.cos(angle) * length + y);
		drawLine(x, y, x2, y2, red, green, blue);
	}

	/** Draws a vector (arrow)
	 * @param ox orgin x coordinate
	 * @param oy orgin y coordinate
	 * @param xdist vector x
	 * @param ydist vector y
	 * @param arrowWidth width of the arrow
	 * @param arrowHeightFactor heght of the arrow
	 * @param red component of color
	 * @param green component of color
	 * @param blue component of color */
	public void drawVector (float ox, float oy, float xdist, float ydist, float arrowWidth, float arrowHeightFactor, float red,
		float green, float blue) {
		float destx = ox + xdist;
		float desty = oy + ydist;
		float angle = (float)Math.atan2(desty - oy, destx - ox);
		float cos = (float)Math.sin(-angle);
		float sin = (float)Math.cos(-angle);
		float xl = -arrowWidth * cos + destx - xdist * arrowHeightFactor;
		float yl = sin * (-arrowWidth) + desty - ydist * arrowHeightFactor;
		float xr = arrowWidth * cos + destx - xdist * arrowHeightFactor;
		float yr = sin * arrowWidth + desty - ydist * arrowHeightFactor;
		drawLine(ox, oy, destx, desty, red, green, blue);
		drawLine(destx, desty, xl, yl, red, green, blue);
		drawLine(destx, desty, xr, yr, red, green, blue);
	}
}
