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

import java.util.List;
import java.util.Set;

import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.extension.IFormulaExtension;
import org.eventb.core.ast.extension.IFormulaExtension.Pair;
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
				int tokenIndex;
				tokenIndex = tokens.add(extension.getSyntaxSymbol());
				final int tag = FormulaFactory.getTag(extension);
				operatorTag.put(tokenIndex, tag);
				final String operatorId = extension.getId();
				operatorFromId.put(operatorId, tag);
				final String groupId = extension.getGroupId();
				OperatorGroup operatorGroup = operatorGroups.get(groupId);
				if (operatorGroup == null) {
					operatorGroup = new OperatorGroup(groupId);
					operatorGroups.put(groupId, operatorGroup);
				}

				groupIds.put(tag, groupId);
				switch (extension.getKind()) {
				case ASSOCIATIVE_INFIX_EXPRESSION:
					subParsers.put(tokenIndex,
							new Parsers.ExtendedAssociativeExpressionInfix(tag));
					break;
					// TODO
				}
			}
		} catch (OverrideException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			for (IFormulaExtension extension : extensions) {
				final String groupId = extension.getGroupId();
				OperatorGroup operatorGroup = operatorGroups.get(groupId);
				final List<Pair<String>> compatibilities = extension.getCompatibilities();
				for (Pair<String> compat : compatibilities) {
					final Integer leftTag = operatorFromId.get(compat.left);
					final Integer rightTag = operatorFromId.get(compat.right);
					operatorGroup.addCompatibility(leftTag, rightTag);
				}
				final List<Pair<String>> associativities = extension.getAssociativities();
				for (Pair<String> assoc : associativities) {
					final Integer leftTag = operatorFromId.get(assoc.left);
					final Integer rightTag = operatorFromId.get(assoc.right);
					operatorGroup.addAssociativity(leftTag, rightTag);
				}

			}
			
		} catch (CycleError e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
