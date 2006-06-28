/*******************************************************************************
 * Copyright (c) 2005-2006 ETH Zurich.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Rodin @ ETH Zurich
 ******************************************************************************/

package org.eventb.internal.ui.eventbeditor;

import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.forms.SectionPart;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.eventb.core.EventBPlugin;
import org.eventb.core.IMachineFile;
import org.eventb.core.IRefinesMachine;
import org.eventb.internal.ui.UIUtils;
import org.rodinp.core.ElementChangedEvent;
import org.rodinp.core.IElementChangedListener;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IParent;
import org.rodinp.core.IRodinDB;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinElementDelta;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;
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
	private static final String SECTION_TITLE = "Refined Machine";

	private static final String SECTION_DESCRIPTION = "Select the machine that this machine refines";

	// The Form editor contains this section.
	private FormEditor editor;

	// Buttons.
	private Button nullButton;

	private Button chooseButton;

	private Button button;

	private Combo machineCombo;

	// The refined internal element.
	private IInternalElement refined;

	private boolean refresh;

	/**
	 * Constructor.
	 * <p>
	 * 
	 * @param editor
	 *            The Form editor contains this section
	 * @param page
	 *            The Dependencies page contains this section
	 * @param parent
	 *            The composite parent
	 */
	public RefinesSection(FormEditor editor, FormToolkit toolkit,
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
	 * Creat the content of the section.
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
		layout.numColumns = 3;
		layout.verticalSpacing = 5;
		comp.setLayout(layout);

		nullButton = toolkit.createButton(comp, "None", SWT.RADIO);
		GridData gd = new GridData();
		gd.horizontalSpan = 3;
		nullButton.setLayoutData(gd);
		nullButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if (nullButton.getSelection()) {
					machineCombo.setEnabled(false);
					button.setEnabled(false);
					try {
						if (refined != null) {
							refined.delete(true, null);
							refined = null;
						}
					} catch (RodinDBException exception) {
						exception.printStackTrace();
					}
				}
			}
		});

		chooseButton = toolkit.createButton(comp, "Choose", SWT.RADIO);
		chooseButton.setLayoutData(new GridData());
		chooseButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if (chooseButton.getSelection()) {
					// UIUtils.debug("Choose selected");
					machineCombo.setEnabled(true);
					// contextText.setEnabled(true);
					button.setEnabled(!machineCombo.getText().equals(""));
					machineCombo.setFocus();
					// contextText.setFocus();
				}
			}
		});

		machineCombo = new Combo(comp, SWT.BORDER);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		machineCombo.setLayoutData(gd);

		machineCombo.addSelectionListener(new SelectionListener() {

			public void widgetSelected(SelectionEvent e) {
				int index = machineCombo.getSelectionIndex();
				if (index != -1) {
					setRefinedMachine(machineCombo.getItems()[index]);
				}
			}

			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}

		});

		machineCombo.addModifyListener(new ModifyListener() {

			public void modifyText(ModifyEvent e) {
				button.setEnabled(!machineCombo.getText().equals(""));
			}

		});

		button = new Button(comp, SWT.PUSH);
		button.setText("Open/Create");
		button.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				handleOpenOrCreate();
			}
		});

		initCombo();
		setComboValue();

		toolkit.paintBordersFor(comp);
		section.setClient(comp);
		gd = new GridData(GridData.FILL_BOTH);
		gd.minimumWidth = 250;
		section.setLayoutData(gd);
	}

	/**
	 * Set the refined context with the given name.
	 * <p>
	 * 
	 * @param machine
	 *            name of the context
	 */
	private void setRefinedMachine(String machine) {
		if (refined == null) { // Create new element
			try {
				IRodinFile rodinFile = ((EventBEditor) editor).getRodinInput();
				refined = rodinFile.createInternalElement(
						IRefinesMachine.ELEMENT_TYPE, machine, null, null);
				refined.setContents(machine);
			} catch (RodinDBException exception) {
				exception.printStackTrace();
				refined = null;
			}
		} else { // Change the element
			try {
				if (!(refined.getContents().equals(machine))) {
					refined.setContents(machine);
				}
			} catch (RodinDBException exception) {
				exception.printStackTrace();
			}
		}

	}

	/**
	 * Handle the open/create action when the corresponding button is clicked.
	 */
	public void handleOpenOrCreate() {
		String machine = machineCombo.getText();
		setRefinedMachine(machine);

		IRodinFile rodinFile = ((EventBEditor) editor).getRodinInput();

		IRodinProject project = (IRodinProject) rodinFile.getParent();
		String machineFileName = EventBPlugin.getMachineFileName(machine);
		IRodinFile machineFile = project.getRodinFile(machineFileName);
		if (!machineFile.exists()) {
			try {
				boolean answer = MessageDialog
						.openQuestion(
								this.getSection().getShell(),
								"Create Machine",
								"Machine "
										+ machineFileName
										+ " does not exist. Do you want to create new refined machine?");
				if (!answer)
					return;
				machineFile = project.createRodinFile(machineFileName, true,
						null);
			} catch (RodinDBException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		UIUtils.linkToEventBEditor(machineFile);

		return;
	}

	public void elementChanged(ElementChangedEvent event) {
		UIUtils.debugEventBEditor("Refine Section: Element change");
		IRodinElementDelta delta = event.getDelta();
		UIUtils.debugEventBEditor("Refines Section - Process Delta: " + delta);
		processDelta(delta);
		Display display = this.getManagedForm().getForm().getDisplay();
		display.syncExec(new Runnable() {

			public void run() {
				updateCombo();
			}

		});
	}

	private void initCombo() {
		IRodinFile rodinFile = ((EventBEditor) editor).getRodinInput();

		try {
			IRodinElement[] machines = ((IParent) rodinFile.getParent())
					.getChildrenOfType(IMachineFile.ELEMENT_TYPE);

			for (IRodinElement machine : machines) {
				if (!rodinFile.equals(machine)) {
					UIUtils.debugEventBEditor("Add to Combo: "
							+ machine.getElementName());
					machineCombo.add(EventBPlugin.getComponentName(machine
							.getElementName()));
				}
			}
		} catch (RodinDBException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

	}

	private void setComboValue() {
		IRodinFile rodinFile = ((EventBEditor) editor).getRodinInput();
		try {
			IRodinElement[] refinedMachines = rodinFile
					.getChildrenOfType(IRefinesMachine.ELEMENT_TYPE);
			if (refinedMachines.length != 0) {
				refined = (IInternalElement) refinedMachines[0];
				try {
					machineCombo.setText(refined.getContents());
					// contextText.setText(refined.getContents());
				} catch (RodinDBException e) {
					e.printStackTrace();
				}
				chooseButton.setSelection(true);
			} else {
				nullButton.setSelection(true);
				chooseButton.setSelection(false);
				machineCombo.setEnabled(false);
				// contextText.setEnabled(false);
				button.setEnabled(false);
				refined = null;
			}
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

	private void updateCombo() {
		IRodinFile rodinFile = ((EventBEditor) editor).getRodinInput();

		UIUtils.debugEventBEditor("Update Combo: " + rodinFile.getElementName()
				+ " --- " + refresh);
		if (refresh) {
			String oldText = machineCombo.getText();
			machineCombo.removeAll();
			initCombo();
			machineCombo.setText(oldText);
			refresh = false;
		}
	}

	/**
	 * Process the delta recursively until finding the creation/deletion of the
	 * a machine the same project.
	 * <p>
	 * 
	 * @param delta
	 *            a Rodin Element Delta
	 */
	private void processDelta(IRodinElementDelta delta) {
		IRodinElement element = delta.getElement();

		if (element instanceof IRodinDB) {
			IRodinElementDelta[] deltas = delta.getAffectedChildren();
			for (int i = 0; i < deltas.length; i++) {
				processDelta(deltas[i]);
			}
			return;
		}
		if (element instanceof IRodinProject) {
			IRodinProject prj = (IRodinProject) element;
			IRodinFile rodinFile = ((EventBEditor) editor).getRodinInput();

			if (!rodinFile.getParent().equals(prj)) {
				return;
			}
			IRodinElementDelta[] deltas = delta.getAffectedChildren();
			for (int i = 0; i < deltas.length; i++) {
				processDelta(deltas[i]);
			}
			return;
		}

		if (element instanceof IRodinFile) {
			UIUtils.debugEventBEditor("Refines Section: file" + element);
			if (!(element instanceof IMachineFile)) {
				return;
			}
			IRodinFile rodinFile = ((EventBEditor) editor).getRodinInput();
			if (rodinFile.equals(element)) {
				IRodinElementDelta[] deltas = delta.getAffectedChildren();
				for (int i = 0; i < deltas.length; i++) {
					processDelta(deltas[i]);
				}
			} else {
				if ((delta.getKind() & IRodinElementDelta.ADDED) != 0
						|| (delta.getKind() & IRodinElementDelta.REMOVED) != 0) {
					refresh = true;
				}
			}
			return;
		}

		if (element instanceof IRefinesMachine) {
			int kind = delta.getKind();
			if ((kind & IRodinElementDelta.REMOVED) != 0) {
				if ((delta.getFlags() & IRodinElementDelta.F_MOVED_TO) == 0) {
					nullButton.setSelection(true);
					chooseButton.setSelection(false);
				}
			} else if ((kind & IRodinElementDelta.ADDED) != 0) {
				chooseButton.setSelection(true);
				nullButton.setSelection(false);
				try {
					machineCombo.setText(((IRefinesMachine) element)
							.getContents());
				} catch (RodinDBException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else if ((kind & IRodinElementDelta.CHANGED) != 0) {
				if ((delta.getFlags() & IRodinElementDelta.F_CONTENT) != 0) {
					String machine;
					try {
						machine = ((IRefinesMachine) element).getContents();
						if (!machineCombo.getText().equals(machine)) {
							machineCombo.setText(machine);
						}
					} catch (RodinDBException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
	}

}