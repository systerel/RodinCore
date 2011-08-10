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

import static java.util.Collections.emptyList;

import java.util.List;


import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eventb.core.preferences.IPrefMapEntry;
import org.eventb.core.seqprover.IAutoTacticRegistry.ITacticDescriptor;

/**
 * @author Nicolas Beauger
 *
 */
public class TacticDetailsProvider implements IDetailsProvider {

	private static final GridData GD = new GridData(SWT.FILL, SWT.FILL, true,
			true);

	private final TacticsProfilesCache cache;
	private org.eclipse.swt.widgets.List detailList;
	
	public TacticDetailsProvider(TacticsProfilesCache cache) {
		this.cache = cache;
	}

	@Override
	public void putDetails(String element, Composite parent) {
		if (detailList != null && detailList.getParent() != parent) {
			detailList.dispose();
			detailList = null;
		}
		if (detailList == null) {
			detailList = new org.eclipse.swt.widgets.List(parent, SWT.BORDER
					| SWT.FILL | SWT.NO_FOCUS | SWT.V_SCROLL);
			detailList.setLayoutData(GD);
			detailList.setVisible(true);
		}
		final IPrefMapEntry<List<ITacticDescriptor>> profile = cache
				.getEntry(element);
		final List<ITacticDescriptor> tactics;
		if (profile == null) {
			tactics = emptyList();
		} else {
			tactics = profile.getValue();
		}
		final String[] result = new String[tactics.size()];
		for (int i = 0; i < result.length; i++) {
			result[i] = tactics.get(i).getTacticName();
		}
		detailList.setItems(result);
	}

	@Override
	public void clear() {
		detailList.removeAll();
	}

}
