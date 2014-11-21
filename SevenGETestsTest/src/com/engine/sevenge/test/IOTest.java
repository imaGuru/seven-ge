
package com.engine.sevenge.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
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
		IO.internal("testCopy.pkg").delete();
		IO.internal("renamed.pkg").delete();
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

	public void testOpenInternalFile () {
		File file = IO.internal("Scripts/main.lua");
		assertFalse(file.exists());
	}

	public void testOpenExternalFile () {
		File file = IO.external("Scripts/main.lua");
		assertFalse(file.exists());
	}

	public void testOpenCacheFile () {
		File file = IO.cache("Scripts/main.lua");
		assertFalse(file.exists());
	}

	public void testReadToString () {
		try {
			String s = IO.readToString(IO.openAsset("Scripts/main.lua"));
			assertFalse(s.isEmpty());
		} catch (IOException e) {
			Assert.fail("File not found");
			e.printStackTrace();
		}
	}

	public void testRenameFile () {
		try {
			File file1 = IO.internal("test.pkg");
			file1.createNewFile();
			File file2 = IO.internal("renamed.pkg");
			IO.renameFile(file1, file2);
			assertTrue(file2.isFile() && file2.exists());
		} catch (IOException e) {
			e.printStackTrace();
			Assert.fail("Failed to rename file");
		}
	}

	public void testCopyFile () {
		try {
			File file1 = IO.internal("test.pkg");
			file1.createNewFile();
			File file2 = IO.internal("testCopy.pkg");
			IO.copyFile(new FileInputStream(file1), new FileOutputStream(file2));
			assertTrue(file2.isFile() && file2.exists());
		} catch (IOException e) {
			e.printStackTrace();
			Assert.fail("Failed to rename file");
		}
	}

}
