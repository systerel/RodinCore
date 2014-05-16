/*******************************************************************************
 * Copyright (c) 2014 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.core.ast.tests.extension;

import static org.eventb.core.ast.FormulaFactory.getCond;
import static org.eventb.core.ast.FormulaFactory.getInstance;
import static org.eventb.core.ast.tests.FastFactory.mDatatypeFactory;
import static org.eventb.core.ast.tests.extension.Extensions.EXTS_FAC;
import static org.junit.Assert.assertSame;

import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.tests.AbstractTests;
import org.eventb.internal.core.ast.extension.ExtensionTranslation;
import org.junit.Test;

/**
 * Unit tests about the computation of the target factory by
 * {@link ExtensionTranslation}.
 * 
 * @author Thomas Muller
 */
public class TestTranslationFactory extends AbstractTests {

	private static final FormulaFactory COND_FAC = getInstance(getCond());

	/**
	 * Checks that languages which do not contain extensions that can be
	 * translated remain unchanged. There are three particular cases to be
	 * checked:<br>
	 * <ul>
	 * <li>the language does not contain any extensions at all</li>
	 * <li>the language contains only datatype extensions</li>
	 * <li>the language contains only non WD strict extensions</li>
	 * </ul>
	 */
	@Test
	public void notTranslatedTargetLanguage() {
		assertLanguageTranslation(ff, ff);
		assertLanguageTranslation(LIST_FAC, LIST_FAC);
		assertLanguageTranslation(COND_FAC, COND_FAC);
	}

	/**
	 * Checks that extensions which can be translated among others that cannot,
	 * are not present in the target language.
	 */
	@Test
	public void extensionLanguage() {
		assertLanguageTranslation(EXTS_FAC, COND_FAC);
	}

	/**
	 * Checks that extensions which can be translated mixed with datatype
	 * extensions are not present in the target language.
	 */
	@Test
	public void remainingExtensions() {
		final String DT_SPEC = "A[T] ::= a[d: T]";
		assertLanguageTranslation(mDatatypeFactory(EXTS_FAC, DT_SPEC),
				mDatatypeFactory(COND_FAC, DT_SPEC));
	}

	private void assertLanguageTranslation(FormulaFactory srcFactory,
			FormulaFactory expected) {
		final ExtensionTranslation translation = new ExtensionTranslation(
				srcFactory.makeTypeEnvironment().makeSnapshot());
		assertSame(expected, translation.getTargetFactory());
	}

}
