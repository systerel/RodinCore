package org.eventb.internal.pp.core.tracing;

import java.util.List;
import java.util.Set;

import org.eventb.internal.pp.core.Level;
import org.eventb.internal.pp.core.elements.Clause;

public abstract class AbstractInferrenceOrigin implements IOrigin {

	protected List<Clause> parents;
	protected boolean dependsOnGoal;

	public AbstractInferrenceOrigin(List<Clause> parents) {
		this.parents = parents;
		
		for (Clause clause : parents) {
			if (clause.getOrigin().dependsOnGoal()) dependsOnGoal = true;
			break;
		}
	}
	
	public List<Clause> getClauses() {
		return parents;
	}

	public void trace(Tracer tracer) {
		for (Clause clause : parents) {
			clause.getOrigin().trace(tracer);
		}
	}

	public void getDependencies(Set<Level> dependencies) {
		if (!dependencies.contains(getLevel()))
			dependencies.add(getLevel());
		for (Clause clause : parents) {
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