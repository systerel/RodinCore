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

import org.eventb.internal.core.ast.extension.IToStringMediator;

/**
 * @author Nicolas Beauger
 *
 */
public interface IParserPrinter<T> {
	
	public static class SubParseResult<T> {
		private final T parsed;
		private final int parsedKind;
		private final boolean isClosed;
		
		public SubParseResult(T parsed, int parsedKind) {
			this(parsed, parsedKind, false);
		}
		
		public SubParseResult(T parsed, int parsedKind, boolean isParenthesized) {
			this.parsed = parsed;
			this.parsedKind = parsedKind;
			this.isClosed = isParenthesized;
		}

		public T getParsed() {
			return parsed;
		}
		
		public int getKind() {
			return parsedKind;
		}
		
		public boolean isClosed() {
			return isClosed;
		}
	}

	// no parse methods, as they are led/nud specific
	
	void toString(IToStringMediator mediator, T toPrint);
}
