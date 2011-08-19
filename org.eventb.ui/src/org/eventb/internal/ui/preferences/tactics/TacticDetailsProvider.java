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

import java.util.Collection;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eventb.core.preferences.IPrefMapEntry;
import org.eventb.core.seqprover.IAutoTacticRegistry.ITacticDescriptor;
import org.eventb.core.seqprover.ICombinedTacticDescriptor;
import org.eventb.core.seqprover.IParamTacticDescriptor;
import org.eventb.core.seqprover.IParameterDesc;
import org.eventb.core.seqprover.IParameterValuation;

/**
 * @author Nicolas Beauger
 *
 */
public class TacticDetailsProvider implements IDetailsProvider {

	private static final int STYLE = SWT.BORDER
			| SWT.FILL | SWT.NO_FOCUS | SWT.V_SCROLL;

	private static final GridData GD = new GridData(SWT.FILL, SWT.FILL, true,
			true);

	private static final int EDITABLE_COLUMN = 1;
	
	private final TacticsProfilesCache cache;
	private Composite parent;
	// TODO remove when no more used
	private org.eclipse.swt.widgets.List detailList;
	private Table paramTable;
	
	public TacticDetailsProvider(TacticsProfilesCache cache) {
		this.cache = cache;
	}

	@Override
	public void setParentComposite(Composite parent) {
		if (parent == this.parent) {
			return;
		}
		this.parent = parent;
		disposeAll();
		initAll();
	}
	
	private void initAll() {
		detailList = new org.eclipse.swt.widgets.List(parent, STYLE);
		detailList.setLayoutData(GD);
		detailList.setVisible(false);
		
		paramTable = new Table(parent, STYLE);
		paramTable.setLayoutData(GD);
		paramTable.setVisible(false);
		
	}

	private void disposeAll() {
		if (detailList != null) {
			detailList.dispose();
			detailList = null;
		}
	}

	@Override
	public void putDetails(String element) {
		final IPrefMapEntry<ITacticDescriptor> profile = cache
				.getEntry(element);
		final ITacticDescriptor desc = profile.getValue();
		if (desc instanceof ICombinedTacticDescriptor) {
			//FIXME recursively make a tree
			final List<ITacticDescriptor> tactics = ((ICombinedTacticDescriptor) desc).getCombinedTactics();
			final String[] result = new String[tactics.size()];
			for (int i = 0; i < result.length; i++) {
				result[i] = tactics.get(i).getTacticName();
			}
			detailList.setItems(result);
			detailList.setVisible(true);
		} else if (desc instanceof IParamTacticDescriptor) {
			final IParamTacticDescriptor paramDesc = (IParamTacticDescriptor) desc;
			final IParameterValuation valuation = paramDesc.getValuation();
			final Collection<IParameterDesc> params = valuation.getParameterDescs();
			final TableColumn labelCol = paramTable.getColumn(0);
			labelCol.setText("label");//TODO at init time
			final TableColumn valueCol = paramTable.getColumn(1);
			valueCol.setText("value");
			paramTable.setItemCount(params.size());
			paramTable.setLinesVisible(true);
//			for (IParameterDesc param : params) {
//				final String label = param.getLabel();
//				labelCol.add(label);
//				switch(param.getType()) {
//				case BOOL:
//					final boolean b = valuation.getBoolean(label);
//					// put a checkbox
//					break;
//				case INT:
//					final int i = valuation.getInt(label);
//					// put a text box that accepts only digits
//					break;
//				case LONG:
//					final long l = valuation.getLong(label);
//					// put a text box that accepts only digits
//					break;
//				case STRING:
//					final String s = valuation.getString(label);
//					// put a text box
//					break;
//				
//				}
//			}
			
		} else {
			final String description = desc.getTacticDescription();
			// put a text with the description
		}
	}

	@Override
	public void clear() {
		detailList.removeAll();
		paramTable.clearAll();
	}

}
