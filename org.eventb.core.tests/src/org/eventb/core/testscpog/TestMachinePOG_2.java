/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.testscpog;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.eventb.core.IPOFile;
import org.eventb.core.IPOIdentifier;
import org.eventb.core.IPOModifiedPredicate;
import org.eventb.core.IPOPredicate;
import org.eventb.core.IPOPredicateSet;
import org.eventb.core.IPOSequent;
import org.eventb.core.ISCMachine;
import org.eventb.core.ast.Assignment;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.IParseResult;
import org.eventb.core.ast.ITypeCheckResult;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.Type;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinElement;

public class TestMachinePOG_2 extends BuilderTest {

	FormulaFactory factory = FormulaFactory.getDefault();
	Type INTEGER = factory.parseType("ℤ").getParsedType();
	
	private String getWDStringForPredicate(String formula, ITypeEnvironment environment) {
		if(environment == null)
			environment = factory.makeTypeEnvironment();
		IParseResult result = factory.parsePredicate(formula);
		assert result.isSuccess();
		Predicate predicate = result.getParsedPredicate();
		ITypeCheckResult tcResult = predicate.typeCheck(environment);
		assert tcResult.isSuccess();
		return predicate.getWDPredicate(factory).toString();
	}

	private String getWDStringForAssignment(String formula, ITypeEnvironment environment) {
		IParseResult result = factory.parseAssignment(formula);
		assert result.isSuccess();
		Assignment assignment = result.getParsedAssignment();
		ITypeCheckResult tcResult = assignment.typeCheck(environment);
		assert tcResult.isSuccess();
		return assignment.getWDPredicate(factory).toString();
	}

	private String getFISStringForAssignment(String formula, ITypeEnvironment environment) {
		IParseResult result = factory.parseAssignment(formula);
		assert result.isSuccess();
		Assignment assignment = result.getParsedAssignment();
		ITypeCheckResult tcResult = assignment.typeCheck(environment);
		assert tcResult.isSuccess();
		return assignment.getFISPredicate(factory).toString();
	}

	private String getBAStringForAssignment(String formula, ITypeEnvironment environment) {
		IParseResult result = factory.parseAssignment(formula);
		assert result.isSuccess();
		Assignment assignment = result.getParsedAssignment();
		ITypeCheckResult tcResult = assignment.typeCheck(environment);
		assert tcResult.isSuccess();
		return assignment.getBAPredicate(factory).toString();
	}

	private int getIndexForName(String name, IRodinElement[] elements) {
		for(int i=0; i<elements.length; i++)
			if(elements[i].getElementName().equals(name))
				return i;
		return -1;
	}
	
	private Set<String> contentsSetOf(IInternalElement[] elements) throws Exception {
		HashSet<String> set = new HashSet<String>(elements.length * 4 / 3 + 1);
		for(IInternalElement element : elements) {
			set.add(element.getContents());
		}
		return set;
	}
	
	/**
	 * Test method for creation of WD-po of one invariant
	 */
	public void testInvariant1() throws Exception {
		String invariant = "(∀x·x≠0⇒1÷x≤1)";
		ISCMachine rodinFile = createSCMachine("test");
		addInvariants(rodinFile, 
				makeList("I1"), 
				makeList(invariant));
		rodinFile.save(null, true);
		IPOFile poFile = runPOG(rodinFile);
		
		String expected = getWDStringForPredicate(invariant, null);
		IPOSequent[] sequents = poFile.getSequents();
		
		assertTrue("There is only one po", sequents.length == 1);
		
		assertTrue("name ok", poFile.getSequents()[0].getName().equals("I1/WD"));
		
		assertEquals("WD formula source", "I1", getSourceName(sequents[0], 0));

		assertTrue("The global hypothesis is empty", poFile.getPredicateSet(sequents[0].getHypothesis().getContents()).getPredicates().length == 0);
		
		assertTrue("goal is a predicate", sequents[0].getGoal() instanceof IPOPredicate);
		
		assertEquals("WD formula", expected, sequents[0].getGoal().getContents());
		
	}

	/**
	 * Test method for non-creation of WD-po of one invariant
	 */
	public void testInvariant2() throws Exception {
		String invariant = "(∀x·x≠0⇒x>0)";
		ISCMachine rodinFile = createSCMachine("test");
		addAxioms(rodinFile, 
				makeList("I1"), 
				makeList(invariant), null);
		rodinFile.save(null, true);

		IPOFile poFile = runPOG(rodinFile);
		assertTrue("No proof obligation", poFile.getSequents().length == 0);
	}
	
	/**
	 * Test method for proper hypotheses set generation for invariants
	 */
	public void testInvariant3() throws Exception {
		String invariant1 = "(∀x·x≠0⇒x>0)";
		String invariant2 = "(∀x·x≠0⇒1÷x≤1)";
		ISCMachine rodinFile = createSCMachine("test");
		addInvariants(rodinFile, 
				makeList("I1", "I2"), 
				makeList(invariant1, invariant2));
		rodinFile.save(null, true);
		IPOFile poFile = runPOG(rodinFile);
		
		final IPOSequent[] sequents = poFile.getSequents();
		assertEquals("Wrong number of proof obligation", 1, sequents.length);
		assertEquals("Wrong PO name", "I2/WD", sequents[0].getName());
		assertEquals("Wrong WD formula source", "I2", getSourceName(sequents[0], 0));

		IPOPredicateSet predicateSet = sequents[0].getHypothesis().getGlobalHypothesis();
		
		assertTrue("only one predicate in global hypothesis", predicateSet.getPredicates().length == 1);
		
		assertEquals("I1 in global hypothesis", invariant1, predicateSet.getPredicates()[0].getContents());
		
		IPOPredicateSet set = predicateSet.getPredicateSet();
		
		assertTrue("No more predicates in global hypothesis", set == null || set.getPredicates().length == 0);
	
	}
	
