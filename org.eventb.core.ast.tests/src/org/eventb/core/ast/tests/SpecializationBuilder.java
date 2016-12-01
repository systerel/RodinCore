/*******************************************************************************
 * Copyright (c) 2012, 2016 Systerel and others.
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

import static java.util.regex.Pattern.compile;
import static org.eventb.core.ast.tests.AbstractTests.parseExpression;
import static org.eventb.core.ast.tests.AbstractTests.parsePredicate;
import static org.eventb.core.ast.tests.AbstractTests.parseType;
import static org.eventb.core.ast.tests.AbstractTests.typeCheck;
import static org.junit.Assert.fail;

import java.util.regex.Pattern;

import org.eventb.core.ast.Expression;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.GivenType;
import org.eventb.core.ast.ISealedTypeEnvironment;
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
public class SpecializationBuilder {

	private static final Pattern LIST_SPLITTER = compile("\\s*\\|\\|\\s*");
	private static final Pattern PAIR_SPLITTER = compile("\\s*:=\\s*");

	private static final String[] NO_IMAGES = new String[0];

	private final FormulaFactory srcFac;
	private final ISealedTypeEnvironment srcTypenv;
	private final FormulaFactory dstFac;
	private final ISpecialization result;

	public SpecializationBuilder(ITypeEnvironment typenv) {
		this(typenv, typenv.getFormulaFactory());
	}

	public SpecializationBuilder(ITypeEnvironment typenv, FormulaFactory dstFac) {
		this.srcFac = typenv.getFormulaFactory();
		this.srcTypenv = typenv.makeSnapshot();
		this.dstFac = dstFac;
		this.result = dstFac.makeSpecialization();
	}

	public ISpecialization getResult() {
		return result;
	}

	public void addSpecialization(String list) {
		final String[] pairImages = splitList(list);
		for (final String pairImage : pairImages) {
			final String[] images = splitPair(pairImage);
			final String srcImage = images[0];
			final String dstImage = images[1];
			if (isGivenType(srcImage)) {
				addTypeSpecialization(srcImage, dstImage);
			} else if (isPredicateVariable(srcImage)) {
				addPredicateSpecialization(srcImage, dstImage);
			} else {
				addIdentSpecialization(srcImage, dstImage);
			}
		}
	}

	private boolean isGivenType(String srcImage) {
		final Type type = srcTypenv.getType(srcImage);
		if (type == null) {
			return false;
		}
		final Type baseType = type.getBaseType();
		if (baseType instanceof GivenType) {
			final GivenType givenType = (GivenType) baseType;
			return givenType.getName().equals(srcImage);
		}
		return false;
	}

	/**
	 * Utility method to check if a string source image is a predicate variable.
	 * 
	 * @param srcImage
	 *            the input source image.
	 * @return <code>true</code> if the input source image is a predicate
	 *         variable.
	 * @author htson
	 */
	private boolean isPredicateVariable(String srcImage) {
		return srcImage.startsWith(PredicateVariable.LEADING_SYMBOL);
	}
	
	public void addTypeSpecializations(String list) {
		final String[] pairImages = splitList(list);
		for (final String pairImage : pairImages) {
			final String[] images = splitPair(pairImage);
			addTypeSpecialization(images[0], images[1]);
		}
	}

	private void addTypeSpecialization(String srcImage, String dstImage) {
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
	private void addPredicateSpecialization(String srcImage, String dstImage) {
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

	private void addIdentSpecialization(String srcImage, String dstImage) {
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

	private String[] splitList(String list) {
		final String trimmed = list.trim();
		if (trimmed.length() == 0) {
			return NO_IMAGES;
		}
		return LIST_SPLITTER.split(trimmed);
	}

	private static String[] splitPair(String pairImage) {
		final String[] images = PAIR_SPLITTER.split(pairImage);
		assert images.length == 2;
		return images;
	}

}
