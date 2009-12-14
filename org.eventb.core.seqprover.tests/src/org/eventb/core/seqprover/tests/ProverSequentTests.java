/*******************************************************************************
 * Copyright (c) 2006, 2009 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - moved all type-checking code to class TypeChecker
 *     Systerel - added checks about predicate variables
 *******************************************************************************/

package org.eventb.core.seqprover.tests;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import junit.framework.TestCase;


import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.internal.core.seqprover.IInternalProverSequent;
import org.junit.Test;

/**
 * Unit tests for Proover Sequents
 *
 * 
 * @author Farhad Mehta
 */
public class ProverSequentTests extends TestCase{	
	
	public final static FormulaFactory factory = FormulaFactory.getDefault();
	public final static Predicate True = factory.makeLiteralPredicate(Formula.BTRUE,null);
	public final static Predicate False = factory.makeLiteralPredicate(Formula.BFALSE,null);
	private final static Set<Predicate> NO_HYPS = Collections.emptySet();
	private final static FreeIdentifier[] NO_FREE_IDENTS = new FreeIdentifier[0];
	private final static FreeIdentifier freeIdent_x_int = factory.makeFreeIdentifier("x", null, factory.makeIntegerType());
	private final static FreeIdentifier freeIdent_x_bool = factory.makeFreeIdentifier("x", null, factory.makeBooleanType());
	private final static FreeIdentifier freeIdent_y_int = factory.makeFreeIdentifier("y", null, factory.makeIntegerType());
	private static final Predicate pv_P = factory.makePredicateVariable("$P", null);
	
	
	
	/**
	 * Tests for sequent modification
	 */
	@Test
	public void testSequentModification(){
		IProverSequent seq;
		IProverSequent newSeq;
		
		// no modification tests
		seq = TestLib.genSeq(" ⊥ |- ⊥ ");
		newSeq = ((IInternalProverSequent)seq).modify(null, null, null);
		assertSame(seq, newSeq);
		newSeq = ((IInternalProverSequent)seq).modify(NO_FREE_IDENTS, NO_HYPS, False);
		assertSame(seq, newSeq);
		newSeq = ((IInternalProverSequent)seq).modify(null, Collections.singleton(False), null);
		assertSame(seq, newSeq);

		
		// failure tests
		seq = TestLib.genSeq(" x = 1 |- x = 1 ");
		newSeq = ((IInternalProverSequent)seq).modify(new FreeIdentifier[] {freeIdent_x_int}, null, null);
		assertNull(newSeq);
		newSeq = ((IInternalProverSequent)seq).modify(new FreeIdentifier[] {freeIdent_x_bool}, null, null);
		assertNull(newSeq);
		Predicate pred_y = TestLib.genPred("y=1");
		newSeq = ((IInternalProverSequent)seq).modify(null, null, pred_y);
		assertNull(newSeq);
		newSeq = ((IInternalProverSequent)seq).modify(null, Collections.singleton(pred_y), null);
		assertNull(newSeq);
		newSeq = ((IInternalProverSequent)seq).modify(new FreeIdentifier[] {freeIdent_y_int, freeIdent_y_int}, null, null);
		assertNull(newSeq);
		newSeq = ((IInternalProverSequent)seq).modify(null, null, pv_P);
		assertNull(newSeq);
		newSeq = ((IInternalProverSequent)seq).modify(null, Collections.singleton(pv_P), null);
		assertNull(newSeq);
		
		// success tests
		seq = TestLib.genSeq(" x = 1 |- x = 1 ");
		newSeq = ((IInternalProverSequent)seq).modify(new FreeIdentifier[] {freeIdent_y_int}, Collections.singleton(pred_y), pred_y);
		assertNotNull(newSeq);
		assertNotSame(seq, newSeq);
		assertTrue(containsFreeIdent(newSeq.typeEnvironment(), freeIdent_y_int));
		assertTrue(newSeq.containsHypothesis(pred_y));
		assertTrue(newSeq.isSelected(pred_y));
		assertSame(newSeq.goal(), pred_y);
	}
	
	/**
	 * Returns <code>true</code> iff the given type environment contains the given free identifier
	 * and their types match
	 * 
	 * @param typeEnv
	 * @param freeIdent
	 * @return
	 */
	private static boolean containsFreeIdent(ITypeEnvironment typeEnv, FreeIdentifier freeIdent){
		return typeEnv.contains(freeIdent.getName()) && typeEnv.getType(freeIdent.getName()).equals(freeIdent.getType());
	}
	
