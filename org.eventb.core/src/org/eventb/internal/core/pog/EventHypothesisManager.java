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
import org.eventb.core.pog.state.IEventHypothesisManager;
import org.eventb.core.tool.IStateType;
import org.rodinp.core.IRodinElement;

/**
 * @author Stefan Hallerstede
 *
 */
public class EventHypothesisManager extends HypothesisManager implements IEventHypothesisManager {

	public static final String HYP_PREFIX = "EVTHYP";
	
	public static final String ALLHYP_NAME = "EVTALLHYP";
	
	public static final String IDENT_HYP_NAME = "EVTIDENT";
	
	private static final int IDENTIFIER_TABLE_SIZE = 43;
	
	public EventHypothesisManager(
			IRodinElement parentElement, 
			IPORoot target,
			ISCPredicateElement[] predicateTable,
			boolean accurate,
			String rootHypName) {
		super(parentElement, target, predicateTable, accurate, 
				rootHypName, 
				HYP_PREFIX + parentElement.getElementName(), 
				ALLHYP_NAME + parentElement.getElementName(),
				IDENT_HYP_NAME + parentElement.getElementName(),
				IDENTIFIER_TABLE_SIZE);
	}

	@Override
	public IStateType<?> getStateType() {
		return STATE_TYPE;
	}

	@Override
	public boolean eventIsAccurate() {
		return accurate;
	}

}