	/**
	 * Test method for creation of po and WD-po of one theorem
	 */
	public void testTheorem1() throws Exception {
		String theorem = "(∀x·x≠0⇒1÷x≤1)";
		ISCMachine rodinFile = createSCMachine("test");
		addTheorems(rodinFile, 
				makeList("T1"), 
				makeList(theorem), null);
		rodinFile.save(null, true);
		IPOFile poFile = runPOG(rodinFile);
		
		String expected = getWDStringForPredicate(theorem, null);
		IPOSequent[] sequents = poFile.getSequents();
		
		assertEquals("Wrong number of POs", 2, sequents.length);
		
		final int t1 = getIndexForName("T1", sequents);
		final int t1wd = getIndexForName("T1/WD", sequents);
		
		assertTrue("names ok", t1 != -1 && t1wd != -1);
		
		assertEquals("formula source", "T1", getSourceName(sequents[t1], 0));
		assertEquals("WD formula source", "T1", getSourceName(sequents[t1wd], 0));

		assertTrue("The global hypothesis is empty", poFile.getPredicateSet(sequents[0].getHypothesis().getContents()).getPredicates().length == 0);
		assertTrue("The global hypothesis is empty", poFile.getPredicateSet(sequents[1].getHypothesis().getContents()).getPredicates().length == 0);
		
		assertTrue("goal is a predicate", sequents[0].getGoal() instanceof IPOPredicate);
		assertTrue("goal is a predicate", sequents[1].getGoal() instanceof IPOPredicate);
		
		assertEquals("WD formula", expected, sequents[t1wd].getGoal().getContents());
		
	}

	/**
	 * Test method for creation of only po of one theorem
	 */
	public void testTheorem2() throws Exception {
		String theorem = "(∀x·x≠0⇒x>0)";
		ISCMachine rodinFile = createSCMachine("test");
		addTheorems(rodinFile, 
				makeList("T1"), 
				makeList(theorem), null);
		rodinFile.save(null, true);
		IPOFile poFile = runPOG(rodinFile);
		
		assertTrue("Only one proof obligation", poFile.getSequents().length == 1);
		assertTrue("name ok", poFile.getSequents()[0].getName().equals("T1"));
	}

	/**
	 * Test method for proper hypotheses set generation for theorems
	 */
	public void testTheorem3() throws Exception {
		String theorem1 = "(∀x·x≠0⇒x>0)";
		String theorem2 = "(∀x·x≠0⇒1÷x≤1)";
		ISCMachine rodinFile = createSCMachine("test");
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
		
		assertEquals("formula source", "T1", getSourceName(sequents[t1], 0));
		assertEquals("formula source", "T2", getSourceName(sequents[t2], 0));
		assertEquals("WD formula source", "T2", getSourceName(sequents[t2wd], 0));

		IPOPredicateSet predicateSet = poFile.getSequents()[t2].getHypothesis().getGlobalHypothesis();
		
		assertTrue("only one predicate in global hypothesis", predicateSet.getPredicates().length == 1);
		
		assertEquals("T1 in global hypothesis", theorem1, predicateSet.getPredicates()[0].getContents());
		
		IPOPredicateSet set = predicateSet.getPredicateSet();
		
		assertTrue("No more predicates in global hypothesis", set == null || set.getPredicates().length == 0);
	}
	
	/**
	 * Test method for hypothesis
	 */
	public void testInvariantAndTheorem1() throws Exception {
		String invariant1 = "(∀y·y>0⇒y+1=0)";
		String theorem1 = "(∀x·x≠0⇒x>0)";
		String theorem2 = "(∃z·z∈ℕ∧z>0)";
		ISCMachine rodinFile = createSCMachine("test");
		addInvariants(rodinFile,
				makeList("I1"),
				makeList(invariant1));
		addTheorems(rodinFile, 
				makeList("T1", "T2"), 
				makeList(theorem1, theorem2), null);
		rodinFile.save(null, true);
		IPOFile poFile = runPOG(rodinFile);
		
		IPOSequent[] sequents = poFile.getSequents();
		
		assertTrue("Exactly two proof obligations", sequents.length == 2);
		
		int t1 = getIndexForName("T1", sequents);
		int t2 = getIndexForName("T2", sequents);
		
		assertTrue("names ok", t1 != -1 && t2 != -1);
		
		IPOPredicateSet set1 = sequents[t1].getHypothesis().getGlobalHypothesis();
		
		assertTrue("Only invariant I1 in global hyp of T1", set1.getPredicates().length == 1 && set1.getPredicates()[0].getContents().equals(invariant1));
		
		IPOPredicateSet set2 = sequents[t2].getHypothesis().getGlobalHypothesis();
		
		assertTrue("Theorem T1 in global hyp of T2", set2.getPredicates().length == 1 && set2.getPredicates()[0].getContents().equals(theorem1));
		
		IPOPredicateSet set3 = set2.getPredicateSet();
		
		assertTrue("Invariant I1 in global hyp of T2", set3.getPredicates().length == 1 && set3.getPredicates()[0].getContents().equals(invariant1));
	}
	
