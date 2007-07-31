package org.eventb.internal.pp.core.inferrers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eventb.internal.pp.core.IVariableContext;
import org.eventb.internal.pp.core.elements.Clause;
import org.eventb.internal.pp.core.elements.DisjunctiveClause;
import org.eventb.internal.pp.core.elements.EquivalenceClause;
import org.eventb.internal.pp.core.elements.Literal;
import org.eventb.internal.pp.core.elements.terms.SimpleTerm;
import org.eventb.internal.pp.core.elements.terms.Variable;
import org.eventb.internal.pp.core.tracing.ClauseOrigin;
import org.eventb.internal.pp.core.tracing.IOrigin;

public class InstantiationInferrer extends AbstractInferrer {

	private Map<Variable, SimpleTerm> instantiationMap = new HashMap<Variable, SimpleTerm>();
	protected Clause result;

	// variable must be the original variable of the clause, not a copy
	// because we increment the instantiation count of the original variable
	public void addInstantiation(Variable variable, SimpleTerm term) {
		// TODO increment instantiation count and handle blocking
//		variable.incrementInstantiationCount();
		if (instantiationMap.containsKey(variable)) throw new IllegalStateException();
		instantiationMap.put(variable, term);
	}
	
	public Clause getResult() {
		return result;
	}
	
	public InstantiationInferrer(IVariableContext context) {
		super(context);
	}

	protected void substitute() {
		HashMap<SimpleTerm, SimpleTerm> map = new HashMap<SimpleTerm, SimpleTerm>();
		for (Entry<Variable, SimpleTerm> entry : instantiationMap.entrySet()) {
			SimpleTerm variableInCopy = substitutionsMap.get(entry.getKey());
			map.put(variableInCopy, entry.getValue());
		}
		substituteInList(predicates, map);
		substituteInList(equalities, map);
		substituteInList(arithmetic, map);
	}
	
	private <T extends Literal<T,?>> void substituteInList(List<T> literals, 
			HashMap<SimpleTerm, SimpleTerm> map) {
		List<T> newList = new ArrayList<T>();
		for (T literal : literals) {
			newList.add(literal.substitute(map));
		}
		literals.clear();
		literals.addAll(newList);
	}
	
	@Override
	protected void inferFromDisjunctiveClauseHelper(Clause clause) {
		substitute();
		result = new DisjunctiveClause(getOrigin(clause),predicates,equalities,arithmetic,conditions);
	}

	@Override
	protected void inferFromEquivalenceClauseHelper(Clause clause) {
		substitute();
		result = new EquivalenceClause(getOrigin(clause),predicates,equalities,arithmetic,conditions);
	}

	@Override
	protected void initialize(Clause clause) throws IllegalStateException {
		if (instantiationMap.isEmpty()) throw new IllegalStateException();
	}

	@Override
	protected void reset() {
		instantiationMap.clear();
	}
	
	protected IOrigin getOrigin(Clause clause) {
		List<Clause> parents = new ArrayList<Clause>();
		parents.add(clause);
		return new ClauseOrigin(parents);
	}

}
