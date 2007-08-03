package org.eventb.internal.pp.core.tracing;

import java.util.Set;

import org.eventb.internal.pp.core.Level;

public class TypingOrigin implements IOrigin {
	
	public boolean dependsOnGoal() {
		return false;
	}

	public void getDependencies(Set<Level> dependencies) {
		if (!dependencies.contains(getLevel()))
			dependencies.add(getLevel());
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

	public int getDepth() {
		return 0;
	}

}
