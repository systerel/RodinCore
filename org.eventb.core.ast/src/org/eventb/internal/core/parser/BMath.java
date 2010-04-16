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

import static org.eventb.core.ast.Formula.BINTER;
import static org.eventb.core.ast.Formula.BUNION;
import static org.eventb.core.ast.Formula.MUL;
import static org.eventb.core.ast.Formula.PLUS;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eventb.core.ast.Formula;
import org.eventb.internal.core.parser.GenParser.SyntaxError;

/**
 * @author Nicolas Beauger
 * TODO needs quite a lot of refactorings
 * TODO make extensible grammars (this one + extensions)
 */
public class BMath {
	
	
	static final int _EOF = 0;
	static final int _LPAR = 1;
	static final int _RPAR = 2;
	static final int _LBRACKET = 3;
	static final int _RBRACKET = 4;
	static final int _LBRACE = 5;
	static final int _RBRACE = 6;
	static final int _EXPN = 7;
	static final int _NOT = 8;
	static final int _CPROD = 9;
	static final int _LAMBDA = 10;
	static final int _UPTO = 11;
	static final int _NATURAL = 12;
	static final int _NATURAL1 = 13;
	static final int _POW = 14;
	static final int _POW1 = 15;
	static final int _INTEGER = 16;
	static final int _TFUN = 17;
	static final int _REL = 18;
	static final int _TSUR = 19;
	static final int _TINJ = 20;
	static final int _MAPSTO = 21;
	static final int _LIMP = 22;
	static final int _LEQV = 23;
	static final int _PFUN = 24;
	static final int _FORALL = 25;
	static final int _EXISTS = 26;
	static final int _EMPTYSET = 27;
	static final int _IN = 28;
	static final int _NOTIN = 29;
	static final int _SETMINUS = 30;
	static final int _MUL = 31;
	static final int _BCOMP = 32;
	static final int _PPROD = 33;
	static final int _LAND = 34;
	static final int _LOR = 35;
	static final int _BINTER = 36;
	static final int _BUNION = 37;
	static final int _BECEQ = 38;
	static final int _BECMO = 39;
	static final int _BECST = 40;
	static final int _EQUAL = 41;
	static final int _NOTEQUAL = 42;
	static final int _LT = 43;
	static final int _LE = 44;
	static final int _GT = 45;
	static final int _GE = 46;
	static final int _SUBSET = 47;
	static final int _NOTSUBSET = 48;
	static final int _SUBSETEQ = 49;
	static final int _NOTSUBSETEQ = 50;
	static final int _DPROD = 51;
	static final int _BTRUE = 52;
	static final int _BFALSE = 53;
	static final int _QINTER = 54;
	static final int _QUNION = 55;
	static final int _QDOT = 56;
	static final int _RANRES = 57;
	static final int _DOMRES = 58;
	static final int _PSUR = 59;
	static final int _PINJ = 60;
	static final int _TBIJ = 61;
	static final int _DOMSUB = 62;
	static final int _RANSUB = 63;
	static final int _TREL = 64;
	static final int _SREL = 65;
	static final int _STREL = 66;
	static final int _OVR = 67;
	static final int _FCOMP = 68;
	static final int _COMMA = 69;
	static final int _PLUS = 70;
	static final int _MINUS = 71;
	static final int _DIV = 72;
	static final int _MID = 73;
	static final int _CONVERSE = 74;
	static final int _BOOL = 75;
	static final int _TRUE = 76;
	static final int _FALSE = 77;
	static final int _KPRED = 78;
	static final int _KSUCC = 79;
	static final int _MOD = 80;
	static final int _KBOOL = 81;
	static final int _KCARD = 82;
	static final int _KUNION = 83;
	static final int _KINTER = 84;
	static final int _KDOM = 85;
	static final int _KRAN = 86;
	static final int _KID = 87;
	static final int _KFINITE = 88;
	static final int _KPRJ1 = 89;
	static final int _KPRJ2 = 90;
	static final int _KMIN = 91;
	static final int _KMAX = 92;
	static final int _KPARTITION = 93;
	static final int _DOT = 94;
	static final int _IDENT = 95;
	static final int _INTLIT = 96;
	static final int _TYPING = 97;
	static final int _PREDVAR = 98;
	static final int maxT = 99;

