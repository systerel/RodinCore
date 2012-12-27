/*******************************************************************************
 * Copyright (c) 2005, 2012 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - mathematical language v2
 *     Systerel - added support for predicate variables
 *     Systerel - added support for mathematical extensions
 *******************************************************************************/
package org.eventb.core.ast;

/**
 * Default visitor that does nothing, while traversing the whole AST.
 * <p>
 * This class is intended to be extended by clients, adding useful behavior.
 * </p>
 * 
 * @author Laurent Voisin
 * @since 1.0
 */
public class DefaultVisitor implements IVisitor2 {

	@Override
	public boolean visitFREE_IDENT(FreeIdentifier ident) {
		return true;
	}

	@Override
	public boolean visitBOUND_IDENT_DECL(BoundIdentDecl ident) {
		return true;
	}

	@Override
	public boolean visitBOUND_IDENT(BoundIdentifier ident) {
		return true;
	}

	@Override
	public boolean visitINTLIT(IntegerLiteral lit) {
		return true;
	}

	@Override
	public boolean enterSETEXT(SetExtension set) {
		return true;
	}

	@Override
	public boolean continueSETEXT(SetExtension set) {
		return true;
	}

	@Override
	public boolean exitSETEXT(SetExtension set) {
		return true;
	}

	@Override
	public boolean enterEQUAL(RelationalPredicate pred) {
		return true;
	}

	@Override
	public boolean continueEQUAL(RelationalPredicate pred) {
		return true;
	}

	@Override
	public boolean exitEQUAL(RelationalPredicate pred) {
		return true;
	}

	@Override
	public boolean enterNOTEQUAL(RelationalPredicate pred) {
		return true;
	}

	@Override
	public boolean continueNOTEQUAL(RelationalPredicate pred) {
		return true;
	}

	@Override
	public boolean exitNOTEQUAL(RelationalPredicate pred) {
		return true;
	}

	@Override
	public boolean enterLT(RelationalPredicate pred) {
		return true;
	}

	@Override
	public boolean continueLT(RelationalPredicate pred) {
		return true;
	}

	@Override
	public boolean exitLT(RelationalPredicate pred) {
		return true;
	}

	@Override
	public boolean enterLE(RelationalPredicate pred) {
		return true;
	}

	@Override
	public boolean continueLE(RelationalPredicate pred) {
		return true;
	}

	@Override
	public boolean exitLE(RelationalPredicate pred) {
		return true;
	}

	@Override
	public boolean enterGT(RelationalPredicate pred) {
		return true;
	}

	@Override
	public boolean continueGT(RelationalPredicate pred) {
		return true;
	}

	@Override
	public boolean exitGT(RelationalPredicate pred) {
		return true;
	}

	@Override
	public boolean enterGE(RelationalPredicate pred) {
		return true;
	}

	@Override
	public boolean continueGE(RelationalPredicate pred) {
		return true;
	}

	@Override
	public boolean exitGE(RelationalPredicate pred) {
		return true;
	}

	@Override
	public boolean enterIN(RelationalPredicate pred) {
		return true;
	}

	@Override
	public boolean continueIN(RelationalPredicate pred) {
		return true;
	}

	@Override
	public boolean exitIN(RelationalPredicate pred) {
		return true;
	}

	@Override
	public boolean enterNOTIN(RelationalPredicate pred) {
		return true;
	}

	@Override
	public boolean continueNOTIN(RelationalPredicate pred) {
		return true;
	}

	@Override
	public boolean exitNOTIN(RelationalPredicate pred) {
		return true;
	}

	@Override
	public boolean enterSUBSET(RelationalPredicate pred) {
		return true;
	}

	@Override
	public boolean continueSUBSET(RelationalPredicate pred) {
		return true;
	}

	@Override
	public boolean exitSUBSET(RelationalPredicate pred) {
		return true;
	}

	@Override
	public boolean enterNOTSUBSET(RelationalPredicate pred) {
		return true;
	}

	@Override
	public boolean continueNOTSUBSET(RelationalPredicate pred) {
		return true;
	}

