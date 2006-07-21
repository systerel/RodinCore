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

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eventb.internal.ui.EventBMath;
import org.eventb.internal.ui.EventBText;
import org.eventb.internal.ui.IEventBInputText;

/**
 * @author htson
 *         <p>
 *         This class extends the Dialog class and provides an input dialog for
 *         creating a new variable along with its type invariant and
 *         initilisation.
 */
public class IntelligentNewVariableInputDialog extends Dialog {

	private String defaultName;

	private String defaultInvariantName;

	private String defaultInitName;

	private String name;

	private String invariantName;

	private String invariantPredicate;

	private String initName;

	private String initSubstitution;

	private IEventBInputText nameText;

	private IEventBInputText invariantNameText;

	private IEventBInputText invariantPredicateText;

	private IEventBInputText initNameText;

	private IEventBInputText initSubstitutionText;

	private ScrolledForm scrolledForm;

	private String title;

	private boolean newInit;

	private boolean newInv;

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
	 * @param defaultInvariantName
	 *            the default invariant name
	 */
	public IntelligentNewVariableInputDialog(Shell parentShell, String title,
			String defaultName, String defaultInvariantName,
			String defaultInitName) {
		super(parentShell);
		this.title = title;
		this.defaultName = defaultName;
		this.defaultInvariantName = defaultInvariantName;
		this.defaultInitName = defaultInitName;
		setShellStyle(getShellStyle() | SWT.RESIZE);
		newInit = true;
		newInv = true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.window.Window#configureShell(org.eclipse.swt.widgets.Shell)
	 */
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText(title);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.dialogs.Dialog#createButtonsForButtonBar(org.eclipse.swt.widgets.Composite)
	 */
	protected void createButtonsForButtonBar(Composite parent) {
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
	protected Control createDialogArea(Composite parent) {
		Composite composite = (Composite) super.createDialogArea(parent);

		FormToolkit toolkit = new FormToolkit(parent.getDisplay());
		toolkit.setBackground(parent.getBackground());
		toolkit.setBorderStyle(SWT.BORDER);

		scrolledForm = toolkit.createScrolledForm(composite);
		Composite body = scrolledForm.getBody();

		GridLayout layout = new GridLayout();
		layout.numColumns = 3;
		layout.verticalSpacing = 10;
		layout.horizontalSpacing = 10;
		body.setLayout(layout);
		GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
		scrolledForm.setLayoutData(gd);

		toolkit.createLabel(body, "Name");

		nameText = new EventBText(toolkit.createText(body, ""));
		gd = new GridData(SWT.FILL, SWT.NONE, true, false);
		gd.horizontalSpan = 2;
		gd.widthHint = 200;
		nameText.getTextWidget().setLayoutData(gd);
		nameText.getTextWidget().addModifyListener(new ModifyListener() {

			public void modifyText(ModifyEvent e) {
				String var = nameText.getTextWidget().getText();
				if (newInv) {
					invariantPredicateText.getTextWidget().setText(
							var + " \u2208 ");
				}
				if (newInit) {
					initSubstitutionText.getTextWidget().setText(
							var + " \u2254 ");
				}
			}

		});

		toolkit.createLabel(body, "Invariant");

		invariantNameText = new EventBText(toolkit.createText(body,
				defaultInvariantName));
		gd = new GridData(SWT.FILL, SWT.NONE, false, false);
		gd.widthHint = 50;
		invariantNameText.getTextWidget().setLayoutData(gd);

		invariantPredicateText = new EventBMath(toolkit.createText(body, ""));
		gd = new GridData(SWT.FILL, SWT.NONE, true, false);
		gd.widthHint = 150;
		invariantPredicateText.getTextWidget().setLayoutData(gd);
		invariantPredicateText.getTextWidget().addModifyListener(
				new ModifyListener() {

					public void modifyText(ModifyEvent e) {
						if (invariantPredicateText.getTextWidget()
								.isFocusControl())
							newInv = false;
					}

				});

		toolkit.createLabel(body, "Initialisation");

		initNameText = new EventBText(toolkit.createText(body,
				defaultInitName));
		gd = new GridData(SWT.FILL, SWT.NONE, false, false);
		gd.widthHint = 50;
		initNameText.getTextWidget().setLayoutData(gd);

		initSubstitutionText = new EventBMath(toolkit.createText(body, ""));
		gd = new GridData(SWT.FILL, SWT.NONE, true, false);
		gd.horizontalSpan = 1;
		gd.widthHint = 150;
		initSubstitutionText.getTextWidget().setLayoutData(gd);
		initSubstitutionText.getTextWidget().addModifyListener(
				new ModifyListener() {

					public void modifyText(ModifyEvent e) {
						if (initSubstitutionText.getTextWidget()
								.isFocusControl())
							newInit = false;
					}

				});
		nameText.getTextWidget().setText(defaultName);
		composite.pack();

		toolkit.paintBordersFor(body);
		applyDialogFont(body);
		return body;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.dialogs.Dialog#buttonPressed(int)
	 */
	protected void buttonPressed(int buttonId) {
		if (buttonId == IDialogConstants.CANCEL_ID) {
			name = null;
			invariantName = null;
			invariantPredicate = null;
			initName = null;
			initSubstitution = null;
		} else if (buttonId == IDialogConstants.OK_ID) {
			name = nameText.getTextWidget().getText();
			if (newInv) {
				invariantName = null;
				invariantPredicate = null;
			}
			else {
				invariantName = invariantNameText.getTextWidget().getText();
				invariantPredicate = invariantPredicateText.getTextWidget()
				.getText();
			}
			if (newInit) {
				initName = null;
				initSubstitution = null;
			}
			else {
				initName = initNameText.getTextWidget().getText();
				initSubstitution = initSubstitutionText.getTextWidget().getText();
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
	 * Get the invariant name.
	 * <p>
	 * 
	 * @return the invariant name as input by the user
	 */
	public String getInvariantName() {
		return invariantName;
	}

	/**
	 * Get the invariant predicate.
	 * <p>
	 * 
	 * @return the invariant predicate as input by the user
	 */
	public String getInvariantPredicate() {
		return invariantPredicate;
	}

	/**
	 * Get the initialisation action.
	 * <p>
	 * 
	 * @return the initialisation action as input by the user
	 */
	public String getInitSubstitution() {
		return initSubstitution;
	}
	
	public String getInitName() {
		return initName;
	}

	@Override
	public boolean close() {
		nameText.dispose();
		invariantNameText.dispose();
		invariantPredicateText.dispose();
		initSubstitutionText.dispose();
		return super.close();
	}

}
