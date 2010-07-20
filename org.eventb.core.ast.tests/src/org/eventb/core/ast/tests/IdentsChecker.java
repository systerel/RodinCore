/*******************************************************************************
 * Copyright (c) 2006, 2010 ETH Zurich and others.
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
import org.eventb.core.ast.ExtendedExpression;
import org.eventb.core.ast.ExtendedPredicate;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.IVisitor;
import org.eventb.core.ast.IntegerLiteral;
import org.eventb.core.ast.LiteralPredicate;
import org.eventb.core.ast.MultiplePredicate;
import org.eventb.core.ast.QuantifiedExpression;
import org.eventb.core.ast.QuantifiedPredicate;
import org.eventb.core.ast.RelationalPredicate;
import org.eventb.core.ast.SetExtension;
import org.eventb.core.ast.SimplePredicate;
import org.eventb.core.ast.UnaryExpression;
import org.eventb.core.ast.UnaryPredicate;

/**
 * Implements the checking of the identifier caches of the formula type-checker,
 * using the AST Visitor design pattern.
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
		return areEqual(freeIdents, formula.getFreeIdentifiers())
				&& areEqual(boundIdents, formula.getBoundIdentifiers());
	}
	
	/**
	 * Checks that the given array is empty.
	 * 
	 * @param <T>
	 *            type of the elements
	 * @param array
	 *            an array
	 * @return <code>true</code> iff the given array is empty
	 */
	private static <T> boolean isEmpty(T[] array) {
		return array.length == 0;
	}
	
	/**
	 * Checks that the given array is a singleton containing the given element.
	 * 
	 * @param <T>
	 *            type of the elements
	 * @param array
	 *            an array
	 * @param value
	 *            a value
	 * @return <code>true</code> iff the given array contains exactly the
	 *         given element
	 */
	private static <T> boolean isSingleton(T[] array, T value) {
		return array.length == 1 && array[0] == value;
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
	
	public boolean continueBCOMP(AssociativeExpression expr) {
		return success;
	}
	
	public boolean continueBINTER(AssociativeExpression expr) {
		return success;
	}

	public boolean continueBUNION(AssociativeExpression expr) {
		return success;
	}

	public boolean continueCPROD(BinaryExpression expr) {
		return success;
	}

	public boolean continueCSET(QuantifiedExpression expr) {
		return success;
	}
	
	public boolean continueDIV(BinaryExpression expr) {
		return success;
	}

	public boolean continueDOMRES(BinaryExpression expr) {
		return success;
	}

	public boolean continueDOMSUB(BinaryExpression expr) {
		return success;
	}

	public boolean continueDPROD(BinaryExpression expr) {
		return success;
	}

	public boolean continueEQUAL(RelationalPredicate pred) {
		return success;
	}

	public boolean continueEXISTS(QuantifiedPredicate pred) {
		return success;
	}

	public boolean continueEXPN(BinaryExpression expr) {
		return success;
	}

	public boolean continueFCOMP(AssociativeExpression expr) {
		return success;
	}

	public boolean continueFORALL(QuantifiedPredicate pred) {
		return success;
	}

	public boolean continueFUNIMAGE(BinaryExpression expr) {
		return success;
	}

	public boolean continueGE(RelationalPredicate pred) {
		return success;
	}

	public boolean continueGT(RelationalPredicate pred) {
		return success;
	}

	public boolean continueIN(RelationalPredicate pred) {
		return success;
	}

	public boolean continueLAND(AssociativePredicate pred) {
		return success;
	}

	public boolean continueLE(RelationalPredicate pred) {
		return success;
	}

	public boolean continueLEQV(BinaryPredicate pred) {
		return success;
	}

	public boolean continueLIMP(BinaryPredicate pred) {
		return success;
	}

	public boolean continueLOR(AssociativePredicate pred) {
		return success;
	}

	public boolean continueLT(RelationalPredicate pred) {
		return success;
	}

	public boolean continueMAPSTO(BinaryExpression expr) {
		return success;
	}

	public boolean continueMINUS(BinaryExpression expr) {
		return success;
	}

	public boolean continueMOD(BinaryExpression expr) {
		return success;
	}

	public boolean continueMUL(AssociativeExpression expr) {
		return success;
	}

	public boolean continueNOTEQUAL(RelationalPredicate pred) {
		return success;
	}

	public boolean continueNOTIN(RelationalPredicate pred) {
		return success;
	}

	public boolean continueNOTSUBSET(RelationalPredicate pred) {
		return success;
	}

	public boolean continueNOTSUBSETEQ(RelationalPredicate pred) {
		return success;
	}

	public boolean continueOVR(AssociativeExpression expr) {
		return success;
	}

	public boolean continuePFUN(BinaryExpression expr) {
		return success;
	}

	public boolean continuePINJ(BinaryExpression expr) {
		return success;
	}

	public boolean continuePLUS(AssociativeExpression expr) {
		return success;
	}

	public boolean continuePPROD(BinaryExpression expr) {
		return success;
	}

	public boolean continuePSUR(BinaryExpression expr) {
		return success;
	}

	public boolean continueQINTER(QuantifiedExpression expr) {
		return success;
	}

	public boolean continueQUNION(QuantifiedExpression expr) {
		return success;
	}

	public boolean continueRANRES(BinaryExpression expr) {
		return success;
	}

	public boolean continueRANSUB(BinaryExpression expr) {
		return success;
	}

	public boolean continueREL(BinaryExpression expr) {
		return success;
	}

	public boolean continueRELIMAGE(BinaryExpression expr) {
		return success;
	}

	public boolean continueSETEXT(SetExtension set) {
		return success;
	}

	public boolean continueSETMINUS(BinaryExpression expr) {
		return success;
	}

	public boolean continueSREL(BinaryExpression expr) {
		return success;
	}

	public boolean continueSTREL(BinaryExpression expr) {
		return success;
	}

	public boolean continueSUBSET(RelationalPredicate pred) {
		return success;
	}

	public boolean continueSUBSETEQ(RelationalPredicate pred) {
		return success;
	}

	public boolean continueTBIJ(BinaryExpression expr) {
		return success;
	}

	public boolean continueTFUN(BinaryExpression expr) {
		return success;
	}

	public boolean continueTINJ(BinaryExpression expr) {
		return success;
	}

	public boolean continueTREL(BinaryExpression expr) {
		return success;
	}

	public boolean continueTSUR(BinaryExpression expr) {
		return success;
	}

	public boolean continueUPTO(BinaryExpression expr) {
		return success;
	}

	public boolean enterBCOMP(AssociativeExpression expr) {
		return standardEnter(expr);
	}

	public boolean enterBINTER(AssociativeExpression expr) {
		return standardEnter(expr);
	}

	public boolean enterBUNION(AssociativeExpression expr) {
		return standardEnter(expr);
	}

	public boolean enterCONVERSE(UnaryExpression expr) {
		return standardEnter(expr);
	}

	public boolean enterCPROD(BinaryExpression expr) {
		return standardEnter(expr);
	}

	public boolean enterCSET(QuantifiedExpression expr) {
		return standardEnter(expr);
	}

	public boolean enterDIV(BinaryExpression expr) {
		return standardEnter(expr);
	}

	public boolean enterDOMRES(BinaryExpression expr) {
		return standardEnter(expr);
	}

	public boolean enterDOMSUB(BinaryExpression expr) {
		return standardEnter(expr);
	}

	public boolean enterDPROD(BinaryExpression expr) {
		return standardEnter(expr);
	}

	public boolean enterEQUAL(RelationalPredicate pred) {
		return standardEnter(pred);
	}

	public boolean enterEXISTS(QuantifiedPredicate pred) {
		return standardEnter(pred);
	}

	public boolean enterEXPN(BinaryExpression expr) {
		return standardEnter(expr);
	}

	public boolean enterFCOMP(AssociativeExpression expr) {
		return standardEnter(expr);
	}

	public boolean enterFORALL(QuantifiedPredicate pred) {
		return standardEnter(pred);
	}

	public boolean enterFUNIMAGE(BinaryExpression expr) {
		return standardEnter(expr);
	}

	public boolean enterGE(RelationalPredicate pred) {
		return standardEnter(pred);
	}

	public boolean enterGT(RelationalPredicate pred) {
		return standardEnter(pred);
	}

	public boolean enterIN(RelationalPredicate pred) {
		return standardEnter(pred);
	}

	public boolean enterKBOOL(BoolExpression expr) {
		return standardEnter(expr);
	}

	public boolean enterKCARD(UnaryExpression expr) {
		return standardEnter(expr);
	}

	public boolean enterKDOM(UnaryExpression expr) {
		return standardEnter(expr);
	}

	public boolean enterKFINITE(SimplePredicate pred) {
		return standardEnter(pred);
	}

	public boolean enterKID(UnaryExpression expr) {
		return standardEnter(expr);
	}

	public boolean enterKINTER(UnaryExpression expr) {
		return standardEnter(expr);
	}

	public boolean enterKMAX(UnaryExpression expr) {
		return standardEnter(expr);
	}

	public boolean enterKMIN(UnaryExpression expr) {
		return standardEnter(expr);
	}

	public boolean enterKPRJ1(UnaryExpression expr) {
		return standardEnter(expr);
	}

	public boolean enterKPRJ2(UnaryExpression expr) {
		return standardEnter(expr);
	}

	public boolean enterKRAN(UnaryExpression expr) {
		return standardEnter(expr);
	}

	public boolean enterKUNION(UnaryExpression expr) {
		return standardEnter(expr);
	}

	public boolean enterLAND(AssociativePredicate pred) {
		return standardEnter(pred);
	}

	public boolean enterLE(RelationalPredicate pred) {
		return standardEnter(pred);
	}

	public boolean enterLEQV(BinaryPredicate pred) {
		return standardEnter(pred);
	}

	public boolean enterLIMP(BinaryPredicate pred) {
		return standardEnter(pred);
	}

	public boolean enterLOR(AssociativePredicate pred) {
		return standardEnter(pred);
	}

	public boolean enterLT(RelationalPredicate pred) {
		return standardEnter(pred);
	}

	public boolean enterMAPSTO(BinaryExpression expr) {
		return standardEnter(expr);
	}

	public boolean enterMINUS(BinaryExpression expr) {
		return standardEnter(expr);
	}

	public boolean enterMOD(BinaryExpression expr) {
		return standardEnter(expr);
	}

	public boolean enterMUL(AssociativeExpression expr) {
		return standardEnter(expr);
	}

	public boolean enterNOT(UnaryPredicate pred) {
		return standardEnter(pred);
	}

	public boolean enterNOTEQUAL(RelationalPredicate pred) {
		return standardEnter(pred);
	}

	public boolean enterNOTIN(RelationalPredicate pred) {
		return standardEnter(pred);
	}

	public boolean enterNOTSUBSET(RelationalPredicate pred) {
		return standardEnter(pred);
	}

	public boolean enterNOTSUBSETEQ(RelationalPredicate pred) {
		return standardEnter(pred);
	}

	public boolean enterOVR(AssociativeExpression expr) {
		return standardEnter(expr);
	}

	public boolean enterPFUN(BinaryExpression expr) {
		return standardEnter(expr);
	}

	public boolean enterPINJ(BinaryExpression expr) {
		return standardEnter(expr);
	}

	public boolean enterPLUS(AssociativeExpression expr) {
		return standardEnter(expr);
	}

	public boolean enterPOW(UnaryExpression expr) {
		return standardEnter(expr);
	}

	public boolean enterPOW1(UnaryExpression expr) {
		return standardEnter(expr);
	}

	public boolean enterPPROD(BinaryExpression expr) {
		return standardEnter(expr);
	}

	public boolean enterPSUR(BinaryExpression expr) {
		return standardEnter(expr);
	}

	public boolean enterQINTER(QuantifiedExpression expr) {
		return standardEnter(expr);
	}

	public boolean enterQUNION(QuantifiedExpression expr) {
		return standardEnter(expr);
	}

	public boolean enterRANRES(BinaryExpression expr) {
		return standardEnter(expr);
	}

	public boolean enterRANSUB(BinaryExpression expr) {
		return standardEnter(expr);
	}

	public boolean enterREL(BinaryExpression expr) {
		return standardEnter(expr);
	}

	public boolean enterRELIMAGE(BinaryExpression expr) {
		return standardEnter(expr);
	}

	public boolean enterSETEXT(SetExtension set) {
		return standardEnter(set);
	}

	public boolean enterSETMINUS(BinaryExpression expr) {
		return standardEnter(expr);
	}

	public boolean enterSREL(BinaryExpression expr) {
		return standardEnter(expr);
	}

	public boolean enterSTREL(BinaryExpression expr) {
		return standardEnter(expr);
	}

	public boolean enterSUBSET(RelationalPredicate pred) {
		return standardEnter(pred);
	}

	public boolean enterSUBSETEQ(RelationalPredicate pred) {
		return standardEnter(pred);
	}

	public boolean enterTBIJ(BinaryExpression expr) {
		return standardEnter(expr);
	}

	public boolean enterTFUN(BinaryExpression expr) {
		return standardEnter(expr);
	}

	public boolean enterTINJ(BinaryExpression expr) {
		return standardEnter(expr);
	}

	public boolean enterTREL(BinaryExpression expr) {
		return standardEnter(expr);
	}

	public boolean enterTSUR(BinaryExpression expr) {
		return standardEnter(expr);
	}

	public boolean enterUNMINUS(UnaryExpression expr) {
		return standardEnter(expr);
	}

	public boolean enterUPTO(BinaryExpression expr) {
		return standardEnter(expr);
	}

	public boolean exitBCOMP(AssociativeExpression expr) {
		return standardExit(expr);
	}

	public boolean exitBINTER(AssociativeExpression expr) {
		return standardExit(expr);
	}

	public boolean exitBUNION(AssociativeExpression expr) {
		return standardExit(expr);
	}

	public boolean exitCONVERSE(UnaryExpression expr) {
		return standardExit(expr);
	}

	public boolean exitCPROD(BinaryExpression expr) {
		return standardExit(expr);
	}

	public boolean exitCSET(QuantifiedExpression expr) {
		return quantifiedExit(expr, expr.getBoundIdentDecls().length);
	}

	public boolean exitDIV(BinaryExpression expr) {
		return standardExit(expr);
	}

	public boolean exitDOMRES(BinaryExpression expr) {
		return standardExit(expr);
	}

	public boolean exitDOMSUB(BinaryExpression expr) {
		return standardExit(expr);
	}

	public boolean exitDPROD(BinaryExpression expr) {
		return standardExit(expr);
	}

	public boolean exitEQUAL(RelationalPredicate pred) {
		return standardExit(pred);
	}

	public boolean exitEXISTS(QuantifiedPredicate pred) {
		return quantifiedExit(pred, pred.getBoundIdentDecls().length);
	}

	public boolean exitEXPN(BinaryExpression expr) {
		return standardExit(expr);
	}

	public boolean exitFCOMP(AssociativeExpression expr) {
		return standardExit(expr);
	}

	public boolean exitFORALL(QuantifiedPredicate pred) {
		return quantifiedExit(pred, pred.getBoundIdentDecls().length);
	}

	public boolean exitFUNIMAGE(BinaryExpression expr) {
		return standardExit(expr);
	}

	public boolean exitGE(RelationalPredicate pred) {
		return standardExit(pred);
	}

	public boolean exitGT(RelationalPredicate pred) {
		return standardExit(pred);
	}

	public boolean exitIN(RelationalPredicate pred) {
		return standardExit(pred);
	}

	public boolean exitKBOOL(BoolExpression expr) {
		return standardExit(expr);
	}

	public boolean exitKCARD(UnaryExpression expr) {
		return standardExit(expr);
	}

	public boolean exitKDOM(UnaryExpression expr) {
		return standardExit(expr);
	}

	public boolean exitKFINITE(SimplePredicate pred) {
		return standardExit(pred);
	}

	public boolean exitKID(UnaryExpression expr) {
		return standardExit(expr);
	}

	public boolean exitKINTER(UnaryExpression expr) {
		return standardExit(expr);
	}

	public boolean exitKMAX(UnaryExpression expr) {
		return standardExit(expr);
	}

	public boolean exitKMIN(UnaryExpression expr) {
		return standardExit(expr);
	}

	public boolean exitKPRJ1(UnaryExpression expr) {
		return standardExit(expr);
	}

	public boolean exitKPRJ2(UnaryExpression expr) {
		return standardExit(expr);
	}

	public boolean exitKRAN(UnaryExpression expr) {
		return standardExit(expr);
	}

	public boolean exitKUNION(UnaryExpression expr) {
		return standardExit(expr);
	}

	public boolean exitLAND(AssociativePredicate pred) {
		return standardExit(pred);
	}

	public boolean exitLE(RelationalPredicate pred) {
		return standardExit(pred);
	}

	public boolean exitLEQV(BinaryPredicate pred) {
		return standardExit(pred);
	}

	public boolean exitLIMP(BinaryPredicate pred) {
		return standardExit(pred);
	}

	public boolean exitLOR(AssociativePredicate pred) {
		return standardExit(pred);
	}

	public boolean exitLT(RelationalPredicate pred) {
		return standardExit(pred);
	}

	public boolean exitMAPSTO(BinaryExpression expr) {
		return standardExit(expr);
	}

	public boolean exitMINUS(BinaryExpression expr) {
		return standardExit(expr);
	}

	public boolean exitMOD(BinaryExpression expr) {
		return standardExit(expr);
	}

	public boolean exitMUL(AssociativeExpression expr) {
		return standardExit(expr);
	}

	public boolean exitNOT(UnaryPredicate pred) {
		return standardExit(pred);
	}

	public boolean exitNOTEQUAL(RelationalPredicate pred) {
		return standardExit(pred);
	}

	public boolean exitNOTIN(RelationalPredicate pred) {
		return standardExit(pred);
	}

	public boolean exitNOTSUBSET(RelationalPredicate pred) {
		return standardExit(pred);
	}

	public boolean exitNOTSUBSETEQ(RelationalPredicate pred) {
		return standardExit(pred);
	}

	public boolean exitOVR(AssociativeExpression expr) {
		return standardExit(expr);
	}

	public boolean exitPFUN(BinaryExpression expr) {
		return standardExit(expr);
	}

	public boolean exitPINJ(BinaryExpression expr) {
		return standardExit(expr);
	}

	public boolean exitPLUS(AssociativeExpression expr) {
		return standardExit(expr);
	}

	public boolean exitPOW(UnaryExpression expr) {
		return standardExit(expr);
	}

	public boolean exitPOW1(UnaryExpression expr) {
		return standardExit(expr);
	}

	public boolean exitPPROD(BinaryExpression expr) {
		return standardExit(expr);
	}

	public boolean exitPSUR(BinaryExpression expr) {
		return standardExit(expr);
	}

	public boolean exitQINTER(QuantifiedExpression expr) {
		return quantifiedExit(expr, expr.getBoundIdentDecls().length);
	}

	public boolean exitQUNION(QuantifiedExpression expr) {
		return quantifiedExit(expr, expr.getBoundIdentDecls().length);
	}

	public boolean exitRANRES(BinaryExpression expr) {
		return standardExit(expr);
	}

	public boolean exitRANSUB(BinaryExpression expr) {
		return standardExit(expr);
	}

	public boolean exitREL(BinaryExpression expr) {
		return standardExit(expr);
	}

	public boolean exitRELIMAGE(BinaryExpression expr) {
		return standardExit(expr);
	}

	public boolean exitSETEXT(SetExtension set) {
		return standardExit(set);
	}

	public boolean exitSETMINUS(BinaryExpression expr) {
		return standardExit(expr);
	}

	public boolean exitSREL(BinaryExpression expr) {
		return standardExit(expr);
	}

	public boolean exitSTREL(BinaryExpression expr) {
		return standardExit(expr);
	}

	public boolean exitSUBSET(RelationalPredicate pred) {
		return standardExit(pred);
	}

	public boolean exitSUBSETEQ(RelationalPredicate pred) {
		return standardExit(pred);
	}

	public boolean exitTBIJ(BinaryExpression expr) {
		return standardExit(expr);
	}

	public boolean exitTFUN(BinaryExpression expr) {
		return standardExit(expr);
	}

	public boolean exitTINJ(BinaryExpression expr) {
		return standardExit(expr);
	}

	public boolean exitTREL(BinaryExpression expr) {
		return standardExit(expr);
	}

	public boolean exitTSUR(BinaryExpression expr) {
		return standardExit(expr);
	}

	public boolean exitUNMINUS(UnaryExpression expr) {
		return standardExit(expr);
	}

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
		if (! success) {
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

	private Set<BoundIdentifier> renumber(Set<BoundIdentifier> boundIdents, int nbBoundIdentDecls) {
		final Set<BoundIdentifier> result = new HashSet<BoundIdentifier>();
		for (final BoundIdentifier boundIdent: boundIdents) {
			final int index = boundIdent.getBoundIndex();
			if (nbBoundIdentDecls <= index) {
				result.add(factory.makeBoundIdentifier(
						index - nbBoundIdentDecls, 
						boundIdent.getSourceLocation(), 
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
	 * @param formula the formula to check
	 * @return <code>true</code> if successful
	 */
	private boolean standardExit(Formula<?> formula) {
		if (! success) {
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

	private boolean standardVisit(Formula<?> formula) {
		if (! success) {
			return false;
		}
		stack.push(formula);
		return isEmpty(formula.getFreeIdentifiers())
				&& isEmpty(formula.getBoundIdentifiers());
	}

	public boolean visitBFALSE(LiteralPredicate pred) {
		return standardVisit(pred);
	}

	public boolean visitBOOL(AtomicExpression expr) {
		return standardVisit(expr);
	}

	public boolean visitBOUND_IDENT(BoundIdentifier ident) {
		if (! success) {
			return false;
		}
		stack.push(ident);
		return success = isEmpty(ident.getFreeIdentifiers())
				&& isSingleton(ident.getBoundIdentifiers(), ident);
	}

	public boolean visitBOUND_IDENT_DECL(BoundIdentDecl ident) {
		return standardVisit(ident);
	}

	public boolean visitBTRUE(LiteralPredicate pred) {
		return standardVisit(pred);
	}

	public boolean visitEMPTYSET(AtomicExpression expr) {
		return standardVisit(expr);
	}

	public boolean visitFALSE(AtomicExpression expr) {
		return standardVisit(expr);
	}

	public boolean visitFREE_IDENT(FreeIdentifier ident) {
		if (! success) {
			return false;
		}
		stack.push(ident);
		return success = isSingleton(ident.getFreeIdentifiers(), ident)
				&& isEmpty(ident.getBoundIdentifiers());
	}

	public boolean visitINTEGER(AtomicExpression expr) {
		return standardVisit(expr);
	}

	public boolean visitINTLIT(IntegerLiteral lit) {
		return standardVisit(lit);
	}

	public boolean visitKPRED(AtomicExpression expr) {
		return standardVisit(expr);
	}

	public boolean visitKSUCC(AtomicExpression expr) {
		return standardVisit(expr);
	}

	public boolean visitNATURAL(AtomicExpression expr) {
		return standardVisit(expr);
	}

	public boolean visitNATURAL1(AtomicExpression expr) {
		return standardVisit(expr);
	}

	public boolean visitTRUE(AtomicExpression expr) {
		return standardVisit(expr);
	}

	public boolean enterBECOMES_EQUAL_TO(BecomesEqualTo assign) {
		return standardEnter(assign);
	}

	public boolean continueBECOMES_EQUAL_TO(BecomesEqualTo assign) {
		return success;
	}

	public boolean exitBECOMES_EQUAL_TO(BecomesEqualTo assign) {
		return standardExit(assign);
	}

	public boolean enterBECOMES_MEMBER_OF(BecomesMemberOf assign) {
		return standardEnter(assign);
	}

	public boolean continueBECOMES_MEMBER_OF(BecomesMemberOf assign) {
		return success;
	}

	public boolean exitBECOMES_MEMBER_OF(BecomesMemberOf assign) {
		return standardExit(assign);
	}

	public boolean enterBECOMES_SUCH_THAT(BecomesSuchThat assign) {
		return standardEnter(assign);
	}

	public boolean continueBECOMES_SUCH_THAT(BecomesSuchThat assign) {
		return success;
	}

	public boolean exitBECOMES_SUCH_THAT(BecomesSuchThat assign) {
		return quantifiedExit(assign, assign.getPrimedIdents().length);
	}

	public boolean enterKPARTITION(MultiplePredicate pred) {
		return standardEnter(pred);
	}

	public boolean continueKPARTITION(MultiplePredicate pred) {
		return success;
	}

	public boolean exitKPARTITION(MultiplePredicate pred) {
		return standardExit(pred);
	}

	public boolean visitKID_GEN(AtomicExpression expr) {
		return standardVisit(expr);
	}

	public boolean visitKPRJ1_GEN(AtomicExpression expr) {
		return standardVisit(expr);
	}

	public boolean visitKPRJ2_GEN(AtomicExpression expr) {
		return standardVisit(expr);
	}

	public boolean enterExtendedExpression(ExtendedExpression expr) {
		return standardEnter(expr);
	}

	public boolean continueExtendedExpression(
			ExtendedExpression expr) {
		return success;
	}

	public boolean exitExtendedExpression(ExtendedExpression expr) {
		return standardExit(expr);
	}

	public boolean enterExtendedPredicate(ExtendedPredicate pred) {
		return standardEnter(pred);
	}

	public boolean continueExtendedPredicate(ExtendedPredicate pred) {
		return success;
	}

	public boolean exitExtendedPredicate(ExtendedPredicate pred) {
		return standardExit(pred);
	}
	
}
