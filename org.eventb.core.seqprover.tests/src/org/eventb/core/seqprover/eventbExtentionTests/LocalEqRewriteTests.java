/*******************************************************************************
 * Copyright (c) 2011 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.core.seqprover.eventbExtentionTests;

import static org.eventb.core.ast.FormulaFactory.makePosition;
import static org.eventb.core.seqprover.tests.TestLib.genPred;
import static org.eventb.core.seqprover.tests.TestLib.genTypeEnv;

import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.seqprover.reasonerExtentionTests.AbstractReasonerTests;
import org.eventb.core.seqprover.reasonerInputs.EmptyInput;
import org.eventb.core.seqprover.tests.TestLib;
import org.eventb.internal.core.seqprover.eventbExtensions.rewriters.LocalEqRewrite;

/**
 * Unit tests for the reasoner LocalEqRewrite.
 * 
 * @author Emmanuel Billaud
 */
public class LocalEqRewriteTests extends AbstractReasonerTests {

	@Override
	public String getReasonerID() {
		return "org.eventb.core.seqprover.locEq";
	}

	@Override
	public SuccessfullReasonerApplication[] getSuccessfulReasonerApplications() {
		final ITypeEnvironment typeEnv = genTypeEnv("A=ℙ(ℤ), B=ℙ(ℤ), C=ℙ(ℤ), D=ℙ(ℤ), x=ℙ(ℤ)");
		return new SuccessfullReasonerApplication[] {
				// Apply in the hypothesis 1/5
				new SuccessfullReasonerApplication(
						TestLib.genSeq(" A∈ℙ(ℤ) ;; x=A∪B ;; x∩C⊆D |- ⊤ "),
						new LocalEqRewrite.Input(genPred(typeEnv, "x∩C⊆D"),
								makePosition("0.0"), genPred(typeEnv, "x=A∪B")),
						"{A=ℙ(ℤ), C=ℙ(ℤ)}[x∩C⊆D][][A∈ℙ(ℤ) ;; x=A∪B ;; (A∪B)∩C⊆D] |- ⊤"),
				// Apply in the hypothesis 2/5
				new SuccessfullReasonerApplication(
						TestLib.genSeq(" A∈ℙ(ℤ) ;; A∪B=x ;; x∩C⊆D |- ⊤ "),
						new LocalEqRewrite.Input(genPred(typeEnv, "x∩C⊆D"),
								makePosition("0.0"), genPred(typeEnv, "A∪B=x")),
						"{A=ℙ(ℤ), C=ℙ(ℤ)}[x∩C⊆D][][A∈ℙ(ℤ) ;; A∪B=x ;; (A∪B)∩C⊆D] |- ⊤"),
				// Apply in the hypothesis 3/5
				new SuccessfullReasonerApplication(
						TestLib.genSeq(" A∈ℙ(ℤ) ;; D∈ℙ(ℤ) ;; x=A∪B ;; y=D∪x ;; y∩C⊆D |- ⊤ "),
						new LocalEqRewrite.Input(genPred(typeEnv, "y=D∪x"),
								makePosition("1.1"), genPred(typeEnv, "x=A∪B")),
						"{A=ℙ(ℤ), C=ℙ(ℤ), D=ℙ(ℤ)}[y=D∪x][][A∈ℙ(ℤ) ;; D∈ℙ(ℤ) ;; x=A∪B ;; y=D∪(A∪B) ;; y∩C⊆D] |- ⊤"),
				// Apply in the hypothesis 4/5
				new SuccessfullReasonerApplication(
						TestLib.genSeq(" A∈ℙ(ℤ) ;; x=A∪B ;; x=y |- ⊤ "),
						new LocalEqRewrite.Input(genPred(typeEnv, "x=y"),
								makePosition("0"), genPred(typeEnv, "x=A∪B")),
						"{A=ℙ(ℤ), x=ℙ(ℤ)}[x=y][][A∈ℙ(ℤ) ;; x=A∪B ;; A∪B=y] |- ⊤"),
				// Apply in the hypothesis 5/5
				new SuccessfullReasonerApplication(
						TestLib.genSeq(" A∈ℙ(ℤ) ;; x=A∪B ;; y=x |- ⊤ "),
						new LocalEqRewrite.Input(genPred(typeEnv, "y=x"),
								makePosition("1"), genPred(typeEnv, "x=A∪B")),
						"{A=ℙ(ℤ), x=ℙ(ℤ)}[y=x][][A∈ℙ(ℤ) ;; x=A∪B ;; y=A∪B] |- ⊤"),
				// Apply in the goal 1/4
				new SuccessfullReasonerApplication(
						TestLib.genSeq(" A∈ℙ(ℤ) ;; x=A∪B |- x∩C⊆D "),
						new LocalEqRewrite.Input(null, makePosition("0.0"),
								genPred(typeEnv, "x=A∪B")),
						"{A=ℙ(ℤ), C=ℙ(ℤ)}[][][A∈ℙ(ℤ) ;; x=A∪B] |- (A∪B)∩C⊆D"),
				// Apply in the goal 2/4
				new SuccessfullReasonerApplication(
						TestLib.genSeq(" A∈ℙ(ℤ) ;; A∪B=x |- x∩C⊆D "),
						new LocalEqRewrite.Input(null, makePosition("0.0"),
								genPred(typeEnv, "A∪B=x")),
						"{A=ℙ(ℤ), C=ℙ(ℤ)}[][][A∈ℙ(ℤ) ;; A∪B=x] |- (A∪B)∩C⊆D"),
				// Apply in the goal 3/4
				new SuccessfullReasonerApplication(
						TestLib.genSeq(" A∈ℙ(ℤ) ;; x=A∪B |- x=y "),
						new LocalEqRewrite.Input(null, makePosition("0"),
								genPred(typeEnv, "x=A∪B")),
						"{A=ℙ(ℤ)}[][][A∈ℙ(ℤ) ;; x=A∪B] |- A∪B=y"),
				// Apply in the goal 4/4
				new SuccessfullReasonerApplication(
						TestLib.genSeq(" A∈ℙ(ℤ) ;; x=A∪B |- y=x "),
						new LocalEqRewrite.Input(null, makePosition("1"),
								genPred(typeEnv, "x=A∪B")),
						"{A=ℙ(ℤ)}[][][A∈ℙ(ℤ) ;; x=A∪B] |- y=A∪B"), };
	}

