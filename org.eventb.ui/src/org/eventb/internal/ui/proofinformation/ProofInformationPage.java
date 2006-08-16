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
import org.eventb.core.pm.IProofStateChangedListener;
import org.eventb.core.pm.IProofStateDelta;
import org.eventb.core.pm.ProofState;
import org.eventb.internal.ui.EventBFormText;
import org.eventb.internal.ui.EventBUIPlugin;
import org.eventb.internal.ui.IEventBFormText;
import org.eventb.internal.ui.UIUtils;
import org.eventb.internal.ui.projectexplorer.ProjectExplorer;
import org.eventb.internal.ui.prover.ProverUI;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IParent;
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
	private ScrolledForm scrolledForm;

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
		if (ps != null)
			setFormText(ps.getPRSequent());

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
	private void setFormText(IPRSequent prSequent) {
		try {
			String formString = "<form>";

			IPODescription desc = prSequent.getDescription();
			IPOSource[] sources = desc.getSources();
			for (IPOSource source : sources) {
				String role = source.getSourceRole();
				formString = formString + "<li style=\"bullet\">" + role
						+ "</li>";

				String id = source.getSourceHandleIdentifier();

				// TODO Dirty fix to get the uncheck element handle identifier
				id = id.replaceFirst("\\.bcm\\|", "\\.bum\\|");
				id = id.replaceFirst("\\.bcc\\|", "\\.buc\\|");
				id = id.replaceAll("org.eventb.core.scInvariant",
						"org.eventb.core.invariant");
				id = id.replaceAll("org.eventb.core.scTheorem",
						"org.eventb.core.theorem");
				id = id.replaceAll("org.eventb.core.scAxiom",
						"org.eventb.core.axiom");
				id = id.replaceAll("org.eventb.core.scGuard",
						"org.eventb.core.guard");
				id = id.replaceAll("org.eventb.core.scEvent",
						"org.eventb.core.event");
				ProofInformation.debug("ID unchecked model " + id);

				IRodinElement element = RodinCore.create(id);
				ProofInformation.debug("id: " + id);
				ProofInformation.debug("Find: " + element);
				if (element instanceof ITheorem) {
					formString = formString
							+ "<li style=\"text\" value=\"\">"
							+ UIUtils.makeHyperlink(id, element
									.getElementName()) + ": ";
					formString = formString
							+ UIUtils.XMLWrapUp(((IInternalElement) element)
									.getContents());
					formString = formString + "</li>";
				}
				if (element instanceof IAxiom) {
					formString = formString
							+ "<li style=\"text\" value=\"\">"
							+ UIUtils.makeHyperlink(id, element
									.getElementName()) + ": ";
					formString = formString
							+ UIUtils.XMLWrapUp(((IInternalElement) element)
									.getContents());
					formString = formString + "</li>";
				} else if (element instanceof IInvariant) {
					formString = formString
							+ "<li style=\"text\" value=\"\">"
							+ UIUtils.makeHyperlink(id, element
									.getElementName()) + ": ";
					formString = formString
							+ UIUtils.XMLWrapUp(((IInternalElement) element)
									.getContents());
					formString = formString + "</li>";
				} else if (element instanceof IEvent) {
					formString = formString
							+ "<li style=\"text\" value=\"\">"
							+ UIUtils.makeHyperlink(id, element
									.getElementName()) + ":</li>";
					IRodinElement[] lvars = ((IParent) element)
							.getChildrenOfType(IVariable.ELEMENT_TYPE);
					IRodinElement[] guards = ((IParent) element)
							.getChildrenOfType(IGuard.ELEMENT_TYPE);
					IRodinElement[] actions = ((IParent) element)
							.getChildrenOfType(IAction.ELEMENT_TYPE);

					if (lvars.length != 0) {
						formString = formString
								+ "<li style=\"text\" value=\"\" bindent = \"20\">";
						formString = formString + "<b>ANY</b> ";
						for (int j = 0; j < lvars.length; j++) {
							if (j == 0) {
								formString = formString
										+ UIUtils.makeHyperlink(lvars[j]
												.getHandleIdentifier(),
												lvars[j].getElementName());
							} else
								formString = formString
										+ ", "
										+ UIUtils.makeHyperlink(lvars[j]
												.getHandleIdentifier(),
												lvars[j].getElementName());
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

					for (int j = 0; j < guards.length; j++) {
						formString = formString
								+ "<li style=\"text\" value=\"\" bindent=\"40\">";
						formString = formString
								+ UIUtils.makeHyperlink(guards[j]
										.getHandleIdentifier(), guards[j]
										.getElementName())
								+ ": "
								+ UIUtils
										.XMLWrapUp(((IInternalElement) guards[j])
												.getContents());
						formString = formString + "</li>";
					}

					if (guards.length != 0) {
						formString = formString
								+ "<li style=\"text\" value=\"\" bindent=\"20\">";
						formString = formString + "<b>THEN</b></li>";
					}

					for (int j = 0; j < actions.length; j++) {
						formString = formString
								+ "<li style=\"text\" value=\"\" bindent=\"40\">";
						formString = formString
								+ UIUtils.makeHyperlink(actions[j]
										.getHandleIdentifier(), actions[j]
										.getElementName())
								+ ": "
								+ UIUtils
										.XMLWrapUp(((IInternalElement) actions[j])
												.getContents());
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
					IRodinElement element = RodinCore.create(id);
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
		if (scrolledForm.getContent().isDisposed()) return;
		Display display = Display.getDefault();
		display.syncExec(new Runnable() {
			public void run() {
				ProofState ps = delta.getProofState();
				if (delta.isNewProofState()) {
					if (ps != null) {
						IPRSequent prSequent = ps.getPRSequent();
						if (prSequent.exists()) {
							scrolledForm.setText(prSequent.getName());
							setFormText(prSequent);
							scrolledForm.reflow(true);
						}
					}
					else {
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