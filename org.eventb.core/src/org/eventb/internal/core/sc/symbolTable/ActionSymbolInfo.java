/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.internal.core.sc.symbolTable;

import org.eventb.internal.core.sc.Messages;
import org.rodinp.core.IRodinElement;

/**
 * @author Stefan Hallerstede
 *
 */
public class ActionSymbolInfo extends LabelSymbolInfo {

	public ActionSymbolInfo(String symbol, IRodinElement element, String component) {
		super(symbol, element, component);
	}

	/* (non-Javadoc)
	 * @see org.eventb.internal.core.sc.symbolTable.LabelSymbolInfo#getLabelConflictMessage()
	 */
	@Override
	public String getLabelConflictMessage() {
		return Messages.scuser_ActionLabelConflict;
	}

}
