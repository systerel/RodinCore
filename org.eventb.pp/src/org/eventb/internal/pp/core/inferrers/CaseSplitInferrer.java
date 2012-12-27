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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eventb.internal.pp.core.Level;
import org.eventb.internal.pp.core.elements.ArithmeticLiteral;
import org.eventb.internal.pp.core.elements.Clause;
import org.eventb.internal.pp.core.elements.EqualityLiteral;
import org.eventb.internal.pp.core.elements.EquivalenceClause;
import org.eventb.internal.pp.core.elements.Literal;
import org.eventb.internal.pp.core.elements.PredicateLiteral;
import org.eventb.internal.pp.core.elements.terms.LocalVariable;
import org.eventb.internal.pp.core.elements.terms.SimpleTerm;
import org.eventb.internal.pp.core.elements.terms.VariableContext;
import org.eventb.internal.pp.core.provers.casesplit.CaseSplitter;
import org.eventb.internal.pp.core.tracing.IOrigin;
import org.eventb.internal.pp.core.tracing.SplitOrigin;

/**
 * This class is responsible for splitting a clause in two.
 * <p>
 * For now, it also contains the logic that chooses where the clause is split.
 * TODO This logic should move in the {@link CaseSplitter}.
 *
 * @author François Terrier
 *
 */
public class CaseSplitInferrer extends AbstractInferrer {

	private Level parent;
	
	public CaseSplitInferrer(VariableContext context) {
		super(context);
	}
	
	public void setLevel(Level parent) {
		this.parent = parent;
	}
	
	public Set<Clause> getLeftCase() {
		return left;
	}

	public Set<Clause> getRightCase() {
		return right;
	}

	private Set<Clause> left, right;
	private List<PredicateLiteral> leftPredicates = new ArrayList<PredicateLiteral>();
	private List<EqualityLiteral> leftEqualities = new ArrayList<EqualityLiteral>();
	private List<ArithmeticLiteral> leftArithmetic = new ArrayList<ArithmeticLiteral>();
	
	private List<PredicateLiteral> rightPredicates = new ArrayList<PredicateLiteral>();
	private List<EqualityLiteral> rightEqualities = new ArrayList<EqualityLiteral>();
	private List<ArithmeticLiteral> rightArithmetic = new ArrayList<ArithmeticLiteral>();
	
	private void splitLeftCase() {
		// warning, both cases must have distinct variables
		// for now if we do not split on variables it is no problem
		if (hasConstantLiteral(predicates)) {
			PredicateLiteral literal = getConstantLiteral(predicates);
			predicates.remove(literal);
			leftPredicates.add(literal);
			rightPredicates.add(inverseLiteral(literal));
		}
		else if (hasConstantLiteral(equalities)) {
			EqualityLiteral literal = getConstantLiteral(equalities);
			equalities.remove(literal);
			leftEqualities.add(literal);
			rightEqualities.add(inverseLiteral(literal));
		}
		else if (hasConstantLiteral(arithmetic)) {
			ArithmeticLiteral literal = getConstantLiteral(arithmetic);
			arithmetic.remove(literal);
			leftArithmetic.add(literal);
			rightArithmetic.add(inverseLiteral(literal));
		}
		else {
			throw new IllegalStateException();
		}
	}
	
	private <T extends Literal<T,?>> T getConstantLiteral(List<T> literals) {
		for (T t : literals) {
			if (t.isConstant()) return t;
		}
		return null;
	}
	
	private <T extends Literal<T,?>> T inverseLiteral(T literal) {
		Literal<T,?> result = literal.getInverse();
		Set<LocalVariable> variables = new HashSet<LocalVariable>();
		result.collectLocalVariables(variables);
		Map<SimpleTerm, SimpleTerm> map = new HashMap<SimpleTerm, SimpleTerm>();
		for (LocalVariable variable : variables) {
			if (variable.isForall()) map.put(variable, variable.getVariable(context));
		}
		return result.substitute(map);
	}
	
