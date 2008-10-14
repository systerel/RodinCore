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


package fr.systerel.explorer.masterDetails.Statistics;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.actions.ActionFactory;

import fr.systerel.explorer.Activator;
import fr.systerel.explorer.RodinNavigator;
import fr.systerel.explorer.masterDetails.INavigatorDetailsTab;
import fr.systerel.explorer.model.IModelElement;

/**
 * The Content Provider for Statistics.
 *
 */
public class StatisticsTab implements INavigatorDetailsTab, ISelectionChangedListener {
	private Label label;
	private TableViewer viewer;
	private TableViewer detailsViewer;
	private Composite container;
	private static final IStructuredContentProvider statisticsContentProvider =
		new StatisticsContentProvider();
	private static final IStructuredContentProvider statisticsDetailsContentProvider =
		new StatisticsDetailsContentProvider();
		

	public TabItem getTabItem(TabFolder tabFolder) {
		
		container = new Composite(tabFolder, SWT.NONE);
		FormLayout layout = new FormLayout();
		container.setLayout(layout);
		TabItem item = new TabItem (tabFolder, SWT.NONE);
		item.setControl(container);
		item.setText ("Statistics ");	
		
		// create a label that is shown when there are no statistics
		label =  new Label(container, SWT.SHADOW_NONE | SWT.LEFT );
		label.setText("No statistics available");
		FormData data = new FormData();
		data.left = new FormAttachment(0);
		data.right = new FormAttachment(100);
		data.top = new FormAttachment(0);
		data.bottom = new FormAttachment(10);
		label.setLayoutData(data);
		
		// create the overview table
		createViewer();

		//create the viewer for the details
		createDetailsViewer();
		
		//sort by name by default.
		detailsViewer.setComparator((new StatisticsDetailsComparator(StatisticsDetailsComparator.NAME)));

		// on double click show the node in the navigator
		detailsViewer.addDoubleClickListener(new IDoubleClickListener() {

			public void doubleClick(DoubleClickEvent event) {
				if (event.getSelection() instanceof IStructuredSelection) {
					if ( (((IStructuredSelection)event.getSelection()).getFirstElement()) instanceof Statistics) {
						IStatistics stats = (Statistics)((IStructuredSelection)event.getSelection()).getFirstElement();
						showInNavigator(stats.getParent());
					}
					
				}
				
			}
			
		});
		
		addPopUpMenu();
		
		return item;
	}
	
	public void registerAsListener(ISelectionProvider selectionProvider) {
		selectionProvider.addSelectionChangedListener(this);
	}

	public void selectionChanged(SelectionChangedEvent event) {
		ISelection selection = event.getSelection();
		if (selection.isEmpty())
			return;

		if (selection instanceof ITreeSelection) {
//			final Object selectedNode = ((ITreeSelection) selection).getPaths()[0]
//					.getLastSegment();
//			if (selectedNode != null) {
//				viewer.setInput(selectedNode);
//				detailsViewer.setInput(selectedNode);
//			}
			// TODO: check if selection is valid
			viewer.setInput(((ITreeSelection) selection).toArray());
			viewer.getTable().setVisible(isValidSelection(((ITreeSelection) selection).toArray()));
			// if the viewer is not visible, show the "no statistics" label
			label.setVisible(!viewer.getTable().getVisible());
			detailsViewer.setInput(((ITreeSelection) selection).toArray());
			detailsViewer.getTable().setVisible(detailsRequired(((ITreeSelection) selection).toArray()));
		}
		
	}

	/**
	 * Decides, if a given selection is valid for statistics
	 * @param elements The selected elements
	 * @return true, if the selection is valid, false otherwise.
	 */
	private boolean isValidSelection(Object[] elements) {
		// TODO
		return true;
	}
	
	/**
	 * Decides, if the details view is required for a given selection
	 * @param elements The selected elements
	 * @return true, if the details view is required, false otherwise
	 */
	private boolean detailsRequired(Object[] elements) {
		//TODO
		return true;
	}
	
	/**
	 * Shows a given element in the RodinNavigator, if it can be shown. Does nothing otherwise.
	 * @param element The element to show.
	 */
	void showInNavigator(Object element){
		IWorkbenchPart part =Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage().getActivePart();
		if (part instanceof RodinNavigator) {
			RodinNavigator navigator = (RodinNavigator) part;
			if (element instanceof IModelElement) {
				navigator.getCommonViewer().setSelection(new StructuredSelection(((IModelElement)element).getInternalElement()), true);
			}
		}
	}
	
