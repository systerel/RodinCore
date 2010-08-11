/*******************************************************************************
 * Copyright (c) 2010 Systerel and others. 
 *  
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - Initial API and implementation
 ******************************************************************************/
package org.eventb.internal.ui.prooftreeui;

import static java.util.Collections.singletonList;
import static org.eclipse.swt.SWT.BOLD;
import static org.eclipse.swt.SWT.FULL_SELECTION;
import static org.eclipse.swt.SWT.ITALIC;
import static org.eclipse.swt.SWT.MULTI;
import static org.eclipse.swt.SWT.NONE;
import static org.eclipse.swt.SWT.TOP;
import static org.eventb.core.seqprover.IHypAction.ISelectionHypAction.DESELECT_ACTION_TYPE;
import static org.eventb.core.seqprover.IHypAction.ISelectionHypAction.HIDE_ACTION_TYPE;
import static org.eventb.core.seqprover.IHypAction.ISelectionHypAction.SELECT_ACTION_TYPE;
import static org.eventb.core.seqprover.IHypAction.ISelectionHypAction.SHOW_ACTION_TYPE;
import static org.eventb.internal.ui.prooftreeui.Messages.RuleDetailsProvider_addedfreeidentifiers_title;
import static org.eventb.internal.ui.prooftreeui.Messages.RuleDetailsProvider_antecedent_title;
import static org.eventb.internal.ui.prooftreeui.Messages.RuleDetailsProvider_deselect_title;
import static org.eventb.internal.ui.prooftreeui.Messages.RuleDetailsProvider_forwardinference_title;
import static org.eventb.internal.ui.prooftreeui.Messages.RuleDetailsProvider_goal;
import static org.eventb.internal.ui.prooftreeui.Messages.RuleDetailsProvider_hide_title;
import static org.eventb.internal.ui.prooftreeui.Messages.RuleDetailsProvider_inputsequent_title;
import static org.eventb.internal.ui.prooftreeui.Messages.RuleDetailsProvider_instantiationcase_with;
import static org.eventb.internal.ui.prooftreeui.Messages.RuleDetailsProvider_rule_title;
import static org.eventb.internal.ui.prooftreeui.Messages.RuleDetailsProvider_select_title;
import static org.eventb.internal.ui.prooftreeui.Messages.RuleDetailsProvider_show_title;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IHypAction;
import org.eventb.core.seqprover.IHypAction.IForwardInfHypAction;
import org.eventb.core.seqprover.IHypAction.ISelectionHypAction;
import org.eventb.core.seqprover.IProofRule;
import org.eventb.core.seqprover.IProofRule.IAntecedent;
import org.eventb.internal.ui.prooftreeui.RuleDetailsModel.RuleDetailsElement;
import org.eventb.internal.ui.prooftreeui.RuleDetailsModel.RuleModelAction;
import org.eventb.internal.ui.prooftreeui.RuleDetailsModel.RuleModelAntecedent;
import org.eventb.internal.ui.prooftreeui.RuleDetailsModel.RuleModelRoot;

/**
 * Class used to compute rule informations to be displayed and traverse them in
 * order to display them.
 */
public class RuleDetailsProvider {

	private static final int START_INDENT = 0;
	private static final int[] TABS = new int[] { 15, 30, 45, 60, 75 };
	private static final String TAB = "\t";
	private static final String CR = "\n";
	private static final String TURNSTILE = "\u22A2";
	private static final String FALSE = "\u22A5";
	private static final String EMPTY_STRING = "";
	private static final String COMMA = ",";
	private static final String SPACE = " ";
	private static final String SEPARATOR = COMMA + SPACE;
	private static final String INST = "inst ";

	private IProofRule rule;
	private StyledText sText;
	private RuleDetailsModel model;

	/**
	 * Constructor for testing only.
	 */
	public RuleDetailsProvider() {
		// Does nothing
	}

	/**
	 * Constructor.
	 * 
	 * @param parent
	 *            the composite that will hold the styled text to print rule
	 *            informations
	 */
	public RuleDetailsProvider(Composite parent) {
		sText = new StyledText(parent, MULTI | FULL_SELECTION);
		final GridData gData = new GridData(TOP, TOP, true, true);
		sText.setLayoutData(gData);
		sText.setTabStops(TABS);
	}

	public Control getRuleDetailsPresentation(IProofRule handledRule) {
		computeRuleDetails(handledRule);
		return getDetailsPresentation();
	}

	public Control getDetailsPresentation() {
		sText.setText(EMPTY_STRING);
		print(model.getModelRoot());
		return sText;
	}

	public void computeRuleDetails(IProofRule handledRule) {
		// if the model is the handled one, we just print it
		if (rule == handledRule && model != null) {
			return;
		}
		rule = handledRule;
		model = new RuleDetailsModel(rule, computeRuleModelRoot());
	}

