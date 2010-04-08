/*******************************************************************************
 * Copyright (c) 2006, 2009 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - mathematical language V2
 *******************************************************************************/
package org.eventb.internal.ui.prover;

import java.util.ArrayList;

import org.eventb.core.ast.AssociativePredicate;
import org.eventb.core.ast.BinaryPredicate;
import org.eventb.core.ast.BoundIdentDecl;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.LiteralPredicate;
import org.eventb.core.ast.MultiplePredicate;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.QuantifiedPredicate;
import org.eventb.core.ast.RelationalPredicate;
import org.eventb.core.ast.SimplePredicate;
import org.eventb.core.ast.SourceLocation;
import org.eventb.core.ast.UnaryPredicate;

public class PredicateUtil {

	private static final int tab = 2;

	public static String prettyPrint(int maxLen, String predString,
			Predicate pred) {
		return prettyPrint(maxLen, predString, pred, 0);
	}

	private static String prettyPrint(int maxLen, String predString,
			Predicate pred, int indent) {

		String str = addSpacing(predString, pred);
		if (str.length() <= maxLen - indent)
			return printSpace(indent) + str;
		
		// Doesn't fit the line length constraint
		String result = "";
		if (pred instanceof AssociativePredicate) {
			AssociativePredicate aPred = (AssociativePredicate) pred;
			String op = "";
			int tag = aPred.getTag();
			if (tag == Predicate.LAND)
				op = "\u2227";
			else if (tag == Predicate.LOR)
				op = "\u2228";
			ArrayList<Predicate> currentChildren = new ArrayList<Predicate>();
			String oldString = "";
			String currentString = "";
			String tmpString = "";
			int currentHeight = -1;
			int i = 0;

			Predicate[] children = aPred.getChildren();
			for (Predicate child : children) {
				i++;
				oldString = currentString;
				int height = getHeight(child);
				if (height > currentHeight) {
					currentString = recalculate(predString, currentChildren,
							op, height);
					currentHeight = height;
				}
				tmpString = addSpacing(predString, child, currentHeight);

				if (i != children.length) {
					tmpString += printSpace(currentHeight + 1) + op;
				}

				String newString;
				if (currentString != "")
					newString = currentString + printSpace(currentHeight + 1)
							+ tmpString;
				else
					newString = tmpString;

				if (i == children.length) {
					if (newString.length() <= maxLen - indent) {
						result += printSpace(indent) + newString;
					} else {
						if (oldString != "")
							result += printSpace(indent) + oldString + "\n";
						result += prettyPrint(maxLen, predString, child,
								indent);
					}
				}

				else {
					if (newString.length() <= maxLen - indent) {
						currentString = newString;
						currentChildren.add(child);
					} else if (oldString == "") {
						result += prettyPrint(maxLen, predString, child,
								indent);
						result += printSpace(indent) + op + "\n";
						currentString = "";
						currentChildren.clear();
						currentHeight = -1;
					} else {
						result += printSpace(indent) + oldString + "\n";
						currentHeight = getHeight(child);
						currentChildren.clear();
						currentChildren.add(child);
						currentString =
							addSpacing(predString, child, currentHeight)
							+ printSpace(currentHeight + 1) + op;
					}
				}

			}
		}

		else if (pred instanceof BinaryPredicate) {
			BinaryPredicate bPred = (BinaryPredicate) pred;
			String op = "";
			int tag = bPred.getTag();
			if (tag == Predicate.LIMP)
				op = "\u21d2";
			else if (tag == Predicate.LEQV)
				op = "\u21d4";
			Predicate left = bPred.getLeft();
			Predicate right = bPred.getRight();
			result = prettyPrint(maxLen, predString, left, indent + tab)
					+ "\n"
					+ printSpace(indent)
					+ op
					+ "\n"
					+ prettyPrint(maxLen, predString, right, indent + tab);
		}

		else if (pred instanceof LiteralPredicate) {
			result = printSpace(indent) + str;
		}

		else if (pred instanceof MultiplePredicate) {
			result = printSpace(indent) + str;
		}

		else if (pred instanceof QuantifiedPredicate) {
			QuantifiedPredicate qPred = (QuantifiedPredicate) pred;
			int tag = qPred.getTag();
			String op = "";
			if (tag == Predicate.FORALL)
				op = "\u2200";
			else if (tag == Predicate.EXISTS)
				op = "\u2203";
			BoundIdentDecl[] decls = qPred.getBoundIdentDecls();
			Predicate predicate = qPred.getPredicate();

			result = printSpace(indent) + op + " "
					+ getDeclsImage(decls, predString) + " \u00b7 "
					+ "\n"
					+ prettyPrint(maxLen, predString, predicate, indent + tab);
		}

		else if (pred instanceof RelationalPredicate) {
			result = printSpace(indent) + str;
		}

		else if (pred instanceof SimplePredicate) {
			result = printSpace(indent) + str;
		}

		else if (pred instanceof UnaryPredicate) {
			UnaryPredicate uPred = (UnaryPredicate) pred;
			int tag = uPred.getTag();
			String op = "";
			if (tag == Predicate.NOT)
				op = "\u00ac";

			Predicate child = uPred.getChild();
			result = printSpace(indent)
					+ op
					+ "\n"
					+ prettyPrint(maxLen, predString, child, indent + tab);
		}

		if (hasBrackets(pred, predString)) {
			result = "(" + result + ')';
		}
		if (ProverUIUtils.DEBUG) {
			ProverUIUtils.debug("Pred: " + pred);
			ProverUIUtils.debug("Result: \n" + result);
		}
		return result;
	}

