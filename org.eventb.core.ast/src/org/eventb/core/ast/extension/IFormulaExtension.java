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

import static org.eventb.internal.core.ast.extension.ExtensionPrinters.INFIX_EXPR_PRINTER;
import static org.eventb.internal.core.ast.extension.ExtensionPrinters.PAREN_PREFIX_EXPR_PRINTER;
import static org.eventb.internal.core.ast.extension.PrecondChecker.NO_LIMIT;

import org.eventb.core.ast.Predicate;
import org.eventb.internal.core.ast.extension.PrecondChecker;
import org.eventb.internal.core.ast.extension.ExtensionPrinters.IExtensionPrinter;

/**
 * @author "Nicolas Beauger"
 * @since 2.0
 */
public interface IFormulaExtension {

	public static enum ExtensionKind {
		// a op b
		BINARY_INFIX_EXPRESSION(new PrecondChecker(2, 2, 0, 0),
				INFIX_EXPR_PRINTER, false),

		// a op b op ... op c
		ASSOCIATIVE_INFIX_EXPRESSION(new PrecondChecker(2, NO_LIMIT, 0, 0),
				INFIX_EXPR_PRINTER, true),

		// op(a, b, ..., c)
		PARENTHESIZED_PREFIX_EXPRESSION(new PrecondChecker(2, NO_LIMIT, 0, 0),
				PAREN_PREFIX_EXPR_PRINTER, false);

		// TODO PARENTHESIZED_PREFIX_PREDICATE

		private final PrecondChecker precondChecker;
		private final IExtensionPrinter printer;
		private final boolean flattenable;
		
		private ExtensionKind(PrecondChecker precondChecker, IExtensionPrinter printer, boolean flattenable) {
			this.precondChecker = precondChecker;
			this.printer = printer;
			this.flattenable = flattenable;
		}
		
		public PrecondChecker getPrecondChecker() {
			return precondChecker;
		}
		
		public IExtensionPrinter getPrinter() {
			return printer;
		}
		
		public boolean isFlattenable() {
			return flattenable;
		}
	}

	String getSyntaxSymbol();

	Predicate getWDPredicate(IWDMediator wdMediator, IExtendedFormula formula);

	String getId();

	String getGroupId();

	ExtensionKind getKind();

	void addCompatibilities(ICompatibilityMediator mediator);

	void addPriorities(IPriorityMediator mediator);

}
