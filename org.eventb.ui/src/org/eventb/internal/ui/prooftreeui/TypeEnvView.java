/*******************************************************************************
 * Copyright (c) 2011, 2016 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.ui.prooftreeui;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.ITypeEnvironment.IIterator;
import org.eventb.core.seqprover.IProofTreeNode;
import org.eventb.ui.EventBUIPlugin;

/**
 * A view that displays the type environment of the currently selected proof
 * tree node.
 * 
 * @author Nicolas Beauger
 * 
 */
public class TypeEnvView extends AbstractProofNodeView implements IProofTreeSelectionListener {

	/**
	 * The identifier of the Rule Details View (value
	 * <code>"org.eventb.ui.views.TypeEnv"</code>).
	 */
	public static final String VIEW_ID = EventBUIPlugin.PLUGIN_ID
			+ ".views.TypeEnv";

	private static class Ident {

		private static final String[] COLUMN_NAMES = new String[] {
				"Identifier", "Type" };

		private final String name;
		private final String type;

		public static Ident[] valueOf(ITypeEnvironment tEnv) {
			final List<Ident> result = new ArrayList<Ident>();
			final IIterator iter = tEnv.getIterator();
			while (iter.hasNext()) {
				iter.advance();
				result.add(new Ident(iter.getName(), iter.getType().toString()));
			}
			return result.toArray(new Ident[result.size()]);
		}

		private Ident(String name, String type) {
			this.name = name;
			this.type = type;
		}

		public static int getNumberOfColumns() {
			return COLUMN_NAMES.length;
		}

		public static String getColumnTitle(int columnIndex) {
			return COLUMN_NAMES[columnIndex];
		}

		public String getColumnText(int columnIndex) {
			if (columnIndex == 0) {
				return name;
			} else {
				return type;
			}
		}

		public int compareTo(Ident other, int columnIndex) {
			final String thisValue = this.getColumnText(columnIndex);
			final String otherValue = other.getColumnText(columnIndex);
			return thisValue.compareTo(otherValue);
		}

	}

	private static class TypeEnvLabelProvider implements ITableLabelProvider {

		public TypeEnvLabelProvider() {
			// avoid synthetic access
		}

		@Override
		public void addListener(ILabelProviderListener listener) {
			// nothing to do
		}

		@Override
		public void dispose() {
			// nothing to do
		}

		@Override
		public boolean isLabelProperty(Object element, String property) {
			return false;
		}

		@Override
		public void removeListener(ILabelProviderListener listener) {
			// nothing to do
		}

		@Override
		public Image getColumnImage(Object element, int columnIndex) {
			return null;
		}

		@Override
		public String getColumnText(Object element, int columnIndex) {
			if (!(element instanceof Ident)) {
				return null;
			}
			return ((Ident) element).getColumnText(columnIndex);
		}

	}

	private static class TypeEnvContentProvider implements
			IStructuredContentProvider {

		public TypeEnvContentProvider() {
			// avoid synthetic access
		}

		@Override
		public void dispose() {
			// nothing to do
		}

		@Override
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			// nothing to do
		}

		@Override
		public Object[] getElements(Object inputElement) {
			if (!(inputElement instanceof ITypeEnvironment)) {
				return null;
			}
			return Ident.valueOf((ITypeEnvironment) inputElement);
		}

	}

	private static class ColumnComparator extends ViewerComparator {

		private int column = 0;
		private boolean ascending = true;

		public ColumnComparator() {
			// avoid synthetic access
		}

		public int getColumn() {
			return column;
		}

		public void setColumn(int column) {
			this.column = column;
		}

		public boolean isAscending() {
			return ascending;
		}

		public void setAscending(boolean ascending) {
			this.ascending = ascending;
		}

		@Override
		public int compare(Viewer viewer, Object e1, Object e2) {
			final Ident ident1 = (Ident) e1;
			final Ident ident2 = (Ident) e2;
			final int ascendCompare = ident1.compareTo(ident2, column);
			if (ascending) {
				return ascendCompare;
			} else {
				return -ascendCompare;
			}
		}
	}

	private static class TypeEnvEditingSupport extends EditingSupport
	{

		private final int column;
		private final CellEditor editor;
		
		public TypeEnvEditingSupport(TableViewer tableViewer, int column) {
			super(tableViewer);
			this.column = column;
			final Composite parent = tableViewer.getTable();
			this.editor = new TextCellEditor(parent, SWT.READ_ONLY);
		}

		@Override
		protected CellEditor getCellEditor(Object element) {
			if (!(element instanceof Ident)) {
				return null;
			}
			return editor;
		}

		@Override
		protected boolean canEdit(Object element) {
			return element instanceof Ident;
		}

		@Override
		protected Object getValue(Object element) {
			if (!(element instanceof Ident)) {
				return null;
			}
			final Ident ident = (Ident) element;
			return ident.getColumnText(column);
		}

		@Override
		protected void setValue(Object element, Object value) {
			// nothing to do			
		}
		
	}

	TableViewer tableViewer;

	@Override
	protected void initializeControl(Composite parent, Font font) {
		tableViewer = new TableViewer(parent, SWT.FULL_SELECTION | SWT.FILL
				| SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
		final ColumnComparator comparator = new ColumnComparator();
		tableViewer.setComparator(comparator);
		createColumns(font);
		tableViewer.setLabelProvider(new TypeEnvLabelProvider());
		tableViewer.setContentProvider(new TypeEnvContentProvider());
		initColumnSorting(comparator);

		final ProofTreeSelectionService treeSelService = ProofTreeSelectionService.getInstance();
		treeSelService.addListener(this);
		// prime the selection to display contents
		final IProofTreeNode currentNode = treeSelService.getCurrent();
		if (currentNode != null) {
			nodeChanged(currentNode);
		}
	}

	private void createColumns(Font font) {
		final Table table = tableViewer.getTable();
		table.setFont(font);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		
		for (int i = 0; i < Ident.getNumberOfColumns(); i++) {
			final String columnName = Ident.getColumnTitle(i);
			final TableViewerColumn colViewer = new TableViewerColumn(
					tableViewer, SWT.WRAP);
			colViewer.setEditingSupport(new TypeEnvEditingSupport(tableViewer, i));
			final TableColumn col = colViewer.getColumn();
			col.setText(columnName);
			col.pack();
		}
	}

	private void initColumnSorting(final ColumnComparator comparator) {
		final TableColumn[] columns = tableViewer.getTable().getColumns();

		for (int i = 0; i < columns.length; i++) {
			final int columnNumber = i;
			columns[i].addSelectionListener(new SelectionAdapter() {

				@Override
				public void widgetSelected(SelectionEvent e) {
					if (comparator.getColumn() == columnNumber) {
						// already sorting with this column's comparator: toggle
						// ascending.
						comparator.setAscending(!comparator.isAscending());
					} else {
						comparator.setColumn(columnNumber);
						comparator.setAscending(true);
					}
					tableViewer.refresh(false);
				}
			});
		}
	}

	@Override
	public void nodeChanged(IProofTreeNode newNode) {
		if (isDisposed()) {
			return;
		}
		final ITypeEnvironment typeEnv = newNode.getSequent().typeEnvironment();
		tableViewer.setInput(typeEnv);
	}

	@Override
	protected void fontChanged(Font font) {
		tableViewer.getTable().setFont(font);
	}
}
