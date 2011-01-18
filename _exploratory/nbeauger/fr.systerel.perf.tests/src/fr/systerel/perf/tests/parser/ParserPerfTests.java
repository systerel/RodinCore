/*******************************************************************************
 * Copyright (c) 2011 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package fr.systerel.perf.tests.parser;

import static fr.systerel.perf.tests.parser.Common.FACTORY;

import org.eventb.core.ast.LanguageVersion;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;

import fr.systerel.perf.tests.Chrono;


/**
 * @author Nicolas Beauger
 *
 */
public class ParserPerfTests {

	private static final int TIMES_REPEAT_ALL = 4000;
	
	@Rule
	public static final TestName testName = new TestName();

	/*
	 * LPAR RPAR LBRACKET RBRACKET LBRACE RBRACE EXPN NOT CPROD LAMBDA UPTO
	 * NATURAL NATURAL1 POW POW1 INTEGER TFUN REL TSUR TINJ MAPSTO LIMP LEQV
	 * PFUN FORALL EXISTS EMPTYSET IN NOTIN SETMINUS MUL BCOMP PPROD LAND LOR
	 * BINTER BUNION EQUAL NOTEQUAL LT LE GT GE SUBSET NOTSUBSET SUBSETEQ
	 * NOTSUBSETEQ DPROD BTRUE BFALSE QINTER QUNION QDOT RANRES DOMRES PSUR PINJ
	 * TBIJ DOMSUB RANSUB TREL SREL STREL OVR FCOMP COMMA PLUS MINUS DIV MID
	 * CONVERSE BOOL TRUE FALSE KPRED KSUCC MOD KBOOL KCARD KUNION KINTER KDOM
	 * KRAN KID KFINITE KPRJ1 KPRJ2 KMIN KMAX DOT FREE_IDENT INTLIT
	 */
	private static final String[] PREDS = new String[] {
			// AtomicPredicate
					"\u22a5", 
					"\u22a4", 
					"finite(x)", 
					"x=x", 
					"x\u2260x", 
					"x<x", 
					"x≤x", 
					"x>x", 
					"x≥x", 
					"x\u2208S", 
					"x\u2209S", 
					"x\u2282S", 
					"x\u2284S", 
					"x\u2286S", 
					"x\u2288S", 
					"(\u22a5)", 
			// LiteralPredicate
					"\u00ac\u22a5", 
					"\u00ac\u00ac\u22a5", 
			
			// PredicateVariable
					"$P",
					"$P\u2227$Q",

			// SimplePredicate
					"\u22a5\u2227\u22a5", 
					"\u22a5\u2228\u22a5", 
					"\u22a5\u2227\u22a5\u2227\u22a5", 
					"\u22a5\u2228\u22a5\u2228\u22a5", 
			
			// MultiplePredicate
					"partition(x)", 
					"partition(x, y)", 
					"partition(x, y, z)",
					"\u2200partition\u00b7partition(x)=y",

			// UnquantifiedPredicate
					"\u22a5\u21d2\u22a5", 
					"\u22a5\u21d4\u22a5", 
			
			// Quantifier + IdentList + Predicate
					"\u2200x\u00b7\u22a5", 
					"\u2203x\u00b7\u22a5", 
					"\u2200x, y, z\u00b7\u22a5", 
					"\u2203x, y, z\u00b7\u22a5", 
					"\u2200x, y\u00b7\u2200s, t\u00b7\u22a5", 
					"\u2203x, y\u00b7\u2203s, t\u00b7\u22a5", 
					"\u2200x, y\u00b7\u2203s, t\u00b7\u22a5", 
					"\u2200 x,y \u00b7\u2200 s,t \u00b7 x\u2208s \u2227 y\u2208t",
			
			// Special cases
					"filter =  { f ∣ ( ∀ a · ⊤ ) } ∧  a = b", 
					
			// with ident bound twice
					"∀x·x ∈ S ∧ (∀x·x ∈ T)",
			
			// with two idents bound twice
					"∀x,y\u00b7x ∈ S ∧ y ∈ T ∧ (∀y,x\u00b7x ∈ T ∧ y ∈ S)",
			
			// with two idents bound twice
					"∀x,y,z \u00b7 finite(x ∪ y ∪ z ∪ {y \u2223 y ⊆ x ∪ z})",
			
			// Test that line terminator and strange spaces are ignored
					"\t\n\r\f ⊤ \u00A0\u2007\u202F",
	};

