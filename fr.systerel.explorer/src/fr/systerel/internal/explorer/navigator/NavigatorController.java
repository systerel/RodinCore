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

package fr.systerel.internal.explorer.navigator;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
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
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.navigator.CommonViewer;
import org.eclipse.ui.navigator.INavigatorFilterService;
import org.eventb.ui.EventBUIPlugin;
import org.eventb.ui.IEventBSharedImages;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinCore;

import fr.systerel.internal.explorer.model.IModelListener;
import fr.systerel.internal.explorer.model.ModelController;
import fr.systerel.internal.explorer.navigator.filters.DischargedFilter;
import fr.systerel.internal.explorer.navigator.filters.ObligationTextFilter;

/**
 * This class is used to set up the Navigator. It sets up the tool bar in the
 * navigator and instantiates the <code>ModelController</code>.
 * @since 1.0
 * 
 */
public class NavigatorController {
	
	private static ArrayList<CommonViewer> registeredViewers = new ArrayList<CommonViewer>();

	private static class ViewerRefresher implements IModelListener {

		private final CommonViewer viewer;

		public ViewerRefresher(CommonViewer viewer) {
			this.viewer = viewer;
		}

		public void refresh(final List<IRodinElement> toRefresh) {
			final Display display = PlatformUI.getWorkbench().getDisplay();
			if (display.isDisposed()) {
				return;
			}
			try {
				display.asyncExec(new Runnable() {
					public void run() {
						refreshViewer(toRefresh);
					}
				});
			} catch (SWTException e) {
				if (e.code == SWT.ERROR_DEVICE_DISPOSED) {
					// do not refresh
					return;
				}
				throw e;
			}
		}

		void refreshViewer(List<IRodinElement> toRefresh) {
			final Control ctrl = viewer.getControl();
			if (ctrl != null && !ctrl.isDisposed()) {
				// refresh everything
				if (toRefresh.contains(RodinCore.getRodinDB())) {
					viewer.refresh();
				} else {
					for (Object elem : toRefresh) {
						if (elem instanceof IRodinProject) {
							viewer.refresh(((IRodinProject) elem).getProject());
						} else {
							viewer.refresh(elem);
						}
					}
				}
			}
		}

	}

	public static void setUpNavigator(CommonViewer viewer) {
		//only treat viewers that have not been set up yet.
		if (!registeredViewers.contains(viewer)) {
			final ViewerRefresher listener = new ViewerRefresher(viewer);
			ModelController.getInstance().addListener(listener);
			setUpFiltersToolBar(viewer);
			registeredViewers.add(viewer);
		}
	}
	
	
	/**
	 * Finds the <code>DischargedFilter</code> of a given viewer.
	 * 
	 * @param viewer
	 * @return The viewer's <code>DischargedFilter</code> if it has one,
	 *         <code>null</code> otherwise.
	 */
	private static DischargedFilter findDischargedFilter(CommonViewer viewer) {
		INavigatorFilterService service = viewer.getNavigatorContentService().getFilterService();
		ViewerFilter[] filters = service.getVisibleFilters(false);
		for (ViewerFilter filter : filters) {
			if (filter instanceof DischargedFilter) {
				return (DischargedFilter) filter;
			}
		}
		return null;
	}

	/**
	 * Finds the <code>ObligationTextFilter</code> of a given viewer.
	 * 
	 * @param viewer
	 * @return The viewer's <code>ObligationTextFilter</code> if it has one,
	 *         <code>null</code> otherwise.
	 */
	private static ObligationTextFilter findObligationTextFilter(CommonViewer viewer) {
		INavigatorFilterService service = viewer.getNavigatorContentService().getFilterService();
		ViewerFilter[] filters = service.getVisibleFilters(false);
		for (ViewerFilter filter : filters) {
			if (filter instanceof ObligationTextFilter) {
				return (ObligationTextFilter) filter;
			}
		}
		return null;
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
		final ObligationTextFilter filter = findObligationTextFilter(viewer);
		final Text filterText = new Text(cool, SWT.SINGLE | SWT.BORDER);
		filterText.addModifyListener(new ModifyListener() {

			public void modifyText(ModifyEvent e) {
				if (filter != null) {
					filter.setText(filterText.getText());
					ExplorerUtils.refreshViewer(viewer);
				}
			}
			
		});
		filterText.pack();
		Point size = filterText.getSize();
		CoolItem item = new CoolItem(cool, SWT.NONE);
		item.setControl(filterText);
		Point preferred = item.computeSize(size.x, size.y);
		item.setPreferredSize(preferred);
		return item;
	}

	private static void createToolItem(CoolBar cool, final CommonViewer viewer) {
		ToolBar toolBar = new ToolBar(cool, SWT.FLAT);
		final DischargedFilter filter = findDischargedFilter(viewer);
		final ToolItem discharge = new ToolItem(toolBar, SWT.CHECK);
		
		ImageRegistry registry = EventBUIPlugin.getDefault().getImageRegistry();
		discharge.setImage(registry.get(IEventBSharedImages.IMG_DISCHARGED));
		discharge.addSelectionListener(new SelectionListener() {

			public void widgetSelected(SelectionEvent e) {
				if (filter != null) {
					filter.setActive(discharge.getSelection());
					ExplorerUtils.refreshViewer(viewer);
				}
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
	}

}
