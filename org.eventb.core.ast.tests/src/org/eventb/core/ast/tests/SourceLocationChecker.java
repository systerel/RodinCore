/*******************************************************************************
 * Copyright (c) 2006, 2012 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - passed methods to protected to allow inheritance
 *******************************************************************************/
package org.eventb.core.ast.tests;

import java.util.Stack;

import org.junit.Assert;

import org.eventb.core.ast.AssociativeExpression;
import org.eventb.core.ast.AssociativePredicate;
import org.eventb.core.ast.AtomicExpression;
import org.eventb.core.ast.BecomesEqualTo;
import org.eventb.core.ast.BecomesMemberOf;
import org.eventb.core.ast.BecomesSuchThat;
import org.eventb.core.ast.BinaryExpression;
import org.eventb.core.ast.BinaryPredicate;
import org.eventb.core.ast.BoolExpression;
import org.eventb.core.ast.BoundIdentDecl;
import org.eventb.core.ast.BoundIdentifier;
import org.eventb.core.ast.DefaultVisitor;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.IntegerLiteral;
import org.eventb.core.ast.LiteralPredicate;
import org.eventb.core.ast.MultiplePredicate;
import org.eventb.core.ast.QuantifiedExpression;
import org.eventb.core.ast.QuantifiedPredicate;
import org.eventb.core.ast.RelationalPredicate;
import org.eventb.core.ast.SetExtension;
import org.eventb.core.ast.SimplePredicate;
import org.eventb.core.ast.SourceLocation;
import org.eventb.core.ast.UnaryExpression;
import org.eventb.core.ast.UnaryPredicate;

/**
 * Simple implementation of the AST visitor to check that source locations are
 * correctly nested into each other, reflecting the formula structure.
 * 
 * @author Laurent Voisin
 */
public class SourceLocationChecker extends DefaultVisitor {

	// Stack of source locations in parents
	Stack<SourceLocation> stack;
	
	public SourceLocationChecker() {
		this.stack = new Stack<SourceLocation>();
	}

	protected boolean enterFormula(Formula<?> formula) {
		visitFormula(formula, true);
		return true;
	}

	protected boolean exitFormula(Formula<?> formula) {
		SourceLocation currentLoc = formula.getSourceLocation();
		if (currentLoc != null) {
			stack.pop();
		}
		return true;
	}
	
	protected boolean visitFormula(Formula<?> formula, boolean push) {
		SourceLocation currentLoc = formula.getSourceLocation();
		if (currentLoc != null) {
			Assert.assertTrue("Improper source location",
					currentLoc.getStart() <= currentLoc.getEnd());
			
			if (! stack.isEmpty()) {
				SourceLocation parentLoc = stack.peek();
				Assert.assertTrue("Improper source location nesting",
						parentLoc.getStart() <= currentLoc.getStart());
				Assert.assertTrue("Improper source location nesting",
						parentLoc.getEnd() >= currentLoc.getEnd());
			}
			
			if (push) {
				stack.push(formula.getSourceLocation());
			}
		}
		return true;
	}

	@Override
	public boolean enterBCOMP(AssociativeExpression expr) {
		return enterFormula(expr);
	}

	@Override
	public boolean exitBCOMP(AssociativeExpression expr) {
		return exitFormula(expr);
	}

	@Override
	public boolean visitBFALSE(LiteralPredicate pred) {
		return visitFormula(pred, false);
	}

	@Override
	public boolean enterBECOMES_EQUAL_TO(BecomesEqualTo assign) {
		return enterFormula(assign);
	}

	@Override
	public boolean enterBECOMES_MEMBER_OF(BecomesMemberOf assign) {
		return enterFormula(assign);
	}

	@Override
	public boolean enterBECOMES_SUCH_THAT(BecomesSuchThat assign) {
		return enterFormula(assign);
	}

	@Override
	public boolean enterBINTER(AssociativeExpression expr) {
		return enterFormula(expr);
	}

	@Override
	public boolean enterBUNION(AssociativeExpression expr) {
		return enterFormula(expr);
	}

	@Override
	public boolean enterCONVERSE(UnaryExpression expr) {
		return enterFormula(expr);
	}

	@Override
	public boolean enterCPROD(BinaryExpression expr) {
		return enterFormula(expr);
	}

	@Override
	public boolean enterCSET(QuantifiedExpression expr) {
		return enterFormula(expr);
	}

	@Override
	public boolean enterDIV(BinaryExpression expr) {
		return enterFormula(expr);
	}

	@Override
	public boolean enterDOMRES(BinaryExpression expr) {
		return enterFormula(expr);
	}

