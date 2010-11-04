/*******************************************************************************
 * Copyright (c) 2007, 2010 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *******************************************************************************/
package org.eventb.pp;

import static org.eventb.internal.pp.core.elements.terms.Util.mList;
import static org.eventb.internal.pp.core.elements.terms.Util.mSet;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.internal.pp.core.elements.terms.Util;
import org.junit.Ignore;
import org.junit.Test;

public class RodinTests extends AbstractRodinTest {

	static ITypeEnvironment env = ff.makeTypeEnvironment();
	static {
		env.addName("f", REL(ty_S, ty_T));
		env.addName("g", REL(ty_T, ty_U));
		env.addName("a", ty_U);
		env.addName("A", POW(ty_S));
		env.addName("B", POW(ty_S));
		env.addName("k", POW(ty_S));
		env.addName("R", POW(ty_T));
		env.addName("rtbl", REL(ty_S, ty_T));
		env.addName("U", POW(POW(ty_S)));
		env.addName("S", POW(ty_S));
		env.addName("q", POW(ty_T));
		env.addName("r", REL(ty_T, ty_T));
		env.addName("s", REL(ty_T, ty_T));
		env.addName("org", REL(ty_T, ty_S));
		env.addName("sit", REL(ty_T, ty_S));
		env.addName("M", POW(POW(ty_M)));
		env.addName("N", POW(ty_M));
	}

	protected static void doTest(Set<String> hypotheses, String goal,
			boolean result, int timeout) {
		doTest(env, hypotheses, goal, result, timeout);
	}

	protected static void doTest(Set<String> hypotheses, String goal,
			boolean result) {
		doTest(env, hypotheses, goal, result);
	}

	@Test
    @Ignore("Takes too much time")
	public void testList() {
		doTest(
			mList(
			"m","ℙ(M×M)",
			"l","M",
			"p","N",
			"n","ℙ(N×N)",
			"N","ℙ(N)",
			"f","M",
			"M","ℙ(M)",
			"s","ℙ(M×N)",
			"d","N"
			),
			 mSet(
			"m∈M ∖ {l} ⤖ M ∖ {f}",
			"n∈N ∖ {d} ⤖ N ∖ {p}",
			"s∈M ↔ N",
			"s;n=m;s",
			"s[{f}]={p}",
			"n;s∼=s∼;m",
			"s∈M ⤖ N",
			"s[{l}] ∖ {d}=∅"
			),"s(l)=d",true);
	}
	
	
    @Test
	public void testFailingLevels() {
		doTest(mList("B","ℙ(S×S×S)","R","ℙ(S×S)"),
				mSet(
						"∀x,y·x ↦ y∈R⇒¬y ↦ x∈R",
						"∀x,y·x ↦ y∈R⇒¬x=y",
						"∀x,y,z·x ↦ y∈R∧y ↦ z∈R⇒x ↦ z∈R",
						"∀x,z·¬x=z⇒(∃y·x ↦ y ↦ z∈B)",
						"∀x,y,z·x ↦ y ↦ z∈B⇒(x ↦ y∈R∧y ↦ z∈R)∨(z ↦ y∈R∧y ↦ x∈R)"
						),"∀x,z·x ↦ z∈R⇒(∃y·x ↦ y∈R∧y ↦ z∈R)",true
		);
		
		doTest(
			mList("P","ℙ(S)","Q","ℙ(S)"),
			new HashSet<String>(),
			"(∀x·∃y·x∈P∧y∈Q)⇒(∃y·∀x·x∈P∧y∈Q)",true
		);
	}
	
    @Test
	public void testSoundness() {
		doTest(
				mList(
				"m","ℙ(M×M)",
				"l","M",
				"f","M",
				"M","ℙ(M)"
				),
				 mSet(
				"(∀x,x0·x ↦ x0∈m⇒¬x=l∧¬x0=f)",
				"(∀x,x0,x1·x ↦ x0∈m∧x ↦ x1∈m⇒x0=x1)",
				"(∀x·¬x=l⇒(∃x0·x ↦ x0∈m))",
				"(∀x·¬x=f⇒(∃x0·x0 ↦ x∈m))",
				"(∀x,x0,x1·x0 ↦ x∈m∧x1 ↦ x∈m⇒x0=x1)",
				"∀x·(∀x0·x0∈x⇒(∃x1·x1∈x∧x1 ↦ x0∈m))⇒(∀x0·¬x0∈x)"
				),"l=f",false,2000);
		
		doTest(
				mList(
				"m","ℙ(M×M)",
				"l","M",
				"f","M",
				"M","ℙ(M)"
				),
				 mSet(
				"m∈M ∖ {l} ⤖ M ∖ {f}",
				"∀x·x⊆m[x]⇒x=∅"
				),"l=f",false,2000);
	}
	
    @Test
	public void testSimpleSplit() {
		doTest(
				new ArrayList<String>(),
				mSet(
						"(A=TRUE⇒B=TRUE)∧(C=TRUE⇒¬D=TRUE)",
						"(E=TRUE⇒¬B=TRUE)∧(¬F=TRUE⇒D=TRUE)",
						"¬E=TRUE∨F=TRUE⇒G=TRUE",
						"¬B=TRUE⇒D=TRUE",
						"A=TRUE∨C=TRUE"
				),"B=TRUE∧G=TRUE",true
		);
	}
	
    @Test
	public void testBirthday() {
		doTest(
				mList(
				"brithday","ℙ(PERSON×DATE)",
				"PERSON","ℙ(PERSON)",
				"DATE","ℙ(DATE)",
				"p","PERSON",
				"d","DATE"
				),
				 mSet(
				"brithday∈PERSON ⇸ DATE",
				"p∈PERSON",
				"d∈DATE",
				"p∉dom(brithday)"
				),"brithday∪{p ↦ d}∈PERSON ⇸ DATE",true);	
	}
	
    @Test
	public void testPOW() {
//		f : s >->t
//		a : POW1(s)
//		|--
//		 f[a] : POW1(t)

//		f : s >->t
//		a : POW(s)
//		a/={}
//		|--
//		 f[a] : POW(t) & f[a]/={}

		doTest(
				mList(
					"s","ℙ(s)",
					"t","ℙ(t)"
				),
				mSet(
				"f ∈ s ↣ t",
				"a ∈ ℙ(s)",
				"a ≠ ∅"
				),"f[a] ∈ ℙ(t) ∧ f[a] ≠ ∅"
		,true);
		
		doTest(
				mList(
					"s","ℙ(s)",
					"t","ℙ(t)"
				),
				mSet(
				"f ∈ s ↣ t",
				"a ∈ ℙ1(s)"
				),"f[a] ∈ ℙ1(t)"
		,true);
		
	}
	
