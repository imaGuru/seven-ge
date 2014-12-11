
package com.sevenge.ecs;

import com.sevenge.SevenGE;

public class AnimationSystem extends System {
	private int i;

	public AnimationSystem (int size) {
		super(SpriteComponent.MASK | AnimationComponent.MASK, size);
	}

	@Override
	public void process () {
		for (i = 0; i < entities.getCount(); i++) {
			Entity entity = entities.get(i);

			AnimationComponent ca = (AnimationComponent)entity.components[3];
			if (ca.isPlaying) {
				SpriteComponent cs = (SpriteComponent)entity.components[1];
				ca.currentFrameTick++;
				if (ca.currentFrameTick * SevenGE.FRAME_TIME > ca.durations[ca.currentFrame]) {
					ca.currentFrame = (ca.currentFrame + 1) % ca.durations.length;
					ca.currentFrameTick = 1;
					cs.textureRegion = ca.frameList[ca.currentFrame];
				}
			}

		}

	}

	@Override
	public void handleMessage (Message m, Entity e) {
		// TODO Auto-generated method stub

	}
}
