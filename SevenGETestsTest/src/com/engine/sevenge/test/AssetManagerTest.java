package com.engine.sevenge.test;

import android.content.Intent;
import android.test.ActivityUnitTestCase;

import com.sevenge.IO;
import com.sevenge.SevenGE;
import com.sevenge.assets.Asset;
import com.sevenge.assets.AudioLoader;
import com.sevenge.utils.UnitTestActivity;

public class AssetManagerTest extends ActivityUnitTestCase<UnitTestActivity> {

	private UnitTestActivity activity;

	public AssetManagerTest() {
		super(UnitTestActivity.class);
	}

	public AssetManagerTest(Class<UnitTestActivity> activityClass) {
		super(activityClass);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void setUp() throws Exception {
		super.setUp();
		Intent intent = new Intent(getInstrumentation().getTargetContext(),
				UnitTestActivity.class);
		startActivity(intent, null, null);
		activity = getActivity();
		// SevenGE.audio = new Audio(activity);

		// SevenGE.assetManager = new AssetManager();

		// SevenGE.getAssetManager().addLoader("spriteSheet", new
		// SpriteSheetFTLoader(SevenGE.getAssetManager()));
		// SevenGE.getAssetManager().addLoader("texture", new
		// TextureLoader(SevenGE.getAssetManager()));
		// SevenGE.getAssetManager().addLoader("audio", new
		// AudioLoader(SevenGE.getAssetManager()));
		// SevenGE.getAssetManager().loadAssets("package_test.pkg");

	}

	@Override
	public void tearDown() {
		// SevenGE.assetManager = null;
	}

	public void testGetNotExistingAsset() {
		Asset asset = null;
		asset = SevenGE.getAssetManager().getAsset("nonexistingasset");
		assertNull(asset);
	}

	public void testLoadAssetAndGetAsset() {

		Asset asset = null;
		// SevenGE.getAssetManager().loadAssets("package.pkg");
		IO.initialize(activity);
		SevenGE.getAssetManager().addLoader("audio",
				new AudioLoader(SevenGE.getAssetManager()));
		SevenGE.getAssetManager().loadAssets("package_test.pkg");
		asset = SevenGE.getAssetManager().getAsset("music1");
		assertNotNull(asset);
	}

	public void testClearAssetsTest() {

		Asset asset = null;
		// SevenGE.getAssetManager().loadAssets("package.pkg");
		SevenGE.getAssetManager().clearAssets();
		asset = SevenGE.getAssetManager().getAsset("music1");
		assertNull(asset);
	}
}
