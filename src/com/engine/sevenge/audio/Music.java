package com.engine.sevenge.audio;

import java.io.IOException;

import android.app.Activity;
import android.app.Application.ActivityLifecycleCallbacks;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;

import com.engine.sevenge.assets.Asset;

public class Music extends Asset implements OnCompletionListener,
		ActivityLifecycleCallbacks
{
	MediaPlayer mediaPlayer;
	boolean isPrepared = false;
	boolean wasPlaying = false;

	public Music(AssetFileDescriptor assetDescriptor)
	{
		mediaPlayer = new MediaPlayer();
		try
		{
			mediaPlayer.setDataSource(assetDescriptor.getFileDescriptor(),
					assetDescriptor.getStartOffset(),
					assetDescriptor.getLength());
			mediaPlayer.prepare();
			isPrepared = true;
			mediaPlayer.setOnCompletionListener(this);
		} catch (Exception e)
		{
			throw new RuntimeException("Couldn't load music");
		}

	}

	public void play()
	{
		if (mediaPlayer.isPlaying())
			return;
		try
		{
			synchronized (this)
			{
				if (!isPrepared)
					mediaPlayer.prepare();
				mediaPlayer.start();
				wasPlaying = true;
			}
		} catch (IllegalStateException e)
		{
			e.printStackTrace();
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	private void resume()
	{
		if (wasPlaying)
		{
			this.play();
		}
	}

	public void stop()
	{
		mediaPlayer.stop();
		synchronized (this)
		{
			isPrepared = false;
			wasPlaying = false;
		}
	}

	public void pause()
	{
		if (mediaPlayer.isPlaying())
			mediaPlayer.pause();
	}

	public void setLooping(boolean isLooping)
	{
		mediaPlayer.setLooping(isLooping);

	}

	public void setVolume(float volume)
	{
		mediaPlayer.setVolume(volume, volume);
	}

	public boolean isPlaying()
	{
		return mediaPlayer.isPlaying();
	}

	public boolean isStopped()
	{
		return !isPrepared;
	}

	public boolean isLooping()
	{
		return mediaPlayer.isLooping();
	}

	@Override
	public void dispose()
	{
		if (mediaPlayer.isPlaying())
		{
			mediaPlayer.stop();
		}
		mediaPlayer.release();

	}

	@Override
	public void onCompletion(MediaPlayer arg0)
	{
		synchronized (this)
		{
			isPrepared = false;
		}

	}

	@Override
	public void onActivityCreated(Activity activity, Bundle savedInstanceState)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void onActivityDestroyed(Activity activity)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void onActivityPaused(Activity activity)
	{
		this.pause();

	}

	@Override
	public void onActivityResumed(Activity activity)
	{

		this.resume();

	}

	@Override
	public void onActivitySaveInstanceState(Activity activity, Bundle outState)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void onActivityStarted(Activity activity)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void onActivityStopped(Activity activity)
	{
		this.stop();

	}

}
