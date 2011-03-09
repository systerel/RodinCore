/*******************************************************************************
 * Copyright (c) 2010 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.core.autocompletion;

import java.util.Set;

import org.rodinp.core.indexer.IDeclaration;

/**
 * @author "Nicolas Beauger"
 *
 */
public class EnumeratedFilter extends AbstractFilter {

	private final Set<IDeclaration> accepted;
	
	public EnumeratedFilter(Set<IDeclaration> accepted) {
		this.accepted = accepted;
	}



	@Override
	protected boolean propose(IDeclaration declaration) {
		return accepted.contains(declaration);
	}

}
