/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.testscpog;

import org.eventb.core.IPOFile;
import org.eventb.core.IPOPredicate;
import org.eventb.core.IPOPredicateSet;
import org.eventb.core.IPOSequent;
import org.eventb.core.ISCContext;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.IParseResult;
import org.eventb.core.ast.ITypeCheckResult;
import org.eventb.core.ast.Predicate;

/**
 * @author halstefa
 *
 */
public class TestContextPOG_2 extends BuilderTest {

	FormulaFactory factory = FormulaFactory.getDefault();

	private Predicate getPredicate(String formula) {
		IParseResult result = factory.parsePredicate(formula);
		assert result.isSuccess();
		Predicate predicate = result.getParsedPredicate();
		return predicate;
	}
	
	private String getWDString(String formula) {
		Predicate predicate = getPredicate(formula);
		ITypeCheckResult tcResult = predicate.typeCheck(factory.makeTypeEnvironment());
		assert tcResult.isSuccess();
		return predicate.getWDPredicate(factory).toStringWithTypes();
	}

	/**
	 * Test method for creation of non-empty carrier set hypotheses
	 */
	public void testCarrierSet1() throws Exception {
		ISCContext rodinFile = createSCContext("cset1");
		addSCCarrierSets(rodinFile, makeList("S"), makeList("ℙ(S)"));
		addTheorems(rodinFile, makeList("T1"), makeList("S ∈ ℙ(S)"), null);
		rodinFile.save(null, true);
		
		IPOFile poFile = runPOG(rodinFile);
		
		IPOSequent[] sequents = poFile.getSequents();
		
		assertTrue("There is only one po", sequents.length == 1);
		
		assertTrue("name ok", poFile.getSequents()[0].getName().equals("T1"));
		
		IPOPredicate[] predicates = sequents[0].getHypothesis().getGlobalHypothesis().getPredicates();
		
		assertTrue("global hypothesis size is 1", predicates.length == 1);
		
		String expected = getPredicate("S≠∅").toString();
		
		assertEquals("carrier set not empty", expected, predicates[0].getContents());
		
	}

	/**
	 * Test method for creation of WD-po of one axiom
	 */
	public void testAxiom1() throws Exception {
		String axiom = "(∀x·x≠0⇒1÷x≤1)";
		ISCContext rodinFile = createSCContext("axiom1");
		addAxioms(rodinFile, makeList("A1"), makeList(axiom), null);
		rodinFile.save(null, true);
		
		IPOFile poFile = runPOG(rodinFile);
		
		String expected = getWDString(axiom);
		IPOSequent[] sequents = poFile.getSequents();
		
		assertTrue("There is only one po", sequents.length == 1);
		
		assertTrue("name ok", poFile.getSequents()[0].getName().equals("A1/WD"));
		
		assertTrue("The global hypothesis is empty", 
				poFile.getPredicateSet(sequents[0].getHypothesis().getContents()).getPredicates().length == 0);
		
		assertTrue("goal is a predicate", sequents[0].getGoal() instanceof IPOPredicate);
		
		assertEquals("WD formula", expected, sequents[0].getGoal().getContents());
		assertEquals("WD formula source", "A1", getSourceName(sequents[0], 0));
		
	}

	/**
	 * Test method for non-creation of WD-po of one axiom
	 */
	public void testAxiom2() throws Exception {
		String axiom = "(∀x·x≠0⇒x>0)";
		ISCContext rodinFile = createSCContext("axiom2");
		addAxioms(rodinFile, 
				makeList("A1"), 
				makeList(axiom), null);
		rodinFile.save(null, true);
		
		IPOFile poFile = runPOG(rodinFile);
		assertEquals("No proof obligation", 0, poFile.getSequents().length);
	}
	
	/**
	 * Test method for proper hypotheses set generation for axioms
	 */
	public void testAxiom3() throws Exception {
		String axiom1 = "(∀x·x≠0⇒x>0)";
		String axiom2 = "(∀x·x≠0⇒1÷x≤1)";
		ISCContext rodinFile = createSCContext("axiom3");
		addAxioms(rodinFile, 
				makeList("A1", "A2"), 
				makeList(axiom1, axiom2), null);
		rodinFile.save(null, true);

		IPOFile poFile = runPOG(rodinFile);
		assertEquals("Exactly one proof obligation", 1, poFile.getSequents().length);
		assertEquals("Wrong PO name", "A2/WD", poFile.getSequents()[0].getName());

		IPOPredicateSet predicateSet = poFile.getSequents()[0].getHypothesis().getGlobalHypothesis();
		assertEquals("only one predicate in global hypothesis", 1, predicateSet.getPredicates().length);
		assertEquals("A1 in global hypothesis", axiom1, predicateSet.getPredicates()[0].getContents());

		IPOPredicateSet set = predicateSet.getPredicateSet();
		assertTrue("No more predicates in global hypothesis", set == null || set.getPredicates().length == 0);
	}
	
