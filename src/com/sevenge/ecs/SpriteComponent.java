package com.sevenge.ecs;

import com.sevenge.graphics.TextureRegion;

public class SpriteComponent extends Component {
	public static final int MASK = 1 << 1; // 0x02
	public float scale;
	public TextureRegion textureRegion;
}
