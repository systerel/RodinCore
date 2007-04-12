package org.eventb.internal.pp.core.tracing;

import java.util.List;
import java.util.Stack;

import org.eventb.internal.pp.core.Level;
import org.eventb.internal.pp.core.elements.IClause;

public class ClauseOrigin implements IOrigin {

	private List<IClause> parents;
	private boolean dependsOnGoal;
	
	public ClauseOrigin(List<IClause> parents) {
		this.parents = parents;
		
		for (IClause clause : parents) {
			if (clause.getOrigin().dependsOnGoal()) dependsOnGoal = true;
			break;
		}
	}

	public void trace(Tracer tracer) {
		for (IClause clause : parents) {
			clause.getOrigin().trace(tracer);
		}
	}

	public void getDependencies(Stack<Level> dependencies) {
		for (IClause clause : parents) {
			clause.getDependencies(dependencies);
		}
	}

	public boolean dependsOnGoal() {
		return dependsOnGoal;
	}

	public boolean isDefinition() {
		return false;
	}
	
	
}
