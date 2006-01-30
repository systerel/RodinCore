/*******************************************************************************
 * Copyright (c) 2005 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.internal.core.protosc;

import org.rodinp.core.IInternalElement;

/**
 * @author halstefa
 *
 */
public interface IContextRule {
	
	public boolean verify(IInternalElement element, ContextCache cache, ISCProblemList problemList);

}
