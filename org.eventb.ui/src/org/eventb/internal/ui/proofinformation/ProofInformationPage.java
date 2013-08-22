/*******************************************************************************
 * Copyright (c) 2005, 2013 ETH Zurich and others.
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
 *     Systerel - refactored to display all possible EventB elements
 *******************************************************************************/
package org.eventb.internal.ui.proofinformation;

import static org.eventb.internal.ui.UIUtils.getVisibleChildrenOfType;
import static org.eventb.internal.ui.proofinformation.ProofInformationListItem.getInfo;
import static org.eventb.ui.EventBUIPlugin.NAVIGATOR_VIEW_ID;
import static org.eventb.ui.eventbeditor.EventBEditorPage.createEventBFormText;

import java.util.ArrayList;
import java.util.Arrays;
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
import org.eventb.core.IEvent;
import org.eventb.core.IGuard;
import org.eventb.core.IIdentifierElement;
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
import org.eventb.internal.ui.eventbeditor.elementdesc.ElementDesc;
import org.eventb.internal.ui.eventbeditor.elementdesc.ElementDescRegistry;
import org.eventb.internal.ui.eventbeditor.elementdesc.IElementDesc;
import org.eventb.internal.ui.eventbeditor.elementdesc.IElementRelationship;
import org.eventb.internal.ui.prover.ProverUIUtils;
import org.eventb.ui.IEventBFormText;
import org.eventb.ui.IImplicitChildProvider;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;

/**
 * This class is an implementation of a Proof Control 'page'.
 * 
 * @author htson
 */
