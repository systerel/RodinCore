/*******************************************************************************
 * Copyright (c) 2008, 2012 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.pp;

import static org.eventb.internal.pp.core.elements.terms.Util.mSet;

import org.junit.Ignore;
import org.junit.Test;

/**
 * Tests about the new Predicate Prover instantiating two many clauses during
 * seed search.
 * 
 * The predicates used here come initially from machine "cache_12" of project
 * "Cache" as used by Jann Röder.
 * 
 * @author Laurent Voisin
 */
@Ignore("Takes too much time")
public class SeedSearchProblem extends AbstractRodinTest {

	@Test
	public void lemma1() throws Exception {
		doTest(mTypeEnvironment(
				"lineRequested=ADDRESS↔PROCESS; E=FLAGS;" //
				+" exclusive=ADDRESS↔PROCESS; M=FLAGS; O=FLAGS;" //
				+" FLAGS=ℙ(FLAGS); I=FLAGS; caches=PROCESS↔ADDRESS↔DATA;" //
				+" invalid=ADDRESS↔PROCESS; RUNNING=TIMER_STATE; S=FLAGS;" //
				+" mem=ADDRESS↔DATA; v_mem=ADDRESS↔DATA; LINE=ℙ(DATA);" //
				+" FLAG=ℙ(FLAGS); CPUReadRequests=PROCESS↔ADDRESS;" //
				+" shared=ADDRESS↔PROCESS;" //
				+" ownershipRequested=ADDRESS↔PROCESS;" //
				+" CPUWriteRequests=PROCESS↔(ADDRESS×DATA); c=PROCESS;" //
				+" a=ADDRESS; ADDRESS=ℙ(ADDRESS);" //
				+" cacheTimer=PROCESS↔TIMER_STATE;" //
				+" l=DATA; OFF=TIMER_STATE; TIMER_STATE=ℙ(TIMER_STATE);" //
				+" owned=ADDRESS↔PROCESS; CONTROLLER=ℙ(PROCESS);" //
				+" modified=ADDRESS↔PROCESS; memoryTimer=TIMER_STATE;" //
				+" flag=PROCESS↔ADDRESS×FLAGS; PROCESS=ℙ(PROCESS);" //
				+" DATA=ℙ(DATA); TIMEOUT=TIMER_STATE",
				ff),
				mSet(
						"l∈LINE", //
						"c∈CONTROLLER", //
						"c∈dom(CPUWriteRequests)", //
						"CPUWriteRequests(c)=a ↦ l", //
						"ownershipRequested=(∅ ⦂ ℙ(ADDRESS×PROCESS))", //
						"(¬CPUReadRequests=(∅ ⦂ ℙ(PROCESS×ADDRESS))∨¬CPUWriteRequests=(∅ ⦂ ℙ(PROCESS×(ADDRESS×DATA))))∧lineRequested=(∅ ⦂ ℙ(ADDRESS×PROCESS))⇒(∃l⦂DATA,c⦂PROCESS,a⦂ADDRESS·l∈LINE∧c∈CONTROLLER∧c∈dom(CPUWriteRequests)∧CPUWriteRequests(c)=a ↦ l⇒flag(c ↦ a)∈{M,E}∧lineRequested=(∅ ⦂ ℙ(ADDRESS×PROCESS)))∨(∃l⦂DATA,c⦂PROCESS,a⦂ADDRESS·l∈LINE∧c∈CONTROLLER∧c∈dom(CPUReadRequests)∧CPUReadRequests(c)=a∧¬flag(c ↦ a)=I∧l=(caches(c))(a)∧(¬lineRequested=(∅ ⦂ ℙ(ADDRESS×PROCESS))⇒a ↦ c∈lineRequested))∨(∃p⦂PROCESS,a⦂ADDRESS·flag(p ↦ a)=I∧lineRequested=(∅ ⦂ ℙ(ADDRESS×PROCESS))∧p∈dom(CPUReadRequests)∧CPUReadRequests(p)=a)∨(∃p⦂PROCESS,a⦂ADDRESS,d⦂DATA·flag(p ↦ a)∈{I,S,O}∧lineRequested=(∅ ⦂ ℙ(ADDRESS×PROCESS))∧p∈dom(CPUWriteRequests)∧CPUWriteRequests(p)=a ↦ d)", //
						"(¬CPUReadRequests=(∅ ⦂ ℙ(PROCESS×ADDRESS))∨¬CPUWriteRequests=(∅ ⦂ ℙ(PROCESS×(ADDRESS×DATA))))∧lineRequested=(∅ ⦂ ℙ(ADDRESS×PROCESS))⇒(∀l⦂DATA,c⦂PROCESS·l∈LINE∧c∈CONTROLLER∧c∈dom(CPUWriteRequests)⇒c∈dom(CPUWriteRequests)∧CPUWriteRequests∈PROCESS ⇸ ADDRESS × DATA)∧(∀l⦂DATA,c⦂PROCESS,a⦂ADDRESS·l∈LINE∧c∈CONTROLLER∧c∈dom(CPUWriteRequests)∧CPUWriteRequests(c)=a ↦ l⇒c ↦ a∈dom(flag)∧flag∈PROCESS × ADDRESS ⇸ FLAGS)∧(∀l⦂DATA,c⦂PROCESS,p⦂PROCESS,a⦂ADDRESS·(l∈LINE∧c∈CONTROLLER∧c∈dom(CPUWriteRequests)∧CPUWriteRequests(c)=a ↦ l⇒flag(c ↦ a)∈{M,E}∧lineRequested=(∅ ⦂ ℙ(ADDRESS×PROCESS)))∨((l∈LINE∧c∈CONTROLLER∧c∈dom(CPUReadRequests)⇒c∈dom(CPUReadRequests)∧CPUReadRequests∈PROCESS ⇸ ADDRESS∧(CPUReadRequests(c)=a⇒c ↦ a∈dom(flag)∧flag∈PROCESS × ADDRESS ⇸ FLAGS∧(¬flag(c ↦ a)=I⇒c∈dom(caches)∧caches∈PROCESS ⇸ ℙ(ADDRESS × DATA)∧a∈dom(caches(c))∧caches(c)∈ADDRESS ⇸ DATA)))∧((l∈LINE∧c∈CONTROLLER∧c∈dom(CPUReadRequests)∧CPUReadRequests(c)=a∧¬flag(c ↦ a)=I∧l=(caches(c))(a)∧(¬lineRequested=(∅ ⦂ ℙ(ADDRESS×PROCESS))⇒a ↦ c∈lineRequested))∨(p ↦ a∈dom(flag)∧flag∈PROCESS × ADDRESS ⇸ FLAGS∧(flag(p ↦ a)=I∧lineRequested=(∅ ⦂ ℙ(ADDRESS×PROCESS))∧p∈dom(CPUReadRequests)⇒p∈dom(CPUReadRequests)∧CPUReadRequests∈PROCESS ⇸ ADDRESS)∧((flag(p ↦ a)=I∧lineRequested=(∅ ⦂ ℙ(ADDRESS×PROCESS))∧p∈dom(CPUReadRequests)∧CPUReadRequests(p)=a)∨(p ↦ a∈dom(flag)∧flag∈PROCESS × ADDRESS ⇸ FLAGS∧(flag(p ↦ a)∈{I,S,O}∧lineRequested=(∅ ⦂ ℙ(ADDRESS×PROCESS))∧p∈dom(CPUWriteRequests)⇒p∈dom(CPUWriteRequests)∧CPUWriteRequests∈PROCESS ⇸ ADDRESS × DATA)))))))", //
						"finite(ADDRESS)", //
						"finite(PROCESS)", //
						"FLAGS={I,M,E,S,O}", //
						"LINE=DATA", //
						"CONTROLLER=PROCESS", //
						"FLAG=FLAGS", //
						"v_mem∈ADDRESS → DATA", //
						"caches∈PROCESS → (ADDRESS ⇸ DATA)", //
						"∀a⦂ADDRESS,p⦂PROCESS·a∈dom(caches(p))⇒(caches(p))(a)=v_mem(a)", //
						"mem∈ADDRESS → DATA", //
						"∀a⦂ADDRESS·v_mem(a)=mem(a)∨(∃p⦂PROCESS·a∈dom(caches(p)))", //
						"v_mem=memunion(caches[PROCESS])", //
						"modified∈ADDRESS ⇸ PROCESS", //
						"exclusive∈ADDRESS ⇸ PROCESS", //
						"owned∈ADDRESS ⇸ PROCESS", //
						"invalid∪modified∪exclusive∪shared∪owned=ADDRESS × PROCESS", //
						"ownershipRequested∈ADDRESS ⇸ PROCESS", //
						"ownershipRequested⊆invalid∪shared∪owned", //
						"flag∈PROCESS × ADDRESS → FLAGS", //
						"∀a⦂ADDRESS,p⦂PROCESS·flag(p ↦ a)=I⇔a ↦ p∈invalid", //
						"∀a⦂ADDRESS,p⦂PROCESS·flag(p ↦ a)=M⇔a ↦ p∈modified", //
						"∀a⦂ADDRESS,p⦂PROCESS·flag(p ↦ a)=E⇔a ↦ p∈exclusive", //
						"∀a⦂ADDRESS,p⦂PROCESS·flag(p ↦ a)=S⇔a ↦ p∈shared", //
						"∀a⦂ADDRESS,p⦂PROCESS·flag(p ↦ a)=O⇔a ↦ p∈owned", //
						"cacheTimer∈PROCESS → TIMER_STATE", //
						"∀a⦂ADDRESS·memoryTimer=TIMEOUT∧a∈dom(lineRequested)⇒flag[PROCESS × {a}]={I}", //
						"∀p⦂PROCESS·cacheTimer(p)=RUNNING⇒p∈ran(ownershipRequested)", //
						"memoryTimer=RUNNING⇒(∃a⦂ADDRESS,c⦂PROCESS·a ↦ c∈lineRequested∧flag(c ↦ a)=I)", //
						"memoryTimer=RUNNING⇒(∀a⦂ADDRESS,c⦂PROCESS·a ↦ c∈lineRequested⇒c ↦ a∈dom(flag)∧flag∈PROCESS × ADDRESS ⇸ FLAGS)", //
						"memoryTimer=OFF⇒(∀a⦂ADDRESS,c⦂PROCESS·a ↦ c∈lineRequested⇒c ↦ a∈dom(flag)∧flag∈PROCESS × ADDRESS ⇸ FLAGS)", //
						"CPUReadRequests∈CONTROLLER ⇸ ADDRESS", //
						"CPUWriteRequests∈CONTROLLER ⇸ ADDRESS × LINE", //
						"lineRequested⊆CPUReadRequests∼", //
						"∀a⦂ADDRESS,c⦂PROCESS·a ↦ c∈ownershipRequested⇒(∃l⦂DATA·c ↦ (a ↦ l)∈CPUWriteRequests)", //
						"∀c⦂PROCESS·cacheTimer(c)=RUNNING⇒¬ownershipRequested=(∅ ⦂ ℙ(ADDRESS×PROCESS))", //
						"memoryTimer=RUNNING⇒¬lineRequested=(∅ ⦂ ℙ(ADDRESS×PROCESS))", //
						"∀a⦂ADDRESS,p⦂PROCESS,d⦂DATA·flag(p ↦ a)∈{I,S,O}∧ownershipRequested=(∅ ⦂ ℙ(ADDRESS×PROCESS))∧lineRequested=(∅ ⦂ ℙ(ADDRESS×PROCESS))∧p∈dom(CPUWriteRequests)∧CPUWriteRequests(p)=a ↦ d⇒{a2⦂ADDRESS,c⦂PROCESS·∃l⦂DATA·c ↦ (a2 ↦ l)∈CPUWriteRequests ∣ a2 ↦ c} ∖ {a ↦ p}⊂{a3⦂ADDRESS,c2⦂PROCESS·∃l2⦂DATA·c2 ↦ (a3 ↦ l2)∈CPUWriteRequests ∣ a3 ↦ c2} ∖ ownershipRequested", //
						"CPUWriteRequests∈PROCESS ⇸ ADDRESS × DATA", //
						"¬I=M", //
						"¬I=E", //
						"¬I=S", //
						"¬I=O", //
						"¬M=E", //
						"¬M=S", //
						"¬M=O", //
						"¬E=S", //
						"¬E=O", //
						"¬S=O", //
						"∀p⦂PROCESS,a⦂ADDRESS·a ↦ p∈lineRequested⇒(a∈dom(caches(p))∧(∃d⦂DATA·d=(caches(p))(a)))∨(¬a∈dom(caches(p))∧a ↦ p∈lineRequested)", //
						"∀a⦂ADDRESS·¬v_mem(a)=mem(a)⇒(∃p⦂PROCESS·a∈dom(caches(p)))", //
						"∀p⦂PROCESS,a⦂ADDRESS·a ↦ p∈lineRequested⇒(∃p2⦂PROCESS·a∈dom(caches(p2))∧¬mem(a)=(caches(p2))(a))∨(a∈dom(caches(p))∧(∃d⦂DATA·d=(caches(p))(a)))∨(¬a∈dom(caches(p))∧mem(a)=v_mem(a)∧a ↦ p∈lineRequested)", //
						"∀p⦂PROCESS,a⦂ADDRESS·lineRequested=(∅ ⦂ ℙ(ADDRESS×PROCESS))⇒(∀p2⦂PROCESS·¬p2=p⇒¬a∈dom(caches(p2)))∨(∃p3⦂PROCESS·¬p3=p∧a∈dom(caches(p3))∧¬mem(a)=(caches(p3))(a))∨(∃p4⦂PROCESS·¬p4=p∧a∈dom(caches(p4))∧(mem(a)=v_mem(a)∨(∃p5⦂PROCESS·¬p5=p4∧a∈dom(caches(p5))))∧¬a ↦ p4∈lineRequested)", //
						"∀a⦂ADDRESS,p⦂PROCESS·¬a∈dom(caches(p))⇒a ↦ p∈invalid", //
						"∀a⦂ADDRESS,p⦂PROCESS·a ↦ p∈invalid⇒¬a∈dom(caches(p))", //
						"¬lineRequested=(∅ ⦂ ℙ(ADDRESS×PROCESS))⇒(∃a⦂ADDRESS,p⦂PROCESS·lineRequested={a ↦ p})", //
						"¬ownershipRequested=(∅ ⦂ ℙ(ADDRESS×PROCESS))⇒(∃a⦂ADDRESS,p⦂PROCESS·ownershipRequested={a ↦ p})", //
						"¬lineRequested=(∅ ⦂ ℙ(ADDRESS×PROCESS))⇒ownershipRequested=(∅ ⦂ ℙ(ADDRESS×PROCESS))", //
						"∀a⦂ADDRESS,p⦂PROCESS·cacheTimer(p)=TIMEOUT∧a ↦ p∈ownershipRequested⇒(∀p2⦂PROCESS·¬p2=p⇒flag(p2 ↦ a)=I)", //
						"memoryTimer=TIMEOUT⇒¬lineRequested=(∅ ⦂ ℙ(ADDRESS×PROCESS))", //
						"memoryTimer=OFF⇒(∀a⦂ADDRESS,c⦂PROCESS·a ↦ c∈lineRequested⇒¬flag(c ↦ a)=I)", //
						"∀p⦂PROCESS·cacheTimer(p)=OFF⇒(∀a⦂ADDRESS·¬a ↦ p∈ownershipRequested)", //
						"∀c⦂PROCESS·cacheTimer(c)=RUNNING⇒¬(∃a⦂ADDRESS,c2⦂PROCESS·(a ↦ c2∈ownershipRequested⇒cacheTimer(c2)=TIMEOUT)∧(¬ownershipRequested=(∅ ⦂ ℙ(ADDRESS×PROCESS))⇒a ↦ c2∈ownershipRequested))", //
						"∀c⦂PROCESS·cacheTimer(c)=RUNNING⇒(∃a⦂ADDRESS,p⦂PROCESS·flag(p ↦ a)∈{S,E}∧lineRequested=(∅ ⦂ ℙ(ADDRESS×PROCESS))∧(¬ownershipRequested=(∅ ⦂ ℙ(ADDRESS×PROCESS))⇒a∈dom(ownershipRequested ⩥ {p})))∨(∃a⦂ADDRESS,p⦂PROCESS·flag(p ↦ a)∈{M,O}∧(¬ownershipRequested=(∅ ⦂ ℙ(ADDRESS×PROCESS))⇒a∈dom(ownershipRequested ⩥ {p}))∧lineRequested=(∅ ⦂ ℙ(ADDRESS×PROCESS)))∨(∃a⦂ADDRESS·a ↦ c∈ownershipRequested∧(∀p2⦂PROCESS·¬p2=c⇒flag(p2 ↦ a)=I)∧cacheTimer(c)=RUNNING)", //
						"memoryTimer=RUNNING⇒¬(∃a⦂ADDRESS,c⦂PROCESS·¬flag(c ↦ a)=I∧(¬lineRequested=(∅ ⦂ ℙ(ADDRESS×PROCESS))⇒a ↦ c∈lineRequested))", //
						"memoryTimer=RUNNING⇒(∀a⦂ADDRESS,c⦂PROCESS·c ↦ a∈dom(flag))∧flag∈PROCESS × ADDRESS ⇸ FLAGS", //
						"memoryTimer=RUNNING⇒(∃psource⦂PROCESS,pdest⦂PROCESS,a⦂ADDRESS·flag(psource ↦ a)∈{E,S}∧flag(pdest ↦ a)=I∧a ↦ pdest∈lineRequested)∨(∃psource⦂PROCESS,pdest⦂PROCESS,a⦂ADDRESS·flag(psource ↦ a)∈{M,O}∧flag(pdest ↦ a)=I∧a ↦ pdest∈lineRequested)∨(∃p⦂PROCESS,a⦂ADDRESS·a ↦ p∈lineRequested∧flag[PROCESS × {a}]={I}∧memoryTimer=RUNNING)", //
						"memoryTimer=RUNNING⇒(∀psource⦂PROCESS,a⦂ADDRESS·psource ↦ a∈dom(flag))∧flag∈PROCESS × ADDRESS ⇸ FLAGS∧(∀psource⦂PROCESS,pdest⦂PROCESS,a⦂ADDRESS·flag(psource ↦ a)∈{E,S}⇒pdest ↦ a∈dom(flag)∧flag∈PROCESS × ADDRESS ⇸ FLAGS)∧(∀psource⦂PROCESS,pdest⦂PROCESS,a⦂ADDRESS·(flag(psource ↦ a)∈{E,S}∧flag(pdest ↦ a)=I∧a ↦ pdest∈lineRequested)∨(psource ↦ a∈dom(flag)∧flag∈PROCESS × ADDRESS ⇸ FLAGS∧(flag(psource ↦ a)∈{M,O}⇒pdest ↦ a∈dom(flag)∧flag∈PROCESS × ADDRESS ⇸ FLAGS)))", //
						"∀a⦂ADDRESS,p⦂PROCESS·flag(p ↦ a)=I∧lineRequested=(∅ ⦂ ℙ(ADDRESS×PROCESS))∧ownershipRequested=(∅ ⦂ ℙ(ADDRESS×PROCESS))∧p∈dom(CPUReadRequests)∧CPUReadRequests(p)=a⇒CPUReadRequests ∖ {p ↦ a}⊂CPUReadRequests ∖ lineRequested∼", //
						"∀c⦂PROCESS·cacheTimer(c)=RUNNING⇒(∀c2⦂PROCESS,a⦂ADDRESS·c2∈CONTROLLER∧¬c2∈dom(CPUReadRequests)∧¬c2∈dom(CPUWriteRequests)⇒(CONTROLLER × ADDRESS) ∖ (CPUReadRequests{c2 ↦ a})⊂(CONTROLLER × ADDRESS) ∖ CPUReadRequests)", //
						"∀c⦂PROCESS·cacheTimer(c)=RUNNING⇒(∀c2⦂PROCESS,a⦂ADDRESS,l⦂DATA·l∈LINE∧c2∈CONTROLLER∧¬c2∈dom(CPUWriteRequests)∧¬c2∈dom(CPUReadRequests)⇒(CONTROLLER × (ADDRESS × LINE)) ∖ (CPUWriteRequests{c2 ↦ (a ↦ l)})⊂(CONTROLLER × (ADDRESS × LINE)) ∖ CPUWriteRequests)", //
						"memoryTimer=RUNNING⇒(∀c⦂PROCESS,a⦂ADDRESS·c∈CONTROLLER∧¬c∈dom(CPUReadRequests)∧¬c∈dom(CPUWriteRequests)⇒(CONTROLLER × ADDRESS) ∖ (CPUReadRequests{c ↦ a})⊂(CONTROLLER × ADDRESS) ∖ CPUReadRequests)", //
						"memoryTimer=RUNNING⇒(∀c⦂PROCESS,a⦂ADDRESS,l⦂DATA·l∈LINE∧c∈CONTROLLER∧¬c∈dom(CPUWriteRequests)∧¬c∈dom(CPUReadRequests)⇒(CONTROLLER × (ADDRESS × LINE)) ∖ (CPUWriteRequests{c ↦ (a ↦ l)})⊂(CONTROLLER × (ADDRESS × LINE)) ∖ CPUWriteRequests)", //
						"∀a⦂ADDRESS,c⦂PROCESS,l⦂DATA·l∈LINE∧c∈CONTROLLER∧c∈dom(CPUReadRequests)∧CPUReadRequests(c)=a∧¬flag(c ↦ a)=I∧l=(caches(c))(a)∧(¬lineRequested=(∅ ⦂ ℙ(ADDRESS×PROCESS))⇒a ↦ c∈lineRequested)∧ownershipRequested=(∅ ⦂ ℙ(ADDRESS×PROCESS))⇒CPUReadRequests ∖ {c ↦ a}⊂CPUReadRequests", //
						"∀c⦂PROCESS,a⦂ADDRESS,l⦂DATA·l∈LINE∧c∈CONTROLLER∧c∈dom(CPUWriteRequests)∧CPUWriteRequests(c)=a ↦ l∧(ownershipRequested=(∅ ⦂ ℙ(ADDRESS×PROCESS))⇒flag(c ↦ a)∈{M,E})∧(a ↦ c∈ownershipRequested⇒cacheTimer(c)=TIMEOUT)∧(¬ownershipRequested=(∅ ⦂ ℙ(ADDRESS×PROCESS))⇒a ↦ c∈ownershipRequested)∧lineRequested=(∅ ⦂ ℙ(ADDRESS×PROCESS))⇒CPUWriteRequests ∖ {c ↦ (a ↦ l)}⊂CPUWriteRequests", //
						"¬lineRequested=(∅ ⦂ ℙ(ADDRESS×PROCESS))⇒(∃l⦂DATA,a⦂ADDRESS,c⦂PROCESS·l∈LINE∧c∈CONTROLLER∧c∈dom(CPUReadRequests)∧CPUReadRequests(c)=a∧¬flag(c ↦ a)=I∧l=(caches(c))(a)∧(¬lineRequested=(∅ ⦂ ℙ(ADDRESS×PROCESS))⇒a ↦ c∈lineRequested)∧ownershipRequested=(∅ ⦂ ℙ(ADDRESS×PROCESS)))∨(∃a⦂ADDRESS,p⦂PROCESS·memoryTimer=TIMEOUT∧a ↦ p∈lineRequested)∨(∃a⦂ADDRESS,psource⦂PROCESS,pdest⦂PROCESS·flag(psource ↦ a)∈{E,S}∧flag(pdest ↦ a)=I∧a ↦ pdest∈lineRequested)∨(∃a⦂ADDRESS,psource⦂PROCESS,pdest⦂PROCESS·flag(psource ↦ a)∈{M,O}∧flag(pdest ↦ a)=I∧a ↦ pdest∈lineRequested)∨(∃a⦂ADDRESS,p⦂PROCESS·a ↦ p∈lineRequested∧flag[PROCESS × {a}]={I}∧memoryTimer=RUNNING)", //
						"¬lineRequested=(∅ ⦂ ℙ(ADDRESS×PROCESS))⇒(∀l⦂DATA,a⦂ADDRESS,c⦂PROCESS·l∈LINE∧c∈CONTROLLER∧c∈dom(CPUReadRequests)⇒c∈dom(CPUReadRequests)∧CPUReadRequests∈PROCESS ⇸ ADDRESS∧(CPUReadRequests(c)=a⇒c ↦ a∈dom(flag)∧flag∈PROCESS × ADDRESS ⇸ FLAGS∧(¬flag(c ↦ a)=I⇒c∈dom(caches)∧caches∈PROCESS ⇸ ℙ(ADDRESS × DATA)∧a∈dom(caches(c))∧caches(c)∈ADDRESS ⇸ DATA)))∧(∀l⦂DATA,a⦂ADDRESS,c⦂PROCESS,psource⦂PROCESS,pdest⦂PROCESS,p⦂PROCESS·(l∈LINE∧c∈CONTROLLER∧c∈dom(CPUReadRequests)∧CPUReadRequests(c)=a∧¬flag(c ↦ a)=I∧l=(caches(c))(a)∧(¬lineRequested=(∅ ⦂ ℙ(ADDRESS×PROCESS))⇒a ↦ c∈lineRequested)∧ownershipRequested=(∅ ⦂ ℙ(ADDRESS×PROCESS)))∨(memoryTimer=TIMEOUT∧a ↦ p∈lineRequested)∨(psource ↦ a∈dom(flag)∧flag∈PROCESS × ADDRESS ⇸ FLAGS∧(flag(psource ↦ a)∈{E,S}⇒pdest ↦ a∈dom(flag)∧flag∈PROCESS × ADDRESS ⇸ FLAGS)∧((flag(psource ↦ a)∈{E,S}∧flag(pdest ↦ a)=I∧a ↦ pdest∈lineRequested)∨(psource ↦ a∈dom(flag)∧flag∈PROCESS × ADDRESS ⇸ FLAGS∧(flag(psource ↦ a)∈{M,O}⇒pdest ↦ a∈dom(flag)∧flag∈PROCESS × ADDRESS ⇸ FLAGS)))))", //
						"¬ownershipRequested=(∅ ⦂ ℙ(ADDRESS×PROCESS))⇒(∃l⦂DATA,a⦂ADDRESS,c⦂PROCESS·l∈LINE∧c∈CONTROLLER∧c∈dom(CPUWriteRequests)∧CPUWriteRequests(c)=a ↦ l∧ownershipRequested=(∅ ⦂ ℙ(ADDRESS×PROCESS))⇒flag(c ↦ a)∈{M,E}∧(a ↦ c∈ownershipRequested⇒cacheTimer(c)=TIMEOUT)∧(¬ownershipRequested=(∅ ⦂ ℙ(ADDRESS×PROCESS))⇒a ↦ c∈ownershipRequested)∧lineRequested=(∅ ⦂ ℙ(ADDRESS×PROCESS)))∨(∃a⦂ADDRESS,p⦂PROCESS·flag(p ↦ a)∈{M,O}∧(¬ownershipRequested=(∅ ⦂ ℙ(ADDRESS×PROCESS))⇒a∈dom(ownershipRequested ⩥ {p}))∧lineRequested=(∅ ⦂ ℙ(ADDRESS×PROCESS)))∨(∃a⦂ADDRESS,p⦂PROCESS·flag(p ↦ a)∈{S,E}∧lineRequested=(∅ ⦂ ℙ(ADDRESS×PROCESS))∧(¬ownershipRequested=(∅ ⦂ ℙ(ADDRESS×PROCESS))⇒a∈dom(ownershipRequested ⩥ {p})))∨(∃a⦂ADDRESS,p⦂PROCESS·a ↦ p∈ownershipRequested∧(∀p2⦂PROCESS·¬p2=p⇒flag(p2 ↦ a)=I)∧cacheTimer(p)=RUNNING)", //
						"¬ownershipRequested=(∅ ⦂ ℙ(ADDRESS×PROCESS))⇒(∀l⦂DATA,c⦂PROCESS·l∈LINE∧c∈CONTROLLER∧c∈dom(CPUWriteRequests)⇒c∈dom(CPUWriteRequests)∧CPUWriteRequests∈PROCESS ⇸ ADDRESS × DATA)∧(∀l⦂DATA,a⦂ADDRESS,c⦂PROCESS·l∈LINE∧c∈CONTROLLER∧c∈dom(CPUWriteRequests)∧CPUWriteRequests(c)=a ↦ l∧ownershipRequested=(∅ ⦂ ℙ(ADDRESS×PROCESS))⇒c ↦ a∈dom(flag)∧flag∈PROCESS × ADDRESS ⇸ FLAGS∧(flag(c ↦ a)∈{M,E}∧a ↦ c∈ownershipRequested⇒c∈dom(cacheTimer)∧cacheTimer∈PROCESS ⇸ TIMER_STATE))∧(∀l⦂DATA,a⦂ADDRESS,c⦂PROCESS,p⦂PROCESS·(l∈LINE∧c∈CONTROLLER∧c∈dom(CPUWriteRequests)∧CPUWriteRequests(c)=a ↦ l∧ownershipRequested=(∅ ⦂ ℙ(ADDRESS×PROCESS))⇒flag(c ↦ a)∈{M,E}∧(a ↦ c∈ownershipRequested⇒cacheTimer(c)=TIMEOUT)∧(¬ownershipRequested=(∅ ⦂ ℙ(ADDRESS×PROCESS))⇒a ↦ c∈ownershipRequested)∧lineRequested=(∅ ⦂ ℙ(ADDRESS×PROCESS)))∨(p ↦ a∈dom(flag)∧flag∈PROCESS × ADDRESS ⇸ FLAGS∧((flag(p ↦ a)∈{M,O}∧(¬ownershipRequested=(∅ ⦂ ℙ(ADDRESS×PROCESS))⇒a∈dom(ownershipRequested ⩥ {p}))∧lineRequested=(∅ ⦂ ℙ(ADDRESS×PROCESS)))∨(p ↦ a∈dom(flag)∧flag∈PROCESS × ADDRESS ⇸ FLAGS∧((flag(p ↦ a)∈{S,E}∧lineRequested=(∅ ⦂ ℙ(ADDRESS×PROCESS))∧(¬ownershipRequested=(∅ ⦂ ℙ(ADDRESS×PROCESS))⇒a∈dom(ownershipRequested ⩥ {p})))∨(a ↦ p∈ownershipRequested⇒(∀p2⦂PROCESS·¬p2=p⇒p2 ↦ a∈dom(flag)∧flag∈PROCESS × ADDRESS ⇸ FLAGS)∧((∀p2⦂PROCESS·¬p2=p⇒flag(p2 ↦ a)=I)⇒p∈dom(cacheTimer)∧cacheTimer∈PROCESS ⇸ TIMER_STATE)))))))", //
						"(¬CPUReadRequests=(∅ ⦂ ℙ(PROCESS×ADDRESS))∨¬CPUWriteRequests=(∅ ⦂ ℙ(PROCESS×(ADDRESS×DATA))))∧lineRequested=(∅ ⦂ ℙ(ADDRESS×PROCESS))⇒(∃l⦂DATA,c⦂PROCESS,a⦂ADDRESS·l∈LINE∧c∈CONTROLLER∧c∈dom(CPUWriteRequests)∧CPUWriteRequests(c)=a ↦ l∧ownershipRequested=(∅ ⦂ ℙ(ADDRESS×PROCESS))⇒flag(c ↦ a)∈{M,E}∧(a ↦ c∈ownershipRequested⇒cacheTimer(c)=TIMEOUT)∧(¬ownershipRequested=(∅ ⦂ ℙ(ADDRESS×PROCESS))⇒a ↦ c∈ownershipRequested)∧lineRequested=(∅ ⦂ ℙ(ADDRESS×PROCESS)))∨(∃l⦂DATA,c⦂PROCESS,a⦂ADDRESS·l∈LINE∧c∈CONTROLLER∧c∈dom(CPUReadRequests)∧CPUReadRequests(c)=a∧¬flag(c ↦ a)=I∧l=(caches(c))(a)∧(¬lineRequested=(∅ ⦂ ℙ(ADDRESS×PROCESS))⇒a ↦ c∈lineRequested)∧ownershipRequested=(∅ ⦂ ℙ(ADDRESS×PROCESS)))∨(∃p⦂PROCESS,a⦂ADDRESS·flag(p ↦ a)=I∧lineRequested=(∅ ⦂ ℙ(ADDRESS×PROCESS))∧ownershipRequested=(∅ ⦂ ℙ(ADDRESS×PROCESS))∧p∈dom(CPUReadRequests)∧CPUReadRequests(p)=a)∨(∃p⦂PROCESS,a⦂ADDRESS,d⦂DATA·flag(p ↦ a)∈{I,S,O}∧ownershipRequested=(∅ ⦂ ℙ(ADDRESS×PROCESS))∧lineRequested=(∅ ⦂ ℙ(ADDRESS×PROCESS))∧p∈dom(CPUWriteRequests)∧CPUWriteRequests(p)=a ↦ d)", //
						"(¬CPUReadRequests=(∅ ⦂ ℙ(PROCESS×ADDRESS))∨¬CPUWriteRequests=(∅ ⦂ ℙ(PROCESS×(ADDRESS×DATA))))∧lineRequested=(∅ ⦂ ℙ(ADDRESS×PROCESS))⇒(∀l⦂DATA,c⦂PROCESS·l∈LINE∧c∈CONTROLLER∧c∈dom(CPUWriteRequests)⇒c∈dom(CPUWriteRequests)∧CPUWriteRequests∈PROCESS ⇸ ADDRESS × DATA)∧(∀l⦂DATA,c⦂PROCESS,a⦂ADDRESS·l∈LINE∧c∈CONTROLLER∧c∈dom(CPUWriteRequests)∧CPUWriteRequests(c)=a ↦ l∧ownershipRequested=(∅ ⦂ ℙ(ADDRESS×PROCESS))⇒c ↦ a∈dom(flag)∧flag∈PROCESS × ADDRESS ⇸ FLAGS∧(flag(c ↦ a)∈{M,E}∧a ↦ c∈ownershipRequested⇒c∈dom(cacheTimer)∧cacheTimer∈PROCESS ⇸ TIMER_STATE))∧(∀l⦂DATA,c⦂PROCESS,p⦂PROCESS,a⦂ADDRESS·(l∈LINE∧c∈CONTROLLER∧c∈dom(CPUWriteRequests)∧CPUWriteRequests(c)=a ↦ l∧ownershipRequested=(∅ ⦂ ℙ(ADDRESS×PROCESS))⇒flag(c ↦ a)∈{M,E}∧(a ↦ c∈ownershipRequested⇒cacheTimer(c)=TIMEOUT)∧(¬ownershipRequested=(∅ ⦂ ℙ(ADDRESS×PROCESS))⇒a ↦ c∈ownershipRequested)∧lineRequested=(∅ ⦂ ℙ(ADDRESS×PROCESS)))∨((l∈LINE∧c∈CONTROLLER∧c∈dom(CPUReadRequests)⇒c∈dom(CPUReadRequests)∧CPUReadRequests∈PROCESS ⇸ ADDRESS∧(CPUReadRequests(c)=a⇒c ↦ a∈dom(flag)∧flag∈PROCESS × ADDRESS ⇸ FLAGS∧(¬flag(c ↦ a)=I⇒c∈dom(caches)∧caches∈PROCESS ⇸ ℙ(ADDRESS × DATA)∧a∈dom(caches(c))∧caches(c)∈ADDRESS ⇸ DATA)))∧((l∈LINE∧c∈CONTROLLER∧c∈dom(CPUReadRequests)∧CPUReadRequests(c)=a∧¬flag(c ↦ a)=I∧l=(caches(c))(a)∧(¬lineRequested=(∅ ⦂ ℙ(ADDRESS×PROCESS))⇒a ↦ c∈lineRequested)∧ownershipRequested=(∅ ⦂ ℙ(ADDRESS×PROCESS)))∨(p ↦ a∈dom(flag)∧flag∈PROCESS × ADDRESS ⇸ FLAGS∧(flag(p ↦ a)=I∧lineRequested=(∅ ⦂ ℙ(ADDRESS×PROCESS))∧ownershipRequested=(∅ ⦂ ℙ(ADDRESS×PROCESS))∧p∈dom(CPUReadRequests)⇒p∈dom(CPUReadRequests)∧CPUReadRequests∈PROCESS ⇸ ADDRESS)∧((flag(p ↦ a)=I∧lineRequested=(∅ ⦂ ℙ(ADDRESS×PROCESS))∧ownershipRequested=(∅ ⦂ ℙ(ADDRESS×PROCESS))∧p∈dom(CPUReadRequests)∧CPUReadRequests(p)=a)∨(p ↦ a∈dom(flag)∧flag∈PROCESS × ADDRESS ⇸ FLAGS∧(flag(p ↦ a)∈{I,S,O}∧ownershipRequested=(∅ ⦂ ℙ(ADDRESS×PROCESS))∧lineRequested=(∅ ⦂ ℙ(ADDRESS×PROCESS))∧p∈dom(CPUWriteRequests)⇒p∈dom(CPUWriteRequests)∧CPUWriteRequests∈PROCESS ⇸ ADDRESS × DATA)))))))" //
				), "c ↦ a∈dom(flag)", //
				true, 100);
	}

