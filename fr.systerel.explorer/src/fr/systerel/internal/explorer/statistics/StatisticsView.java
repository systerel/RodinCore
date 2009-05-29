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


package fr.systerel.internal.explorer.statistics;

import static fr.systerel.explorer.ExplorerPlugin.NAVIGATOR_ID;

import java.util.HashMap;
import java.util.List;

import org.eclipse.core.resources.IProject;
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
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.ISelectionService;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.navigator.CommonNavigator;
import org.eclipse.ui.part.ViewPart;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinCore;

import fr.systerel.explorer.IElementNode;
import fr.systerel.internal.explorer.model.IModelElement;
import fr.systerel.internal.explorer.model.IModelListener;
import fr.systerel.internal.explorer.model.ModelController;

/**
 * This class implements a view that shows statistics according to the selection in the
 * navigator.
 *
 */
public class StatisticsView extends ViewPart implements ISelectionListener,
		IModelListener {
	private Label label;
	private TableViewer viewer;
	TableViewer detailsViewer;
	private Composite container;
	private HashMap<Integer, StatisticsColumn> detailColumns = new HashMap<Integer, StatisticsColumn>();
	private HashMap<Integer, StatisticsColumn> overviewColumns = new HashMap<Integer, StatisticsColumn>();
	
	private IStructuredContentProvider statisticsContentProvider =
		new StatisticsContentProvider();
	private IStructuredContentProvider statisticsDetailsContentProvider =
		new StatisticsDetailsContentProvider();

	private static final Object[] EMPTY_SELECTION = new Object[0];
	protected Object[] currentSelection = EMPTY_SELECTION;
	
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
		ModelController.getInstance().addListener(this);

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
	
	@Override
	public void dispose(){
		super.dispose();
		ModelController.getInstance().removeListener(this);
		ISelectionService selectionService = getSite().getWorkbenchWindow().getSelectionService();
		selectionService.removeSelectionListener(NAVIGATOR_ID, this);
		
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
		Table table =viewer.getTable();
		table.setHeaderVisible(true);
		
		addOverviewColumn(new StatisticsColumn.TotalColumn(table));
		addOverviewColumn(new StatisticsColumn.AutoColumn(table));
		addOverviewColumn(new StatisticsColumn.ManualColumn(table));
		addOverviewColumn(new StatisticsColumn.ReviewedColumn(table));
		addOverviewColumn(new StatisticsColumn.UndischargedColumn(table));
		
		viewer.setContentProvider(statisticsContentProvider);
		viewer.setLabelProvider(new StatisticsLabelProvider(this));
		viewer.getTable().setLayout(new RowLayout (SWT.VERTICAL));
		FormData data = createFormData(0, -1);
		viewer.getControl().setLayoutData(data);
	}
	
	private void addOverviewColumn(StatisticsColumn column) {
		overviewColumns.put(new Integer(column.getIndex()), column);
		
	}

	private void setUpDetailsColumn(StatisticsColumn column, StatisticsDetailsComparator comparator) {
		detailColumns.put(new Integer(column.getIndex()), column);
		addSelectionListener(column, comparator);
	}
	
	private void createDetailsViewer() {
		detailsViewer = new TableViewer(container,  SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION);
		detailsViewer.setContentProvider(statisticsDetailsContentProvider);
		detailsViewer.setLabelProvider(new StatisticsDetailsLabelProvider(this));
		detailsViewer.getTable().setHeaderVisible(true);
		detailsViewer.getTable().setVisible(false);
		Table table = detailsViewer.getTable();
		
		setUpDetailsColumn(new StatisticsColumn.NameColumn(table), StatisticsDetailsComparator.NAME);
		setUpDetailsColumn(new StatisticsColumn.TotalColumn(table), StatisticsDetailsComparator.TOTAL);
		setUpDetailsColumn(new StatisticsColumn.AutoColumn(table), StatisticsDetailsComparator.AUTO);
		setUpDetailsColumn(new StatisticsColumn.ManualColumn(table), StatisticsDetailsComparator.MANUAL);
		setUpDetailsColumn(new StatisticsColumn.ReviewedColumn(table), StatisticsDetailsComparator.REVIEWED);
		setUpDetailsColumn(new StatisticsColumn.UndischargedColumn(table), StatisticsDetailsComparator.UNDISCHARGED);
		
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

	private void addSelectionListener(StatisticsColumn column, final StatisticsDetailsComparator comparator) {
		// Add listener to column to sort when clicked on the header.
		column.getColumn().addSelectionListener(new SelectionAdapter() {
       	
			@Override
			public void widgetSelected(SelectionEvent e) {
				// already sorting with this column's comparator: toggle order.
				if (detailsViewer.getComparator() == comparator) {
					comparator.setOrder(!comparator.getOrder());
					
				//sort with this column's comparator.
				} else {
					comparator.setOrder(StatisticsDetailsComparator.ASCENDING);
					detailsViewer.setComparator(comparator);
				}
			
				detailsViewer.refresh(false);
				colorEvery2ndLine();
				
			}
		});
	}
	
	public StatisticsColumn getDetailColumn(int index){
		return detailColumns.get(new Integer(index));
	}

	public StatisticsColumn getOverviewColumn(int index){
		return overviewColumns.get(new Integer(index));
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
			final Object[] input = ((ITreeSelection) selection).toArray();
			final String message = StatisticsUtil.isValidSelection(input);
			if (message == null) {
				currentSelection = input;
				refreshValid(input);
			} else {
				currentSelection = EMPTY_SELECTION;
				refreshEmpty(message);
			}
		}
	}

	private void resize() {
		Point size = container.getSize();
		container.pack();
		container.setSize(size);
	}
	
	protected void refreshValid(Object[] input) {
		viewer.setInput(input);
		viewer.getTable().setVisible(true);
		label.setVisible(false);
		detailsViewer.setInput(input);
		detailsViewer.getTable().setVisible(
				StatisticsUtil.detailsRequired((input)));

		colorEvery2ndLine();
		resize();
	}
	
	private void refreshEmpty(String message) {
		// if the viewer is not visible, show the "no statistics" label
		viewer.getTable().setVisible(false);
		label.setText("No statistics available: " + message);
		label.setVisible(true);
		detailsViewer.getTable().setVisible(false);
		resize();
	}

	public void refresh(final List<IRodinElement> elements) {
		if (currentSelection == null)
			return;
		PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
			public void run() {
				doRefresh(elements);
			}
		});
	}

	void doRefresh(List<IRodinElement> elements) {
		for (IRodinElement toRefresh : elements) {
			for (Object o : currentSelection) {
				final IRodinElement selected = getRodinElement(o);
				if (toRefresh.equals(selected)
						|| toRefresh.isAncestorOf(selected)) {
					refreshValid(currentSelection);
					return;
				}
			}
		}
	}

	private IRodinElement getRodinElement(Object el) {
		if (el instanceof IProject) {
			return RodinCore.valueOf((IProject) el);
		} else if (el instanceof IElementNode) {
			return ((IElementNode) el).getParent();
		} else if (el instanceof IRodinElement) {
			return (IRodinElement) el;
		} else {
			return null;
		}
	}

}
