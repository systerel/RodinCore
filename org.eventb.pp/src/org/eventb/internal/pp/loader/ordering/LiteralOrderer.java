package org.eventb.internal.pp.loader.ordering;

import java.util.Comparator;

import org.eventb.internal.pp.loader.formula.AbstractClause;
import org.eventb.internal.pp.loader.formula.ArithmeticLiteral;
import org.eventb.internal.pp.loader.formula.EqualityLiteral;
import org.eventb.internal.pp.loader.formula.ISignedFormula;
import org.eventb.internal.pp.loader.formula.PredicateLiteral;
import org.eventb.internal.pp.loader.formula.QuantifiedLiteral;

public class LiteralOrderer implements Comparator<ISignedFormula> {

	public int compare(ISignedFormula o1, ISignedFormula o2) {
//		if (o1.equals(o2)) return 0;
			
//		System.out.println(o1.getFormula().getClass());
//		System.out.println(o2.getFormula().getClass());
		
		int signComp = -new Boolean(o1.isPositive()).compareTo(o2.isPositive());
		if (o1.getFormula() instanceof PredicateLiteral) {
			if (o2.getFormula() instanceof PredicateLiteral) {
				PredicateLiteral pred1 = (PredicateLiteral)o1.getFormula();
				PredicateLiteral pred2 = (PredicateLiteral)o2.getFormula();
				return compare(pred1, pred2, signComp);	
			}
			else return -1;
		}
		if (o2.getFormula() instanceof PredicateLiteral) return 1;
		// from here, neither o1 nor o2 is a predicate literal
		if (o1.getFormula() instanceof EqualityLiteral) {
			if (o2.getFormula() instanceof EqualityLiteral) {
				EqualityLiteral eq1 = (EqualityLiteral)o1.getFormula();
				EqualityLiteral eq2	= (EqualityLiteral)o2.getFormula();
				return compare(eq1, eq2, signComp);
			}
			else return -1;
		}
		if (o2.getFormula() instanceof EqualityLiteral) return 1;
		// from here, neither o1 nor o2 is an equality literal
		if (o1.getFormula() instanceof ArithmeticLiteral) {
			if (o2.getFormula() instanceof ArithmeticLiteral) {
				ArithmeticLiteral a1 = (ArithmeticLiteral)o1.getFormula();
				ArithmeticLiteral a2 = (ArithmeticLiteral)o2.getFormula();
				return compare(a1, a2, signComp);
			}
			else return -1;
		}
		if (o2.getFormula() instanceof ArithmeticLiteral) return 1;
		// from here, neither o1 nor o2 is an arithmetic literal
		if (o1.getFormula() instanceof AbstractClause) {
			if (o2.getFormula() instanceof AbstractClause) {
				AbstractClause c1 = (AbstractClause)o1.getFormula();
				AbstractClause c2 = (AbstractClause)o2.getFormula();
				return compare(c1, c2, signComp);
			}
			else return -1;
		}
		if (o2.getFormula() instanceof AbstractClause) return 1;
		// from here, there is only quantified literals
		if (o1.getFormula() instanceof QuantifiedLiteral) {
			if (o2.getFormula() instanceof QuantifiedLiteral) {
				QuantifiedLiteral c1 = (QuantifiedLiteral)o1.getFormula();
				QuantifiedLiteral c2 = (QuantifiedLiteral)o2.getFormula();
				return compare(c1, c2, signComp);
			}
			else return -1;
		}
		if (o2.getFormula() instanceof QuantifiedLiteral) return 1;
		
//		assert o1 instanceof QuantifiedLiteral && o2 instanceof QuantifiedLiteral;
		return compare((ISignedFormula)o1.getFormula(), (ISignedFormula)o2.getFormula());
		
	}
	
	private int compare(QuantifiedLiteral p1, QuantifiedLiteral p2, int sign) {
		if (p1.getLiteralDescriptor().getIndex() == p2.getLiteralDescriptor().getIndex()) {
			if (sign == 0) {
				// TODO enter predicate terms
				return 0;
			}
			else return sign;
		}
		else return p2.getLiteralDescriptor().getIndex()-p1.getLiteralDescriptor().getIndex();
	}
	
	private int compare(AbstractClause c1, AbstractClause c2, int sign) {
		return 0;
	}
	
	private int compare(ArithmeticLiteral a1, ArithmeticLiteral a2, int sign) {
		return 0;
	}
	
	private int compare(EqualityLiteral eq1, EqualityLiteral eq2, int sign) {
		if (eq1.getLiteralDescriptor().getSort() == eq2.getLiteralDescriptor().getSort()) {
			if (sign == 0) {
				// TODO enter predicate terms
				return 0;
			}
			else return sign;
		}
		else return eq1.getLiteralDescriptor().getSort().compareTo(eq2.getLiteralDescriptor().getSort());
	}
	
	private int compare(PredicateLiteral p1, PredicateLiteral p2, int sign) {
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
