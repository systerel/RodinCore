/*******************************************************************************
 * Copyright (c) 2010, 2024 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.core.ast.extension;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.eventb.core.ast.extension.IFormulaExtension;
import org.eventb.internal.core.ast.ASTPlugin;
import org.eventb.internal.core.parser.AbstractGrammar;
import org.eventb.internal.core.parser.TokenSet;

/**
 * @author Nicolas Beauger
 * 
 */
public class ExtnUnicityChecker {

	private final AbstractGrammar standardGrammar;

	public ExtnUnicityChecker(AbstractGrammar standardGrammar) {
		this.standardGrammar = standardGrammar;
	}

	/**
	 * Tests if a symbol is used in a set of extensions.
	 *
	 * This will only test if the symbol appears in the set of extensions or the
	 * standard grammar. The unicity of the extension symbols among each other is
	 * not tested. To do so, use {@link #checkUnicity(Set)}.
	 *
	 * @param symbol symbol to check
	 * @param extns  extensions to consider
	 * @return whether the symbol is used in the extensions
	 */
	public boolean isUsedSymbol(String symbol, Set<IFormulaExtension> extns) {
		return standardGrammar.getTokens().contains(symbol)
				|| extns.stream().anyMatch(extn -> extn.getSyntaxSymbol().equals(symbol));
	}

	/**
	 * Tests if an ID is used in a set of extensions.
	 *
	 * This will only test if the ID appears in the set of extensions or the
	 * standard grammar. The unicity of the extension IDs among each other is not
	 * tested. To do so, use {@link #checkUnicity(Set)}.
	 *
	 * @param id    id to check
	 * @param extns extensions to consider
	 * @return whether the id is used in the extensions
	 */
	public boolean isUsedId(String id, Set<IFormulaExtension> extns) {
		return standardGrammar.isDeclared(id) || extns.stream().anyMatch(extn -> extn.getId().equals(id));
	}

	public void checkUnicity(Set<IFormulaExtension> extns) {
		checkSymbolUnicity(extns);
		checkIdUnicity(extns);
	}

	private void checkSymbolUnicity(Set<IFormulaExtension> extns) {
		final TokenSet standardSymbols = standardGrammar.getTokens();
		final List<String> symbols = new ArrayList<String>();
		for (IFormulaExtension extn : extns) {
			final String symbol = extn.getSyntaxSymbol();
			if (standardSymbols.contains(symbol) || symbols.contains(symbol)) {
				processInvalid(extn, "overrides existing symbol: " + symbol);
			}
			symbols.add(symbol);
		}
	}

	private void checkIdUnicity(Set<IFormulaExtension> extns) {
		final List<String> ids = new ArrayList<String>();
		for (IFormulaExtension extn : extns) {
			final String id = extn.getId();
			if (ids.contains(id) || !hasGloballyUnicId(extn)) {
				processInvalid(extn, "overrides existing id: " + id);
			}
			ids.add(id);
		}
	}

	private boolean hasGloballyUnicId(IFormulaExtension newExtn) {
		final String newId = newExtn.getId();
		if (standardGrammar.isDeclared(newId)) {
			return false;
		}
		return true;
	}

	private static void processInvalid(IFormulaExtension newExtn, String reason) {
		final String message = makeInvalidMessage(newExtn, reason);
		ASTPlugin.log(null, message);
		throw new IllegalArgumentException(message);
	}

	private static String makeInvalidMessage(IFormulaExtension newExtn,
			String reason) {
		return "invalid extension " + newExtn.getId() + ": " + reason;
	}

}
