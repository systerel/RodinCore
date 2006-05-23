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

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.SectionPart;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.eventb.core.EventBPlugin;
import org.eventb.core.IContext;
import org.eventb.core.ISees;
import org.eventb.internal.ui.UIUtils;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IParent;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.RodinDBException;

/**
 * @author htson
 *         <p>
 *         An implementation of Section Part for displaying and editting Sees
 *         clause.
 */
public class SeeSection extends SectionPart {

	// Title and description of the section.
	private static final String SECTION_TITLE = "Required Contexts";

	private static final String SECTION_DESCRIPTION = "Select the context that the model sees";

	// The Form editor contains this section.
	private FormEditor editor;

	// Buttons.
	private Button nullButton;

	private Button chooseButton;

	private Button browseButton;

	// Text area.
	private Text contextText;

	// The seen internal element.
	private IInternalElement seen;

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
	public SeeSection(FormEditor editor, FormToolkit toolkit, Composite parent) {
		super(parent, toolkit, ExpandableComposite.TITLE_BAR
				| Section.DESCRIPTION);
		this.editor = editor;
		createClient(getSection(), toolkit);
	}

	/**
	 * Creat the content of the section.
	 * <p>
	 * @param section the parent section
	 * @param toolkit the FormToolkit used to create the content
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
					UIUtils.debug("Null selected");
					contextText.setEnabled(false);
					browseButton.setEnabled(false);
					try {
						if (seen != null) {
							seen.delete(true, null);
							seen = null;
							markDirty();
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
					UIUtils.debug("Choose selected");
					contextText.setEnabled(true);
					browseButton.setEnabled(true);
					contextText.setFocus();
				}
			}
		});

		contextText = new Text(comp, SWT.BORDER | SWT.SINGLE);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		contextText.setLayoutData(gd);
		contextText.addFocusListener(new FocusListener() {
			public void focusGained(FocusEvent e) {
				// Do nothing
			}

			public void focusLost(FocusEvent e) {
				// Create or change the element
				if (chooseButton.getSelection()) {
					if (contextText.getText().equals(""))
						return;
					setSeenContext(contextText.getText());
				}
			}
		});

		browseButton = new Button(comp, SWT.PUSH);
		browseButton.setText("Browse...");
		browseButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				handleBrowse();
			}
		});

		IRodinFile rodinFile = ((EventBEditor) editor).getRodinInput();
		try {
			IRodinElement[] seenContexts = rodinFile
					.getChildrenOfType(ISees.ELEMENT_TYPE);
			if (seenContexts.length != 0) {
				seen = (IInternalElement) seenContexts[0];
				try {
					contextText.setText(seen.getContents());
				} catch (RodinDBException e) {
					e.printStackTrace();
				}
				chooseButton.setSelection(true);
			} else {
				nullButton.setSelection(true);
				contextText.setEnabled(false);
				browseButton.setEnabled(false);
				seen = null;
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

		toolkit.paintBordersFor(comp);
		section.setClient(comp);
		gd = new GridData(GridData.FILL_BOTH);
		gd.minimumWidth = 250;
		section.setLayoutData(gd);
	}

	/**
	 * Set the seen context with the given name.
	 * <p>
	 * 
	 * @param context
	 *            name of the context
	 */
	private void setSeenContext(String context) {
		if (seen == null) { // Create new element
			try {
				UIUtils.debug("Creat new sees clause");
				IRodinFile rodinFile = ((EventBEditor) editor).getRodinInput();
				seen = rodinFile.createInternalElement(ISees.ELEMENT_TYPE,
						null, null, null);
				seen.setContents(context);
				markDirty();
			} catch (RodinDBException exception) {
				exception.printStackTrace();
				seen = null;
			}
		} else { // Change the element
			try {
				UIUtils.debug("Change sees clause");
				// if (!(seen.getContents().equals(contextText.getText()))) {
				seen.setContents(context);
				markDirty();
				// }
			} catch (RodinDBException exception) {
				exception.printStackTrace();
				seen = null;
			}
		}

	}

