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
package org.eventb.core.pog;

import org.rodinp.core.IRodinElement;

/**
 * Common protocol for source elements associated with proof obligations.
 * <p>
 * This interface is not intended to be implemented by clients.
 * </p> 
 * 
 * @author Stefan Hallerstede
 *
 * @since 1.0
 */
public interface IPOGSource {
	
	public String getRole();
	
	public IRodinElement getSource();

}
