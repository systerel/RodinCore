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
package org.eventb.core.tool;

import org.eventb.core.pog.IPOGFilterModule;
import org.eventb.core.sc.ISCFilterModule;


/**
 * Basic type for filter modules. A filter module is one of the two kinds of
 * modules ({@link IModule}) used by a core tool.
 * <p>
 * Filter modules are intended to evaluate a boolean condition on some element.
 * It is implemented by the static checker and the proof obligation generator.
 * </p>
 * 
 * @see IModule
 * @see ISCFilterModule
 * @see IPOGFilterModule
 * 
 * @author Stefan Hallerstede
 * @since 3.0
 * @noimplement This interface is not intended to be implemented by clients.
 *              Extend a provided implementation class instead.
 */
public interface IFilterModule extends IModule {

	// basic type for filter modules 
	
}
