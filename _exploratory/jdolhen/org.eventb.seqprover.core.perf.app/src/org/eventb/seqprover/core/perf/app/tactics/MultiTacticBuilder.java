/*******************************************************************************
 * Copyright (c) 2013 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.seqprover.core.perf.app.tactics;

import static java.util.Arrays.asList;

import org.eventb.core.seqprover.ITacticDescriptor;
import org.eventb.core.seqprover.ICombinatorDescriptor;

/**
 * For running several external provers, one after the other.
 * 
 * @author Laurent Voisin
 */
public class MultiTacticBuilder extends TacticBuilder {

	private static final ICombinatorDescriptor COMPOSE_UNTIL_SUCCESS //
	= REGISTRY.getCombinatorDescriptor(//
			"org.eventb.core.seqprover.composeUntilSuccess");

	private final TacticBuilder[] builders;

	public MultiTacticBuilder(String id, TacticBuilder... builders) {
		super(id);
		this.builders = builders;
	}

	@Override
	public ITacticDescriptor makeTactic(boolean restricted) {
		final String id = getTacticId(restricted);
		final int len = builders.length;
		final ITacticDescriptor[] descs = new ITacticDescriptor[len];
		for (int i = 0; i < len; i++) {
			descs[i] = builders[i].makeTactic(restricted);
		}
		return COMPOSE_UNTIL_SUCCESS.combine(asList(descs), id);
	}

}
