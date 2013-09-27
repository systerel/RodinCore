/*******************************************************************************
 * Copyright (c) 2005, 2013 ETH Zurich and others.
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
 *     Systerel - replaced setFieldValues() with checkAndSetFieldValues()
 *     Systerel - add widget to edit theorem attribute
 *     Systerel - refactored to use NewEventsWizard
 *******************************************************************************/
package org.eventb.internal.ui.eventbeditor.dialogs;

import static org.eclipse.jface.dialogs.IDialogConstants.CANCEL_ID;
import static org.eclipse.jface.dialogs.IDialogConstants.CANCEL_LABEL;
import static org.eclipse.jface.dialogs.IDialogConstants.OK_ID;
import static org.eclipse.jface.dialogs.IDialogConstants.OK_LABEL;
import static org.eventb.core.EventBAttributes.ASSIGNMENT_ATTRIBUTE;
import static org.eventb.core.EventBAttributes.LABEL_ATTRIBUTE;
import static org.eventb.core.EventBAttributes.PREDICATE_ATTRIBUTE;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eventb.core.IAction;
import org.eventb.core.IEvent;
import org.eventb.core.IGuard;
import org.eventb.core.IMachineRoot;
import org.eventb.core.IParameter;
import org.eventb.internal.ui.IEventBInputText;
import org.eventb.internal.ui.Pair;
import org.eventb.internal.ui.UIUtils;
import org.eventb.internal.ui.autocompletion.ProviderModifyListener;
import org.eventb.internal.ui.eventbeditor.Triplet;
import org.eventb.internal.ui.eventbeditor.wizards.AbstractEventBCreationWizard;
import org.eventb.internal.ui.preferences.PreferenceUtils;
import org.rodinp.core.IInternalElementType;

/**
 * This class extends the Dialog class and provides an input dialog for new
 * event with some parameters, guards and actSubstitutions.
 * 
 * @author htson
 */
public class NewEventDialog extends EventBDialog {

	private static final String TMP_ELEM_NAME = "tmp";

	private static final int DEFAULT_ITEM_NUMBER = 3;

	protected String labelResult;

	protected final Collection<String> parsResult = new ArrayList<String>();

	private final Collection<Triplet<String, String, Boolean>> grdResults = new ArrayList<Triplet<String, String, Boolean>>();

	private final Collection<Pair<String, String>> actResults = new ArrayList<Pair<String, String>>();

	private IEventBInputText labelText;

	private Collection<IEventBInputText> parTexts;

	private Collection<Triplet<IEventBInputText, IEventBInputText, Button>> grdTexts;

	private Collection<Pair<IEventBInputText, IEventBInputText>> actTexts;

	private Composite parComposite;
	
	private Composite actionSeparator; 

	private int grdCount;

	private int parCount;

	private int actCount;

	private Composite composite;
	
	private final String guardPrefix;

	private final String actPrefix;

	private ProviderModifyListener providerListener;

	private final AbstractEventBCreationWizard wizard;

	private IEvent event;
	
	/**
	 * Constructor.
	 * 
	 * @param wizard
	 *            the parent wizard of this dialog
	 * @param root
	 *            the root element to which events will be added
	 * @param parentShell
	 *            the parent shell of the dialog
	 * @param title
	 *            the title of the dialog
	 */
	public NewEventDialog(AbstractEventBCreationWizard wizard, IMachineRoot root, Shell parentShell,
			String title) {
		super(parentShell, root, title);
		this.wizard = wizard;
		initValue();
		dirtyTexts = new HashSet<Text>();
		
		setShellStyle(getShellStyle() | SWT.RESIZE);

		guardPrefix = getAutoNamePrefix(IGuard.ELEMENT_TYPE);
		actPrefix = getAutoNamePrefix(IAction.ELEMENT_TYPE);
	}

	private void initValue(){
		labelResult = null;
		parsResult.clear();
		grdResults.clear();
		actResults.clear();
	}

	private String getAutoNamePrefix(IInternalElementType<?> type) {
		return PreferenceUtils.getAutoNamePrefix(root, type);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.dialogs.Dialog#createButtonsForButtonBar(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, ADD_ID, ADD_LABEL);
		createButton(parent, MORE_PARAMETER_ID, MORE_PARAMETER_LABEL);
		createButton(parent, MORE_GUARD_ID, MORE_GUARD_LABEL);
		createButton(parent, MORE_ACTION_ID, MORE_ACTION_LABEL);
		createDefaultButton(parent, OK_ID, OK_LABEL);
		createButton(parent, CANCEL_ID, CANCEL_LABEL);
	}

