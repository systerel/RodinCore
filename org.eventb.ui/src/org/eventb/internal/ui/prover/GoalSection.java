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
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.SectionPart;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.events.IHyperlinkListener;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ImageHyperlink;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.eventb.core.ast.BoundIdentDecl;
import org.eventb.core.ast.IParseResult;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.QuantifiedPredicate;
import org.eventb.core.ast.SourceLocation;
import org.eventb.core.pm.ProofState;
import org.eventb.core.pm.UserSupport;
import org.eventb.core.seqprover.IProofTreeNode;
import org.eventb.core.seqprover.Lib;
import org.eventb.internal.ui.EventBMath;
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

	private Composite composite;

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
	private void createSimpleText(String text, Color color) {
		textInput = new EventBMath(toolkit
				.createText(comp, text, SWT.READ_ONLY));
		GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
		Text textWidget = textInput.getTextWidget();
		textWidget.setLayoutData(gd);
		textWidget.setBackground(color);
	}

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
		if (composite != null)
			composite.dispose();
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

		textBoxes = new ArrayList<IEventBInputText>();
		
		if (node != null) {

			if (!node.isOpen()) {
				Predicate goal = node.getSequent().goal();
				createHyperlinks(toolkit, buttonComposite, node, false);

				Color color = Display.getCurrent().getSystemColor(
						SWT.COLOR_GRAY);
				createSimpleText(goal.toString(), color);
			} else {
				Predicate goal = node.getSequent().goal();
				createHyperlinks(toolkit, buttonComposite, node, true);

				if (Lib.isExQuant(goal)) {
					String goalString = goal.toString();
					IParseResult parseResult = Lib.ff
							.parsePredicate(goalString);
					assert parseResult.isSuccess();
					QuantifiedPredicate qpred = (QuantifiedPredicate) parseResult
							.getParsedPredicate();

					BoundIdentDecl[] idents = qpred.getBoundIdentDecls();

					composite = toolkit.createComposite(comp);

					GridLayout gl = new GridLayout();
					gl.numColumns = idents.length * 2 + 2;
					composite.setLayout(gl);

					toolkit.createLabel(composite, "\u2203 ");

					int i = 0;
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
						GridData gd = new GridData();
						gd.widthHint = 25;
						mathBox.getTextWidget().setLayoutData(gd);
						toolkit.paintBordersFor(composite);
						textBoxes.add(mathBox);
					}

					SourceLocation loc = qpred.getPredicate()
							.getSourceLocation();
					String image = goalString.substring(loc.getStart(), loc
							.getEnd());
					EventBMath text = new EventBMath(toolkit.createText(
							composite, " \u00b7 " + image, SWT.READ_ONLY));
					GridData gd = new GridData(GridData.FILL_HORIZONTAL);
					text.getTextWidget().setLayoutData(gd);
					scrolledForm.reflow(true);
				} else {
					Color color = Display.getCurrent().getSystemColor(
							SWT.COLOR_WHITE);
					createSimpleText(goal.toString(), color);
				}

			}
		} else {
			Color color = Display.getCurrent().getSystemColor(SWT.COLOR_GRAY);
			createSimpleText("No current goal", color);
		}
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
					String[] inputs = new String[textBoxes.size()];
					int i = 0;
					for (IEventBInputText text : textBoxes) {
						inputs[i++] = text.getTextWidget().getText();
					}
					((ProverUI) page.getEditor()).getUserSupport().applyTactic(
							tactic.getTactic(node, inputs));
				}

			});
			hyperlink.setToolTipText(tactic.getHint());
			hyperlink.setEnabled(enable);
		}

		return;
	}

}