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

import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.extension.IFormulaExtension;
import org.eventb.internal.core.ast.extension.CompatibilityMediator;
import org.eventb.internal.core.ast.extension.PriorityMediator;
import org.eventb.internal.core.parser.IndexedSet.OverrideException;

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
				final int tokenIndex = tokens.add(extension.getSyntaxSymbol());
				final int tag = FormulaFactory.getTag(extension);
				operatorTag.put(tokenIndex, tag);
				final String operatorId = extension.getId();
				final String groupId = extension.getGroupId();
				opRegistry.addOperator(tag, operatorId, groupId);
				switch (extension.getKind()) {
				case ASSOCIATIVE_INFIX_EXPRESSION:
					subParsers.put(tokenIndex,
							new Parsers.ExtendedAssociativeExpressionInfix(tag));
					break;
				case BINARY_INFIX_EXPRESSION:
					subParsers.put(tokenIndex,
							new Parsers.ExtendedBinaryExpressionInfix(tag));
					break;
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

}
