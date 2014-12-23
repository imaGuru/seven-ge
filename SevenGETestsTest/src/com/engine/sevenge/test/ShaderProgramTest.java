
package com.engine.sevenge.test;

import android.opengl.GLES20;

import com.sevenge.IO;
import com.sevenge.graphics.ColorShaderProgram;
import com.sevenge.graphics.ShaderUtils;
import com.sevenge.graphics.TextureShaderProgram;

public class ShaderProgramTest extends BaseOpenGLES20UnitTest {

	public ShaderProgramTest () {

	}

	public void testColorProgramLoading () throws Throwable {
		runOnGLThread(new TestWrapper() {
			@Override
			public void executeTest () throws Throwable {
				int v = ShaderUtils.compileShader(IO.readToString(IO.openAsset("Shaders/simple_vertex_shader.glsl")),
					GLES20.GL_VERTEX_SHADER);
				int f = ShaderUtils.compileShader(IO.readToString(IO.openAsset("Shaders/simple_fragment_shader.glsl")),
					GLES20.GL_FRAGMENT_SHADER);
				ColorShaderProgram csp = new ColorShaderProgram(v, f);
				assertFalse(csp.mGlID == 0);
			}
		});
	}

	public void testTextureProgramLoading () throws Throwable {
		runOnGLThread(new TestWrapper() {
			@Override
			public void executeTest () throws Throwable {
				int v = ShaderUtils.compileShader(IO.readToString(IO.openAsset("Shaders/texture_vertex_shader.glsl")),
					GLES20.GL_VERTEX_SHADER);
				int f = ShaderUtils.compileShader(IO.readToString(IO.openAsset("Shaders/texture_fragment_shader.glsl")),
					GLES20.GL_FRAGMENT_SHADER);
				TextureShaderProgram tsp = new TextureShaderProgram(v, f);
				assertFalse(tsp.mGlID == 0);
			}
		});
	}

	public void emulatortestProgramValidationInvalid () throws Throwable {
		runOnGLThread(new TestWrapper() {
			@Override
			public void executeTest () throws Throwable {
				int f = ShaderUtils.compileShader(IO.readToString(IO.openAsset("Shaders/texture_fragment_shader.glsl")),
					GLES20.GL_FRAGMENT_SHADER);
				TextureShaderProgram tsp = new TextureShaderProgram(f, f);
				assertFalse(ShaderUtils.validateProgram(tsp.mGlID));
			}
		});
	}

	public void testProgramValidationValid () throws Throwable {
		runOnGLThread(new TestWrapper() {
			@Override
			public void executeTest () throws Throwable {
				int v = ShaderUtils.compileShader(IO.readToString(IO.openAsset("Shaders/texture_vertex_shader.glsl")),
					GLES20.GL_VERTEX_SHADER);
				int f = ShaderUtils.compileShader(IO.readToString(IO.openAsset("Shaders/texture_fragment_shader.glsl")),
					GLES20.GL_FRAGMENT_SHADER);
				TextureShaderProgram tsp = new TextureShaderProgram(v, f);
				assertTrue(ShaderUtils.validateProgram(tsp.mGlID));
			}
		});
	}
}
