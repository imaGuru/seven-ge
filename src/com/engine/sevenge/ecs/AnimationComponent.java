
package com.engine.sevenge.ecs;

import com.engine.sevenge.graphics.SubTexture2D;

public class AnimationComponent extends Component {
	public static int MASK = 1 << 2; // 0x04
	public int[] durations;
	public SubTexture2D[] frameList;
	public boolean isPlaying;
	public int currentFrameTick;
	public int currentFrame;
}
