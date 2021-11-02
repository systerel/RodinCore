/*******************************************************************************
 * Copyright (c) 2012, 2021 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *     University of Southampton - added support for predicate variables.
 *     Université de Lorraine - added support for extensions
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
import org.eventb.core.ast.extension.IExpressionExtension;
import org.eventb.core.ast.extension.IFormulaExtension;
import org.eventb.core.ast.extension.IPredicateExtension;

/**
 * Common implementation for building and verifying specialization instances.
 * 
 * @author Laurent Voisin
 */
public abstract class AbstractSpecializationHelper {

	public static final String EXTENSION_PREFIX = "@";

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
			} else if (isExtension(srcImage)) {
				if (!isExtension(dstImage)) {
					throw new IllegalArgumentException("Can only specialize an extension with an extension");
				}
				addExtension(srcImage, dstImage);
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

	private boolean isExtension(String srcImage) {
		return srcImage.startsWith(EXTENSION_PREFIX);
	}

	protected IFormulaExtension findExtension(FormulaFactory fac, String image) {
		for (IFormulaExtension ext : fac.getExtensions()) {
			if (ext.toString().equals(image)) {
				return ext;
			}
		}
		return null;
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

	protected abstract void addExpressionExtension(IExpressionExtension srcExt, IExpressionExtension dstExt);

	protected abstract void addPredicateExtension(IPredicateExtension srcExt, IPredicateExtension dstExt);

	protected void addExtension(String srcImage, String dstImage) {
		IFormulaExtension srcExt = findExtension(srcFac, srcImage);
		if (srcExt == null) {
			throw new IllegalArgumentException("Source extension " + srcImage + " not found");
		}
		IFormulaExtension dstExt = findExtension(dstFac, dstImage);
		if (dstExt == null) {
			throw new IllegalArgumentException("Destination extension " + dstImage + " not found");
		}
		if (srcExt instanceof IExpressionExtension) {
			if (dstExt instanceof IExpressionExtension) {
				addExpressionExtension((IExpressionExtension) srcExt, (IExpressionExtension) dstExt);
			} else {
				throw new IllegalArgumentException(
						"Expression extension can only be specialized by an expression extension");
			}
		} else if (srcExt instanceof IPredicateExtension) {
			if (dstExt instanceof IPredicateExtension) {
				addPredicateExtension((IPredicateExtension) srcExt, (IPredicateExtension) dstExt);
			} else {
				throw new IllegalArgumentException(
						"Predicate extension can only be specialized by a predicate extension");
			}
		} else {
			throw new IllegalStateException("Found an extension that is neither an expression nor a predicate");
		}
	}

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
