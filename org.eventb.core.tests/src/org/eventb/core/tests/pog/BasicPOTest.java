/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.tests.pog;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.eventb.core.IContextFile;
import org.eventb.core.IMachineFile;
import org.eventb.core.IPOFile;
import org.eventb.core.IPOIdentifier;
import org.eventb.core.IPOPredicate;
import org.eventb.core.IPOPredicateSet;
import org.eventb.core.IPOSequent;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Type;
import org.eventb.core.tests.EventBTest;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

/**
 * @author Stefan Hallerstede
 *
 */
public abstract class BasicPOTest extends EventBTest {

	public final Type intType = factory.makeIntegerType();
	public final Type boolType = factory.makeBooleanType();
	public final Type powIntType = factory.makePowerSetType(intType);

	public Set<String> getElementNameSet(IRodinElement[] elements) throws RodinDBException {
		HashSet<String> names = new HashSet<String>(elements.length * 4 / 3 + 1);
		for(IRodinElement element : elements)
			names.add(element.getElementName());
		return names;
	}

	public static final String ALLHYP_NAME = "ALLHYP";
	
	public void getIdentifiersFromPredSets(
			Set<String> nameSet, 
			IPOFile file, 
			IPOPredicateSet predicateSet, 
			boolean forSequent) throws RodinDBException {

		assertTrue("predicate set should exist", predicateSet.exists());
		
		if (forSequent && predicateSet.getElementName().equals(ALLHYP_NAME))
			return;
		for (IPOIdentifier identifier : predicateSet.getIdentifiers()) {
			nameSet.add(identifier.getIdentifierString());
		}
		IPOPredicateSet parentPredicateSet = predicateSet.getParentPredicateSet();
		if (parentPredicateSet == null)
			return;
		else
			getIdentifiersFromPredSets(nameSet, file, parentPredicateSet, forSequent);
	}
	
	public void containsIdentifiers(IPOFile file, String... strings) throws RodinDBException {
		
		Set<String> nameSet = new HashSet<String>(43);
		
		getIdentifiersFromPredSets(nameSet, file, file.getPredicateSet(ALLHYP_NAME), false);
		
		assertEquals("wrong number of identifiers", strings.length, nameSet.size());
		if (strings.length == 0)
			return;
		
	
		for (String string : strings)
			assertTrue("should contain " + string, nameSet.contains(string));
	}
	
	public void sequentHasIdentifiers(IPOSequent sequent, String... strings) throws RodinDBException {
		
		IPOPredicateSet predicateSet = sequent.getHypotheses()[0].getParentPredicateSet();
		
		Set<String> nameSet = new HashSet<String>(43);
		
		IPOFile file = (IPOFile) sequent.getOpenable();
		
		getIdentifiersFromPredSets(nameSet, file, predicateSet, true);
		
		assertEquals("wrong number of identifiers", strings.length, nameSet.size());
		
		if (strings.length == 0)
			return;
		
		for (String string : strings)
			assertTrue("should contain " + string, nameSet.contains(string));
	}

	public List<IPOSequent> getSequents(IPOFile file, String... strings) {
		LinkedList<IPOSequent> sequents = new LinkedList<IPOSequent>();
		
		for (String string : strings)
			sequents.add(getSequent(file, string));
		
		return sequents;
	}
	
	public IPOSequent getSequent(IPOFile file, String name) {
		IPOSequent sequent = (IPOSequent) file.getInternalElement(IPOSequent.ELEMENT_TYPE, name);
		
		assertTrue("sequent should exist: " + name, sequent.exists());
		
		return sequent;
	}
	
	public void noSequent(IPOFile file, String name) {
		IPOSequent sequent = (IPOSequent) file.getInternalElement(IPOSequent.ELEMENT_TYPE, name);
		
		assertFalse("sequent should not exist", sequent.exists());
	}
	
	public void sequentHasGoal(
			IPOSequent sequent, 
			ITypeEnvironment typeEnvironment, 
			String predicate) throws Exception {
		String string = getNormalizedPredicate(predicate, typeEnvironment);
		assertEquals("goal should be " + string, string, 
				sequent.getGoals()[0].getPredicateString());
	}
	
	private HashSet<String> getPredicatesFromSets(IPOPredicateSet predicateSet) throws Exception {
		HashSet<String> result = new HashSet<String>(4047);
		IPOPredicateSet set = predicateSet;
		while (set != null) {
			IPOPredicate[] predicates = set.getPredicates();
			for (IPOPredicate predicate : predicates) {
				result.add(predicate.getPredicateString());
			}
			set = set.getParentPredicateSet();
		}
		return result;
	}
	
	public void sequentHasHypotheses(
			IPOSequent sequent, 
			ITypeEnvironment typeEnvironment, 
			String... strings) throws Exception {
		
		IPOPredicateSet predicateSet = sequent.getHypotheses()[0];
		
		HashSet<String> predicates = getPredicatesFromSets(predicateSet);
		
		for (String string : strings) {
			
			assertTrue("should have hypothesis " + string, 
					predicates.contains(getNormalizedPredicate(string, typeEnvironment)));
			
		}
	}

	public void sequentHasNotHypotheses(
			IPOSequent sequent, 
			ITypeEnvironment typeEnvironment, 
			String... strings) throws Exception {
		
		IPOPredicateSet predicateSet = sequent.getHypotheses()[0];
		
		HashSet<String> predicates = getPredicatesFromSets(predicateSet);
		
		for (String string : strings) {
			
			assertFalse("should not have hypothesis " + string, 
					predicates.contains(getNormalizedPredicate(string, typeEnvironment)));
			
		}
	}

	public void sequentHasNoHypotheses(IPOSequent sequent) throws Exception {
		
		IPOPredicateSet predicateSet = sequent.getHypotheses()[0];
		
		HashSet<String> predicates = getPredicatesFromSets(predicateSet);
		
		assertEquals("hypthesis should be empty", 0, predicates.size());
		
	}

	public IPOFile getPOFile(IContextFile rodinFile) throws RodinDBException {
		return rodinFile.getPOFile();
	}
	
	public IPOFile getPOFile(IMachineFile rodinFile) throws RodinDBException {
		return rodinFile.getPOFile();
	}

	public Type given(String s) {
		return factory.makeGivenType(s);
	}
	
}
