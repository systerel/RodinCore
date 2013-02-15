/*******************************************************************************
 * Copyright (c) 2010, 2013 Systerel and others.
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
import org.eventb.core.ast.UnaryExpression;

/**
 * @author Nicolas Beauger
 *
 */
public class BMathV1 extends BMath {
	
	protected BMathV1() {
		// constructor is protected
	}

	public static final BMath B_MATH_V1 = new BMathV1();
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
	
		addCompatibility(REL_ID, REL_ID);
		addCompatibility(TREL_ID, TREL_ID);
		addCompatibility(SREL_ID, SREL_ID);
		addCompatibility(STREL_ID, STREL_ID);
		addCompatibility(PFUN_ID, PFUN_ID);
		addCompatibility(TFUN_ID, TFUN_ID);
		addCompatibility(PINJ_ID, PINJ_ID);
		addCompatibility(TINJ_ID, TINJ_ID);
		addCompatibility(PSUR_ID, PSUR_ID);
		addCompatibility(TSUR_ID, TSUR_ID);
		addCompatibility(TBIJ_ID, TBIJ_ID);
		
	}

	@Override
	public String toString() {
		return "V1";
	}

}
