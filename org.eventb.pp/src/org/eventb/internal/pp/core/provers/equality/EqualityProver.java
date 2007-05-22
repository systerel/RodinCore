package org.eventb.internal.pp.core.provers.equality;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
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
import org.eventb.internal.pp.core.elements.terms.Constant;
import org.eventb.internal.pp.core.elements.terms.Variable;
import org.eventb.internal.pp.core.inferrers.EqualityInferrer;
import org.eventb.internal.pp.core.inferrers.EqualityInstantiationInferrer;
import org.eventb.internal.pp.core.search.IterableHashSet;
import org.eventb.internal.pp.core.search.ResetIterator;
import org.eventb.internal.pp.core.tracing.ClauseOrigin;
import org.eventb.internal.pp.core.tracing.IOrigin;


public class EqualityProver extends DefaultChangeListener implements IProver {

	private IEquivalenceManager manager = new EquivalenceManager();
	private IDispatcher dispatcher;
	private ClauseSimplifier simplifier;
	
	private IterableHashSet<IClause> generatedClauses;
	private ResetIterator<IClause> backtrackIterator;
	private ResetIterator<IClause> dispatcherIterator;
	
	private Set<IClause> subsumedClauses; 
	
	private EqualityInferrer inferrer;
	private EqualityInstantiationInferrer instantiationInferrer;
	
	public EqualityProver(IVariableContext context) {
		this.generatedClauses = new IterableHashSet<IClause>();
		this.inferrer = new EqualityInferrer(context);
		this.instantiationInferrer = new EqualityInstantiationInferrer(context);
		
		backtrackIterator = generatedClauses.iterator();
		dispatcherIterator = generatedClauses.iterator();
		
		subsumedClauses = new HashSet<IClause>();
	}
	
	public void contradiction(Level oldLevel, Level newLevel, Stack<Level> dependencies) {
		manager.backtrack(newLevel);
		
		// TODO check if necessary
		backtrackIterator.reset();
		while (backtrackIterator.hasNext()) {
			IClause clause = backtrackIterator.next();
			if (newLevel.isAncestorOf(clause.getLevel())) generatedClauses.remove(clause);
		}
	}

	public void initialize(IDispatcher dispatcher, IObservable clauses, ClauseSimplifier simplifier) {
		this.dispatcher = dispatcher;
		this.simplifier = simplifier;
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
			if (equality.isConstant()) manager.removeQueryEquality(equality, clause);
			else if (isInstantiationCandidate(equality)) manager.removeInstantiation(equality, clause);
		}
		for (IEquality equality : clause.getConditions()) {
			if (equality.isConstant()) manager.removeQueryEquality(equality, clause);
			else if (isInstantiationCandidate(equality)) manager.removeInstantiation(equality, clause);
		}
	}
	
	private boolean isInstantiationCandidate(IEquality equality) {
		if ((equality.getTerms().get(0) instanceof Variable && equality.getTerms().get(1) instanceof Constant)
				||	(equality.getTerms().get(1) instanceof Variable && equality.getTerms().get(0) instanceof Constant))
			return true;
		return false;
	}
	
	private void addClause(IClause clause) {
		if (clause.isUnit() && (clause.getEqualityLiterals().size()>0 || clause.getConditions().size()>0)) {
			IEquality equality = null;
			if (clause.getConditions().size()==1) equality = clause.getConditions().get(0);
			else equality = clause.getEqualityLiterals().get(0);
			
			if (!equality.isConstant()) {
				// TODO handle this case, x = a or x = y
				return;
			}
			
			IFactResult result = manager.addFactEquality(equality, clause);
			handleFactResult(result);
		}
		else if (clause.getEqualityLiterals().size()>0 || clause.getConditions().size()>0) {
			ArrayList<IQueryResult> queryResult = new ArrayList<IQueryResult>();
			ArrayList<IInstantiationResult> instantiationResult = new ArrayList<IInstantiationResult>();

			// if equivalence, then we do the standard instantiations
			// x=a -> x/a, x\=a -> x/a
			if (clause.isEquivalence()) doTrivialInstantiations(clause);
			
			handleEqualityList(clause.getEqualityLiterals(), clause,
					queryResult, instantiationResult, !clause.isEquivalence());
			handleEqualityList(clause.getConditions(), clause,
					queryResult, instantiationResult, true);
			handleQueryResult(queryResult);
			handleInstantiationResult(instantiationResult);
		}
	}
	
	private void handleEqualityList(List<IEquality> equalityList, IClause clause,
			List<IQueryResult> queryResult, List<IInstantiationResult> instantiationResult,
			boolean handleOnlyPositives) {
		for (IEquality equality : equalityList) {
			if (equality.isConstant()) {
				IQueryResult result = manager.addQueryEquality(equality, clause);
				if (result != null) queryResult.add(result);
			}
			else if (handleOnlyPositives?equality.isPositive():true) {
				if (isInstantiationCandidate(equality)) {
					List<? extends IInstantiationResult> result = manager.addInstantiationEquality(equality, clause);
					if (result != null) instantiationResult.addAll(result);
					
				}
			}
			// TODO handle other cases x = a or x = y or #x.x=y etc ...
		}
	}
	
	private void doTrivialInstantiations(IClause clause) {
		for (IEquality equality : clause.getEqualityLiterals()) {
			if (isInstantiationCandidate(equality)) {
				Constant constant = null;
				if (equality.getTerms().get(0) instanceof Constant) constant = (Constant)equality.getTerms().get(0);
				else if (equality.getTerms().get(1) instanceof Constant) constant = (Constant)equality.getTerms().get(1);
				instantiationInferrer.addEqualityEqual(equality, constant);
				
				clause.infer(instantiationInferrer);
				IClause inferredClause = instantiationInferrer.getResult();
				
				inferredClause = simplifier.run(inferredClause);
				if (inferredClause.isFalse()) {
					dispatcher.contradiction(inferredClause.getOrigin());
					return;
				}
				if (!inferredClause.isTrue()) generatedClauses.appends(inferredClause);
			}
		}
	}

	private void handleFactResult(IFactResult result) {
		if (result == null) return;
		if (!result.hasContradiction()) {
			if (result.getSolvedQueries() != null) handleQueryResult(result.getSolvedQueries());
			if (result.getSolvedInstantiations() != null) handleInstantiationResult(result.getSolvedInstantiations());
		}
		else {
			List<IClause> contradictionOrigin = result.getContradictionOrigin();
			IOrigin origin = new ClauseOrigin(contradictionOrigin);
			dispatcher.contradiction(origin);
		}
	}
	
	private <T> void addToList(Map<IClause, Set<T>> values, IClause clause, T equality) {
		if (!values.containsKey(clause)) {
			Set<T> equalities = new HashSet<T>();
			values.put(clause, equalities);
		}
		values.get(clause).add(equality);
	}
	
	private void handleInstantiationResult(List<? extends IInstantiationResult> result) {
		if (result == null) return;
		for (IInstantiationResult insRes : result) {
			for (IClause clause : insRes.getSolvedClauses()) {
				instantiationInferrer.addEqualityUnequal(insRes.getEquality(), insRes.getInstantiationValue());
				instantiationInferrer.addParentClauses(new ArrayList<IClause>(insRes.getSolvedValueOrigin()));
				clause.infer(instantiationInferrer);
				IClause inferredClause = instantiationInferrer.getResult();
				
				inferredClause = simplifier.run(inferredClause);
				if (inferredClause.isFalse()) {
					dispatcher.contradiction(inferredClause.getOrigin());
					return;
				}
				if (!inferredClause.isTrue()) generatedClauses.appends(inferredClause);
			}
		}
	}
	
