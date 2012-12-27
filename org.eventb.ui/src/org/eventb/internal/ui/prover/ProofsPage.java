/*******************************************************************************
 * Copyright (c) 2005, 2012 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - Added a constant for the user support manager
 *     Systerel - used EventBSharedColor
 *     Systerel - computed size of hypothesis composite
 *     Systerel - redirected dialog opening
 *******************************************************************************/
package org.eventb.internal.ui.prover;

import static org.eventb.internal.ui.utils.Messages.dialogs_po_Deleted;
import static org.eventb.internal.ui.utils.Messages.title_po_Deleted;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.swt.widgets.ScrollBar;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eventb.core.EventBPlugin;
import org.eventb.core.pm.IProofState;
import org.eventb.core.pm.IProofStateDelta;
import org.eventb.core.pm.IUserSupport;
import org.eventb.core.pm.IUserSupportDelta;
import org.eventb.core.pm.IUserSupportManager;
import org.eventb.core.pm.IUserSupportManagerChangedListener;
import org.eventb.core.pm.IUserSupportManagerDelta;
import org.eventb.internal.ui.EventBSharedColor;
import org.eventb.internal.ui.UIUtils;
import org.rodinp.core.RodinDBException;

/**
 * @author htson
 *         <p>
 *         This is the implementation of the Proof Page in the Prover UI Editor.
 */
