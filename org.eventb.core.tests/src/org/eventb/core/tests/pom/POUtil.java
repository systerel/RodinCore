/*******************************************************************************
 * Copyright (c) 2005, 2009 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - separation of file and root element
 *     Systerel - mathematical language V2
 *******************************************************************************/
package org.eventb.core.tests.pom;

import static org.eventb.core.ast.LanguageVersion.V2;

import java.util.NoSuchElementException;

import org.eventb.core.IPOIdentifier;
import org.eventb.core.IPOPredicate;
import org.eventb.core.IPOPredicateSet;
import org.eventb.core.IPORoot;
import org.eventb.core.IPOSequent;
import org.eventb.core.IPOStampedElement;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.IParseResult;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Type;
import org.rodinp.core.RodinDBException;

/**
 * Utility methods for writing a PO file.
 * 
 * @author halstefa
 * @author Laurent Voisin
 */
public class POUtil {
	
	public static final FormulaFactory ff = FormulaFactory.getDefault();

	/**
	 * Creates a new type environment from the given strings. The given strings
	 * are alternatively an identifier name and its type.
	 * 
	 * @param strings
	 *            an even number of strings
	 * @return a new type environment
	 */
	public static final ITypeEnvironment mTypeEnvironment(String... strings) {
		// even number of strings
		assert (strings.length & 1) == 0;
		final ITypeEnvironment result = ff.makeTypeEnvironment();
		for (int i = 0; i < strings.length; i += 2) {
			final String name = strings[i];
			final String typeString = strings[i + 1];
			final IParseResult pResult = ff.parseType(typeString, V2);
			assert !pResult.hasProblem();
			final Type type = pResult.getParsedType();
			result.addName(name, type);
		}
		return result;
	}

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
	public static IPOPredicateSet addPredicateSet(IPORoot poRoot, String setName,
			IPOPredicateSet parentSet, ITypeEnvironment typEnv,
			String... predStrings) throws RodinDBException {

		IPOPredicateSet poSet = poRoot.getPredicateSet(setName);
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
	public static void addSequent(IPORoot poRoot, String poName, long poStamp,
			String goalString, IPOPredicateSet globalSet, ITypeEnvironment typEnv,
			String... localHypStrings) throws RodinDBException {
		
		IPOSequent poSeq = poRoot.getSequent(poName);
		poSeq.create(null, null);
		poSeq.setPOStamp(poStamp, null);
		IPOPredicate poGoal = poSeq.getGoal("goal");
		poGoal.create(null, null);
		poGoal.setPredicateString(goalString, null);
		
		IPOPredicateSet poSet = poSeq.getHypothesis("local");
		createPredicateSet(poSet, globalSet, typEnv, localHypStrings);
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
	public static void addSequent(IPORoot poRoot, String poName,
			String goalString, IPOPredicateSet globalSet, ITypeEnvironment typEnv,
			String... localHypStrings) throws RodinDBException {
		addSequent(poRoot, poName, IPOStampedElement.INIT_STAMP, goalString, globalSet, typEnv, localHypStrings);
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