    @Test
	public void testJR() {
//		f : A-->E
//		f[a] <: b
//		|---
//		a <: f~[b]
		
		doTest(
				mList(
				"A","ℙ(A)",
				"E","ℙ(A)"
				),
				mSet(
				"f ∈ A→E",
				"f[a] ⊆ b"
				),"a ⊆ f∼[b]"
		,true);
		
//		f : E-->E
//		f~[b] : dom(K)
//		K : POW(E) +->POW(E)
//		f[K(f~[b])] <: b
//		|---
//		K(f~[b]) <: f~[b]
		
		doTest(
				mList(
					"E","ℙ(E)"
				),
				mSet(
				"f ∈ E → E",
				"K ∈ ℙ(E) ⇸ ℙ(E)",
				"f∼[b] ∈ dom(K)",
				"f[K(f∼[b])] ⊆ b"
				),"K(f∼[b]) ⊆ f∼[b]"
		,true);
	}
	
    @Test
	public void testConjunctiveGoals() {
//		q : t <-> t
//		!a,b. a:t & b:t => (a|->b : q <=> a<:b)
//		x|->x0 : q/\q~
//		|--
//		x:t & x=x0
		
//		doTest(	mList(
//				"t","ℙ(ℙ(S))"
//				),
//				mSet(
//				"q∈t↔t",
//				"∀a,b·a∈t∧b∈t ⇒ (a↦b∈q⇔a⊆b)",
//				"x↦x0 ∈ q∩q~"
//				),"x∈t ∧ (∀y·y∈x⇔y∈x0) ∧ x=x0",true
//		);
		
		doTest(	mList(
				"x","ℙ(s)",
				"x0","ℙ(s)",
				"q","ℙ(ℙ(s)×ℙ(s))",
				"t","ℙ(ℙ(s))"
				),
				 mSet(
				"q∈t ↔ t",
				"∀a,b·a∈t∧b∈t⇒(a ↦ b∈q⇔a⊆b)",
				"x ↦ x0∈q∩q∼"
				),"(∀y·y∈x⇔y∈x0)",true);
		
		doTest(
				mList(
				"x","ℙ(s)",
				"x0","ℙ(s)",
				"q","ℙ(ℙ(s)×ℙ(s))",
				"t","ℙ(ℙ(s))"
				),
				 mSet(
				"q∈t ↔ t",
				"∀a,b·a∈t∧b∈t⇒(a ↦ b∈q⇔a⊆b)",
				"x ↦ x0∈q∩q∼"
				),"x∈t",true);
		
		doTest(
				mList(
				"x","ℙ(s)",
				"x0","ℙ(s)",
				"q","ℙ(ℙ(s)×ℙ(s))",
				"t","ℙ(ℙ(s))"
				),
				 mSet(
				"q∈t ↔ t",
				"∀a,b·a∈t∧b∈t⇒(a ↦ b∈q⇔a⊆b)",
				"x ↦ x0∈q∩q∼"
				),"x=x0",true);
		
		doTest(
				mList(
				"x","ℙ(s)",
				"x0","ℙ(s)",
				"q","ℙ(ℙ(s)×ℙ(s))",
				"t","ℙ(ℙ(s))"
				),
				 mSet(
				"q∈t ↔ t",
				"∀a,b·a∈t∧b∈t⇒(a ↦ b∈q⇔a⊆b)",
				"x ↦ x0∈q∩q∼"
				),"x∈t∧x=x0",true);
	}
	
    @Test
	public void testConjunctiveGoals2() {
//		 (∀x0,x1,x2·((x0 ↦ x1∈f∧x0∈x)∨(x1 ↦ x0∈g∧¬(∃x0·x0∈x∧x0 ↦ x1∈f)))∧((x0 ↦ x2∈f∧x0∈x)∨(x2 ↦ x0∈g∧¬(∃x0·x0∈x∧x0 ↦ x2∈f)))⇒x1=x2)
//		 ∧
//		 (∀x0·∃x1·(x0 ↦ x1∈f∧x0∈x)∨(x1 ↦ x0∈g∧¬(∃x0·x0∈x∧x0 ↦ x1∈f)))
//		 ∧
//		 (∀x0·∃x1·(x1 ↦ x0∈f∧x1∈x)∨(x0 ↦ x1∈g∧¬(∃x1·x1∈x∧x1 ↦ x0∈f)))
//		 ∧
//		 (∀x0,x1,x2·((x1 ↦ x0∈f∧x1∈x)∨(x0 ↦ x1∈g∧¬(∃x1·x1∈x∧x1 ↦ x0∈f)))∧((x2 ↦ x0∈f∧x2∈x)∨(x0 ↦ x2∈g∧¬(∃x1·x1∈x∧x1 ↦ x0∈f)))⇒x1=x2)
		
		doTest(
				mList(
				"T","ℙ(T)",
				"x","ℙ(S)",
				"S","ℙ(S)",
				"f","ℙ(S×T)",
				"g","ℙ(T×S)"
				),
				 mSet(
				"f∈S ↣ T",
				"g∈T ↣ S",
				"x=S ∖ g[T ∖ f[x]]"
				),"(∀x0,x1,x2·((x0 ↦ x1∈f∧x0∈x)∨(x1 ↦ x0∈g∧¬(∃x0·x0∈x∧x0 ↦ x1∈f)))∧((x0 ↦ x2∈f∧x0∈x)∨(x2 ↦ x0∈g∧¬(∃x0·x0∈x∧x0 ↦ x2∈f)))⇒x1=x2)",true);
		
		doTest(
				mList(
				"T","ℙ(T)",
				"x","ℙ(S)",
				"S","ℙ(S)",
				"f","ℙ(S×T)",
				"g","ℙ(T×S)"
				),
				 mSet(
				"f∈S ↣ T",
				"g∈T ↣ S",
				"x=S ∖ g[T ∖ f[x]]"
				),"(∀x0·∃x1·(x0 ↦ x1∈f∧x0∈x)∨(x1 ↦ x0∈g∧¬(∃x0·x0∈x∧x0 ↦ x1∈f)))",true);
		
		doTest(
				mList(
				"T","ℙ(T)",
				"x","ℙ(S)",
				"S","ℙ(S)",
				"f","ℙ(S×T)",
				"g","ℙ(T×S)"
				),
				 mSet(
				"f∈S ↣ T",
				"g∈T ↣ S",
				"x=S ∖ g[T ∖ f[x]]"
				),"(∀x0·∃x1·(x1 ↦ x0∈f∧x1∈x)∨(x0 ↦ x1∈g∧¬(∃x1·x1∈x∧x1 ↦ x0∈f)))",true);
		
		doTest(
				mList(
				"T","ℙ(T)",
				"x","ℙ(S)",
				"S","ℙ(S)",
				"f","ℙ(S×T)",
				"g","ℙ(T×S)"
				),
				 mSet(
				"f∈S ↣ T",
				"g∈T ↣ S",
				"x=S ∖ g[T ∖ f[x]]"
				),"(∀x0,x1,x2·((x1 ↦ x0∈f∧x1∈x)∨(x0 ↦ x1∈g∧¬(∃x1·x1∈x∧x1 ↦ x0∈f)))∧((x2 ↦ x0∈f∧x2∈x)∨(x0 ↦ x2∈g∧¬(∃x1·x1∈x∧x1 ↦ x0∈f)))⇒x1=x2)",true);
		
		
		doTest(
				mList(
				"T","ℙ(T)",
				"x","ℙ(S)",
				"S","ℙ(S)",
				"f","ℙ(S×T)",
				"g","ℙ(T×S)"
				),
				 mSet(
				"f∈S ↣ T",
				"g∈T ↣ S",
				"x=S ∖ g[T ∖ f[x]]"
				),		"(∀x0,x1,x2·((x0 ↦ x1∈f∧x0∈x)∨(x1 ↦ x0∈g∧¬(∃x0·x0∈x∧x0 ↦ x1∈f)))∧((x0 ↦ x2∈f∧x0∈x)∨(x2 ↦ x0∈g∧¬(∃x0·x0∈x∧x0 ↦ x2∈f)))⇒x1=x2)" +
						"∧" +
						"(∀x0·∃x1·(x0 ↦ x1∈f∧x0∈x)∨(x1 ↦ x0∈g∧¬(∃x0·x0∈x∧x0 ↦ x1∈f)))",true);
		
		
		doTest(
				mList(
				"T","ℙ(T)",
				"x","ℙ(S)",
				"S","ℙ(S)",
				"f","ℙ(S×T)",
				"g","ℙ(T×S)"
				),
				 mSet(
				"f∈S ↣ T",
				"g∈T ↣ S",
				"x=S ∖ g[T ∖ f[x]]"
				),"(x ◁ f)∪((T ∖ f[x]) ◁ g)∼∈S ⤖ T",true);
	}
	
