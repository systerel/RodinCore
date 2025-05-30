/*******************************************************************************
 * Copyright (c) 2006, 2025 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - mathematical language v2
 *     Systerel - added support for mathematical extensions
 *     Systerel - add given sets to free identifier cache
 *******************************************************************************/
package org.eventb.core.ast.tests;

import static java.util.Arrays.asList;
import static java.util.Collections.emptySet;
import static java.util.Collections.singleton;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

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
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.GivenType;
import org.eventb.core.ast.IVisitor;
import org.eventb.core.ast.Identifier;
import org.eventb.core.ast.IntegerLiteral;
import org.eventb.core.ast.LiteralPredicate;
import org.eventb.core.ast.MultiplePredicate;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.QuantifiedExpression;
import org.eventb.core.ast.QuantifiedPredicate;
import org.eventb.core.ast.RelationalPredicate;
import org.eventb.core.ast.SetExtension;
import org.eventb.core.ast.SimplePredicate;
import org.eventb.core.ast.Type;
import org.eventb.core.ast.UnaryExpression;
import org.eventb.core.ast.UnaryPredicate;

/**
 * Checks the identifier caches of formulas. Call method
 * {@link #check(Formula)} to check some formula.
 * <p>
 * The check is implemented by traversing the AST of the formula, collecting
 * both bound and free identifiers. We then verify on every node that the caches
 * indeed contain the identifiers that were collected during traversal.
 * </p>
 * <p>
 * The traversal uses a stack to remember the nodes that have been traversed. At
 * any time, the stack contains all the nodes that are either above (parents) of
 * before (left-hand side siblings) of the current node. During the traversal of
 * an internal node, the stack also contains the children of the node (which are
 * popped off when exiting the node).
 * </p>
 * 
 * @author Laurent Voisin
 */
public class IdentsChecker implements IVisitor {

	/**
	 * Checks the identifiers cache of the given formula. This is the entry
	 * point of this class.
	 * 
	 * @param formula
	 *            the formula to check
	 */
	public static void check(Formula<?> formula) {
		final IdentsChecker checker = new IdentsChecker();
		formula.accept(checker);
		// Self-test ensure stack contains only root
		assertEquals(1, checker.stack.size());
		assertEquals(formula, checker.stack.peek());
	}

	/*
	 * Checks whether the formula bears the expected type-checker caches of
	 * identifiers.
	 */
	private static void checkFormula(Formula<?> formula,
			Set<FreeIdentifier> freeIdents, Set<BoundIdentifier> boundIdents) {
		assertEqualFreeIdentifiers(formula, freeIdents);
		assertEqualBoundIdentifiers(formula, boundIdents);
	}

	// If the formula is not type-checked, we check only names, ignoring any
	// type information.
	private static void assertEqualFreeIdentifiers(Formula<?> formula,
			Set<FreeIdentifier> expected) {
		final FreeIdentifier[] actual = formula.getFreeIdentifiers();
		assertSorted(actual);
		if (formula.isTypeChecked()) {
			assertTypeChecked(actual);
			assertEqualSets(expected, actual);
		} else {
			assertEqualNames(expected, actual);
		}
	}

	private static void assertSorted(FreeIdentifier[] actual) {
		String last = "";
		for (final FreeIdentifier ident : actual) {
			final String name = ident.getName();
			assertTrue(last.compareTo(name) < 0);
			last = name;
		}
	}

	private static void assertEqualBoundIdentifiers(Formula<?> formula,
			Set<BoundIdentifier> expected) {
		final BoundIdentifier[] actual = formula.getBoundIdentifiers();
		assertSorted(actual);
		if (formula.isTypeChecked()) {
			assertTypeChecked(actual);
		}
		assertEqualSets(expected, actual);
	}

	private static void assertSorted(BoundIdentifier[] actual) {
		int last = -1;
		for (final BoundIdentifier ident : actual) {
			final int index = ident.getBoundIndex();
			assertTrue(last < index);
			last = index;
		}
	}

	private static <T extends Identifier> void assertTypeChecked(T[] idents) {
		for (final T ident : idents) {
			assertTrue(ident.isTypeChecked());
		}
	}

	private static <T> void assertEqualSets(Set<T> expected, T[] array) {
		final Set<T> actual = new HashSet<T>(asList(array));
		assertEquals(expected, actual);
	}