	@Test
	public void lemma2() throws Exception {
		doTest(mTypeEnvironment(
				"E=FLAGS; lineRequested=ADDRESS↔PROCESS;" //
				+" exclusive=ADDRESS↔PROCESS; M=FLAGS; O=FLAGS;" //
				+" FLAGS=ℙ(FLAGS); I=FLAGS; caches=PROCESS↔ℙ(ADDRESS×DATA);" //
				+" invalid=ADDRESS↔PROCESS; RUNNING=TIMER_STATE; S=FLAGS;" //
				+" mem=ADDRESS↔DATA; v_mem=ADDRESS↔DATA; LINE=ℙ(DATA);" //
				+" FLAG=ℙ(FLAGS); CPUReadRequests=PROCESS↔ADDRESS;" //
				+" shared=ADDRESS↔PROCESS;" //
				+" ownershipRequested=ADDRESS↔PROCESS;" //
				+" CPUWriteRequests=PROCESS↔(ADDRESS×DATA); c=PROCESS;" //
				+" a=ADDRESS; ADDRESS=ℙ(ADDRESS);" //
				+" cacheTimer=PROCESS↔TIMER_STATE; l=DATA;" //
				+" OFF=TIMER_STATE; TIMER_STATE=ℙ(TIMER_STATE);" //
				+" owned=ADDRESS↔PROCESS; CONTROLLER=ℙ(PROCESS);" //
				+" modified=ADDRESS↔PROCESS; memoryTimer=TIMER_STATE;" //
				+" flag=PROCESS↔ADDRESS×FLAGS; PROCESS=ℙ(PROCESS);" //
				+" DATA=ℙ(DATA); TIMEOUT=TIMER_STATE" //
				, ff),
				mSet(
						"CPUReadRequests∈CONTROLLER ⇸ ADDRESS", //
						"c∈CONTROLLER", //
						"c∈dom(CPUReadRequests)", //
						"CPUReadRequests(c)=a", //
						"ownershipRequested=(∅ ⦂ ℙ(ADDRESS×PROCESS))", //
						"l=(caches(c))(CPUReadRequests(c))", //
						"¬flag(c ↦ CPUReadRequests(c))=I", //
						"¬lineRequested=(∅ ⦂ ℙ(ADDRESS×PROCESS))⇒CPUReadRequests(c) ↦ c∈lineRequested", //
						"(caches(c))(CPUReadRequests(c))∈LINE", //
						"(¬CPUReadRequests=(∅ ⦂ ℙ(PROCESS×ADDRESS))∨¬CPUWriteRequests=(∅ ⦂ ℙ(PROCESS×(ADDRESS×DATA))))∧lineRequested=(∅ ⦂ ℙ(ADDRESS×PROCESS))⇒(∃l⦂DATA,c⦂PROCESS,a⦂ADDRESS·l∈LINE∧c∈CONTROLLER∧c∈dom(CPUWriteRequests)∧CPUWriteRequests(c)=a ↦ l⇒flag(c ↦ a)∈{M,E}∧lineRequested=(∅ ⦂ ℙ(ADDRESS×PROCESS)))∨(∃l⦂DATA,c⦂PROCESS,a⦂ADDRESS·l∈LINE∧c∈CONTROLLER∧c∈dom(CPUReadRequests)∧CPUReadRequests(c)=a∧¬flag(c ↦ a)=I∧l=(caches(c))(a)∧(¬lineRequested=(∅ ⦂ ℙ(ADDRESS×PROCESS))⇒a ↦ c∈lineRequested))∨(∃p⦂PROCESS,a⦂ADDRESS·flag(p ↦ a)=I∧lineRequested=(∅ ⦂ ℙ(ADDRESS×PROCESS))∧p∈dom(CPUReadRequests)∧CPUReadRequests(p)=a)∨(∃p⦂PROCESS,a⦂ADDRESS,d⦂DATA·flag(p ↦ a)∈{I,S,O}∧lineRequested=(∅ ⦂ ℙ(ADDRESS×PROCESS))∧p∈dom(CPUWriteRequests)∧CPUWriteRequests(p)=a ↦ d)", //
						"(¬CPUReadRequests=(∅ ⦂ ℙ(PROCESS×ADDRESS))∨¬CPUWriteRequests=(∅ ⦂ ℙ(PROCESS×(ADDRESS×DATA))))∧lineRequested=(∅ ⦂ ℙ(ADDRESS×PROCESS))⇒(∀l⦂DATA,c⦂PROCESS·l∈LINE∧c∈CONTROLLER∧c∈dom(CPUWriteRequests)⇒c∈dom(CPUWriteRequests)∧CPUWriteRequests∈PROCESS ⇸ ADDRESS × DATA)∧(∀l⦂DATA,c⦂PROCESS,a⦂ADDRESS·l∈LINE∧c∈CONTROLLER∧c∈dom(CPUWriteRequests)∧CPUWriteRequests(c)=a ↦ l⇒c ↦ a∈dom(flag)∧flag∈PROCESS × ADDRESS ⇸ FLAGS)∧(∀l⦂DATA,c⦂PROCESS,p⦂PROCESS,a⦂ADDRESS·(l∈LINE∧c∈CONTROLLER∧c∈dom(CPUWriteRequests)∧CPUWriteRequests(c)=a ↦ l⇒flag(c ↦ a)∈{M,E}∧lineRequested=(∅ ⦂ ℙ(ADDRESS×PROCESS)))∨((l∈LINE∧c∈CONTROLLER∧c∈dom(CPUReadRequests)⇒c∈dom(CPUReadRequests)∧CPUReadRequests∈PROCESS ⇸ ADDRESS∧(CPUReadRequests(c)=a⇒c ↦ a∈dom(flag)∧flag∈PROCESS × ADDRESS ⇸ FLAGS∧(¬flag(c ↦ a)=I⇒c∈dom(caches)∧caches∈PROCESS ⇸ ℙ(ADDRESS × DATA)∧a∈dom(caches(c))∧caches(c)∈ADDRESS ⇸ DATA)))∧((l∈LINE∧c∈CONTROLLER∧c∈dom(CPUReadRequests)∧CPUReadRequests(c)=a∧¬flag(c ↦ a)=I∧l=(caches(c))(a)∧(¬lineRequested=(∅ ⦂ ℙ(ADDRESS×PROCESS))⇒a ↦ c∈lineRequested))∨(p ↦ a∈dom(flag)∧flag∈PROCESS × ADDRESS ⇸ FLAGS∧(flag(p ↦ a)=I∧lineRequested=(∅ ⦂ ℙ(ADDRESS×PROCESS))∧p∈dom(CPUReadRequests)⇒p∈dom(CPUReadRequests)∧CPUReadRequests∈PROCESS ⇸ ADDRESS)∧((flag(p ↦ a)=I∧lineRequested=(∅ ⦂ ℙ(ADDRESS×PROCESS))∧p∈dom(CPUReadRequests)∧CPUReadRequests(p)=a)∨(p ↦ a∈dom(flag)∧flag∈PROCESS × ADDRESS ⇸ FLAGS∧(flag(p ↦ a)∈{I,S,O}∧lineRequested=(∅ ⦂ ℙ(ADDRESS×PROCESS))∧p∈dom(CPUWriteRequests)⇒p∈dom(CPUWriteRequests)∧CPUWriteRequests∈PROCESS ⇸ ADDRESS × DATA)))))))", //
						"finite(ADDRESS)", //
						"finite(PROCESS)", //
						"FLAGS={I,M,E,S,O}", //
						"LINE=DATA", //
						"CONTROLLER=PROCESS", //
						"FLAG=FLAGS", //
						"v_mem∈ADDRESS → DATA", //
						"caches∈PROCESS → (ADDRESS ⇸ DATA)", //
						"∀a⦂ADDRESS,p⦂PROCESS·a∈dom(caches(p))⇒(caches(p))(a)=v_mem(a)", //
						"mem∈ADDRESS → DATA", //
						"∀a⦂ADDRESS·v_mem(a)=mem(a)∨(∃p⦂PROCESS·a∈dom(caches(p)))", //
						"v_mem=memunion(caches[PROCESS])", //
						"modified∈ADDRESS ⇸ PROCESS", //
						"exclusive∈ADDRESS ⇸ PROCESS", //
						"owned∈ADDRESS ⇸ PROCESS", //
						"invalid∪modified∪exclusive∪shared∪owned=ADDRESS × PROCESS", //
						"ownershipRequested∈ADDRESS ⇸ PROCESS", //
						"ownershipRequested⊆invalid∪shared∪owned", //
						"flag∈PROCESS × ADDRESS → FLAGS", //
						"∀a⦂ADDRESS,p⦂PROCESS·flag(p ↦ a)=I⇔a ↦ p∈invalid", //
						"∀a⦂ADDRESS,p⦂PROCESS·flag(p ↦ a)=M⇔a ↦ p∈modified", //
						"∀a⦂ADDRESS,p⦂PROCESS·flag(p ↦ a)=E⇔a ↦ p∈exclusive", //
						"∀a⦂ADDRESS,p⦂PROCESS·flag(p ↦ a)=S⇔a ↦ p∈shared", //
						"∀a⦂ADDRESS,p⦂PROCESS·flag(p ↦ a)=O⇔a ↦ p∈owned", //
						"cacheTimer∈PROCESS → TIMER_STATE", //
						"∀a⦂ADDRESS·memoryTimer=TIMEOUT∧a∈dom(lineRequested)⇒flag[PROCESS × {a}]={I}", //
						"∀p⦂PROCESS·cacheTimer(p)=RUNNING⇒p∈ran(ownershipRequested)", //
						"memoryTimer=RUNNING⇒(∃a⦂ADDRESS,c⦂PROCESS·a ↦ c∈lineRequested∧flag(c ↦ a)=I)", //
						"memoryTimer=RUNNING⇒(∀a⦂ADDRESS,c⦂PROCESS·a ↦ c∈lineRequested⇒c ↦ a∈dom(flag)∧flag∈PROCESS × ADDRESS ⇸ FLAGS)", //
						"memoryTimer=OFF⇒(∀a⦂ADDRESS,c⦂PROCESS·a ↦ c∈lineRequested⇒c ↦ a∈dom(flag)∧flag∈PROCESS × ADDRESS ⇸ FLAGS)", //
						"CPUWriteRequests∈CONTROLLER ⇸ ADDRESS × LINE", //
						"lineRequested⊆CPUReadRequests∼", //
						"∀a⦂ADDRESS,c⦂PROCESS·a ↦ c∈ownershipRequested⇒(∃l⦂DATA·c ↦ (a ↦ l)∈CPUWriteRequests)", //
						"∀c⦂PROCESS·cacheTimer(c)=RUNNING⇒¬ownershipRequested=(∅ ⦂ ℙ(ADDRESS×PROCESS))", //
						"memoryTimer=RUNNING⇒¬lineRequested=(∅ ⦂ ℙ(ADDRESS×PROCESS))", //
						"∀a⦂ADDRESS,p⦂PROCESS,d⦂DATA·flag(p ↦ a)∈{I,S,O}∧ownershipRequested=(∅ ⦂ ℙ(ADDRESS×PROCESS))∧lineRequested=(∅ ⦂ ℙ(ADDRESS×PROCESS))∧p∈dom(CPUWriteRequests)∧CPUWriteRequests(p)=a ↦ d⇒{a2⦂ADDRESS,c⦂PROCESS·∃l⦂DATA·c ↦ (a2 ↦ l)∈CPUWriteRequests ∣ a2 ↦ c} ∖ {a ↦ p}⊂{a3⦂ADDRESS,c2⦂PROCESS·∃l2⦂DATA·c2 ↦ (a3 ↦ l2)∈CPUWriteRequests ∣ a3 ↦ c2} ∖ ownershipRequested", //
						"l∈LINE", //
						"CPUReadRequests∈PROCESS ⇸ ADDRESS", //
						"c ↦ a∈dom(flag)", //
						"flag∈PROCESS × ADDRESS ⇸ FLAGS", //
						"l=(caches(c))(a)", //
						"c∈dom(caches)", //
						"caches∈PROCESS ⇸ ℙ(ADDRESS × DATA)", //
						"a∈dom(caches(c))", //
						"caches(c)∈ADDRESS ⇸ DATA", //
						"¬I=M", //
						"¬I=E", //
						"¬I=S", //
						"¬I=O", //
						"¬M=E", //
						"¬M=S", //
						"¬M=O", //
						"¬E=S", //
						"¬E=O", //
						"¬S=O", //
						"∀p⦂PROCESS,a⦂ADDRESS·a ↦ p∈lineRequested⇒(a∈dom(caches(p))∧(∃d⦂DATA·d=(caches(p))(a)))∨(¬a∈dom(caches(p))∧a ↦ p∈lineRequested)", //
						"∀a⦂ADDRESS·¬v_mem(a)=mem(a)⇒(∃p⦂PROCESS·a∈dom(caches(p)))", //
						"∀p⦂PROCESS,a⦂ADDRESS·a ↦ p∈lineRequested⇒(∃p2⦂PROCESS·a∈dom(caches(p2))∧¬mem(a)=(caches(p2))(a))∨(a∈dom(caches(p))∧(∃d⦂DATA·d=(caches(p))(a)))∨(¬a∈dom(caches(p))∧mem(a)=v_mem(a)∧a ↦ p∈lineRequested)", //
						"∀p⦂PROCESS,a⦂ADDRESS·lineRequested=(∅ ⦂ ℙ(ADDRESS×PROCESS))⇒(∀p2⦂PROCESS·¬p2=p⇒¬a∈dom(caches(p2)))∨(∃p3⦂PROCESS·¬p3=p∧a∈dom(caches(p3))∧¬mem(a)=(caches(p3))(a))∨(∃p4⦂PROCESS·¬p4=p∧a∈dom(caches(p4))∧(mem(a)=v_mem(a)∨(∃p5⦂PROCESS·¬p5=p4∧a∈dom(caches(p5))))∧¬a ↦ p4∈lineRequested)", //
						"∀a⦂ADDRESS,p⦂PROCESS·¬a∈dom(caches(p))⇒a ↦ p∈invalid", //
						"∀a⦂ADDRESS,p⦂PROCESS·a ↦ p∈invalid⇒¬a∈dom(caches(p))", //
						"¬lineRequested=(∅ ⦂ ℙ(ADDRESS×PROCESS))⇒(∃a⦂ADDRESS,p⦂PROCESS·lineRequested={a ↦ p})", //
						"¬ownershipRequested=(∅ ⦂ ℙ(ADDRESS×PROCESS))⇒(∃a⦂ADDRESS,p⦂PROCESS·ownershipRequested={a ↦ p})", //
						"¬lineRequested=(∅ ⦂ ℙ(ADDRESS×PROCESS))⇒ownershipRequested=(∅ ⦂ ℙ(ADDRESS×PROCESS))", //
						"∀a⦂ADDRESS,p⦂PROCESS·cacheTimer(p)=TIMEOUT∧a ↦ p∈ownershipRequested⇒(∀p2⦂PROCESS·¬p2=p⇒flag(p2 ↦ a)=I)", //
						"memoryTimer=TIMEOUT⇒¬lineRequested=(∅ ⦂ ℙ(ADDRESS×PROCESS))", //
						"memoryTimer=OFF⇒(∀a⦂ADDRESS,c⦂PROCESS·a ↦ c∈lineRequested⇒¬flag(c ↦ a)=I)", //
						"∀p⦂PROCESS·cacheTimer(p)=OFF⇒(∀a⦂ADDRESS·¬a ↦ p∈ownershipRequested)", //
						"∀c⦂PROCESS·cacheTimer(c)=RUNNING⇒¬(∃a⦂ADDRESS,c2⦂PROCESS·(a ↦ c2∈ownershipRequested⇒cacheTimer(c2)=TIMEOUT)∧(¬ownershipRequested=(∅ ⦂ ℙ(ADDRESS×PROCESS))⇒a ↦ c2∈ownershipRequested))", //
						"∀c⦂PROCESS·cacheTimer(c)=RUNNING⇒(∃a⦂ADDRESS,p⦂PROCESS·flag(p ↦ a)∈{S,E}∧lineRequested=(∅ ⦂ ℙ(ADDRESS×PROCESS))∧(¬ownershipRequested=(∅ ⦂ ℙ(ADDRESS×PROCESS))⇒a∈dom(ownershipRequested ⩥ {p})))∨(∃a⦂ADDRESS,p⦂PROCESS·flag(p ↦ a)∈{M,O}∧(¬ownershipRequested=(∅ ⦂ ℙ(ADDRESS×PROCESS))⇒a∈dom(ownershipRequested ⩥ {p}))∧lineRequested=(∅ ⦂ ℙ(ADDRESS×PROCESS)))∨(∃a⦂ADDRESS·a ↦ c∈ownershipRequested∧(∀p2⦂PROCESS·¬p2=c⇒flag(p2 ↦ a)=I)∧cacheTimer(c)=RUNNING)", //
						"memoryTimer=RUNNING⇒¬(∃a⦂ADDRESS,c⦂PROCESS·¬flag(c ↦ a)=I∧(¬lineRequested=(∅ ⦂ ℙ(ADDRESS×PROCESS))⇒a ↦ c∈lineRequested))", //
						"memoryTimer=RUNNING⇒(∀a⦂ADDRESS,c⦂PROCESS·c ↦ a∈dom(flag))∧flag∈PROCESS × ADDRESS ⇸ FLAGS", //
						"memoryTimer=RUNNING⇒(∃psource⦂PROCESS,pdest⦂PROCESS,a⦂ADDRESS·flag(psource ↦ a)∈{E,S}∧flag(pdest ↦ a)=I∧a ↦ pdest∈lineRequested)∨(∃psource⦂PROCESS,pdest⦂PROCESS,a⦂ADDRESS·flag(psource ↦ a)∈{M,O}∧flag(pdest ↦ a)=I∧a ↦ pdest∈lineRequested)∨(∃p⦂PROCESS,a⦂ADDRESS·a ↦ p∈lineRequested∧flag[PROCESS × {a}]={I}∧memoryTimer=RUNNING)", //
						"memoryTimer=RUNNING⇒(∀psource⦂PROCESS,a⦂ADDRESS·psource ↦ a∈dom(flag))∧flag∈PROCESS × ADDRESS ⇸ FLAGS∧(∀psource⦂PROCESS,pdest⦂PROCESS,a⦂ADDRESS·flag(psource ↦ a)∈{E,S}⇒pdest ↦ a∈dom(flag)∧flag∈PROCESS × ADDRESS ⇸ FLAGS)∧(∀psource⦂PROCESS,pdest⦂PROCESS,a⦂ADDRESS·(flag(psource ↦ a)∈{E,S}∧flag(pdest ↦ a)=I∧a ↦ pdest∈lineRequested)∨(psource ↦ a∈dom(flag)∧flag∈PROCESS × ADDRESS ⇸ FLAGS∧(flag(psource ↦ a)∈{M,O}⇒pdest ↦ a∈dom(flag)∧flag∈PROCESS × ADDRESS ⇸ FLAGS)))", //
						"∀a⦂ADDRESS,p⦂PROCESS·flag(p ↦ a)=I∧lineRequested=(∅ ⦂ ℙ(ADDRESS×PROCESS))∧ownershipRequested=(∅ ⦂ ℙ(ADDRESS×PROCESS))∧p∈dom(CPUReadRequests)∧CPUReadRequests(p)=a⇒CPUReadRequests ∖ {p ↦ a}⊂CPUReadRequests ∖ lineRequested∼", //
						"¬flag(c ↦ a)=I", //
						"¬lineRequested=(∅ ⦂ ℙ(ADDRESS×PROCESS))⇒a ↦ c∈lineRequested", //
						"∀c⦂PROCESS·cacheTimer(c)=RUNNING⇒(∀c2⦂PROCESS,a⦂ADDRESS·c2∈CONTROLLER∧¬c2∈dom(CPUReadRequests)∧¬c2∈dom(CPUWriteRequests)⇒(CONTROLLER × ADDRESS) ∖ (CPUReadRequests{c2 ↦ a})⊂(CONTROLLER × ADDRESS) ∖ CPUReadRequests)", //
						"∀c⦂PROCESS·cacheTimer(c)=RUNNING⇒(∀c2⦂PROCESS,a⦂ADDRESS,l⦂DATA·l∈LINE∧c2∈CONTROLLER∧¬c2∈dom(CPUWriteRequests)∧¬c2∈dom(CPUReadRequests)⇒(CONTROLLER × (ADDRESS × LINE)) ∖ (CPUWriteRequests{c2 ↦ (a ↦ l)})⊂(CONTROLLER × (ADDRESS × LINE)) ∖ CPUWriteRequests)", //
						"memoryTimer=RUNNING⇒(∀c⦂PROCESS,a⦂ADDRESS·c∈CONTROLLER∧¬c∈dom(CPUReadRequests)∧¬c∈dom(CPUWriteRequests)⇒(CONTROLLER × ADDRESS) ∖ (CPUReadRequests{c ↦ a})⊂(CONTROLLER × ADDRESS) ∖ CPUReadRequests)", //
						"memoryTimer=RUNNING⇒(∀c⦂PROCESS,a⦂ADDRESS,l⦂DATA·l∈LINE∧c∈CONTROLLER∧¬c∈dom(CPUWriteRequests)∧¬c∈dom(CPUReadRequests)⇒(CONTROLLER × (ADDRESS × LINE)) ∖ (CPUWriteRequests{c ↦ (a ↦ l)})⊂(CONTROLLER × (ADDRESS × LINE)) ∖ CPUWriteRequests)", //
						"∀a⦂ADDRESS,c⦂PROCESS,l⦂DATA·l∈LINE∧c∈CONTROLLER∧c∈dom(CPUReadRequests)∧CPUReadRequests(c)=a∧¬flag(c ↦ a)=I∧l=(caches(c))(a)∧(¬lineRequested=(∅ ⦂ ℙ(ADDRESS×PROCESS))⇒a ↦ c∈lineRequested)∧ownershipRequested=(∅ ⦂ ℙ(ADDRESS×PROCESS))⇒CPUReadRequests ∖ {c ↦ a}⊂CPUReadRequests", //
						"∀c⦂PROCESS,a⦂ADDRESS,l⦂DATA·l∈LINE∧c∈CONTROLLER∧c∈dom(CPUWriteRequests)∧CPUWriteRequests(c)=a ↦ l∧(ownershipRequested=(∅ ⦂ ℙ(ADDRESS×PROCESS))⇒flag(c ↦ a)∈{M,E})∧(a ↦ c∈ownershipRequested⇒cacheTimer(c)=TIMEOUT)∧(¬ownershipRequested=(∅ ⦂ ℙ(ADDRESS×PROCESS))⇒a ↦ c∈ownershipRequested)∧lineRequested=(∅ ⦂ ℙ(ADDRESS×PROCESS))⇒CPUWriteRequests ∖ {c ↦ (a ↦ l)}⊂CPUWriteRequests", //
						"¬lineRequested=(∅ ⦂ ℙ(ADDRESS×PROCESS))⇒(∃l⦂DATA,a⦂ADDRESS,c⦂PROCESS·l∈LINE∧c∈CONTROLLER∧c∈dom(CPUReadRequests)∧CPUReadRequests(c)=a∧¬flag(c ↦ a)=I∧l=(caches(c))(a)∧(¬lineRequested=(∅ ⦂ ℙ(ADDRESS×PROCESS))⇒a ↦ c∈lineRequested)∧ownershipRequested=(∅ ⦂ ℙ(ADDRESS×PROCESS)))∨(∃a⦂ADDRESS,p⦂PROCESS·memoryTimer=TIMEOUT∧a ↦ p∈lineRequested)∨(∃a⦂ADDRESS,psource⦂PROCESS,pdest⦂PROCESS·flag(psource ↦ a)∈{E,S}∧flag(pdest ↦ a)=I∧a ↦ pdest∈lineRequested)∨(∃a⦂ADDRESS,psource⦂PROCESS,pdest⦂PROCESS·flag(psource ↦ a)∈{M,O}∧flag(pdest ↦ a)=I∧a ↦ pdest∈lineRequested)∨(∃a⦂ADDRESS,p⦂PROCESS·a ↦ p∈lineRequested∧flag[PROCESS × {a}]={I}∧memoryTimer=RUNNING)", //
						"¬lineRequested=(∅ ⦂ ℙ(ADDRESS×PROCESS))⇒(∀l⦂DATA,a⦂ADDRESS,c⦂PROCESS·l∈LINE∧c∈CONTROLLER∧c∈dom(CPUReadRequests)⇒c∈dom(CPUReadRequests)∧CPUReadRequests∈PROCESS ⇸ ADDRESS∧(CPUReadRequests(c)=a⇒c ↦ a∈dom(flag)∧flag∈PROCESS × ADDRESS ⇸ FLAGS∧(¬flag(c ↦ a)=I⇒c∈dom(caches)∧caches∈PROCESS ⇸ ℙ(ADDRESS × DATA)∧a∈dom(caches(c))∧caches(c)∈ADDRESS ⇸ DATA)))∧(∀l⦂DATA,a⦂ADDRESS,c⦂PROCESS,psource⦂PROCESS,pdest⦂PROCESS,p⦂PROCESS·(l∈LINE∧c∈CONTROLLER∧c∈dom(CPUReadRequests)∧CPUReadRequests(c)=a∧¬flag(c ↦ a)=I∧l=(caches(c))(a)∧(¬lineRequested=(∅ ⦂ ℙ(ADDRESS×PROCESS))⇒a ↦ c∈lineRequested)∧ownershipRequested=(∅ ⦂ ℙ(ADDRESS×PROCESS)))∨(memoryTimer=TIMEOUT∧a ↦ p∈lineRequested)∨(psource ↦ a∈dom(flag)∧flag∈PROCESS × ADDRESS ⇸ FLAGS∧(flag(psource ↦ a)∈{E,S}⇒pdest ↦ a∈dom(flag)∧flag∈PROCESS × ADDRESS ⇸ FLAGS)∧((flag(psource ↦ a)∈{E,S}∧flag(pdest ↦ a)=I∧a ↦ pdest∈lineRequested)∨(psource ↦ a∈dom(flag)∧flag∈PROCESS × ADDRESS ⇸ FLAGS∧(flag(psource ↦ a)∈{M,O}⇒pdest ↦ a∈dom(flag)∧flag∈PROCESS × ADDRESS ⇸ FLAGS)))))", //
						"¬ownershipRequested=(∅ ⦂ ℙ(ADDRESS×PROCESS))⇒(∃l⦂DATA,a⦂ADDRESS,c⦂PROCESS·l∈LINE∧c∈CONTROLLER∧c∈dom(CPUWriteRequests)∧CPUWriteRequests(c)=a ↦ l∧ownershipRequested=(∅ ⦂ ℙ(ADDRESS×PROCESS))⇒flag(c ↦ a)∈{M,E}∧(a ↦ c∈ownershipRequested⇒cacheTimer(c)=TIMEOUT)∧(¬ownershipRequested=(∅ ⦂ ℙ(ADDRESS×PROCESS))⇒a ↦ c∈ownershipRequested)∧lineRequested=(∅ ⦂ ℙ(ADDRESS×PROCESS)))∨(∃a⦂ADDRESS,p⦂PROCESS·flag(p ↦ a)∈{M,O}∧(¬ownershipRequested=(∅ ⦂ ℙ(ADDRESS×PROCESS))⇒a∈dom(ownershipRequested ⩥ {p}))∧lineRequested=(∅ ⦂ ℙ(ADDRESS×PROCESS)))∨(∃a⦂ADDRESS,p⦂PROCESS·flag(p ↦ a)∈{S,E}∧lineRequested=(∅ ⦂ ℙ(ADDRESS×PROCESS))∧(¬ownershipRequested=(∅ ⦂ ℙ(ADDRESS×PROCESS))⇒a∈dom(ownershipRequested ⩥ {p})))∨(∃a⦂ADDRESS,p⦂PROCESS·a ↦ p∈ownershipRequested∧(∀p2⦂PROCESS·¬p2=p⇒flag(p2 ↦ a)=I)∧cacheTimer(p)=RUNNING)", //
						"¬ownershipRequested=(∅ ⦂ ℙ(ADDRESS×PROCESS))⇒(∀l⦂DATA,c⦂PROCESS·l∈LINE∧c∈CONTROLLER∧c∈dom(CPUWriteRequests)⇒c∈dom(CPUWriteRequests)∧CPUWriteRequests∈PROCESS ⇸ ADDRESS × DATA)∧(∀l⦂DATA,a⦂ADDRESS,c⦂PROCESS·l∈LINE∧c∈CONTROLLER∧c∈dom(CPUWriteRequests)∧CPUWriteRequests(c)=a ↦ l∧ownershipRequested=(∅ ⦂ ℙ(ADDRESS×PROCESS))⇒c ↦ a∈dom(flag)∧flag∈PROCESS × ADDRESS ⇸ FLAGS∧(flag(c ↦ a)∈{M,E}∧a ↦ c∈ownershipRequested⇒c∈dom(cacheTimer)∧cacheTimer∈PROCESS ⇸ TIMER_STATE))∧(∀l⦂DATA,a⦂ADDRESS,c⦂PROCESS,p⦂PROCESS·(l∈LINE∧c∈CONTROLLER∧c∈dom(CPUWriteRequests)∧CPUWriteRequests(c)=a ↦ l∧ownershipRequested=(∅ ⦂ ℙ(ADDRESS×PROCESS))⇒flag(c ↦ a)∈{M,E}∧(a ↦ c∈ownershipRequested⇒cacheTimer(c)=TIMEOUT)∧(¬ownershipRequested=(∅ ⦂ ℙ(ADDRESS×PROCESS))⇒a ↦ c∈ownershipRequested)∧lineRequested=(∅ ⦂ ℙ(ADDRESS×PROCESS)))∨(p ↦ a∈dom(flag)∧flag∈PROCESS × ADDRESS ⇸ FLAGS∧((flag(p ↦ a)∈{M,O}∧(¬ownershipRequested=(∅ ⦂ ℙ(ADDRESS×PROCESS))⇒a∈dom(ownershipRequested ⩥ {p}))∧lineRequested=(∅ ⦂ ℙ(ADDRESS×PROCESS)))∨(p ↦ a∈dom(flag)∧flag∈PROCESS × ADDRESS ⇸ FLAGS∧((flag(p ↦ a)∈{S,E}∧lineRequested=(∅ ⦂ ℙ(ADDRESS×PROCESS))∧(¬ownershipRequested=(∅ ⦂ ℙ(ADDRESS×PROCESS))⇒a∈dom(ownershipRequested ⩥ {p})))∨(a ↦ p∈ownershipRequested⇒(∀p2⦂PROCESS·¬p2=p⇒p2 ↦ a∈dom(flag)∧flag∈PROCESS × ADDRESS ⇸ FLAGS)∧((∀p2⦂PROCESS·¬p2=p⇒flag(p2 ↦ a)=I)⇒p∈dom(cacheTimer)∧cacheTimer∈PROCESS ⇸ TIMER_STATE)))))))", //
						"(¬CPUReadRequests=(∅ ⦂ ℙ(PROCESS×ADDRESS))∨¬CPUWriteRequests=(∅ ⦂ ℙ(PROCESS×(ADDRESS×DATA))))∧lineRequested=(∅ ⦂ ℙ(ADDRESS×PROCESS))⇒(∃l⦂DATA,c⦂PROCESS,a⦂ADDRESS·l∈LINE∧c∈CONTROLLER∧c∈dom(CPUWriteRequests)∧CPUWriteRequests(c)=a ↦ l∧ownershipRequested=(∅ ⦂ ℙ(ADDRESS×PROCESS))⇒flag(c ↦ a)∈{M,E}∧(a ↦ c∈ownershipRequested⇒cacheTimer(c)=TIMEOUT)∧(¬ownershipRequested=(∅ ⦂ ℙ(ADDRESS×PROCESS))⇒a ↦ c∈ownershipRequested)∧lineRequested=(∅ ⦂ ℙ(ADDRESS×PROCESS)))∨(∃l⦂DATA,c⦂PROCESS,a⦂ADDRESS·l∈LINE∧c∈CONTROLLER∧c∈dom(CPUReadRequests)∧CPUReadRequests(c)=a∧¬flag(c ↦ a)=I∧l=(caches(c))(a)∧(¬lineRequested=(∅ ⦂ ℙ(ADDRESS×PROCESS))⇒a ↦ c∈lineRequested)∧ownershipRequested=(∅ ⦂ ℙ(ADDRESS×PROCESS)))∨(∃p⦂PROCESS,a⦂ADDRESS·flag(p ↦ a)=I∧lineRequested=(∅ ⦂ ℙ(ADDRESS×PROCESS))∧ownershipRequested=(∅ ⦂ ℙ(ADDRESS×PROCESS))∧p∈dom(CPUReadRequests)∧CPUReadRequests(p)=a)∨(∃p⦂PROCESS,a⦂ADDRESS,d⦂DATA·flag(p ↦ a)∈{I,S,O}∧ownershipRequested=(∅ ⦂ ℙ(ADDRESS×PROCESS))∧lineRequested=(∅ ⦂ ℙ(ADDRESS×PROCESS))∧p∈dom(CPUWriteRequests)∧CPUWriteRequests(p)=a ↦ d)", //
						"(¬CPUReadRequests=(∅ ⦂ ℙ(PROCESS×ADDRESS))∨¬CPUWriteRequests=(∅ ⦂ ℙ(PROCESS×(ADDRESS×DATA))))∧lineRequested=(∅ ⦂ ℙ(ADDRESS×PROCESS))⇒(∀l⦂DATA,c⦂PROCESS·l∈LINE∧c∈CONTROLLER∧c∈dom(CPUWriteRequests)⇒c∈dom(CPUWriteRequests)∧CPUWriteRequests∈PROCESS ⇸ ADDRESS × DATA)∧(∀l⦂DATA,c⦂PROCESS,a⦂ADDRESS·l∈LINE∧c∈CONTROLLER∧c∈dom(CPUWriteRequests)∧CPUWriteRequests(c)=a ↦ l∧ownershipRequested=(∅ ⦂ ℙ(ADDRESS×PROCESS))⇒c ↦ a∈dom(flag)∧flag∈PROCESS × ADDRESS ⇸ FLAGS∧(flag(c ↦ a)∈{M,E}∧a ↦ c∈ownershipRequested⇒c∈dom(cacheTimer)∧cacheTimer∈PROCESS ⇸ TIMER_STATE))∧(∀l⦂DATA,c⦂PROCESS,p⦂PROCESS,a⦂ADDRESS·(l∈LINE∧c∈CONTROLLER∧c∈dom(CPUWriteRequests)∧CPUWriteRequests(c)=a ↦ l∧ownershipRequested=(∅ ⦂ ℙ(ADDRESS×PROCESS))⇒flag(c ↦ a)∈{M,E}∧(a ↦ c∈ownershipRequested⇒cacheTimer(c)=TIMEOUT)∧(¬ownershipRequested=(∅ ⦂ ℙ(ADDRESS×PROCESS))⇒a ↦ c∈ownershipRequested)∧lineRequested=(∅ ⦂ ℙ(ADDRESS×PROCESS)))∨((l∈LINE∧c∈CONTROLLER∧c∈dom(CPUReadRequests)⇒c∈dom(CPUReadRequests)∧CPUReadRequests∈PROCESS ⇸ ADDRESS∧(CPUReadRequests(c)=a⇒c ↦ a∈dom(flag)∧flag∈PROCESS × ADDRESS ⇸ FLAGS∧(¬flag(c ↦ a)=I⇒c∈dom(caches)∧caches∈PROCESS ⇸ ℙ(ADDRESS × DATA)∧a∈dom(caches(c))∧caches(c)∈ADDRESS ⇸ DATA)))∧((l∈LINE∧c∈CONTROLLER∧c∈dom(CPUReadRequests)∧CPUReadRequests(c)=a∧¬flag(c ↦ a)=I∧l=(caches(c))(a)∧(¬lineRequested=(∅ ⦂ ℙ(ADDRESS×PROCESS))⇒a ↦ c∈lineRequested)∧ownershipRequested=(∅ ⦂ ℙ(ADDRESS×PROCESS)))∨(p ↦ a∈dom(flag)∧flag∈PROCESS × ADDRESS ⇸ FLAGS∧(flag(p ↦ a)=I∧lineRequested=(∅ ⦂ ℙ(ADDRESS×PROCESS))∧ownershipRequested=(∅ ⦂ ℙ(ADDRESS×PROCESS))∧p∈dom(CPUReadRequests)⇒p∈dom(CPUReadRequests)∧CPUReadRequests∈PROCESS ⇸ ADDRESS)∧((flag(p ↦ a)=I∧lineRequested=(∅ ⦂ ℙ(ADDRESS×PROCESS))∧ownershipRequested=(∅ ⦂ ℙ(ADDRESS×PROCESS))∧p∈dom(CPUReadRequests)∧CPUReadRequests(p)=a)∨(p ↦ a∈dom(flag)∧flag∈PROCESS × ADDRESS ⇸ FLAGS∧(flag(p ↦ a)∈{I,S,O}∧ownershipRequested=(∅ ⦂ ℙ(ADDRESS×PROCESS))∧lineRequested=(∅ ⦂ ℙ(ADDRESS×PROCESS))∧p∈dom(CPUWriteRequests)⇒p∈dom(CPUWriteRequests)∧CPUWriteRequests∈PROCESS ⇸ ADDRESS × DATA)))))))" //
				),
				"CPUReadRequests ∖ {c ↦ CPUReadRequests(c)}∈CONTROLLER ⇸ ADDRESS", //
				true, 100);
	}