    @Test
	public void testCelebrity() {
		doTest(
				mList(
				"Q","ℙ(ℤ)",
				"P","ℙ(ℤ)",
				"x","ℤ",
				"y","ℤ",
				"c","ℤ",
				"k","ℙ(ℤ×ℤ)"
				),
				 mSet(
				"c∈Q",
				"x∈Q",
				"y∈Q",
				"x ↦ y∈k",
				"k∈P ∖ {c} ↔ P"
				),"¬x=c",true);
	}
    @Test
	public void testFailingExample2() {
		doTest(
				mList(
				"S","ℙ(S)",
				"r","ℙ(S×S)"
				),
				 mSet(
				"r∈S ↔ S",
				"ran(r)=S",
				"∀p·p⊆S∧p⊆r∼[p]⇒p=∅",
				"∀q·q⊆S∧S ∖ r∼[S ∖ q]⊆q⇒S⊆q"
				),"r∈S ⇸ S⇒(∀q·q⊆S∧S ∖ dom(r)⊆q∧r∼[q]⊆q⇒S⊆q)",true);
	}
	
	// fails because of overriding
//	public void testFailingExample3() {
//		initDebug();
//		doTest(
//				mList(
//				"guest","ℙ(CARD×GUEST)",
//				"r0","ROOM",
//				"c","CARD",
//				"KEY","ℙ(KEY)",
//				"owns","ℙ(ROOM×GUEST)",
//				"key","ℙ(KEY)",
//				"k","KEY",
//				"CARD","ℙ(CARD)",
//				"isin","ℙ(ROOM×GUEST)",
//				"crd","ℙ(CARD)",
//				"g","GUEST",
//				"c0","CARD",
//				"safe","ℙ(ROOM)",
//				"snd","ℙ(CARD×KEY)",
//				"currk","ℙ(ROOM×KEY)",
//				"ROOM","ℙ(ROOM)",
//				"fst","ℙ(CARD×KEY)",
//				"GUEST","ℙ(GUEST)",
//				"roomk","ℙ(ROOM×KEY)",
//				"r","ROOM"
//				),
//				 mSet(
//				"g∈GUEST",
//				"∀r,c·¬r∈safe∧c∈crd∧r ↦ guest(c)∈owns∧currk(r)=snd(c)⇒¬roomk(r)=snd(c)",
//				"¬r∈dom(owns)",
//				"¬c∈crd",
//				"¬k∈key",
//				"¬r0∈safe",
//				"c0∈crd∪{c}",
//				"¬r0=r",
//				"¬c0=c",
//				"currk(r0)=snd(c0)",
//				"r0 ↦ guest(c0)∈owns{r ↦ g}",
//				"isin∈ROOM ⇸ GUEST",
//				"crd⊆CARD",
//				"fst∈crd → key",
//				"ran(currk)∩ran(fst)=∅",
//				"r0 ↦ (guest{c ↦ g})(c0)∈owns{r ↦ g}",
//				"key⊆KEY",
//				"owns∈ROOM ⇸ GUEST",
//				"safe⊆dom(owns)",
//				"snd∈crd ↣ key",
//				"∀r,c·r∈safe∧c∈crd∧roomk(r)=snd(c)⇒r ↦ guest(c)∈owns",
//				"safe ◁ isin⊆owns",
//				"guest∈crd → GUEST",
//				"currk∈ROOM ↣ key",
//				"roomk∈ROOM → key",
//				"currk(r0)=(snd{c ↦ k})(c0)",
//				"(currk{r ↦ k})(r0)=(snd{c ↦ k})(c0)",
//				"safe ◁ roomk=safe ◁ currk"
//				),"¬roomk(r0)=snd(c0)",true);
//	}
	
    @Test
	public void testfifth() {
		doTest(
				mList(
				"T","ℙ(T)",
				"A","ℙ(S)",
				"B","ℙ(T)",
				"S","ℙ(S)",
				"b","T",
				"a","S",
				"f","ℙ(S×T)"
				),
				 mSet(
				"A⊆S",
				"B⊆T",
				"f∈A ⇸ B",
				"¬a∈A",
				"¬b∈B"
				),"(∀x,x0·x ↦ x0∈f∨(x=a∧x0=b)⇒(x∈A∨x=a)∧(x0∈B∨x0=b))",true);
		
		doTest(
				mList(
				"T","ℙ(T)",
				"A","ℙ(S)",
				"B","ℙ(T)",
				"S","ℙ(S)",
				"b","T",
				"a","S",
				"f","ℙ(S×T)"
				),
				 mSet(
				"A⊆S",
				"B⊆T",
				"f∈A ⇸ B",
				"¬a∈A",
				"¬b∈B"
				),"(∀x,x0,x1·(x ↦ x0∈f∨(x=a∧x0=b))∧(x ↦ x1∈f∨(x=a∧x1=b))⇒x0=x1)",true);
		
		doTest(
				mList(
				"T","ℙ(T)",
				"A","ℙ(S)",
				"B","ℙ(T)",
				"S","ℙ(S)",
				"b","T",
				"a","S",
				"f","ℙ(S×T)"
				),
				 mSet(
				"A⊆S",
				"B⊆T",
				"f∈A ⇸ B",
				"¬a∈A",
				"¬b∈B"
				),"f∪{a ↦ b}∈A∪{a} ⇸ B∪{b}",true);
		
	}
	
//	public void testLoop() {
//		doTest(
//				mList(
//				"q","ℙ(S)",
//				"r","ℙ(S×S)"
//				),
//				 mSet(
//				"∀q·q⊆ran(r)∧ran(r) ∖ r∼[ran(r) ∖ q]⊆q⇒ran(r)⊆q",
//				"r∈ran(r) ⇸ ran(r)",
//				"r∼[q]⊆q",
//				"ran(r) ∖ dom(r)⊆q",
//				"q⊆ran(r)"
//				),"ran(r)⊆q",true);	
//	}

