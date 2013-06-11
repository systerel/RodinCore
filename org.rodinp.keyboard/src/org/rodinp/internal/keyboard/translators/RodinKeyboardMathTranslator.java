/*******************************************************************************
 * Copyright (c) 2006, 2013 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * This used to be abstract class AbstractSymbols. 
 * 
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - supported contribution through extension and at runtime
 *******************************************************************************/
package org.rodinp.internal.keyboard.translators;

import org.rodinp.internal.keyboard.KeyboardCoreUtils;

/**
 * @author htson
 *         <p>
 *         The translator for mathematical symbols.
 *         </p>
 */
public class RodinKeyboardMathTranslator extends
		AbstractRodinKeyboardTranslator {

	public RodinKeyboardMathTranslator() {
		super(false, KeyboardCoreUtils.MATH_DEBUG,
				new SymbolComputer.MathSymbolComputer());
	}

}
