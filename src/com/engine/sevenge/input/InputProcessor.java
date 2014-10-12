
package com.engine.sevenge.input;

public interface InputProcessor {

	public boolean touchDown (int screenX, int screenY, int pointer, int button);

	public boolean touchUp (int screenX, int screenY, int pointer, int button);

	public boolean touchMove (int screenX, int screenY, int pointer);

}
