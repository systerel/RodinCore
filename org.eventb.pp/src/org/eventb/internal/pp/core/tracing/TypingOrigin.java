package org.eventb.internal.pp.core.tracing;

import java.util.Stack;

import org.eventb.internal.pp.core.Level;

public class TypingOrigin implements IOrigin {
	
	public boolean dependsOnGoal() {
		return false;
	}

	public void getDependencies(Stack<Level> dependencies) {
		if (!dependencies.contains(getLevel()))
			dependencies.push(getLevel());
	}

	public Level getLevel() {
		return Level.base;
	}

	public boolean isDefinition() {
		return false;
	}

	public void trace(Tracer tracer) {
		// do nothing
	}

}
