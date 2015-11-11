package com.sevenge;

/** Action class which can be schedule for execution **/
public abstract class Action {

	protected boolean finished = false;

	abstract boolean execute();

}
