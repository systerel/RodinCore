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
package org.eventb.internal.pp.core.inferrers;

import java.util.ArrayList;
import java.util.List;

import org.eventb.internal.pp.core.elements.Clause;
import org.eventb.internal.pp.core.elements.EqualityLiteral;
import org.eventb.internal.pp.core.elements.EquivalenceClause;
import org.eventb.internal.pp.core.elements.terms.Constant;
import org.eventb.internal.pp.core.elements.terms.Variable;
import org.eventb.internal.pp.core.elements.terms.VariableContext;
import org.eventb.internal.pp.core.tracing.ClauseOrigin;
import org.eventb.internal.pp.core.tracing.IOrigin;

public class EqualityInstantiationInferrer extends InstantiationInferrer {

	private List<EqualityLiteral> instantiationEqualities = new ArrayList<EqualityLiteral>();
	private List<Clause> parents = new ArrayList<Clause>();
	
	private boolean inverse;
	private boolean hasTrueLiterals;
	
	public EqualityInstantiationInferrer(VariableContext context) {
		super(context);
	}
	
	public void addEqualityEqual(EqualityLiteral equality, Constant constant) {
		addEquality(equality, constant);
		
		if (equality.isPositive()) hasTrueLiterals = true;
		else inverse = !inverse;
	}
	
	public void addEqualityUnequal(EqualityLiteral equality, Constant constant) {
		addEquality(equality, constant);
		
		if (equality.isPositive()) inverse = !inverse;
		else hasTrueLiterals = true;
	}
	
	private void addEquality(EqualityLiteral equality, Constant constant) {
		instantiationEqualities.add(equality);
		
		Variable variable = null;
		if (equality.getTerm(0) instanceof Variable) variable = (Variable)equality.getTerm(0);
		else if (equality.getTerm(1) instanceof Variable) variable = (Variable)equality.getTerm(1);
		else assert false;
		
		super.addInstantiation(variable, constant);
	}

	@Override
	protected void initialize(Clause clause) throws IllegalStateException {
		super.initialize(clause);
		if (instantiationEqualities.isEmpty()) throw new IllegalStateException();
		
		// remove equalities
		for (EqualityLiteral equality : instantiationEqualities) {
			conditions.remove(equality);
			equalities.remove(equality);
		}
	}
	
	@Override
	protected void inferFromEquivalenceClauseHelper(Clause clause) {
		substitute();
		if (isEmptyWithConditions() && !inverse) result = cf.makeTRUE(getOrigin(clause));
		else if (isEmptyWithConditions() && inverse) result = cf.makeFALSE(getOrigin(clause));
		else {
			if (inverse) EquivalenceClause.inverseOneliteral(predicates, equalities, arithmetic);
			result = cf.makeClauseFromEquivalenceClause(getOrigin(clause),predicates,equalities,arithmetic,conditions, context);
		}
	}

	@Override
	protected void inferFromDisjunctiveClauseHelper(Clause clause) {
		substitute();
		if (hasTrueLiterals) result = cf.makeTRUE(getOrigin(clause));
		if (isEmptyWithConditions()) result = cf.makeFALSE(getOrigin(clause));
		result = cf.makeDisjunctiveClause(getOrigin(clause),predicates,equalities,arithmetic,conditions);
	}
	
	public void addParentClauses(List<Clause> clauses) {
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
	protected IOrigin getOrigin(Clause clause) {
		List<Clause> clauseParents = new ArrayList<Clause>();
		clauseParents.addAll(parents);
		clauseParents.add(clause);
		return new ClauseOrigin(clauseParents);
	}

}