	private RuleModelRoot computeRuleModelRoot() {
		final RuleModelRoot root = new RuleModelRoot(rule.getDisplayName(),
				rule.getConfidence());
		setRootInputSequent(root);
		root.setChildren(computeRuleChildren());
		return root;
	}

	private List<RuleDetailsElement> computeRuleChildren() {
		final List<RuleDetailsElement> children = new ArrayList<RuleDetailsElement>();
		final IAntecedent[] antecedents = rule.getAntecedents();
		int cnt = 1;
		for (IAntecedent a : antecedents) {
			children.add(computeAntecedent(a, cnt));
			cnt++;
		}
		return children;
	}

	private void setRootInputSequent(RuleModelRoot root) {
		final Set<Predicate> neededHyps = rule.getNeededHyps();
		final Predicate goal = rule.getGoal();
		if (neededHyps.size() == 0 && goal == null) {
			return;
		}
		if (rule.getAntecedents().length > 0 && !hasAddedHyps()
				&& !hasAntecedentGoals()) {
			return;
		}
		root.setH(getPredicateStrings(rule.getNeededHyps()));
		if (goal != null)
			root.setG(getPredicateStrings(goal));
	}

	private boolean hasAddedHyps() {
		for (IAntecedent a : rule.getAntecedents()) {
			if (a.getAddedHyps().size() > 0) {
				return true;
			}
		}
		return false;
	}

	private boolean hasAntecedentGoals() {
		for (IAntecedent a : rule.getAntecedents()) {
			if (a.getGoal() != null) {
				return true;
			}
		}
		return false;
	}

	private List<String> getPredicateStrings(Collection<Predicate> predicates) {
		final List<String> result = new ArrayList<String>();
		for (Predicate p : predicates) {
			result.add(p.toString());
		}
		return result;
	}

	private List<String> getPredicateStrings(Predicate p) {
		return singletonList(p.toString());
	}

	private RuleDetailsElement computeAntecedent(IAntecedent a, int cnt) {
		final RuleModelAntecedent child = new RuleModelAntecedent(
				RuleDetailsProvider_antecedent_title + cnt);
		child.setAddedIdentifiers(a.getAddedFreeIdents(), NONE);
		child.setH(getPredicateStrings(a.getAddedHyps()));
		final Predicate goal = a.getGoal();
		if (goal != null)
			child.setG(getPredicateStrings(goal));

		final List<RuleDetailsElement> actions = new ArrayList<RuleDetailsElement>();
		for (IHypAction hypAction : a.getHypActions()) {
			actions.add(computeHypActions(hypAction));
		}
		child.setChildren(actions);
		return child;
	}

	private RuleDetailsElement computeHypActions(IHypAction hypAction) {
		final RuleModelAction action = new RuleModelAction(
				getHypActionName(hypAction));
		if (hypAction instanceof ISelectionHypAction) {
			final ISelectionHypAction selha = (ISelectionHypAction) hypAction;
			action.setH(getPredicateStrings(selha.getHyps()));
		}
		if (hypAction instanceof IForwardInfHypAction) {
			final IForwardInfHypAction fwdha = (IForwardInfHypAction) hypAction;
			action.setH(getPredicateStrings(fwdha.getHyps()));
			action.setG(getPredicateStrings(fwdha.getInferredHyps()));
		}
		return action;
	}

	private void print(RuleModelRoot root) {
		final int level = START_INDENT;
		append(RuleDetailsProvider_rule_title, level, BOLD);
		printRuleName(root.getName(), level);
		newLine();
		printInputSequent(root, level);
		printAntecedent(root, level);
	}

	private void printRuleName(String name, int level) {
		final String instString = INST; //$NON-NLS-1$
		if (!name.contains(instString)) {
			appendln(name, level);
			return;
		}
		final String[] split = name.substring(0, name.length() - 1).split(
				instString);
		final String[] instantiations = split[1].split(COMMA);
		append(split[0].substring(0, split[0].length() - 1), level);
		appendln(RuleDetailsProvider_instantiationcase_with, level, BOLD);
		for (String s : instantiations) {
			appendln(s, level + 1, true);
		}
	}

	private void printInputSequent(RuleModelRoot root, int level) {
		final List<String> hypotheses = root.getH();
		final List<String> goal = root.getG();
		if (hypotheses == null && goal == null) {
			return;
		}
		appendln(RuleDetailsProvider_inputsequent_title, level, BOLD);
		if (!printSequent(level, hypotheses, goal)) {
			appendTurnstile(level + 1);
			if (rule.getAntecedents().length == 0) {
				appendln(FALSE, level + 1, true);
			} else {
				appendln(RuleDetailsProvider_goal, level + 1, SWT.ITALIC, true);
			}
		}
		newLine();
	}