//	private void handleInstantiationResult(List<? extends IInstantiationResult> result) {
//		if (result == null) return;
//		Map<IClause, Map<IEquality, Constant>> values = new HashMap<IClause, Map<IEquality,Constant>>();
//		Map<IClause, Set<IClause>> origins = new HashMap<IClause, Set<IClause>>();
//		
//		for (IInstantiationResult insRes : result) {
//			for (IClause clause : insRes.getSolvedClauses()) {
//				if (!values.containsKey(clause)) {
//					values.put(clause, new HashMap<IEquality, Constant>());
//					origins.put(clause, new HashSet<IClause>());
//				}
//				Map<IEquality, Constant> map = values.get(clause);
//				map.put(insRes.getEquality(), insRes.getInstantiationValue());
//				Set<IClause> origin = origins.get(clause);
//				origin.addAll(insRes.getSolvedClauses());
//			}
//		}
//		
//		for (Entry<IClause, Map<IEquality, Constant>> entry : values.entrySet()) {
//			for (Entry<IEquality, Constant> entry2 : entry.getValue().entrySet()) {
//				instantiationInferrer.addEquality(entry2.getKey(), entry2.getValue());
//			}
//			instantiationInferrer.addParentClauses(new ArrayList<IClause>(origins.get(entry.getKey())));
//			entry.getKey().infer(instantiationInferrer);
//			IClause inferredClause = instantiationInferrer.getResult();
//			
//			inferredClause = simplifier.run(inferredClause);
//			if (inferredClause.isFalse()) {
//				dispatcher.contradiction(inferredClause.getOrigin());
//				return;
//			}
//			if (!inferredClause.isTrue()) generatedClauses.appends(inferredClause);
//		}
//	}
	
	// takes a query result
	private void handleQueryResult(List<? extends IQueryResult> result) {
		if (result == null) return;
		Map<IClause, Set<IEquality>> trueValues = new HashMap<IClause, Set<IEquality>>();
		Map<IClause, Set<IEquality>> falseValues = new HashMap<IClause, Set<IEquality>>();
		Map<IClause, Set<IClause>> clauses = new HashMap<IClause, Set<IClause>>();
		
		// take into account the level of the clause
		// -> done by the prover
		for (IQueryResult queryResult : result) {
			Map<IClause, Set<IEquality>> map = queryResult.getValue()?trueValues:falseValues;
			for (IClause clause : queryResult.getSolvedClauses()) {
				for (IClause originClause : queryResult.getSolvedValueOrigin()) {
					addToList(clauses, clause, originClause);
				}
				addToList(map, clause, queryResult.getEquality());
			}
		}
		
		for (Entry<IClause, Set<IClause>> entry : clauses.entrySet()) {
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
			inferrer.addParentClauses(new ArrayList<IClause>(entry.getValue()));
			entry.getKey().infer(inferrer);
			IClause inferredClause = inferrer.getResult();
//			inferredClause = simplifier.run(inferredClause);
			if (inferredClause.isFalse()) {
				dispatcher.contradiction(inferredClause.getOrigin());
				return;
			}
			if (!inferredClause.isTrue()) generatedClauses.appends(inferredClause);
			
			subsumedClauses.add(entry.getKey());
		}
		
	}

	public ResetIterator<IClause> getGeneratedClauses() {
		return dispatcherIterator;
	}
	
	public void clean() {
		generatedClauses.clear();
	}

	public Set<IClause> getSubsumedClauses() {
		Set<IClause> result = new HashSet<IClause>(subsumedClauses);
		subsumedClauses.clear();
		return result;
	}
}
