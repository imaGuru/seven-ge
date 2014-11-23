
package com.engine.sevenge.test;

import android.content.Intent;
import android.test.ActivityUnitTestCase;

import com.engine.sevenge.SevenGE;
import com.engine.sevenge.TestActivity;
import com.engine.sevenge.assets.Asset;
import com.engine.sevenge.assets.AssetManager;
import com.engine.sevenge.audio.Audio;
import com.engine.sevenge.io.IO;

public class AssetManagerTest extends ActivityUnitTestCase<TestActivity> {

	private TestActivity activity;

	public AssetManagerTest () {
		super(TestActivity.class);
	}

	public AssetManagerTest (Class<TestActivity> activityClass) {
		super(activityClass);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void setUp () throws Exception {
		super.setUp();
		Intent intent = new Intent(getInstrumentation().getTargetContext(), TestActivity.class);
		startActivity(intent, null, null);
		activity = getActivity();
		SevenGE.audio = new Audio(activity);
		SevenGE.io = new IO(activity);
		SevenGE.assetManager = new AssetManager();

	}

	@Override
	public void tearDown () {
		SevenGE.assetManager = null;
	}

	public void testGetNotExistingAsset () {
		Asset asset = null;
		asset = SevenGE.assetManager.getAsset("nonexistingasset");
		assertNull(asset);
	}

	public void testLoadAssetAndGetAsset () {

		Asset asset = null;
		SevenGE.assetManager.loadAssets("sample.pkg");
		asset = SevenGE.assetManager.getAsset("apple");
		assertNotNull(asset);
	}

	public void testClearAssetsTest () {

		Asset asset = null;
		SevenGE.assetManager.loadAssets("sample.pkg");
		SevenGE.assetManager.clearAssets();
		asset = SevenGE.assetManager.getAsset("apple");
		assertNull(asset);
	}
}
