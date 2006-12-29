/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.internal.core.pog;

import org.eventb.core.IPOFile;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.pog.state.IState;
import org.eventb.core.pog.state.IStateRepository;
import org.eventb.internal.core.tool.state.ToolStateRepository;

/**
 * @author Stefan Hallerstede
 *
 */
public class POGStateRepository extends ToolStateRepository<IState> implements IStateRepository {

	private final IPOFile target;
	
	public POGStateRepository(FormulaFactory factory, IPOFile target) {
		super(factory);
		
		assert target.exists();
		
		this.target = target;
	}

	public IPOFile getTarget() {
		return target;
	}

}