public class ProofsPage extends FormPage implements
		IUserSupportManagerChangedListener {

	private static final IUserSupportManager USM = EventBPlugin
			.getUserSupportManager();

	// ID, title and the tab-title
	public static final String PAGE_ID = "Selected Hypotheses"; //$NON-NLS-1$

	public static final String PAGE_TITLE = "No Current Obligation"; //$NON-NLS-1$

	public static final String PAGE_TAB_TITLE = PAGE_ID; //$NON-NLS-1$

	private static final int DEFAULT_HEIGHT = 400;

	private static final int DEFAULT_WIDTH = 400;

	private HypothesisComposite hypComposite;

	IUserSupport userSupport;

	Composite body;

	boolean layouting = false;

	private Composite tmpComp;
	
	private Composite control;

	/**
	 * Constructor.
	 * <p>
	 * 
	 * @param editor
	 *            the Prover UI Editor contains this page.
	 */
	public ProofsPage(ProverUI editor) {
		super(editor, PAGE_ID, PAGE_TAB_TITLE); //$NON-NLS-1$
		userSupport = editor.getUserSupport();
		USM.addChangeListener(this);
	}

	@Override
	public void dispose() {
		hypComposite.dispose();
		USM.removeChangeListener(this);
		super.dispose();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.forms.editor.FormPage#createFormContent(org.eclipse.ui.forms.IManagedForm)
	 */
	@Override
	protected void createFormContent(IManagedForm managedForm) {
		super.createFormContent(managedForm);
		final ScrolledForm form = managedForm.getForm();
		form.setText(PAGE_TITLE);
		body = form.getBody();

		control = new Composite(body, SWT.NULL);
		final GridLayout gridLayout = new GridLayout();
		gridLayout.horizontalSpacing = 0;
		gridLayout.verticalSpacing = 0;
		gridLayout.marginHeight = 0;
		gridLayout.marginWidth = 0;
		
		control.setLayout(gridLayout);

		if (ProverUIUtils.DEBUG) {
			control.setBackground(EventBSharedColor
					.getSystemColor(SWT.COLOR_BLUE));
		}
		else {
			control.setBackground(form.getBackground());
		}

		tmpComp = new Composite(control, SWT.NULL);
		if (ProverUIUtils.DEBUG) {
			tmpComp.setBackground(EventBSharedColor
					.getSystemColor(SWT.COLOR_DARK_GRAY));
		}
		else {
			tmpComp.setBackground(form.getBackground());
		}
		final GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
		gridData.heightHint = 0;
		gridData.widthHint = 0;
		tmpComp.setLayoutData(gridData);

		hypComposite = new SelectedHypothesisComposite(userSupport, form,
				(ProverUI) this.getEditor());
		hypComposite.createControl(control);
		hypComposite.getControl().setLayoutData(
				new GridData(SWT.FILL, SWT.FILL, false, false));

		body.setLayout(new ProofsPageLayout());
		setPartName();
	}

	/**
	 * @author htson
	 *         <p>
	 *         A special layout class for the Proofs Page to the selected hypotheses
	 *         into the client area of the page.
	 * 
	 */
	protected class ProofsPageLayout extends Layout {

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.swt.widgets.Layout#computeSize(org.eclipse.swt.widgets.Composite,
		 *      int, int, boolean)
		 */
		@Override
		protected Point computeSize(Composite composite, int wHint, int hHint,
				boolean flushCache) {
			ScrolledForm form = ProofsPage.this.getManagedForm().getForm();
			Rectangle bounds = form.getBody().getBounds();

			return new Point(bounds.x, bounds.y);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.swt.widgets.Layout#layout(org.eclipse.swt.widgets.Composite,
		 *      boolean)
		 */
		@Override
		protected void layout(Composite composite, boolean flushCache) {
			// Do nothing if already layouting (avoid looping)
			if (layouting == true)
				return;
			autoLayout();
		}

	}

	void autoLayout() {
		layouting = true;
		ScrolledForm form = this.getManagedForm().getForm();
		Rectangle original = form.getBody().getBounds();

		if (ProverUIUtils.DEBUG) {
			ProverUIUtils.debug("*********************");
			ProverUIUtils.debug("Client area height "
					+ form.getClientArea().height);
			ProverUIUtils.debug("Client area width "
					+ form.getClientArea().width);
		}
		// -1 in totalHeight to avoid the vertical scrollbar in the beginning???
		int totalHeight = form.getClientArea().height - original.y - 1;
		int totalWidth = form.getClientArea().width;

		ScrollBar horizontal = form.getHorizontalBar();
		ScrollBar vertical = form.getVerticalBar();

		if (horizontal != null && horizontal.isVisible()) {
			totalHeight += horizontal.getSize().y;
		}

		if (vertical != null && vertical.isVisible()) {
			totalWidth += vertical.getSize().x;
		}

		final Point selectedSize = hypComposite.getControl().computeSize(
				totalWidth, SWT.DEFAULT);

		if (totalHeight < 1) {
			totalHeight = DEFAULT_HEIGHT;
			totalWidth = DEFAULT_WIDTH;
		}

		final int hypHeigth = (selectedSize.y < totalHeight) ? selectedSize.y
				: totalHeight;

		control.setBounds(0, 0, totalWidth, totalHeight);
		control.layout(true);
		hypComposite.setSize(selectedSize.x, hypHeigth);

		if (ProverUIUtils.DEBUG) {
			ProverUIUtils.debug("Desired Height " + selectedSize.y);
			ProverUIUtils.debug("Total Width " + totalWidth);
			ProverUIUtils.debug("Total Height " + totalHeight);
		}
		layouting = false;
	}

	@Override
	public void userSupportManagerChanged(IUserSupportManagerDelta delta) {
		if (ProverUIUtils.DEBUG)
			ProverUIUtils.debug("Begin User Support Manager Changed");

		final ScrolledForm scrolledForm = this.getManagedForm().getForm();
		// Do nothing if the managed form is disposed.
		if (scrolledForm.isDisposed())
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
			// Pop up dialog to notify user.
		}

		// This case should NOT happened.
		if (kind == IUserSupportDelta.ADDED) {
			if (ProverUIUtils.DEBUG)
				ProverUIUtils
						.debug("Error: Delta said that the user support is added");
			return; // Do nothing
		}

		Display display = scrolledForm.getDisplay();

		display.syncExec(new Runnable() {
			@Override
			public void run() {
				// Do nothing if the managed form is disposed.
				if (scrolledForm.isDisposed())
					return;

				// Handle the case where the user support has changed.
				if (kind == IUserSupportDelta.CHANGED) {
					int usFlags = affectedUserSupport.getFlags();
					
					if ((usFlags & IUserSupportDelta.F_CURRENT) != 0) {
						setPartName();
					}
					
					if ((usFlags & IUserSupportDelta.F_STATE) != 0) {
						// If the changes occurs in some proof states.
						IProofState proofState = userSupport.getCurrentPO();
						// Trying to get the change for the current proof state.
						final IProofStateDelta affectedProofState = ProverUIUtils
								.getProofStateDelta(affectedUserSupport,
										proofState);
						if (affectedProofState != null) {
							// If there are some changes
							int psKind = affectedProofState.getKind();
							if (psKind == IProofStateDelta.ADDED) {
								// This case should not happened
								if (ProverUIUtils.DEBUG)
									ProverUIUtils
											.debug("Error: Delta said that the proof state is added");
								return;
							}

							if (psKind == IProofStateDelta.REMOVED) {
								UIUtils.showInfo(title_po_Deleted,
										dialogs_po_Deleted);
								try {
									userSupport.setCurrentPO(null, new NullProgressMonitor());
								} catch (RodinDBException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								return;
							}
						}
					}
				}
			}
		});

		if (ProverUIUtils.DEBUG)
			ProverUIUtils.debug("End User Support Manager Changed");
	}

	protected void setPartName() {
		IProofState currentPO = userSupport.getCurrentPO();
		ScrolledForm form = this.getManagedForm().getForm();
		if (currentPO != null) {
			String name = currentPO.getPSStatus().getElementName();
			form.setText(name);
		} else {
			form.setText(PAGE_TITLE);
		}
	}

}