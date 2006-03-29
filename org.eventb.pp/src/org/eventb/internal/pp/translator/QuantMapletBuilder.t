/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eventb.internal.pp.translator;

import java.util.*;
import java.math.BigInteger;

import org.eventb.core.ast.*;


/**
 * ...
 * 
 * @author Matthias Konrad
 */
@SuppressWarnings("unused")
public class QuantMapletBuilder {

%include {Formula.tom}

	private Counter c = null;
	private LinkedList<BoundIdentDecl> identDecls;
	private Expression maplet;

	public void calculate(Type type, int offset, SourceLocation loc, FormulaFactory ff) {
		c = new Counter(offset);
		maplet = mapletOfType(type, loc, ff);
		identDecls = new LinkedList<BoundIdentDecl>();
		for( int i = offset; i < c.value(); i++) {
			identDecls.addLast(ff.makeBoundIdentDecl("x", loc));
		}
	}
	
	public Expression getMaplet() {
		return maplet;
	}
	
	public LinkedList<BoundIdentDecl> getIdentDecls() {
		return identDecls;
	}
	
	private Expression mapletOfType(Type type, SourceLocation loc, FormulaFactory ff) {
		%match (Type type) {
			PowerSetType (_) -> {
				return ff.makeBoundIdentifier(c.increment(), loc, `type);
			}
			ProductType (left, right) -> {
				return ff.makeBinaryExpression(
					Formula.MAPSTO, 
					mapletOfType(`left, loc, ff),
					mapletOfType(`right, loc, ff),
					loc);
			}
			type -> {
				return ff.makeBoundIdentifier(c.increment(), loc, `type);	
			}
		}
	}
}