public class ProofInformationPage extends Page implements
		IProofInformationPage, IUserSupportManagerChangedListener {

	private static final IUserSupportManager USM = EventBPlugin
			.getUserSupportManager();
	private static final int subLevel = 3;

	protected final IUserSupport userSupport;
	protected IProofState proofState;
	protected ScrolledForm scrolledForm;
	private IEventBFormText formText;
	

	/**
	 * Constructor.
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
	 * 
	 * @see org.eclipse.ui.part.IPage#createControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createControl(Composite parent) {
		final FormToolkit toolkit = new FormToolkit(parent.getDisplay());
		scrolledForm = toolkit.createScrolledForm(parent);

		if (proofState != null)
			scrolledForm.setText(proofState.getPSStatus().getElementName());

		final Composite body = scrolledForm.getBody();
		body.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		final GridLayout gl = new GridLayout();
		body.setLayout(gl);

		formText = createEventBFormText(toolkit.createFormText(body, true));
		if (proofState != null)
			setFormText(proofState.getPSStatus(), new NullProgressMonitor());

		toolkit.paintBordersFor(body);
		scrolledForm.reflow(true);
	}

	/**
	 * Set the formText according to the current prSequent.
	 * 
	 * @param prSequent
	 *            the current prSequent
	 */
	// TODO share code with ASTConverter
	// Should there be a pretty-print form for every RodinElement?
	void setFormText(IPSStatus prSequent, IProgressMonitor monitor) {
		try {
			final StringBuilder formBuilder = new StringBuilder("<form>");
			final ElementDescRegistry descRegistry = ElementDescRegistry
					.getInstance();
			final IPOSource[] sources = prSequent.getPOSequent().getSources();
			for (IPOSource source : sources) {
				final IRodinElement element = source.getSource();
				final String id = element.getHandleIdentifier();
				if (ProofInformationUtils.DEBUG) {
					ProofInformationUtils.debug("id: " + id);
					ProofInformationUtils.debug("Find: " + element);
				}
				formBuilder.append(ProofInformationRootItem.getInfo(element));
				if (element instanceof IEvent) {
					appendEventInfo(formBuilder, (IEvent) element, id);
				} else {

					final ElementDesc elementDesc = descRegistry
							.getElementDesc(element);
					for (IElementRelationship rel : elementDesc
							.getChildRelationships()) {
						final IImplicitChildProvider icp = rel
								.getImplicitChildProvider();

						if (!(element instanceof IInternalElement)) {
							continue;
						}

						final IInternalElement ie = (IInternalElement) element;
						final List<IInternalElement> children = new ArrayList<IInternalElement>();
						if (icp != null) {
							children.addAll(icp.getImplicitChildren(ie));
						}
						final IInternalElementType<?> childType = rel
								.getChildType();
						children.addAll(Arrays.asList(ie
								.getChildrenOfType(childType)));
						final IElementDesc childDesc = descRegistry
								.getElementDesc(childType);
						final String prefix = childDesc.getPrefix();
						if (prefix.length() != 0 && !children.isEmpty()) {
							appendPrefixOrSuffix(formBuilder, prefix);
						}
						// Display IIdentifierElements in the same row
						if (!children.isEmpty()
								&& children.get(0) instanceof IIdentifierElement) {
							formBuilder.append(ProofInformationListItem
									.getInfo(children, subLevel));
						} else {
							for (IInternalElement child : children) {
								formBuilder.append(ProofInformationListItem
										.getInfo(child, subLevel));
							}
						}
					}
					final String suffix = elementDesc.getChildrenSuffix();
					if (suffix.length() != 0) {
						appendPrefixOrSuffix(formBuilder, suffix);
					}
				}
			}
			formBuilder.append("</form>");
			formText.getFormText().setText(formBuilder.toString(), true, false);
			formText.getFormText().addHyperlinkListener(new HyperlinkAdapter() {

				/*
				 * (non-Javadoc)
				 * 
				 * @see
				 * org.eclipse.ui.forms.events.HyperlinkAdapter#linkActivated
				 * (org.eclipse.ui.forms.events.HyperlinkEvent)
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

	private void appendEventInfo(StringBuilder sb, IEvent event, String id)
			throws RodinDBException {
		final IRefinesEvent[] refinesClauses = event.getRefinesClauses();
		final List<IParameter> params = getVisibleChildrenOfType(event,
				IParameter.ELEMENT_TYPE);
		final List<IGuard> guards = getVisibleChildrenOfType(event,
				IGuard.ELEMENT_TYPE);
		final IWitness[] witnesses = event.getWitnesses();
		final List<IAction> actions = getVisibleChildrenOfType(event,
				IAction.ELEMENT_TYPE);

		if (refinesClauses.length != 0) {
			appendPrefixOrSuffix(sb, "REFINES");
			for (IRefinesEvent refinesClause : refinesClauses) {
				sb.append(getInfo(refinesClause, subLevel));
			}
		}
		if (params.size() != 0) {
			appendPrefixOrSuffix(sb, "ANY");
			sb.append(getInfo(params, subLevel));
			appendPrefixOrSuffix(sb, "WHERE");
		} else if (guards.size() != 0) {
			appendPrefixOrSuffix(sb, "WHEN");
		} else {
			appendPrefixOrSuffix(sb, "BEGIN");
		}
		for (IGuard guard : guards) {
			sb.append(getInfo(guard, subLevel));
		}
		if (witnesses.length != 0) {
			appendPrefixOrSuffix(sb, "WITH");
			for (IWitness witness : witnesses) {
				sb.append(getInfo(witness, subLevel));
			}
		}
		if (actions.size() != 0) {
			appendPrefixOrSuffix(sb, "THEN");
		}
		for (IAction action : actions) {
			sb.append(getInfo(action, subLevel));
		}
		appendPrefixOrSuffix(sb, "END");
	}

	private static void appendPrefixOrSuffix(StringBuilder strb, String str) {
		strb.append("<li style=\"text\" value=\"\" bindent = \"20\">");
		strb.append("<b>" + str + "</b></li>");
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

	@Override
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

		final Display display = scrolledForm.getDisplay();
		
		display.syncExec(new Runnable() {
			@Override
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
							final IPSStatus prSequent = proofState.getPSStatus();
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
							final IPSStatus prSequent = proofState.getPSStatus();
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