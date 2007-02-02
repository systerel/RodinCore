/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
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

/**
 * Unit tests for Proover Sequents
 *
 * 
 * @author Farhad Mehta
 */
public class ProverSequentTests extends TestCase {	
	
	public final static FormulaFactory factory = FormulaFactory.getDefault();
	public final static Predicate True = factory.makeLiteralPredicate(Formula.BTRUE,null);
	public final static Predicate False = factory.makeLiteralPredicate(Formula.BFALSE,null);
	private final static Set<Predicate> NO_HYPS = Collections.emptySet();
	private final static FreeIdentifier[] NO_FREE_IDENTS = new FreeIdentifier[0];
	private final static FreeIdentifier freeIdent_x_int = factory.makeFreeIdentifier("x", null, factory.makeIntegerType());
	private final static FreeIdentifier freeIdent_x_bool = factory.makeFreeIdentifier("x", null, factory.makeBooleanType());
	private final static FreeIdentifier freeIdent_y_int = factory.makeFreeIdentifier("y", null, factory.makeIntegerType());
	
	/**
	 * Tests for sequent modification
	 */
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
	 * Tests for forward inference operations
	 */
	public void testFwdInfRuleApplication(){
		IProverSequent seq;
		IProverSequent newSeq;
		
		final Predicate pred1 = TestLib.genPred("1=1");
		Collection<Predicate> hyps = Arrays.asList(pred1,False);
		FreeIdentifier[] freeIdent_x = new FreeIdentifier[] {freeIdent_x_int};
		final Predicate pred2_x = TestLib.genPred("x=1");
		Collection<Predicate> infHyps = Arrays.asList(pred2_x,True);
		// IProofRule fwdInf = fwdInf(pred1, freeIdent_x, pred2);
		
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
		
	}
}
