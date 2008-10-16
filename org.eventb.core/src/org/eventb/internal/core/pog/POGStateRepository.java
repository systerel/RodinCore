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
package org.eventb.internal.core.pog;

import org.eventb.core.IPORoot;
import org.eventb.core.pog.state.IPOGState;
import org.eventb.core.pog.state.IPOGStateRepository;
import org.eventb.internal.core.tool.state.StateRepository;

/**
 * @author Stefan Hallerstede
 *
 */
public class POGStateRepository extends StateRepository<IPOGState> implements IPOGStateRepository {

	private final IPORoot target;
	
	public POGStateRepository(IPORoot target) {
		super();
		
		assert target.exists();
		
		this.target = target;
	}

	public IPORoot getTarget() {
		return target;
	}

}
