/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eventb.internal.pp.core.tracing;

import java.util.Set;

import org.eventb.internal.pp.core.Level;

public interface IOrigin {

	public void trace(Tracer tracer);
	
	// helper
	public void getDependencies(Set<Level> dependencies);
	
	public boolean dependsOnGoal();
	
	public boolean isDefinition();
	
	public Level getLevel();
	
	
	public int getDepth();
}
