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
import java.util.Iterator;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.SectionPart;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.widgets.FormText;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.eventb.core.ast.BoundIdentDecl;
import org.eventb.core.ast.IParseResult;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.QuantifiedPredicate;
import org.eventb.core.ast.SourceLocation;
import org.eventb.core.pm.ProofState;
import org.eventb.core.pm.UserSupport;
import org.eventb.core.prover.IProofTreeNode;
import org.eventb.core.prover.Lib;
import org.eventb.core.prover.tactics.Tactics;
import org.eventb.internal.ui.EventBFormText;
import org.eventb.internal.ui.EventBMath;
import org.eventb.internal.ui.IEventBFormText;
import org.eventb.internal.ui.IEventBInputText;

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

	private Composite composite;

	private List<IEventBInputText> textBoxes;

	private IEventBInputText textInput;

	/**
	 * @author htson
	 *         <p>
	 *         This class extends HyperlinkAdapter and provide response actions
	 *         when a hyperlink is activated.
	 */
	private class GoalITacticHyperlinkAdapter extends HyperlinkAdapter {

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.ui.forms.events.IHyperlinkListener#linkActivated(org.eclipse.ui.forms.events.HyperlinkEvent)
		 */
		public void linkActivated(HyperlinkEvent e) {
			if (e.getHref().equals(ProverUIUtils.CONJI_SYMBOL)) {
				((ProverUI) GoalSection.this.page.getEditor()).getUserSupport()
						.applyTactic(Tactics.conjI());
				return;
			}
			if (e.getHref().equals(ProverUIUtils.IMPI_SYMBOL)) {
				((ProverUI) GoalSection.this.page.getEditor()).getUserSupport()
						.applyTactic(Tactics.impI());
				return;
			}

			if (e.getHref().equals(ProverUIUtils.ALLI_SYMBOL)) {
				((ProverUI) GoalSection.this.page.getEditor()).getUserSupport()
						.applyTactic(Tactics.allI());
				return;
			}

			if (e.getHref().equals(ProverUIUtils.EXI_SYMBOL)) {
				String[] inputs = new String[textBoxes.size()];
				int i = 0;
				for (IEventBInputText text : textBoxes) {
					inputs[i++] = text.getTextWidget().getText();
				}
				((ProverUI) GoalSection.this.page.getEditor()).getUserSupport()
						.applyTactic(Tactics.exI(inputs));
				return;
			}

			if (e.getHref().equals(ProverUIUtils.NEG_SYMBOL)) {
				((ProverUI) GoalSection.this.page.getEditor()).getUserSupport()
						.applyTactic(Tactics.removeNegGoal());
				return;
			}

			if (e.getHref().equals(ProverUIUtils.DISJE_SYMBOL)) {
				((ProverUI) GoalSection.this.page.getEditor()).getUserSupport()
						.applyTactic(Tactics.disjToImpGoal());
				return;
			}
		}

	}

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
		
		Composite comp = scrolledForm.getBody();
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		layout.verticalSpacing = 5;
		comp.setLayout(layout);
		section.setClient(scrolledForm);
		toolkit.paintBordersFor(scrolledForm);

		formText = new EventBFormText(toolkit.createFormText(comp, true));
		FormText ft = formText.getFormText();
		ft.addHyperlinkListener(new GoalITacticHyperlinkAdapter());
		GridData gd = new GridData(SWT.FILL, SWT.FILL, false, false);
		gd.widthHint = 145;
		ft.setLayoutData(gd);
		toolkit.paintBordersFor(comp);

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
	private void createSimpleText(String text, Color color) {
		composite.setLayout(new GridLayout());
		textInput = new EventBMath(toolkit.createText(composite, text,
				SWT.READ_ONLY));
		GridData gd = new GridData();
		Text textWidget = textInput.getTextWidget();
		textWidget.setLayoutData(gd);
		textWidget.setBackground(color);
	}

	/**
	 * Set the current goal
	 * <p>
	 * 
	 * @param pt
	 *            the current proof tree node.
	 */
	public void setGoal(IProofTreeNode pt) {
		if (composite != null)
			composite.dispose();
		composite = toolkit.createComposite(scrolledForm.getBody());
		GridData gd = new GridData(SWT.FILL, SWT.FILL, true, false);
		composite.setLayoutData(gd);

		if (pt == null) {
			clearFormText();
			Color color = Display.getCurrent().getSystemColor(SWT.COLOR_GRAY);
			createSimpleText("No current goal", color);
			scrolledForm.reflow(true);
		} else if (!pt.isOpen()) {
			clearFormText();
			Predicate goal = pt.getSequent().goal();
			Color color = Display.getCurrent().getSystemColor(SWT.COLOR_GRAY);
			createSimpleText(goal.toString(), color);
			// Label label = toolkit.createLabel(composite, goal.toString(),
			// SWT.LEFT | SWT.SHADOW_IN);
			// label.setLayoutData(new GridData());
			// label.pack();
			scrolledForm.reflow(true);
		} else {
			Predicate goal = pt.getSequent().goal();
			setFormText(goal);

			if (Lib.isExQuant(goal)) {
				String goalString = goal.toString();
				IParseResult parseResult = Lib.ff.parsePredicate(goalString);
				assert parseResult.isSuccess();
				QuantifiedPredicate qpred = (QuantifiedPredicate) parseResult
						.getParsedPredicate();

				BoundIdentDecl[] idents = qpred.getBoundIdentDecls();

				GridLayout gl = new GridLayout();
				gl.numColumns = idents.length * 2 + 2;
				composite.setLayout(gl);

				toolkit.createLabel(composite, "\u2203 ");

				int i = 0;
				textBoxes = new ArrayList<IEventBInputText>();
				for (BoundIdentDecl ident : idents) {
					SourceLocation loc = ident.getSourceLocation();
					String image = goalString.substring(loc.getStart(), loc
							.getEnd());
					if (i++ != 0)
						toolkit.createLabel(composite, ", " + image);
					else
						toolkit.createLabel(composite, image);
					EventBMath mathBox = new EventBMath(toolkit.createText(
							composite, ""));
					gd = new GridData();
					gd.widthHint = 25;
					mathBox.getTextWidget().setLayoutData(gd);
					toolkit.paintBordersFor(composite);
					textBoxes.add(mathBox);
				}

				SourceLocation loc = qpred.getPredicate().getSourceLocation();
				String image = goalString.substring(loc.getStart(), loc
						.getEnd());
				EventBMath text = new EventBMath(toolkit.createText(composite,
						" \u00b7 " + image, SWT.READ_ONLY));
				gd = new GridData(GridData.FILL_HORIZONTAL);
				text.getTextWidget().setLayoutData(gd);
				scrolledForm.reflow(true);
			} else {
				Color color = Display.getCurrent().getSystemColor(
						SWT.COLOR_WHITE);
				createSimpleText(goal.toString(), color);
			}
		}
		scrolledForm.reflow(true);
		return;
	}

	/**
	 * Utility method to clear the form text.
	 */
	private void clearFormText() {
		formText.getFormText().setText("<form></form>", true, false);
		scrolledForm.reflow(true);
		return;
	}

	/**
	 * Set the current form text to display the current goal.
	 * <p>
	 * 
	 * @param goal
	 *            the current goal
	 */
	private void setFormText(Predicate goal) {
		String formString = "<form><li style=\"text\" value=\"\">";
		List<String> tactics = ProverUIUtils.getApplicableToGoal(goal);

		for (Iterator<String> it = tactics.iterator(); it.hasNext();) {
			String t = it.next();
			formString = formString + "<a href=\"" + t + "\">" + t + "</a> ";
		}

		formString = formString + "</li></form>";
		formText.getFormText().setText(formString, true, false);
		scrolledForm.reflow(true);

		return;
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eventb.core.pm.IGoalChangedListener#goalChanged(org.eventb.core.pm.IGoalChangeEvent)
	 */
	// public void goalChanged(IGoalChangeEvent e) {
	// final IGoalDelta delta = e.getDelta();
	//
	// Display display = EventBUIPlugin.getDefault().getWorkbench()
	// .getDisplay();
	// display.syncExec(new Runnable() {
	// public void run() {
	// setGoal(delta.getProofTreeNode());
	// }
	// });
	// }
	//	
	// public void proofStateChanged(final IProofStateDelta delta) {
	// final ProofState ps = delta.getNewProofState();
	// if (ps != null) { // Change PO
	// Display display = EventBUIPlugin.getDefault().getWorkbench()
	// .getDisplay();
	// display.syncExec(new Runnable() {
	// public void run() {
	// setGoal(ps.getCurrentNode());
	// }
	// });
	// } else {
	// final IProofTreeNode node = delta.getNewProofTreeNode();
	// if (node != null) {
	// Display display = EventBUIPlugin.getDefault().getWorkbench()
	// .getDisplay();
	// display.syncExec(new Runnable() {
	// public void run() {
	// setGoal(node);
	// }
	// });
	// }
	// }
	// }
}