	@Override
	public boolean exitNOTSUBSET(RelationalPredicate pred) {
		return true;
	}

	@Override
	public boolean enterSUBSETEQ(RelationalPredicate pred) {
		return true;
	}

	@Override
	public boolean continueSUBSETEQ(RelationalPredicate pred) {
		return true;
	}

	@Override
	public boolean exitSUBSETEQ(RelationalPredicate pred) {
		return true;
	}

	@Override
	public boolean enterNOTSUBSETEQ(RelationalPredicate pred) {
		return true;
	}

	@Override
	public boolean continueNOTSUBSETEQ(RelationalPredicate pred) {
		return true;
	}

	@Override
	public boolean exitNOTSUBSETEQ(RelationalPredicate pred) {
		return true;
	}

	@Override
	public boolean enterMAPSTO(BinaryExpression expr) {
		return true;
	}

	@Override
	public boolean continueMAPSTO(BinaryExpression expr) {
		return true;
	}

	@Override
	public boolean exitMAPSTO(BinaryExpression expr) {
		return true;
	}

	@Override
	public boolean enterREL(BinaryExpression expr) {
		return true;
	}

	@Override
	public boolean continueREL(BinaryExpression expr) {
		return true;
	}

	@Override
	public boolean exitREL(BinaryExpression expr) {
		return true;
	}

	@Override
	public boolean enterTREL(BinaryExpression expr) {
		return true;
	}

	@Override
	public boolean continueTREL(BinaryExpression expr) {
		return true;
	}

	@Override
	public boolean exitTREL(BinaryExpression expr) {
		return true;
	}

	@Override
	public boolean enterSREL(BinaryExpression expr) {
		return true;
	}

	@Override
	public boolean continueSREL(BinaryExpression expr) {
		return true;
	}

	@Override
	public boolean exitSREL(BinaryExpression expr) {
		return true;
	}

	@Override
	public boolean enterSTREL(BinaryExpression expr) {
		return true;
	}

	@Override
	public boolean continueSTREL(BinaryExpression expr) {
		return true;
	}

	@Override
	public boolean exitSTREL(BinaryExpression expr) {
		return true;
	}

	@Override
	public boolean enterPFUN(BinaryExpression expr) {
		return true;
	}

	@Override
	public boolean continuePFUN(BinaryExpression expr) {
		return true;
	}

	@Override
	public boolean exitPFUN(BinaryExpression expr) {
		return true;
	}

	@Override
	public boolean enterTFUN(BinaryExpression expr) {
		return true;
	}

	@Override
	public boolean continueTFUN(BinaryExpression expr) {
		return true;
	}

	@Override
	public boolean exitTFUN(BinaryExpression expr) {
		return true;
	}

	@Override
	public boolean enterPINJ(BinaryExpression expr) {
		return true;
	}

	@Override
	public boolean continuePINJ(BinaryExpression expr) {
		return true;
	}

	@Override
	public boolean exitPINJ(BinaryExpression expr) {
		return true;
	}

	@Override
	public boolean enterTINJ(BinaryExpression expr) {
		return true;
	}

	@Override
	public boolean continueTINJ(BinaryExpression expr) {
		return true;
	}

	@Override
	public boolean exitTINJ(BinaryExpression expr) {
		return true;
	}

	@Override
	public boolean enterPSUR(BinaryExpression expr) {
		return true;
	}

	@Override
	public boolean continuePSUR(BinaryExpression expr) {
		return true;
	}

	@Override
	public boolean exitPSUR(BinaryExpression expr) {
		return true;
	}

	@Override
	public boolean enterTSUR(BinaryExpression expr) {
		return true;
	}

	@Override
	public boolean continueTSUR(BinaryExpression expr) {
		return true;
	}

	@Override
	public boolean exitTSUR(BinaryExpression expr) {
		return true;
	}

	@Override
	public boolean enterTBIJ(BinaryExpression expr) {
		return true;
	}

	@Override
	public boolean continueTBIJ(BinaryExpression expr) {
		return true;
	}