	@Override
	public UnsuccessfullReasonerApplication[] getUnsuccessfullReasonerApplications() {
		final ITypeEnvironment typeEnv = genTypeEnv("A=ℙ(ℤ), B=ℙ(ℤ), C=ℙ(ℤ), D=ℙ(ℤ)");
		return new UnsuccessfullReasonerApplication[] {
				// Bad input
				new UnsuccessfullReasonerApplication(
						TestLib.genSeq(" ⊤ |- ⊤ "), new EmptyInput()),
				// Should denote an equality
				new UnsuccessfullReasonerApplication(
						TestLib.genSeq(" A∈ℙ(ℤ) ;; x⊆A∪B ;; D⊆x∩C |- ⊤ "),
						new LocalEqRewrite.Input(genPred(typeEnv, "D⊆x∩C"),
								makePosition("0.0"), genPred(typeEnv, "x⊆A∪B"))),
				// Should be a hypothesis of the sequent 1/2
				new UnsuccessfullReasonerApplication(
						TestLib.genSeq(" C∈ℙ(ℤ) ;; D⊆x∩C |- ⊤ "),
						new LocalEqRewrite.Input(genPred(typeEnv, "D⊆x∩C"),
								makePosition("0.0"), genPred(typeEnv, "x=A∪B"))),
				// Should be a hypothesis of the sequent 2/2
				new UnsuccessfullReasonerApplication(
						TestLib.genSeq(" A∈ℙ(ℤ) ;; x=A∪B |- ⊤ "),
						new LocalEqRewrite.Input(genPred(typeEnv, "D⊆x∩C"),
								makePosition("0.0"), genPred(typeEnv, "x=A∪B"))),
				// Equality predicate is not related to a free identifier
				new UnsuccessfullReasonerApplication(
						TestLib.genSeq(" A∈ℙ(ℤ) ;; A∩B=A∪B ;; (A∩B)∩C⊆D |- ⊤ "),
						new LocalEqRewrite.Input(genPred(typeEnv, "(A∩B)∩C⊆D"),
								makePosition("0.0"),
								genPred(typeEnv, "A∩B=A∪B"))), };
	}

}
