package org.eventb.internal.pp.core.provers.seedsearch;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Stack;

import org.eventb.internal.pp.core.ClauseSimplifier;
import org.eventb.internal.pp.core.Dumper;
import org.eventb.internal.pp.core.IDispatcher;
import org.eventb.internal.pp.core.IProver;
import org.eventb.internal.pp.core.IVariableContext;
import org.eventb.internal.pp.core.Level;
import org.eventb.internal.pp.core.datastructure.DefaultChangeListener;
import org.eventb.internal.pp.core.datastructure.IObservable;
import org.eventb.internal.pp.core.elements.IClause;
import org.eventb.internal.pp.core.elements.IPredicate;
import org.eventb.internal.pp.core.elements.PPEqClause;
import org.eventb.internal.pp.core.elements.PPPredicate;
import org.eventb.internal.pp.core.elements.terms.Term;
import org.eventb.internal.pp.core.elements.terms.Variable;
import org.eventb.internal.pp.core.inferrers.InstantiationInferrer;
import org.eventb.internal.pp.core.provers.NonUnitMatcher;
import org.eventb.internal.pp.core.search.IterableHashSet;
import org.eventb.internal.pp.core.search.ResetIterator;


public class SeedSearchProver extends DefaultChangeListener implements IProver {

	/**
	 * Debug flag for <code>PROVER_SEEDSEARCH_TRACE</code>
	 */
	public static boolean DEBUG;
	public static void debug(String message){
		if (DEBUG)
			System.out.println(message);
	}

	private IterableHashSet<IClause> candidates;
	private ResetIterator<IClause> candidatesIterator;
	private IObservable clauses;
	
	private InstantiationInferrer inferrer;
	
	public SeedSearchProver(IVariableContext context) {
		this.inferrer = new InstantiationInferrer(context);
	}
	
	public void contradiction(Level oldLevel, Level newLevel, boolean proofFound, Stack<Level> dependencies) {
		// do nothing
	}

	public void initialize(IDispatcher dispatcher,
			IObservable clauses, ClauseSimplifier simplifier) {
		this.clauses = clauses;
		candidates = new IterableHashSet<IClause>();
		candidatesIterator = candidates.iterator();
		
		clauses.addChangeListener(this);
	}

	private IClause currentClause;
	private List<Term> termList;
	private Term term;
	
	private static final int ALLOWED_SEED_SEARCH = 2;
	
	private int currentSeedSearch = 0;
	
	private void nextClause() {
		// for now we have only unit clauses
		currentClause = candidatesIterator.next();
		IPredicate predicate = currentClause.getPredicateLiterals().get(0);

		int i = 0;
		for (Term term : predicate.getTerms()) {
			if (!term.isConstant()) {
				termList = new ArrayList<Term>();
				termList.addAll(seedSearch(predicate,i,new Hashtable<IPredicate, List<Integer>>(),0));
				this.term = term;
				return;
			}
			i++;
		}
	}
	
	public IClause next() {
		if (ALLOWED_SEED_SEARCH == currentSeedSearch) {
			currentSeedSearch = 0;
			return null;
		}
		else currentSeedSearch++;
		
		debug("SeedSearch: initiating");
		
		while ((currentClause == null || termList.isEmpty()) && candidatesIterator.hasNext()) {
			nextClause();
		}
		if (currentClause == null || termList.isEmpty()) {
			debug("SeedSearch: nothing found");
			return null;
		}
		debug("SeedSearch: seeds for "+currentClause+", "+termList);
		
		// TODO clean up this
		Term newTerm = termList.remove(0);
		Set<Variable> variables = new HashSet<Variable>();
		term.collectVariables(variables);
		Variable v = variables.iterator().next();
		Term t = getTermForVariable(term, newTerm, v);
		//////
		
		inferrer.setTerm(t);
		inferrer.setVariable(v);
		
		currentClause.infer(inferrer);
		
		IClause result = inferrer.getResult();
		
		debug("SeedSearch: final clause "+result);
		return result;
	}
	
	private Term getTermForVariable(Term originalTerm, Term newTerm, Variable v) {
		return ( originalTerm == v ) ? newTerm : null;
	}
	
	private boolean verifyAndAddToTable(IPredicate predicate, int position, Hashtable<IPredicate, List<Integer>> visitedPredicates) {
		if (visitedPredicates.containsKey(predicate)) return visitedPredicates.get(predicate).contains(position);
		else {
			List<Integer> table = new ArrayList<Integer>();
			table.add(position);
			visitedPredicates.put(predicate, table);
			return false;
		}
	}
	
