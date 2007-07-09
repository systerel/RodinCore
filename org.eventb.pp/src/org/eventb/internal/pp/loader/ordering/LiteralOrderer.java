package org.eventb.internal.pp.loader.ordering;

import java.util.Comparator;

import org.eventb.internal.pp.loader.formula.AbstractClause;
import org.eventb.internal.pp.loader.formula.ArithmeticFormula;
import org.eventb.internal.pp.loader.formula.EqualityFormula;
import org.eventb.internal.pp.loader.formula.ISignedFormula;
import org.eventb.internal.pp.loader.formula.PredicateFormula;
import org.eventb.internal.pp.loader.formula.QuantifiedFormula;

public class LiteralOrderer implements Comparator<ISignedFormula> {

	public int compare(ISignedFormula o1, ISignedFormula o2) {
//		if (o1.equals(o2)) return 0;
			
//		System.out.println(o1.getFormula().getClass());
//		System.out.println(o2.getFormula().getClass());
		
		int signComp = -new Boolean(o1.isPositive()).compareTo(o2.isPositive());
		if (o1.getFormula() instanceof PredicateFormula) {
			if (o2.getFormula() instanceof PredicateFormula) {
				PredicateFormula pred1 = (PredicateFormula)o1.getFormula();
				PredicateFormula pred2 = (PredicateFormula)o2.getFormula();
				return compare(pred1, pred2, signComp);	
			}
			else return -1;
		}
		if (o2.getFormula() instanceof PredicateFormula) return 1;
		// from here, neither o1 nor o2 is a predicate literal
		if (o1.getFormula() instanceof EqualityFormula) {
			if (o2.getFormula() instanceof EqualityFormula) {
				EqualityFormula eq1 = (EqualityFormula)o1.getFormula();
				EqualityFormula eq2	= (EqualityFormula)o2.getFormula();
				return compare(eq1, eq2, signComp);
			}
			else return -1;
		}
		if (o2.getFormula() instanceof EqualityFormula) return 1;
		// from here, neither o1 nor o2 is an equality literal
		if (o1.getFormula() instanceof ArithmeticFormula) {
			if (o2.getFormula() instanceof ArithmeticFormula) {
				ArithmeticFormula a1 = (ArithmeticFormula)o1.getFormula();
				ArithmeticFormula a2 = (ArithmeticFormula)o2.getFormula();
				return compare(a1, a2, signComp);
			}
			else return -1;
		}
		if (o2.getFormula() instanceof ArithmeticFormula) return 1;
		// from here, neither o1 nor o2 is an arithmetic literal
		if (o1.getFormula() instanceof AbstractClause) {
			if (o2.getFormula() instanceof AbstractClause) {
				AbstractClause<?> c1 = (AbstractClause<?>)o1.getFormula();
				AbstractClause<?> c2 = (AbstractClause<?>)o2.getFormula();
				return compare(c1, c2, signComp);
			}
			else return -1;
		}
		if (o2.getFormula() instanceof AbstractClause) return 1;
		// from here, there is only quantified literals
		if (o1.getFormula() instanceof QuantifiedFormula) {
			if (o2.getFormula() instanceof QuantifiedFormula) {
				QuantifiedFormula c1 = (QuantifiedFormula)o1.getFormula();
				QuantifiedFormula c2 = (QuantifiedFormula)o2.getFormula();
				return compare(c1, c2, signComp);
			}
			else return -1;
		}
		if (o2.getFormula() instanceof QuantifiedFormula) return 1;
		
//		assert o1 instanceof QuantifiedFormula && o2 instanceof QuantifiedFormula;
		return compare((ISignedFormula)o1.getFormula(), (ISignedFormula)o2.getFormula());
		
	}
	
	private int compare(QuantifiedFormula p1, QuantifiedFormula p2, int sign) {
		if (p1.getLiteralDescriptor().getIndex() == p2.getLiteralDescriptor().getIndex()) {
			if (sign == 0) {
				// TODO enter predicate terms
				return 0;
			}
			else return sign;
		}
		else return p2.getLiteralDescriptor().getIndex()-p1.getLiteralDescriptor().getIndex();
	}
	
	private int compare(AbstractClause<?> c1, AbstractClause<?> c2, int sign) {
		return 0;
	}
	
	private int compare(ArithmeticFormula a1, ArithmeticFormula a2, int sign) {
		return 0;
	}
	
	private int compare(EqualityFormula eq1, EqualityFormula eq2, int sign) {
		if (eq1.getLiteralDescriptor().getSort() == eq2.getLiteralDescriptor().getSort()) {
			if (sign == 0) {
				// TODO enter predicate terms
				return 0;
			}
			else return sign;
		}
		else return eq1.getLiteralDescriptor().getSort().compareTo(eq2.getLiteralDescriptor().getSort());
	}
	
	private int compare(PredicateFormula p1, PredicateFormula p2, int sign) {
		if (p1.getLiteralDescriptor().getIndex() == p2.getLiteralDescriptor().getIndex()) {
			if (sign == 0) {
				// TODO enter predicate terms
				return 0;
			}
			else return sign;
		}
		else return p1.getLiteralDescriptor().getIndex()-p2.getLiteralDescriptor().getIndex();
	}
		
}