	private static void assertEqualNames(Set<FreeIdentifier> expected,
			FreeIdentifier[] actual) {
		final Set<String> expectedNames = namesOf(expected);
		final Set<String> actualNames = namesOf(asList(actual));
		assertEquals(expectedNames, actualNames);
	}

	private static Set<String> namesOf(Collection<FreeIdentifier> idents) {
		final Set<String> result = new HashSet<String>(idents.size());
		for (FreeIdentifier ident : idents) {
			result.add(ident.getName());
		}
		return result;
	}

	final private Stack<Formula<?>> stack;


	private IdentsChecker() {
		this.stack = new Stack<Formula<?>>();
	}

	@Override
	public boolean continueBCOMP(AssociativeExpression expr) {
		return true;
	}

	@Override
	public boolean continueBINTER(AssociativeExpression expr) {
		return true;
	}

	@Override
	public boolean continueBUNION(AssociativeExpression expr) {
		return true;
	}

	@Override
	public boolean continueCPROD(BinaryExpression expr) {
		return true;
	}

	@Override
	public boolean continueCSET(QuantifiedExpression expr) {
		return true;
	}

	@Override
	public boolean continueDIV(BinaryExpression expr) {
		return true;
	}

	@Override
	public boolean continueDOMRES(BinaryExpression expr) {
		return true;
	}

	@Override
	public boolean continueDOMSUB(BinaryExpression expr) {
		return true;
	}

	@Override
	public boolean continueDPROD(BinaryExpression expr) {
		return true;
	}

	@Override
	public boolean continueEQUAL(RelationalPredicate pred) {
		return true;
	}

	@Override
	public boolean continueEXISTS(QuantifiedPredicate pred) {
		return true;
	}

	@Override
	public boolean continueEXPN(BinaryExpression expr) {
		return true;
	}

	@Override
	public boolean continueFCOMP(AssociativeExpression expr) {
		return true;
	}

	@Override
	public boolean continueFORALL(QuantifiedPredicate pred) {
		return true;
	}

	@Override
	public boolean continueFUNIMAGE(BinaryExpression expr) {
		return true;
	}

	@Override
	public boolean continueGE(RelationalPredicate pred) {
		return true;
	}

	@Override
	public boolean continueGT(RelationalPredicate pred) {
		return true;
	}

	@Override
	public boolean continueIN(RelationalPredicate pred) {
		return true;
	}

	@Override
	public boolean continueLAND(AssociativePredicate pred) {
		return true;
	}

	@Override
	public boolean continueLE(RelationalPredicate pred) {
		return true;
	}

	@Override
	public boolean continueLEQV(BinaryPredicate pred) {
		return true;
	}

	@Override
	public boolean continueLIMP(BinaryPredicate pred) {
		return true;
	}

	@Override
	public boolean continueLOR(AssociativePredicate pred) {
		return true;
	}

	@Override
	public boolean continueLT(RelationalPredicate pred) {
		return true;
	}

	@Override
	public boolean continueMAPSTO(BinaryExpression expr) {
		return true;
	}

	@Override
	public boolean continueMINUS(BinaryExpression expr) {
		return true;
	}

	@Override
	public boolean continueMOD(BinaryExpression expr) {
		return true;
	}

	@Override
	public boolean continueMUL(AssociativeExpression expr) {
		return true;
	}

	@Override
	public boolean continueNOTEQUAL(RelationalPredicate pred) {
		return true;
	}

	@Override
	public boolean continueNOTIN(RelationalPredicate pred) {
		return true;
	}

	@Override
	public boolean continueNOTSUBSET(RelationalPredicate pred) {
		return true;
	}

	@Override
	public boolean continueNOTSUBSETEQ(RelationalPredicate pred) {
		return true;
	}

	@Override
	public boolean continueOVR(AssociativeExpression expr) {
		return true;
	}

	@Override
	public boolean continuePFUN(BinaryExpression expr) {
		return true;
	}

	@Override
	public boolean continuePINJ(BinaryExpression expr) {
		return true;
	}

	@Override
	public boolean continuePLUS(AssociativeExpression expr) {
		return true;
	}

	@Override
	public boolean continuePPROD(BinaryExpression expr) {
		return true;
	}

	@Override
	public boolean continuePSUR(BinaryExpression expr) {
		return true;
	}

	@Override
	public boolean continueQINTER(QuantifiedExpression expr) {
		return true;
	}

	@Override
	public boolean continueQUNION(QuantifiedExpression expr) {
		return true;
	}

	@Override
	public boolean continueRANRES(BinaryExpression expr) {
		return true;
	}

