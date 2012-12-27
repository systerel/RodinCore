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
	
	@Override
	public boolean dependsOnGoal() {
		return false;
	}

	@Override
	public void addDependenciesTo(Set<Level> dependencies) {
		if (!dependencies.contains(getLevel()))
			dependencies.add(getLevel());
	}

	@Override
	public Level getLevel() {
		return Level.BASE;
	}

	@Override
	public boolean isDefinition() {
		return false;
	}

	@Override
	public void trace(Tracer tracer) {
		// do nothing
	}

	@Override
	public int getDepth() {
		return 0;
	}

}
