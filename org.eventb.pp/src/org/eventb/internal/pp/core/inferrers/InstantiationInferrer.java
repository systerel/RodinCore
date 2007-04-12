package org.eventb.internal.pp.core.inferrers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.eventb.internal.pp.core.IVariableContext;
import org.eventb.internal.pp.core.Level;
import org.eventb.internal.pp.core.elements.IClause;
import org.eventb.internal.pp.core.elements.ILiteral;
import org.eventb.internal.pp.core.elements.PPDisjClause;
import org.eventb.internal.pp.core.elements.PPEqClause;
import org.eventb.internal.pp.core.elements.terms.AbstractVariable;
import org.eventb.internal.pp.core.elements.terms.Term;
import org.eventb.internal.pp.core.elements.terms.Variable;
import org.eventb.internal.pp.core.tracing.ClauseOrigin;

public class InstantiationInferrer extends AbstractInferrer {

	private Variable variable;
	private Term term;
	
	private Level level;
	
	private IClause result;
	
	public void setVariable(Variable variable) {
		this.variable = variable;
	}
	
	public void setTerm(Term term) {
		assert term.isConstant();
		
		this.term = term;
	}
	
	public IClause getResult() {
		return result;
	}
	
	public InstantiationInferrer(IVariableContext context) {
		super(context);
	}

	private void substitute() {
		AbstractVariable variableInCopy = substitutionsMap.get(variable);
		HashMap<AbstractVariable, Term> map = new HashMap<AbstractVariable, Term>();
		map.put(variableInCopy, term);
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
	protected void inferFromDisjunctiveClauseHelper() {
		substitute();
		result = new PPDisjClause(level,predicates,equalities,arithmetic);
	}

	@Override
	protected void inferFromEquivalenceClauseHelper() {
		substitute();
		result = new PPEqClause(level,predicates,equalities,arithmetic,conditions);
	}

	@Override
	protected void initialize(IClause clause) throws IllegalStateException {
		if (variable == null || term == null) throw new IllegalStateException();
		level = clause.getLevel();
	}

	@Override
	protected void reset() {
		variable = null;
		term = null;
	}

	@Override
	protected void setParents(IClause clause) {
		List<IClause> parents = new ArrayList<IClause>();
		parents.add(clause);
		result.setOrigin(new ClauseOrigin(parents));
	}

	public boolean canInfer(IClause clause) {
		return true;
	}

}
