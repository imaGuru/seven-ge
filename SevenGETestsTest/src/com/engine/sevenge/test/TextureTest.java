package com.engine.sevenge.test;

import java.io.IOException;

import junit.framework.Assert;

import com.sevenge.IO;
import com.sevenge.assets.Texture;
import com.sevenge.graphics.TextureUtils;

public class TextureTest extends BaseOpenGLES20UnitTest {

	private final float[] correctv = { 5, 5, -5, 5, -5, -5, 5, -5 };
	private final float[] correctuvs = { 0, 0, 0, 0.009765625f, 0.009765625f,
			0.009765625f, 0.009765625f, 0 };

	public TextureTest() {
		super();
	}

	public void testTextureLoading() throws Throwable {
		runOnGLThread(new TestWrapper() {
			@Override
			public void executeTest() throws Throwable {
				Texture tex;
				int glid = 0;
				try {
					tex = TextureUtils.createTexture(IO
							.openAsset("Textures/SpaceSheet.png"));
					glid = tex.glID;
				} catch (IOException e) {
					Assert.fail("Error " + e.getMessage());
					e.printStackTrace();
				}
				assertTrue(glid > 0);
			}
		});
	}
}
