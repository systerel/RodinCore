/*******************************************************************************
 * Copyright (c) 2006, 2009 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - separation of file and root element
 *     University of Dusseldorf - added theorem attribute
 *******************************************************************************/
package org.eventb.core.tests.pog;

import org.eventb.core.IContextRoot;
import org.eventb.core.IEvent;
import org.eventb.core.IGuard;
import org.eventb.core.IInvariant;
import org.eventb.core.IMachineRoot;
import org.eventb.core.IPOPredicateSet;
import org.eventb.core.IPORoot;
import org.eventb.core.IPOSequent;
import org.eventb.core.IPOStampedElement;

/**
 * @author Stefan Hallerstede
 *
 */
public class DeltaCheckingTest extends EventBPOTest {

	/**
	 * verifies that the intial PO file stamp is generated
	 * for PO files of contexts and machines. 
	 */
	public void testDelta_00_initalStamp() throws Exception {
		IContextRoot root = createContext("ctx");
		saveRodinFileOf(root);
		IMachineRoot mac = createMachine("mch");
		saveRodinFileOf(mac);
		
		runBuilder();
		
		hasStamp(root.getPORoot(), IPOStampedElement.INIT_STAMP);
		hasStamp(root.getPORoot(), IPOStampedElement.INIT_STAMP);
	}
	
	/**
	 * verifies that predicate sets created initially receive
	 * the intial PO stamp, where the source is a context.
	 */
	public void testDelta_01_initCtxPredSet() throws Exception {
		IContextRoot root = createContext("ctx");
		addAxioms(root, makeSList("A"), makeSList("ℕ≠∅"), false);
		saveRodinFileOf(root);
		
		runBuilder();
		
		hasStamp(root.getPORoot().getPredicateSets()[0], IPOStampedElement.INIT_STAMP);
		hasStamp(root.getPORoot().getPredicateSets()[1], IPOStampedElement.INIT_STAMP);
		
	}
	
	/**
	 * verifies that sequents created initially receive
	 * the intial PO stamp, where the source is a context.
	 */
	public void testDelta_02_initCtxSequent() throws Exception {
		IContextRoot root = createContext("ctx");
		addAxioms(root, makeSList("T"), makeSList("ℕ≠∅"), true);
		saveRodinFileOf(root);
	
		runBuilder();
		
		hasStamp(root.getPORoot().getSequents()[0], IPOStampedElement.INIT_STAMP);
		
	}
	
	/**
	 * verifies that predicate sets created initially receive
	 * the intial PO stamp, where the source is a machine.
	 */
	public void testDelta_03_initMchPredSet() throws Exception {
		IMachineRoot root = createMachine("mch");
		addInvariants(root, makeSList("A"), makeSList("ℕ≠∅"), false);
		saveRodinFileOf(root);
		
		runBuilder();
		
		hasStamp(root.getPORoot().getPredicateSets()[0], IPOStampedElement.INIT_STAMP);
		hasStamp(root.getPORoot().getPredicateSets()[1], IPOStampedElement.INIT_STAMP);
		hasStamp(root.getPORoot().getPredicateSets()[2], IPOStampedElement.INIT_STAMP);
		
	}
	
	/**
	 * verifies that sequents created initially receive
	 * the intial PO stamp, where the source is a machine.
	 */
	public void testDelta_04_initMchSequent() throws Exception {
		IMachineRoot root = createMachine("mch");
		addInvariants(root, makeSList("A"), makeSList("ℕ≠∅"), true);
		saveRodinFileOf(root);
		
		runBuilder();
		
		hasStamp(root.getPORoot().getPredicateSets()[0], IPOStampedElement.INIT_STAMP);
		
	}
	
	/**
	 * verifies that predicate sets that are changed by adding a predicate
	 * receive an incremented PO stamp, where the source is a context.
	 */
	public void testDelta_05_addCtxPredSetAndSeq() throws Exception {
		IContextRoot con = createContext("ctx");
		addAxioms(con, makeSList("A"), makeSList("ℕ≠∅"), false);
		saveRodinFileOf(con);
		
		runBuilder();
		
		addAxioms(con, makeSList("B"), makeSList("∀x·x÷x=1"), false);
		saveRodinFileOf(con);
		
		runBuilder();
		
		hasStamp(con.getPORoot().getPredicateSets()[0], IPOStampedElement.INIT_STAMP);
		hasStamp(con.getPORoot().getPredicateSets()[1], IPOStampedElement.INIT_STAMP);
		hasStamp(con.getPORoot().getPredicateSets()[2], IPOStampedElement.INIT_STAMP+1);

		hasStamp(con.getPORoot().getSequents()[0], IPOStampedElement.INIT_STAMP+1);
	}
	
