/*******************************************************************************
 * Copyright (c) 2006, 2008 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - separation of file and root element
 *******************************************************************************/
package org.eventb.core.pog.state;

import org.eventb.core.IPORoot;
import org.eventb.core.tool.IStateRepository;

/**
 * This class provides access to the proof obligation generator state repository.
 * 
 * <p>
 * This interface is not intended to be implemented by clients.
 * </p>
 * 
 * @author Stefan Hallerstede
 *
 */
public interface IPOGStateRepository extends IStateRepository<IPOGState> {

	/**
	 * Returns a handle to the PO file to be generated.
	 * This returned PO file is guaranteed to exist.
	 * 
	 * @return a handle to the PO file to be generated
	 */
	IPORoot getTarget();
	
}