    @Test
	public void testRelation() {
		doTest(
				mList(
					"A","ℙ(A)",
					"B","ℙ(B)"
				),mSet(
					"f∈A→B",
					"p⊆A",
					"x∈p"
				),"f(x)∈f[p]",true);
	}
	
	
    @Test
	public void testProfile() {
		// !x!y!z (Rxy & Ryz -> Rxz),
		// !x Rxx,
		// !x!y#z (Rxz & Ryz)
		// |- !x!y (Rxy | Ryx);
		doTest(mSet("∀N,T·N∈M∧T∈M⇒(∃x·(∀x0·x0∈x⇔x0∈N∧x0∈T)∧x∈M)", "E∈M",
				"∀x·x∈ae⇒x∈N", "∀x·x∈N⇒x∈ae", "¬(∀x·x∈ae⇔x∈N)"), "ae=N", true);
		doTest(mSet("∀x·∀y·∀z·x↦y∈r ∧ y↦z∈r ⇒ x↦z∈r", "∀x·x↦x∈r",
				"∀x·∀y·∃z·x↦z∈r ∧ y↦z∈r"), "∀x·∀y·x↦y∈r ∨ y↦x∈r", false, 500);
		doTest(mSet("r∼[q]⊆q", "ran(r) ∖ dom(r)⊆q", "q⊆ran(r)",
				"q⊆ran(r) ∧ ran(r) ∖ r∼[ran(r) ∖ q]⊆q ⇒ ran(r)⊆q"), "ran(r)⊆q",
				true);
		doTest(mSet("r∼[q]⊆q", "ran(r) ∖ dom(r)⊆q", "q⊆ran(r)",
				"∀q·q⊆ran(r)∧ran(r) ∖ r∼[ran(r) ∖ q]⊆q⇒ran(r)⊆q"), "ran(r)⊆q",
				true);
	}
	
    @Test
	public void testRubin() {
		doTest(
				mList("A","ℙ(E)"),
				mSet(	"∀x·x∈A⇒x∈B",
						"∀y·y∈B⇒(∀x·x∈A)"
				),"(∀x·x∈B)⇔a∈B",true
		);
		
		doTest(mList("S","ℙ(E)","R","ℙ(E×E)"),mSet(
				"∃x·x∈P∧x ↦ a∈R",
				"a∈S",
				"∀x·x∈P∧¬(∃y·y∈Q∧x ↦ y∈R)⇒¬(∃z·z∈S∧x ↦ z∈R)"),
				"∃x,y·x∈P∧y∈Q∧x ↦ y∈R",true
		);
		
		doTest(mList("P","ℙ(E)"),new HashSet<String>(),"(∀x·x∈P⇔x∈Q)⇒((∀x·x∈P)⇔(∀x·x∈Q))",true);
	}
	
    @Test
	public void testInjection() {
		
//		doTest(mSet("r ∈ E ↣ E" , "s ∈ E ↣ E"), 
//				"r;s ∈ E ↣ E", true);
		
//		(∀x,x0,x1·(∃x1·x ↦ x1∈r∧x1 ↦ x0∈s)∧(∃x0·x ↦ x0∈r∧x0 ↦ x1∈s)⇒x0=x1)
//		∧
//		(∀x·∃x0,x1·x ↦ x1∈r∧x1 ↦ x0∈s)
//		∧
//		(∀x,x0,x1·(∃x1·x0 ↦ x1∈r∧x1 ↦ x∈s)∧(∃x0·x1 ↦ x0∈r∧x0 ↦ x∈s)⇒x0=x1)
		
		doTest(
				mList(
				"S","ℙ(S)",
				"r","ℙ(S×S)",
				"s","ℙ(S×S)"
				),
				 mSet(
				"r∈S ↣ S",
				"s∈S ↣ S"
				),"(∀x,x0,x1·(∃x1·x ↦ x1∈r∧x1 ↦ x0∈s)∧(∃x0·x ↦ x0∈r∧x0 ↦ x1∈s)⇒x0=x1)",true);
		
		doTest(
				mList(
				"S","ℙ(S)",
				"r","ℙ(S×S)",
				"s","ℙ(S×S)"
				),
				 mSet(
				"r∈S ↣ S",
				"s∈S ↣ S"
				),"(∀x·∃x0,x1·x ↦ x1∈r∧x1 ↦ x0∈s)",true);
		
		doTest(
				mList(
				"S","ℙ(S)",
				"r","ℙ(S×S)",
				"s","ℙ(S×S)"
				),
				 mSet(
				"r∈S ↣ S",
				"s∈S ↣ S"
				),"(∀x,x0,x1·(∃x1·x0 ↦ x1∈r∧x1 ↦ x∈s)∧(∃x0·x1 ↦ x0∈r∧x0 ↦ x∈s)⇒x0=x1)",true);

		doTest(
				mList(
				"S","ℙ(S)",
				"r","ℙ(S×S)",
				"s","ℙ(S×S)"
				),
				 mSet(
				"r∈S ↣ S",
				"s∈S ↣ S"
				),"(∀x·∃x0,x1·x ↦ x1∈r∧x1 ↦ x0∈s)"+
					"∧(∀x,x0,x1·(∃x1·x0 ↦ x1∈r∧x1 ↦ x∈s)∧(∃x0·x1 ↦ x0∈r∧x0 ↦ x∈s)⇒x0=x1)",true);

		doTest(
				mList(
				"S","ℙ(S)",
				"r","ℙ(S×S)",
				"s","ℙ(S×S)"
				),
				 mSet(
				"r∈S ↣ S",
				"s∈S ↣ S"
				),"(∀x,x0,x1·(∃x1·x ↦ x1∈r∧x1 ↦ x0∈s)∧(∃x0·x ↦ x0∈r∧x0 ↦ x1∈s)⇒x0=x1)"+
					"∧(∀x,x0,x1·(∃x1·x0 ↦ x1∈r∧x1 ↦ x∈s)∧(∃x0·x1 ↦ x0∈r∧x0 ↦ x∈s)⇒x0=x1)",true);
		
		doTest(
				mList(
				"S","ℙ(S)",
				"r","ℙ(S×S)",
				"s","ℙ(S×S)"
				),
				 mSet(
				"r∈S ↣ S",
				"s∈S ↣ S"
				),"(∀x,x0,x1·(∃x1·x ↦ x1∈r∧x1 ↦ x0∈s)∧(∃x0·x ↦ x0∈r∧x0 ↦ x1∈s)⇒x0=x1)"+
					"∧(∀x·∃x0,x1·x ↦ x1∈r∧x1 ↦ x0∈s)",true);
		
		
		doTest(mList("S", "ℙ(S)", "r", "ℙ(S×S)", "s", "ℙ(S×S)"), mSet(
				"r∈S ↣ S", "s∈S ↣ S"),
				"r;s∈S ↣ S",true);
	}
	
