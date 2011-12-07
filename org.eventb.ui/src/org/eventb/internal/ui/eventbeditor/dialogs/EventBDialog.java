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
 *     Systerel - add getNameInputText and getContentInputText to factor several methods
 *     Systerel - added checkNewIdentifiers()
 *     ETH Zurich - adapted to org.rodinp.keyboard
 *     Systerel - add widget to edit theorem attribute
 *******************************************************************************/
package org.eventb.internal.ui.eventbeditor.dialogs;

import static org.eclipse.jface.dialogs.IDialogConstants.CLIENT_ID;
import static org.eventb.internal.ui.EventBUtils.getFormulaFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.fieldassist.IContentProposalProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eventb.core.IEventBRoot;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.internal.ui.EventBMath;
import org.eventb.internal.ui.EventBSharedColor;
import org.eventb.internal.ui.EventBText;
import org.eventb.internal.ui.IEventBInputText;
import org.eventb.internal.ui.Pair;
import org.eventb.internal.ui.UIUtils;
import org.eventb.internal.ui.autocompletion.ContentProposalFactory;
import org.eventb.internal.ui.autocompletion.WizardProposalProvider;
import org.eventb.internal.ui.eventbeditor.EventBEditorUtils;
import org.eventb.internal.ui.eventbeditor.Triplet;
import org.eventb.internal.ui.utils.Messages;
import org.rodinp.core.IAttributeType;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.RodinCore;
import org.rodinp.core.location.IAttributeLocation;
import org.rodinp.keyboard.RodinKeyboardPlugin;

/**
 * @author htson
 *         <p>
 *         This class extends the Dialog class and provides an input dialog for
 *         new event with some local varialbes, guards and actSubstitutions.
 */
public abstract class EventBDialog extends Dialog {
	protected Collection<Text> dirtyTexts;

	protected FormToolkit toolkit;

	protected ScrolledForm scrolledForm;

	private String title;

	private final int MAX_WIDTH = 800;

	private final int MAX_HEIGHT = 500;
	
	protected static final int ADD_ID = CLIENT_ID + 1;
	protected static final int MORE_PARAMETER_ID = CLIENT_ID + 2;
	protected static final int MORE_GUARD_ID = CLIENT_ID + 3;
	protected static final int MORE_ACTION_ID = CLIENT_ID + 4;
	protected static final int MORE_INVARIANT_ID = CLIENT_ID + 5;
	protected static final int MORE_AXIOM_ID = CLIENT_ID + 6;
	protected static final int MORE_ELEMENT_ID = CLIENT_ID + 7;
	protected static final int MORE_ID = CLIENT_ID + 8;

	protected static final String MORE_PARAMETER_LABEL = "More &Par.";
	protected static final String MORE_GUARD_LABEL = "More &Grd.";
	protected static final String MORE_ACTION_LABEL = "More A&ct.";
	protected static final String MORE_INVARIANT_LABEL = "&More Inv.";
	protected static final String MORE_AXIOM_LABEL = "&More Axm.";
	protected static final String MORE_ELEMENT_LABEL = "&More Element";
	protected static final String MORE_LABEL = "&More";
	protected static final String ADD_LABEL = "&Add";
	protected static final String EMPTY = "";

	protected final IEventBRoot root;

	private final int FORM_SPACING = 10;

	private final int DEFAULT_SPAN = 1;
	
	/**
	 * Constructor
	 * 
	 * @param parentShell
	 *            the parent shell of the dialog
	 * @param root
	 *            the root element that the elements will be added in
	 * @param title
	 *            the title of the dialog
	 */
	public EventBDialog(Shell parentShell, IEventBRoot root, String title) {
		super(parentShell);
		this.root = root;
		this.title = title;
		dirtyTexts = new HashSet<Text>();
		setShellStyle(getShellStyle() | SWT.RESIZE);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.window.Window#configureShell(org.eclipse.swt.widgets.Shell)
	 */
	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText(title);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.dialogs.Dialog#createDialogArea(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		final Composite composite = (Composite) super.createDialogArea(parent);

		toolkit = new FormToolkit(parent.getDisplay());
		toolkit.setBackground(parent.getBackground());
		toolkit.setBorderStyle(SWT.BORDER);

		scrolledForm = toolkit.createScrolledForm(composite);
		final Composite body = scrolledForm.getBody();

		createContents();

		composite.pack();

		toolkit.paintBordersFor(body);
		applyDialogFont(body);
		return body;
	}

	protected abstract void createContents();

