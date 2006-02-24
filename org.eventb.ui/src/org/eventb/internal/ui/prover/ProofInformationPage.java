/*******************************************************************************
 * Copyright (c) 2005 ETH-Zurich
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH RODIN Group
 *******************************************************************************/

package org.eventb.internal.ui.prover;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.forms.widgets.FormText;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.part.Page;
import org.eventb.core.IEvent;
import org.eventb.core.IPRFile;
import org.eventb.core.IPRSequent;
import org.eventb.core.pm.IPOChangeEvent;
import org.eventb.core.pm.IPOChangedListener;
import org.eventb.internal.ui.EventBUIPlugin;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinFile;

/**
 * This sample class demonstrates how to plug-in a new
 * workbench view. The view shows data obtained from the
 * model. The sample creates a dummy model on the fly,
 * but a real implementation would connect to the model
 * available either in this or another plug-in (e.g. the workspace).
 * The view is connected to the model using a content provider.
 * <p>
 * The view uses a label provider to define how model
 * objects should be presented in the view. Each
 * view can present the same model objects using
 * different labels and icons, if needed. Alternatively,
 * a single label provider can be shared between views
 * in order to ensure that objects of the same type are
 * presented in the same way everywhere.
 * <p>
 */

public class ProofInformationPage 
	extends Page 
	implements	IProofInformationPage,
				IPOChangedListener
{
	private ScrolledForm scrolledForm;
	private ProverUI editor;
	private FormText formText;
	
	/*
	 * The content provider class is responsible for
	 * providing objects to the view. It can wrap
	 * existing objects in adapters or simply return
	 * objects as-is. These objects may be sensitive
	 * to the current input of the view, or ignore
	 * it and always show the same content 
	 * (like Task List, for example).
	 */
	 	
	/**
	 * The constructor.
	 */
	public ProofInformationPage(ProverUI editor) {
		this.editor = editor;
		editor.getUserSupport().addPOChangedListener(this);
	}
    
	@Override
	public void dispose() {
		// Deregister with the user support.
		editor.getUserSupport().removePOChangedListener(this);
		super.dispose();
	}
	
	/**
	 * This is a callback that will allow us
	 * to create the viewer and initialize it.
	 */
	public void createControl(Composite parent) {
		FormToolkit toolkit = new FormToolkit(parent.getDisplay());
		
		scrolledForm = toolkit.createScrolledForm(parent);
		scrolledForm.setText(editor.getUserSupport().getCurrentPO().getPRSequent().getName());

		Composite body = scrolledForm.getBody();
		body.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		GridLayout gl = new GridLayout();
		body.setLayout(gl);

		formText = toolkit.createFormText(body, true);
		setFormText(editor.getUserSupport().getCurrentPO().getPRSequent());
		
		toolkit.paintBordersFor(body);
		scrolledForm.reflow(true);
	}

	private void setFormText(IPRSequent prSequent) {
		IPRFile prFile = (IPRFile) prSequent.getOpenable();
		IRodinFile rodinFile = null;
		if (prFile.getMachine().exists()) rodinFile = prFile.getMachine();
		else if (prFile.getContext().exists()) rodinFile = prFile.getContext();
		System.out.println("File " + rodinFile.getElementName());
		
		String formString = "<form>";

		String prName = prSequent.getName();
		System.out.println("PR Name " + prName);
		int beginIndex = prName.indexOf("/");
		
		if (beginIndex != -1) {
			String name = prName.substring(0, beginIndex);
			System.out.println("Element name " + name);
			IRodinElement element = rodinFile.getInternalElement(IEvent.ELEMENT_TYPE, name);
			System.out.println("Element found " + element.toString());
			if (element != null) {
				
			}
		}
			
		formString = formString + "</form>";
		formText.setText(formString, true, false);
		scrolledForm.reflow(true);
	}
	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus() {
		scrolledForm.setFocus();
	}
	
	
    /* (non-Javadoc)
	 * @see org.eclipse.ui.part.Page#getControl()
	 */
	@Override
	public Control getControl() {
        if (scrolledForm == null)
            return null;
        return scrolledForm;
    }

	/* (non-Javadoc)
	 * @see org.eventb.core.pm.IPOChangedListener#poChanged(org.eventb.core.pm.IPOChangeEvent)
	 */
	public void poChanged(IPOChangeEvent e) {
		final IPRSequent prSequent = e.getDelta().getProofState().getPRSequent();
		Display display = EventBUIPlugin.getDefault().getWorkbench().getDisplay();
		display.syncExec (new Runnable () {
			public void run () {
				scrolledForm.setText(prSequent.getName());
				scrolledForm.reflow(true);
				setFormText(prSequent);
			}
		});
	}
	
}