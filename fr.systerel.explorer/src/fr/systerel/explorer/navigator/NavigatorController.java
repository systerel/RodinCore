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

import java.util.ArrayList;

import org.eclipse.jface.resource.ImageRegistry;
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
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.navigator.CommonViewer;
import org.eventb.internal.ui.TimerText;
import org.eventb.ui.EventBUIPlugin;
import org.eventb.ui.IEventBSharedImages;

import fr.systerel.explorer.model.ModelController;
import fr.systerel.explorer.navigator.filters.DischargedFilter;
import fr.systerel.explorer.navigator.filters.ObligationTextFilter;

/**
 * This class is used to set up the Navigator. It sets up the tool bar in the
 * navigator and instantiates the <code>ModelController</code>.
 * 
 */
public class NavigatorController {
	
	private static ArrayList<CommonViewer> registeredViewers = new ArrayList<CommonViewer>();

	public static void setUpNavigator(CommonViewer viewer) {
		//only treat viewers that have not been set up yet.
		if (!registeredViewers.contains(viewer)) {
			ModelController.createInstance(viewer);
			setUpFiltersToolBar(viewer);
			registeredViewers.add(viewer);
		}
	}

	
	/**
	 * Creates and formats the toolbar for the filters
	 */
	private static void setUpFiltersToolBar(CommonViewer viewer) {
		Composite container = viewer.getTree().getParent();
		CoolBar coolBar = new CoolBar(container, SWT.FLAT);
		coolBar.setLocation(0, 0);
		container.setLayout(new FormLayout());
		FormData coolData = new FormData();
		coolData.left = new FormAttachment(0);
		coolData.right = new FormAttachment(100);
		coolData.top = new FormAttachment(0);
		coolBar.setLayoutData(coolData);
		createText(coolBar, viewer);
		createToolItem(coolBar, viewer);
		
		FormData textData = new FormData();
		textData.left = new FormAttachment(0);
		textData.top = new FormAttachment(coolBar);
		textData.right = new FormAttachment(100);
		textData.bottom = new FormAttachment(100);
		viewer.getControl().setLayoutData(textData);

		Point size = container.getSize();
		container.pack(true);
		container.setSize(size);
		
	
		
	}
	
	private static CoolItem createText(CoolBar cool, final CommonViewer viewer) {
		final Text filterText = new Text(cool, SWT.SINGLE | SWT.BORDER);
		new TimerText(filterText, 1000) {

			@Override
			protected void response() {
				ObligationTextFilter.text = filterText.getText();
				PlatformUI.getWorkbench().getDisplay().asyncExec(
						new Runnable() {
							public void run() {
								Control ctrl = viewer.getControl();
								if (ctrl != null && !ctrl.isDisposed()) {
									Object[] expanded = viewer
											.getExpandedElements();
									viewer.refresh(false);
									viewer.setExpandedElements(expanded);
								}
							}
						});
			}

		};
		filterText.pack();
		Point size = filterText.getSize();
		CoolItem item = new CoolItem(cool, SWT.NONE);
		item.setControl(filterText);
		Point preferred = item.computeSize(size.x, size.y);
		item.setPreferredSize(preferred);
		return item;
	}

	private static CoolItem createToolItem(CoolBar cool, final CommonViewer viewer) {
		ToolBar toolBar = new ToolBar(cool, SWT.FLAT);
		final ToolItem discharge = new ToolItem(toolBar, SWT.CHECK);
		ImageRegistry registry = EventBUIPlugin.getDefault().getImageRegistry();
		discharge.setImage(registry.get(IEventBSharedImages.IMG_DISCHARGED));
		discharge.addSelectionListener(new SelectionListener() {

			public void widgetSelected(SelectionEvent e) {
				DischargedFilter.active = discharge.getSelection();
				PlatformUI.getWorkbench().getDisplay().asyncExec(
						new Runnable() {
							public void run() {
								Control ctrl = viewer.getControl();
								if (ctrl != null && !ctrl.isDisposed()) {
									Object[] expanded = viewer
											.getExpandedElements();
									viewer.refresh(false);
									viewer.setExpandedElements(expanded);
								}
							}
						});
			}

			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
		});

		toolBar.pack();
		Point size = toolBar.getSize();
		CoolItem item = new CoolItem(cool, SWT.NONE);
		item.setControl(toolBar);
		Point preferred = item.computeSize(size.x, size.y);
		item.setPreferredSize(preferred);
		return item;
	}

}