	/**
	 * verifies that predicate sets that are changed by adding a predicate
	 * receive an incremented PO stamp, where the source is a machine.
	 */
	public void testDelta_06_addMchPredSetAndSeq() throws Exception {
		IMachineRoot root = createMachine("mch");
		addInvariants(root, makeSList("A"), makeSList("ℕ≠∅"), false);
		saveRodinFileOf(root);
		
		runBuilder();
		
		addInvariants(root, makeSList("B"), makeSList("∀x·x÷x=1"), false);
		saveRodinFileOf(root);
		
		runBuilder();
		
		hasStamp(root.getPORoot().getPredicateSets()[0], IPOStampedElement.INIT_STAMP);
		hasStamp(root.getPORoot().getPredicateSets()[1], IPOStampedElement.INIT_STAMP);
		hasStamp(root.getPORoot().getPredicateSets()[2], IPOStampedElement.INIT_STAMP);
		hasStamp(root.getPORoot().getPredicateSets()[3], IPOStampedElement.INIT_STAMP+1);

		hasStamp(root.getPORoot().getSequents()[0], IPOStampedElement.INIT_STAMP+1);
		
	}
	
	/**
	 * verifies that predicate sets that are changed by replacing a predicate
	 * receive an incremented PO stamp, where the source is a context.
	 */
	public void testDelta_07_chgCtxPredSetAndSeq() throws Exception {
		IContextRoot con = createContext("ctx");
		addAxioms(con, makeSList("A"), makeSList("∀f·f(0)=0"), false);
		saveRodinFileOf(con);
		
		runBuilder();
		
		hasStamp(con.getPORoot().getSequents()[0], IPOStampedElement.INIT_STAMP);
		
		con = createContext("ctx");
		
		addAxioms(con, makeSList("B"), makeSList("∀x·x÷x=1"), false);
		saveRodinFileOf(con);
		
		runBuilder();
		
		hasStamp(con.getPORoot().getPredicateSets()[0], IPOStampedElement.INIT_STAMP);
		hasStamp(con.getPORoot().getPredicateSets()[1], IPOStampedElement.INIT_STAMP+1);

		hasStamp(con.getPORoot().getSequents()[0], IPOStampedElement.INIT_STAMP+1);
	}
	
	/**
	 * verifies that predicate sets that are changed by replacing a predicate
	 * receive an incremented PO stamp, where the source is a machine.
	 */
	public void testDelta_08_chgMchPredSetAndSeq() throws Exception {
		IMachineRoot mac = createMachine("mch");
		addInvariants(mac, makeSList("A"), makeSList("∀f·f(0)=0"), false);
		saveRodinFileOf(mac);
		
		runBuilder();
		
		hasStamp(mac.getPORoot().getSequents()[0], IPOStampedElement.INIT_STAMP);
		
		mac = createMachine("mch");
		addInvariants(mac, makeSList("B"), makeSList("∀x·x÷x=1"), false);
		saveRodinFileOf(mac);
		
		runBuilder();
		
		hasStamp(mac.getPORoot().getPredicateSets()[0], IPOStampedElement.INIT_STAMP);
		hasStamp(mac.getPORoot().getPredicateSets()[1], IPOStampedElement.INIT_STAMP);
		hasStamp(mac.getPORoot().getPredicateSets()[2], IPOStampedElement.INIT_STAMP+1);

		hasStamp(mac.getPORoot().getSequents()[0], IPOStampedElement.INIT_STAMP+1);
		
	}

	/**
	 * verifies that the PO file stamp is incremented when a predicate set 
	 * is deleted, where the source is a context.
	 */
	public void testDelta_09_delCtxPredSet() throws Exception {
		IContextRoot con = createContext("ctx");
		addAxioms(con, makeSList("A"), makeSList("ℕ≠∅"), false);
		saveRodinFileOf(con);
		
		runBuilder();

		hasStamp(con.getPORoot(), IPOStampedElement.INIT_STAMP);
		

		con = createContext("ctx");
		saveRodinFileOf(con);
		
		runBuilder();
		
		hasStamp(con.getPORoot(), IPOStampedElement.INIT_STAMP+1);
		
	}

