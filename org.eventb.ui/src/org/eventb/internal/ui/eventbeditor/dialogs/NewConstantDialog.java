/*******************************************************************************
 * Copyright (c) 2005, 2010 ETH Zurich and others.
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
 *     Systerel - replaced setFieldValues() with checkAndSetFieldValues()
 *******************************************************************************/
package org.eventb.internal.ui.eventbeditor.dialogs;

import static java.util.Collections.singletonList;
import static org.eclipse.jface.dialogs.IDialogConstants.CANCEL_ID;
import static org.eclipse.jface.dialogs.IDialogConstants.CANCEL_LABEL;
import static org.eclipse.jface.dialogs.IDialogConstants.OK_ID;
import static org.eclipse.jface.dialogs.IDialogConstants.OK_LABEL;
import static org.eventb.core.EventBAttributes.PREDICATE_ATTRIBUTE;

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
import org.eventb.internal.ui.preferences.PreferenceUtils;
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

	private final Collection<Pair<String, String>> axmResults = new ArrayList<Pair<String, String>>();
	
	private Collection<Pair<IEventBInputText, IEventBInputText>> axiomTexts;
	
	private final IEventBEditor<IContextRoot> editor;

	private Composite composite;
	
	/**
	 * Constructor.
	 * 
	 * @param editor
	 *            the editor that called this dialog
	 * @param root
	 *            the context root to which constants will be added
	 * @param parentShell
	 *            the parent shell of the dialog
	 * @param title
	 *            the title of the dialog
	 */
	public NewConstantDialog(IEventBEditor<IContextRoot> editor, IContextRoot root,
			Shell parentShell, String title) {
		super(parentShell, root, title);
		this.editor = editor;
		axmPrefix = getAxiomPrefix();
	}

	private String getAxiomPrefix() {
		return PreferenceUtils.getAutoNamePrefix(root, IAxiom.ELEMENT_TYPE);
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
			axmResults.clear();
		} else if (buttonId == MORE_AXIOM_ID) {
			createAxiom();
			updateSize();
		} else if (buttonId == OK_ID) {
			if (!checkAndSetFieldValues()) {
				return;
			}
		} else if (buttonId == ADD_ID) {
			if (!checkAndSetFieldValues()) {
				return;
			}
			addValues();
			initialise();
		}
		super.buttonPressed(buttonId);
	}

	private Pair<IEventBInputText, IEventBInputText> createAxiom(){
		createLabel(composite, "Axiom");
		final IEventBInputText axiomNameText = createNameInputText(composite,
				getNewAxiomName());
		final IEventBInputText axiomPredicateText = createContentInputText(
				composite, IAxiom.ELEMENT_TYPE, PREDICATE_ATTRIBUTE);
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

	private boolean checkAndSetFieldValues() {
		identifierResult = identifierText.getTextWidget().getText();
		axmResults.clear();
		if (!checkNewIdentifiers(singletonList(identifierResult), true, root.getFormulaFactory())) {
			identifierResult = null;
			return false;
		}
		
		fillPairResult(axiomTexts, axmResults);
		return true;
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
