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
 *     Systerel - used label prefix set by user
 *     Systerel - replaced setFieldValues() with checkAndSetFieldValues()
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

import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
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
import org.eventb.internal.ui.autocompletion.WizardProposalProvider;
import org.eventb.internal.ui.eventbeditor.EventBEditorUtils;
import org.eventb.internal.ui.preferences.PreferenceUtils;
import org.eventb.ui.eventbeditor.IEventBEditor;
import org.rodinp.core.IAttributeType;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;

/**
 * @author htson
 *         <p>
 *         This class extends the Dialog class and provides an input dialog for
 *         new event with some parameters, guards and actSubstitutions.
 */
public class NewEventDialog extends EventBDialog {

	protected String labelResult;

	protected final Collection<String> parsResult = new ArrayList<String>();

	private final Collection<Pair<String, String>> grdResults = new ArrayList<Pair<String, String>>();

	private final Collection<Pair<String, String>> actResults = new ArrayList<Pair<String, String>>();

	private IEventBInputText labelText;

	private Collection<IEventBInputText> parTexts;

	private Collection<Pair<IEventBInputText, IEventBInputText>> grdTexts;

	private Collection<Pair<IEventBInputText, IEventBInputText>> actTexts;

	private Composite parComposite;
	
	private Composite actionSeparator; 

	private int grdCount;

	private int parCount;

	private int actCount;

	protected final IEventBEditor<IMachineRoot> editor;
	
	private Composite composite;
	
	private final String guardPrefix;

	private final String actPrefix;

	private ProviderModifyListener providerListener;
	
	/**
	 * Constructor.
	 * 
	 * @param editor
	 *            the editor that called this dialog
	 * @param root
	 *            the root element to which events will be added
	 * @param parentShell
	 *            the parent shell of the dialog
	 * @param title
	 *            the title of the dialog
	 */
	public NewEventDialog(IEventBEditor<IMachineRoot> editor, IMachineRoot root, Shell parentShell,
			String title) {
		super(parentShell, root, title);
		this.editor = editor;
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
		return PreferenceUtils.getAutoNamePrefix(editor.getRodinInput(), type);
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

	private IEventBInputText createContentText() {
		return createContentInputText(composite);
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

	private Composite createContainer() {
		final Composite comp = toolkit.createComposite(composite);
		final GridData gd = new GridData(SWT.FILL, SWT.FILL, true, false);
		final GridLayout layout = newLayout(1, 0, 10);
		layout.makeColumnsEqualWidth = true;
		comp.setLayoutData(gd);
		comp.setLayout(layout);
		return comp;
	}
	
	private void createDialogContents(Composite parent) {
		parTexts = new ArrayList<IEventBInputText>();
		grdTexts = new ArrayList<Pair<IEventBInputText, IEventBInputText>>();
		actTexts = new ArrayList<Pair<IEventBInputText, IEventBInputText>>();
		providerListener = new ProviderModifyListener();

		composite = toolkit.createComposite(parent);
		setDebugBackgroundColor();
		setFormGridLayout(composite, 3);
		setFormGridData();

		createLabels("Label", "Parameter identifier(s)");

		labelText = createBText(createContainer(), getFreeEventLabel());
		addProposalAdapter(IEvent.ELEMENT_TYPE, LABEL_ATTRIBUTE, labelText);

		createSpace();
		parComposite = createContainer();

		createSeparator();

		createLabels("Guard label(s)", "Guard predicate(s)");
		
		for (int i = 1; i <= 3; i++) {
			final IEventBInputText parText = createBText(parComposite, EMPTY);
			addIdentifierAdapter(parText, IParameter.ELEMENT_TYPE,
					LABEL_ATTRIBUTE);
			final IEventBInputText grdLabel = createNameText(guardPrefix + i);
			addContentAdapter(grdLabel, IGuard.ELEMENT_TYPE, LABEL_ATTRIBUTE);
			createSpace();
			final IEventBInputText grdPredicate = createContentText();
			addContentAdapter(grdPredicate, IGuard.ELEMENT_TYPE,
					PREDICATE_ATTRIBUTE);

			addGuardListener(parText, grdPredicate);

			parTexts.add(parText);
			grdTexts.add(newWidgetPair(grdLabel, grdPredicate));
		}
		grdCount = 3;
		parCount = 3;

		changeColumn(parComposite, parCount);

		actionSeparator = createSeparator();
		actCount = 0;
		createLabels("Action label(s)", "Action substitution(s)");
		for (int i = 1; i <= 3; i++) {
			createAction();
		}
		select(labelText);
	}

	private void addContentAdapter(IEventBInputText input,
			IInternalElementType<?> elementType, IAttributeType attributeType) {
		final WizardProposalProvider provider = getProposalProviderWithIdent(
				elementType, attributeType);
		addProposalAdapter(provider, input);
		providerListener.addProvider(provider);
	}
	
	private void addIdentifierAdapter(IEventBInputText input,
			IInternalElementType<?> elementType, IAttributeType attributeType) {
		providerListener.addInputText(input);
		addProposalAdapter(elementType, attributeType, input);
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
			final IEventBInputText parLabel = createBText(parComposite, EMPTY);
			addIdentifierAdapter(parLabel, IParameter.ELEMENT_TYPE,
					LABEL_ATTRIBUTE);
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
		addContentAdapter(actionLabel, IAction.ELEMENT_TYPE, LABEL_ATTRIBUTE);
		createSpace();
		final IEventBInputText actionSub = createContentText();
		addContentAdapter(actionSub, IAction.ELEMENT_TYPE, ASSIGNMENT_ATTRIBUTE);
		actTexts.add(newWidgetPair(actionLabel, actionSub));
	}
	
	private IEventBInputText createGuard() {
		final IEventBInputText grdLabel = createNameText(guardPrefix + ++grdCount);
		moveAbove(grdLabel, actionSeparator);
		addContentAdapter(grdLabel, IGuard.ELEMENT_TYPE, LABEL_ATTRIBUTE);
		final Composite separator = createSpace();
		separator.moveAbove(actionSeparator);
		final IEventBInputText grdPred = createContentText();
		moveAbove(grdPred, actionSeparator);
		addContentAdapter(grdPred, IGuard.ELEMENT_TYPE, PREDICATE_ATTRIBUTE);
		grdTexts.add(newWidgetPair(grdLabel, grdPred));
		return grdPred;
	}
	
	private void addValues() {
		try {
			RodinCore.run(new IWorkspaceRunnable() {

				@Override
				public void run(IProgressMonitor pm) throws RodinDBException {

					final String[] grdNames = getGrdLabels();
					final String[] lGrdPredicates = getGrdPredicates();

					final String[] actNames = getActLabels();
					final String[] lActSub = getActSubstitutions();

					final String[] paramNames = parsResult.toArray(new String[parsResult
							.size()]);
					EventBEditorUtils.newEvent(editor, labelResult, paramNames,
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
		fillPairResult(grdTexts, grdResults);
		
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
		return getFirst(grdResults);
	}

	/**
	 * Get the list of guard predicates of the new event.
	 * <p>
	 * 
	 * @return the list of the guard predicates as input by user
	 */
	public String[] getGrdPredicates() {
		return getSecond(grdResults);
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
		disposePairs(grdTexts);
		disposePairs(actTexts);
		return super.close();
	}
}
