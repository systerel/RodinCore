/*******************************************************************************
 * Copyright (c) 2006, 2012 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.core.sc.modules;

import org.eventb.core.EventBAttributes;
import org.eventb.core.EventBPlugin;
import org.eventb.core.sc.SCCore;
import org.eventb.core.tool.IModuleType;
import org.rodinp.core.IAttributeType;

/**
 * @author Stefan Hallerstede
 * 
 */
public class MachineInvariantFreeIdentsModule extends
		MachineFormulaFreeIdentsModule {

	public static final IModuleType<MachineInvariantFreeIdentsModule> MODULE_TYPE = SCCore
			.getModuleType(EventBPlugin.PLUGIN_ID
					+ ".machineInvariantFreeIdentsModule"); //$NON-NLS-1$

	@Override
	public IModuleType<?> getModuleType() {
		return MODULE_TYPE;
	}

	@Override
	protected IAttributeType.String getAttributeType() {
		return EventBAttributes.PREDICATE_ATTRIBUTE;
	}

}
