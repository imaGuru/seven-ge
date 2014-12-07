
package com.sevenge.ecs;

import com.sevenge.assets.TextureRegion;

public class AnimationComponent extends Component {
	public static int MASK = 1 << 3; // 0x04
	public int[] durations;
	public TextureRegion[] frameList;
	public boolean isPlaying;
	public int currentFrameTick;
	public int currentFrame;
}
