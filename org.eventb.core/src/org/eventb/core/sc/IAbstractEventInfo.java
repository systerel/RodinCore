/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.sc;

import org.eventb.core.ISCEvent;
import org.eventb.core.ast.Assignment;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.Predicate;

/**
 * @author Stefan Hallerstede
 *
 */
public interface IAbstractEventInfo extends Comparable {

	ISCEvent getEvent();
	String getEventLabel();
	
	FreeIdentifier getIdentifier(String name);
	FreeIdentifier[] getIdentifiers();
	
	Predicate[] getGuards();
	Assignment[] getActions();
	
	void setForbidden(boolean value);
	boolean isForbidden();
	
	void setRefined(boolean value);
	boolean isRefined();

}
