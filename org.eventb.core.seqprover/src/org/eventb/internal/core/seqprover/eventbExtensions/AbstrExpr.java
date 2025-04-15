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
 *     INP Toulouse - rewrote input, stopped serializing expression
 *******************************************************************************/
package org.eventb.internal.core.seqprover.eventbExtensions;

import static java.util.stream.Collectors.joining;
import static org.eventb.core.ast.Formula.EQUAL;
import static org.eventb.core.ast.Formula.FREE_IDENT;
import static org.eventb.core.seqprover.ProverFactory.reasonerFailure;
import static org.eventb.core.seqprover.eventbExtensions.DLib.parseExpression;
import static org.eventb.core.seqprover.eventbExtensions.DLib.parsePredicate;
import static org.eventb.internal.core.seqprover.eventbExtensions.utils.FreshInstantiation.genFreshFreeIdent;

import java.util.LinkedHashSet;
import java.util.Set;

import org.eventb.core.ast.Expression;
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
import org.eventb.core.seqprover.ITranslatableReasonerInput;
import org.eventb.core.seqprover.ProverFactory;
import org.eventb.core.seqprover.SequentProver;
import org.eventb.core.seqprover.SerializeException;
import org.eventb.core.seqprover.eventbExtensions.DLib;
import org.eventb.core.seqprover.eventbExtensions.Lib;
import org.eventb.core.seqprover.proofBuilder.ReplayHints;

/**
 * This reasoner abstracts a given expression with a fresh free identifier.
 * 
 * It does this by introducing a new free variable and an equality hypothesis that can be
 * used to later rewrite all occurrences of the expression by the free variable.
 * 
 * @author Farhad Mehta
 *
 */
public class AbstrExpr implements IReasoner {

	/**
	 * Input for {@link AbstrExpr} that can be an expression or a pattern.
	 *
	 * In this context, a pattern is a predicate that is an equality. Checking the
	 * left- and right-hand-side parts of the equality is not done in this class and
	 * left to the reasoner.
	 *
	 * @author Guillaume Verdier
	 */
	public static class Input implements IReasonerInput, ITranslatableReasonerInput {

		// Expression to abstract (or null if errorMessage is set)
		// This is type checked.
		private Expression expression;

		// Pattern (or null if absent or if errorMessage is set)
		// This is not type checked (as it is just a pattern to create fresh identifiers).
		private Expression pattern;

		private String errorMessage;

		public Input(String input, ITypeEnvironment typeEnv) {
			var ff = typeEnv.getFormulaFactory();
			var predicate = parsePredicate(ff, input);
			if (predicate == null) {
				expression = parseExpression(ff, input);
				if (expression == null) {
					errorMessage = "Failed parsing input " + input;
				}
			} else if (predicate.getTag() == EQUAL) {
				var equal = (RelationalPredicate) predicate;
				pattern = equal.getLeft();
				expression = equal.getRight();
			} else {
				errorMessage = "Expect an expression or a predicate in the form ident=expr";
			}
			if (expression != null) {
				var tcResult = expression.typeCheck(typeEnv);
				if (tcResult.hasProblem()) {
					errorMessage = "Failed type checking input: "
							+ tcResult.getProblems().stream().map(Object::toString).collect(joining(", "));
					expression = null;
					pattern = null;
				}
			}
		}

		private Input(Expression expression, Expression pattern) {
			this.expression = expression;
			this.pattern = pattern;
		}

		public Input(IReasonerInputReader reader) throws SerializeException {
			IAntecedent[] antecedents = reader.getAntecedents();
			// The first antecedent is the WD; we are interested in the second one
			if (antecedents.length != 2) {
				throw deserializationError("Serialized input of ae should have two antecedents");
			}
			Set<FreeIdentifier> idents = Set.of(antecedents[1].getAddedFreeIdents());
			if (idents.size() == 0) {
				throw deserializationError("Serialized input of ae should have added at least one identifier");
			}
			// Multiple hypotheses may have been added: WD conditions in addition to the
			// definition of all the added identifiers
			for (var hyp : antecedents[1].getAddedHyps()) {
				if (hyp.getTag() == EQUAL) {
					var rel = (RelationalPredicate) hyp;
					if (idents.equals(Set.of(rel.getLeft().getFreeIdentifiers()))) {
						pattern = rel.getLeft();
						expression = rel.getRight();
						break;
					}
				}
			}
			if (expression == null) {
				throw deserializationError("Serialized input of ae missing hypothesis with definition of added identifier");
			}
		}

		private SerializeException deserializationError(String error) {
			return new SerializeException(new IllegalStateException(error));
		}

		public Expression getExpression() {
			return expression;
		}

		public Expression getPattern() {
			return pattern;
		}

		@Override
		public boolean hasError() {
			return errorMessage != null;
		}

		@Override
		public String getError() {
			return errorMessage;
		}

		@Override
		public void applyHints(ReplayHints renaming) {
			expression = renaming.applyHints(expression);
			pattern = renaming.applyHints(pattern);
		}

		@Override
		public IReasonerInput translate(FormulaFactory factory) {
			if (expression == null) {
				return this;
			}
			return new Input(expression.translate(factory), pattern == null ? null : pattern.translate(factory));
		}

		@Override
		public ITypeEnvironment getTypeEnvironment(FormulaFactory factory) {
			var typeEnv = factory.makeTypeEnvironment();
			if (expression != null) {
				typeEnv.addAll(expression.getFreeIdentifiers());
			}
			return typeEnv;
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

		// Extract input name and expression from formula
		Expression expr = input.getExpression();
		Expression pattern = input.getPattern();
		String nameBase;
		if (pattern == null) {
			nameBase = "ae"; // Default name if none provided by the user
		} else {
			if (pattern.getTag() != FREE_IDENT) {
				return reasonerFailure(this, reasonerInput,
						"Expect an expression or a predicate in the form ident=expr");
			}
			nameBase = ((FreeIdentifier) pattern).getName();
		}

		// We can now assume that lemma has been properly parsed and typed.
		
		// Generate the well definedness condition for the lemma
		final FormulaFactory ff = seq.getFormulaFactory();
		final Predicate exprWD = DLib.WD(expr);
		final Set<Predicate> exprWDs = Lib.breakPossibleConjunct(exprWD);
		DLib.removeTrue(ff, exprWDs);
		
		// Generate a fresh free identifier
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
	public void serializeInput(IReasonerInput input, IReasonerInputWriter writer) throws SerializeException {
		// Nothing to do
	}

	@Override
	public IReasonerInput deserializeInput(IReasonerInputReader reader) throws SerializeException {
		return new Input(reader);
	}
}
