
package com.engine.sevenge.test;

import android.opengl.GLES20;

import com.engine.sevenge.graphics.Shader;

public class ShaderTest extends BaseOpenGLES20UnitTest {
	private static final String validFragmentShaderCode = "precision mediump float;                            \n"
		+ "void main()                                         \n" + "{                                                   \n"
		+ "  gl_FragColor = vec4(1.0, 0.0, 0.0, 1.0);          \n" + "}                                                   \n";

	private static final String invalidFragmentShaderCode = "precision mediump float;                            \n"
		+ "void main()                                         \n" + "{                                                   \n"
		+ "  syntax error                                      \n" + "}                                                   \n";

	public ShaderTest () {
	}

	public void testShaderLoadingValid () throws Throwable {
		runOnGLThread(new TestWrapper() {
			@Override
			public void executeTest () throws Throwable {
				Shader handle = new Shader(validFragmentShaderCode, GLES20.GL_FRAGMENT_SHADER);
				assertTrue(handle.getGLID() != 0);
				assertTrue(GLES20.glIsShader(handle.getGLID()));
			}
		});
	}

	public void testShaderLoadingInvalid () throws Throwable {
		runOnGLThread(new TestWrapper() {
			@Override
			public void executeTest () throws Throwable {
				Shader handle = new Shader(invalidFragmentShaderCode, GLES20.GL_FRAGMENT_SHADER);
				assertEquals(0, handle.getGLID());
			}
		});
	}
}
