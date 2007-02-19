package org.eventb.ui.prover;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.graphics.Point;
import org.eventb.core.ast.AssociativeExpression;
import org.eventb.core.ast.AssociativePredicate;
import org.eventb.core.ast.AtomicExpression;
import org.eventb.core.ast.BinaryExpression;
import org.eventb.core.ast.BinaryPredicate;
import org.eventb.core.ast.BoolExpression;
import org.eventb.core.ast.BoundIdentDecl;
import org.eventb.core.ast.BoundIdentifier;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.IPosition;
import org.eventb.core.ast.LiteralPredicate;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.QuantifiedPredicate;
import org.eventb.core.ast.RelationalPredicate;
import org.eventb.core.ast.SimplePredicate;
import org.eventb.core.ast.SourceLocation;
import org.eventb.core.ast.UnaryPredicate;
import org.eventb.core.seqprover.IProofTreeNode;
import org.eventb.core.seqprover.ITactic;

public class DefaultTacticProvider implements ITacticProvider {

	public List<IPosition> getApplicablePositions(IProofTreeNode node,
			Predicate hyp, String input) {
		if (isApplicable(node, hyp, input))
			return new ArrayList<IPosition>();
		return null;
	}

	public ITactic getTactic(IProofTreeNode node, Predicate hyp,
			IPosition position, String[] inputs) {
		return null;
	}

	/**
	 * @deprecated Use
	 *             {@link #getApplicablePositions(IProofTreeNode,Predicate,String)}
	 *             instead
	 */
	@Deprecated
	public boolean isApplicable(IProofTreeNode node, Predicate hyp, String input) {
		return false;
	}

	public Point getOperatorPosition(Predicate predicate, String predStr,
			IPosition position) {
		Formula subFormula = predicate.getSubFormula(position);
		if (subFormula instanceof AssociativePredicate) {
			return new Point(0, 1);
		}
		if (subFormula instanceof BinaryPredicate) {
			BinaryPredicate bPred = (BinaryPredicate) subFormula;
			SourceLocation leftLocation = bPred.getLeft().getSourceLocation();
			SourceLocation rightLocation = bPred.getRight().getSourceLocation();
			return getOperatorPosition(predStr, leftLocation.getEnd() + 1,
					rightLocation.getStart());
		}
		if (subFormula instanceof LiteralPredicate) {
			return new Point(0, 1);
		}
		if (subFormula instanceof QuantifiedPredicate) {
			QuantifiedPredicate qPred = (QuantifiedPredicate) subFormula;
			BoundIdentDecl[] boundIdentDecls = qPred.getBoundIdentDecls();
			int index = boundIdentDecls[0].getSourceLocation().getStart();
			return getOperatorPosition(predStr, 0, index);
		}
		if (subFormula instanceof RelationalPredicate) {
			RelationalPredicate rPred = (RelationalPredicate) subFormula;
			Expression left = rPred.getLeft();
			Expression right = rPred.getRight();
			return getOperatorPosition(predStr, left.getSourceLocation()
					.getEnd() + 1, right.getSourceLocation().getStart());
		}
		if (subFormula instanceof SimplePredicate) {
			return new Point(0, 1);
		}
		if (subFormula instanceof UnaryPredicate) {
			UnaryPredicate uPred = (UnaryPredicate) subFormula;
			Predicate child = uPred.getChild();
			return getOperatorPosition(predStr, subFormula.getSourceLocation().getStart(), child.getSourceLocation()
					.getStart());
		}
		if (subFormula instanceof AssociativeExpression) {
			return new Point(0, 1);
		}
		if (subFormula instanceof AtomicExpression) {
			return new Point(0, 1);
		}
		if (subFormula instanceof BinaryExpression) {
			return new Point(0, 1);
		}
		if (subFormula instanceof BoolExpression) {
			return new Point(0, 1);
		}
		if (subFormula instanceof BoundIdentifier) {
			return new Point(0, 1);
		}
		if (subFormula instanceof FreeIdentifier) {
			return new Point(0, 1);
		}
		return new Point(0, 1);// The first character
	}

	protected Point getOperatorPosition(String predStr, int start, int end) {
		int i = start;
		int x = start;
		int y;
		boolean letter = false;
		while (i < end) {
			char c = predStr.charAt(i);
			if (letter == false && !isSpaceOrBracket(c)) {
				x = i;
				letter = true;
			} else if (letter == true && isSpaceOrBracket(c)) {
				y = i;
				return new Point(x, y);
			}
			++i;
		}
		if (letter == true)
			return new Point(x, end);
		else
			return new Point(start, end);
	}

	private boolean isSpaceOrBracket(char c) {
		return (c == ' ' || c == '(' || c == ')');
	}

}
