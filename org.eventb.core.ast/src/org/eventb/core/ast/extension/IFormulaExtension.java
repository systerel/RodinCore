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
package org.eventb.core.ast.extension;

import java.util.List;

import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Predicate;

/**
 * @author "Nicolas Beauger"
 * @since 2.0
 */
public interface IFormulaExtension {

	public static enum ExtensionKind {
		ASSOCIATIVE_INFIX_EXPRESSION
		
	}
	
	// TODO rather make classes for associativity and compatibility
	// with addAssociativities(IAssociativityMediator) etc
	// TODO rename everywhere associativity to priority
	public static class Pair<T> {
		public final T left;
		public final T right;
		
		public Pair(T left, T right) {
			this.left = left;
			this.right = right;
		}
	}
	
	void checkPreconditions(Expression[] expressions, Predicate[] predicates);

	String getSyntaxSymbol();

	void toString(IToStringMediator mediator, IExtendedFormula formula);

	Predicate getWDPredicate(IWDMediator wdMediator, IExtendedFormula formula);

	boolean isFlattenable();

	String getId();

	String getGroupId();

	ExtensionKind getKind();

	List<Pair<String>> getCompatibilities();

	List<Pair<String>> getAssociativities();

}
