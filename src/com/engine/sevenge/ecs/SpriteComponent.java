
package com.engine.sevenge.ecs;

import com.engine.sevenge.graphics.SubTexture2D;

public class SpriteComponent extends Component {
	public static int MASK = 1 << 1; // 0x02
	public float scale;
	public SubTexture2D subTexture;
}