	/**
	 * Test method for context hypothesis
	 */
	public void testContextForInvariantAndTheorem1() throws Exception {
		String ctxAxiom1 = "(∃x·x=1)";
		String ctxTheorem1 = "(∀x·x=1)";
		String invariant1 = "(∀y·y>0⇒y+1=0)";
		String theorem1 = "(∀x·x≠0⇒x>0)";
		String theorem2 = "(∃z·z∈ℕ∧z>0)";
		ISCMachine rodinFile = createSCMachine("test");
		addAxioms(rodinFile, makeList("A1"), makeList(ctxAxiom1), "CONTEXT");
		addTheorems(rodinFile, makeList("X1"), makeList(ctxTheorem1), "CONTEXT");
		addInvariants(rodinFile,
				makeList("I1"),
				makeList(invariant1));
		addTheorems(rodinFile, 
				makeList("T1", "T2"), 
				makeList(theorem1, theorem2), null);
		rodinFile.save(null, true);
		IPOFile poFile = runPOG(rodinFile);
		
		IPOSequent[] sequents = poFile.getSequents();
		
		assertTrue("Exactly two proof obligations", sequents.length == 2);
		
		int t1 = getIndexForName("T1", sequents);
		int t2 = getIndexForName("T2", sequents);
		
		assertTrue("names ok", t1 != -1 && t2 != -1);
		
		IPOPredicateSet set1 = sequents[t1].getHypothesis().getGlobalHypothesis();
		
		assertTrue("Invariant I1 in global hyp of T1", set1.getPredicates().length == 1 && set1.getPredicates()[0].getContents().equals(invariant1));
		
		IPOPredicateSet set1a = set1.getPredicateSet();
		
		Set<String> hset = contentsSetOf(set1a.getPredicates());
		
		assertTrue("Two predicates", hset.size() == 2);
		
		assertTrue("Axiom A1 in global hyp of T1", hset.contains(ctxAxiom1));

		assertTrue("Theorem X1 in global hyp of T1", hset.contains(ctxTheorem1));

		IPOPredicateSet set2 = sequents[t2].getHypothesis().getGlobalHypothesis();
		
		assertTrue("Theorem T1 in global hyp of T2", set2.getPredicates().length == 1 && set2.getPredicates()[0].getContents().equals(theorem1));
		
		assertEquals("rest of hyp same as for T1", set1, set2.getPredicateSet());

	}		
	
	/**
	 * Test method for guard well-definedness
	 */
	public void testGuard1() throws Exception {
		
		String guard1 = factory.parsePredicate("(∀x·x=1÷x)").getParsedPredicate().toString();
		
		ISCMachine rodinFile = createSCMachine("test");
		addSCEvent(rodinFile, "E1", makeList(), makeList("G1"), makeList(guard1), makeList(), makeList());
		rodinFile.save(null, true);
		IPOFile poFile = runPOG(rodinFile);
		
		IPOSequent[] sequents = poFile.getSequents();
		
		assertTrue("Exactly two proof obligations", sequents.length == 2);
		
		int g1 = getIndexForName("E1/G1/WD", sequents);
		int dlk = getIndexForName("DLK", sequents);
		
		assertTrue("names ok", g1 != -1 && dlk != -1);
		
		assertEquals("WD formula source", "G1", getSourceName(sequents[g1], 0));

		assertEquals("wd predicate ok", getWDStringForPredicate(guard1, null), sequents[g1].getGoal().getContents());
		
		assertEquals("dlk predicate ok", guard1, sequents[dlk].getGoal().getContents());
	}

	/**
	 * Test method for guard well-definedness and hypothesis
	 */
	public void testGuard2() throws Exception {
		
		String invariant1 = factory.parsePredicate("(∀z·z+z>z)").getParsedPredicate().toString();
		String guard1 = factory.parsePredicate("(∀x·x=1÷x)").getParsedPredicate().toString();
		String guard2 = factory.parsePredicate("(∀x·x=(1÷x)−x)").getParsedPredicate().toString();
		String dlk1 = factory.parsePredicate("(∀x·x=1÷x)∧(∀x·x=(1÷x)−x)").getParsedPredicate().toString();
		
		ISCMachine rodinFile = createSCMachine("test");
		addInvariants(rodinFile, makeList("I1"), makeList(invariant1));
		addSCEvent(rodinFile, "E1", makeList(), 
				makeList("G1", "G2"), 
				makeList(guard1, guard2), makeList(), makeList());
		rodinFile.save(null, true);
		IPOFile poFile = runPOG(rodinFile);
		
		IPOSequent[] sequents = poFile.getSequents();
		
		assertTrue("Exactly three proof obligations", sequents.length == 3);
		
		int g1 = getIndexForName("E1/G1/WD", sequents);
		int g2 = getIndexForName("E1/G2/WD", sequents);
		int dlk = getIndexForName("DLK", sequents);
		
		assertTrue("names ok", g1 != -1 && g2 != -1 && dlk != -1);
		
		assertEquals("WD formula source", "G1", getSourceName(sequents[g1], 0 ));
		assertEquals("WD formula source", "G2", getSourceName(sequents[g2], 0));

		assertEquals("wd predicate 1 ok", getWDStringForPredicate(guard1, null), sequents[g1].getGoal().getContents());
		
		assertEquals("wd predicate 2 ok", getWDStringForPredicate(guard2, null), sequents[g2].getGoal().getContents());
		
		assertEquals("wd 2 hypothesis contains guard 1", guard1, sequents[g2].getHypothesis().getLocalHypothesis()[0].getContents());
		
		assertEquals("wd 2 hypothesis contains invariant 1", invariant1, sequents[g2].getHypothesis().getGlobalHypothesis().getPredicates()[0].getContents());
		
		assertEquals("dlk predicate ok", dlk1, sequents[dlk].getGoal().getContents());
	}