	@Override
	public boolean enterDOMSUB(BinaryExpression expr) {
		return enterFormula(expr);
	}

	@Override
	public boolean enterDPROD(BinaryExpression expr) {
		return enterFormula(expr);
	}

	@Override
	public boolean enterEQUAL(RelationalPredicate pred) {
		return enterFormula(pred);
	}

	@Override
	public boolean enterEXISTS(QuantifiedPredicate pred) {
		return enterFormula(pred);
	}

	@Override
	public boolean enterEXPN(BinaryExpression expr) {
		return enterFormula(expr);
	}

	@Override
	public boolean enterFCOMP(AssociativeExpression expr) {
		return enterFormula(expr);
	}

	@Override
	public boolean enterFORALL(QuantifiedPredicate pred) {
		return enterFormula(pred);
	}

	@Override
	public boolean enterFUNIMAGE(BinaryExpression expr) {
		return enterFormula(expr);
	}

	@Override
	public boolean enterGE(RelationalPredicate pred) {
		return enterFormula(pred);
	}

	@Override
	public boolean enterGT(RelationalPredicate pred) {
		return enterFormula(pred);
	}

	@Override
	public boolean enterIN(RelationalPredicate pred) {
		return enterFormula(pred);
	}

	@Override
	public boolean enterKBOOL(BoolExpression expr) {
		return enterFormula(expr);
	}

	@Override
	public boolean enterKCARD(UnaryExpression expr) {
		return enterFormula(expr);
	}

	@Override
	public boolean enterKDOM(UnaryExpression expr) {
		return enterFormula(expr);
	}

	@Override
	public boolean enterKFINITE(SimplePredicate pred) {
		return enterFormula(pred);
	}

	@Override
	public boolean enterKID(UnaryExpression expr) {
		return enterFormula(expr);
	}

	@Override
	public boolean enterKINTER(UnaryExpression expr) {
		return enterFormula(expr);
	}

	@Override
	public boolean enterKMAX(UnaryExpression expr) {
		return enterFormula(expr);
	}

	@Override
	public boolean enterKMIN(UnaryExpression expr) {
		return enterFormula(expr);
	}

	@Override
	public boolean enterKPARTITION(MultiplePredicate pred) {
		return enterFormula(pred);
	}

	@Override
	public boolean enterKPRJ1(UnaryExpression expr) {
		return enterFormula(expr);
	}

	@Override
	public boolean enterKPRJ2(UnaryExpression expr) {
		return enterFormula(expr);
	}

	@Override
	public boolean enterKRAN(UnaryExpression expr) {
		return enterFormula(expr);
	}

	@Override
	public boolean enterKUNION(UnaryExpression expr) {
		return enterFormula(expr);
	}

	@Override
	public boolean enterLAND(AssociativePredicate pred) {
		return enterFormula(pred);
	}

	@Override
	public boolean enterLE(RelationalPredicate pred) {
		return enterFormula(pred);
	}

	@Override
	public boolean enterLEQV(BinaryPredicate pred) {
		return enterFormula(pred);
	}

	@Override
	public boolean enterLIMP(BinaryPredicate pred) {
		return enterFormula(pred);
	}

	@Override
	public boolean enterLOR(AssociativePredicate pred) {
		return enterFormula(pred);
	}

	@Override
	public boolean enterLT(RelationalPredicate pred) {
		return enterFormula(pred);
	}

	@Override
	public boolean enterMAPSTO(BinaryExpression expr) {
		return enterFormula(expr);
	}

	@Override
	public boolean enterMINUS(BinaryExpression expr) {
		return enterFormula(expr);
	}

	@Override
	public boolean enterMOD(BinaryExpression expr) {
		return enterFormula(expr);
	}

	@Override
	public boolean enterMUL(AssociativeExpression expr) {
		return enterFormula(expr);
	}

	@Override
	public boolean enterNOT(UnaryPredicate pred) {
		return enterFormula(pred);
	}

	@Override
	public boolean enterNOTEQUAL(RelationalPredicate pred) {
		return enterFormula(pred);
	}

	@Override
	public boolean enterNOTIN(RelationalPredicate pred) {
		return enterFormula(pred);
	}

	@Override
	public boolean enterNOTSUBSET(RelationalPredicate pred) {
		return enterFormula(pred);
	}

	@Override
	public boolean enterNOTSUBSETEQ(RelationalPredicate pred) {
		return enterFormula(pred);
	}

	@Override
	public boolean enterOVR(AssociativeExpression expr) {
		return enterFormula(expr);
	}

