/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.sc.symbolTable;

import org.eventb.core.sc.state.IEventRefinesInfo;

/**
 * Symbol info for (concrete) events.
 * <p>
 * This interface is not intended to be implemented by clients.
 * </p>
 * 
 * @author Stefan Hallerstede
 *
 */
public interface IEventSymbolInfo extends ISymbolInfo {

	/**
	 * Returns whether the event is inherited (from an abstract machine).
	 * 
	 * @return whether the event is inherited
	 */
	boolean isInherited();
	
	/**
	 * Returns the refinement information associated with this event, 
	 * or <code>null</code> if there is none.
	 * 
	 * @return the refinement information associated with this event, 
	 * or <code>null</code> if there is none
	 */
	IEventRefinesInfo getRefinesInfo();

}
