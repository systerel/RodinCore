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

import static org.eventb.core.ast.BinaryExpression.PFUN_ID;
import static org.eventb.core.ast.BinaryExpression.PINJ_ID;
import static org.eventb.core.ast.BinaryExpression.PSUR_ID;
import static org.eventb.core.ast.BinaryExpression.REL_ID;
import static org.eventb.core.ast.BinaryExpression.SREL_ID;
import static org.eventb.core.ast.BinaryExpression.STREL_ID;
import static org.eventb.core.ast.BinaryExpression.TBIJ_ID;
import static org.eventb.core.ast.BinaryExpression.TFUN_ID;
import static org.eventb.core.ast.BinaryExpression.TINJ_ID;
import static org.eventb.core.ast.BinaryExpression.TREL_ID;
import static org.eventb.core.ast.BinaryExpression.TSUR_ID;

import org.eventb.core.ast.AtomicExpression;
import org.eventb.core.ast.LanguageVersion;
import org.eventb.core.ast.UnaryExpression;

/**
 * @author Nicolas Beauger
 *
 */
public class BMathV1 extends BMath {
	
	protected BMathV1() {
		super(LanguageVersion.V1);
	}

	public static final BMath B_MATH_V1 = new BMathV2();
	static {
		B_MATH_V1.init();
	}
	
	@Override
	protected void addOperators() {
		super.addOperators();
		// AtomicExpression
		AtomicExpression.initV1(this);
		
		// UnaryExpression
		UnaryExpression.initV1(this);
	}
	
	@Override
	protected void addOperatorRelationships() {
		super.addOperatorRelationships();
	
		addCompatibility(REL_ID, REL_ID, LanguageVersion.V1);
		addCompatibility(TREL_ID, TREL_ID, LanguageVersion.V1);
		addCompatibility(SREL_ID, SREL_ID, LanguageVersion.V1);
		addCompatibility(STREL_ID, STREL_ID, LanguageVersion.V1);
		addCompatibility(PFUN_ID, PFUN_ID, LanguageVersion.V1);
		addCompatibility(TFUN_ID, TFUN_ID, LanguageVersion.V1);
		addCompatibility(PINJ_ID, PINJ_ID, LanguageVersion.V1);
		addCompatibility(TINJ_ID, TINJ_ID, LanguageVersion.V1);
		addCompatibility(PSUR_ID, PSUR_ID, LanguageVersion.V1);
		addCompatibility(TSUR_ID, TSUR_ID, LanguageVersion.V1);
		addCompatibility(TBIJ_ID, TBIJ_ID, LanguageVersion.V1);
		
	}

}
