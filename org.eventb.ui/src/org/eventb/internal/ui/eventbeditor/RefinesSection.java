/*******************************************************************************
 * Copyright (c) 2005, 2013 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - added history support
 *     Systerel - separation of file and root element
 *     Systerel - used IAttributeFactory
 *     Systerel - update combo list on focus gain
 *     Systerel - prevented from editing generated elements
 *******************************************************************************/
package org.eventb.internal.ui.eventbeditor;

import static org.eventb.internal.ui.EventBUtils.isReadOnly;

import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.SectionPart;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.eventb.core.EventBAttributes;
import org.eventb.core.IMachineRoot;
import org.eventb.core.IRefinesMachine;
import org.eventb.internal.ui.EventBUtils;
import org.eventb.internal.ui.UIUtils;
import org.eventb.internal.ui.eventbeditor.manipulation.RefinesMachineAbstractMachineNameAttributeManipulation;
import org.eventb.internal.ui.eventbeditor.operations.AtomicOperation;
import org.eventb.internal.ui.eventbeditor.operations.History;
import org.eventb.internal.ui.eventbeditor.operations.OperationFactory;
import org.eventb.ui.eventbeditor.IEventBEditor;
import org.eventb.ui.manipulation.IAttributeManipulation;
import org.rodinp.core.ElementChangedEvent;
import org.rodinp.core.IElementChangedListener;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinElementDelta;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;

/**
 * @author htson
 *         <p>
 *         An implementation of Section Part for displaying and editting Refines
 *         clause.
 */
