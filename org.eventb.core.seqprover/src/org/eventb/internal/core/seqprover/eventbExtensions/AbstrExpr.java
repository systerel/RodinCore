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
 *     INP Toulouse - added optional pattern with mapsto and constructor
 *******************************************************************************/
package org.eventb.internal.core.seqprover.eventbExtensions;

import static java.util.stream.Collectors.joining;
import static org.eventb.core.ast.Formula.EQUAL;
import static org.eventb.core.ast.Formula.EXISTS;
import static org.eventb.core.ast.Formula.FREE_IDENT;
import static org.eventb.core.ast.Formula.MAPSTO;
import static org.eventb.core.seqprover.ProverFactory.reasonerFailure;
import static org.eventb.core.seqprover.eventbExtensions.DLib.parseExpression;
import static org.eventb.core.seqprover.eventbExtensions.DLib.parsePredicate;
import static org.eventb.core.seqprover.eventbExtensions.Lib.typeCheckClosed;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.eventb.core.ast.BinaryExpression;
import org.eventb.core.ast.BoundIdentDecl;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.ExtendedExpression;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.RelationalPredicate;
import org.eventb.core.ast.datatype.IConstructorExtension;
import org.eventb.core.ast.datatype.IDatatype;
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
 * This reasoner abstracts a given expression with fresh free identifiers.
 * 
 * It does this by introducing new free variables based on a pattern (or, if no
 * pattern is provided, a single identifier) and an equality hypothesis that can
 * be used to later rewrite all occurrences of the expression by the pattern.
 *
 * The pattern can be:
 * <ul>
 * <li>a single identifier</li>
 * <li>a combination of identifiers and mapsto (e.g., (a ↦ b) ↦ c)</li>
 * <li>the constructor of a datatype which has only one constructor</li>
 * </ul>
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
				errorMessage = "Expect an expression or a predicate in the form pattern=expr";
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

		final FormulaFactory ff = seq.getFormulaFactory();

		// Extract input name and expression from formula
		Expression expr = input.getExpression();
		Expression pattern = input.getPattern();
		if (pattern == null) {
			pattern = ff.makeFreeIdentifier("ae", null); // Default name if none provided by the user
		} else {
			String error = checkPattern(pattern);
			if (error != null) {
				return reasonerFailure(this, reasonerInput, error);
			}
		}

		// Generate fresh free identifiers
		List<BoundIdentDecl> bound = new ArrayList<>();
		var boundPattern = pattern.bindAllFreeIdents(bound);
		var exists = ff.makeQuantifiedPredicate(EXISTS, bound,
				ff.makeRelationalPredicate(EQUAL, boundPattern, expr, null), null);
		if (!typeCheckClosed(exists, seq.typeEnvironment())) {
			return reasonerFailure(this, reasonerInput, "Type check failed for pattern " + pattern + " and expression " + expr);
		}
		var freeIdents = seq.typeEnvironment().makeBuilder().makeFreshIdentifiers(exists.getBoundIdentDecls());
				
		// We can now assume that lemma has been properly parsed and typed.
		
		// Generate the well definedness condition for the lemma
		final Predicate exprWD = DLib.WD(expr);
		final Set<Predicate> exprWDs = Lib.breakPossibleConjunct(exprWD);
		DLib.removeTrue(ff, exprWDs);
		
		// Generate the equality predicate
		final Predicate aeEq = exists.instantiate(freeIdents, ff);
		
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
				freeIdents, null);
		
		// Generate the proof rule
		return ProverFactory.makeProofRule(this, input, null,
				"ae (" + expr.toString() + ")", antecedents);
	}

	// Checks if an expression is a valid pattern.
	// Returns an error message or null if the expression is valid
	private String checkPattern(Expression e) {
		switch (e.getTag()) {
		case FREE_IDENT:
			return null;
		case MAPSTO:
			return checkProductPattern(e, new HashSet<>());
		default:
			if (e instanceof ExtendedExpression extExpr) {
				return checkConstructorPattern(extExpr);
			}
			return "Expect an expression or a predicate in the form pattern=expr";
		}
	}

	// Checks if an expression is a combination of mapsto and unique identifiers
	// Returns an error message or null if the expression is valid
	private String checkProductPattern(Expression e, Set<FreeIdentifier> seenIdents) {
		if (e.getTag() == MAPSTO) {
			var bin = (BinaryExpression) e;
			String error = checkProductPattern(bin.getLeft(), seenIdents);
			if (error == null) {
				error = checkProductPattern(bin.getRight(), seenIdents);
			}
			return error;
		}
		if (e.getTag() == FREE_IDENT) {
			if (seenIdents.contains(e)) {
				return "Identifier " + e + " appears twice in pattern";
			}
			seenIdents.add((FreeIdentifier) e);
			return null;
		}
		return "Patterns with mapsto must only contain free identifiers";
	}

	// Checks if an expression is a valid constructor pattern
	// Returns an error message or null if the expression is valid
	private String checkConstructorPattern(ExtendedExpression expr) {
		var ext = expr.getExtension();
		if (!(ext instanceof IConstructorExtension)) {
			return "Expect an expression or a predicate in the form pattern=expr";
		}
		IDatatype dt = ((IConstructorExtension) ext).getOrigin();
		if (dt.getConstructors().length != 1) {
			return "Pattern constructor(...)=expr is only valid for datatypes with a single constructor";
		}
		Set<FreeIdentifier> seenIdents = new HashSet<>();
		for (var child : expr.getChildExpressions()) {
			if (child.getTag() != FREE_IDENT) {
				return "In pattern, constructor parameters should be unique identifiers";
			}
			if (seenIdents.contains(child)) {
				return "Identifier " + child + " appears twice in pattern";
			}
			seenIdents.add((FreeIdentifier) child);
		}
		return null;
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
