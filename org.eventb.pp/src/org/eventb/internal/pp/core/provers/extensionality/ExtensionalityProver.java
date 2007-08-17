/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eventb.internal.pp.core.provers.extensionality;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.eventb.internal.pp.core.ClauseSimplifier;
import org.eventb.internal.pp.core.Dumper;
import org.eventb.internal.pp.core.IProverModule;
import org.eventb.internal.pp.core.IVariableContext;
import org.eventb.internal.pp.core.Level;
import org.eventb.internal.pp.core.PredicateTable;
import org.eventb.internal.pp.core.ProverResult;
import org.eventb.internal.pp.core.elements.ArithmeticLiteral;
import org.eventb.internal.pp.core.elements.Clause;
import org.eventb.internal.pp.core.elements.ClauseFactory;
import org.eventb.internal.pp.core.elements.ComplexPredicateLiteral;
import org.eventb.internal.pp.core.elements.EqualityLiteral;
import org.eventb.internal.pp.core.elements.PredicateLiteral;
import org.eventb.internal.pp.core.elements.PredicateLiteralDescriptor;
import org.eventb.internal.pp.core.elements.Sort;
import org.eventb.internal.pp.core.elements.terms.Constant;
import org.eventb.internal.pp.core.elements.terms.SimpleTerm;
import org.eventb.internal.pp.core.elements.terms.Term;
import org.eventb.internal.pp.core.elements.terms.Variable;
import org.eventb.internal.pp.core.tracing.ExtensionalityOrigin;

public class ExtensionalityProver implements IProverModule {
	
	private PredicateTable predicateTable;
	private IVariableContext variableContext;
	private ClauseSimplifier simplifier;
	private ClauseFactory cf = ClauseFactory.getDefault();
	
	public ExtensionalityProver(PredicateTable predicateTable,
			IVariableContext variableContext) {
		this.predicateTable = predicateTable;
		this.variableContext = variableContext;
	}
	
	public ProverResult addClauseAndDetectContradiction(Clause clause) {
		Clause derivedClause = null;
		if (clause.getOrigin() instanceof ExtensionalityOrigin) return ProverResult.EMPTY_RESULT;
		
		if (isUnitSetEqualityLiteral(clause)) {
			derivedClause = getEquivalence(clause, getUnitEqualityLiteral(clause));
		}
		else if (isEquivalenceCandidate(clause)) {
			PredicateLiteral literal1 = clause.getPredicateLiteral(0);
			PredicateLiteral literal2 = clause.getPredicateLiteral(1);
			boolean sameSign = haveSameSign(literal1, literal2);
			if (	(sameSign && haveSameVariables(literal1, literal2))
				|| 	(!sameSign && haveSameConstants(literal1, literal2))) {
				SimpleTerm term1 = getLastTerm(literal1);
				SimpleTerm term2 = getLastTerm(literal2);
				assert term1.getSort().equals(term2.getSort()) && predicateTable.getDescriptor(term1.getSort()) != null;
				if (isRealConstant(term1) && isRealConstant(term2)) {
					derivedClause = getUnitEqualityClause(term1, term2, sameSign, clause);
				}
			}
		}
		else if (isDisjunctiveCandidate(clause)) {
			//
		}
		if (derivedClause != null) return new ProverResult(simplifier.run(derivedClause));
		return ProverResult.EMPTY_RESULT;
	}

	private boolean isRealConstant(SimpleTerm term) {
		return term.isConstant() && !term.isQuantified();
	}
	
	private Clause getUnitEqualityClause(SimpleTerm term1, SimpleTerm term2, boolean isPositive, Clause clause) {
		EqualityLiteral equality = new EqualityLiteral(term1, term2, isPositive);
		List<EqualityLiteral> equalities = new ArrayList<EqualityLiteral>();
		equalities.add(equality);
		return cf.makeDisjunctiveClause(new ExtensionalityOrigin(clause), new ArrayList<PredicateLiteral>(),
				equalities, new ArrayList<ArithmeticLiteral>(), new ArrayList<EqualityLiteral>());
	}
	
	private SimpleTerm getLastTerm(PredicateLiteral literal) {
		return literal.getTerm(literal.getDescriptor().getArity()-1);
	}
	
	private boolean haveSameConstants(PredicateLiteral literal1, PredicateLiteral literal2) {
		for (int i = 0;i<literal1.getDescriptor().getArity()-1;i++) {
			Term term1 = literal1.getTerm(i);
			Term term2 = literal2.getTerm(i);
			if (!term1.isConstant()) return false;
			if (!term1.equals(term2)) return false;
		}
		return true;
	}
	
