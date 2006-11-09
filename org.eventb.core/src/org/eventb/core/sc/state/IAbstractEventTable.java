/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.sc.state;

import org.eventb.core.EventBPlugin;
import org.eventb.core.ISCMachineFile;

/**
 * @author Stefan Hallerstede
 *
 */
public interface IAbstractEventTable extends IStateSC, Iterable<IAbstractEventInfo> {
	
	final static String STATE_TYPE = EventBPlugin.PLUGIN_ID + ".abstractEventSymbolTable";

	ISCMachineFile getMachineFile();
	
	void putAbstractEventInfo(IAbstractEventInfo info);
	
	IAbstractEventInfo getAbstractEventInfo(String label);
	
	boolean isLocalVariable(String name);
	
}
