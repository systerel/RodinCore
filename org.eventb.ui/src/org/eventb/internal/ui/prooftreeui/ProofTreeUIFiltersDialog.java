/*******************************************************************************
 * Copyright (c) 2000, 2024 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     ETH Zurich - adapted from org.eclipse.jdt.internal.ui.filters.CustomFiltersDialog
 *******************************************************************************/
package org.eventb.internal.ui.prooftreeui;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.joining;
import static org.eclipse.swt.events.SelectionListener.widgetSelectedAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.PatternFilter;
import org.eclipse.ui.dialogs.SelectionDialog;
import org.eventb.core.seqprover.IReasonerRegistry;
import org.eventb.core.seqprover.SequentProver;

public class ProofTreeUIFiltersDialog extends SelectionDialog {

	private static final String SEPARATOR = ","; //$NON-NLS-1$

	private ProofTreeUIPage proofTreeUI;

	static List<RuleFilter> fBuiltInFilters = null;

	CheckboxTableViewer fCheckBoxList;

	Button fEnableUserDefinedPatterns;

	Text fUserDefinedPatterns;

	/**
	 * Creates a dialog to customize Java element filters.
	 * 
	 * @param shell
	 *            the parent shell
	 */
	public ProofTreeUIFiltersDialog(Shell shell, ProofTreeUIPage proofTreeUI) {
		super(shell);

		this.proofTreeUI = proofTreeUI;

		if (fBuiltInFilters == null)
			getFilters();

		setShellStyle(getShellStyle() | SWT.RESIZE);
	}

	private void getFilters() {
		if (ProofTreeUIUtils.DEBUG)
			ProofTreeUIUtils.debug("Get initial filter list");
		final IReasonerRegistry reasonerRegistry = 
			SequentProver.getReasonerRegistry();
		// Group reasoners with several levels by name
		Map<String, Set<String>> reasonersByName = new HashMap<>();
		for (String reasonerID : reasonerRegistry.getRegisteredIDs()) {
			String name = reasonerRegistry.getReasonerDesc(reasonerID).getName();
			reasonersByName.computeIfAbsent(name, key -> new TreeSet<>()).add(reasonerID);
		}
		// Display a filter for each name (sorted), with possibly several levels
		fBuiltInFilters = new ArrayList<RuleFilter>(reasonersByName.size());
		for (var reasonerGroup : reasonersByName.entrySet()) {
			fBuiltInFilters.add(new RuleFilter(reasonerGroup.getKey(), reasonerGroup.getValue()));
		}
		fBuiltInFilters.sort(comparing(filter -> filter.reasonerName));
	}

	@Override
	protected void configureShell(Shell shell) {
		setTitle("Proof Rule Filters");
		setMessage("Please choose a set of reasoners to filter out");
		super.configureShell(shell);
		// PlatformUI.getWorkbench().getHelpSystem().setHelp(shell,
		// IJavaHelpContextIds.CUSTOM_FILTERS_DIALOG);
	}

