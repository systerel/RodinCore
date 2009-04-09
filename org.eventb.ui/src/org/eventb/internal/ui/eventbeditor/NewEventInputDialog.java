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
 *     Systerel - used label prefix set by user
 *******************************************************************************/
package org.eventb.internal.ui.eventbeditor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eventb.core.IAction;
import org.eventb.core.IEvent;
import org.eventb.core.IGuard;
import org.eventb.core.IMachineRoot;
import org.eventb.eventBKeyboard.Text2EventBMathTranslator;
import org.eventb.internal.ui.EventBMath;
import org.eventb.internal.ui.EventBSharedColor;
import org.eventb.internal.ui.EventBText;
import org.eventb.internal.ui.IEventBInputText;
import org.eventb.internal.ui.UIUtils;
import org.eventb.ui.eventbeditor.IEventBEditor;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;

/**
 * @author htson
 *         <p>
 *         This class extends the Dialog class and provides an input dialog for
 *         new event with some parameterss, guards and actSubstitutions.
 */
public class NewEventInputDialog extends EventBInputDialog {

	String label;

	Collection<String> pars;

	Collection<String> grdLabels;

	Collection<String> grdPredicates;

	Collection<String> actLabels;

	Collection<String> actSubstitutions;

	private IEventBInputText labelText;

	private Collection<IEventBInputText> parTexts;

	private Collection<IEventBInputText> grdLabelTexts;

	private Collection<IEventBInputText> grdPredicateTexts;

	private Collection<IEventBInputText> actLabelTexts;

	private Collection<IEventBInputText> actSubstitutionTexts;

	private Composite parComposite;
	
	private Composite actionSeparator; 

	private int grdCount;

	private int parCount;

	private int actCount;

	IEventBEditor<IMachineRoot> editor;
	
	private Composite composite;
	
	private final String guardPrefix;

	private final String actPrefix;
	
	/**
	 * Constructor.
	 * <p>
	 * 
	 * @param parentShell
	 *            the parent shell of the dialog
	 * @param title
	 *            the title of the dialog
	 */
	public NewEventInputDialog(IEventBEditor<IMachineRoot> editor, Shell parentShell,
			String title) {
		super(parentShell, title);
		this.editor = editor;
		pars = new HashSet<String>();
		grdLabels = new HashSet<String>();
		grdPredicates = new HashSet<String>();
		actLabels = new HashSet<String>();
		actSubstitutions = new HashSet<String>();
		dirtyTexts = new HashSet<Text>();
		setShellStyle(getShellStyle() | SWT.RESIZE);
		guardPrefix = getAutoNamePrefix(IGuard.ELEMENT_TYPE);
		actPrefix = getAutoNamePrefix(IAction.ELEMENT_TYPE);
	}

	private String getAutoNamePrefix(IInternalElementType<?> type) {
		return UIUtils.getAutoNamePrefix(editor.getRodinInput(), type);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.dialogs.Dialog#createButtonsForButtonBar(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.RETRY_ID, "&Add", false);

		createButton(parent, IDialogConstants.YES_ID, "More &Par.", false);

		createButton(parent, IDialogConstants.NO_ID, "More &Grd.", false);

		createButton(parent, IDialogConstants.YES_TO_ALL_ID, "More A&ct.",
				false);

		createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL,
				true);

