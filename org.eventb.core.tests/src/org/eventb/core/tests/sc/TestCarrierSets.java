/*******************************************************************************
 * Copyright (c) 2006, 2014 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - separation of file and root element
 *     Systerel - use marker matcher
 *******************************************************************************/
package org.eventb.core.tests.sc;

import static org.eventb.core.EventBAttributes.IDENTIFIER_ATTRIBUTE;
import static org.eventb.core.EventBAttributes.TARGET_ATTRIBUTE;
import static org.eventb.core.sc.GraphProblem.CarrierSetNameConflictError;
import static org.eventb.core.sc.GraphProblem.CarrierSetNameImportConflictWarning;
import static org.eventb.core.sc.GraphProblem.InvalidIdentifierError;
import static org.eventb.core.tests.MarkerMatcher.marker;

import org.eventb.core.ICarrierSet;
import org.eventb.core.IContextRoot;
import org.eventb.core.ISCContextRoot;
import org.eventb.core.ISCInternalContext;
import org.junit.Test;

/**
 * @author Stefan Hallerstede
 *
 */
public class TestCarrierSets extends BasicSCTestWithFwdConfig {

	/**
	 * creation of carrier sets
	 */
	@Test
	public void testCarrierSets_00_createCarrierSet() throws Exception {
		IContextRoot con = createContext("ctx");

		addCarrierSets(con, makeSList("S1"));
		
		saveRodinFileOf(con);
		
		runBuilderCheck();
		
		ISCContextRoot file = con.getSCContextRoot();
		
		containsCarrierSets(file, "S1");
	}

	/**
	 * creation of two carrier sets
	 */
	@Test
	public void testCarrierSets_01_twoCarrierSets() throws Exception {
		IContextRoot con = createContext("ctx");

		addCarrierSets(con, makeSList("S1", "S2"));
		
		saveRodinFileOf(con);
		
		runBuilderCheck();
		
		ISCContextRoot file = con.getSCContextRoot();
		
		containsCarrierSets(file, "S1", "S2");
	}

	/**
	 * name conflict between two carrier sets
	 */
	@Test
	public void testCarrierSets_02_twoCarrierSetsNameConflict() throws Exception {
		IContextRoot con = createContext("ctx");

		addCarrierSets(con, makeSList("S1", "S1"));
		
		saveRodinFileOf(con);
		
		final ICarrierSet[] cs = con.getCarrierSets();
		runBuilderCheck(
				marker(cs[0], IDENTIFIER_ATTRIBUTE,
						CarrierSetNameConflictError, "S1"),
				marker(cs[1], IDENTIFIER_ATTRIBUTE,
						CarrierSetNameConflictError, "S1"));

		ISCContextRoot file = con.getSCContextRoot();
		
		containsCarrierSets(file);
	}

	/**
	 * faulty names for carrier sets
	 */
	@Test
	public void testCarrierSets_03_carrierSetFaultyName() throws Exception {
		IContextRoot con = createContext("ctx");

		addCarrierSets(con, makeSList("S>", "k-1", "#"));
		
		saveRodinFileOf(con);
		
		final ICarrierSet[] cs = con.getCarrierSets();
		runBuilderCheck(
				marker(cs[0], IDENTIFIER_ATTRIBUTE, InvalidIdentifierError,
						"S>"),
				marker(cs[1], IDENTIFIER_ATTRIBUTE, InvalidIdentifierError,
						"k-1"),
				marker(cs[2], IDENTIFIER_ATTRIBUTE, InvalidIdentifierError, "#"));
		
		ISCContextRoot file = con.getSCContextRoot();
		
		containsCarrierSets(file);
	}

	/**
	 * copying of carrier sets from abstraction
	 */
	@Test
	public void testCarrierSets_04_carrierSetOfAbstraction() throws Exception {
		IContextRoot abs = createContext("abs");

		addCarrierSets(abs, makeSList("S1"));
		
		saveRodinFileOf(abs);
		
		IContextRoot con = createContext("ctx");
		addContextExtends(con, "abs");
		
		addCarrierSets(con, makeSList("S2"));
		
		saveRodinFileOf(con);
		
		runBuilderCheck();
		
		ISCContextRoot file = con.getSCContextRoot();
		
		containsCarrierSets(file, "S2");
		
		ISCInternalContext[] contexts = getInternalContexts(file, 1);
		
		containsCarrierSets(contexts[0], "S1");
	}

	/**
	 * name conflict with carrier set from abstraction
	 */
	@Test
	public void testCarrierSets_05_carrierSetOfAbstractionNameConflict() throws Exception {
		IContextRoot abs = createContext("abs");
		addCarrierSets(abs, makeSList("S1"));
		
		saveRodinFileOf(abs);
		
		IContextRoot con = createContext("ctx");
		addContextExtends(con, "abs");
		
		addCarrierSets(con, makeSList("S1"));
		
		saveRodinFileOf(con);
		
		runBuilderCheck(
				marker(con.getCarrierSets()[0], IDENTIFIER_ATTRIBUTE,
						CarrierSetNameConflictError, "S1"),
				marker(con.getExtendsClauses()[0], TARGET_ATTRIBUTE,
						CarrierSetNameImportConflictWarning, "S1", "abs"));
		
		ISCContextRoot file = con.getSCContextRoot();
		
		containsCarrierSets(file);
		
		ISCInternalContext[] contexts = getInternalContexts(file, 1);
		
		containsCarrierSets(contexts[0], "S1");
	}


}