	@Override
	protected void createContents() {
		getBody().setLayout(new FillLayout());
		createDialogContents(getBody());
	}

	private void moveAbove(IEventBInputText text, Control control) {
		text.getTextWidget().moveAbove(control);
	}

	private String getFreeEventLabel() {
		return UIUtils.getFreeElementLabel(root, IEvent.ELEMENT_TYPE);
	}

	private void createLabel(String text) {
		createLabel(composite, text);
	}

	private void createLabels(String left, String right) {
		createLabel(left);
		createSpace();
		createLabel(right);
	}

	private IEventBInputText createNameText(String value) {
		return createNameInputText(composite, value);
	}

	private IEventBInputText createContentText(Composite parent) {
		return createContentInputText(parent);
	}

	private Composite createSpace() {
		final Composite separator = toolkit.createComposite(composite);
		final GridData gd = new GridData(SWT.NONE, SWT.NONE, false, false);
		gd.widthHint = 30;
		gd.heightHint = 20;
		separator.setLayoutData(gd);
		return separator;
	}
	
	private Composite createSeparator() {
		final Composite separator = toolkit.createCompositeSeparator(composite);
		final GridData gd = new GridData();
		gd.heightHint = 5;
		gd.horizontalSpan = 3;
		separator.setLayoutData(gd);
		return separator;
	}

	private Composite createContainer(int numColumn) {
		final Composite comp = toolkit.createComposite(composite);
		final GridData gd = new GridData(SWT.FILL, SWT.FILL, true, false);
		final GridLayout layout = newLayout(numColumn, 0, 10);
		layout.makeColumnsEqualWidth = false;
		comp.setLayoutData(gd);
		comp.setLayout(layout);
		return comp;
	}
	
	private void createDialogContents(Composite parent) {
		parTexts = new ArrayList<IEventBInputText>();
		grdTexts = new ArrayList<Triplet<IEventBInputText, IEventBInputText, Button>>();
		actTexts = new ArrayList<Pair<IEventBInputText, IEventBInputText>>();
		providerListener = new ProviderModifyListener();

		composite = toolkit.createComposite(parent);
		setDebugBackgroundColor();
		setFormGridLayout(composite, 3);
		setFormGridData();

		createLabels("Label", "Parameter identifier(s)");

		labelText = createBText(createContainer(1), getFreeEventLabel());
		
		event = root.getInternalElement(IEvent.ELEMENT_TYPE, "tmpEvent");
		addProposalAdapter(event, LABEL_ATTRIBUTE, labelText);

		createSpace();
		parComposite = createContainer(1);

		createSeparator();

		createLabels("Guard label(s)", "Guard predicate(s)");

		for (int i = 1; i <= DEFAULT_ITEM_NUMBER; i++) {
			createParameter();
			createGuard();
		}

		changeColumn(parComposite, parCount);

		actionSeparator = createSeparator();
		actCount = 0;
		createLabels("Action label(s)", "Action substitution(s)");
		for (int i = 1; i <= DEFAULT_ITEM_NUMBER; i++) {
			createAction();
		}
		select(labelText);
	}

	private IEventBInputText createParameter() {
		parCount += 1;
		final IEventBInputText parLabel = createBText(parComposite, EMPTY);
		providerListener.addInputText(parLabel);
		final IParameter param = event.getInternalElement(
				IParameter.ELEMENT_TYPE, TMP_ELEM_NAME);
		addProposalAdapter(param, LABEL_ATTRIBUTE, parLabel);
		parTexts.add(parLabel);
		return parLabel;
	}
	