	@Override
	protected void inferFromDisjunctiveClauseHelper(Clause clause) {
		splitLeftCase();
		left.add(cf.makeDisjunctiveClause(getOrigin(clause, parent.getLeftBranch()),leftPredicates,leftEqualities,leftArithmetic,new ArrayList<EqualityLiteral>()));
		// right case
		right.add(cf.makeDisjunctiveClause(getOrigin(clause, parent.getRightBranch()),predicates,equalities,arithmetic,new ArrayList<EqualityLiteral>()));
	}

	@Override
	protected void inferFromEquivalenceClauseHelper(Clause clause) {
		splitLeftCase();
		left.add(cf.makeClauseFromEquivalenceClause(getOrigin(clause, parent.getLeftBranch()),leftPredicates,leftEqualities,leftArithmetic,new ArrayList<EqualityLiteral>(),context));
		HashMap<SimpleTerm, SimpleTerm> map = new HashMap<SimpleTerm, SimpleTerm>();
		
		List<PredicateLiteral> predicates = new ArrayList<PredicateLiteral>();
		predicates.addAll(this.predicates);
		getListCopy(predicates, map, context);
		
		List<EqualityLiteral> equalities = new ArrayList<EqualityLiteral>();
		equalities.addAll(this.equalities);
		getListCopy(equalities, map, context);
		
		List<ArithmeticLiteral> arithmetic = new ArrayList<ArithmeticLiteral>();
		arithmetic.addAll(this.arithmetic);
		getListCopy(arithmetic, map, context);
		
		left.add(cf.makeClauseFromEquivalenceClause(getOrigin(clause, parent.getLeftBranch()), predicates, equalities, arithmetic, new ArrayList<EqualityLiteral>(), context));
		
		// right case
		right.add(cf.makeClauseFromEquivalenceClause(getOrigin(clause, parent.getRightBranch()), rightPredicates, rightEqualities, rightArithmetic,new ArrayList<EqualityLiteral>(),context));
		EquivalenceClause.inverseOneliteral(this.predicates, this.equalities, this.arithmetic);
		right.add(cf.makeClauseFromEquivalenceClause(getOrigin(clause, parent.getRightBranch()), this.predicates, this.equalities, this.arithmetic, new ArrayList<EqualityLiteral>(), context));
	}

	@Override
	protected void initialize(Clause clause) throws IllegalStateException {
		// we should do this, for performance reason we let it down
		// if (!canInfer(clause)) throw new IllegalStateException(); 
		if (parent == null) throw new IllegalStateException();

		left = new HashSet<Clause>();
		right = new HashSet<Clause>();
		leftArithmetic.clear();
		leftEqualities.clear();
		leftPredicates.clear();
		rightArithmetic.clear();
		rightEqualities.clear();
		rightPredicates.clear();
	}

	@Override
	protected void reset() {
		parent = null;
		super.reset();
	}

	protected IOrigin getOrigin(Clause clause, Level level) {
		List<Clause> parents = new ArrayList<Clause>();
		parents.add(clause);
		return new SplitOrigin(parents, level);
	}

	
	public boolean canInfer(Clause clause) {
		if (clause.isUnit()) return false;
		if (clause.getOrigin().isDefinition()) return false;
		if (clause.hasConditions()) return false;
		
		if (!(	hasConstantLiteral(clause.getPredicateLiterals())
				|| hasConstantLiteral(clause.getArithmeticLiterals())
				|| hasConstantLiteral(clause.getEqualityLiterals())
				|| hasConstantLiteral(clause.getConditions()))
		) return false;
		
//		if (clause.getOrigin().getLevel().getHeight()>2) return false;
//		if (clause.getArithmeticLiterals().size()+clause.getEqualityLiterals().size()+clause.getPredicateLiterals().size()>2) return false;
//		if (!clause.getOrigin().dependsOnGoal()) return false;

		return true;
	}
	
	private boolean hasConstantLiteral(List<? extends Literal<?,?>> literals) {
		for (Literal<?,?> literal : literals) {
			if (literal.isConstant()) return true;
		}
		return false;
	}
	
}
