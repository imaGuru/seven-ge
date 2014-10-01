
package com.engine.sevenge.ecs;

public class SpriteComponent extends Component {
	public int type = 1 << 1; // 0x02
	public float scale;
	public String subTexture;
}
