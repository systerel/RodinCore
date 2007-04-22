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

package org.eventb.internal.ui.prover;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.util.PropertyChangeEvent;
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
import org.eventb.core.pm.IProofState;
import org.eventb.core.pm.IProofStateDelta;
import org.eventb.core.pm.IUserSupport;
import org.eventb.core.pm.IUserSupportDelta;
import org.eventb.core.pm.IUserSupportManagerChangedListener;
import org.eventb.core.pm.IUserSupportManagerDelta;
import org.eventb.internal.ui.preferences.PreferenceConstants;
import org.eventb.internal.ui.proofcontrol.ProofControlUtils;
import org.eventb.ui.EventBUIPlugin;

/**
 * @author htson
 *         <p>
 *         This is the implementation of the Proof Page in the Prover UI Editor.
 */
public class ProofsPage extends FormPage implements
		IUserSupportManagerChangedListener {

	// ID, title and the tab-title
	public static final String PAGE_ID = "Selected Hypotheses"; //$NON-NLS-1$

	public static final String PAGE_TITLE = "Selected Hypotheses";

	public static final String PAGE_TAB_TITLE = "State";

	private static final int DEFAULT_HEIGHT = 400;

	private static final int DEFAULT_WIDTH = 400;

	private HypothesisComposite hypComposite;

	IUserSupport userSupport;

	Composite body;

	boolean layouting = false;
	
	Action layoutAction;

	private Composite tmpComp;
	
	private Composite control;

	Display display = Display.getDefault();
	
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
	}

	@Override
	public void dispose() {
		hypComposite.dispose();
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
		ScrolledForm form = managedForm.getForm();
		form.setText(PAGE_TITLE); //$NON-NLS-1$
		body = form.getBody();

		control = new Composite(body, SWT.NULL);
		control.setLayout(new GridLayout());
		if (ProverUIUtils.DEBUG) {
			control.setBackground(display.getSystemColor(SWT.COLOR_BLUE));
		}
		else {
			control.setBackground(form.getBackground());
		}
//		control.setSize(400, 600);
		
		tmpComp = new Composite(control, SWT.NULL);
		if (ProverUIUtils.DEBUG) {
			tmpComp.setBackground(display.getSystemColor(SWT.COLOR_CYAN));
		}
		else {
			tmpComp.setBackground(form.getBackground());
		}
		GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
		gridData.heightHint = 0;
		gridData.widthHint = 0;
		tmpComp.setLayoutData(gridData);
		
		hypComposite = new SelectedHypothesisComposite(userSupport, form);
		hypComposite.createControl(control);
		hypComposite.getControl().setLayoutData(
				new GridData(SWT.FILL, SWT.FILL, false, false));

		body.setLayout(new ProofsPageLayout());

		createToolBarActions(managedForm);
	}

	/**
	 * Create the Toolbar actions.
	 * <p>
	 * 
	 * @param managedForm
	 *            The managed form contains the Toolbar.
	 */
	protected void createToolBarActions(IManagedForm managedForm) {
		final IPreferenceStore store = EventBUIPlugin.getDefault()
				.getPreferenceStore();

		layoutAction = new Action("auto", Action.AS_CHECK_BOX) {
			@Override
			public void run() {
				boolean checked = this.isChecked();
				store.setValue(PreferenceConstants.P_PROOFPAGE_AUTOLAYOUT,
						checked);
				if (checked) {
					ProofsPage.this.body.layout();
				}
			}
		};

		layoutAction.setChecked(store
				.getBoolean(PreferenceConstants.P_PROOFPAGE_AUTOLAYOUT));
		layoutAction.setToolTipText("Automatically layout");

		ScrolledForm form = managedForm.getForm();

		form.getToolBarManager().add(layoutAction);
		form.updateToolBar();
	}

	/**
	 * @author htson
	 *         <p>
	 *         A special layout class for the Proofs Page to fit the sections
	 *         into the client area of the page. Depending on the state of the
	 *         auto layout button, the sections are resize to the best display
	 *         or not
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
			layouting = true;
			if (layoutAction.isChecked())
				autoLayout();
			else
				scaleLayout();
			layouting = false;
		}

	}

	public void userSupportManagerChanged(IUserSupportManagerDelta delta) {
		// Do nothing if the page is disposed.
		if (this.getManagedForm().getForm().isDisposed())
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
			if (ProverUIUtils.DEBUG)
				ProverUIUtils
						.debug("Error: Delta said that the user Support is added");
			return; // Do nothing
		}

		display.syncExec(new Runnable() {
			public void run() {
				// Handle the case where the user support has changed.
				if (kind == IUserSupportDelta.CHANGED) {
					int flags = affectedUserSupport.getFlags();
					if ((flags & IUserSupportDelta.F_CURRENT) != 0) {
						// The current proof state is changed, reupdate the page
						IProofState ps = userSupport.getCurrentPO();
						if (ps != null) {
//							Do nothing
						} else {
//							Do nothing
						}
						ProofsPage.this.getManagedForm().getForm().reflow(true);
						return;
					} 
					
					if ((flags & IUserSupportDelta.F_STATE) != 0) {
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
								if (ProofControlUtils.DEBUG)
									ProofControlUtils
											.debug("Error: Delta said that the proof state is added");
								return;
							}

							if (psKind == IProofStateDelta.REMOVED) {
								// Do nothing in this case, this will be handled
								// by the main proof editor.
								return;
							}
							
							if (psKind == IProofStateDelta.CHANGED) {
								// If there are some changes to the proof state.
								int psFlags = affectedProofState.getFlags();

								if ((psFlags & IProofStateDelta.F_NODE) != 0
										|| (psFlags & IProofStateDelta.F_PROOFTREE) != 0) {
									// Do nothing
								}

								ProofsPage.this.getManagedForm().getForm()
										.reflow(true);
							}
						}
					}
				}
			}
		});
	}

	void autoLayout() {
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

		int selectedHeight = hypComposite.getControl().computeSize(totalWidth,
				SWT.DEFAULT).y;

		if (ProverUIUtils.DEBUG) {
			ProverUIUtils.debug("Desired Height " + selectedHeight);
		}
		if (totalHeight < 1) {
			totalHeight = DEFAULT_HEIGHT;
			totalWidth = DEFAULT_WIDTH;
		}
		if (selectedHeight < totalHeight) {
			if (ProverUIUtils.DEBUG) {
				ProverUIUtils.debug("Total Width " + totalWidth);
				ProverUIUtils.debug("Total Height " + totalHeight);
			}
			control.setBounds(0, 0, totalWidth, totalHeight);
			control.layout(true);
			hypComposite.reflow(true);
			form.reflow(true);
		} else {
			control.setBounds(0, 0, totalWidth, totalHeight);
			hypComposite.setBounds(0, 0, totalWidth, totalHeight);
			hypComposite.reflow(true);
		}
	}

	void scaleLayout() {
		hypComposite.reflow(true);
//		Rectangle rect = sashForm.computeTrim(0, 0, totalWidth, totalHeight);
//		sashForm.setBounds(rect);
	}

	public void propertyChange(PropertyChangeEvent event) {
		if (event.getProperty().equals(
				PreferenceConstants.P_PROOFPAGE_AUTOLAYOUT)) {
			Object newValue = event.getNewValue();
			assert newValue instanceof Boolean || newValue instanceof String;
			if (newValue instanceof String)
				layoutAction.setChecked(((String) newValue)
						.compareToIgnoreCase("true") == 0);
			else {
				layoutAction.setChecked((Boolean) newValue);
			}
			// Run layout action
			layoutAction.run();
		}

	}

}