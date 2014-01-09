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
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLUtils;

import com.engine.sevenge.utils.Log;

public class Texture2D {
	private static final String TAG = "Texture2D";
	private final int textureID;

	public Texture2D(Context context, int resourceID) {
		final int[] textureObjectIds = new int[1];
		glGenTextures(1, textureObjectIds, 0);
		if (textureObjectIds[0] == 0) {
			Log.w(TAG, "Could not generate a new OpenGL texture object.");
			textureID = 0;
			return;
		}
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inScaled = false;
		final Bitmap bitmap = BitmapFactory.decodeResource(
				context.getResources(), resourceID, options);
		if (bitmap == null) {
			Log.w(TAG, "Resource ID " + resourceID + " could not be decoded.");
			glDeleteTextures(1, textureObjectIds, 0);
		}
		glBindTexture(GL_TEXTURE_2D, textureObjectIds[0]);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER,
				GL_LINEAR_MIPMAP_LINEAR);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
		GLUtils.texImage2D(GL_TEXTURE_2D, 0, bitmap, 0);
		bitmap.recycle();
		glGenerateMipmap(GL_TEXTURE_2D);
		glBindTexture(GL_TEXTURE_2D, 0);
		textureID = textureObjectIds[0];
	}

	public void bindTexture(int textureUnit) {
		glActiveTexture(textureUnit);
		glBindTexture(GL_TEXTURE_2D, textureID);
	}

	public int getGLID() {
		return textureID;
	}
}