	/**
	 * Test method for guard well-definedness and hypothesis with local variable
	 */
	public void testGuard3() throws Exception {
		
		String guard1 = factory.parsePredicate("(x=1÷x)").getParsedPredicate().toString();
		String guard2 = factory.parsePredicate("(x=1−x)").getParsedPredicate().toString();
		String dlk1 = factory.parsePredicate("(∃x·x=1÷x∧x=1−x)").getParsedPredicate().toString();
		
		ISCMachine rodinFile = createSCMachine("test");
		addSCEvent(rodinFile, "E1", makeList("x"), 
				makeList("G1", "G2"), 
				makeList(guard1, guard2), makeList(), makeList("ℤ"));
		rodinFile.save(null, true);
		IPOFile poFile = runPOG(rodinFile);
		
		IPOSequent[] sequents = poFile.getSequents();
		
		assertTrue("Exactly two proof obligations", sequents.length == 2);
		
		int g1 = getIndexForName("E1/G1/WD", sequents);
		int dlk = getIndexForName("DLK", sequents);
		
		assertTrue("names ok", g1 != -1 && dlk != -1);
		
		assertEquals("wd predicate 1 ok", getWDStringForPredicate(guard1, null), sequents[g1].getGoal().getContents());
		
		assertEquals("dlk predicate ok", dlk1, sequents[dlk].getGoal().getContents());
	}

	/**
	 * Test method for guard enabledness
	 */
	public void testGuard4() throws Exception {
		
		String guard1 = factory.parsePredicate("(x=1∗x)").getParsedPredicate().toString();
		String guard2 = factory.parsePredicate("(x=1−x)").getParsedPredicate().toString();
		String dlk1 = factory.parsePredicate("(∃x·x=1∗x∧x=1−x)∨(∃x·x=1+x)").getParsedPredicate().toString();
		String guard3 = factory.parsePredicate("(x=1+x)").getParsedPredicate().toString();
		
		ISCMachine rodinFile = createSCMachine("test");
		addSCEvent(rodinFile, "E1", makeList("x"), 
				makeList("G1", "G2"), 
				makeList(guard1, guard2), makeList(), makeList("ℤ"));
		addSCEvent(rodinFile, "E2", makeList("x"), 
				makeList("G1"), 
				makeList(guard3), makeList(), makeList("ℤ"));
		rodinFile.save(null, true);
		IPOFile poFile = runPOG(rodinFile);
		
		IPOSequent[] sequents = poFile.getSequents();
		
		assertTrue("Exactly one proof obligation", sequents.length == 1);
		
		int dlk = getIndexForName("DLK", sequents);
		
		assertTrue("names ok", dlk != -1);

		assertEquals("dlk predicate ok", dlk1, sequents[dlk].getGoal().getContents());
	}

	/**
	 * Test method for guard hypothesis with theorem
	 */
	public void testGuard5() throws Exception {
		
		String invariant1 = factory.parsePredicate("(∀z·z+z>z)").getParsedPredicate().toString();
		String theorem1 = factory.parsePredicate("(∃p·∀q·q=p∧p>1)").getParsedPredicate().toString();
		String guard1 = factory.parsePredicate("(∀x·x=1÷x)").getParsedPredicate().toString();
		String guard2 = factory.parsePredicate("(∀x·x=(1÷x)−x)").getParsedPredicate().toString();
		String dlk1 = factory.parsePredicate("(∀x·x=1÷x)∧(∀x·x=(1÷x)−x)").getParsedPredicate().toString();
		
		ISCMachine rodinFile = createSCMachine("test");
		addInvariants(rodinFile, makeList("I1"), makeList(invariant1));
		addTheorems(rodinFile, makeList("T1"), makeList(theorem1), null);
		addSCEvent(rodinFile, "E1", makeList(), 
				makeList("G1", "G2"), 
				makeList(guard1, guard2), makeList(), makeList());
		rodinFile.save(null, true);
		IPOFile poFile = runPOG(rodinFile);
		
		IPOSequent[] sequents = poFile.getSequents();
		
		assertTrue("Exactly four proof obligations", sequents.length == 4);
		
		int t1 = getIndexForName("T1", sequents);
		int g1 = getIndexForName("E1/G1/WD", sequents);
		int g2 = getIndexForName("E1/G2/WD", sequents);
		int dlk = getIndexForName("DLK", sequents);
		
		assertTrue("names ok", t1 != -1 && g1 != -1 && g2 != -1 && dlk != -1);
		
		assertEquals("formula source", "T1", getSourceName(sequents[t1], 0));
		assertEquals("WD formula source", "G2", getSourceName(sequents[g2], 0));
		assertEquals("WD formula source", "G1", getSourceName(sequents[g1], 0));

		assertEquals("wd predicate 1 ok", getWDStringForPredicate(guard1, null), sequents[g1].getGoal().getContents());
		
		assertEquals("wd predicate 2 ok", getWDStringForPredicate(guard2, null), sequents[g2].getGoal().getContents());
		
		assertEquals("wd 2 hypothesis contains guard 1", guard1, 
				sequents[g2].getHypothesis().getLocalHypothesis()[0].getContents());
		
		assertEquals("wd 2 hypothesis contains theorem 1", theorem1, 
				sequents[g2].getHypothesis().getGlobalHypothesis().getPredicates()[0].getContents());
		
		assertEquals("wd 2 hypothesis contains invariant 1", invariant1, 
				sequents[g2].getHypothesis().getGlobalHypothesis().getPredicateSet().getPredicates()[0].getContents());
		
		assertEquals("dlk predicate ok", dlk1, sequents[dlk].getGoal().getContents());
	}