	/**
	 * Overrides method in Dialog
	 * 
	 * @see org.eclipse.jface.dialogs.Dialog#createDialogArea(Composite)
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		initializeDialogUnits(parent);

		// create a composite with standard margins and spacing
		Composite composite = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.marginHeight = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_MARGIN);
		layout.marginWidth = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_MARGIN);
		layout.verticalSpacing = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_SPACING);
		layout.horizontalSpacing = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_SPACING);
		composite.setLayout(layout);
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));
		composite.setFont(parent.getFont());
		Composite group = composite;

		// Checkbox
		fEnableUserDefinedPatterns = new Button(group, SWT.CHECK);
		fEnableUserDefinedPatterns.setFocus();
		fEnableUserDefinedPatterns.setText("Filter nodes based on their label");

		// Pattern field
		fUserDefinedPatterns = new Text(group, SWT.SINGLE | SWT.BORDER);
		GridData data = new GridData(GridData.HORIZONTAL_ALIGN_FILL
				| GridData.GRAB_HORIZONTAL);
		data.widthHint = convertWidthInCharsToPixels(59);
		fUserDefinedPatterns.setLayoutData(data);
		String patterns = "Some patterns";
		fUserDefinedPatterns.setText(patterns);

		// Info text
		final Label info = new Label(group, SWT.LEFT);
		info.setText("The patterns are separated by commas, where * = anystring, ? = anycharacter");

		// Enabling / disabling of pattern group
		fEnableUserDefinedPatterns.setSelection(false);
		fUserDefinedPatterns.setEnabled(false);
		fEnableUserDefinedPatterns.addSelectionListener(widgetSelectedAdapter(e -> {
			boolean state = fEnableUserDefinedPatterns.getSelection();
			fUserDefinedPatterns.setEnabled(state);
			if (state)
				fUserDefinedPatterns.setFocus();
		}));

		// Filters provided by extension point
		if (fBuiltInFilters.size() > 0)
			createCheckBoxList(group);

		applyDialogFont(parent);
		return parent;
	}

	private void createCheckBoxList(Composite parent) {
		// Filler
		new Label(parent, SWT.NONE);

		Label info = new Label(parent, SWT.LEFT);
		info.setText("Select the rules to be filtered");

		fCheckBoxList = CheckboxTableViewer.newCheckList(parent, SWT.BORDER);
		GridData data = new GridData(GridData.FILL_BOTH);
		data.heightHint = fCheckBoxList.getTable().getItemHeight() * 10;
		fCheckBoxList.getTable().setLayoutData(data);

		fCheckBoxList.setContentProvider(new ArrayContentProvider());
		fCheckBoxList.setInput(fBuiltInFilters);
		Object[] filters = proofTreeUI.getFilters();
		if (filters != null)
			setInitialSelections(filters);

		List<?> initialSelection = getInitialElementSelections();
		if (initialSelection != null && !initialSelection.isEmpty())
			checkInitialSelections();

		// Description
		info = new Label(parent, SWT.LEFT);
		info.setText("Description of the rule");

		final Text description = new Text(parent, SWT.LEFT | SWT.WRAP
				| SWT.MULTI | SWT.READ_ONLY | SWT.BORDER | SWT.V_SCROLL);
		data = new GridData(GridData.FILL_HORIZONTAL);
		data.heightHint = convertHeightInCharsToPixels(3);
		description.setLayoutData(data);
		fCheckBoxList.addSelectionChangedListener(event -> {
			ISelection selection = event.getSelection();
			if (selection instanceof IStructuredSelection && !selection.isEmpty()) {
				var filter = (RuleFilter) ((IStructuredSelection) selection).getFirstElement();
				Set<String> ids = filter.reasonerIDs;
				String text;
				if (ids.size() == 1) {
					text = "ID: " + ids.iterator().next();
				} else {
					text = "IDs:" + ids.stream().map(id -> "\n- " + id).collect(joining());
				}
				description.setText(text);
			}
		});

		addSelectionButtons(parent);
	}

	private void addSelectionButtons(Composite composite) {
		Composite buttonComposite = new Composite(composite, SWT.RIGHT);
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		buttonComposite.setLayout(layout);
		GridData data = new GridData(GridData.HORIZONTAL_ALIGN_END
				| GridData.GRAB_HORIZONTAL);
		data.grabExcessHorizontalSpace = true;
		composite.setData(data);

		// Select All button
		String label = "Select all";
		Button selectButton = createButton(buttonComposite,
				IDialogConstants.SELECT_ALL_ID, label, false);
		// SWTUtil.setButtonDimensionHint(selectButton);
		selectButton.addSelectionListener(widgetSelectedAdapter(e -> {
			fCheckBoxList.setAllChecked(true);
		}));

		// Deselect All button
		label = "Deselect all";
		Button deselectButton = createButton(buttonComposite,
				IDialogConstants.DESELECT_ALL_ID, label, false);
		// SWTUtil.setButtonDimensionHint(deselectButton);
		deselectButton.addSelectionListener(widgetSelectedAdapter(e -> {
			fCheckBoxList.setAllChecked(false);
		}));
	}

	private void checkInitialSelections() {
		var patternBuilder = new StringBuilder();
		String separator = "";
		for (Object itemToCheck : getInitialElementSelections()) {
			if (itemToCheck instanceof RuleFilter) {
				fCheckBoxList.setChecked(itemToCheck, true);
			} else if (itemToCheck instanceof RulePatternFilter) {
				patternBuilder.append(separator).append(((RulePatternFilter) itemToCheck).pattern);
				separator = SEPARATOR;
			}
		}
		String pattern = patternBuilder.toString();
		if (!pattern.isEmpty()) {
			fUserDefinedPatterns.setText(pattern);
			fUserDefinedPatterns.setEnabled(true);
			fEnableUserDefinedPatterns.setSelection(true);
		}
	}

	@Override
	protected void okPressed() {
		if (fBuiltInFilters != null) {
			ArrayList<ViewerFilter> result = new ArrayList<ViewerFilter>();
			for (ViewerFilter filter : fBuiltInFilters) {
				if (fCheckBoxList.getChecked(filter)) {
					result.add(filter);
				}
			}
			if (fUserDefinedPatterns.isEnabled()) {
				String rawPattern = fUserDefinedPatterns.getText();
				if (!rawPattern.isBlank()) {
					for (String pattern : rawPattern.split(SEPARATOR)) {
						result.add(new RulePatternFilter(pattern.strip()));
					}
				}
			}
			setResult(result);
		}
		super.okPressed();
	}

	/**
	 * A custom pattern filter for rules.
	 *
	 * Contrary to the default pattern filter, this one only matches the current
	 * element (not its children: this is done in ProofTreeUIPage) and the match is
	 * reversed: elements that match the pattern are hidden, not shown.
	 *
	 * The original pattern is also kept to restore the contents of the text field
	 * when re-opening the filters dialog.
	 */
	private static class RulePatternFilter extends PatternFilter {
		protected final String pattern;

		public RulePatternFilter(String pattern) {
			super();
			this.pattern = pattern;
			setPattern(pattern);
		}

		@Override
		public boolean isElementVisible(Viewer viewer, Object element) {
			return !isLeafMatch(viewer, element);
		}
	}

}
