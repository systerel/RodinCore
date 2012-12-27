/*******************************************************************************
 * Copyright (c) 2006, 2012 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - mathematical language V2
 *     Systerel - extracted computation of predicate height
 *     Systerel - refactored to use StringBuilder instead of String concat
 *******************************************************************************/
package org.eventb.internal.ui.prover;

import static org.eventb.internal.ui.prover.ProverUIUtils.appendTabs;
import static org.eventb.internal.ui.utils.PredicateHeightComputer.getHeight;

import java.util.ArrayList;

import org.eventb.core.ast.AssociativePredicate;
import org.eventb.core.ast.BinaryPredicate;
import org.eventb.core.ast.BoundIdentDecl;
import org.eventb.core.ast.ExtendedPredicate;
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
	private static final String SPACES = "                              ";
	private static final int SPACES_LENGTH = SPACES.length();
	
	
	public static String prettyPrint(int maxLen, String predString,
			Predicate pred) {
		return prettyPrint(maxLen, predString, pred, 0);
	}
	
	public static String prettyPrint(int maxLen, String predString,
			Predicate pred, int leftTabs) {
		final StringBuilder sb = new StringBuilder(predString.length());
		prettyPrint(sb, maxLen, predString, pred, 0, leftTabs);
		return sb.toString();
	}

	private static void prettyPrint(StringBuilder sb, int maxLen,
			String predString, Predicate pred, int indent, int leftTabs) {

		final int start = sb.length();
		appendSpaces(sb, indent);
		appendPredicate(sb, predString, pred);
		if (sb.length() - start <= maxLen) {
			return;
		}
		
		// Doesn't fit the line length constraint, restart
		final String str = sb.substring(start + indent);
		sb.setLength(start);
		final boolean bracketed = hasBrackets(pred, predString);
		if (bracketed) {
			sb.append('(');
		}

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
			int currentHeight = -1;
			int i = 0;

			Predicate[] children = aPred.getChildren();
			for (Predicate child : children) {
				i++;
				oldString = currentString;
				final String newString;
				{
					final StringBuilder sb2 = new StringBuilder();
					int height = getHeight(child);
					if (height > currentHeight) {
						recalculate(sb2, predString, currentChildren, op,
								height);
						currentHeight = height;
					} else {
						sb2.append(currentString);
					}
					if (sb2.length() != 0) {
						appendSpaces(sb2, currentHeight + 1);
					}
					addSpacing(sb2, predString, child, currentHeight);

					if (i != children.length) {
						appendSpaces(sb2, currentHeight + 1);
						sb2.append(op);
					}
					newString = sb2.toString();
				}

				if (i == children.length) {
					if (newString.length() <= maxLen - indent) {
						appendSpaces(sb, indent);
						sb.append(newString);
					} else {
						if (oldString != "") {
							appendSpaces(sb, indent);
							sb.append(oldString);
							sb.append('\n');
							appendTabs(sb, leftTabs);
						}
						prettyPrint(sb, maxLen, predString, child, indent, leftTabs);
					}
				}

				else {
					if (newString.length() <= maxLen - indent) {
						currentString = newString;
						currentChildren.add(child);
					} else if (oldString == "") {
						prettyPrint(sb, maxLen, predString, child, indent, leftTabs);
						appendSpaces(sb, indent);
						sb.append(op);
						sb.append('\n');
						appendTabs(sb, leftTabs);
						currentString = "";
						currentChildren.clear();
						currentHeight = -1;
					} else {
						appendSpaces(sb, indent);
						sb.append(oldString);
						sb.append('\n');
						appendTabs(sb, leftTabs);
						currentHeight = getHeight(child);
						currentChildren.clear();
						currentChildren.add(child);
						{
							final StringBuilder sb2 = new StringBuilder();
							addSpacing(sb2, predString, child, currentHeight);
							appendSpaces(sb2, currentHeight + 1);
							sb2.append(op);
							currentString = sb2.toString();
						}
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
			prettyPrint(sb, maxLen, predString, bPred.getLeft(), indent + tab, leftTabs);
			sb.append('\n');
			appendTabs(sb, leftTabs);
			appendSpaces(sb, indent);
			sb.append(op);
			sb.append('\n');
			appendTabs(sb, leftTabs);
			prettyPrint(sb, maxLen, predString, bPred.getRight(), indent + tab, leftTabs);
		}

		else if (pred instanceof LiteralPredicate) {
			appendSpaces(sb, indent);
			sb.append(str);
		}

		else if (pred instanceof MultiplePredicate) {
			appendSpaces(sb, indent);
			sb.append(str);
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

			appendSpaces(sb, indent);
			sb.append(op);
			sb.append(' ');
			appendDeclsImage(sb, decls, predString);
			sb.append(" \u00b7 \n");
			appendTabs(sb, leftTabs);
			prettyPrint(sb, maxLen, predString, predicate, indent + tab, leftTabs);
		}

		else if (pred instanceof RelationalPredicate) {
			appendSpaces(sb, indent);
			sb.append(str);
		}

		else if (pred instanceof SimplePredicate) {
			appendSpaces(sb, indent);
			sb.append(str);
		}

		else if (pred instanceof UnaryPredicate) {
			UnaryPredicate uPred = (UnaryPredicate) pred;
			int tag = uPred.getTag();
			String op = "";
			if (tag == Predicate.NOT)
				op = "\u00ac";

			Predicate child = uPred.getChild();
			appendSpaces(sb, indent);
			sb.append(op);
			sb.append('\n');
			appendTabs(sb, leftTabs);
			prettyPrint(sb, maxLen, predString, child, indent + tab, leftTabs);
		}
		// TODO refactoring needed here (default case)
		else if (pred instanceof ExtendedPredicate) {
			appendSpaces(sb, indent);
			sb.append(str);
		}


		if (bracketed) {
			sb.append(')');
		}
		if (ProverUIUtils.DEBUG) {
			ProverUIUtils.debug("Pred: " + pred);
			ProverUIUtils.debug("Result: \n" + sb.substring(start));
		}
	}

	private static void appendImage(StringBuilder sb, Formula<?> formula,
			String string) {

		final boolean bracketed = hasBrackets(formula, string);
		if (bracketed) {
			sb.append('(');
		}
		final SourceLocation loc = formula.getSourceLocation();
		sb.append(string, loc.getStart(), loc.getEnd()+1); //+1 ?
		if (bracketed) {
			sb.append(')');
		}
	}

	private static void appendDeclsImage(StringBuilder sb,
			BoundIdentDecl[] decls, String string) {
		String sep = "";
		for (BoundIdentDecl decl: decls) {
			sb.append(sep);
			sep = ", ";
			appendImage(sb, decl, string);
		}
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
	private static boolean hasBrackets(Formula<?> formula, String string) {
		final SourceLocation loc = formula.getSourceLocation();
		final int before = loc.getStart() - 1;
		final int after = loc.getEnd() + 1;
		if (0 <= before && after < string.length()) {
			return string.charAt(before) == '(' && string.charAt(after) == ')';
		}
		return false;
	}

	// Assume that there are more children after
	private static void recalculate(StringBuilder sb, String predString,
			ArrayList<Predicate> currentChildren, String op, int height) {
		int i = 0;
		for (Predicate child : currentChildren) {
			if (i != 0)
				appendSpaces(sb, height + 1);
			addSpacing(sb, predString, child, height);
			appendSpaces(sb, height + 1);
			sb.append(op);
			i++;
		}
	}

	// Made public for testing purposes.
	public static void appendPredicate(StringBuilder sb, String predString,
			Predicate pred) {
		addSpacing(sb, predString, pred, getHeight(pred));
	}

	private static void addSpacing(StringBuilder sb, String predString,
			Predicate pred, int height) {

		final boolean bracketed = hasBrackets(pred, predString);
		if (bracketed) {
			sb.append('(');
		}

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
					appendOp(sb, op, height);
				addSpacing(sb, predString, child, height - 1);
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
			addSpacing(sb, predString, bPred.getLeft(), height - 1);
			appendOp(sb, op, height);
			addSpacing(sb, predString, bPred.getRight(), height - 1);
		}

		else if (pred instanceof LiteralPredicate) {
			appendImage(sb, pred, predString);
		}

		else if (pred instanceof MultiplePredicate) {
			appendImage(sb, pred, predString);
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
			sb.append(op);
			sb.append(' ');
			appendDeclsImage(sb, decls, predString);
			sb.append(" \u00b7 ");
			addSpacing(sb, predString, child, height);
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

			appendImage(sb, rPred.getLeft(), predString);
			appendOp(sb, op, height);
			appendImage(sb, rPred.getRight(), predString);
		}

		else if (pred instanceof SimplePredicate) {
			appendImage(sb, pred, predString);
		}

		else if (pred instanceof UnaryPredicate) {
			UnaryPredicate uPred = (UnaryPredicate) pred;
			int tag = uPred.getTag();
			String op = "";
			if (tag == Predicate.NOT)
				op = "\u00ac";

			appendOp(sb, op, height);
			addSpacing(sb, predString, uPred.getChild(), height - 1);
		}
		
		else if (pred instanceof ExtendedPredicate) {
			appendImage(sb, pred, predString);
		}

		if (bracketed) {
			sb.append(')');
		}
	}

	private static void appendOp(StringBuilder sb, String op, int height) {
		appendSpaces(sb, height);
		sb.append(op);
		appendSpaces(sb, height);
	}

	private static void appendSpaces(StringBuilder sb, int number) {
		while (number > SPACES_LENGTH) {
			sb.append(SPACES);
			number -= SPACES_LENGTH;
		}
		sb.append(SPACES, 0, number);
	}
	
}
