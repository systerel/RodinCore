/*******************************************************************************
 * Copyright (c) 2011 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.rodinp.core;

/**
 * Common protocol for refinement registry. The refinement registry can be
 * obtained by calling {@link RodinCore#getRefinementRegistry()}.
 * 
 * @author Nicolas Beauger
 * @since 1.4
 * 
 */
public interface IRefinementRegistry {

	IInternalElementType<?> getRootType(String refinementId);

}