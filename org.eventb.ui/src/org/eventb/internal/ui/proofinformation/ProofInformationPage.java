/*******************************************************************************
 * Copyright (c) 2005, 2008 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - Added a constant for the user support manager
 *     Systerel - replaced variable by parameter
 *     Systerel - separation of file and root element
 *     Systerel - added implicit children for events
 ******************************************************************************/
package org.eventb.internal.ui.proofinformation;

import static org.eventb.ui.EventBUIPlugin.NAVIGATOR_VIEW_ID;

import java.util.List;

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
import org.eventb.core.IEventBRoot;
import org.eventb.core.IGuard;
import org.eventb.core.IInvariant;
import org.eventb.core.IPOSource;
import org.eventb.core.IPSStatus;
import org.eventb.core.IParameter;
import org.eventb.core.IRefinesEvent;
import org.eventb.core.IWitness;
import org.eventb.core.pm.IProofState;
import org.eventb.core.pm.IProofStateDelta;
import org.eventb.core.pm.IUserSupport;
import org.eventb.core.pm.IUserSupportDelta;
import org.eventb.core.pm.IUserSupportManager;
import org.eventb.core.pm.IUserSupportManagerChangedListener;
import org.eventb.core.pm.IUserSupportManagerDelta;
import org.eventb.internal.ui.UIUtils;
import org.eventb.internal.ui.prover.ProverUIUtils;
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

	private static final IUserSupportManager USM = EventBPlugin
			.getUserSupportManager();

	ScrolledForm scrolledForm;

	private IEventBFormText formText;

	IUserSupport userSupport;
	
	IProofState proofState;

	/**
	 * Constructor.
	 * <p>
	 * 
	 * @param userSupport
	 *            the User Support corresponding to this page.
	 */
	public ProofInformationPage(IUserSupport userSupport) {
		this.userSupport = userSupport;
		this.proofState = userSupport.getCurrentPO();
		USM.addChangeListener(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.part.IPage#dispose()
	 */
	@Override
	public void dispose() {
		// Deregister with the user support manager.
		USM.removeChangeListener(this);
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

		if (proofState != null)
			scrolledForm.setText(proofState.getPSStatus().getElementName());

		Composite body = scrolledForm.getBody();
		body.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		GridLayout gl = new GridLayout();
		body.setLayout(gl);

		formText = new EventBFormText(toolkit.createFormText(body, true));
		if (proofState != null)
			setFormText(proofState.getPSStatus(), new NullProgressMonitor());

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
	// Should there be a pretty-print form for every RodinElement?
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
				if (element instanceof IAxiom) {
					IAxiom axm = (IAxiom) element;
					formBuilder.append("<li style=\"bullet\">Axiom in ");
					formBuilder.append(((IEventBRoot) axm.getParent()).getComponentName());
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
					formBuilder.append(((IEventBRoot) inv.getParent()).getComponentName());
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
					formBuilder.append(((IEventBRoot) evt.getParent()).getComponentName());
					formBuilder.append("</li><li style=\"text\" value=\"\">");
					formBuilder.append(UIUtils
							.makeHyperlink(id, evt.getLabel()));
					formBuilder.append(":</li>");
					IRefinesEvent[] refinesClauses = evt.getRefinesClauses();
					List<IParameter> params = UIUtils.getVisibleChildrenOfType(evt, IParameter.ELEMENT_TYPE);
					List<IGuard> guards = UIUtils.getVisibleChildrenOfType(evt, IGuard.ELEMENT_TYPE);
					IWitness[] witnesses = evt.getWitnesses();
					List<IAction> actions = UIUtils.getVisibleChildrenOfType(evt, IAction.ELEMENT_TYPE);
					
					if (refinesClauses.length != 0) {
						formBuilder
								.append("<li style=\"text\" value=\"\" bindent = \"20\">");
						formBuilder.append("<b>REFINES</b></li>");
						for (IRefinesEvent refinesClause : refinesClauses) {
							formBuilder
									.append("<li style=\"text\" value=\"\" bindent=\"40\">");
							formBuilder
									.append(UIUtils.makeHyperlink(refinesClause
											.getHandleIdentifier(),
									refinesClause.getAbstractEventLabel()));
							formBuilder.append("</li>");
						}
					}
					
					if (params.size() != 0) {
						formBuilder
								.append("<li style=\"text\" value=\"\" bindent = \"20\">");
						formBuilder.append("<b>ANY</b> ");
						String sep = "";
						for (IParameter param : params) {
							formBuilder.append(sep);
							sep = ", ";
							formBuilder.append(UIUtils.makeHyperlink(param
									.getHandleIdentifier(), param
									.getIdentifierString()));
						}
						formBuilder.append(" <b>WHERE</b></li>");
					} else if (guards.size() != 0) {
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

					if (witnesses.length != 0) {
						formBuilder
								.append("<li style=\"text\" value=\"\" bindent = \"20\">");
						formBuilder.append("<b>WITH</b></li>");
						for (IWitness witness : witnesses) {
							formBuilder
									.append("<li style=\"text\" value=\"\" bindent=\"40\">");
							formBuilder.append(UIUtils.makeHyperlink(witness
									.getHandleIdentifier(), witness.getLabel()));
							formBuilder.append(": ");
							formBuilder.append(UIUtils.XMLWrapUp(witness
									.getPredicateString()));
							formBuilder.append("</li>");
						}
					}
					
					if (actions.size() != 0) {
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
				else {
					if (ProofInformationUtils.DEBUG) {
						ProofInformationUtils.debug("Unknow element " + element);
					}
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
					UIUtils.activateView(NAVIGATOR_VIEW_ID);
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
		if (ProofInformationUtils.DEBUG)
			ProofInformationUtils.debug("Begin User Support Manager Changed");

		// Do nothing if the control has been disposed.
		if (this.getControl().isDisposed())
			return;
		
		// Trying to get the changes for the current user support.
		final IUserSupportDelta affectedUserSupport = ProverUIUtils
				.getUserSupportDelta(delta, userSupport);

		// Do nothing if there is no change for this current user support.
		if (affectedUserSupport == null)
			return;

		// If the user support has been removed, do nothing. This will be handle
		// by the main proof editor.
		final int kind = affectedUserSupport.getKind();
		if (kind == IUserSupportDelta.REMOVED) {
			return; // Do nothing
		}

		// This case should NOT happened.
		if (kind == IUserSupportDelta.ADDED) {
			if (ProofInformationUtils.DEBUG)
				ProofInformationUtils
						.debug("Error: Delta said that the user Support is added");
			return; // Do nothing
		}

		Display display = scrolledForm.getDisplay();
		
		display.syncExec(new Runnable() {
			public void run() {
				
				if (scrolledForm.isDisposed())
					return;

				// Handle the case where the user support has changed.
				if (kind == IUserSupportDelta.CHANGED) {
					int flags = affectedUserSupport.getFlags();
					
					if ((flags & IUserSupportDelta.F_CURRENT) != 0) {
						// The current proof state is changed.
						proofState = userSupport.getCurrentPO();
						if (proofState != null) {
							IPSStatus prSequent = proofState.getPSStatus();
							if (prSequent.exists()) {
								scrolledForm.setText(prSequent.getElementName());
								setFormText(prSequent, new NullProgressMonitor());
								scrolledForm.reflow(true);
							}
							else {
								scrolledForm.setText(prSequent.getElementName()
										+ "does not exists");
								scrolledForm.reflow(true);								
							}
						}
						else {
							clearFormText();							
						}
					}
					
					if ((flags & IUserSupportDelta.F_STATE) != 0) {
						IProofStateDelta proofStateDelta = ProverUIUtils
								.getProofStateDelta(affectedUserSupport,
										proofState);
						
						if (proofStateDelta == null)
							return;
						
						if (proofState != null) {
							IPSStatus prSequent = proofState.getPSStatus();
							if (prSequent.exists()) {
								scrolledForm.setText(prSequent.getElementName());
								setFormText(prSequent, new NullProgressMonitor());
								scrolledForm.reflow(true);
							}
							else {
								scrolledForm.setText(prSequent.getElementName()
										+ "does not exists");
								scrolledForm.reflow(true);								
							}
						}
						else {
							clearFormText();							
						}
					}
				}
			}
		});

		if (ProofInformationUtils.DEBUG)
			ProofInformationUtils.debug("End User Support Manager Changed");

	}

	/**
	 * Utility method to clear the form text, i.e. set the form text to blank.
	 */
	protected void clearFormText() {
		scrolledForm.setText("");
		formText.getFormText().setText("<form></form>", true, false);
		scrolledForm.reflow(true);
	}
}