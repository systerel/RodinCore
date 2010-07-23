/*******************************************************************************
 * Copyright (c) 2006, 2008 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - separation of file and root element
 *******************************************************************************/
package org.eventb.internal.core.pog;

import org.eventb.core.IPORoot;
import org.eventb.core.ISCPredicateElement;
import org.eventb.core.pog.state.IContextHypothesisManager;
import org.eventb.core.tool.IStateType;
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
	
	public static final String IDENT_HYP_NAME = "IDENT";
	
	private static final int IDENTIFIER_TABLE_SIZE = 213;

	public ContextHypothesisManager(
			IRodinElement parentElement, 
			IPORoot target,
			ISCPredicateElement[] predicateTable,
			boolean accurate) {
		super(parentElement, target, predicateTable, accurate,
				ABS_HYP_NAME, HYP_PREFIX, ALLHYP_NAME, IDENT_HYP_NAME,
				IDENTIFIER_TABLE_SIZE);
	}

	/* (non-Javadoc)
	 * @see org.eventb.core.state.IState#getStateType()
	 */
	@Override
	public IStateType<?> getStateType() {
		return STATE_TYPE;
	}

	@Override
	public boolean contextIsAccurate() {
		return accurate;
	}

}
