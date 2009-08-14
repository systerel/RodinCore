/*******************************************************************************
 * Copyright (c) 2006, 2009 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - refactored symbol definitions
 *******************************************************************************/
package org.eventb.eventBKeyboard.internal.translators;

public class TextSymbols extends AbstractSymbols {

	public TextSymbols() {
		super(new Symbol[] {
				new Symbol("NAT1", "\u2115\u2081"), //$NON-NLS-1$ //$NON-NLS-2$
				new Symbol("NAT", "\u2115"), //$NON-NLS-1$ //$NON-NLS-2$
				new Symbol("POW1", "\u2119\u2081"), //$NON-NLS-1$ //$NON-NLS-2$
				new Symbol("POW", "\u2119"), //$NON-NLS-1$ //$NON-NLS-2$
				new Symbol("INT", "\u2124"), //$NON-NLS-1$ //$NON-NLS-2$
				new Symbol("INTER", "\u22c2"), //$NON-NLS-1$ //$NON-NLS-2$
				new Symbol("UNION", "\u22c3"), //$NON-NLS-1$ //$NON-NLS-2$
				new Symbol("or", "\u2228"), //$NON-NLS-1$ //$NON-NLS-2$
				new Symbol("not", "\u00ac"), //$NON-NLS-1$ //$NON-NLS-2$
				new Symbol("true", "\u22a4"), //$NON-NLS-1$ //$NON-NLS-2$
				new Symbol("false", "\u22a5"), //$NON-NLS-1$ //$NON-NLS-2$
				new Symbol("circ", "\u2218"), //$NON-NLS-1$ //$NON-NLS-2$
				new Symbol("oftype", "\u2982"), //$NON-NLS-1$ //$NON-NLS-2$
		});
	}
	
	public TextSymbols(Symbol[] rawSymbols) {
		super(rawSymbols);
	}

}