	private static final String[] EXPRS = new String[] {
			// SimpleExpression
					"bool(\u22a5)", 
					"bool($P)",
					"card(x)", 
					"\u2119(x)", 
					"\u21191(x)", 
					"union(x)", 
					"inter(x)", 
					"dom(x)", 
					"ran(x)", 
					"prj1(x)", 
					"prj2(x)", 
					"id(x)", 
					"(x)", 
					"{x, y\u00b7\u22a5\u2223z}", 
					"{x\u00b7\u22a5\u2223z}", 
					"{x, y\u00b7\u22a5\u2223y}", 
					"{x\u00b7\u22a5\u2223x}", 
					"{x\u2223\u22a5}", 
					"{x+y\u2223\u22a5}", 
					"{}", 
					"{x}", 
					"{x, y}", 
					"{x, y, z}", 
					"\u2124", 
					"\u2115", 
					"\u21151", 
					"BOOL", 
					"TRUE", 
					"FALSE", 
					"pred", 
					"succ", 
					"prj1",
					"prj2",
					"id",
					"2", 
					"−1", 
			
			// Primary
					"x\u223c", 
					"x\u223c\u223c", 
			
			// Image
					"f(x)", 
					"f[x]", 
					"f[x](y)", 
					"f(x)[y]", 
					"f(x)(y)", 
					"f[x][y]", 
			
			// Factor
					"x^y", 
			
			// Term
					"x\u2217y", 
					"x\u2217y\u2217z", 
					"x\u00f7y", 
					"x mod y", 
			
			// ArithmeticExpr
					"x+y", 
					"x+y+z", 
					"−x+y+z", 
					"x−y", 
					"x−y−z", 
					"−x−y", 
					"x−y+z−t", 
					"−x−y+z−t", 
					"x+y−z+t", 
					"−x+y−z+t", 
					"− 3", 
					"−(4)", 
					"−x", 
					"−(x+y)", 
			
			// IntervalExpr
					"x\u2025y", 
			
			// RelationExpr
					"x\u2297y", 
					"x;y", 
					"x;y;z", 
					"x\u25b7y", 
					"x\u2a65y", 
					"x\u2229y", 
					"x\u2229y\u2229z", 
					"x\u2216y", 
					"x;y\u2a65z", 
					"x\u2229y\u2a65z", 
					"x\u2229y\u2216z", 
			
			// SetExpr
					"x\u222ay", 
					"x\u222ay\u222az", 
					"x\u00d7y", 
					"x\u00d7y\u00d7z", 
					"x\ue103y", 
					"x\ue103y\ue103z", 
					"x\u2218y", 
					"x\u2218y\u2218z", 
					"x\u2225y", 
					"x\u25c1y", 
					"x\u2a64y", 
			
			// RelationalSetExpr
					"x\ue100y", 
					"x\ue100y\ue100z", 
					"x\ue101y", 
					"x\ue101y\ue101z", 
					"x\ue102y", 
					"x\ue102y\ue102z", 
					"x\u2900y", 
					"x\u2900y\u2900z", 
					"x\u2914y", 
					"x\u2914y\u2914z", 
					"x\u2916y", 
					"x\u2916y\u2916z", 
					"x\u2192y", 
					"x\u2192y\u2192z", 
					"x\u2194y", 
					"x\u2194y\u2194z", 
					"x\u21a0y", 
					"x\u21a0y\u21a0z", 
					"x\u21a3y", 
					"x\u21a3y\u21a3z", 
					"x\u21f8y", 
					"x\u21f8y\u21f8z", 
			
			// PairExpr
					"x\u21a6y", 
					"x\u21a6y\u21a6z", 
			
			// QuantifiedExpr & IdentPattern
			// UnBound
					"\u03bb x\u00b7\u22a5\u2223z", 
					"\u03bb x\u21a6y\u00b7\u22a5\u2223z", 
					"\u03bb x\u21a6y\u21a6s\u00b7\u22a5\u2223z", 
					"\u03bb x\u21a6(y\u21a6s)\u00b7\u22a5\u2223z", 
			
			// Bound
					"\u03bb x\u00b7\u22a5\u2223x", 
					"\u03bb x\u21a6y\u00b7\u22a5\u2223y", 
					"\u03bb x\u21a6y\u21a6s\u00b7\u22a5\u2223s", 
					"\u03bb x\u21a6(y\u21a6s)\u00b7\u22a5\u2223s", 
			
			// UnBound
					"\u22c3x\u00b7\u22a5\u2223z", 
					"\u22c3 x, y \u00b7\u22a5\u2223z", 
					"\u22c3 x, y, s \u00b7\u22a5\u2223z", 
			
			// Bound
					"\u22c3x\u00b7\u22a5\u2223x", 
					"\u22c3 x, y \u00b7\u22a5\u2223y", 
					"\u22c3 x, y, s \u00b7\u22a5\u2223s", 
			
			// UnBound
					"\u22c3x\u2223\u22a5", 
					"\u22c3 x−y \u2223\u22a5", 
			
			// UnBound
					"\u22c2x\u00b7\u22a5\u2223z", 
					"\u22c2 x, y \u00b7\u22a5\u2223z", 
					"\u22c2 x, y, s \u00b7\u22a5\u2223z", 
			
			// Bound
					"\u22c2 x \u00b7\u22a5\u2223x", 
					"\u22c2 x, y \u00b7\u22a5\u2223y", 
					"\u22c2 x, y, s \u00b7\u22a5\u2223s", 
			
			// UnBound
					"\u22c2x\u2223\u22a5", 
					"\u22c2y−x\u2223\u22a5", 

			// Typed empty set
					"(\u2205\u2982\u2119(\u2124))", 
					"(\u2205\u2982\u2119(\u2119(\u2124)))", 
			
			// Misc.
					"f∼(x)", 
					"f(x)∼",
					"f∼[x]", 
					"f(x)∼[y]", 
			
	};
	