	@Override
	public boolean exitTBIJ(BinaryExpression expr) {
		return true;
	}

	@Override
	public boolean enterSETMINUS(BinaryExpression expr) {
		return true;
	}

	@Override
	public boolean continueSETMINUS(BinaryExpression expr) {
		return true;
	}

	@Override
	public boolean exitSETMINUS(BinaryExpression expr) {
		return true;
	}

	@Override
	public boolean enterCPROD(BinaryExpression expr) {
		return true;
	}

	@Override
	public boolean continueCPROD(BinaryExpression expr) {
		return true;
	}

	@Override
	public boolean exitCPROD(BinaryExpression expr) {
		return true;
	}

	@Override
	public boolean enterDPROD(BinaryExpression expr) {
		return true;
	}

	@Override
	public boolean continueDPROD(BinaryExpression expr) {
		return true;
	}

	@Override
	public boolean exitDPROD(BinaryExpression expr) {
		return true;
	}

	@Override
	public boolean enterPPROD(BinaryExpression expr) {
		return true;
	}

	@Override
	public boolean continuePPROD(BinaryExpression expr) {
		return true;
	}

	@Override
	public boolean exitPPROD(BinaryExpression expr) {
		return true;
	}

	@Override
	public boolean enterDOMRES(BinaryExpression expr) {
		return true;
	}

	@Override
	public boolean continueDOMRES(BinaryExpression expr) {
		return true;
	}

	@Override
	public boolean exitDOMRES(BinaryExpression expr) {
		return true;
	}

	@Override
	public boolean enterDOMSUB(BinaryExpression expr) {
		return true;
	}

	@Override
	public boolean continueDOMSUB(BinaryExpression expr) {
		return true;
	}

	@Override
	public boolean exitDOMSUB(BinaryExpression expr) {
		return true;
	}

	@Override
	public boolean enterRANRES(BinaryExpression expr) {
		return true;
	}

	@Override
	public boolean continueRANRES(BinaryExpression expr) {
		return true;
	}

	@Override
	public boolean exitRANRES(BinaryExpression expr) {
		return true;
	}

	@Override
	public boolean enterRANSUB(BinaryExpression expr) {
		return true;
	}

	@Override
	public boolean continueRANSUB(BinaryExpression expr) {
		return true;
	}

	@Override
	public boolean exitRANSUB(BinaryExpression expr) {
		return true;
	}

	@Override
	public boolean enterUPTO(BinaryExpression expr) {
		return true;
	}

	@Override
	public boolean continueUPTO(BinaryExpression expr) {
		return true;
	}

	@Override
	public boolean exitUPTO(BinaryExpression expr) {
		return true;
	}

	@Override
	public boolean enterMINUS(BinaryExpression expr) {
		return true;
	}

	@Override
	public boolean continueMINUS(BinaryExpression expr) {
		return true;
	}

	@Override
	public boolean exitMINUS(BinaryExpression expr) {
		return true;
	}

	@Override
	public boolean enterDIV(BinaryExpression expr) {
		return true;
	}

	@Override
	public boolean continueDIV(BinaryExpression expr) {
		return true;
	}

	@Override
	public boolean exitDIV(BinaryExpression expr) {
		return true;
	}

	@Override
	public boolean enterMOD(BinaryExpression expr) {
		return true;
	}

	@Override
	public boolean continueMOD(BinaryExpression expr) {
		return true;
	}

	@Override
	public boolean exitMOD(BinaryExpression expr) {
		return true;
	}

	@Override
	public boolean enterEXPN(BinaryExpression expr) {
		return true;
	}

	@Override
	public boolean continueEXPN(BinaryExpression expr) {
		return true;
	}

	@Override
	public boolean exitEXPN(BinaryExpression expr) {
		return true;
	}

	@Override
	public boolean enterFUNIMAGE(BinaryExpression expr) {
		return true;
	}

	@Override
	public boolean continueFUNIMAGE(BinaryExpression expr) {
		return true;
	}

