/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.sc.state;

import org.eventb.core.EventBPlugin;
import org.eventb.core.IEvent;
import org.eventb.core.sc.IFilterModule;
import org.eventb.core.sc.symbolTable.IEventSymbolInfo;

/**
 * The static checker protocol does not permit to pass the current event as
 * a parameter to filter modules {@link IFilterModule} for elements contained in an event.
 * This state component can be used to access the current event instead.
 * 
 * @author Stefan Hallerstede
 *
 */
public interface ICurrentEvent extends IState {
	
	final static String STATE_TYPE = EventBPlugin.PLUGIN_ID + ".currentEvent";

	/**
	 * Returns the current event.
	 * 
	 * @return the current event
	 */
	IEvent getCurrentEvent();
	
	/**
	 * Returns the symbol info for the event, or <code>null</code> if no symbol info
	 * has been generated for the event.
	 * 
	 * @return the symbol info for the event, or <code>null</code> if no symbol info
	 * has been generated for the event
	 */
	IEventSymbolInfo getCurrentEventSymbolInfo();

}
