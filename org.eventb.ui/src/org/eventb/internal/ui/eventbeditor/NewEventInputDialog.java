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
import java.util.HashSet;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eventb.eventBKeyboard.Text2EventBMathTranslator;
import org.eventb.internal.ui.EventBMath;
import org.eventb.internal.ui.EventBText;
import org.eventb.internal.ui.IEventBInputText;

/**
 * @author htson
 *         <p>
 *         This class extends the Dialog class and provides an input dialog for
 *         new event with some local varialbes, guards and actSubstitutions.
 */
public class NewEventInputDialog extends EventBInputDialog {
	private String defaultName;

	private String name;

	private Collection<String> varNames;

	private Collection<String> grdNames;

	private Collection<String> grdPredicates;

	private Collection<String> actNames;

	private Collection<String> actSubstitutions;

	private IEventBInputText nameText;

	private Collection<IEventBInputText> varNameTexts;

	private Collection<IEventBInputText> grdNameTexts;

	private Collection<IEventBInputText> grdPredicateTexts;

	private Collection<IEventBInputText> actNameTexts;

	private Collection<IEventBInputText> actSubstitutionTexts;

	private Composite varComposite;

	private Composite grdNameComposite;

	private Composite grdPredComposite;

	private Composite actNameComposite;

	private Composite actSubComposite;

	private int grdCount;

	private int varCount;

	private int actCount;

	/**
	 * Constructor.
	 * <p>
	 * 
	 * @param parentShell
	 *            the parent shell of the dialog
	 * @param title
	 *            the title of the dialog
	 * @param defaultName
	 *            the default name for the event
	 */
	public NewEventInputDialog(Shell parentShell, String title,
			String defaultName) {
		super(parentShell, title);
		this.defaultName = defaultName;
		varNames = new HashSet<String>();
		grdNames = new HashSet<String>();
		grdPredicates = new HashSet<String>();
		actNames = new HashSet<String>();
		actSubstitutions = new HashSet<String>();
		varNameTexts = new ArrayList<IEventBInputText>();
		grdNameTexts = new ArrayList<IEventBInputText>();
		grdPredicateTexts = new ArrayList<IEventBInputText>();
		actNameTexts = new ArrayList<IEventBInputText>();
		actSubstitutionTexts = new ArrayList<IEventBInputText>();
		dirtyTexts = new HashSet<Text>();
		setShellStyle(getShellStyle() | SWT.RESIZE);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.dialogs.Dialog#createButtonsForButtonBar(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.YES_ID, "More &Var.", false);

		createButton(parent, IDialogConstants.NO_ID, "More &Grd.", false);

		createButton(parent, IDialogConstants.YES_TO_ALL_ID, "More &Act.",
				false);

		createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL,
				true);