	@Override
	public boolean continueRANSUB(BinaryExpression expr) {
		return true;
	}

	@Override
	public boolean continueREL(BinaryExpression expr) {
		return true;
	}

	@Override
	public boolean continueRELIMAGE(BinaryExpression expr) {
		return true;
	}

	@Override
	public boolean continueSETEXT(SetExtension set) {
		return true;
	}

	@Override
	public boolean continueSETMINUS(BinaryExpression expr) {
		return true;
	}

	@Override
	public boolean continueSREL(BinaryExpression expr) {
		return true;
	}

	@Override
	public boolean continueSTREL(BinaryExpression expr) {
		return true;
	}

	@Override
	public boolean continueSUBSET(RelationalPredicate pred) {
		return true;
	}

	@Override
	public boolean continueSUBSETEQ(RelationalPredicate pred) {
		return true;
	}

	@Override
	public boolean continueTBIJ(BinaryExpression expr) {
		return true;
	}

	@Override
	public boolean continueTFUN(BinaryExpression expr) {
		return true;
	}

	@Override
	public boolean continueTINJ(BinaryExpression expr) {
		return true;
	}

	@Override
	public boolean continueTREL(BinaryExpression expr) {
		return true;
	}

	@Override
	public boolean continueTSUR(BinaryExpression expr) {
		return true;
	}

	@Override
	public boolean continueUPTO(BinaryExpression expr) {
		return true;
	}

	@Override
	public boolean enterBCOMP(AssociativeExpression expr) {
		return standardEnter(expr);
	}

	@Override
	public boolean enterBINTER(AssociativeExpression expr) {
		return standardEnter(expr);
	}

	@Override
	public boolean enterBUNION(AssociativeExpression expr) {
		return standardEnter(expr);
	}

	@Override
	public boolean enterCONVERSE(UnaryExpression expr) {
		return standardEnter(expr);
	}

	@Override
	public boolean enterCPROD(BinaryExpression expr) {
		return standardEnter(expr);
	}

	@Override
	public boolean enterCSET(QuantifiedExpression expr) {
		return standardEnter(expr);
	}

	@Override
	public boolean enterDIV(BinaryExpression expr) {
		return standardEnter(expr);
	}

	@Override
	public boolean enterDOMRES(BinaryExpression expr) {
		return standardEnter(expr);
	}

	@Override
	public boolean enterDOMSUB(BinaryExpression expr) {
		return standardEnter(expr);
	}

	@Override
	public boolean enterDPROD(BinaryExpression expr) {
		return standardEnter(expr);
	}

	@Override
	public boolean enterEQUAL(RelationalPredicate pred) {
		return standardEnter(pred);
	}

	@Override
	public boolean enterEXISTS(QuantifiedPredicate pred) {
		return standardEnter(pred);
	}

	@Override
	public boolean enterEXPN(BinaryExpression expr) {
		return standardEnter(expr);
	}

	@Override
	public boolean enterFCOMP(AssociativeExpression expr) {
		return standardEnter(expr);
	}

	@Override
	public boolean enterFORALL(QuantifiedPredicate pred) {
		return standardEnter(pred);
	}

	@Override
	public boolean enterFUNIMAGE(BinaryExpression expr) {
		return standardEnter(expr);
	}

	@Override
	public boolean enterGE(RelationalPredicate pred) {
		return standardEnter(pred);
	}

	@Override
	public boolean enterGT(RelationalPredicate pred) {
		return standardEnter(pred);
	}

	@Override
	public boolean enterIN(RelationalPredicate pred) {
		return standardEnter(pred);
	}

	@Override
	public boolean enterKBOOL(BoolExpression expr) {
		return standardEnter(expr);
	}

	@Override
	public boolean enterKCARD(UnaryExpression expr) {
		return standardEnter(expr);
	}

	@Override
	public boolean enterKDOM(UnaryExpression expr) {
		return standardEnter(expr);
	}

	@Override
	public boolean enterKFINITE(SimplePredicate pred) {
		return standardEnter(pred);
	}

	@Override
	public boolean enterKID(UnaryExpression expr) {
		return standardEnter(expr);
	}

	@Override
	public boolean enterKINTER(UnaryExpression expr) {
		return standardEnter(expr);
	}

	@Override
	public boolean enterKMAX(UnaryExpression expr) {
		return standardEnter(expr);
	}

	@Override
	public boolean enterKMIN(UnaryExpression expr) {
		return standardEnter(expr);
	}

