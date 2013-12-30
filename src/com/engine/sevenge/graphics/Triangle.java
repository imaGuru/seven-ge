package com.engine.sevenge.graphics;

import static android.opengl.GLES20.GL_FRAGMENT_SHADER;
import static android.opengl.GLES20.GL_TRIANGLES;
import static android.opengl.GLES20.GL_VERTEX_SHADER;
import static android.opengl.GLES20.glDrawArrays;
import static android.opengl.GLES20.glGetAttribLocation;
import static android.opengl.GLES20.glGetUniformLocation;
import static android.opengl.GLES20.glUniformMatrix4fv;
import android.content.Context;

import com.engine.sevenge.R;
import com.engine.sevenge.utils.Helper;

public class Triangle implements Drawable{

	private static final int POSITION_COMPONENT_COUNT = 2;
	private static final int COLOR_COMPONENT_COUNT = 3;
	private static final int BYTES_PER_FLOAT = 4;
	private static final int STRIDE = (POSITION_COMPONENT_COUNT + COLOR_COMPONENT_COUNT)
			* BYTES_PER_FLOAT;

	private static final String A_COLOR = "a_Color";
	private int aColorLocation;
	
	private static final String A_POSITION = "a_Position";
	private int aPositionLocation;

	private static final String U_MATRIX = "u_Matrix";
	private int uMatrixLocation;
	
	float[] matrix;
	ShaderProgram program;
	VertexArray vertexArray;
	
	public Triangle(float[] arr, Context context)
	{
		Shader vertexShader = new Shader(Helper.readRawTextFile(context, R.raw.simple_vertex_shader),GL_VERTEX_SHADER);
		Shader fragmentShader = new Shader(Helper.readRawTextFile(context, R.raw.simple_fragment_shader),GL_FRAGMENT_SHADER);
		program = new ShaderProgram(vertexShader.getGLID(), fragmentShader.getGLID());
		
		aPositionLocation = glGetAttribLocation(program.getGLID(), A_POSITION);
		aColorLocation = glGetAttribLocation(program.getGLID(), A_COLOR);
		
		uMatrixLocation = glGetUniformLocation(program.getGLID(), U_MATRIX);
		
		vertexArray = new VertexArray(arr);
		program.validateProgram();
	}
	public void setMatrix(float[] m)
	{
		matrix = m;
	}
	@Override
	public void draw() {
		program.use();
		glUniformMatrix4fv(uMatrixLocation, 1, false, matrix, 0);
		vertexArray.setVertexAttribPointer(0, aPositionLocation, POSITION_COMPONENT_COUNT, STRIDE);
		vertexArray.setVertexAttribPointer(POSITION_COMPONENT_COUNT, aColorLocation, COLOR_COMPONENT_COUNT, STRIDE);
		glDrawArrays(GL_TRIANGLES, 0, 3);
	}
	
}
