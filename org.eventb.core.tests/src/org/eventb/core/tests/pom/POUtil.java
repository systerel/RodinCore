/*******************************************************************************
 * Copyright (c) 2005 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eventb.core.tests.pom;

import org.eventb.core.IPOIdentifier;
import org.eventb.core.IPOPredicate;
import org.eventb.core.IPOPredicateSet;
import org.eventb.core.IPOSequent;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalParent;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.RodinDBException;

/**
 * @author halstefa
 *
 */
public class POUtil {

	// Functions to write PO files
	
	public static void addTypes(IInternalParent parent, String[] names, String[] types) throws RodinDBException {
		for(int i=0; i<names.length; i++) {
			IInternalElement element = parent.createInternalElement(IPOIdentifier.ELEMENT_TYPE, names[i], null, null);
			element.setContents(types[i]);
		}
	}
	
	public static void addPredicateSet(IRodinFile file, String name, String[] predicates, String parentSet) throws RodinDBException {
		IPOPredicateSet parent = (IPOPredicateSet) file.createInternalElement(IPOPredicateSet.ELEMENT_TYPE, name, null, null);
		if(parentSet != null)
			parent.setContents(parentSet);
		int idx = 1;
		for(int i=0; i<predicates.length; i++) {
			name = "p" + idx++;
			IInternalElement element = parent.createInternalElement(IPOPredicate.ELEMENT_TYPE, name, null, null);
			element.setContents(predicates[i]);
		}
	}
	
	public static void addSequent(IRodinFile file, 
			String poName,
			String globalHypothesis, 
			String[] localNames,
			String[] localTypes,
			String[] localHypothesis,
			String goal) throws RodinDBException {
		IPOSequent sequent = (IPOSequent) file.createInternalElement(IPOSequent.ELEMENT_TYPE, poName, null, null);
		addTypes(sequent, localNames, localTypes);
		addHypothesis(sequent, globalHypothesis, localHypothesis);
		addPredicate("goal", sequent,goal);
	}
	
	private static void addHypothesis(IPOSequent sequent, 
			String globalHypothesis, 
			String[] localHypothesis) throws RodinDBException {
		IPOPredicateSet hypothesis = (IPOPredicateSet) sequent.createInternalElement(IPOPredicateSet.ELEMENT_TYPE, "glob-hyp", null, null);
		hypothesis.setContents(globalHypothesis);
		int idx = 1;
		for(int i=0; i<localHypothesis.length; i++) {
			addPredicate("p" + idx++, hypothesis, localHypothesis[i]);
		}
	}
	
	private static void addPredicate(String name, IInternalParent internalParent, String predicate) throws RodinDBException {
		IInternalElement element = internalParent.createInternalElement(IPOPredicate.ELEMENT_TYPE, name, null, null);
		element.setContents(predicate);
	}

}
