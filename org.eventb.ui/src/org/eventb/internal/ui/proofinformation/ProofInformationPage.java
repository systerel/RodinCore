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
import org.eventb.core.IEventBFile;
import org.eventb.core.IGuard;
import org.eventb.core.IInvariant;
import org.eventb.core.IPOSource;
import org.eventb.core.IPSStatus;
import org.eventb.core.ITheorem;
import org.eventb.core.IVariable;
import org.eventb.core.pm.IProofState;
import org.eventb.core.pm.IUserSupport;
import org.eventb.core.pm.IUserSupportDelta;
import org.eventb.core.pm.IUserSupportManagerChangedListener;
import org.eventb.core.pm.IUserSupportManagerDelta;
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
		IProofInformationPage, IUserSupportManagerChangedListener {
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
		EventBPlugin.getDefault().getUserSupportManager().addChangeListener(
				this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.part.IPage#dispose()
	 */
	@Override
	public void dispose() {
		// Deregister with the user support.
		EventBPlugin.getDefault().getUserSupportManager().removeChangeListener(
				this);
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
			scrolledForm.setText(ps.getPSStatus().getElementName());

		Composite body = scrolledForm.getBody();
		body.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		GridLayout gl = new GridLayout();
		body.setLayout(gl);

		formText = new EventBFormText(toolkit.createFormText(body, true));
		if (ps != null)
			setFormText(ps.getPSStatus(), new NullProgressMonitor());

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
	// TODO share code with ASTConverter
	void setFormText(IPSStatus prSequent, IProgressMonitor monitor) {
		try {
			StringBuilder formBuilder = new StringBuilder("<form>");

			IPOSource[] sources = prSequent.getPOSequent().getSources();
			for (IPOSource source : sources) {
				IRodinElement element = source.getSource();
				String id = element.getHandleIdentifier();
				if (ProofInformationUtils.DEBUG) {
					ProofInformationUtils.debug("id: " + id);
					ProofInformationUtils.debug("Find: " + element);
				}
//				final IEventBFile file = (IEventBFile) element.getParent();
				if (element instanceof ITheorem) {
					final ITheorem thm = (ITheorem) element;
					formBuilder.append("<li style=\"bullet\">Theorem in ");
					formBuilder.append(((IEventBFile) thm.getParent()).getComponentName());
					formBuilder.append("</li><li style=\"text\" value=\"\">");
					formBuilder.append(UIUtils
							.makeHyperlink(id, thm.getLabel()));
					formBuilder.append(": ");
					formBuilder.append(UIUtils.XMLWrapUp(thm
							.getPredicateString()));
					formBuilder.append("</li>");
				}
				if (element instanceof IAxiom) {
					IAxiom axm = (IAxiom) element;
					formBuilder.append("<li style=\"bullet\">Axiom in ");
					formBuilder.append(((IEventBFile) axm.getParent()).getComponentName());
					formBuilder.append("</li><li style=\"text\" value=\"\">");
					formBuilder.append(UIUtils
							.makeHyperlink(id, axm.getLabel()));
					formBuilder.append(": ");
					formBuilder.append(UIUtils.XMLWrapUp(axm
							.getPredicateString()));
					formBuilder.append("</li>");
				} else if (element instanceof IInvariant) {
					IInvariant inv = (IInvariant) element;
					formBuilder.append("<li style=\"bullet\">Invariant in ");
					formBuilder.append(((IEventBFile) inv.getParent()).getComponentName());
					formBuilder.append("</li><li style=\"text\" value=\"\">");
					formBuilder.append(UIUtils
							.makeHyperlink(id, inv.getLabel()));
					formBuilder.append(": ");
					formBuilder.append(UIUtils.XMLWrapUp(inv
							.getPredicateString()));
					formBuilder.append("</li>");
				} else if (element instanceof IEvent) {
					IEvent evt = (IEvent) element;
					formBuilder.append("<li style=\"bullet\">Event in ");
					formBuilder.append(((IEventBFile) evt.getParent()).getComponentName());
					formBuilder.append("</li><li style=\"text\" value=\"\">");
					formBuilder.append(UIUtils
							.makeHyperlink(id, evt.getLabel()));
					formBuilder.append(":</li>");
					IVariable[] lvars = evt.getVariables();
					IGuard[] guards = evt.getGuards();
					IAction[] actions = evt.getActions();

					if (lvars.length != 0) {
						formBuilder
								.append("<li style=\"text\" value=\"\" bindent = \"20\">");
						formBuilder.append("<b>ANY</b> ");
						String sep = "";
						for (IVariable var : lvars) {
							formBuilder.append(sep);
							sep = ", ";
							formBuilder.append(UIUtils.makeHyperlink(var
									.getHandleIdentifier(), var
									.getIdentifierString()));
						}
						formBuilder.append(" <b>WHERE</b></li>");
					} else if (guards.length != 0) {
						formBuilder
								.append("<li style=\"text\" value=\"\" bindent = \"20\">");
						formBuilder.append("<b>WHEN</b></li>");
					} else {
						formBuilder
								.append("<li style=\"text\" value=\"\" bindent = \"20\">");
						formBuilder.append("<b>BEGIN</b></li>");
					}

					for (IGuard guard : guards) {
						formBuilder
								.append("<li style=\"text\" value=\"\" bindent=\"40\">");
						formBuilder.append(UIUtils.makeHyperlink(guard
								.getHandleIdentifier(), guard.getLabel()));
						formBuilder.append(": ");
						formBuilder.append(UIUtils.XMLWrapUp(guard
								.getPredicateString()));
						formBuilder.append("</li>");
					}

					if (guards.length != 0) {
						formBuilder
								.append("<li style=\"text\" value=\"\" bindent=\"20\">");
						formBuilder.append("<b>THEN</b></li>");
					}

					for (IAction action : actions) {
						formBuilder
								.append("<li style=\"text\" value=\"\" bindent=\"40\">");
						formBuilder.append(UIUtils.makeHyperlink(action
								.getHandleIdentifier(), action.getLabel()));
						formBuilder.append(": ");
						formBuilder.append(UIUtils.XMLWrapUp(action
								.getAssignmentString()));
						formBuilder.append("</li>");
					}
					formBuilder
							.append("<li style=\"text\" value=\"\" bindent=\"20\">");
					formBuilder.append("<b>END</b></li>");
				}
			}
			formBuilder.append("</form>");
			formText.getFormText().setText(formBuilder.toString(), true, false);

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

	public void userSupportManagerChanged(final IUserSupportManagerDelta delta) {
		if (scrolledForm.getContent().isDisposed())
			return;

		final IUserSupport userSupport = this.editor.getUserSupport();

		IUserSupportDelta[] affectedUserSupports = delta
				.getAffectedUserSupports();
		IUserSupportDelta userSupportDelta = null;
		for (IUserSupportDelta tmp : affectedUserSupports) {
			if (tmp.getUserSupport() == userSupport) {
				userSupportDelta = tmp;
				break;
			}
		}
		if (userSupportDelta == null)
			return;
		final IUserSupportDelta affectedUserSupport = userSupportDelta;

		final int kind = affectedUserSupport.getKind();
		if (kind == IUserSupportDelta.REMOVED)
			return;

		Display display = Display.getDefault();
		display.syncExec(new Runnable() {
			public void run() {
				IProofState ps = userSupport.getCurrentPO();
				if (ps != null) {
					IPSStatus prSequent = ps.getPSStatus();
					if (prSequent.exists()) {
						scrolledForm.setText(prSequent.getElementName());
						setFormText(prSequent, new NullProgressMonitor());
						scrolledForm.reflow(true);
					}
				} else {
					clearFormText();
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