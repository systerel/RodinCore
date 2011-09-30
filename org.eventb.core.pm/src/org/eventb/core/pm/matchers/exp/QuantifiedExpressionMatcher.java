package org.eventb.core.pm.matchers.exp;

import org.eventb.core.ast.BoundIdentDecl;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.PredicateVariable;
import org.eventb.core.ast.QuantifiedExpression;
import org.eventb.core.pm.ExpressionMatcher;
import org.eventb.core.pm.IBinding;
import org.eventb.core.pm.basis.engine.MatchingUtilities;

/**
 * TODO FIXME this is good when we only have one quantifier variable.
 * @since 1.0
 * @author maamria
 *
 */
public class QuantifiedExpressionMatcher extends  ExpressionMatcher<QuantifiedExpression>{

	public QuantifiedExpressionMatcher(){
		super(QuantifiedExpression.class);
	}
	
	@Override
	protected boolean gatherBindings(QuantifiedExpression qeForm,
			QuantifiedExpression qePattern, IBinding existingBinding){
		if(qeForm.getTag() == Formula.CSET){
			if(qeForm.getForm() != qePattern.getForm()){
				return false;
			}
		}
		BoundIdentDecl[] fDec = qeForm.getBoundIdentDecls();
		BoundIdentDecl[] pDec = qePattern.getBoundIdentDecls();
		if(!MatchingUtilities.boundIdentDecsMatch(fDec, pDec, existingBinding)){
			return false;
		}
		Expression fExp = qeForm.getExpression();
		Expression pExp = qePattern.getExpression();
		if(pExp instanceof FreeIdentifier){
			if(!existingBinding.putExpressionMapping((FreeIdentifier) pExp, fExp)){
				return false;
			}
		}
		else {
			if(!matchingFactory.match(fExp, pExp, existingBinding)){
				return false;
			}
		}
		Predicate fPred = qeForm.getPredicate();
		Predicate pPred = qePattern.getPredicate();
		if(pPred instanceof PredicateVariable){
			return existingBinding.putPredicateMapping((PredicateVariable) pPred, fPred);
		}
		return matchingFactory.match(fPred, pPred, existingBinding);
	}

	@Override
	protected QuantifiedExpression getExpression(Expression e) {
		return (QuantifiedExpression) e;
	}
}