	/**
	 * Configuration table used to parameterize the scanner, with Rodin
	 * mathematical language tokens.
	 * 
	 */
	static Map<String, Integer> basicConf = new HashMap<String, Integer>();
	static {
		basicConf.put("(", _LPAR);
		basicConf.put(")", _RPAR);
		basicConf.put("[", _LBRACKET);
		basicConf.put("]", _RBRACKET);
		basicConf.put("{", _LBRACE);
		basicConf.put("}", _RBRACE);
		basicConf.put(";", _FCOMP);
		basicConf.put(",", _COMMA);
		basicConf.put("+", _PLUS);
		basicConf.put("\u005e", _EXPN);
		basicConf.put("\u00ac", _NOT);
		basicConf.put("\u00d7", _CPROD);
		basicConf.put("\u00f7", _DIV);
		basicConf.put("\u03bb", _LAMBDA);
		basicConf.put("\u2025", _UPTO);
		basicConf.put("\u2115", _NATURAL);
		basicConf.put("\u21151", _NATURAL1);
		basicConf.put("\u2119", _POW);
		basicConf.put("\u21191", _POW1);
		basicConf.put("\u2124", _INTEGER);
		basicConf.put("\u2192", _TFUN);
		basicConf.put("\u2194", _REL);
		basicConf.put("\u21a0", _TSUR);
		basicConf.put("\u21a3", _TINJ);
		basicConf.put("\u21a6", _MAPSTO);
		basicConf.put("\u21d2", _LIMP);
		basicConf.put("\u21d4", _LEQV);
		basicConf.put("\u21f8", _PFUN);
		basicConf.put("\u2200", _FORALL);
		basicConf.put("\u2203", _EXISTS);
		basicConf.put("\u2205", _EMPTYSET);
		basicConf.put("\u2208", _IN);
		basicConf.put("\u2209", _NOTIN);
		basicConf.put("\u2212", _MINUS);
		basicConf.put("\u2216", _SETMINUS);
		basicConf.put("\u2217", _MUL);
		basicConf.put("\u2218", _BCOMP);
		basicConf.put("\u2223", _MID);
		basicConf.put("\u2225", _PPROD);
		basicConf.put("\u2227", _LAND);
		basicConf.put("\u2228", _LOR);
		basicConf.put("\u2229", _BINTER);
		basicConf.put("\u222a", _BUNION);
		basicConf.put("\u223c", _CONVERSE);
		basicConf.put("\u2254", _BECEQ);
		basicConf.put(":\u2208", _BECMO);
		basicConf.put(":\u2223", _BECST);
		basicConf.put("=", _EQUAL);
		basicConf.put("\u2260", _NOTEQUAL);
		basicConf.put("<", _LT);
		basicConf.put("\u2264", _LE);
		basicConf.put(">", _GT);
		basicConf.put("\u2265", _GE);
		basicConf.put("\u2282", _SUBSET);
		basicConf.put("\u2284", _NOTSUBSET);
		basicConf.put("\u2286", _SUBSETEQ);
		basicConf.put("\u2288", _NOTSUBSETEQ);
		basicConf.put("\u2297", _DPROD);
		basicConf.put("\u22a4", _BTRUE);
		basicConf.put("\u22a5", _BFALSE);
		basicConf.put("\u22c2", _QINTER);
		basicConf.put("\u22c3", _QUNION);
		basicConf.put("\u00b7", _QDOT);
		basicConf.put("\u25b7", _RANRES);
		basicConf.put("\u25c1", _DOMRES);
		basicConf.put("\u2900", _PSUR);
		basicConf.put("\u2914", _PINJ);
		basicConf.put("\u2916", _TBIJ);
		basicConf.put("\u2982", _TYPING);
		basicConf.put("\u2a64", _DOMSUB);
		basicConf.put("\u2a65", _RANSUB);
		basicConf.put("\ue100", _TREL);
		basicConf.put("\ue101", _SREL);
		basicConf.put("\ue102", _STREL);
		basicConf.put("\ue103", _OVR);
		basicConf.put("BOOL", _BOOL);
		basicConf.put("FALSE", _FALSE);
		basicConf.put("TRUE", _TRUE);
		basicConf.put("bool", _KBOOL);
		basicConf.put("card", _KCARD);
		basicConf.put("dom", _KDOM);
		basicConf.put("finite", _KFINITE);
		basicConf.put("id", _KID);
		basicConf.put("inter", _KINTER);
		basicConf.put("max", _KMAX);
		basicConf.put("min", _KMIN);
		basicConf.put("mod", _MOD);
		basicConf.put("pred", _KPRED);
		basicConf.put("prj1", _KPRJ1);
		basicConf.put("prj2", _KPRJ2);
		basicConf.put("ran", _KRAN);
		basicConf.put("succ", _KSUCC);
		basicConf.put("union", _KUNION);
		basicConf.put("partition", _KPARTITION);
		basicConf.put("partitition", _KPARTITION);
		basicConf.put(".", _DOT);
		basicConf.put("\u2024", _DOT);
		basicConf.put("\u2982", _TYPING);
	}


