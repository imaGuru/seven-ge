
package com.engine.sevenge.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

import android.content.Context;
import android.util.Log;

public class IO {
	private static Context context = null;

	public IO (Context context) {
		IO.context = context;
	}

	public static File internal (String file) {
		return new File(context.getFilesDir(), file);
	}

	public static File external (String file) {
		return new File(context.getExternalFilesDir(null), file);
	}

	public static File cache (String file) {
		return new File(context.getCacheDir(), file);
	}

	public static InputStream openAsset (String file) throws IOException {
		return context.getAssets().open(file);
	}

	public static void renameFile (String oldName, String newName) throws IOException {
		File srcFile = new File(oldName);
		boolean bSucceeded = false;
		try {
			File destFile = new File(newName);
			if (destFile.exists()) {
				if (!destFile.delete()) {
					throw new IOException(oldName + " was not successfully renamed to " + newName);
				}
			}
			if (!srcFile.renameTo(destFile)) {
				throw new IOException(oldName + " was not successfully renamed to " + newName);
			} else {
				bSucceeded = true;
			}
		} finally {
			if (bSucceeded) {
				srcFile.delete();
			}
		}
	}

	public static void copyFile (String inputPath, String outputPath) {

		InputStream in = null;
		OutputStream out = null;
		try {

			// create output directory if it doesn't exist
			File dir = new File(outputPath);
			if (!dir.exists()) {
				dir.mkdirs();
			}

			in = new FileInputStream(inputPath);
			out = new FileOutputStream(outputPath);

			byte[] buffer = new byte[1024];
			int read;
			while ((read = in.read(buffer)) != -1) {
				out.write(buffer, 0, read);
			}
			in.close();
			in = null;

			// write the output file (You have now copied the file)
			out.flush();
			out.close();
			out = null;

		} catch (FileNotFoundException e) {
			Log.e("IO", e.getMessage());
		} catch (Exception e) {
			Log.e("IO", e.getMessage());
		}
	}

	public static void moveFile (String inputPath, String outputPath) {
		copyFile(inputPath, outputPath);
		// delete the original file
		new File(inputPath).delete();

	}

	public static void delete (String filePath) {
		new File(filePath).delete();
	}

	public static String readToString (InputStream ins) {
		try {
			InputStreamReader isr = new InputStreamReader(ins);
			int read;
			char[] buffer = new char[1024];
			StringBuilder text = new StringBuilder();
			while ((read = isr.read(buffer)) != -1) {
				text.append(buffer, 0, read);
			}
			return text.toString();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			return null;
		}
	}
}
