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


package fr.systerel.explorer.masterDetails;

import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
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
import org.eclipse.ui.forms.IFormPart;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.navigator.CommonViewer;
import org.eventb.internal.ui.TimerText;
import org.eventb.ui.EventBUIPlugin;
import org.eventb.ui.IEventBSharedImages;

import fr.systerel.explorer.RodinNavigator;
import fr.systerel.explorer.navigator.filters.DischargedFilter;
import fr.systerel.explorer.navigator.filters.ObligationTextFilter;

/**
 * This is the Masterpart of the MasterDetails pattern of the Navigator.
 * The CommonViewer of the RodinNavigator is created from here,
 * so that it ends up in the right place (with respect to the DetailsPart).
 *
 */
public class NavigatorMasterPart implements IFormPart {
	CommonViewer commonViewer;
	IManagedForm managedForm;
	private Composite container;
	private CoolBar coolBar;
	Text filterText;
	ToolItem discharge;


	public NavigatorMasterPart(RodinNavigator navigator, Composite parent) {
		//create a container for the commonViewer and the toolbar
		container = new Composite(parent, SWT.NONE);
		FormLayout layout = new FormLayout();
		container.setLayout(layout);
		
		setUpFiltersToolBar();
		
		setUpCommonViewer(navigator);
		
		
	}

	
	private final ISelectionChangedListener listener = 
		new ISelectionChangedListener() {

		public void selectionChanged(SelectionChangedEvent event) {
			managedForm.fireSelectionChanged(NavigatorMasterPart.this, event
					.getSelection());
		}
	};
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.forms.IFormPart#commit(boolean)
	 */
	public void commit(boolean onSave) {
		//do nothing
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.forms.IFormPart#dispose()
	 */
	public void dispose() {
		container.dispose();

	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.forms.IFormPart#initialize(org.eclipse.ui.forms.IManagedForm)
	 */
	public void initialize(IManagedForm form) {
		this.managedForm = form;	
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.forms.IFormPart#isDirty()
	 */
	public boolean isDirty() {
		return false;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.forms.IFormPart#isStale()
	 */
	public boolean isStale() {
		return false;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.forms.IFormPart#refresh()
	 */
	public void refresh() {
		commonViewer.refresh();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.forms.IFormPart#setFocus()
	 */
	public void setFocus() {
		// do nothing

	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.forms.IFormPart#setFormInput(java.lang.Object)
	 */
	public boolean setFormInput(Object input) {
		//do nothing. input is directly set to the CommonViewer
		return false;
	}

	public Composite getViewerContainer() {
		return container;
	}
	
	/**
	 * Creates and formats the toolbar for the filters
	 */
	public void setUpFiltersToolBar() {
		
		coolBar = new CoolBar(container, SWT.FLAT);
		FormData coolData = new FormData();
		coolData.left = new FormAttachment(0);
		coolData.right = new FormAttachment(100);
		coolData.top = new FormAttachment(0);
		coolBar.setLayoutData(coolData);
		createText(coolBar);
		createToolItem(coolBar);
		
	}
	
	/**
	 *  Creates and initializes the CommonViewer 
	 */
	public void setUpCommonViewer(RodinNavigator navigator) {
		FormData textData = new FormData();
		textData.left = new FormAttachment(0);
		textData.right = new FormAttachment(100);
		textData.top = new FormAttachment(coolBar);
		textData.bottom = new FormAttachment(100);

		//this creates the Common Viewer and initializes it.
		navigator.superCreatePartControl(container);
		commonViewer  = navigator.getCommonViewer();
		commonViewer.addSelectionChangedListener(listener);
		commonViewer.getControl().setLayoutData(textData);
		
	}
	
	public CoolItem createText(CoolBar cool) {
		filterText = new Text(cool, SWT.SINGLE | SWT.BORDER);
		new TimerText(filterText, 1000) {

			@Override
			protected void response() {
				ObligationTextFilter.text = filterText.getText();
				commonViewer.getControl().getShell().getDisplay().asyncExec(new Runnable(){
					public void run() {
						CommonViewer viewer = commonViewer;
						Control ctrl = viewer.getControl();
						if (ctrl != null && !ctrl.isDisposed()) {
							Object[] expanded = viewer.getExpandedElements();
							viewer.refresh(false);
							viewer.setExpandedElements(expanded);
						}
				}});
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

	public CoolItem createToolItem(CoolBar cool) {
		ToolBar toolBar = new ToolBar(cool, SWT.FLAT);
		discharge = new ToolItem(toolBar, SWT.CHECK);
		ImageRegistry registry = EventBUIPlugin.getDefault().getImageRegistry();
		discharge.setImage(registry.get(IEventBSharedImages.IMG_DISCHARGED));
		discharge.addSelectionListener(new SelectionListener() {

			public void widgetSelected(SelectionEvent e) {
				DischargedFilter.active = discharge.getSelection();
				commonViewer.getControl().getShell().getDisplay().asyncExec(new Runnable(){
					public void run() {
						CommonViewer viewer = commonViewer;
						Control ctrl = viewer.getControl();
						if (ctrl != null && !ctrl.isDisposed()) {
							Object[] expanded = viewer.getExpandedElements();
							viewer.refresh(false);
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
		CoolItem item = new CoolItem(cool, SWT.NONE);
		item.setControl(toolBar);
		Point preferred = item.computeSize(size.x, size.y);
		item.setPreferredSize(preferred);
		return item;
	}
	

}
