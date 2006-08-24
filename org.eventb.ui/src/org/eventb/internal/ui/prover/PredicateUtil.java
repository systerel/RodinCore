package org.eventb.internal.ui.prover;

import java.util.ArrayList;

import org.eventb.core.ast.AssociativePredicate;
import org.eventb.core.ast.BinaryPredicate;
import org.eventb.core.ast.BoundIdentDecl;
import org.eventb.core.ast.LiteralPredicate;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.QuantifiedPredicate;
import org.eventb.core.ast.RelationalPredicate;
import org.eventb.core.ast.SimplePredicate;
import org.eventb.core.ast.SourceLocation;
import org.eventb.core.ast.UnaryPredicate;

public class PredicateUtil {

	private static final int tab = 2;
	
	private static String printSpace(int indent) {
		String result = "";
		for (int i = 0; i < indent; i++)
			result += " ";
		return result;
	}

	public static String prettyPrint(int max_length, String predString,
			Predicate pred) {
		return prettyPrint(max_length, predString, pred, 0);
	}

	private static String prettyPrint(int max_length, String predString,
			Predicate pred, int indent) {
		// Pseudo-code:

		String str = addSpacing(predString, pred);
		if (str.length() <= max_length - indent)
			return printSpace(indent) + str;
		if (pred instanceof AssociativePredicate) {
			AssociativePredicate aPred = (AssociativePredicate) pred;
			String op = "";
			int tag = aPred.getTag();
			if (tag == Predicate.LAND)
				op = "\u2227";
			else if (tag == Predicate.LOR)
				op = "\u2228";
			String result = "";
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
					if (newString.length() <= max_length - indent) {
						result += printSpace(indent) + newString;
					} else {
						if (oldString != "")
							result += printSpace(indent) + oldString + "\n";
						result += prettyPrint(max_length, predString, child, indent);
					}
				}

				else {
					if (newString.length() <= max_length - indent) {
						currentString = newString;
						currentChildren.add(child);
					} else if (oldString == "") {
						result += prettyPrint(max_length, predString, child, indent);
						result += printSpace(indent) + op + "\n";
						currentString = "";
						currentChildren.clear();
						currentHeight = -1;
					} else {
						result += printSpace(indent) + oldString + "\n";
						currentHeight = getHeight(child);
						currentChildren.clear();
						currentChildren.add(child);
						currentString = addSpacing(predString, child,
								currentHeight)
								+ printSpace(currentHeight + 1) + op;
					}
				}

			}
			ProverUIUtils.debugProverUI("Pred: " + pred);
			ProverUIUtils.debugProverUI("Result: \n" + result);
			return result;
		}

		if (pred instanceof BinaryPredicate) {
			BinaryPredicate bPred = (BinaryPredicate) pred;
			String op = "";
			int tag = bPred.getTag();
			if (tag == Predicate.LIMP)
				op = "\u21d2";
			else if (tag == Predicate.LEQV)
				op = "\u21d4";
			return prettyPrint(max_length, predString, bPred.getLeft(), indent + tab)
					+ "\n" + printSpace(indent) + op + "\n"
					+ prettyPrint(max_length, predString, bPred.getRight(), indent + tab);
		}

		if (pred instanceof LiteralPredicate) {
			return printSpace(indent) + str;
		}

		if (pred instanceof QuantifiedPredicate) {
			QuantifiedPredicate qPred = (QuantifiedPredicate) pred;
			int tag = qPred.getTag();
			String op = "";
			if (tag == Predicate.FORALL)
				op = "\u2200";
			else if (tag == Predicate.EXISTS)
				op = "\u2203";
			BoundIdentDecl[] idents = qPred.getBoundIdentDecls();

			String result = printSpace(indent) + op + " ";
			int i = 0;
			for (BoundIdentDecl ident : idents) {
				SourceLocation loc = ident.getSourceLocation();
				String image = predString.substring(loc.getStart(), loc
						.getEnd());
				ProverUIUtils.debugProverUI("Ident: " + image);

				if (i++ == 0) {
					result += image;
				} else {
					result += ", " + image;
				}
			}

			result += " \u00b7 ";

			return result
					+ "\n"
					+ prettyPrint(max_length, predString, qPred.getPredicate(), indent
							+ tab);
		}

		if (pred instanceof RelationalPredicate) {
			return printSpace(indent) + str;
		}

		if (pred instanceof SimplePredicate) {
			return printSpace(indent) + str;
		}

		if (pred instanceof UnaryPredicate) {
			UnaryPredicate uPred = (UnaryPredicate) pred;
			int tag = uPred.getTag();
			String op = "";
			if (tag == Predicate.NOT)
				op = "\u00ac";

			return printSpace(indent) + op + "\n"
					+ prettyPrint(max_length, predString, uPred.getChild(), indent + tab);
		}
		return "";
	}

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

	public static String addSpacing(String predString, Predicate pred) {
		return addSpacing(predString, pred, getHeight(pred));
	}

	private static String addSpacing(String predString, Predicate pred,
			int height) {
		if (pred instanceof AssociativePredicate) {
			AssociativePredicate aPred = (AssociativePredicate) pred;
			String op = "";
			int tag = aPred.getTag();
			if (tag == Predicate.LAND)
				op = "\u2227";
			else if (tag == Predicate.LOR)
				op = "\u2228";
			String result = "";
			int i = 0;
			for (Predicate child : aPred.getChildren()) {
				if (i != 0)
					result += addSpacing(op, height);
				result += addSpacing(predString, child, height - 1);
				i++;
			}
			return result;
		}

		if (pred instanceof BinaryPredicate) {
			BinaryPredicate bPred = (BinaryPredicate) pred;
			String op = "";
			int tag = bPred.getTag();
			if (tag == Predicate.LIMP)
				op = "\u21d2";
			else if (tag == Predicate.LEQV)
				op = "\u21d4";
			return addSpacing(predString, bPred.getLeft(), height - 1)
					+ addSpacing(op, height)
					+ addSpacing(predString, bPred.getRight(), height - 1);
		}

		if (pred instanceof LiteralPredicate) {
			SourceLocation loc = pred.getSourceLocation();
			String image = predString.substring(loc.getStart(), loc.getEnd());
			return image;
		}

		if (pred instanceof QuantifiedPredicate) {
			QuantifiedPredicate qPred = (QuantifiedPredicate) pred;

			int tag = qPred.getTag();
			String op = "";
			if (tag == Predicate.FORALL)
				op = "\u2200";
			else if (tag == Predicate.EXISTS)
				op = "\u2203";
			BoundIdentDecl[] idents = qPred.getBoundIdentDecls();

			String result = op + " ";
			int i = 0;
			for (BoundIdentDecl ident : idents) {
				SourceLocation loc = ident.getSourceLocation();
				String image = predString.substring(loc.getStart(), loc
						.getEnd());
				ProverUIUtils.debugProverUI("Ident: " + image);

				if (i++ == 0) {
					result += image;
				} else {
					result += ", " + image;
				}
			}

			result += " \u00b7 "
					+ addSpacing(predString, qPred.getPredicate(), height);

			return result;
		}

		if (pred instanceof RelationalPredicate) {
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

			SourceLocation loc = rPred.getLeft().getSourceLocation();
			String imageLeft = predString.substring(loc.getStart(), loc
					.getEnd());
			loc = rPred.getRight().getSourceLocation();
			String imageRight = predString.substring(loc.getStart(), loc
					.getEnd());
			return imageLeft + addSpacing(op, height) + imageRight;
		}

		if (pred instanceof SimplePredicate) {
			SourceLocation loc = pred.getSourceLocation();
			String image = predString.substring(loc.getStart(), loc.getEnd());
			return image;
		}

		if (pred instanceof UnaryPredicate) {
			UnaryPredicate uPred = (UnaryPredicate) pred;
			int tag = uPred.getTag();
			String op = "";
			if (tag == Predicate.NOT)
				op = "\u00ac";

			return addSpacing(op, height)
					+ addSpacing(predString, uPred.getChild(), height - 1);
		}
		return "";
	}

	private static String addSpacing(String op, int height) {
		String result = "";
		for (int i = 0; i < height; i++)
			result += " ";
		result += op;
		for (int i = 0; i < height; i++)
			result += " ";
		return result;
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
			return leftHeight > rightHeight ? leftHeight : rightHeight + 1;
		}

		if (pred instanceof LiteralPredicate) {
			return 0;
		}

		if (pred instanceof QuantifiedPredicate) {
			return getHeight(((QuantifiedPredicate) pred).getPredicate());
		}

		if (pred instanceof RelationalPredicate) {
			// TODO Get the height of the expression?
			return 1;
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
