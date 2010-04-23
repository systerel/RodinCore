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

import org.eventb.internal.core.parser.GenParser.SyntaxError;

/**
 * @author Nicolas Beauger
 *
 */
public abstract class AbstractGrammar {

	protected static class SyntaxCompatibleError extends SyntaxError {

		private static final long serialVersionUID = -6230478311681172354L;

		public SyntaxCompatibleError(String reason) {
			super(reason);
		}
	}

	protected final IndexedSet<String> tokens = new IndexedSet<String>();
	protected final Map<Integer, Integer> operatorTag = new HashMap<Integer, Integer>();
		
	protected final Map<Integer, ISubParser> subParsers = new HashMap<Integer, ISubParser>();
	
	protected final OperatorRegistry opRegistry = new OperatorRegistry();
	
	public OperatorRegistry getOperatorRegistry() {
		return opRegistry;
	}
	
	
	public int getOperatorTag(Token token) throws SyntaxError {
		final Integer tag = operatorTag.get(token.kind);
		if (tag == null) {
			throw new SyntaxError("not an operator: " + token.val);
		}
		return tag;
	}
	
	public IndexedSet<String> getTokens() {
		return tokens;
	}

	// TODO split into several init methods, one for each data
	public abstract void init();

	public ISubParser getSubParser(int kind) {
		return subParsers.get(kind);
	}
	
}
