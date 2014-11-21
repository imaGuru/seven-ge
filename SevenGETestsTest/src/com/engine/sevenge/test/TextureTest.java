
package com.engine.sevenge.test;

import java.io.IOException;

import junit.framework.Assert;

import com.engine.sevenge.graphics.Texture;
import com.engine.sevenge.io.IO;

public class TextureTest extends BaseOpenGLES20UnitTest {

	public TextureTest () {
		super();
	}

	public void testTextureLoading () throws Throwable {
		runOnGLThread(new TestWrapper() {
			@Override
			public void executeTest () throws Throwable {
				Texture tex;
				int glid = 0;
				try {
					tex = new Texture(IO.openAsset("Textures/SpaceSheet.png"));
					glid = tex.getGLID();
				} catch (IOException e) {
					Assert.fail("Error " + e.getMessage());
					e.printStackTrace();
				}
				assertTrue(glid > 0);
			}
		});
	}
}
