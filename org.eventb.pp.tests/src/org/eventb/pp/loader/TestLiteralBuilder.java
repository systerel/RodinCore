///*******************************************************************************
// * Copyright (c) 2006 ETH Zurich.
// * All rights reserved. This program and the accompanying materials
// * are made available under the terms of the Eclipse Public License v1.0
// * which accompanies this distribution, and is available at
// * http://www.eclipse.org/legal/epl-v10.html
// *******************************************************************************/
//
//
//package org.eventb.pp.loader;
//
//import static org.eventb.pp.Util.L;
//import static org.eventb.pp.Util.P;
//import static org.eventb.pp.Util.Q;
//import static org.eventb.pp.Util.mBoundIdentifier;
//import static org.eventb.pp.Util.mConstant;
//import static org.eventb.pp.Util.mFreeIdentifier;
//import static org.eventb.pp.Util.mIR;
//import static org.eventb.pp.Util.mIn;
//import static org.eventb.pp.Util.mList;
//import static org.eventb.pp.Util.mPlus;
//import static org.eventb.pp.Util.mVHolder;
//import static org.eventb.pp.Util.mVariable;
//import static org.eventb.pptrans.Util.V;
//
//import java.util.List;
//
//import org.junit.TestCase;
//
//import org.eventb.core.ast.FormulaFactory;
//import org.eventb.core.ast.ITypeEnvironment;
//import org.eventb.core.ast.Predicate;
//import org.eventb.internal.pp.core.elements.Sort;
//import org.eventb.internal.pp.loader.formula.AbstractFormula;
//import org.eventb.internal.pp.loader.formula.descriptor.LiteralDescriptor;
//import org.eventb.internal.pp.loader.formula.terms.TermSignature;
//import org.eventb.internal.pp.loader.predicate.IContext;
//import org.eventb.internal.pp.loader.predicate.INormalizedFormula;
//import org.eventb.internal.pp.loader.predicate.PredicateBuilder;
//import org.eventb.pp.Util;
//
///**
// * This test checks that the predicate builder functions correctly. It verifies
// * that the created terms of the predicate indeed corresponds to the expected
// * terms. 
// *
// * @author François Terrier
// *
// */
//@SuppressWarnings("unchecked")
//public class TestLiteralBuilder extends TestCase {
//
//	private static FormulaFactory ff = FormulaFactory.getDefault();
//	private static class TestPair {
//		// for the readability of the test, use of string
//		PredicateFormula predicate;
//		List<AbstractFormula> labels;
//		List<AbstractFormula> quantifiers;
//		List<? extends LiteralDescriptor> sig;
//		
//		TestPair (PredicateFormula predicate, List<? extends LiteralDescriptor> sig) {
//			this.predicate = predicate;
//
//			this.sig = sig;
//		}
//		TestPair (String strPredicate, List<? extends LiteralDescriptor> sig) {
//			this.predicate = (PredicateFormula)Util.parsePredicate(strPredicate);
//			predicate.typeCheck(env);
//
//			this.sig = sig;
//		}
//	}
//
//	private static TermSignature a = mConstant("a");
//	private static TermSignature b = mConstant("b");
//	private static TermSignature c = mConstant("c");
//	private static TermSignature d = mConstant("d");
//	private static TermSignature e = mConstant("e");
//	private static TermSignature n = mConstant("n");
//	private static TermSignature D = mConstant("D");
//	private static TermSignature S = mConstant("S");
//	private static TermSignature SS = mConstant("SS");
//	private static TermSignature P = mConstant("P");
//	private static TermSignature Q = mConstant("Q");
//	private static TermSignature R = mConstant("R");
//	private static TermSignature M = mConstant("M");
//	private static TermSignature T = mConstant("T");
//	private static TermSignature U = mConstant("U");
//	private static TermSignature one = mConstant("1");
//	private static TermSignature x = mVariable(0);
//	private static TermSignature TRUE = mConstant("TRUE");
//
//	private static Sort INT = new Sort(ff.makeGivenType("A"));
//	
//	private static ITypeEnvironment env = ff.makeTypeEnvironment();
//	static {
//		env.addName("a", ff.makeIntegerType());
//		env.addName("b", ff.makeIntegerType());
//		env.addName("c", ff.makeIntegerType());
//		env.addName("d", ff.makeIntegerType());
//		env.addName("e", ff.makeBooleanType());
//		env.addName("n", ff.makeIntegerType());
//		
//		env.addName("P", ff.makePowerSetType(ff.makeGivenType("B")));
//		env.addName("Q", ff.makePowerSetType(ff.makeGivenType("A")));
//		env.addName("R", ff.makePowerSetType(ff.makeGivenType("A")));
//		env.addName("M", ff.makePowerSetType(ff.makeProductType(ff.makeProductType(ff.makeGivenType("B"),ff.makeGivenType("A")),ff.makeGivenType("A"))));
//
//		env.addName("T", ff.makePowerSetType(ff.makeIntegerType()));
//		env.addName("U", ff.makePowerSetType(ff.makeProductType(ff.makeIntegerType(),ff.makeIntegerType())));
//	}
//
//	TestPair[] testsPredicate = new TestPair[]{
//			new TestPair("a ∈ S",
//					mList(
//							P(0, mIR(a,S))
//					)
//			),
//			new TestPair("a ↦ b ∈ S",
//					mList(
//							P(0, mIR(a,b,S))
//					)
//			),
//			new TestPair("(a ↦ b) ↦ c ∈ S",
//					mList(
//							P(0, mIR(a,b,c,S))
//					)
//			),
//			new TestPair("a ↦ (b ↦ c) ∈ S",
//					mList(
//							P(0, mIR(a,b,c,S))
//					)
//			),
//			new TestPair("a ↦ ((b ↦ c) ↦ d) ∈ S",
//					mList(
//							P(0, mIR(a,b,c,d,S))
//					)
//			),
//			new TestPair("(a ↦ b) ↦ (c ↦ d) ∈ S",
//					mList(
//							P(0, mIR(a,b,c,d,S))
//					)
//			),
//			new TestPair("((a ↦ b) ↦ c) ↦ d ∈ S",
//					mList(
//							P(0, mIR(a,b,c,d,S))
//					)
//			),
//			new TestPair("a + 1 ∈ S",
//					mList(
//							P(0, mIR(mPlus(a,one),S))
//					)
//			),
//			new TestPair("a + 1 ↦ b ∈ S",
//					mList(
//							P(0, mIR(mPlus(a,one),b,S))
//					)
//			),
//			new TestPair(
//					mIn(mBoundIdentifier(0,ff.makeIntegerType()),
//							mFreeIdentifier("S",ff.makePowerSetType(ff.makeIntegerType()))),
//							mList(
//									P(0, mIR(V(0),S))
//							)
//			),
//
//			////// negations
//			new TestPair("¬ (a ∈ S)",
//					mList(
//							P(0, mIR(a,S))
//					)
//			),
//
//			////// clauses
//			new TestPair("a + 1 ∈ S ∨ a + 1 ∈ S",
//					mList(
//							P(0, 
//									mIR(mPlus(a,one),S),
//									mIR(mPlus(a,one),S)
//							),
//							L(1,mList("P0","P0"),
//									mIR(mIR(mPlus(a,one),S),mIR(mPlus(a,one),S))
//							)
//					)
//			),
//			new TestPair("¬ (a + 1 ∈ S) ∨ a + 1 ∈ S",
//					mList(
//							P(0, 
//									mIR(mPlus(a,one),S),
//									mIR(mPlus(a,one),S)
//							),
//							L(1,mList("P0","not P0"),
//									mIR(mIR(mPlus(a,one),S),mIR(mPlus(a,one),S))
//							)
//					)
//			),
//			
//			// TODO see if we order literals in a specific order
//			new TestPair("¬(¬(a + 1 ∈ S) ∨ a + 1 ∈ S) ∨ (a + 1 ∈ S ∨ ¬(a + 1 ∈ S))",
//					mList(
//							P(0, 
//									mIR(mPlus(a,one),S),
//									mIR(mPlus(a,one),S),
//									mIR(mPlus(a,one),S),
//									mIR(mPlus(a,one),S)
//							),
//							L(1,mList("P0","not P0"),
//									mIR(mPlus(a,one),S,mPlus(a,one),S),
//									mIR(mPlus(a,one),S,mPlus(a,one),S)
//							),
//							L(2,mList("not L1", "L1"),
//									mIR(mPlus(a,one),S,mPlus(a,one),S,mPlus(a,one),S,mPlus(a,one),S)
//							)
//					)
//			),
//			
//			new TestPair("a ∈ S ∧ a ∈ S",
//					mList(
//						P(0,
//								mIR(a,S),
//								mIR(a,S)	
//						),
//						L(1,mList("not P0","not P0"),
//								mIR(mIR(a,S),mIR(a,S))
//						)
//					)
//			),
//			new TestPair("a ∈ S ∧ a ∈ S ∧ a ∈ S",
//					mList(
//						P(0,
//								mIR(a,S),
//								mIR(a,S),
//								mIR(a,S)
//						),
//						L(1,mList("not P0","not P0", "not P0"),
//								mIR(mIR(a,S),mIR(a,S),mIR(a,S))
//						)
//					)
//			),
//			new TestPair("(a ∈ S ∧ a ∈ S) ∧ a ∈ S",
//					mList(
//						P(0,
//								mIR(a,S),
//								mIR(a,S),
//								mIR(a,S)
//						),
//						L(1,mList("not P0","not P0"),
//								mIR(mIR(a,S),mIR(a,S))
//						),
//						L(2,mList("not P0","L1"),
//								mIR(mIR(a,S),mIR(a,S),mIR(a,S))
//						)
//					)
//			),
//			new TestPair("¬(a ∈ S ∧ a ∈ S) ∧ a ∈ S",
//					mList(
//						P(0,
//								mIR(a,S),
//								mIR(a,S),
//								mIR(a,S)
//						),
//						L(1,mList("not P0","not P0"),
//								mIR(mIR(a,S),mIR(a,S))
//						),
//						L(2,mList("not P0","not L1"),
//								mIR(mIR(a,S),mIR(a,S),mIR(a,S))
//						)
//					)
//			),
//			
////			"x ∈ S",
////			"x ↦ y ∈ S",
////			"x + 1 ∈ S",
////			"x + 1 ↦ y ∈ S"
//	};
//
//	TestPair[] testsQuantifiers = new TestPair[]{
//			new TestPair("∀x·x∈T",
//					mList(
//							P(0, mIR(V(0),T)),
//							Q(1,"P0",0,mList(mVariable(-1),mVHolder()),mIR(T))
//					)
//			),	
//			new TestPair(
//					"∃x·x∈T",
//					mList(
//							P(0, mIR(V(0),T)),
//							Q(1,"P0",0,mList(mVariable(-1),mVHolder()),mIR(T))
//					)
//			),	
//			// tests good gestion of quantifiers
//			new TestPair("(∀x·x∈T) ∨ (∃x·x∈T)",
//					mList(
//							P(0, 
//									mIR(V(0),T),
//									mIR(V(1),T)),
//							Q(1,"P0",0,mList(mVariable(-1),mVHolder()),mIR(T)),
//							Q(2,"P0",0,mList(mVariable(-1),mVHolder()),mIR(T)),
//							L(3,mList("Q1","Q2"),
//									mIR(T,T)
//							)
//					)
//			),
//			new TestPair("(∀x·x∈T) ∨ (∀x·x + 1∈T)",
//					mList(
//							P(0, 
//									mIR(V(0),T),
//									mIR(V(1),T)
//							),
//							Q(1,"P0",0,mList(mVariable(-1),mVHolder()),mIR(T)),
//							Q(2,"P0",0,mList(mPlus(mVariable(-1),mVHolder()),mVHolder()),
//									mIR(one,T)
//							),
//							L(3,mList("Q1","Q2"),
//									mIR(T,one,T)
//							)
//					)
//			),
//			new TestPair("¬(∀x·¬(x∈T)) ∨ (∃x·x∈T)",
//					mList(
//							P(0, 
//									mIR(V(0),T),
//									mIR(V(1),T)
//							),
//							Q(1,"P0",0,mList(mVariable(-1),mVHolder()),
//									mIR(T),
//									mIR(T)
//							),
//							L(2,mList("Q1","Q1"),
//									mIR(T,T)
//							)
//					)
//			),	
//			
//			new TestPair(
//					"∃x,y·x ↦ y ∈ U",
//					mList(
//							P(0, mIR(V(0),V(1),U)),
//							Q(1,"P0",0,mList(mVariable(-1),mVariable(-2),mVHolder()),mIR(U))
//					)
//			),	
//			new TestPair(
//					"∃x,y·x + 1 ↦ y + 1 ∈ S",
//					mList(
//							P(0, 
//									mIR(V(0),V(1),S)
//							),
//							Q(1,"P0",0,mList(
//									mPlus(mVariable(-1),mVHolder()),
//									mPlus(mVariable(-2),mVHolder()),
//									mVHolder()),
//									mIR(one,one,S)
//							)
//					)	
//			),
//			new TestPair(
//					"∀x,y·x + 1 ↦ y + 1 ∈ S",
//					mList(
//							P(0, 
//									mIR(V(0),V(1),S)
//							),
//							Q(1,"P0",0,mList(
//									mPlus(mVariable(-1),mVHolder()),
//									mPlus(mVariable(-2),mVHolder()),
//									mVHolder()),
//									mIR(one,one,S)
//							)
//					)			
//			),
//			new TestPair(
//					"∀x,y·x + 1 ↦ y + x + a + 1 ∈ S",
//					mList(
//							P(0, 
//									mIR(V(0),V(1),S)
//							),
//							Q(1,"P0",0,mList(
//									mPlus(mVariable(-1),mVHolder()),
//									mPlus(mVariable(-2),mVariable(-1),mVHolder(),mVHolder()),
//									mVHolder()),
//									mIR(one,a,one,S)
//							)
//					)
//			),
//			new TestPair(
//					"∀x,y·x + 1 ↦ y + x + a + 1 ∈ S ∨ (∀z·z + x ↦ y + 1 ∈ S ∨ x + y ↦ a + 1 ∈ S)",
//					mList(
//							P(0,
//									mIR(V(0),V(1),S),
//									mIR(V(2),V(3),S),
//									mIR(V(4),mPlus(a,one),S)
//							),
//							L(1,mList("P0","P0"),
//									mIR(V(2),V(3),S,V(4),mPlus(a,one),S)
//							),
//							Q(2,"L1",-2,mList(
//									mPlus(mVariable(-3),mVHolder()),
//									mVHolder(),
//									mVHolder(),
//									mVHolder(),
//									mVHolder(),
//									mVHolder()),
//									mIR(V(5),V(6),S,V(7),mPlus(a,one),S)
//							),
//							L(3,mList("P0","Q2"),
//									mIR(V(0),V(1),S,V(5),V(6),S,V(7),mPlus(a,one),S)
//							),
//							Q(4,"L3",0,mList(
//									mPlus(mVariable(-1),mVHolder()),
//									mPlus(mVariable(-2),mVariable(-1),mVHolder(),mVHolder()),
//									mVHolder(),
//									mVariable(-1),
//									mPlus(mVariable(-2),mVHolder()),
//									mVHolder(),
//									mPlus(mVariable(-1),mVariable(-2)),
//									mVHolder(),
//									mVHolder()),
//									mIR(one,a,one,S,one,S,mPlus(a,one),S)
//							)
//					)
//			),
//			// special test, verifies that an index is correctly kept over quantifiers
//			new TestPair(
//					"∀x·x + 1 ∈ S ∨ (∀z·x + 1 ∈ S ∨ z ∈ S)",
//					mList(
//							P(0,	mIR(V(0),S),
//									mIR(V(1),S),
//									mIR(V(2),S)
//							),
//							L(1,mList("P0","P0"),
//									mIR(V(1),S,V(2),S)
//							),
//							Q(2,"L1",-1,
//									mList(
//											mVHolder(),
//											mVHolder(),
//											mVariable(-2),
//											mVHolder()
//									),
//									mIR(V(0),S,S)
//							),
//							L(3,mList("P0","Q2"),
//									mIR(V(0),S,V(0),S,S)
//							),
//							Q(4,"L3",0,
//									mList(
//											mPlus(mVariable(-1),mVHolder()),
//											mVHolder(),
//											mPlus(mVariable(-1),mVHolder()),
//											mVHolder(),
//											mVHolder()
//									),
//									mIR(one,S,one,S,S)
//							)
//					)
//			),
//			// special test to verify that quantifier terms are correctly factorized
//			new TestPair(
//					"∀x·x + 1 ∈ S ∨ x + 1 ∈ S",
//					mList(
//							P(0,	
//									mIR(V(0),S),
//									mIR(V(0),S)
//							),
//							L(1,mList("P0","P0"),
//									mIR(V(0),S,V(0),S)
//							),
//							Q(2,"L1",0,
//									mList(	mPlus(mVariable(-1),mVHolder()),
//											mVHolder(),
//											mPlus(mVariable(-1),mVHolder()),
//											mVHolder()),
//									mIR(one,S,one,S)
//							)
//					)
//			),
//			new TestPair(
//					"∀x, y · x ∈ P ∧ y ∈ Q ⇒ (∃z · z ∈ R ∧ (x↦y)↦z ∈ M)",
//					mList(
//							P(0,
//									mIR(V(0),P)
//							),
//							P(1,
//									mIR(V(1),Q),
//									mIR(V(2),R)
//							),
//							L(2, mList("not P0","not P1"),
//									mIR(V(0),P,V(1),Q)
//							),
//							P(3, 
//									mIR(V(3),V(4),V(2),M)
//							),
//							L(4, mList("not P1","not P3"),
//									mIR(V(2),R,V(3),V(4),V(2),M)
//							),
//							Q(5,"not L4",-2,
//									mList(
//											mVariable(-3),
//											mVHolder(),
//											mVHolder(),
//											mVHolder(),
//											mVariable(-3),
//											mVHolder()
//									),
//									mIR(R,V(0),V(1),M)
//							),
//							L(6, mList("L2","Q5"),
//									mIR(V(0),P,V(1),Q,R,V(0),V(1),M)
//							),
//							Q(7, "L6", 0,
//									mList(
//											mVariable(-1),
//											mVHolder(),
//											mVariable(-2),
//											mVHolder(),
//											mVHolder(),
//											mVariable(-1),
//											mVariable(-2),
//											mVHolder()
//									),
//									mIR(P,Q,R,M)
//							)
//					)
//			),
//			new TestPair(
//					"a ↦ b ∈ SS ∨ (∀z·z + a ↦ 1 ∈ S ∨ z ↦ a + 1 ∈ S)",
//					null
//			),
//			new TestPair(
//					"∀z·z + a ↦ 1 ∈ S ∨ z ↦ a + 1 ∈ S",
//					null
//			),
//			new TestPair(
//					"(∀z·z + a ↦ 1 ∈ S ∨ z ↦ a + 1 ∈ S) ∨ a + 1 ↦ b ∈ SS",
//					null
//			),
//			new TestPair(
//					"∀x·(∀z·z + a ↦ x + 1 ∈ S ∨ z ↦ a + 1 ∈ S) ∨ x + 1 ↦ b ∈ SS",
//					null
//			),
//			new TestPair(
//					"∀x·(x ∈ T ∨ x ↦ a ∈ U) ∨ (∀y·(y ∈ T ∨ x ↦ b ∈ U) ∨ (∀z·z ∈ T ∨ y ↦ b ∈ U))",
//					null
//			),
//			new TestPair(
//					"∀y·(∀z·z ∈ T ∨ y ↦ b ∈ U) ∨ (∀w·w ∈ T ∨ y ↦ b ∈ U)",
//					null
//			),
//			new TestPair(
//					"∀x·(x ∈ T ∨ x ↦ a ∈ U) ∨ (∀y·(y ∈ T ∨ x ↦ b ∈ U) ∨ (∀z·z ∈ T ∨ y ↦ b ∈ U) ∨ (∀w·w ∈ T ∨ y ↦ b ∈ U))",
//					null
//			),
//
////			// example with count
////			new TestPair(
////					"∃x·" +
////					"	(∀x0,x1·x0 ↦ x1∈x⇒" +
////					"		(∀x·" +
////					"			(∀x0,x1·" +
////					"				x0 ↦ x1∈x" +
////					"				⇒" +
////					"					x0=n" +
////					"					∧" +
////					"					(∀x·n ↦ x∈D⇒x1=x+1)" +
////					"			)" +
////					"			∧" +
////					"			(∀x0,x1·" +
////					"				x0=n" +
////					"				∧" +
////					"				(∀x·n ↦ x∈D⇒x1=x+1)" +
////					"					⇒" +
////					"					x0 ↦ x1∈x" +
////					"			)" +
////					"			⇒" +
////					"			(x0 ↦ x1∈D∧" +
////					"				¬(∃x1·x0 ↦ x1∈x)" +
////					"			)" +
////					"			∨" +
////					"			x0 ↦ x1∈x" +
////					"		)" +
////					"	)",
////					mList(
////							P(0,
////									mIR(V(0),V(1),V(2)),
////									mIR(V(3),V(4),V(5)),
////									mIR(n,V(6),D),
////									mIR(n,V(11),D),
////									mIR(V(10),V(14),V(15)),
////									mIR(V(16),V(17),D),
////									mIR(V(18),V(19),V(20)),
////									mIR(V(16),V(17),V(9))
////							),
////							E(INT,
////									mIR(V(3),n),
////									mIR(V(7),V(8)),
////									mIR(V(10),n),
////									mIR(V(12),V(13))
////							),
////							L(1,mList("not P0","Eℤ"),
////									mIR(mIR(n,V(6),D),mIR(V(7),V(8))),
////									mIR(mIR(n,V(11),D),mIR(V(12),V(13)))
////							),
////							Q(2,"not L1",
////									mList(
////											mVHolder(),
////											mVariable(-7),
////											mVHolder(),
////											mVHolder(),
////											mPlus(mVariable(-7),mVHolder())
////									),
////									mIR(mIR(n,D),mIR(V(4),one)),
////									mIR(mIR(n,D),mIR(V(14),one))
////							),
////							L(3,mList("not Eℤ","Q2"),
////									mIR(mIR(V(3),n),mIR(mIR(n,D),mIR(V(4),one))),
////									mIR(mIR(V(10),n),mIR(mIR(n,D),mIR(V(14),one)))
////							),
////							L(4,mList("not P0","not L3"),
////									mIR(mIR(V(3),V(4),V(5)),mIR(V(3),n),mIR(mIR(n,D),mIR(V(4),one)))
////							),
////							Q(5,"not L4",
////									mList(
////											mVariable(-5),
////											mVariable(-6),
////											mVHolder(),
////											mVariable(-5),
////											mVHolder(),
////											mVHolder(),
////											mVHolder(),
////											mVariable(-6),
////											mVHolder()
////									),
////									mIR(V(9),n,n,D,one)
////							),
////							L(6,mList("P0","L3"),
////									mIR(mIR(V(10),V(14),V(15)),mIR(V(10),n),mIR(mIR(n,D),mIR(V(14),one)))
////							),
////							Q(7,"not L6",
////									mList(
////											mVariable(-5),
////											mVariable(-6),
////											mVHolder(),
////											mVariable(-5),
////											mVHolder(),
////											mVHolder(),
////											mVHolder(),
////											mVariable(-6),
////											mVHolder()
////									),
////									mIR(V(9),n,n,D,one)
////							),
////							L(8,mList("Q5","Q7"),
////									mIR(mIR(V(9),n,n,D,one),mIR(V(9),n,n,D,one))
////							),
////							Q(9,"P0",
////									mList(
////											mVHolder(),
////											mVariable(-5),
////											mVHolder()
////									),
////									mIR(V(16),V(9))
////							),
////							L(10,mList("not P0","Q9"),
////									mIR(mIR(V(16),V(17),D),mIR(V(16),V(9)))
////							),
////							L(11,mList("P0","not L10"),
////									mIR(mIR(V(16),V(17),V(9)),mIR(mIR(V(16),V(17),D),mIR(V(16),V(9))))
////							),
////							L(12,mList("L8","L11"),
////									mIR(mIR(V(9),n,n,D,one),mIR(V(9),n,n,D,one),mIR(mIR(V(16),V(17),V(9)),mIR(mIR(V(16),V(17),D),mIR(V(16),V(9)))))
////							),
////							Q(13,"L12",
////									mList(
////											mVariable(-4),
////											mVHolder(),
////											mVHolder(),
////											mVHolder(),
////											mVHolder(),
////											mVariable(-4),
////											mVHolder(),
////											mVHolder(),
////											mVHolder(),
////											mVHolder(),
////											mVHolder(),
////											mVHolder(),
////											mVariable(-4),
////											mVHolder(),
////											mVHolder(),
////											mVHolder(),
////											mVHolder(),
////											mVariable(-4)
////									),
////									mIR(n,n,D,one,n,n,D,one,V(0),V(1),V(0),V(1),D,V(0))
////							),
////							L(14,mList("not P0","Q13"),
////									mIR(mIR(V(0),V(1),V(2)),mIR(n,n,D,one,n,n,D,one,V(0),V(1),V(0),V(1),D,V(0)))
////							),
////							Q(15,"L14",
////									mList(
////											mVariable(-2),
////											mVariable(-3),
////											mVHolder(),
////											mVHolder(),
////											mVHolder(),
////											mVHolder(),
////											mVHolder(),
////											mVHolder(),
////											mVHolder(),
////											mVHolder(),
////											mVHolder(),
////											mVariable(-2),
////											mVariable(-3),
////											mVariable(-2),
////											mVariable(-3),
////											mVHolder(),
////											mVariable(-2)
////									),
////									mIR(V(21),n,n,D,one,n,n,D,one,D)
////							),
////							Q(16,"Q15",
////									mList(
////											mVariable(-1),
////											mVHolder(),
////											mVHolder(),
////											mVHolder(),
////											mVHolder(),
////											mVHolder(),
////											mVHolder(),
////											mVHolder(),
////											mVHolder(),
////											mVHolder()
////									),
////									mIR(n,n,D,one,n,n,D,one,D)
////							)
////					)
////			),
////			new TestPair("∀x·∀y·x ↦ y ∈ U",
////					mIR(
////							P(1, 
////									mIR(V(0),V(1),U)
////							)
////					),
////					U
////			)
//			
//	};
//
//	TestPair[] testsEquality = new TestPair[]{
//			new TestPair("a = b",
//					null
//			),
//			new TestPair("a ≠ b",
//					null
//			),
////			new TestPair("a = b",
////			b, a
////			),		
//			new TestPair("a = 1",
//					null
//			),
//			new TestPair("e = TRUE",
//					null
//			),
//	};
//
//	public void doTest(PredicateFormula predicate, List<? extends LiteralDescriptor> sigs) {
//		// we assume that the string is a relational predicate and more
//		// precisely Formula.IN
//		PredicateBuilder builder = new PredicateBuilder();
//		
//		builder.build(predicate,false);
//		IContext context = builder.getContext();
//		INormalizedFormula actual = builder.getContext().getResults().get(0);
//
////		System.out.println("#####\n" + builder.getResult());
//
//		if (sigs != null) {
//			assertTrue("\nPredicate:\n" + predicate.toString()
//					+ "\nExpected:\n" + sigs + "\nActual:\n"
//					+ context.getAllDescriptors(), context.getAllDescriptors()
//					.containsAll(sigs));
//			assertEquals(sigs.size(), builder.getContext().getAllDescriptors()
//					.size());
//		}
//	}
//
//
//	public void testPredicateBuilder() {
//		for (TestPair test : testsPredicate) {
//			doTest(test.predicate, test.sig);
//		}
//	}
//
//	public void testQuantifierBuilder() {
//		for (TestPair test : testsQuantifiers) {
//			doTest(test.predicate, test.sig);
//		}
//	}
//
//
//	public void testEqualityBuilder() {
//		for (TestPair test : testsEquality) {
//			doTest(test.predicate, test.sig);
//		}
//	}
//}
