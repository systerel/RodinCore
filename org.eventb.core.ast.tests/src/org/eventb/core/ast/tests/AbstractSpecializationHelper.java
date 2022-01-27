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

import static java.util.regex.Pattern.compile;

import java.util.regex.Pattern;

import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.GivenType;
import org.eventb.core.ast.ISealedTypeEnvironment;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.PredicateVariable;
import org.eventb.core.ast.Type;

/**
 * Common implementation for building and verifying specialization instances.
 * 
 * @author Laurent Voisin
 */
public abstract class AbstractSpecializationHelper {

	private static final Pattern LIST_SPLITTER = compile("\\s*\\|\\|\\s*");
	private static final Pattern PAIR_SPLITTER = compile("\\s*:=\\s*");

	private static final String[] NO_IMAGES = new String[0];

	protected final FormulaFactory srcFac;
	protected final ISealedTypeEnvironment srcTypenv;
	protected final FormulaFactory dstFac;

	public AbstractSpecializationHelper(ITypeEnvironment typenv) {
		this(typenv, typenv.getFormulaFactory());
	}

	public AbstractSpecializationHelper(ITypeEnvironment typenv,
			FormulaFactory dstFac) {
		this.srcFac = typenv.getFormulaFactory();
		this.srcTypenv = typenv.makeSnapshot();
		this.dstFac = dstFac;
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

	protected abstract void addTypeSpecialization(String srcImage,
			String dstImage);

	protected abstract void addPredicateSpecialization(String srcImage,
			String dstImage);

	protected abstract void addIdentSpecialization(String srcImage,
			String dstImage);

	private static String[] splitList(String list) {
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
