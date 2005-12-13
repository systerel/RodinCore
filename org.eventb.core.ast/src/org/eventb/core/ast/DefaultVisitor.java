/*******************************************************************************
 * Copyright (c) 2005 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eventb.core.ast;

/**
 * Default visitor that does nothing, while traversing the whole AST.
 * <p>
 * This class is intended to be extended by clients, adding useful behavior.
 * </p>
 * 
 * @author Laurent Voisin
 */
public class DefaultVisitor implements IVisitor {

	public boolean visitFREE_IDENT(FreeIdentifier ident) {
		return true;
	}

	public boolean visitBOUND_IDENT_DECL(BoundIdentDecl ident) {
		return true;
	}

	public boolean visitBOUND_IDENT(BoundIdentifier ident) {
		return true;
	}

	public boolean visitINTLIT(IntegerLiteral lit) {
		return true;
	}

	public boolean enterSETEXT(SetExtension set) {
		return true;
	}

	public boolean exitSETEXT(SetExtension set) {
		return true;
	}

	public boolean enterEQUAL(RelationalPredicate pred) {
		return true;
	}

	public boolean exitEQUAL(RelationalPredicate pred) {
		return true;
	}

	public boolean enterNOTEQUAL(RelationalPredicate pred) {
		return true;
	}

	public boolean exitNOTEQUAL(RelationalPredicate pred) {
		return true;
	}

	public boolean enterLT(RelationalPredicate pred) {
		return true;
	}

	public boolean exitLT(RelationalPredicate pred) {
		return true;
	}

	public boolean enterLE(RelationalPredicate pred) {
		return true;
	}

	public boolean exitLE(RelationalPredicate pred) {
		return true;
	}

	public boolean enterGT(RelationalPredicate pred) {
		return true;
	}

	public boolean exitGT(RelationalPredicate pred) {
		return true;
	}

	public boolean enterGE(RelationalPredicate pred) {
		return true;
	}

	public boolean exitGE(RelationalPredicate pred) {
		return true;
	}

	public boolean enterIN(RelationalPredicate pred) {
		return true;
	}

	public boolean exitIN(RelationalPredicate pred) {
		return true;
	}

	public boolean enterNOTIN(RelationalPredicate pred) {
		return true;
	}

	public boolean exitNOTIN(RelationalPredicate pred) {
		return true;
	}

	public boolean enterSUBSET(RelationalPredicate pred) {
		return true;
	}

	public boolean exitSUBSET(RelationalPredicate pred) {
		return true;
	}

	public boolean enterNOTSUBSET(RelationalPredicate pred) {
		return true;
	}

	public boolean exitNOTSUBSET(RelationalPredicate pred) {
		return true;
	}

	public boolean enterSUBSETEQ(RelationalPredicate pred) {
		return true;
	}

	public boolean exitSUBSETEQ(RelationalPredicate pred) {
		return true;
	}

	public boolean enterNOTSUBSETEQ(RelationalPredicate pred) {
		return true;
	}

	public boolean exitNOTSUBSETEQ(RelationalPredicate pred) {
		return true;
	}

	public boolean enterMAPSTO(BinaryExpression expr) {
		return true;
	}

	public boolean exitMAPSTO(BinaryExpression expr) {
		return true;
	}

	public boolean enterREL(BinaryExpression expr) {
		return true;
	}

	public boolean exitREL(BinaryExpression expr) {
		return true;
	}

	public boolean enterTREL(BinaryExpression expr) {
		return true;
	}

	public boolean exitTREL(BinaryExpression expr) {
		return true;
	}

	public boolean enterSREL(BinaryExpression expr) {
		return true;
	}

	public boolean exitSREL(BinaryExpression expr) {
		return true;
	}

	public boolean enterSTREL(BinaryExpression expr) {
		return true;
	}

	public boolean exitSTREL(BinaryExpression expr) {
		return true;
	}

	public boolean enterPFUN(BinaryExpression expr) {
		return true;
	}

	public boolean exitPFUN(BinaryExpression expr) {
		return true;
	}

	public boolean enterTFUN(BinaryExpression expr) {
		return true;
	}

	public boolean exitTFUN(BinaryExpression expr) {
		return true;
	}

	public boolean enterPINJ(BinaryExpression expr) {
		return true;
	}

	public boolean exitPINJ(BinaryExpression expr) {
		return true;
	}

	public boolean enterTINJ(BinaryExpression expr) {
		return true;
	}

	public boolean exitTINJ(BinaryExpression expr) {
		return true;
	}

	public boolean enterPSUR(BinaryExpression expr) {
		return true;
	}

