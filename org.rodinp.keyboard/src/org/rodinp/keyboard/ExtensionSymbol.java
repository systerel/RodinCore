/*******************************************************************************
 * Copyright (c) 2010, 2012 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.rodinp.keyboard;

import org.rodinp.internal.keyboard.translators.Symbol;


/**
 * Class defining a extension symbol programmatically. This class emulates
 * symbol elements of the symbols extension point. See
 * org.rodinp.keyboard.schema/symbols.exsd
 * 
 * @author Thomas Muller
 * @since 1.1
 * 
 */
public class ExtensionSymbol extends Symbol {

	private final String id;
	private final String name;

	/**
	 * A new symbol intended to extend the keyboard.
	 * 
	 * @param id
	 *            the id of this symbol. This id must be unique
	 * @param name
	 *            the name of this symbol, used for example to show a tooltip on
	 *            the symbol view associated with this symbol
	 * @param combo
	 *            the key combination to be entered to produce this symbol
	 * @param translation
	 *            the corresponding translation of this symbol in the model. It
	 *            can be a textual translation or a mathematical translation
	 *            (i.e. using math symbols)
	 */
	public ExtensionSymbol(String id, String name, String combo,
			String translation) {
		super(combo, translation);
		this.id = id;
		this.name = name;
	}

	/**
	 * Returns the id of this symbol.
	 * 
	 * @return the id of this symbol
	 */
	public String getId() {
		return id;
	}

	/**
	 * Returns the given name of this symbol.
	 * 
	 * @return the name of this symbol
	 */
	public String getName() {
		return name;
	}

	/**
	 * Returns the combo intended to be entered by the user to produce this
	 * symbol.
	 * 
	 * @return the combo of this symbol
	 */
	@Override
	public String getCombo() {
		return super.getCombo();
	}

	/**
	 * Returns the textual or mathematical translation associated to this
	 * symbol.
	 * 
	 * @return the translation of this symbol
	 */
	@Override
	public String getTranslation() {
		return super.getTranslation();
	}

}
