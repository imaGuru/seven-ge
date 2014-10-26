
package com.engine.sevenge.ecs;

import com.engine.sevenge.graphics.TextureRegion;

public class SpriteComponent extends Component {
	public static int MASK = 1 << 1; // 0x02
	public float scale;
	public TextureRegion subTexture;
}
