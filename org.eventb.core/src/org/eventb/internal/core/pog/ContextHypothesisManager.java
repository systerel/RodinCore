/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.internal.core.pog;

import org.eventb.core.ISCPredicateElement;
import org.eventb.core.pog.state.IContextHypothesisManager;
import org.rodinp.core.IRodinElement;

/**
 * @author Stefan Hallerstede
 *
 */
public class ContextHypothesisManager extends HypothesisManager implements
		IContextHypothesisManager {

	public static final String ABS_HYP_NAME = "ABSHYP";
	
	public static final String HYP_PREFIX = "HYP";
	
	public static final String ALLHYP_NAME = "ALLHYP";
	
	private static final int IDENTIFIER_TABLE_SIZE = 213;

	public ContextHypothesisManager(
			IRodinElement parentElement, 
			ISCPredicateElement[] predicateTable) {
		super(parentElement, predicateTable, ABS_HYP_NAME, HYP_PREFIX, ALLHYP_NAME,
				IDENTIFIER_TABLE_SIZE);
	}

	/* (non-Javadoc)
	 * @see org.eventb.core.state.IState#getStateType()
	 */
	public String getStateType() {
		return STATE_TYPE;
	}

}
