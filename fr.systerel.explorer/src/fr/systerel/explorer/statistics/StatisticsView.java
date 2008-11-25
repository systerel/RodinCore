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


package fr.systerel.explorer.statistics;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.ISelectionService;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.navigator.CommonNavigator;
import org.eclipse.ui.part.ViewPart;

import fr.systerel.explorer.model.IModelElement;
import fr.systerel.explorer.navigator.IElementNode;

/**
 * This class implements a view that shows statistics according to the selection in the
 * navigator.
 *
 */
public class StatisticsView extends ViewPart implements ISelectionListener {
	private Label label;
	private TableViewer viewer;
	TableViewer detailsViewer;
	private Composite container;
	
	private IStructuredContentProvider statisticsContentProvider =
		new StatisticsContentProvider();
	private IStructuredContentProvider statisticsDetailsContentProvider =
		new StatisticsDetailsContentProvider();

	private static final String NAVIGATOR_ID = "fr.systerel.explorer.navigator.view";

	/**
	 * 
	 */
	public StatisticsView() {
		// do nothing
	}


	@Override
	public void setFocus() {
		if (container != null) {
			container.setFocus();
		}
	}
	
	
	@Override
	public void createPartControl(Composite parent) {
		ISelectionService selectionService = getSite().getWorkbenchWindow().getSelectionService();
		selectionService.addSelectionListener(NAVIGATOR_ID, this);

		container = new Composite(parent, SWT.NONE);
		FormLayout layout = new FormLayout();
		container.setLayout(layout);
		createNoStatisticsLabel();
		
		createOverviewViewer();
		createDetailsViewer();
		
		Point size = container.getSize();
		container.pack();
		container.setSize(size);
		
		addPopUpMenu();
	
	}
		

	void colorEvery2ndLine() {
		boolean colored = false;
		Color gray = detailsViewer.getControl().getDisplay().getSystemColor(SWT.COLOR_GRAY);
		for (TableItem item : detailsViewer.getTable().getItems()) {
			if (colored) {
				item.setBackground(gray);

			}
			colored = !colored;
		}
	}
	
	/**
	 * Shows a given element in the Navigator, if it can be shown. 
	 * 
	 * @param element
	 *            The element to show.
	 */
	void showInNavigator(Object element){
		IWorkbenchPart part = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().findView(NAVIGATOR_ID);
		if (part instanceof CommonNavigator) {
			CommonNavigator navigator = (CommonNavigator) part;
			if (element instanceof IElementNode) {
				navigator.getCommonViewer().setSelection(new StructuredSelection(element), true);
			}
			else if (element instanceof IModelElement) {
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
	    
	}
	
	
	private void createOverviewViewer(){
		viewer = new TableViewer(container,  SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION);
		viewer.getTable().setHeaderVisible(true);
		addOverviewColumn("Total");
		addOverviewColumn("Auto");
		addOverviewColumn("Manual");
		addOverviewColumn("Reviewed");
		addOverviewColumn("Undischarged");
		viewer.setContentProvider(statisticsContentProvider);
		viewer.setLabelProvider(new StatisticsLabelProvider());
		viewer.getTable().setLayout(new RowLayout (SWT.VERTICAL));
		FormData data = createFormData(0, -1);
		viewer.getControl().setLayoutData(data);
	}
	
	private void addOverviewColumn(String headerText) {
		TableColumn column = new TableColumn(viewer.getTable(), SWT.NONE);
		column.setText(headerText);
		column.pack();
		
	}

	private void createDetailsViewer() {
		detailsViewer = new TableViewer(container,  SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION);
		detailsViewer.setContentProvider(statisticsDetailsContentProvider);
		detailsViewer.setLabelProvider(new StatisticsDetailsLabelProvider());
		detailsViewer.getTable().setHeaderVisible(true);
		detailsViewer.getTable().setVisible(false);
		addDetailsColumn("Name", StatisticsDetailsComparator.NAME, 0);
		addDetailsColumn("Total", StatisticsDetailsComparator.TOTAL, 1);
		addDetailsColumn("Auto.", StatisticsDetailsComparator.AUTO, 2);
		addDetailsColumn("Manual.", StatisticsDetailsComparator.MANUAL, 3);
		addDetailsColumn("Reviewed", StatisticsDetailsComparator.REVIEWED, 4);
		addDetailsColumn("Undischarged", StatisticsDetailsComparator.UNDISCHARGED, 5);
		
		FormData tableData = createFormData(viewer.getControl());
		detailsViewer.getControl().setLayoutData(tableData);

		//sort by name by default.
		detailsViewer.setComparator(StatisticsDetailsComparator.NAME);
		
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

	}

	private void addDetailsColumn(final String text, final StatisticsDetailsComparator comparator, final int index) {
		final TableColumn column = new TableColumn(detailsViewer.getTable(), SWT.NONE);
		column.setText(text);
		column.pack();

		// Add listener to column to sort when clicked on the header.
		column.addSelectionListener(new SelectionAdapter() {
       	
			@Override
			public void widgetSelected(SelectionEvent e) {
				comparator.updateColumn(index);
				detailsViewer.setComparator(comparator);
				detailsViewer.refresh(false);
				colorEvery2ndLine();
				
			}
		});
	}
	

	private void createNoStatisticsLabel() {
		label =  new Label(container, SWT.SHADOW_NONE | SWT.LEFT | SWT.WRAP);
		label.setText("No statistics available");
		FormData data = createFormData(0, 100);
		label.setLayoutData(data);
	}
	
	private FormData createFormData(int top, int bottom){
		FormData data = new FormData();
		data.left = new FormAttachment(0);
		data.right = new FormAttachment(100);
		data.top = new FormAttachment(top);
		if (bottom >= 0) {
			data.bottom = new FormAttachment(bottom);
		}
		return data;
	}

	private FormData createFormData(Control top){
		FormData data = new FormData();
		data.left = new FormAttachment(0);
		data.right = new FormAttachment(100);
		data.top = new FormAttachment(top);
		data.bottom = new FormAttachment(100);
		return data;
	}
	

	public void selectionChanged(IWorkbenchPart part, ISelection selection) {
		if (selection.isEmpty())
			return;

		if (selection instanceof ITreeSelection) {
			String valid = StatisticsUtil.isValidSelection(((ITreeSelection) selection).toArray());
			if ( valid == null) {
				viewer.setInput(((ITreeSelection) selection).toArray());
				viewer.getTable().setVisible(true);
				label.setVisible(false);
				detailsViewer.setInput(((ITreeSelection) selection).toArray());
				detailsViewer.getTable().setVisible( StatisticsUtil.detailsRequired(((ITreeSelection) selection).toArray()));
				
				colorEvery2ndLine();

			} else {
				// if the viewer is not visible, show the "no statistics" label
				viewer.getTable().setVisible(false);
				label.setText("No statistics available: " +valid);
				label.setVisible(true);
				detailsViewer.getTable().setVisible(false);
			}
			Point size = container.getSize();
			container.pack();
			container.setSize(size);
		
		}
	}


}
