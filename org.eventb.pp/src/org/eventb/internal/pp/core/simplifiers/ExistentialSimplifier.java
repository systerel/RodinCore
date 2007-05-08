package org.eventb.internal.pp.core.simplifiers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eventb.internal.pp.core.elements.IArithmetic;
import org.eventb.internal.pp.core.elements.IClause;
import org.eventb.internal.pp.core.elements.IEquality;
import org.eventb.internal.pp.core.elements.ILiteral;
import org.eventb.internal.pp.core.elements.IPredicate;
import org.eventb.internal.pp.core.elements.PPDisjClause;
import org.eventb.internal.pp.core.elements.PPEqClause;
import org.eventb.internal.pp.core.elements.terms.AbstractVariable;
import org.eventb.internal.pp.core.elements.terms.Constant;
import org.eventb.internal.pp.core.elements.terms.LocalVariable;
import org.eventb.internal.pp.core.elements.terms.Term;

public class ExistentialSimplifier implements ISimplifier {

	private List<IPredicate> predicates;
	private List<IEquality> equalities;
	private List<IArithmetic> arithmetic;
	private List<IEquality> conditions;
	
	private void init(IClause clause) {
		predicates = clause.getPredicateLiterals();
		equalities = clause.getEqualityLiterals();
		arithmetic = clause.getArithmeticLiterals();
		conditions = clause.getConditions();
	}
	
	public IClause simplifyDisjunctiveClause(PPDisjClause clause) {
		init(clause);
		simplifyExistentialHelper(predicates);
		simplifyExistentialHelper(equalities);
		simplifyExistentialHelper(arithmetic);
		simplifyExistentialHelper(conditions);
		IClause result = new PPDisjClause(clause.getOrigin(),predicates,equalities,arithmetic,conditions);
		return result;
	}

	
	// TODO test !
	public IClause simplifyEquivalenceClause(PPEqClause clause) {
		init(clause);
		simplifyExistentialHelper(conditions);
		IClause result = new PPEqClause(clause.getOrigin(),predicates,equalities,arithmetic,conditions);
		return result;
	}

	
	public <T extends ILiteral<T>> T simplifyExistential(ILiteral<T> literal) {
		List<LocalVariable> existentials = new ArrayList<LocalVariable>();
		for (Term term : literal.getTerms()) {
			term.collectLocalVariables(existentials);
		}
		Map<AbstractVariable, Term> map = new HashMap<AbstractVariable, Term>();
		for (AbstractVariable variable : existentials) {
			map.put(variable, new Constant(String.valueOf(Constant.uniqueIdentifier++),variable.getSort()));
		}
		return literal.substitute(map);
	}

	protected <T extends ILiteral<T>> void simplifyExistentialHelper(List<T> list) {
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

	public boolean canSimplify(IClause clause) {
		return true;
	}
	
}
