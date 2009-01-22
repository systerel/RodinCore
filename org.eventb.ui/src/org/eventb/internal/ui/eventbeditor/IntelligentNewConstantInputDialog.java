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
package org.eventb.internal.ui.eventbeditor;

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eventb.core.IAxiom;
import org.eventb.core.IConstant;
import org.eventb.core.IContextRoot;
import org.eventb.eventBKeyboard.Text2EventBMathTranslator;
import org.eventb.internal.ui.EventBSharedColor;
import org.eventb.internal.ui.EventBText;
import org.eventb.internal.ui.IEventBInputText;
import org.eventb.internal.ui.Pair;
import org.eventb.internal.ui.UIUtils;
import org.eventb.ui.eventbeditor.IEventBEditor;

/**
 * @author htson
 *         <p>
 *         This class extends the Dialog class and provides an input dialog for
 *         creating a new variable along with its type axiom
 */
public class IntelligentNewConstantInputDialog extends EventBInputDialog {

	private final String axmPrefix;
	
	String identifier;

	private IEventBInputText identifierText;

	Collection<String> axmLabels;
	
	Collection<String> axmSubs;
	
	private Collection<Pair<IEventBInputText, IEventBInputText>> axiomPairTexts;

	IEventBEditor<IContextRoot> editor;

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
	public IntelligentNewConstantInputDialog(IEventBEditor<IContextRoot> editor,
			Shell parentShell, String title) {
		super(parentShell, title);
		this.editor = editor;
		final IContextRoot root = editor.getRodinInput();
		axmPrefix = getAxiomPrefix(root);
	}

	private String getAxiomPrefix(IContextRoot root) {
		return UIUtils.getAutoNamePrefix(root, IAxiom.ELEMENT_TYPE);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.dialogs.Dialog#createButtonsForButtonBar(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.RETRY_ID, "&Add", false);

		createButton(parent, IDialogConstants.YES_ID, "&More Axm.", false);

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
		body.setLayout(new FillLayout());
		createDialogContents(body);

	}

	private void createDialogContents(Composite parent) {
		final IContextRoot root = editor.getRodinInput();
		composite = toolkit.createComposite(parent);
		if (EventBEditorUtils.DEBUG)
			composite.setBackground(EventBSharedColor.getSystemColor(
					SWT.COLOR_CYAN));

		axiomPairTexts = new ArrayList<Pair<IEventBInputText, IEventBInputText>>();
		GridLayout layout = new GridLayout();
		layout.numColumns = 3;
		layout.verticalSpacing = 10;
		layout.horizontalSpacing = 10;
		composite.setLayout(layout);
		GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
		scrolledForm.setLayoutData(gd);

		Label label = toolkit.createLabel(composite, "Identifier");
		label.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));

		identifierText = new EventBText(toolkit.createText(composite, ""));
		gd = new GridData(SWT.FILL, SWT.NONE, true, false);
		gd.horizontalSpan = 2;
		gd.widthHint = 200;
		Text textWidget = identifierText.getTextWidget();
		textWidget.setLayoutData(gd);
		textWidget.addModifyListener(new DirtyStateListener());

		label = toolkit.createLabel(composite, "Axiom");
		label.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));

		final IEventBInputText axiomNameText = getNameInputText(toolkit,
				composite, getNewAxiomName());
		final IEventBInputText axiomPredicateText = getContentInputText(
				toolkit, composite);
		axiomPairTexts.add(new Pair<IEventBInputText, IEventBInputText>(
				axiomNameText, axiomPredicateText));

		textWidget.addModifyListener(new GuardListener(axiomPredicateText
				.getTextWidget()));
		textWidget.setText(UIUtils.getFreeElementIdentifier(root,
				IConstant.ELEMENT_TYPE));
		textWidget.selectAll();
		textWidget.setFocus();
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
			axmLabels = null;
			axmSubs = null;
		} else if (buttonId == IDialogConstants.YES_ID) {
			Label label = toolkit.createLabel(composite, "Axiom");
			GridData gd = new GridData(SWT.FILL, SWT.FILL, false, false);
			label.setLayoutData(gd);

			final IEventBInputText axiomNameText = getNameInputText(toolkit,
					composite, getNewAxiomName());
			final IEventBInputText axiomPredicateText = getContentInputText(
					toolkit, composite);

			axiomPairTexts.add(new Pair<IEventBInputText, IEventBInputText>(
					axiomNameText, axiomPredicateText));

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

	private String getNewAxiomName() {
		final String axmIndex = UIUtils.getFreeElementLabelIndex(editor
				.getRodinInput(), IAxiom.ELEMENT_TYPE, axmPrefix);
		final int index = Integer.parseInt(axmIndex) + axiomPairTexts.size();
		return axmPrefix + index;
	}
	
	private void addValues() {
		final String[] axmNames = axmLabels
				.toArray(new String[axmLabels.size()]);
		final String[] lAxmSubs = axmSubs.toArray(new String[axmSubs.size()]);
		EventBEditorUtils.newConstant(editor, identifier, axmNames, lAxmSubs);
	}

	private void initialise() {
		clearDirtyTexts();
		composite.dispose();
		createDialogContents(scrolledForm.getBody());
		scrolledForm.reflow(true);
	}

	private void setFieldValues() {
		identifier = identifierText.getTextWidget().getText();

		axmLabels = new ArrayList<String>();
		axmSubs = new ArrayList<String>();
		for (Pair<IEventBInputText, IEventBInputText> pair : axiomPairTexts) {
			IEventBInputText axiomPredicateText = pair.getSecond();
			IEventBInputText axiomNameText = pair.getFirst();
			if (dirtyTexts.contains(axiomPredicateText.getTextWidget())) {
				String axmName = axiomNameText.getTextWidget().getText();
				String sub = Text2EventBMathTranslator
						.translate(axiomPredicateText.getTextWidget()
								.getText());
				axmLabels.add(axmName);
				axmSubs.add(sub);
			}
		}
	}

	/**
	 * Get the variable name.
	 * <p>
	 * 
	 * @return the variable name as input by the user
	 */
	public String getIdentifier() {
		return identifier;
	}

	@Override
	public boolean close() {
		identifierText.dispose();
		for (Pair<IEventBInputText, IEventBInputText> pair : axiomPairTexts) {
			IEventBInputText axiomPredicateText = pair.getSecond();
			IEventBInputText axiomNameText = pair.getFirst();
			axiomNameText.dispose();
			axiomPredicateText.dispose();
		}
		return super.close();
	}

	public String[] getAxiomNames() {
		return axmLabels.toArray(new String[axmLabels.size()]);
	}

	public String[] getAxiomPredicates() {
		return axmSubs.toArray(new String[axmSubs.size()]);
	}

}