	/**
	 * Test method for creation of po and WD-po of one theorem
	 */
	public void testTheorem1() throws Exception {
		String theorem = "(∀x·x≠0⇒1÷x≤1)";
		ISCContext rodinFile = createSCContext("theorem1");
		addTheorems(rodinFile, 
				makeList("T1"), 
				makeList(theorem), null);
		rodinFile.save(null, true);

		IPOFile poFile = runPOG(rodinFile);
		String expected = getWDString(theorem);
		IPOSequent[] sequents = poFile.getSequents();
		
		assertTrue("There is only one po", sequents.length == 2);
		
		int thm_sequent = (sequents[0].getName().equals("T1")) ? 0 : 1;
		
		assertTrue("name ok", sequents[thm_sequent].getName().equals("T1"));
		assertTrue("name ok", sequents[1-thm_sequent].getName().equals("T1/WD"));
		
		assertEquals("formula source", "T1", getSourceName(sequents[0], 0));
		assertEquals("WD formula source", "T1", getSourceName(sequents[1], 0));

		assertTrue("The global hypothesis is empty", poFile.getPredicateSet(sequents[0].getHypothesis().getContents()).getPredicates().length == 0);
		assertTrue("The global hypothesis is empty", poFile.getPredicateSet(sequents[1].getHypothesis().getContents()).getPredicates().length == 0);
		
		assertTrue("goal is a predicate", sequents[0].getGoal() instanceof IPOPredicate);
		assertTrue("goal is a predicate", sequents[1].getGoal() instanceof IPOPredicate);
		
		assertEquals("WD formula", expected, sequents[1-thm_sequent].getGoal().getContents());
		
	}

	/**
	 * Test method for creation of only po of one theorem
	 */
	public void testTheorem2() throws Exception {
		String theorem = "(∀x·x≠0⇒x>0)";
		ISCContext rodinFile = createSCContext("theorem2");
		addTheorems(rodinFile, 
				makeList("T1"), 
				makeList(theorem), null);
		rodinFile.save(null, true);
		IPOFile poFile = runPOG(rodinFile);
		
		assertTrue("Only one proof obligation", poFile.getSequents().length == 1);
		assertTrue("name ok", poFile.getSequents()[0].getName().equals("T1"));
	}
	
	private int getIndexForName(String name, IPOSequent[] sequents) {
		for(int i=0; i<sequents.length; i++)
			if(sequents[i].getName().equals(name))
				return i;
		return -1;
	}
	
	/**
	 * Test method for proper hypotheses set generation for theorems
	 */
	public void testTheorem3() throws Exception {
		String theorem1 = "(∀x·x≠0⇒x>0)";
		String theorem2 = "(∀x·x≠0⇒1÷x≤1)";
		ISCContext rodinFile = createSCContext("theorem3");
		addTheorems(rodinFile, 
				makeList("T1", "T2"), 
				makeList(theorem1, theorem2), null);
		rodinFile.save(null, true);
		
		IPOFile poFile = runPOG(rodinFile);
		
		IPOSequent[] sequents = poFile.getSequents();
		
		assertTrue("Exactly three proof obligations", sequents.length == 3);
		
		int t1 = getIndexForName("T1", sequents);
		int t2 = getIndexForName("T2", sequents);
		int t2wd = getIndexForName("T2/WD", sequents);
		
		assertTrue("names ok", t1 != -1 && t2 != -1 && t2wd != -1);
		
		IPOPredicateSet predicateSet = poFile.getSequents()[t2].getHypothesis().getGlobalHypothesis();
		
		assertTrue("only one predicate in global hypothesis", predicateSet.getPredicates().length == 1);
		
		assertEquals("T1 in global hypothesis", theorem1, predicateSet.getPredicates()[0].getContents());
		
		IPOPredicateSet set = predicateSet.getPredicateSet();
		
		assertTrue("No more predicates in global hypothesis", set == null || set.getPredicates().length == 0);
	}
	
	/**
	 * Test method for non-creation of WD-po of one axiom and two theorems
	 */
	public void testAxiomAndTheorem1() throws Exception {
		String axiom1 = "(∀y·y>0⇒y+1=0)";
		String theorem1 = "(∀x·x≠0⇒x>0)";
		String theorem2 = "(∃z·z∈ℕ∧z>0)";
		ISCContext rodinFile = createSCContext("theorem3");
		addAxioms(rodinFile,
				makeList("A1"),
				makeList(axiom1), null);
		addTheorems(rodinFile, 
				makeList("T1", "T2"), 
				makeList(theorem1, theorem2), null);
		rodinFile.save(null, true);

		IPOFile poFile = runPOG(rodinFile);

		IPOSequent[] sequents = poFile.getSequents();
		assertEquals("Wrong number of proof obligations", 2, sequents.length);
		
		int t1 = getIndexForName("T1", sequents);
		int t2 = getIndexForName("T2", sequents);
		
		assertTrue("names ok", t1 != -1 && t2 != -1);

		assertEquals("formula source", "T1", getSourceName(sequents[t1], 0));
		assertEquals("formula source", "T2", getSourceName(sequents[t2], 0));
		
		IPOPredicateSet set1 = sequents[t1].getHypothesis().getGlobalHypothesis();
		
		assertTrue("Only axiom A1 in global hyp of T1", set1.getPredicates().length == 1 && set1.getPredicates()[0].getContents().equals(axiom1));
		
		IPOPredicateSet set2 = sequents[t2].getHypothesis().getGlobalHypothesis();
		
		assertTrue("Theorem T1 in global hyp of T2", set2.getPredicates().length == 1 && set2.getPredicates()[0].getContents().equals(theorem1));
		
		IPOPredicateSet set3 = set2.getPredicateSet();
		
		assertTrue("Axiom A1 in global hyp of T2", set3.getPredicates().length == 1 && set3.getPredicates()[0].getContents().equals(axiom1));
	}
	
}
