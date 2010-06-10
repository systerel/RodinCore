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

import org.eventb.core.ast.Expression;
import org.eventb.core.ast.extension.IExtendedFormula;

/**
 * @author Nicolas Beauger
 * 
 */
public class ExtensionPrinters {

	public static interface IExtensionPrinter {
		void toString(IToStringMediator mediator, IExtendedFormula formula);
	}

	public static final IExtensionPrinter BINARY_INFIX_EXPR_PRINTER = new IExtensionPrinter() {
		public void toString(IToStringMediator mediator,
				IExtendedFormula formula) {
			final Expression[] childExpressions = formula.getChildExpressions();
			mediator.append(childExpressions[0], false);
			mediator.appendOperator();
			mediator.append(childExpressions[1], true);
		}

	};

	public static final IExtensionPrinter ASSOC_INFIX_EXPR_PRINTER = new IExtensionPrinter() {

		public void toString(IToStringMediator mediator,
				IExtendedFormula formula) {
			final Expression[] childExpressions = formula.getChildExpressions();
			mediator.append(childExpressions[0], false);
			for (int i = 1; i < childExpressions.length; i++) {
				mediator.appendOperator();
				mediator.append(childExpressions[i], true);
			}
		}

	};

	public static final IExtensionPrinter PAREN_PREFIX_EXPR_PRINTER = new IExtensionPrinter() {

		public void toString(IToStringMediator mediator,
				IExtendedFormula formula) {
			mediator.appendOperator();
			mediator.append("(");
			final Expression[] childExpressions = formula.getChildExpressions();
			mediator.append(childExpressions[0], false);
			for (int i = 1; i < childExpressions.length; i++) {
				mediator.append(",");
				mediator.append(childExpressions[i], true);
			}
			mediator.append(")");
		}
	};
}