	/**
	 * verifies that the PO file stamp is incremented when a predicate set 
	 * is deleted, where the source is a machine.
	 */
	public void testDelta_10_delMchPredSet() throws Exception {
		IMachineRoot mch = createMachine("mch");
		addInvariants(mch, makeSList("A"), makeSList("ℕ≠∅"), false);
		saveRodinFileOf(mch);
		
		runBuilder();

		hasStamp(mch.getPORoot(), IPOStampedElement.INIT_STAMP);
		
		mch = createMachine("mch");
		saveRodinFileOf(mch);
		
		runBuilder();
		
		hasStamp(mch.getPORoot(), IPOStampedElement.INIT_STAMP+1);
		
	}

	/**
	 * verifies that the PO file stamp is incremented when a predicate set 
	 * in the abstraction is deleted, where the source is a context.
	 */
	public void testDelta_11_chgAbsCtxPredSet() throws Exception {
		IContextRoot abs = createContext("abs");
		addAxioms(abs, makeSList("Z"), makeSList("ℕ≠∅"), false);
		saveRodinFileOf(abs);
		
		IContextRoot con = createContext("ctx");
		addContextExtends(con, "abs");
		addAxioms(con, makeSList("A"), makeSList("ℕ≠∅"), false);
		saveRodinFileOf(con);
		
		runBuilder();

		hasStamp(con.getPORoot(), IPOStampedElement.INIT_STAMP);
		

		abs = createContext("abs");
		saveRodinFileOf(abs);
		
		runBuilder();
		
		hasStamp(con.getPORoot(), IPOStampedElement.INIT_STAMP+1);
		
		hasStamp(con.getPORoot().getPredicateSets()[1], IPOStampedElement.INIT_STAMP+1);
	}
	
	/**
	 * verifies that the PO file stamp is incremented when a predicate set 
	 * in the abstraction is deleted, where the source is a machine.
	 */
	public void testDelta_12_chgAbsMchPredSet() throws Exception {
		IMachineRoot abs = createMachine("abs");
		addInvariants(abs, makeSList("Z"), makeSList("ℕ≠∅"), false);
		saveRodinFileOf(abs);
		
		IMachineRoot mch = createMachine("mch");
		addMachineRefines(mch, "abs");
		addInvariants(mch, makeSList("A"), makeSList("ℕ≠∅"), false);
		saveRodinFileOf(mch);
		
		runBuilder();

		hasStamp(mch.getPORoot(), IPOStampedElement.INIT_STAMP);
		
		abs = createMachine("abs");
		saveRodinFileOf(abs);
		
		runBuilder();
		
		hasStamp(mch.getPORoot(), IPOStampedElement.INIT_STAMP+1);
		
		hasStamp(mch.getPORoot().getPredicateSets()[2], IPOStampedElement.INIT_STAMP+1);
	}
	
	/**
	 * verifies that the PO file stamp is incremented when a predicate set 
	 * changes several times, where the source is a context.
	 */
	public void testDelta_13_chgManyCtxPredSet() throws Exception {
		for (int i=0; i<10; i++) {
			IContextRoot con = createContext("ctx");
			addAxioms(con, makeSList("A"), makeSList("∀x·x=" + i), false);
			saveRodinFileOf(con);
		
			runBuilder();
		
			hasStamp(con.getPORoot().getPredicateSets()[0], IPOStampedElement.INIT_STAMP);
			hasStamp(con.getPORoot().getPredicateSets()[1], IPOStampedElement.INIT_STAMP+i);
		}
		
	}
	
	/**
	 * verifies that stamps are not changed when the source is unchanged 
	 * for PO files of contexts and machines. 
	 */
	public void testDelta_14_noChange() throws Exception {
		IContextRoot con = createContext("ctx");
		addAxioms(con, makeSList("T"), makeSList("∀x·x=1"), true);
		saveRodinFileOf(con);
		IMachineRoot mac = createMachine("mch");
		addInvariants(mac, makeSList("T"), makeSList("∀x·x=1"), true);
		saveRodinFileOf(mac);
		
		runBuilder();
		
		hasStamp(con.getPORoot(), IPOStampedElement.INIT_STAMP);
		hasStamp(con.getPORoot().getSequents()[0], IPOStampedElement.INIT_STAMP);
		hasStamp(mac.getPORoot(), IPOStampedElement.INIT_STAMP);
		hasStamp(mac.getPORoot().getSequents()[0], IPOStampedElement.INIT_STAMP);
		
		con.getResource().touch(null);
		mac.getResource().touch(null);
		
		runBuilder();
		
		hasStamp(con.getPORoot(), IPOStampedElement.INIT_STAMP);
		hasStamp(con.getPORoot().getSequents()[0], IPOStampedElement.INIT_STAMP);
		hasStamp(mac.getPORoot(), IPOStampedElement.INIT_STAMP);
		hasStamp(mac.getPORoot().getSequents()[0], IPOStampedElement.INIT_STAMP);
		
	}
	