	/**
	 * Tests for hypothesis selection operations
	 */
	@Test
	public void testHypSelection(){
		IProverSequent seq;
		IProverSequent newSeq;

		final List<Predicate> FalseTrue = Arrays.asList(False, True);
		
		// no modification tests
		seq = TestLib.genSeq(" ⊥ |- ⊥ ");
		newSeq = ((IInternalProverSequent)seq).selectHypotheses(null);
		assertSame(seq, newSeq);
		newSeq = ((IInternalProverSequent)seq).selectHypotheses(NO_HYPS);
		assertSame(seq, newSeq);
		newSeq = ((IInternalProverSequent)seq).selectHypotheses(FalseTrue);
		assertSame(seq, newSeq);
		newSeq = ((IInternalProverSequent)seq).selectHypotheses(Collections.singleton(True));
		assertSame(seq, newSeq);
		
		// success tests
		seq = TestLib.genSeq(" ⊥ |- ⊥ ");
		newSeq = ((IInternalProverSequent)seq).hideHypotheses(FalseTrue);
		assertTrue(newSeq.isHidden(False));
		assertFalse(newSeq.isSelected(False));
		newSeq = ((IInternalProverSequent)newSeq).showHypotheses(FalseTrue);
		assertFalse(newSeq.isHidden(False));
		assertFalse(newSeq.isSelected(False));
		newSeq = ((IInternalProverSequent)newSeq).selectHypotheses(FalseTrue);
		assertFalse(newSeq.isHidden(False));
		assertTrue(newSeq.isSelected(False));
		newSeq = ((IInternalProverSequent)newSeq).deselectHypotheses(FalseTrue);
		assertFalse(newSeq.isHidden(False));
		assertFalse(newSeq.isSelected(False));
	}
	
	/**
	 * Tests for hypotheses order
	 */
	@Test
	public void testSelectedHypOrder(){
		IProverSequent seq;
		IProverSequent newSeq;

		final Predicate p0 = TestLib.genPred("0=0");
		final Predicate p1 = TestLib.genPred("1=1");
		final Predicate p2 = TestLib.genPred("2=2");
		final List<Predicate> ps = Arrays.asList(TestLib.genPred("3=3"),TestLib.genPred("4=4"));

		seq = TestLib.genSeq(" ⊥ ;; 0=0 |- ⊥ ");
		newSeq = ((IInternalProverSequent)seq).modify(null, Collections.singleton(p1), null);
		// The next line should not change the order
		newSeq = ((IInternalProverSequent)newSeq).modify(null, Collections.singleton(False), null);
		newSeq = ((IInternalProverSequent)newSeq).modify(null, Collections.singleton(True), null);
		newSeq = ((IInternalProverSequent)newSeq).modify(null, Collections.singleton(p2), null);
		newSeq = ((IInternalProverSequent)newSeq).modify(null, ps , null);
		
		// Test order of selected hypotheses
		testIterable(new Predicate[]{False,p0,p1,True,p2,ps.get(0),ps.get(1)}, newSeq.selectedHypIterable());
		
		// Test order of all hypotheses
		testIterable(new Predicate[]{False,p0,p1,True,p2,ps.get(0),ps.get(1)}, newSeq.hypIterable());
		
		// Hide two hypotheses	
		newSeq = ((IInternalProverSequent)newSeq).hideHypotheses(Collections.singleton(True));
		newSeq = ((IInternalProverSequent)newSeq).hideHypotheses(Collections.singleton(False));
		
		// Order of all hypotheses remains unchanged
		testIterable(new Predicate[]{False,p0,p1,True,p2,ps.get(0),ps.get(1)}, newSeq.hypIterable());
		
		// Test Order of hidden hypotheses
		testIterable(new Predicate[]{True,False}, newSeq.hiddenHypIterable());

		// Test order of selected hypotheses
		testIterable(new Predicate[]{p0,p1,p2,ps.get(0),ps.get(1)}, newSeq.selectedHypIterable());

		// Test Order of visible hypotheses
		testIterable(new Predicate[]{p0,p1,p2,ps.get(0),ps.get(1)}, newSeq.visibleHypIterable());

		// Select a hidden hypotheses.
		newSeq = ((IInternalProverSequent)newSeq).selectHypotheses(Collections.singleton(True));		

		// Order of all hypotheses remains unchanged
		testIterable(new Predicate[]{False,p0,p1,True,p2,ps.get(0),ps.get(1)}, newSeq.hypIterable());
		
		// Test Order of hidden hypotheses
		testIterable(new Predicate[]{False}, newSeq.hiddenHypIterable());

		// Test order of selected hypotheses
		testIterable(new Predicate[]{p0,p1,p2,ps.get(0),ps.get(1),True}, newSeq.selectedHypIterable());

		// Test Order of visible hypotheses
		testIterable(new Predicate[]{p0,p1,True,p2,ps.get(0),ps.get(1)}, newSeq.visibleHypIterable());
		
		// Hide a selected hypothesis.
		newSeq = ((IInternalProverSequent)newSeq).hideHypotheses(Collections.singleton(ps.get(1)));		

		// Order of all hypotheses remains unchanged
		testIterable(new Predicate[]{False,p0,p1,True,p2,ps.get(0),ps.get(1)}, newSeq.hypIterable());
		
		// Test Order of hidden hypotheses
		testIterable(new Predicate[]{False,ps.get(1)}, newSeq.hiddenHypIterable());

		// Test order of selected hypotheses
		testIterable(new Predicate[]{p0,p1,p2,ps.get(0),True}, newSeq.selectedHypIterable());

		// Test Order of visible hypotheses
		testIterable(new Predicate[]{p0,p1,True,p2,ps.get(0)}, newSeq.visibleHypIterable());
		
	}
	

