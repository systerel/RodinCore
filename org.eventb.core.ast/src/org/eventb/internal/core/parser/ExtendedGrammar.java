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

import java.util.Set;

import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.extension.IFormulaExtension;
import org.eventb.core.ast.extension.IFormulaExtension.ExtensionKind;
import org.eventb.internal.core.ast.extension.CompatibilityMediator;
import org.eventb.internal.core.ast.extension.PriorityMediator;
import org.eventb.internal.core.parser.GenParser.OverrideException;

/**
 * @author Nicolas Beauger
 *
 */
public class ExtendedGrammar extends BMath {

	private final Set<IFormulaExtension> extensions;

	public ExtendedGrammar(Set<IFormulaExtension> extensions) {
		this.extensions = extensions;
	}
	

	@Override
	public void init() {
		super.init();
		try {
			for (IFormulaExtension extension : extensions) {
				final int tag = FormulaFactory.getTag(extension);
				final String operatorId = extension.getId();
				final String groupId = extension.getGroupId();
				final ExtensionKind kind = extension.getKind();
				if (isLed(kind)) {
				final ILedParser<? extends Formula<?>> subParser = makeLedParser(
						kind, tag);
				addOperator(extension.getSyntaxSymbol(), operatorId, groupId,
						subParser);
				} else {
					final INudParser<? extends Formula<?>> subParser = makeNudParser(
							kind, tag);
					addOperator(extension.getSyntaxSymbol(), operatorId, groupId,
							subParser);
				}
			}
		} catch (OverrideException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for (IFormulaExtension extension : extensions) {
			extension.addCompatibilities(new CompatibilityMediator(opRegistry));
			extension.addPriorities(new PriorityMediator(opRegistry));

		}
	}

	private static boolean isLed(ExtensionKind kind) {
		return kind != ExtensionKind.PARENTHESIZED_PREFIX_EXPRESSION;
	}

	private static ILedParser<? extends Formula<?>> makeLedParser(
			ExtensionKind kind, int tag) {
		final ILedParser<? extends Formula<?>> subParser;
		switch (kind) {
		case ASSOCIATIVE_INFIX_EXPRESSION:
			subParser = new SubParsers.ExtendedAssociativeExpressionInfix(
					tag);
			break;
		case BINARY_INFIX_EXPRESSION:
			subParser = new SubParsers.ExtendedBinaryExpressionInfix(tag);
			break;
		default:
			// should not be ever possible
			throw new IllegalStateException("Unknown extension kind: "
					+ kind);
		}
		return subParser;
	}

	private static INudParser<? extends Formula<?>> makeNudParser(
			ExtensionKind kind, int tag) {
		final INudParser<? extends Formula<?>> subParser;
		switch (kind) {
		case PARENTHESIZED_PREFIX_EXPRESSION:
			subParser = new SubParsers.ExtendedExprParen(tag);
			break;
		default:
			// should not be ever possible
			throw new IllegalStateException("Unknown extension kind: " + kind);
		}
		return subParser;
	}

}
