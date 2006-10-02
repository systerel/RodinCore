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
public class ContextPredicateFreeIdentsModule extends FormulaFreeIdentsModule {

	@Override
	protected String declaredFreeIdentifierErrorMessage() {
		return Messages.scuser_PredicateFreeIdentifierError;
	}

	// uses default implementation
	
	
}
