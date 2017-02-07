/*******************************************************************************
 * Copyright (c) 2012, 2017 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *     University of Southampton - added support for predicate variables.
 *******************************************************************************/
package org.eventb.core.ast.tests;

import static org.eventb.core.ast.tests.AbstractTests.parseExpression;
import static org.eventb.core.ast.tests.AbstractTests.parsePredicate;
import static org.eventb.core.ast.tests.AbstractTests.parseType;
import static org.eventb.core.ast.tests.AbstractTests.typeCheck;
import static org.junit.Assert.fail;

import org.eventb.core.ast.Expression;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.GivenType;
import org.eventb.core.ast.ISpecialization;
import org.eventb.core.ast.ITypeCheckResult;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.PredicateVariable;
import org.eventb.core.ast.Type;

/**
 * Utility class for building specialization objects from simple strings.
 * Specialization are given in the form of pairs <code>src := dst</code>
 * separated by double bars (<code>||</code>).
 * 
 * @author Laurent Voisin
 * @author htson - added support for predicate variables.
 */
public class SpecializationBuilder extends AbstractSpecializationHelper {

	private final ISpecialization result;

	public SpecializationBuilder(ITypeEnvironment typenv) {
		this(typenv, typenv.getFormulaFactory());
	}

	public SpecializationBuilder(ITypeEnvironment typenv, FormulaFactory dstFac) {
		super(typenv, dstFac);
		this.result = dstFac.makeSpecialization();
	}

	public ISpecialization getResult() {
		return result;
	}

	@Override
	protected void addTypeSpecialization(String srcImage, String dstImage) {
		final GivenType src = srcFac.makeGivenType(srcImage);
		final Type dst = parseType(dstImage, dstFac);
		result.put(src, dst);
	}

	/**
	 * Utility method to add a predicate specialization.
	 * 
	 * @param srcImage
	 *            the string source image
	 * @param dstImage
	 *            the image corresponding to the predicate for instantiation.
	 * @htson
	 */
	@Override
	protected void addPredicateSpecialization(String srcImage, String dstImage) {
		final PredicateVariable predVar = srcFac.makePredicateVariable(
				srcImage, null);
		Predicate pred = parsePredicate(dstImage, dstFac);
		final ISpecialization temp = result.clone();
		final ITypeEnvironment dstTypenv = srcTypenv.specialize(temp);
		final ITypeCheckResult tcResult = pred.typeCheck(dstTypenv);
		if (tcResult.hasProblem()) {
			fail("Typecheck failed for predicate " + dstImage + "\n"
					+ "Type environment is " + dstTypenv + "\n"
					+ tcResult.getProblems());
		}
		result.put(predVar, pred);
	}

	@Override
	protected void addIdentSpecialization(String srcImage, String dstImage) {
		final FreeIdentifier src = srcFac.makeFreeIdentifier(srcImage, null);
		typeCheck(src, srcTypenv);
		final Expression dst = parseExpression(dstImage, dstFac);
		final Type dstType = src.getType().specialize(result);
		final ISpecialization temp = result.clone();
		final ITypeEnvironment dstTypenv = srcTypenv.specialize(temp);
		final ITypeCheckResult tcResult = dst.typeCheck(dstTypenv, dstType);
		if (tcResult.hasProblem()) {
			fail("Typecheck failed for expression " + dstImage
					+ "\nExpected type is " + dstType
					+ "\nType environment is " + dstTypenv + "\n"
					+ tcResult.getProblems());
		}
		result.put(src, dst);
	}

}
