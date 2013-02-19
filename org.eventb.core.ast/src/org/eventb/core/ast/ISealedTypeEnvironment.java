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
package org.eventb.core.ast;

/**
 * This type represents a sealed (immutable) type environment. It is the same
 * interface as {@link ITypeEnvironment} but the type guarantees the immutable
 * state of the environment.
 * <p>
 * See {@link ITypeEnvironment} for the general description.
 * </p>
 * 
 * @since 3.0
 */
public interface ISealedTypeEnvironment extends ITypeEnvironment {

	@Override
	public ISealedTypeEnvironment translate(FormulaFactory factory);

}
