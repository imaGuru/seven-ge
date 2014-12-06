// This is a OpenGL ES 1.0 dynamic font rendering system. It loads actual font
// files, generates a font map (texture) from them, and allows rendering of
// text strings.
//
// NOTE: the rendering portions of this class uses a sprite batcher in order
// provide decent speed rendering. Also, rendering assumes a BOTTOM-LEFT
// origin, and the (x,y) positions are relative to that, as well as the
// bottom-left of the string to render.

package com.sevenge.fonts;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.microedition.khronos.opengles.GL10;

import com.sevenge.IO;
import com.sevenge.assets.TextureRegion;
import com.sevenge.graphics.SpriteBatch;
import com.sevenge.utils.Log;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.opengl.GLUtils;
import android.os.Environment;

public class GLText {

  // --Constants--//
  public final static int CHAR_START = 32; // First Character (ASCII Code)
  public final static int CHAR_END = 126; // Last Character (ASCII Code)
  public final static int CHAR_CNT = (((CHAR_END - CHAR_START) + 1) + 1); // Character Count
                                                                          // (Including Character to
                                                                          // use for Unknown)

  public final static int CHAR_NONE = 32; // Character to Use for Unknown (ASCII Code)
  public final static int CHAR_UNKNOWN = (CHAR_CNT - 1); // Index of the Unknown Character

  public final static int FONT_SIZE_MIN = 6; // Minumum Font Size (Pixels)
  public final static int FONT_SIZE_MAX = 180; // Maximum Font Size (Pixels)

  public final static int CHAR_BATCH_SIZE = 100; // Number of Characters to Render Per Batch

  // --Members--//
  GL10 gl; // GL10 Instance
  AssetManager assets; // Asset Manager
  SpriteBatch batch; // Batch Renderer

  int fontPadX, fontPadY; // Font Padding (Pixels; On Each Side, ie. Doubled on Both X+Y Axis)

  float fontHeight; // Font Height (Actual; Pixels)
  float fontAscent; // Font Ascent (Above Baseline; Pixels)
  float fontDescent; // Font Descent (Below Baseline; Pixels)

  int textureId; // Font Texture ID [NOTE: Public for Testing Purposes Only!]
  int textureSize; // Texture Size for Font (Square) [NOTE: Public for Testing Purposes Only!]
  TextureRegion textureRgn; // Full Texture Region


  float charWidthMax; // Character Width (Maximum; Pixels)
  float charHeight; // Character Height (Maximum; Pixels)
  final float[] charWidths; // Width of Each Character (Actual; Pixels)
  TextureRegion[] charRgn; // Region of Each Character (Texture Coordinates)
  int cellWidth, cellHeight; // Character Cell Width/Height
  int rowCnt, colCnt; // Number of Rows/Columns

  float scaleX, scaleY; // Font Scale (X,Y Axis)
  float spaceX; // Additional (X,Y Axis) Spacing (Unscaled)


  // --Constructor--//
  // D: save GL instance + asset manager, create arrays, and initialize the members
  // A: gl - OpenGL ES 10 Instance
  public GLText(GL10 gl, AssetManager assets) {
    this.gl = gl; // Save the GL10 Instance
    this.assets = assets; // Save the Asset Manager Instance



    charWidths = new float[CHAR_CNT]; // Create the Array of Character Widths
    charRgn = new TextureRegion[CHAR_CNT]; // Create the Array of Character Regions

    // initialize remaining members
    fontPadX = 0;
    fontPadY = 0;

    fontHeight = 0.0f;
    fontAscent = 0.0f;
    fontDescent = 0.0f;

    textureId = -1;
    textureSize = 0;

    charWidthMax = 0;
    charHeight = 0;

    cellWidth = 0;
    cellHeight = 0;
    rowCnt = 0;
    colCnt = 0;

    scaleX = 1.0f; // Default Scale = 1 (Unscaled)
    scaleY = 1.0f; // Default Scale = 1 (Unscaled)
    spaceX = 0.0f;
  }