	protected abstract class AbstractListener implements ModifyListener{
		private final Text textWidget;

		public AbstractListener(Text textWidget) {
			this.textWidget = textWidget;
		}

		@Override
		public void modifyText(ModifyEvent e) {
			final Text varText = (Text) e.widget;
			if (!dirtyTexts.contains(textWidget)) {
				final String text = varText.getText();
				if (text.equals(EMPTY))
					textWidget.setText(EMPTY);
				else
					textWidget.setText(text + " " + getSymbol() + " ");
			}
		}
		
		protected abstract String getSymbol();
	}
	
	protected class GuardListener extends AbstractListener {
		public GuardListener(Text textWidget) {
			super(textWidget);
		}

		@Override
		protected String getSymbol() {
			return "\u2208";
		}
	}

	protected class ActionListener extends AbstractListener {
		public ActionListener(Text textWidget) {
			super(textWidget);
		}

		@Override
		protected String getSymbol() {
			return "\u2254";
		}
	}

	protected class DirtyStateListener implements ModifyListener {

		@Override
		public void modifyText(ModifyEvent e) {
			final Text text = (Text) e.widget;
			if (EventBEditorUtils.DEBUG)
				EventBEditorUtils.debug("Modified: " + text.getText());
			if (text.getText().equals(EMPTY)) {
				dirtyTexts.remove(text);
				text.setBackground(EventBSharedColor.getSystemColor(SWT.COLOR_WHITE));
			} else if (text.isFocusControl()) {
				dirtyTexts.add(text);
				text.setBackground(EventBSharedColor.getSystemColor(SWT.COLOR_YELLOW));
			}
		}
	}

	protected void clearDirtyTexts() {
		for (Text text : dirtyTexts) {
			text.setBackground(EventBSharedColor.getSystemColor(SWT.COLOR_WHITE));
		}
		dirtyTexts.clear();
	}

	protected void updateSize() {
		final Composite parent = this.getContents().getParent();
		final Point curr = parent.getSize();
		final Point pt = parent.computeSize(SWT.DEFAULT,
				SWT.DEFAULT);

		if (curr.x < pt.x || curr.y < pt.y) {
			final int x = curr.x < pt.x ? pt.x : curr.x;
			final int y = curr.y < pt.y ? pt.y : curr.y;
			if (x <= MAX_WIDTH && y <= MAX_HEIGHT)
				parent.setSize(x, y);
		}
		else { // Bug: resize to force refresh
			parent.setSize(curr.x + 1, curr.y);
		}
		scrolledForm.reflow(true);
	}

	protected Button createIsTheoremCheck(Composite composite) {
		return toolkit.createButton(composite, "", SWT.CHECK);
	}

	protected Button createIsTheoremToogle(Composite composite) {
		final Button button = toolkit.createButton(composite, "not theorem",
				SWT.TOGGLE);
		button.addSelectionListener(new SelectionListener() {

			private String getText() {
				return (button.getSelection() ? "theorem" : "not theorem");
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}

			@Override
			public void widgetSelected(SelectionEvent e) {
				button.setText(getText());
			}
		});
		return button;
	}

	protected IEventBInputText createNameInputText(Composite composite,
			String text) {
		return new EventBText(createText(composite, text, 50, false, DEFAULT_SPAN));
	}

	protected EventBMath createContentInputText(Composite composite) {
		final EventBMath input = new EventBMath(createText(composite, EMPTY,
				150, true, DEFAULT_SPAN));
		return input;
	}

	protected EventBMath createContentInputText(Composite composite,
			int horizontalSpan) {
		final EventBMath input = new EventBMath(createText(composite, EMPTY,
				150, true, horizontalSpan));
		return input;
	}
	
	protected EventBText createBText(Composite parent, String value) {
		return new EventBText(createText(parent, value, true));
	}

	protected EventBText createBText(Composite parent, String value,
			int widthHint, boolean grabExcessHorizontalSpace) {
		return new EventBText(createText(parent, value,
				newGridData(grabExcessHorizontalSpace, widthHint)));
	}

	protected EventBText createBText(Composite parent, String value,
			int widthHint, boolean grabExcessHorizontalSpace, int horizontalSpan) {
		return new EventBText(createText(parent, value,
				newGridData(true, widthHint, horizontalSpan)));
	}

	private Text createText(Composite parent, String value,
			boolean grabExcessHorizontalSpace) {
		final GridData gd = newGridData(grabExcessHorizontalSpace);
		return createText(parent, value, gd);
	}

