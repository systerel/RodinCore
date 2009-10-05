/*******************************************************************************
 * Copyright (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Heinrich Heine Universitaet Duesseldorf - initial API and implementation
 *******************************************************************************/

package org.eventb.symboltable.internal;

import java.util.ArrayList;
import java.util.List;

import org.eventb.symboltable.internal.Symbol.Category;

public class SymbolProvider {

	private static final List<Symbol> symbolList = new ArrayList<Symbol>();

	static {
		// FIXME Set correct categories for symbols

		symbolList.add(new Symbol("|>>", "\u2a65", Messages.symbol_2, //$NON-NLS-1$ //$NON-NLS-2$
				Category.Relations));
		symbolList.add(new Symbol("|>", "\u25b7", Messages.symbol_5, //$NON-NLS-1$ //$NON-NLS-2$
				Category.Relations));
		symbolList.add(new Symbol("\\/", "\u222a", Messages.symbol_8, Category.Sets)); //$NON-NLS-1$ //$NON-NLS-2$
		symbolList.add(new Symbol("/\\", "\u2229", Messages.symbol_11, //$NON-NLS-1$ //$NON-NLS-2$
				Category.Sets));
		symbolList.add(new Symbol("|->", "\u21a6", Messages.symbol_14, Category.Sets)); //$NON-NLS-1$ //$NON-NLS-2$
		symbolList.add(new Symbol("-->", "\u2192", Messages.symbol_17, //$NON-NLS-1$ //$NON-NLS-2$
				Category.Relations));
		symbolList.add(new Symbol("/<<:", "\u2284", Messages.symbol_20, //$NON-NLS-1$ //$NON-NLS-2$
				Category.Sets));
		symbolList
				.add(new Symbol("/<:", "\u2288", Messages.symbol_23, Category.Sets)); //$NON-NLS-1$ //$NON-NLS-2$
		symbolList.add(new Symbol("/:", "\u2209", Messages.symbol_26, //$NON-NLS-1$ //$NON-NLS-2$
				Category.Sets));
		symbolList.add(new Symbol("<=>", "\u21d4", Messages.symbol_29, //$NON-NLS-1$ //$NON-NLS-2$
				Category.Other));
		symbolList
				.add(new Symbol("=>", "\u21d2", Messages.symbol_32, Category.Other)); //$NON-NLS-1$ //$NON-NLS-2$
		symbolList
				.add(new Symbol("&", "\u2227", Messages.symbol_35, Category.Other)); //$NON-NLS-1$ //$NON-NLS-2$
		symbolList.add(new Symbol("!", "\u2200", Messages.symbol_38, //$NON-NLS-1$ //$NON-NLS-2$
				Category.Other));
		symbolList.add(new Symbol("#", "\u2203", Messages.symbol_41, //$NON-NLS-1$ //$NON-NLS-2$
				Category.Other));
		symbolList.add(new Symbol("/=", "\u2260", Messages.symbol_44, //$NON-NLS-1$ //$NON-NLS-2$
				Category.Comparison));
		symbolList.add(new Symbol("<=", "\u2264", Messages.symbol_47, //$NON-NLS-1$ //$NON-NLS-2$
				Category.Comparison));
		symbolList.add(new Symbol(">=", "\u2265", Messages.symbol_50, //$NON-NLS-1$ //$NON-NLS-2$
				Category.Comparison));
		symbolList.add(new Symbol("<<:", "\u2282", Messages.symbol_53, //$NON-NLS-1$ //$NON-NLS-2$
				Category.Sets));
		symbolList.add(new Symbol("<:", "\u2286", Messages.symbol_56, Category.Sets)); //$NON-NLS-1$ //$NON-NLS-2$
		symbolList.add(new Symbol("<<->>", "\ue102", //$NON-NLS-1$ //$NON-NLS-2$
				Messages.symbol_59, Category.Relations));
		symbolList.add(new Symbol("<<->", "\ue100", Messages.symbol_62, //$NON-NLS-1$ //$NON-NLS-2$
				Category.Relations));
		symbolList.add(new Symbol("<->>", "\ue101", Messages.symbol_65, //$NON-NLS-1$ //$NON-NLS-2$
				Category.Relations));
		symbolList.add(new Symbol("<->", "\u2194", Messages.symbol_68, //$NON-NLS-1$ //$NON-NLS-2$
				Category.Relations));
		symbolList.add(new Symbol(">->>", "\u2916", Messages.symbol_71, //$NON-NLS-1$ //$NON-NLS-2$
				Category.Relations));
		symbolList.add(new Symbol("+->", "\u21f8", Messages.symbol_74, //$NON-NLS-1$ //$NON-NLS-2$
				Category.Relations));
		symbolList.add(new Symbol(">+>", "\u2914", Messages.symbol_77, //$NON-NLS-1$ //$NON-NLS-2$
				Category.Relations));
		symbolList.add(new Symbol(">->", "\u21a3", Messages.symbol_80, //$NON-NLS-1$ //$NON-NLS-2$
				Category.Relations));
		symbolList.add(new Symbol("+>>", "\u2900", Messages.symbol_83, //$NON-NLS-1$ //$NON-NLS-2$
				Category.Relations));
		symbolList.add(new Symbol("->>", "\u21a0", Messages.symbol_86, //$NON-NLS-1$ //$NON-NLS-2$
				Category.Relations));
		symbolList.add(new Symbol("{}", "\u2205", Messages.symbol_89, Category.Sets)); //$NON-NLS-1$ //$NON-NLS-2$
		symbolList.add(new Symbol("\\", "\u2216", Messages.symbol_92, //$NON-NLS-1$ //$NON-NLS-2$
				Category.Sets));
		symbolList.add(new Symbol("**", "\u00d7", Messages.symbol_95, //$NON-NLS-1$ //$NON-NLS-2$
				Category.Sets));
		symbolList.add(new Symbol("<+", "\ue103", Messages.symbol_98, //$NON-NLS-1$ //$NON-NLS-2$
				Category.Relations));
		symbolList.add(new Symbol("><", "\u2297", Messages.symbol_101, //$NON-NLS-1$ //$NON-NLS-2$
				Category.Relations));
		symbolList.add(new Symbol("||", "\u2225", Messages.symbol_104, //$NON-NLS-1$ //$NON-NLS-2$
				Category.Relations));
		symbolList.add(new Symbol("~", "\u223c", Messages.symbol_107, //$NON-NLS-1$ //$NON-NLS-2$
				Category.Relations));
		symbolList.add(new Symbol("<<|", "\u2a64", Messages.symbol_110, //$NON-NLS-1$ //$NON-NLS-2$
				Category.Relations));
		symbolList.add(new Symbol("<|", "\u25c1", Messages.symbol_113, //$NON-NLS-1$ //$NON-NLS-2$
				Category.Relations));
		symbolList.add(new Symbol("%", "\u03bb", Messages.symbol_116, Category.Other)); //$NON-NLS-1$ //$NON-NLS-2$
		symbolList.add(new Symbol("..", "\u2025", Messages.symbol_119, //$NON-NLS-1$ //$NON-NLS-2$
				Category.Other));
		symbolList.add(new Symbol(".", "\u00b7", Messages.symbol_122, Category.Other)); //$NON-NLS-1$ //$NON-NLS-2$
		symbolList.add(new Symbol("+", "\u002b", Messages.symbol_125, Category.Other)); //$NON-NLS-1$ //$NON-NLS-2$
		symbolList.add(new Symbol("-", "\u2212", Messages.symbol_128, Category.Other)); //$NON-NLS-1$ //$NON-NLS-2$
		symbolList.add(new Symbol("^", "\u005e", Messages.symbol_131, //$NON-NLS-1$ //$NON-NLS-2$
				Category.Other));
		symbolList.add(new Symbol("*", "\u2217", Messages.symbol_134, //$NON-NLS-1$ //$NON-NLS-2$
				Category.Other));
		symbolList.add(new Symbol("/", "\u00f7", Messages.symbol_137, Category.Other)); //$NON-NLS-1$ //$NON-NLS-2$
		symbolList.add(new Symbol(":=", "\u2254", Messages.symbol_140, //$NON-NLS-1$ //$NON-NLS-2$
				Category.Assignment));
		symbolList.add(new Symbol("::", ":\u2208", Messages.symbol_143, //$NON-NLS-1$ //$NON-NLS-2$
				Category.Assignment));
		symbolList.add(new Symbol(":|", ":\u2223", Messages.symbol_146, //$NON-NLS-1$ //$NON-NLS-2$
				Category.Assignment));
		symbolList.add(new Symbol(":", "\u2208", Messages.symbol_149, Category.Sets)); //$NON-NLS-1$ //$NON-NLS-2$
		symbolList.add(new Symbol("|", "\u2223", Messages.symbol_152, Category.Other)); //$NON-NLS-1$ //$NON-NLS-2$
		symbolList.add(new Symbol(",,", "\u21a6", Messages.symbol_155, Category.Sets)); //$NON-NLS-1$ //$NON-NLS-2$

		symbolList.add(new Symbol("NAT1", "\u2115\u0031", Messages.symbol_158, //$NON-NLS-1$ //$NON-NLS-2$
				Category.Sets));
		symbolList.add(new Symbol("NAT", "\u2115", Messages.symbol_161, //$NON-NLS-1$ //$NON-NLS-2$
				Category.Sets));
		symbolList.add(new Symbol("POW1", "\u2119\u0031", Messages.symbol_164, //$NON-NLS-1$ //$NON-NLS-2$
				Category.Sets));
		symbolList.add(new Symbol("POW", "\u2119", Messages.symbol_167, Category.Sets)); //$NON-NLS-1$ //$NON-NLS-2$
		symbolList.add(new Symbol("INT", "\u2124", Messages.symbol_170, Category.Sets)); //$NON-NLS-1$ //$NON-NLS-2$
		symbolList.add(new Symbol("INTER", "\u22c2", Messages.symbol_173, //$NON-NLS-1$ //$NON-NLS-2$
				Category.Sets));
		symbolList.add(new Symbol("UNION", "\u22c3", Messages.symbol_176, //$NON-NLS-1$ //$NON-NLS-2$
				Category.Sets));
		symbolList
				.add(new Symbol("or", "\u2228", Messages.symbol_179, Category.Other)); //$NON-NLS-1$ //$NON-NLS-2$
		symbolList.add(new Symbol("not", "\u00ac", Messages.symbol_182, Category.Other)); //$NON-NLS-1$ //$NON-NLS-2$
		symbolList.add(new Symbol("true", "\u22a4", Messages.symbol_185, //$NON-NLS-1$ //$NON-NLS-2$
				Category.Other));
		symbolList.add(new Symbol("false", "\u22a5", Messages.symbol_188, //$NON-NLS-1$ //$NON-NLS-2$
				Category.Other));
		symbolList.add(new Symbol("circ", "\u2218", Messages.symbol_191, //$NON-NLS-1$ //$NON-NLS-2$
				Category.Relations));
		symbolList
				.add(new Symbol("oftype", "\u2982", Messages.symbol_194, Category.Other)); //$NON-NLS-1$ //$NON-NLS-2$
	}

	public List<Symbol> getSymbols() {
		return symbolList;
	}
}
