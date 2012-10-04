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
package org.rodinp.internal.core.relations;

import static java.util.Collections.emptyList;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IConfigurationElement;

/**
 * Parser for the <code>itemRelations</code> extension point. Errors encountered
 * during parsing are available from {@link #getErrors()} to log them.
 * 
 * @author Laurent Voisin
 */
public class ItemRelationParser {

	// FIXME Store parse errors locally.

	public List<ItemRelation> parse(IConfigurationElement[] elems) {
		final List<ItemRelation> result = new ArrayList<ItemRelation>();
		for (final IConfigurationElement elem : elems) {
			// FIXME take care that errors do not break loop
			final ItemRelation parsedRelation = parse(elem);
			result.add(parsedRelation);
		}
		return result;
	}

	// public for testing purpose
	public ItemRelation parse(IConfigurationElement elem) {
		// TODO Auto-generated method stub
		return null;
	}

	public List<String> getErrors() {
		// FIXME to be implemented
		return emptyList();
	}

}
