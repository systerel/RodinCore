/*******************************************************************************
 * Copyright (c) 2006, 2012 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - separation of file and root element
 *******************************************************************************/
package org.eventb.core.tests.pog;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.eventb.core.IContextRoot;
import org.eventb.core.IEventBRoot;
import org.eventb.core.IMachineRoot;
import org.eventb.core.IPOIdentifier;
import org.eventb.core.IPOPredicate;
import org.eventb.core.IPOPredicateSet;
import org.eventb.core.IPORoot;
import org.eventb.core.IPOSelectionHint;
import org.eventb.core.IPOSequent;
import org.eventb.core.IPOStampedElement;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.tests.EventBTest;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

/**
 * @author Stefan Hallerstede
 *
 */
public abstract class EventBPOTest extends EventBTest {

	private static final String FWD_CONFIG = "org.eventb.core.fwd";

	@Override
	protected IContextRoot createContext(String bareName) throws RodinDBException {
		IContextRoot root = super.createContext(bareName);
		root.setConfiguration(FWD_CONFIG, null);
		addRoot(root.getPORoot());
		return root;
	}

	@Override
	protected IMachineRoot createMachine(String bareName) throws RodinDBException {
		IMachineRoot root = super.createMachine(bareName);
		root.setConfiguration(FWD_CONFIG, null);
		addRoot(root.getPORoot());
		return root;
	}

	public Set<String> getElementNameSet(IRodinElement[] elements) throws RodinDBException {
		HashSet<String> names = new HashSet<String>(elements.length * 4 / 3 + 1);
		for(IRodinElement element : elements)
			names.add(element.getElementName());
		return names;
	}

	public static final String ALLHYP_NAME = "ALLHYP";
	
	public void getIdentifiersFromPredSets(
			Set<String> nameSet, 
			IPORoot root, 
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
			getIdentifiersFromPredSets(nameSet, root, parentPredicateSet, forSequent);
	}
	
	public void containsIdentifiers(IPORoot root, String... strings) throws RodinDBException {
		
		Set<String> nameSet = new HashSet<String>(43);
		
		getIdentifiersFromPredSets(nameSet, root, root.getPredicateSet(ALLHYP_NAME), false);
		
		assertEquals("wrong number of identifiers", strings.length, nameSet.size());
		if (strings.length == 0)
			return;
		
	
		for (String string : strings)
			assertTrue("should contain " + string, nameSet.contains(string));
	}
	
	public void sequentHasIdentifiers(IPOSequent sequent, String... strings) throws RodinDBException {
		
		IPOPredicateSet predicateSet = sequent.getHypotheses()[0].getParentPredicateSet();
		
		Set<String> nameSet = new HashSet<String>(43);
		
		IPORoot root = (IPORoot) sequent.getRoot();
		
		getIdentifiersFromPredSets(nameSet, root, predicateSet, true);
		
		assertEquals("wrong number of identifiers", strings.length, nameSet.size());
		
		if (strings.length == 0)
			return;
		
		for (String string : strings)
			assertTrue("should contain " + string, nameSet.contains(string));
	}

	public List<IPOSequent> getSequents(IPORoot root, String... strings) {
		LinkedList<IPOSequent> sequents = new LinkedList<IPOSequent>();
		
		for (String string : strings)
			sequents.add(getSequent(root, string));
		
		return sequents;
	}
	
	public IPOSequent getSequent(IPORoot root, String name) {
		IPOSequent sequent = root.getInternalElement(IPOSequent.ELEMENT_TYPE, name);
		
		assertTrue("sequent should exist: " + name, sequent.exists());
		
		return sequent;
	}
	
	public List<IPOSequent> getExactSequents(IPORoot root, String... strings)
			throws RodinDBException {
		assertEquals(strings.length, root.getSequents().length);
		return getSequents(root, strings);
	}
		
	public void noSequent(IPORoot root, String name) {
		IPOSequent sequent = root.getInternalElement(IPOSequent.ELEMENT_TYPE, name);

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
	
	public void sequentHasSelectionHints(
			IPOSequent sequent, 
			ITypeEnvironment typeEnvironment, 
			String... predicates) throws Exception {
		HashSet<String> selected = getSelectionHints(sequent);
		
		for (String predicate : predicates) {
			String string = getNormalizedPredicate(predicate, typeEnvironment);
			assertTrue("hints should contain " + string, selected.contains(string));
		}
	}
	
	public void sequentHasNotSelectionHints(
			IPOSequent sequent, 
			ITypeEnvironment typeEnvironment, 
			String... predicates) throws Exception {
		HashSet<String> selected = getSelectionHints(sequent);
		
		for (String predicate : predicates) {
			String string = getNormalizedPredicate(predicate, typeEnvironment);
			assertFalse("hints should not contain " + string, selected.contains(string));
		}
	}
	
	private HashSet<String> getSelectionHints(IPOSequent sequent) throws Exception {
		IPOSelectionHint[] selectionHints = sequent.getSelectionHints();
		HashSet<String> set = new HashSet<String>(selectionHints.length * 17 / 3 + 1);
		for (IPOSelectionHint selectionHint : selectionHints) {
			IPOPredicateSet endP = selectionHint.getEnd();
			if (endP == null) {
				IPOPredicate pred = selectionHint.getPredicate();
				set.add(pred.getPredicateString());
			} else {
				IPOPredicateSet startP = selectionHint.getStart();
				while (!endP.equals(startP)) {
					IPOPredicate[] preds = endP.getPredicates();
					for (IPOPredicate predicate : preds) {
						set.add(predicate.getPredicateString());
					}
					endP = endP.getParentPredicateSet();
				}
			}
		}
		return set;
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

	public void sequentHasExactlyHypotheses(IPOSequent sequent,
			ITypeEnvironment typeEnvironment, String... strings)
			throws Exception {
		final IPOPredicateSet predicateSet = sequent.getHypotheses()[0];
		final Set<String> expected = new HashSet<String>();
		for (final String string : strings) {
			expected.add(getNormalizedPredicate(string, typeEnvironment));
		}
		final Set<String> actual = getPredicatesFromSets(predicateSet);
		assertEquals(expected, actual);
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

	public IPORoot getPOFile(IMachineRoot root) throws RodinDBException {
		return root.getPORoot();
	}

	public void sequentIsAccurate(IPORoot po, String name) throws Exception {
		IPOSequent seq = getSequent(po, name);
		assertTrue("sequent should be accurate", seq.isAccurate());
	}

	public void sequentIsNotAccurate(IPORoot po, String name) throws Exception {
		IPOSequent seq = getSequent(po, name);
		assertFalse("sequent should not be accurate", seq.isAccurate());
	}
	
	public void hasStamp(IPOStampedElement stampedElem, long value) throws Exception {
		long stamp = stampedElem.getPOStamp();
		assertEquals("Unexpected stamp", value, stamp);
	}
	
	public void hasNewStamp(IInternalElement stampedElem) throws Exception {
		long value = ((IPORoot) stampedElem.getRoot()).getPOStamp();
		hasStamp((IPOStampedElement) stampedElem, value);
	}

	/**
	 * Prints the names of all the sequents of an Event-B component. Useful for
	 * debugging purposes.
	 * 
	 * @param root
	 *            the root of some Event-B component
	 * @throws RodinDBException
	 *             in case of error accessing the Rodin database
	 */
	public static void printSequents(IEventBRoot root) throws RodinDBException {
		System.out.println(root.getComponentName() + ":\n");
		final IPOSequent[] sequents = root.getPORoot().getSequents();
		for (IPOSequent seq : sequents) {
			System.out.println("\t" + seq.getElementName());
		}
	}

}
