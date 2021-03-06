/*******************************************************************************
 * Copyright (c) 2006, 2014 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *******************************************************************************/
package org.eventb.core.pog;

import org.eventb.core.tool.IFilterModule;
import org.eventb.core.tool.IProcessorModule;

/**
 * Default implementation of a proof obligation generator filter module. 
 * 
 * @see IPOGFilterModule
 * @see POGModule
 * 
 * @author Stefan Hallerstede
 * 
 * @since 1.0
 */
public abstract class POGFilterModule extends POGModule implements IPOGFilterModule {

	@Override
	protected final IFilterModule[] getFilterModules() {
		throw new UnsupportedOperationException("Attempt to load submodules in filter module");
	}

	@Override
	protected final IProcessorModule[] getProcessorModules() {
		throw new UnsupportedOperationException("Attempt to load submodules in filter module");
	}

}