	@Override
	public boolean enterKPRJ1(UnaryExpression expr) {
		return standardEnter(expr);
	}

	@Override
	public boolean enterKPRJ2(UnaryExpression expr) {
		return standardEnter(expr);
	}

	@Override
	public boolean enterKRAN(UnaryExpression expr) {
		return standardEnter(expr);
	}

	@Override
	public boolean enterKUNION(UnaryExpression expr) {
		return standardEnter(expr);
	}

	@Override
	public boolean enterLAND(AssociativePredicate pred) {
		return standardEnter(pred);
	}

	@Override
	public boolean enterLE(RelationalPredicate pred) {
		return standardEnter(pred);
	}

	@Override
	public boolean enterLEQV(BinaryPredicate pred) {
		return standardEnter(pred);
	}

	@Override
	public boolean enterLIMP(BinaryPredicate pred) {
		return standardEnter(pred);
	}

	@Override
	public boolean enterLOR(AssociativePredicate pred) {
		return standardEnter(pred);
	}

	@Override
	public boolean enterLT(RelationalPredicate pred) {
		return standardEnter(pred);
	}

	@Override
	public boolean enterMAPSTO(BinaryExpression expr) {
		return standardEnter(expr);
	}

	@Override
	public boolean enterMINUS(BinaryExpression expr) {
		return standardEnter(expr);
	}

	@Override
	public boolean enterMOD(BinaryExpression expr) {
		return standardEnter(expr);
	}

	@Override
	public boolean enterMUL(AssociativeExpression expr) {
		return standardEnter(expr);
	}

	@Override
	public boolean enterNOT(UnaryPredicate pred) {
		return standardEnter(pred);
	}

	@Override
	public boolean enterNOTEQUAL(RelationalPredicate pred) {
		return standardEnter(pred);
	}

	@Override
	public boolean enterNOTIN(RelationalPredicate pred) {
		return standardEnter(pred);
	}

	@Override
	public boolean enterNOTSUBSET(RelationalPredicate pred) {
		return standardEnter(pred);
	}

	@Override
	public boolean enterNOTSUBSETEQ(RelationalPredicate pred) {
		return standardEnter(pred);
	}

	@Override
	public boolean enterOVR(AssociativeExpression expr) {
		return standardEnter(expr);
	}

	@Override
	public boolean enterPFUN(BinaryExpression expr) {
		return standardEnter(expr);
	}

	@Override
	public boolean enterPINJ(BinaryExpression expr) {
		return standardEnter(expr);
	}

	@Override
	public boolean enterPLUS(AssociativeExpression expr) {
		return standardEnter(expr);
	}

	@Override
	public boolean enterPOW(UnaryExpression expr) {
		return standardEnter(expr);
	}

	@Override
	public boolean enterPOW1(UnaryExpression expr) {
		return standardEnter(expr);
	}

	@Override
	public boolean enterPPROD(BinaryExpression expr) {
		return standardEnter(expr);
	}

	@Override
	public boolean enterPSUR(BinaryExpression expr) {
		return standardEnter(expr);
	}

	@Override
	public boolean enterQINTER(QuantifiedExpression expr) {
		return standardEnter(expr);
	}

	@Override
	public boolean enterQUNION(QuantifiedExpression expr) {
		return standardEnter(expr);
	}

	@Override
	public boolean enterRANRES(BinaryExpression expr) {
		return standardEnter(expr);
	}

	@Override
	public boolean enterRANSUB(BinaryExpression expr) {
		return standardEnter(expr);
	}

	@Override
	public boolean enterREL(BinaryExpression expr) {
		return standardEnter(expr);
	}

	@Override
	public boolean enterRELIMAGE(BinaryExpression expr) {
		return standardEnter(expr);
	}

	@Override
	public boolean enterSETEXT(SetExtension set) {
		return standardEnter(set);
	}

	@Override
	public boolean enterSETMINUS(BinaryExpression expr) {
		return standardEnter(expr);
	}

	@Override
	public boolean enterSREL(BinaryExpression expr) {
		return standardEnter(expr);
	}

	@Override
	public boolean enterSTREL(BinaryExpression expr) {
		return standardEnter(expr);
	}

	@Override
	public boolean enterSUBSET(RelationalPredicate pred) {
		return standardEnter(pred);
	}

	@Override
	public boolean enterSUBSETEQ(RelationalPredicate pred) {
		return standardEnter(pred);
	}

	@Override
	public boolean enterTBIJ(BinaryExpression expr) {
		return standardEnter(expr);
	}