	/**
	 * Test method for guard deadlock freeness
	 */
	public void testGuard6() throws Exception {
		
		String guard1 = factory.parsePredicate("x∈ℕ").getParsedPredicate().toString();
		String dlk1 = factory.parsePredicate("(∃x·x∈ℕ)").getParsedPredicate().toString();
		
		ISCMachine rodinFile = createSCMachine("test");
		addSCEvent(rodinFile, "E",
				makeList("x"), 
				makeList("G"), 
				makeList(guard1),
				makeList(),
				makeList("ℤ"));
		rodinFile.save(null, true);
		IPOFile poFile = runPOG(rodinFile);
		
		IPOSequent[] sequents = poFile.getSequents();
		
		assertTrue("number of proof obligations", sequents.length == 1);
		
		int dlk = getIndexForName("DLK", sequents);
		assertTrue("names ok", dlk != -1);
		assertEquals("dlk predicate ok", dlk1, sequents[dlk].getGoal().getContents());
	}

	/**
	 * Test method for independence of type environments of local variables
	 */
	public void testLocalVariables1() throws Exception {
		
		String guard1 = factory.parsePredicate("x∈ℕ").getParsedPredicate().toString();
		String guard2 = factory.parsePredicate("x∈BOOL").getParsedPredicate().toString();
		String dlk1 = factory.parsePredicate("(∃x·x∈ℕ) ∨ (∃x·x∈BOOL)").getParsedPredicate().toString();	
		
		ISCMachine rodinFile = createSCMachine("test");
		addSCEvent(rodinFile, "E",
				makeList("x"), 
				makeList("G"), 
				makeList(guard1),
				makeList(),
				makeList("ℤ"));
		addSCEvent(rodinFile, "F",
				makeList("x"), 
				makeList("G"), 
				makeList(guard2),
				makeList(),
				makeList("BOOL"));
		rodinFile.save(null, true);
		IPOFile poFile = runPOG(rodinFile);
		
		IPOSequent[] sequents = poFile.getSequents();
		
		assertTrue("number of proof obligations", sequents.length == 1);
		
		int dlk = getIndexForName("DLK", sequents);
		assertTrue("names ok", dlk != -1);
		assertEquals("dlk predicate ok", dlk1, sequents[dlk].getGoal().getContents());
	}

