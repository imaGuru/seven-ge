package com.engine.sevenge.ecs;

import java.util.List;

import com.engine.sevenge.GameActivity;

public class SAnimation extends System
{

	@Override
	public void process(List<Entity> entities)
	{
		for (Entity entity : entities)
		{
			if ((6 & entity.mask) == 6)
			{
				CAnimation ca = (CAnimation) entity.components.get(4);

				if (ca.isPlaying)
				{
					CSprite cs = (CSprite) entity.components.get(2);
					ca.currentFrameTick++;
					if (ca.currentFrameTick * GameActivity.FRAME_TIME > ca.durations[ca.currentFrame])
					{

						ca.currentFrame = (ca.currentFrame + 1)
								% ca.durations.length;

						ca.currentFrameTick = 1;
						cs.subTexture = ca.frameList[ca.currentFrame];
					}
				}
			}
		}

	}
}
