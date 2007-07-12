/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.tests.pog;

import org.eventb.core.IContextFile;
import org.eventb.core.IMachineFile;
import org.eventb.core.IPOFile;
import org.eventb.core.IPOStamp;

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
		IContextFile con = createContext("ctx");
		con.save(null, true);
		IMachineFile mac = createMachine("mch");
		mac.save(null, true);
		
		runBuilder();
		
		hasStamp(con.getPOFile(), IPOStamp.INIT_STAMP);
		hasStamp(mac.getPOFile(), IPOStamp.INIT_STAMP);
	}
	
	/**
	 * verifies that predicate sets created initially receive
	 * the intial PO stamp, where the source is a context.
	 */
	public void testDelta_01_initCtxPredSet() throws Exception {
		IContextFile con = createContext("ctx");
		addAxioms(con, makeSList("A"), makeSList("ℕ≠∅"));
		con.save(null, true);
		
		runBuilder();
		
		hasStamp(con.getPOFile().getPredicateSets()[0], IPOStamp.INIT_STAMP);
		hasStamp(con.getPOFile().getPredicateSets()[1], IPOStamp.INIT_STAMP);
		
	}
	
	/**
	 * verifies that sequents created initially receive
	 * the intial PO stamp, where the source is a context.
	 */
	public void testDelta_02_initCtxSequent() throws Exception {
		IContextFile con = createContext("ctx");
		addTheorems(con, makeSList("T"), makeSList("ℕ≠∅"));
		con.save(null, true);
	
		runBuilder();
		
		hasStamp(con.getPOFile().getSequents()[0], IPOStamp.INIT_STAMP);
		
	}
	
	/**
	 * verifies that predicate sets created initially receive
	 * the intial PO stamp, where the source is a machine.
	 */
	public void testDelta_03_initMchPredSet() throws Exception {
		IMachineFile mac = createMachine("mch");
		addInvariants(mac, makeSList("A"), makeSList("ℕ≠∅"));
		mac.save(null, true);
		
		runBuilder();
		
		hasStamp(mac.getPOFile().getPredicateSets()[0], IPOStamp.INIT_STAMP);
		hasStamp(mac.getPOFile().getPredicateSets()[1], IPOStamp.INIT_STAMP);
		hasStamp(mac.getPOFile().getPredicateSets()[2], IPOStamp.INIT_STAMP);
		
	}
	
	/**
	 * verifies that sequents created initially receive
	 * the intial PO stamp, where the source is a machine.
	 */
	public void testDelta_04_initMchSequent() throws Exception {
		IMachineFile mac = createMachine("mch");
		addTheorems(mac, makeSList("A"), makeSList("ℕ≠∅"));
		mac.save(null, true);
		
		runBuilder();
		
		hasStamp(mac.getPOFile().getPredicateSets()[0], IPOStamp.INIT_STAMP);
		
	}
	
	/**
	 * verifies that predicate sets that are changed by adding a predicate
	 * receive an incremented PO stamp, where the source is a context.
	 */
	public void testDelta_05_addCtxPredSetAndSeq() throws Exception {
		IContextFile con = createContext("ctx");
		addAxioms(con, makeSList("A"), makeSList("ℕ≠∅"));
		con.save(null, true);
		
		runBuilder();
		
		addAxioms(con, makeSList("B"), makeSList("∀x·x÷x=1"));
		con.save(null, true);
		
		runBuilder();
		
		hasStamp(con.getPOFile().getPredicateSets()[0], IPOStamp.INIT_STAMP);
		hasStamp(con.getPOFile().getPredicateSets()[1], IPOStamp.INIT_STAMP);
		hasStamp(con.getPOFile().getPredicateSets()[2], IPOStamp.INIT_STAMP+1);

		hasStamp(con.getPOFile().getSequents()[0], IPOStamp.INIT_STAMP+1);
	}
	
	/**
	 * verifies that predicate sets that are changed by adding a predicate
	 * receive an incremented PO stamp, where the source is a machine.
	 */
	public void testDelta_06_addMchPredSetAndSeq() throws Exception {
		IMachineFile mac = createMachine("mch");
		addInvariants(mac, makeSList("A"), makeSList("ℕ≠∅"));
		mac.save(null, true);
		
		runBuilder();
		
		addInvariants(mac, makeSList("B"), makeSList("∀x·x÷x=1"));
		mac.save(null, true);
		
		runBuilder();
		
		hasStamp(mac.getPOFile().getPredicateSets()[0], IPOStamp.INIT_STAMP);
		hasStamp(mac.getPOFile().getPredicateSets()[1], IPOStamp.INIT_STAMP);
		hasStamp(mac.getPOFile().getPredicateSets()[2], IPOStamp.INIT_STAMP);
		hasStamp(mac.getPOFile().getPredicateSets()[3], IPOStamp.INIT_STAMP+1);

		hasStamp(mac.getPOFile().getSequents()[0], IPOStamp.INIT_STAMP+1);
		
	}
	
	/**
	 * verifies that predicate sets that are changed by replacing a predicate
	 * receive an incremented PO stamp, where the source is a context.
	 */
	public void testDelta_07_chgCtxPredSetAndSeq() throws Exception {
		IContextFile con = createContext("ctx");
		addAxioms(con, makeSList("A"), makeSList("∀f·f(0)=0"));
		con.save(null, true);
		
		runBuilder();
		
		hasStamp(con.getPOFile().getSequents()[0], IPOStamp.INIT_STAMP);
		
		con = createContext("ctx");
		addAxioms(con, makeSList("B"), makeSList("∀x·x÷x=1"));
		con.save(null, true);
		
		runBuilder();
		
		hasStamp(con.getPOFile().getPredicateSets()[0], IPOStamp.INIT_STAMP);
		hasStamp(con.getPOFile().getPredicateSets()[1], IPOStamp.INIT_STAMP+1);

		hasStamp(con.getPOFile().getSequents()[0], IPOStamp.INIT_STAMP+1);
	}
	
	/**
	 * verifies that predicate sets that are changed by replacing a predicate
	 * receive an incremented PO stamp, where the source is a machine.
	 */
	public void testDelta_08_chgMchPredSetAndSeq() throws Exception {
		IMachineFile mac = createMachine("mch");
		addInvariants(mac, makeSList("A"), makeSList("∀f·f(0)=0"));
		mac.save(null, true);
		
		runBuilder();
		
		hasStamp(mac.getPOFile().getSequents()[0], IPOStamp.INIT_STAMP);
		
		mac = createMachine("mch");
		addInvariants(mac, makeSList("B"), makeSList("∀x·x÷x=1"));
		mac.save(null, true);
		
		runBuilder();
		
		hasStamp(mac.getPOFile().getPredicateSets()[0], IPOStamp.INIT_STAMP);
		hasStamp(mac.getPOFile().getPredicateSets()[1], IPOStamp.INIT_STAMP);
		hasStamp(mac.getPOFile().getPredicateSets()[2], IPOStamp.INIT_STAMP+1);

		hasStamp(mac.getPOFile().getSequents()[0], IPOStamp.INIT_STAMP+1);
		
	}

	/**
	 * verifies that the PO file stamp is incremented when a predicate set 
	 * is deleted, where the source is a context.
	 */
	public void testDelta_09_delCtxPredSet() throws Exception {
		IContextFile con = createContext("ctx");
		addAxioms(con, makeSList("A"), makeSList("ℕ≠∅"));
		con.save(null, true);
		
		runBuilder();

		hasStamp(con.getPOFile(), IPOStamp.INIT_STAMP);
		
		con = createContext("ctx");
		con.save(null, true);
		
		runBuilder();
		
		hasStamp(con.getPOFile(), IPOStamp.INIT_STAMP+1);
		
	}

	/**
	 * verifies that the PO file stamp is incremented when a predicate set 
	 * is deleted, where the source is a machine.
	 */
	public void testDelta_10_delMchPredSet() throws Exception {
		IMachineFile mch = createMachine("mch");
		addInvariants(mch, makeSList("A"), makeSList("ℕ≠∅"));
		mch.save(null, true);
		
		runBuilder();

		hasStamp(mch.getPOFile(), IPOStamp.INIT_STAMP);
		
		mch = createMachine("mch");
		mch.save(null, true);
		
		runBuilder();
		
		hasStamp(mch.getPOFile(), IPOStamp.INIT_STAMP+1);
		
	}

	/**
	 * verifies that the PO file stamp is incremented when a predicate set 
	 * in the abstraction is deleted, where the source is a context.
	 */
	public void testDelta_11_chgAbsCtxPredSet() throws Exception {
		IContextFile abs = createContext("abs");
		addAxioms(abs, makeSList("Z"), makeSList("ℕ≠∅"));
		abs.save(null, true);
		
		IContextFile con = createContext("ctx");
		addContextExtends(con, "abs");
		addAxioms(con, makeSList("A"), makeSList("ℕ≠∅"));
		con.save(null, true);
		
		runBuilder();

		hasStamp(con.getPOFile(), IPOStamp.INIT_STAMP);
		
		abs = createContext("abs");
		abs.save(null, true);
		
		runBuilder();
		
		hasStamp(con.getPOFile(), IPOStamp.INIT_STAMP+1);
		
		hasStamp(con.getPOFile().getPredicateSets()[1], IPOStamp.INIT_STAMP+1);
	}
	
	/**
	 * verifies that the PO file stamp is incremented when a predicate set 
	 * in the abstraction is deleted, where the source is a machine.
	 */
	public void testDelta_12_chgAbsMchPredSet() throws Exception {
		IMachineFile abs = createMachine("abs");
		addInvariants(abs, makeSList("Z"), makeSList("ℕ≠∅"));
		abs.save(null, true);
		
		IMachineFile mch = createMachine("mch");
		addMachineRefines(mch, "abs");
		addInvariants(mch, makeSList("A"), makeSList("ℕ≠∅"));
		mch.save(null, true);
		
		runBuilder();

		hasStamp(mch.getPOFile(), IPOStamp.INIT_STAMP);
		
		abs = createMachine("abs");
		abs.save(null, true);
		
		runBuilder();
		
		hasStamp(mch.getPOFile(), IPOStamp.INIT_STAMP+1);
		
		hasStamp(mch.getPOFile().getPredicateSets()[2], IPOStamp.INIT_STAMP+1);
	}
	
	/**
	 * verifies that the PO file stamp is incremented when a predicate set 
	 * changes several times, where the source is a context.
	 */
	public void testDelta_13_chgManyCtxPredSet() throws Exception {
		for (int i=0; i<10; i++) {
			IContextFile con = createContext("ctx");
			addAxioms(con, makeSList("A"), makeSList("∀x·x=" + i));
			con.save(null, true);
		
			runBuilder();
		
			hasStamp(con.getPOFile().getPredicateSets()[0], IPOStamp.INIT_STAMP);
			hasStamp(con.getPOFile().getPredicateSets()[1], IPOStamp.INIT_STAMP+i);
		}
		
	}
	
	/**
	 * verifies that stamps are not changed when the source is unchanged 
	 * for PO files of contexts and machines. 
	 */
	public void testDelta_14_noChange() throws Exception {
		IContextFile con = createContext("ctx");
		addTheorems(con, makeSList("T"), makeSList("∀x·x=1"));
		con.save(null, true);
		IMachineFile mac = createMachine("mch");
		addTheorems(mac, makeSList("T"), makeSList("∀x·x=1"));
		mac.save(null, true);
		
		runBuilder();
		
		hasStamp(con.getPOFile(), IPOStamp.INIT_STAMP);
		hasStamp(con.getPOFile().getSequents()[0], IPOStamp.INIT_STAMP);
		hasStamp(mac.getPOFile(), IPOStamp.INIT_STAMP);
		hasStamp(mac.getPOFile().getSequents()[0], IPOStamp.INIT_STAMP);
		
		con.getResource().touch(null);
		mac.getResource().touch(null);
		
		runBuilder();
		
		hasStamp(con.getPOFile(), IPOStamp.INIT_STAMP);
		hasStamp(con.getPOFile().getSequents()[0], IPOStamp.INIT_STAMP);
		hasStamp(mac.getPOFile(), IPOStamp.INIT_STAMP);
		hasStamp(mac.getPOFile().getSequents()[0], IPOStamp.INIT_STAMP);
		
	}
	
	/**
	 * Ensures that the proof obligation file of a machine is modified only
	 * when needed.
	 */
	public void testDelta_15_Machine() throws Exception {
		final IMachineFile mac = createMachine("mac");
		final IPOFile po = mac.getPOFile();
		
		addInvariants(mac, makeSList("I"), makeSList("1∈ℤ"));
		mac.save(null, true);

		runBuilder();

		addVariant(mac, "1");
		mac.save(null, true);

		runBuilderNotChanged(po);
	}


}
