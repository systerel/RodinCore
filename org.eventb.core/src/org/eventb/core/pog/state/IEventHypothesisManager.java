/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.pog.state;

import java.util.List;

import org.eventb.core.EventBPlugin;
import org.eventb.core.ISCEvent;

/**
 * @author Stefan Hallerstede
 *
 */
public interface IEventHypothesisManager extends IHypothesisManager {

	final static String STATE_TYPE = EventBPlugin.PLUGIN_ID + ".eventHypothesisManager";

	void setAbstractEvents(ISCEvent[] events);
	
	List<ISCEvent> getAbstractEvents();
	
	ISCEvent getFirstAbstractEvent();
	
}
