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

import org.eventb.core.ast.Formula;
import org.eventb.internal.core.parser.GenParser.SyntaxError;
import org.eventb.internal.core.parser.IndexedSet.OverrideException;

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
	
	// maps operator token kind to formula tag
	private final Map<Integer, Integer> operatorTag = new HashMap<Integer, Integer>();
		
	// maps operator token kind to a subparser
	// TODO when backtracking there will be several subparsers for one kind
	private final Map<Integer, ISubParser> subParsers = new HashMap<Integer, ISubParser>();
	
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
	
	protected void addOperator(String token, int tag, String operatorId, String groupId,
			ISubParser subParser) throws OverrideException {
		opRegistry.addOperator(tag, operatorId, groupId);
		final int kind = tokens.add(token);
		operatorTag.put(kind, tag);
		subParsers.put(kind, subParser);
	}
	
	protected int addReservedSubParser(ISubParser subParser)
			throws OverrideException {
		final int kind = tokens.reserved();
		subParsers.put(kind, subParser);
		return kind;
	}
	
	protected void addClosedSubParser(String open, String close)
			throws OverrideException {
		final int openKind = tokens.add(open);
		final int closeKind = tokens.add(close);
		operatorTag.put(closeKind, Formula.NO_TAG);
		subParsers.put(openKind, new Parsers.ClosedSugar(closeKind));
	}
	
	protected void addLiteralOperator(String token, int tag,
			ISubParser subParser) throws OverrideException {
		final int kind = tokens.add(token);
		operatorTag.put(kind, tag);
		subParsers.put(kind, subParser);
	}
	
}
