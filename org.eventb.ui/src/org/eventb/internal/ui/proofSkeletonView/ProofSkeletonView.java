/*******************************************************************************
 * Copyright (c) 2008, 2014 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.ui.proofSkeletonView;

import static org.rodinp.keyboard.ui.preferences.PreferenceConstants.RODIN_MATH_FONT;

import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.forms.ManagedForm;
import org.eclipse.ui.handlers.IHandlerService;
import org.eclipse.ui.part.ViewPart;

/**
 * ViewPart for displaying proof skeletons.
 * 
 * @author Nicolas Beauger
 * 
 */
public class ProofSkeletonView extends ViewPart implements IPropertyChangeListener {

	public static boolean DEBUG;
	
	protected PrfSklMasterDetailsBlock masterDetailsBlock;
	private InputManager selManager;
	private ManagedForm managedForm;
	
	@Override
	public void createPartControl(Composite parent) {
		parent.setLayout(new FillLayout());
		managedForm = new ManagedForm(parent);
		masterDetailsBlock = new PrfSklMasterDetailsBlock();
		masterDetailsBlock.createContent(managedForm);
		masterDetailsBlock.activateHandlers((IHandlerService) getSite()
				.getService(IHandlerService.class));

		selManager = new InputManager(this);
		selManager.register();
		
		addContextMenu();
		JFaceResources.getFontRegistry().addListener(this);
	}

	private void addContextMenu() {
		final Viewer viewer = masterDetailsBlock.getViewer();
		final Control control = viewer.getControl();
		final MenuManager menuManager = new MenuManager();
		final Menu menu = menuManager.createContextMenu(control);
		control.setMenu(menu);
		getSite().registerContextMenu(menuManager, viewer);
		getSite().setSelectionProvider(viewer);
	}

	@Override
	public void setFocus() {
		managedForm.getForm().setFocus();
	}

	@Override
	public void dispose() {
		getSite().setSelectionProvider(null);
		selManager.unregister();
		JFaceResources.getFontRegistry().removeListener(this);
		managedForm.dispose();
		super.dispose();
	}

	/**
	 * Expand or collapse the master part tree viewer.
	 * 
	 * @param expand
	 *            expands all when <code>true</code>; collapses all when
	 *            <code>false</code>.
	 */
	public void changeExpansionState(boolean expand) {
		if (expand) {
			masterDetailsBlock.getViewer().expandAll();
		} else {
			masterDetailsBlock.getViewer().collapseAll();
		}
	}

	public void switchOrientation() {
		masterDetailsBlock.switchOrientation();
	}

	public void setInput(IViewerInput input) {
		if (managedForm != null) {
			if(!managedForm.getForm().isDisposed()) {
				managedForm.setInput(input);
			}
		}
		setTitleToolTip(input.getTitleTooltip());
	}

	@Override
	public void propertyChange(PropertyChangeEvent event) {
		if (event.getProperty().equals(RODIN_MATH_FONT)) {
			final Font font = JFaceResources.getFont(RODIN_MATH_FONT);
			masterDetailsBlock.setFont(font);
		}
		
	}

}
