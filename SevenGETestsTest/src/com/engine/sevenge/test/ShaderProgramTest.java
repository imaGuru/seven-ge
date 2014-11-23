
package com.engine.sevenge.test;

import android.opengl.GLES20;

import com.engine.sevenge.graphics.ColorShaderProgram;
import com.engine.sevenge.graphics.Shader;
import com.engine.sevenge.graphics.TextureShaderProgram;
import com.engine.sevenge.io.IO;

public class ShaderProgramTest extends BaseOpenGLES20UnitTest {

	public ShaderProgramTest () {

	}

	public void testColorProgramLoading () throws Throwable {
		runOnGLThread(new TestWrapper() {
			@Override
			public void executeTest () throws Throwable {
				Shader v = new Shader(IO.readToString(IO.openAsset("Shaders/simple_vertex_shader.glsl")), GLES20.GL_VERTEX_SHADER);
				Shader f = new Shader(IO.readToString(IO.openAsset("Shaders/simple_fragment_shader.glsl")), GLES20.GL_FRAGMENT_SHADER);
				ColorShaderProgram csp = new ColorShaderProgram(v, f);
				assertFalse(csp.getGLID() == 0);
			}
		});
	}

	public void testTextureProgramLoading () throws Throwable {
		runOnGLThread(new TestWrapper() {
			@Override
			public void executeTest () throws Throwable {
				Shader v = new Shader(IO.readToString(IO.openAsset("Shaders/texture_vertex_shader.glsl")), GLES20.GL_VERTEX_SHADER);
				Shader f = new Shader(IO.readToString(IO.openAsset("Shaders/texture_fragment_shader.glsl")),
					GLES20.GL_FRAGMENT_SHADER);
				TextureShaderProgram tsp = new TextureShaderProgram(v, f);
				assertFalse(tsp.getGLID() == 0);
			}
		});
	}

	public void emulatortestProgramValidationInvalid () throws Throwable {
		runOnGLThread(new TestWrapper() {
			@Override
			public void executeTest () throws Throwable {
				Shader f = new Shader(IO.readToString(IO.openAsset("Shaders/texture_fragment_shader.glsl")),
					GLES20.GL_FRAGMENT_SHADER);
				TextureShaderProgram tsp = new TextureShaderProgram(f, f);
				assertFalse(tsp.validateProgram());
			}
		});
	}

	public void testProgramValidationValid () throws Throwable {
		runOnGLThread(new TestWrapper() {
			@Override
			public void executeTest () throws Throwable {
				Shader v = new Shader(IO.readToString(IO.openAsset("Shaders/texture_vertex_shader.glsl")), GLES20.GL_VERTEX_SHADER);
				Shader f = new Shader(IO.readToString(IO.openAsset("Shaders/texture_fragment_shader.glsl")),
					GLES20.GL_FRAGMENT_SHADER);
				TextureShaderProgram tsp = new TextureShaderProgram(v, f);
				assertTrue(tsp.validateProgram());
			}
		});
	}
}
