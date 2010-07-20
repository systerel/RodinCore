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

import static org.eventb.core.ast.extension.IOperatorProperties.BINARY;
import static org.eventb.core.ast.extension.IOperatorProperties.MULTARY_2;
import static org.eventb.core.ast.extension.IOperatorProperties.NULLARY;
import static org.eventb.core.ast.extension.IOperatorProperties.FormulaType.EXPRESSION;
import static org.eventb.core.ast.extension.IOperatorProperties.Notation.INFIX;
import static org.eventb.core.ast.extension.IOperatorProperties.Notation.PREFIX;
import static org.eventb.internal.core.ast.extension.OperatorProperties.makeOperProps;

import org.eventb.core.ast.ExtendedExpression;
import org.eventb.core.ast.extension.IOperatorProperties;
import org.eventb.core.ast.extension.IOperatorProperties.Arity;

/**
 * @author Nicolas Beauger
 * TODO implement for all parsers then move to appropriate classes
 */
public class ParserInfos  {

	public static enum ExtendedExpressionParsers implements
			IPropertyParserInfo<ExtendedExpression> {

		EXTENDED_ATOMIC_EXPRESSION(makeOperProps(PREFIX, EXPRESSION, NULLARY, EXPRESSION, false), true) {

			public IParserPrinter<ExtendedExpression> makeParser(int kind, int tag) {
				return new SubParsers.ExtendedAtomicExpressionParser(kind, tag);
			}

		},

		EXTENDED_BINARY_EXPRESSION(makeOperProps(INFIX, EXPRESSION, BINARY, EXPRESSION, false), true) {

			public IParserPrinter<ExtendedExpression> makeParser(int kind, int tag) {
				return new SubParsers.ExtendedBinaryExpressionInfix(kind, tag);
			}
		},

		EXTENDED_ASSOCIATIVE_EXPRESSION(makeOperProps(INFIX, EXPRESSION, MULTARY_2, EXPRESSION, true), true) {

			public IParserPrinter<ExtendedExpression> makeParser(int kind, int tag) {
				return new SubParsers.ExtendedAssociativeExpressionInfix(kind, tag);
			}
		},
		
		// the arity given here stands for 'any fixed arity in 1 .. MAX_ARITY'
		PARENTHESIZED_EXPRESSION(makeOperProps(PREFIX, EXPRESSION, new Arity(1,
				IOperatorProperties.MAX_ARITY), EXPRESSION, false), true) {

			public IParserPrinter<ExtendedExpression> makeParser(int kind, int tag) {
				return new SubParsers.ExtendedExprParen(kind, tag);
			}
		},
	
		;
		
		private final IOperatorProperties operProps;
		private final boolean isExtension;
		
		private ExtendedExpressionParsers(IOperatorProperties operProps,
				boolean isExtension) {
			this.operProps = operProps;
			this.isExtension = isExtension;
		}
		
		public IOperatorProperties getProperties() {
			return operProps;
		}

		public boolean isExtension() {
			return isExtension;
		}

		
	}
}
