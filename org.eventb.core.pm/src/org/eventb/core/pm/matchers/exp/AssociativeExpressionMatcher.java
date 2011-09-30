package org.eventb.core.pm.matchers.exp;

import org.eventb.core.ast.AssociativeExpression;
import org.eventb.core.ast.Expression;
import org.eventb.core.pm.ExpressionMatcher;
import org.eventb.core.pm.IBinding;
import org.eventb.core.pm.assoc.ACExpressionProblem;
import org.eventb.core.pm.assoc.AExpressionProblem;
import org.eventb.core.pm.assoc.AssociativityProblem;
import org.eventb.core.pm.basis.engine.MatchingUtilities;

/**
 * TODO better matching
 * @since 1.0
 * @author maamria
 *
 */
public class AssociativeExpressionMatcher extends ExpressionMatcher<AssociativeExpression> {

	public AssociativeExpressionMatcher() {
		super(AssociativeExpression.class);
	}

	@Override
	protected boolean gatherBindings(AssociativeExpression form,
			AssociativeExpression pattern, IBinding existingBinding){
		boolean isAC = MatchingUtilities.isAssociativeCommutative(form.getTag());
		// get the children
		Expression[] formChildren = form.getChildren();
		Expression[] patternChildren = pattern.getChildren();
		AssociativityProblem<Expression> problem = null;
		if (isAC){
			problem = new ACExpressionProblem(form.getTag(), formChildren, patternChildren, existingBinding); 
		}
		else {
			problem = new AExpressionProblem(form.getTag(), formChildren, patternChildren, existingBinding);
		}
		boolean partialMatchAcceptable = existingBinding.isPartialMatchAcceptable();
		IBinding solution = (IBinding) problem.solve(partialMatchAcceptable);
		if (solution != null){
			solution.makeImmutable();
			if (existingBinding.insertBinding(solution)){
				if(partialMatchAcceptable){
					existingBinding.setAssociativeExpressionComplement(solution.getAssociativeExpressionComplement());
				}
				return true;
			}
		}
		return false;
	}

	@Override
	protected AssociativeExpression getExpression(Expression e) {
		return (AssociativeExpression) e;
	}
	
}