	@Override
	public boolean exitFUNIMAGE(BinaryExpression expr) {
		return true;
	}

	@Override
	public boolean enterRELIMAGE(BinaryExpression expr) {
		return true;
	}

	@Override
	public boolean continueRELIMAGE(BinaryExpression expr) {
		return true;
	}

	@Override
	public boolean exitRELIMAGE(BinaryExpression expr) {
		return true;
	}

	@Override
	public boolean enterLIMP(BinaryPredicate pred) {
		return true;
	}

	@Override
	public boolean continueLIMP(BinaryPredicate pred) {
		return true;
	}

	@Override
	public boolean exitLIMP(BinaryPredicate pred) {
		return true;
	}

	@Override
	public boolean enterLEQV(BinaryPredicate pred) {
		return true;
	}

	@Override
	public boolean continueLEQV(BinaryPredicate pred) {
		return true;
	}

	@Override
	public boolean exitLEQV(BinaryPredicate pred) {
		return true;
	}

	@Override
	public boolean enterBUNION(AssociativeExpression expr) {
		return true;
	}

	@Override
	public boolean continueBUNION(AssociativeExpression expr) {
		return true;
	}

	@Override
	public boolean exitBUNION(AssociativeExpression expr) {
		return true;
	}

	@Override
	public boolean enterBINTER(AssociativeExpression expr) {
		return true;
	}

	@Override
	public boolean continueBINTER(AssociativeExpression expr) {
		return true;
	}

	@Override
	public boolean exitBINTER(AssociativeExpression expr) {
		return true;
	}

	@Override
	public boolean enterBCOMP(AssociativeExpression expr) {
		return true;
	}

	@Override
	public boolean continueBCOMP(AssociativeExpression expr) {
		return true;
	}

	@Override
	public boolean exitBCOMP(AssociativeExpression expr) {
		return true;
	}

	@Override
	public boolean enterFCOMP(AssociativeExpression expr) {
		return true;
	}

	@Override
	public boolean continueFCOMP(AssociativeExpression expr) {
		return true;
	}

	@Override
	public boolean exitFCOMP(AssociativeExpression expr) {
		return true;
	}

	@Override
	public boolean enterOVR(AssociativeExpression expr) {
		return true;
	}

	@Override
	public boolean continueOVR(AssociativeExpression expr) {
		return true;
	}

	@Override
	public boolean exitOVR(AssociativeExpression expr) {
		return true;
	}

	@Override
	public boolean enterPLUS(AssociativeExpression expr) {
		return true;
	}

	@Override
	public boolean continuePLUS(AssociativeExpression expr) {
		return true;
	}

	@Override
	public boolean exitPLUS(AssociativeExpression expr) {
		return true;
	}

	@Override
	public boolean enterMUL(AssociativeExpression expr) {
		return true;
	}

	@Override
	public boolean continueMUL(AssociativeExpression expr) {
		return true;
	}

	@Override
	public boolean exitMUL(AssociativeExpression expr) {
		return true;
	}

	@Override
	public boolean enterLAND(AssociativePredicate pred) {
		return true;
	}

	@Override
	public boolean continueLAND(AssociativePredicate pred) {
		return true;
	}

	@Override
	public boolean exitLAND(AssociativePredicate pred) {
		return true;
	}

	@Override
	public boolean enterLOR(AssociativePredicate pred) {
		return true;
	}

	@Override
	public boolean continueLOR(AssociativePredicate pred) {
		return true;
	}

	@Override
	public boolean exitLOR(AssociativePredicate pred) {
		return true;
	}

	@Override
	public boolean visitINTEGER(AtomicExpression expr) {
		return true;
	}

	@Override
	public boolean visitNATURAL(AtomicExpression expr) {
		return true;
	}

	@Override
	public boolean visitNATURAL1(AtomicExpression expr) {
		return true;
	}

	@Override
	public boolean visitBOOL(AtomicExpression expr) {
		return true;
	}

	@Override
	public boolean visitTRUE(AtomicExpression expr) {
		return true;
	}

