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
package org.eventb.internal.core.ast.extension;

import org.eventb.internal.core.parser.AbstractGrammar;

/**
 * @author Nicolas Beauger
 *
 */
public class KindMediator {

	private final AbstractGrammar grammar;
	
	public KindMediator(AbstractGrammar grammar) {
		this.grammar = grammar;
	}

	public int getKind(String operatorImage) {
		final int kind = grammar.getKind(operatorImage);
		return kind;
	}
	
}