	/**
	 * Test method for action without guard (well-definedness, feasility, invariant)
	 */
	public void testAction1() throws Exception {
		
		ITypeEnvironment env = factory.makeTypeEnvironment();
		env.addName("x", INTEGER);
		env.addName("y", INTEGER);
		env.addName("z", INTEGER);
		
		String invariant1 = factory.parsePredicate("x∈ℕ").getParsedPredicate().toString();
		String invariant2 = factory.parsePredicate("y∈ℕ").getParsedPredicate().toString();
		String invariant3 = factory.parsePredicate("z∈ℕ").getParsedPredicate().toString();
		String assignment1 = factory.parseAssignment("x≔y mod z").getParsedAssignment().toString();
		String assignment2 = factory.parseAssignment("y:∈{x÷z}").getParsedAssignment().toString();
		String assignment3 = factory.parseAssignment("z :∣ ∃p·p<z'∧p÷z=1").getParsedAssignment().toString();
	
		ISCMachine rodinFile = createSCMachine("test");
		addInvariants(rodinFile, makeList("I1", "I2", "I3"), makeList(invariant1, invariant2, invariant3));
		addSCVariables(rodinFile, makeList("x", "y", "z"), makeList("ℤ", "ℤ", "ℤ"));
		addSCEvent(rodinFile, "E1", makeList(), 
				makeList(), makeList(), makeList(assignment1, assignment2, assignment3), makeList());
		rodinFile.save(null, true);
		
		IPOFile poFile = runPOG(rodinFile);
		
		IPOSequent[] sequents = poFile.getSequents();
		
		assertTrue("Exactly 8 proof obligations", sequents.length == 8);
		
		int a1wd = getIndexForName("E1/x/WD", sequents);
		int a2wd = getIndexForName("E1/y/WD", sequents);
		int a3wd = getIndexForName("E1/z/WD", sequents);
		int a2fis = getIndexForName("E1/y/FIS", sequents);
		int a3fis = getIndexForName("E1/z/FIS", sequents);
		int i1 = getIndexForName("E1/I1/INV", sequents);
		int i2 = getIndexForName("E1/I2/INV", sequents);
		int i3 = getIndexForName("E1/I3/INV", sequents);
		
		assertTrue("names ok", a1wd != -1 && a2wd != -1 && a3wd != -1 && a2fis != -1 && a3fis != -1 && i1 != -1 && i2 != -1 && i3 != -1);
		
		int ie1 = getIndexForName("event", sequents[i1].getDescription().getSources());
		int ie2 = getIndexForName("event", sequents[i2].getDescription().getSources());
		int ie3 = getIndexForName("event", sequents[i3].getDescription().getSources());
		
		assertEquals("formula source", "E1", getSourceName(sequents[i1], ie1));
		assertEquals("formula source", "E1", getSourceName(sequents[i2], ie2));
		assertEquals("formula source", "E1", getSourceName(sequents[i3], ie3));

		assertEquals("formula source", "I1", getSourceName(sequents[i1], 1-ie1));
		assertEquals("formula source", "I2", getSourceName(sequents[i2], 1-ie2));
		assertEquals("formula source", "I3", getSourceName(sequents[i3], 1-ie3));
		
		assertEquals("formula source", assignment1, getSourceContents(sequents[a1wd], 0));
		assertEquals("formula source", assignment2, getSourceContents(sequents[a2wd], 0));
		assertEquals("formula source", assignment2, getSourceContents(sequents[a2fis], 0));
		assertEquals("formula source", assignment3, getSourceContents(sequents[a3fis], 0));

		assertEquals("wd predicate 1 ok", getWDStringForAssignment(assignment1, env), sequents[a1wd].getGoal().getContents());
		assertEquals("wd predicate 2 ok", getWDStringForAssignment(assignment2, env), sequents[a2wd].getGoal().getContents());
		assertEquals("wd predicate 3 ok", getWDStringForAssignment(assignment3, env), sequents[a3wd].getGoal().getContents());
		
		assertEquals("fis predicate 2 ok", getFISStringForAssignment(assignment2, env), sequents[a2fis].getGoal().getContents());
		assertEquals("fis predicate 3 ok", getFISStringForAssignment(assignment3, env), sequents[a3fis].getGoal().getContents());
	
		
		assertEquals("i1 goal ok s", assignment1, ((IPOModifiedPredicate) sequents[i1].getGoal()).getSubstitution());
		assertEquals("i1 goal ok p", invariant1, ((IPOModifiedPredicate) sequents[i1].getGoal()).getPredicate().getContents());
		
		assertEquals("i2 hypothesis", getBAStringForAssignment(assignment2, env), 
				sequents[i2].getHypothesis().getLocalHypothesis()[0].getContents());
		String assignment4 = factory.parseAssignment("y≔y'").getParsedAssignment().toString();
		assertEquals("i2 goal ok s", assignment4, ((IPOModifiedPredicate) sequents[i2].getGoal()).getSubstitution());
		assertEquals("i2 goal ok p", invariant2, ((IPOModifiedPredicate) sequents[i2].getGoal()).getPredicate().getContents());
		
		assertEquals("i3 hypothesis", getBAStringForAssignment(assignment3, env), 
				sequents[i3].getHypothesis().getLocalHypothesis()[0].getContents());
		String assignment5 = factory.parseAssignment("z≔z'").getParsedAssignment().toString();
		assertEquals("i3 goal ok s", assignment5, ((IPOModifiedPredicate) sequents[i3].getGoal()).getSubstitution());
		assertEquals("i3 goal ok p", invariant3, ((IPOModifiedPredicate) sequents[i3].getGoal()).getPredicate().getContents());
		
	}