	public boolean exitPSUR(BinaryExpression expr) {
		return true;
	}

	public boolean enterTSUR(BinaryExpression expr) {
		return true;
	}

	public boolean exitTSUR(BinaryExpression expr) {
		return true;
	}

	public boolean enterTBIJ(BinaryExpression expr) {
		return true;
	}

	public boolean exitTBIJ(BinaryExpression expr) {
		return true;
	}

	public boolean enterSETMINUS(BinaryExpression expr) {
		return true;
	}

	public boolean exitSETMINUS(BinaryExpression expr) {
		return true;
	}

	public boolean enterCPROD(BinaryExpression expr) {
		return true;
	}

	public boolean exitCPROD(BinaryExpression expr) {
		return true;
	}

	public boolean enterDPROD(BinaryExpression expr) {
		return true;
	}

	public boolean exitDPROD(BinaryExpression expr) {
		return true;
	}

	public boolean enterPPROD(BinaryExpression expr) {
		return true;
	}

	public boolean exitPPROD(BinaryExpression expr) {
		return true;
	}

	public boolean enterDOMRES(BinaryExpression expr) {
		return true;
	}

	public boolean exitDOMRES(BinaryExpression expr) {
		return true;
	}

	public boolean enterDOMSUB(BinaryExpression expr) {
		return true;
	}

	public boolean exitDOMSUB(BinaryExpression expr) {
		return true;
	}

	public boolean enterRANRES(BinaryExpression expr) {
		return true;
	}

	public boolean exitRANRES(BinaryExpression expr) {
		return true;
	}

	public boolean enterRANSUB(BinaryExpression expr) {
		return true;
	}

	public boolean exitRANSUB(BinaryExpression expr) {
		return true;
	}

	public boolean enterUPTO(BinaryExpression expr) {
		return true;
	}

	public boolean exitUPTO(BinaryExpression expr) {
		return true;
	}

	public boolean enterMINUS(BinaryExpression expr) {
		return true;
	}

	public boolean exitMINUS(BinaryExpression expr) {
		return true;
	}

	public boolean enterDIV(BinaryExpression expr) {
		return true;
	}

	public boolean exitDIV(BinaryExpression expr) {
		return true;
	}

	public boolean enterMOD(BinaryExpression expr) {
		return true;
	}

	public boolean exitMOD(BinaryExpression expr) {
		return true;
	}

	public boolean enterEXPN(BinaryExpression expr) {
		return true;
	}

	public boolean exitEXPN(BinaryExpression expr) {
		return true;
	}

	public boolean enterFUNIMAGE(BinaryExpression expr) {
		return true;
	}

	public boolean exitFUNIMAGE(BinaryExpression expr) {
		return true;
	}

	public boolean enterRELIMAGE(BinaryExpression expr) {
		return true;
	}

	public boolean exitRELIMAGE(BinaryExpression expr) {
		return true;
	}

	public boolean enterLIMP(BinaryPredicate pred) {
		return true;
	}

	public boolean exitLIMP(BinaryPredicate pred) {
		return true;
	}

	public boolean enterLEQV(BinaryPredicate pred) {
		return true;
	}

	public boolean exitLEQV(BinaryPredicate pred) {
		return true;
	}

	public boolean enterBUNION(AssociativeExpression expr) {
		return true;
	}

	public boolean exitBUNION(AssociativeExpression expr) {
		return true;
	}

	public boolean enterBINTER(AssociativeExpression expr) {
		return true;
	}

	public boolean exitBINTER(AssociativeExpression expr) {
		return true;
	}

	public boolean enterBCOMP(AssociativeExpression expr) {
		return true;
	}

	public boolean exitBCOMP(AssociativeExpression expr) {
		return true;
	}

	public boolean enterFCOMP(AssociativeExpression expr) {
		return true;
	}

	public boolean exitFCOMP(AssociativeExpression expr) {
		return true;
	}

	public boolean enterOVR(AssociativeExpression expr) {
		return true;
	}

	public boolean exitOVR(AssociativeExpression expr) {
		return true;
	}

	public boolean enterPLUS(AssociativeExpression expr) {
		return true;
	}

	public boolean exitPLUS(AssociativeExpression expr) {
		return true;
	}

	public boolean enterMUL(AssociativeExpression expr) {
		return true;
	}

	public boolean exitMUL(AssociativeExpression expr) {
		return true;
	}

	public boolean enterLAND(AssociativePredicate pred) {
		return true;
	}

	public boolean exitLAND(AssociativePredicate pred) {
		return true;
	}

	public boolean enterLOR(AssociativePredicate pred) {
		return true;
	}

