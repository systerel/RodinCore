/*******************************************************************************
 * Copyright (c) 2010 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.core.parser;

import org.eventb.core.ast.AtomicExpression;
import org.eventb.core.ast.LanguageVersion;
import org.eventb.core.ast.MultiplePredicate;
import org.eventb.core.ast.UnaryExpression;

/**
 * @author Nicolas Beauger
 *
 */
public class BMathV2 extends BMath {
	
	protected BMathV2() {
		super(LanguageVersion.V2);
	}

	public static final BMath B_MATH_V2 = new BMathV2();
	static {
		B_MATH_V2.init();
	}
	
	@Override
	protected void addOperators() {
		super.addOperators();

		// AtomicExpression
		AtomicExpression.initV2(this);
		
		// MultiplePredicate
		MultiplePredicate.initV2(this);
		
		// UnaryExpression
		UnaryExpression.initV2(this);
	}

}
