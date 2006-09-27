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

import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
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
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.eventb.core.EventBPlugin;
import org.eventb.core.IMachineFile;
import org.eventb.core.IRefinesMachine;
import org.eventb.internal.ui.UIUtils;
import org.eventb.internal.ui.eventbeditor.actions.PrefixRefinesMachineName;
import org.rodinp.core.ElementChangedEvent;
import org.rodinp.core.IElementChangedListener;
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
	private static final String SECTION_TITLE = "Abstract Machine";

	private static final String SECTION_DESCRIPTION = "Select the abstraction of this machine";

	// The Form editor contains this section.
	private FormEditor editor;

	private final static String NULL_VALUE = "--- None ---";

	// Buttons.
	// private Button nullButton;
	//
	// private Button chooseButton;

	// private Button openOrCreateButton;

	// The combo box
	private Combo machineCombo;

	// The refined internal element.
	private IRefinesMachine refined;

	// Flag to indicate if the combo box need to be updated.
	private boolean refreshCombo;

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
	public RefinesSection(FormEditor editor, FormToolkit toolkit,
			Composite parent) {
		super(parent, toolkit, ExpandableComposite.TITLE_BAR
				| Section.DESCRIPTION);
		this.editor = editor;
		createClient(getSection(), toolkit);
		RodinCore.addElementChangedListener(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.forms.IFormPart#dispose()
	 */
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
		layout.numColumns = 2;
		layout.verticalSpacing = 5;
		comp.setLayout(layout);

		// Create the "Null" button.
		// nullButton = toolkit.createButton(comp, "None", SWT.RADIO);
		// GridData gd = new GridData();
		// gd.horizontalSpan = 3;
		// nullButton.setLayoutData(gd);
		// nullButton.addSelectionListener(new SelectionAdapter() {
		// public void widgetSelected(SelectionEvent e) {
		// if (nullButton.getSelection()) {
		// machineCombo.setEnabled(false);
		// openOrCreateButton.setEnabled(false);
		// try {
		// if (refined != null) {
		// refined.delete(true, null);
		// refined = null;
		// }
		// } catch (RodinDBException exception) {
		// exception.printStackTrace();
		// }
		// }
		// }
		// });

		// Create the "Choose" button
		// chooseButton = toolkit.createButton(comp, "Choose", SWT.RADIO);
		// chooseButton.setLayoutData(new GridData());
		// chooseButton.addSelectionListener(new SelectionAdapter() {
		// public void widgetSelected(SelectionEvent e) {
		// if (chooseButton.getSelection()) {
		// // UIUtils.debug("Choose selected");
		// final IRodinFile rodinFile = ((EventBEditor) editor)
		// .getRodinInput();
		// IRodinElement[] refinedMachines;
		// try {
		// refinedMachines = rodinFile
		// .getChildrenOfType(IRefinesMachine.ELEMENT_TYPE);
		// if (refinedMachines.length != 0) {
		// refined = (IInternalElement) refinedMachines[0];
		// machineCombo.setText(refined.getContents());
		// } else {
		// machineCombo.setText("");
		// }
		// } catch (RodinDBException e1) {
		// // TODO Auto-generated catch block
		// e1.printStackTrace();
		// }
		// machineCombo.setEnabled(true);
		// openOrCreateButton.setEnabled(!machineCombo.getText()
		// .equals(""));
		// machineCombo.setFocus();
		// }
		// }
		// });

		Label label = toolkit.createLabel(comp, "Abstract machine: ");
		label.setLayoutData(new GridData());

		// Create the combo box
		machineCombo = new Combo(comp, SWT.DROP_DOWN);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		machineCombo.setLayoutData(gd);
		machineCombo.addSelectionListener(new SelectionListener() {

			/*
			 * (non-Javadoc)
			 * 
			 * @see org.eclipse.swt.events.SelectionListener#widgetSelected(org.eclipse.swt.events.SelectionEvent)
			 */
			public void widgetSelected(SelectionEvent e) {
				setRefinedMachine(machineCombo.getText());
			}

			/*
			 * (non-Javadoc)
			 * 
			 * @see org.eclipse.swt.events.SelectionListener#widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent)
			 */
			public void widgetDefaultSelected(SelectionEvent e) {
				setRefinedMachine(machineCombo.getText());
			}

		});

		// machineCombo.addListener(SWT.Traverse, new Listener() {
		//
		// public void handleEvent(Event event) {
		// switch (event.type) {
		// case SWT.Traverse:
		// switch (event.detail) {
		// case SWT.TRAVERSE_ESCAPE:
		// machineCombo.setText(NULL_VALUE);
		// break;
		// }
		// }
		// }
		//			
		// });
		machineCombo.addFocusListener(new FocusListener() {

			public void focusGained(FocusEvent e) {
				// TODO Auto-generated method stub

			}

			public void focusLost(FocusEvent e) {
				setRefinedMachine(machineCombo.getText());
			}

		});

		// machineCombo.addVerifyListener(new VerifyListener() {
		//
		// public void verifyText(VerifyEvent e) {
		// int index = machineCombo.getSelectionIndex();
		// if (index != -1) {
		// setRefinedMachine(machineCombo.getItems()[index]);
		// }
		// }
		//			
		// });

		// machineCombo.addModifyListener(new ModifyListener() {
		//
		// /*
		// * (non-Javadoc)
		// *
		// * @see
		// org.eclipse.swt.events.ModifyListener#modifyText(org.eclipse.swt.events.ModifyEvent)
		// */
		// public void modifyText(ModifyEvent e) {
		// openOrCreateButton.setEnabled(!machineCombo.getText()
		// .equals(""));
		// }
		//
		// });

		// Create the "Open/Create" button.
		// openOrCreateButton = new Button(comp, SWT.PUSH);
		// openOrCreateButton.setText("Open/Create");
		// openOrCreateButton.addSelectionListener(new SelectionAdapter() {

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.swt.events.SelectionListener#widgetSelected(org.eclipse.swt.events.SelectionEvent)
		 */
		// public void widgetSelected(SelectionEvent e) {
		// handleOpenOrCreate();
		// }
		//
		// });
		// Initialise the value of the combo box
		initCombo();
		setComboValue();

		toolkit.paintBordersFor(comp);
		section.setClient(comp);
	}

	/**
	 * Set the refines clause to the given machine name.
	 * <p>
	 * 
	 * @param machine
	 *            name of the machine
	 */
	private void setRefinedMachine(final String machine) {
		if (machine.equals(NULL_VALUE)) {
			try {
				if (refined != null) {
					refined.delete(true, null);
					refined = null;
				}
			} catch (RodinDBException exception) {
				exception.printStackTrace();
			}
		} else {
			if (refined == null) { // Create new element
				try {
					final IRodinFile rodinFile = ((EventBEditor) editor)
							.getRodinInput();
					RodinCore.run(new IWorkspaceRunnable() { // Batch the
								// creation
								public void run(IProgressMonitor monitor)
										throws CoreException {
									refined = (IRefinesMachine) rodinFile
											.createInternalElement(
													IRefinesMachine.ELEMENT_TYPE,
													UIUtils
															.getFreeElementName(
																	(EventBEditor) editor,
																	rodinFile,
																	IRefinesMachine.ELEMENT_TYPE,
																	PrefixRefinesMachineName.QUALIFIED_NAME,
																	PrefixRefinesMachineName.DEFAULT_PREFIX),
													null, monitor);

									refined.setAbstractMachineName(machine);
								}
							}, null);
				} catch (RodinDBException exception) {
					exception.printStackTrace();
					refined = null;
				} catch (CoreException e) {
					e.printStackTrace();
					refined = null;
				}
			} else { // Change the element
				try {
					if (!(refined.getAbstractMachineName().equals(machine))) {
						refined.setAbstractMachineName(machine);
					}
				} catch (RodinDBException exception) {
					exception.printStackTrace();
				}
			}
		}
	}

	/**
	 * Handle the open/create action when the corresponding openOrCreateButton
	 * is clicked.
	 */
	// private void handleOpenOrCreate() {
	// String machine = machineCombo.getText();
	// setRefinedMachine(machine);
	//
	// IRodinFile rodinFile = ((EventBEditor) editor).getRodinInput();
	//
	// IRodinProject project = (IRodinProject) rodinFile.getParent();
	// String machineFileName = EventBPlugin.getMachineFileName(machine);
	// IRodinFile machineFile = project.getRodinFile(machineFileName);
	// if (!machineFile.exists()) {
	// boolean answer = MessageDialog
	// .openQuestion(
	// this.getSection().getShell(),
	// "Create Machine",
	// "Machine "
	// + machineFileName
	// + " does not exist. Do you want to create new refined machine?");
	//
	// if (!answer)
	// return;
	//			
	// try {
	// machineFile = project.createRodinFile(machineFileName, true,
	// null);
	// } catch (RodinDBException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// }
	// UIUtils.linkToEventBEditor(machineFile);
	//
	// return;
	// }
	public void elementChanged(final ElementChangedEvent event) {
		if (machineCombo.isDisposed())
			return;
		Display display = machineCombo.getDisplay();
		display.syncExec(new Runnable() {

			public void run() {
				if (EventBEditor.DEBUG)
					EventBEditorUtils.debug("Refine Section: Element change");
				IRodinElementDelta delta = event.getDelta();
				if (EventBEditor.DEBUG)
					EventBEditorUtils.debug("Refines Section - Process Delta: "
							+ delta);
				processDelta(delta);
				updateCombo();
			}

		});
	}

	private void initCombo() {
		IRodinFile rodinFile = ((EventBEditor) editor).getRodinInput();
		machineCombo.add(NULL_VALUE);
		try {
			IRodinElement[] machines = ((IParent) rodinFile.getParent())
					.getChildrenOfType(IMachineFile.ELEMENT_TYPE);

			for (IRodinElement machine : machines) {
				if (!rodinFile.equals(machine)) {
					if (EventBEditor.DEBUG)
						EventBEditorUtils.debug("Add to Combo: "
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
				refined = (IRefinesMachine) refinedMachines[0];
				try {
					machineCombo.setText(refined.getAbstractMachineName());
					// contextText.setText(refined.getContents());
				} catch (RodinDBException e) {
					e.printStackTrace();
				}
				// chooseButton.setSelection(true);
			} else {
				// nullButton.setSelection(true);
				// chooseButton.setSelection(false);
				machineCombo.setText(NULL_VALUE);
				// contextText.setEnabled(false);
				// openOrCreateButton.setEnabled(false);
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
		if (EventBEditor.DEBUG)
			EventBEditorUtils.debug("Update Combo: "
					+ rodinFile.getElementName() + " --- " + refreshCombo);
		if (refreshCombo) {
			String oldText = machineCombo.getText();
			machineCombo.removeAll();
			initCombo();
			machineCombo.setText(oldText);
			refreshCombo = false;
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
			if (EventBEditor.DEBUG)
				EventBEditorUtils.debug("Refines Section: file" + element);
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
					refreshCombo = true;
				}
			}
			return;
		}

		if (element instanceof IRefinesMachine) {
			int kind = delta.getKind();
			if ((kind & IRodinElementDelta.REMOVED) != 0) {
				if ((delta.getFlags() & IRodinElementDelta.F_MOVED_TO) == 0) {
					// nullButton.setSelection(true);
					// chooseButton.setSelection(false);
				}
			} else if ((kind & IRodinElementDelta.ADDED) != 0) {
				// chooseButton.setSelection(true);
				// nullButton.setSelection(false);
				try {
					machineCombo.setText(((IRefinesMachine) element)
							.getAbstractMachineName());
				} catch (RodinDBException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else if ((kind & IRodinElementDelta.CHANGED) != 0) {
				if ((delta.getFlags() & IRodinElementDelta.F_CONTENT) != 0) {
					String machine;
					try {
						machine = ((IRefinesMachine) element)
								.getAbstractMachineName();
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