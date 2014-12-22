
package com.sevenge.ecs;

public class CameraComponent extends Component {
	public static final int MASK = 1 << 2;
	public final float[] projectionMatrix = new float[16];
	public final float[] viewMatrix = new float[16];
	public final float[] viewProjectionMatrix = new float[16];
	public final float[] invertedVPMatrix = new float[16];
	public int height;
	public int width;
	public int scaledWidth;
	public int scaledHeight;
	public float scale = 1f;
}
