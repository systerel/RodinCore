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
 *     Systerel - used label prefix set by user
 *     Systerel - replaced setFieldValues() with checkAndSetFieldValues()
 *     Systerel - add widget to edit theorem attribute
 *******************************************************************************/
package fr.systerel.editor.internal.dialogs;

import static java.util.Collections.singletonList;
import static org.eclipse.jface.dialogs.IDialogConstants.CANCEL_ID;
import static org.eclipse.jface.dialogs.IDialogConstants.CANCEL_LABEL;
import static org.eclipse.jface.dialogs.IDialogConstants.OK_ID;
import static org.eclipse.jface.dialogs.IDialogConstants.OK_LABEL;
import static org.eventb.core.EventBAttributes.ASSIGNMENT_ATTRIBUTE;
import static org.eventb.core.EventBAttributes.LABEL_ATTRIBUTE;
import static org.eventb.core.EventBAttributes.PREDICATE_ATTRIBUTE;

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eventb.core.IAction;
import org.eventb.core.IInvariant;
import org.eventb.core.IMachineRoot;
import org.eventb.core.IVariable;
import org.eventb.internal.ui.IEventBInputText;
import org.eventb.internal.ui.UIUtils;
import org.eventb.internal.ui.autocompletion.ProviderModifyListener;
import org.eventb.internal.ui.autocompletion.WizardProposalProvider;
import org.eventb.internal.ui.eventbeditor.EventBEditorUtils;
import org.eventb.internal.ui.eventbeditor.Triplet;
import org.eventb.ui.eventbeditor.IEventBEditor;
import org.rodinp.core.IAttributeType;
import org.rodinp.core.IInternalElementType;

import fr.systerel.editor.actions.IWizardElementMaker;

/**
 * @author htson
 *         <p>
 *         This class extends the Dialog class and provides an input dialog for
 *         creating a new variable along with its type invariant and
 *         initilisation.
 */
public class NewVariableDialog extends EventBDialog {

	private final String invPrefix;

	private String invIndex;

	private String identifierResult;

	private Collection<Triplet<String, String, Boolean>> invariantsResult;

	private String initLabelResult;

	private String initSubstitutionResult;

	private IEventBInputText identifierText;

	private Collection<Triplet<IEventBInputText, IEventBInputText, Button>> invariantsTexts;

	private IEventBInputText initLabelText;

	private IEventBInputText initSubstitutionText;

	private ProviderModifyListener providerListener;
	
	/**
	 * Constructor.
	 * 
	 * @param editor
	 *            the editor that made the call to this method
	 * @param root
	 *            the root element to which variable will be added
	 * @param parentShell
	 *            the parent shell of the dialog
	 * @param title
	 *            the title of the dialog
	 */
	public NewVariableDialog(IWizardElementMaker maker, String title,
			String invPrefix) {
		super(maker.getShell(), maker.getRoot(), title, maker);
		this.invPrefix = invPrefix;
		this.invIndex = getInvariantFirstIndex();
		invariantsTexts = new ArrayList<Triplet<IEventBInputText, IEventBInputText, Button>>();
	}

	private String getInvariantFirstIndex() {
		return UIUtils.getFreeElementLabelIndex(root, IInvariant.ELEMENT_TYPE,
				invPrefix);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.dialogs.Dialog#createButtonsForButtonBar(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, ADD_ID, ADD_LABEL);
		createButton(parent, MORE_INVARIANT_ID, MORE_INVARIANT_LABEL);
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
		setDebugBackgroundColor();
		setFormGridLayout(getBody(), 4);
		setFormGridData();

		providerListener = new ProviderModifyListener();
		
		createLabel(getBody(), "Identifier");
		identifierText = createBText(getBody(), EMPTY, 200, true, 3);
		addIdentifierAdapter(identifierText, IVariable.ELEMENT_TYPE,
				LABEL_ATTRIBUTE);
		
		createLabel(getBody(), "Initialisation");
		initLabelText = createNameInputText(getBody(),
				getFreeInitialisationActionName());
		addContentAdapter(initLabelText, IAction.ELEMENT_TYPE, LABEL_ATTRIBUTE);
		initSubstitutionText = createContentInputText(getBody(), 2);
		addContentAdapter(initSubstitutionText, IAction.ELEMENT_TYPE,
				ASSIGNMENT_ATTRIBUTE);
		identifierText.getTextWidget().addModifyListener(
				new ActionListener(initSubstitutionText.getTextWidget()));

		final Triplet<IEventBInputText, IEventBInputText, Button> invariant = createInvariant();
		addGuardListener(identifierText, invariant.getSecond());

		setText(identifierText, getFreeVariable());
		select(identifierText);
	}

