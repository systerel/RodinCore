/*******************************************************************************
 * Copyright (c) 2009, 2013 Universitaet Duesseldorf and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Universitaet Duesseldorf - initial API and implementation
 *     Systerel - port to JUnit 4
 *******************************************************************************/
package org.eventb.core.ast.tests;

import org.eventb.core.ast.expander.tests.PartitionExpanderTests;
import org.eventb.core.ast.expander.tests.SmartFactoryTests;
import org.eventb.core.ast.tests.datatype.TestConstructor;
import org.eventb.core.ast.tests.datatype.TestConstructorBuilder;
import org.eventb.core.ast.tests.datatype.TestDatatypeParser;
import org.eventb.core.ast.tests.datatype.TestDatatypeRewriter;
import org.eventb.core.ast.tests.datatype.TestDatatypeTranslation;
import org.eventb.core.ast.tests.datatype.TestDatatypeTranslator;
import org.eventb.core.ast.tests.datatype.TestDatatypes;
import org.eventb.core.ast.tests.datatype.TestEnumDatatypeTranslator;
import org.eventb.core.ast.tests.datatype.TestSetInstantiation;
import org.eventb.core.ast.tests.datatype.TestTypeInstantiation;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
	// $JUnit-BEGIN$
	TestTypeCheckError.class,
	TestTypes.class,
	TestLegibility.class,
	TestExprTypeChecker.class,
	DocTests.class,
	TestTypeEnvironment.class,
	TestIdentRenaming.class,
	TestBA.class,
	TestTypedConstructor.class,
	TestOrigin.class,
	TestAST.class,
	TestFreeIdents.class,
	TestFormulaFactory.class,
	TestUnparse.class,
	TestSourceLocation.class,
	TestTypeChecker.class,
	TestSubstituteFormula.class,
	TestLexer.class,
	TestWD.class,
	TestTypedIdentDecl.class,
	TestDeBruijn.class,
	TestParser.class,
	TestTypedGeneric.class,
	TestCollectNamesAbove.class,
	TestFlattener.class,
	TestSimpleVisitor.class,
	TestPosition.class,
	TestVisitor.class,
	TestBoundIdentRenaming.class,
	TestEquals.class,
	TestFIS.class,
	TestErrors.class,
	TestLocation.class,
	TestIntStack.class,
	TestConflictResolver.class,
	TestGivenTypes.class,
	TestSubFormulas.class,
	TestVersionUpgrader.class,
	PartitionExpanderTests.class,
	SmartFactoryTests.class,
	TestPredicateVariables.class,
	TestFormulaInspector.class,
	TestWDStrict.class,
	TestGenParser.class,
	TestSpecialization.class,
	TestTypeSpecialization.class,
	TestTypenvSpecialization.class,
	TestFormulaSpecialization.class,
	TestDatatypeRewriter.class,
	TestDatatypeTranslation.class,
	TestDatatypeTranslator.class,
	TestEnumDatatypeTranslator.class,
	TestFreshNameSolver.class,
	TestFactoryTranslation.class,
	TestDatatypes.class,
	TestDatatypeParser.class,
	TestConstructorBuilder.class,
	TestConstructor.class,
	TestTypeInstantiation.class,
	TestSetInstantiation.class,
	TestFactoryCreation.class,
	// $JUnit-END$
})
public class AllTests {

	// Now empty, all is in the annotations above

}
