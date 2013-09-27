/*******************************************************************************
 * Copyright (c) 2006, 2013 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - mathematical language V2
 *     Systerel - test with simple sequents
 *******************************************************************************/
package org.eventb.pptrans.tests;

import static java.util.Collections.emptyList;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.GivenType;
import org.eventb.core.ast.IParseResult;
import org.eventb.core.ast.ITypeCheckResult;
import org.eventb.core.ast.ITypeEnvironmentBuilder;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.Type;
import org.eventb.core.ast.datatype.IDatatype;
import org.eventb.core.ast.datatype.IDatatypeBuilder;
import org.eventb.core.seqprover.transformer.ISimpleSequent;
import org.eventb.core.seqprover.transformer.SimpleSequents;

public abstract class AbstractTranslationTests {
	
	protected static final FormulaFactory ff = FormulaFactory.getDefault();
	
	protected static final Type INT = ff.makeIntegerType();
	protected static final Type BOOL = ff.makeBooleanType();
	protected static final Type INT_SET = POW(INT);
	protected static final Type ty_S = ff.makeGivenType("S");

	/**
	 * A simple datatype
	 */
	public static final IDatatype DT;
	static {
		final GivenType[] typeParams = {};
		final IDatatypeBuilder DT_BUILDER = ff.makeDatatypeBuilder(
				"DT", typeParams);
		DT_BUILDER.addConstructor("dt");
		DT = DT_BUILDER.finalizeDatatype();
	}

	protected static final FormulaFactory DT_FF = DT.getFactory();

	protected static final List<Predicate> NONE = emptyList();

	protected static Type POW(Type base) {
		return ff.makePowerSetType(base);
	}

	protected static Type CPROD(Type left, Type right) {
		return ff.makeProductType(left, right);
	}

	protected static Type REL(Type left, Type right) {
		return ff.makeRelationalType(left, right);
	}

	protected static Type mGivenSet(String name) {
		return ff.makeGivenType(name);
	}

	public static Predicate parse(String string, ITypeEnvironmentBuilder te) {
		final FormulaFactory factory = te.getFormulaFactory();
		IParseResult parseResult = factory.parsePredicate(string, null);
		assertFalse("Parse error for: " + string +
				"\nProblems: " + parseResult.getProblems(),
				parseResult.hasProblem());
		Predicate pred = parseResult.getParsedPredicate();
		ITypeCheckResult tcResult = pred.typeCheck(te);
		assertTrue(string + " is not typed. Problems: " + tcResult.getProblems(),
				pred.isTypeChecked());
		te.addAll(tcResult.getInferredEnvironment());
		return pred;
	}

	public static Predicate parse(String string) {
		return parse(string, ff.makeTypeEnvironment());
	}

	protected static ISimpleSequent make(FormulaFactory factory,
			String goalImage, String... hypImages) {
		final ITypeEnvironmentBuilder typenv = factory.makeTypeEnvironment();
		return make(typenv, goalImage, hypImages);
	}

	protected static ISimpleSequent make(String goalImage, String... hypImages) {
		return make(ff, goalImage, hypImages);
	}

	protected static ISimpleSequent make(ITypeEnvironmentBuilder typenv,
			String goalImage, String... hypImages) {
		final FormulaFactory factory = typenv.getFormulaFactory();
		final Predicate[] hyps = new Predicate[hypImages.length];
		for (int i = 0; i < hyps.length; i++) {
			hyps[i] = parse(hypImages[i], typenv);
		}
		final Predicate goal = goalImage == null ? null : parse(goalImage,
				typenv);
		return SimpleSequents.make(hyps, goal, factory);
	}

}
