/*******************************************************************************
 * Copyright (c) 2011 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package fr.systerel.perf.tests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import fr.systerel.perf.tests.builder.BuilderPerfTests;
import fr.systerel.perf.tests.parser.LexerPerfTests;
import fr.systerel.perf.tests.parser.ParserPerfTests;
import fr.systerel.perf.tests.rodinDB.RodinDBPerfTests;

/**
 * @author Nicolas Beauger
 * 
 */
@RunWith(Suite.class)
@SuiteClasses({ BuilderPerfTests.class, RodinDBPerfTests.class,
		LexerPerfTests.class, ParserPerfTests.class })
public class PerfTestSuite {

}
