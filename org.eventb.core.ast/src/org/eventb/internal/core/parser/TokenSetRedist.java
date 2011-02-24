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
package org.eventb.internal.core.parser;

import static org.eventb.internal.core.parser.TokenSet.FIRST_KIND;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eventb.internal.core.parser.operators.ExternalViewUtils.Instantiator;

/**
 * @author Nicolas Beauger
 * 
 */
public class TokenSetRedist {

	// given instantiator is a bijection on a subset of tokenSet kinds
	// also completes opKindInst with uninstantiated kinds
	public TokenSet redistribute(TokenSet tokenSet,
			Instantiator<Integer, Integer> opKindInst) {
		completeInst(tokenSet, opKindInst);

		final Map<String, Integer> newLexTokens = new HashMap<String, Integer>();
		final Map<String, Integer> newReserved = new HashMap<String, Integer>();

		for (Integer kind = 0; kind < tokenSet.size(); kind++) {
			final String image = tokenSet.getImage(kind);
			if (!opKindInst.hasInst(kind)) {
				throw new IllegalStateException(
						"expected kind instantiation for kind=" + kind
								+ " image=" + image);
			}
			final Integer newKind = opKindInst.instantiate(kind);
			if (tokenSet.isReserved(kind)) {
				newReserved.put(image, newKind);
			} else {
				newLexTokens.put(image, newKind);
			}
		}

		return new TokenSet(newLexTokens, newReserved);
	}

	private void completeInst(TokenSet tokenSet, Instantiator<Integer, Integer> opKindInst) {
		final List<Integer> uninstantiated = new ArrayList<Integer>();
		final Collection<Integer> newKinds = opKindInst.values();
		final List<Integer> freeNewKinds = new ArrayList<Integer>();
		for (Integer kind = FIRST_KIND; kind < FIRST_KIND + tokenSet.size(); kind++) {
			if (!opKindInst.hasInst(kind)) {
				uninstantiated.add(kind);
			}
			if (!newKinds.contains(kind)) {
				freeNewKinds.add(kind);
			}
		}
		assert uninstantiated.size() == freeNewKinds.size();

		for (int i = 0; i < uninstantiated.size(); i++) {
			opKindInst.setInst(uninstantiated.get(i), freeNewKinds.get(i));
		}
	}

}
