/*******************************************************************************
 * Copyright (c) 2006, 2013 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - mathematical language v2
 *     Systerel - added support for mathematical extensions
 *******************************************************************************/
package org.eventb.core.ast.tests;

import static java.util.Collections.emptySet;
import static java.util.Collections.singleton;

import java.util.Arrays;
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
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.GivenType;
import org.eventb.core.ast.IVisitor;
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
 * Implements the checking of the identifier caches of the formula type-checker,
 * using the AST Visitor design pattern. Identifier cache in a formula must
 * always be coherent with the set of identifiers that could be constructed when
 * exiting a node using the stack part of visited nodes and leaves under that
 * node to retrieve all identifiers defined.
 * 
 * @author Laurent Voisin
 */
public class IdentsChecker implements IVisitor {

	/**
	 * Checks that the given set contains exactly the elements of the array.
	 * 
	 * @param <T>
	 *            type of the elements
	 * @param set
	 *            a set of elements
	 * @param array
	 *            an array of elements
	 * @return <code>true</code> iff the set and the array contain exactly the
	 *         same elements (without duplicates)
	 */
	private static <T> boolean areEqual(Set<T> set, T[] array) {
		if (set.size() != array.length) {
			return false;
		}
		Set<T> other = new HashSet<T>(Arrays.asList(array));
		return set.equals(other);
	}

	private static boolean areEqualFreeIdentifiers(Formula<?> f,
			Set<FreeIdentifier> expected, FreeIdentifier[] found) {
		if (f.isTypeChecked()) {
			return areEqual(expected, found);
		} else {
			// If formula is not typed we can only check that all free
			// identifiers names are present (same name could have different
			// types in expected set whereas in the array found only one type is
			// keeped).
			Set<String> names_expected = new HashSet<String>(expected.size());
			Set<String> names_present = new HashSet<String>(found.length);
			for (FreeIdentifier freeid : expected) {
				names_expected.add(freeid.getName());
			}
			for (FreeIdentifier freeid : found) {
				names_present.add(freeid.getName());
			}
			return names_expected.equals(names_expected);
		}
	}

	public static boolean check(Formula<?> f, FormulaFactory factory) {
		IdentsChecker checker = new IdentsChecker(f, factory);
		f.accept(checker);
		if (checker.success) {
			// Self-test ensure stack contains only root if successful
			assert checker.stack.size() == 1;
			assert checker.stack.peek() == f;
		}
		return checker.success;
	}