	@Override
	public boolean enterTFUN(BinaryExpression expr) {
		return standardEnter(expr);
	}

	@Override
	public boolean enterTINJ(BinaryExpression expr) {
		return standardEnter(expr);
	}

	@Override
	public boolean enterTREL(BinaryExpression expr) {
		return standardEnter(expr);
	}

	@Override
	public boolean enterTSUR(BinaryExpression expr) {
		return standardEnter(expr);
	}

	@Override
	public boolean enterUNMINUS(UnaryExpression expr) {
		return standardEnter(expr);
	}

	@Override
	public boolean enterUPTO(BinaryExpression expr) {
		return standardEnter(expr);
	}

	@Override
	public boolean exitBCOMP(AssociativeExpression expr) {
		return standardExit(expr);
	}

	@Override
	public boolean exitBINTER(AssociativeExpression expr) {
		return standardExit(expr);
	}

	@Override
	public boolean exitBUNION(AssociativeExpression expr) {
		return standardExit(expr);
	}

	@Override
	public boolean exitCONVERSE(UnaryExpression expr) {
		return standardExit(expr);
	}

	@Override
	public boolean exitCPROD(BinaryExpression expr) {
		return standardExit(expr);
	}

	@Override
	public boolean exitCSET(QuantifiedExpression expr) {
		return quantifiedExit(expr, expr.getBoundIdentDecls().length);
	}

	@Override
	public boolean exitDIV(BinaryExpression expr) {
		return standardExit(expr);
	}

	@Override
	public boolean exitDOMRES(BinaryExpression expr) {
		return standardExit(expr);
	}

	@Override
	public boolean exitDOMSUB(BinaryExpression expr) {
		return standardExit(expr);
	}

	@Override
	public boolean exitDPROD(BinaryExpression expr) {
		return standardExit(expr);
	}

	@Override
	public boolean exitEQUAL(RelationalPredicate pred) {
		return standardExit(pred);
	}

	@Override
	public boolean exitEXISTS(QuantifiedPredicate pred) {
		return quantifiedExit(pred, pred.getBoundIdentDecls().length);
	}

	@Override
	public boolean exitEXPN(BinaryExpression expr) {
		return standardExit(expr);
	}

	@Override
	public boolean exitFCOMP(AssociativeExpression expr) {
		return standardExit(expr);
	}

	@Override
	public boolean exitFORALL(QuantifiedPredicate pred) {
		return quantifiedExit(pred, pred.getBoundIdentDecls().length);
	}

	@Override
	public boolean exitFUNIMAGE(BinaryExpression expr) {
		return standardExit(expr);
	}

	@Override
	public boolean exitGE(RelationalPredicate pred) {
		return standardExit(pred);
	}

	@Override
	public boolean exitGT(RelationalPredicate pred) {
		return standardExit(pred);
	}

	@Override
	public boolean exitIN(RelationalPredicate pred) {
		return standardExit(pred);
	}

	@Override
	public boolean exitKBOOL(BoolExpression expr) {
		return standardExit(expr);
	}

	@Override
	public boolean exitKCARD(UnaryExpression expr) {
		return standardExit(expr);
	}

	@Override
	public boolean exitKDOM(UnaryExpression expr) {
		return standardExit(expr);
	}

	@Override
	public boolean exitKFINITE(SimplePredicate pred) {
		return standardExit(pred);
	}

	@Override
	public boolean exitKID(UnaryExpression expr) {
		return standardExit(expr);
	}

	@Override
	public boolean exitKINTER(UnaryExpression expr) {
		return standardExit(expr);
	}

	@Override
	public boolean exitKMAX(UnaryExpression expr) {
		return standardExit(expr);
	}

	@Override
	public boolean exitKMIN(UnaryExpression expr) {
		return standardExit(expr);
	}

	@Override
	public boolean exitKPRJ1(UnaryExpression expr) {
		return standardExit(expr);
	}

	@Override
	public boolean exitKPRJ2(UnaryExpression expr) {
		return standardExit(expr);
	}

	@Override
	public boolean exitKRAN(UnaryExpression expr) {
		return standardExit(expr);
	}

	@Override
	public boolean exitKUNION(UnaryExpression expr) {
		return standardExit(expr);
	}

	@Override
	public boolean exitLAND(AssociativePredicate pred) {
		return standardExit(pred);
	}

	@Override
	public boolean exitLE(RelationalPredicate pred) {
		return standardExit(pred);
	}

