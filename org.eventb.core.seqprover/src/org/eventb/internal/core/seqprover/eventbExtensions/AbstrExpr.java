/*******************************************************************************
 * Copyright (c) 2007, 2025 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - added broken input repair mechanism
 *     UPEC - added optional input name for fresh ident
 *******************************************************************************/
package org.eventb.internal.core.seqprover.eventbExtensions;

import static org.eventb.core.ast.Formula.EQUAL;
import static org.eventb.core.ast.Formula.FREE_IDENT;
import static org.eventb.core.seqprover.eventbExtensions.DLib.parsePredicate;
import static org.eventb.internal.core.seqprover.eventbExtensions.utils.FreshInstantiation.genFreshFreeIdent;

import java.util.LinkedHashSet;
import java.util.Set;

import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.RelationalPredicate;
import org.eventb.core.seqprover.IProofMonitor;
import org.eventb.core.seqprover.IProofRule.IAntecedent;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.IReasoner;
import org.eventb.core.seqprover.IReasonerInput;
import org.eventb.core.seqprover.IReasonerInputReader;
import org.eventb.core.seqprover.IReasonerInputWriter;
import org.eventb.core.seqprover.IReasonerOutput;
import org.eventb.core.seqprover.IRepairableInputReasoner;
import org.eventb.core.seqprover.ProverFactory;
import org.eventb.core.seqprover.SequentProver;
import org.eventb.core.seqprover.SerializeException;
import org.eventb.core.seqprover.eventbExtensions.DLib;
import org.eventb.core.seqprover.eventbExtensions.Lib;
import org.eventb.core.seqprover.reasonerInputs.SingleExprInput;

/**
 * This reasoner abstracts a given expression with a fresh free identifier.
 * 
 * It does this by introducing a new free variable and an equality hypothesis that can be
 * used to later rewrite all occurrences of the expression by the free variable.
 * 
 * @author Farhad Mehta
 *
 */
public class AbstrExpr implements IReasoner, IRepairableInputReasoner {

	/**
	 * Extension of {@link SingleExprInput} that allows to also specify an optional
	 * name for the expression.
	 *
	 * In practice, it means that the input can be not only an expression, but also
	 * a predicate, as long as the predicate matches the pattern ident=expr. In that
	 * case, the identifier is the optional name.
	 *
	 * @author Guillaume Verdier
	 */
	public static class Input extends SingleExprInput {

		private String name;

		public Input(String exprString, ITypeEnvironment typeEnv) {
			super(exprString, typeEnv);
		}

		@Override
		protected Expression parseExpression(String exprString, ITypeEnvironment typeEnv) {
			// Try to match input as a predicate ident=expr
			// or fall back to super class if input is not a predicate
			Predicate pred = parsePredicate(typeEnv.getFormulaFactory(), exprString);
			if (pred == null) {
				return super.parseExpression(exprString, typeEnv);
			}
			if (pred.getTag() != EQUAL) {
				setError("Expect an expression or a predicate in the form ident=expr");
				return null;
			}
			var equal = (RelationalPredicate) pred;
			if (equal.getLeft().getTag() != FREE_IDENT) {
				setError("Expect an expression or a predicate in the form ident=expr");
				return null;
			}
			name = ((FreeIdentifier) equal.getLeft()).getName();
			return equal.getRight();
		}

		public Input(Expression expression) {
			super(expression);
		}

		public Input(Expression expression, String name) {
			super(expression);
			this.name = name;
		}

		public Input(IReasonerInputReader reader) throws SerializeException {
			super(reader);
			// Try to find the name used; if it's not possible to find it, just silently
			// fail and let the reasoner deal with a null name (since the name is optional)
			IAntecedent[] antecedents = reader.getAntecedents();
			// The first antecedent is the WD; the name is introduced in the second
			// antecedent (index 1)
			if (antecedents.length > 1) {
				FreeIdentifier[] idents = antecedents[1].getAddedFreeIdents();
				if (idents.length == 1) {
					name = idents[0].getName();
				}
			}
		}

		@Override
		public IReasonerInput translate(FormulaFactory factory) {
			var result = (SingleExprInput) super.translate(factory);
			return new Input(result.getExpression(), name);
		}

		public String getName() {
			return name;
		}

	}

	public static final String REASONER_ID = SequentProver.PLUGIN_ID + ".ae";
	
	@Override
	public String getReasonerID() {
		return REASONER_ID;
	}
	
	@Override
	public IReasonerOutput apply(IProverSequent seq,IReasonerInput reasonerInput, IProofMonitor pm){
		
		// Organize Input
		var input = (Input) reasonerInput;
		
		if (input.hasError())
			return ProverFactory.reasonerFailure(this,reasonerInput,input.getError());

		Expression expr = input.getExpression();
				
		// We can now assume that lemma has been properly parsed and typed.
		
		// Generate the well definedness condition for the lemma
		final FormulaFactory ff = seq.getFormulaFactory();
		final Predicate exprWD = DLib.WD(expr);
		final Set<Predicate> exprWDs = Lib.breakPossibleConjunct(exprWD);
		DLib.removeTrue(ff, exprWDs);
		
		// Generate a fresh free identifier
		String nameBase = input.getName();
		if (nameBase == null) {
			nameBase = "ae"; // Default name if none provided by the user
		}
		final FreeIdentifier freeIdent = genFreshFreeIdent(seq.typeEnvironment(), nameBase, expr.getType());
		
		// Generate the equality predicate
		final Predicate aeEq = DLib.makeEq(freeIdent, expr);
		
		// Generate the antecedents
		final IAntecedent[] antecedents = new IAntecedent[2];
		
		// Well definedness condition
		antecedents[0] = ProverFactory.makeAntecedent(exprWD);
		
		// 
		final Set<Predicate> addedHyps = new LinkedHashSet<Predicate>();
		addedHyps.addAll(exprWDs);
		// aeEq is always the last addedHyp
		addedHyps.add(aeEq);
		antecedents[1] = ProverFactory.makeAntecedent(
				null, addedHyps,
				new FreeIdentifier[] {freeIdent}, null);
		
		// Generate the proof rule
		return ProverFactory.makeProofRule(this, input, null,
				"ae (" + expr.toString() + ")", antecedents);
	}
	
	@Override
	public IReasonerInput repair(IReasonerInputReader reader) {
		// might be caused by bug 3370087 => infer input
		final IAntecedent[] antecedents = reader.getAntecedents();
		if (antecedents.length != 2) return null;
		
		final FreeIdentifier[] addedFreeIdents = antecedents[1].getAddedFreeIdents();
		if (addedFreeIdents.length != 1) return null;
		
		final FreeIdentifier ident = addedFreeIdents[0];
		final Set<Predicate> addedHyps = antecedents[1].getAddedHyps();
		final Expression expr = findAeExpr(ident, addedHyps);
		if (expr == null) return null;
		
		return new Input(expr, ident.getName());
	}

	private static Expression findAeExpr(FreeIdentifier aeIdent, Set<Predicate> hyps) {
		for (Predicate hyp : hyps) {
			if (hyp.getTag() != Formula.EQUAL) continue;
			final RelationalPredicate eq = (RelationalPredicate) hyp;
			if (eq.getLeft().equals(aeIdent)) {
				return eq.getRight();
			}
		}
		return null;
	}

	@Override
	public void serializeInput(IReasonerInput input, IReasonerInputWriter writer) throws SerializeException {
		((Input) input).serialize(writer);
	}

	@Override
	public IReasonerInput deserializeInput(IReasonerInputReader reader) throws SerializeException {
		return new Input(reader);
	}
}