	@Override
	public boolean enterPFUN(BinaryExpression expr) {
		return enterFormula(expr);
	}

	@Override
	public boolean enterPINJ(BinaryExpression expr) {
		return enterFormula(expr);
	}

	@Override
	public boolean enterPLUS(AssociativeExpression expr) {
		return enterFormula(expr);
	}

	@Override
	public boolean enterPOW(UnaryExpression expr) {
		return enterFormula(expr);
	}

	@Override
	public boolean enterPOW1(UnaryExpression expr) {
		return enterFormula(expr);
	}

	@Override
	public boolean enterPPROD(BinaryExpression expr) {
		return enterFormula(expr);
	}

	@Override
	public boolean enterPSUR(BinaryExpression expr) {
		return enterFormula(expr);
	}

	@Override
	public boolean enterQINTER(QuantifiedExpression expr) {
		return enterFormula(expr);
	}

	@Override
	public boolean enterQUNION(QuantifiedExpression expr) {
		return enterFormula(expr);
	}

	@Override
	public boolean enterRANRES(BinaryExpression expr) {
		return enterFormula(expr);
	}

	@Override
	public boolean enterRANSUB(BinaryExpression expr) {
		return enterFormula(expr);
	}

	@Override
	public boolean enterREL(BinaryExpression expr) {
		return enterFormula(expr);
	}

	@Override
	public boolean enterRELIMAGE(BinaryExpression expr) {
		return enterFormula(expr);
	}

	@Override
	public boolean enterSETEXT(SetExtension set) {
		return enterFormula(set);
	}

	@Override
	public boolean enterSETMINUS(BinaryExpression expr) {
		return enterFormula(expr);
	}

	@Override
	public boolean enterSREL(BinaryExpression expr) {
		return enterFormula(expr);
	}

	@Override
	public boolean enterSTREL(BinaryExpression expr) {
		return enterFormula(expr);
	}

	@Override
	public boolean enterSUBSET(RelationalPredicate pred) {
		return enterFormula(pred);
	}

	@Override
	public boolean enterSUBSETEQ(RelationalPredicate pred) {
		return enterFormula(pred);
	}

	@Override
	public boolean enterTBIJ(BinaryExpression expr) {
		return enterFormula(expr);
	}

	@Override
	public boolean enterTFUN(BinaryExpression expr) {
		return enterFormula(expr);
	}

	@Override
	public boolean enterTINJ(BinaryExpression expr) {
		return enterFormula(expr);
	}

	@Override
	public boolean enterTREL(BinaryExpression expr) {
		return enterFormula(expr);
	}

	@Override
	public boolean enterTSUR(BinaryExpression expr) {
		return enterFormula(expr);
	}

	@Override
	public boolean enterUNMINUS(UnaryExpression expr) {
		return enterFormula(expr);
	}

	@Override
	public boolean enterUPTO(BinaryExpression expr) {
		return enterFormula(expr);
	}

	@Override
	public boolean exitBECOMES_EQUAL_TO(BecomesEqualTo assign) {
		return exitFormula(assign);
	}

	@Override
	public boolean exitBECOMES_MEMBER_OF(BecomesMemberOf assign) {
		return exitFormula(assign);
	}

	@Override
	public boolean exitBECOMES_SUCH_THAT(BecomesSuchThat assign) {
		return exitFormula(assign);
	}

	@Override
	public boolean exitBINTER(AssociativeExpression expr) {
		return exitFormula(expr);
	}

	@Override
	public boolean exitBUNION(AssociativeExpression expr) {
		return exitFormula(expr);
	}

	@Override
	public boolean exitCONVERSE(UnaryExpression expr) {
		return exitFormula(expr);
	}

	@Override
	public boolean exitCPROD(BinaryExpression expr) {
		return exitFormula(expr);
	}

	@Override
	public boolean exitCSET(QuantifiedExpression expr) {
		return exitFormula(expr);
	}

	@Override
	public boolean exitDIV(BinaryExpression expr) {
		return exitFormula(expr);
	}

	@Override
	public boolean exitDOMRES(BinaryExpression expr) {
		return exitFormula(expr);
	}

	@Override
	public boolean exitDOMSUB(BinaryExpression expr) {
		return exitFormula(expr);
	}

	@Override
	public boolean exitDPROD(BinaryExpression expr) {
		return exitFormula(expr);
	}

	@Override
	public boolean exitEQUAL(RelationalPredicate pred) {
		return exitFormula(pred);
	}

	@Override
	public boolean exitEXISTS(QuantifiedPredicate pred) {
		return exitFormula(pred);
	}