	protected GridData newGridData(boolean grabExcessHorizontalSpace) {
		return new GridData(SWT.FILL, SWT.NONE, grabExcessHorizontalSpace,
				false);
	}

	protected GridData newGridData(boolean grabExcessHorizontalSpace,
			int widthHint) {
		final GridData gd = newGridData(grabExcessHorizontalSpace);
		gd.widthHint = widthHint;
		return gd;
	}

	protected GridData newGridData(boolean grabExcessHorizontalSpace,
			int widthHint, int horizontalSpan) {
		final GridData gd = newGridData(grabExcessHorizontalSpace, widthHint);
		gd.horizontalSpan = horizontalSpan;
		return gd;
	}

	private Text createText(Composite parent, String value, int widthHint,
			boolean grabExcessHorizontalSpace, int horizontalSpan) {
		final GridData gd = newGridData(grabExcessHorizontalSpace, widthHint,
				horizontalSpan);
		return createText(parent, value, gd);
	}

	private Text createText(Composite parent, String value, GridData gd) {
		final Text text = toolkit.createText(parent, value);
		text.setLayoutData(gd);
		text.addModifyListener(new DirtyStateListener());
		return text;
	}

	protected void createLabel(Composite parent, String text, int hspan) {
		final Label widget = toolkit.createLabel(parent, text, SWT.NONE);
		final GridData gd = new GridData(SWT.FILL, SWT.CENTER, false, false,
				hspan, DEFAULT_SPAN);
		widget.setLayoutData(gd);
	}

	protected void createLabel(Composite parent, String text) {
		createLabel(parent, text, DEFAULT_SPAN);
	}

	protected GridLayout newLayout(int numColumns, int verticalSpacing,
			int horizontalSpacing) {
		final GridLayout layout = new GridLayout();
		layout.numColumns = numColumns;
		layout.verticalSpacing = verticalSpacing;
		layout.horizontalSpacing = horizontalSpacing;
		return layout;
	}
	
	protected void setFormGridLayout(Composite composite, int numColumns) {
		composite.setLayout(newLayout(numColumns, FORM_SPACING, FORM_SPACING));
	}
	
	protected void select(IEventBInputText text){
		text.getTextWidget().selectAll();
		text.getTextWidget().setFocus();
	}
	
	protected void dispose(Collection<IEventBInputText> collection) {
		for (IEventBInputText text : collection)
			text.dispose();
	}

