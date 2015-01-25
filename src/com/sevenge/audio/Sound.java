
package com.sevenge.audio;

import android.media.SoundPool;

import com.sevenge.assets.Asset;
/** Music class which manages the playback of audio files with the Android SoundPool**/
public class Sound extends Asset {

	int soundId;
	SoundPool soundPool;

	public Sound (SoundPool soundPool, int soundId) {
		this.soundId = soundId;
		this.soundPool = soundPool;
	}
	/** Starts the playback **/
	public void play (float volume) {
		soundPool.play(soundId, volume, volume, 0, 0, 1);
	}

	@Override
	public void dispose () {
		soundPool.unload(soundId);
	}
}
