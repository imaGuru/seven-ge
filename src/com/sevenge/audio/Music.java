
package com.sevenge.audio;

import java.io.IOException;

import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;

import com.sevenge.assets.Asset;

public class Music extends Asset implements OnCompletionListener {
	MediaPlayer mediaPlayer;
	boolean isPrepared = false;

	public Music (AssetFileDescriptor assetDescriptor) {
		mediaPlayer = new MediaPlayer();
		try {
			mediaPlayer.setDataSource(assetDescriptor.getFileDescriptor(), assetDescriptor.getStartOffset(),
				assetDescriptor.getLength());
			mediaPlayer.prepare();
			isPrepared = true;
			mediaPlayer.setOnCompletionListener(this);
		} catch (Exception e) {
			throw new RuntimeException("Couldn't load music");
		}

	}

	public void play () {
		if (mediaPlayer.isPlaying()) return;
		try {
			synchronized (this) {
				if (!isPrepared) mediaPlayer.prepare();
				mediaPlayer.start();
			}
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void stop () {
		mediaPlayer.stop();
		synchronized (this) {
			isPrepared = false;
		}
	}

	public void pause () {
		if (mediaPlayer.isPlaying()) mediaPlayer.pause();
	}

	public void setLooping (boolean isLooping) {
		mediaPlayer.setLooping(isLooping);

	}

	public void setVolume (float volume) {
		mediaPlayer.setVolume(volume, volume);
	}

	public boolean isPlaying () {
		return mediaPlayer.isPlaying();
	}

	public boolean isStopped () {
		return !isPrepared;
	}

	public boolean isLooping () {
		return mediaPlayer.isLooping();
	}

	@Override
	public void dispose () {
		if (mediaPlayer.isPlaying()) {
			mediaPlayer.stop();
		}
		mediaPlayer.release();

	}

	@Override
	public void onCompletion (MediaPlayer arg0) {
		synchronized (this) {
			isPrepared = false;
		}

	}

}
