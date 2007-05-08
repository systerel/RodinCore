package org.eventb.internal.pp.core.tracing;

import java.util.List;
import java.util.Stack;

import org.eventb.internal.pp.core.Level;
import org.eventb.internal.pp.core.elements.IClause;

public abstract class AbstractInferrenceOrigin implements IOrigin {

	protected List<IClause> parents;
	protected boolean dependsOnGoal;

	public AbstractInferrenceOrigin(List<IClause> parents) {
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
		if (!dependencies.contains(getLevel()))
			dependencies.push(getLevel());
		for (IClause clause : parents) {
			clause.getOrigin().getDependencies(dependencies);
		}
	}

	public boolean dependsOnGoal() {
		return dependsOnGoal;
	}

	public boolean isDefinition() {
		return false;
	}
	
	@Override
	public String toString() {
		return getLevel().toString();
	}

}