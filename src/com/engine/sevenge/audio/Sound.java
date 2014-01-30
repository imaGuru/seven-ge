package com.engine.sevenge.audio;

import com.engine.sevenge.assets.Asset;

import android.media.SoundPool;

public class Sound extends Asset{

	int soundId;
	SoundPool soundPool;

	public Sound(SoundPool soundPool, int soundId) {
		this.soundId = soundId;
		this.soundPool = soundPool;
	}

	public void play(float volume) {
		soundPool.play(soundId, volume, volume, 0, 0, 1);
	}

	@Override
	public void dispose() {
		soundPool.unload(soundId);
	}
}