    @Test
	public void testFunction() {
		doTest(mSet("r ∈ E → E", "s ∈ E → E"), "r;s ∈ E → E", true);
	}
	
    @Test
	public void testFunctionWithExtraHypotheses() {
		doTest(
				mList(
				"h","ℙ(S×S)",
				"S","ℙ(S)",
				"k","ℙ(S×S)",
				"f","ℙ(S×S)",
				"g","ℙ(S×S)"
				),
				 mSet(
				"f∈S ↣ S",
				"g∈S ↣ S",
				"h∈S → S",
				"k∈S → S",
				"f;g∈S ↣ S"
				),"h;k∈S → S",true);
	}
	
    @Test
	public void testAllFunctionSameType() {
		
		doTest(mList("S", "ℙ(S)", "r", "ℙ(S×S)", "s", "ℙ(S×S)"), mSet(
				"r∈S ↣ S", "s∈S ↣ S"),
				"r;s∈S ↣ S",true);
		
		doTest(mList("S", "ℙ(S)", "r", "ℙ(S×S)", "s", "ℙ(S×S)"), mSet(
				"r∈S ⤖ S", "s∈S ⤖ S"),
				"r;s∈S ⤖ S",true);
		
		doTest(mList("S", "ℙ(S)", "r", "ℙ(S×S)", "s", "ℙ(S×S)"), mSet(
				"r∈S ↠ S", "s∈S ↠ S"),
				"r;s∈S ↠ S",true);
		
		doTest(mList("S", "ℙ(S)", "r", "ℙ(S×S)", "s", "ℙ(S×S)"), mSet(
				"r∈S ⤔ S", "s∈S ⤔ S"),
				"r;s∈S ⤔ S",true);

		doTest(mList("S", "ℙ(S)", "r", "ℙ(S×S)", "s", "ℙ(S×S)"), mSet(
				"r∈S ⤀ S", "s∈S ⤀ S"),
				"r;s∈S ⤀ S",true);
		
		doTest(mList("S", "ℙ(S)", "r", "ℙ(S×S)", "s", "ℙ(S×S)"), mSet(
				"r∈S → S", "s∈S → S"),
				"r;s∈S → S",true);
	}
	
    @Test
	public void testSurjection() {
		
//		doTest(mSet("r ∈ E ↣ E", "s ∈ E ↣ E"), "r;s ∈ E ↣ E", true);
		
		doTest(mSet("r ∈ E ↣ E" , "s ∈ E ↣ E"), 
				"r ∈ E ↣ E", true);
		
		// injection + total + fonction + definition
		doTest(mSet("r ∈ E ↣ E", "s ∈ E ↣ E"),
				"(∀x,x0,x1·x0 ↦ x∈r∧x1 ↦ x∈r⇒x0=x1)∧(∀x·x∈E⇒(∃x0·x ↦ x0∈r))" +
				"∧(∀x,x0,x1·x ↦ x0∈r∧x ↦ x1∈r⇒x0=x1)∧(∀x,x0·x ↦ x0∈r⇒x∈E∧x0∈E)", true);
		
		// injection 
		doTest(mSet("r ∈ E ↣ E", "s ∈ E ↣ E"), 
				"(∀x,x0,x1·x0 ↦ x∈r∧x1 ↦ x∈r⇒x0=x1)", true);
		// total
		doTest(mSet("r ∈ E ↣ E", "s ∈ E ↣ E"), 
				"(∀x·x∈E⇒(∃x0·x ↦ x0∈r))", true);
		
		// fonction
		doTest(mSet("r ∈ E ↣ E", "s ∈ E ↣ E"), 
				"(∀x,x0,x1·x ↦ x0∈r∧x ↦ x1∈r⇒x0=x1)", true);
		
		// definition
		doTest(mSet("r ∈ E ↣ E", "s ∈ E ↣ E"), 
				"(∀x,x0·x ↦ x0∈r⇒x∈E∧x0∈E)", true);
		
		// injection + total
		doTest(mSet("r ∈ E ↣ E", "s ∈ E ↣ E"), 
				"(∀x,x0,x1·x0 ↦ x∈r∧x1 ↦ x∈r⇒x0=x1)∧(∀x·x∈E⇒(∃x0·x ↦ x0∈r))", true);
		
		// injection + total + fonction
		doTest(mSet("r ∈ E ↣ E", "s ∈ E ↣ E"), 
				"(∀x,x0,x1·x0 ↦ x∈r∧x1 ↦ x∈r⇒x0=x1)∧(∀x·x∈E⇒(∃x0·x ↦ x0∈r))∧(∀x,x0,x1·x ↦ x0∈r∧x ↦ x1∈r⇒x0=x1)", true);

		// injection + fonction
		doTest(mSet("r ∈ E ↣ E", "s ∈ E ↣ E"), 
				"(∀x,x0,x1·x0 ↦ x∈r∧x1 ↦ x∈r⇒x0=x1)∧(∀x,x0,x1·x ↦ x0∈r∧x ↦ x1∈r⇒x0=x1)", true);

		// injection + fonction + definition
		doTest(mSet("r ∈ E ↣ E", "s ∈ E ↣ E"), 
				"(∀x,x0,x1·x0 ↦ x∈r∧x1 ↦ x∈r⇒x0=x1)∧(∀x,x0,x1·x ↦ x0∈r∧x ↦ x1∈r⇒x0=x1)∧(∀x,x0·x ↦ x0∈r⇒x∈E∧x0∈E)", true);

		// injection + definition
		doTest(mSet("r ∈ E ↣ E", "s ∈ E ↣ E"), 
				"(∀x,x0,x1·x0 ↦ x∈r∧x1 ↦ x∈r⇒x0=x1)∧(∀x,x0·x ↦ x0∈r⇒x∈E∧x0∈E)", true);

		
		doTest(mSet("r ∈ E ⤖ E", "s ∈ E ⤖ E"), 
				"(∀x,x0,x1·x0 ↦ x∈r∧x1 ↦ x∈r⇒x0=x1)", true);
		// total
		doTest(mSet("r ∈ E ⤖ E", "s ∈ E ⤖ E"), 
				"(∀x·x∈E⇒(∃x0·x ↦ x0∈r))", true);
		
		doTest(mSet("r ∈ E ⤖ E", "s ∈ E ⤖ E"), 
				"(∀x,x0,x1·x ↦ x0∈r∧x ↦ x1∈r⇒x0=x1)", true);
		
		doTest(mSet("r ∈ E ⤖ E", "s ∈ E ⤖ E"), 
				"(∀x,x0·x ↦ x0∈r⇒x∈E∧x0∈E)", true);
		
		// injection 
		doTest(mSet("r ∈ E ↣ E", "s ∈ E ↣ E"), 
				"(∀x,x0,x1·(∃x1·x0 ↦ x1∈r∧x1 ↦ x∈s)∧(∃x0·x1 ↦ x0∈r∧x0 ↦ x∈s)⇒x0=x1)", true);
		// total
		doTest(mSet("r ∈ E ↣ E", "s ∈ E ↣ E"), 
				"(∀x·x∈E⇒(∃x0,x1·x ↦ x1∈r∧x1 ↦ x0∈s))", true);
		
		// fonction
		doTest(mSet("r ∈ E ↣ E", "s ∈ E ↣ E"), 
				"(∀x,x0,x1·(∃x1·x ↦ x1∈r∧x1 ↦ x0∈s)∧(∃x0·x ↦ x0∈r∧x0 ↦ x1∈s)⇒x0=x1)", true);
		
		// definition
		doTest(mSet("r ∈ E ↣ E", "s ∈ E ↣ E"), 
				"(∀x,x0·(∃x1·x ↦ x1∈r∧x1 ↦ x0∈s)⇒x∈E∧x0∈E)", true);
		
		
		// injection total
		doTest(mSet("r ∈ E ↣ E", "s ∈ E ↣ E"), 
				"(∀x,x0,x1·(∃x1·x0 ↦ x1∈r∧x1 ↦ x∈s)∧(∃x0·x1 ↦ x0∈r∧x0 ↦ x∈s)⇒x0=x1)" +
				"∧(∀x·x∈E⇒(∃x0,x1·x ↦ x1∈r∧x1 ↦ x0∈s))", true);
		
		// injection total fonction
		doTest(mSet("r ∈ E ↣ E", "s ∈ E ↣ E"), 
				"(∀x,x0,x1·(∃x1·x0 ↦ x1∈r∧x1 ↦ x∈s)∧(∃x0·x1 ↦ x0∈r∧x0 ↦ x∈s)⇒x0=x1)" +
				"∧(∀x·x∈E⇒(∃x0,x1·x ↦ x1∈r∧x1 ↦ x0∈s))" +
				"∧(∀x,x0,x1·(∃x1·x ↦ x1∈r∧x1 ↦ x0∈s)∧(∃x0·x ↦ x0∈r∧x0 ↦ x1∈s)⇒x0=x1)", true);
		
		// fails
		doTest(mSet("r ∈ E ↠ E", "s ∈ E ↠ E"), "r;s ∈ E ↠ E", true);
		doTest(mSet("r ∈ E ⤖ E", "s ∈ E ⤖ E"), "r;s ∈ E ⤖ E", true);
		
		// works
		doTest(mSet("r ∈ E ⤔ E", "s ∈ E ⤔ E"), "r;s ∈ E ⤔ E", true);
		doTest(mSet("r ∈ E ⤀ E", "s ∈ E ⤀ E"), "r;s ∈ E ⤀ E", true);
	}