	private Set<Term> seedSearch(IPredicate predicate, int position, Hashtable<IPredicate, List<Integer>> visitedPredicates, int count) {
		if (verifyAndAddToTable(predicate, position, visitedPredicates)) return new HashSet<Term>();
		if (count > 10) return new HashSet<Term>();
		
//		debug("SeedSearch: predicate "+predicate+" at pos "+position);
		Set<Term> result = new HashSet<Term>();
		ResetIterator<IClause> nonUnitIterator = clauses.iterator();
		NonUnitMatcher matcher = new NonUnitMatcher(nonUnitIterator);
		Iterator<IClause> iterator = matcher.iterator(predicate,true);
		while (iterator.hasNext()) {
			IClause clause = iterator.next();
//			debug("SeedSearch: matching clause "+clause);
			List<IPredicate> matchingPredicates = getMatchingPredicates(clause, predicate);
			for (IPredicate matchingPredicate : matchingPredicates) {
				Term t = matchingPredicate.getTerms().get(position);
				if (t.isConstant() && !t.isQuantified()) result.add(t);
				else {
					Set<Variable> variables = new HashSet<Variable>();
					t.collectVariables(variables);
					Hashtable<Variable, List<Term>> table = new Hashtable<Variable, List<Term>>();
					for (Variable variable : variables) {
						List<Term> values = new ArrayList<Term>();
						for (IPredicate predicateWithVariable : getLiteralsWithVariable(variable, clause)) {
							for (Integer i : getPositions(predicateWithVariable, variable)) {
								// avoid looping on the matching predicates
								if (i == position && matchingPredicates.contains(predicateWithVariable)) continue;
								values.addAll(seedSearch(predicateWithVariable, i, visitedPredicates, ++count));
							}
						}
						table.put(variable, values);
					}
					result.addAll(computeValues(t,table));
				}
			}
		}
		nonUnitIterator.delete();
		return result;
	}
	
	private List<Term> computeValues(Term t, Hashtable<Variable, List<Term>> table) {
		return ( table.containsKey(t) ) ? table.get(t) : new ArrayList<Term>();
	}
	
	private Set<Integer> getPositions(IPredicate predicate, Variable v) {
		Set<Integer> result = new HashSet<Integer>();
		int i = 0;
		for (Term term : predicate.getTerms()) {
			if (term.contains(v)) result.add(i);
			i++;
		}
		return result;
	}
	
	
	// gets the literals with variable v occurring in it in clause 
	// the result could also contain the predicates
	private List<IPredicate> getLiteralsWithVariable(Variable v,
			IClause clause) {
		List<IPredicate> result = new ArrayList<IPredicate>();
		for (IPredicate predicate : clause.getPredicateLiterals()) {
			for (Term term : predicate.getTerms()) {
				if (term.contains(v)) result.add(predicate);
			}
		}
		return result;
	}
	
	// TODO this code is also in ResolutionResolver
	private List<IPredicate> getMatchingPredicates(IClause clause, IPredicate predicate) {
		List<IPredicate> result = new ArrayList<IPredicate>();
		List<IPredicate> literals = clause.getPredicateLiterals();
		for (int i = 0;i<literals.size();i++) {
			if (PPPredicate.match(predicate, clause.getPredicateLiterals().get(i), clause instanceof PPEqClause)) {
				result.add(literals.get(i));
			}
		}
		return result;
	}
	
	public void registerDumper(Dumper dumper) {
		dumper.addDataStructure("seed search", candidates.iterator());
	}
	
	private boolean accepts(IClause clause) {
		return clause.isUnit() 
			&& clause.getPredicateLiterals().size() == 1 
			&& isQuantified(clause);
	}
	
	private boolean isQuantified(IClause clause) {
		IPredicate predicate = clause.getPredicateLiterals().get(0);
		for (Term term : predicate.getTerms()) {
			if (term.isQuantified()) return true;
		}
		return false;
	}
	
	@Override
	public void removeClause(IClause clause) {
		if (clause.equals(currentClause)) {
			currentClause = null;
			termList = null;
		}
		
		if (accepts(clause)) candidates.remove(clause);
	}

	@Override
	public void newClause(IClause clause) {
		if (accepts(clause)) candidates.appends(clause);
	}

	private Set<IClause> emptySet = new HashSet<IClause>();
	public Set<IClause> getGeneratedClauses() {
		return emptySet;
	}

	
}
