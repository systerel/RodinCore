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
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.part.Page;
import org.eventb.core.IAction;
import org.eventb.core.IAxiom;
import org.eventb.core.IEvent;
import org.eventb.core.IGuard;
import org.eventb.core.IInvariant;
import org.eventb.core.IPODescription;
import org.eventb.core.IPOSource;
import org.eventb.core.IPRSequent;
import org.eventb.core.ITheorem;
import org.eventb.core.IVariable;
import org.eventb.core.pm.IPOChangeEvent;
import org.eventb.core.pm.IPOChangedListener;
import org.eventb.core.pm.ProofState;
import org.eventb.internal.ui.EventBFormText;
import org.eventb.internal.ui.EventBUIPlugin;
import org.eventb.internal.ui.IEventBFormText;
import org.eventb.internal.ui.UIUtils;
import org.eventb.internal.ui.projectexplorer.ProjectExplorer;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IParent;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;

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
	private IEventBFormText formText;
	
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
		
		ProofState ps = editor.getUserSupport().getCurrentPO();
		if (ps != null)
			scrolledForm.setText(ps.getPRSequent().getName());

		Composite body = scrolledForm.getBody();
		body.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		GridLayout gl = new GridLayout();
		body.setLayout(gl);

		formText = new EventBFormText(toolkit.createFormText(body, true));
		if (ps != null) setFormText(ps.getPRSequent());
		
		toolkit.paintBordersFor(body);
		scrolledForm.reflow(true);
	}

	private void setFormText(IPRSequent prSequent) {		
		try {
			String formString = "<form>";

			IPODescription desc = prSequent.getDescription();
			IPOSource [] sources = desc.getSources();
			for (IPOSource source : sources) {
				String role = source.getSourceRole();
//				UIUtils.debug("Role " + role);
				formString = formString + "<li style=\"bullet\">" + role + "</li>";

				String id = source.getSourceHandleIdentifier();

				// TODO Dirty fix to get the uncheck element handle identifier
				id = id.replaceFirst("bcm", "bum");
				id = id.replaceFirst("bcc", "buc");
				id = id.replaceFirst("scEvent", "event");
//				UIUtils.debug("ID unchecked model " + id);
				
				IRodinElement element = RodinCore.create(id);
				if (element instanceof ITheorem) {
					formString = formString + "<li style=\"text\" value=\"\">" + UIUtils.makeHyperlink(id, element.getElementName()) + ": ";
					formString = formString + UIUtils.XMLWrapUp(((IInternalElement) element).getContents()); 
					formString = formString + "</li>";
				}
				if (element instanceof IAxiom) {
					formString = formString + "<li style=\"text\" value=\"\">" + UIUtils.makeHyperlink(id, element.getElementName()) + ": ";
					formString = formString + UIUtils.XMLWrapUp(((IInternalElement) element).getContents()); 
					formString = formString + "</li>";
				}
				else if (element instanceof IInvariant) {
					formString = formString + "<li style=\"text\" value=\"\">" + UIUtils.makeHyperlink(id, element.getElementName()) + ": ";
					formString = formString + UIUtils.XMLWrapUp(((IInternalElement) element).getContents()); 
					formString = formString + "</li>";
				}
				else if (element instanceof IEvent) {
					formString = formString + "<li style=\"text\" value=\"\">" + UIUtils.makeHyperlink(id, element.getElementName()) + ":</li>";
					IRodinElement [] lvars = ((IParent) element).getChildrenOfType(IVariable.ELEMENT_TYPE);
					IRodinElement [] guards = ((IParent) element).getChildrenOfType(IGuard.ELEMENT_TYPE);
					IRodinElement [] actions = ((IParent) element).getChildrenOfType(IAction.ELEMENT_TYPE);
					
					if (lvars.length != 0) {
						formString = formString + "<li style=\"text\" value=\"\" bindent = \"20\">";
						formString = formString + "<b>ANY</b> ";
						for (int j = 0; j < lvars.length; j++) {
							if (j == 0)	{
								formString = formString + UIUtils.makeHyperlink(lvars[j].getHandleIdentifier(), lvars[j].getElementName());
							}
							else formString = formString + ", " + UIUtils.makeHyperlink(lvars[j].getHandleIdentifier(), lvars[j].getElementName());
						}			
						formString = formString + " <b>WHERE</b>";
						formString = formString + "</li>";
					}
					else {
						if (guards.length !=0) {
							formString = formString + "<li style=\"text\" value=\"\" bindent = \"20\">";
							formString = formString + "<b>WHEN</b></li>";
						}
						else {
							formString = formString + "<li style=\"text\" value=\"\" bindent = \"20\">";
							formString = formString + "<b>BEGIN</b></li>";
						}
					
					}

					for (int j = 0; j < guards.length; j++) {
						formString = formString + "<li style=\"text\" value=\"\" bindent=\"40\">";
						formString = formString + UIUtils.makeHyperlink(guards[j].getHandleIdentifier(), guards[j].getElementName()) + ": "
							+ UIUtils.XMLWrapUp(((IInternalElement) guards[j]).getContents());
						formString = formString + "</li>";
					}
					
					if (guards.length != 0) {
						formString = formString + "<li style=\"text\" value=\"\" bindent=\"20\">";
						formString = formString + "<b>THEN</b></li>";
					}
				
					for (int j = 0; j < actions.length; j++) {
						formString = formString + "<li style=\"text\" value=\"\" bindent=\"40\">";
						formString = formString + UIUtils.makeHyperlink(actions[j].getHandleIdentifier(), ((IInternalElement) actions[j]).getContents());
						formString = formString + "</li>";
					}
					formString = formString + "<li style=\"text\" value=\"\" bindent=\"20\">";
					formString = formString + "<b>END</b></li>";
				}
			}
			formString = formString + "</form>";
			formText.getFormText().setText(formString, true, false);
			
			formText.getFormText().addHyperlinkListener(new HyperlinkAdapter() {

				/* (non-Javadoc)
				 * @see org.eclipse.ui.forms.events.HyperlinkAdapter#linkActivated(org.eclipse.ui.forms.events.HyperlinkEvent)
				 */
				@Override
				public void linkActivated(HyperlinkEvent e) {
					String id = (String) e.getHref();
					IRodinElement element = RodinCore.create(id);
					UIUtils.linkToEventBEditor(element);
					UIUtils.activateView(IPageLayout.ID_PROBLEM_VIEW);
					UIUtils.activateView(ProjectExplorer.VIEW_ID);
				}

			});
			scrolledForm.reflow(true);
		}
		catch (RodinDBException e) {
			e.printStackTrace();
		}
			
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
				if (prSequent.exists()) {
					scrolledForm.setText(prSequent.getName());
					scrolledForm.reflow(true);
					setFormText(prSequent);
				}
			}
		});
	}
	
}