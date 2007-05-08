package org.eventb.internal.pp.core.provers.equality;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.Map.Entry;

import org.eventb.internal.pp.core.ClauseSimplifier;
import org.eventb.internal.pp.core.Dumper;
import org.eventb.internal.pp.core.IDispatcher;
import org.eventb.internal.pp.core.IProver;
import org.eventb.internal.pp.core.IVariableContext;
import org.eventb.internal.pp.core.Level;
import org.eventb.internal.pp.core.datastructure.DefaultChangeListener;
import org.eventb.internal.pp.core.datastructure.IObservable;
import org.eventb.internal.pp.core.elements.IClause;
import org.eventb.internal.pp.core.elements.IEquality;
import org.eventb.internal.pp.core.inferrers.EqualityInferrer;
import org.eventb.internal.pp.core.tracing.ClauseOrigin;
import org.eventb.internal.pp.core.tracing.IOrigin;


public class EqualityProver extends DefaultChangeListener implements IProver {

	private IEquivalenceManager manager = new EquivalenceManager();
	private IDispatcher dispatcher;
//	private ClauseSimplifier simplifier;
	
	private Set<IClause> generatedClauses;
	
	private EqualityInferrer inferrer;
	
	public EqualityProver(IVariableContext context) {
		this.generatedClauses = new HashSet<IClause>();
		this.inferrer = new EqualityInferrer(context);
	}
	
	public void contradiction(Level oldLevel, Level newLevel, boolean proofFound, Stack<Level> dependencies) {
		manager.backtrack(newLevel);
		
		// TODO check if necessary
		for (Iterator<IClause> iter = generatedClauses.iterator(); iter.hasNext();) {
			IClause clause = iter.next();
			if (newLevel.isAncestorOf(clause.getLevel())) iter.remove();
		}
	}

	public void initialize(IDispatcher dispatcher, IObservable clauses, ClauseSimplifier simplifier) {
		this.dispatcher = dispatcher;
//		this.simplifier = simplifier;
		clauses.addChangeListener(this);
	}

	public IClause next() {
		return null;
	}

	public void registerDumper(Dumper dumper) {
		// no dumper yet
	}

	@Override
	public void newClause(IClause clause) {
		if (dispatcher != null) addClause(clause);
	}
	
	@Override
	public void removeClause(IClause clause) {
		if (clause.isUnit()) return;
		
		for (IEquality equality : clause.getEqualityLiterals()) {
			manager.removeQueryEquality(equality, clause);
		}
		for (IEquality equality : clause.getConditions()) {
			manager.removeQueryEquality(equality, clause);
		}
	}
	
	private void addClause(IClause clause) {
		if (clause.isUnit() && (clause.getEqualityLiterals().size()>0 || clause.getConditions().size()>0)) {
			IEquality equality = null;
			if (clause.getConditions().size()==1) equality = clause.getConditions().get(0);
			else equality = clause.getEqualityLiterals().get(0);
			
			IFactResult result = manager.addFactEquality(equality, clause);
			handleFactResult(result);
		}
		else if (clause.getEqualityLiterals().size()>0 || clause.getConditions().size()>0) {
			ArrayList<IQueryResult> queryResults = new ArrayList<IQueryResult>();
			for (IEquality equality : clause.getEqualityLiterals()) {
				IQueryResult result = manager.addQueryEquality(equality, clause);
				if (result != null) queryResults.add(result);
			}
			for (IEquality equality : clause.getConditions()) {
				IQueryResult result = manager.addQueryEquality(equality, clause);
				if (result != null) queryResults.add(result);
			}
			handleQueryResult(queryResults);
		}
	}

	private void handleFactResult(IFactResult result) {
		if (result == null) return;
		if (!result.hasContradiction()) {
			handleQueryResult(result.getSolvedQueries());
		}
		else {
			List<IClause> contradictionOrigin = result.getContradictionOrigin();
			IOrigin origin = new ClauseOrigin(contradictionOrigin);
			dispatcher.contradiction(origin);
		}
	}
	
	private <T> void addToList(Map<IClause, List<T>> values, IClause clause, T equality) {
		if (!values.containsKey(clause)) {
			List<T> equalities = new ArrayList<T>();
			values.put(clause, equalities);
		}
		values.get(clause).add(equality);
	}
	
	// takes a query result
	private void handleQueryResult(List<? extends IQueryResult> result) {
		if (result == null) return;
		Map<IClause, List<IEquality>> trueValues = new HashMap<IClause, List<IEquality>>();
		Map<IClause, List<IEquality>> falseValues = new HashMap<IClause, List<IEquality>>();
		Map<IClause, List<IClause>> clauses = new HashMap<IClause, List<IClause>>();
		
		// TODO take into account the level of the clause
		for (IQueryResult queryResult : result) {
			Map<IClause, List<IEquality>> map = queryResult.getValue()?trueValues:falseValues;
			for (IClause clause : queryResult.getSolvedClauses()) {
				for (IClause originClause : queryResult.getSolvedValueOrigin()) {
					addToList(clauses, clause, originClause);
				}
				addToList(map, clause, queryResult.getEquality());
			}
		}
		
		for (Entry<IClause, List<IClause>> entry : clauses.entrySet()) {
			if (trueValues.containsKey(entry.getKey())) {
				for (IEquality equality : trueValues.get(entry.getKey())) {
					inferrer.addEquality(equality, true);
				}
			}
			if (falseValues.containsKey(entry.getKey())) {
				for (IEquality equality : falseValues.get(entry.getKey())) {
					inferrer.addEquality(equality, false);
				}
			}
			inferrer.addParentClauses(entry.getValue());
			entry.getKey().infer(inferrer);
			IClause inferredClause = inferrer.getResult();
//			inferredClause = simplifier.run(inferredClause);
			if (inferredClause.isFalse()) {
				dispatcher.contradiction(inferredClause.getOrigin());
				return;
			}
			if (!inferredClause.isTrue()) generatedClauses.add(inferredClause);
			
			// TODO warning, this calls back remove in the equality prover,
			// but the clause has already been removed
			dispatcher.removeClause(entry.getKey());
		}
		
	}

	public Set<IClause> getGeneratedClauses() {
		Set<IClause> result = new HashSet<IClause>(generatedClauses);
		generatedClauses.clear();
		return result;
	}
	
}
