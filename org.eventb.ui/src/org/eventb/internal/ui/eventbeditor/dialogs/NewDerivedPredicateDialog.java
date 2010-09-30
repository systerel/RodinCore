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
package org.eventb.internal.ui.eventbeditor.dialogs;

import static org.eclipse.jface.dialogs.IDialogConstants.CANCEL_ID;
import static org.eclipse.jface.dialogs.IDialogConstants.CANCEL_LABEL;
import static org.eclipse.jface.dialogs.IDialogConstants.OK_ID;
import static org.eclipse.jface.dialogs.IDialogConstants.OK_LABEL;
import static org.eventb.core.EventBAttributes.PREDICATE_ATTRIBUTE;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eventb.core.IEventBRoot;
import org.eventb.core.ILabeledElement;
import org.eventb.internal.ui.IEventBInputText;
import org.eventb.internal.ui.Pair;
import org.eventb.internal.ui.UIUtils;
import org.eventb.internal.ui.preferences.PreferenceUtils;
import org.eventb.ui.eventbeditor.IEventBEditor;
import org.rodinp.core.IInternalElementType;

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
	 * @param editor
	 *            the editor that called this dialog
	 * @param root
	 *            the root element of the editor
	 * @param parentShell
	 *            the parent shell of the dialog
	 * @param title
	 *            the title of the dialog
	 * @param type
	 *            the element type for wizard
	 */
	public NewDerivedPredicateDialog(IEventBEditor<?> editor, IEventBRoot root,
			Shell parentShell, String title, IInternalElementType<?> type) {
		super(parentShell, root, title);
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
		final IEventBInputText content = createContentInputText(getBody(),
				type, PREDICATE_ATTRIBUTE);
		final Button button = createIsTheoremButton();
		texts.add(newWidgetTriplet(name, content, button));
	}

	private Button createIsTheoremButton(){
		return toolkit.createButton(getBody(), "", SWT.CHECK);
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

	protected void fillTripletResult(
			Collection<Triplet<IEventBInputText, IEventBInputText, Button>> fields,
			Collection<Triplet<String, String, Boolean>> result) {
		for (Triplet<IEventBInputText, IEventBInputText, Button> triplet : fields) {
			final IEventBInputText labelInput = triplet.getFirst();
			final IEventBInputText contentInput = triplet.getSecond();
			final boolean bool = triplet.getThird().getSelection();
			if (dirtyTexts.contains(contentInput.getTextWidget())) {
				final String name = getText(labelInput);
				result.add(new Triplet<String, String, Boolean>(name,
						translate(contentInput), bool));
			}
		}
	}

	protected String[] getFirstTriplet(
			Collection<Triplet<String, String, Boolean>> triplets) {
		final Collection<String> result = new ArrayList<String>();
		for (Triplet<String, String, Boolean> t : triplets)
			result.add(t.getFirst());
		return result.toArray(new String[result.size()]);
	}

	protected String[] getSecondTriplet(
			Collection<Triplet<String, String, Boolean>> triplets) {
		final Collection<String> result = new ArrayList<String>();
		for (Triplet<String, String, Boolean> t : triplets)
			result.add(t.getSecond());
		return result.toArray(new String[result.size()]);
	}

	protected boolean[] getThirdTriplet(
			Collection<Triplet<String, String, Boolean>> triplets) {
		final boolean[] result = new boolean[triplets.size()];
		int i = 0;
		for (Triplet<String, String, Boolean> t : triplets) {
			result[i] = t.getThird();
			i++;
		}
		return result;
	}

	protected Triplet<IEventBInputText, IEventBInputText, Button> newWidgetTriplet(
			IEventBInputText name, IEventBInputText content, Button button) {
		return new Triplet<IEventBInputText, IEventBInputText, Button>(name,
				content, button);
	}

	protected void disposeTriplets(
			Collection<Triplet<IEventBInputText, IEventBInputText, Button>> triplets) {
		for (Triplet<IEventBInputText, IEventBInputText, Button> triplet : triplets) {
			triplet.getFirst().dispose();
			triplet.getSecond().dispose();
			triplet.getThird().dispose();
		}
	}

	class Triplet<X extends Object, Y extends Object, Z extends Object> {
		private final Pair<X, Pair<Y, Z>> obj;

		public Triplet(X x, Y y, Z z) {
			obj = new Pair<X, Pair<Y, Z>>(x, new Pair<Y, Z>(y, z));
		}

		public X getFirst() {
			return obj.getFirst();
		}

		public Y getSecond() {
			return obj.getSecond().getFirst();
		}

		public Z getThird() {
			return obj.getSecond().getSecond();
		}

		@Override
		public boolean equals(Object o) {
			return obj.equals(o);
		}

		@Override
		public String toString() {
			return obj.toString();
		}
	}
	
}
