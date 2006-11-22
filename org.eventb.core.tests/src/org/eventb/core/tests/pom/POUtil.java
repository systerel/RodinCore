/*******************************************************************************
 * Copyright (c) 2005 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eventb.core.tests.pom;

import java.util.NoSuchElementException;

import org.eventb.core.IPOFile;
import org.eventb.core.IPOIdentifier;
import org.eventb.core.IPOPredicate;
import org.eventb.core.IPOPredicateSet;
import org.eventb.core.IPOSequent;
import org.eventb.core.ast.ITypeEnvironment;
import org.rodinp.core.RodinDBException;

/**
 * Utility methods for writing a PO file.
 * 
 * @author halstefa
 * @author Laurent Voisin
 */
public class POUtil {

	/**
	 * Adds a predicate set to the given PO file, using the given contents.
	 * 
	 * @param poFile
	 *            file in which to add the predicate set
	 * @param setName
	 *            name of the set
	 * @param parentSet
	 *            parent set (may be <code>null</code>)
	 * @param typEnv
	 *            type environment for the set
	 * @param predStrings
	 *            predicates of the set as strings
	 * @return a handle to the created set
	 * @throws RodinDBException
	 */
	public static IPOPredicateSet addPredicateSet(IPOFile poFile, String setName,
			IPOPredicateSet parentSet, ITypeEnvironment typEnv,
			String... predStrings) throws RodinDBException {

		IPOPredicateSet poSet = poFile.getPredicateSet(setName);
		createPredicateSet(poSet, parentSet, typEnv, predStrings);
		return poSet;
	}

	/**
	 * Adds a PO to the given PO file with the supplied information.
	 * 
	 * @param poFile
	 *            file where to create the PO
	 * @param poName
	 *            name of the PO
	 * @param goalString
	 *            goal of the PO
	 * @param globalSet
	 *            handle to the set of global hypotheses
	 * @param typEnv
	 *            local type environment
	 * @param localHypStrings
	 *            local hypotheses as strings
	 * @throws RodinDBException
	 */
	public static void addSequent(IPOFile poFile, String poName, String goalString,
			IPOPredicateSet globalSet, ITypeEnvironment typEnv,
			String... localHypStrings) throws RodinDBException {
		
		IPOSequent poSeq = poFile.getSequent(poName);
		poSeq.create(null, null);
		
		IPOPredicate poGoal = poSeq.getGoal("goal");
		poGoal.create(null, null);
		poGoal.setPredicateString(goalString, null);
		
		IPOPredicateSet poSet = poSeq.getHypothesis("local");
		createPredicateSet(poSet, globalSet, typEnv, localHypStrings);
	}

	/**
	 * Creates and populates the given predicate set with the supplied information.
	 * 
	 * @param poSet
	 *            predicate set to create and populate
	 * @param parentSet
	 *            parent set (may be <code>null</code>)
	 * @param typEnv
	 *            type environment for the set
	 * @param predStrings
	 *            predicates of the set as strings
	 * @throws RodinDBException
	 */
	private static void createPredicateSet(IPOPredicateSet poSet,
			IPOPredicateSet parentSet, ITypeEnvironment typEnv,
			String... predStrings) throws RodinDBException,
			NoSuchElementException {

		poSet.create(null, null);
		if (parentSet != null) {
			poSet.setParentPredicateSet(parentSet, null);
		}
		ITypeEnvironment.IIterator iter = typEnv.getIterator();
		while (iter.hasNext()) {
			iter.advance();
			IPOIdentifier poIdent = poSet.getIdentifier(iter.getName());
			poIdent.create(null, null);
			poIdent.setType(iter.getType(), null);
		}
		int idx = 1;
		for (String predString: predStrings) {
			IPOPredicate poPred = poSet.getPredicate("p" + idx++);
			poPred.create(null, null);
			poPred.setPredicateString(predString, null);
		}
	}
	
}
