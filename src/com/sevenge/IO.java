package com.sevenge;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

/** Provides helper methods for Android IO **/
public class IO {
	private static Context context = null;
	public static String INTERNAL_PATH;
	public static String EXTERNAL_PATH;
	public static String CACHE_PATH;

	/** Initializes IO with context of app activity. Has to be called before use */
	public static void initialize(Context context) {
		IO.context = context;
		INTERNAL_PATH = context.getFilesDir().getAbsolutePath();
		EXTERNAL_PATH = context.getExternalFilesDir(null).getAbsolutePath();
		CACHE_PATH = context.getCacheDir().getAbsolutePath();
	}

	/** Returns android asset manager of the current app */
	public static AssetManager getAssetManager() {
		return IO.context.getAssets();
	}

	/**
	 * Opens or creates a file residing in internal android storage
	 * 
	 * @param file
	 *            string with the path to the file (has to relative to internal
	 *            file directory)
	 * @return file handle to the specified file
	 */
	public static File internal(String file) {
		return new File(context.getFilesDir(), file);
	}

	/**
	 * Opens or creates a file residing in external android storage
	 * 
	 * @param file
	 *            string with the path to the file (has to relative to external
	 *            file directory)
	 * @return file handle to the specified file
	 */
	public static File external(String file) {
		return new File(context.getExternalFilesDir(null), file);
	}

	/**
	 * Opens or creates a file residing in cache android storage
	 * 
	 * @param file
	 *            string with the path to the file (has to relative to cache
	 *            file directory)
	 * @return file handle to the specified file
	 */
	public static File cache(String file) {
		return new File(context.getCacheDir(), file);
	}

	/**
	 * Opens an asset embedded in the apk
	 * 
	 * @param file
	 *            string with the path to the file (has to relative to internal
	 *            file directory)
	 * @return inputstream to the asset
	 */
	public static InputStream openAsset(String file) throws IOException {
		return context.getAssets().open(file);
	}

	/**
	 * Renames the file specified
	 * 
	 * @param srcFile
	 *            File we want to rename
	 * @param destFile
	 *            File we want to rename srcFile to
	 * @throws IOException
	 */
	public static void renameFile(File srcFile, File destFile)
			throws IOException {
		boolean bSucceeded = false;
		try {
			if (destFile.exists()) {
				if (!destFile.delete()) {
					throw new IOException("Error renaming files");
				}
			}
			if (!srcFile.renameTo(destFile)) {
				throw new IOException("Error renaming files");
			} else {
				bSucceeded = true;
			}
		} finally {
			if (bSucceeded) {
				srcFile.delete();
			}
		}
	}

	/**
	 * Copies the file opened in in to the file output out
	 * 
	 * @param in
	 *            InputStream to the file we want to copy
	 * @param out
	 *            OutputStream to the output file
	 */
	public static void copyFile(InputStream in, OutputStream out) {
		try {
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

	/**
	 * Reads the specified inputStream into a string
	 * 
	 * @param ins
	 *            InputStream of the file we want read
	 * @return String with file contents
	 */
	public static String readToString(InputStream ins) {
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

	/**
	 * Lists files from the specified root path
	 * 
	 * @param rootPath
	 *            directory to scan
	 * @return array with file handles
	 */
	public static File[] getFiles(String rootPath) {
		File f = new File(rootPath);
		File files[] = f.listFiles();
		return files;
	}

	/**
	 * Deletes file at the given location
	 * 
	 * @param filePath
	 *            path to file
	 */
	public static void deleteFile(String filePath) {
		File file = new File(filePath);
		file.delete();
	}

}
