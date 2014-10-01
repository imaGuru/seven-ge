
package com.engine.sevenge.graphics;

import static android.opengl.GLES20.GL_LINEAR;
import static android.opengl.GLES20.GL_LINEAR_MIPMAP_LINEAR;
import static android.opengl.GLES20.GL_TEXTURE_2D;
import static android.opengl.GLES20.GL_TEXTURE_MAG_FILTER;
import static android.opengl.GLES20.GL_TEXTURE_MIN_FILTER;
import static android.opengl.GLES20.glActiveTexture;
import static android.opengl.GLES20.glBindTexture;
import static android.opengl.GLES20.glDeleteTextures;
import static android.opengl.GLES20.glGenTextures;
import static android.opengl.GLES20.glGenerateMipmap;
import static android.opengl.GLES20.glTexParameteri;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLUtils;

import com.engine.sevenge.assets.Asset;
import com.engine.sevenge.io.FileHandle;
import com.engine.sevenge.utils.Log;

public class Texture2D extends Asset {
	private static final String TAG = "Texture2D";
	private final int[] textureID;
	private final int width;
	private final int height;

	public Texture2D (FileHandle fh) {
		textureID = new int[1];
		glGenTextures(1, textureID, 0);
		if (textureID[0] == 0) {
			Log.w(TAG, "Could not generate a new OpenGL texture object.");
			width = 0;
			height = 0;
			return;
		}
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inScaled = false;
		final Bitmap bitmap = BitmapFactory.decodeStream(fh.getInputStream(), null, options);
		if (bitmap == null) {
			Log.w(TAG, "Texture could not be decoded.");
			glDeleteTextures(1, textureID, 0);
		}
		glBindTexture(GL_TEXTURE_2D, textureID[0]);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
		GLUtils.texImage2D(GL_TEXTURE_2D, 0, bitmap, 0);
		width = bitmap.getWidth();
		height = bitmap.getHeight();
		bitmap.recycle();
		glGenerateMipmap(GL_TEXTURE_2D);
		glBindTexture(GL_TEXTURE_2D, 0);
	}

	public void bindTexture (int textureUnit) {
		glActiveTexture(textureUnit);
		glBindTexture(GL_TEXTURE_2D, textureID[0]);
	}

	public int getGLID () {
		return textureID[0];
	}

	@Override
	public void dispose () {
		glDeleteTextures(1, textureID, 0);
	}

	public int getHeight () {
		return height;
	}

	public int getWidth () {
		return width;
	}
}