	private static final String ARITHMETIC = "Arithmetic";
	private static final String SET_OPERATORS = "Set Operators";
	private static final String GROUP0 = "GROUP 0";

	private static class CycleError extends Exception {
		public CycleError(String reason) {
			super(reason);
		}
	}
	
	private static class SyntaxCompatibleError extends SyntaxError {
		public SyntaxCompatibleError(String reason) {
			super(reason);
		}
	}

	private static class Relation<T> {
		private final Map<T, Set<T>> maplets = new HashMap<T, Set<T>>();

		public void add(T a, T b) {
			Set<T> set = maplets.get(a);
			if (set == null) {
				set = new HashSet<T>();
				maplets.put(a, set);
			}
			set.add(b);
		}

		public boolean contains(T a, T b) {
			Set<T> set = maplets.get(a);
			if (set == null) {
				return false;
			}
			return set.contains(b);
		}

	}

	private static class Closure<T> {// TODO extends Relation<T> ?
		private final Map<T, Set<T>> reachable = new HashMap<T, Set<T>>();
		private final Map<T, Set<T>> reachableReverse = new HashMap<T, Set<T>>();

		public boolean contains(T a, T b) {
			return contains(reachable, a, b);
		}

		public void add(T a, T b) throws CycleError {
			add(reachable, a, b);
			addAll(reachable, a, get(reachable, b));
			add(reachableReverse, b, a);
			addAll(reachableReverse, b, get(reachableReverse, a));
			if (!a.equals(b) && contains(reachableReverse, a, b)) {
				throw new CycleError("Adding " + a + "|->" + b
						+ " makes a cycle.");
			}
			for (T e : get(reachableReverse, a)) {
				addAll(reachable, e, get(reachable, a));
			}
			for (T e : get(reachable, b)) {
				addAll(reachableReverse, e, get(reachableReverse, b));
			}
		}

		private static <T> void add(Map<T, Set<T>> map, T a, T b) {
			final Set<T> set = get(map, a, true);
			set.add(b);
		}

		private static <T> Set<T> get(Map<T, Set<T>> map, T a, boolean addIfNeeded) {
			Set<T> set = map.get(a);
			if (set == null) {
				set = new HashSet<T>();
				if (addIfNeeded) {
					map.put(a, set);
				}
			}
			return set;
		}

		private static <T> void addAll(Map<T, Set<T>> map, T a, Set<T> s) {
			final Set<T> set = get(map, a, true);
			set.addAll(s);
		}

		private static <T> Set<T> get(Map<T, Set<T>> map, T a) {
			return get(map, a, false);
		}

		private static <T> boolean contains(Map<T, Set<T>> map, T a, T b) {
			return get(map, a).contains(b);
		}
	}

	private static class OperatorGroup {
		private final Set<Integer> operators = new HashSet<Integer>();
		private final Relation<Integer> compatibilityRelation = new Relation<Integer>();
		private final Closure<Integer> operatorAssociativity = new Closure<Integer>();

		private final String id;

		public OperatorGroup(String id) {
			this.id = id;
		}

		public void addCompatibility(Integer a, Integer b) {
			operators.add(b);
			compatibilityRelation.add(a, b);
		}

		public void addAssociativity(Integer a, Integer b)
				throws CycleError {
			operatorAssociativity.add(a, b);
		}

		public boolean contains(Integer a) {
			return operators.contains(a);
		}

