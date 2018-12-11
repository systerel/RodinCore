/*******************************************************************************
 * Copyright (c) 2018 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.ui.refine;

import static java.math.BigInteger.ONE;
import static java.math.BigInteger.ZERO;
import static java.util.regex.Pattern.compile;

import java.math.BigInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jface.dialogs.IInputValidator;

/**
 * Proposes new names for refine/extend actions.
 * 
 * @author Laurent Voisin
 */
public class RefineProposer {

	private static final Pattern PREFIX_PATTERN = compile("^\\d+");
	private static final Pattern SUFFIX_PATTERN = compile("\\d+$");

	public static String getTentativeName(String name, IInputValidator validator) {
		return new RefineProposer(name, validator).getResult();
	}

	private final String oldName;
	private final IInputValidator validator;

	private String base;
	private BigInteger number;
	private int width;
	private boolean isPrefix;

	private RefineProposer(String name, IInputValidator validator) {
		this.oldName = name;
		this.validator = validator;
	}

	private String getResult() {
		if (!extractPrefix() && !extractSuffix()) {
			// No prefix, nor suffix, assume empty numeric suffix
			base = oldName;
			number = ZERO;
			width = 1;
			isPrefix = false;
		}

		String newName;
		do {
			number = number.add(ONE);
			newName = getNewName();
		} while (validator.isValid(newName) != null);

		return newName;
	}

	private boolean extractPrefix() {
		final Matcher matcher = PREFIX_PATTERN.matcher(oldName);
		if (!matcher.find()) {
			return false;
		}
		String match = matcher.group();
		base = oldName.substring(matcher.end());
		number = new BigInteger(match);
		width = match.length();
		isPrefix = true;
		return true;

	}

	private boolean extractSuffix() {
		final Matcher matcher = SUFFIX_PATTERN.matcher(oldName);
		if (!matcher.find()) {
			return false;
		}
		String match = matcher.group();
		base = oldName.substring(0, matcher.start());
		number = new BigInteger(match);
		width = match.length();
		isPrefix = false;
		return true;
	}

	private String getNewName() {
		final StringBuilder sb = new StringBuilder();
		if (isPrefix) {
			appendPaddedNumber(sb);
		}
		sb.append(base);
		if (!isPrefix) {
			appendPaddedNumber(sb);
		}
		return sb.toString();
	}

	private void appendPaddedNumber(StringBuilder sb) {
		final String image = number.toString();
		final int padLength = width - image.length();
		for (int i = 0; i < padLength; ++i) {
			sb.append('0');
		}
		sb.append(image);
	}

}
