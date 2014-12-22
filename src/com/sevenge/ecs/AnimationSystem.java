
package com.sevenge.ecs;

import com.sevenge.SevenGE;

public class AnimationSystem extends SubSystem {
	private int i;

	public AnimationSystem (int size) {
		super(SpriteComponent.MASK | AnimationComponent.MASK, size);
	}

	@Override
	public void process () {
		for (i = 0; i < mEntities.getCount(); i++) {
			Entity entity = mEntities.get(i);

			AnimationComponent ca = (AnimationComponent)entity.mComponents[3];
			if (ca.isPlaying) {
				SpriteComponent cs = (SpriteComponent)entity.mComponents[1];
				ca.currentFrameTick++;
				if (ca.currentFrameTick * SevenGE.FRAME_TIME > ca.durations[ca.currentFrame]) {
					ca.currentFrame = (ca.currentFrame + 1) % ca.durations.length;
					ca.currentFrameTick = 1;
					cs.textureRegion = ca.frameList[ca.currentFrame];
				}
			}

		}

	}
}
