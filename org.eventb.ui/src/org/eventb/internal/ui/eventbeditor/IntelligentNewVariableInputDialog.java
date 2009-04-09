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
 *     Systerel - used label prefix set by user
 *******************************************************************************/
package org.eventb.internal.ui.eventbeditor;

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eventb.core.IAction;
import org.eventb.core.IInvariant;
import org.eventb.core.IMachineRoot;
import org.eventb.core.IVariable;
import org.eventb.eventBKeyboard.Text2EventBMathTranslator;
import org.eventb.internal.ui.EventBMath;
import org.eventb.internal.ui.EventBSharedColor;
import org.eventb.internal.ui.EventBText;
import org.eventb.internal.ui.IEventBInputText;
import org.eventb.internal.ui.Pair;
import org.eventb.internal.ui.UIUtils;
import org.eventb.ui.eventbeditor.IEventBEditor;
import org.rodinp.core.RodinDBException;

/**
 * @author htson
 *         <p>
 *         This class extends the Dialog class and provides an input dialog for
 *         creating a new variable along with its type invariant and
 *         initilisation.
 */
public class IntelligentNewVariableInputDialog extends EventBInputDialog {

	private String invPrefix;

	private String invIndex;

	private String identifier;

	private Collection<Pair<String, String>> invariants;

	private String initLabel;

	private String initSubstitution;

	private IEventBInputText identifierText;

	private Collection<Pair<IEventBInputText, IEventBInputText>> invariantPairTexts;

	private IEventBInputText initLabelText;

	private IEventBInputText initSubstitutionText;

	IEventBEditor<IMachineRoot> editor;

	/**
	 * Constructor.
	 * <p>
	 * 
	 * @param parentShell
	 *            the parent shell of the dialog
	 * @param title
	 *            the title of the dialog
	 */
	public IntelligentNewVariableInputDialog(IEventBEditor<IMachineRoot> editor,
			Shell parentShell, String title,
			String invPrefix) {
		super(parentShell, title);
		this.editor = editor;
		this.invPrefix = invPrefix;
		this.invIndex = UIUtils.getFreeElementLabelIndex(
				editor.getRodinInput(), IInvariant.ELEMENT_TYPE, invPrefix);
		invariantPairTexts = new ArrayList<Pair<IEventBInputText, IEventBInputText>>();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.dialogs.Dialog#createButtonsForButtonBar(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.RETRY_ID, "&Add", true);
		createButton(parent, IDialogConstants.YES_ID, "&More Inv.", true);

		createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL,
				true);

		createButton(parent, IDialogConstants.CANCEL_ID,
				IDialogConstants.CANCEL_LABEL, false);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.dialogs.Dialog#createDialogArea(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected void createContents() {
		final IMachineRoot root = editor.getRodinInput();
		Composite body = scrolledForm.getBody();
		if (EventBEditorUtils.DEBUG)
			body.setBackground(EventBSharedColor.getSystemColor(
					SWT.COLOR_CYAN));

		GridLayout layout = new GridLayout();
		layout.numColumns = 3;
		layout.verticalSpacing = 10;
		layout.horizontalSpacing = 10;
		body.setLayout(layout);
		GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
		scrolledForm.setLayoutData(gd);

