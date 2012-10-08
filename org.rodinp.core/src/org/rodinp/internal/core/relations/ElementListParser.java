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

import org.eclipse.core.runtime.IConfigurationElement;

/**
 * Parser for a list of configuration elements contributed to the
 * <code>itemRelations</code> extension points.
 * 
 * @author Laurent Voisin
 * @author Thomas Muller
 */
public class ElementListParser {

	private final ItemRelationParser parent;
	private final ElementParser[] subParsers;

	public ElementListParser(ItemRelationParser parent,
			ElementParser[] subParsers) {
		this.parent = parent;
		this.subParsers = subParsers;
	}

	public void parse(IConfigurationElement[] elements) {
		for (final IConfigurationElement element : elements) {
			processElement(element);
		}
	}

	private void processElement(IConfigurationElement element) {
		final String name = element.getName();
		for (final ElementParser subParser : subParsers) {
			if (name.equals(subParser.elementName)) {
				subParser.parse(element);
				return;
			}
		}
		parent.addError("Unknown element " + name, element);
	}

}