		createButton(parent, IDialogConstants.CANCEL_ID,
				IDialogConstants.CANCEL_LABEL, false);
	}

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

		Label label = toolkit.createLabel(body, "Name", SWT.LEFT);
		gd = new GridData(SWT.FILL, SWT.NONE, false, false);
		label.setLayoutData(gd);

		Composite separator = toolkit.createComposite(body);
		gd = new GridData(SWT.NONE, SWT.NONE, false, false);
		gd.widthHint = 30;
		gd.heightHint = 20;
		separator.setLayoutData(gd);

		label = toolkit.createLabel(body, "Variable name(s)", SWT.CENTER);
		gd = new GridData(SWT.FILL, SWT.NONE, false, false);
		label.setLayoutData(gd);

		Composite comp = toolkit.createComposite(body);
		gd = new GridData(SWT.FILL, SWT.NONE, true, false);
		gd.widthHint = 50;
		comp.setLayoutData(gd);
		layout = new GridLayout();
		layout.verticalSpacing = 0;
		layout.horizontalSpacing = 10;
		layout.numColumns = 1;
		comp.setLayout(layout);

		nameText = new EventBText(toolkit.createText(comp, defaultName));
		gd = new GridData(SWT.FILL, SWT.NONE, true, false);
		nameText.getTextWidget().setLayoutData(gd);
		nameText.getTextWidget().addModifyListener(new DirtyStateListener());

		separator = toolkit.createComposite(body);
		gd = new GridData(SWT.NONE, SWT.NONE, false, false);
		gd.widthHint = 30;
		gd.heightHint = 20;
		separator.setLayoutData(gd);

		varComposite = toolkit.createComposite(body);
		gd = new GridData(SWT.FILL, SWT.NONE, true, false);
		gd.widthHint = 190;
		varComposite.setLayoutData(gd);
		layout = new GridLayout();
		layout.verticalSpacing = 0;
		layout.horizontalSpacing = 10;
		layout.makeColumnsEqualWidth = true;
		varComposite.setLayout(layout);

		separator = toolkit.createCompositeSeparator(body);
		GridData separatorGD = new GridData();
		separatorGD.heightHint = 5;
		separatorGD.horizontalSpan = 3;
		separator.setLayoutData(separatorGD);

		label = toolkit.createLabel(body, "Guard name(s)", SWT.LEFT);
		gd = new GridData(SWT.FILL, SWT.NONE, false, false);
		label.setLayoutData(gd);

		separator = toolkit.createComposite(body);
		gd = new GridData(SWT.NONE, SWT.NONE, false, false);
		gd.widthHint = 30;
		gd.heightHint = 20;
		separator.setLayoutData(gd);

		label = toolkit.createLabel(body, "Guard predicate(s)", SWT.LEFT);
		gd = new GridData(SWT.FILL, SWT.NONE, true, false);
		gd.horizontalSpan = 1;
		label.setLayoutData(gd);

		grdNameComposite = toolkit.createComposite(body);
		gd = new GridData(SWT.FILL, SWT.NONE, false, false);
		grdNameComposite.setLayoutData(gd);
		layout = new GridLayout();
		layout.numColumns = 1;
		layout.verticalSpacing = 10;
		grdNameComposite.setLayout(layout);

		separator = toolkit.createComposite(body);
		gd = new GridData(SWT.NONE, SWT.NONE, false, false);
		gd.widthHint = 30;
		gd.heightHint = 20;
		separator.setLayoutData(gd);

		grdPredComposite = toolkit.createComposite(body);
		gd = new GridData(SWT.FILL, SWT.NONE, true, false);
		grdPredComposite.setLayoutData(gd);
		layout = new GridLayout();
		layout.numColumns = 1;
		layout.verticalSpacing = 10;
		grdPredComposite.setLayout(layout);

		for (int i = 1; i <= 3; i++) {
			final IEventBInputText varText = new EventBText(toolkit.createText(
					varComposite, ""));
			gd = new GridData(SWT.FILL, SWT.NONE, true, false);
			gd.widthHint = 30;
			varText.getTextWidget().setLayoutData(gd);
			varText.getTextWidget().addModifyListener(new DirtyStateListener());
			varNameTexts.add(varText);

			IEventBInputText text = new EventBText(toolkit.createText(
					grdNameComposite, "grd" + i));
			gd = new GridData(SWT.FILL, SWT.NONE, true, false);
			text.getTextWidget().setLayoutData(gd);
			text.getTextWidget().addModifyListener(new DirtyStateListener());
			grdNameTexts.add(text);

			separator = toolkit.createComposite(body);
			gd = new GridData(SWT.NONE, SWT.NONE, false, false);
			gd.widthHint = 30;
			gd.heightHint = 20;
			separator.setLayoutData(gd);

			final IEventBInputText grdText = new EventBMath(toolkit.createText(
					grdPredComposite, ""));
			gd = new GridData(SWT.FILL, SWT.NONE, true, false);
			gd.horizontalSpan = 1;
			gd.widthHint = 190;
			grdText.getTextWidget().setLayoutData(gd);
			grdPredicateTexts.add(grdText);

			varText.getTextWidget().addModifyListener(
					new GuardListener(grdText.getTextWidget()));
			grdText.getTextWidget().addModifyListener(new DirtyStateListener());
		}
		grdCount = 3;
		varCount = 3;
		layout = (GridLayout) varComposite.getLayout();
		layout.numColumns = varCount;
		varComposite.setLayout(layout);

		separator = toolkit.createCompositeSeparator(body);
		separator.setLayoutData(separatorGD);

		label = toolkit.createLabel(body, "Action(s)", SWT.LEFT);
		gd = new GridData(SWT.FILL, SWT.NONE, true, false);
		gd.horizontalSpan = 3;
		label.setLayoutData(gd);

		actNameComposite = toolkit.createComposite(body);
		gd = new GridData(SWT.FILL, SWT.NONE, false, false);
		actNameComposite.setLayoutData(gd);
		layout = new GridLayout();
		layout.numColumns = 1;
		layout.verticalSpacing = 10;
		actNameComposite.setLayout(layout);

		separator = toolkit.createComposite(body);
		gd = new GridData(SWT.NONE, SWT.NONE, false, false);
		gd.widthHint = 30;
		gd.heightHint = 20;
		separator.setLayoutData(gd);

		actSubComposite = toolkit.createComposite(body);
		gd = new GridData(SWT.FILL, SWT.NONE, true, false);
		actSubComposite.setLayoutData(gd);
		layout = new GridLayout();
		layout.numColumns = 1;
		layout.verticalSpacing = 10;
		actSubComposite.setLayout(layout);

		for (int i = 1; i <= 3; i++) {
			IEventBInputText text = new EventBText(toolkit.createText(
					actNameComposite, "act" + i));
			gd = new GridData(SWT.FILL, SWT.NONE, true, false);
			text.getTextWidget().setLayoutData(gd);
			text.getTextWidget().addModifyListener(new DirtyStateListener());
			actNameTexts.add(text);

			separator = toolkit.createComposite(body);
			gd = new GridData(SWT.NONE, SWT.NONE, false, false);
			gd.widthHint = 30;
			gd.heightHint = 20;
			separator.setLayoutData(gd);

			text = new EventBMath(toolkit.createText(actSubComposite, ""));
			gd = new GridData(SWT.FILL, SWT.NONE, true, false);
			gd.horizontalSpan = 3;
			gd.widthHint = 190;
			text.getTextWidget().setLayoutData(gd);
			actSubstitutionTexts.add(text);
			text.getTextWidget().addModifyListener(new DirtyStateListener());
		}
		actCount = 3;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.dialogs.Dialog#buttonPressed(int)
	 */
	@Override
	protected void buttonPressed(int buttonId) {
		Composite body = scrolledForm.getBody();
		if (buttonId == IDialogConstants.CANCEL_ID) {
			name = null;
			varNames = new HashSet<String>();
			grdNames = new HashSet<String>();
			grdPredicates = new HashSet<String>();
			actNames = new HashSet<String>();
			actSubstitutions = new HashSet<String>();
		} else if (buttonId == IDialogConstants.YES_ID) {
			final IEventBInputText varText = new EventBText(toolkit.createText(
					varComposite, ""));
			GridData gd = new GridData(SWT.FILL, SWT.NONE, true, false);
			gd.widthHint = 30;
			varText.getTextWidget().setLayoutData(gd);
			varText.getTextWidget().addModifyListener(new DirtyStateListener());
			varNameTexts.add(varText);

			IEventBInputText text = new EventBText(toolkit.createText(
					grdNameComposite, "grd" + ++grdCount));
			gd = new GridData(SWT.FILL, SWT.NONE, true, false);
			text.getTextWidget().setLayoutData(gd);
			text.getTextWidget().addModifyListener(new DirtyStateListener());
			grdNameTexts.add(text);

			Composite separator = toolkit.createComposite(body);
			gd = new GridData(SWT.NONE, SWT.NONE, false, false);
			gd.widthHint = 30;
			gd.heightHint = 20;
			separator.setLayoutData(gd);

			final IEventBInputText grdText = new EventBMath(toolkit.createText(
					grdPredComposite, ""));
			gd = new GridData(SWT.FILL, SWT.NONE, true, false);
			gd.horizontalSpan = 1;
			gd.widthHint = 190;
			grdText.getTextWidget().setLayoutData(gd);
			grdPredicateTexts.add(grdText);

			varText.getTextWidget().addModifyListener(
					new GuardListener(grdText.getTextWidget()));

			grdText.getTextWidget().addModifyListener(new DirtyStateListener());
			varCount++;
			GridLayout layout = (GridLayout) varComposite.getLayout();
			layout.numColumns = varCount;
			varComposite.setLayout(layout);
			gd = (GridData) varComposite.getLayoutData();
			gd.widthHint = 50 * varCount + 10 * (varCount - 1);
			updateSize();

		} else if (buttonId == IDialogConstants.NO_ID) {
			IEventBInputText text = new EventBText(toolkit.createText(
					grdNameComposite, "grd" + ++grdCount));
			GridData gd = new GridData(SWT.FILL, SWT.NONE, true, false);
			text.getTextWidget().setLayoutData(gd);
			text.getTextWidget().addModifyListener(new DirtyStateListener());
			grdNameTexts.add(text);

			Composite separator = toolkit.createComposite(body);
			gd = new GridData(SWT.NONE, SWT.NONE, false, false);
			gd.widthHint = 30;
			gd.heightHint = 20;
			separator.setLayoutData(gd);

			final IEventBInputText grdText = new EventBMath(toolkit.createText(
					grdPredComposite, ""));
			gd = new GridData(SWT.FILL, SWT.NONE, true, false);
			gd.horizontalSpan = 1;
			gd.widthHint = 190;
			grdText.getTextWidget().setLayoutData(gd);
			grdPredicateTexts.add(grdText);
			grdText.getTextWidget().addModifyListener(new DirtyStateListener());
			updateSize();

		} else if (buttonId == IDialogConstants.YES_TO_ALL_ID) {
			IEventBInputText text = new EventBText(toolkit.createText(
					actNameComposite, "act" + ++actCount));
			GridData gd = new GridData(SWT.FILL, SWT.NONE, true, false);
			text.getTextWidget().setLayoutData(gd);
			text.getTextWidget().addModifyListener(new DirtyStateListener());
			actNameTexts.add(text);

			Composite separator = toolkit.createComposite(body);
			gd = new GridData(SWT.NONE, SWT.NONE, false, false);
			gd.widthHint = 30;
			gd.heightHint = 20;
			separator.setLayoutData(gd);

			text = new EventBMath(toolkit.createText(actSubComposite, ""));
			gd = new GridData(SWT.FILL, SWT.NONE, true, false);
			gd.horizontalSpan = 3;
			gd.widthHint = 190;
			text.getTextWidget().setLayoutData(gd);
			actSubstitutionTexts.add(text);
			text.getTextWidget().addModifyListener(new DirtyStateListener());
			updateSize();

		} else if (buttonId == IDialogConstants.OK_ID) {
			name = nameText.getTextWidget().getText();

			varNames = new ArrayList<String>();
			Object[] varNameList = varNameTexts.toArray();
			for (int i = 0; i < varNameList.length; i++) {
				Text text = ((IEventBInputText) varNameList[i]).getTextWidget();
				if (!text.getText().equals("")) {
					varNames.add(text.getText());
				}
			}

			grdNames = new ArrayList<String>();
			grdPredicates = new ArrayList<String>();
			Object[] grdNameList = grdNameTexts.toArray();
			Object[] grdPredicateList = grdPredicateTexts.toArray();
			for (int i = 0; i < grdNameList.length; i++) {
				Text predicateText = ((IEventBInputText) grdPredicateList[i])
						.getTextWidget();
				if (dirtyTexts.contains(predicateText)) {
					Text text = ((IEventBInputText) grdNameList[i])
							.getTextWidget();
					grdNames.add(text.getText());
					grdPredicates.add(Text2EventBMathTranslator
							.translate(predicateText.getText()));
				}
			}

			actNames = new ArrayList<String>();
			actSubstitutions = new ArrayList<String>();
			Object[] actNameList = actNameTexts.toArray();
			Object[] actSubtitutionList = actSubstitutionTexts.toArray();
			for (int i = 0; i < actSubtitutionList.length; i++) {
				Text actSubstitutionText = ((IEventBInputText) actSubtitutionList[i])
						.getTextWidget();
				if (dirtyTexts.contains(actSubstitutionText)) {
					Text text = ((IEventBInputText) actNameList[i])
							.getTextWidget();
					actNames.add(text.getText());
					actSubstitutions.add(Text2EventBMathTranslator
							.translate(actSubstitutionText.getText()));
				}
			}
		}
		super.buttonPressed(buttonId);
	}

	/**
	 * Get the name of the new event.
	 * <p>
	 * 
	 * @return name of the new event as input by user
	 */
	public String getName() {
		return name;
	}

	/**
	 * Get the list of local variables of the new event.
	 * <p>
	 * 
	 * @return the list of new local variables as input by user
	 */
	public String[] getVariables() {
		return varNames.toArray(new String[varNames.size()]);
	}

	/**
	 * Get the list of guard names of the new event.
	 * <p>
	 * 
	 * @return the list of the guard names as input by user
	 */
	public String[] getGrdNames() {
		return grdNames.toArray(new String[grdNames.size()]);
	}

	/**
	 * Get the list of guard predicates of the new event.
	 * <p>
	 * 
	 * @return the list of the guard predicates as input by user
	 */
	public String[] getGrdPredicates() {
		return grdPredicates.toArray(new String[grdPredicates.size()]);
	}

	/**
	 * Get the list of guard names of the new event.
	 * <p>
	 * 
	 * @return the list of the guard names as input by user
	 */
	public String[] getActNames() {
		return actNames.toArray(new String[actNames.size()]);
	}

	/**
	 * Get the list of action of the new event.
	 * <p>
	 * 
	 * @return the list the actSubstitutions as input by user
	 */
	public String[] getActSubstitutions() {
		return actSubstitutions.toArray(new String[actSubstitutions.size()]);
	}

	@Override
	public boolean close() {
		nameText.dispose();
		for (IEventBInputText text : grdNameTexts)
			text.dispose();

		for (IEventBInputText text : grdPredicateTexts)
			text.dispose();

		for (IEventBInputText text : actNameTexts)
			text.dispose();

		for (IEventBInputText text : actSubstitutionTexts)
			text.dispose();
		return super.close();
	}

}
