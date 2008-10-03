/*******************************************************************************
 * Copyright (c) 2006, 2008 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - added history support
 *******************************************************************************/
package org.eventb.internal.ui;

import org.eventb.core.IRefinesMachine;
import org.eventb.internal.ui.eventbeditor.editpage.IAttributeFactory;
import org.eventb.internal.ui.eventbeditor.editpage.RefinesMachineAbstractMachineNameAttributeFactory;
import org.eventb.ui.IElementModifier;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

public class RefinesMachineModifier implements IElementModifier {

	public void modify(IRodinElement element, String text)
			throws RodinDBException {
		if (element instanceof IRefinesMachine) {
			IRefinesMachine aElement = (IRefinesMachine) element;
			IAttributeFactory factory = new RefinesMachineAbstractMachineNameAttributeFactory();
			UIUtils.setStringAttribute(aElement, factory, text, null);
		}
		return;
	}

}
