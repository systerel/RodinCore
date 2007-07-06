package org.eventb.internal.pp.core.simplifiers;

import java.util.Iterator;
import java.util.List;

import org.eventb.internal.pp.core.IVariableContext;
import org.eventb.internal.pp.core.elements.ArithmeticLiteral;
import org.eventb.internal.pp.core.elements.Clause;
import org.eventb.internal.pp.core.elements.DisjunctiveClause;
import org.eventb.internal.pp.core.elements.EqualityLiteral;
import org.eventb.internal.pp.core.elements.EquivalenceClause;
import org.eventb.internal.pp.core.elements.FalseClause;
import org.eventb.internal.pp.core.elements.PredicateLiteral;
import org.eventb.internal.pp.core.elements.TrueClause;

public class EqualitySimplifier implements ISimplifier {

	private List<EqualityLiteral> equalities;
	private List<PredicateLiteral> predicates;
	private List<ArithmeticLiteral> arithmetic;
	private List<EqualityLiteral> conditions;
	private boolean isEquivalence = false;
	private IVariableContext context;
	
	public EqualitySimplifier(IVariableContext context) {
		this.context = context;
	}
	
	private void init(Clause clause) {
		equalities = clause.getEqualityLiterals();
		predicates = clause.getPredicateLiterals();
		arithmetic = clause.getArithmeticLiterals();
		conditions = clause.getConditions();
	}
	
	private boolean isEmpty() {
		return conditions.size() + predicates.size() + arithmetic.size() + equalities.size() == 0;
	}
	
	public Clause simplifyDisjunctiveClause(DisjunctiveClause clause) {
		init(clause);
		boolean ok = simplifyEquality(equalities);
		if (!ok) {
			return new TrueClause(clause.getOrigin());
		}
		ok = simplifyEquality(conditions);
		if (!ok) {
			return new TrueClause(clause.getOrigin());
		}
		
		Clause result;
		if (isEmpty()) result = new FalseClause(clause.getOrigin()); 
		else result = new DisjunctiveClause(clause.getOrigin(),predicates,equalities,arithmetic,conditions);
		return result;
	}

	// TODO redo with tests
	public Clause simplifyEquivalenceClause(EquivalenceClause clause) {
		init(clause);
		isTrue = true;
		isEquivalence = true;
		simplifyEquality(equalities);
		isEquivalence = false;
		boolean ok = simplifyEquality(conditions);
		if (!ok) return new TrueClause(clause.getOrigin());
		if (!isTrue) {
			// we must inverse one predicate
			EquivalenceClause.inverseOneliteral(predicates, equalities, arithmetic);
		}
		else if (predicates.size() + equalities.size() 
				+ conditions.size() + arithmetic.size() == 0)
				return new TrueClause(clause.getOrigin());

		if (isEmpty()) return new FalseClause(clause.getOrigin());
		else return EquivalenceClause.newClause(clause.getOrigin(),
				predicates, equalities, arithmetic, conditions, context);
	}
	
	private boolean isTrue;
	private boolean simplifyEquality(List<EqualityLiteral> list) {
		// TODO adapt to equivalenceClassManager
		for (Iterator<EqualityLiteral> iter = list.iterator(); iter.hasNext();) {
			EqualityLiteral equality = iter.next();
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

	public boolean equal(EqualityLiteral equality) {
		return equality.getTerms().get(0).equals(equality.getTerms().get(1));
	}

	public boolean canSimplify(Clause clause) {
		return clause.getEqualityLiterals().size() > 0 || clause.getConditions().size() > 0;
	}
}