	@Test
	public void lemma3() throws Exception {
		doTest(mTypeEnvironment(
				"lineRequested=ADDRESS↔PROCESS; E=FLAGS;" //
				+" exclusive=ADDRESS↔PROCESS; M=FLAGS; O=FLAGS;" //
				+" FLAGS=ℙ(FLAGS); I=FLAGS; caches=PROCESS↔ℙ(ADDRESS×DATA);" //
				+" invalid=ADDRESS↔PROCESS; RUNNING=TIMER_STATE; S=FLAGS;" //
				+" mem=ADDRESS↔DATA; v_mem=ADDRESS↔DATA; LINE=ℙ(DATA);" //
				+" FLAG=ℙ(FLAGS); CPUReadRequests=PROCESS↔ADDRESS;" //
				+" shared=ADDRESS↔PROCESS;" //
				+" ownershipRequested=ADDRESS↔PROCESS;" //
				+" CPUWriteRequests=PROCESS↔(ADDRESS×DATA);" //
				+" ADDRESS=ℙ(ADDRESS); a=ADDRESS;" //
				+" cacheTimer=PROCESS↔TIMER_STATE; OFF=TIMER_STATE;" //
				+" TIMER_STATE=ℙ(TIMER_STATE); owned=ADDRESS↔PROCESS;" //
				+" modified=ADDRESS↔PROCESS; CONTROLLER=ℙ(PROCESS);" //
				+" memoryTimer=TIMER_STATE; flag=PROCESS↔ADDRESS×FLAGS;" //
				+" p=PROCESS; PROCESS=ℙ(PROCESS); DATA=ℙ(DATA); TIMEOUT=TIMER_STATE" //
				, ff),
				mSet(
						"memoryTimer=TIMEOUT", //
						"a ↦ p∈lineRequested", //
						"¬lineRequested=(∅ ⦂ ℙ(ADDRESS×PROCESS))", //
						"∃a⦂ADDRESS,p⦂PROCESS·lineRequested={a ↦ p}", //
						"ownershipRequested=(∅ ⦂ ℙ(ADDRESS×PROCESS))", //
						"∀l⦂DATA,a⦂ADDRESS,c⦂PROCESS·l∈LINE∧c∈CONTROLLER∧c∈dom(CPUReadRequests)⇒c∈dom(CPUReadRequests)∧CPUReadRequests∈PROCESS ⇸ ADDRESS∧(CPUReadRequests(c)=a⇒c ↦ a∈dom(flag)∧flag∈PROCESS × ADDRESS ⇸ FLAGS∧(¬flag(c ↦ a)=I⇒c∈dom(caches)∧caches∈PROCESS ⇸ ℙ(ADDRESS × DATA)∧a∈dom(caches(c))∧caches(c)∈ADDRESS ⇸ DATA))", //
						"(∃l⦂DATA,a⦂ADDRESS,c⦂PROCESS·l∈LINE∧c∈CONTROLLER∧c∈dom(CPUReadRequests)∧CPUReadRequests(c)=a∧¬flag(c ↦ a)=I∧l=(caches(c))(a)∧(¬lineRequested=(∅ ⦂ ℙ(ADDRESS×PROCESS))⇒a ↦ c∈lineRequested))∨(∃a⦂ADDRESS,p⦂PROCESS·a ↦ p∈lineRequested)∨(∃a⦂ADDRESS,psource⦂PROCESS,pdest⦂PROCESS·flag(psource ↦ a)∈{E,S}∧flag(pdest ↦ a)=I∧a ↦ pdest∈lineRequested)∨(∃a⦂ADDRESS,psource⦂PROCESS,pdest⦂PROCESS·flag(psource ↦ a)∈{M,O}∧flag(pdest ↦ a)=I∧a ↦ pdest∈lineRequested)∨(∃a⦂ADDRESS,p⦂PROCESS·a ↦ p∈lineRequested∧flag[PROCESS × {a}]={I}∧TIMEOUT=RUNNING)", //
						"∀l⦂DATA,a⦂ADDRESS,c⦂PROCESS,psource⦂PROCESS,pdest⦂PROCESS,p⦂PROCESS·(l∈LINE∧c∈CONTROLLER∧c∈dom(CPUReadRequests)∧CPUReadRequests(c)=a∧¬flag(c ↦ a)=I∧l=(caches(c))(a)∧(¬lineRequested=(∅ ⦂ ℙ(ADDRESS×PROCESS))⇒a ↦ c∈lineRequested))∨a ↦ p∈lineRequested∨(psource ↦ a∈dom(flag)∧flag∈PROCESS × ADDRESS ⇸ FLAGS∧(flag(psource ↦ a)∈{E,S}⇒pdest ↦ a∈dom(flag)∧flag∈PROCESS × ADDRESS ⇸ FLAGS)∧((flag(psource ↦ a)∈{E,S}∧flag(pdest ↦ a)=I∧a ↦ pdest∈lineRequested)∨(psource ↦ a∈dom(flag)∧flag∈PROCESS × ADDRESS ⇸ FLAGS∧(flag(psource ↦ a)∈{M,O}⇒pdest ↦ a∈dom(flag)∧flag∈PROCESS × ADDRESS ⇸ FLAGS))))", //
						"(¬CPUReadRequests=(∅ ⦂ ℙ(PROCESS×ADDRESS))∨¬CPUWriteRequests=(∅ ⦂ ℙ(PROCESS×(ADDRESS×DATA))))∧lineRequested=(∅ ⦂ ℙ(ADDRESS×PROCESS))⇒(∃l⦂DATA,c⦂PROCESS,a⦂ADDRESS·l∈LINE∧c∈CONTROLLER∧c∈dom(CPUWriteRequests)∧CPUWriteRequests(c)=a ↦ l⇒flag(c ↦ a)∈{M,E}∧lineRequested=(∅ ⦂ ℙ(ADDRESS×PROCESS)))∨(∃l⦂DATA,c⦂PROCESS,a⦂ADDRESS·l∈LINE∧c∈CONTROLLER∧c∈dom(CPUReadRequests)∧CPUReadRequests(c)=a∧¬flag(c ↦ a)=I∧l=(caches(c))(a)∧(¬lineRequested=(∅ ⦂ ℙ(ADDRESS×PROCESS))⇒a ↦ c∈lineRequested))∨(∃p⦂PROCESS,a⦂ADDRESS·flag(p ↦ a)=I∧lineRequested=(∅ ⦂ ℙ(ADDRESS×PROCESS))∧p∈dom(CPUReadRequests)∧CPUReadRequests(p)=a)∨(∃p⦂PROCESS,a⦂ADDRESS,d⦂DATA·flag(p ↦ a)∈{I,S,O}∧lineRequested=(∅ ⦂ ℙ(ADDRESS×PROCESS))∧p∈dom(CPUWriteRequests)∧CPUWriteRequests(p)=a ↦ d)", //
						"(¬CPUReadRequests=(∅ ⦂ ℙ(PROCESS×ADDRESS))∨¬CPUWriteRequests=(∅ ⦂ ℙ(PROCESS×(ADDRESS×DATA))))∧lineRequested=(∅ ⦂ ℙ(ADDRESS×PROCESS))⇒(∀l⦂DATA,c⦂PROCESS·l∈LINE∧c∈CONTROLLER∧c∈dom(CPUWriteRequests)⇒c∈dom(CPUWriteRequests)∧CPUWriteRequests∈PROCESS ⇸ ADDRESS × DATA)∧(∀l⦂DATA,c⦂PROCESS,a⦂ADDRESS·l∈LINE∧c∈CONTROLLER∧c∈dom(CPUWriteRequests)∧CPUWriteRequests(c)=a ↦ l⇒c ↦ a∈dom(flag)∧flag∈PROCESS × ADDRESS ⇸ FLAGS)∧(∀l⦂DATA,c⦂PROCESS,p⦂PROCESS,a⦂ADDRESS·(l∈LINE∧c∈CONTROLLER∧c∈dom(CPUWriteRequests)∧CPUWriteRequests(c)=a ↦ l⇒flag(c ↦ a)∈{M,E}∧lineRequested=(∅ ⦂ ℙ(ADDRESS×PROCESS)))∨((l∈LINE∧c∈CONTROLLER∧c∈dom(CPUReadRequests)⇒c∈dom(CPUReadRequests)∧CPUReadRequests∈PROCESS ⇸ ADDRESS∧(CPUReadRequests(c)=a⇒c ↦ a∈dom(flag)∧flag∈PROCESS × ADDRESS ⇸ FLAGS∧(¬flag(c ↦ a)=I⇒c∈dom(caches)∧caches∈PROCESS ⇸ ℙ(ADDRESS × DATA)∧a∈dom(caches(c))∧caches(c)∈ADDRESS ⇸ DATA)))∧((l∈LINE∧c∈CONTROLLER∧c∈dom(CPUReadRequests)∧CPUReadRequests(c)=a∧¬flag(c ↦ a)=I∧l=(caches(c))(a)∧(¬lineRequested=(∅ ⦂ ℙ(ADDRESS×PROCESS))⇒a ↦ c∈lineRequested))∨(p ↦ a∈dom(flag)∧flag∈PROCESS × ADDRESS ⇸ FLAGS∧(flag(p ↦ a)=I∧lineRequested=(∅ ⦂ ℙ(ADDRESS×PROCESS))∧p∈dom(CPUReadRequests)⇒p∈dom(CPUReadRequests)∧CPUReadRequests∈PROCESS ⇸ ADDRESS)∧((flag(p ↦ a)=I∧lineRequested=(∅ ⦂ ℙ(ADDRESS×PROCESS))∧p∈dom(CPUReadRequests)∧CPUReadRequests(p)=a)∨(p ↦ a∈dom(flag)∧flag∈PROCESS × ADDRESS ⇸ FLAGS∧(flag(p ↦ a)∈{I,S,O}∧lineRequested=(∅ ⦂ ℙ(ADDRESS×PROCESS))∧p∈dom(CPUWriteRequests)⇒p∈dom(CPUWriteRequests)∧CPUWriteRequests∈PROCESS ⇸ ADDRESS × DATA)))))))", //
						"finite(ADDRESS)", //
						"finite(PROCESS)", //
						"FLAGS={I,M,E,S,O}", //
						"TIMER_STATE={OFF,RUNNING,TIMEOUT}", //
						"LINE=DATA", //
						"CONTROLLER=PROCESS", //
						"FLAG=FLAGS", //
						"v_mem∈ADDRESS → DATA", //
						"caches∈PROCESS → (ADDRESS ⇸ DATA)", //
						"∀a⦂ADDRESS,p⦂PROCESS·a∈dom(caches(p))⇒(caches(p))(a)=v_mem(a)", //
						"mem∈ADDRESS → DATA", //
						"∀a⦂ADDRESS·v_mem(a)=mem(a)∨(∃p⦂PROCESS·a∈dom(caches(p)))", //
						"v_mem=memunion(caches[PROCESS])", //
						"modified∈ADDRESS ⇸ PROCESS", //
						"exclusive∈ADDRESS ⇸ PROCESS", //
						"owned∈ADDRESS ⇸ PROCESS", //
						"invalid∪modified∪exclusive∪shared∪owned=ADDRESS × PROCESS", //
						"ownershipRequested∈ADDRESS ⇸ PROCESS", //
						"ownershipRequested⊆invalid∪shared∪owned", //
						"flag∈PROCESS × ADDRESS → FLAGS", //
						"∀a⦂ADDRESS,p⦂PROCESS·flag(p ↦ a)=I⇔a ↦ p∈invalid", //
						"∀a⦂ADDRESS,p⦂PROCESS·flag(p ↦ a)=M⇔a ↦ p∈modified", //
						"∀a⦂ADDRESS,p⦂PROCESS·flag(p ↦ a)=E⇔a ↦ p∈exclusive", //
						"∀a⦂ADDRESS,p⦂PROCESS·flag(p ↦ a)=S⇔a ↦ p∈shared", //
						"∀a⦂ADDRESS,p⦂PROCESS·flag(p ↦ a)=O⇔a ↦ p∈owned", //
						"cacheTimer∈PROCESS → TIMER_STATE", //
						"∀a⦂ADDRESS·memoryTimer=TIMEOUT∧a∈dom(lineRequested)⇒flag[PROCESS × {a}]={I}", //
						"∀p⦂PROCESS·cacheTimer(p)=RUNNING⇒p∈ran(ownershipRequested)", //
						"memoryTimer=RUNNING⇒(∃a⦂ADDRESS,c⦂PROCESS·a ↦ c∈lineRequested∧flag(c ↦ a)=I)", //
						"memoryTimer=RUNNING⇒(∀a⦂ADDRESS,c⦂PROCESS·a ↦ c∈lineRequested⇒c ↦ a∈dom(flag)∧flag∈PROCESS × ADDRESS ⇸ FLAGS)", //
						"memoryTimer=OFF⇒(∀a⦂ADDRESS,c⦂PROCESS·a ↦ c∈lineRequested⇒c ↦ a∈dom(flag)∧flag∈PROCESS × ADDRESS ⇸ FLAGS)", //
						"CPUReadRequests∈CONTROLLER ⇸ ADDRESS", //
						"CPUWriteRequests∈CONTROLLER ⇸ ADDRESS × LINE", //
						"lineRequested⊆CPUReadRequests∼", //
						"∀a⦂ADDRESS,c⦂PROCESS·a ↦ c∈ownershipRequested⇒(∃l⦂DATA·c ↦ (a ↦ l)∈CPUWriteRequests)", //
						"∀c⦂PROCESS·cacheTimer(c)=RUNNING⇒¬ownershipRequested=(∅ ⦂ ℙ(ADDRESS×PROCESS))", //
						"memoryTimer=RUNNING⇒¬lineRequested=(∅ ⦂ ℙ(ADDRESS×PROCESS))", //
						"∀a⦂ADDRESS,p⦂PROCESS,d⦂DATA·flag(p ↦ a)∈{I,S,O}∧ownershipRequested=(∅ ⦂ ℙ(ADDRESS×PROCESS))∧lineRequested=(∅ ⦂ ℙ(ADDRESS×PROCESS))∧p∈dom(CPUWriteRequests)∧CPUWriteRequests(p)=a ↦ d⇒{a2⦂ADDRESS,c⦂PROCESS·∃l⦂DATA·c ↦ (a2 ↦ l)∈CPUWriteRequests ∣ a2 ↦ c} ∖ {a ↦ p}⊂{a3⦂ADDRESS,c2⦂PROCESS·∃l2⦂DATA·c2 ↦ (a3 ↦ l2)∈CPUWriteRequests ∣ a3 ↦ c2} ∖ ownershipRequested", //
						"p ↦ a∈dom(flag)", //
						"flag∈PROCESS × ADDRESS ⇸ FLAGS", //
						"¬I=M", //
						"¬I=E", //
						"¬I=S", //
						"¬I=O", //
						"¬M=E", //
						"¬M=S", //
						"¬M=O", //
						"¬E=S", //
						"¬E=O", //
						"¬S=O", //
						"¬OFF=RUNNING", //
						"¬OFF=TIMEOUT", //
						"¬RUNNING=TIMEOUT", //
						"∀p⦂PROCESS,a⦂ADDRESS·a ↦ p∈lineRequested⇒(a∈dom(caches(p))∧(∃d⦂DATA·d=(caches(p))(a)))∨(¬a∈dom(caches(p))∧a ↦ p∈lineRequested)", //
						"∀a⦂ADDRESS·¬v_mem(a)=mem(a)⇒(∃p⦂PROCESS·a∈dom(caches(p)))", //
						"∀p⦂PROCESS,a⦂ADDRESS·a ↦ p∈lineRequested⇒(∃p2⦂PROCESS·a∈dom(caches(p2))∧¬mem(a)=(caches(p2))(a))∨(a∈dom(caches(p))∧(∃d⦂DATA·d=(caches(p))(a)))∨(¬a∈dom(caches(p))∧mem(a)=v_mem(a)∧a ↦ p∈lineRequested)", //
						"∀p⦂PROCESS,a⦂ADDRESS·lineRequested=(∅ ⦂ ℙ(ADDRESS×PROCESS))⇒(∀p2⦂PROCESS·¬p2=p⇒¬a∈dom(caches(p2)))∨(∃p3⦂PROCESS·¬p3=p∧a∈dom(caches(p3))∧¬mem(a)=(caches(p3))(a))∨(∃p4⦂PROCESS·¬p4=p∧a∈dom(caches(p4))∧(mem(a)=v_mem(a)∨(∃p5⦂PROCESS·¬p5=p4∧a∈dom(caches(p5))))∧¬a ↦ p4∈lineRequested)", //
						"∀a⦂ADDRESS,p⦂PROCESS·¬a∈dom(caches(p))⇒a ↦ p∈invalid", //
						"∀a⦂ADDRESS,p⦂PROCESS·a ↦ p∈invalid⇒¬a∈dom(caches(p))", //
						"¬ownershipRequested=(∅ ⦂ ℙ(ADDRESS×PROCESS))⇒(∃a⦂ADDRESS,p⦂PROCESS·ownershipRequested={a ↦ p})", //
						"∀a⦂ADDRESS,p⦂PROCESS·cacheTimer(p)=TIMEOUT∧a ↦ p∈ownershipRequested⇒(∀p2⦂PROCESS·¬p2=p⇒flag(p2 ↦ a)=I)", //
						"memoryTimer=OFF⇒(∀a⦂ADDRESS,c⦂PROCESS·a ↦ c∈lineRequested⇒¬flag(c ↦ a)=I)", //
						"∀p⦂PROCESS·cacheTimer(p)=OFF⇒(∀a⦂ADDRESS·¬a ↦ p∈ownershipRequested)", //
						"∀c⦂PROCESS·cacheTimer(c)=RUNNING⇒¬(∃a⦂ADDRESS,c2⦂PROCESS·(a ↦ c2∈ownershipRequested⇒cacheTimer(c2)=TIMEOUT)∧(¬ownershipRequested=(∅ ⦂ ℙ(ADDRESS×PROCESS))⇒a ↦ c2∈ownershipRequested))", //
						"∀c⦂PROCESS·cacheTimer(c)=RUNNING⇒(∃a⦂ADDRESS,p⦂PROCESS·flag(p ↦ a)∈{S,E}∧lineRequested=(∅ ⦂ ℙ(ADDRESS×PROCESS))∧(¬ownershipRequested=(∅ ⦂ ℙ(ADDRESS×PROCESS))⇒a∈dom(ownershipRequested ⩥ {p})))∨(∃a⦂ADDRESS,p⦂PROCESS·flag(p ↦ a)∈{M,O}∧(¬ownershipRequested=(∅ ⦂ ℙ(ADDRESS×PROCESS))⇒a∈dom(ownershipRequested ⩥ {p}))∧lineRequested=(∅ ⦂ ℙ(ADDRESS×PROCESS)))∨(∃a⦂ADDRESS·a ↦ c∈ownershipRequested∧(∀p2⦂PROCESS·¬p2=c⇒flag(p2 ↦ a)=I)∧cacheTimer(c)=RUNNING)", //
						"memoryTimer=RUNNING⇒¬(∃a⦂ADDRESS,c⦂PROCESS·¬flag(c ↦ a)=I∧(¬lineRequested=(∅ ⦂ ℙ(ADDRESS×PROCESS))⇒a ↦ c∈lineRequested))", //
						"memoryTimer=RUNNING⇒(∀a⦂ADDRESS,c⦂PROCESS·c ↦ a∈dom(flag))∧flag∈PROCESS × ADDRESS ⇸ FLAGS", //
						"memoryTimer=RUNNING⇒(∃psource⦂PROCESS,pdest⦂PROCESS,a⦂ADDRESS·flag(psource ↦ a)∈{E,S}∧flag(pdest ↦ a)=I∧a ↦ pdest∈lineRequested)∨(∃psource⦂PROCESS,pdest⦂PROCESS,a⦂ADDRESS·flag(psource ↦ a)∈{M,O}∧flag(pdest ↦ a)=I∧a ↦ pdest∈lineRequested)∨(∃p⦂PROCESS,a⦂ADDRESS·a ↦ p∈lineRequested∧flag[PROCESS × {a}]={I}∧memoryTimer=RUNNING)", //
						"memoryTimer=RUNNING⇒(∀psource⦂PROCESS,a⦂ADDRESS·psource ↦ a∈dom(flag))∧flag∈PROCESS × ADDRESS ⇸ FLAGS∧(∀psource⦂PROCESS,pdest⦂PROCESS,a⦂ADDRESS·flag(psource ↦ a)∈{E,S}⇒pdest ↦ a∈dom(flag)∧flag∈PROCESS × ADDRESS ⇸ FLAGS)∧(∀psource⦂PROCESS,pdest⦂PROCESS,a⦂ADDRESS·(flag(psource ↦ a)∈{E,S}∧flag(pdest ↦ a)=I∧a ↦ pdest∈lineRequested)∨(psource ↦ a∈dom(flag)∧flag∈PROCESS × ADDRESS ⇸ FLAGS∧(flag(psource ↦ a)∈{M,O}⇒pdest ↦ a∈dom(flag)∧flag∈PROCESS × ADDRESS ⇸ FLAGS)))", //
						"∀a⦂ADDRESS,p⦂PROCESS·flag(p ↦ a)=I∧lineRequested=(∅ ⦂ ℙ(ADDRESS×PROCESS))∧ownershipRequested=(∅ ⦂ ℙ(ADDRESS×PROCESS))∧p∈dom(CPUReadRequests)∧CPUReadRequests(p)=a⇒CPUReadRequests ∖ {p ↦ a}⊂CPUReadRequests ∖ lineRequested∼", //
						"∀c⦂PROCESS·cacheTimer(c)=RUNNING⇒(∀c2⦂PROCESS,a⦂ADDRESS·c2∈CONTROLLER∧¬c2∈dom(CPUReadRequests)∧¬c2∈dom(CPUWriteRequests)⇒(CONTROLLER × ADDRESS) ∖ (CPUReadRequests{c2 ↦ a})⊂(CONTROLLER × ADDRESS) ∖ CPUReadRequests)", //
						"∀c⦂PROCESS·cacheTimer(c)=RUNNING⇒(∀c2⦂PROCESS,a⦂ADDRESS,l⦂DATA·l∈LINE∧c2∈CONTROLLER∧¬c2∈dom(CPUWriteRequests)∧¬c2∈dom(CPUReadRequests)⇒(CONTROLLER × (ADDRESS × LINE)) ∖ (CPUWriteRequests{c2 ↦ (a ↦ l)})⊂(CONTROLLER × (ADDRESS × LINE)) ∖ CPUWriteRequests)", //
						"memoryTimer=RUNNING⇒(∀c⦂PROCESS,a⦂ADDRESS·c∈CONTROLLER∧¬c∈dom(CPUReadRequests)∧¬c∈dom(CPUWriteRequests)⇒(CONTROLLER × ADDRESS) ∖ (CPUReadRequests{c ↦ a})⊂(CONTROLLER × ADDRESS) ∖ CPUReadRequests)", //
						"memoryTimer=RUNNING⇒(∀c⦂PROCESS,a⦂ADDRESS,l⦂DATA·l∈LINE∧c∈CONTROLLER∧¬c∈dom(CPUWriteRequests)∧¬c∈dom(CPUReadRequests)⇒(CONTROLLER × (ADDRESS × LINE)) ∖ (CPUWriteRequests{c ↦ (a ↦ l)})⊂(CONTROLLER × (ADDRESS × LINE)) ∖ CPUWriteRequests)", //
						"∀a⦂ADDRESS,c⦂PROCESS,l⦂DATA·l∈LINE∧c∈CONTROLLER∧c∈dom(CPUReadRequests)∧CPUReadRequests(c)=a∧¬flag(c ↦ a)=I∧l=(caches(c))(a)∧(¬lineRequested=(∅ ⦂ ℙ(ADDRESS×PROCESS))⇒a ↦ c∈lineRequested)∧ownershipRequested=(∅ ⦂ ℙ(ADDRESS×PROCESS))⇒CPUReadRequests ∖ {c ↦ a}⊂CPUReadRequests", //
						"∀c⦂PROCESS,a⦂ADDRESS,l⦂DATA·l∈LINE∧c∈CONTROLLER∧c∈dom(CPUWriteRequests)∧CPUWriteRequests(c)=a ↦ l∧(ownershipRequested=(∅ ⦂ ℙ(ADDRESS×PROCESS))⇒flag(c ↦ a)∈{M,E})∧(a ↦ c∈ownershipRequested⇒cacheTimer(c)=TIMEOUT)∧(¬ownershipRequested=(∅ ⦂ ℙ(ADDRESS×PROCESS))⇒a ↦ c∈ownershipRequested)∧lineRequested=(∅ ⦂ ℙ(ADDRESS×PROCESS))⇒CPUWriteRequests ∖ {c ↦ (a ↦ l)}⊂CPUWriteRequests", //
						"¬ownershipRequested=(∅ ⦂ ℙ(ADDRESS×PROCESS))⇒(∃l⦂DATA,a⦂ADDRESS,c⦂PROCESS·l∈LINE∧c∈CONTROLLER∧c∈dom(CPUWriteRequests)∧CPUWriteRequests(c)=a ↦ l∧ownershipRequested=(∅ ⦂ ℙ(ADDRESS×PROCESS))⇒flag(c ↦ a)∈{M,E}∧(a ↦ c∈ownershipRequested⇒cacheTimer(c)=TIMEOUT)∧(¬ownershipRequested=(∅ ⦂ ℙ(ADDRESS×PROCESS))⇒a ↦ c∈ownershipRequested)∧lineRequested=(∅ ⦂ ℙ(ADDRESS×PROCESS)))∨(∃a⦂ADDRESS,p⦂PROCESS·flag(p ↦ a)∈{M,O}∧(¬ownershipRequested=(∅ ⦂ ℙ(ADDRESS×PROCESS))⇒a∈dom(ownershipRequested ⩥ {p}))∧lineRequested=(∅ ⦂ ℙ(ADDRESS×PROCESS)))∨(∃a⦂ADDRESS,p⦂PROCESS·flag(p ↦ a)∈{S,E}∧lineRequested=(∅ ⦂ ℙ(ADDRESS×PROCESS))∧(¬ownershipRequested=(∅ ⦂ ℙ(ADDRESS×PROCESS))⇒a∈dom(ownershipRequested ⩥ {p})))∨(∃a⦂ADDRESS,p⦂PROCESS·a ↦ p∈ownershipRequested∧(∀p2⦂PROCESS·¬p2=p⇒flag(p2 ↦ a)=I)∧cacheTimer(p)=RUNNING)", //
						"¬ownershipRequested=(∅ ⦂ ℙ(ADDRESS×PROCESS))⇒(∀l⦂DATA,c⦂PROCESS·l∈LINE∧c∈CONTROLLER∧c∈dom(CPUWriteRequests)⇒c∈dom(CPUWriteRequests)∧CPUWriteRequests∈PROCESS ⇸ ADDRESS × DATA)∧(∀l⦂DATA,a⦂ADDRESS,c⦂PROCESS·l∈LINE∧c∈CONTROLLER∧c∈dom(CPUWriteRequests)∧CPUWriteRequests(c)=a ↦ l∧ownershipRequested=(∅ ⦂ ℙ(ADDRESS×PROCESS))⇒c ↦ a∈dom(flag)∧flag∈PROCESS × ADDRESS ⇸ FLAGS∧(flag(c ↦ a)∈{M,E}∧a ↦ c∈ownershipRequested⇒c∈dom(cacheTimer)∧cacheTimer∈PROCESS ⇸ TIMER_STATE))∧(∀l⦂DATA,a⦂ADDRESS,c⦂PROCESS,p⦂PROCESS·(l∈LINE∧c∈CONTROLLER∧c∈dom(CPUWriteRequests)∧CPUWriteRequests(c)=a ↦ l∧ownershipRequested=(∅ ⦂ ℙ(ADDRESS×PROCESS))⇒flag(c ↦ a)∈{M,E}∧(a ↦ c∈ownershipRequested⇒cacheTimer(c)=TIMEOUT)∧(¬ownershipRequested=(∅ ⦂ ℙ(ADDRESS×PROCESS))⇒a ↦ c∈ownershipRequested)∧lineRequested=(∅ ⦂ ℙ(ADDRESS×PROCESS)))∨(p ↦ a∈dom(flag)∧flag∈PROCESS × ADDRESS ⇸ FLAGS∧((flag(p ↦ a)∈{M,O}∧(¬ownershipRequested=(∅ ⦂ ℙ(ADDRESS×PROCESS))⇒a∈dom(ownershipRequested ⩥ {p}))∧lineRequested=(∅ ⦂ ℙ(ADDRESS×PROCESS)))∨(p ↦ a∈dom(flag)∧flag∈PROCESS × ADDRESS ⇸ FLAGS∧((flag(p ↦ a)∈{S,E}∧lineRequested=(∅ ⦂ ℙ(ADDRESS×PROCESS))∧(¬ownershipRequested=(∅ ⦂ ℙ(ADDRESS×PROCESS))⇒a∈dom(ownershipRequested ⩥ {p})))∨(a ↦ p∈ownershipRequested⇒(∀p2⦂PROCESS·¬p2=p⇒p2 ↦ a∈dom(flag)∧flag∈PROCESS × ADDRESS ⇸ FLAGS)∧((∀p2⦂PROCESS·¬p2=p⇒flag(p2 ↦ a)=I)⇒p∈dom(cacheTimer)∧cacheTimer∈PROCESS ⇸ TIMER_STATE)))))))", //
						"(∃l⦂DATA,a⦂ADDRESS,c⦂PROCESS·l∈LINE∧c∈CONTROLLER∧c∈dom(CPUReadRequests)∧CPUReadRequests(c)=a∧¬flag(c ↦ a)=I∧l=(caches(c))(a)∧(¬lineRequested=(∅ ⦂ ℙ(ADDRESS×PROCESS))⇒a ↦ c∈lineRequested)∧ownershipRequested=(∅ ⦂ ℙ(ADDRESS×PROCESS)))∨(∃a⦂ADDRESS,p⦂PROCESS·memoryTimer=TIMEOUT∧a ↦ p∈lineRequested)∨(∃a⦂ADDRESS,psource⦂PROCESS,pdest⦂PROCESS·flag(psource ↦ a)∈{E,S}∧flag(pdest ↦ a)=I∧a ↦ pdest∈lineRequested)∨(∃a⦂ADDRESS,psource⦂PROCESS,pdest⦂PROCESS·flag(psource ↦ a)∈{M,O}∧flag(pdest ↦ a)=I∧a ↦ pdest∈lineRequested)∨(∃a⦂ADDRESS,p⦂PROCESS·a ↦ p∈lineRequested∧flag[PROCESS × {a}]={I}∧memoryTimer=RUNNING)", //
						"∀l⦂DATA,a⦂ADDRESS,c⦂PROCESS,psource⦂PROCESS,pdest⦂PROCESS,p⦂PROCESS·(l∈LINE∧c∈CONTROLLER∧c∈dom(CPUReadRequests)∧CPUReadRequests(c)=a∧¬flag(c ↦ a)=I∧l=(caches(c))(a)∧(¬lineRequested=(∅ ⦂ ℙ(ADDRESS×PROCESS))⇒a ↦ c∈lineRequested)∧ownershipRequested=(∅ ⦂ ℙ(ADDRESS×PROCESS)))∨(memoryTimer=TIMEOUT∧a ↦ p∈lineRequested)∨(psource ↦ a∈dom(flag)∧flag∈PROCESS × ADDRESS ⇸ FLAGS∧(flag(psource ↦ a)∈{E,S}⇒pdest ↦ a∈dom(flag)∧flag∈PROCESS × ADDRESS ⇸ FLAGS)∧((flag(psource ↦ a)∈{E,S}∧flag(pdest ↦ a)=I∧a ↦ pdest∈lineRequested)∨(psource ↦ a∈dom(flag)∧flag∈PROCESS × ADDRESS ⇸ FLAGS∧(flag(psource ↦ a)∈{M,O}⇒pdest ↦ a∈dom(flag)∧flag∈PROCESS × ADDRESS ⇸ FLAGS))))", //
						"(∃l⦂DATA,a⦂ADDRESS,c⦂PROCESS·l∈LINE∧c∈CONTROLLER∧c∈dom(CPUReadRequests)∧CPUReadRequests(c)=a∧¬flag(c ↦ a)=I∧l=(caches(c))(a)∧(¬lineRequested=(∅ ⦂ ℙ(ADDRESS×PROCESS))⇒a ↦ c∈lineRequested)∧ownershipRequested=(∅ ⦂ ℙ(ADDRESS×PROCESS)))∨(∃a⦂ADDRESS,p⦂PROCESS·a ↦ p∈lineRequested)∨(∃a⦂ADDRESS,psource⦂PROCESS,pdest⦂PROCESS·flag(psource ↦ a)∈{E,S}∧flag(pdest ↦ a)=I∧a ↦ pdest∈lineRequested)∨(∃a⦂ADDRESS,psource⦂PROCESS,pdest⦂PROCESS·flag(psource ↦ a)∈{M,O}∧flag(pdest ↦ a)=I∧a ↦ pdest∈lineRequested)∨(∃a⦂ADDRESS,p⦂PROCESS·a ↦ p∈lineRequested∧flag[PROCESS × {a}]={I}∧TIMEOUT=RUNNING)", //
						"∀l⦂DATA,a⦂ADDRESS,c⦂PROCESS,psource⦂PROCESS,pdest⦂PROCESS,p⦂PROCESS·(l∈LINE∧c∈CONTROLLER∧c∈dom(CPUReadRequests)∧CPUReadRequests(c)=a∧¬flag(c ↦ a)=I∧l=(caches(c))(a)∧(¬lineRequested=(∅ ⦂ ℙ(ADDRESS×PROCESS))⇒a ↦ c∈lineRequested)∧ownershipRequested=(∅ ⦂ ℙ(ADDRESS×PROCESS)))∨a ↦ p∈lineRequested∨(psource ↦ a∈dom(flag)∧flag∈PROCESS × ADDRESS ⇸ FLAGS∧(flag(psource ↦ a)∈{E,S}⇒pdest ↦ a∈dom(flag)∧flag∈PROCESS × ADDRESS ⇸ FLAGS)∧((flag(psource ↦ a)∈{E,S}∧flag(pdest ↦ a)=I∧a ↦ pdest∈lineRequested)∨(psource ↦ a∈dom(flag)∧flag∈PROCESS × ADDRESS ⇸ FLAGS∧(flag(psource ↦ a)∈{M,O}⇒pdest ↦ a∈dom(flag)∧flag∈PROCESS × ADDRESS ⇸ FLAGS))))", //
						"(¬CPUReadRequests=(∅ ⦂ ℙ(PROCESS×ADDRESS))∨¬CPUWriteRequests=(∅ ⦂ ℙ(PROCESS×(ADDRESS×DATA))))∧lineRequested=(∅ ⦂ ℙ(ADDRESS×PROCESS))⇒(∃l⦂DATA,c⦂PROCESS,a⦂ADDRESS·l∈LINE∧c∈CONTROLLER∧c∈dom(CPUWriteRequests)∧CPUWriteRequests(c)=a ↦ l∧ownershipRequested=(∅ ⦂ ℙ(ADDRESS×PROCESS))⇒flag(c ↦ a)∈{M,E}∧(a ↦ c∈ownershipRequested⇒cacheTimer(c)=TIMEOUT)∧(¬ownershipRequested=(∅ ⦂ ℙ(ADDRESS×PROCESS))⇒a ↦ c∈ownershipRequested)∧lineRequested=(∅ ⦂ ℙ(ADDRESS×PROCESS)))∨(∃l⦂DATA,c⦂PROCESS,a⦂ADDRESS·l∈LINE∧c∈CONTROLLER∧c∈dom(CPUReadRequests)∧CPUReadRequests(c)=a∧¬flag(c ↦ a)=I∧l=(caches(c))(a)∧(¬lineRequested=(∅ ⦂ ℙ(ADDRESS×PROCESS))⇒a ↦ c∈lineRequested)∧ownershipRequested=(∅ ⦂ ℙ(ADDRESS×PROCESS)))∨(∃p⦂PROCESS,a⦂ADDRESS·flag(p ↦ a)=I∧lineRequested=(∅ ⦂ ℙ(ADDRESS×PROCESS))∧ownershipRequested=(∅ ⦂ ℙ(ADDRESS×PROCESS))∧p∈dom(CPUReadRequests)∧CPUReadRequests(p)=a)∨(∃p⦂PROCESS,a⦂ADDRESS,d⦂DATA·flag(p ↦ a)∈{I,S,O}∧ownershipRequested=(∅ ⦂ ℙ(ADDRESS×PROCESS))∧lineRequested=(∅ ⦂ ℙ(ADDRESS×PROCESS))∧p∈dom(CPUWriteRequests)∧CPUWriteRequests(p)=a ↦ d)", //
						"(¬CPUReadRequests=(∅ ⦂ ℙ(PROCESS×ADDRESS))∨¬CPUWriteRequests=(∅ ⦂ ℙ(PROCESS×(ADDRESS×DATA))))∧lineRequested=(∅ ⦂ ℙ(ADDRESS×PROCESS))⇒(∀l⦂DATA,c⦂PROCESS·l∈LINE∧c∈CONTROLLER∧c∈dom(CPUWriteRequests)⇒c∈dom(CPUWriteRequests)∧CPUWriteRequests∈PROCESS ⇸ ADDRESS × DATA)∧(∀l⦂DATA,c⦂PROCESS,a⦂ADDRESS·l∈LINE∧c∈CONTROLLER∧c∈dom(CPUWriteRequests)∧CPUWriteRequests(c)=a ↦ l∧ownershipRequested=(∅ ⦂ ℙ(ADDRESS×PROCESS))⇒c ↦ a∈dom(flag)∧flag∈PROCESS × ADDRESS ⇸ FLAGS∧(flag(c ↦ a)∈{M,E}∧a ↦ c∈ownershipRequested⇒c∈dom(cacheTimer)∧cacheTimer∈PROCESS ⇸ TIMER_STATE))∧(∀l⦂DATA,c⦂PROCESS,p⦂PROCESS,a⦂ADDRESS·(l∈LINE∧c∈CONTROLLER∧c∈dom(CPUWriteRequests)∧CPUWriteRequests(c)=a ↦ l∧ownershipRequested=(∅ ⦂ ℙ(ADDRESS×PROCESS))⇒flag(c ↦ a)∈{M,E}∧(a ↦ c∈ownershipRequested⇒cacheTimer(c)=TIMEOUT)∧(¬ownershipRequested=(∅ ⦂ ℙ(ADDRESS×PROCESS))⇒a ↦ c∈ownershipRequested)∧lineRequested=(∅ ⦂ ℙ(ADDRESS×PROCESS)))∨((l∈LINE∧c∈CONTROLLER∧c∈dom(CPUReadRequests)⇒c∈dom(CPUReadRequests)∧CPUReadRequests∈PROCESS ⇸ ADDRESS∧(CPUReadRequests(c)=a⇒c ↦ a∈dom(flag)∧flag∈PROCESS × ADDRESS ⇸ FLAGS∧(¬flag(c ↦ a)=I⇒c∈dom(caches)∧caches∈PROCESS ⇸ ℙ(ADDRESS × DATA)∧a∈dom(caches(c))∧caches(c)∈ADDRESS ⇸ DATA)))∧((l∈LINE∧c∈CONTROLLER∧c∈dom(CPUReadRequests)∧CPUReadRequests(c)=a∧¬flag(c ↦ a)=I∧l=(caches(c))(a)∧(¬lineRequested=(∅ ⦂ ℙ(ADDRESS×PROCESS))⇒a ↦ c∈lineRequested)∧ownershipRequested=(∅ ⦂ ℙ(ADDRESS×PROCESS)))∨(p ↦ a∈dom(flag)∧flag∈PROCESS × ADDRESS ⇸ FLAGS∧(flag(p ↦ a)=I∧lineRequested=(∅ ⦂ ℙ(ADDRESS×PROCESS))∧ownershipRequested=(∅ ⦂ ℙ(ADDRESS×PROCESS))∧p∈dom(CPUReadRequests)⇒p∈dom(CPUReadRequests)∧CPUReadRequests∈PROCESS ⇸ ADDRESS)∧((flag(p ↦ a)=I∧lineRequested=(∅ ⦂ ℙ(ADDRESS×PROCESS))∧ownershipRequested=(∅ ⦂ ℙ(ADDRESS×PROCESS))∧p∈dom(CPUReadRequests)∧CPUReadRequests(p)=a)∨(p ↦ a∈dom(flag)∧flag∈PROCESS × ADDRESS ⇸ FLAGS∧(flag(p ↦ a)∈{I,S,O}∧ownershipRequested=(∅ ⦂ ℙ(ADDRESS×PROCESS))∧lineRequested=(∅ ⦂ ℙ(ADDRESS×PROCESS))∧p∈dom(CPUWriteRequests)⇒p∈dom(CPUWriteRequests)∧CPUWriteRequests∈PROCESS ⇸ ADDRESS × DATA)))))))" //
				), "flag(p ↦ a)=I", //
				true, 100);
	}

