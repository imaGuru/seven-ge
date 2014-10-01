
package com.engine.sevenge.io;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

import android.content.res.AssetManager;
import android.util.Log;

public class FileHandle {
	private String file;
	private AssetManager assets = null;
	private final String TAG = "IO";

	public FileHandle (String file) {
		this.file = file;
	}

	public FileHandle (AssetManager assets, String file) {
		this.assets = assets;
		this.file = file;
	}

	public InputStream getInputStream () {
		InputStream inputStream;
		try {
			if (assets != null)
				inputStream = assets.open(file);
			else
				inputStream = new FileInputStream(file);
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
			return null;
		} catch (IOException e) {
			return null;
		}
		return inputStream;
	}

	public OutputStream getOutputStream () {
		OutputStream outputStream = null;
		if (assets != null) {
			Log.e(TAG, "You are trying to write an asset! That's illegal");
			return outputStream;
		} else
			try {
				outputStream = new FileOutputStream(file);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		return outputStream;
	}

	public String readString () {
		InputStream inputStream;
		try {
			if (assets != null)
				inputStream = assets.open(file);
			else
				inputStream = new FileInputStream(file);
			InputStreamReader inputreader = new InputStreamReader(inputStream);
			BufferedReader buffreader = new BufferedReader(inputreader);
			String line;
			StringBuilder text = new StringBuilder();
			while ((line = buffreader.readLine()) != null) {
				text.append(line);
				text.append('\n');
			}
			return text.toString();
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
			return null;
		} catch (IOException e) {
			return null;
		}
	}
}
