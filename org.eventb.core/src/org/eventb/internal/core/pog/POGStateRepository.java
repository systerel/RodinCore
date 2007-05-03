/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.internal.core.pog;

import org.eventb.core.IPOFile;
import org.eventb.core.pog.state.IPOGState;
import org.eventb.core.pog.state.IPOGStateRepository;
import org.eventb.internal.core.tool.state.StateRepository;

/**
 * @author Stefan Hallerstede
 *
 */
public class POGStateRepository extends StateRepository<IPOGState> implements IPOGStateRepository {

	private final IPOFile target;
	
	public POGStateRepository(IPOFile target) {
		super();
		
		assert target.exists();
		
		this.target = target;
	}

	public IPOFile getTarget() {
		return target;
	}

}
