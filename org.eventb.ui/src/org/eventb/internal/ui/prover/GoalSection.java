/*******************************************************************************
 * Copyright (c) 2005 ETH Zurich.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Rodin @ ETH Zurich
 ******************************************************************************/

package org.eventb.internal.ui.prover;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.forms.SectionPart;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.events.IHyperlinkListener;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ImageHyperlink;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.eventb.core.ast.AssociativePredicate;
import org.eventb.core.ast.BinaryPredicate;
import org.eventb.core.ast.BoundIdentDecl;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.IParseResult;
import org.eventb.core.ast.LiteralPredicate;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.QuantifiedPredicate;
import org.eventb.core.ast.RelationalPredicate;
import org.eventb.core.ast.SimplePredicate;
import org.eventb.core.ast.SourceLocation;
import org.eventb.core.ast.UnaryPredicate;
import org.eventb.core.pm.ProofState;
import org.eventb.core.pm.UserSupport;
import org.eventb.core.seqprover.IProofTreeNode;
import org.eventb.core.seqprover.Lib;
import org.eventb.internal.ui.IEventBFormText;
import org.eventb.internal.ui.IEventBInputText;
import org.eventb.internal.ui.prover.goaltactics.GoalTacticUI;

/**
 * @author htson
 *         <p>
 *         This class implements the goal section in the Prover UI Editor.
 */
public class GoalSection extends SectionPart {

	// Title and description.
	private static final String SECTION_TITLE = "Goal";

	private static final String SECTION_DESCRIPTION = "The current goal";

	private FormPage page;

	private IEventBFormText formText;

	private FormToolkit toolkit;

	private ScrolledForm scrolledForm;

	private List<IEventBInputText> textBoxes;

	private IEventBInputText textInput;

	private Composite buttonComposite;

	private Composite comp;

	private ScrolledForm goalComposite;

	private EventBPredicateText goalText;

	private Predicate parsedPred;

	private String actualString;

	private int max_length = 25;

	private static final int tab = 2;

	/**
	 * Constructor
	 * <p>
	 * 
	 * @param page
	 *            The page that contain this section
	 * @param parent
	 *            the composite parent of the section
	 * @param style
	 *            style to create this section
	 */
	public GoalSection(FormPage page, Composite parent, int style) {
		super(parent, page.getManagedForm().getToolkit(), style);
		this.page = page;
		FormToolkit toolkit = page.getManagedForm().getToolkit();
		createClient(getSection(), toolkit);
	}

	/**
	 * Creating the client of the section.
	 * <p>
	 * 
	 * @param section
	 *            the section that used as the parent of the client
	 * @param toolkit
	 *            the FormToolkit used to create the client
	 */
	public void createClient(Section section, FormToolkit toolkit) {
		this.toolkit = toolkit;
		section.setText(SECTION_TITLE);
		section.setDescription(SECTION_DESCRIPTION);
		scrolledForm = toolkit.createScrolledForm(section);

		comp = scrolledForm.getBody();
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		layout.verticalSpacing = 5;
		comp.setLayout(layout);
		section.setClient(scrolledForm);
		toolkit.paintBordersFor(scrolledForm);

		UserSupport userSupport = ((ProverUI) ((ProofsPage) this.page)
				.getEditor()).getUserSupport();
		ProofState ps = userSupport.getCurrentPO();
		if (ps != null) {
			setGoal(ps.getCurrentNode());
		}
	}

	/**
	 * Utility method to create a simple Text Widget.
	 * <p>
	 * 
	 * @param text
	 *            the string to create the widget
	 */
//	private void createSimpleText(String text, Color color) {
//		textInput = new EventBMath(toolkit
//				.createText(comp, text, SWT.READ_ONLY));
//		GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
//		Text textWidget = textInput.getTextWidget();
//		textWidget.setLayoutData(gd);
//		textWidget.setBackground(color);
//	}

	/**
	 * Set the current goal
	 * <p>
	 * 
	 * @param node
	 *            the current proof tree node.
	 */
	public void setGoal(IProofTreeNode node) {
		if (buttonComposite != null)
			buttonComposite.dispose();
		if (textInput != null)
			textInput.dispose();
		if (goalComposite != null)
			goalComposite.dispose();
		if (textBoxes != null)
			for (IEventBInputText text : textBoxes) {
				text.dispose();
			}

		buttonComposite = toolkit.createComposite(comp);
		GridLayout layout = new GridLayout();
		layout.makeColumnsEqualWidth = true;
		layout.numColumns = 5;

		buttonComposite.setLayout(layout);
		buttonComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false,
				true));

		goalComposite = toolkit.createScrolledForm(comp);
		GridData gd = new GridData(GridData.FILL_BOTH);
		goalComposite.setLayoutData(gd);
		goalComposite.getBody().setLayout(new GridLayout());

		Predicate goal = node.getSequent().goal();
		createHyperlinks(toolkit, buttonComposite, node, true);
		actualString = goal.toString();
		IParseResult parseResult = Lib.ff.parsePredicate(actualString);
		assert parseResult.isSuccess();
		parsedPred = parseResult.getParsedPredicate();

		createGoalText(node);

