/*******************************************************************************
 * Copyright (c) 2006, 2009 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - ensure that all AST problems are reported
 *     University of Dusseldorf - added theorem attribute
 * 	   Systerel - added check on primed identifiers
 *******************************************************************************/
package org.eventb.core.tests.sc;

import org.eventb.core.ast.ITypeEnvironment;
import org.rodinp.core.IRodinElement;


/**
 * @author Stefan Hallerstede
 *
 */
public abstract class GenericIdentTest <E extends IRodinElement, SCE extends IRodinElement> 
extends GenericEventBSCTest<E, SCE> {

	/**
	 * Creating a constant or variable without a type must fail
	 */
	public void testIdents_00() throws Exception {
		E cmp = getGeneric().createElement("cmp");

		getGeneric().addIdents(cmp, makeSList("V1"));
		
		getGeneric().save(cmp);
		
		runBuilder();
		
		SCE file = getGeneric().getSCElement(cmp);
		
		getGeneric().containsIdents(file);
		
		hasMarker(getGeneric().getIdents(cmp)[0]);
	}

	/**
	 * Creating a constant or variable without a type must succeed
	 */
	public void testIdents_01() throws Exception {
		E cmp = getGeneric().createElement("cmp");

		getGeneric().addIdents(cmp, makeSList("V1"));
		getGeneric().addPredicates(cmp, makeSList("I1"), makeSList("V1∈ℤ"), false);
		
		getGeneric().save(cmp);
		
		runBuilder();
		
		ITypeEnvironment environment = factory.makeTypeEnvironment();
		environment.addName("V1", factory.makeIntegerType());
		
		SCE file = getGeneric().getSCElement(cmp);
		
		getGeneric().containsIdents(file, "V1");
		
		getGeneric().containsPredicates(file, environment, makeSList("I1"), makeSList("V1∈ℤ"), false);

		getGeneric().containsMarkers(cmp, false);
	}
	
	/**
	 * refering to a nondeclared identifier should fail
	 */
	public void testIdents_02() throws Exception {
		E cmp = getGeneric().createElement("cmp");

		getGeneric().addIdents(cmp, makeSList("V1"));
		getGeneric().addPredicates(cmp, makeSList("I1"), makeSList("V2∈ℤ"), false);

		getGeneric().save(cmp);
		
		runBuilder();
		
		SCE file = getGeneric().getSCElement(cmp);
		
		getGeneric().containsIdents(file);
		
		getGeneric().containsPredicates(file, emptyEnv, makeSList(), makeSList());
		
		hasMarker(getGeneric().getIdents(cmp)[0]);
		hasMarker(getGeneric().getPredicates(cmp)[0]);
	}
	
	/**
	 * refering to identifiers with faulty declaration should fail
	 */
	public void testIdents_03() throws Exception {
		E cmp = getGeneric().createElement("cmp");

		getGeneric().addIdents(cmp, makeSList("V1", "V1"));
		getGeneric().addPredicates(cmp, makeSList("I1"), makeSList("V1∈ℤ"), false);

		getGeneric().save(cmp);
		
		runBuilder();
		
		SCE file = getGeneric().getSCElement(cmp);
		
		getGeneric().containsIdents(file);
		
		getGeneric().containsPredicates(file, emptyEnv, makeSList(), makeSList());
		
		hasMarker(getGeneric().getIdents(cmp)[0]);
		hasMarker(getGeneric().getIdents(cmp)[1]);
		hasMarker(getGeneric().getPredicates(cmp)[0]);
	}

	/**
	 * An identifier declaration containing an invalid character is reported
	 */
	public void testIdents_04_bug2689872() throws Exception {
		E cmp = getGeneric().createElement("cmp");

		getGeneric().addIdents(cmp, makeSList("/V1"));
		getGeneric().addPredicates(cmp, makeSList("I1"), makeSList("/V1∈ℤ"), false);

		getGeneric().save(cmp);
		
		runBuilder();
		
		SCE file = getGeneric().getSCElement(cmp);
		
		getGeneric().containsIdents(file);
		
		getGeneric().containsPredicates(file, emptyEnv, makeSList(), makeSList());
		
		hasMarker(getGeneric().getIdents(cmp)[0]);
		hasMarker(getGeneric().getPredicates(cmp)[0]);
	}

	
	/**
	 * An identifier declaration cannot be primed.
	 */
	public void testIdents_05_bug2815882() throws Exception {
		final E cmp = getGeneric().createElement("cmp");

		getGeneric().addIdents(cmp, makeSList("v'"));
		getGeneric().addPredicates(cmp, makeSList("I1"), makeSList("v'∈ℤ"), false);

		getGeneric().save(cmp);
		
		runBuilder();
		
		final ITypeEnvironment environment = factory.makeTypeEnvironment();
		environment.addName("v'", factory.makeIntegerType());
		
		final SCE file = getGeneric().getSCElement(cmp);
		
		getGeneric().containsIdents(file);
		
		getGeneric().containsPredicates(file, emptyEnv, makeSList(), makeSList());
		
		hasMarker(getGeneric().getIdents(cmp)[0]);
		hasMarker(getGeneric().getPredicates(cmp)[0]);
	}
	
}
