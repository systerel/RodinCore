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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Default implementation of {@link INode}.
 * 
 * @author Nicolas Beauger
 * 
 */
public class DefaultNode<T, N extends INode<T, N>> implements INode<T, N> {

	protected final T label;
	protected final List<N> predecessors = new ArrayList<N>();
	protected final List<N> successors = new ArrayList<N>();

	public DefaultNode(T label) {
		this.label = label;
	}

	@Override
	public T getLabel() {
		return label;
	}

	@Override
	public int degree() {
		return predecessors.size();
	}

	@Override
	public List<N> getSuccessors() {
		return Collections.unmodifiableList(successors);
	}

}
