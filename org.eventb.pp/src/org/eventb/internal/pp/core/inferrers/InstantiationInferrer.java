package org.eventb.internal.pp.core.inferrers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eventb.internal.pp.core.IVariableContext;
import org.eventb.internal.pp.core.elements.IClause;
import org.eventb.internal.pp.core.elements.ILiteral;
import org.eventb.internal.pp.core.elements.PPDisjClause;
import org.eventb.internal.pp.core.elements.PPEqClause;
import org.eventb.internal.pp.core.elements.terms.AbstractVariable;
import org.eventb.internal.pp.core.elements.terms.Term;
import org.eventb.internal.pp.core.elements.terms.Variable;
import org.eventb.internal.pp.core.tracing.ClauseOrigin;
import org.eventb.internal.pp.core.tracing.IOrigin;

public class InstantiationInferrer extends AbstractInferrer {

	private Map<Variable, Term> instantiationMap = new HashMap<Variable, Term>();
	protected IClause result;

	public void addInstantiation(Variable variable, Term term) {
		if (instantiationMap.containsKey(variable)) throw new IllegalStateException();
		instantiationMap.put(variable, term);
	}
	
	public IClause getResult() {
		return result;
	}
	
	public InstantiationInferrer(IVariableContext context) {
		super(context);
	}

	protected void substitute() {
		HashMap<AbstractVariable, Term> map = new HashMap<AbstractVariable, Term>();
		for (Entry<Variable, Term> entry : instantiationMap.entrySet()) {
			AbstractVariable variableInCopy = substitutionsMap.get(entry.getKey());
			map.put(variableInCopy, entry.getValue());
		}
		substituteInList(predicates, map);
		substituteInList(equalities, map);
		substituteInList(arithmetic, map);
	}
	
	private <T extends ILiteral<T>> void substituteInList(List<T> literals, 
			HashMap<AbstractVariable, Term> map) {
		List<T> newList = new ArrayList<T>();
		for (T literal : literals) {
			newList.add(literal.substitute(map));
		}
		literals.clear();
		literals.addAll(newList);
	}
	
	@Override
	protected void inferFromDisjunctiveClauseHelper(IClause clause) {
		substitute();
		result = new PPDisjClause(getOrigin(clause),predicates,equalities,arithmetic,conditions);
	}

	@Override
	protected void inferFromEquivalenceClauseHelper(IClause clause) {
		substitute();
		result = new PPEqClause(getOrigin(clause),predicates,equalities,arithmetic,conditions);
	}

	@Override
	protected void initialize(IClause clause) throws IllegalStateException {
		if (instantiationMap.isEmpty()) throw new IllegalStateException();
	}

	@Override
	protected void reset() {
		instantiationMap.clear();
	}
	
	protected IOrigin getOrigin(IClause clause) {
		List<IClause> parents = new ArrayList<IClause>();
		parents.add(clause);
		return new ClauseOrigin(parents);
	}

}