	private void changeColumn(Composite comp, int numColumn) {
		final GridLayout layout = (GridLayout) comp.getLayout();
		layout.numColumns = numColumn;
		comp.setLayout(layout);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.dialogs.Dialog#buttonPressed(int)
	 */
	@Override
	protected void buttonPressed(int buttonId) {
		if (buttonId == CANCEL_ID) {
			initValue();
		} else if (buttonId == MORE_PARAMETER_ID) {
			final IEventBInputText parLabel = createParameter();
			final IEventBInputText grdPred = createGuard();
			addGuardListener(parLabel, grdPred);

			parCount++;
			changeColumn(parComposite, parCount);
			changeWidthParameter();
			updateSize();
			
			parTexts.add(parLabel);
		} else if (buttonId == MORE_GUARD_ID) {
			createGuard();
			updateSize();
		} else if (buttonId == MORE_ACTION_ID) {
			createAction();
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
			updateSize();
		}
		super.buttonPressed(buttonId);
	}

	private void changeWidthParameter() {
		final GridData gd = (GridData) parComposite.getLayoutData();
		gd.widthHint = 50 * parCount + 10 * (parCount - 1);
	}
	
	private void createAction() {
		actCount++;
		final IEventBInputText actionLabel = createNameText(actPrefix
				+ actCount);
		final IAction action = event.getInternalElement(IAction.ELEMENT_TYPE,
				TMP_ELEM_NAME);
		addProposalAdapter(action, LABEL_ATTRIBUTE, actionLabel);
		createSpace();
		final IEventBInputText actionSub = createContentText(composite);
		addProposalAdapter(action, ASSIGNMENT_ATTRIBUTE, actionSub);
		actTexts.add(newWidgetPair(actionLabel, actionSub));
	}
	
	private IEventBInputText createGuard() {
		grdCount++;
		final IEventBInputText grdLabel = createNameText(guardPrefix + grdCount);
		final IGuard guard = event.getInternalElement(IGuard.ELEMENT_TYPE,
				TMP_ELEM_NAME);
		addProposalAdapter(guard, LABEL_ATTRIBUTE, grdLabel);
		final Composite separator = createSpace();
		final Composite parent = createContainer(2);
		final IEventBInputText grdPred = createContentText(parent);
		addProposalAdapter(guard, PREDICATE_ATTRIBUTE, grdPred);
		final Button button = createIsTheoremToogle(parent);
		if (actionSeparator != null) {
			moveAbove(grdLabel, actionSeparator);
			separator.moveAbove(actionSeparator);
			parent.moveAbove(actionSeparator);
		}
		grdTexts.add(newWidgetTriplet(grdLabel, grdPred, button));
		return grdPred;
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
		labelResult = getText(labelText);

		parsResult.clear();
		fillResult(parTexts, parsResult);

		final List<String> names = new ArrayList<String>(parsResult);
		names.add(labelResult);
		if (!checkNewIdentifiers(names, true, root.getFormulaFactory())) {
			labelResult = null;
			parsResult.clear();
			return false;
		}
		
		grdResults.clear();
		fillTripletResult(grdTexts, grdResults);
		
		actResults.clear();
		fillPairResult(actTexts, actResults);
		return true;
	}

	/**
	 * Get the label of the new event.
	 * <p>
	 * 
	 * @return label of the new event as input by user
	 */
	public String getLabel() {
		return labelResult;
	}

	/**
	 * Get the list of parameters of the new event.
	 * <p>
	 * 
	 * @return the list of new parameters as input by user
	 */
	public String[] getParameters() {
		return parsResult.toArray(new String[parsResult.size()]);
	}

	/**
	 * Get the list of guard labels of the new event.
	 * <p>
	 * 
	 * @return the list of the guard labels as input by user
	 */
	public String[] getGrdLabels() {
		return getFirstTriplet(grdResults);
	}

	/**
	 * Get the list of guard predicates of the new event.
	 * <p>
	 * 
	 * @return the list of the guard predicates as input by user
	 */
	public String[] getGrdPredicates() {
		return getSecondTriplet(grdResults);
	}

	/**
	 * Get the list of guard theorem attribute of the new event.
	 * <p>
	 * 
	 * @return the list of the guard theorem attribute as input by user
	 */
	public boolean[] getGrdIsTheorem() {
		return getThirdTriplet(grdResults);
	}

	/**
	 * Get the list of action labels of the new event.
	 * <p>
	 * 
	 * @return the list of the action labels as input by user
	 */
	public String[] getActLabels() {
		return getFirst(actResults);
	}

	/**
	 * Get the list of action subtitutions of the new event.
	 * <p>
	 * 
	 * @return the list the action substitutions as input by user
	 */
	public String[] getActSubstitutions() {
		return getSecond(actResults);
	}

	@Override
	public boolean close() {
		labelText.dispose();
		disposeTriplets(grdTexts);
		disposePairs(actTexts);
		return super.close();
	}
}