	/**
	 * Returns <code>true</code> if both literals are of the form PxyzA where xyz are
	 * the same variables for both literals and A can be either a constant or a variable 
	 * and not necessarily the same for both literals.
	 * 
	 * @param literal1
	 * @param literal2
	 * @return
	 */
	private boolean haveSameVariables(PredicateLiteral literal1, PredicateLiteral literal2) {
		for (int i = 0;i<literal1.getDescriptor().getArity()-1;i++) {
			Term term1 = literal1.getTerm(i);
			Term term2 = literal2.getTerm(i);
			if (term1.isConstant() || term1 != term2) return false;
		}
		return true;
	}
	
	private boolean haveSameSign(PredicateLiteral literal1, PredicateLiteral literal2) {
		return literal1.isPositive() == literal2.isPositive();
	}

	private boolean isCandidate(Clause clause) {
		if (clause.isBlockedOnConditions()) return false;
		if (clause.sizeWithoutConditions() != 2) return false;
		if (clause.getPredicateLiteralsSize() != 2) return false;
		PredicateLiteral literal1 = clause.getPredicateLiteral(0);
		PredicateLiteral literal2 = clause.getPredicateLiteral(1);
		if (!literal1.getDescriptor().equals(literal2.getDescriptor())) return false;
		if (!literal1.getDescriptor().isCompletePredicate()) return false;
		return true;
	}
	
	private boolean isDisjunctiveCandidate(Clause clause) {
		if (clause.isEquivalence()) return false;
		return isCandidate(clause);
	}
	
	private boolean isEquivalenceCandidate(Clause clause) {
		if (!clause.isEquivalence()) return false;
		return isCandidate(clause);
	}

	private Clause getEquivalence(Clause clause, EqualityLiteral equality) {
		Sort sort = equality.getSort();
		PredicateLiteralDescriptor descriptor = predicateTable.getDescriptor(sort);
		List<SimpleTerm> terms1 = new ArrayList<SimpleTerm>();
		List<SimpleTerm> terms2 = new ArrayList<SimpleTerm>();
		PredicateLiteral literal1,literal2;
		if (equality.isPositive()) {
			int i = 1;
			for (Sort variableSort : descriptor.getSortList()) {
				if (i<descriptor.getArity()) {
					Variable variable = variableContext.getNextVariable(variableSort);
					terms1.add(variable);
					terms2.add(variable);
					i++;
				}
			}
		}
		else {
			int i = 1;
			for (Sort variableSort : descriptor.getSortList()) {
				if (i<descriptor.getArity()) {
					Constant constant = variableContext.getNextFreshConstant(variableSort);
					terms1.add(constant);
					terms2.add(constant);
					i++;
				}
			}
		}
		terms1.add(equality.getTerm1());
		terms2.add(equality.getTerm2());
		assert terms1.size() == terms2.size() && terms1.size() == descriptor.getArity();
		literal1 = new ComplexPredicateLiteral(descriptor,equality.isPositive(),terms1);
		literal2 = new ComplexPredicateLiteral(descriptor,true,terms2);
		return cf.makeEquivalenceClause(new ExtensionalityOrigin(clause), getPredicateLiterals(literal1, literal2),
				new ArrayList<EqualityLiteral>(), new ArrayList<ArithmeticLiteral>(),
				new ArrayList<EqualityLiteral>());
	}
	
	private List<PredicateLiteral> getPredicateLiterals(PredicateLiteral literal1, PredicateLiteral literal2) {
		List<PredicateLiteral> predicates = new ArrayList<PredicateLiteral>();
		predicates.add(literal1);
		predicates.add(literal2);
		return predicates;
	}
	
	private boolean isUnitSetEqualityLiteral(Clause clause) {
		if (!clause.isUnit()) return false;
		if (clause.getEqualityLiteralsSize() == 1) {
			if (isSetEquality(clause.getEqualityLiteral(0))) return true;
		}
		return false;
	}
	
	private boolean isSetEquality(EqualityLiteral equality) {
		if (!equality.getSort().isSetSort()) return false;
		PredicateLiteralDescriptor descriptor = predicateTable.getDescriptor(equality.getSort());
		if (descriptor == null) return false;
		if (!descriptor.isCompletePredicate()) return false;
		return true;
	}
	
	private EqualityLiteral getUnitEqualityLiteral(Clause clause) {
		return clause.getEqualityLiteral(0);
	}

	public ProverResult contradiction(Level oldLevel, Level newLevel, Set<Level> dependencies) {
		// do nothing
		return ProverResult.EMPTY_RESULT;
	}

	public void initialize(ClauseSimplifier simplifier) {
		this.simplifier = simplifier;
	}

	public ProverResult next(boolean force) {
		return ProverResult.EMPTY_RESULT;
	}

	public void registerDumper(Dumper dumper) {
		// do nothing
	}

	public void removeClause(Clause clause) {
		// do nothing
	}
	
	@Override
	public String toString() {
		return "ExtensionalityProver";
	}


}
