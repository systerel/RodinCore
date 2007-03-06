/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
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
		
	}

	/**
	 * Creating a constant or variable without a type must succeed
	 */
	public void testIdents_01() throws Exception {
		E cmp = getGeneric().createElement("cmp");

		getGeneric().addIdents(cmp, makeSList("V1"));
		getGeneric().addNonTheorems(cmp, makeSList("I1"), makeSList("V1∈ℤ"));
		
		getGeneric().save(cmp);
		
		runBuilder();
		
		ITypeEnvironment environment = factory.makeTypeEnvironment();
		environment.addName("V1", factory.makeIntegerType());
		
		SCE file = getGeneric().getSCElement(cmp);
		
		getGeneric().containsIdents(file, "V1");
		
		getGeneric().containsNonTheorems(file, environment, makeSList("I1"), makeSList("V1∈ℤ"));

		getGeneric().containsMarkers(cmp, false);
	}
	
	/**
	 * refering to a nondeclared identifier should fail
	 */
	public void testIdents_02() throws Exception {
		E cmp = getGeneric().createElement("cmp");

		getGeneric().addIdents(cmp, makeSList("V1"));
		getGeneric().addNonTheorems(cmp, makeSList("I1"), makeSList("V2∈ℤ"));

		getGeneric().save(cmp);
		
		runBuilder();
		
		SCE file = getGeneric().getSCElement(cmp);
		
		getGeneric().containsIdents(file);
		
		getGeneric().containsNonTheorems(file, emptyEnv, makeSList(), makeSList());
	}


}