	public boolean exitLOR(AssociativePredicate pred) {
		return true;
	}

	public boolean visitINTEGER(AtomicExpression expr) {
		return true;
	}

	public boolean visitNATURAL(AtomicExpression expr) {
		return true;
	}

	public boolean visitNATURAL1(AtomicExpression expr) {
		return true;
	}

	public boolean visitBOOL(AtomicExpression expr) {
		return true;
	}

	public boolean visitTRUE(AtomicExpression expr) {
		return true;
	}

	public boolean visitFALSE(AtomicExpression expr) {
		return true;
	}

	public boolean visitEMPTYSET(AtomicExpression expr) {
		return true;
	}

	public boolean visitKPRED(AtomicExpression expr) {
		return true;
	}

	public boolean visitKSUCC(AtomicExpression expr) {
		return true;
	}

	public boolean enterKBOOL(BoolExpression expr) {
		return true;
	}

	public boolean exitKBOOL(BoolExpression expr) {
		return true;
	}

	public boolean visitBTRUE(LiteralPredicate pred) {
		return true;
	}

	public boolean visitBFALSE(LiteralPredicate pred) {
		return true;
	}

	public boolean enterKFINITE(SimplePredicate pred) {
		return true;
	}

	public boolean exitKFINITE(SimplePredicate pred) {
		return true;
	}

	public boolean enterNOT(UnaryPredicate pred) {
		return true;
	}

	public boolean exitNOT(UnaryPredicate pred) {
		return true;
	}

	public boolean enterKCARD(UnaryExpression expr) {
		return true;
	}

	public boolean exitKCARD(UnaryExpression expr) {
		return true;
	}

	public boolean enterPOW(UnaryExpression expr) {
		return true;
	}

	public boolean exitPOW(UnaryExpression expr) {
		return true;
	}

	public boolean enterPOW1(UnaryExpression expr) {
		return true;
	}

	public boolean exitPOW1(UnaryExpression expr) {
		return true;
	}

	public boolean enterKUNION(UnaryExpression expr) {
		return true;
	}

	public boolean exitKUNION(UnaryExpression expr) {
		return true;
	}

	public boolean enterKINTER(UnaryExpression expr) {
		return true;
	}

	public boolean exitKINTER(UnaryExpression expr) {
		return true;
	}

	public boolean enterKDOM(UnaryExpression expr) {
		return true;
	}

	public boolean exitKDOM(UnaryExpression expr) {
		return true;
	}

	public boolean enterKRAN(UnaryExpression expr) {
		return true;
	}

	public boolean exitKRAN(UnaryExpression expr) {
		return true;
	}

	public boolean enterKPRJ1(UnaryExpression expr) {
		return true;
	}

	public boolean exitKPRJ1(UnaryExpression expr) {
		return true;
	}

	public boolean enterKPRJ2(UnaryExpression expr) {
		return true;
	}

	public boolean exitKPRJ2(UnaryExpression expr) {
		return true;
	}

	public boolean enterKID(UnaryExpression expr) {
		return true;
	}

	public boolean exitKID(UnaryExpression expr) {
		return true;
	}

	public boolean enterKMIN(UnaryExpression expr) {
		return true;
	}

	public boolean exitKMIN(UnaryExpression expr) {
		return true;
	}

	public boolean enterKMAX(UnaryExpression expr) {
		return true;
	}

	public boolean exitKMAX(UnaryExpression expr) {
		return true;
	}

	public boolean enterCONVERSE(UnaryExpression expr) {
		return true;
	}

	public boolean exitCONVERSE(UnaryExpression expr) {
		return true;
	}

	public boolean enterUNMINUS(UnaryExpression expr) {
		return true;
	}

	public boolean exitUNMINUS(UnaryExpression expr) {
		return true;
	}

	public boolean enterQUNION(QuantifiedExpression expr) {
		return true;
	}

	public boolean exitQUNION(QuantifiedExpression expr) {
		return true;
	}

	public boolean enterQINTER(QuantifiedExpression expr) {
		return true;
	}

	public boolean exitQINTER(QuantifiedExpression expr) {
		return true;
	}

	public boolean enterCSET(QuantifiedExpression expr) {
		return true;
	}

	public boolean exitCSET(QuantifiedExpression expr) {
		return true;
	}

	public boolean enterFORALL(QuantifiedPredicate pred) {
		return true;
	}

	public boolean exitFORALL(QuantifiedPredicate pred) {
		return true;
	}

	public boolean enterEXISTS(QuantifiedPredicate pred) {
		return true;
	}

	public boolean exitEXISTS(QuantifiedPredicate pred) {
		return true;
	}

}
