/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.internal.core.sc.modules;

import org.eventb.internal.core.sc.Messages;

/**
 * @author Stefan Hallerstede
 *
 */
public class MachineTheoremFreeIdentsModule extends
		MachineFormulaFreeIdentsModule {

	/* (non-Javadoc)
	 * @see org.eventb.internal.core.sc.modules.FormulaFreeIdentsModule#declaredFreeIdentifierErrorMessage()
	 */
	@Override
	protected String declaredFreeIdentifierErrorMessage() {
		return Messages.scuser_TheoremFreeIdentifierError;
	}

}
