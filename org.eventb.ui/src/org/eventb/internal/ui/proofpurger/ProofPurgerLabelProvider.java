/*******************************************************************************
 * Copyright (c) 2008, 2012 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.ui.proofpurger;

import org.eclipse.jface.viewers.LabelProvider;
import org.rodinp.core.IRodinElement;

/**
 * ProofPurger specific LabelProvider.
 * 
 * @author Nicolas Beauger
 *
 */
public class ProofPurgerLabelProvider extends LabelProvider {

	public ProofPurgerLabelProvider() {
		// Do nothing
	}
	
	@Override
	public String getText(Object element) {
		if (element instanceof IRodinElement) {
			return ((IRodinElement) element).getElementName();
		}
		return super.getText(element);
	}
}
