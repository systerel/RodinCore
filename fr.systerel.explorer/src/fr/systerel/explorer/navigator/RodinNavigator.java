/*******************************************************************************
 * Copyright (c) 2008 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License  v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
  *******************************************************************************/

package fr.systerel.explorer.navigator;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.CoolBar;
import org.eclipse.swt.widgets.CoolItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.IWorkingSet;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.WorkingSetFilterActionGroup;
import org.eclipse.ui.navigator.CommonNavigator;
import org.eclipse.ui.navigator.CommonViewer;
import org.eclipse.ui.navigator.CommonViewerSorter;
import org.eventb.internal.ui.TimerText;
import org.eventb.ui.EventBUIPlugin;
import org.eventb.ui.IEventBSharedImages;

import fr.systerel.explorer.model.ModelController;
import fr.systerel.explorer.navigator.filters.DischargedFilter;
import fr.systerel.explorer.navigator.filters.HideNoneRodinFilter;
import fr.systerel.explorer.navigator.filters.ObligationTextFilter;
import fr.systerel.explorer.navigator.filters.WorkingSetProjectFilter;

/**
 * The Navigator for the CommonNavigator framework.
 * There are some customizations for the menus 
 * and the working sets.
 *
 */
public class RodinNavigator extends CommonNavigator implements IPropertyChangeListener {
	
	Text filterText;
	ToolItem discharge;

	public RodinNavigator(){
		controller = new ModelController(this);
	}
	
	/**
	 * The Controller of the internal model.
	 */
	@SuppressWarnings("unused")
	private ModelController controller;
	
	
//	/**
//	 * Take the <code>RodinDB</code> as InitialInput and not the <code>Workspace</code>.
//	 *
//	 */
//	@Override
//	protected IAdaptable getInitialInput() {
//		this.getCommonViewer().refresh();
//		return RodinCore.getRodinDB();
//	}

	/**
	 * Add some custom items for filtering to the toolbar.
	 */
	@Override
	public void createPartControl(Composite parent) {
		super.createPartControl(parent);
		
		FormLayout layout = new FormLayout();
		parent.setLayout(layout);
		CoolBar coolBar = new CoolBar(parent, SWT.FLAT);
		FormData coolData = new FormData();
		coolData.left = new FormAttachment(0);
		coolData.right = new FormAttachment(100);
		coolData.top = new FormAttachment(0);
		coolBar.setLayoutData(coolData);

		createText(coolBar);
		createToolItem(coolBar);

		FormData textData = new FormData();
		textData.left = new FormAttachment(0);
		textData.right = new FormAttachment(100);
		textData.top = new FormAttachment(coolBar);
		textData.bottom = new FormAttachment(100);
		getCommonViewer().getControl().setLayoutData(textData);
		WorkingSetFilterActionGroup group = new WorkingSetFilterActionGroup(this.getSite().getShell(), this);
		group.fillContextMenu(this.getViewSite().getActionBars().getMenuManager());
		
		//add the filters here
		//those filters are selected by default and can't be chosen
		//via the "customize view" dialog
		getCommonViewer().addFilter(new DischargedFilter());
		getCommonViewer().addFilter(new ObligationTextFilter());
		getCommonViewer().addFilter(new HideNoneRodinFilter());
		getCommonViewer().addFilter(new WorkingSetProjectFilter());
			
	}
	
	CoolItem createText(CoolBar coolBar) {
		filterText = new Text(coolBar, SWT.SINGLE | SWT.BORDER);
		new TimerText(filterText, 1000) {

			@Override
			protected void response() {
				ObligationTextFilter.text = filterText.getText();
				getViewSite().getShell().getDisplay().asyncExec(new Runnable(){
					public void run() {
						CommonViewer viewer = getCommonViewer();
						Control ctrl = viewer.getControl();
						if (ctrl != null && !ctrl.isDisposed()) {
							Object[] expanded = viewer.getExpandedElements();
							viewer.refresh();
							viewer.setExpandedElements(expanded);
						}
				}});
			}

		};
		filterText.pack();
		Point size = filterText.getSize();
		CoolItem item = new CoolItem(coolBar, SWT.NONE);
		item.setControl(filterText);
		Point preferred = item.computeSize(size.x, size.y);
		item.setPreferredSize(preferred);
		return item;
	}

	CoolItem createToolItem(CoolBar coolBar) {
		ToolBar toolBar = new ToolBar(coolBar, SWT.FLAT);
		discharge = new ToolItem(toolBar, SWT.CHECK);
		ImageRegistry registry = EventBUIPlugin.getDefault().getImageRegistry();
		discharge.setImage(registry.get(IEventBSharedImages.IMG_DISCHARGED));
		discharge.addSelectionListener(new SelectionListener() {

			public void widgetSelected(SelectionEvent e) {
				DischargedFilter.active = discharge.getSelection();
				getViewSite().getShell().getDisplay().asyncExec(new Runnable(){
					public void run() {
						CommonViewer viewer = getCommonViewer();
						Control ctrl = viewer.getControl();
						if (ctrl != null && !ctrl.isDisposed()) {
							Object[] expanded = viewer.getExpandedElements();
							viewer.refresh();
							viewer.setExpandedElements(expanded);
						}
				}});
			}

			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
		});
		

		toolBar.pack();
		Point size = toolBar.getSize();
		CoolItem item = new CoolItem(coolBar, SWT.NONE);
		item.setControl(toolBar);
		Point preferred = item.computeSize(size.x, size.y);
		item.setPreferredSize(preferred);
		return item;
	}

	/**
	 * React to changes in working set selection:
	 * Set the input for the ContentProviders accordingly.
	 */
	public void propertyChange(final PropertyChangeEvent event) {
		final Object root;
		if (event.getNewValue() instanceof IWorkingSet) {
			root =  event.getNewValue();
		}
		else {
			//set to initial input
			root = getSite().getPage().getInput();
		}
		
		getViewSite().getShell().getDisplay().asyncExec(new Runnable(){
			public void run() {
				CommonViewer viewer = getCommonViewer();
				Control ctrl = viewer.getControl();
				if (ctrl != null && !ctrl.isDisposed()) {
					Object[] expanded = viewer.getExpandedElements();
					viewer.setInput(root);
					viewer.setExpandedElements(expanded);
				}
		}});
		
	}
	
}
