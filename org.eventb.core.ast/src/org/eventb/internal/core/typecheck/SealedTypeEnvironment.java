/*******************************************************************************
 * Copyright (c) 2012 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation 
 *******************************************************************************/
package org.eventb.internal.core.typecheck;

import org.eventb.core.ast.ISealedTypeEnvironment;

/**
 * Here we reuse the implementation of type environment, only creating a
 * separate type to guarantee immutability.
 * 
 * @author Vincent Monfort
 */
public final class SealedTypeEnvironment extends TypeEnvironment implements
		ISealedTypeEnvironment {

	protected SealedTypeEnvironment(TypeEnvironment typenv) {
		super(typenv);
	}

	@Override
	public ISealedTypeEnvironment makeSnapshot() {
		// No need to make a copy, as we already are immutable.
		return this;
	}
	
}
