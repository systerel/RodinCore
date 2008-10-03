/*******************************************************************************
 * Copyright (c) 2007, 2008 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - added history support
 *******************************************************************************/
package org.eventb.internal.ui.propertiesView;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.IConvergenceElement;
import org.eventb.core.IConvergenceElement.Convergence;
import org.eventb.internal.ui.UIUtils;
import org.eventb.internal.ui.eventbeditor.editpage.ConvergenceAttributeFactory;
import org.rodinp.core.RodinDBException;

public class ConvergenceSection extends CComboSection {

	@Override
	String getLabel() {
		return "Conv.";
	}

	@Override
	String getText() throws RodinDBException {
		IConvergenceElement cElement = (IConvergenceElement) element;
		String defaultConv = ConvergenceAttributeFactory.ORDINARY;
		try {
			if (!cElement.hasConvergence())
				return defaultConv;
			Convergence convergence = cElement.getConvergence();
			if (convergence == Convergence.ORDINARY)
				return ConvergenceAttributeFactory.ORDINARY;
			if (convergence == Convergence.CONVERGENT)
				return ConvergenceAttributeFactory.CONVERGENT;
			if (convergence == Convergence.ANTICIPATED)
				return ConvergenceAttributeFactory.ANTICIPATED;
			return defaultConv;
		} catch (RodinDBException e) {
			return defaultConv;
		}

	}

	@Override
	void setData() {
		ConvergenceAttributeFactory factory = new ConvergenceAttributeFactory();
		for(String value : factory.getPossibleValues(element, null)){
			comboWidget.add(value);
		}
//		comboWidget.add(ORDINARY);
//		comboWidget.add(CONVERGENT);
//		comboWidget.add(ANTICIPATED);
	}

	@Override
	void setText(String text, IProgressMonitor monitor) throws RodinDBException {
		UIUtils.setStringAttribute(element, new ConvergenceAttributeFactory(),
				text, monitor);
	}
}
