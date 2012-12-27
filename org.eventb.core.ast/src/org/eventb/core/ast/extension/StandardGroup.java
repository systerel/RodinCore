/*******************************************************************************
 * Copyright (c) 2010, 2012 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.core.ast.extension;

import org.eventb.internal.core.ast.ASTPlugin;

/**
 * An enumeration of the standard operator groups in the mathematical language.
 * 
 * @since 2.5
 */
public enum StandardGroup {
	GROUP_0("group0", "Least Priority Group"),
	RELOP_PRED("relOp", "Relational Operator Predicate"),
	QUANTIFICATION("quantification", "Quantification"),
	PAIR("pair", "Pair"),
	RELATION("relation", "Set of Relations"),
	BINOP("binOp", "Binary Operator"),
	INTERVAL("interval", "Interval"),
	ARITHMETIC("arithmetic", "Arithmetic"),
	UNARY_RELATION("unaryRelation", "Unary Relation"),
	TYPED("typed", "Typed"),
	FUNCTIONAL("functional", "Functional"),
	BRACE_SETS("braceSets", "Brace Sets"),
	QUANTIFIED_PRED("quantifiedPred", "Quantified"),
	LOGIC_PRED("logicPred", "Logic Predicate"),
	INFIX_PRED("infixPred", "Infix Predicate"),
	NOT_PRED("notPred", "Not Predicate"),
	ATOMIC_PRED("atomicPred", "Atomic Predicate"),
	ATOMIC_EXPR("atomicExpr", "Atomic Expression"),
	CLOSED("closed", "Closed"),
	BOOL_EXPR("boolExpr", "Bool"),
	INFIX_SUBST("infixSubst", "Infix Substitution"),
	;
	private final String id;
	private final String name;
	private StandardGroup(String bareId, String name) {
		this.id = ASTPlugin.PLUGIN_ID + "." + bareId;
		this.name = name;
	}
	
	public String getId() {
		return id;
	}
	
	public String getName() {
		return name;
	}
}