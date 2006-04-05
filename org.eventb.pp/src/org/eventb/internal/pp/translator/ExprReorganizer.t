package org.eventb.internal.pp.translator;

import java.util.LinkedList;

import org.eventb.core.ast.BoolExpression;
import org.eventb.core.ast.BoundIdentDecl;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.QuantifiedPredicate;
import org.eventb.core.ast.SourceLocation;
import org.eventb.core.ast.UnaryExpression;

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
		%match(Predicate pred) {
			Equal(Identifier(), Card(_)) | Equal(Identifier(), Bool(_))-> {
				return identityTranslate(pred, ff);
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
			_ -> {
				return super.translate(expr, ff);
			}
		}
	}
}
