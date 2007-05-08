package org.eventb.internal.pp.core.simplifiers;

import java.util.Iterator;
import java.util.List;

import org.eventb.internal.pp.core.IVariableContext;
import org.eventb.internal.pp.core.elements.IArithmetic;
import org.eventb.internal.pp.core.elements.IClause;
import org.eventb.internal.pp.core.elements.IEquality;
import org.eventb.internal.pp.core.elements.IPredicate;
import org.eventb.internal.pp.core.elements.PPDisjClause;
import org.eventb.internal.pp.core.elements.PPEqClause;
import org.eventb.internal.pp.core.elements.PPFalseClause;
import org.eventb.internal.pp.core.elements.PPTrueClause;

public class EqualitySimplifier implements ISimplifier {

	private List<IEquality> equalities;
	private List<IPredicate> predicates;
	private List<IArithmetic> arithmetic;
	private List<IEquality> conditions;
	private boolean isEquivalence = false;
	private IVariableContext context;
	
	public EqualitySimplifier(IVariableContext context) {
		this.context = context;
	}
	
	private void init(IClause clause) {
		equalities = clause.getEqualityLiterals();
		predicates = clause.getPredicateLiterals();
		arithmetic = clause.getArithmeticLiterals();
		conditions = clause.getConditions();
	}
	
	private boolean isEmpty() {
		return conditions.size() + predicates.size() + arithmetic.size() + equalities.size() == 0;
	}
	
	public IClause simplifyDisjunctiveClause(PPDisjClause clause) {
		init(clause);
		boolean ok = simplifyEquality(equalities);
		if (!ok) {
			return new PPTrueClause(clause.getOrigin());
		}
		ok = simplifyEquality(conditions);
		if (!ok) {
			return new PPTrueClause(clause.getOrigin());
		}
		
		IClause result;
		if (isEmpty()) result = new PPFalseClause(clause.getOrigin()); 
		else result = new PPDisjClause(clause.getOrigin(),predicates,equalities,arithmetic,conditions);
		return result;
	}

	// TODO redo with tests
	public IClause simplifyEquivalenceClause(PPEqClause clause) {
		init(clause);
		isTrue = true;
		isEquivalence = true;
		simplifyEquality(equalities);
		isEquivalence = false;
		boolean ok = simplifyEquality(conditions);
		if (!ok) return new PPTrueClause(clause.getOrigin());
		if (!isTrue) {
			// we must inverse one predicate
			PPEqClause.inverseOneliteral(predicates, equalities, arithmetic);
		}
		else if (predicates.size() + equalities.size() 
				+ conditions.size() + arithmetic.size() == 0)
				return new PPTrueClause(clause.getOrigin());

		if (isEmpty()) return new PPFalseClause(clause.getOrigin());
		else return PPEqClause.newClause(clause.getOrigin(),
				predicates, equalities, arithmetic, conditions, context);
	}
	
	private boolean isTrue;
	private boolean simplifyEquality(List<IEquality> list) {
		// TODO adapt to equivalenceClassManager
		for (Iterator<IEquality> iter = list.iterator(); iter.hasNext();) {
			IEquality equality = iter.next();
			if (equal(equality)) {
				if (!isEquivalence && !equality.isPositive()) iter.remove();
				else if (!isEquivalence && equality.isPositive()) {
					return false;
				}
				else if (isEquivalence) {
					iter.remove();
					if (!equality.isPositive()) isTrue = !isTrue;
				}
			}
		}
		return true;
	}

	public boolean equal(IEquality equality) {
		return equality.getTerms().get(0).equals(equality.getTerms().get(1));
	}

	public boolean canSimplify(IClause clause) {
		return clause.getEqualityLiterals().size() > 0 || clause.getConditions().size() > 0;
	}
}
