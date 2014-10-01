
package com.engine.sevenge.io;

import android.content.Context;

public class IO {
	private Context context;

	public IO (Context context) {
		this.context = context;
	}

	public FileHandle internal (String file) {
		return new FileHandle(context.getFilesDir() + "/" + file);
	}

	public FileHandle external (String file) {
		return new FileHandle(context.getExternalFilesDir(null) + "/" + file);
	}

	public FileHandle cache (String file) {
		return new FileHandle(context.getCacheDir() + "/" + file);
	}

	public FileHandle asset (String file) {
		return new FileHandle(context.getAssets(), file);
	}
}