	@Override
	public boolean visitFALSE(AtomicExpression expr) {
		return true;
	}

	@Override
	public boolean visitEMPTYSET(AtomicExpression expr) {
		return true;
	}

	@Override
	public boolean visitKPRED(AtomicExpression expr) {
		return true;
	}

	@Override
	public boolean visitKSUCC(AtomicExpression expr) {
		return true;
	}

	@Override
	public boolean visitKPRJ1_GEN(AtomicExpression expr) {
		return true;
	}

	@Override
	public boolean visitKPRJ2_GEN(AtomicExpression expr) {
		return true;
	}

	@Override
	public boolean visitKID_GEN(AtomicExpression expr) {
		return true;
	}

	@Override
	public boolean enterKBOOL(BoolExpression expr) {
		return true;
	}

	@Override
	public boolean exitKBOOL(BoolExpression expr) {
		return true;
	}

	@Override
	public boolean visitBTRUE(LiteralPredicate pred) {
		return true;
	}

	@Override
	public boolean visitBFALSE(LiteralPredicate pred) {
		return true;
	}

	/**
	 * @since 1.2
	 */
	@Override
	public boolean visitPREDICATE_VARIABLE(PredicateVariable predVar){
		return true;
	}
	
	@Override
	public boolean enterKFINITE(SimplePredicate pred) {
		return true;
	}

	@Override
	public boolean exitKFINITE(SimplePredicate pred) {
		return true;
	}

	@Override
	public boolean enterNOT(UnaryPredicate pred) {
		return true;
	}

	@Override
	public boolean exitNOT(UnaryPredicate pred) {
		return true;
	}

	@Override
	public boolean enterKCARD(UnaryExpression expr) {
		return true;
	}

	@Override
	public boolean exitKCARD(UnaryExpression expr) {
		return true;
	}

	@Override
	public boolean enterPOW(UnaryExpression expr) {
		return true;
	}

	@Override
	public boolean exitPOW(UnaryExpression expr) {
		return true;
	}

	@Override
	public boolean enterPOW1(UnaryExpression expr) {
		return true;
	}

	@Override
	public boolean exitPOW1(UnaryExpression expr) {
		return true;
	}

	@Override
	public boolean enterKUNION(UnaryExpression expr) {
		return true;
	}

	@Override
	public boolean exitKUNION(UnaryExpression expr) {
		return true;
	}

	@Override
	public boolean enterKINTER(UnaryExpression expr) {
		return true;
	}

	@Override
	public boolean exitKINTER(UnaryExpression expr) {
		return true;
	}

	@Override
	public boolean enterKDOM(UnaryExpression expr) {
		return true;
	}

	@Override
	public boolean exitKDOM(UnaryExpression expr) {
		return true;
	}

	@Override
	public boolean enterKRAN(UnaryExpression expr) {
		return true;
	}

	@Override
	public boolean exitKRAN(UnaryExpression expr) {
		return true;
	}

	@Override
	public boolean enterKPRJ1(UnaryExpression expr) {
		return true;
	}

	@Override
	public boolean exitKPRJ1(UnaryExpression expr) {
		return true;
	}

	@Override
	public boolean enterKPRJ2(UnaryExpression expr) {
		return true;
	}

	@Override
	public boolean exitKPRJ2(UnaryExpression expr) {
		return true;
	}

	@Override
	public boolean enterKID(UnaryExpression expr) {
		return true;
	}

	@Override
	public boolean exitKID(UnaryExpression expr) {
		return true;
	}

	@Override
	public boolean enterKMIN(UnaryExpression expr) {
		return true;
	}

	@Override
	public boolean exitKMIN(UnaryExpression expr) {
		return true;
	}

	@Override
	public boolean enterKMAX(UnaryExpression expr) {
		return true;
	}

	@Override
	public boolean exitKMAX(UnaryExpression expr) {
		return true;
	}

	@Override
	public boolean enterCONVERSE(UnaryExpression expr) {
		return true;
	}

	@Override
	public boolean exitCONVERSE(UnaryExpression expr) {
		return true;
	}

