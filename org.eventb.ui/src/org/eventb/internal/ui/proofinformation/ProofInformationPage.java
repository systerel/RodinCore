/*******************************************************************************
 * Copyright (c) 2005 ETH Zurich.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Rodin @ ETH Zurich
 ******************************************************************************/

package org.eventb.internal.ui.proofinformation;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
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
import org.eventb.core.EventBPlugin;
import org.eventb.core.IAction;
import org.eventb.core.IAxiom;
import org.eventb.core.IEvent;
import org.eventb.core.IGuard;
import org.eventb.core.IInvariant;
import org.eventb.core.IPOSource;
import org.eventb.core.IPSStatus;
import org.eventb.core.ITheorem;
import org.eventb.core.IVariable;
import org.eventb.core.pm.IProofState;
import org.eventb.core.pm.IProofStateChangedListener;
import org.eventb.core.pm.IProofStateDelta;
import org.eventb.internal.ui.UIUtils;
import org.eventb.internal.ui.projectexplorer.ProjectExplorer;
import org.eventb.internal.ui.prover.ProverUI;
import org.eventb.ui.EventBFormText;
import org.eventb.ui.IEventBFormText;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;

/**
 * @author htson
 *         <p>
 *         This class is an implementation of a Proof Control 'page'.
 */