	/**
	 * Tests that the given iterable iterates over exactly the same elements
	 * in the same order as expected
	 * 
	 * @param expected
	 * 			Array containing the expected iteration order
	 * @param iterable
	 * 			The iterable to test
	 */
	private void testIterable(Predicate[] expected, Iterable<Predicate> iterable){
		int i = 0;
		for (Predicate hyp : iterable)
		  {
			assertTrue("Iterable has more elements than expected",i<expected.length);
			assertTrue("Expected: "+expected[i]+" got: "+hyp,hyp.equals(expected[i]));
			i++;
		  }
		assertEquals("Iterable has less elements than expected",i, expected.length);
	}
	
	
	/**
	 * Tests for forward inference operations
	 */
	@Test
	public void testFwdInfRuleApplication(){
		IProverSequent seq;
		IProverSequent newSeq;
		
		final Predicate pred1 = TestLib.genPred("1=1");
		Collection<Predicate> hyps = Arrays.asList(pred1,False);
		FreeIdentifier[] freeIdent_x = new FreeIdentifier[] {freeIdent_x_int};
		final Predicate pred2_x = TestLib.genPred("x=1");
		Collection<Predicate> infHyps = Arrays.asList(pred2_x,True);
		
		// unmodifying
		// hyp absent
		seq = TestLib.genSeq(" ⊥ |- ⊥ ");
		newSeq = ((IInternalProverSequent)seq).performfwdInf(null, null, null);
		assertSame(seq, newSeq);
		newSeq = ((IInternalProverSequent)seq).performfwdInf(hyps, freeIdent_x, infHyps);
		assertSame(seq, newSeq);
				
		// Free Ident clash
		seq = TestLib.genSeq(" x=1 ;; ⊥ |- ⊥ ");
		newSeq = ((IInternalProverSequent)seq).performfwdInf(hyps, freeIdent_x, infHyps);
		assertSame(seq, newSeq);
		
		// infHyps present
		seq = TestLib.genSeq(" 1=1 ;; ⊥ |- ⊥ ");
		seq = ((IInternalProverSequent)seq).hideHypotheses(Collections.singleton(pred1));
		newSeq = ((IInternalProverSequent)seq).performfwdInf(hyps, null, Collections.singleton(pred1));
		assertSame(seq, newSeq);

		
		// modifying
		seq = TestLib.genSeq(" 1=1 ;; ⊥ |- ⊥ ");
		newSeq = ((IInternalProverSequent)seq).hideHypotheses(Collections.singleton(pred1));
		newSeq = ((IInternalProverSequent)newSeq).performfwdInf(hyps, freeIdent_x, infHyps);
		assertNotSame(seq, newSeq);
		assertTrue(newSeq.typeEnvironment().contains(freeIdent_x_int.getName()));
		assertTrue(newSeq.containsHypotheses(infHyps));
		assertTrue(newSeq.isSelected(pred2_x));
		assertTrue(newSeq.isSelected(True));
		
		seq = TestLib.genSeq(" 1=1 ;; ⊥ ;; ⊤ |- ⊥ ");
		newSeq = ((IInternalProverSequent)seq).hideHypotheses(hyps);
		newSeq = ((IInternalProverSequent)newSeq).performfwdInf(hyps, freeIdent_x, infHyps);
		assertNotSame(seq, newSeq);
		assertTrue(newSeq.typeEnvironment().contains(freeIdent_x_int.getName()));
		assertTrue(newSeq.containsHypotheses(infHyps));
		assertTrue(newSeq.isHidden(pred2_x));
		assertTrue(newSeq.isSelected(True));
		
		// predicate variables in infHyps
		seq = TestLib.genSeq(" 1=1 ;; ⊥ |- ⊥ ");
		newSeq = ((IInternalProverSequent)seq).performfwdInf(hyps, null,
				Collections.singleton(pv_P));
		assertNull(newSeq);
	}
}