	private Triplet<IEventBInputText, IEventBInputText, Button> createInvariant() {
		createLabel(getBody(), "Invariant");
		final IEventBInputText invariantNameText = createNameInputText(
				getBody(),
				getNewInvariantName(invIndex, invariantsTexts.size()));
		addContentAdapter(initLabelText, IInvariant.ELEMENT_TYPE,
				LABEL_ATTRIBUTE);
		final IEventBInputText invariantPredicateText = createContentInputText(getBody());
		addContentAdapter(initLabelText, IInvariant.ELEMENT_TYPE,
				PREDICATE_ATTRIBUTE);
		final Button button = createIsTheoremToogle(getBody());
		final Triplet<IEventBInputText, IEventBInputText, Button> p = newWidgetTriplet(
				invariantNameText, invariantPredicateText, button);
		invariantsTexts.add(p);
		return p;
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
			invariantsResult = null;
			initLabelResult = null;
			initSubstitutionResult = null;
		} else if (buttonId == MORE_INVARIANT_ID) {
			createInvariant();
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
	
	private String getNewInvariantName(String firstIndex, int num) {
		final int index = Integer.parseInt(firstIndex) + num;
		return invPrefix + index;
	}
	
	private void addValues() {
		getElementMaker().addValues(this);
	}

	private void initialise() {
		clearDirtyTexts();
		invIndex = getInvariantFirstIndex();

		int num = 0 ;
		for (Triplet<IEventBInputText, IEventBInputText, Button> triplet: invariantsTexts) {
			setText(triplet.getFirst(), getNewInvariantName(invIndex, num));
			setText(triplet.getSecond(), EMPTY);
			triplet.getThird().setSelection(false);
			num++;
		}	

		setText(initLabelText, getFreeInitialisationActionName());
		setText(initSubstitutionText, EMPTY);
		setText(identifierText, getFreeVariable());
		clearDirtyTexts();
		select(identifierText);
	}

	private String getFreeVariable() {
		return UIUtils.getFreeElementIdentifier(root, IVariable.ELEMENT_TYPE);
	}

	private String getFreeInitialisationActionName() {
		return EventBEditorUtils
				.getFreeInitialisationActionName((IMachineRoot) root);
	}
	
	private boolean checkAndSetFieldValues() {
		identifierResult = getText(identifierText);

		if (!checkNewIdentifiers(singletonList(identifierResult), true,
				root.getFormulaFactory())) {
			identifierResult = null;
			return false;
		}
		
		invariantsResult = new ArrayList<Triplet<String, String, Boolean>>();
		fillTripletResult(invariantsTexts, invariantsResult);
		if (dirtyTexts.contains(initSubstitutionText.getTextWidget())) {
			initLabelResult = getText(initLabelText);
			initSubstitutionResult = getText(initSubstitutionText);
		} else {
			initLabelResult = null;
			initSubstitutionResult = null;
		}
		return true;
	}

	/**
	 * Get the variable name.
	 * <p>
	 * 
	 * @return the variable name as input by the user
	 */
	public String getName() {
		return identifierResult;
	}

	/**
	 * Get invariants (name and predicate) as input by the user.
	 * 
	 * @return invariants as a collection of pairs (name, predicate); never
	 *         <code>null</code>, but might be empty
	 */
	public Collection<Triplet<String, String, Boolean>> getInvariants() {
		return invariantsResult;
	}

	/**
	 * Get the initialization action.
	 * 
	 * @return the initialization action as input by the user, or
	 *         <code>null</code> if the action was not modified
	 */
	public String getInitActionSubstitution() {
		return initSubstitutionResult;
	}

	/**
	 * Get the initialization action label.
	 * 
	 * @return the initialization action label as input by the user, or
	 *         <code>null</code> if the action label was not modified
	 */
	public String getInitActionName() {
		return initLabelResult;
	}

	@Override
	public boolean close() {
		identifierText.dispose();
		disposeTriplets(invariantsTexts);
		initSubstitutionText.dispose();
		return super.close();
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