		public boolean isAssociative(Integer a, Integer b) {
			return operatorAssociativity.contains(a, b);
		}
		
		public boolean isCompatible(Integer a, Integer b) {
			return compatibilityRelation.contains(a, b);
		}
	}
	
	private final Map<Integer, String> groupIds = new HashMap<Integer, String>();
	private final Closure<String> groupAssociativity = new Closure<String>();
	private final Map<String, OperatorGroup> operatorGroups = new HashMap<String, OperatorGroup>();
	private final Map<Integer, ISubParser> subParsers = new HashMap<Integer, ISubParser>();
	private final Map<Integer, Integer> operatorTag = new HashMap<Integer, Integer>();
	
	public int getOperatorTag(Token token) throws SyntaxError {
		final Integer tag = operatorTag.get(token.kind);
		if (tag == null) {
			throw new SyntaxError("not an operator: "+token.val);
		}
		return tag;
	}
	


	public void init() {
		try {
			operatorTag.put(_PLUS, Formula.PLUS);
			operatorTag.put(_MUL, Formula.MUL);
			operatorTag.put(_BUNION, Formula.BUNION);
			operatorTag.put(_BINTER, Formula.BINTER);
			operatorTag.put(_RPAR, Formula.NO_TAG);

			groupIds.put(PLUS, ARITHMETIC);
			groupIds.put(MUL, ARITHMETIC);
			final OperatorGroup arithGroup = new OperatorGroup(ARITHMETIC);
			arithGroup.addCompatibility(PLUS, PLUS);
			arithGroup.addCompatibility(MUL, MUL);
			arithGroup.addAssociativity(PLUS, MUL);
			operatorGroups.put(ARITHMETIC, arithGroup);

			groupIds.put(BUNION, SET_OPERATORS);
			groupIds.put(BINTER, SET_OPERATORS);
			final OperatorGroup setOpGroup = new OperatorGroup(SET_OPERATORS);
			setOpGroup.addCompatibility(BUNION, BUNION);
			setOpGroup.addCompatibility(BINTER, BINTER);
			operatorGroups.put(SET_OPERATORS, setOpGroup);
			
			groupIds.put(Formula.NO_TAG, GROUP0);
			groupAssociativity.add(GROUP0, ARITHMETIC);
			groupAssociativity.add(GROUP0, SET_OPERATORS);
			
		} catch (CycleError e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		subParsers.put(_INTLIT, Parsers.INTLIT_SUBPARSER);
		subParsers.put(_IDENT, Parsers.FREE_IDENT_SUBPARSER);
		subParsers.put(_BUNION, new Parsers.AssociativeExpressionInfix(Formula.BUNION));
		subParsers.put(_BINTER, new Parsers.AssociativeExpressionInfix(Formula.BINTER));
		subParsers.put(_PLUS, new Parsers.AssociativeExpressionInfix(Formula.PLUS));
		subParsers.put(_MUL, new Parsers.AssociativeExpressionInfix(Formula.MUL));
		subParsers.put(_LPAR, new Parsers.ClosedSugar(_RPAR));
	}

	public ISubParser getSubParser(int kind) {
		return subParsers.get(kind);
	}
	

	/**
	 * priority(tagLeft) < priority(tagRight) 
	 */
	public boolean hasLessPriority(int tagleft, int tagRight) throws SyntaxCompatibleError {
		// TODO right associativity
		final String gid1 = groupIds.get(tagleft);
		final String gid2 = groupIds.get(tagRight);
		
		if (gid1.equals(GROUP0) && gid2.equals(GROUP0)) {
			return false;
		} else if (groupAssociativity.contains(gid1, gid2)) {
			return true;
		} else if (groupAssociativity.contains(gid2, gid1)) {
			return false;
		} else if (gid1.equals(gid2)) {
			final OperatorGroup opGroup = operatorGroups.get(gid1);
			if (opGroup.isAssociative(tagleft, tagRight)) {
				return true;
			} else if (opGroup.isAssociative(tagRight, tagleft)) {
				return false;
			} else if (opGroup.isCompatible(tagleft, tagRight)) {
				return false;
			} else throw new SyntaxCompatibleError("Incompatible symbols: "+ tagleft +" with "+tagRight);
		} else {
			return false;
		}

	}
	

}
