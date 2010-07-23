/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eventb.internal.pp.core.tracing;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.eventb.internal.pp.core.Level;
import org.eventb.internal.pp.core.Tracer;
import org.eventb.internal.pp.core.elements.Clause;

/**
 * Abstract implementation of {@link IOrigin} for clauses that have been
 * inferred by the prover.
 * <p>
 * All inferred clauses share the characteristic that they have parent clauses
 * from which they were derived. This class provides the necessary mechanisms
 * for handling parent clauses.
 *
 * @author François Terrier
 *
 */
public abstract class AbstractInferrenceOrigin implements IOrigin {

	protected List<Clause> parents;
	protected boolean dependsOnGoal;
	protected int depth;
	
	public AbstractInferrenceOrigin(List<Clause> parents) {
		this.parents = parents;
		init();
	}
	
	protected AbstractInferrenceOrigin(Clause clause) {
		this.parents = new ArrayList<Clause>();
		parents.add(clause);
		init();
	}
	
	private void init() {
		for (Clause clause : parents) {
			if (clause.getOrigin().getDepth() > depth) depth = clause.getOrigin().getDepth();
			if (clause.getOrigin().dependsOnGoal()) dependsOnGoal = true;
		}
		this.depth++;
	}

	public List<Clause> getClauses() {
		return parents;
	}

	@Override
	public void trace(Tracer tracer) {
		for (Clause clause : parents) {
			clause.getOrigin().trace(tracer);
		}
	}

	@Override
	public void addDependenciesTo(Set<Level> dependencies) {
		dependencies.add(getLevel());
		for (Clause clause : parents) {
			clause.getOrigin().addDependenciesTo(dependencies);
		}
	}

	@Override
	public boolean dependsOnGoal() {
		return dependsOnGoal;
	}

	@Override
	public boolean isDefinition() {
		return false;
	}
	
	@Override
	public String toString() {
		return getLevel().toString();
	}
	
	@Override
	public int getDepth() {
		return depth;
	}

}