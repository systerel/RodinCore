/*******************************************************************************
 * Copyright (c) 2012 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.core.ast.tests;

import static java.util.regex.Pattern.compile;
import static org.eventb.core.ast.tests.AbstractTests.parseExpression;
import static org.eventb.core.ast.tests.AbstractTests.parseType;
import static org.eventb.core.ast.tests.AbstractTests.typeCheck;

import java.util.regex.Pattern;

import org.eventb.core.ast.Expression;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.GivenType;
import org.eventb.core.ast.ISpecialization;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Type;

/**
 * Utility class for building specialization objects from simple strings.
 * Specialization are given in the form of pairs <code>src := dst</code>
 * separated by double bars (<code>||</code>).
 * 
 * @author Laurent Voisin
 */
public class SpecializationBuilder {

	private static final Pattern LIST_SPLITTER = compile("\\s*\\|\\|\\s*");
	private static final Pattern PAIR_SPLITTER = compile("\\s*:=\\s*");

	private static final String[] NO_IMAGES = new String[0];

	private final FormulaFactory fac;
	private final ITypeEnvironment srcTypenv;
	private final ISpecialization result;

	public SpecializationBuilder(ITypeEnvironment typenv) {
		this.fac = typenv.getFormulaFactory();
		this.srcTypenv = typenv;
		this.result = fac.makeSpecialization();
	}

	public ISpecialization getResult() {
		return result;
	}

	public void addTypeSpecialization(String list) {
		final String[] pairImages = splitList(list);
		for (final String pairImage : pairImages) {
			final String[] images = splitPair(pairImage);
			final GivenType src = fac.makeGivenType(images[0]);
			final Type dst = parseType(images[1], fac);
			result.put(src, dst);
		}
	}

	public void addIdentSpecialization(String list) {
		final String[] pairImages = splitList(list);
		for (final String pairImage : pairImages) {
			final String[] images = splitPair(pairImage);
			final FreeIdentifier src = fac.makeFreeIdentifier(images[0], null);
			typeCheck(src, srcTypenv);
			final Expression dst = parseExpression(images[1], fac);
			final Type dstType = src.getType().specialize(result);
			dst.typeCheck(srcTypenv.specialize(result), dstType);
			result.put(src, dst);
		}
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