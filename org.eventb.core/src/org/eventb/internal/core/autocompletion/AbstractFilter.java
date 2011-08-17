/*******************************************************************************
 * Copyright (c) 2008-2009 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.core.autocompletion;

import java.util.Iterator;
import java.util.Set;

import org.rodinp.core.indexer.IDeclaration;

/**
 * @author Nicolas Beauger
 *
 */
public abstract class AbstractFilter {

	// Returns whether or not to propose the given declaration for completion.
	protected abstract boolean propose(IDeclaration declaration);
	
	public void apply(Set<IDeclaration> declarations) {
		final Iterator<IDeclaration> iter = declarations.iterator();
		while (iter.hasNext()) {
			if (!propose(iter.next())) {
				iter.remove();
			}
		}
	}

}
