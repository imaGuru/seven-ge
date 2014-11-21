
package com.engine.sevenge.test;

import java.io.IOException;
import java.io.InputStream;

import junit.framework.Assert;
import android.test.AndroidTestCase;

import com.engine.sevenge.SevenGE;
import com.engine.sevenge.io.IO;

public class IOTest extends AndroidTestCase {

	public IOTest () {
	}

	@Override
	public void setUp () {
		SevenGE.io = new IO(mContext);
	}

	@Override
	public void tearDown () {
		SevenGE.io = null;
	}

	public void testOpenAssetsValid () {
		InputStream asset = null;
		try {
			asset = IO.openAsset("Textures/SpaceSheet.png");
		} catch (IOException e) {
			e.printStackTrace();
		}
		assertNotNull(asset);
	}

	public void testOpenAssetsInvalid () {
		try {
			IO.openAsset("nonexistent.png");
			Assert.fail();
		} catch (IOException e) {
		}
	}

}
