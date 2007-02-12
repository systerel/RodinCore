/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.internal.core.sc;

import org.eventb.core.sc.ISCFilterModule;


/**
 * @author Stefan Hallerstede
 *
 */
interface IFilterCreator extends IModuleCreator {

	ISCFilterModule[] create();
	
}
