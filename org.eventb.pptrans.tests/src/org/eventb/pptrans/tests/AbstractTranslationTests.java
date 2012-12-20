/*******************************************************************************
 * Copyright (c) 2006, 2010 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - mathematical language V2
 *******************************************************************************/
package org.eventb.pptrans.tests;

import static java.util.Collections.emptyList;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.eventb.core.ast.FormulaFactory.getInstance;
import static org.eventb.core.ast.LanguageVersion.V2;

import java.util.List;

import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.IParseResult;
import org.eventb.core.ast.ITypeCheckResult;
import org.eventb.core.ast.ITypeEnvironmentBuilder;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.Type;
import org.eventb.core.ast.extension.datatype.IConstructorMediator;
import org.eventb.core.ast.extension.datatype.IDatatype;
import org.eventb.core.ast.extension.datatype.IDatatypeExtension;
import org.eventb.core.ast.extension.datatype.ITypeConstructorMediator;
import org.eventb.core.seqprover.transformer.ISimpleSequent;
import org.eventb.core.seqprover.transformer.SimpleSequents;

public abstract class AbstractTranslationTests {
	
	protected static final FormulaFactory ff = FormulaFactory.getDefault();
	
	protected static final Type INT = ff.makeIntegerType();
	protected static final Type BOOL = ff.makeBooleanType();
	protected static final Type INT_SET = POW(INT);
	protected static final Type ty_S = ff.makeGivenType("S");

	/**
	 * A simple datatype extension.
	 */
	private static final IDatatypeExtension DT_TYPE = new IDatatypeExtension() {

		@Override
		public String getTypeName() {
			return "DT";
		}

		@Override
		public String getId() {
			return "DT.id";
		}

		@Override
		public void addTypeParameters(ITypeConstructorMediator mediator) {
			// none
		}

		@Override
		public void addConstructors(IConstructorMediator mediator) {
			mediator.addConstructor("dt", "dt.id");
		}
	};

	private static final IDatatype DT = ff.makeDatatype(DT_TYPE);

	protected static final FormulaFactory DT_FF = getInstance(DT.getExtensions());

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
		IParseResult parseResult = factory.parsePredicate(string, V2, null);
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

	public static void assertTypeChecked(Formula<?> formula) {
		assertTrue("Formula is not typed: " + formula, formula.isTypeChecked());
	}

	protected ISimpleSequent make(FormulaFactory factory, String goalImage,
			String... hypImages) {
		final ITypeEnvironmentBuilder typenv = factory.makeTypeEnvironment();
		final Predicate[] hyps = new Predicate[hypImages.length];
		for (int i = 0; i < hyps.length; i++) {
			hyps[i] = parse(hypImages[i], typenv);
		}
		final Predicate goal = goalImage == null ? null : parse(goalImage,
				typenv);
		return SimpleSequents.make(hyps, goal, factory);
	}

	protected ISimpleSequent make(String goalImage, String... hypImages) {
		return make(ff, goalImage, hypImages);
	}

}
