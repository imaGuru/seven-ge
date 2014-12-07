
package com.sevenge.graphics;

import java.io.File;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;

import com.sevenge.assets.Font;
import com.sevenge.assets.TextureRegion;

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

	public static Font load (File ttfFont, int size, int padX, int padY) {
		int fontPadX, fontPadY; // Font Padding (Pixels; On Each Side, ie. Doubled on Both X+Y Axis)

		float fontHeight; // Font Height (Actual; Pixels)
		float fontAscent; // Font Ascent (Above Baseline; Pixels)
		float fontDescent; // Font Descent (Below Baseline; Pixels)

		float charWidthMax; // Character Width (Maximum; Pixels)
		float charHeight; // Character Height (Maximum; Pixels)
		final float[] charWidths = new float[CHAR_CNT]; // Width of Each Character (Actual; Pixels)
		TextureRegion[] charRgn = new TextureRegion[CHAR_CNT]; // Region of Each Character (Texture Coordinates)
		int cellWidth, cellHeight; // Character Cell Width/Height
		int rowCnt, colCnt; // Number of Rows/Columns
		int textureSize;
		float scaleX, scaleY; // Font Scale (X,Y Axis)
		float spaceX; // Additional (X,Y Axis) Spacing (Unscaled)
		// setup requested values
		fontPadX = padX; // Set Requested X Axis Padding
		fontPadY = padY; // Set Requested Y Axis Padding

		// load the font and setup paint instance for drawing
		Typeface tf = Typeface.createFromFile(ttfFont);
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
			charWidths[cnt] = w[0]; // Get Width
			if (charWidths[cnt] > charWidthMax) // IF Width Larger Than Max Width
				charWidthMax = charWidths[cnt]; // Save New Max Width
			cnt++; // Advance Array Counter
		}
		s[0] = CHAR_NONE; // Set Unknown Character
		paint.getTextWidths(s, 0, 1, w); // Get Character Bounds
		charWidths[cnt] = w[0]; // Get Width
		if (charWidths[cnt] > charWidthMax) // IF Width Larger Than Max Width
			charWidthMax = charWidths[cnt]; // Save New Max Width
		cnt++; // Advance Array Counter

		// set character height to font height
		charHeight = fontHeight; // Set Character Height

		// find the maximum size, validate, and setup cell sizes
		cellWidth = (int)charWidthMax + (2 * fontPadX); // Set Cell Width
		cellHeight = (int)charHeight + (2 * fontPadY); // Set Cell Height
		int maxSize = cellWidth > cellHeight ? cellWidth : cellHeight; // Save Max Size (Width/Height)
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
		colCnt = textureSize / cellWidth; // Calculate Number of Columns
		rowCnt = (int)Math.ceil((float)CHAR_CNT / (float)colCnt); // Calculate Number of Rows

		// render each of the characters to the canvas (ie. build the font map)
		float x = fontPadX; // Set Start Position (X)
		float y = (cellHeight - 1) - fontDescent - fontPadY; // Set Start Position (Y)
		for (char c = CHAR_START; c <= CHAR_END; c++) { // FOR Each Character
			s[0] = c; // Set Character to Draw
			canvas.drawText(s, 0, 1, x, y, paint); // Draw Character
			x += cellWidth; // Move to Next Character
			if ((x + cellWidth - fontPadX) > textureSize) { // IF End of Line Reached
				x = fontPadX; // Set X for New Row
				y += cellHeight; // Move Down a Row
			}
		}
		s[0] = CHAR_NONE; // Set Character to Use for NONE
		canvas.drawText(s, 0, 1, x, y, paint); // Draw Character
		// release the bitmap
		bitmap.recycle(); // Release the Bitmap
		return new Font();
	}

	public void draw (String text, float x, float y, Font font, SpriteBatcher sb) {
		float chrHeight = font.cellHeight * font.scaleY; // Calculate Scaled Character Height
		float chrWidth = font.cellWidth * font.scaleX; // Calculate Scaled Character Width
		int len = text.length(); // Get String Length
		x += (chrWidth / 2.0f) - (font.fontPadX * font.scaleX); // Adjust Start X
		y += (chrHeight / 2.0f) - (font.fontPadY * font.scaleY); // Adjust Start Y
		for (int i = 0; i < len; i++) { // FOR Each Character in String
			int c = (int)text.charAt(i) - CHAR_START; // Calculate Character Index (Offset by First Char in Font)
			if (c < 0 || c >= CHAR_CNT) // IF Character Not In Font
				c = CHAR_UNKNOWN; // Set to Unknown Character Index
			sb.addSprite(x, y, chrWidth, chrHeight, font.charRgn[c]); // Draw the Character
			x += (font.charWidths[c] + font.spaceX) * font.scaleX; // Advance X Position by Scaled Character Width
		}
	}

	public float getLength (String text) {
		float len = 0.0f; // Working Length
		int strLen = text.length(); // Get String Length (Characters)
		for (int i = 0; i < strLen; i++) { // For Each Character in String (Except Last
			int c = (int)text.charAt(i) - CHAR_START; // Calculate Character Index (Offset by First Char in Font)
			len += (charWidths[c] * scaleX); // Add Scaled Character Width to Total Length
		}
		len += (strLen > 1 ? ((strLen - 1) * spaceX) * scaleX : 0); // Add Space Length
		return len; // Return Total Length
	}
}