	@Override
	public boolean exitEXPN(BinaryExpression expr) {
		return exitFormula(expr);
	}

	@Override
	public boolean exitFCOMP(AssociativeExpression expr) {
		return exitFormula(expr);
	}

	@Override
	public boolean exitFORALL(QuantifiedPredicate pred) {
		return exitFormula(pred);
	}

	@Override
	public boolean exitFUNIMAGE(BinaryExpression expr) {
		return exitFormula(expr);
	}

	@Override
	public boolean exitGE(RelationalPredicate pred) {
		return exitFormula(pred);
	}

	@Override
	public boolean exitGT(RelationalPredicate pred) {
		return exitFormula(pred);
	}

	@Override
	public boolean exitIN(RelationalPredicate pred) {
		return exitFormula(pred);
	}

	@Override
	public boolean exitKBOOL(BoolExpression expr) {
		return exitFormula(expr);
	}

	@Override
	public boolean exitKCARD(UnaryExpression expr) {
		return exitFormula(expr);
	}

	@Override
	public boolean exitKDOM(UnaryExpression expr) {
		return exitFormula(expr);
	}

	@Override
	public boolean exitKFINITE(SimplePredicate pred) {
		return exitFormula(pred);
	}

	@Override
	public boolean exitKID(UnaryExpression expr) {
		return exitFormula(expr);
	}

	@Override
	public boolean exitKINTER(UnaryExpression expr) {
		return exitFormula(expr);
	}

	@Override
	public boolean exitKMAX(UnaryExpression expr) {
		return exitFormula(expr);
	}

	@Override
	public boolean exitKMIN(UnaryExpression expr) {
		return exitFormula(expr);
	}
	
	@Override
	public boolean exitKPARTITION(MultiplePredicate pred) {
		return exitFormula(pred);
	}

	@Override
	public boolean exitKPRJ1(UnaryExpression expr) {
		return exitFormula(expr);
	}

	@Override
	public boolean exitKPRJ2(UnaryExpression expr) {
		return exitFormula(expr);
	}

	@Override
	public boolean exitKRAN(UnaryExpression expr) {
		return exitFormula(expr);
	}

	@Override
	public boolean exitKUNION(UnaryExpression expr) {
		return exitFormula(expr);
	}

	@Override
	public boolean exitLAND(AssociativePredicate pred) {
		return exitFormula(pred);
	}

	@Override
	public boolean exitLE(RelationalPredicate pred) {
		return exitFormula(pred);
	}

	@Override
	public boolean exitLEQV(BinaryPredicate pred) {
		return exitFormula(pred);
	}

	@Override
	public boolean exitLIMP(BinaryPredicate pred) {
		return exitFormula(pred);
	}

	@Override
	public boolean exitLOR(AssociativePredicate pred) {
		return exitFormula(pred);
	}

	@Override
	public boolean exitLT(RelationalPredicate pred) {
		return exitFormula(pred);
	}

	@Override
	public boolean exitMAPSTO(BinaryExpression expr) {
		return exitFormula(expr);
	}

	@Override
	public boolean exitMINUS(BinaryExpression expr) {
		return exitFormula(expr);
	}

	@Override
	public boolean exitMOD(BinaryExpression expr) {
		return exitFormula(expr);
	}

	@Override
	public boolean exitMUL(AssociativeExpression expr) {
		return exitFormula(expr);
	}

	@Override
	public boolean exitNOT(UnaryPredicate pred) {
		return exitFormula(pred);
	}

	@Override
	public boolean exitNOTEQUAL(RelationalPredicate pred) {
		return exitFormula(pred);
	}

	@Override
	public boolean exitNOTIN(RelationalPredicate pred) {
		return exitFormula(pred);
	}

	@Override
	public boolean exitNOTSUBSET(RelationalPredicate pred) {
		return exitFormula(pred);
	}

	@Override
	public boolean exitNOTSUBSETEQ(RelationalPredicate pred) {
		return exitFormula(pred);
	}

	@Override
	public boolean exitOVR(AssociativeExpression expr) {
		return exitFormula(expr);
	}

	@Override
	public boolean exitPFUN(BinaryExpression expr) {
		return exitFormula(expr);
	}

	@Override
	public boolean exitPINJ(BinaryExpression expr) {
		return exitFormula(expr);
	}

	@Override
	public boolean exitPLUS(AssociativeExpression expr) {
		return exitFormula(expr);
	}

	@Override
	public boolean exitPOW(UnaryExpression expr) {
		return exitFormula(expr);
	}

