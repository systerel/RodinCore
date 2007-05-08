package org.eventb.internal.pp.core.tracing;

import java.util.Stack;

import org.eventb.internal.pp.core.Level;

public interface IOrigin {

	public void trace(Tracer tracer);
	
	public void getDependencies(Stack<Level> dependencies);
	
	public boolean dependsOnGoal();
	
	public boolean isDefinition();
	
	public Level getLevel();
	
}
