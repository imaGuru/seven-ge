
package com.engine.sevenge.ecs;

public class AnimationComponent extends Component {
	public int type = 1 << 2; // 0x04
	public int[] durations;
	public String[] frameList;
	public boolean isPlaying;
	public int currentFrameTick;
	public int currentFrame;
}
