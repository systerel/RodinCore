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

import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Image;


/**
 * This is a LabelProvider for the IStatistics that also shows a label (name)
 * for each line in the table. It is used in the details viewer.
 *
 */
public class StatisticsDetailsLabelProvider implements ITableLabelProvider {
	
	private StatisticsView view;
	
	public StatisticsDetailsLabelProvider(StatisticsView view){
		this.view = view;
	}

	@Override
	public Image getColumnImage(Object element, int columnIndex) {
		return null;
	}

	@Override
	public String getColumnText(Object element, int columnIndex) {
		if (element instanceof IStatistics) {
			IStatistics stats = (IStatistics) element;
			StatisticsColumn column = view.getDetailColumn(columnIndex);
			if (column != null) {
				return column.getLabel(stats);
			}
		}
		return null;
	}

	@Override
	public void addListener(ILabelProviderListener listener) {
		// do nothing

	}

	@Override
	public void dispose() {
		// do nothing

	}

	@Override
	public boolean isLabelProperty(Object element, String property) {
		return false;
	}

	@Override
	public void removeListener(ILabelProviderListener listener) {
		// do nothing

	}

}
