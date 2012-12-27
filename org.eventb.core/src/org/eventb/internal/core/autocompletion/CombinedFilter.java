/*******************************************************************************
 * Copyright (c) 2008, 2012 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.core.autocompletion;

import org.rodinp.core.indexer.IDeclaration;

/**
 * @author Nicolas Beauger
 *
 */
public class CombinedFilter extends AbstractFilter {

	private final AbstractFilter[] filters;

	public CombinedFilter(AbstractFilter... filters) {
		this.filters = filters;
	}
	
	// returns true if any component filter returns true; 
	@Override
	protected boolean propose(IDeclaration declaration) {
		for (AbstractFilter filter: filters) {
			if (filter.propose(declaration)) {
				return true;
			}
		}
		return false;
	}

}
