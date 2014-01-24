package com.engine.sevenge.io;

import android.content.Context;

public class IO {
	private Context ctx;

	public IO(Context ctx) {
		this.ctx = ctx;
	}

	public FileHandle internal(String file) {
		return new FileHandle(ctx.getFilesDir() + "/" + file);
	}

	public FileHandle external(String file) {
		return new FileHandle(ctx.getExternalFilesDir(null) + "/" + file);
	}

	public FileHandle cache(String file) {
		return new FileHandle(ctx.getCacheDir() + "/" + file);
	}

	public FileHandle asset(String file) {
		return new FileHandle(ctx.getAssets(), file);
	}
}
