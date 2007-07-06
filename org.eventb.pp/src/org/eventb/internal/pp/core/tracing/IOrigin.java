package org.eventb.internal.pp.core.tracing;

import java.util.Set;

import org.eventb.internal.pp.core.Level;

public interface IOrigin {

	public void trace(Tracer tracer);
	
	// helper
	public void getDependencies(Set<Level> dependencies);
	
	public boolean dependsOnGoal();
	
	public boolean isDefinition();
	
	public Level getLevel();
	
}
