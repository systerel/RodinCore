/*******************************************************************************
 * Copyright (c) 2011 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License  v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package fr.systerel.internal.explorer.navigator.filters;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eventb.core.IPSStatus;

import fr.systerel.internal.explorer.model.ModelController;
import fr.systerel.internal.explorer.model.ModelProofObligation;

/**
 * Implements filtering of discharged proof obligations.
 */
public class ManualPOFilter extends ViewerFilter {

	public ManualPOFilter() {
		// do nothing
	}

	@Override
	public boolean select(Viewer viewer, Object parentElement, Object element) {

		if (element instanceof IPSStatus) {
			final ModelProofObligation modelPO = ModelController
					.getModelPO((IPSStatus) element);
			return !modelPO.isDischarged() || !modelPO.isManual();
		}
		return true;
	}

}
