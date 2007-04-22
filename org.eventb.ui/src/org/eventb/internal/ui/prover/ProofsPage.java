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
import org.eventb.core.pm.IUserSupport;

/**
 * @author htson
 *         <p>
 *         This is the implementation of the Proof Page in the Prover UI Editor.
 */
public class ProofsPage extends FormPage {

	// ID, title and the tab-title
	public static final String PAGE_ID = "Selected Hypotheses"; //$NON-NLS-1$

	public static final String PAGE_TITLE = "Selected Hypotheses"; //$NON-NLS-1$

	public static final String PAGE_TAB_TITLE = "State"; //$NON-NLS-1$

	private static final int DEFAULT_HEIGHT = 400;

	private static final int DEFAULT_WIDTH = 400;

	private HypothesisComposite hypComposite;

	IUserSupport userSupport;

	Composite body;

	boolean layouting = false;

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
		form.setText(PAGE_TITLE);
		body = form.getBody();

		control = new Composite(body, SWT.NULL);
		control.setLayout(new GridLayout());
		if (ProverUIUtils.DEBUG) {
			control.setBackground(display.getSystemColor(SWT.COLOR_BLUE));
		}
		else {
			control.setBackground(form.getBackground());
		}

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
			layouting = true;
			autoLayout();
			layouting = false;
		}

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

}