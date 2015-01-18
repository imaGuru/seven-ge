
package com.sevenge.graphics;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;

import com.sevenge.assets.Font;
import com.sevenge.assets.Texture;

public class FontUtils {
	public final static int CHAR_START = 32; // First Character (ASCII Code)
	public final static int CHAR_END = 126; // Last Character (ASCII Code)
	public final static int CHAR_CNT = (((CHAR_END - CHAR_START) + 1) + 1); // Character Count
																									// (Including Character to
																									// use for Unknown)

	public final static int CHAR_NONE = 32; // Character to Use for Unknown (ASCII Code)
	public final static int CHAR_UNKNOWN = (CHAR_CNT - 1); // Index of the Unknown Character

	public final static int FONT_SIZE_MIN = 6; // Minumum Font Size (Pixels)
	public final static int FONT_SIZE_MAX = 180; // Maximum Font Size (Pixels)

	/** Loads specified font with the given size and padding
	 * @param as AndroidAssetManager
	 * @param ttfFont path to font asset
	 * @param size font size
	 * @param padX padding between letters
	 * @param padY padding between letters
	 * @return loaded font */
	public static Font load (AssetManager as, String ttfFont, int size, int padX, int padY) {

		float fontHeight; // Font Height (Actual; Pixels)
		float fontAscent; // Font Ascent (Above Baseline; Pixels)
		float fontDescent; // Font Descent (Below Baseline; Pixels)

		float charWidthMax; // Character Width (Maximum; Pixels)
		float charHeight; // Character Height (Maximum; Pixels)
		int rowCnt, colCnt; // Number of Rows/Columns
		int textureSize;
		// setup requested values
		Font font = new Font();
		font.fontPadX = padX; // Set Requested X Axis Padding
		font.fontPadY = padY; // Set Requested Y Axis Padding

		font.charWidths = new float[CHAR_CNT]; // Width of Each Character (Actual; Pixels)
		font.charRgn = new TextureRegion[CHAR_CNT]; // Region of Each Character (Texture Coordinates)
		// load the font and setup paint instance for drawing
		Typeface tf = Typeface.createFromAsset(as, ttfFont);
		Paint paint = new Paint(); // Create Android Paint Instance
		paint.setAntiAlias(true); // Enable Anti Alias
		paint.setTextSize(size); // Set Text Size
		paint.setColor(0xffffffff); // Set ARGB (White, Opaque)
		paint.setTypeface(tf); // Set Typeface

		// get font metrics
		Paint.FontMetrics fm = paint.getFontMetrics(); // Get Font Metrics
		fontHeight = (float)Math.ceil(Math.abs(fm.bottom) + Math.abs(fm.top)); // Calculate Font Height
		fontAscent = (float)Math.ceil(Math.abs(fm.ascent)); // Save Font Ascent
		fontDescent = (float)Math.ceil(Math.abs(fm.descent)); // Save Font Descent

		// determine the width of each character (including unknown character)
		// also determine the maximum character width
		char[] s = new char[2]; // Create Character Array
		charWidthMax = charHeight = 0; // Reset Character Width/Height Maximums
		float[] w = new float[2]; // Working Width Value
		int cnt = 0; // Array Counter
		for (char c = CHAR_START; c <= CHAR_END; c++) { // FOR Each Character
			s[0] = c; // Set Character
			paint.getTextWidths(s, 0, 1, w); // Get Character Bounds
			font.charWidths[cnt] = w[0]; // Get Width
			if (font.charWidths[cnt] > charWidthMax) // IF Width Larger Than Max Width
				charWidthMax = font.charWidths[cnt]; // Save New Max Width
			cnt++; // Advance Array Counter
		}
		s[0] = CHAR_NONE; // Set Unknown Character
		paint.getTextWidths(s, 0, 1, w); // Get Character Bounds
		font.charWidths[cnt] = w[0]; // Get Width
		if (font.charWidths[cnt] > charWidthMax) // IF Width Larger Than Max Width
			charWidthMax = font.charWidths[cnt]; // Save New Max Width
		cnt++; // Advance Array Counter

		// set character height to font height
		charHeight = fontHeight; // Set Character Height

		// find the maximum size, validate, and setup cell sizes
		font.cellWidth = (int)charWidthMax + (2 * font.fontPadX); // Set Cell Width
		font.cellHeight = (int)charHeight + (2 * font.fontPadY); // Set Cell Height
		int maxSize = font.cellWidth > font.cellHeight ? font.cellWidth : font.cellHeight; // Save Max Size (Width/Height)
		if (maxSize < FONT_SIZE_MIN || maxSize > FONT_SIZE_MAX) // IF Maximum Size Outside Valid Bounds
			return null; // Return Error

		// set texture size based on max font size (width or height)
		// NOTE: these values are fixed, based on the defined characters. when
		// changing start/end characters (CHAR_START/CHAR_END) this will need adjustment too!
		if (maxSize <= 24) // IF Max Size is 18 or Less
			textureSize = 256; // Set 256 Texture Size
		else if (maxSize <= 40) // ELSE IF Max Size is 40 or Less
			textureSize = 512; // Set 512 Texture Size
		else if (maxSize <= 80) // ELSE IF Max Size is 80 or Less
			textureSize = 1024; // Set 1024 Texture Size
		else
			// ELSE IF Max Size is Larger Than 80 (and Less than FONT_SIZE_MAX)
			textureSize = 2048; // Set 2048 Texture Size

		// create an empty bitmap (alpha only)
		Bitmap bitmap = Bitmap.createBitmap(textureSize, textureSize, Bitmap.Config.ARGB_8888); // Create
																																// Bitmap
		Canvas canvas = new Canvas(bitmap); // Create Canvas for Rendering to Bitmap
		bitmap.eraseColor(0x00000000); // Set Transparent Background (ARGB)

		// calculate rows/columns
		// NOTE: while not required for anything, these may be useful to have :)
		colCnt = textureSize / font.cellWidth; // Calculate Number of Columns
		rowCnt = (int)Math.ceil((float)CHAR_CNT / (float)colCnt); // Calculate Number of Rows

		// render each of the characters to the canvas (ie. build the font map)
		float x = font.fontPadX; // Set Start Position (X)
		float y = (font.cellHeight - 1) - fontDescent - font.fontPadY; // Set Start Position (Y)
		for (char c = CHAR_START; c <= CHAR_END; c++) { // FOR Each Character
			s[0] = c; // Set Character to Draw
			canvas.drawText(s, 0, 1, x, y, paint); // Draw Character
			x += font.cellWidth; // Move to Next Character
			if ((x + font.cellWidth - font.fontPadX) > textureSize) { // IF End of Line Reached
				x = font.fontPadX; // Set X for New Row
				y += font.cellHeight; // Move Down a Row
			}
		}
		s[0] = CHAR_NONE; // Set Character to Use for NONE
		canvas.drawText(s, 0, 1, x, y, paint); // Draw Character
		// release the bitmap
		Texture tex = TextureUtils.createTexture(bitmap);
		x = 0; // Initialize X
		y = 0; // Initialize Y
		for (int c = 0; c < CHAR_CNT; c++) { // FOR Each Character (On Texture)
			font.charRgn[c] = new TextureRegion(font.cellWidth - 1, font.cellHeight - 1, (int)x, (int)y, tex);
			x += font.cellWidth; // Move to Next Char (Cell)
			if (x + font.cellWidth > textureSize) {
				x = 0; // Reset X Position to Start
				y += font.cellHeight; // Move to Next Row (Cell)
			}
		}
		font.texture = tex.glID;
		return font;
	}

	/** Returns length of the rendered text with the given font
	 * @param text
	 * @param font
	 * @return length of the text in pixels */
	public static float getLength (String text, Font font) {
		float len = 0.0f; // Working Length
		int strLen = text.length(); // Get String Length (Characters)
		for (int i = 0; i < strLen; i++) { // For Each Character in String (Except Last
			int c = (int)text.charAt(i) - CHAR_START; // Calculate Character Index (Offset by First Char in Font)
			len += (font.charWidths[c] * font.scaleX); // Add Scaled Character Width to Total Length
		}
		len += (strLen > 1 ? ((strLen - 1) * font.spaceX) * font.scaleX : 0); // Add Space Length
		return len; // Return Total Length
	}
}
