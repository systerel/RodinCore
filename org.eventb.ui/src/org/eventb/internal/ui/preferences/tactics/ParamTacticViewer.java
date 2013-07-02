/*******************************************************************************
 * Copyright (c) 2011, 2013 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.ui.preferences.tactics;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static org.eventb.core.seqprover.IParameterDesc.ParameterType.INT;
import static org.eventb.core.seqprover.IParameterDesc.ParameterType.LONG;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.ICellEditorValidator;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eventb.core.seqprover.IAutoTacticRegistry;
import org.eventb.core.seqprover.IParamTacticDescriptor;
import org.eventb.core.seqprover.IParameterDesc;
import org.eventb.core.seqprover.IParameterDesc.ParameterType;
import org.eventb.core.seqprover.IParameterSetting;
import org.eventb.core.seqprover.IParameterValuation;
import org.eventb.core.seqprover.IParameterizerDescriptor;
import org.eventb.core.seqprover.SequentProver;

/**
 * @author Nicolas Beauger
 * 
 */
public class ParamTacticViewer extends AbstractTacticViewer<IParamTacticDescriptor> {

	private static class Param {
		private final IParameterDesc desc;
		private Object value;

		public Param(IParameterDesc desc, Object value) {
			super();
			this.desc = desc;
			this.value = value;
		}

		public IParameterDesc getDesc() {
			return desc;
		}

		public Object getValue() {
			return value;
		}
		
		public void setValue(Object value) {
			this.value = value;
		}
	}

	private static enum Columns {
		LABEL,
		VALUE, TYPE, DEFAULT, DESCRIPTION;
		
		public String getText() {
			return toString().toLowerCase();
		}
	}

	private static class ParamLabelProvider implements ITableLabelProvider {

		public ParamLabelProvider() {
			// avoid synthetic access
		}

		@Override
		public void addListener(ILabelProviderListener listener) {
			// nothing
		}

		@Override
		public void dispose() {
			// nothing
		}

		@Override
		public boolean isLabelProperty(Object element, String property) {
			return false;
		}

		@Override
		public void removeListener(ILabelProviderListener listener) {
			// nothing
		}

		@Override
		public Image getColumnImage(Object element, int columnIndex) {
			return null;
		}

		@Override
		public String getColumnText(Object element, int columnIndex) {
			if (!(element instanceof Param)) {
				return null;
			}
			final Param param = (Param) element;
			final IParameterDesc desc = param.getDesc();
			
			final Columns column = Columns.values()[columnIndex];
			switch (column) {
			case LABEL:
				return desc.getLabel();
			case VALUE:
				return param.getValue().toString();
			case TYPE:
				return desc.getType().toString();
			case DEFAULT:
				return desc.getDefaultValue().toString();
			case DESCRIPTION:
				return desc.getDescription();
			default:
				return null;
			}
		}

	}

	private static class ParamContentProvider implements
			IStructuredContentProvider {

		public ParamContentProvider() {
			// avoid synthetic access
		}

		@Override
		public void dispose() {
			// nothing
		}

		@Override
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			// nothing
		}

