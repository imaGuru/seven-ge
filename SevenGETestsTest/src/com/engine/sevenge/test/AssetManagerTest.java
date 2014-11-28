
package com.engine.sevenge.test;

import junit.framework.TestCase;

import com.engine.sevenge.SevenGE;
import com.engine.sevenge.assets.Asset;
import com.engine.sevenge.assets.AssetManager;

public class AssetManagerTest extends TestCase {
	public AssetManagerTest () {

	}

	@Override
	public void setUp () {
		SevenGE.assetManager = new AssetManager();
	}

	@Override
	public void tearDown () {
		SevenGE.assetManager = null;
	}

	public void getNotExistingAssetTest () {
		Asset asset = null;
		asset = SevenGE.assetManager.getAsset("nonexistingasset");
		assertNull(asset);
	}

	public void loadAssetAndGetAssetTest () {

		Asset asset = null;
		SevenGE.assetManager.loadAssets("sample.pkg");
		asset = SevenGE.assetManager.getAsset("apple");
		assertNotNull(asset);
	}

	public void clearAssetsTest () {

		Asset asset = null;
		SevenGE.assetManager.loadAssets("sample.pkg");
		SevenGE.assetManager.clearAssets();
		asset = SevenGE.assetManager.getAsset("apple");
		assertNull(asset);
	}
}
