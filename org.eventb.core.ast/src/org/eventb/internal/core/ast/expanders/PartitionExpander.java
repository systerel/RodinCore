/*******************************************************************************
 * Copyright (c) 2009 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.core.ast.expanders;


import java.util.ArrayList;
import java.util.List;

import org.eventb.core.ast.Expression;
import org.eventb.core.ast.MultiplePredicate;
import org.eventb.core.ast.Predicate;

/**
 * Utility class for expanding the definition of <code>partition</code>
 * formulas.
 * 
 * @author Laurent Voisin
 * @since 1.0.0
 */
public class PartitionExpander extends SmartFactory {

	private final Expression set;
	private final Expression[] components;

	public PartitionExpander(MultiplePredicate predicate) {
		super(predicate.getFactory());

		final Expression[] children = predicate.getChildren();
		this.set = children[0];

		final int nbComps = children.length - 1;
		this.components = new Expression[nbComps];
		System.arraycopy(children, 1, components, 0, nbComps);
	}

	public Predicate expand() {
		// Expected number of conjuncts (actually an upper bound)
		final int initSize = 1 + components.length * components.length / 2;
		final List<Predicate> conjuncts = new ArrayList<Predicate>(initSize);
		conjuncts.add(equals(set, union(set.getType(), components)));
		final int end = components.length - 1;
		for (int i = 0; i < end; i++) {
			for (int j = i + 1; j <= end; j++) {
				conjuncts.add(disjoint(components[i], components[j]));
			}
		}
		return land(conjuncts);
	}
}
