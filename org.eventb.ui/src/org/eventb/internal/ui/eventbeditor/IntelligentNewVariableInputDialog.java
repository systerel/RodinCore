/*******************************************************************************
 * Copyright (c) 2005-2006 ETH Zurich.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Rodin @ ETH Zurich
 ******************************************************************************/

package org.eventb.internal.ui.eventbeditor;

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eventb.core.IInvariant;
import org.eventb.core.IMachineFile;
import org.eventb.core.IVariable;
import org.eventb.eventBKeyboard.Text2EventBMathTranslator;
import org.eventb.internal.ui.EventBMath;
import org.eventb.internal.ui.EventBText;
import org.eventb.internal.ui.IEventBInputText;
import org.eventb.internal.ui.Pair;
import org.eventb.internal.ui.UIUtils;
import org.eventb.internal.ui.eventbeditor.actions.PrefixVarName;
import org.eventb.ui.eventbeditor.IEventBEditor;
import org.rodinp.core.RodinCore;
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

	private int invIndex;

	private String identifier;

	private Collection<Pair<String, String>> invariants;

	private String initLabel;

	private String initSubstitution;

	private IEventBInputText identifierText;

	private Collection<Pair<IEventBInputText, IEventBInputText>> invariantPairTexts;

	private IEventBInputText initLabelText;

	private IEventBInputText initSubstitutionText;

	IEventBEditor<IMachineFile> editor;

	/**
	 * Constructor.
	 * <p>
	 * 
	 * @param parentShell
	 *            the parent shell of the dialog
	 * @param title
	 *            the title of the dialog
	 */
	public IntelligentNewVariableInputDialog(IEventBEditor<IMachineFile> editor,
			Shell parentShell, String title,
			String invPrefix, int invIndex) {
		super(parentShell, title);
		this.editor = editor;
		this.invIndex = invIndex;
		this.invPrefix = invPrefix;
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
		Composite body = scrolledForm.getBody();
		if (EventBEditorUtils.DEBUG)
			body.setBackground(body.getDisplay().getSystemColor(
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

		String actLabel = "act";
		try {
			actLabel = EventBEditorUtils.getFreeInitialisationActionName(editor);
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

		IEventBInputText invariantNameText = new EventBText(toolkit.createText(
				body, invPrefix + invIndex));

		gd = new GridData(SWT.FILL, SWT.FILL, false, false);
		gd.widthHint = 50;
		invariantNameText.getTextWidget().setLayoutData(gd);
		invariantNameText.getTextWidget().addModifyListener(
				new DirtyStateListener());

		IEventBInputText invariantPredicateText = new EventBMath(toolkit
				.createText(body, ""));
		gd = new GridData(SWT.FILL, SWT.FILL, true, false);
		gd.widthHint = 150;
		invariantPredicateText.getTextWidget().setLayoutData(gd);
		invariantPredicateText.getTextWidget().addModifyListener(
				new DirtyStateListener());
		identifierText.getTextWidget().addModifyListener(
				new GuardListener(invariantPredicateText.getTextWidget()));

		invariantPairTexts.add(new Pair<IEventBInputText, IEventBInputText>(
				invariantNameText, invariantPredicateText));

		Text nameTextWidget = identifierText.getTextWidget();
		String varName = "var";
		try {
			varName = UIUtils.getFreeElementIdentifier(editor,
					editor.getRodinInput(), IVariable.ELEMENT_TYPE,
					PrefixVarName.DEFAULT_PREFIX);
		} catch (RodinDBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		nameTextWidget.setText(varName);
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

			try {
				invIndex = UIUtils.getFreeElementLabelIndex(editor, editor
						.getRodinInput(), IInvariant.ELEMENT_TYPE, invPrefix,
						invIndex + 1);
			} catch (RodinDBException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			IEventBInputText invariantNameText = new EventBText(toolkit
					.createText(body, invPrefix + invIndex));

			gd = new GridData(SWT.FILL, SWT.FILL, false, false);
			invariantNameText.getTextWidget().setLayoutData(gd);
			invariantNameText.getTextWidget().addModifyListener(
					new DirtyStateListener());

			IEventBInputText invariantPredicateText = new EventBMath(toolkit
					.createText(body, ""));
			gd = new GridData(SWT.FILL, SWT.NONE, true, false);
			invariantPredicateText.getTextWidget().setLayoutData(gd);
			invariantPredicateText.getTextWidget().addModifyListener(
					new DirtyStateListener());

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

	private void addValues() {
		try {
			RodinCore.run(new IWorkspaceRunnable() {
				
				public void run(IProgressMonitor monitor) throws CoreException {
					EventBEditorUtils.createNewVariable(editor, getName(),
							monitor);
					EventBEditorUtils.createNewInvariant(editor, getInvariants(),
							monitor);

					String actName = getInitActionName();
					String actSub = getInitActionSubstitution();
					EventBEditorUtils.createNewInitialisationAction(editor, actName,
							actSub, monitor);
				}
				
			}, new NullProgressMonitor());
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void initialise() {
		clearDirtyTexts();
		for (Pair<IEventBInputText, IEventBInputText> pair : invariantPairTexts) {
			IEventBInputText invariantPredicateText = pair.getSecond();
			IEventBInputText invariantNameText = pair.getFirst();
			try {
				invIndex = UIUtils.getFreeElementLabelIndex(editor, editor
						.getRodinInput(), IInvariant.ELEMENT_TYPE, invPrefix,
						invIndex + 1);
			} catch (RodinDBException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			invariantNameText.getTextWidget().setText(invPrefix + invIndex);
			invariantPredicateText.getTextWidget().setText("");
		}
		String actionName = "act";
		try {
			actionName = EventBEditorUtils.getFreeInitialisationActionName(editor);
		} catch (RodinDBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		initLabelText.getTextWidget().setText(actionName);
		initSubstitutionText.getTextWidget().setText("");
		Text nameTextWidget = identifierText.getTextWidget();
		String varName = "var";
		try {
			varName = UIUtils.getFreeElementIdentifier(editor,
					editor.getRodinInput(), IVariable.ELEMENT_TYPE,
					PrefixVarName.DEFAULT_PREFIX);
		} catch (RodinDBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		nameTextWidget.setText(varName);
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
	 * Get the initialisation action.
	 * <p>
	 * 
	 * @return the initialisation action as input by the user
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
