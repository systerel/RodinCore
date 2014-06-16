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

import static org.eventb.core.EventBAttributes.TARGET_ATTRIBUTE;
import static org.eventb.core.sc.GraphProblem.AbstractContextRedundantWarning;
import static org.eventb.core.sc.GraphProblem.AbstractContextWithoutConfigurationError;
import static org.eventb.core.sc.GraphProblem.CarrierSetNameImportConflictError;
import static org.eventb.core.sc.GraphProblem.ConfigurationMissingError;
import static org.eventb.core.tests.MarkerMatcher.marker;

import org.eventb.core.IContextRoot;
import org.eventb.core.ISCContextRoot;
import org.eventb.core.ISCInternalContext;
import org.junit.Test;


/**
 * @author Stefan Hallerstede
 *
 */
public class TestExtendsContext extends BasicSCTestWithFwdConfig {

	/**
	 * a carrier set should be copied into internal contexts
	 */
	@Test
	public void testExtendsContext_01_createCarrierSet() throws Exception {
		IContextRoot abs = createContext("abs");
		addCarrierSets(abs, makeSList("S"));
		
		saveRodinFileOf(abs);
		
		IContextRoot con = createContext("ctx");
		addContextExtends(con, "abs");
		
		saveRodinFileOf(con);
		
		runBuilderCheck();
		
		ISCContextRoot file = con.getSCContextRoot();
		
		extendsContexts(file, "abs");
		ISCInternalContext[] contexts = getInternalContexts(file, 1);
		
		containsCarrierSets(contexts[0], "S");
	}

	/**
	 * two carrier sets should be copied into internal contexts
	 */
	@Test
	public void testExtendsContext_02_twoCarrierSets() throws Exception {
		IContextRoot abs = createContext("abs");
		addCarrierSets(abs, makeSList("S1", "S2"));
		
		saveRodinFileOf(abs);
		
		IContextRoot con = createContext("ctx");
		addContextExtends(con, "abs");
		
		saveRodinFileOf(con);
		
		runBuilderCheck();
		
		ISCContextRoot file = con.getSCContextRoot();
		
		ISCInternalContext[] contexts = getInternalContexts(file, 1);
		
		containsCarrierSets(contexts[0], "S1", "S2");
	}
	
	/**
	 * internal contexts that would introduce name conflicts must be removed
	 */
	@Test
	public void testExtendsContext_03_extendsConflict() throws Exception {
		IContextRoot abs1 = createContext("abs1");
		addCarrierSets(abs1, makeSList("S11", "S12"));
		
		saveRodinFileOf(abs1);
		
		IContextRoot abs2 = createContext("abs2");
		addCarrierSets(abs2, makeSList("S11", "S22"));
		
		saveRodinFileOf(abs2);
		
		IContextRoot con = createContext("ctx");
		addContextExtends(con, "abs1");
		addContextExtends(con, "abs2");
		
		saveRodinFileOf(con);
		
		runBuilderCheck(
				marker(con.getExtendsClauses()[0], TARGET_ATTRIBUTE,
						CarrierSetNameImportConflictError, "S11", "abs1"),
				marker(con.getExtendsClauses()[1], TARGET_ATTRIBUTE,
						CarrierSetNameImportConflictError, "S11", "abs2"));

		ISCContextRoot file = con.getSCContextRoot();
		extendsContexts(file);
		getInternalContexts(file, 0);
	}

	/**
	 * carrier sets from different contexts should be copied into corresponding internal contexts
	 */
	@Test
	public void testExtendsContext_04_extendsNoConflict() throws Exception {
		IContextRoot abs1 = createContext("abs1");
		addCarrierSets(abs1, makeSList("S11", "S12"));
		
		saveRodinFileOf(abs1);
		
		IContextRoot abs2 = createContext("abs2");
		addCarrierSets(abs2, makeSList("S21", "S22"));
		
		saveRodinFileOf(abs2);
		
		IContextRoot con = createContext("ctx");
		addContextExtends(con, "abs1");
		addContextExtends(con, "abs2");
		
		saveRodinFileOf(con);
		
		runBuilderCheck();
		

		ISCContextRoot file = con.getSCContextRoot();

		extendsContexts(file, "abs1", "abs2");
		containsContexts(file, "abs1", "abs2");
		
		ISCInternalContext[] contexts = getInternalContexts(file, 2);
		
		containsCarrierSets(contexts[0], "S11", "S12");

		containsCarrierSets(contexts[1], "S21", "S22");
	}
	
