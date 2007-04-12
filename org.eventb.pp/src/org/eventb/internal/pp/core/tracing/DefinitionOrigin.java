package org.eventb.internal.pp.core.tracing;

import java.util.Stack;

import org.eventb.internal.pp.core.Level;

public class DefinitionOrigin implements IOrigin {

	public void getDependencies(Stack<Level> dependencies) {
		// do nothing
	}

	public void trace(Tracer tracer) {
		// do nothing
	}

	public boolean dependsOnGoal() {
		return false;
	}

	public boolean isDefinition() {
		return true;
	}

}
