/*******************************************************************************
 * Copyright (c) 2008, 2014 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *     ETH Zurich - adapted to org.rodinp.keyboard.ui
 *******************************************************************************/
package org.eventb.internal.ui.proofSkeletonView;

import static org.eventb.internal.ui.prooftreeui.ProofTreeUIUtils.setupCommentTooltip;
import static org.rodinp.keyboard.ui.preferences.PreferenceConstants.RODIN_MATH_FONT;

import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.DecoratingLabelProvider;
import org.eclipse.jface.viewers.ILabelDecorator;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.IFormPart;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.handlers.CollapseAllHandler;
import org.eclipse.ui.handlers.ExpandAllHandler;
import org.eclipse.ui.handlers.IHandlerService;

/**
 * Master part of the MasterDetailsBlock for the proof skeleton viewer.
 * 
 * @author Nicolas Beauger
 * 
 */
public class PrfSklMasterPart implements IFormPart {

	private final TreeViewer viewer;
	private IManagedForm managedForm;

	// listener to the tree selection
	private final ISelectionChangedListener treeListener = new ISelectionChangedListener() {

		@Override
		public void selectionChanged(SelectionChangedEvent event) {
			fireSelectionChanged(event);
		}

	};
	private CollapseAllHandler collapseHandler;
	private ExpandAllHandler expandHandler;

	void fireSelectionChanged(SelectionChangedEvent event) {
		managedForm.fireSelectionChanged(this, event.getSelection());
	}

	/**
	 * Constructor.
	 * 
	 * @param parent
	 *            the parent Composite.
	 * @param site
	 *            the part site (for registering the context menu)
	 */
	public PrfSklMasterPart(Composite parent, IWorkbenchPartSite site) {
		this.viewer = new TreeViewer(parent);
		setFont(JFaceResources.getFont(RODIN_MATH_FONT));
		viewer.setContentProvider(new PrfSklContentProvider());
		final ILabelDecorator decorator = PlatformUI.getWorkbench()
				.getDecoratorManager().getLabelDecorator();
		viewer.setLabelProvider(new DecoratingLabelProvider(
				new PrfSklLabelProvider(), decorator));
		viewer.addSelectionChangedListener(treeListener);
		setupCommentTooltip(viewer);
		createContextMenu(site);
		activateHandlers((IHandlerService) site.getService(IHandlerService.class));
	}

	private void createContextMenu(IWorkbenchPartSite site) {
		final Control control = viewer.getControl();
		final MenuManager menuManager = new MenuManager();
		final Menu menu = menuManager.createContextMenu(control);
		control.setMenu(menu);
		site.registerContextMenu(menuManager, viewer);
		site.setSelectionProvider(viewer);
	}

	public void setFont(Font font) {
		if (viewer == null || viewer.getControl().isDisposed())
			return;
		viewer.getControl().setFont(font);
	}

	@Override
	public boolean setFormInput(Object input) {
		if (input instanceof IViewerInput) {
			setViewerInput((IViewerInput) input);
			return true;
		}
		return false;
	}

	void setViewerInput(IViewerInput input) {
		if (viewer != null) {
			viewer.setInput(input);
			viewer.getTree().setSelection(viewer.getTree().getItem(0));
			treeListener.selectionChanged(new SelectionChangedEvent(viewer,
					viewer.getSelection()));
		}
	}

	@Override
	public void setFocus() {
		// Do nothing
	}

	@Override
	public void refresh() {
		viewer.refresh();
	}

	@Override
	public boolean isStale() {
		return false;
	}

	@Override
	public boolean isDirty() {
		return false;
	}

	@Override
	public void initialize(IManagedForm form) {
		this.managedForm = form;
	}

	@Override
	public void dispose() {
		collapseHandler.dispose();
		expandHandler.dispose();
		viewer.removeSelectionChangedListener(treeListener);
		viewer.getTree().dispose();
		viewer.getControl().dispose();
	}

	@Override
	public void commit(boolean onSave) {
		// Do nothing
	}

	/**
	 * Activates the handlers using the given handler service.
	 * 
	 * @param handlerService
	 *            the handler service to use for handler activation
	 */
	private void activateHandlers(IHandlerService handlerService) {
		collapseHandler = new CollapseAllHandler(viewer);
		handlerService.activateHandler(CollapseAllHandler.COMMAND_ID,
				collapseHandler);
		
		expandHandler = new ExpandAllHandler(viewer);
		handlerService.activateHandler(ExpandAllHandler.COMMAND_ID,
				expandHandler);
	}
}