	@Override
	public boolean exitPOW1(UnaryExpression expr) {
		return exitFormula(expr);
	}

	@Override
	public boolean exitPPROD(BinaryExpression expr) {
		return exitFormula(expr);
	}

	@Override
	public boolean exitPSUR(BinaryExpression expr) {
		return exitFormula(expr);
	}

	@Override
	public boolean exitQINTER(QuantifiedExpression expr) {
		return exitFormula(expr);
	}

	@Override
	public boolean exitQUNION(QuantifiedExpression expr) {
		return exitFormula(expr);
	}

	@Override
	public boolean exitRANRES(BinaryExpression expr) {
		return exitFormula(expr);
	}

	@Override
	public boolean exitRANSUB(BinaryExpression expr) {
		return exitFormula(expr);
	}

	@Override
	public boolean exitREL(BinaryExpression expr) {
		return exitFormula(expr);
	}

	@Override
	public boolean exitRELIMAGE(BinaryExpression expr) {
		return exitFormula(expr);
	}

	@Override
	public boolean exitSETEXT(SetExtension set) {
		return exitFormula(set);
	}

	@Override
	public boolean exitSETMINUS(BinaryExpression expr) {
		return exitFormula(expr);
	}

	@Override
	public boolean exitSREL(BinaryExpression expr) {
		return exitFormula(expr);
	}

	@Override
	public boolean exitSTREL(BinaryExpression expr) {
		return exitFormula(expr);
	}

	@Override
	public boolean exitSUBSET(RelationalPredicate pred) {
		return exitFormula(pred);
	}

	@Override
	public boolean exitSUBSETEQ(RelationalPredicate pred) {
		return exitFormula(pred);
	}

	@Override
	public boolean exitTBIJ(BinaryExpression expr) {
		return exitFormula(expr);
	}

	@Override
	public boolean exitTFUN(BinaryExpression expr) {
		return exitFormula(expr);
	}

	@Override
	public boolean exitTINJ(BinaryExpression expr) {
		return exitFormula(expr);
	}

	@Override
	public boolean exitTREL(BinaryExpression expr) {
		return exitFormula(expr);
	}

	@Override
	public boolean exitTSUR(BinaryExpression expr) {
		return exitFormula(expr);
	}

	@Override
	public boolean exitUNMINUS(UnaryExpression expr) {
		return exitFormula(expr);
	}

	@Override
	public boolean exitUPTO(BinaryExpression expr) {
		return exitFormula(expr);
	}

	@Override
	public boolean visitBOOL(AtomicExpression expr) {
		return visitFormula(expr, false);
	}

	@Override
	public boolean visitBOUND_IDENT_DECL(BoundIdentDecl ident) {
		return visitFormula(ident, false);
	}

	@Override
	public boolean visitBOUND_IDENT(BoundIdentifier ident) {
		return visitFormula(ident, false);
	}

	@Override
	public boolean visitBTRUE(LiteralPredicate pred) {
		return visitFormula(pred, false);
	}

	@Override
	public boolean visitEMPTYSET(AtomicExpression expr) {
		return visitFormula(expr, false);
	}

	@Override
	public boolean visitFALSE(AtomicExpression expr) {
		return visitFormula(expr, false);
	}

	@Override
	public boolean visitFREE_IDENT(FreeIdentifier ident) {
		return visitFormula(ident, false);
	}

	@Override
	public boolean visitINTEGER(AtomicExpression expr) {
		return visitFormula(expr, false);
	}

	@Override
	public boolean visitINTLIT(IntegerLiteral lit) {
		return visitFormula(lit, false);
	}

	@Override
	public boolean visitKID_GEN(AtomicExpression expr) {
		return visitFormula(expr, false);
	}

	@Override
	public boolean visitKPRED(AtomicExpression expr) {
		return visitFormula(expr, false);
	}

	@Override
	public boolean visitKPRJ1_GEN(AtomicExpression expr) {
		return visitFormula(expr, false);
	}

	@Override
	public boolean visitKPRJ2_GEN(AtomicExpression expr) {
		return visitFormula(expr, false);
	}

	@Override
	public boolean visitKSUCC(AtomicExpression expr) {
		return visitFormula(expr, false);
	}

	@Override
	public boolean visitNATURAL(AtomicExpression expr) {
		return visitFormula(expr, false);
	}

	@Override
	public boolean visitNATURAL1(AtomicExpression expr) {
		return visitFormula(expr, false);
	}

	@Override
	public boolean visitTRUE(AtomicExpression expr) {
		return visitFormula(expr, false);
	}

}