	/**
	 * Handle the browse action when the corresponding button is clicked.
	 */
	public void handleBrowse() {
		ContextChoosingDialog dialog = new ContextChoosingDialog(null,
				"Context Name", "Please choose the seen context");
		dialog.open();
		String name = dialog.getContext();
		if (name != null) {
			setSeenContext(name);
			contextText.setText(name);
			contextText.setFocus();
		}
		dialog.close();

		return;
	}

	/**
	 * @author htson An extension of Dialog for choosing seen context.
	 */
	private class ContextChoosingDialog extends Dialog {
		private String message;

		private String context;

		private String title;

		/**
		 * Ok button widget.
		 */
		private Button okButton;

		public ContextChoosingDialog(Shell parentShell, String dialogTitle,
				String message) {
			super(parentShell);
			this.title = dialogTitle;
			this.message = message;
			context = null;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.jface.dialogs.Dialog#createButtonsForButtonBar(org.eclipse.swt.widgets.Composite)
		 */
		protected void createButtonsForButtonBar(Composite parent) {
			// create OK and Cancel buttons by default
			okButton = createButton(parent, IDialogConstants.OK_ID,
					IDialogConstants.OK_LABEL, true);
			if (seen != null) {
				// TODO Should be enable only if the seen is valid?
				okButton.setEnabled(true);
			} else
				okButton.setEnabled(false);
			createButton(parent, IDialogConstants.CANCEL_ID,
					IDialogConstants.CANCEL_LABEL, false);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.jface.window.Window#configureShell(org.eclipse.swt.widgets.Shell)
		 */
		protected void configureShell(Shell shell) {
			super.configureShell(shell);
			if (title != null)
				shell.setText(title);
		}

		/*
		 * (non-Javadoc) Method declared on Dialog.
		 */
		protected Control createDialogArea(Composite parent) {
			// create composite
			Composite composite = (Composite) super.createDialogArea(parent);
			// create message
			if (message != null) {
				Label label = new Label(composite, SWT.WRAP);
				label.setText(message);
				GridData data = new GridData(GridData.GRAB_HORIZONTAL
						| GridData.GRAB_VERTICAL
						| GridData.HORIZONTAL_ALIGN_FILL
						| GridData.VERTICAL_ALIGN_CENTER);
				data.widthHint = convertHorizontalDLUsToPixels(IDialogConstants.MINIMUM_MESSAGE_AREA_WIDTH);
				label.setLayoutData(data);
				label.setFont(parent.getFont());
			}

			IRodinFile rodinFile = ((EventBEditor) editor).getRodinInput();
			try {
				IRodinElement[] contexts = ((IParent) rodinFile.getParent())
						.getChildrenOfType(IContext.ELEMENT_TYPE);
				for (int i = 0; i < contexts.length; i++) {
					Button button = new Button(composite, SWT.RADIO);
					button.setText(EventBPlugin.getComponentName(contexts[i]
							.getElementName()));
					button.addSelectionListener(new ButtonSelectionListener(
							button.getText()));
					if (seen != null) {
						try {
							if (seen.getContents().equals(button.getText())) {
								button.setSelection(true);
							}
						} catch (RodinDBException e) {
							// TODO Handle Exception
							e.printStackTrace();
						}
					}
				}
			} catch (RodinDBException e) {
				e.printStackTrace();
			}

			applyDialogFont(composite);
			return composite;
		}

		private class ButtonSelectionListener implements SelectionListener {
			String name;

			public ButtonSelectionListener(String name) {
				this.name = name;
			}

			public void widgetDefaultSelected(SelectionEvent e) {
				// Do nothing
			}

			public void widgetSelected(SelectionEvent e) {
				okButton.setEnabled(true);
				context = name;
			}
		}

		protected void buttonPressed(int buttonId) {
			if (buttonId == IDialogConstants.CANCEL_ID) {
				context = null;
			}
			super.buttonPressed(buttonId);
		}

		public String getContext() {
			return context;
		}
	}

}