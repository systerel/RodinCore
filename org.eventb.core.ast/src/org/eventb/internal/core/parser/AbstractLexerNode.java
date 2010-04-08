/*******************************************************************************
 * Copyright (c) 2010 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.core.parser;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * An abstract node in the recognition graph used by the parameterizable lexer.
 * 
 * @author Christophe Métayer
 * @since 2.0
 */
public abstract class AbstractLexerNode {
	Map<Character, LexerNode> nodes;
    protected final GenScan genScan;

	public AbstractLexerNode(GenScan gs) {
		this.genScan = gs;
	}

	public abstract void progress();

	public void adopt(String n, int kind, boolean ident) {
		assert n.length() != 0;
		final Character charAt = new Character(n.charAt(0));
		final boolean firstIdent = ident && genScan.firstIdent(charAt);
		if (nodes == null) {
			nodes = new HashMap<Character, LexerNode>();
		}
		if (!nodes.containsKey(charAt)) {
			final LexerNode node = new LexerNode(genScan, n.substring(1, n.length()),
					kind, firstIdent, true);
			nodes.put(charAt, node);
		} else {
			// We have a common prefix
			final LexerNode node = nodes.get(charAt);
			nodes.put(charAt, node.add(n.substring(1), kind, firstIdent));
		}
	}

	public void toString(StringBuilder b, int i) {
		if (nodes != null) {
			for (Entry<Character, LexerNode> e : nodes.entrySet()) {
				for (int j = 0; j < i; j++) {
					b.append('\t');
				}
				b.append(e.getKey());
				e.getValue().toString(b, i + 1);
			}
		}
	}
}