    @Test
	public void testOverride() {
		
		doTest(mList("C","ℙ(C)","D","ℙ(D)"),mSet("f ∈ C → D", "c ∈ C", "b ∈ D"),
				"(∀x,x0,x1·((x ↦ x0∈f∧¬x=c)∨(x=c∧x0=b))∧((x ↦ x1∈f∧¬x=c)∨(x=c∧x1=b))⇒x0=x1)", true);
		
//		doTest(mList("C","ℙ(E)","D","ℙ(E)"),mSet("f ∈ C → D", "c ∈ C", "b ∈ D"),
//				"(∀x,x0·(x ↦ x0∈f∧¬x=c)∨(x=c∧x0=b)⇒x∈C∧x0∈D)", true);
//		doTest(mList("C","ℙ(E)","D","ℙ(E)"),mSet("f ∈ C → D", "c ∈ C", "b ∈ D"),
//				"(∀x·x∈C⇒(∃x0·(x ↦ x0∈f∧¬x=c)∨(x=c∧x0=b)))", true);
//
//		
//		doTest(mList("C","ℙ(E)","D","ℙ(E)"),mSet("f ∈ C → D", "c ∈ C", "b ∈ D"),
//				"(∀x,x0·(x ↦ x0∈f∧¬x=c)∨(x=c∧x0=b)⇒x∈C∧x0∈D)" +
//				"∧(∀x,x0,x1·((x ↦ x0∈f∧¬x=c)∨(x=c∧x0=b))∧((x ↦ x1∈f∧¬x=c)∨(x=c∧x1=b))⇒x0=x1)", true);
//		doTest(mList("C","ℙ(E)","D","ℙ(E)"),mSet("f ∈ C → D", "c ∈ C", "b ∈ D"),
//				"(∀x,x0·(x ↦ x0∈f∧¬x=c)∨(x=c∧x0=b)⇒x∈C∧x0∈D)" +
//				"∧(∀x·x∈C⇒(∃x0·(x ↦ x0∈f∧¬x=c)∨(x=c∧x0=b)))", true);
//		doTest(mList("C","ℙ(E)","D","ℙ(E)"),mSet("f ∈ C → D", "c ∈ C", "b ∈ D"),
//				"(∀x,x0,x1·((x ↦ x0∈f∧¬x=c)∨(x=c∧x0=b))∧((x ↦ x1∈f∧¬x=c)∨(x=c∧x1=b))⇒x0=x1)" +
//				"∧(∀x·x∈C⇒(∃x0·(x ↦ x0∈f∧¬x=c)∨(x=c∧x0=b)))", true);
		
		
		doTest(mSet("f ∈ C → D", "c ∈ C", "b ∈ D"),
				"(({c}⩤f)∪{c↦b}) ∈  C → D", true);	
		
		doTest(mSet(
				"f ∈ C ↔ D",
				"c ∈ C",
				"b ∈ D"
		), "f{c↦b} ∈ C ↔ D",true
		);
	}
	
