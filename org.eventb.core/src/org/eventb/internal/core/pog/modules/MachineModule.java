/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.internal.core.pog.modules;

import org.eventb.core.EventBPlugin;
import org.eventb.core.pog.POGCore;
import org.eventb.core.tool.IModuleType;

/**
 * @author Stefan Hallerstede
 *
 */
public class MachineModule extends BaseModule {

	public static final IModuleType<MachineModule> MODULE_TYPE = 
		POGCore.getModuleType(EventBPlugin.PLUGIN_ID + ".machineModule"); //$NON-NLS-1$
	
	public IModuleType<?> getModuleType() {
		return MODULE_TYPE;
	}


}
