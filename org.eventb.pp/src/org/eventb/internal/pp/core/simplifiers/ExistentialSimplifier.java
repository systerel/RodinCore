package org.eventb.internal.pp.core.simplifiers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eventb.internal.pp.core.elements.ArithmeticLiteral;
import org.eventb.internal.pp.core.elements.Clause;
import org.eventb.internal.pp.core.elements.DisjunctiveClause;
import org.eventb.internal.pp.core.elements.EqualityLiteral;
import org.eventb.internal.pp.core.elements.EquivalenceClause;
import org.eventb.internal.pp.core.elements.Literal;
import org.eventb.internal.pp.core.elements.PredicateLiteral;
import org.eventb.internal.pp.core.elements.terms.Constant;
import org.eventb.internal.pp.core.elements.terms.LocalVariable;
import org.eventb.internal.pp.core.elements.terms.SimpleTerm;
import org.eventb.internal.pp.core.elements.terms.Term;

public class ExistentialSimplifier implements ISimplifier {

	private int constantIdentifier = 0;
	
	private List<PredicateLiteral> predicates;
	private List<EqualityLiteral> equalities;
	private List<ArithmeticLiteral> arithmetic;
	private List<EqualityLiteral> conditions;
	
	private void init(Clause clause) {
		predicates = clause.getPredicateLiterals();
		equalities = clause.getEqualityLiterals();
		arithmetic = clause.getArithmeticLiterals();
		conditions = clause.getConditions();
	}
	
	public Clause simplifyDisjunctiveClause(DisjunctiveClause clause) {
		init(clause);
		simplifyExistentialHelper(predicates);
		simplifyExistentialHelper(equalities);
		simplifyExistentialHelper(arithmetic);
		simplifyExistentialHelper(conditions);
		Clause result = new DisjunctiveClause(clause.getOrigin(),predicates,equalities,arithmetic,conditions);
		return result;
	}

	
	// TODO test !
	public Clause simplifyEquivalenceClause(EquivalenceClause clause) {
		init(clause);
		simplifyExistentialHelper(conditions);
		Clause result = new EquivalenceClause(clause.getOrigin(),predicates,equalities,arithmetic,conditions);
		return result;
	}

	
	public <T extends Literal<T,?>> T simplifyExistential(Literal<T,?> literal) {
		Set<LocalVariable> existentials = new HashSet<LocalVariable>();
		for (Term term : literal.getTerms()) {
			term.collectLocalVariables(existentials);
		}
		Map<SimpleTerm, SimpleTerm> map = new HashMap<SimpleTerm, SimpleTerm>();
		for (SimpleTerm variable : existentials) {
			map.put(variable, new Constant(String.valueOf(constantIdentifier++),variable.getSort()));
		}
		return literal.substitute(map);
	}

	protected <T extends Literal<T,?>> void simplifyExistentialHelper(List<T> list) {
		ArrayList<T> tmp1 = new ArrayList<T>();
		for (T literal : list) {
			if (literal.isConstant()) {
				tmp1.add(simplifyExistential(literal));
			}
			else {
				tmp1.add(literal);
			}
		}
		list.clear();
		list.addAll(tmp1);
	}

	public boolean canSimplify(Clause clause) {
		return true;
	}
	
}
