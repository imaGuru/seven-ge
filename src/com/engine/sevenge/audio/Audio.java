package com.engine.sevenge.audio;

import java.io.IOException;

import android.app.Activity;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.AudioManager;
import android.media.SoundPool;

public class Audio
{
	AssetManager assets;
	SoundPool soundPool;
	Activity activity;

	public final static int MAX_SIMULTANEOUS_SOUNDS = 10;

	public Audio(Activity activity)
	{
		activity.setVolumeControlStream(AudioManager.STREAM_MUSIC);
		this.assets = activity.getAssets();
		this.soundPool = new SoundPool(MAX_SIMULTANEOUS_SOUNDS,
				AudioManager.STREAM_MUSIC, 0);
		this.activity = activity;
	}

	public Music getMusic(String filename)
	{
		try
		{
			AssetFileDescriptor assetDescriptor = assets.openFd(filename);
			Music newMusic = new Music(assetDescriptor);
			return newMusic;
		} catch (IOException e)
		{
			throw new RuntimeException("Couldn't load music '" + filename + "'");
		}
	}

	public Sound getSound(String filename)
	{
		try
		{
			AssetFileDescriptor assetDescriptor = assets.openFd(filename);
			int soundId = soundPool.load(assetDescriptor, 0);
			return new Sound(soundPool, soundId);
		} catch (IOException e)
		{
			throw new RuntimeException("Couldn't load sound '" + filename + "'");
		}
	}

}
