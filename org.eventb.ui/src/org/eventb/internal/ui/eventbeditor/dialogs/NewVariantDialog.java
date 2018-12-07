/*******************************************************************************
 * Copyright (c) 2005, 2018 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - used EventBSharedColor
 *     Systerel - refactored after wizard refactoring
 *     Systerel - lexicographic variants
 *******************************************************************************/
package org.eventb.internal.ui.eventbeditor.dialogs;

import static java.lang.Integer.parseInt;
import static org.eclipse.jface.dialogs.IDialogConstants.CANCEL_ID;
import static org.eclipse.jface.dialogs.IDialogConstants.CANCEL_LABEL;
import static org.eclipse.jface.dialogs.IDialogConstants.OK_ID;
import static org.eclipse.jface.dialogs.IDialogConstants.OK_LABEL;
import static org.eventb.core.EventBAttributes.LABEL_ATTRIBUTE;
import static org.eventb.core.EventBAttributes.PREDICATE_ATTRIBUTE;
import static org.eventb.internal.ui.UIUtils.getFreeElementLabelIndex;
import static org.eventb.internal.ui.preferences.PreferenceUtils.getAutoNamePrefix;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eventb.core.IMachineRoot;
import org.eventb.core.IVariant;
import org.eventb.internal.ui.IEventBInputText;
import org.eventb.internal.ui.Pair;
import org.rodinp.core.IInternalElementType;

/**
 * @author htson
 *         <p>
 *         This class extends the Dialog class and provides an input dialog for
 *         entering a list of labelled variants
 */
public class NewVariantDialog extends EventBDialog {

	private final Collection<Pair<String, String>> results;
	private final List<Pair<IEventBInputText, IEventBInputText>> texts;

	private static final String messageName = "Label(s)";

	private static final String messageContent = "Expression(s)";

	private final String prefix;

	private final int firstIndex;

	private final IInternalElementType<?> type;

	/**
	 * Constructor.
	 * 
	 * @param root        the root element to which variants will be added
	 * @param parentShell The parent shell of the dialog
	 * @param title       The title of the dialog
	 */
	public NewVariantDialog(IMachineRoot root, Shell parentShell, String title) {
		super(parentShell, root, title);
		this.type = IVariant.ELEMENT_TYPE;
		this.results = new ArrayList<>();
		this.texts = new ArrayList<>();
		setShellStyle(getShellStyle() | SWT.RESIZE);

		this.prefix = getAutoNamePrefix(root, type);
		this.firstIndex = parseInt(getFreeElementLabelIndex(root, type, prefix));
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
		setFormGridLayout(getBody(), 2);
		setFormGridData();

		createLabel(getBody(), messageName);
		createLabel(getBody(), messageContent);

		for (int i = 0; i < 3; i++) {
			createInputs(firstIndex + i);
		}
	}

	private IEventBInputText createInputs(int index) {
		final IEventBInputText label = createNameInputText(getBody(), prefix + index);
		addProposalAdapter(type, LABEL_ATTRIBUTE, label);
		final IEventBInputText content = createContentInputText(getBody());
		addProposalAdapter(type, PREDICATE_ATTRIBUTE, content);
		texts.add(newWidgetPair(label, content));
		return content;
	}

	@Override
	protected void buttonPressed(int buttonId) {
		if (buttonId == CANCEL_ID) {
			// do nothing
		} else if (buttonId == MORE_ID) {
			final int index = firstIndex + texts.size();
			final IEventBInputText text = createInputs(index);
			updateSize();
			select(text);
		} else if (buttonId == OK_ID) {
			fillPairResult(texts, results);
		}
		super.buttonPressed(buttonId);
	}

	public String[] getNewLabels() {
		return getFirst(results);
	}

	public String[] getNewExpressions() {
		return getSecond(results);
	}

	@Override
	public boolean close() {
		disposePairs(texts);
		return super.close();
	}

}
