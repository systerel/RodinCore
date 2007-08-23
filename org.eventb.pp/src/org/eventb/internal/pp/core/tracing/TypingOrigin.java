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
import org.eventb.internal.pp.core.Tracer;

/**
 * Implementation of {@link IOrigin} for typing clauses.
 * <p>
 * TODO rethink about this
 *
 * @author François Terrier
 *
 */
public class TypingOrigin implements IOrigin {
	
	public boolean dependsOnGoal() {
		return false;
	}

	public void getDependencies(Set<Level> dependencies) {
		if (!dependencies.contains(getLevel()))
			dependencies.add(getLevel());
	}

	public Level getLevel() {
		return Level.base;
	}

	public boolean isDefinition() {
		return false;
	}

	public void trace(Tracer tracer) {
		// do nothing
	}

	public int getDepth() {
		return 0;
	}

}
