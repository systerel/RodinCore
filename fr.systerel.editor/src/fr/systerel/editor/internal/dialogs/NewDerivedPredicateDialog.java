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
 *     Systerel - increased index of label when add new input
 *     Systerel - add widget to edit theorem attribute
 *******************************************************************************/
package fr.systerel.editor.internal.dialogs;

import static org.eclipse.jface.dialogs.IDialogConstants.CANCEL_ID;
import static org.eclipse.jface.dialogs.IDialogConstants.CANCEL_LABEL;
import static org.eclipse.jface.dialogs.IDialogConstants.OK_ID;
import static org.eclipse.jface.dialogs.IDialogConstants.OK_LABEL;
import static org.eventb.core.EventBAttributes.LABEL_ATTRIBUTE;
import static org.eventb.core.EventBAttributes.PREDICATE_ATTRIBUTE;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eventb.core.ILabeledElement;
import org.eventb.internal.ui.IEventBInputText;
import org.eventb.internal.ui.UIUtils;
import org.eventb.internal.ui.eventbeditor.Triplet;
import org.eventb.internal.ui.preferences.PreferenceUtils;
import org.rodinp.core.IInternalElementType;

import fr.systerel.editor.actions.IWizardElementMaker;

/**
 * @author htson
 *         <p>
 *         This class extends the Dialog class and provides an input dialog for
 *         entering a list of element name with content.
 */
public class NewDerivedPredicateDialog<T extends ILabeledElement> extends
		EventBDialog {

	private final Collection<Triplet<String, String, Boolean>> results;
	private final List<Triplet<IEventBInputText, IEventBInputText, Button>> texts;

	private static final String messageName = "Label(s)";

	private static final String messageContent = "Predicate(s)";

	private static final String messageIsDerived= "Theorem";

	private final String prefix;

	private final String firstIndex;

	private final IInternalElementType<?> type;
	
	/**
	 * Constructor.
	 * 
	 * @param root
	 *            the root element of the editor
	 * @param parentShell
	 *            the parent shell of the dialog
	 * @param title
	 *            the title of the dialog
	 * @param type
	 *            the element type for wizard
	 */
	public NewDerivedPredicateDialog(IWizardElementMaker derivedPredicateMaker,
			String title, IInternalElementType<?> type) {
		super(derivedPredicateMaker.getShell(),
				derivedPredicateMaker.getRoot(), title, derivedPredicateMaker);
		this.type = type;
		results = new ArrayList<Triplet<String, String, Boolean>>();
		texts = new ArrayList<Triplet<IEventBInputText, IEventBInputText, Button>>();
		setShellStyle(getShellStyle() | SWT.RESIZE);

		prefix = PreferenceUtils.getAutoNamePrefix(root, type);
		firstIndex = UIUtils.getFreeElementLabelIndex(root, type, prefix);
	}

	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, MORE_ID, MORE_LABEL);
		createDefaultButton(parent, OK_ID, OK_LABEL);
		createButton(parent, CANCEL_ID, CANCEL_LABEL);
	}

	@Override
	protected void createContents() {
		setDebugBackgroundColor();
		setFormGridLayout(getBody(), 3);
		setFormGridData();

		createLabel(getBody(), messageName);
		createLabel(getBody(), messageContent);
		createLabel(getBody(), messageIsDerived);

		for (int i = 0; i < 3; i++) {
			createInputs();
		}
		select(texts.get(0).getSecond());
	}
	
	private void createInputs() {
		final int index = Integer.parseInt(firstIndex) + texts.size();
		final IEventBInputText name = createNameInputText(getBody(), prefix
				+ index);
		addProposalAdapter(type, LABEL_ATTRIBUTE, name);
		final IEventBInputText content = createContentInputText(getBody());
		addProposalAdapter(type, PREDICATE_ATTRIBUTE, content);
		final Button button = createIsTheoremCheck(getBody());
		texts.add(newWidgetTriplet(name, content, button));
	}

	@Override
	protected void buttonPressed(int buttonId) {
		if (buttonId == CANCEL_ID) {
			// do nothing
		} else if (buttonId == MORE_ID) {
			createInputs();
			updateSize();
		} else if (buttonId == OK_ID) {
			fillTripletResult(texts, results);
		}
		super.buttonPressed(buttonId);
	}

	/**
	 * Get the list of new names.
	 * <p>
	 * 
	 * @return The list of new names (strings)
	 */
	public String[] getNewNames() {
		return getFirstTriplet(results);
	}

	/**
	 * Get the list of new contents.
	 * <p>
	 * 
	 * @return The list of new contents (strings)
	 */
	public String[] getNewContents() {
		return getSecondTriplet(results);
	}

	public boolean[] getIsTheorem() {
		return getThirdTriplet(results);
	}

	@Override
	public boolean close() {
		disposeTriplets(texts);
		return super.close();
	}
	
}