	/**
	 * Similar to lemma 3, but reduced set of hypotheses.
	 */
	@Test
	public void reduced3() throws Exception {
		doTest(mTypeEnvironment(
				"lineRequested=ADD↔PRC; M=FLAGS; O=FLAGS; I=FLAGS;" //
				+" caches=PRC↔ℙ(ADD×DATA); S=FLAGS;" //
				+" CPUReadRequests=PRC↔ADD;" //
				+" CPUWriteRequests=PRC↔(ADD×DATA); a=ADD;" //
				+" flag=PRC↔ADD×FLAGS; p=PRC" //
				, ff),		
				mSet("∃a,p·lineRequested={a ↦ p}", //
						"∀l,c,p,a·" //
						+ "  ( CPUWriteRequests(c) = a ↦ l" //
						+ "  ⇒ flag(c ↦ a) = M" //
						+ "  ∧ lineRequested = ∅" //
						+ "  )" //
						+ "∨ ( ( CPUReadRequests(c) = a" //
						+ "    ⇒ c ↦ a ∈ dom(flag)" //
						+ "    )" //
						+ "  ∧ ( ( c ∈ dom(CPUReadRequests)" //
						+ "      ∧ CPUReadRequests(c) = a" //
						+ "      ∧ ¬flag(c ↦ a) = I" //
						+ "      ∧ l = caches(c)(a)" //
						+ "      ∧ a ↦ c ∈ lineRequested" //
						+ "      )" //
						+ "    ∨ ( ( flag(p ↦ a) = I" //
						+ "        ∧ lineRequested = ∅" //
						+ "        ∧ p ∈ dom(CPUReadRequests)" //
						+ "        ∧ CPUReadRequests(p) = a" //
						+ "        )" //
						+ "      ∨ ( flag(p ↦ a) ∈ {I,S,O}" //
						+ "        ∧ lineRequested = ∅" //
						+ "        ⇒ p ∈ dom(CPUWriteRequests)" //
						+ "        ∧ CPUWriteRequests ∈ PRC ⇸ ADD × DATA" //
						+ "        )" //
						+ "      )" //
						+ "    )" //
						+ "  )" //
		), "flag(p ↦ a)=I", //
				false, 100);
	}
}