	@Override
	public boolean exitLEQV(BinaryPredicate pred) {
		return standardExit(pred);
	}

	@Override
	public boolean exitLIMP(BinaryPredicate pred) {
		return standardExit(pred);
	}

	@Override
	public boolean exitLOR(AssociativePredicate pred) {
		return standardExit(pred);
	}

	@Override
	public boolean exitLT(RelationalPredicate pred) {
		return standardExit(pred);
	}

	@Override
	public boolean exitMAPSTO(BinaryExpression expr) {
		return standardExit(expr);
	}

	@Override
	public boolean exitMINUS(BinaryExpression expr) {
		return standardExit(expr);
	}

	@Override
	public boolean exitMOD(BinaryExpression expr) {
		return standardExit(expr);
	}

	@Override
	public boolean exitMUL(AssociativeExpression expr) {
		return standardExit(expr);
	}

	@Override
	public boolean exitNOT(UnaryPredicate pred) {
		return standardExit(pred);
	}

	@Override
	public boolean exitNOTEQUAL(RelationalPredicate pred) {
		return standardExit(pred);
	}

	@Override
	public boolean exitNOTIN(RelationalPredicate pred) {
		return standardExit(pred);
	}

	@Override
	public boolean exitNOTSUBSET(RelationalPredicate pred) {
		return standardExit(pred);
	}

	@Override
	public boolean exitNOTSUBSETEQ(RelationalPredicate pred) {
		return standardExit(pred);
	}

	@Override
	public boolean exitOVR(AssociativeExpression expr) {
		return standardExit(expr);
	}

	@Override
	public boolean exitPFUN(BinaryExpression expr) {
		return standardExit(expr);
	}

	@Override
	public boolean exitPINJ(BinaryExpression expr) {
		return standardExit(expr);
	}

	@Override
	public boolean exitPLUS(AssociativeExpression expr) {
		return standardExit(expr);
	}

	@Override
	public boolean exitPOW(UnaryExpression expr) {
		return standardExit(expr);
	}

	@Override
	public boolean exitPOW1(UnaryExpression expr) {
		return standardExit(expr);
	}

	@Override
	public boolean exitPPROD(BinaryExpression expr) {
		return standardExit(expr);
	}

	@Override
	public boolean exitPSUR(BinaryExpression expr) {
		return standardExit(expr);
	}

	@Override
	public boolean exitQINTER(QuantifiedExpression expr) {
		return quantifiedExit(expr, expr.getBoundIdentDecls().length);
	}

	@Override
	public boolean exitQUNION(QuantifiedExpression expr) {
		return quantifiedExit(expr, expr.getBoundIdentDecls().length);
	}

	@Override
	public boolean exitRANRES(BinaryExpression expr) {
		return standardExit(expr);
	}

	@Override
	public boolean exitRANSUB(BinaryExpression expr) {
		return standardExit(expr);
	}

	@Override
	public boolean exitREL(BinaryExpression expr) {
		return standardExit(expr);
	}

	@Override
	public boolean exitRELIMAGE(BinaryExpression expr) {
		return standardExit(expr);
	}

	@Override
	public boolean exitSETEXT(SetExtension set) {
		return standardExit(set);
	}

	@Override
	public boolean exitSETMINUS(BinaryExpression expr) {
		return standardExit(expr);
	}

	@Override
	public boolean exitSREL(BinaryExpression expr) {
		return standardExit(expr);
	}

	@Override
	public boolean exitSTREL(BinaryExpression expr) {
		return standardExit(expr);
	}

	@Override
	public boolean exitSUBSET(RelationalPredicate pred) {
		return standardExit(pred);
	}

	@Override
	public boolean exitSUBSETEQ(RelationalPredicate pred) {
		return standardExit(pred);
	}

	@Override
	public boolean exitTBIJ(BinaryExpression expr) {
		return standardExit(expr);
	}

	@Override
	public boolean exitTFUN(BinaryExpression expr) {
		return standardExit(expr);
	}

	@Override
	public boolean exitTINJ(BinaryExpression expr) {
		return standardExit(expr);
	}

	@Override
	public boolean exitTREL(BinaryExpression expr) {
		return standardExit(expr);
	}

	@Override
	public boolean exitTSUR(BinaryExpression expr) {
		return standardExit(expr);
	}

	@Override
	public boolean exitUNMINUS(UnaryExpression expr) {
		return standardExit(expr);
	}

	@Override
	public boolean exitUPTO(BinaryExpression expr) {
		return standardExit(expr);
	}