//		if (node != null) {
//
//			if (!node.isOpen()) {
//				Predicate goal = node.getSequent().goal();
//				createHyperlinks(toolkit, buttonComposite, node, false);
//				// Color color = Display.getCurrent().getSystemColor(
//				// SWT.COLOR_GRAY);
//				// createSimpleText(goal.toString(), color);
//			} else {
//
//				createGoalText();
//			}
//		} else {
//			Color color = Display.getCurrent().getSystemColor(SWT.COLOR_GRAY);
//			createSimpleText("No current goal", color);
//		}

		// textBoxes = new ArrayList<IEventBInputText>();

		// } else {
		//
		// if (Lib.isExQuant(goal)) {
		// String goalString = goal.toString();
		// IParseResult parseResult = Lib.ff
		// .parsePredicate(goalString);
		// assert parseResult.isSuccess();
		// QuantifiedPredicate qpred = (QuantifiedPredicate) parseResult
		// .getParsedPredicate();
		//					
		// BoundIdentDecl[] idents = qpred.getBoundIdentDecls();
		//
		// composite = toolkit.createComposite(comp);
		//
		// GridLayout gl = new GridLayout();
		// gl.numColumns = idents.length * 2 + 2;
		// composite.setLayout(gl);
		//
		// toolkit.createLabel(composite, "\u2203 ");
		//
		// int i = 0;
		// for (BoundIdentDecl ident : idents) {
		// SourceLocation loc = ident.getSourceLocation();
		// String image = goalString.substring(loc.getStart(), loc
		// .getEnd());
		// if (i++ != 0)
		// toolkit.createLabel(composite, ", " + image);
		// else
		// toolkit.createLabel(composite, image);
		// EventBMath mathBox = new EventBMath(toolkit.createText(
		// composite, ""));
		// GridData gd = new GridData();
		// gd.widthHint = 25;
		// mathBox.getTextWidget().setLayoutData(gd);
		// toolkit.paintBordersFor(composite);
		// textBoxes.add(mathBox);
		// }
		//
		// SourceLocation loc = qpred.getPredicate()
		// .getSourceLocation();
		// String image = goalString.substring(loc.getStart(), loc
		// .getEnd());
		// EventBMath text = new EventBMath(toolkit.createText(
		// composite, " \u00b7 " + image, SWT.READ_ONLY));
		// GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		// text.getTextWidget().setLayoutData(gd);
		// scrolledForm.reflow(true);
		// } else {
		// Color color = Display.getCurrent().getSystemColor(
		// SWT.COLOR_WHITE);
		// createSimpleText(goal.toString(), color);
		// }
		//
		// }
		scrolledForm.reflow(true);

		return;
	}

	public void createGoalText(IProofTreeNode node) {
		Color color = Display.getCurrent().getSystemColor(SWT.COLOR_GRAY);
		if (goalText != null)
			goalText.dispose();
		goalText = new EventBPredicateText(toolkit, goalComposite);
		goalText.getMainTextWidget().setLayoutData(
				new GridData(SWT.FILL, SWT.FILL, true, true));

		int borderWidth = goalText.getMainTextWidget().getBorderWidth();
		goalText.getMainTextWidget().setText(" ");
		goalComposite.pack(true);
		int textWidth = goalText.getMainTextWidget().getSize().x;

		Rectangle rec = goalComposite.getBounds();
		Point size = goalComposite.getSize();
		int compositeWidth = goalComposite.getClientArea().width;
		// if (textWidth != 0) {
		// max_length = (compositeWidth - borderWidth) / textWidth;
		// } else
		// max_length = 30;

		if (node == null) {
			Collection<Point> indexes = new ArrayList<Point>();
			goalText.setText("No current goal", indexes);
			goalText.getMainTextWidget().setBackground(color);
		}
		if (node != null && node.isOpen()
				&& parsedPred instanceof QuantifiedPredicate
				&& parsedPred.getTag() == Formula.EXISTS) {
			QuantifiedPredicate qpred = (QuantifiedPredicate) parsedPred;
			Collection<Point> indexes = new ArrayList<Point>();

			String string = "\u2203\n";
			BoundIdentDecl[] idents = qpred.getBoundIdentDecls();

			int i = 0;
			for (BoundIdentDecl ident : idents) {
				SourceLocation loc = ident.getSourceLocation();
				String image = actualString.substring(loc.getStart(), loc
						.getEnd());
				// ProverUIUtils.debugProverUI("Ident: " + image);
				string += "  " + image + "  ";
				int x = string.length();
				string += "      ";
				int y = string.length();
				indexes.add(new Point(x, y));

				if (++i == idents.length) {
					string += "\n";
				} else {
					string += "  ,\n";
				}
			}
			String str = prettyPrint(actualString, qpred.getPredicate(), 0);

			string += "\u00b7\n";
			string += str;
			goalText.setText(string, indexes);
		} else {
			String str = prettyPrint(actualString, parsedPred);
			Collection<Point> indexes = new ArrayList<Point>();
			goalText.setText(str, indexes);
			if (!node.isOpen()) {
				goalText.getMainTextWidget().setBackground(color);
			}
		}
		toolkit.paintBordersFor(goalComposite);
	}

	@Override
	public void dispose() {
		if (formText != null)
			formText.dispose();
		if (textInput != null)
			textInput.dispose();
		if (textBoxes != null)
			for (IEventBInputText text : textBoxes)
				text.dispose();
		super.dispose();
	}

	/**
	 * Utility methods to create hyperlinks for applicable tactics.
	 * <p>
	 * 
	 * @param formText
	 *            the formText parent of these hyperlinks
	 */
	private void createHyperlinks(FormToolkit toolkit, Composite parent,
			final IProofTreeNode node, boolean enable) {
		Collection<GoalTacticUI> tactics = ProverUIUtils
				.getApplicableToGoal(node);

		for (final GoalTacticUI tactic : tactics) {
			ImageHyperlink hyperlink = new ImageHyperlink(buttonComposite,
					SWT.CENTER);
			hyperlink
					.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
			toolkit.adapt(hyperlink, true, true);
			hyperlink.setImage(tactic.getImage());
			hyperlink.addHyperlinkListener(new IHyperlinkListener() {

				public void linkEntered(HyperlinkEvent e) {
					return;
				}

				public void linkExited(HyperlinkEvent e) {
					return;
				}

				public void linkActivated(HyperlinkEvent e) {
					String [] inputs = goalText.getResults();
//					String[] inputs = new String[textBoxes.size()];
//					int i = 0;
//					for (IEventBInputText text : textBoxes) {
//						inputs[i++] = text.getTextWidget().getText();
//					}
					((ProverUI) page.getEditor()).getUserSupport().applyTactic(
							tactic.getTactic(node, inputs));
				}

			});
			hyperlink.setToolTipText(tactic.getHint());
			hyperlink.setEnabled(enable);
		}

		return;
	}

	private String printSpace(int indent) {
		String result = "";
		for (int i = 0; i < indent; i++)
			result += " ";
		return result;
	}

	private String prettyPrint(String predString, Predicate pred) {
		return prettyPrint(predString, pred, 0);
	}

	private String prettyPrint(String predString, Predicate pred, int indent) {
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
						result += prettyPrint(predString, child, indent);
					}
				}

				else {
					if (newString.length() <= max_length - indent) {
						currentString = newString;
						currentChildren.add(child);
					} else if (oldString == "") {
						result += prettyPrint(predString, child, indent);
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
			return prettyPrint(predString, bPred.getLeft(), indent + tab)
					+ "\n" + printSpace(indent) + op + "\n"
					+ prettyPrint(predString, bPred.getRight(), indent + tab);
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
					+ prettyPrint(predString, qPred.getPredicate(), indent
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
					+ prettyPrint(predString, uPred.getChild(), indent + tab);
		}
		return "";
	}

	private String recalculate(String predString,
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

	private String addSpacing(String predString, Predicate pred) {
		return addSpacing(predString, pred, getHeight(pred));
	}

	private String addSpacing(String predString, Predicate pred, int height) {
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

	private String addSpacing(String op, int height) {
		String result = "";
		for (int i = 0; i < height; i++)
			result += " ";
		result += op;
		for (int i = 0; i < height; i++)
			result += " ";
		return result;
	}

	private int getHeight(Predicate pred) {
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