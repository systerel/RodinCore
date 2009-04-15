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
 *     Systerel - increased index of label when add new input
 *******************************************************************************/
package org.eventb.internal.ui.eventbeditor.dialogs;

import static org.eclipse.jface.dialogs.IDialogConstants.CANCEL_ID;
import static org.eclipse.jface.dialogs.IDialogConstants.CANCEL_LABEL;
import static org.eclipse.jface.dialogs.IDialogConstants.OK_ID;
import static org.eclipse.jface.dialogs.IDialogConstants.OK_LABEL;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eventb.core.ILabeledElement;
import org.eventb.internal.ui.IEventBInputText;
import org.eventb.internal.ui.Pair;
import org.eventb.internal.ui.UIUtils;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalElementType;

/**
 * @author htson
 *         <p>
 *         This class extends the Dialog class and provides an input dialog for
 *         entering a list of element name with content.
 */
public class NewElementNameContentDialog<T extends ILabeledElement> extends
		EventBDialog {

	private final Collection<Pair<String, String>> results;
	private final List<Pair<IEventBInputText, IEventBInputText>> texts;

	private static final String messageName = "Label(s)";

	private static final String messageContent = "Predicate(s)";

	private final String prefix;

	private final String firstIndex;

	/**
	 * Constructor.
	 * <p>
	 * 
	 * @param parentShell
	 *            The parent shell of the dialog
	 * @param title
	 *            The title of the dialog
	 * @param root
	 *            The root element of the editor
	 * @param type
	 *            The element type for wizard
	 */
	public NewElementNameContentDialog(Shell parentShell, String title,
			IInternalElement root, IInternalElementType<?> type) {
		super(parentShell, title);
		results = new ArrayList<Pair<String,String>>();
		texts = new ArrayList<Pair<IEventBInputText,IEventBInputText>>();
		setShellStyle(getShellStyle() | SWT.RESIZE);

		prefix = UIUtils.getAutoNamePrefix(root, type);
		firstIndex = UIUtils.getFreeElementLabelIndex(root, type, prefix);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.dialogs.Dialog#createButtonsForButtonBar(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, MORE_ID, MORE_LABEL);
		createDefaultButton(parent, OK_ID, OK_LABEL);
		createButton(parent, CANCEL_ID, CANCEL_LABEL);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.dialogs.Dialog#createDialogArea(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected void createContents() {
		setDebugBackgroundColor();
		setFormGridLayout(getBody(), 2);
		setFormGridData();

		createLabel(getBody(),messageName);
		createLabel(getBody(),messageContent);

		for (int i = 0; i < 3; i++) {
			createInputs();
		}
		select(texts.get(0).getSecond());
	}
	
	private void createInputs() {
		final int index = Integer.parseInt(firstIndex) + texts.size();
		final IEventBInputText name = createNameInputText(getBody(), prefix
				+ index);
		final IEventBInputText content = createContentInputText(getBody());
		texts.add(newWidgetPair(name, content));
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.dialogs.Dialog#buttonPressed(int)
	 */
	@Override
	protected void buttonPressed(int buttonId) {
		if (buttonId == CANCEL_ID) {
			// do nothing
		} else if (buttonId == MORE_ID) {
			createInputs();
			updateSize();
		} else if (buttonId == OK_ID) {
			fillPairResult(texts, results);
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
		return getFirst(results);
	}

	/**
	 * Get the list of new contents.
	 * <p>
	 * 
	 * @return The list of new contents (strings)
	 */
	public String[] getNewContents() {
		return getSecond(results);
	}

	@Override
	public boolean close() {
		disposePairs(texts);
		return super.close();
	}

}