		createButton(parent, IDialogConstants.CANCEL_ID,
				IDialogConstants.CANCEL_LABEL, false);
	}

	@Override
	protected void createContents() {
		Composite body = scrolledForm.getBody();
		body.setLayout(new FillLayout());
		createDialogContents(body);
	}

	private void createDialogContents(Composite parent) {
		composite = toolkit.createComposite(parent);
		if (EventBEditorUtils.DEBUG)
			composite.setBackground(EventBSharedColor.getSystemColor(
					SWT.COLOR_CYAN));
		
		parTexts = new ArrayList<IEventBInputText>();
		grdLabelTexts = new ArrayList<IEventBInputText>();
		grdPredicateTexts = new ArrayList<IEventBInputText>();
		actLabelTexts = new ArrayList<IEventBInputText>();
		actSubstitutionTexts = new ArrayList<IEventBInputText>();

		GridLayout layout = new GridLayout();
		layout.numColumns = 3;
		layout.verticalSpacing = 10;
		layout.horizontalSpacing = 10;
		composite.setLayout(layout);
		GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
		scrolledForm.setLayoutData(gd);

		Label labelWidget = toolkit.createLabel(composite, "Label", SWT.LEFT);
		gd = new GridData(SWT.FILL, SWT.NONE, false, false);
		labelWidget.setLayoutData(gd);

		Composite separator = toolkit.createComposite(composite);
		gd = new GridData(SWT.NONE, SWT.NONE, false, false);
		gd.widthHint = 30;
		gd.heightHint = 20;
		separator.setLayoutData(gd);

		labelWidget = toolkit.createLabel(composite, "Parameter identifier(s)", SWT.CENTER);
		gd = new GridData(SWT.FILL, SWT.NONE, false, false);
		labelWidget.setLayoutData(gd);

		Composite comp = toolkit.createComposite(composite);
		gd = new GridData(SWT.FILL, SWT.NONE, true, false);
		gd.widthHint = 50;
		comp.setLayoutData(gd);
		layout = new GridLayout();
		layout.verticalSpacing = 0;
		layout.horizontalSpacing = 10;
		layout.numColumns = 1;
		comp.setLayout(layout);

		final IMachineRoot root = editor.getRodinInput();
		final String evtLabel = UIUtils.getFreeElementLabel(root,
				IEvent.ELEMENT_TYPE);

		labelText = new EventBText(toolkit.createText(comp, evtLabel));
		gd = new GridData(SWT.FILL, SWT.NONE, true, false);
		labelText.getTextWidget().setLayoutData(gd);
		labelText.getTextWidget().addModifyListener(new DirtyStateListener());

		separator = toolkit.createComposite(composite);
		gd = new GridData(SWT.NONE, SWT.NONE, false, false);
		gd.widthHint = 30;
		gd.heightHint = 20;
		separator.setLayoutData(gd);

		parComposite = toolkit.createComposite(composite);
		gd = new GridData(SWT.FILL, SWT.FILL, true, false);
		gd.widthHint = 50 * parCount + 10 * (parCount - 1);
		parComposite.setLayoutData(gd);
		layout = new GridLayout();
		layout.verticalSpacing = 0;
		layout.horizontalSpacing = 10;
		layout.makeColumnsEqualWidth = true;
		parComposite.setLayout(layout);

		separator = toolkit.createCompositeSeparator(composite);
		GridData separatorGD = new GridData();
		separatorGD.heightHint = 5;
		separatorGD.horizontalSpan = 3;
		separator.setLayoutData(separatorGD);

		labelWidget = toolkit.createLabel(composite, "Guard label(s)", SWT.LEFT);
		gd = new GridData(SWT.FILL, SWT.NONE, false, false);
		labelWidget.setLayoutData(gd);

		separator = toolkit.createComposite(composite);
		gd = new GridData(SWT.NONE, SWT.NONE, false, false);
		gd.widthHint = 30;
		gd.heightHint = 20;
		separator.setLayoutData(gd);

		labelWidget = toolkit.createLabel(composite, "Guard predicate(s)", SWT.LEFT);
		gd = new GridData(SWT.FILL, SWT.NONE, true, false);
		gd.horizontalSpan = 1;
		labelWidget.setLayoutData(gd);

		for (int i = 1; i <= 3; i++) {
			final IEventBInputText parText = new EventBText(toolkit.createText(
					parComposite, ""));
			gd = new GridData(SWT.FILL, SWT.NONE, true, false);
			gd.widthHint = 30;
			parText.getTextWidget().setLayoutData(gd);
			parText.getTextWidget().addModifyListener(new DirtyStateListener());
			parTexts.add(parText);

			IEventBInputText text = new EventBText(toolkit.createText(
					composite, guardPrefix + i));
			gd = new GridData(SWT.FILL, SWT.NONE, true, false);
			text.getTextWidget().setLayoutData(gd);
			text.getTextWidget().addModifyListener(new DirtyStateListener());
			grdLabelTexts.add(text);

			separator = toolkit.createComposite(composite);
			gd = new GridData(SWT.NONE, SWT.NONE, false, false);
			gd.widthHint = 30;
			gd.heightHint = 20;
			separator.setLayoutData(gd);

			final IEventBInputText grdText = new EventBMath(toolkit.createText(
					composite, ""));
			gd = new GridData(SWT.FILL, SWT.NONE, true, false);
			gd.horizontalSpan = 1;
			gd.widthHint = 190;
			grdText.getTextWidget().setLayoutData(gd);
			grdPredicateTexts.add(grdText);

			parText.getTextWidget().addModifyListener(
					new GuardListener(grdText.getTextWidget()));
			grdText.getTextWidget().addModifyListener(new DirtyStateListener());
		}
		grdCount = 3;
		parCount = 3;
		layout = (GridLayout) parComposite.getLayout();
		layout.numColumns = parCount;
		parComposite.setLayout(layout);

		actionSeparator = toolkit.createCompositeSeparator(composite);
		actionSeparator.setLayoutData(separatorGD);

		labelWidget = toolkit.createLabel(composite, "Action label(s)",
				SWT.LEFT);
		gd = new GridData(SWT.FILL, SWT.NONE, false, false);
		labelWidget.setLayoutData(gd);

		separator = toolkit.createComposite(composite);
		gd = new GridData(SWT.NONE, SWT.NONE, false, false);
		gd.widthHint = 30;
		gd.heightHint = 20;
		separator.setLayoutData(gd);

		labelWidget = toolkit.createLabel(composite, "Action substitution(s)",
				SWT.LEFT);
		gd = new GridData(SWT.FILL, SWT.NONE, true, false);
		gd.horizontalSpan = 1;
		labelWidget.setLayoutData(gd);
		
		for (int i = 1; i <= 3; i++) {
			IEventBInputText text = new EventBText(toolkit.createText(
					composite, actPrefix + i));
			gd = new GridData(SWT.FILL, SWT.NONE, true, false);
			text.getTextWidget().setLayoutData(gd);
			text.getTextWidget().addModifyListener(new DirtyStateListener());
			actLabelTexts.add(text);

			separator = toolkit.createComposite(composite);
			gd = new GridData(SWT.NONE, SWT.NONE, false, false);
			gd.widthHint = 30;
			gd.heightHint = 20;
			separator.setLayoutData(gd);

			text = new EventBMath(toolkit.createText(composite, ""));
			gd = new GridData(SWT.FILL, SWT.NONE, true, false);
			text.getTextWidget().setLayoutData(gd);
			actSubstitutionTexts.add(text);
			text.getTextWidget().addModifyListener(new DirtyStateListener());
		}
		actCount = 3;
		labelText.getTextWidget().selectAll();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.dialogs.Dialog#buttonPressed(int)
	 */
	@Override
	protected void buttonPressed(int buttonId) {
		if (buttonId == IDialogConstants.CANCEL_ID) {
			label = null;
			pars = new HashSet<String>();
			grdLabels = new HashSet<String>();
			grdPredicates = new HashSet<String>();
			actLabels = new HashSet<String>();
			actSubstitutions = new HashSet<String>();
		} else if (buttonId == IDialogConstants.YES_ID) {
			final IEventBInputText parText = new EventBText(toolkit.createText(
					parComposite, ""));
			GridData gd = new GridData(SWT.FILL, SWT.NONE, true, false);
			gd.widthHint = 30;
			parText.getTextWidget().setLayoutData(gd);
			parText.getTextWidget().addModifyListener(new DirtyStateListener());
			parTexts.add(parText);

			IEventBInputText text = new EventBText(toolkit.createText(
					composite, guardPrefix + ++grdCount));
			gd = new GridData(SWT.FILL, SWT.NONE, true, false);
			Text grdLabelWidget = text.getTextWidget();
			grdLabelWidget.setLayoutData(gd);
			grdLabelWidget.addModifyListener(new DirtyStateListener());
			grdLabelWidget.moveAbove(actionSeparator);
			grdLabelTexts.add(text);

			Composite separator = toolkit.createComposite(composite);
			gd = new GridData(SWT.NONE, SWT.NONE, false, false);
			gd.widthHint = 30;
			gd.heightHint = 20;
			separator.setLayoutData(gd);
			separator.moveAbove(actionSeparator);

			final IEventBInputText grdText = new EventBMath(toolkit.createText(
					composite, ""));
			gd = new GridData(SWT.FILL, SWT.NONE, true, false);
			gd.horizontalSpan = 1;
			gd.widthHint = 190;
			Text grdPredWidget = grdText.getTextWidget();
			grdPredWidget.setLayoutData(gd);
			grdPredicateTexts.add(grdText);
			grdPredWidget.moveAbove(actionSeparator);
			
			parText.getTextWidget().addModifyListener(
					new GuardListener(grdPredWidget));

			grdPredWidget.addModifyListener(new DirtyStateListener());
			parCount++;
			GridLayout layout = (GridLayout) parComposite.getLayout();
			layout.numColumns = parCount;
			parComposite.setLayout(layout);
			gd = (GridData) parComposite.getLayoutData();
			gd.widthHint = 50 * parCount + 10 * (parCount - 1);
			updateSize();

		} else if (buttonId == IDialogConstants.NO_ID) {
			IEventBInputText text = new EventBText(toolkit.createText(
					composite, guardPrefix + ++grdCount));
			GridData gd = new GridData(SWT.FILL, SWT.NONE, true, false);
			Text grdLabelWidget = text.getTextWidget();
			grdLabelWidget.setLayoutData(gd);
			grdLabelWidget.addModifyListener(new DirtyStateListener());
			grdLabelWidget.moveAbove(actionSeparator);
			grdLabelTexts.add(text);

			Composite separator = toolkit.createComposite(composite);
			gd = new GridData(SWT.NONE, SWT.NONE, false, false);
			gd.widthHint = 30;
			gd.heightHint = 20;
			separator.setLayoutData(gd);
			separator.moveAbove(actionSeparator);

			final IEventBInputText grdText = new EventBMath(toolkit.createText(
					composite, ""));
			gd = new GridData(SWT.FILL, SWT.NONE, true, false);
			gd.horizontalSpan = 1;
			gd.widthHint = 190;
			Text grdPredWidget = grdText.getTextWidget();
			grdPredWidget.setLayoutData(gd);
			grdPredicateTexts.add(grdText);
			grdPredWidget.addModifyListener(new DirtyStateListener());
			grdPredWidget.moveAbove(actionSeparator);
			updateSize();

		} else if (buttonId == IDialogConstants.YES_TO_ALL_ID) {
			IEventBInputText text = new EventBText(toolkit.createText(
					composite, actPrefix + ++actCount));
			GridData gd = new GridData(SWT.FILL, SWT.NONE, true, false);
			text.getTextWidget().setLayoutData(gd);
			text.getTextWidget().addModifyListener(new DirtyStateListener());
			actLabelTexts.add(text);

			Composite separator = toolkit.createComposite(composite);
			gd = new GridData(SWT.NONE, SWT.NONE, false, false);
			gd.widthHint = 30;
			gd.heightHint = 20;
			separator.setLayoutData(gd);

			text = new EventBMath(toolkit.createText(composite, ""));
			gd = new GridData(SWT.FILL, SWT.NONE, true, false);
			text.getTextWidget().setLayoutData(gd);
			actSubstitutionTexts.add(text);
			text.getTextWidget().addModifyListener(new DirtyStateListener());
			updateSize();

		} else if (buttonId == IDialogConstants.OK_ID) {
			setFieldValues();
		} else if (buttonId == IDialogConstants.RETRY_ID) {
			setFieldValues();
			addValues();
			initialise();
			updateSize();
		}
		super.buttonPressed(buttonId);
	}

	private void addValues() {
		try {
			RodinCore.run(new IWorkspaceRunnable() {

				public void run(IProgressMonitor pm) throws RodinDBException {

					final String[] grdNames = grdLabels
							.toArray(new String[grdLabels.size()]);
					final String[] lGrdPredicates = grdPredicates
							.toArray(new String[grdPredicates.size()]);

					final String[] actNames = actLabels
							.toArray(new String[actLabels.size()]);
					final String[] lActSub = actSubstitutions
							.toArray(new String[actSubstitutions.size()]);

					final String[] paramNames = pars.toArray(new String[pars
							.size()]);
					EventBEditorUtils.newEvent(editor, label, paramNames,
							grdNames, lGrdPredicates, actNames, lActSub);

				}

			}, null);

		} catch (RodinDBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void initialise() {
		clearDirtyTexts();
		composite.dispose();
		createDialogContents(scrolledForm.getBody());
		scrolledForm.reflow(true);
	}

	private void setFieldValues() {
		label = labelText.getTextWidget().getText();

		pars = new ArrayList<String>();
		Object[] parList = parTexts.toArray();
		for (int i = 0; i < parList.length; i++) {
			Text text = ((IEventBInputText) parList[i]).getTextWidget();
			if (!text.getText().equals("")) {
				pars.add(text.getText());
			}
		}

		grdLabels = new ArrayList<String>();
		grdPredicates = new ArrayList<String>();
		Object[] grdLabelList = grdLabelTexts.toArray();
		Object[] grdPredicateList = grdPredicateTexts.toArray();
		for (int i = 0; i < grdLabelList.length; i++) {
			Text predicateText = ((IEventBInputText) grdPredicateList[i])
					.getTextWidget();
			if (dirtyTexts.contains(predicateText)) {
				Text text = ((IEventBInputText) grdLabelList[i])
						.getTextWidget();
				grdLabels.add(text.getText());
				grdPredicates.add(Text2EventBMathTranslator
						.translate(predicateText.getText()));
			}
		}

		actLabels = new ArrayList<String>();
		actSubstitutions = new ArrayList<String>();
		Object[] actLabelList = actLabelTexts.toArray();
		Object[] actSubtitutionList = actSubstitutionTexts.toArray();
		for (int i = 0; i < actSubtitutionList.length; i++) {
			Text actSubstitutionText = ((IEventBInputText) actSubtitutionList[i])
					.getTextWidget();
			if (dirtyTexts.contains(actSubstitutionText)) {
				Text text = ((IEventBInputText) actLabelList[i])
						.getTextWidget();
				actLabels.add(text.getText());
				actSubstitutions.add(Text2EventBMathTranslator
						.translate(actSubstitutionText.getText()));
			}
		}
	}

	/**
	 * Get the label of the new event.
	 * <p>
	 * 
	 * @return label of the new event as input by user
	 */
	public String getLabel() {
		return label;
	}

	/**
	 * Get the list of parameters of the new event.
	 * <p>
	 * 
	 * @return the list of new parameters as input by user
	 */
	public String[] getParameters() {
		return pars.toArray(new String[pars.size()]);
	}

	/**
	 * Get the list of guard labels of the new event.
	 * <p>
	 * 
	 * @return the list of the guard labels as input by user
	 */
	public String[] getGrdLabels() {
		return grdLabels.toArray(new String[grdLabels.size()]);
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
	 * Get the list of action labels of the new event.
	 * <p>
	 * 
	 * @return the list of the action labels as input by user
	 */
	public String[] getActLabels() {
		return actLabels.toArray(new String[actLabels.size()]);
	}

	/**
	 * Get the list of action subtitutions of the new event.
	 * <p>
	 * 
	 * @return the list the action substitutions as input by user
	 */
	public String[] getActSubstitutions() {
		return actSubstitutions.toArray(new String[actSubstitutions.size()]);
	}

	@Override
	public boolean close() {
		labelText.dispose();
		for (IEventBInputText text : grdLabelTexts)
			text.dispose();

		for (IEventBInputText text : grdPredicateTexts)
			text.dispose();

		for (IEventBInputText text : actLabelTexts)
			text.dispose();

		for (IEventBInputText text : actSubstitutionTexts)
			text.dispose();
		return super.close();
	}

}
