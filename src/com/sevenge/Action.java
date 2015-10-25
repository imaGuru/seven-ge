package com.sevenge;

/** Represents an action which can be scheduled for execution **/
public abstract class Action {

	protected boolean finished = false;

	/** Executes the Action **/
	abstract boolean execute();

}
