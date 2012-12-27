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
package org.eventb.internal.pp.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.eventb.core.ast.Predicate;
import org.eventb.internal.pp.core.tracing.IOrigin;
import org.eventb.pp.ITracer;

/**
 * The tracer is responsible for keeping track of what formulas of a sequent
 * are needed to get to a proof. On top of the functionality of {@link ITracer},
 * this class takes care of the proof tree.
 * <p>
 * Whenever a contradiction is detected, this class is responsible for computing
 * the level on which the prover needs to continue. The last closed level is always
 * accessible using method {@link #getLastClosedLevel()}.
 *
 * @author François Terrier
 */
public final class Tracer implements ITracer, ILevelController {

	private final Map<Level, Pair> origins = new HashMap<Level, Pair>(); 
	private Level lastClosedLevel = null;
	private Level currentLevel = Level.BASE;
	
	private HashSet<Predicate> originalPredicates;
	private boolean goalNeeded = false;
	
	/* (non-Javadoc)
	 * @see org.eventb.internal.pp.core.ILevelController#getCurrentLevel()
	 */
	@Override
	public Level getCurrentLevel() {
		return currentLevel;
	}
	
	/* (non-Javadoc)
	 * @see org.eventb.internal.pp.core.ILevelController#nextLevel()
	 */
	@Override
	public void nextLevel() {
		if (lastClosedLevel!=null && currentLevel.getLeftBranch().equals(lastClosedLevel)) {
			currentLevel = currentLevel.getRightBranch();
		}
		else {
			currentLevel = currentLevel.getLeftBranch();
		}
	}
	
	/**
	 * Returns the last closed level, or <code>null</code> if
	 * no level has been closed yet.
	 * <p>
	 * If the last closed level is equal to {@link Level#BASE}, the base
	 * level is closed and the proof is done.
	 * 
	 * @return the last closed level or <code>null</code>
	 */
	public Level getLastClosedLevel() {
		return lastClosedLevel;
	}
	
	/**
	 * Adds the origin as a closing clause of its level.
	 * <p>
	 * This method has several effects. It records the origin as a closing origin
	 * for its level, and it computes the level that has been closed. After this method returns,
	 * the level on which the prover needs to continue can be retrieved using
	 * getLastClosedLevel().getParent().
	 * 
	 * @param origin the origin that closes a branch
	 * @throws IllegalStateException in case there is an inconsistency in the proof tree
	 */
	public void addClosingClauseAndUpdateLevel(IOrigin origin) throws IllegalStateException {
		if (lastClosedLevel!=null && lastClosedLevel.isAncestorInSameTree(origin.getLevel())) {
			throw new IllegalStateException();
		}
		
		Set<Level> dependencies = new HashSet<Level>();
		origin.addDependenciesTo(dependencies);
		
		for (Iterator<Entry<Level, Pair>> iter = origins.entrySet().iterator(); iter.hasNext();) {
			Entry<Level, Pair> entry = iter.next();
			if (origin.getLevel().isAncestorInSameTree(entry.getValue().origin.getLevel())) iter.remove();
			if (entry.getKey().isAncestorInSameTree(origin.getLevel())) {
				throw new IllegalStateException();
			}
		}
		
		Level tmp = origin.getLevel();
		if (!tmp.equals(Level.BASE) && tmp.isRightBranch()) assert origins.containsKey(tmp.getParent().getLeftBranch());
		
		Set<Level> cumulatedDependencies = new HashSet<Level>(dependencies);
		while (!tmp.equals(Level.BASE)) {
			Level current = tmp;
			Level parent = tmp.getParent();
			if (current.isRightBranch()) {
				Pair leftBranchOrigin = origins.get(parent.getLeftBranch());
				if (!leftBranchOrigin.dependencies.contains(parent.getLeftBranch())) {
					throw new IllegalStateException();
				}
				if (cumulatedDependencies.contains(parent.getRightBranch())) {
					cumulatedDependencies.addAll(leftBranchOrigin.dependencies);
				}
				else origins.remove(parent.getLeftBranch());
			}
			else if (current.isLeftBranch()) {
				assert !origins.containsKey(current);
				if (cumulatedDependencies.contains(current)) {
					tmp = current;
					break;
				}
			}
			tmp = parent;
		}
		assert !origins.containsKey(tmp);
		origins.put(tmp, new Pair(origin,cumulatedDependencies));
		assert !tmp.equals(Level.BASE)?!tmp.isRightBranch():true;
		setLastClosedLevel(tmp);
	}
	
	private void setLastClosedLevel(Level level) {
		lastClosedLevel = level;
		currentLevel = lastClosedLevel.getParent();
	}
	
	/* (non-Javadoc)
	 * @see org.eventb.internal.pp.core.tracing.ITracer#getClauses()
	 */
	public Set<IOrigin> getClosingOrigins() {
		Set<IOrigin> result = new HashSet<IOrigin>();
		for (Pair pair : origins.values()) {
			result.add(pair.origin);
		}
		return result;
	}
	
	private static final class Pair {
		IOrigin origin;
		Set<Level> dependencies;
		Pair(IOrigin origin, Set<Level> dependencies) {
			this.origin = origin;
			this.dependencies = dependencies;
		}
	}
	
	/* (non-Javadoc)
	 * @see org.eventb.internal.pp.core.tracing.ITracer#getOriginalPredicates()
	 */
	@Override
	public List<Predicate> getNeededHypotheses() {
		if (originalPredicates==null) calculateOriginalPredicates();
		return new ArrayList<Predicate>(originalPredicates);
	}
	
	private void calculateOriginalPredicates() {
		originalPredicates = new HashSet<Predicate>();
		for (Pair pair : origins.values()) {
			pair.origin.trace(this);	
		}
	}
	
	/**
	 * Adds this predicate as a needed hypothesis.
	 * 
	 * @param predicate the predicate to add
	 */
	public void addNeededHypothesis(Predicate predicate) {
		originalPredicates.add(predicate);
	}

	/* (non-Javadoc)
	 * @see org.eventb.pp.ITracer#isGoalNeeded()
	 */
	@Override
	public boolean isGoalNeeded() {
		if (originalPredicates == null) calculateOriginalPredicates();
		return goalNeeded;
	}
	
	/**
	 * Sets the specified value as the value for whether the
	 * goal is needed for the proof or not.
	 * 
	 * @param goalNeeded the new value
	 */
	public void setGoalNeeded(boolean goalNeeded) {
		this.goalNeeded = goalNeeded;
	}
}
