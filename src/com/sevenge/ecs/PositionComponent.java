package com.sevenge.ecs;

public class PositionComponent extends Component {
	public static final int MASK = 1 << 0; // 0x01
	public float x;
	public float y;
	public float rotation;
	public int layer;
}