		Label label = toolkit.createLabel(body, "Identifier");
		label.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));

		identifierText = new EventBText(toolkit.createText(body, ""));
		gd = new GridData(SWT.FILL, SWT.FILL, true, false);
		gd.horizontalSpan = 2;
		gd.widthHint = 200;
		identifierText.getTextWidget().setLayoutData(gd);
		identifierText.getTextWidget().addModifyListener(new DirtyStateListener());

		label = toolkit.createLabel(body, "Initialisation");
		label.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));

		String actLabel = UIUtils.getAutoNamePrefix(root, IAction.ELEMENT_TYPE);
		try {
			actLabel = EventBEditorUtils.getFreeInitialisationActionName(root);
		} catch (RodinDBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		initLabelText = new EventBText(toolkit.createText(body, actLabel));
		gd = new GridData(SWT.FILL, SWT.FILL, false, false);
		gd.widthHint = 50;
		initLabelText.getTextWidget().setLayoutData(gd);
		initLabelText.getTextWidget()
				.addModifyListener(new DirtyStateListener());

		initSubstitutionText = new EventBMath(toolkit.createText(body, ""));
		gd = new GridData(SWT.FILL, SWT.FILL, true, false);
		gd.widthHint = 150;
		initSubstitutionText.getTextWidget().setLayoutData(gd);
		initSubstitutionText.getTextWidget().addModifyListener(
				new DirtyStateListener());
		identifierText.getTextWidget().addModifyListener(
				new ActionListener(initSubstitutionText.getTextWidget()));

		label = toolkit.createLabel(body, "Invariant");
		label.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));

		final IEventBInputText invariantNameText = getNameInputText(toolkit,
				scrolledForm.getBody(), getNewInvariantName(invPrefix,
						invIndex, 0));
		final IEventBInputText invariantPredicateText = getContentInputText(
				toolkit, scrolledForm.getBody());
		invariantPairTexts.add(new Pair<IEventBInputText, IEventBInputText>(
				invariantNameText, invariantPredicateText));

		identifierText.getTextWidget().addModifyListener(
				new GuardListener(invariantPredicateText.getTextWidget()));

		Text nameTextWidget = identifierText.getTextWidget();
		nameTextWidget.setText(UIUtils.getFreeElementIdentifier(root,
				IVariable.ELEMENT_TYPE));
		nameTextWidget.selectAll();
		nameTextWidget.setFocus();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.dialogs.Dialog#buttonPressed(int)
	 */
	@Override
	protected void buttonPressed(int buttonId) {
		if (buttonId == IDialogConstants.CANCEL_ID) {
			identifier = null;
			invariants = null;
			initLabel = null;
			initSubstitution = null;
		} else if (buttonId == IDialogConstants.YES_ID) {
			Composite body = scrolledForm.getBody();
			Label label = toolkit.createLabel(body, "Invariant");
			GridData gd = new GridData(SWT.FILL, SWT.FILL, false, false);
			label.setLayoutData(gd);

			final IEventBInputText invariantNameText = getNameInputText(
					toolkit, scrolledForm.getBody(), getNewInvariantName(
							invPrefix, invIndex, invariantPairTexts.size()));
			final IEventBInputText invariantPredicateText = getContentInputText(
					toolkit, scrolledForm.getBody());
			invariantPairTexts
					.add(new Pair<IEventBInputText, IEventBInputText>(
							invariantNameText, invariantPredicateText));

			updateSize();
		} else if (buttonId == IDialogConstants.OK_ID) {
			setFieldValues();
		} else if (buttonId == IDialogConstants.RETRY_ID) {
			setFieldValues();
			addValues();
			initialise();
		}
		super.buttonPressed(buttonId);
	}
	
	private String getNewInvariantName(String prefix, String firstIndex, int num) {
		final int index = Integer.parseInt(firstIndex) + num;
		return prefix + index;
	}
	
	private void addValues() {

		final String varName = getName();
		final Collection<Pair<String, String>> invariant = getInvariants();
		final String actName = getInitActionName();
		final String actSub = getInitActionSubstitution();
		EventBEditorUtils.newVariable(editor, varName, invariant, actName,
				actSub);

	}

	private void initialise() {
		final IMachineRoot root = editor.getRodinInput();
		clearDirtyTexts();
		invIndex = UIUtils.getFreeElementLabelIndex(root,
				IInvariant.ELEMENT_TYPE, invPrefix);
		int num = 0 ;
		for (Pair<IEventBInputText, IEventBInputText> pair : invariantPairTexts) {
			IEventBInputText invariantPredicateText = pair.getSecond();
			IEventBInputText invariantNameText = pair.getFirst();

			invariantNameText.getTextWidget().setText(
					getNewInvariantName(invPrefix, invIndex, num));
			invariantPredicateText.getTextWidget().setText("");
			num++;
		}
		String actionName = UIUtils.getAutoNamePrefix(root, IAction.ELEMENT_TYPE);
		try {
			actionName = EventBEditorUtils.getFreeInitialisationActionName(root);
		} catch (RodinDBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		initLabelText.getTextWidget().setText(actionName);
		initSubstitutionText.getTextWidget().setText("");
		Text nameTextWidget = identifierText.getTextWidget();
		nameTextWidget.setText(UIUtils.getFreeElementIdentifier(root,
				IVariable.ELEMENT_TYPE));
		nameTextWidget.selectAll();
		nameTextWidget.setFocus();
	}

	private void setFieldValues() {
		identifier = identifierText.getTextWidget().getText();

		invariants = new ArrayList<Pair<String, String>>();
		for (Pair<IEventBInputText, IEventBInputText> pair : invariantPairTexts) {
			IEventBInputText invariantPredicateText = pair.getSecond();
			IEventBInputText invariantNameText = pair.getFirst();
			if (dirtyTexts.contains(invariantPredicateText.getTextWidget())) {
				String invName = invariantNameText.getTextWidget()
						.getText();
				String pred = Text2EventBMathTranslator
						.translate(invariantPredicateText.getTextWidget()
								.getText());
				invariants.add(new Pair<String, String>(invName, pred));
			}
		}
		if (dirtyTexts.contains(initSubstitutionText.getTextWidget())) {
			initLabel = initLabelText.getTextWidget().getText();
			initSubstitution = initSubstitutionText.getTextWidget()
					.getText();
		} else {
			initLabel = null;
			initSubstitution = null;
		}
	}

	/**
	 * Get the variable name.
	 * <p>
	 * 
	 * @return the variable name as input by the user
	 */
	public String getName() {
		return identifier;
	}

	/**
	 * Get the invariant name.
	 * <p>
	 * 
	 * @return the invariant name as input by the user
	 */
	public Collection<Pair<String, String>> getInvariants() {
		return invariants;
	}

	/**
	 * Get the initialization action.
	 * <p>
	 * 
	 * @return the initialization action as input by the user
	 */
	public String getInitActionSubstitution() {
		return initSubstitution;
	}

	public String getInitActionName() {
		return initLabel;
	}

	@Override
	public boolean close() {
		identifierText.dispose();
		for (Pair<IEventBInputText, IEventBInputText> pair : invariantPairTexts) {
			IEventBInputText invariantPredicateText = pair.getSecond();
			IEventBInputText invariantNameText = pair.getFirst();
			invariantNameText.dispose();
			invariantPredicateText.dispose();
		}
		initSubstitutionText.dispose();
		return super.close();
	}

}
