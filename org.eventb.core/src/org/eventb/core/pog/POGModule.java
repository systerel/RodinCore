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

import org.eventb.internal.core.tool.Module;
import org.eventb.internal.core.tool.types.IModule;

/**
 * This is the base class of all proof obligation generator modules.
 * 
 * @see IModule
 * @see IPOGFilterModule
 * @see IPOGProcessorModule
 * 
 * @author Stefan Hallerstede
 *
 * @since 1.0
 */
public abstract class POGModule extends Module {
	
	public static boolean DEBUG_MODULE = false;

}
