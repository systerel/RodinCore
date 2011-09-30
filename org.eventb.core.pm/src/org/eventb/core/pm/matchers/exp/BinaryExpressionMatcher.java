package org.eventb.core.pm.matchers.exp;

import org.eventb.core.ast.BinaryExpression;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.pm.ExpressionMatcher;
import org.eventb.core.pm.IBinding;

/**
 * @since 1.0
 * @author maamria
 *
 */
public class BinaryExpressionMatcher extends  ExpressionMatcher<BinaryExpression>{

	public BinaryExpressionMatcher(){
		super(BinaryExpression.class);
	}
	
	@Override
	protected boolean gatherBindings(BinaryExpression beForm,
			BinaryExpression bePattern, IBinding existingBinding){
		// for left's
		Expression fLeft = beForm.getLeft();
		Expression pLeft = bePattern.getLeft();
		if(pLeft instanceof FreeIdentifier){
			if(!existingBinding.putExpressionMapping((FreeIdentifier) pLeft, fLeft)){
				return false;
			}
		}
		else{
			if(!matchingFactory.match(fLeft, pLeft, existingBinding)){
				return false;
			}
		}
		// for right's
		Expression fRight = beForm.getRight();
		Expression pRight = bePattern.getRight();
		if(pRight instanceof FreeIdentifier){
			return existingBinding.putExpressionMapping((FreeIdentifier) pRight, fRight);
		}
		return matchingFactory.match(fRight, pRight, existingBinding);
		
	}

	@Override
	protected BinaryExpression getExpression(Expression e) {
		return (BinaryExpression) e;
	}

	

}
