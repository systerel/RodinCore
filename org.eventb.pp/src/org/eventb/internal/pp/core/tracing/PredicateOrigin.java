package org.eventb.internal.pp.core.tracing;

import java.util.Stack;

import org.eventb.core.ast.Predicate;
import org.eventb.internal.pp.core.Level;

public class PredicateOrigin implements IOrigin {

	private Predicate predicate;
	private boolean isGoal;
	
	public PredicateOrigin(Predicate predicate, boolean isGoal) {
		this.predicate = predicate;
		this.isGoal = isGoal;
	}

	public void getDependencies(Stack<Level> dependencies) {
		return;
	}

	public void trace(Tracer tracer) {
		if (isGoal) tracer.setGoalNeeded(isGoal);
		else tracer.addNeededHypothesis(predicate);
	}

	public boolean dependsOnGoal() {
		return isGoal;
	}

	public boolean isDefinition() {
		return false;
	}
	
}