	private static final String[] ASSIGNS = new String[] {
					"x ≔ y",
					"x,y ≔ z,t",
					"x,y,z ≔ t,u,v",
					"x :∈ S",
					"x :\u2223 x' = x",
					"x,y :\u2223 x' = y ∧ y' = x",
					"x,y,z :\u2223 x' = y ∧ y' = z ∧ z' = x",
	};

	private static final String[] BIG_PREDS = new String[] {
		"p_cell_support' ∈ ℙ(Cellules×Trains) ∧" +
		"p_cell_support' ∈ Cellules ⤀ Trains ∧" +
		"dom(p_cell_support') ∩ dom(p_cell_support')∼ = ∅ ∧" +
		"dom(p_cell_support') ∩ p_cell_appariees[dom(p_cell_support')] = ∅ ∧" +
		"p_pos_avant' ∈ Trains→Points ∧" +
		"(∀t· t∈Trains ⇒ p_pos_avant'(t) ∈ ran(p_cell_support'∼[{t}])) ∧" +
		"p_res_dyn' ⊆ Cellules ∧" +
		"(∀ x,y,z · x↦y∈p_res_dyn' ∧ x↦z ∈ p_res_dyn' ⇒ (x↦y)↦(x↦z) ∉ p_cell_appariees) ∧" +
		"dom(p_cell_support') ⊆ p_res_dyn'∧" +
		"p_pos_arriere' ∈ Trains → Points ∧" +
		"(∀ t · t∈Trains ⇒ p_pos_arriere'(t)↦ p_pos_avant'(t)↦ p_pt_support'∼[{t}]↦p_cell_support'∼[{t}]  ∈ Chaines) ∧" +
		"p_pos_arriere' ∩ p_pos_avant' = ∅ ∧" +
		"s_deb' ∈ Routes ⇸ Points ∧" +
		"s_fin' ∈ Routes ⤔ Points ∧" +
		"ran(s_deb') ∩ ran(s_fin') = ∅ ∧" +
		"s_cell_supp_r' ∈ Cellules ⇸ Routes ∧" +
		"dom(s_cell_supp_r')∩p_cell_appariees[dom(s_cell_supp_r')] = ∅ ∧" +
		"ran(s_cell_supp_r') ⊆ ran(s_pt_supp_r')  ∧" +
		"s_deb' ⊆ s_pt_supp_r'∼ ∧" +
		"s_fin' ⊆ s_pt_supp_r'∼ ∧" +
		"dom(s_cell_supp_r')∩dom(s_cell_supp_r')∼=∅ ∧" +
		"dom(s_deb') = ran(s_pt_supp_r') ∧" +
		"dom(s_fin') = ran(s_pt_supp_r') ∧" +
		"(∀ r · r ∈ ran(s_pt_supp_r') ⇒ s_deb'(r)↦s_fin'(r)↦s_pt_supp_r'∼[{r}]↦ s_cell_supp_r'∼[{r}] ∈ Chaines) ∧" +
		"s_deb' ∩ s_fin' = ∅ ∧" +
		"(∀ r1, r2 · r1 ∈ ran(s_pt_supp_r') ∧ r2 ∈ ran(s_pt_supp_r') ∧ r1≠r2 ⇒" +
		"s_pt_supp_r'∼[{r1}] ∩ s_pt_supp_r'∼[{r2}] ⊆ s_deb'[{r1,r2}]" +
		") ∧" +
		"dom(s_cell_supp_r') ⊆ p_res_dyn' ∧" +
		"dom(p_cell_support') ⊆ (dom(s_cell_supp_r')∪dom(s_cell_supp_r')∼) ∧" +
		"a_rd' ∈ ℙ(Trains×ran(s_pt_supp_r')) ∧" +
		"a_rd'∈Trains ⇸ ran(s_pt_supp_r') ∧" +
		"(∀ t · p_cell_support'∼[{t}]⊆s_cell_supp_r'∼[a_rd'[{t}]]) ∧" +
		"a_fin_ze' ∈ Trains → Points ∧" +
		"a_pt_ze' ∈ Points↔Trains ∧" +
		"a_cell_ze' ∈ Cellules ↔ Trains ∧" +
		"(∀ t ·p_pos_arriere'(t)↦a_fin_ze'(t)↦a_pt_ze'∼[{t}]↦a_cell_ze'∼[{t}] ∈ Chaines) ∧" +
		"dom(a_cell_ze') ⊆ dom(s_cell_supp_r')∼ ∧" +
		"(∀t · a_cell_ze'∼[{t}] ∩ (p_cell_support'∼[{t}] ∪ (p_cell_support'∼[{t}])∼) = ∅) ",
	};

	@Test
	public void parseAll() {
		final Chrono chrono = new Chrono(testName);
		chrono.startMeasure();
		for (int i = 0; i < TIMES_REPEAT_ALL; i++) {
			for (String pred : PREDS) {
				FACTORY.parsePredicate(pred, LanguageVersion.LATEST, null);
			}
			for (String expr : EXPRS) {
				FACTORY.parseExpression(expr, LanguageVersion.LATEST, null);
			}
			for (String assign : ASSIGNS) {
				FACTORY.parseAssignment(assign, LanguageVersion.LATEST, null);
			}
			for (String pred : BIG_PREDS) {
				FACTORY.parsePredicate(pred, LanguageVersion.LATEST, null);
			}
		}
		chrono.endMeasure();
	}
}
