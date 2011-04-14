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
package org.rodinp.internal.core.indexer.sort;

import java.util.List;

/**
 * @author Nicolas Beauger
 * 
 * @param <T>
 */
public interface INode<T, N extends INode<T, ?>> {

	T getLabel();

	/**
	 * @return
	 */
	int degree();

	List<N> getSuccessors();

}