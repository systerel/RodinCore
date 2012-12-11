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
 * separate type for immutable type environment to guarantee its immutable
 * property.
 * 
 * @author Vincent Monfort
 */
public class SealedTypeEnvironment extends TypeEnvironment implements
		ISealedTypeEnvironment {

	protected SealedTypeEnvironment(TypeEnvironment typenv) {
		super(typenv);
	}

	@Override
	public ISealedTypeEnvironment makeSnapshot() {
		return this;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return super.hashCode() << 1;
	}

}