	private static <T extends Formula<T>> String getImage(T formula,
			String string) {

		final SourceLocation loc = formula.getSourceLocation();
		final String image = string.substring(loc.getStart(), loc.getEnd() + 1);
		if (hasBrackets(formula, string)) {
			return "(" + image + ')';
		}
		return image;
	}

	private static String getDeclsImage(BoundIdentDecl[] decls, String string) {
		final StringBuilder builder = new StringBuilder();
		String sep = "";
		for (BoundIdentDecl decl: decls) {
			builder.append(sep);
			sep = ", ";
			builder.append(getImage(decl, string));
		}
		return builder.toString();
	}

	/**
	 * Tells whether the given sub-formula occurs surrounded by brackets in the
	 * given string. The source location of the given sub-formula must originate
	 * from the given string.
	 * 
	 * @param formula
	 *            a sub-formula
	 * @param string
	 *            a string where the given sub-formula has been parsed from
	 * @return <code>true</code> iff the given sub-formula is surrounded by
	 *         brackets
	 */
	private static <T extends Formula<T>> boolean hasBrackets(T formula,
			String string) {

		final SourceLocation loc = formula.getSourceLocation();
		final int before = loc.getStart() - 1;
		final int after = loc.getEnd() + 1;
		if (0 <= before && after < string.length()) {
			return string.charAt(before) == '(' && string.charAt(after) == ')';
		}
		return false;
	}

	// Assume that there are more children after
	private static String recalculate(String predString,
			ArrayList<Predicate> currentChildren, String op, int height) {
		String result = "";
		int i = 0;
		for (Predicate child : currentChildren) {
			if (i != 0)
				result += printSpace(height + 1);
			result += addSpacing(predString, child, height);
			result += printSpace(height + 1) + op;
			i++;
		}
		return result;
	}

	// Made public for testing purposes.
	public static String addSpacing(String predString, Predicate pred) {
		return addSpacing(predString, pred, getHeight(pred));
	}

