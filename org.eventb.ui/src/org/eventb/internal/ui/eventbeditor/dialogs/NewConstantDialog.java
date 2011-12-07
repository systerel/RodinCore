/*******************************************************************************
 * Copyright (c) 2005, 2011 ETH Zurich and others.
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
 *     Systerel - add widget to edit theorem attribute
 *     Systerel - refactored according to wizard refactoring
 *******************************************************************************/
package org.eventb.internal.ui.eventbeditor.dialogs;

import static java.util.Collections.singletonList;
import static org.eclipse.jface.dialogs.IDialogConstants.CANCEL_ID;
import static org.eclipse.jface.dialogs.IDialogConstants.CANCEL_LABEL;
import static org.eclipse.jface.dialogs.IDialogConstants.OK_ID;
import static org.eclipse.jface.dialogs.IDialogConstants.OK_LABEL;
import static org.eventb.core.EventBAttributes.LABEL_ATTRIBUTE;
import static org.eventb.core.EventBAttributes.PREDICATE_ATTRIBUTE;

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eventb.core.IAxiom;
import org.eventb.core.IConstant;
import org.eventb.core.IContextRoot;
import org.eventb.internal.ui.IEventBInputText;
import org.eventb.internal.ui.UIUtils;
import org.eventb.internal.ui.autocompletion.ProviderModifyListener;
import org.eventb.internal.ui.autocompletion.WizardProposalProvider;
import org.eventb.internal.ui.eventbeditor.Triplet;
import org.eventb.internal.ui.eventbeditor.wizards.EventBCreationWizards.NewConstantsWizard;
import org.eventb.internal.ui.preferences.PreferenceUtils;
import org.rodinp.core.IAttributeType;
import org.rodinp.core.IInternalElementType;

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

	private final Collection<Triplet<String, String, Boolean>> axmResults = new ArrayList<Triplet<String, String, Boolean>>();

	private Collection<Triplet<IEventBInputText, IEventBInputText, Button>> axiomTexts;
	
	private Composite composite;
	
	private ProviderModifyListener providerListener;

	private final NewConstantsWizard wizard;
	
	/**
	 * Constructor.
	 * 
	 * @param wizard
	 *            the parent wizard of this dialog
	 * @param root
	 *            the context root to which constants will be added
	 * @param parentShell
	 *            the parent shell of the dialog
	 * @param title
	 *            the title of the dialog
	 */
	public NewConstantDialog(NewConstantsWizard wizard, IContextRoot root,
			Shell parentShell, String title) {
		super(parentShell, root, title);
		this.wizard = wizard;
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

		setFormGridLayout(composite, 4);
		setFormGridData();

		axiomTexts = new ArrayList<Triplet<IEventBInputText, IEventBInputText, Button>>();

		providerListener = new ProviderModifyListener();
		createLabel(composite, "Identifier");

		final String cstIdentifier = UIUtils.getFreeElementIdentifier(root,
				IConstant.ELEMENT_TYPE);
		identifierText = createBText(composite, EMPTY, 200, true, 3);
		addIdentifierAdapter(identifierText, IConstant.ELEMENT_TYPE,
				LABEL_ATTRIBUTE);
		providerListener.addInputText(identifierText);
		
		final Triplet<IEventBInputText, IEventBInputText, Button> axiom = createAxiom();
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

	private Triplet<IEventBInputText, IEventBInputText, Button> createAxiom() {
		createLabel(composite, "Axiom");
		final IEventBInputText axiomNameText = createNameInputText(composite,
				getNewAxiomName());
		addContentAdapter(axiomNameText, IAxiom.ELEMENT_TYPE, LABEL_ATTRIBUTE);
		final IEventBInputText axiomPredicateText = createContentInputText(composite);
		addContentAdapter(axiomPredicateText, IAxiom.ELEMENT_TYPE,
				PREDICATE_ATTRIBUTE);
		final Button button = createIsTheoremToogle(composite);
		final Triplet<IEventBInputText, IEventBInputText, Button> p = newWidgetTriplet(
				axiomNameText, axiomPredicateText, button);
		axiomTexts.add(p);
		return p;
	}

	private String getNewAxiomName() {
		final String axmIndex = UIUtils.getFreeElementLabelIndex(root,
				IAxiom.ELEMENT_TYPE, axmPrefix);
		final int index = Integer.parseInt(axmIndex) + axiomTexts.size();
		return axmPrefix + index;
	}
	
	private void addValues() {
		wizard.getAndRegisterCreationOperation(this);
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
		
		fillTripletResult(axiomTexts, axmResults);
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
		disposeTriplets(axiomTexts);
		return super.close();
	}

	public String[] getAxiomNames() {
		return getFirstTriplet(axmResults);
	}

	public String[] getAxiomPredicates() {
		return getSecondTriplet(axmResults);
	}
	
	public boolean[] getAxiomIsTheorem() {
		return getThirdTriplet(axmResults);
	}
	
	private void addContentAdapter(IEventBInputText input,
			IInternalElementType<?> elementType, IAttributeType attributeType) {
		final WizardProposalProvider providerPar = getProposalProviderWithIdent(
				elementType, attributeType);
		addProposalAdapter(providerPar, input);
		providerListener.addProvider(providerPar);
	}
	
	private void addIdentifierAdapter(IEventBInputText input,
			IInternalElementType<?> elementType, IAttributeType attributeType) {
		providerListener.addInputText(input);
		addProposalAdapter(elementType, attributeType, input);
	}
}
