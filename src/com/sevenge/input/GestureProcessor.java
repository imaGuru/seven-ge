
package com.sevenge.input;
/** interface which provides handlers for gesture events **/
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;

public interface GestureProcessor {

	/**
	 * 
	 * Notified when a double-tap occurs.
	 *
	 * @param The down motion event of the first tap of the double-tap. 
	 *  
	 */
	public boolean onDoubleTap (MotionEvent arg0);

	
	/**
	 * 
	 * Unlike onSingleTapUp(MotionEvent), this will only be called after the detector is confident that the user's first tap is not followed by a second tap leading to a double-tap gesture.
	 *  
	 * @param e The down motion event of the single-tap.
	 * @return true if the event is consumed, else false 
	 */
	public boolean onSingleTapConfirmed (MotionEvent arg0);

	/**
	 * 
	 * Notified of a fling event when it occurs with the initial on down MotionEvent and the matching up MotionEvent. The calculated velocity is supplied along the x and y axis in pixels per second.
	 *	
	 * @param e1  The first down motion event that started the fling. 
	 * @param e2  The move motion event that triggered the current onFling. 
	 * @param velocityX  The velocity of this fling measured in pixels per second along the x axis. 
	 * @param velocityY  The velocity of this fling measured in pixels per second along the y axis. 
	 *
	 */
	public boolean onFling (MotionEvent arg0, MotionEvent arg1, float arg2, float arg3);

	/**
	 * Notified when a long press occurs with the initial on down MotionEvent that trigged it.
	 *
	 * @param  The initial on down motion event that started the longpress.  
	 * 
	 */
	public void onLongPress (MotionEvent arg0);
	
	/**
	 * 	Notified when a scroll occurs with the initial on down MotionEvent and the current move MotionEvent. The distance in x and y is also supplied for convenience.
	 *
	 *	Parameters
	 *	@param e1  The first down motion event that started the scrolling. 
	 *	@param e2  The move motion event that triggered the current onScroll. 
	 *	@param distanceX  The distance along the X axis that has been scrolled since the last call to onScroll. This is NOT the distance between e1 and e2. 
	 *	@param distanceY  The distance along the Y axis that has been scrolled since the last call to onScroll. This is NOT the distance between e1 and e2. 
	 *	
	 */
	public boolean onScroll (MotionEvent e1, MotionEvent e2, float distanceX , float distanceY);

	/** 
	 * Responds to scaling events for a gesture in progress. Reported by pointer motion.
	 */
	public boolean onScale (float currentSpan);
	/**
	 * Responds to the end of a scale gesture. Reported by existing pointers going up.
	 * Once a scale has ended, getFocusX() and getFocusY() will return focal point of the pointers remaining on the screen.
	 */
	public void onScaleEnd (float currentSpan);
	/**
	 * Responds to the beginning of a scaling gesture. Reported by new pointers going down.
	 */
	public void onScaleBegin (float currentSpan);

}