    @Test
	public void testAll() {
			// f: S-->T
			// a/:S
			// b/:T
			// |--
			// f<+{a|->b} : S\/{a} --> T\/{b}

		doTest(mSet("f ∈ C ↔ D", "c ∈ C", "b ∈ D"), 
				"(({c}⩤f)∪{c↦b}) ∈ C ↔ D",
				true);
		

			// (!(x?$10,x?$9).(x?$10,x?$9: f and not(x?$10 = c) or (x?$10 = c
			// and x?$9 = d) => x?$10: C and x?$9: D)) and
			// !(x?$13,x?$12,x?$11).(x?$13,x?$12: f and not(x?$13 = c) or (x?$13
			// = c and x?$12 = d) and (x?$13,x?$11: f and not(x?$13 = c) or
			// (x?$13 = c and x?$11 = d)) => x?$12 = x?$11) and
			// !(x?$14).(x?$14: C => #(x?$15).(x?$14,x?$15: f and not(x?$14 = c)
			// or (x?$14 = c and x?$15 = d)))

			// doTest(mSet(
			// "f ∈ C → D",
			// "c ∈ C",
			// "b ∈ D"
			// ), "f{c↦b} ∈ C → D",true
			// );

			// doTest(mSet(
			// "f ∈ C → D",
			// "c ∉ C",
			// "b ∉ D"
			// ), "f{c↦b} ∈ C ∪ {c} → D ∪ {b}",true
			// );
			doTest(mSet("∃y·y = k ∧ y ∈ x"), "k∈x", true);

			doTest(mSet("X ⊆ B", "B ⊆ X"), "∀x·x∈X ⇔ x∈B", true);
			doTest(mSet("X ⊆ M", "M ⊆ X"), "M = X", true);
			doTest(mSet("X ⊆ B", "B ⊆ X"), "X = B", true);
			doTest(mSet("x ⊆ B"), "B ∖ (B ∖ x) = x", true);
			doTest(mSet("x ⊆ B"), "B ∖ (B ∖ x) = x", true);
			doTest(new HashSet<String>(), "S ∖ (S ∖ k) = k", true);
			// doTest(mSet(
			// "S ∖ (S ∖ k) ∈ x"
			// ),"k ∈ x",true
			// );

			// translation
			// doTest(new HashSet<String>(),
			// "((D=TRUE ⇔ E=TRUE) ⇔ F=TRUE) ⇔ (D=TRUE ⇔ (E=TRUE ⇔
			// F=TRUE))",true
			// );
			// doTest(mSet(
			// "c^2 ∈ C",
			// "c^2 ∉ C"
			// ),"⊥",false
			// );

			// r : a<->b
			// c <: a
			// |---
			// r[c]<:b
			// doTest(mSet(
			// "r ∈ d↔e",
			// "c ⊆ d"
			// ), "r[c] ⊆ e", true
			// );
			//			
			// // x|->y : r
			// // !x,y. y|->x : r => x : b
			// // |---
			// // y:b
			//			
			// doTest(mSet(
			// "x↦y ∈ r",
			// "∀x,y· y↦x ∈ r ⇒ x∈b"
			// ), "y∈b", true
			// );

			// doTest(mSet(
			// "∀x·x^2 ∈ C",
			// "∀x·x^2 ∉ C"
			// ),"⊥",false
			// );
			
			doTest(mSet("r ∈ E ↔ E"), "r ∈ E ↔ E", true);
			
			doTest(mSet("r ∈ E ↔ E", "s ∈ E ↔ E"), "r;s ⊆ E × E", true);
			doTest(mSet("r ∈ E ↔ E", "s ∈ E ↔ E"), "r;s ∈ E ↔ E", true);
			doTest(mSet("r ∈ E ⇸ E", "s ∈ E ⇸ E"), "r;s ∈ E ⇸ E", true);
			doTest(mSet("r ∈ E ↔ E", "s ∈ E ↔ E"), "r;s ∈ ℙ(E × E)", true);
			doTest(mSet("∀x,y·x ↦ y ∈ s ⇒ (x∈E ∧ y∈E)",
					"∀x,y·x ↦ y ∈ r ⇒ (x∈E ∧ y∈E)"),
					"∀x,y·(∃z·x ↦ z ∈ r ∧ z ↦ y ∈ r) ⇒ (x∈E ∧ y∈E)", true);


			// // requires adding set hypothesis
			doTest(mSet("A = S", "C ⊆ S", "A ∈ U"), "C ∪ A ∈ U", true);
			// // requires adding set membership hypothesis
			doTest(mSet("C ⊆ S", "S ∈ U"), "C ∪ S ∈ U", true);
			doTest(mSet("C ⊆ B", "B ∈ U"), "C ∪ B ∈ U", true);
			doTest(mSet("A ⊆ B", "B ⊆ C"), "A ⊆ C", true);
			// fails at the moment
			doTest(mSet("f ∈ S ⇸ T", "x ∉ dom(f)", "y ∈ T"),
					"f ∪ {x ↦ y} ∈ S ⇸ T", true);
			doTest(mSet("f ∈ S ⇸ T"), "f∼[C ∩ D] = f∼[C] ∩ f∼[D]", true);
			// fails when not generating negative labels
			doTest(mSet("f ∈ S ⇸ T"), "f∼[C ∖ D] = f∼[C] ∖ f∼[D]", true);
			doTest(mSet("f ∈ S ⤔ T"), "f[C ∩ D] = f[C] ∩ f[D]", true);
			// doTest(mSet(
			// "f ∈ S ↔ T"
			// ),"f∼[C ∩ D] = f∼[C] ∩ f∼[D]", false
			// );
			doTest(mSet("dap;org ⊆ sit", "sit(p)=org(d)",
			// "p ∈ dom(sit)", // unneeded
					// "d ∈ dom(org)", // unneeded
					"org ∈ D ⇸ L", "sit ∈ P → L"), "(dap ∪ {p↦d});org ⊆ sit",
					true);
			// doTest(mSet(
			// "dap;org ⊆ sit",
			// "sit(p)=org(d)",
			// // "p ∈ dom(sit)",
			// // "d ∈ dom(org)",
			// "org ∈ D ⇸ L",
			// "sit ∈ P → L"
			// ),"(dap  {p↦d});org ⊆ sit",true
			// );
			doTest(mSet("(A∪B)∩(A∪C)∈U"), "A∪(B∩C)∈U", true);
			// fails when instantiationcount = 1
			doTest(mSet("A∪B∈U", "(A∪B)∩(A∪C)∈U"), "A∪(B∩C)∈U", true);
			doTest(mSet("A∪B∈U", "A∪C∈U", "(A∪B)∩(A∪C)∈U"),
					"A∪(B∩C)=(A∪B)∩(A∪C)", true);
			doTest(mSet("A∪B∈U", "(A∪B)∩(A∪C)∈U"), "A∪(B∩C)=(A∪B)∩(A∪C)",
					true);
			doTest(mSet("A∪B∈U", "A∪C∈U", "(A∪B)∩(A∪C)∈U"),
					"A∪(B∩C)=(A∪B)∩(A∪C)", true);
			doTest(mSet("∅∉U", "A∪B∈U", "A∪C∈U", "(A∪B)∩(A∪C)∈U"),
					"A∪(B∩C)=(A∪B)∩(A∪C)", true);
			doTest(mSet("(A∪B)∩(A∪C)∈U"), "A∪(B∩C)=(A∪B)∩(A∪C)", true);
			doTest(mSet("(A∪B)∈U"), "A∈U", false);
			doTest(mSet("r ∈ ran(r)∖{x} → ran(r)", "r∼[q]⊆q", "x∈q"),
					"ran(r)∖r∼[ran(r)∖q]⊆q", true);
			doTest(mSet("A = G"), "G ∪ (B ∩ C) = (A ∪ B) ∩ (A ∪ C)", true);
			doTest(mSet("q ⊆ R"), "R ∖ q ⊆ R", true);
			doTest(
					mSet(
							"∀r·r∈R⇒nxt(r)∈rtbl∼[{r}] ∖ {lst(r)} ⤖ rtbl∼[{r}] ∖ {fst(r)}",
							"nxt∈R → (B ⤔ B)"),
					"∀r·r∈R⇒r∈dom(nxt)∧nxt∼;({r} ◁ nxt)⊆id∧r∈dom(nxt)∧nxt∼;({r} ◁ nxt)⊆id",
					true);
			doTest(mSet("R ⊆ C"), "r[R] ⊆ r[C]", true);
			doTest(mSet("a = c"), "a ∈ {c,d}", true);
			doTest(mSet("(∃x,y·f(x)=y ∧ g(y)=a)"), "(∃x·(g∘f)(x)=a)",
					true);
	// doTest(mSet("(∀x·(∃x0·x ↦ x0∈SIG)⇒(∃x0·x0 ↦ x∈fst))" +
	// "∧" +
	// "(∀x,x0,x1·x ↦ x0∈SIG∧x ↦ x1∈SIG⇒x0=x1)" +
	// "∧" +
	// "(∀x·(∃x0·x0 ↦ x∈fst)⇒(∃x0·x ↦ x0∈SIG))" +
	// "∧" +
	// "(∀x·∃x0·x0 ↦ x∈SIG)" +
	// "∧" +
	// "(∀x,x0,x1·x0 ↦ x∈SIG∧x1 ↦ x∈SIG⇒x0=x1)"),"⊥",false)
	}

