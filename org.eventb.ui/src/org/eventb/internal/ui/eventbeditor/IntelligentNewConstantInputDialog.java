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

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eventb.core.IAxiom;
import org.eventb.internal.ui.EventBMath;
import org.eventb.internal.ui.EventBText;
import org.eventb.internal.ui.IEventBInputText;
import org.eventb.internal.ui.UIUtils;
import org.eventb.ui.eventbeditor.IEventBEditor;
import org.rodinp.core.RodinDBException;

/**
 * @author htson
 *         <p>
 *         This class extends the Dialog class and provides an input dialog for
 *         creating a new variable along with its type axiom
 */
public class IntelligentNewConstantInputDialog extends EventBInputDialog {

	private String defaultName;

	private int axmIndex;

	private String axmPrefix;

	private String name;

	private Collection<Pair> axioms;

	private IEventBInputText nameText;

	private Collection<Pair> axiomPairTexts;

	private IEventBEditor editor;

	/**
	 * Constructor.
	 * <p>
	 * 
	 * @param parentShell
	 *            the parent shell of the dialog
	 * @param title
	 *            the title of the dialog
	 * @param defaultName
	 *            the default variable name
	 */
	public IntelligentNewConstantInputDialog(IEventBEditor editor,
			Shell parentShell, String title, String defaultName,
			String axmPrefix, int axmIndex) {
		super(parentShell, title);
		this.editor = editor;
		this.defaultName = defaultName;
		this.axmIndex = axmIndex;
		this.axmPrefix = axmPrefix;
		axiomPairTexts = new ArrayList<Pair>();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.dialogs.Dialog#createButtonsForButtonBar(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.YES_ID, "&More Axm.", true);

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

		GridLayout layout = new GridLayout();
		layout.numColumns = 3;
		layout.verticalSpacing = 10;
		layout.horizontalSpacing = 10;
		body.setLayout(layout);
		GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
		scrolledForm.setLayoutData(gd);

		Label label = toolkit.createLabel(body, "Name");
		label.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));

		nameText = new EventBText(toolkit.createText(body, ""));
		gd = new GridData(SWT.FILL, SWT.NONE, true, false);
		gd.horizontalSpan = 2;
		gd.widthHint = 200;
		nameText.getTextWidget().setLayoutData(gd);
		nameText.getTextWidget().addModifyListener(new DirtyStateListener());

		label = toolkit.createLabel(body, "Axiom");
		label.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));

		IEventBInputText axiomNameText = new EventBText(toolkit.createText(
				body, axmPrefix + axmIndex));

		gd = new GridData(SWT.FILL, SWT.NONE, false, false);
		gd.widthHint = 50;
		axiomNameText.getTextWidget().setLayoutData(gd);
		axiomNameText.getTextWidget().addModifyListener(new DirtyStateListener());

		IEventBInputText axiomPredicateText = new EventBMath(toolkit
				.createText(body, ""));
		gd = new GridData(SWT.FILL, SWT.NONE, true, false);
		gd.widthHint = 150;
		axiomPredicateText.getTextWidget().setLayoutData(gd);
		axiomPredicateText.getTextWidget().addModifyListener(
				new DirtyStateListener());
		nameText.getTextWidget().addModifyListener(
				new GuardListener(axiomPredicateText.getTextWidget()));

		axiomPairTexts.add(new Pair(axiomNameText, axiomPredicateText));

		nameText.getTextWidget().setText(defaultName);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.dialogs.Dialog#buttonPressed(int)
	 */
	@Override
	protected void buttonPressed(int buttonId) {
		if (buttonId == IDialogConstants.CANCEL_ID) {
			name = null;
			axioms = null;
		} else if (buttonId == IDialogConstants.YES_ID) {
			Composite body = scrolledForm.getBody();
			Label label = toolkit.createLabel(body, "Axiom");
			GridData gd = new GridData(SWT.FILL, SWT.FILL, false, false);
			label.setLayoutData(gd);

			try {
				axmIndex = UIUtils.getFreeElementLabelIndex(editor, editor
						.getRodinInput(), IAxiom.ELEMENT_TYPE, axmPrefix,
						axmIndex + 1);
			} catch (RodinDBException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			IEventBInputText axiomNameText = new EventBText(toolkit.createText(
					body, axmPrefix + axmIndex));
			gd = new GridData(SWT.FILL, SWT.NONE, false, false);
			axiomNameText.getTextWidget().setLayoutData(gd);
			axiomNameText.getTextWidget().addModifyListener(
					new DirtyStateListener());

			IEventBInputText axiomPredicateText = new EventBMath(toolkit
					.createText(body, ""));
			gd = new GridData(SWT.FILL, SWT.NONE, true, false);
			axiomPredicateText.getTextWidget().setLayoutData(gd);
			axiomPredicateText.getTextWidget().addModifyListener(
					new DirtyStateListener());

			axiomPairTexts.add(new Pair(axiomNameText, axiomPredicateText));

			updateSize();
		} else if (buttonId == IDialogConstants.OK_ID) {
			name = nameText.getTextWidget().getText();

			axioms = new ArrayList<Pair>();
			for (Pair pair : axiomPairTexts) {
				IEventBInputText axiomPredicateText = (IEventBInputText) pair
						.getSecond();
				IEventBInputText axiomNameText = (IEventBInputText) pair
						.getFirst();
				if (dirtyTexts.contains(axiomPredicateText.getTextWidget())) {
					String axmName = axiomNameText.getTextWidget().getText();
					String pred = axiomPredicateText.getTextWidget().getText();
					axioms.add(new Pair(axmName, pred));
				}
			}
		}
		super.buttonPressed(buttonId);
	}

	/**
	 * Get the variable name.
	 * <p>
	 * 
	 * @return the variable name as input by the user
	 */
	public String getName() {
		return name;
	}

	/**
	 * Get the axioms
	 * <p>
	 * 
	 * @return the axioms as input by the user
	 */
	public Collection<Pair> getAxioms() {
		return axioms;
	}

	@Override
	public boolean close() {
		nameText.dispose();
		for (Pair pair : axiomPairTexts) {
			IEventBInputText axiomPredicateText = (IEventBInputText) pair
					.getSecond();
			IEventBInputText axiomNameText = (IEventBInputText) pair.getFirst();
			axiomNameText.dispose();
			axiomPredicateText.dispose();
		}
		return super.close();
	}

}
