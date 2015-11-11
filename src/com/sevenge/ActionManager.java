package com.sevenge;

import java.util.ArrayList;

/** ActionManager class which manages Action instances scheduled for execution **/
public class ActionManager {

	private ArrayList<Action> actions = new ArrayList<Action>(10);

	public void add(Action action) {
		actions.add(action);
	}

	/** executes all registered Action instances **/
	public void process() {

		ArrayList<Action> finished = new ArrayList<Action>();
		for (Action action : actions) {
			if (action.execute()) {
				finished.add(action);
			}
		}
		actions.retainAll(finished);
	}
}