    @Test
	public void testTrueGoal() {
		doTest(new HashSet<String>(), "⊤", true);
	}

    @Test
	public void testFalseHypothesis() {
		doTest(mSet("⊥"), "⊥", true);
	}
	
    @Test
	public void testBug1833264() {
		doTest(mList("DO", "S"),
				new HashSet<String>(),
				"f(bool((DO=DC ∧ oD=TRUE) ∨ (DO=DO ∧ cD=TRUE)) ↦ DO)" +
				"< f(bool((dEC=DC ∧ oD=TRUE) ∨ (dEC=DO∧cD=TRUE)) ↦ dEC)",
				false);
		
	}
	
    @Test
	public void test1833264_1() throws Exception {
		
		doTest(mList("DO", "S"),
				new HashSet<String>(),
				"(DO=DC ∧ oD=TRUE) ⇔ " +
				"((dEC=DC ∧ oD=TRUE) ∨ (dEC=DO∧cD=TRUE))",
				false);
				
	}
	
    @Test
	public void testBug1840292(){
		doTest(mList("r3", "ℙ(S×S)", "r2", "ℙ(S×S)", "r", "ℙ(S×S)", "S",
				"ℙ(S)", "R", "ℙ(ℙ(S×S))"), mSet("R∈ℙ(S ↔ S)", "r∈R", "r∼∈R",
				"r∩id=∅", "∅∈R", "r3∈S ↔ S", "r2∈S ↔ S"), "(r ∖ r2);r3⊆r3",
				false,
				2000);
	}
	
    @Test
	public void testBug1840292_1(){
		doTest(mList("r", "ℙ(S×S)", "S", "ℙ(S)", "R", "ℙ(ℙ(S×S))"),
				mSet("r∼∈ U", "∅∈R", "r∩id=∅"),
				"r = ∅",
				false,
				2000);
	}

	/**
	 * Initial lemma as entered in the bug report
	 */
    @Test
	public void testBug_1920747() {
		doTest(
				mList(
					"set1","ℙ(set1)"
				), mSet(
					"cst1 ⊆ set1",
					"cst2 ⊆ set1",
					"cst3 ∈ cst1"
				), "cst3 ∈ cst2"
				, false);
	}

	/**
	 * Simplified version of the lemma from the bug report
	 */
    @Test
	public void testBug_1920747_1() {
		doTest(
				mList(
					"A","ℙ(S)",
					"B","ℙ(S)"
				), mSet(
					"x ∈ A"
				), "x ∈ B"
				, false);
	}

	/**
	 * Ensures that a simple lemma of typed set-theory is discharged.
	 * <p>
	 * Here, we need that a quantified variable gets instantiated by a simple type.
	 * </p>
	 */
    @Test
	public void testTypeInstantiation() {
		doTest(
				mList(
						"S","ℙ(S)"
				), Util.<String>mSet(
				), "∃y·∀x·x ∈ S ⇒ x ∈ y"
				, true);
	}

	// public static void main(String[] args) {
	// RodinTests test = new RodinTests();
	// test.testAll();
	// }

    // Test from Son's model of dynamic stable LSR
    // Used to cause an internal error in the loader
    @Test
	public void testLSR() throws Exception {
		doTest( //
				mList( //
						"NODES", "ℙ(NODES)", //
						"DLinks", "ℙ(NODES×NODES)", //
						"RLinks", "ℙ(NODES×NODES)", //
						"link", "NODES×NODES", //
						"rlinks", "ℙ(NODES×ℙ(NODES×NODES))", //
						"n", "NODES", //
						"x", "NODES", //
						"x0", "NODES" //
				), mSet( //
						"∀n·rlinks(n) ⊆ RLinks ∪ DLinks", //
						"¬link∈RLinks", //
						"x ↦ x0∈rlinks(n)" //
				), "x ↦ x0 ∈ RLinks ∪ {link} ∪ (DLinks ∖ {link})", //
				true);
	}

    @Test
	public void testBUG() throws Exception {
		doTest( //
				mList( //
						"S", "ℙ(S)", //
						"a", "S" //
				), Util.<String> mSet(), //
				"a=b ∨ c=b ⇔ a=b ∨ d=b", //
				false);
	}

	@Test
	public void testMapletVariable() throws Exception {
		doTest( //
				mList( //
						"S", "ℙ(S)", //
						"T", "ℙ(T)", //
						"A", "ℙ(S)", //
						"a", "ℙ(S)", //
						"B", "ℙ(T)", //
						"b", "ℙ(T)", //
						"x", "S×T" //
				), mSet(//
						"a ⊆ A",//
						"b ⊆ B",//
						"x ∈ a×b"), //
				"x ∈ A×B", //
				true);
	}

	@Test
	public void bug2961857() throws Exception {
		doTest( //
				mList( //
						"S", "ℙ(S)", //
						"T", "ℙ(T)", //
						"p", "S×T" //
				), Util.<String>mSet(), //
				"p ∈ dom(prj1)", //
				true);
	}

	@Test
	public void bug3029910() {
		doTest(mList("a", "S", "b", "S"), mSet("{a,b} = ∅"), "⊥", true);
	}

	@Test
	public void bug3085103() {
		doTest(mList("y", "BOOL"), mSet("t=FALSE", "y=z"), "t=TRUE ⇔ y=z",
				false);
	}

	@Test
	public void bug3102775() {
		doTest(mTypeEnvironment(), mSet("f(TRUE) = 1", "f(FALSE) = 0"), "⊥",
				false);
	}

}