	/*
	 * Checks cached identifier sets of a quantified formula (on exit).
	 */
	private boolean quantifiedExit(Formula<?> formula, int nbBoundIdentDecls) {
		final Set<FreeIdentifier> freeIdents = new HashSet<FreeIdentifier>();
		Set<BoundIdentifier> boundIdents = new HashSet<BoundIdentifier>();
		while (stack.peek() != formula) {
			final Formula<?> child = stack.pop();
			freeIdents.addAll(asList(child.getFreeIdentifiers()));
			boundIdents.addAll(asList(child.getBoundIdentifiers()));
		}
		boundIdents = renumber(boundIdents, nbBoundIdentDecls);
		checkFormula(formula, freeIdents, boundIdents);
		return true;
	}

	private Set<BoundIdentifier> renumber(Set<BoundIdentifier> boundIdents,
			int nbBoundIdentDecls) {
		final Set<BoundIdentifier> result = new HashSet<BoundIdentifier>();
		for (final BoundIdentifier boundIdent : boundIdents) {
			final int index = boundIdent.getBoundIndex();
			if (nbBoundIdentDecls <= index) {
				result.add(boundIdent.getFactory().makeBoundIdentifier(
						index - nbBoundIdentDecls,
						boundIdent.getSourceLocation(), boundIdent.getType()));
			}
		}
		return result;
	}

	private boolean standardEnter(Formula<?> formula) {
		stack.push(formula);
		return true;
	}

	/*
	 * Checks cached identifier sets of a non-atomic formula (on exit).
	 */
	private boolean standardExit(Formula<?> formula) {
		final Set<FreeIdentifier> freeIdents = new HashSet<FreeIdentifier>();
		final Set<BoundIdentifier> boundIdents = new HashSet<BoundIdentifier>();
		while (stack.peek() != formula) {
			final Formula<?> child = stack.pop();
			freeIdents.addAll(asList(child.getFreeIdentifiers()));
			boundIdents.addAll(asList(child.getBoundIdentifiers()));
		}
		// The type of an expression also contributes free identifiers
		if (formula instanceof Expression expr) {
			freeIdents.addAll(getGivenTypeIdentifiers(expr));
		}
		checkFormula(formula, freeIdents, boundIdents);
		return true;
	}

	private boolean standardVisitExpression(Expression expr) {
		stack.push(expr);
		final Set<FreeIdentifier> freeIdents = getGivenTypeIdentifiers(expr);
		final Set<BoundIdentifier> boundIdents = emptySet();
		checkFormula(expr, freeIdents, boundIdents);
		return true;
	}

	private boolean standardVisitPredicate(Predicate pred) {
		stack.push(pred);
		final Set<FreeIdentifier> freeIdents = emptySet();
		final Set<BoundIdentifier> boundIdents = emptySet();
		checkFormula(pred, freeIdents, boundIdents);
		return true;
	}

	@Override
	public boolean visitBFALSE(LiteralPredicate pred) {
		return standardVisitPredicate(pred);
	}

	@Override
	public boolean visitBOOL(AtomicExpression expr) {
		return standardVisitExpression(expr);
	}

	@Override
	public boolean visitBOUND_IDENT(BoundIdentifier ident) {
		stack.push(ident);
		final Set<FreeIdentifier> freeIdents = getGivenTypeIdentifiers(ident);
		final Set<BoundIdentifier> boundIdents = singleton(ident);
		checkFormula(ident, freeIdents, boundIdents);
		return true;
	}

	@Override
	public boolean visitBOUND_IDENT_DECL(BoundIdentDecl decl) {
		stack.push(decl);
		final Set<FreeIdentifier> freeIdents = getGivenTypeIdentifiers(decl);
		final Set<BoundIdentifier> boundIdents = emptySet();
		checkFormula(decl, freeIdents, boundIdents);
		return true;
	}

	@Override
	public boolean visitBTRUE(LiteralPredicate pred) {
		return standardVisitPredicate(pred);
	}

	@Override
	public boolean visitEMPTYSET(AtomicExpression expr) {
		return standardVisitExpression(expr);
	}

	@Override
	public boolean visitFALSE(AtomicExpression expr) {
		return standardVisitExpression(expr);
	}

	@Override
	public boolean visitFREE_IDENT(FreeIdentifier ident) {
		stack.push(ident);
		final Set<FreeIdentifier> freeIdents = getGivenTypeIdentifiers(ident);
		freeIdents.add(ident);
		final Set<BoundIdentifier> boundIdents = emptySet();
		checkFormula(ident, freeIdents, boundIdents);
		return true;
	}