	/**
	 * Adds a popupMenu to the viewers
	 */
	private void addPopUpMenu () {

	    MenuManager popupMenu = new MenuManager();
	    IAction copyAction = new StatisticsCopyAction(detailsViewer, true);
	    popupMenu.add(copyAction);
	    Menu menu = popupMenu.createContextMenu(detailsViewer.getTable());
	    detailsViewer.getTable().setMenu(menu);
	    

	    popupMenu = new MenuManager();
	    copyAction = new StatisticsCopyAction(viewer, false);
	    popupMenu.add(copyAction);
	    menu = popupMenu.createContextMenu(viewer.getTable());
	    viewer.getTable().setMenu(menu);
//		IWorkbenchPart part =Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage().getActivePart();
//		if (part instanceof RodinNavigator) {
//			RodinNavigator navigator = (RodinNavigator) part;
//			navigator.getViewSite().getActionBars().setGlobalActionHandler(ActionFactory.COPY.getId(), copyAction);
//			navigator.getViewSite().getActionBars().updateActionBars();
//		}
	}
	
	
	private void createViewer(){
		viewer = new TableViewer(container,  SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION);
		viewer.getTable().setHeaderVisible(true);
		TableColumn column = new TableColumn(viewer.getTable(), SWT.NONE);
		column.setText("Total");
		column.pack();
		column = new TableColumn(viewer.getTable(), SWT.NONE);
		column.setText("Auto.");
		column.pack();
		column = new TableColumn(viewer.getTable(), SWT.NONE);
		column.setText("Manual.");
		column.pack();
		column = new TableColumn(viewer.getTable(), SWT.NONE);
		column.setText("Reviewed");
		column.pack();
		column = new TableColumn(viewer.getTable(), SWT.NONE);
		column.setText("Undischarged");
		column.pack();
		viewer.setContentProvider(statisticsContentProvider);
		viewer.setLabelProvider(new StatisticsLabelProvider());
		viewer.getTable().setLayout(new RowLayout (SWT.VERTICAL));
		FormData data = new FormData();
		data.left = new FormAttachment(0);
		data.right = new FormAttachment(100);
		data.top = new FormAttachment(0);
		data.bottom = new FormAttachment(10);
		viewer.getControl().setLayoutData(data);
	}

	private void createDetailsViewer() {
		detailsViewer = new TableViewer(container,  SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION);
		detailsViewer.setContentProvider(statisticsDetailsContentProvider);
		detailsViewer.setLabelProvider(new StatisticsDetailsLabelProvider());
		detailsViewer.getTable().setHeaderVisible(true);
		detailsViewer.setComparator(new ViewerComparator());
		TableColumn column = new TableColumn(detailsViewer.getTable(), SWT.NONE);
		column.setText("Name");
		// Add listener to column to sort when clicked on the header.
		column.addSelectionListener(new SelectionAdapter() {
       	
			@Override
			public void widgetSelected(SelectionEvent e) {
				detailsViewer.setComparator((new StatisticsDetailsComparator(StatisticsDetailsComparator.NAME)));
			}
		});
		column.pack();
		column = new TableColumn(detailsViewer.getTable(), SWT.NONE);
		column.setText("Total");
		column.addSelectionListener(new SelectionAdapter() {
	       	
			@Override
			public void widgetSelected(SelectionEvent e) {
				detailsViewer.setComparator((new StatisticsDetailsComparator(StatisticsDetailsComparator.TOTAL)));
			}
		});
		column.pack();
		column = new TableColumn(detailsViewer.getTable(), SWT.NONE);
		column.setText("Auto.");
		column.addSelectionListener(new SelectionAdapter() {
	       	
			@Override
			public void widgetSelected(SelectionEvent e) {
				detailsViewer.setComparator((new StatisticsDetailsComparator(StatisticsDetailsComparator.AUTO)));
			}
		});
		column.pack();
		column = new TableColumn(detailsViewer.getTable(), SWT.NONE);
		column.setText("Manual.");
		column.addSelectionListener(new SelectionAdapter() {
	       	
			@Override
			public void widgetSelected(SelectionEvent e) {
				detailsViewer.setComparator((new StatisticsDetailsComparator(StatisticsDetailsComparator.MANUAL)));
			}
		});
		column.pack();
		column = new TableColumn(detailsViewer.getTable(), SWT.NONE);
		column.setText("Reviewed");
		column.addSelectionListener(new SelectionAdapter() {
	       	
			@Override
			public void widgetSelected(SelectionEvent e) {
				detailsViewer.setComparator((new StatisticsDetailsComparator(StatisticsDetailsComparator.REVIEWED)));
			}
		});
		column.pack();
		column = new TableColumn(detailsViewer.getTable(), SWT.NONE);
		column.setText("Undischarged");
		column.addSelectionListener(new SelectionAdapter() {
	       	
			@Override
			public void widgetSelected(SelectionEvent e) {
				detailsViewer.setComparator((new StatisticsDetailsComparator(StatisticsDetailsComparator.UNDISCHARGED)));
			}
		});
		column.pack();
		FormData tableData = new FormData();
		tableData.left = new FormAttachment(0);
		tableData.right = new FormAttachment(100);
		tableData.top = new FormAttachment(10);
		tableData.bottom = new FormAttachment(100);
		detailsViewer.getControl().setLayoutData(tableData);
		
	}
	
}
