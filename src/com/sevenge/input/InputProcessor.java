
package com.sevenge.input;
/** Interface which provides handlers for touch events **/
public interface InputProcessor {

	 
	/** Responds to a single touch event **/
	public boolean touchDown (int screenX, int screenY, int pointer, int button);
	
	/** Responds to a finger leaving the screen **/
	public boolean touchUp (int screenX, int screenY, int pointer, int button);

	/** Responds to a finger moving on the screen **/
	public boolean touchMove (int screenX, int screenY, int pointer);

}
