
package com.engine.sevenge.sample;

import com.engine.sevenge.GameActivity;
import com.engine.sevenge.GameState;

public class SampleGame extends GameActivity {

	@Override
	public GameState getStartStage () {
		return new SampleGameState(this);
	}

}
