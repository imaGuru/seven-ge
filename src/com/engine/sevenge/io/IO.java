
package com.engine.sevenge.io;

import java.io.File;

import android.content.Context;

public class IO {
	private Context context;

	public IO (Context context) {
		this.context = context;
	}

	public File internal (String file) {
		return new File(context.getFilesDir(), file);
	}

	public File external (String file) {
		return new File(context.getExternalFilesDir(null), file);
	}

	public File cache (String file) {
		return new File(context.getCacheDir(), file);
	}

	public File asset (String file) {
		return context.getAssets().open(file);
	}
}