	private static String addSpacing(String predString, Predicate pred,
			int height) {

		String result = "";

		if (pred instanceof AssociativePredicate) {
			AssociativePredicate aPred = (AssociativePredicate) pred;
			String op = "";
			int tag = aPred.getTag();
			if (tag == Predicate.LAND)
				op = "\u2227";
			else if (tag == Predicate.LOR)
				op = "\u2228";
			int i = 0;
			for (Predicate child : aPred.getChildren()) {
				if (i != 0)
					result += addSpacing(op, height);
				result += addSpacing(predString, child, height - 1);
				i++;
			}
		}

		else if (pred instanceof BinaryPredicate) {
			BinaryPredicate bPred = (BinaryPredicate) pred;
			String op = "";
			int tag = bPred.getTag();
			if (tag == Predicate.LIMP)
				op = "\u21d2";
			else if (tag == Predicate.LEQV)
				op = "\u21d4";
			result = addSpacing(predString, bPred.getLeft(), height - 1)
					+ addSpacing(op, height)
					+ addSpacing(predString, bPred.getRight(), height - 1);
		}

		else if (pred instanceof LiteralPredicate) {
			result = getImage(pred, predString);
		}

		else if (pred instanceof MultiplePredicate) {
			result = getImage(pred, predString);
		}

		else if (pred instanceof QuantifiedPredicate) {
			final QuantifiedPredicate qPred = (QuantifiedPredicate) pred;

			final String op;
			final int tag = qPred.getTag();
			if (tag == Predicate.FORALL)
				op = "\u2200";
			else if (tag == Predicate.EXISTS)
				op = "\u2203";
			else
				op = "";
			
			final BoundIdentDecl[] decls = qPred.getBoundIdentDecls();
			final Predicate child = qPred.getPredicate();
			result = op + " " + getDeclsImage(decls, predString) + " \u00b7 "
					+ addSpacing(predString, child, height);
		}

		else if (pred instanceof RelationalPredicate) {
			RelationalPredicate rPred = (RelationalPredicate) pred;

			int tag = rPred.getTag();
			String op = "";
			if (tag == Predicate.EQUAL)
				op = "=";
			else if (tag == Predicate.NOTEQUAL)
				op = "\u2260";
			else if (tag == Predicate.LT)
				op = "<";
			else if (tag == Predicate.LE)
				op = "\u2264";
			else if (tag == Predicate.GT)
				op = ">";
			else if (tag == Predicate.GE)
				op = "\u2265";
			else if (tag == Predicate.IN)
				op = "\u2208";
			else if (tag == Predicate.NOTIN)
				op = "\u2209";
			else if (tag == Predicate.SUBSET)
				op = "\u2282";
			else if (tag == Predicate.NOTSUBSET)
				op = "\u2284";
			else if (tag == Predicate.SUBSETEQ)
				op = "\u2286";
			else if (tag == Predicate.NOTSUBSETEQ)
				op = "\u2288";

			final String imageLeft = getImage(rPred.getLeft(), predString);
			final String imageRight = getImage(rPred.getRight(), predString);
			result = imageLeft + addSpacing(op, height) + imageRight;
		}

		else if (pred instanceof SimplePredicate) {
			result = getImage(pred, predString);
		}

		else if (pred instanceof UnaryPredicate) {
			UnaryPredicate uPred = (UnaryPredicate) pred;
			int tag = uPred.getTag();
			String op = "";
			if (tag == Predicate.NOT)
				op = "\u00ac";

			result = addSpacing(op, height)
					+ addSpacing(predString, uPred.getChild(), height - 1);
		}

		if (hasBrackets(pred, predString)) {
			result = "(" + result + ')';
		}
		return result;
	}

	private static String addSpacing(String op, int height) {
		final StringBuilder builder = new StringBuilder();
		for (int i = 0; i < height; i++)
			builder.append(' ');
		builder.append(op);
		for (int i = 0; i < height; i++)
			builder.append(' ');
		return builder.toString();
	}

	private static String printSpace(int indent) {
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < indent; i++)
			builder.append(' ');
		return builder.toString();
	}

	private static int getHeight(Predicate pred) {
		if (pred instanceof AssociativePredicate) {
			int maxHeight = 0;
			AssociativePredicate associativePred = (AssociativePredicate) pred;
			for (Predicate child : associativePred.getChildren()) {
				int height = getHeight(child);
				maxHeight = height > maxHeight ? height : maxHeight;
			}
			return maxHeight + 1;
		}

		if (pred instanceof BinaryPredicate) {
			int leftHeight = getHeight(((BinaryPredicate) pred).getLeft());
			int rightHeight = getHeight(((BinaryPredicate) pred).getRight());
			return leftHeight > rightHeight ? leftHeight + 1 : rightHeight + 1;
		}

		if (pred instanceof LiteralPredicate) {
			return 0;
		}

		if (pred instanceof QuantifiedPredicate) {
			return getHeight(((QuantifiedPredicate) pred).getPredicate());
		}

		if (pred instanceof RelationalPredicate) {
			// TODO Get the height of the expression?
			return 0;
		}

		if (pred instanceof SimplePredicate) {
			// TODO Get the height of the expression?
			return 0;
		}

		if (pred instanceof UnaryPredicate) {
			return getHeight(((UnaryPredicate) pred).getChild()) + 1;
		}

		return 0;
	}

}
