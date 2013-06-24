/*******************************************************************************
 * Copyright (c) 2006, 2013 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - supported contribution through extension and at runtime
 *******************************************************************************/
package org.rodinp.internal.keyboard.core.symbols;

import org.rodinp.keyboard.core.ISymbol;


public class Symbol implements ISymbol {

	private final String combo;
	private final String translation;

	public Symbol(String combo, String translation) {
		this.combo = combo;
		this.translation = translation;
	}

	@Override
	public String getCombo() {
		return combo;
	}

	@Override
	public String getTranslation() {
		return translation;
	}

	@Override
	public String toString() {
		return "\"" + combo + "\" --> \"" + translation + "\"";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((combo == null) ? 0 : combo.hashCode());
		result = prime * result
				+ ((translation == null) ? 0 : translation.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof Symbol)) {
			return false;
		}
		Symbol other = (Symbol) obj;
		if (combo == null) {
			if (other.combo != null) {
				return false;
			}
		} else if (!combo.equals(other.combo)) {
			return false;
		}
		if (translation == null) {
			if (other.translation != null) {
				return false;
			}
		} else if (!translation.equals(other.translation)) {
			return false;
		}
		return true;
	}

	
}
