/*******************************************************************************
 * Copyright (c) 2006, 2010 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - added MultiplePredicate processing (math V2)
 *******************************************************************************/
package org.eventb.ui.prover;

import java.util.Collections;
import java.util.List;

import org.eclipse.swt.graphics.Image;
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
import org.eventb.core.ast.MultiplePredicate;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.QuantifiedPredicate;
import org.eventb.core.ast.RelationalPredicate;
import org.eventb.core.ast.SimplePredicate;
import org.eventb.core.ast.SourceLocation;
import org.eventb.core.ast.UnaryExpression;
import org.eventb.core.ast.UnaryPredicate;
import org.eventb.core.seqprover.IProofTreeNode;
import org.eventb.core.seqprover.ITactic;

/**
 * @author htson
 *         <p>
 *         This class provides a default implementation for
 *         {@link org.eventb.ui.prover.ITacticProvider}. Plug-in writers should
 *         extends this class in order to provide their own tactic to the
 *         Proving User Interface.
 * @since 1.0
 */
public class DefaultTacticProvider implements ITacticProvider {

	/**
	 * @since 2.0
	 */
	public static class DefaultPositionApplication implements
			IPositionApplication {

		protected final Predicate hyp;
		protected final IPosition position;

		public DefaultPositionApplication(Predicate hyp, IPosition position) {
			this.hyp = hyp;
			this.position = position;
		}

		@Override
		public Point getHyperlinkBounds(String parsedString,
				Predicate parsedPredicate) {
			return TacticProviderUtils.getOperatorPosition(parsedPredicate,
					parsedString, position);
		}

		@Override
		public String getHyperlinkLabel() {
			return null;
		}

		/**
		 * This is the default implementation for finding the source location of
		 * position in predicate. Usually, plug-in writers do not need to
		 * override this method.
		 */
		public Point getOperatorPosition(Predicate predicate, String predStr) {
			Formula<?> subFormula = predicate.getSubFormula(position);
			if (subFormula instanceof AssociativePredicate) {
				// Return the operator between the first and second child
				Predicate[] children = ((AssociativePredicate) subFormula)
						.getChildren();
				return getOperatorPosition(predStr, children[0].getSourceLocation()
						.getEnd() + 1, children[1].getSourceLocation().getStart());
			}
			if (subFormula instanceof BinaryPredicate) {
				BinaryPredicate bPred = (BinaryPredicate) subFormula;
				SourceLocation leftLocation = bPred.getLeft().getSourceLocation();
				SourceLocation rightLocation = bPred.getRight().getSourceLocation();
				return getOperatorPosition(predStr, leftLocation.getEnd() + 1,
						rightLocation.getStart());
			}
			if (subFormula instanceof LiteralPredicate) {
				return makeAtomicPos(subFormula.getSourceLocation());
			}
			if (subFormula instanceof MultiplePredicate) {
				final MultiplePredicate mPred = (MultiplePredicate) subFormula;
				final Expression[] children = mPred.getChildren();
				return getOperatorPosition(predStr, subFormula.getSourceLocation()
						.getStart(), children[0].getSourceLocation().getStart());
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
			if (subFormula instanceof UnaryPredicate) {
				UnaryPredicate uPred = (UnaryPredicate) subFormula;
				Predicate child = uPred.getChild();
				return getOperatorPosition(predStr, subFormula.getSourceLocation()
						.getStart(), child.getSourceLocation().getStart());
			}
			if (subFormula instanceof AssociativeExpression) {
				// Return the operator between the first and second child
				Expression[] children = ((AssociativeExpression) subFormula)
						.getChildren();
				return getOperatorPosition(predStr, children[0].getSourceLocation()
						.getEnd() + 1, children[1].getSourceLocation().getStart());
			}
			if (subFormula instanceof AtomicExpression) {
				return makeAtomicPos(subFormula.getSourceLocation());
			}
			if (subFormula instanceof BinaryExpression) {
				BinaryExpression bExp = (BinaryExpression) subFormula;
				SourceLocation leftLocation = bExp.getLeft().getSourceLocation();
				SourceLocation rightLocation = bExp.getRight().getSourceLocation();
				return getOperatorPosition(predStr, leftLocation.getEnd() + 1,
						rightLocation.getStart());
			}
			if (subFormula instanceof UnaryExpression) {
				UnaryExpression uPred = (UnaryExpression) subFormula;
				if (uPred.getTag() == Expression.CONVERSE) {
					Expression child = uPred.getChild();
					return getOperatorPosition(predStr, child.getSourceLocation()
							.getEnd() + 1,
							subFormula.getSourceLocation().getEnd() + 1);
				}
				Expression child = uPred.getChild();
				return getOperatorPosition(predStr, subFormula.getSourceLocation()
						.getStart(), child.getSourceLocation().getStart());
			}
			if (subFormula instanceof SimplePredicate) {
				SimplePredicate sPred = (SimplePredicate) subFormula;
				Expression expression = sPred.getExpression();
				return getOperatorPosition(predStr, subFormula.getSourceLocation()
						.getStart(), expression.getSourceLocation().getStart());
			}
			if (subFormula instanceof BoolExpression) {
				return makeAtomicPos(subFormula.getSourceLocation());
			}
			if (subFormula instanceof BoundIdentifier) {
				return makeAtomicPos(subFormula.getSourceLocation());
			}
			if (subFormula instanceof FreeIdentifier) {
				return makeAtomicPos(subFormula.getSourceLocation());
			}
			return makeAtomicPos(subFormula.getSourceLocation());// The first character
		}

		private static Point makeAtomicPos(SourceLocation srcLoc) {
			return new Point(srcLoc.getStart(), srcLoc.getEnd() + 1);
		}

		/**
		 * An utility method to return the operator source location within the range
		 * (start, end).
		 * <p>
		 * 
		 * @param predStr
		 *            the actual predicate string.
		 * @param start
		 *            the starting index for searching.
		 * @param end
		 *            the last index for searching
		 * @return the location in the predicate string ignore the empty spaces or
		 *         brackets in the beginning and in the end.
		 */
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

		/**
		 * A private utility method to check if a character is either a space or a
		 * bracket.
		 * <p>
		 * 
		 * @param c
		 *            the character to check.
		 * @return <code>true</code> if the character is a space or bracket,
		 *         otherwise return <code>false</code>.
		 */
		private boolean isSpaceOrBracket(char c) {
			return (c == '\t' || c == '\n' || c == ' ' || c == '(' || c == ')');
		}

		@Override
		public ITactic getTactic(String[] inputs, String globalInput) {
			return null;
		}

		@Override
		public String getTacticID() {
			return null;
		}

	}

	/**
	 * @since 2.0
	 */
	public static class DefaultPredicateApplication implements
			IPredicateApplication {
		
		@Override
		public ITactic getTactic(String[] inputs, String globalInput) {
			return null;
		}

		@Override
		public String getTacticID() {
			return null;
		}

		@Override
		public Image getIcon() {
			return null;
		}
		
		@Override
		public String getTooltip() {
			return null;
		}

	}

	/**
	 * @since 2.0
	 */
	@Override
	public List<ITacticApplication> getPossibleApplications(
			IProofTreeNode node, Predicate hyp, String globalInput) {
		return Collections.emptyList();
	}

}