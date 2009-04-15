/*******************************************************************************
 * Copyright (c) 2005, 2008 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - used EventBSharedColor
 *     Systerel - added history support
 *     Systerel - separation of file and root element
 *     Systerel - increased index of label when add new input
 *******************************************************************************/
package org.eventb.internal.ui.eventbeditor.dialogs;

import static org.eclipse.jface.dialogs.IDialogConstants.CANCEL_ID;
import static org.eclipse.jface.dialogs.IDialogConstants.CANCEL_LABEL;
import static org.eclipse.jface.dialogs.IDialogConstants.OK_ID;
import static org.eclipse.jface.dialogs.IDialogConstants.OK_LABEL;

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eventb.core.IAxiom;
import org.eventb.core.IConstant;
import org.eventb.core.IContextRoot;
import org.eventb.internal.ui.IEventBInputText;
import org.eventb.internal.ui.Pair;
import org.eventb.internal.ui.UIUtils;
import org.eventb.internal.ui.eventbeditor.EventBEditorUtils;
import org.eventb.ui.eventbeditor.IEventBEditor;

/**
 * @author htson
 *         <p>
 *         This class extends the Dialog class and provides an input dialog for
 *         creating a new variable along with its type axiom
 */
public class NewConstantDialog extends EventBDialog {

	private final String axmPrefix;
	
	private String identifierResult;

	private IEventBInputText identifierText;

	private Collection<Pair<String,String>> axmResults;
	
	private Collection<Pair<IEventBInputText, IEventBInputText>> axiomTexts;
	
	private final IEventBEditor<IContextRoot> editor;

	private Composite composite;
	
	/**
	 * Constructor.
	 * <p>
	 * 
	 * @param parentShell
	 *            the parent shell of the dialog
	 * @param title
	 *            the title of the dialog
	 */
	public NewConstantDialog(IEventBEditor<IContextRoot> editor,
			Shell parentShell, String title) {
		super(parentShell, title);
		this.editor = editor;
		axmPrefix = getAxiomPrefix();
	}

	private String getAxiomPrefix() {
		final IContextRoot root = editor.getRodinInput();
		return UIUtils.getAutoNamePrefix(root, IAxiom.ELEMENT_TYPE);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.dialogs.Dialog#createButtonsForButtonBar(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, ADD_ID, ADD_LABEL);
		createButton(parent, MORE_AXIOM_ID, MORE_AXIOM_LABEL);
		createDefaultButton(parent, OK_ID, OK_LABEL);
		createButton(parent, CANCEL_ID, CANCEL_LABEL);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.dialogs.Dialog#createDialogArea(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected void createContents() {
		getBody().setLayout(new FillLayout());
		createDialogContents(getBody());
	}

	private void createDialogContents(Composite parent) {
		final IContextRoot root = editor.getRodinInput();
		composite = toolkit.createComposite(parent);
		setDebugBackgroundColor();

		setFormGridLayout(composite, 3);
		setFormGridData();

		axiomTexts = new ArrayList<Pair<IEventBInputText, IEventBInputText>>();

		createLabel(composite, "Identifier");

		final String cstIdentifier = UIUtils.getFreeElementIdentifier(root,
				IConstant.ELEMENT_TYPE);
		identifierText = createBText(composite, EMPTY, 200, true, 2);

		final Pair<IEventBInputText, IEventBInputText> axiom = createAxiom();
		addGuardListener(identifierText, axiom.getSecond());

		setText(identifierText, cstIdentifier);
		select(identifierText);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.dialogs.Dialog#buttonPressed(int)
	 */
	@Override
	protected void buttonPressed(int buttonId) {
		if (buttonId == CANCEL_ID) {
			identifierResult = null;
			axmResults = null;
		} else if (buttonId == MORE_AXIOM_ID) {
			createAxiom();
			updateSize();
		} else if (buttonId == OK_ID) {
			setFieldValues();
		} else if (buttonId == ADD_ID) {
			setFieldValues();
			addValues();
			initialise();
		}
		super.buttonPressed(buttonId);
	}

	private Pair<IEventBInputText, IEventBInputText> createAxiom(){
		createLabel(composite, "Axiom");
		final IEventBInputText axiomNameText = createNameInputText(composite,
				getNewAxiomName());
		final IEventBInputText axiomPredicateText = createContentInputText(composite);
		final Pair<IEventBInputText, IEventBInputText> p = newWidgetPair(
				axiomNameText, axiomPredicateText);
		axiomTexts.add(p);
		return p;
	}

	private String getNewAxiomName() {
		final String axmIndex = UIUtils.getFreeElementLabelIndex(editor
				.getRodinInput(), IAxiom.ELEMENT_TYPE, axmPrefix);
		final int index = Integer.parseInt(axmIndex) + axiomTexts.size();
		return axmPrefix + index;
	}
	
	private void addValues() {
		EventBEditorUtils.newConstant(editor, identifierResult,
				getAxiomNames(), getAxiomPredicates());
	}
	
	private void initialise() {
		clearDirtyTexts();
		composite.dispose();
		createDialogContents(getBody());
		scrolledForm.reflow(true);
	}

	private void setFieldValues() {
		identifierResult = identifierText.getTextWidget().getText();
		axmResults = new ArrayList<Pair<String, String>>();
		fillPairResult(axiomTexts, axmResults);
	}

	/**
	 * Get the variable name.
	 * <p>
	 * 
	 * @return the variable name as input by the user
	 */
	public String getIdentifier() {
		return identifierResult;
	}

	@Override
	public boolean close() {
		identifierText.dispose();
		disposePairs(axiomTexts);
		return super.close();
	}

	public String[] getAxiomNames() {
		return getFirst(axmResults);
	}

	public String[] getAxiomPredicates() {
		return getSecond(axmResults);
	}
}
