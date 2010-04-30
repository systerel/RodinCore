/*******************************************************************************
 * Copyright (c) 2006, 2009 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * This used to be abstract class AbstractSymbols. 
 * 
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *******************************************************************************/
package org.rodinp.internal.keyboard.translators;

public class Symbol {

	private final String combo;
	private final String translation;

	public Symbol(String combo, String translation) {
		this.combo = combo;
		this.translation = translation;
	}

	public String getCombo() {
		return combo;
	}

	public String getTranslation() {
		return translation;
	}

	@Override
	public String toString() {
		return "\"" + combo + "\" --> \"" + translation + "\"";
	}

}