	/**
	 * Ensures that the proof obligation file of a machine is modified only
	 * when needed.
	 */
	public void testDelta_15_Machine() throws Exception {
		final IMachineRoot mac = createMachine("mac");
		final IPORoot po = mac.getPORoot();
		
		addInvariants(mac, makeSList("I"), makeSList("1∈ℤ"), false);
		saveRodinFileOf(mac);

		runBuilder();

		addVariant(mac, "1");
		saveRodinFileOf(mac);

		runBuilderNotChanged(po);
	}

	/**
	 * Ensures that modifying the guard of an event changes the stamp for the
	 * invariant preservation POs associated to the event.
	 */
	public void testDelta_16_chgIndirectGuard() throws Exception {
		final IMachineRoot mac = createMachine("mac");
		addVariables(mac, "x");
		addInvariants(mac, makeSList("I1"), makeSList("x∈0‥4"), false);
		addEvent(mac, "evt", 
				makeSList(), 
				makeSList("G1"), makeSList("1 < x"), 
				makeSList("A1"), makeSList("x≔x+1"));
		saveRodinFileOf(mac);
		
		runBuilder();
		final IPORoot po = mac.getPORoot();
		final IPOSequent sequent = getSequent(po, "evt/I1/INV");
		hasStamp(po, IPOStampedElement.INIT_STAMP);
		hasStamp(sequent, IPOStampedElement.INIT_STAMP);

		final IEvent evt = mac.getEvents()[0];
		final IGuard g1 = evt.getGuards()[0];
		g1.setPredicateString("x < 4", null);
		saveRodinFileOf(mac);
		
		runBuilder();
		final IPOPredicateSet hyps = sequent.getHypotheses()[0];
		final IPOPredicateSet parent = hyps.getParentPredicateSet();
		hasStamp(parent, IPOStampedElement.INIT_STAMP+1);
		hasStamp(sequent, IPOStampedElement.INIT_STAMP+1);
		hasStamp(po, IPOStampedElement.INIT_STAMP+1);
	}
	
	/**
	 * verifies that stamps of predicate sets and sequents are 
	 * transitively changed, where the source is a machine.
	 */
	public void testDelta_17_chgIndirectInvariant() throws Exception {
		IMachineRoot abs = createMachine("abs");
		addVariables(abs, "u");
		addInvariants(abs, makeSList("H"), makeSList("u>0"), false);
		addEvent(abs, "evt", 
				makeSList(), 
				makeSList(), makeSList(), 
				makeSList("Z"), makeSList("u≔u+1"));
		saveRodinFileOf(abs);
		
		IMachineRoot mac = createMachine("mch");
		addMachineRefines(mac, "abs");
		addVariables(mac, "u", "v");
		addInvariants(mac, makeSList("I"), makeSList("v=u"), false);
		IEvent evt = addEvent(mac, "evt", 
				makeSList(), 
				makeSList(), makeSList(), 
				makeSList("A", "B"), makeSList("v≔v+1", "u≔v"));
		addEventRefines(evt, "evt");
		saveRodinFileOf(mac);
		
		runBuilder();
		
		IPOSequent sequent = getSequent(mac.getPORoot(), "evt/Z/SIM");
		
		hasStamp(sequent, IPOStampedElement.INIT_STAMP);
		hasStamp(sequent.getHypotheses()[0].getParentPredicateSet(), IPOStampedElement.INIT_STAMP);
		
		// in the refined machine mac only the predicate of invariant I is changed
		
		IInvariant invariant = mac.getInvariants()[0];
		invariant.setPredicateString("v=u+1", null);
		saveRodinFileOf(mac);
		
		runBuilder();
		
		sequent = getSequent(mac.getPORoot(), "evt/Z/SIM");
		
		hasStamp(sequent, IPOStampedElement.INIT_STAMP+1);
		hasStamp(sequent.getHypotheses()[0].getParentPredicateSet(), IPOStampedElement.INIT_STAMP+1);
		
	}


}
