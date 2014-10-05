
package com.engine.sevenge.ecs;

import java.util.List;

public abstract class System {
	public abstract void process (List<Entity> entities);

	public abstract void handleMessage (Message m, Entity e);
}
