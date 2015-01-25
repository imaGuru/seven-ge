
package com.sevenge.audio;

import java.io.IOException;

import android.app.Activity;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.AudioManager;
import android.media.SoundPool;
/** Audio class which creates Music and Sound instances **/
public class Audio {
	AssetManager assets;
	SoundPool soundPool;
	Activity activity;

	public final static int MAX_SIMULTANEOUS_SOUNDS = 10;

	public Audio (Activity activity) {
		activity.setVolumeControlStream(AudioManager.STREAM_MUSIC);
		this.assets = activity.getAssets();
		this.soundPool = new SoundPool(MAX_SIMULTANEOUS_SOUNDS, AudioManager.STREAM_MUSIC, 0);
		this.activity = activity;
	}
	/**
	 * Returns an instance of Music with the given filename 
	 * @param filename
	 */
	public Music getMusic (String filename) {
		try {
			AssetFileDescriptor assetDescriptor = assets.openFd(filename);
			Music newMusic = new Music(assetDescriptor);
			return newMusic;
		} catch (IOException e) {
			throw new RuntimeException("Couldn't load music '" + filename + "'");
		}
	}
	/**
	 * Returns an instance of Sound with the given filename 
	 * @param filename
	 */
	public Sound getSound (String filename) {
		try {
			AssetFileDescriptor assetDescriptor = assets.openFd(filename);
			int soundId = soundPool.load(assetDescriptor, 0);
			return new Sound(soundPool, soundId);
		} catch (IOException e) {
			throw new RuntimeException("Couldn't load sound '" + filename + "'");
		}
	}

}
