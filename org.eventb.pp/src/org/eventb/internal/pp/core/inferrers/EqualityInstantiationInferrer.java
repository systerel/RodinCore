 package org.eventb.internal.pp.core.inferrers;

import java.util.ArrayList;
import java.util.List;

import org.eventb.internal.pp.core.IVariableContext;
import org.eventb.internal.pp.core.elements.IClause;
import org.eventb.internal.pp.core.elements.IEquality;
import org.eventb.internal.pp.core.elements.PPDisjClause;
import org.eventb.internal.pp.core.elements.PPEqClause;
import org.eventb.internal.pp.core.elements.PPFalseClause;
import org.eventb.internal.pp.core.elements.PPTrueClause;
import org.eventb.internal.pp.core.elements.terms.Constant;
import org.eventb.internal.pp.core.elements.terms.Variable;
import org.eventb.internal.pp.core.tracing.ClauseOrigin;
import org.eventb.internal.pp.core.tracing.IOrigin;

public class EqualityInstantiationInferrer extends InstantiationInferrer {

	private List<IEquality> instantiationEqualities = new ArrayList<IEquality>();
	private List<IClause> parents = new ArrayList<IClause>();
	
	private boolean inverse;
	private boolean hasTrueLiterals;
	
	public EqualityInstantiationInferrer(IVariableContext context) {
		super(context);
	}
	
	public void addEqualityEqual(IEquality equality, Constant constant) {
		addEquality(equality, constant);
		
		if (equality.isPositive()) hasTrueLiterals = true;
		else inverse = !inverse;
	}
	
	public void addEqualityUnequal(IEquality equality, Constant constant) {
		addEquality(equality, constant);
		
		if (equality.isPositive()) inverse = !inverse;
		else hasTrueLiterals = true;
	}
	
	private void addEquality(IEquality equality, Constant constant) {
		instantiationEqualities.add(equality);
		
		Variable variable = null;
		if (equality.getTerms().get(0) instanceof Variable) variable = (Variable)equality.getTerms().get(0);
		else if (equality.getTerms().get(1) instanceof Variable) variable = (Variable)equality.getTerms().get(1);
		else assert false;
		
		super.addInstantiation(variable, constant);
	}

	@Override
	protected void initialize(IClause clause) throws IllegalStateException {
		super.initialize(clause);
		if (instantiationEqualities.isEmpty()) throw new IllegalStateException();
		
		// remove equalities
		for (IEquality equality : instantiationEqualities) {
			conditions.remove(equality);
			equalities.remove(equality);
		}
	}
	
	@Override
	protected void inferFromEquivalenceClauseHelper(IClause clause) {
		substitute();
		if (isEmpty() && !inverse) result = new PPTrueClause(getOrigin(clause));
		else if (isEmpty() && inverse) result = new PPFalseClause(getOrigin(clause));
		else {
			if (inverse) PPEqClause.inverseOneliteral(predicates, equalities, arithmetic);
			result = PPEqClause.newClause(getOrigin(clause),predicates,equalities,arithmetic,conditions, context);
		}
	}

	@Override
	protected void inferFromDisjunctiveClauseHelper(IClause clause) {
		substitute();
		if (hasTrueLiterals) result = new PPTrueClause(getOrigin(clause));
		if (isEmpty()) result = new PPFalseClause(getOrigin(clause));
		result = new PPDisjClause(getOrigin(clause),predicates,equalities,arithmetic,conditions);
	}
	
	public void addParentClauses(List<IClause> clauses) {
		// these are the unit equality clauses
		parents.addAll(clauses);
	}
	
	@Override
	protected void reset() {
		inverse = false;
		hasTrueLiterals = false;
		instantiationEqualities.clear();
		parents.clear();
		super.reset();
	}
	
	@Override
	protected IOrigin getOrigin(IClause clause) {
		List<IClause> clauseParents = new ArrayList<IClause>();
		clauseParents.addAll(parents);
		clauseParents.add(clause);
		return new ClauseOrigin(clauseParents);
	}

}
