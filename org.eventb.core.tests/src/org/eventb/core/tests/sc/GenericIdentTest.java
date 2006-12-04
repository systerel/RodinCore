/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.tests.sc;

import org.eventb.core.ast.ITypeEnvironment;
import org.rodinp.core.IRodinFile;


/**
 * @author Stefan Hallerstede
 *
 */
public abstract class GenericIdentTest <IRF extends IRodinFile, ISCRF extends IRodinFile> 
extends BasicSCTest 
implements IGenericSCTest<IRF, ISCRF> {

	public void testIdents_00() throws Exception {
		IRF cmp = createComponent("cmp", (IRF) null);

		addIdents(cmp, makeSList("V1"));
		
		cmp.save(null, true);
		
		runBuilder();
		
		ISCRF file = getSCComponent(cmp);
		
		containsIdents(file);
		
	}

	public void testIdents_01() throws Exception {
		IRF cmp = createComponent("cmp", (IRF) null);

		addIdents(cmp, makeSList("V1"));
		addNonTheorems(cmp, makeSList("I1"), makeSList("V1∈ℤ"));
		
		cmp.save(null, true);
		
		runBuilder();
		
		ITypeEnvironment environment = factory.makeTypeEnvironment();
		environment.addName("V1", factory.makeIntegerType());
		
		ISCRF file = getSCComponent(cmp);
		
		containsIdents(file, "V1");
		
		containsNonTheorems(file, environment, makeSList("I1"), makeSList("V1∈ℤ"));

	}
	
	public void testIdents_02() throws Exception {
		IRF cmp = createComponent("cmp", (IRF) null);

		addIdents(cmp, makeSList("V1"));
		addNonTheorems(cmp, makeSList("I1"), makeSList("V2∈ℤ"));

		cmp.save(null, true);
		
		runBuilder();
		
		ISCRF file = getSCComponent(cmp);
		
		containsIdents(file);
		
		containsNonTheorems(file, emptyEnv, makeSList(), makeSList());
	}


}
