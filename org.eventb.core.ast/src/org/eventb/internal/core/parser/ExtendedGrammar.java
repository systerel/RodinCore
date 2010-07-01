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
import org.eventb.core.ast.extension.IExtensionKind;
import org.eventb.core.ast.extension.IFormulaExtension;
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
	
	// TODO associate tag to parser to fetch printers
	@Override
	public void init() {
		super.init();
		try {
			for (IParserInfo<? extends Formula<?>> parserInfo: ParserInfos.ExtendedParsers.values()) {
				addParser(parserInfo);
			}
			for (IFormulaExtension extension : extensions) {
				final int tag = FormulaFactory.getTag(extension);
				final String operatorId = extension.getId();
				final String groupId = extension.getGroupId();
				final IExtensionKind kind = extension.getKind();
				final IParserPrinter<? extends Formula<?>> parser = getParser(kind.getProperties(),
						tag);
				// FIXME the syntax symbol must not already exist as an
				// operator (an extension shall not add backtracking)
				if (parser instanceof INudParser<?>) {
				addOperator(extension.getSyntaxSymbol(), operatorId, groupId,
						(INudParser<? extends Formula<?>>) parser);
				} else if (parser instanceof ILedParser<?>) {
					addOperator(extension.getSyntaxSymbol(), operatorId, groupId,
							(ILedParser<? extends Formula<?>>) parser);
				} else {
					// should not be ever possible
					throw new IllegalStateException("Unparseable extension kind: "
							+ kind);
					
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
