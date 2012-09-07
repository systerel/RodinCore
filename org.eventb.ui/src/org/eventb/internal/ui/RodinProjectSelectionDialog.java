/*******************************************************************************
 * Copyright (c) 2000, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   IBM Corporation - initial API and implementation 
 *   Sebastian Davids <sdavids@gmx.de> - Fix for bug 19346 - Dialog
 *     font should be activated and used by other components.
 *   ETH Zurich - adaptation to Event-B
 *   Systerel - remove unused code
 *******************************************************************************/
package org.eventb.internal.ui;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.resource.JFaceColors;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.ISelectionValidator;
import org.eclipse.ui.dialogs.SelectionDialog;
import org.rodinp.core.IRodinProject;

public class RodinProjectSelectionDialog extends SelectionDialog {

    // the widget group;
    RodinProjectSelectionGroup group;

    // the root resource to populate the viewer with
    private IRodinProject initialSelection;

    // allow the user to type in a new project name
    private boolean allowNewProjectName = true;

    // the validation message
    Label statusMessage;

    //for validating the selection
    ISelectionValidator validator;

    /**
     * Creates a rodin project selection dialog rooted at the given rodin database.
     * All selections are considered valid. 
     *
     * @param parentShell the parent shell
     * @param initialRoot the initial selection in the tree
     * @param allowNewProjectName <code>true</code> to enable the user to type in
     *  a new project name, and <code>false</code> to restrict the user to just
     *  selecting from existing ones
     * @param message the message to be displayed at the top of this dialog, or
     *    <code>null</code> to display a default message
     */
    public RodinProjectSelectionDialog(Shell parentShell, IRodinProject initialRoot,
            boolean allowNewProjectName, String title, String message) {
		super(parentShell);
        setTitle(title);
        this.initialSelection = initialRoot;
        this.allowNewProjectName = allowNewProjectName;
        if (message != null) {
			setMessage(message);
		} else {
			setMessage("Select a project");
		}
        setShellStyle(getShellStyle() | SWT.RESIZE);
	}

    /* (non-Javadoc)
     * Method declared on Dialog.
     */
    @Override
	protected Control createDialogArea(Composite parent) {
        // create composite 
        Composite area = (Composite) super.createDialogArea(parent);

        Listener listener = new Listener() {
            @Override
			public void handleEvent(Event event) {
                if (statusMessage != null && validator != null) {
                    String errorMsg = validator.isValid(group
                            .getProject());
                    if (errorMsg == null || errorMsg.equals("")) { //$NON-NLS-1$
                        statusMessage.setText(""); //$NON-NLS-1$
                        getOkButton().setEnabled(true);
                    } else {
                        statusMessage.setForeground(JFaceColors
                                .getErrorText(statusMessage.getDisplay()));
                        statusMessage.setText(errorMsg);
                        getOkButton().setEnabled(false);
                    }
                }
            }
        };

        // project selection group
        group = new RodinProjectSelectionGroup(area, listener,
                allowNewProjectName, getMessage());
        if (initialSelection != null) {
            group.setSelectedProject(initialSelection);
        }

        statusMessage = new Label(parent, SWT.NONE);
        statusMessage.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        statusMessage.setFont(parent.getFont());

        return dialogArea;
    }
    

    /**
     * The <code>RodinProjectSelectionDialog</code> implementation of this 
     * <code>Dialog</code> method builds a list of the selected RODIN projects
     * for later retrieval by the client and closes this dialog.
     */
    @Override
	protected void okPressed() {
        List<IRodinProject> projectNames = new ArrayList<IRodinProject>();
        IRodinProject returnValue = group.getProject();
        if (returnValue != null) {
			projectNames.add(returnValue);
		}
        setResult(projectNames);
        super.okPressed();
    }

    /**
     * Sets the validator to use.  
     * 
     * @param validator A selection validator
     */
    public void setValidator(ISelectionValidator validator) {
        this.validator = validator;
    }

}
