/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.internal.core.tool;

import org.eventb.core.tool.IFilterModule;
import org.eventb.core.tool.IModuleType;
import org.eventb.core.tool.IProcessorModule;

/**
 * @author Stefan Hallerstede
 *
 */
public interface IModuleFactory<FM extends IFilterModule, PM extends IProcessorModule> {
	
	FM[] getFilterModules(IModuleType<? extends PM> parent);
	
	PM[] getProcessorModules(IModuleType<? extends PM> parent);
	

}