	/**
	 * Test method for action without guard (well-definedness, feasility, invariant)
	 */
	public void testAction2() throws Exception {
		
		ITypeEnvironment env = factory.makeTypeEnvironment();
		env.addName("x", INTEGER);
		env.addName("y", INTEGER);
		env.addName("z", INTEGER);
		
		String axiom1 = factory.parsePredicate("(∀p·p∈ℤ ⇒ p∈ℕ)").getParsedPredicate().toString();
		String theorem1 = factory.parsePredicate("(∃w·w∈ℕ∧w=w)").getParsedPredicate().toString();
		String invariant1 = factory.parsePredicate("x∈ℕ").getParsedPredicate().toString();
		String invariant2 = factory.parsePredicate("y∈ℕ").getParsedPredicate().toString();
		String invariant3 = factory.parsePredicate("z∈ℕ").getParsedPredicate().toString();
		String theorem2 = factory.parsePredicate("(∀b·b>z)").getParsedPredicate().toString();
		String assignment1 = factory.parseAssignment("x≔y mod z").getParsedAssignment().toString();
		String assignment2 = factory.parseAssignment("y:∈{x÷z}").getParsedAssignment().toString();
		String assignment3 = factory.parseAssignment("z:∣∃p·p<z'∧p÷z=1").getParsedAssignment().toString();
	
		ISCMachine rodinFile = createSCMachine("test");
		addAxioms(rodinFile, makeList("A1"), makeList(axiom1), "CONTEXT");
		addTheorems(rodinFile, makeList("T1"), makeList(theorem1), "CONTEXT");
		addInvariants(rodinFile, makeList("I1", "I2", "I3"), makeList(invariant1, invariant2, invariant3));
		addTheorems(rodinFile, makeList("T2"), makeList(theorem2), null);
		addSCVariables(rodinFile, makeList("x", "y", "z"), makeList("ℤ", "ℤ", "ℤ"));
		addSCEvent(rodinFile, "E1", makeList(), 
				makeList(), makeList(), makeList(assignment1, assignment2, assignment3), makeList());
		rodinFile.save(null, true);
		IPOFile poFile = runPOG(rodinFile);
		
		IPOSequent[] sequents = poFile.getSequents();
		
		assertTrue("Exactly 9 proof obligations", sequents.length == 9);
		
		int t2 = getIndexForName("T2", sequents);
		int a1wd = getIndexForName("E1/x/WD", sequents);
		int a2wd = getIndexForName("E1/y/WD", sequents);
		int a3wd = getIndexForName("E1/z/WD", sequents);
		int a2fis = getIndexForName("E1/y/FIS", sequents);
		int a3fis = getIndexForName("E1/z/FIS", sequents);
		int i1 = getIndexForName("E1/I1/INV", sequents);
		int i2 = getIndexForName("E1/I2/INV", sequents);
		int i3 = getIndexForName("E1/I3/INV", sequents);
		
		assertTrue("names ok", t2 != -1 && a1wd != -1 && a2wd != -1 && a3wd != -1 && a2fis != -1 && a3fis != -1 && i1 != -1 && i2 != -1 && i3 != -1);
		
		assertEquals("wd predicate 1 ok", getWDStringForAssignment(assignment1, env), sequents[a1wd].getGoal().getContents());
		assertEquals("wd hypothesis 1 ok", theorem2, sequents[a1wd].getHypothesis().getGlobalHypothesis().getPredicates()[0].getContents());
		
		assertEquals("fis predicate 2 ok", getFISStringForAssignment(assignment2, env), sequents[a2fis].getGoal().getContents());
		assertEquals("fis hypothesis 2 ok", theorem2, sequents[a2fis].getHypothesis().getGlobalHypothesis().getPredicates()[0].getContents());
	
		
		assertEquals("i1 goal ok s", assignment1, ((IPOModifiedPredicate) sequents[i1].getGoal()).getSubstitution());
		assertEquals("i1 goal ok p", invariant1, ((IPOModifiedPredicate) sequents[i1].getGoal()).getPredicate().getContents());
		assertEquals("i1 hypothesis ok", theorem2, sequents[i1].getHypothesis().getGlobalHypothesis().getPredicates()[0].getContents());
	
	}

