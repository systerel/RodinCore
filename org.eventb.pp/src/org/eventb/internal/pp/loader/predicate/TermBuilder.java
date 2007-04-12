/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eventb.internal.pp.loader.predicate;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import org.eventb.core.ast.AssociativeExpression;
import org.eventb.core.ast.AtomicExpression;
import org.eventb.core.ast.BinaryExpression;
import org.eventb.core.ast.BoundIdentifier;
import org.eventb.core.ast.DefaultVisitor;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.IntegerLiteral;
import org.eventb.core.ast.UnaryExpression;
import org.eventb.internal.pp.core.elements.Sort;
import org.eventb.internal.pp.loader.formula.terms.ConstantSignature;
import org.eventb.internal.pp.loader.formula.terms.DivideSignature;
import org.eventb.internal.pp.loader.formula.terms.ExpnSignature;
import org.eventb.internal.pp.loader.formula.terms.MinusSignature;
import org.eventb.internal.pp.loader.formula.terms.ModSignature;
import org.eventb.internal.pp.loader.formula.terms.PlusSignature;
import org.eventb.internal.pp.loader.formula.terms.TermSignature;
import org.eventb.internal.pp.loader.formula.terms.TimesSignature;
import org.eventb.internal.pp.loader.formula.terms.TrueConstantSignature;
import org.eventb.internal.pp.loader.formula.terms.UnaryMinusSignature;
import org.eventb.internal.pp.loader.formula.terms.VariableSignature;

/**
 * This class is the builder for terms.
 *
 * @author François Terrier
 *
 */
public class TermBuilder extends DefaultVisitor {
	
	private Stack<TermSignature> terms;
	private Stack<? extends INormalizedFormula> results;
	
	private boolean invertSign = false;
	public boolean invertSign() {
		return invertSign;
	}
	
	public TermBuilder(Stack<? extends INormalizedFormula> results) {
		assert results != null;

		this.results = results;
	}
	
	public TermSignature buildTerm(Expression expression) {
		assert expression != null;
		
		this.invertSign = false;
		this.terms = new Stack<TermSignature>();
		
		expression.accept(this);
		
		assert terms.size() == 1;
		
		return terms.pop();
	}
	
	@Override
	public boolean exitDIV(BinaryExpression expr) {
		assert terms.size() == 2;
		
		TermSignature right = terms.pop();
		TermSignature left = terms.pop();
		TermSignature div = new DivideSignature(left, right);
		terms.push(div);
		return true;
	}

	@Override
	public boolean exitEXPN(BinaryExpression expr) {
		assert terms.size() == 2;
		
		TermSignature right = terms.pop();
		TermSignature left = terms.pop();
		TermSignature expn = new ExpnSignature(left, right);
		terms.push(expn);
		return true;
	}

	@Override
	public boolean exitMINUS(BinaryExpression expr) {
		assert terms.size() == 2;
		
		TermSignature right = terms.pop();
		TermSignature left = terms.pop();
		TermSignature minus = new MinusSignature(left, right);
		terms.push(minus);
		return true;
	}

	@Override
	public boolean exitMOD(BinaryExpression expr) {
		assert terms.size() == 2;
		
		TermSignature right = terms.pop();
		TermSignature left = terms.pop();
		TermSignature mod = new ModSignature(left, right);
		terms.push(mod);
		return true;
	}

	@Override
	public boolean exitMUL(AssociativeExpression expr) {
		List<TermSignature> temp = new ArrayList<TermSignature>();
		temp.addAll(terms);
		TermSignature times = new TimesSignature(temp);
		terms.removeAllElements();
		terms.push(times);
		return true;
	}

	@Override
	public boolean exitPLUS(AssociativeExpression expr) {
		List<TermSignature> temp = new ArrayList<TermSignature>();
		temp.addAll(terms);
		TermSignature plus = new PlusSignature(temp);
		terms.removeAllElements();
		terms.push(plus);
		return true;
	}

	@Override
	public boolean exitUNMINUS(UnaryExpression expr) {
		assert terms.size() == 1;
		
		TermSignature minus = new UnaryMinusSignature(terms.pop());
		terms.push(minus);
		return true;
	}

	@Override
	public boolean visitBOUND_IDENT(BoundIdentifier ident) {
		int index = getIndex(ident.getBoundIndex());
		TermSignature boundIdent = new VariableSignature(getStartIndex(ident.getBoundIndex()), index, new Sort(ident.getType()));
		terms.push(boundIdent);
		return true;
	}

	@Override
	public boolean visitFREE_IDENT(FreeIdentifier ident) {
		TermSignature freeIdent = new ConstantSignature(ident.getName(), new Sort(ident.getType()));
		terms.push(freeIdent);
		return true;
	}

	@Override
	public boolean visitINTLIT(IntegerLiteral lit) {
		// TODO change?
		TermSignature intlit = new ConstantSignature(lit.getValue().toString(), new Sort(lit.getType()));
		terms.push(intlit);
		return true;
	}

	@Override
	public boolean visitTRUE(AtomicExpression expr) {
		TermSignature tr = new TrueConstantSignature(new Sort(expr.getType()));
		terms.push(tr);
		return true;
	}

	@Override
	public boolean visitFALSE(AtomicExpression expr) {
		invertSign = true;
		TermSignature tr = new TrueConstantSignature(new Sort(expr.getType()));
		terms.push(tr);
		return true;
	}
	
	private int getIndex(int boundIndex) {
		int tmp = 0;
		int i = results.size()-1;
		
		while (i > 0) {
			tmp = tmp+results.get(i).getBoundIdentDecls().length;
			i--;
		}
		return tmp-1-boundIndex;
	}
	
	private int getStartIndex(int boundIndex) {
		int tmp = boundIndex;
		int i = results.size()-1;
		
		while (tmp >= results.get(i).getBoundIdentDecls().length) {
			tmp = tmp - results.get(i).getBoundIdentDecls().length;
			i--;
		}
		return (results.get(i).getBoundIdentDecls().length-tmp-1)+results.get(i).getStartAbsolute();
	}
}
