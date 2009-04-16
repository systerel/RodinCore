/*******************************************************************************
 * Copyright (c) 2005, 2009 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - mathematical language v2
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

	public boolean continueSETEXT(SetExtension set) {
		return true;
	}

	public boolean exitSETEXT(SetExtension set) {
		return true;
	}

	public boolean enterEQUAL(RelationalPredicate pred) {
		return true;
	}

	public boolean continueEQUAL(RelationalPredicate pred) {
		return true;
	}

	public boolean exitEQUAL(RelationalPredicate pred) {
		return true;
	}

	public boolean enterNOTEQUAL(RelationalPredicate pred) {
		return true;
	}

	public boolean continueNOTEQUAL(RelationalPredicate pred) {
		return true;
	}

	public boolean exitNOTEQUAL(RelationalPredicate pred) {
		return true;
	}

	public boolean enterLT(RelationalPredicate pred) {
		return true;
	}

	public boolean continueLT(RelationalPredicate pred) {
		return true;
	}

	public boolean exitLT(RelationalPredicate pred) {
		return true;
	}

	public boolean enterLE(RelationalPredicate pred) {
		return true;
	}

	public boolean continueLE(RelationalPredicate pred) {
		return true;
	}

	public boolean exitLE(RelationalPredicate pred) {
		return true;
	}

	public boolean enterGT(RelationalPredicate pred) {
		return true;
	}

	public boolean continueGT(RelationalPredicate pred) {
		return true;
	}

	public boolean exitGT(RelationalPredicate pred) {
		return true;
	}

	public boolean enterGE(RelationalPredicate pred) {
		return true;
	}

	public boolean continueGE(RelationalPredicate pred) {
		return true;
	}

	public boolean exitGE(RelationalPredicate pred) {
		return true;
	}

	public boolean enterIN(RelationalPredicate pred) {
		return true;
	}

	public boolean continueIN(RelationalPredicate pred) {
		return true;
	}

	public boolean exitIN(RelationalPredicate pred) {
		return true;
	}

	public boolean enterNOTIN(RelationalPredicate pred) {
		return true;
	}

	public boolean continueNOTIN(RelationalPredicate pred) {
		return true;
	}

	public boolean exitNOTIN(RelationalPredicate pred) {
		return true;
	}

	public boolean enterSUBSET(RelationalPredicate pred) {
		return true;
	}

	public boolean continueSUBSET(RelationalPredicate pred) {
		return true;
	}

	public boolean exitSUBSET(RelationalPredicate pred) {
		return true;
	}

	public boolean enterNOTSUBSET(RelationalPredicate pred) {
		return true;
	}

	public boolean continueNOTSUBSET(RelationalPredicate pred) {
		return true;
	}

	public boolean exitNOTSUBSET(RelationalPredicate pred) {
		return true;
	}

	public boolean enterSUBSETEQ(RelationalPredicate pred) {
		return true;
	}

	public boolean continueSUBSETEQ(RelationalPredicate pred) {
		return true;
	}

	public boolean exitSUBSETEQ(RelationalPredicate pred) {
		return true;
	}

	public boolean enterNOTSUBSETEQ(RelationalPredicate pred) {
		return true;
	}

	public boolean continueNOTSUBSETEQ(RelationalPredicate pred) {
		return true;
	}

	public boolean exitNOTSUBSETEQ(RelationalPredicate pred) {
		return true;
	}

	public boolean enterMAPSTO(BinaryExpression expr) {
		return true;
	}

	public boolean continueMAPSTO(BinaryExpression expr) {
		return true;
	}

	public boolean exitMAPSTO(BinaryExpression expr) {
		return true;
	}

	public boolean enterREL(BinaryExpression expr) {
		return true;
	}

	public boolean continueREL(BinaryExpression expr) {
		return true;
	}

	public boolean exitREL(BinaryExpression expr) {
		return true;
	}

	public boolean enterTREL(BinaryExpression expr) {
		return true;
	}

	public boolean continueTREL(BinaryExpression expr) {
		return true;
	}

	public boolean exitTREL(BinaryExpression expr) {
		return true;
	}

	public boolean enterSREL(BinaryExpression expr) {
		return true;
	}

	public boolean continueSREL(BinaryExpression expr) {
		return true;
	}

	public boolean exitSREL(BinaryExpression expr) {
		return true;
	}

	public boolean enterSTREL(BinaryExpression expr) {
		return true;
	}

	public boolean continueSTREL(BinaryExpression expr) {
		return true;
	}

	public boolean exitSTREL(BinaryExpression expr) {
		return true;
	}

	public boolean enterPFUN(BinaryExpression expr) {
		return true;
	}

	public boolean continuePFUN(BinaryExpression expr) {
		return true;
	}

	public boolean exitPFUN(BinaryExpression expr) {
		return true;
	}

	public boolean enterTFUN(BinaryExpression expr) {
		return true;
	}

	public boolean continueTFUN(BinaryExpression expr) {
		return true;
	}

	public boolean exitTFUN(BinaryExpression expr) {
		return true;
	}

	public boolean enterPINJ(BinaryExpression expr) {
		return true;
	}

	public boolean continuePINJ(BinaryExpression expr) {
		return true;
	}

	public boolean exitPINJ(BinaryExpression expr) {
		return true;
	}

	public boolean enterTINJ(BinaryExpression expr) {
		return true;
	}

	public boolean continueTINJ(BinaryExpression expr) {
		return true;
	}

	public boolean exitTINJ(BinaryExpression expr) {
		return true;
	}

	public boolean enterPSUR(BinaryExpression expr) {
		return true;
	}

	public boolean continuePSUR(BinaryExpression expr) {
		return true;
	}

	public boolean exitPSUR(BinaryExpression expr) {
		return true;
	}

	public boolean enterTSUR(BinaryExpression expr) {
		return true;
	}

	public boolean continueTSUR(BinaryExpression expr) {
		return true;
	}

	public boolean exitTSUR(BinaryExpression expr) {
		return true;
	}

	public boolean enterTBIJ(BinaryExpression expr) {
		return true;
	}

	public boolean continueTBIJ(BinaryExpression expr) {
		return true;
	}

	public boolean exitTBIJ(BinaryExpression expr) {
		return true;
	}

	public boolean enterSETMINUS(BinaryExpression expr) {
		return true;
	}

	public boolean continueSETMINUS(BinaryExpression expr) {
		return true;
	}

	public boolean exitSETMINUS(BinaryExpression expr) {
		return true;
	}

	public boolean enterCPROD(BinaryExpression expr) {
		return true;
	}

	public boolean continueCPROD(BinaryExpression expr) {
		return true;
	}

	public boolean exitCPROD(BinaryExpression expr) {
		return true;
	}

	public boolean enterDPROD(BinaryExpression expr) {
		return true;
	}

	public boolean continueDPROD(BinaryExpression expr) {
		return true;
	}

	public boolean exitDPROD(BinaryExpression expr) {
		return true;
	}

	public boolean enterPPROD(BinaryExpression expr) {
		return true;
	}

	public boolean continuePPROD(BinaryExpression expr) {
		return true;
	}

	public boolean exitPPROD(BinaryExpression expr) {
		return true;
	}

	public boolean enterDOMRES(BinaryExpression expr) {
		return true;
	}

	public boolean continueDOMRES(BinaryExpression expr) {
		return true;
	}

	public boolean exitDOMRES(BinaryExpression expr) {
		return true;
	}

	public boolean enterDOMSUB(BinaryExpression expr) {
		return true;
	}

	public boolean continueDOMSUB(BinaryExpression expr) {
		return true;
	}

	public boolean exitDOMSUB(BinaryExpression expr) {
		return true;
	}

	public boolean enterRANRES(BinaryExpression expr) {
		return true;
	}

	public boolean continueRANRES(BinaryExpression expr) {
		return true;
	}

	public boolean exitRANRES(BinaryExpression expr) {
		return true;
	}

	public boolean enterRANSUB(BinaryExpression expr) {
		return true;
	}

	public boolean continueRANSUB(BinaryExpression expr) {
		return true;
	}

	public boolean exitRANSUB(BinaryExpression expr) {
		return true;
	}

	public boolean enterUPTO(BinaryExpression expr) {
		return true;
	}

	public boolean continueUPTO(BinaryExpression expr) {
		return true;
	}

	public boolean exitUPTO(BinaryExpression expr) {
		return true;
	}

	public boolean enterMINUS(BinaryExpression expr) {
		return true;
	}

	public boolean continueMINUS(BinaryExpression expr) {
		return true;
	}

	public boolean exitMINUS(BinaryExpression expr) {
		return true;
	}

	public boolean enterDIV(BinaryExpression expr) {
		return true;
	}

	public boolean continueDIV(BinaryExpression expr) {
		return true;
	}

	public boolean exitDIV(BinaryExpression expr) {
		return true;
	}

	public boolean enterMOD(BinaryExpression expr) {
		return true;
	}

	public boolean continueMOD(BinaryExpression expr) {
		return true;
	}

	public boolean exitMOD(BinaryExpression expr) {
		return true;
	}

	public boolean enterEXPN(BinaryExpression expr) {
		return true;
	}

	public boolean continueEXPN(BinaryExpression expr) {
		return true;
	}

	public boolean exitEXPN(BinaryExpression expr) {
		return true;
	}

	public boolean enterFUNIMAGE(BinaryExpression expr) {
		return true;
	}

	public boolean continueFUNIMAGE(BinaryExpression expr) {
		return true;
	}

	public boolean exitFUNIMAGE(BinaryExpression expr) {
		return true;
	}

	public boolean enterRELIMAGE(BinaryExpression expr) {
		return true;
	}

	public boolean continueRELIMAGE(BinaryExpression expr) {
		return true;
	}

	public boolean exitRELIMAGE(BinaryExpression expr) {
		return true;
	}

	public boolean enterLIMP(BinaryPredicate pred) {
		return true;
	}

	public boolean continueLIMP(BinaryPredicate pred) {
		return true;
	}

	public boolean exitLIMP(BinaryPredicate pred) {
		return true;
	}

	public boolean enterLEQV(BinaryPredicate pred) {
		return true;
	}

	public boolean continueLEQV(BinaryPredicate pred) {
		return true;
	}

	public boolean exitLEQV(BinaryPredicate pred) {
		return true;
	}

	public boolean enterBUNION(AssociativeExpression expr) {
		return true;
	}

	public boolean continueBUNION(AssociativeExpression expr) {
		return true;
	}

	public boolean exitBUNION(AssociativeExpression expr) {
		return true;
	}

	public boolean enterBINTER(AssociativeExpression expr) {
		return true;
	}

	public boolean continueBINTER(AssociativeExpression expr) {
		return true;
	}

	public boolean exitBINTER(AssociativeExpression expr) {
		return true;
	}

	public boolean enterBCOMP(AssociativeExpression expr) {
		return true;
	}

	public boolean continueBCOMP(AssociativeExpression expr) {
		return true;
	}

	public boolean exitBCOMP(AssociativeExpression expr) {
		return true;
	}

	public boolean enterFCOMP(AssociativeExpression expr) {
		return true;
	}

	public boolean continueFCOMP(AssociativeExpression expr) {
		return true;
	}

	public boolean exitFCOMP(AssociativeExpression expr) {
		return true;
	}

	public boolean enterOVR(AssociativeExpression expr) {
		return true;
	}

	public boolean continueOVR(AssociativeExpression expr) {
		return true;
	}

	public boolean exitOVR(AssociativeExpression expr) {
		return true;
	}

	public boolean enterPLUS(AssociativeExpression expr) {
		return true;
	}

	public boolean continuePLUS(AssociativeExpression expr) {
		return true;
	}

	public boolean exitPLUS(AssociativeExpression expr) {
		return true;
	}

	public boolean enterMUL(AssociativeExpression expr) {
		return true;
	}

	public boolean continueMUL(AssociativeExpression expr) {
		return true;
	}

	public boolean exitMUL(AssociativeExpression expr) {
		return true;
	}

	public boolean enterLAND(AssociativePredicate pred) {
		return true;
	}

	public boolean continueLAND(AssociativePredicate pred) {
		return true;
	}

	public boolean exitLAND(AssociativePredicate pred) {
		return true;
	}

	public boolean enterLOR(AssociativePredicate pred) {
		return true;
	}

	public boolean continueLOR(AssociativePredicate pred) {
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

	public boolean visitKPRJ1_GEN(AtomicExpression expr) {
		return true;
	}

	public boolean visitKPRJ2_GEN(AtomicExpression expr) {
		return true;
	}

	public boolean visitKID_GEN(AtomicExpression expr) {
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

	public boolean continueQUNION(QuantifiedExpression expr) {
		return true;
	}

	public boolean exitQUNION(QuantifiedExpression expr) {
		return true;
	}

	public boolean enterQINTER(QuantifiedExpression expr) {
		return true;
	}

	public boolean continueQINTER(QuantifiedExpression expr) {
		return true;
	}

	public boolean exitQINTER(QuantifiedExpression expr) {
		return true;
	}

	public boolean enterCSET(QuantifiedExpression expr) {
		return true;
	}

	public boolean continueCSET(QuantifiedExpression expr) {
		return true;
	}

	public boolean exitCSET(QuantifiedExpression expr) {
		return true;
	}

	public boolean enterFORALL(QuantifiedPredicate pred) {
		return true;
	}

	public boolean continueFORALL(QuantifiedPredicate pred) {
		return true;
	}

	public boolean exitFORALL(QuantifiedPredicate pred) {
		return true;
	}

	public boolean enterEXISTS(QuantifiedPredicate pred) {
		return true;
	}

	public boolean continueEXISTS(QuantifiedPredicate pred) {
		return true;
	}

	public boolean exitEXISTS(QuantifiedPredicate pred) {
		return true;
	}

	public boolean enterBECOMES_EQUAL_TO(BecomesEqualTo assign) {
		return true;
	}

	public boolean continueBECOMES_EQUAL_TO(BecomesEqualTo assign) {
		return true;
	}

	public boolean exitBECOMES_EQUAL_TO(BecomesEqualTo assign) {
		return true;
	}

	public boolean enterBECOMES_MEMBER_OF(BecomesMemberOf assign) {
		return true;
	}

	public boolean continueBECOMES_MEMBER_OF(BecomesMemberOf assign) {
		return true;
	}

	public boolean exitBECOMES_MEMBER_OF(BecomesMemberOf assign) {
		return true;
	}

	public boolean enterBECOMES_SUCH_THAT(BecomesSuchThat assign) {
		return true;
	}

	public boolean continueBECOMES_SUCH_THAT(BecomesSuchThat assign) {
		return true;
	}

	public boolean exitBECOMES_SUCH_THAT(BecomesSuchThat assign) {
		return true;
	}

	public boolean enterKPARTITION(MultiplePredicate pred) {
		return true;
	}

	public boolean continueKPARTITION(MultiplePredicate pred) {
		return true;
	}

	public boolean exitKPARTITION(MultiplePredicate pred) {
		return true;
	}

}