	/**
	 * Test method for action without guard (well-definedness, feasility, invariant)
	 */
	public void testInitialisation1() throws Exception {
		
		ITypeEnvironment env = factory.makeTypeEnvironment();
		env.addName("x", INTEGER);
		env.addName("y", INTEGER);
		env.addName("z", INTEGER);
		
		String axiom1 = factory.parsePredicate("(∀p·p∈ℤ ⇒ p∈ℕ)").getParsedPredicate().toString();
		String theorem1 = factory.parsePredicate("(∃w·w∈ℕ∧w=w)").getParsedPredicate().toString();
		String invariant1 = factory.parsePredicate("x∈ℕ").getParsedPredicate().toString();
		String invariant2 = factory.parsePredicate("y∈ℕ").getParsedPredicate().toString();
		String invariant3 = factory.parsePredicate("z∈ℕ").getParsedPredicate().toString();
		String theorem2 = factory.parsePredicate("(∀b·b>z)").getParsedPredicate().toString();
		String assignment1 = factory.parseAssignment("x≔y mod z").getParsedAssignment().toString();
		String assignment2 = factory.parseAssignment("y:∈{x÷z}").getParsedAssignment().toString();
		String assignment3 = factory.parseAssignment("z:∣∃p·p<z'∧p÷z=1").getParsedAssignment().toString();
	
		ISCMachine rodinFile = createSCMachine("test");
		addAxioms(rodinFile, makeList("A1"), makeList(axiom1), "CONTEXT");
		addTheorems(rodinFile, makeList("T1"), makeList(theorem1), "CONTEXT");
		addInvariants(rodinFile, makeList("I1", "I2", "I3"), makeList(invariant1, invariant2, invariant3));
		addTheorems(rodinFile, makeList("T2"), makeList(theorem2), null);
		addIdentifiers(rodinFile, makeList("x", "y", "z"), makeList("ℤ", "ℤ", "ℤ"));
		addSCEvent(rodinFile, "INITIALISATION", makeList(), 
				makeList(), makeList(), makeList(assignment1, assignment2, assignment3), makeList());
		rodinFile.save(null, true);
		
		IPOFile poFile = runPOG(rodinFile);
		
		IPOSequent[] sequents = poFile.getSequents();
		
		assertTrue("Exactly 9 proof obligations", sequents.length == 9);
		
		int t2 = getIndexForName("T2", sequents);
		int a1wd = getIndexForName("INITIALISATION/x/WD", sequents);
		int a2wd = getIndexForName("INITIALISATION/y/WD", sequents);
		int a3wd = getIndexForName("INITIALISATION/z/WD", sequents);
		int a2fis = getIndexForName("INITIALISATION/y/FIS", sequents);
		int a3fis = getIndexForName("INITIALISATION/z/FIS", sequents);
		int i1 = getIndexForName("INITIALISATION/I1/INV", sequents);
		int i2 = getIndexForName("INITIALISATION/I2/INV", sequents);
		int i3 = getIndexForName("INITIALISATION/I3/INV", sequents);
		
		assertTrue("names ok", t2 != -1 && a1wd != -1 && a2wd != -1 && a3wd != -1 && a2fis != -1 && a3fis != -1 && i1 != -1 && i2 != -1 && i3 != -1);
		
		assertEquals("wd predicate 1 ok", getWDStringForAssignment(assignment1, env), sequents[a1wd].getGoal().getContents());
		Set<String> hyp = contentsSetOf(sequents[a1wd].getHypothesis().getGlobalHypothesis().getPredicates());
		assertTrue("wd hypothesis 1 ok", hyp.contains(theorem1));
		assertTrue("wd hypothesis 1 ok", !hyp.contains(theorem2));
		assertEquals("wd predicate 2 ok", getWDStringForAssignment(assignment2, env), sequents[a2wd].getGoal().getContents());
		assertEquals("wd predicate 3 ok", getWDStringForAssignment(assignment3, env), sequents[a3wd].getGoal().getContents());
		
		assertEquals("fis predicate 2 ok", getFISStringForAssignment(assignment2, env), sequents[a2fis].getGoal().getContents());
		hyp = contentsSetOf(sequents[a2fis].getHypothesis().getGlobalHypothesis().getPredicates());
		assertTrue("wd hypothesis 1 ok", hyp.contains(theorem1));
		assertTrue("wd hypothesis 1 ok", !hyp.contains(theorem2));	
		assertEquals("fis predicate 3 ok", getFISStringForAssignment(assignment3, env), sequents[a3fis].getGoal().getContents());
		
		assertEquals("i1 goal ok s", assignment1, ((IPOModifiedPredicate) sequents[i1].getGoal()).getSubstitution());
		assertEquals("i1 goal ok p", invariant1, ((IPOModifiedPredicate) sequents[i1].getGoal()).getPredicate().getContents());
		hyp = contentsSetOf(sequents[i1].getHypothesis().getGlobalHypothesis().getPredicates());
		assertTrue("wd hypothesis 1 ok", hyp.contains(theorem1));
		assertTrue("wd hypothesis 1 ok", !hyp.contains(theorem2));	

		assertEquals("i2 hypothesis", getBAStringForAssignment(assignment2, env), 
				sequents[i2].getHypothesis().getLocalHypothesis()[0].getContents());
		String assignment4 = factory.parseAssignment("y≔y'").getParsedAssignment().toString();
		assertEquals("i2 goal ok s", assignment4, ((IPOModifiedPredicate) sequents[i2].getGoal()).getSubstitution());
		assertEquals("i2 goal ok p", invariant2, ((IPOModifiedPredicate) sequents[i2].getGoal()).getPredicate().getContents());
		
		assertEquals("i3 hypothesis", getBAStringForAssignment(assignment3, env), 
				sequents[i3].getHypothesis().getLocalHypothesis()[0].getContents());
		String assignment5 = factory.parseAssignment("z≔z'").getParsedAssignment().toString();
		assertEquals("i3 goal ok s", assignment5, ((IPOModifiedPredicate) sequents[i3].getGoal()).getSubstitution());
		assertEquals("i3 goal ok p", invariant3, ((IPOModifiedPredicate) sequents[i3].getGoal()).getPredicate().getContents());
		
	}

	/**
	 * Test method for action without guard (well-definedness, feasility, invariant)
	 */
	public void testVariables1() throws Exception {
		
		String invariant1 = factory.parsePredicate("x∈ℕ").getParsedPredicate().toString();
		String invariant2 = factory.parsePredicate("y∈BOOL").getParsedPredicate().toString();
		String invariant3 = factory.parsePredicate("z∈{0}×{0}").getParsedPredicate().toString();
	
		ISCMachine rodinFile = createSCMachine("test");
		addInvariants(rodinFile, makeList("I1", "I2", "I3"), makeList(invariant1, invariant2, invariant3));
		addSCVariables(rodinFile, makeList("x", "y", "z"), makeList("ℤ", "BOOL", "ℤ×ℤ"));
		rodinFile.save(null, true);
		
		IPOFile poFile = runPOG(rodinFile);
		
		IPOIdentifier[] identifiers = poFile.getIdentifiers();
		
		assertTrue("6 identifiers", identifiers.length == 6);
		
		HashMap<Character, String> types = new HashMap<Character, String>(5);
		
		
		HashSet<String> expected = new HashSet<String>(11);
		expected.add("x");
		expected.add("y");
		expected.add("z");
		expected.add("x'");
		expected.add("y'");
		expected.add("z'");
		for(IPOIdentifier identifier : identifiers) {
			Character cc = identifier.getName().charAt(0);
			String type = types.get(cc);
			if(type == null)
				types.put(cc, identifier.getType());
			else
				assertEquals("same type: " + cc, type, identifier.getType());
			expected.remove(identifier.getElementName());
		}
		assertTrue("0 identifiers", expected.size() == 0);
	}

}
