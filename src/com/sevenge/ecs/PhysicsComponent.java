
package com.sevenge.ecs;

import com.badlogic.gdx.physics.box2d.Body;

public class PhysicsComponent extends Component {

	public static final int MASK = 1 << 4; // 0x10

	private Body body;

	public Body getBody () {
		return body;
	}

	public void setBody (Body body) {
		this.body = body;
	}

}
