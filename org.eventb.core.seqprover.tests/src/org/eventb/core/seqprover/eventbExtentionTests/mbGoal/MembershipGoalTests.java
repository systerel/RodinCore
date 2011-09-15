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
package org.eventb.core.seqprover.eventbExtentionTests.mbGoal;

import static org.eventb.core.seqprover.tests.TestLib.genPred;

import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.seqprover.IReasonerInput;
import org.eventb.core.seqprover.reasonerExtentionTests.AbstractReasonerTests;
import org.eventb.core.seqprover.reasonerInputs.EmptyInput;
import org.eventb.core.seqprover.reasonerInputs.HypothesesReasoner;
import org.eventb.core.seqprover.tests.TestLib;
import org.eventb.internal.core.seqprover.eventbExtensions.mbGoal.MembershipGoal;

/**
 * Unit tests for the reasoner MembershipGoal
 * 
 * @author Emmanuel Billaud
 */
public class MembershipGoalTests extends AbstractReasonerTests {

	private static final IReasonerInput EmptyInput = new EmptyInput();

	@Override
	public String getReasonerID() {
		return new MembershipGoal().getReasonerID();
	}

	@Override
	public SuccessfullReasonerApplication[] getSuccessfulReasonerApplications() {
		final ITypeEnvironment typeEnv = TestLib
				.genTypeEnv("B=ℙ(ℤ), f=ℙ(ℤ×ℤ), m=ℙ(ℤ×ℤ×ℤ)");
		return new SuccessfullReasonerApplication[] {
				// Basic test
				new SuccessfullReasonerApplication(
						TestLib.genSeq(" B∈ℙ(ℤ) ;; x∈B |- x∈B "),
						new HypothesesReasoner.Input(genPred(typeEnv, "x∈B"))),
				// Basic test : works with SUBSETEQ
				new SuccessfullReasonerApplication(
						TestLib.genSeq(" B∈ℙ(ℤ) ;; B⊆C ;; x∈B |- x∈C "),
						new HypothesesReasoner.Input(genPred(typeEnv, "x∈B"),
								genPred(typeEnv, "B⊆C"))),
				// Basic test : works with SUBSET
				new SuccessfullReasonerApplication(
						TestLib.genSeq(" B∈ℙ(ℤ) ;; B⊂C ;; x∈B |- x∈C "),
						new HypothesesReasoner.Input(genPred(typeEnv, "x∈B"),
								genPred(typeEnv, "B⊂C"))),
				// Basic test : works with EQUALITY
				new SuccessfullReasonerApplication(
						TestLib.genSeq(" B∈ℙ(ℤ) ;; B=C ;; x∈B |- x∈C "),
						new HypothesesReasoner.Input(genPred(typeEnv, "x∈B"),
								genPred(typeEnv, "B=C"))),
				// Basic test : works with EQUALITY
				new SuccessfullReasonerApplication(
						TestLib.genSeq(" B∈ℙ(ℤ) ;; C=B ;; x∈B |- x∈C "),
						new HypothesesReasoner.Input(genPred(typeEnv, "x∈B"),
								genPred(typeEnv, "C=B"))),
				// Basic test : works with SETEXT
				new SuccessfullReasonerApplication(
						TestLib.genSeq(" B∈ℙ(ℤ) ;; B⊆C ;; {x,z}⊆B |- x∈C "),
						new HypothesesReasoner.Input(
								genPred(typeEnv, "{x,z}⊆B"), genPred(typeEnv,
										"B⊆C"))),
				// Basic test : works with SETEXT
				new SuccessfullReasonerApplication(
						TestLib.genSeq(" B∈ℙ(ℤ) ;; B⊆C ;; {x,z}⊂B |- x∈C "),
						new HypothesesReasoner.Input(
								genPred(typeEnv, "{x,z}⊂B"), genPred(typeEnv,
										"B⊆C"))),
				// Basic test : works with SETEXT
				new SuccessfullReasonerApplication(
						TestLib.genSeq(" B∈ℙ(ℤ) ;; B⊆C ;; {x,z}=B |- x∈C "),
						new HypothesesReasoner.Input(
								genPred(typeEnv, "{x,z}=B"), genPred(typeEnv,
										"B⊆C"))),
				// Basic test : works with SETEXT
				new SuccessfullReasonerApplication(
						TestLib.genSeq(" B∈ℙ(ℤ) ;; B⊆C ;; B={x,z} |- x∈C "),
						new HypothesesReasoner.Input(
								genPred(typeEnv, "B={x,z}"), genPred(typeEnv,
										"B⊆C"))),
				// Basic test : works with SETMINUS
				new SuccessfullReasonerApplication(
						TestLib.genSeq(" B∈ℙ(ℤ) ;; x∈B∖A |- x∈B "),
						new HypothesesReasoner.Input(genPred(typeEnv, "x∈B∖A"))),
				// Basic test : works with SETMINUS
				new SuccessfullReasonerApplication(
						TestLib.genSeq(" B∈ℙ(ℤ) ;; B⊆C∖A ;; x∈B |- x∈C "),
						new HypothesesReasoner.Input(genPred(typeEnv, "x∈B"),
								genPred(typeEnv, "B⊆C∖A"))),
				// Advanced test : works with SETMINUS
				new SuccessfullReasonerApplication(
						TestLib.genSeq(" B∈ℙ(ℤ) ;; B⊆D∖E ;; x∈B |- x∈(C∪D)∖(A∩E) "),
						new HypothesesReasoner.Input(genPred(typeEnv, "x∈B"),
								genPred(typeEnv, "B⊆D∖E"))),
				// Basic test : works with BINTER
				new SuccessfullReasonerApplication(
						TestLib.genSeq(" B∈ℙ(ℤ) ;;  x∈B∩A |- x∈B "),
						new HypothesesReasoner.Input(genPred(typeEnv, "x∈B∩A"))),
				// Basic test : works with BINTER
				new SuccessfullReasonerApplication(
						TestLib.genSeq(" B∈ℙ(ℤ) ;; B⊆C∩A ;; x∈B |- x∈C "),
						new HypothesesReasoner.Input(genPred(typeEnv, "x∈B"),
								genPred(typeEnv, "B⊆C∩A"))),
				// Advanced test : works with BINTER
				new SuccessfullReasonerApplication(
						TestLib.genSeq(" B∈ℙ(ℤ) ;; A⊆B∩C∩D∩E ;; x∈A |- x∈B∩D "),
						new HypothesesReasoner.Input(genPred(typeEnv, "x∈A"),
								genPred(typeEnv, "A⊆B∩C∩D∩E"))),
				// Advanced test : works with BINTER
				new SuccessfullReasonerApplication(
						TestLib.genSeq(" f∈ℙ(ℤ×ℤ) ;; dom(f∩g)⊆E ;; x∈dom(f∩g∩h∩i) |- x∈E "),
						new HypothesesReasoner.Input(genPred(typeEnv,
								"x∈dom(f∩g∩h∩i)"), genPred(typeEnv,
								"dom(f∩g)⊆E"))),
				// Advanced test : works with BINTER
				new SuccessfullReasonerApplication(
						TestLib.genSeq(" f∈ℙ(ℤ×ℤ) ;; g∈ℙ(ℤ×ℤ) ;; h∈ℙ(ℤ×ℤ) ;; dom(f)∩dom(g)⊆E ;; x∈dom(f)∩dom(g)∩dom(h) |- x∈E "),
						new HypothesesReasoner.Input(genPred(typeEnv,
								"dom(f)∩dom(g)⊆E"), genPred(typeEnv,
								"x∈dom(f)∩dom(g)∩dom(h)"))),
				// Advanced test : works with BINTER
				new SuccessfullReasonerApplication(
						TestLib.genSeq(" f∈ℙ(ℤ×ℤ) ;; ran(f∩g)⊆E ;; x∈ran(f∩g∩h∩i) |- x∈E "),
						new HypothesesReasoner.Input(genPred(typeEnv,
								"x∈ran(f∩g∩h∩i)"), genPred(typeEnv,
								"ran(f∩g)⊆E"))),
				// Advanced test : works with BINTER
				new SuccessfullReasonerApplication(
						TestLib.genSeq(" f∈ℙ(ℤ×ℤ) ;; g∈ℙ(ℤ×ℤ) ;; h∈ℙ(ℤ×ℤ) ;; ran(f)∩ran(g)⊆E ;; x∈ran(f)∩ran(g)∩ran(h) |- x∈E "),
						new HypothesesReasoner.Input(genPred(typeEnv,
								"ran(f)∩ran(g)⊆E"), genPred(typeEnv,
								"x∈ran(f)∩ran(g)∩ran(h)"))),
				// Advanced test : works with BINTER
				new SuccessfullReasonerApplication(
						TestLib.genSeq(" f∈ℙ(ℤ×ℤ) ;; g∈ℙ(ℤ×ℤ) ;; h∈ℙ(ℤ×ℤ) ;; f∼∩g∼⊆i ;; y∈f∼∩g∼∩h |- y∈i "),
						new HypothesesReasoner.Input(genPred(typeEnv,
								"y∈f∼∩g∼∩h"), genPred(typeEnv, "f∼∩g∼⊆i"))),
				// Advanced test : works with BINTER
				new SuccessfullReasonerApplication(
						TestLib.genSeq(" f∈ℙ(ℤ×ℤ) ;; g∈ℙ(ℤ×ℤ) ;; h∈ℙ(ℤ×ℤ) ;; (f∩g)∼⊆i ;; y∈(f∩g∩h)∼ |- y∈i "),
						new HypothesesReasoner.Input(genPred(typeEnv,
								"y∈(f∩g∩h)∼"), genPred(typeEnv, "(f∩g)∼⊆i"))),
				// Basic test : works with BUNION
				new SuccessfullReasonerApplication(
						TestLib.genSeq(" B∈ℙ(ℤ) ;; x∈B |- x∈B∪A "),
						new HypothesesReasoner.Input(genPred(typeEnv, "x∈B"))),
				// Basic test : works with BUNION
				new SuccessfullReasonerApplication(
						TestLib.genSeq(" B∈ℙ(ℤ) ;; B⊆C ;; x∈B |- x∈C∪A "),
						new HypothesesReasoner.Input(genPred(typeEnv, "x∈B"),
								genPred(typeEnv, "B⊆C"))),
				// Advanced test : works with BINTER
				new SuccessfullReasonerApplication(
						TestLib.genSeq(" B∈ℙ(ℤ) ;; A⊆B∪D ;; x∈A |- x∈B∪C∪D∪E "),
						new HypothesesReasoner.Input(genPred(typeEnv, "x∈A"),
								genPred(typeEnv, "A⊆B∪D"))),
				// Advanced test : works with BUNION
				new SuccessfullReasonerApplication(
						TestLib.genSeq(" f∈ℙ(ℤ×ℤ) ;; dom(f∪g∪h∪i)⊆E ;; x∈dom(f∪g) |- x∈E "),
						new HypothesesReasoner.Input(genPred(typeEnv,
								"x∈dom(f∪g)"), genPred(typeEnv,
								"dom(f∪g∪h∪i)⊆E"))),
				// Advanced test : works with BUNION
				new SuccessfullReasonerApplication(
						TestLib.genSeq(" f∈ℙ(ℤ×ℤ) ;; g∈ℙ(ℤ×ℤ) ;; h∈ℙ(ℤ×ℤ) ;; dom(f)∪dom(g)∪dom(h)⊆E ;; x∈dom(f)∪dom(g) |- x∈E "),
						new HypothesesReasoner.Input(genPred(typeEnv,
								"dom(f)∪dom(g)∪dom(h)⊆E"), genPred(typeEnv,
								"x∈dom(f)∪dom(g)"))),
				// Advanced test : works with BUNION
				new SuccessfullReasonerApplication(
						TestLib.genSeq(" f∈ℙ(ℤ×ℤ) ;; ran(f∪g∪h∪i)⊆E ;; x∈ran(f∪g) |- x∈E "),
						new HypothesesReasoner.Input(genPred(typeEnv,
								"x∈ran(f∪g)"), genPred(typeEnv,
								"ran(f∪g∪h∪i)⊆E"))),
				// Advanced test : works with BUNION
				new SuccessfullReasonerApplication(
						TestLib.genSeq(" f∈ℙ(ℤ×ℤ) ;; g∈ℙ(ℤ×ℤ) ;; h∈ℙ(ℤ×ℤ) ;; ran(f)∪ran(g)∪ran(h)⊆E ;; x∈ran(f)∪ran(g) |- x∈E "),
						new HypothesesReasoner.Input(genPred(typeEnv,
								"ran(f)∪ran(g)∪ran(h)⊆E"), genPred(typeEnv,
								"x∈ran(f)∪ran(g)"))),
				// Advanced test : works with BUNION
				new SuccessfullReasonerApplication(
						TestLib.genSeq(" f∈ℙ(ℤ×ℤ) ;; g∈ℙ(ℤ×ℤ) ;; h∈ℙ(ℤ×ℤ) ;; f∼∪g∼∪h⊆i ;; y∈f∼∪g∼ |- y∈i "),
						new HypothesesReasoner.Input(
								genPred(typeEnv, "y∈f∼∪g∼"), genPred(typeEnv,
										"f∼∪g∼∪h⊆i"))),
				// Advanced test : works with BUNION
				new SuccessfullReasonerApplication(
						TestLib.genSeq(" f∈ℙ(ℤ×ℤ) ;; g∈ℙ(ℤ×ℤ) ;; h∈ℙ(ℤ×ℤ) ;; (f∪g∪h)∼⊆i ;; y∈(f∪g)∼ |- y∈i "),
						new HypothesesReasoner.Input(genPred(typeEnv,
								"y∈(f∪g)∼"), genPred(typeEnv, "(f∪g∪h)∼⊆i"))),
				// Basic test : works with RANRES
				new SuccessfullReasonerApplication(
						TestLib.genSeq(" f∈ℙ(ℤ×ℤ) ;; y∈f▷B |- y∈f "),
						new HypothesesReasoner.Input(genPred(typeEnv, "y∈f▷B"))),
				// Basic test : works with RANRES
				new SuccessfullReasonerApplication(
						TestLib.genSeq(" f∈ℙ(ℤ×ℤ) ;; f⊆g▷B ;; y∈f |- y∈g "),
						new HypothesesReasoner.Input(genPred(typeEnv, "y∈f"),
								genPred(typeEnv, "f⊆g▷B"))),
				// Basic test : works with DOMRES
				new SuccessfullReasonerApplication(
						TestLib.genSeq(" f∈ℙ(ℤ×ℤ) ;; y∈B◁f |- y∈f "),
						new HypothesesReasoner.Input(genPred(typeEnv, "y∈B◁f"))),
				// Basic test : works with DOMRES
				new SuccessfullReasonerApplication(
						TestLib.genSeq(" f∈ℙ(ℤ×ℤ) ;; f⊆B◁g ;; y∈f |- y∈g "),
						new HypothesesReasoner.Input(genPred(typeEnv, "y∈f"),
								genPred(typeEnv, "f⊆B◁g"))),
				// Basic test : works with RANSUB
				new SuccessfullReasonerApplication(
						TestLib.genSeq(" f∈ℙ(ℤ×ℤ) ;;  y∈f⩥B |- y∈f "),
						new HypothesesReasoner.Input(genPred(typeEnv, "y∈f⩥B"))),
				// Basic test : works with RANSUB
				new SuccessfullReasonerApplication(
						TestLib.genSeq(" f∈ℙ(ℤ×ℤ) ;; f⊆g⩥B ;; y∈f |- y∈g "),
						new HypothesesReasoner.Input(genPred(typeEnv, "y∈f"),
								genPred(typeEnv, "f⊆g⩥B"))),
				// Advanced test : works with RANSUB
				new SuccessfullReasonerApplication(
						TestLib.genSeq(" f∈ℙ(ℤ×ℤ) ;; (f∪h)⩥(B∩A)⊆g ;; y∈f⩥B |- y∈g "),
						new HypothesesReasoner.Input(genPred(typeEnv, "y∈f⩥B"),
								genPred(typeEnv, "(f∪h)⩥(B∩A)⊆g"))),
				// Advanced test : works with RANSUB
				new SuccessfullReasonerApplication(
						TestLib.genSeq(" f∈ℙ(ℤ×ℤ) ;; f⊆g⩥B ;; y∈f |- y∈(g∪h)⩥(B∩A) "),
						new HypothesesReasoner.Input(genPred(typeEnv, "y∈f"),
								genPred(typeEnv, "f⊆g⩥B"))),
				// Basic test : works with DOMSUB
				new SuccessfullReasonerApplication(
						TestLib.genSeq(" f∈ℙ(ℤ×ℤ) ;; y∈B⩤f |- y∈f "),
						new HypothesesReasoner.Input(genPred(typeEnv, "y∈B⩤f"))),
				// Basic test : works with DOMSUB
				new SuccessfullReasonerApplication(
						TestLib.genSeq(" f∈ℙ(ℤ×ℤ) ;; f⊆B⩤g ;; y∈f |- y∈g "),
						new HypothesesReasoner.Input(genPred(typeEnv, "y∈f"),
								genPred(typeEnv, "f⊆B⩤g"))),
				// Advanced test : works with DOMSUB
				new SuccessfullReasonerApplication(
						TestLib.genSeq(" f∈ℙ(ℤ×ℤ) ;; f⊆B⩤g ;; y∈f |- y∈(B∩A)⩤(g∪h) "),
						new HypothesesReasoner.Input(genPred(typeEnv, "y∈f"),
								genPred(typeEnv, "f⊆B⩤g"))),
				// Advanced test : works with DOMSUB
				new SuccessfullReasonerApplication(
						TestLib.genSeq(" f∈ℙ(ℤ×ℤ) ;; (B∩A)⩤(f∪h)⊆g ;; y∈B⩤f |- y∈g "),
						new HypothesesReasoner.Input(genPred(typeEnv, "y∈B⩤f"),
								genPred(typeEnv, "(B∩A)⩤(f∪h)⊆g"))),
				// Basic test : works with OVR
				new SuccessfullReasonerApplication(
						TestLib.genSeq(" f∈ℙ(ℤ×ℤ) ;; y∈f |- y∈gf "),
						new HypothesesReasoner.Input(genPred(typeEnv, "y∈f"))),
				// Basic test : works with OVR
				new SuccessfullReasonerApplication(
						TestLib.genSeq(" f∈ℙ(ℤ×ℤ) ;;  y∈gf |- y∈kgf "),
						new HypothesesReasoner.Input(genPred(typeEnv, "y∈gf"))),
				// Basic test : works with OVR
				new SuccessfullReasonerApplication(
						TestLib.genSeq(" f∈ℙ(ℤ×ℤ) ;; gf⊆h ;; y∈f |- y∈h "),
						new HypothesesReasoner.Input(genPred(typeEnv, "y∈f"),
								genPred(typeEnv, "gf⊆h"))),
				// Basic test : works with OVR
				new SuccessfullReasonerApplication(
						TestLib.genSeq(" f∈ℙ(ℤ×ℤ) ;; h⊆gf ;; y∈h |- y∈kgf "),
						new HypothesesReasoner.Input(genPred(typeEnv, "y∈h"),
								genPred(typeEnv, "h⊆gf"))),
				// Advanced test : works with OVR
				new SuccessfullReasonerApplication(
						TestLib.genSeq(" f∈ℙ(ℤ×ℤ) ;; k(g∪l)f⊆h ;; y∈gf |- y∈h "),
						new HypothesesReasoner.Input(genPred(typeEnv, "y∈gf"),
								genPred(typeEnv, "k(g∪l)f⊆h"))),
				// Advanced test : works with OVR
				new SuccessfullReasonerApplication(
						TestLib.genSeq(" f∈ℙ(ℤ×ℤ) ;; h⊆gf ;; y∈h |- y∈k(g∪l)f "),
						new HypothesesReasoner.Input(genPred(typeEnv, "y∈h"),
								genPred(typeEnv, "h⊆gf"))),
				// Basic test : works with MAPSTO
				new SuccessfullReasonerApplication(
						TestLib.genSeq(" f∈ℙ(ℤ×ℤ) ;; x↦z∈f |- x∈dom(f) "),
						new HypothesesReasoner.Input(genPred(typeEnv, "x↦z∈f"))),
				// Basic test : works with MAPSTO
				new SuccessfullReasonerApplication(
						TestLib.genSeq(" f∈ℙ(ℤ×ℤ) ;; x↦z∈f |- z∈ran(f) "),
						new HypothesesReasoner.Input(genPred(typeEnv, "x↦z∈f"))),
				// Basic test : works with MAPSTO
				new SuccessfullReasonerApplication(
						TestLib.genSeq(" f∈ℙ(ℤ×ℤ) ;; f⊆B×C ;; x↦z∈f ;; B⊆A |- x∈A "),
						new HypothesesReasoner.Input(genPred(typeEnv, "x↦z∈f"),
								genPred(typeEnv, "f⊆B×C"), genPred(typeEnv,
										"B⊆A"))),
				// Basic test : works with MAPSTO
				new SuccessfullReasonerApplication(
						TestLib.genSeq(" f∈ℙ(ℤ×ℤ) ;; f⊆g ;; x↦z∈f |- x∈dom(g) "),
						new HypothesesReasoner.Input(genPred(typeEnv, "x↦z∈f"),
								genPred(typeEnv, "f⊆g"))),
				// Advanced test : works with CPROD
				new SuccessfullReasonerApplication(
						TestLib.genSeq(" f∈ℙ(ℤ×ℤ) ;; (A∩C)×(C∩E)⊆f ;; y∈(A∩B∩C)×(C∩D∩E) |- y∈f "),
						new HypothesesReasoner.Input(genPred(typeEnv,
								"y∈(A∩B∩C)×(C∩D∩E)"), genPred(typeEnv,
								"(A∩C)×(C∩E)⊆f"))),
				// Basic test : works with multiple inclusion
				new SuccessfullReasonerApplication(
						TestLib.genSeq(" B∈ℙ(ℤ) ;; B⊂C ;; C⊆D ;; D⊂E ;; E⊆F ;; x∈B |- x∈F "),
						new HypothesesReasoner.Input(genPred(typeEnv, "x∈B"),
								genPred(typeEnv, "B⊂C"),
								genPred(typeEnv, "C⊆D"), TestLib.genPred(
										typeEnv, "D⊂E"), TestLib.genPred(
										typeEnv, "E⊆F"))),
				// Advanced test : works with POSITION
				new SuccessfullReasonerApplication(
						TestLib.genSeq(" f∈ℙ(ℤ×ℤ) ;; x∈dom(f) ;; dom(f)⊆A |- x∈A "),
						new HypothesesReasoner.Input(genPred(typeEnv,
								"x∈dom(f)"), genPred(typeEnv, "dom(f)⊆A"))),
				// Advanced test : works with POSITION
				new SuccessfullReasonerApplication(
						TestLib.genSeq(" f∈ℙ(ℤ×ℤ) ;; x∈ran(f) ;; ran(f)⊆A |- x∈A "),
						new HypothesesReasoner.Input(genPred(typeEnv,
								"x∈ran(f)"), genPred(typeEnv, "ran(f)⊆A"))),
				// Advanced test : works with POSITION
				new SuccessfullReasonerApplication(
						TestLib.genSeq(" m∈ℙ(ℤ×ℤ×ℤ) ;; x∈ran(dom(m)) ;; ran(dom(m))⊆A |- x∈A "),
						new HypothesesReasoner.Input(genPred(typeEnv,
								"x∈ran(dom(m))"), genPred(typeEnv,
								"ran(dom(m))⊆A"))),
				// Advanced test : works with POSITION
				new SuccessfullReasonerApplication(
						TestLib.genSeq(" m∈ℙ(ℤ×ℤ×ℤ) ;; x∈ran(dom(m)) ;; dom(m)⊆f ;; ran(f)⊆A |- x∈A "),
						new HypothesesReasoner.Input(genPred(typeEnv,
								" dom(m)⊆f"), genPred(typeEnv, "ran(f)⊆A"),
								genPred(typeEnv, "x∈ran(dom(m))"))),
				// Advanced test : works with POSITION
				new SuccessfullReasonerApplication(
						TestLib.genSeq(" f∈ℙ(ℤ×ℤ) ;; y∈f ;; f∼⊆g |- y∈g∼ "),
						new HypothesesReasoner.Input(genPred(typeEnv, "y∈f"),
								genPred(typeEnv, "f∼⊆g"))),
				// Advanced test : works with POSITION
				new SuccessfullReasonerApplication(
						TestLib.genSeq(" f∈ℙ(ℤ×ℤ) ;; y∈f∼ ;; f⊆g |- y∈g∼ "),
						new HypothesesReasoner.Input(genPred(typeEnv, "y∈f∼"),
								genPred(typeEnv, "f⊆g"))),
				// Advanced test : works with POSITION
				new SuccessfullReasonerApplication(
						TestLib.genSeq(" f∈ℙ(ℤ×ℤ) ;; y∈f∼ ;; f∼⊆g |- y∈g "),
						new HypothesesReasoner.Input(genPred(typeEnv, "y∈f∼"),
								genPred(typeEnv, "f∼⊆g"))),
				// Advanced test : works with POSITION
				new SuccessfullReasonerApplication(
						TestLib.genSeq(" f∈ℙ(ℤ×ℤ) ;; y∈f∼ ;; f⊆g∼ |- y∈g "),
						new HypothesesReasoner.Input(genPred(typeEnv, "y∈f∼"),
								genPred(typeEnv, "f⊆g∼"))),
				// Advanced test : works with POSITION
				new SuccessfullReasonerApplication(
						TestLib.genSeq(" f∈ℙ(ℤ×ℤ) ;; y∈f∼ ;; hf⊆g |- y∈g∼ "),
						new HypothesesReasoner.Input(genPred(typeEnv, "y∈f∼"),
								genPred(typeEnv, "hf⊆g"))),
				// Advanced test : works with POSITION
				new SuccessfullReasonerApplication(
						TestLib.genSeq(" f∈ℙ(ℤ×ℤ) ;; x∈ran(f) ;; dom(f)∪ran(f)⊆A |- x∈A "),
						new HypothesesReasoner.Input(genPred(typeEnv,
								"x∈ran(f)"),
								genPred(typeEnv, "dom(f)∪ran(f)⊆A"))),
				// Advanced test : works with POSITION
				new SuccessfullReasonerApplication(
						TestLib.genSeq(" f∈ℙ(ℤ×ℤ) ;; x∈ran(f) ;; ran(f)∪dom(f)⊆A |- x∈A "),
						new HypothesesReasoner.Input(genPred(typeEnv,
								"x∈ran(f)"),
								genPred(typeEnv, "ran(f)∪dom(f)⊆A"))),
				// Advanced test : works with POSITION
				new SuccessfullReasonerApplication(
						TestLib.genSeq(" m∈ℙ(ℤ×ℤ×ℤ) ;; x∈ran(dom(m)) ;; dom(m)∼⊆f |- x∈dom(f) "),
						new HypothesesReasoner.Input(genPred(typeEnv,
								"x∈ran(dom(m))"), genPred(typeEnv, "dom(m)∼⊆f"))),
				// Advanced test : works with POSITION
				new SuccessfullReasonerApplication(
						TestLib.genSeq(" f∈ℙ(ℤ×ℤ) ;; y∈f∼ ;; hf∼⊆g |- y∈g "),
						new HypothesesReasoner.Input(genPred(typeEnv, "y∈f∼"),
								genPred(typeEnv, "hf∼⊆g"))),
				// Advanced test : works with POSITION
				new SuccessfullReasonerApplication(
						TestLib.genSeq(" f∈ℙ(ℤ×ℤ) ;; y∈f∼ ;; hf⊆g |- y∈g∼ "),
						new HypothesesReasoner.Input(genPred(typeEnv, "y∈f∼"),
								genPred(typeEnv, "hf⊆g"))),
				// Advanced test : works with POSITION
				new SuccessfullReasonerApplication(
						TestLib.genSeq(" f∈ℙ(ℤ×ℤ) ;; y∈f ;; hf∼⊆g |- y∈g∼ "),
						new HypothesesReasoner.Input(genPred(typeEnv, "y∈f"),
								genPred(typeEnv, "hf∼⊆g"))),
				// Advanced test : works with POSITION
				new SuccessfullReasonerApplication(
						TestLib.genSeq(" m∈ℙ(ℤ×ℤ×ℤ) ;; x∈ran(dom(m)) ;; m∼⊆q |- x∈ran(ran(q)) "),
						new HypothesesReasoner.Input(genPred(typeEnv,
								"x∈ran(dom(m))"), genPred(typeEnv, "m∼⊆q"))),
				// Advanced test : works with POSITION
				new SuccessfullReasonerApplication(
						TestLib.genSeq(" m∈ℙ(ℤ×ℤ×ℤ) ;; x∈ran(dom(m)) ;; dom(m)∼⊆f |- x∈dom(f) "),
						new HypothesesReasoner.Input(genPred(typeEnv,
								"x∈ran(dom(m))"), genPred(typeEnv, "dom(m)∼⊆f"))),
		};
	}

