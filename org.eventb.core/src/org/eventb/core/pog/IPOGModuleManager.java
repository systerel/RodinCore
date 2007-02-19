/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.pog;

import org.eventb.core.tool.IModuleType;
import org.eventb.core.tool.IProcessorModule;

/**
 * @author Stefan Hallerstede
 *
 */
public interface IPOGModuleManager {

	public abstract IPOGProcessorModule[] getProcessorModules(IModuleType<? extends IProcessorModule> moduleType);

}