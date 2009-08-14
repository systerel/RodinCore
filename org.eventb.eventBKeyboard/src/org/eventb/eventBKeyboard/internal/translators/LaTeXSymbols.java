/*******************************************************************************
 * Copyright (c) 2009 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.eventBKeyboard.internal.translators;

public class LaTeXSymbols extends TextSymbols {

	public LaTeXSymbols() {
		super(new Symbol[] {
				new Symbol("bcmeq", "\u2254"), //$NON-NLS-1$ //$NON-NLS-2$
				new Symbol("bcmin", "\u003a\u2208"), //$NON-NLS-1$ //$NON-NLS-2$
				new Symbol("bcmsuch", "\u003a\u2223"), //$NON-NLS-1$ //$NON-NLS-2$
				new Symbol("bcomp", "\u2218"), //$NON-NLS-1$ //$NON-NLS-2$
				new Symbol("bfalse", "\u22a5"), //$NON-NLS-1$ //$NON-NLS-2$
				new Symbol("binter", "\u2229"), //$NON-NLS-1$ //$NON-NLS-2$
				new Symbol("btrue", "\u22a4"), //$NON-NLS-1$ //$NON-NLS-2$
				new Symbol("bunion", "\u222a"), //$NON-NLS-1$ //$NON-NLS-2$
				new Symbol("cprod", "\u00d7"), //$NON-NLS-1$ //$NON-NLS-2$
				new Symbol("div", "\u00f7"), //$NON-NLS-1$ //$NON-NLS-2$
				new Symbol("domres", "\u25c1"), //$NON-NLS-1$ //$NON-NLS-2$
				new Symbol("domsub", "\u2a64"), //$NON-NLS-1$ //$NON-NLS-2$
				new Symbol("dprod", "\u2297"), //$NON-NLS-1$ //$NON-NLS-2$
				new Symbol("emptyset", "\u2205"), //$NON-NLS-1$ //$NON-NLS-2$
				new Symbol("exists", "\u2203"), //$NON-NLS-1$ //$NON-NLS-2$
				new Symbol("expn", "\u005e"), //$NON-NLS-1$ //$NON-NLS-2$
				new Symbol("fcomp", "\u003b"), //$NON-NLS-1$ //$NON-NLS-2$
				new Symbol("forall", "\u2200"), //$NON-NLS-1$ //$NON-NLS-2$
				new Symbol("geq", "\u2265"), //$NON-NLS-1$ //$NON-NLS-2$
				new Symbol("in", "\u2208"), //$NON-NLS-1$ //$NON-NLS-2$
				new Symbol("intg", "\u2124"), //$NON-NLS-1$ //$NON-NLS-2$		
				new Symbol("Inter", "\u22c2"), //$NON-NLS-1$ //$NON-NLS-2$
				new Symbol("lambda", "\u03bb"), //$NON-NLS-1$ //$NON-NLS-2$
				new Symbol("land", "\u2227"), //$NON-NLS-1$ //$NON-NLS-2$
				new Symbol("leq", "\u2264"), //$NON-NLS-1$ //$NON-NLS-2$
				new Symbol("leqv", "\u21d4"), //$NON-NLS-1$ //$NON-NLS-2$
				new Symbol("limp", "\u21d2"), //$NON-NLS-1$ //$NON-NLS-2$
				new Symbol("lnot", "\u00ac"), //$NON-NLS-1$ //$NON-NLS-2$
				new Symbol("lor", "\u2228"), //$NON-NLS-1$ //$NON-NLS-2$
				new Symbol("mapsto", "\u21a6"), //$NON-NLS-1$ //$NON-NLS-2$
				new Symbol("mid", "\u2223"), //$NON-NLS-1$ //$NON-NLS-2$
				new Symbol("nat", "\u2115"), //$NON-NLS-1$ //$NON-NLS-2$
				new Symbol("natn", "\u2115\u2081"), //$NON-NLS-1$ //$NON-NLS-2$
				new Symbol("neq", "\u2260"), //$NON-NLS-1$ //$NON-NLS-2$
				new Symbol("notin", "\u2209"), //$NON-NLS-1$ //$NON-NLS-2$
				new Symbol("ovl", "\ue103"), //$NON-NLS-1$ //$NON-NLS-2$
				new Symbol("pfun", "\u21f8"), //$NON-NLS-1$ //$NON-NLS-2$
				new Symbol("pinj", "\u2914"), //$NON-NLS-1$ //$NON-NLS-2$
				new Symbol("pow", "\u2119"), //$NON-NLS-1$ //$NON-NLS-2$
				new Symbol("pown", "\u2119\u2081"), //$NON-NLS-1$ //$NON-NLS-2$	
				new Symbol("pprod", "\u2225"), //$NON-NLS-1$ //$NON-NLS-2$
				new Symbol("psur", "\u2900"), //$NON-NLS-1$ //$NON-NLS-2$
				new Symbol("qdot", "\u00b7"), //$NON-NLS-1$ //$NON-NLS-2$
				new Symbol("ranres", "\u25b7"), //$NON-NLS-1$ //$NON-NLS-2$
				new Symbol("ransub", "\u2a65"), //$NON-NLS-1$ //$NON-NLS-2$	
				new Symbol("rel", "\u2194"), //$NON-NLS-1$ //$NON-NLS-2$
				new Symbol("setminus", "\u2216"), //$NON-NLS-1$ //$NON-NLS-2$
				new Symbol("srel", "\ue101"), //$NON-NLS-1$ //$NON-NLS-2$
				new Symbol("strel", "\ue102"), //$NON-NLS-1$ //$NON-NLS-2$
				new Symbol("subset", "\u2282"), //$NON-NLS-1$ //$NON-NLS-2$
				new Symbol("notsubset", "\u2284"), //$NON-NLS-1$ //$NON-NLS-2$
				new Symbol("subseteq", "\u2286"), //$NON-NLS-1$ //$NON-NLS-2$
				new Symbol("notsubseteq", "\u2288"), //$NON-NLS-1$ //$NON-NLS-2$
				new Symbol("tbij", "\u2916"), //$NON-NLS-1$ //$NON-NLS-2$
				new Symbol("tfun", "\u2192"), //$NON-NLS-1$ //$NON-NLS-2$
				new Symbol("tinj", "\u21a3"), //$NON-NLS-1$ //$NON-NLS-2$
				new Symbol("trel", "\ue100"), //$NON-NLS-1$ //$NON-NLS-2$
				new Symbol("tsur", "\u21a0"), //$NON-NLS-1$ //$NON-NLS-2$
				new Symbol("Union", "\u22c3"), //$NON-NLS-1$ //$NON-NLS-2$
				new Symbol("upto", "\u2025"), //$NON-NLS-1$ //$NON-NLS-2$
		});
	}

}