	@Override
	public UnsuccessfullReasonerApplication[] getUnsuccessfullReasonerApplications() {
		final ITypeEnvironment typeEnv = TestLib
				.genTypeEnv("B=ℙ(ℤ), f=ℙ(ℤ×ℤ), x=ℤ");
		return new UnsuccessfullReasonerApplication[] {
				//
				new UnsuccessfullReasonerApplication(TestLib.genSeq(" ⊤ |- ⊤"),
						EmptyInput, "Goal does not denote a membership."),

				new UnsuccessfullReasonerApplication(
						TestLib.genSeq("B∈ℙ(ℤ) |- x∈B"), EmptyInput,
						"The input must be a HypothesesReasoner Input."),
				//
				new UnsuccessfullReasonerApplication(
						TestLib.genSeq("B∈ℙ(ℤ) |- x∈B"),
						new HypothesesReasoner.Input(genPred("⊤")),
						"The input must contain only hypotheses."),
				//
				new UnsuccessfullReasonerApplication(
						TestLib.genSeq("B∈ℙ(ℤ) |- x∈B"),
						new HypothesesReasoner.Input(null, genPred("⊤")),
						"The input must contain only hypotheses."),
				//
				new UnsuccessfullReasonerApplication(
						TestLib.genSeq("B∈ℙ(ℤ) |- x∈B"),
						new HypothesesReasoner.Input(genPred(typeEnv, "x∈B"),
								genPred(typeEnv, "x∈C")),
						"The input must contain only hypotheses."),
				//
				new UnsuccessfullReasonerApplication(
						TestLib.genSeq("B∈ℙ(ℤ) ;; C⊆B ;; x∈C |- x∈B"),
						new HypothesesReasoner.Input(genPred(typeEnv, "x∈C"),
								genPred(typeEnv, "x∈C")),
						"There should be only one hypothesis giving information about the goal's member."),
				//
				new UnsuccessfullReasonerApplication(
						TestLib.genSeq("B∈ℙ(ℤ) ;; C⊆B ;; x∈C ;; ⊤ |- x∈B"),
						new HypothesesReasoner.Input(genPred(typeEnv, "x∈C"),
								genPred("⊤")),
						"Given hypotheses must denote either an equality, or a subset or a membership."),
				//
				new UnsuccessfullReasonerApplication(
						TestLib.genSeq(" B∈ℙ(ℤ) ;; B⊂C ;; C⊆D ;; D⊂E ;; E⊆F ;; x∈B |- x∈F "),
						new HypothesesReasoner.Input(genPred(typeEnv, "x∈B"),
								genPred(typeEnv, "B⊂C"),
								genPred(typeEnv, "C⊆D"), TestLib.genPred(
										TestLib.genTypeEnv("E=ℙ(ℤ)"), "E⊆F")),
						"No path can be found."),
				// The current rules do not allow to validate it although we
				// could find a path
				new UnsuccessfullReasonerApplication(
						TestLib.genSeq(" f∈ℙ(ℤ×ℤ) ;; x∈dom(f∪g) ;; dom(f)∪dom(g)⊆B |- x∈B "),
						new HypothesesReasoner.Input(genPred(typeEnv,
								"x∈dom(f∪g)"), genPred(typeEnv,
								"dom(f)∪dom(g)⊆B")), "No path can be found."),
				// The current rules do not allow to validate it although we
				// could find a path
				new UnsuccessfullReasonerApplication(
						TestLib.genSeq(" f∈ℙ(ℤ×ℤ) ;; x∈ran(f∪g) ;; ran(f)∪ran(g)⊆B |- x∈B "),
						new HypothesesReasoner.Input(genPred(typeEnv,
								"x∈ran(f∪g)"), genPred(typeEnv,
								"ran(f)∪ran(g)⊆B")), "No path can be found."),
				// The current rules do not allow to validate it although we
				// could find a path
				new UnsuccessfullReasonerApplication(
						TestLib.genSeq(" f∈ℙ(ℤ×ℤ) ;; y∈(f∪g)∼ ;; f∼∪g∼⊆h |- y∈h "),
						new HypothesesReasoner.Input(genPred(typeEnv,
								"y∈(f∪g)∼"), genPred(typeEnv, "f∼∪g∼⊆h")),
						"No path can be found."),
				// 
				new UnsuccessfullReasonerApplication(
						TestLib.genSeq(" f∈ℙ(ℤ×ℤ) ;; x∈A∩dom(f∩g) ;; A∩dom(f)⊆B |- x∈B "),
						new HypothesesReasoner.Input(genPred(typeEnv,
								"x∈A∩dom(f∩g)"), genPred(typeEnv, "A∩dom(f)⊆B")),
						"No path can be found."),
				//
				new UnsuccessfullReasonerApplication(
						TestLib.genSeq(" A∈ℙ(ℤ) ;; B∈ℙ(ℤ) ;; C∈ℙ(ℤ) ;; y∈A×dom(B×C) |- y∈A×B "),
						new HypothesesReasoner.Input(genPred(typeEnv,
								"y∈A×dom(B×C)")), "No path can be found."),
				//
				new UnsuccessfullReasonerApplication(
						TestLib.genSeq(" A∈ℙ(ℤ) ;; B∈ℙ(ℤ) ;; C∈ℙ(ℤ) ;; y∈A×dom(B×C) |- y∈A×B "),
						new HypothesesReasoner.Input(genPred(typeEnv,
								"y∈A×dom(B×C)")), "No path can be found."),

		};
	}

}