
package com.sevenge.graphics;

import static android.opengl.GLES20.GL_COMPILE_STATUS;
import static android.opengl.GLES20.GL_LINK_STATUS;
import static android.opengl.GLES20.GL_VALIDATE_STATUS;
import static android.opengl.GLES20.glAttachShader;
import static android.opengl.GLES20.glCompileShader;
import static android.opengl.GLES20.glCreateProgram;
import static android.opengl.GLES20.glCreateShader;
import static android.opengl.GLES20.glDeleteProgram;
import static android.opengl.GLES20.glDeleteShader;
import static android.opengl.GLES20.glGetProgramInfoLog;
import static android.opengl.GLES20.glGetProgramiv;
import static android.opengl.GLES20.glGetShaderInfoLog;
import static android.opengl.GLES20.glGetShaderiv;
import static android.opengl.GLES20.glLinkProgram;
import static android.opengl.GLES20.glShaderSource;
import static android.opengl.GLES20.glValidateProgram;

import com.sevenge.utils.DebugLog;

/** Class creating opengl shader and holding reference to opengl object */
public class ShaderUtils {
	private static final String TAG = "ShaderUtils";
	public static final String textureVertexShader = "uniform mat4 u_Matrix;"//
		+ "attribute vec4 a_Position;" //
		+ "attribute vec2 a_TextureCoordinates;" + "varying vec2 v_TextureCoordinates;"//
		+ "void main()"//
		+ "{"//
		+ "	v_TextureCoordinates = a_TextureCoordinates;"//
		+ "	gl_Position = u_Matrix * a_Position;"//
		+ "}";//
	public static final String textureFragmentShader = "precision mediump float;"//
		+ "uniform sampler2D u_TextureUnit;"//
		+ "varying vec2 v_TextureCoordinates;" + "void main()"//
		+ "{"//
		+ "        gl_FragColor = texture2D(u_TextureUnit, v_TextureCoordinates);"//
		+ "}";//

	public static final String colorVertexShader = "uniform mat4 u_Matrix;"//
		+ "attribute vec4 a_Position;"//
		+ "attribute vec4 a_Color;"//
		+ "varying vec4 v_Color;"//
		+ "void main()"//
		+ "{"//
		+ "	v_Color = a_Color;"//
		+ "	gl_Position = u_Matrix * a_Position;"//
		+ "	gl_PointSize = 30.0;"//
		+ "}";
	public static final String colorFragmentShader = "precision mediump float;"//
		+ "varying vec4 v_Color;"//
		+ "void main()"//
		+ "{"//
		+ "	gl_FragColor = v_Color;"//
		+ "}";

	/** Creates shader of specified type (vertex, fragment)
	 * @param shaderCode string with shader code to compile
	 * @param type of the shader (vertex, fragment) */
	public static int compileShader (String shaderCode, int type) {
		int shaderObjectId = glCreateShader(type);
		if (shaderObjectId == 0) {
			DebugLog.w(TAG, "Could not create new shader.");
			return shaderObjectId;
		}
		glShaderSource(shaderObjectId, shaderCode);
		glCompileShader(shaderObjectId);

		final int[] compileStatus = new int[1];
		glGetShaderiv(shaderObjectId, GL_COMPILE_STATUS, compileStatus, 0);
		DebugLog.v(TAG, "Results of compiling source:" + "\n" + shaderCode + "\n:" + glGetShaderInfoLog(shaderObjectId));
		if (compileStatus[0] == 0) {
			// If it failed, delete the shader object.
			glDeleteShader(shaderObjectId);
			shaderObjectId = 0;
			DebugLog.w(TAG, "Compilation of shader failed.");
			return shaderObjectId;
		}
		return shaderObjectId;
	}

	/** Links given vertex shader and fragment shader into an OpenGL shaderProgram
	 * @param vs OpenGL id of vertexShader
	 * @param fs OpenGL id of fragmentShader
	 * @return OpenGL id of compiled shaderProgram */
	public static int linkShaderProgram (int vs, int fs) {
		int programID = glCreateProgram();
		if (programID == 0) {
			DebugLog.w(TAG, "Could not create new program");
		}
		glAttachShader(programID, vs);
		glAttachShader(programID, fs);
		glLinkProgram(programID);
		final int[] linkStatus = new int[1];
		glGetProgramiv(programID, GL_LINK_STATUS, linkStatus, 0);
		DebugLog.v(TAG, "Results of linking program:\n" + glGetProgramInfoLog(programID));
		if (linkStatus[0] == 0) {
			// If it failed, delete the program object.
			glDeleteProgram(programID);
			DebugLog.w(TAG, "Linking of program failed.");
		}
		return programID;
	}

	/** Validates whether compilation of given OpenGL shaderProgram completed successfully
	 * @param programID
	 * @return */
	public static boolean validateProgram (int programID) {
		glValidateProgram(programID);
		final int[] validateStatus = new int[1];
		glGetProgramiv(programID, GL_VALIDATE_STATUS, validateStatus, 0);
		DebugLog.v(TAG, "Results of validating program: " + validateStatus[0] + "\nLog:" + glGetProgramInfoLog(programID));
		return validateStatus[0] != 0;
	}
}
