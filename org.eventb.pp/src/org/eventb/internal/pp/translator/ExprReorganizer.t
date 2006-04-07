package org.eventb.internal.pp.translator;

import java.util.*;
import java.math.*;

import org.eventb.core.ast.*;

public class ExprReorganizer extends Sub2QuantTranslator {

	private ExprReorganizer(){}
	
	public static Predicate reorganize(Predicate P, FormulaFactory ff) {
		return Sub2QuantTranslator.translate(P, new ExprReorganizer(), ff);
	}
	
	@Override
	protected Sub2QuantTranslator create() {
		return new ExprReorganizer();
	}

%include {Formula.tom}

	@Override
	protected Predicate translate(Predicate pred, FormulaFactory ff) {
		SourceLocation loc = pred.getSourceLocation();
		
		%match(Predicate pred) {
			Equal(ident@Identifier(), Card(S)) | Equal(Card(S), ident@Identifier())-> {
				Expression newS = translate(`S, ff);
				if(newS == `S)
					return pred;
				else
					return ff.makeRelationalPredicate(
						Formula.EQUAL,
						`ident,
						ff.makeUnaryExpression(Formula.KCARD, newS, loc),
						loc);
			}
			Equal(ident@Identifier(), Bool(P)) | Equal(Bool(P), ident@Identifier()) -> {
				Predicate newP = translate(`P, ff);
				if(newP == `P)
					return pred;
				else
					return ff.makeRelationalPredicate(
						Formula.EQUAL,
						`ident,
						ff.makeBoolExpression(newP, loc),
						loc);
			}/*
			RelationalPredicate(E1, FunImage(r, E2)) {
				Expression newE1 = translate(`E1, ff);
				Expression newE2 = translate(`E2, ff);
				Expression nr = translate(`r, ff);
				
				if(newE1 == `E1 && newE2 == `E2 && nr == `r)
					return pred;
				else
					return ff.makeRelationalPredicate(
						pred.getTag(),
						E,
						ff.makeBoolExpression(newP, loc),
						loc);*/
			}
			
			
			_-> {
				return super.translate(pred, ff);
			}
		}		
	}
	
	@Override
	protected Expression translate(Expression expr, FormulaFactory ff) {
		%match(Expression expr) {
			Card(_) | Bool(_) -> {
				return bindExpression(expr, ff);
			}
			FunImage(_) -> {
				if(expr.getType().getBaseType() != null)
					return super.translate(expr, ff);
				else
					return bindExpression(expr, ff);
			}
			_ -> {
				return super.translate(expr, ff);
			}
		}
	}
}