	private boolean printSequent(int level, List<String> hypotheses,
			List<String> goal) {
		printHypotheses(level, hypotheses);
		if (goal != null) {
			appendTurnstile(level + 1);
			printGoalNotNull(level, goal);
			return true;
		}
		return false;
	}

	private void printHypotheses(int level, final List<String> hypotheses) {
		for (String hyp : hypotheses) {
			appendln(hyp, level + 2);
		}
	}

	private void printGoalNotNull(int level, final List<String> goal) {
		appendln(goal.get(0), 1, true);
		for (int i = 1; i < goal.size(); i++) {
			appendln(goal.get(i), level + 2, true);
		}
	}

	private void printAntecedent(RuleModelRoot root, int level) {
		for (RuleDetailsElement child : root.getChildren()) {
			final RuleModelAntecedent antecedent = (RuleModelAntecedent) child;
			appendln(antecedent.getName(), level, BOLD);
			printAddedIdentifiers(level, antecedent);
			final List<String> hypotheses = child.getH();
			final List<String> goal = child.getG();
			if (hypotheses.size() == 0 && goal == null) {
				printHypActions(child.getChildren(), level + 1);
				return;
			}
			if (!printSequent(level, hypotheses, goal)) {
				appendTurnstile(level + 1);
				appendln(RuleDetailsProvider_goal, level + 1, ITALIC, true);
			}
			printHypActions(child.getChildren(), level + 2);
			newLine();
		}
	}

	private void printAddedIdentifiers(int level, RuleModelAntecedent antecedent) {
		final List<String> addedIdent = antecedent.getAddedIdentifiers();
		if (addedIdent.size() > 0) {
			append(RuleDetailsProvider_addedfreeidentifiers_title, level + 1,
					BOLD);
			append(addedIdent.get(0), 0);
			for (int i = 1; i < addedIdent.size(); i++) {
				append(SEPARATOR + addedIdent.get(i), 0);
			}
			newLine();
		}
	}

	private void printHypActions(List<RuleDetailsElement> children, int level) {
		for (RuleDetailsElement element : children) {
			final RuleModelAction action = (RuleModelAction) element;
			final List<String> goal = action.getG();
			final List<String> hypotheses = action.getH();
			if (hypotheses.isEmpty() && goal == null) {
				return;
			}
			appendln(action.getName(), level, BOLD);
			printSequent(level, hypotheses, goal);
		}
	}

	private String getHypActionName(IHypAction hypAction) {
		final String type = hypAction.getActionType();
		if (type.equals(SELECT_ACTION_TYPE)) {
			return RuleDetailsProvider_select_title;
		}
		if (type.equals(DESELECT_ACTION_TYPE)) {
			return RuleDetailsProvider_deselect_title;
		}
		if (type.equals(HIDE_ACTION_TYPE)) {
			return RuleDetailsProvider_hide_title;
		}
		if (type.equals(SHOW_ACTION_TYPE)) {
			return RuleDetailsProvider_show_title;
		} else {
			// it is a forward inference
			return RuleDetailsProvider_forwardinference_title;
		}
	}

	/*
	 * Printing helper methods
	 */
	private void append(String str, int indent, int style) {
		append(str, indent, style, false);
	}

	private void append(String str, int indent, int style, boolean color) {
		final StyleRange styleRange = new StyleRange();
		styleRange.start = getCurrentPosition(sText) + indent;
		styleRange.length = str.length();
		styleRange.fontStyle = style;
		if (color) {
			final Color c = sText.getDisplay().getSystemColor(
					SWT.COLOR_DARK_BLUE);
			styleRange.foreground = c;
		}
		append(str, indent);
		sText.setStyleRange(styleRange);
	}

	private void appendTurnstile(int indentLevel) {
		append(TURNSTILE, indentLevel);
	}

	private void appendln(String str, int indent, int style, boolean colored) {
		append(str, indent, style, colored);
		sText.append(CR);
	}

	private void appendln(String str, int indent) {
		appendln(str, indent, NONE, false);
	}

	private void appendln(String str, int indent, boolean colored) {
		appendln(str, indent, NONE, colored);
	}

	private void appendln(String str, int indent, int style) {
		appendln(str, indent, style, false);
	}

	private void append(String str, int indent) {
		String tabs = EMPTY_STRING;
		for (int i = 0; i < indent; i++) {
			tabs = tabs + TAB;
		}
		sText.append(tabs + str);
	}

	private static int getCurrentPosition(StyledText text) {
		return text.getText().length();
	}

	private void newLine() {
		sText.append(CR);
	}

}
