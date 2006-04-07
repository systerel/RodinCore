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
	private FormulaFactory ff;

	public void calculate(Type type, SourceLocation loc, FormulaFactory ff) {
		calculate(type, 0, loc, ff);
	}

	public void calculate(Type type, int offset, SourceLocation loc, FormulaFactory ff) {
		this.ff = ff;
		c = new Counter(offset);
		identDecls = new LinkedList<BoundIdentDecl>();
		maplet = mapletOfType(type, loc);
	}
	
	public Expression getMaplet() {
		return maplet;
	}

	public Expression V() {
		return maplet;
	}
	
	public int offset() {
		return identDecls.size();
	}
	
	public LinkedList<BoundIdentDecl> getIdentDecls() {
		return identDecls;
	}
	
	public LinkedList<BoundIdentDecl> X() {
		return identDecls;
	}
	
	private Expression mapletOfType(Type type, SourceLocation loc) {
		%match (Type type) {
			PowSet(_) | Set(_) -> {
				identDecls.add(ff.makeBoundIdentDecl("X", loc, `type));
				return ff.makeBoundIdentifier(c.increment(), loc, `type);
			}
			
			CProd (left, right) -> {
				return ff.makeBinaryExpression(
					Formula.MAPSTO, 
					mapletOfType(`left, loc),
					mapletOfType(`right, loc),
					loc);
			}
			type -> {
				identDecls.add(ff.makeBoundIdentDecl("x", loc, `type));
				return ff.makeBoundIdentifier(c.increment(), loc, `type);	
			}
		}
	}
}