
package com.sevenge.sample;

import com.sevenge.GameActivity;
import com.sevenge.GameState;

public class SampleGame extends GameActivity {

	@Override
	public GameState getStartStage () {
		return new SampleGameState(this);
	}

}
