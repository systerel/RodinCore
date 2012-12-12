/*******************************************************************************
 * Copyright (c) 2005, 2010 ETH Zurich and others.
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
package org.eventb.core.ast.tests;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.fail;
import static org.eventb.core.ast.QuantifiedExpression.Form.Explicit;
import static org.eventb.core.ast.tests.FastFactory.mBoundIdentDecl;
import static org.eventb.core.ast.tests.FastFactory.mFreeIdentifier;
import static org.eventb.core.ast.tests.FastFactory.mIntegerLiteral;
import static org.eventb.core.ast.tests.FastFactory.mList;
import static org.eventb.core.ast.tests.FastFactory.mLiteralPredicate;

import java.math.BigInteger;

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
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.ExtendedExpression;
import org.eventb.core.ast.ExtendedPredicate;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.IVisitor;
import org.eventb.core.ast.IVisitor2;
import org.eventb.core.ast.IntegerLiteral;
import org.eventb.core.ast.LiteralPredicate;
import org.eventb.core.ast.MultiplePredicate;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.PredicateVariable;
import org.eventb.core.ast.QuantifiedExpression;
import org.eventb.core.ast.QuantifiedPredicate;
import org.eventb.core.ast.RelationalPredicate;
import org.eventb.core.ast.SetExtension;
import org.eventb.core.ast.SimplePredicate;
import org.eventb.core.ast.UnaryExpression;
import org.eventb.core.ast.UnaryPredicate;
import org.junit.Test;

public class TestVisitor {
	
	FormulaFactory ff = FormulaFactory.getDefault();
	
	private static class TestItem {
		private final Formula<?> formula;
		private final int expectedCount;
		private final CounterVisitor visitor;
		
		TestItem(Formula<?> formula, int expectedCount) {
			this(formula, expectedCount, new CounterVisitor());
		}
		
		TestItem(Formula<?> formula, int expectedCount, CounterVisitor visitor) {
			this.formula = formula;
			this.expectedCount = expectedCount;
			this.visitor = visitor;
		}

		public void runTest() {
			formula.accept(visitor);
			assertEquals(formula.toString(), expectedCount, visitor.getCount());
		}

	}

	private static class CounterVisitor implements IVisitor {

		protected int count;
		
		CounterVisitor() { super(); }
		
		int getCount() {
			return count;
		}

		@Override
		public boolean continueBCOMP(AssociativeExpression expr) {
			++ count;
			return true;
		}

		@Override
		public boolean continueBINTER(AssociativeExpression expr) {
			++ count;
			return true;
		}

		@Override
		public boolean continueBUNION(AssociativeExpression expr) {
			++ count;
			return true;
		}

		@Override
		public boolean continueCPROD(BinaryExpression expr) {
			++ count;
			return true;
		}

		@Override
		public boolean continueCSET(QuantifiedExpression expr) {
			++ count;
			return true;
		}

		@Override
		public boolean continueDIV(BinaryExpression expr) {
			++ count;
			return true;
		}

		@Override
		public boolean continueDOMRES(BinaryExpression expr) {
			++ count;
			return true;
		}

		@Override
		public boolean continueDOMSUB(BinaryExpression expr) {
			++ count;
			return true;
		}

		@Override
		public boolean continueDPROD(BinaryExpression expr) {
			++ count;
			return true;
		}

		@Override
		public boolean continueEQUAL(RelationalPredicate pred) {
			++ count;
			return true;
		}

		@Override
		public boolean continueEXISTS(QuantifiedPredicate pred) {
			++ count;
			return true;
		}

		@Override
		public boolean continueEXPN(BinaryExpression expr) {
			++ count;
			return true;
		}

		@Override
		public boolean continueFCOMP(AssociativeExpression expr) {
			++ count;
			return true;
		}

		@Override
		public boolean continueFORALL(QuantifiedPredicate pred) {
			++ count;
			return true;
		}

		@Override
		public boolean continueFUNIMAGE(BinaryExpression expr) {
			++ count;
			return true;
		}

		@Override
		public boolean continueGE(RelationalPredicate pred) {
			++ count;
			return true;
		}

		@Override
		public boolean continueGT(RelationalPredicate pred) {
			++ count;
			return true;
		}

		@Override
		public boolean continueIN(RelationalPredicate pred) {
			++ count;
			return true;
		}

		@Override
		public boolean continueLAND(AssociativePredicate pred) {
			++ count;
			return true;
		}

		@Override
		public boolean continueLE(RelationalPredicate pred) {
			++ count;
			return true;
		}

		@Override
		public boolean continueLEQV(BinaryPredicate pred) {
			++ count;
			return true;
		}

		@Override
		public boolean continueLIMP(BinaryPredicate pred) {
			++ count;
			return true;
		}

		@Override
		public boolean continueLOR(AssociativePredicate pred) {
			++ count;
			return true;
		}

		@Override
		public boolean continueLT(RelationalPredicate pred) {
			++ count;
			return true;
		}

		@Override
		public boolean continueMAPSTO(BinaryExpression expr) {
			++ count;
			return true;
		}

		@Override
		public boolean continueMINUS(BinaryExpression expr) {
			++ count;
			return true;
		}

		@Override
		public boolean continueMOD(BinaryExpression expr) {
			++ count;
			return true;
		}

		@Override
		public boolean continueMUL(AssociativeExpression expr) {
			++ count;
			return true;
		}

		@Override
		public boolean continueNOTEQUAL(RelationalPredicate pred) {
			++ count;
			return true;
		}

		@Override
		public boolean continueNOTIN(RelationalPredicate pred) {
			++ count;
			return true;
		}

		@Override
		public boolean continueNOTSUBSET(RelationalPredicate pred) {
			++ count;
			return true;
		}

		@Override
		public boolean continueNOTSUBSETEQ(RelationalPredicate pred) {
			++ count;
			return true;
		}

		@Override
		public boolean continueOVR(AssociativeExpression expr) {
			++ count;
			return true;
		}

		@Override
		public boolean continuePFUN(BinaryExpression expr) {
			++ count;
			return true;
		}

		@Override
		public boolean continuePINJ(BinaryExpression expr) {
			++ count;
			return true;
		}

		@Override
		public boolean continuePLUS(AssociativeExpression expr) {
			++ count;
			return true;
		}

		@Override
		public boolean continuePPROD(BinaryExpression expr) {
			++ count;
			return true;
		}

		@Override
		public boolean continuePSUR(BinaryExpression expr) {
			++ count;
			return true;
		}

		@Override
		public boolean continueQINTER(QuantifiedExpression expr) {
			++ count;
			return true;
		}

		@Override
		public boolean continueQUNION(QuantifiedExpression expr) {
			++ count;
			return true;
		}

		@Override
		public boolean continueRANRES(BinaryExpression expr) {
			++ count;
			return true;
		}

		@Override
		public boolean continueRANSUB(BinaryExpression expr) {
			++ count;
			return true;
		}

		@Override
		public boolean continueREL(BinaryExpression expr) {
			++ count;
			return true;
		}

		@Override
		public boolean continueRELIMAGE(BinaryExpression expr) {
			++ count;
			return true;
		}

		@Override
		public boolean continueSETEXT(SetExtension set) {
			++ count;
			return true;
		}

		@Override
		public boolean continueSETMINUS(BinaryExpression expr) {
			++ count;
			return true;
		}

		@Override
		public boolean continueSREL(BinaryExpression expr) {
			++ count;
			return true;
		}

		@Override
		public boolean continueSTREL(BinaryExpression expr) {
			++ count;
			return true;
		}

		@Override
		public boolean continueSUBSET(RelationalPredicate pred) {
			++ count;
			return true;
		}

		@Override
		public boolean continueSUBSETEQ(RelationalPredicate pred) {
			++ count;
			return true;
		}

		@Override
		public boolean continueTBIJ(BinaryExpression expr) {
			++ count;
			return true;
		}

		@Override
		public boolean continueTFUN(BinaryExpression expr) {
			++ count;
			return true;
		}

		@Override
		public boolean continueTINJ(BinaryExpression expr) {
			++ count;
			return true;
		}

		@Override
		public boolean continueTREL(BinaryExpression expr) {
			++ count;
			return true;
		}

		@Override
		public boolean continueTSUR(BinaryExpression expr) {
			++ count;
			return true;
		}

		@Override
		public boolean continueUPTO(BinaryExpression expr) {
			++ count;
			return true;
		}

		@Override
		public boolean enterBCOMP(AssociativeExpression expr) {
			++ count;
			return true;
		}

		@Override
		public boolean enterBINTER(AssociativeExpression expr) {
			++ count;
			return true;
		}

		@Override
		public boolean enterBUNION(AssociativeExpression expr) {
			++ count;
			return true;
		}

		@Override
		public boolean enterCONVERSE(UnaryExpression expr) {
			++ count;
			return true;
		}

		@Override
		public boolean enterCPROD(BinaryExpression expr) {
			++ count;
			return true;
		}

		@Override
		public boolean enterCSET(QuantifiedExpression expr) {
			++ count;
			return true;
		}

		@Override
		public boolean enterDIV(BinaryExpression expr) {
			++ count;
			return true;
		}

		@Override
		public boolean enterDOMRES(BinaryExpression expr) {
			++ count;
			return true;
		}

		@Override
		public boolean enterDOMSUB(BinaryExpression expr) {
			++ count;
			return true;
		}

		@Override
		public boolean enterDPROD(BinaryExpression expr) {
			++ count;
			return true;
		}

		@Override
		public boolean enterEQUAL(RelationalPredicate pred) {
			++ count;
			return true;
		}

		@Override
		public boolean enterEXISTS(QuantifiedPredicate pred) {
			++ count;
			return true;
		}

		@Override
		public boolean enterEXPN(BinaryExpression expr) {
			++ count;
			return true;
		}

		@Override
		public boolean enterFCOMP(AssociativeExpression expr) {
			++ count;
			return true;
		}

		@Override
		public boolean enterFORALL(QuantifiedPredicate pred) {
			++ count;
			return true;
		}

		@Override
		public boolean enterFUNIMAGE(BinaryExpression expr) {
			++ count;
			return true;
		}

		@Override
		public boolean enterGE(RelationalPredicate pred) {
			++ count;
			return true;
		}

		@Override
		public boolean enterGT(RelationalPredicate pred) {
			++ count;
			return true;
		}

		@Override
		public boolean enterIN(RelationalPredicate pred) {
			++ count;
			return true;
		}

		@Override
		public boolean enterKBOOL(BoolExpression expr) {
			++ count;
			return true;
		}

		@Override
		public boolean enterKCARD(UnaryExpression expr) {
			++ count;
			return true;
		}

		@Override
		public boolean enterKDOM(UnaryExpression expr) {
			++ count;
			return true;
		}

		@Override
		public boolean enterKFINITE(SimplePredicate pred) {
			++ count;
			return true;
		}

		@Override
		public boolean enterKID(UnaryExpression expr) {
			++ count;
			return true;
		}

		@Override
		public boolean enterKINTER(UnaryExpression expr) {
			++ count;
			return true;
		}

		@Override
		public boolean enterKMAX(UnaryExpression expr) {
			++ count;
			return true;
		}

		@Override
		public boolean enterKMIN(UnaryExpression expr) {
			++ count;
			return true;
		}

		@Override
		public boolean enterKPRJ1(UnaryExpression expr) {
			++ count;
			return true;
		}

		@Override
		public boolean enterKPRJ2(UnaryExpression expr) {
			++ count;
			return true;
		}

		@Override
		public boolean enterKRAN(UnaryExpression expr) {
			++ count;
			return true;
		}

		@Override
		public boolean enterKUNION(UnaryExpression expr) {
			++ count;
			return true;
		}

		@Override
		public boolean enterLAND(AssociativePredicate pred) {
			++ count;
			return true;
		}

		@Override
		public boolean enterLE(RelationalPredicate pred) {
			++ count;
			return true;
		}

		@Override
		public boolean enterLEQV(BinaryPredicate pred) {
			++ count;
			return true;
		}

		@Override
		public boolean enterLIMP(BinaryPredicate pred) {
			++ count;
			return true;
		}

		@Override
		public boolean enterLOR(AssociativePredicate pred) {
			++ count;
			return true;
		}

		@Override
		public boolean enterLT(RelationalPredicate pred) {
			++ count;
			return true;
		}

		@Override
		public boolean enterMAPSTO(BinaryExpression expr) {
			++ count;
			return true;
		}

		@Override
		public boolean enterMINUS(BinaryExpression expr) {
			++ count;
			return true;
		}

		@Override
		public boolean enterMOD(BinaryExpression expr) {
			++ count;
			return true;
		}

		@Override
		public boolean enterMUL(AssociativeExpression expr) {
			++ count;
			return true;
		}

		@Override
		public boolean enterNOT(UnaryPredicate pred) {
			++ count;
			return true;
		}

		@Override
		public boolean enterNOTEQUAL(RelationalPredicate pred) {
			++ count;
			return true;
		}

		@Override
		public boolean enterNOTIN(RelationalPredicate pred) {
			++ count;
			return true;
		}

		@Override
		public boolean enterNOTSUBSET(RelationalPredicate pred) {
			++ count;
			return true;
		}

		@Override
		public boolean enterNOTSUBSETEQ(RelationalPredicate pred) {
			++ count;
			return true;
		}

		@Override
		public boolean enterOVR(AssociativeExpression expr) {
			++ count;
			return true;
		}

		@Override
		public boolean enterPFUN(BinaryExpression expr) {
			++ count;
			return true;
		}

		@Override
		public boolean enterPINJ(BinaryExpression expr) {
			++ count;
			return true;
		}

		@Override
		public boolean enterPLUS(AssociativeExpression expr) {
			++ count;
			return true;
		}

		@Override
		public boolean enterPOW(UnaryExpression expr) {
			++ count;
			return true;
		}

		@Override
		public boolean enterPOW1(UnaryExpression expr) {
			++ count;
			return true;
		}

		@Override
		public boolean enterPPROD(BinaryExpression expr) {
			++ count;
			return true;
		}

		@Override
		public boolean enterPSUR(BinaryExpression expr) {
			++ count;
			return true;
		}

		@Override
		public boolean enterQINTER(QuantifiedExpression expr) {
			++ count;
			return true;
		}

		@Override
		public boolean enterQUNION(QuantifiedExpression expr) {
			++ count;
			return true;
		}

		@Override
		public boolean enterRANRES(BinaryExpression expr) {
			++ count;
			return true;
		}

		@Override
		public boolean enterRANSUB(BinaryExpression expr) {
			++ count;
			return true;
		}

		@Override
		public boolean enterREL(BinaryExpression expr) {
			++ count;
			return true;
		}

		@Override
		public boolean enterRELIMAGE(BinaryExpression expr) {
			++ count;
			return true;
		}

		@Override
		public boolean enterSETEXT(SetExtension set) {
			++ count;
			return true;
		}

		@Override
		public boolean enterSETMINUS(BinaryExpression expr) {
			++ count;
			return true;
		}

		@Override
		public boolean enterSREL(BinaryExpression expr) {
			++ count;
			return true;
		}

		@Override
		public boolean enterSTREL(BinaryExpression expr) {
			++ count;
			return true;
		}

		@Override
		public boolean enterSUBSET(RelationalPredicate pred) {
			++ count;
			return true;
		}

		@Override
		public boolean enterSUBSETEQ(RelationalPredicate pred) {
			++ count;
			return true;
		}

		@Override
		public boolean enterTBIJ(BinaryExpression expr) {
			++ count;
			return true;
		}

		@Override
		public boolean enterTFUN(BinaryExpression expr) {
			++ count;
			return true;
		}

		@Override
		public boolean enterTINJ(BinaryExpression expr) {
			++ count;
			return true;
		}

		@Override
		public boolean enterTREL(BinaryExpression expr) {
			++ count;
			return true;
		}

		@Override
		public boolean enterTSUR(BinaryExpression expr) {
			++ count;
			return true;
		}

		@Override
		public boolean enterUNMINUS(UnaryExpression expr) {
			++ count;
			return true;
		}

		@Override
		public boolean enterUPTO(BinaryExpression expr) {
			++ count;
			return true;
		}

		@Override
		public boolean exitBCOMP(AssociativeExpression expr) {
			++ count;
			return true;
		}

		@Override
		public boolean exitBINTER(AssociativeExpression expr) {
			++ count;
			return true;
		}

		@Override
		public boolean exitBUNION(AssociativeExpression expr) {
			++ count;
			return true;
		}

		@Override
		public boolean exitCONVERSE(UnaryExpression expr) {
			++ count;
			return true;
		}

		@Override
		public boolean exitCPROD(BinaryExpression expr) {
			++ count;
			return true;
		}

		@Override
		public boolean exitCSET(QuantifiedExpression expr) {
			++ count;
			return true;
		}

		@Override
		public boolean exitDIV(BinaryExpression expr) {
			++ count;
			return true;
		}

		@Override
		public boolean exitDOMRES(BinaryExpression expr) {
			++ count;
			return true;
		}

		@Override
		public boolean exitDOMSUB(BinaryExpression expr) {
			++ count;
			return true;
		}

		@Override
		public boolean exitDPROD(BinaryExpression expr) {
			++ count;
			return true;
		}

		@Override
		public boolean exitEQUAL(RelationalPredicate pred) {
			++ count;
			return true;
		}

		@Override
		public boolean exitEXISTS(QuantifiedPredicate pred) {
			++ count;
			return true;
		}

		@Override
		public boolean exitEXPN(BinaryExpression expr) {
			++ count;
			return true;
		}

		@Override
		public boolean exitFCOMP(AssociativeExpression expr) {
			++ count;
			return true;
		}

		@Override
		public boolean exitFORALL(QuantifiedPredicate pred) {
			++ count;
			return true;
		}

		@Override
		public boolean exitFUNIMAGE(BinaryExpression expr) {
			++ count;
			return true;
		}

		@Override
		public boolean exitGE(RelationalPredicate pred) {
			++ count;
			return true;
		}

		@Override
		public boolean exitGT(RelationalPredicate pred) {
			++ count;
			return true;
		}

		@Override
		public boolean exitIN(RelationalPredicate pred) {
			++ count;
			return true;
		}

		@Override
		public boolean exitKBOOL(BoolExpression expr) {
			++ count;
			return true;
		}

		@Override
		public boolean exitKCARD(UnaryExpression expr) {
			++ count;
			return true;
		}

		@Override
		public boolean exitKDOM(UnaryExpression expr) {
			++ count;
			return true;
		}

		@Override
		public boolean exitKFINITE(SimplePredicate pred) {
			++ count;
			return true;
		}

		@Override
		public boolean exitKID(UnaryExpression expr) {
			++ count;
			return true;
		}

		@Override
		public boolean exitKINTER(UnaryExpression expr) {
			++ count;
			return true;
		}

		@Override
		public boolean exitKMAX(UnaryExpression expr) {
			++ count;
			return true;
		}

		@Override
		public boolean exitKMIN(UnaryExpression expr) {
			++ count;
			return true;
		}

		@Override
		public boolean exitKPRJ1(UnaryExpression expr) {
			++ count;
			return true;
		}

		@Override
		public boolean exitKPRJ2(UnaryExpression expr) {
			++ count;
			return true;
		}

		@Override
		public boolean exitKRAN(UnaryExpression expr) {
			++ count;
			return true;
		}

		@Override
		public boolean exitKUNION(UnaryExpression expr) {
			++ count;
			return true;
		}

		@Override
		public boolean exitLAND(AssociativePredicate pred) {
			++ count;
			return true;
		}

		@Override
		public boolean exitLE(RelationalPredicate pred) {
			++ count;
			return true;
		}

		@Override
		public boolean exitLEQV(BinaryPredicate pred) {
			++ count;
			return true;
		}

		@Override
		public boolean exitLIMP(BinaryPredicate pred) {
			++ count;
			return true;
		}

		@Override
		public boolean exitLOR(AssociativePredicate pred) {
			++ count;
			return true;
		}

		@Override
		public boolean exitLT(RelationalPredicate pred) {
			++ count;
			return true;
		}

		@Override
		public boolean exitMAPSTO(BinaryExpression expr) {
			++ count;
			return true;
		}

		@Override
		public boolean exitMINUS(BinaryExpression expr) {
			++ count;
			return true;
		}

		@Override
		public boolean exitMOD(BinaryExpression expr) {
			++ count;
			return true;
		}

		@Override
		public boolean exitMUL(AssociativeExpression expr) {
			++ count;
			return true;
		}

		@Override
		public boolean exitNOT(UnaryPredicate pred) {
			++ count;
			return true;
		}

		@Override
		public boolean exitNOTEQUAL(RelationalPredicate pred) {
			++ count;
			return true;
		}

		@Override
		public boolean exitNOTIN(RelationalPredicate pred) {
			++ count;
			return true;
		}

		@Override
		public boolean exitNOTSUBSET(RelationalPredicate pred) {
			++ count;
			return true;
		}

		@Override
		public boolean exitNOTSUBSETEQ(RelationalPredicate pred) {
			++ count;
			return true;
		}

		@Override
		public boolean exitOVR(AssociativeExpression expr) {
			++ count;
			return true;
		}

		@Override
		public boolean exitPFUN(BinaryExpression expr) {
			++ count;
			return true;
		}

		@Override
		public boolean exitPINJ(BinaryExpression expr) {
			++ count;
			return true;
		}

		@Override
		public boolean exitPLUS(AssociativeExpression expr) {
			++ count;
			return true;
		}

		@Override
		public boolean exitPOW(UnaryExpression expr) {
			++ count;
			return true;
		}

		@Override
		public boolean exitPOW1(UnaryExpression expr) {
			++ count;
			return true;
		}

		@Override
		public boolean exitPPROD(BinaryExpression expr) {
			++ count;
			return true;
		}

		@Override
		public boolean exitPSUR(BinaryExpression expr) {
			++ count;
			return true;
		}

		@Override
		public boolean exitQINTER(QuantifiedExpression expr) {
			++ count;
			return true;
		}

		@Override
		public boolean exitQUNION(QuantifiedExpression expr) {
			++ count;
			return true;
		}

		@Override
		public boolean exitRANRES(BinaryExpression expr) {
			++ count;
			return true;
		}

		@Override
		public boolean exitRANSUB(BinaryExpression expr) {
			++ count;
			return true;
		}

		@Override
		public boolean exitREL(BinaryExpression expr) {
			++ count;
			return true;
		}

		@Override
		public boolean exitRELIMAGE(BinaryExpression expr) {
			++ count;
			return true;
		}

		@Override
		public boolean exitSETEXT(SetExtension set) {
			++ count;
			return true;
		}

		@Override
		public boolean exitSETMINUS(BinaryExpression expr) {
			++ count;
			return true;
		}

		@Override
		public boolean exitSREL(BinaryExpression expr) {
			++ count;
			return true;
		}

		@Override
		public boolean exitSTREL(BinaryExpression expr) {
			++ count;
			return true;
		}

		@Override
		public boolean exitSUBSET(RelationalPredicate pred) {
			++ count;
			return true;
		}

		@Override
		public boolean exitSUBSETEQ(RelationalPredicate pred) {
			++ count;
			return true;
		}

		@Override
		public boolean exitTBIJ(BinaryExpression expr) {
			++ count;
			return true;
		}

		@Override
		public boolean exitTFUN(BinaryExpression expr) {
			++ count;
			return true;
		}

		@Override
		public boolean exitTINJ(BinaryExpression expr) {
			++ count;
			return true;
		}

		@Override
		public boolean exitTREL(BinaryExpression expr) {
			++ count;
			return true;
		}

		@Override
		public boolean exitTSUR(BinaryExpression expr) {
			++ count;
			return true;
		}

		@Override
		public boolean exitUNMINUS(UnaryExpression expr) {
			++ count;
			return true;
		}

		@Override
		public boolean exitUPTO(BinaryExpression expr) {
			++ count;
			return true;
		}

		@Override
		public boolean visitBFALSE(LiteralPredicate pred) {
			++ count;
			return true;
		}

		@Override
		public boolean visitBOOL(AtomicExpression expr) {
			++ count;
			return true;
		}

		@Override
		public boolean visitBOUND_IDENT_DECL(BoundIdentDecl ident) {
			++ count;
			return true;
		}

		@Override
		public boolean visitBOUND_IDENT(BoundIdentifier ident) {
			++ count;
			return true;
		}

		@Override
		public boolean visitBTRUE(LiteralPredicate pred) {
			++ count;
			return true;
		}

		@Override
		public boolean visitEMPTYSET(AtomicExpression expr) {
			++ count;
			return true;
		}

		@Override
		public boolean visitFALSE(AtomicExpression expr) {
			++ count;
			return true;
		}

		@Override
		public boolean visitFREE_IDENT(FreeIdentifier ident) {
			++ count;
			return true;
		}

		@Override
		public boolean visitINTEGER(AtomicExpression expr) {
			++ count;
			return true;
		}

		@Override
		public boolean visitINTLIT(IntegerLiteral lit) {
			++ count;
			return true;
		}

		@Override
		public boolean visitKPRED(AtomicExpression expr) {
			++ count;
			return true;
		}

		@Override
		public boolean visitKSUCC(AtomicExpression expr) {
			++ count;
			return true;
		}

		@Override
		public boolean visitNATURAL(AtomicExpression expr) {
			++ count;
			return true;
		}

		@Override
		public boolean visitNATURAL1(AtomicExpression expr) {
			++ count;
			return true;
		}

		@Override
		public boolean visitTRUE(AtomicExpression expr) {
			++ count;
			return true;
		}

		@Override
		public boolean enterBECOMES_EQUAL_TO(BecomesEqualTo assign) {
			++ count;
			return true;
		}

		@Override
		public boolean continueBECOMES_EQUAL_TO(BecomesEqualTo assign) {
			++ count;
			return true;
		}

		@Override
		public boolean exitBECOMES_EQUAL_TO(BecomesEqualTo assign) {
			++ count;
			return true;
		}

		@Override
		public boolean enterBECOMES_MEMBER_OF(BecomesMemberOf assign) {
			++ count;
			return true;
		}

		@Override
		public boolean continueBECOMES_MEMBER_OF(BecomesMemberOf assign) {
			++ count;
			return true;
		}

		@Override
		public boolean exitBECOMES_MEMBER_OF(BecomesMemberOf assign) {
			++ count;
			return true;
		}

		@Override
		public boolean enterBECOMES_SUCH_THAT(BecomesSuchThat assign) {
			++ count;
			return true;
		}

		@Override
		public boolean continueBECOMES_SUCH_THAT(BecomesSuchThat assign) {
			++ count;
			return true;
		}

		@Override
		public boolean exitBECOMES_SUCH_THAT(BecomesSuchThat assign) {
			++ count;
			return true;
		}

		@Override
		public boolean enterKPARTITION(MultiplePredicate pred) {
			++ count;
			return true;
		}

		@Override
		public boolean continueKPARTITION(MultiplePredicate pred) {
			++ count;
			return true;
		}

		@Override
		public boolean exitKPARTITION(MultiplePredicate pred) {
			++ count;
			return true;
		}

		@Override
		public boolean visitKID_GEN(AtomicExpression expr) {
			++ count;
			return true;
		}

		@Override
		public boolean visitKPRJ1_GEN(AtomicExpression expr) {
			++ count;
			return true;
		}

		@Override
		public boolean visitKPRJ2_GEN(AtomicExpression expr) {
			++ count;
			return true;
		}

		@Override
		public boolean continueExtendedExpression(ExtendedExpression expr) {
			++ count;
			return true;
		}

		@Override
		public boolean continueExtendedPredicate(ExtendedPredicate pred) {
			++ count;
			return true;
		}

		@Override
		public boolean enterExtendedExpression(ExtendedExpression expr) {
			++ count;
			return true;
		}

		@Override
		public boolean enterExtendedPredicate(ExtendedPredicate pred) {
			++ count;
			return true;
		}

		@Override
		public boolean exitExtendedExpression(ExtendedExpression expr) {
			++ count;
			return true;
		}

		@Override
		public boolean exitExtendedPredicate(ExtendedPredicate pred) {
			++ count;
			return true;
		}

	}
	
	private static class CounterVisitor2 extends CounterVisitor implements IVisitor2 {
	
		CounterVisitor2() {
			super();
		}
		
		@Override
		public boolean visitPREDICATE_VARIABLE(PredicateVariable predVar) {
			++ count;
			return true;
		}
	}

	private static void assertException(Formula<?> formula, IVisitor visitor) {
		try {
			formula.accept(visitor);
			fail("IllegalArgumentException expected for "
					+ formula.toString());
		} catch (IllegalArgumentException e) {
			// as expected
		}
	}

	// Some simple expressions.
	private Expression e1 = mIntegerLiteral();
	private Expression e2 = mIntegerLiteral();
	private Expression e3 = mIntegerLiteral();
	
	// Some simple predicates.
	private Predicate p1 = mLiteralPredicate();
	private Predicate p2 = mLiteralPredicate();
	private Predicate p3 = mLiteralPredicate();
	
	// A bound identifier declaration.
	private BoundIdentDecl bid = mBoundIdentDecl("x");

	// A free identifier.
	private FreeIdentifier idx = mFreeIdentifier("x");
	
	@SuppressWarnings("deprecation")
	private TestItem[] items = new TestItem[] {
			new TestItem(
					ff.makeFreeIdentifier("x", null),
					1
				),
				new TestItem(
					ff.makeBoundIdentDecl("x", null),
					1
				),
				new TestItem(
					ff.makeBoundIdentifier(0, null),
					1
				),
				new TestItem(
					ff.makeIntegerLiteral(BigInteger.ZERO, null),
					1
				),
				new TestItem(
					ff.makePredicateVariable("$P", null),
					1, new CounterVisitor2()
				),
				new TestItem(
					ff.makeSetExtension(mList(e1, e2, e3), null),
					7
				),
				new TestItem(
					ff.makeRelationalPredicate(Formula.EQUAL, e1, e2, null),
					5
				),
				new TestItem(
					ff.makeRelationalPredicate(Formula.NOTEQUAL, e1, e2, null),
					5
				),
				new TestItem(
					ff.makeRelationalPredicate(Formula.LT, e1, e2, null),
					5
				),
				new TestItem(
					ff.makeRelationalPredicate(Formula.LE, e1, e2, null),
					5
				),
				new TestItem(
					ff.makeRelationalPredicate(Formula.GT, e1, e2, null),
					5
				),
				new TestItem(
					ff.makeRelationalPredicate(Formula.GE, e1, e2, null),
					5
				),
				new TestItem(
					ff.makeRelationalPredicate(Formula.IN, e1, e2, null),
					5
				),
				new TestItem(
					ff.makeRelationalPredicate(Formula.NOTIN, e1, e2, null),
					5
				),
				new TestItem(
					ff.makeRelationalPredicate(Formula.SUBSET, e1, e2, null),
					5
				),
				new TestItem(
					ff.makeRelationalPredicate(Formula.NOTSUBSET, e1, e2, null),
					5
				),
				new TestItem(
					ff.makeRelationalPredicate(Formula.SUBSETEQ, e1, e2, null),
					5
				),
				new TestItem(
					ff.makeRelationalPredicate(Formula.NOTSUBSETEQ, e1, e2, null),
					5
				),
				new TestItem(
					ff.makeBinaryExpression(Formula.MAPSTO, e1, e2, null),
					5
				),
				new TestItem(
					ff.makeBinaryExpression(Formula.REL, e1, e2, null),
					5
				),
				new TestItem(
					ff.makeBinaryExpression(Formula.TREL, e1, e2, null),
					5
				),
				new TestItem(
					ff.makeBinaryExpression(Formula.SREL, e1, e2, null),
					5
				),
				new TestItem(
					ff.makeBinaryExpression(Formula.STREL, e1, e2, null),
					5
				),
				new TestItem(
					ff.makeBinaryExpression(Formula.PFUN, e1, e2, null),
					5
				),
				new TestItem(
					ff.makeBinaryExpression(Formula.TFUN, e1, e2, null),
					5
				),
				new TestItem(
					ff.makeBinaryExpression(Formula.PINJ, e1, e2, null),
					5
				),
				new TestItem(
					ff.makeBinaryExpression(Formula.TINJ, e1, e2, null),
					5
				),
				new TestItem(
					ff.makeBinaryExpression(Formula.PSUR, e1, e2, null),
					5
				),
				new TestItem(
					ff.makeBinaryExpression(Formula.TSUR, e1, e2, null),
					5
				),
				new TestItem(
					ff.makeBinaryExpression(Formula.TBIJ, e1, e2, null),
					5
				),
				new TestItem(
					ff.makeBinaryExpression(Formula.SETMINUS, e1, e2, null),
					5
				),
				new TestItem(
					ff.makeBinaryExpression(Formula.CPROD, e1, e2, null),
					5
				),
				new TestItem(
					ff.makeBinaryExpression(Formula.DPROD, e1, e2, null),
					5
				),
				new TestItem(
					ff.makeBinaryExpression(Formula.PPROD, e1, e2, null),
					5
				),
				new TestItem(
					ff.makeBinaryExpression(Formula.DOMRES, e1, e2, null),
					5
				),
				new TestItem(
					ff.makeBinaryExpression(Formula.DOMSUB, e1, e2, null),
					5
				),
				new TestItem(
					ff.makeBinaryExpression(Formula.RANRES, e1, e2, null),
					5
				),
				new TestItem(
					ff.makeBinaryExpression(Formula.RANSUB, e1, e2, null),
					5
				),
				new TestItem(
					ff.makeBinaryExpression(Formula.UPTO, e1, e2, null),
					5
				),
				new TestItem(
					ff.makeBinaryExpression(Formula.MINUS, e1, e2, null),
					5
				),
				new TestItem(
					ff.makeBinaryExpression(Formula.DIV, e1, e2, null),
					5
				),
				new TestItem(
					ff.makeBinaryExpression(Formula.MOD, e1, e2, null),
					5
				),
				new TestItem(
					ff.makeBinaryExpression(Formula.EXPN, e1, e2, null),
					5
				),
				new TestItem(
					ff.makeBinaryExpression(Formula.FUNIMAGE, e1, e2, null),
					5
				),
				new TestItem(
					ff.makeBinaryExpression(Formula.RELIMAGE, e1, e2, null),
					5
				),
				new TestItem(
					ff.makeBinaryPredicate(Formula.LIMP, p1, p2, null),
					5
				),
				new TestItem(
					ff.makeBinaryPredicate(Formula.LEQV, p1, p2, null),
					5
				),
				new TestItem(
					ff.makeAssociativeExpression(Formula.BUNION, mList(e1, e2, e3), null),
					7
				),
				new TestItem(
					ff.makeAssociativeExpression(Formula.BINTER, mList(e1, e2, e3), null),
					7
				),
				new TestItem(
					ff.makeAssociativeExpression(Formula.BCOMP, mList(e1, e2, e3), null),
					7
				),
				new TestItem(
					ff.makeAssociativeExpression(Formula.FCOMP, mList(e1, e2, e3), null),
					7
				),
				new TestItem(
					ff.makeAssociativeExpression(Formula.OVR, mList(e1, e2, e3), null),
					7
				),
				new TestItem(
					ff.makeAssociativeExpression(Formula.PLUS, mList(e1, e2, e3), null),
					7
				),
				new TestItem(
					ff.makeAssociativeExpression(Formula.MUL, mList(e1, e2, e3), null),
					7
				),
				new TestItem(
					ff.makeAssociativePredicate(Formula.LAND, mList(p1, p2, p3), null),
					7
				),
				new TestItem(
					ff.makeAssociativePredicate(Formula.LOR, mList(p1, p2, p3), null),
					7
				),
				new TestItem(
					ff.makeAtomicExpression(Formula.INTEGER, null),
					1
				),
				new TestItem(
					ff.makeAtomicExpression(Formula.NATURAL, null),
					1
				),
				new TestItem(
					ff.makeAtomicExpression(Formula.NATURAL1, null),
					1
				),
				new TestItem(
					ff.makeAtomicExpression(Formula.BOOL, null),
					1
				),
				new TestItem(
					ff.makeAtomicExpression(Formula.TRUE, null),
					1
				),
				new TestItem(
					ff.makeAtomicExpression(Formula.FALSE, null),
					1
				),
				new TestItem(
					ff.makeAtomicExpression(Formula.EMPTYSET, null),
					1
				),
				new TestItem(
					ff.makeAtomicExpression(Formula.KPRED, null),
					1
				),
				new TestItem(
					ff.makeAtomicExpression(Formula.KSUCC, null),
					1
				),
				new TestItem(
					ff.makeAtomicExpression(Formula.KPRJ1_GEN, null),
					1
				),
				new TestItem(
					ff.makeAtomicExpression(Formula.KPRJ2_GEN, null),
					1
				),
				new TestItem(
					ff.makeAtomicExpression(Formula.KID_GEN, null),
					1
				),
				new TestItem(
					ff.makeBoolExpression(p1, null),
					3
				),
				new TestItem(
					ff.makeLiteralPredicate(Formula.BTRUE, null),
					1
				),
				new TestItem(
					ff.makeLiteralPredicate(Formula.BFALSE, null),
					1
				),
				new TestItem(
					ff.makeSimplePredicate(Formula.KFINITE, e1, null),
					3
				),
				new TestItem(
					ff.makeUnaryPredicate(Formula.NOT, p1, null),
					3
				),
				new TestItem(
					ff.makeUnaryExpression(Formula.KCARD, e1, null),
					3
				),
				new TestItem(
					ff.makeUnaryExpression(Formula.POW, e1, null),
					3
				),
				new TestItem(
					ff.makeUnaryExpression(Formula.POW1, e1, null),
					3
				),
				new TestItem(
					ff.makeUnaryExpression(Formula.KUNION, e1, null),
					3
				),
				new TestItem(
					ff.makeUnaryExpression(Formula.KINTER, e1, null),
					3
				),
				new TestItem(
					ff.makeUnaryExpression(Formula.KDOM, e1, null),
					3
				),
				new TestItem(
					ff.makeUnaryExpression(Formula.KRAN, e1, null),
					3
				),
				new TestItem(
					ff.makeUnaryExpression(Formula.KPRJ1, e1, null),
					3
				),
				new TestItem(
					ff.makeUnaryExpression(Formula.KPRJ2, e1, null),
					3
				),
				new TestItem(
					ff.makeUnaryExpression(Formula.KID, e1, null),
					3
				),
				new TestItem(
					ff.makeUnaryExpression(Formula.KMIN, e1, null),
					3
				),
				new TestItem(
					ff.makeUnaryExpression(Formula.KMAX, e1, null),
					3
				),
				new TestItem(
					ff.makeUnaryExpression(Formula.CONVERSE, e1, null),
					3
				),
				new TestItem(
					ff.makeUnaryExpression(Formula.UNMINUS, e1, null),
					3
				),
				new TestItem(
					ff.makeQuantifiedExpression(Formula.QUNION, mList(bid), p1, e1, null, Explicit),
					7
				),
				new TestItem(
					ff.makeQuantifiedExpression(Formula.QINTER, mList(bid), p1, e1, null, Explicit),
					7
				),
				new TestItem(
					ff.makeQuantifiedExpression(Formula.CSET, mList(bid), p1, e1, null, Explicit),
					7
				),
				new TestItem(
					ff.makeQuantifiedPredicate(Formula.FORALL, mList(bid), p2, null),
					5
				),
				new TestItem(
					ff.makeQuantifiedPredicate(Formula.EXISTS, mList(bid), p2, null),
					5
				),
				new TestItem(
					ff.makeMultiplePredicate(Formula.KPARTITION, mList(e1, e2), null),
					5
				),
				new TestItem(
					ff.makeBecomesEqualTo(idx, e1, null),
					5
				),
				new TestItem(
					ff.makeBecomesMemberOf(idx, e1, null),
					5
				),
				new TestItem(
					ff.makeBecomesSuchThat(idx, bid, p1, null),
					7
				),
	};
	
	/**
	 * Simple test of the visitor implementation where we always do a full
	 * traversal of the AST.
	 */
	@Test 
	public void testAcceptFull() {
		for (TestItem item: items) {
			item.runTest();
		}
	}

	/**
	 * Test based on the examples given in {@link org.eventb.core.ast.IVisitor}
	 * documentation.
	 */
	@Test 
	public void testIVisitorDoc() {
		final Expression id_x = mFreeIdentifier("x");
		final Expression id_y = mFreeIdentifier("y");
		final Expression id_z = mFreeIdentifier("z");
		final Expression id_t = mFreeIdentifier("t");
		
		Expression expr = ff.makeAssociativeExpression(Formula.PLUS,
				mList(
						id_x,
						ff.makeBinaryExpression(Formula.MINUS, id_y, id_z, null),
						id_t
				), null
		);
		
		// Example without shortcut
		CounterVisitor visitor = new CounterVisitor();
		expr.accept(visitor);
		assertEquals(11, visitor.getCount());
		
		// Example with shortcut after "x"
		visitor = new CounterVisitor() {
			@Override
			public boolean visitFREE_IDENT(FreeIdentifier ident) {
				super.visitFREE_IDENT(ident);
				return ident != id_x;
			}
		};
		expr.accept(visitor);
		assertEquals(3, visitor.getCount());
		
		// Example with shortcut after entering MINUS
		visitor = new CounterVisitor() {
			@Override
			public boolean enterMINUS(BinaryExpression binExpr) {
				super.enterMINUS(binExpr);
				return false;
			}
		};
		expr.accept(visitor);
		assertEquals(8, visitor.getCount());

		// Example with shortcut after continuing PLUS
		visitor = new CounterVisitor() {
			@Override
			public boolean continuePLUS(AssociativeExpression assocExpr) {
				super.continuePLUS(assocExpr);
				return false;
			}
		};
		expr.accept(visitor);
		assertEquals(4, visitor.getCount());
	}

	/**
	 * Ensures that an old visitor run on a predicate variable raises an
	 * exception.
	 */
	@Test 
	public void testOldVisitorOnPredicateVariable() throws Exception {
		final Predicate pv = ff.makePredicateVariable("$P", null);
		assertException(pv, new CounterVisitor());
	}

}
