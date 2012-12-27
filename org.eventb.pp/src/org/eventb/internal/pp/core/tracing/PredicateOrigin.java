/*******************************************************************************
 * Copyright (c) 2006, 2012 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.pp.core.tracing;

import java.util.Set;

import org.eventb.core.ast.Predicate;
import org.eventb.internal.pp.core.Level;
import org.eventb.internal.pp.core.Tracer;

/**
 * Implementation of {@link IOrigin} for original clauses.
 * <p>
 * Original clauses are clauses that are not definition clauses but
 * are the clauses that have been created by the loader.  
 *
 * @author François Terrier
 *
 */
public class PredicateOrigin implements IOrigin {

	private Predicate predicate;
	private boolean isGoal;
	
	public PredicateOrigin(Predicate predicate, boolean isGoal) {
		this.predicate = predicate;
		this.isGoal = isGoal;
	}

	@Override
	public void addDependenciesTo(Set<Level> dependencies) {
		if (!dependencies.contains(getLevel()))
			dependencies.add(getLevel());
	}

	@Override
	public void trace(Tracer tracer) {
		if (isGoal) tracer.setGoalNeeded(isGoal);
		else tracer.addNeededHypothesis(predicate);
	}

	@Override
	public boolean dependsOnGoal() {
		return isGoal;
	}

	@Override
	public boolean isDefinition() {
		return false;
	}

	@Override
	public Level getLevel() {
		return Level.BASE;
	}

	@Override
	public String toString() {
		return getLevel().toString();
	}

	@Override
	public int getDepth() {
		return 0;
	}

}