	@Override
	public boolean visitINTEGER(AtomicExpression expr) {
		return standardVisitExpression(expr);
	}

	@Override
	public boolean visitINTLIT(IntegerLiteral lit) {
		return standardVisitExpression(lit);
	}

	@Override
	public boolean visitKPRED(AtomicExpression expr) {
		return standardVisitExpression(expr);
	}

	@Override
	public boolean visitKSUCC(AtomicExpression expr) {
		return standardVisitExpression(expr);
	}

	@Override
	public boolean visitNATURAL(AtomicExpression expr) {
		return standardVisitExpression(expr);
	}

	@Override
	public boolean visitNATURAL1(AtomicExpression expr) {
		return standardVisitExpression(expr);
	}

	@Override
	public boolean visitTRUE(AtomicExpression expr) {
		return standardVisitExpression(expr);
	}

	@Override
	public boolean enterBECOMES_EQUAL_TO(BecomesEqualTo assign) {
		return standardEnter(assign);
	}

	@Override
	public boolean continueBECOMES_EQUAL_TO(BecomesEqualTo assign) {
		return true;
	}

	@Override
	public boolean exitBECOMES_EQUAL_TO(BecomesEqualTo assign) {
		return standardExit(assign);
	}

	@Override
	public boolean enterBECOMES_MEMBER_OF(BecomesMemberOf assign) {
		return standardEnter(assign);
	}

	@Override
	public boolean continueBECOMES_MEMBER_OF(BecomesMemberOf assign) {
		return true;
	}

	@Override
	public boolean exitBECOMES_MEMBER_OF(BecomesMemberOf assign) {
		return standardExit(assign);
	}

	@Override
	public boolean enterBECOMES_SUCH_THAT(BecomesSuchThat assign) {
		return standardEnter(assign);
	}

	@Override
	public boolean continueBECOMES_SUCH_THAT(BecomesSuchThat assign) {
		return true;
	}

	@Override
	public boolean exitBECOMES_SUCH_THAT(BecomesSuchThat assign) {
		return quantifiedExit(assign, assign.getPrimedIdents().length);
	}

	@Override
	public boolean enterKPARTITION(MultiplePredicate pred) {
		return standardEnter(pred);
	}

	@Override
	public boolean continueKPARTITION(MultiplePredicate pred) {
		return true;
	}

	@Override
	public boolean exitKPARTITION(MultiplePredicate pred) {
		return standardExit(pred);
	}

	@Override
	public boolean visitKID_GEN(AtomicExpression expr) {
		return standardVisitExpression(expr);
	}

	@Override
	public boolean visitKPRJ1_GEN(AtomicExpression expr) {
		return standardVisitExpression(expr);
	}

	@Override
	public boolean visitKPRJ2_GEN(AtomicExpression expr) {
		return standardVisitExpression(expr);
	}

	@Override
	public boolean enterExtendedExpression(ExtendedExpression expr) {
		return standardEnter(expr);
	}

	@Override
	public boolean continueExtendedExpression(ExtendedExpression expr) {
		return true;
	}

	@Override
	public boolean exitExtendedExpression(ExtendedExpression expr) {
		return standardExit(expr);
	}

	@Override
	public boolean enterExtendedPredicate(ExtendedPredicate pred) {
		return standardEnter(pred);
	}

	@Override
	public boolean continueExtendedPredicate(ExtendedPredicate pred) {
		return true;
	}

	@Override
	public boolean exitExtendedPredicate(ExtendedPredicate pred) {
		return standardExit(pred);
	}

	// Returns a set containing the identifiers for each given type
	// occurring in the type of the given formula (if any)
	private Set<FreeIdentifier> getGivenTypeIdentifiers(Expression expr) {
		return getGivenTypeIdentifiers(expr.getType());
	}

	private Set<FreeIdentifier> getGivenTypeIdentifiers(BoundIdentDecl decl) {
		return getGivenTypeIdentifiers(decl.getType());
	}

	private Set<FreeIdentifier> getGivenTypeIdentifiers(Type type) {
		final Set<FreeIdentifier> result = new HashSet<FreeIdentifier>();
		if (type == null) {
			return result; // must not be immutable
		}
		for (final GivenType givenType : type.getGivenTypes()) {
			result.add(givenType.toExpression());
		}
		return result;
	}

}