		@Override
		public Object[] getElements(Object inputElement) {
			if (!(inputElement instanceof IParamTacticDescriptor)) {
				return null;
			}
			final IParamTacticDescriptor desc = (IParamTacticDescriptor) inputElement;
			final IParameterValuation valuation = desc.getValuation();
			final Collection<IParameterDesc> parameterDescs = valuation
					.getParameterDescs();
			final List<Param> result = new ArrayList<Param>(
					parameterDescs.size());
			for (IParameterDesc param : parameterDescs) {
				final String label = param.getLabel();
				final Object value = valuation.get(label);
				result.add(new Param(param, value));
			}
			return result.toArray(new Object[result.size()]);
		}
	}

	private static class NumberEditorValidator implements ICellEditorValidator {
		// either INT or LONG
		private final ParameterType type;

		public NumberEditorValidator(ParameterType type) {
			this.type = type;
		}

		@Override
		public String isValid(Object value) {
			if (!(value instanceof String)) {
				throw new IllegalArgumentException("expected a String"); //$NON-NLS-1$
			}
			try {
				type.parse((String) value);
				return null;
			} catch (NumberFormatException e) {
				return "invalid number " + value + " : " + e.getMessage(); //$NON-NLS-1$ //$NON-NLS-2$
			}
		}
		
	}
	
	private static class ParamEditingSupport extends EditingSupport {

		// boolean editing support
		private static final String[] BOOL_STRINGS = new String[] {
				FALSE.toString(), TRUE.toString() };

		private static final Boolean[] BOOL_VALUES = new Boolean[] { FALSE,
				TRUE };

		private static int getIndex(Boolean b) {
			return b ? 1 : 0;
		}

		private final TableViewer tableViewer;
		private final Table table;
		private final ComboBoxCellEditor boolEditor;
		private final TextCellEditor stringEditor;
		private final TextCellEditor intEditor;
		private final TextCellEditor longEditor;
		
		public ParamEditingSupport(TableViewer viewer) {
			super(viewer);
			tableViewer = viewer;
			table = tableViewer.getTable();
			boolEditor = new ComboBoxCellEditor(table, BOOL_STRINGS,
					SWT.READ_ONLY);
			stringEditor = new TextCellEditor(table);
			intEditor = new TextCellEditor(table);
			intEditor.setValidator(new NumberEditorValidator(INT));
			longEditor = new TextCellEditor(table);
			longEditor.setValidator(new NumberEditorValidator(LONG));
		}

		@Override
		protected CellEditor getCellEditor(Object element) {
			if (!(element instanceof Param)) {
				return null;
			}
			final Param param = (Param) element;
			final ParameterType type = param.getDesc().getType();
			switch (type) {
			case BOOL:
				return boolEditor;
			case INT:
				return intEditor;
			case LONG:
				return longEditor;
			case STRING:
				return stringEditor;
			default:
				assert false;
				return null;
			}
		}

		@Override
		protected boolean canEdit(Object element) {
			return element instanceof Param;
		}

		@Override
		protected Object getValue(Object element) {
			if (!(element instanceof Param)) {
				return null;
			}
			final Param param = (Param) element;
			final ParameterType type = param.getDesc().getType();
			final Object value = param.getValue();
			if (type == ParameterType.BOOL) {
				// index in combo box
				return getIndex((Boolean) value);
			}
			if (type == ParameterType.STRING) {
				return value;
			}
			// INT & LONG
			// must be a String for the text cell editor
			return value.toString();
		}

		@Override
		protected void setValue(Object element, Object value) {
			if (!(element instanceof Param)) {
				return;
			}
			final Param param = (Param) element;
			final ParameterType type = param.getDesc().getType();

			if (!checkEditorValue(value, type)) {
				return;
			}

			final Object paramValue; // conversion from value
			switch (type) {
			case BOOL:
				final int index = (Integer) value;
				paramValue = BOOL_VALUES[index];
				break;
			case STRING:
				paramValue = value;
				break;
			case INT:
			case LONG:
				paramValue = type.parse((String) value);
				break;
			default:
				throw new IllegalStateException("unknown type: " + type); //$NON-NLS-1$
			}
			param.setValue(paramValue);
			tableViewer.refresh(element);
			final int valueColumnIndex = Columns.VALUE.ordinal();
			final TableColumn valueColumn = table.getColumn(valueColumnIndex);
			valueColumn.pack();
		}

		private static boolean checkEditorValue(Object value, ParameterType type) {
			if (type == ParameterType.BOOL) {
				// index in combo box
				return value instanceof Integer;
			}
			return value instanceof String;
		}
		
	}
	
	private static class EditOnSelection implements ISelectionChangedListener {
		
		private final TableViewer viewer;
		private Param current = null;

		public EditOnSelection(TableViewer viewer) {
			this.viewer = viewer;
		}
		
		@Override
		public void selectionChanged(SelectionChangedEvent event) {
			final ISelection selection = event.getSelection();
			if (!(selection instanceof IStructuredSelection)) {
				return;
			}
			IStructuredSelection sel = (IStructuredSelection) selection;
			final Object elem = sel.getFirstElement();
			if (!(elem instanceof Param)) {
				return;
			}
			Param param = (Param) elem;
			if (param == current) return;
			current = param;
			viewer.cancelEditing();
			viewer.editElement(current, Columns.VALUE.ordinal());
		}
	}
	
	private TableViewer tableViewer;
	private Label tacticName;

	@Override
	public void createContents(Composite parent) {
		tacticName = new Label(parent, SWT.NONE);
		tableViewer = new TableViewer(parent, SWT.FULL_SELECTION | SWT.MULTI
				| SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
		createColumns();
		tableViewer.setLabelProvider(new ParamLabelProvider());
		tableViewer.setContentProvider(new ParamContentProvider());
		tableViewer.addSelectionChangedListener(new EditOnSelection(tableViewer));
	}
	
	private void createColumns() {
		final Columns[] columns = Columns.values();
		final String[] columnNames = new String[columns.length];
		for (int i = 0; i < columns.length; i++) {
			columnNames[i] = columns[i].getText();
		}
		

		final Table table = tableViewer.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);

		for (Columns column : columns) {
			final TableColumn col;
			if (column == Columns.VALUE) {
				final TableViewerColumn colViewer = new TableViewerColumn(
						tableViewer, SWT.WRAP);
				colViewer
						.setEditingSupport(new ParamEditingSupport(tableViewer));
				col = colViewer.getColumn();
			} else {
				col = new TableColumn(table, SWT.WRAP);
			}
			col.setText(column.getText());
		}
		tableViewer.setColumnProperties(columnNames);
	}

	@Override
	public void setInput(IParamTacticDescriptor desc) {
		if (tableViewer == null || tacticName == null) {
			return;
		}
		tableViewer.setInput(desc);
		if (desc == null) {
			tacticName.setText("");
			return;
		}
		tacticName.setText(desc.getTacticName());
		resize(tableViewer);
	}

	private static void resize(TableViewer viewer) {
		final Table table = viewer.getTable();
		final TableColumn[] columns = table.getColumns();
		for (TableColumn column : columns) {
			column.pack();
		}
		table.pack();
	}
	
	@Override
	public Control getControl() {
		if (tableViewer == null) {
			return null;
		}
		return tableViewer.getTable();
	}
	
	@Override
	public IParamTacticDescriptor getEditResult() {
		final IParamTacticDescriptor desc = getInput();
		if (desc == null) return null;
		final String parameterizerId = desc.getParameterizerId();
		final IAutoTacticRegistry reg = SequentProver.getAutoTacticRegistry();
		final IParameterizerDescriptor parameterizer = reg
				.getParameterizerDescriptor(parameterizerId);
		final IParameterSetting currentValuation = parameterizer
				.makeParameterSetting();
		setCurrentValuation(currentValuation);
		if (currentValuation.equals(desc.getValuation())) {
			return desc;
		}
		return parameterizer.instantiate(currentValuation, parameterizerId
				+ ".custom"); //$NON-NLS-1$
	}

	@Override
	public IParamTacticDescriptor getInput() {
		final Object input = tableViewer.getInput();
		if (!(input instanceof IParamTacticDescriptor)) {
			return null;
		}
		return (IParamTacticDescriptor) input;
	}

	private void setCurrentValuation(IParameterSetting paramSetting) {
		final int numParams = tableViewer.getTable().getItemCount();
		for (int i=0;i<numParams;i++) {
			final Param param = (Param) tableViewer.getElementAt(i);
			final String label = param.getDesc().getLabel();
			final Object value = param.getValue();
			paramSetting.set(label, value);
		}
	}

	@Override
	public ISelection getSelection() {
		return tableViewer.getSelection();
	}

	@Override
	public void refresh() {
		tableViewer.refresh();
		
	}

	@Override
	public void setSelection(ISelection selection, boolean reveal) {
		tableViewer.setSelection(selection, reveal);
		
	}
}