public class RefinesSection extends SectionPart implements
		IElementChangedListener {

	// Title and description of the section.
	private static final String SECTION_TITLE = "Abstract Machine";

	private static final String SECTION_DESCRIPTION = "Select the abstraction of this machine";

	// The Event B Editor contains this section.
	IEventBEditor<IMachineRoot> editor;

	private final static String NULL_VALUE = "--- None ---";

	private final static IAttributeManipulation factory = new RefinesMachineAbstractMachineNameAttributeManipulation();
	
	// The combo box
	Combo machineCombo;

	// The refined internal element. Must be null if there is no refined Machine.
	IRefinesMachine refined;

	/**
	 * Constructor.
	 * <p>
	 * 
	 * @param editor
	 *            The Form editor contains this section
	 * @param toolkit
	 *            The FormToolkit used to create this section
	 * @param parent
	 *            The composite parent
	 */
	public RefinesSection(IEventBEditor<IMachineRoot> editor, FormToolkit toolkit,
			Composite parent) {
		super(parent, toolkit, ExpandableComposite.TITLE_BAR
				| Section.DESCRIPTION);
		this.editor = editor;
		createClient(getSection(), toolkit);
		RodinCore.addElementChangedListener(this);
	}

	@Override
	public void dispose() {
		RodinCore.removeElementChangedListener(this);
		super.dispose();
	}

	/**
	 * Creates the content of the section.
	 * <p>
	 * 
	 * @param section
	 *            the parent section
	 * @param toolkit
	 *            the FormToolkit used to create the content
	 */
	public void createClient(Section section, FormToolkit toolkit) {
		section.setText(SECTION_TITLE);
		section.setDescription(SECTION_DESCRIPTION);
		Composite comp = toolkit.createComposite(section);
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		layout.verticalSpacing = 5;
		comp.setLayout(layout);

		Label label = toolkit.createLabel(comp, "Abstract machine: ");
		label.setLayoutData(new GridData());

		// Create the combo box
		machineCombo = new Combo(comp, SWT.DROP_DOWN);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		machineCombo.setLayoutData(gd);
		machineCombo.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				setRefinedMachine();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				setRefinedMachine();
			}
		});

		machineCombo.addFocusListener(new FocusListener() {
			@Override
			public void focusGained(FocusEvent e) {
				updateCombo();
			}

			@Override
			public void focusLost(FocusEvent e) {
				setRefinedMachine();
			}
		});

		initCombo();
		setComboValue();

		toolkit.paintBordersFor(comp);
		section.setClient(comp);
	}

	/**
	 * Set the refines clause to the selected machine name of combo list.
	 */
	void setRefinedMachine() {
		final String machine = machineCombo.getText();
		if (machine.equals(NULL_VALUE)) {
			if (refined != null && refined.exists()) {
				AtomicOperation operation = OperationFactory
						.deleteElement(refined);
				History.getInstance().addOperation(operation);
				refined = null;
			}
		} else if (refined == null) { // Create new element
			AtomicOperation operation = OperationFactory.createElement(editor
					.getRodinInput(), IRefinesMachine.ELEMENT_TYPE,
					EventBAttributes.TARGET_ATTRIBUTE, machine);
			History.getInstance().addOperation(operation);
			refined = (IRefinesMachine) operation.getCreatedElement();
		} else { // Change the element
			UIUtils.setStringAttribute(refined, factory, machine, null);
		}
	}

	@Override
	public void elementChanged(final ElementChangedEvent event) {
		if (machineCombo.isDisposed())
			return;
		Display display = machineCombo.getDisplay();
		display.syncExec(new Runnable() {

			@Override
			public void run() {
				if (EventBEditorUtils.DEBUG)
					EventBEditorUtils.debug("Refine Section: Element change");
				IRodinElementDelta delta = event.getDelta();
				if (EventBEditorUtils.DEBUG)
					EventBEditorUtils.debug("Refines Section - Process Delta: "
							+ delta);
				processDelta(delta);
			}

		});
	}

	private IRefinesMachine getRefinesMachine() throws RodinDBException {
		final IMachineRoot root = editor.getRodinInput();
		final IRefinesMachine[] refinesClauses = root.getRefinesClauses();
		if (refinesClauses.length == 0) {
			return null;
		}
		return refinesClauses[0];
	}

	private void initCombo() {
		final IMachineRoot root = editor.getRodinInput();
		machineCombo.setEnabled(!isReadOnly(root));
		machineCombo.add(NULL_VALUE);
		try {
			final String childName = EventBUtils.getFreeChildName(root,
					IRefinesMachine.ELEMENT_TYPE, "i");
			final IRefinesMachine clause = root.getRefinesClause(childName);
			final String[] possibleValues = factory.getPossibleValues(clause,
					null);
			for (String value : possibleValues) {
				machineCombo.add(value);
			}
		} catch (RodinDBException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	private void setComboValue() {
		try {
			final String value;
			refined = getRefinesMachine();
			if (refined != null && factory.hasValue(refined, null)) {
				value = factory.getValue(refined, null);
			} else {
				value = NULL_VALUE;
			}
			setComboText(value);
		} catch (RodinDBException e) {
			// TODO Refesh?

			e.printStackTrace();
			InputDialog dialog = new InputDialog(null, "Resource out of sync",
					"Refresh? (Y/N)", "Y", null);
			dialog.open();
			dialog.getValue();
			EventBMachineEditorContributor.sampleAction.refreshAll();
		}
	}

	void updateCombo() {
		final IMachineRoot root = editor.getRodinInput();
		if (EventBEditorUtils.DEBUG)
			EventBEditorUtils.debug("Update Combo: " + root.getElementName());
		final String oldText = machineCombo.getText();
		machineCombo.removeAll();
		initCombo();
		setComboText(oldText);
	}

	// Sets the combo text, taking care to update the selected index if possible
	private void setComboText(String text) {
		final int index = machineCombo.indexOf(text);
		if (index >= 0) {
			machineCombo.select(index);
		} else {
			machineCombo.setText(text);
		}
	}

	/**
	 * Process the delta recursively to detect if the refines clause has
	 * changed.
	 * 
	 * @param delta
	 *            a Rodin Element Delta
	 */
	void processDelta(IRodinElementDelta delta) {
		final IRodinElement element = delta.getElement();
		final IInternalElement root = editor.getRodinInput();

		if (element.isAncestorOf(root)) {
			processChildDeltas(delta);
			return;
		}

		if (element instanceof IMachineRoot) {
			if (root.equals(element)) {
				processChildDeltas(delta);
			}
			return;
		}

		if (element instanceof IRefinesMachine) {
			setComboValue();
		}
	}

	private void processChildDeltas(IRodinElementDelta delta) {
		for (IRodinElementDelta childDelta: delta.getAffectedChildren()) {
			processDelta(childDelta);
		}
	}	
}