  // --Load Font--//
  // description
  // this will load the specified font file, create a texture for the defined
  // character range, and setup all required values used to render with it.
  // arguments:
  // file - Filename of the font (.ttf, .otf) to use. In 'Assets' folder.
  // size - Requested pixel size of font (height)
  // padX, padY - Extra padding per character (X+Y Axis); to prevent overlapping characters.
  public boolean load(String file, int size, int padX, int padY) {

    // setup requested values
    fontPadX = padX; // Set Requested X Axis Padding
    fontPadY = padY; // Set Requested Y Axis Padding

    // load the font and setup paint instance for drawing
    Typeface tf = Typeface.createFromAsset(assets, file); // Create the Typeface from Font File
    Paint paint = new Paint(); // Create Android Paint Instance
    paint.setAntiAlias(true); // Enable Anti Alias
    paint.setTextSize(size); // Set Text Size
    paint.setColor(0xffffffff); // Set ARGB (White, Opaque)
    paint.setTypeface(tf); // Set Typeface

    // get font metrics
    Paint.FontMetrics fm = paint.getFontMetrics(); // Get Font Metrics
    fontHeight = (float) Math.ceil(Math.abs(fm.bottom) + Math.abs(fm.top)); // Calculate Font Height
    fontAscent = (float) Math.ceil(Math.abs(fm.ascent)); // Save Font Ascent
    fontDescent = (float) Math.ceil(Math.abs(fm.descent)); // Save Font Descent

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
    cellWidth = (int) charWidthMax + (2 * fontPadX); // Set Cell Width
    cellHeight = (int) charHeight + (2 * fontPadY); // Set Cell Height
    int maxSize = cellWidth > cellHeight ? cellWidth : cellHeight; // Save Max Size (Width/Height)
    if (maxSize < FONT_SIZE_MIN || maxSize > FONT_SIZE_MAX) // IF Maximum Size Outside Valid Bounds
      return false; // Return Error

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
    Bitmap bitmap = Bitmap.createBitmap(textureSize, textureSize, Bitmap.Config.ALPHA_8); // Create
                                                                                          // Bitmap
    Canvas canvas = new Canvas(bitmap); // Create Canvas for Rendering to Bitmap
    bitmap.eraseColor(0x00000000); // Set Transparent Background (ARGB)

    // calculate rows/columns
    // NOTE: while not required for anything, these may be useful to have :)
    colCnt = textureSize / cellWidth; // Calculate Number of Columns
    rowCnt = (int) Math.ceil((float) CHAR_CNT / (float) colCnt); // Calculate Number of Rows

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

    // generate a new texture
    int[] textureIds = new int[1]; // Array to Get Texture Id
//    gl.glGenTextures(1, textureIds, 0); // Generate New Texture
    textureId = textureIds[0]; // Save Texture Id

    // setup filters for texture
//    gl.glBindTexture(GL10.GL_TEXTURE_2D, textureId); // Bind Texture
//    gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_NEAREST); // Set
                                                                                         // Minification
                                                                                         // Filter
//    gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR); // Set
//                                                                                        // Magnification
//                                                                                        // Filter
//    gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S, GL10.GL_CLAMP_TO_EDGE); // Set U
//                                                                                           // Wrapping
//    gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T, GL10.GL_CLAMP_TO_EDGE); // Set V
//                                                                                           // Wrapping

    // load the generated bitmap onto the texture
    GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap, 0); // Load Bitmap to Texture
//    gl.glBindTexture(GL10.GL_TEXTURE_2D, 0); // Unbind Texture

    //gowno sie dzieje
    
    String root = Environment.getExternalStorageDirectory().toString();
    File f = IO.internal("sexy_font.png");
    Log.i("LOCATION", f.toString());
    OutputStream fOut = null;
    try {
      fOut = new FileOutputStream(f);
    } catch (FileNotFoundException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    bitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut); 
    try {
      fOut.flush();
      fOut.close(); 
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    // release the bitmap
    bitmap.recycle(); // Release the Bitmap
    
    // setup the array of character texture regions
    x = 0; // Initialize X
    y = 0; // Initialize Y

    return true; 
  }



}
