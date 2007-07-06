package org.eventb.internal.pp.core.tracing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.eventb.core.ast.Predicate;
import org.eventb.internal.pp.core.Level;
import org.eventb.pp.ITracer;

public class Tracer implements ITracer {

	private static class Pair {
		IOrigin origin;
		Set<Level> dependencies;
		Pair(IOrigin origin, Set<Level> dependencies) {
			this.origin = origin;
			this.dependencies = dependencies;
		}
	}
	
	private Map<Level, Pair> origins = new HashMap<Level, Pair>(); 
	private Level level = null;
	
	public Level getLastClosedLevel() {
		return level;
	}
	
	public void addClosingClause(IOrigin origin) throws IllegalStateException {
		if (level!=null && level.isAncestorInSameTree(origin.getLevel())) {
			throw new IllegalStateException();
		}
		
		Set<Level> dependencies = new HashSet<Level>();
		origin.getDependencies(dependencies);
		
		for (Iterator<Entry<Level, Pair>> iter = origins.entrySet().iterator(); iter.hasNext();) {
			Entry<Level, Pair> entry = iter.next();
			if (origin.getLevel().isAncestorInSameTree(entry.getValue().origin.getLevel())) iter.remove();
			if (entry.getKey().isAncestorInSameTree(origin.getLevel())) {
				throw new IllegalStateException();
			}
		}
		
		Level tmp = origin.getLevel();
		if (!tmp.equals(Level.base) && tmp.isRightBranch()) assert origins.containsKey(tmp.getParent().getLeftBranch());
		
		Set<Level> cumulatedDependencies = new HashSet<Level>(dependencies);
		while (!tmp.equals(Level.base)) {
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
		assert !tmp.equals(Level.base)?!tmp.isRightBranch():true;
		level = tmp;
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
	
	/* (non-Javadoc)
	 * @see org.eventb.internal.pp.core.tracing.ITracer#getOriginalPredicates()
	 */
	public List<Predicate> getOriginalPredicates() {
		if (originalPredicates==null) calculateOriginalPredicates();
		return new ArrayList<Predicate>(originalPredicates);
	}
	
	private void calculateOriginalPredicates() {
		originalPredicates = new HashSet<Predicate>();
		for (Pair pair : origins.values()) {
			pair.origin.trace(this);	
		}
	}
	
	private HashSet<Predicate> originalPredicates;
	protected void addNeededHypothesis(Predicate predicate) {
		originalPredicates.add(predicate);
	}

	private boolean goalNeeded = false;
	public boolean isGoalNeeded() {
		if (originalPredicates == null) calculateOriginalPredicates();
		return goalNeeded;
	}
	
	protected void setGoalNeeded(boolean goalNeeded) {
		this.goalNeeded = goalNeeded;
	}
}
