
package com.engine.sevenge.test;

import android.content.Intent;
import android.test.ActivityUnitTestCase;

import com.sevenge.IO;
import com.sevenge.SevenGE;
import com.sevenge.assets.Asset;
import com.sevenge.assets.AssetManager;
import com.sevenge.audio.Audio;
import com.sevenge.utils.UnitTestActivity;

public class AssetManagerTest extends ActivityUnitTestCase<UnitTestActivity> {

	private UnitTestActivity activity;

	public AssetManagerTest () {
		super(UnitTestActivity.class);
	}

	public AssetManagerTest (Class<UnitTestActivity> activityClass) {
		super(activityClass);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void setUp () throws Exception {
		super.setUp();
		Intent intent = new Intent(getInstrumentation().getTargetContext(), UnitTestActivity.class);
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
