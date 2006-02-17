package org.eventb.core.prover.tests;

import junit.framework.TestCase;

import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Predicate;
import org.eventb.core.prover.IExternalReasoner;
import org.eventb.core.prover.Lib;
import org.eventb.core.prover.externalReasoners.ImpE;
import org.eventb.core.prover.externalReasoners.ImpE.Input;
import org.eventb.core.prover.sequent.Hypothesis;
import org.eventb.core.prover.sequent.IProverSequent;
import org.eventb.core.prover.sequent.SimpleProverSequent;

public class LibTest extends TestCase {
	
	public void testIdentical(){
		assertTrue( Lib.identical(
				TestLib.genSeq("1=1 ;; 2=2 |- 1=1 ∧2=2 ∧2=2"),
				TestLib.genSeq("1=1 ;; 2=2 |- 1=1 ∧2=2 ∧2=2")));
		assertFalse( Lib.identical(
				TestLib.genSeq("1=1 ;; 2=2 |- 1=1 ∧2=2"),
				TestLib.genSeq("1=1 ;; 2=2 |- 1=1 ∧2=2 ∧2=2")));
		assertFalse( Lib.identical(
				TestLib.genSeq("1=1 |- 1=1 ∧2=2"),
				TestLib.genSeq("1=1 ;; 2=2 |- 1=1 ∧2=2 ∧2=2")));
		assertTrue( Lib.identical(
				TestLib.genSeq("x=1 ∨x=2 |- x < 3 "),
				TestLib.genSeq("x=1 ∨x=2 |- x < 3 ")));
		// TODO : tests for selected, hidden hyps and type environments 
		
	}
	
	// TODO other tests for Lib.java
	
}
