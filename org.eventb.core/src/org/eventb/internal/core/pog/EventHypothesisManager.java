/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.internal.core.pog;

import org.eventb.core.ISCEvent;
import org.eventb.core.ISCPredicateElement;
import org.eventb.core.pog.IEventHypothesisManager;
import org.rodinp.core.IRodinElement;

/**
 * @author Stefan Hallerstede
 *
 */
public class EventHypothesisManager extends HypothesisManager implements IEventHypothesisManager {

	public static String HYP_PREFIX = "EVTHYP";
	
	public static String ALLHYP_NAME = "EVTALLHYP";
	
	private ISCEvent[] abstractEvents;
	
	public EventHypothesisManager(
			IRodinElement parentElement, 
			ISCPredicateElement[] predicateTable,
			String rootHypName) {
		super(parentElement, predicateTable, rootHypName, 
				HYP_PREFIX + parentElement.getElementName(), 
				ALLHYP_NAME + parentElement.getElementName());
	}

	public String getStateType() {
		return STATE_TYPE;
	}

	public void setAbstractEvents(ISCEvent[] events) {
		abstractEvents = events;
	}

	public ISCEvent[] getAbstractEvents() {
		return abstractEvents;
	}

	public ISCEvent getFirstAbstractEvent() {
		if (abstractEvents.length == 0)
			return null;
		
		return abstractEvents[0];
	}

}
