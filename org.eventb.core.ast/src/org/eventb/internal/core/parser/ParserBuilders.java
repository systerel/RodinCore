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

import static org.eventb.core.ast.extension.IOperatorProperties.Arity.BINARY;
import static org.eventb.core.ast.extension.IOperatorProperties.Arity.*;
import static org.eventb.core.ast.extension.IOperatorProperties.Arity.NULLARY;
import static org.eventb.core.ast.extension.IOperatorProperties.Notation.INFIX;
import static org.eventb.core.ast.extension.IOperatorProperties.Notation.PREFIX;
import static org.eventb.core.ast.extension.IOperatorProperties.FormulaType.EXPRESSION;
import static org.eventb.internal.core.ast.extension.ExtensionPrinters.*;
import static org.eventb.internal.core.ast.extension.ExtensionPrinters.INFIX_EXPR_PRINTER;
import static org.eventb.internal.core.ast.extension.OperatorProperties.makeOperProps;

import org.eventb.core.ast.extension.IOperatorProperties;
import org.eventb.internal.core.ast.extension.ExtensionPrinters.IExtensionPrinter;

/**
 * @author Nicolas Beauger
 *
 */
public class ParserBuilders  {

	public static enum ExtendedParsers implements IParserInfo {

		EXTENDED_ATOMIC_EXPRESSION(makeOperProps(PREFIX, EXPRESSION, NULLARY, EXPRESSION), ATOMIC_EXPR_PRINTER, true) {

			public IParserPrinter makeParser(int tag) {
				return new SubParsers.ExtendedAtomicExpressionParser(tag);
			}

		},

		EXTENDED_BINARY_EXPRESSION(makeOperProps(INFIX, EXPRESSION, BINARY, EXPRESSION), INFIX_EXPR_PRINTER, true) {

			public IParserPrinter makeParser(int tag) {
				return new SubParsers.ExtendedBinaryExpressionInfix(tag);
			}
		},

		EXTENDED_ASSOCIATIVE_EXPRESSION(makeOperProps(INFIX, EXPRESSION, MULTARY_2, EXPRESSION), INFIX_EXPR_PRINTER, true) {

			public IParserPrinter makeParser(int tag) {
				return new SubParsers.ExtendedAssociativeExpressionInfix(tag);
			}
		},
		
		// FIXME cannot chose other cardinality => do not fix it here
		PARENTHESIZED_EXPRESSION_1(makeOperProps(PREFIX, EXPRESSION, MULTARY_1, EXPRESSION), PAREN_PREFIX_EXPR_PRINTER, true) {

			public IParserPrinter makeParser(int tag) {
				return new SubParsers.ExtendedExprParen(tag);
			}
		},
		
		PARENTHESIZED_EXPRESSION_2(makeOperProps(PREFIX, EXPRESSION, MULTARY_2, EXPRESSION), PAREN_PREFIX_EXPR_PRINTER, true) {

			public IParserPrinter makeParser(int tag) {
				return new SubParsers.ExtendedExprParen(tag);
			}
		},
		;
		
		private final IOperatorProperties operProps;
		private final IExtensionPrinter printer;
		private final boolean isExtension;
		
		private ExtendedParsers(IOperatorProperties operProps,
				IExtensionPrinter printer, boolean isExtension) {
			this.operProps = operProps;
			this.printer = printer;
			this.isExtension = isExtension;
		}
		
		public IOperatorProperties getProperties() {
			return operProps;
		}

		public IExtensionPrinter getPrinter() {
			return printer;
		}

		public boolean isExtension() {
			return isExtension;
		}

	}
}