public class ProofInformationPage extends Page implements
		IProofInformationPage, IProofStateChangedListener {
	ScrolledForm scrolledForm;

	private ProverUI editor;

	private IEventBFormText formText;

	/**
	 * Constructor.
	 * <p>
	 * 
	 * @param editor
	 *            the Prover UI Editor corresponding to this page.
	 */
	public ProofInformationPage(ProverUI editor) {
		this.editor = editor;
		editor.getUserSupport().addStateChangedListeners(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.part.IPage#dispose()
	 */
	@Override
	public void dispose() {
		// Deregister with the user support.
		editor.getUserSupport().removeStateChangedListeners(this);
		formText.dispose();
		super.dispose();
	}

	/**
	 * This is a callback that will allow us to create the viewer and initialize
	 * it.
	 * <p>
	 * 
	 * @see org.eclipse.ui.part.IPage#createControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createControl(Composite parent) {
		FormToolkit toolkit = new FormToolkit(parent.getDisplay());

		scrolledForm = toolkit.createScrolledForm(parent);

		IProofState ps = editor.getUserSupport().getCurrentPO();
		if (ps != null)
			scrolledForm.setText(ps.getPRSequent().getElementName());

		Composite body = scrolledForm.getBody();
		body.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		GridLayout gl = new GridLayout();
		body.setLayout(gl);

		formText = new EventBFormText(toolkit.createFormText(body, true));
		if (ps != null)
			setFormText(ps.getPRSequent(), new NullProgressMonitor());

		toolkit.paintBordersFor(body);
		scrolledForm.reflow(true);
	}

	/**
	 * Set the formText according to the current prSequent.
	 * <p>
	 * 
	 * @param prSequent
	 *            the current prSequent
	 */
	void setFormText(IPSStatus prSequent, IProgressMonitor monitor) {
		try {
			String formString = "<form>";

			IPOSource[] sources = prSequent.getPOSequent().getSources(monitor);
			for (IPOSource source : sources) {
				IRodinElement element = source.getSource(monitor);
				String id = element.getHandleIdentifier();
				if (ProofInformationUtils.DEBUG) {
					ProofInformationUtils.debug("id: " + id);
					ProofInformationUtils.debug("Find: " + element);
				}
				if (element instanceof ITheorem) {
					ITheorem thm = (ITheorem) element;
					formString = formString
							+ "<li style=\"bullet\">"
							+ "Theorem in "
							+ EventBPlugin.getComponentName(thm.getParent()
									.getElementName()) + "</li>";
					formString = formString
							+ "<li style=\"text\" value=\"\">"
							+ UIUtils.makeHyperlink(id, thm
									.getLabel(new NullProgressMonitor()))
							+ ": ";
					formString = formString
							+ UIUtils.XMLWrapUp(thm.getPredicateString(monitor));
					formString = formString + "</li>";
				}
				if (element instanceof IAxiom) {
					IAxiom axm = (IAxiom) element;
					formString = formString
							+ "<li style=\"bullet\">"
							+ "Axiom in "
							+ EventBPlugin.getComponentName(axm.getParent()
									.getElementName()) + "</li>";
					formString = formString
							+ "<li style=\"text\" value=\"\">"
							+ UIUtils.makeHyperlink(id, axm
									.getLabel(new NullProgressMonitor()))
							+ ": ";
					formString = formString
							+ UIUtils.XMLWrapUp(axm.getPredicateString(monitor));
					formString = formString + "</li>";
				} else if (element instanceof IInvariant) {
					IInvariant inv = (IInvariant) element;
					formString = formString
							+ "<li style=\"bullet\">"
							+ "Invariant in "
							+ EventBPlugin.getComponentName(inv.getParent()
									.getElementName()) + "</li>";
					formString = formString
							+ "<li style=\"text\" value=\"\">"
							+ UIUtils.makeHyperlink(id, inv
									.getLabel(new NullProgressMonitor()))
							+ ": ";
					formString = formString
							+ UIUtils.XMLWrapUp(inv.getPredicateString(monitor));
					formString = formString + "</li>";
				} else if (element instanceof IEvent) {
					IEvent evt = (IEvent) element;
					formString = formString
							+ "<li style=\"bullet\">"
							+ "Event in "
							+ EventBPlugin.getComponentName(evt.getParent()
									.getElementName()) + "</li>";
					formString = formString
							+ "<li style=\"text\" value=\"\">"
							+ UIUtils.makeHyperlink(id, evt
									.getLabel(new NullProgressMonitor()))
							+ ":</li>";
					IRodinElement[] lvars = evt
							.getChildrenOfType(IVariable.ELEMENT_TYPE);
					IRodinElement[] guards = evt
							.getChildrenOfType(IGuard.ELEMENT_TYPE);
					IRodinElement[] actions = evt
							.getChildrenOfType(IAction.ELEMENT_TYPE);

					if (lvars.length != 0) {
						formString = formString
								+ "<li style=\"text\" value=\"\" bindent = \"20\">";
						formString = formString + "<b>ANY</b> ";
						for (int j = 0; j < lvars.length; j++) {
							IVariable var = (IVariable) lvars[j];
							if (j == 0) {
								formString = formString
										+ UIUtils.makeHyperlink(var
												.getHandleIdentifier(), var
												.getIdentifierString(monitor));
							} else
								formString = formString
										+ ", "
										+ UIUtils.makeHyperlink(var
												.getHandleIdentifier(), var
												.getIdentifierString(monitor));
						}
						formString = formString + " <b>WHERE</b>";
						formString = formString + "</li>";
					} else {
						if (guards.length != 0) {
							formString = formString
									+ "<li style=\"text\" value=\"\" bindent = \"20\">";
							formString = formString + "<b>WHEN</b></li>";
						} else {
							formString = formString
									+ "<li style=\"text\" value=\"\" bindent = \"20\">";
							formString = formString + "<b>BEGIN</b></li>";
						}

					}

					for (IRodinElement child : guards) {
						IGuard guard = (IGuard) child;
						formString = formString
								+ "<li style=\"text\" value=\"\" bindent=\"40\">";
						formString = formString
								+ UIUtils.makeHyperlink(guard
										.getHandleIdentifier(), guard
										.getLabel(new NullProgressMonitor()))
								+ ": "
								+ UIUtils.XMLWrapUp(guard.getPredicateString(monitor));
						formString = formString + "</li>";
					}

					if (guards.length != 0) {
						formString = formString
								+ "<li style=\"text\" value=\"\" bindent=\"20\">";
						formString = formString + "<b>THEN</b></li>";
					}

					for (IRodinElement child : actions) {
						IAction action = (IAction) child;
						formString = formString
								+ "<li style=\"text\" value=\"\" bindent=\"40\">";
						formString = formString
								+ UIUtils.makeHyperlink(action
										.getHandleIdentifier(), action
										.getLabel(new NullProgressMonitor()))
								+ ": "
								+ UIUtils.XMLWrapUp(action
										.getAssignmentString(monitor));
						formString = formString + "</li>";
					}
					formString = formString
							+ "<li style=\"text\" value=\"\" bindent=\"20\">";
					formString = formString + "<b>END</b></li>";
				}
			}
			formString = formString + "</form>";
			formText.getFormText().setText(formString, true, false);

			formText.getFormText().addHyperlinkListener(new HyperlinkAdapter() {

				/*
				 * (non-Javadoc)
				 * 
				 * @see org.eclipse.ui.forms.events.HyperlinkAdapter#linkActivated(org.eclipse.ui.forms.events.HyperlinkEvent)
				 */
				@Override
				public void linkActivated(HyperlinkEvent e) {
					String id = (String) e.getHref();
					IRodinElement element = RodinCore.valueOf(id);
					UIUtils.linkToEventBEditor(element);
					UIUtils.activateView(IPageLayout.ID_PROBLEM_VIEW);
					UIUtils.activateView(ProjectExplorer.VIEW_ID);
				}

			});
			scrolledForm.reflow(true);
		} catch (RodinDBException e) {
			e.printStackTrace();
		}

	}

	/**
	 * Passing the focus request to the ScrolledForm.
	 * <p>
	 * 
	 * @see org.eclipse.ui.part.IPage#setFocus()
	 */
	@Override
	public void setFocus() {
		scrolledForm.setFocus();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.part.Page#getControl()
	 */
	@Override
	public Control getControl() {
		if (scrolledForm == null)
			return null;
		return scrolledForm;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eventb.core.pm.IProofStateChangedListener#proofStateChanged(org.eventb.core.pm.IProofStateDelta)
	 */
	public void proofStateChanged(final IProofStateDelta delta) {
		if (scrolledForm.getContent().isDisposed())
			return;
		Display display = Display.getDefault();
		display.syncExec(new Runnable() {
			public void run() {
				IProofState ps = delta.getProofState();
				if (delta.isNewProofState()) {
					if (ps != null) {
						IPSStatus prSequent = ps.getPRSequent();
						if (prSequent.exists()) {
							scrolledForm.setText(prSequent.getElementName());
							setFormText(prSequent, new NullProgressMonitor());
							scrolledForm.reflow(true);
						}
					} else {
						clearFormText();
					}
				}
			}
		});

	}

	protected void clearFormText() {
		scrolledForm.setText("");
		formText.getFormText().setText("<form></form>", true, false);
		scrolledForm.reflow(true);
	}
}