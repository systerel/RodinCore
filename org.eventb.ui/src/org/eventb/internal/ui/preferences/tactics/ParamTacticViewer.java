/*******************************************************************************
 * Copyright (c) 2011 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.ui.preferences.tactics;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eventb.core.seqprover.IParamTacticDescriptor;
import org.eventb.core.seqprover.IParameterDesc;
import org.eventb.core.seqprover.IParameterValuation;

/**
 * @author Nicolas Beauger
 * 
 */
public class ParamTacticViewer {

	private static class Param {
		final IParameterDesc desc;
		final String value;

		public Param(IParameterDesc desc, String value) {
			super();
			this.desc = desc;
			this.value = value;
		}

		public IParameterDesc getDesc() {
			return desc;
		}

		public String getValue() {
			return value;
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
			switch (columnIndex) {
			case 0: // label
				return desc.getLabel();
			case 1: // type
				return desc.getType().toString();
			case 2: // value
				return param.getValue();
			case 3: // default
				return desc.getDefaultValue().toString();
			case 4: // description
				return desc.getDescription();
			default:
				return null;
			}
		}

	}

	private static class ParamContentProvider implements
			IStructuredContentProvider {

		public ParamContentProvider() {
			// TODO Auto-generated constructor stub
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
				final String value = valuation.get(label).toString();
				result.add(new Param(param, value));
			}
			return result.toArray(new Object[result.size()]);
		}
	}

	private TableViewer tableViewer;

	public void createContents(Composite parent) {
		tableViewer = new TableViewer(parent);
		createColumns();
		tableViewer.setLabelProvider(new ParamLabelProvider());
		tableViewer.setContentProvider(new ParamContentProvider());
	}

	private void createColumns() {
		final Table table = tableViewer.getTable();
		table.setLayout(new RowLayout(SWT.VERTICAL));
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		final String[] names = new String[] { "label", "type", "value",
				"default", "description" };
		for (String name : names) {
			final TableColumn col = new TableColumn(table, SWT.NONE);
			col.setText(name);
			col.pack(); // TODO that may be the problem with container in
						// details provider, check StatisticsView
		}
	}

	public void setInput(IParamTacticDescriptor desc) {
		if (tableViewer == null) {
			return;
		}
		tableViewer.setInput(desc);
	}

	public void dispose() {
		if (tableViewer != null) {
			tableViewer.getTable().dispose();
		}
	}

}