	/**
	 * internal contexts that would introduce name conflicts must be removed
	 * but contexts untouched should be kept.
	 */
	@Test
	public void testExtendsContext_05_extendsPartialConflict() throws Exception {
		IContextRoot abs1 = createContext("abs1");
		addCarrierSets(abs1, makeSList("S11", "S12"));
		
		saveRodinFileOf(abs1);
		
		IContextRoot abs2 = createContext("abs2");
		addCarrierSets(abs2, makeSList("S11", "S22"));
		
		saveRodinFileOf(abs2);
		
		IContextRoot abs3 = createContext("abs3");
		addCarrierSets(abs3, makeSList("S31", "S32"));
		
		saveRodinFileOf(abs3);
		
		IContextRoot con = createContext("ctx");
		addContextExtends(con, "abs1");
		addContextExtends(con, "abs2");
		addContextExtends(con, "abs3");
		
		saveRodinFileOf(con);
		
		runBuilderCheck(
				marker(con.getExtendsClauses()[0], TARGET_ATTRIBUTE,
						CarrierSetNameImportConflictError, "S11", "abs1"),
				marker(con.getExtendsClauses()[1], TARGET_ATTRIBUTE,
						CarrierSetNameImportConflictError, "S11", "abs2"));

		ISCContextRoot file = con.getSCContextRoot();
		
		extendsContexts(file, "abs3");
		containsContexts(file, "abs3");

		ISCInternalContext[] contexts = getInternalContexts(file, 1);
		
		containsCarrierSets(contexts[0], "S31", "S32");
	}
	
	/**
	 * Contexts should be included transitively.
	 */
	@Test
	public void testExtendsContext_06_transitive() throws Exception {
		IContextRoot abs1 = createContext("abs1");
		addCarrierSets(abs1, makeSList("S1"));
		saveRodinFileOf(abs1);
		
		IContextRoot abs2 = createContext("abs2");
		addContextExtends(abs2, "abs1");
		addCarrierSets(abs2, makeSList("S2"));
		saveRodinFileOf(abs2);
		
		IContextRoot con = createContext("ctx");
		addContextExtends(con, "abs2");
		saveRodinFileOf(con);

		runBuilderCheck();

		ISCContextRoot file = con.getSCContextRoot();
		extendsContexts(file, "abs2");
		containsContexts(file, "abs1", "abs2");
	}

	/**
	 * Contexts extended transitively by means of a extended contexts
	 * should not be extended directly by the context.
	 * (This should be a warning not an error)
	 */
	@Test
	public void testExtendsContext_07_redundant() throws Exception {
		IContextRoot cab = createContext("cab");
		
		saveRodinFileOf(cab);
		
		IContextRoot cco = createContext("cco");
		addContextExtends(cco, "cab");
		IContextRoot con = createContext("ctx");
		addContextExtends(con, "cco");
		addContextExtends(con, "cab");

		saveRodinFileOf(cco);
		saveRodinFileOf(con);
		
		runBuilderCheck(marker(con.getExtendsClauses()[1], TARGET_ATTRIBUTE,
				AbstractContextRedundantWarning, "cab"));
	}

	/**
	 * A context should not be extended directly more than once by the same context.
	 * (This should be a warning not an error)
	 */
	@Test
	public void testExtendsContext_08_redundant() throws Exception {
		IContextRoot cco = createContext("cco");
		IContextRoot con = createContext("ctx");
		addContextExtends(con, "cco");
		addContextExtends(con, "cco");

		saveRodinFileOf(cco);
		saveRodinFileOf(con);
		
		runBuilderCheck(marker(con.getExtendsClauses()[1], TARGET_ATTRIBUTE,
				AbstractContextRedundantWarning, "cco"));
	}

	/**
	 * abstract context not saved!
	 */
	@Test
	public void testExtendsContext_09_abstractContextNotSaved() throws Exception {
		IContextRoot abs = createContext("abs");
		addCarrierSets(abs, makeSList("S"));
		
		IContextRoot con = createContext("ctx");
		addContextExtends(con, "abs");
		addCarrierSets(con, makeSList("T"));
		
		saveRodinFileOf(con);
		
		// TODO why marker on SCContextRoot?
		runBuilderCheck(
				marker(abs, ConfigurationMissingError, "abs"),
				marker(abs.getSCContextRoot(), ConfigurationMissingError, "abs"),
				marker(con.getExtendsClauses()[0], TARGET_ATTRIBUTE,
						AbstractContextWithoutConfigurationError, "abs"));

		ISCContextRoot file = con.getSCContextRoot();
		containsCarrierSets(file, "T");
	}

}