	@Override
	public boolean enterUNMINUS(UnaryExpression expr) {
		return true;
	}

	@Override
	public boolean exitUNMINUS(UnaryExpression expr) {
		return true;
	}

	@Override
	public boolean enterQUNION(QuantifiedExpression expr) {
		return true;
	}

	@Override
	public boolean continueQUNION(QuantifiedExpression expr) {
		return true;
	}

	@Override
	public boolean exitQUNION(QuantifiedExpression expr) {
		return true;
	}

	@Override
	public boolean enterQINTER(QuantifiedExpression expr) {
		return true;
	}

	@Override
	public boolean continueQINTER(QuantifiedExpression expr) {
		return true;
	}

	@Override
	public boolean exitQINTER(QuantifiedExpression expr) {
		return true;
	}

	@Override
	public boolean enterCSET(QuantifiedExpression expr) {
		return true;
	}

	@Override
	public boolean continueCSET(QuantifiedExpression expr) {
		return true;
	}

	@Override
	public boolean exitCSET(QuantifiedExpression expr) {
		return true;
	}

	@Override
	public boolean enterFORALL(QuantifiedPredicate pred) {
		return true;
	}

	@Override
	public boolean continueFORALL(QuantifiedPredicate pred) {
		return true;
	}

	@Override
	public boolean exitFORALL(QuantifiedPredicate pred) {
		return true;
	}

	@Override
	public boolean enterEXISTS(QuantifiedPredicate pred) {
		return true;
	}

	@Override
	public boolean continueEXISTS(QuantifiedPredicate pred) {
		return true;
	}

	@Override
	public boolean exitEXISTS(QuantifiedPredicate pred) {
		return true;
	}

	@Override
	public boolean enterBECOMES_EQUAL_TO(BecomesEqualTo assign) {
		return true;
	}

	@Override
	public boolean continueBECOMES_EQUAL_TO(BecomesEqualTo assign) {
		return true;
	}

	@Override
	public boolean exitBECOMES_EQUAL_TO(BecomesEqualTo assign) {
		return true;
	}

	@Override
	public boolean enterBECOMES_MEMBER_OF(BecomesMemberOf assign) {
		return true;
	}

	@Override
	public boolean continueBECOMES_MEMBER_OF(BecomesMemberOf assign) {
		return true;
	}

	@Override
	public boolean exitBECOMES_MEMBER_OF(BecomesMemberOf assign) {
		return true;
	}

	@Override
	public boolean enterBECOMES_SUCH_THAT(BecomesSuchThat assign) {
		return true;
	}

	@Override
	public boolean continueBECOMES_SUCH_THAT(BecomesSuchThat assign) {
		return true;
	}

	@Override
	public boolean exitBECOMES_SUCH_THAT(BecomesSuchThat assign) {
		return true;
	}

	@Override
	public boolean enterKPARTITION(MultiplePredicate pred) {
		return true;
	}

	@Override
	public boolean continueKPARTITION(MultiplePredicate pred) {
		return true;
	}

	@Override
	public boolean exitKPARTITION(MultiplePredicate pred) {
		return true;
	}

	/**
	 * @since 2.0
	 */
	@Override
	public boolean enterExtendedExpression(ExtendedExpression expression) {
		return true;
	}

	/**
	 * @since 2.0
	 */
	@Override
	public boolean continueExtendedExpression(ExtendedExpression expression) {
		return true;
	}

	/**
	 * @since 2.0
	 */
	@Override
	public boolean exitExtendedExpression(ExtendedExpression expression) {
		return true;
	}

	/**
	 * @since 2.0
	 */
	@Override
	public boolean enterExtendedPredicate(ExtendedPredicate predicate) {
		return true;
	}

	/**
	 * @since 2.0
	 */
	@Override
	public boolean continueExtendedPredicate(ExtendedPredicate predicate) {
		return true;
	}

	/**
	 * @since 2.0
	 */
	@Override
	public boolean exitExtendedPredicate(ExtendedPredicate predicate) {
		return true;
	}

}
