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

import java.util.Arrays;

import org.eventb.internal.pp.core.Level;
import org.eventb.internal.pp.core.elements.Clause;
import org.eventb.internal.pp.core.provers.extensionality.ExtensionalityProver;

/**
 * Implementation of {@link IOrigin} for clauses that come from the
 * application of the axiom of extensionality.
 *
 * @author François Terrier
 * @see ExtensionalityProver
 *
 */
public class ExtensionalityOrigin extends AbstractInferrenceOrigin { 
	private Level level;
	
	public ExtensionalityOrigin(Clause parent) {
		super(parent);
		
		this.level = parent.getLevel();
	}
	
	public ExtensionalityOrigin(Clause parent1, Clause parent2) {
		super(Arrays.asList(new Clause[]{parent1,parent2}));
		
		this.level = parent1.getLevel().isAncestorOf(parent2.getLevel())?parent2.getLevel():parent1.getLevel();
	}

	@Override
	public Level getLevel() {
		return level;
	}

}
