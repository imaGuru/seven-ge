
package com.sevenge.graphics;

import static android.opengl.GLES20.GL_LINEAR;
import static android.opengl.GLES20.GL_LINEAR_MIPMAP_LINEAR;
import static android.opengl.GLES20.GL_TEXTURE_2D;
import static android.opengl.GLES20.GL_TEXTURE_MAG_FILTER;
import static android.opengl.GLES20.GL_TEXTURE_MIN_FILTER;
import static android.opengl.GLES20.glBindTexture;
import static android.opengl.GLES20.glDeleteTextures;
import static android.opengl.GLES20.glGenTextures;
import static android.opengl.GLES20.glGenerateMipmap;
import static android.opengl.GLES20.glTexParameteri;

import java.io.InputStream;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLUtils;

import com.sevenge.assets.Texture;
import com.sevenge.utils.DebugLog;

/** Responsible for loading and using textures in opengl */
public class TextureUtils {
	private static final String TAG = "Texture2D";

	/** Loads the texture from specified inputstream
	 * @param in InputStream with texture data */
	public static Texture createTexture (InputStream in) {
		int[] textureID = new int[1];
		glGenTextures(1, textureID, 0);
		if (textureID[0] == 0) {
			DebugLog.w(TAG, "Could not generate a new OpenGL texture object.");
			return null;
		}
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inScaled = false;
		final Bitmap bitmap = BitmapFactory.decodeStream(in, null, options);
		if (bitmap == null) {
			DebugLog.w(TAG, "Texture could not be decoded.");
			glDeleteTextures(1, textureID, 0);
		}
		glBindTexture(GL_TEXTURE_2D, textureID[0]);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
		GLUtils.texImage2D(GL_TEXTURE_2D, 0, bitmap, 0);
		bitmap.recycle();
		glGenerateMipmap(GL_TEXTURE_2D);
		glBindTexture(GL_TEXTURE_2D, 0);
		return new Texture(textureID[0], bitmap.getWidth(), bitmap.getHeight());
	}

	/** Creates a new OpenGL texture from given bitmap
	 * @param bitmap
	 * @return */
	public static Texture createTexture (Bitmap bitmap) {
		int[] textureID = new int[1];
		glGenTextures(1, textureID, 0);
		if (textureID[0] == 0) {
			DebugLog.w(TAG, "Could not generate a new OpenGL texture object.");
			return null;
		}
		if (bitmap == null) {
			DebugLog.w(TAG, "Texture is null.");
			glDeleteTextures(1, textureID, 0);
		}
		glBindTexture(GL_TEXTURE_2D, textureID[0]);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
		GLUtils.texImage2D(GL_TEXTURE_2D, 0, bitmap, 0);
		bitmap.recycle();
		glGenerateMipmap(GL_TEXTURE_2D);
		glBindTexture(GL_TEXTURE_2D, 0);
		return new Texture(textureID[0], bitmap.getWidth(), bitmap.getHeight());
	}
}
