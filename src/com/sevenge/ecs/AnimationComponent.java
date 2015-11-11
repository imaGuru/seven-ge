package com.sevenge.ecs;

import com.sevenge.graphics.TextureRegion;

public class AnimationComponent extends Component {
	public static final int MASK = 1 << 3; // 0x08
	public int[] durations;
	public TextureRegion[] frameList;
	public boolean isPlaying;
	public int currentFrameTick;
	public int currentFrame;
	public boolean isLooping;
}
