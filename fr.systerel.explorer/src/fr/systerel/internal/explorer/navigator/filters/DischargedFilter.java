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

package fr.systerel.internal.explorer.navigator.filters;

import static fr.systerel.internal.explorer.model.ModelController.getModelPO;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eventb.core.IPSStatus;

import fr.systerel.internal.explorer.model.ModelProofObligation;

/**
 * Implements filtering of discharged proof obligations.
 */
public class DischargedFilter extends ViewerFilter {

	private boolean active = false;

	public DischargedFilter() {
		// do nothing
	}

	@Override
	public boolean select(Viewer viewer, Object parentElement, Object element) {

		if (element instanceof IPSStatus) {
			if (!active) {
				// No filtering on discharged POs
				return true;
			}

			final ModelProofObligation modelPO = getModelPO((IPSStatus) element);
			if (modelPO == null) {
				// PO not in model, no filtering
				return true;
			}
			return !modelPO.isDischarged();
		}
		return true;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

}
