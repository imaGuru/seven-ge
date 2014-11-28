
package com.engine.sevenge.test;

import java.io.IOException;

import junit.framework.Assert;

import com.engine.sevenge.assets.TextureRegion;
import com.engine.sevenge.graphics.TextureUtils;
import com.engine.sevenge.io.IO;

public class TextureTest extends BaseOpenGLES20UnitTest {

	private final float[] correctv = {5, 5, -5, 5, -5, -5, 5, -5};
	private final float[] correctuvs = {0, 0, 0, 0.009765625f, 0.009765625f, 0.009765625f, 0.009765625f, 0};

	public TextureTest () {
		super();
	}

	public void testTextureLoading () throws Throwable {
		runOnGLThread(new TestWrapper() {
			@Override
			public void executeTest () throws Throwable {
				TextureUtils tex;
				int glid = 0;
				try {
					tex = new TextureUtils(IO.openAsset("Textures/SpaceSheet.png"));
					glid = tex.getGLID();
				} catch (IOException e) {
					Assert.fail("Error " + e.getMessage());
					e.printStackTrace();
				}
				assertTrue(glid > 0);
			}
		});
	}

	public void testTextureRegionCreation () throws Throwable {
		runOnGLThread(new TestWrapper() {
			@Override
			public void executeTest () throws Throwable {
				TextureUtils tex;
				int glid = 0;
				try {
					tex = new TextureUtils(IO.openAsset("Textures/SpaceSheet.png"));
					TextureRegion tr = new TextureRegion(10, 10, 0, 0, tex);
					for (int i = 0; i < tr.UVs.length; i++) {
						if (tr.vertices[i] != correctv[i]) Assert.fail("Bad texture region vertex coordinate variables");
						if (tr.UVs[i] != correctuvs[i]) Assert.fail("Bad texture region uv coordinate variables");

					}
				} catch (IOException e) {
					Assert.fail("Error " + e.getMessage());
					e.printStackTrace();
				}
				assertTrue(glid > 0);
			}
		});
	}
}