	/**
	 * Checks whether the formula bears the expected type-checker caches of
	 * identifiers.
	 * 
	 * @param formula
	 *            the formula to test
	 * @param freeIdents
	 *            expected set of free identifiers
	 * @param boundIdents
	 *            expected set of bound identifiers
	 * @return <code>true</code> iff the formula bears the expected set of
	 *         cached identifiers
	 */
	private static boolean checkFormula(Formula<?> formula,
			Set<FreeIdentifier> freeIdents, Set<BoundIdentifier> boundIdents) {
		return areEqualFreeIdentifiers(formula, freeIdents,
				formula.getFreeIdentifiers())
				&& areEqual(boundIdents, formula.getBoundIdentifiers());
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
			return result;
		}
		for (final GivenType givenType : type.getGivenTypes()) {
			result.add(givenType.toExpression(factory));
		}
		return result;
	}

	final private FormulaFactory factory;

	final private Stack<Formula<?>> stack;

	private boolean success;

	/**
	 * Creates a new instance of this checker.
	 */
	private IdentsChecker(Formula<?> f, FormulaFactory factory) {
		this.success = true;
		this.stack = new Stack<Formula<?>>();
		this.factory = factory;
	}

	@Override
	public boolean continueBCOMP(AssociativeExpression expr) {
		return success;
	}

	@Override
	public boolean continueBINTER(AssociativeExpression expr) {
		return success;
	}

	@Override
	public boolean continueBUNION(AssociativeExpression expr) {
		return success;
	}

	@Override
	public boolean continueCPROD(BinaryExpression expr) {
		return success;
	}

	@Override
	public boolean continueCSET(QuantifiedExpression expr) {
		return success;
	}

	@Override
	public boolean continueDIV(BinaryExpression expr) {
		return success;
	}

	@Override
	public boolean continueDOMRES(BinaryExpression expr) {
		return success;
	}

	@Override
	public boolean continueDOMSUB(BinaryExpression expr) {
		return success;
	}

	@Override
	public boolean continueDPROD(BinaryExpression expr) {
		return success;
	}

	@Override
	public boolean continueEQUAL(RelationalPredicate pred) {
		return success;
	}

	@Override
	public boolean continueEXISTS(QuantifiedPredicate pred) {
		return success;
	}

	@Override
	public boolean continueEXPN(BinaryExpression expr) {
		return success;
	}

	@Override
	public boolean continueFCOMP(AssociativeExpression expr) {
		return success;
	}

	@Override
	public boolean continueFORALL(QuantifiedPredicate pred) {
		return success;
	}

	@Override
	public boolean continueFUNIMAGE(BinaryExpression expr) {
		return success;
	}

	@Override
	public boolean continueGE(RelationalPredicate pred) {
		return success;
	}

	@Override
	public boolean continueGT(RelationalPredicate pred) {
		return success;
	}

	@Override
	public boolean continueIN(RelationalPredicate pred) {
		return success;
	}

	@Override
	public boolean continueLAND(AssociativePredicate pred) {
		return success;
	}

	@Override
	public boolean continueLE(RelationalPredicate pred) {
		return success;
	}

	@Override
	public boolean continueLEQV(BinaryPredicate pred) {
		return success;
	}

	@Override
	public boolean continueLIMP(BinaryPredicate pred) {
		return success;
	}

	@Override
	public boolean continueLOR(AssociativePredicate pred) {
		return success;
	}

	@Override
	public boolean continueLT(RelationalPredicate pred) {
		return success;
	}

	@Override
	public boolean continueMAPSTO(BinaryExpression expr) {
		return success;
	}

	@Override
	public boolean continueMINUS(BinaryExpression expr) {
		return success;
	}

	@Override
	public boolean continueMOD(BinaryExpression expr) {
		return success;
	}

	@Override
	public boolean continueMUL(AssociativeExpression expr) {
		return success;
	}

	@Override
	public boolean continueNOTEQUAL(RelationalPredicate pred) {
		return success;
	}

	@Override
	public boolean continueNOTIN(RelationalPredicate pred) {
		return success;
	}

	@Override
	public boolean continueNOTSUBSET(RelationalPredicate pred) {
		return success;
	}

	@Override
	public boolean continueNOTSUBSETEQ(RelationalPredicate pred) {
		return success;
	}

	@Override
	public boolean continueOVR(AssociativeExpression expr) {
		return success;
	}

	@Override
	public boolean continuePFUN(BinaryExpression expr) {
		return success;
	}

	@Override
	public boolean continuePINJ(BinaryExpression expr) {
		return success;
	}

	@Override
	public boolean continuePLUS(AssociativeExpression expr) {
		return success;
	}

	@Override
	public boolean continuePPROD(BinaryExpression expr) {
		return success;
	}

	@Override
	public boolean continuePSUR(BinaryExpression expr) {
		return success;
	}

	@Override
	public boolean continueQINTER(QuantifiedExpression expr) {
		return success;
	}

	@Override
	public boolean continueQUNION(QuantifiedExpression expr) {
		return success;
	}

	@Override
	public boolean continueRANRES(BinaryExpression expr) {
		return success;
	}

	@Override
	public boolean continueRANSUB(BinaryExpression expr) {
		return success;
	}

	@Override
	public boolean continueREL(BinaryExpression expr) {
		return success;
	}

	@Override
	public boolean continueRELIMAGE(BinaryExpression expr) {
		return success;
	}

	@Override
	public boolean continueSETEXT(SetExtension set) {
		return success;
	}

	@Override
	public boolean continueSETMINUS(BinaryExpression expr) {
		return success;
	}

	@Override
	public boolean continueSREL(BinaryExpression expr) {
		return success;
	}

	@Override
	public boolean continueSTREL(BinaryExpression expr) {
		return success;
	}

	@Override
	public boolean continueSUBSET(RelationalPredicate pred) {
		return success;
	}

	@Override
	public boolean continueSUBSETEQ(RelationalPredicate pred) {
		return success;
	}

	@Override
	public boolean continueTBIJ(BinaryExpression expr) {
		return success;
	}

	@Override
	public boolean continueTFUN(BinaryExpression expr) {
		return success;
	}

	@Override
	public boolean continueTINJ(BinaryExpression expr) {
		return success;
	}

	@Override
	public boolean continueTREL(BinaryExpression expr) {
		return success;
	}

	@Override
	public boolean continueTSUR(BinaryExpression expr) {
		return success;
	}

	@Override
	public boolean continueUPTO(BinaryExpression expr) {
		return success;
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

	/**
	 * Checks cached identifier sets of a quantified formula (on exit).
	 * 
	 * @param formula
	 *            the formula to check
	 * @param nbBoundIdentDecls
	 *            number of identifiers bound by the given formula
	 * @return <code>true</code> if successful
	 */
	private boolean quantifiedExit(Formula<?> formula, int nbBoundIdentDecls) {
		if (!success) {
			return false;
		}
		final Set<FreeIdentifier> freeIdents = new HashSet<FreeIdentifier>();
		Set<BoundIdentifier> boundIdents = new HashSet<BoundIdentifier>();
		while (stack.peek() != formula) {
			final Formula<?> child = stack.pop();
			freeIdents.addAll(Arrays.asList(child.getFreeIdentifiers()));
			boundIdents.addAll(Arrays.asList(child.getBoundIdentifiers()));
		}
		boundIdents = renumber(boundIdents, nbBoundIdentDecls);
		return success = checkFormula(formula, freeIdents, boundIdents);
	}

	private Set<BoundIdentifier> renumber(Set<BoundIdentifier> boundIdents,
			int nbBoundIdentDecls) {
		final Set<BoundIdentifier> result = new HashSet<BoundIdentifier>();
		for (final BoundIdentifier boundIdent : boundIdents) {
			final int index = boundIdent.getBoundIndex();
			if (nbBoundIdentDecls <= index) {
				result.add(factory.makeBoundIdentifier(index
						- nbBoundIdentDecls, boundIdent.getSourceLocation(),
						boundIdent.getType()));
			}
		}
		return result;
	}

	private boolean standardEnter(Formula<?> formula) {
		if (success) {
			stack.push(formula);
		}
		return success;
	}

	/**
	 * Checks cached identifier sets of a non-atomic formula (on exit).
	 * 
	 * @param formula
	 *            the formula to check
	 * @return <code>true</code> if successful
	 */
	private boolean standardExit(Formula<?> formula) {
		if (!success) {
			return false;
		}
		final Set<FreeIdentifier> freeIdents = new HashSet<FreeIdentifier>();
		final Set<BoundIdentifier> boundIdents = new HashSet<BoundIdentifier>();
		while (stack.peek() != formula) {
			final Formula<?> child = stack.pop();
			freeIdents.addAll(Arrays.asList(child.getFreeIdentifiers()));
			boundIdents.addAll(Arrays.asList(child.getBoundIdentifiers()));
		}
		return success = checkFormula(formula, freeIdents, boundIdents);
	}

	private boolean standardVisitExpression(Expression expr) {
		if (!success) {
			return false;
		}
		stack.push(expr);
		final Set<FreeIdentifier> freeIdents = getGivenTypeIdentifiers(expr);
		final Set<BoundIdentifier> boundIdents = emptySet();
		return success = checkFormula(expr, freeIdents, boundIdents);
	}

	private boolean standardVisitPredicate(Predicate pred) {
		if (!success) {
			return false;
		}
		stack.push(pred);
		final Set<FreeIdentifier> freeIdents = emptySet();
		final Set<BoundIdentifier> boundIdents = emptySet();
		return success = checkFormula(pred, freeIdents, boundIdents);
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
		if (! success) {
			return false;
		}
		stack.push(ident);
		final Set<FreeIdentifier> freeIdents = getGivenTypeIdentifiers(ident);
		final Set<BoundIdentifier> boundIdents = singleton(ident);
		return success = checkFormula(ident, freeIdents, boundIdents);
	}

	@Override
	public boolean visitBOUND_IDENT_DECL(BoundIdentDecl decl) {
		if (! success) {
			return false;
		}
		stack.push(decl);
		final Set<FreeIdentifier> freeIdents = getGivenTypeIdentifiers(decl);
		final Set<BoundIdentifier> boundIdents = emptySet();
		return success = checkFormula(decl, freeIdents, boundIdents);
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
		if (! success) {
			return false;
		}
		stack.push(ident);
		final Set<FreeIdentifier> freeIdents = getGivenTypeIdentifiers(ident);
		freeIdents.add(ident);
		final Set<BoundIdentifier> boundIdents = emptySet();
		return success = checkFormula(ident, freeIdents, boundIdents);
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
		return success;
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
		return success;
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
		return success;
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
		return success;
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
		return success;
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
		return success;
	}

	@Override
	public boolean exitExtendedPredicate(ExtendedPredicate pred) {
		return standardExit(pred);
	}

}
