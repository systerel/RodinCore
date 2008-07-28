/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.pog.state;

import org.eventb.core.EventBPlugin;
import org.eventb.core.ISCTheorem;
import org.eventb.core.pog.POGCore;
import org.eventb.core.tool.IStateType;

/**
 * Common protocol for accessing all theorems of a context.
 * 
 * <p>
 * This interface is not intended to be implemented by clients.
 * </p>
 * 
 * @author Stefan Hallerstede
 *
 */
public interface IContextTheoremTable extends IPredicateTable<ISCTheorem> {
	
	final static IStateType<IContextTheoremTable> STATE_TYPE = 
		POGCore.getToolStateType(EventBPlugin.PLUGIN_ID + ".contextTheoremTable");

}