	protected void disposePairs(
			Collection<Pair<IEventBInputText, IEventBInputText>> pairs) {
		for (Pair<IEventBInputText, IEventBInputText> pair : pairs) {
			pair.getFirst().dispose();
			pair.getSecond().dispose();
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

	protected void setDebugBackgroundColor() {
		if (EventBEditorUtils.DEBUG)
			scrolledForm.getBody().setBackground(
					EventBSharedColor.getSystemColor(SWT.COLOR_CYAN));
	}
	
	protected void setFormGridData() {
		final GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
		scrolledForm.setLayoutData(gd);
	}
	
	protected void setText(IEventBInputText text, String value){
		text.getTextWidget().setText(value);
	}
	
	protected void addGuardListener(IEventBInputText text,
			IEventBInputText toModify) {
		text.getTextWidget().addModifyListener(
				new GuardListener(toModify.getTextWidget()));
	}
	
	protected Composite getBody() {
		return scrolledForm.getBody();
	}
	
	protected void createDefaultButton(Composite parent, int id, String text) {
		createButton(parent, id, text, true);
	}

	protected void createButton(Composite parent, int id, String text) {
		createButton(parent, id, text, false);
	}
	
	protected boolean isValid(IEventBInputText text){
		return dirtyTexts.contains(text.getTextWidget());
	}

	protected void fillResult(Collection<IEventBInputText> fields,
			Collection<String> results) {
		for (IEventBInputText field : fields) {
			final Text text = field.getTextWidget();
			if (dirtyTexts.contains(text)) {
				results.add(text.getText());
			}
		}
	}

	protected void fillPairResult(
			Collection<Pair<IEventBInputText, IEventBInputText>> fields,
			Collection<Pair<String, String>> result) {
		for (Pair<IEventBInputText, IEventBInputText> pair : fields) {
			final IEventBInputText labelInput = pair.getFirst();
			final IEventBInputText contentInput = pair.getSecond();
			if (dirtyTexts.contains(contentInput.getTextWidget())) {
				final String name = getText(labelInput);
				result.add(new Pair<String, String>(name,
						translate(contentInput)));
			}
		}
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

	protected String getText(IEventBInputText text) {
		return text.getTextWidget().getText();
	}

	protected String translate(IEventBInputText text) {
		return RodinKeyboardPlugin.getDefault().translate(
				text.getTextWidget().getText());
	}
	
	protected String[] getFirst(Collection<Pair<String, String>> pairs) {
		final Collection<String> result = new ArrayList<String>();
		for (Pair<String, String> p : pairs)
			result.add(p.getFirst());
		return result.toArray(new String[result.size()]);
	}

	protected String[] getSecond(Collection<Pair<String, String>> pairs) {
		final Collection<String> result = new ArrayList<String>();
		for (Pair<String, String> p : pairs)
			result.add(p.getSecond());
		return result.toArray(new String[result.size()]);
	}
	
	protected Pair<IEventBInputText, IEventBInputText> newWidgetPair(
			IEventBInputText name, IEventBInputText content) {
		return new Pair<IEventBInputText, IEventBInputText>(name, content);
	}
	
	protected static boolean checkNewIdentifiers(List<String> names,
			boolean showInfoOnProblem, FormulaFactory ff) {
		final Collection<String> invalidIdentifiers = getInvalidIdentifiers(
				names, ff);
		if (!invalidIdentifiers.isEmpty()) {
			if (showInfoOnProblem) {
				UIUtils.showInfo(Messages.dialogs_invalidIdentifiers + ":\n"
						+ invalidIdentifiers);
			}
			return false;
		}
		final Collection<String> duplicateNames = getDuplicateNames(names);
		if (!duplicateNames.isEmpty()) {
			if (showInfoOnProblem) {
				UIUtils.showInfo(Messages.dialogs_duplicateNames + ":\n"
						+ duplicateNames);
			}
			return false;
		}
		return true;
	}

	private static Collection<String> getInvalidIdentifiers(
			Collection<String> names, FormulaFactory ff) {
		final List<String> invalidIdentifiers = new ArrayList<String>();
		for (String name : names) {
			if (!ff.isValidIdentifierName(name)) {
				invalidIdentifiers.add(name);
			}
		}
		return invalidIdentifiers;
	}

	private static Collection<String> getDuplicateNames(List<String> names) {
		final Set<String> duplicateNames = new LinkedHashSet<String>();
		for (int i = 1; i < names.size(); i++) {
			final String name = names.get(i - 1);
			if (names.subList(i, names.size()).contains(name)) {
				duplicateNames.add(name);
			}
		}
		return duplicateNames;
	}
	
	/**
	 * Construct a content proposal adapter that can assist the user with
	 * choosing content for a Event-B Input Text.
	 * 
	 * @param elementType
	 *            type of element associated to the input
	 * @param attributeType
	 *            type of attribute associated to the input
	 * @param input
	 *            a Event-B Input Text
	 */
	protected void addProposalAdapter(
			IInternalElementType<?> elementType, IAttributeType attributeType,
			IEventBInputText input) {
		final IInternalElement element = root.getInternalElement(elementType,
				"tmp");
		final FormulaFactory ff = getFormulaFactory(root);
		final IAttributeLocation location = RodinCore.getInternalLocation(
				element, attributeType);
		ContentProposalFactory.makeContentProposal(location,
				input.getTextWidget(), ff);
	}
	
	/**
	 * Construct a content proposal adapter that can assist the user with
	 * choosing content for a Event-B Input Text.
	 * 
	 * @param input
	 *            a Event-B Input Text
	 */
	protected void addProposalAdapter(IContentProposalProvider provider,
			IEventBInputText input) {
		ContentProposalFactory.makeContentProposal(provider,
				input.getTextWidget());
	}

	/**
	 * Construct a content proposal adapter that can assist the user with
	 * choosing content for a Event-B Input Text.
	 * 
	 * @param elementType
	 *            type of element associated to the input
	 * @param attributeType
	 *            type of attribute associated to the input
	 */
	protected WizardProposalProvider getProposalProviderWithIdent(
			IInternalElementType<?> elementType, IAttributeType attributeType) {
		final IInternalElement element = root.getInternalElement(elementType,
				"tmp");
		final FormulaFactory ff = getFormulaFactory(root);
		final IAttributeLocation location = RodinCore.getInternalLocation(
				element, attributeType);
		return new WizardProposalProvider(location, ff);
	}
	
	public IEventBRoot getRoot() {
		return root;
	}
}
