/*******************************************************************************
 * Copyright (c) 2006, 2014 ETH Zurich and others.
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
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.IPosition;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.SourceLocation;
import org.eventb.core.ast.extension.IFormulaExtension;
import org.eventb.core.ast.extension.IOperatorProperties.Notation;
import org.eventb.core.seqprover.IProofTreeNode;
import org.eventb.core.seqprover.ITactic;

/**
 * @author htson
 *         <p>
 *         This class provides a default implementation for
 *         {@link org.eventb.ui.prover.ITacticProvider}.
 *         </p>
 *         <p>
 *         Plug-in writers should extends this class in order to provide their
 *         own tactic to the Proving User Interface.
 *         </p>
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

		private static Notation getNotation(Formula<?> formula) {
			final int tag = formula.getTag();

			if (tag < Formula.FIRST_RELATIONAL_PREDICATE) {
				// free identifier
				// bound identifier declaration
				// bound identifier
				// integer literal
				// set extension
				// [assignment operators irrelevant here]
				// [predicate variable irrelevant here]
				return Notation.PREFIX;
			}
			if (tag < Formula.FIRST_ATOMIC_EXPRESSION) {
				// relational predicate
				// binary expression
				// binary predicate
				// associative expression
				// associative predicate
				return Notation.INFIX;
			}
			if (tag < Formula.FIRST_UNARY_EXPRESSION) {
				// bool expression
				// literal predicate
				// simple predicate
				// unary predicate
				return Notation.PREFIX;
			}
			if (tag == Formula.CONVERSE) {
				return Notation.POSTFIX;
			}
			if (tag < Formula.FIRST_EXTENSION_TAG) {
				// unary expression except CONVERSE
				// quantified expression
				// quantified predicate
				// multiple predicate
				return Notation.PREFIX;
			}

			// all extensions
			final IFormulaExtension extension = formula.getFactory()
					.getExtension(tag);
			return extension.getKind().getProperties().getNotation();
		}

		/**
		 * This is the default implementation for finding the source location of
		 * position in predicate. Usually, plug-in writers do not need to
		 * override this method.
		 */
		public Point getOperatorPosition(Predicate predicate, String predStr) {
			final Formula<?> subFormula = predicate.getSubFormula(position);
			final int childCount = subFormula.getChildCount();
			final SourceLocation subLoc = subFormula.getSourceLocation();

			if (childCount == 0) {
				return getOperatorPosition(predStr, subLoc.getStart(),
						subLoc.getEnd() + 1);
			}

			final Notation notation = getNotation(subFormula);
			final SourceLocation firstLoc = subFormula.getChild(0)
					.getSourceLocation();

			switch (notation) {
			case PREFIX:
				return getOperatorPosition(predStr, subLoc.getStart(),
						firstLoc.getStart());
			case INFIX:
				final SourceLocation secondLoc = subFormula.getChild(1)
						.getSourceLocation();
				return getOperatorPosition(predStr, firstLoc.getEnd() + 1,
						secondLoc.getStart());
			case POSTFIX:
				final SourceLocation lastLoc = subFormula.getChild(
						childCount - 1).getSourceLocation();
				return getOperatorPosition(predStr, lastLoc.getEnd() + 1,
						subLoc.getEnd() + 1);

			default:
				throw new IllegalStateException("Unsupported notation: "
						+ notation + " for root operator of: " + subFormula);
			}
		}

		/**
		 * An utility method to return the operator source location within the
		 * range (start, end).
		 * <p>
		 * 
		 * @param predStr
		 *            the actual predicate string.
		 * @param start
		 *            the starting index for searching.
		 * @param end
		 *            the last index for searching
		 * @return the location in the predicate string ignore the empty spaces
		 *         or brackets in the beginning and in the end.
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
		 * A private utility method to check if a character is either a space or
		 * a bracket.
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
