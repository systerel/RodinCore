/*******************************************************************************
 * Copyright (c) 2011, 2012 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.core.seqprover.eventbExtensionTests.mbGoal;

import static org.eventb.core.seqprover.tests.TestLib.genExpr;
import static org.eventb.core.seqprover.tests.TestLib.genPred;
import static org.eventb.core.seqprover.tests.TestLib.mTypeEnvironment;
import org.junit.Assert;

import org.eventb.core.ast.Expression;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.ITypeEnvironmentBuilder;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.RelationalPredicate;
import org.eventb.internal.core.seqprover.eventbExtensions.mbGoal.MembershipGoalRules;
import org.eventb.internal.core.seqprover.eventbExtensions.mbGoal.Rule;
import org.junit.Test;

/**
 * Unit tests for the class Rule.
 * 
 * @author Emmanuel Billaud
 */
public class RuleTest {

	private static final FormulaFactory ff = FormulaFactory.getDefault();

	private static final MembershipGoalRules rf = new MembershipGoalRules(ff);

	@Test
	public void hypothesis() {
		new Rule.Hypothesis<Predicate>(genPred("1=1"), ff);
		new Rule.Hypothesis<Predicate>(genPred("1∈{1}"), ff);
		new Rule.Hypothesis<Predicate>(genPred("{1}⊆{1}"), ff);
		new Rule.Hypothesis<Predicate>(genPred("{1}⊂{1,2}"), ff);
	}

	@Test
	public void expr() {
		final ITypeEnvironmentBuilder typeEnv = mTypeEnvironment("a=ℙ(ℤ)");
		final Rule.Expr expr = new Rule.Expr(genExpr(typeEnv, "a"), ff);
		Assert.assertEquals(genPred(typeEnv, "a⊆a"), expr.getConsequent());
	}

	@Test
	public void domain() {
		final ITypeEnvironmentBuilder typeEnv = mTypeEnvironment("f=ℤ↔ℤ; L1=ℙ(ℤ×ℤ×ℤ×(ℤ×ℤ)); L2=ℙ(ℤ×(ℤ×ℤ)×(ℤ×ℤ))");
		applyDomain(typeEnv, "f⊆g", "dom(f)⊆dom(g)");
		applyDomain(typeEnv, "f⊂g", "dom(f)⊂dom(g)");
		applyDomain(typeEnv, "f∼⊆g", "ran(f)⊆dom(g)");
		applyDomain(typeEnv, "f∼⊂g", "ran(f)⊂dom(g)");
		applyDomain(typeEnv, "f⊆g∼", "dom(f)⊆ran(g)");
		applyDomain(typeEnv, "f⊂g∼", "dom(f)⊂ran(g)");
		applyDomain(typeEnv, "x↦y∈g", "x∈dom(g)");
	}

	private void applyDomain(final ITypeEnvironmentBuilder typeEnv, String predicate,
			String modifiedPredicate) {
		final Predicate pred = genPred(typeEnv, predicate);
		final Predicate modifiedpred = genPred(typeEnv, modifiedPredicate);
		final RelationalPredicate rPred = (RelationalPredicate) pred;
		final Rule.Hypothesis<RelationalPredicate> hyp = new Rule.Hypothesis<RelationalPredicate>(
				rPred, ff);
		final Rule.Domain domain = new Rule.Domain(hyp);
		Assert.assertEquals(domain.getConsequent(), modifiedpred);
	}

	@Test
	public void range() {
		final ITypeEnvironmentBuilder typeEnv = mTypeEnvironment("f=ℤ↔ℤ; L1=ℙ(ℤ×ℤ×ℤ×(ℤ×ℤ)); L2=ℙ(ℤ×(ℤ×ℤ)×(ℤ×ℤ))");
		applyRange(typeEnv, "f⊆g", "ran(f)⊆ran(g)");
		applyRange(typeEnv, "f⊂g", "ran(f)⊂ran(g)");
		applyRange(typeEnv, "f∼⊆g", "dom(f)⊆ran(g)");
		applyRange(typeEnv, "f∼⊂g", "dom(f)⊂ran(g)");
		applyRange(typeEnv, "f⊆g∼", "ran(f)⊆dom(g)");
		applyRange(typeEnv, "f⊂g∼", "ran(f)⊂dom(g)");
		applyRange(typeEnv, "x↦y∈g", "y∈ran(g)");
	}

	private void applyRange(final ITypeEnvironmentBuilder typeEnv, String predicate,
			String modifiedPredicate) {
		final Predicate pred = genPred(typeEnv, predicate);
		final Predicate modifiedpred = genPred(typeEnv, modifiedPredicate);
		final RelationalPredicate rPred = (RelationalPredicate) pred;
		final Rule.Hypothesis<RelationalPredicate> hyp = new Rule.Hypothesis<RelationalPredicate>(
				rPred, ff);
		final Rule.Range range = new Rule.Range(hyp);
		Assert.assertEquals(range.getConsequent(), modifiedpred);
	}

	@Test
	public void converse() {
		final ITypeEnvironmentBuilder typeEnv = mTypeEnvironment("f=ℤ↔ℤ");
		applyConverse(typeEnv, "f⊆g", "f∼⊆g∼");
		applyConverse(typeEnv, "f⊂g", "f∼⊂g∼");
		applyConverse(typeEnv, "f∼⊆g", "f⊆g∼");
		applyConverse(typeEnv, "f∼⊂g", "f⊂g∼");
		applyConverse(typeEnv, "f⊆g∼", "f∼⊆g");
		applyConverse(typeEnv, "f⊂g∼", "f∼⊂g");

		applyConverse(typeEnv, "f◁id⊆H", "id▷f⊆H∼");
		applyConverse(typeEnv, "H⊆f◁id", "H∼⊆id▷f");
		applyConverse(typeEnv, "id▷f⊆H", "id▷f⊆H∼");
		applyConverse(typeEnv, "H⊆id▷f", "H∼⊆id▷f");

	}

	private void applyConverse(final ITypeEnvironmentBuilder typeEnv,
			String predicate, String modifiedPredicate) {
		final Predicate pred = genPred(typeEnv, predicate);
		final Predicate modifiedpred = genPred(typeEnv, modifiedPredicate);
		final RelationalPredicate rPred = (RelationalPredicate) pred;
		final Rule.Hypothesis<RelationalPredicate> hyp = new Rule.Hypothesis<RelationalPredicate>(
				rPred, ff);
		final Rule.Converse converse = new Rule.Converse(hyp);
		Assert.assertEquals(converse.getConsequent(), modifiedpred);
	}

	@Test
	public void equalLeft() {
		final ITypeEnvironmentBuilder typeEnv = mTypeEnvironment("f=ℤ↔ℤ");
		final Predicate pred = genPred(typeEnv, "f=g");
		final Predicate modifiedpred = genPred(typeEnv, "f⊆g");
		final RelationalPredicate rPred = (RelationalPredicate) pred;
		final Rule.Hypothesis<RelationalPredicate> hyp = new Rule.Hypothesis<RelationalPredicate>(
				rPred, ff);
		final Rule.EqualLeft equalfLeft = new Rule.EqualLeft(hyp);
		Assert.assertEquals(equalfLeft.getConsequent(), modifiedpred);
	}

	@Test
	public void equalRight() {
		final ITypeEnvironmentBuilder typeEnv = mTypeEnvironment("f=ℤ↔ℤ");
		final Predicate pred = genPred(typeEnv, "f=g");
		final Predicate modifiedpred = genPred(typeEnv, "g⊆f");
		final RelationalPredicate rPred = (RelationalPredicate) pred;
		final Rule.Hypothesis<RelationalPredicate> hyp = new Rule.Hypothesis<RelationalPredicate>(
				rPred, ff);
		final Rule.EqualRight equalfRight = new Rule.EqualRight(hyp);
		Assert.assertEquals(equalfRight.getConsequent(), modifiedpred);
	}

	@Test
	public void contBInter() {
		final ITypeEnvironmentBuilder typeEnv = mTypeEnvironment("A=ℙ(ℤ)");
		final String subseteq = "A⊆B∩C∩D";
		applyContBInter(typeEnv, subseteq, "A⊆B", "B");
		applyContBInter(typeEnv, subseteq, "A⊆B∩C", "B", "C");
		applyContBInter(typeEnv, subseteq, "A⊆B∩D", "B", "D");
		applyContBInter(typeEnv, subseteq, "A⊆C", "C");
		applyContBInter(typeEnv, subseteq, "A⊆C∩D", "C", "D");
		applyContBInter(typeEnv, subseteq, "A⊆D", "D");
		applyContBInter(typeEnv, subseteq, subseteq, "B", "C", "D");
		final String subset = "A⊂B∩C∩D";
		applyContBInter(typeEnv, subset, "A⊂B", "B");
		applyContBInter(typeEnv, subset, "A⊂B∩C", "B", "C");
		applyContBInter(typeEnv, subset, "A⊂B∩D", "B", "D");
		applyContBInter(typeEnv, subset, "A⊂C", "C");
		applyContBInter(typeEnv, subset, "A⊂C∩D", "C", "D");
		applyContBInter(typeEnv, subset, "A⊂D", "D");
		applyContBInter(typeEnv, subset, subset, "B", "C", "D");
	}

	private void applyContBInter(final ITypeEnvironmentBuilder typeEnv,
			String predicate, String modifiedPredicate, String... expStr) {
		final Predicate pred = genPred(typeEnv, predicate);
		final Predicate modifiedpred = genPred(typeEnv, modifiedPredicate);
		Expression[] expressions = new Expression[expStr.length];
		for (int i = 0; i < expStr.length; i++) {
			expressions[i] = genExpr(typeEnv, expStr[i]);
		}
		final RelationalPredicate rPred = (RelationalPredicate) pred;
		final Rule.Hypothesis<RelationalPredicate> hyp = new Rule.Hypothesis<RelationalPredicate>(
				rPred, ff);
		final Rule.ContBInter contBInter = new Rule.ContBInter(hyp, expressions);
		Assert.assertEquals(contBInter.getConsequent(), modifiedpred);
	}

	@Test
	public void contSetminus() {
		final ITypeEnvironmentBuilder typeEnv = mTypeEnvironment("A=ℙ(ℤ)");
		applyContSetminus(typeEnv, "A⊆B∖C", "A⊆B");
		applyContSetminus(typeEnv, "A⊂B∖C", "A⊂B");
	}

	private void applyContSetminus(final ITypeEnvironmentBuilder typeEnv,
			String predicate, String modifiedPredicate) {
		final Predicate pred = genPred(typeEnv, predicate);
		final Predicate modifiedpred = genPred(typeEnv, modifiedPredicate);
		final RelationalPredicate rPred = (RelationalPredicate) pred;
		final Rule.Hypothesis<RelationalPredicate> hyp = new Rule.Hypothesis<RelationalPredicate>(
				rPred, ff);
		final Rule.ContSetminus contSetminus = new Rule.ContSetminus(hyp);
		Assert.assertEquals(contSetminus.getConsequent(), modifiedpred);
	}

	@Test
	public void contRanres() {
		final ITypeEnvironmentBuilder typeEnv = mTypeEnvironment("f=ℤ↔ℤ");
		applyContRanres(typeEnv, "f⊆g▷A", "f⊆g");
		applyContRanres(typeEnv, "f⊂g▷A", "f⊂g");
	}

	private void applyContRanres(final ITypeEnvironmentBuilder typeEnv,
			String predicate, String modifiedPredicate) {
		final Predicate pred = genPred(typeEnv, predicate);
		final Predicate modifiedpred = genPred(typeEnv, modifiedPredicate);
		final RelationalPredicate rPred = (RelationalPredicate) pred;
		final Rule.Hypothesis<RelationalPredicate> hyp = new Rule.Hypothesis<RelationalPredicate>(
				rPred, ff);
		final Rule.ContRanres contRanres = new Rule.ContRanres(hyp);
		Assert.assertEquals(contRanres.getConsequent(), modifiedpred);
	}

	@Test
	public void contRansub() {
		final ITypeEnvironmentBuilder typeEnv = mTypeEnvironment("f=ℤ↔ℤ");
		applyContRansub(typeEnv, "f⊆g⩥A", "f⊆g");
		applyContRansub(typeEnv, "f⊂g⩥A", "f⊂g");
	}

	private void applyContRansub(final ITypeEnvironmentBuilder typeEnv,
			String predicate, String modifiedPredicate) {
		final Predicate pred = genPred(typeEnv, predicate);
		final Predicate modifiedpred = genPred(typeEnv, modifiedPredicate);
		try {
			final RelationalPredicate rPred = (RelationalPredicate) pred;
			final Rule.Hypothesis<RelationalPredicate> hyp = new Rule.Hypothesis<RelationalPredicate>(
					rPred, ff);
			final Rule.ContRansub contRansub = new Rule.ContRansub(hyp);
			Assert.assertEquals(contRansub.getConsequent(), modifiedpred);
		} catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void contDomRes() {
		final ITypeEnvironmentBuilder typeEnv = mTypeEnvironment("f=ℤ↔ℤ");
		applyContDomres(typeEnv, "f⊆A◁g", "f⊆g");
		applyContDomres(typeEnv, "f⊂A◁g", "f⊂g");
	}

	private void applyContDomres(final ITypeEnvironmentBuilder typeEnv,
			String predicate, String modifiedPredicate) {
		final Predicate pred = genPred(typeEnv, predicate);
		final Predicate modifiedpred = genPred(typeEnv, modifiedPredicate);
		final RelationalPredicate rPred = (RelationalPredicate) pred;
		final Rule.Hypothesis<RelationalPredicate> hyp = new Rule.Hypothesis<RelationalPredicate>(
				rPred, ff);
		final Rule.ContDomres contDomres = new Rule.ContDomres(hyp);
		Assert.assertEquals(contDomres.getConsequent(), modifiedpred);
	}

	@Test
	public void contDomsub() {
		final ITypeEnvironmentBuilder typeEnv = mTypeEnvironment("f=ℤ↔ℤ");
		applyContDomsub(typeEnv, "f⊆A⩤g", "f⊆g");
		applyContDomsub(typeEnv, "f⊂A⩤g", "f⊂g");
	}

	private void applyContDomsub(final ITypeEnvironmentBuilder typeEnv,
			String predicate, String modifiedPredicate) {
		final Predicate pred = genPred(typeEnv, predicate);
		final Predicate modifiedpred = genPred(typeEnv, modifiedPredicate);
		final RelationalPredicate rPred = (RelationalPredicate) pred;
		final Rule.Hypothesis<RelationalPredicate> hyp = new Rule.Hypothesis<RelationalPredicate>(
				rPred, ff);
		final Rule.ContDomsub contDomsub = new Rule.ContDomsub(hyp);
		Assert.assertEquals(contDomsub.getConsequent(), modifiedpred);
	}

	@Test
	public void inclSetext() {
		final ITypeEnvironmentBuilder typeEnv = mTypeEnvironment("A=ℙ(ℤ)");
		final String subseteq = "{x,y,z}⊆A";
		applyInclSetext(typeEnv, subseteq, "x∈A", "x");
		applyInclSetext(typeEnv, subseteq, "{x,y}⊆A", "x", "y");
		applyInclSetext(typeEnv, subseteq, "{x,z}⊆A", "x", "z");
		applyInclSetext(typeEnv, subseteq, "y∈A", "y");
		applyInclSetext(typeEnv, subseteq, "{y,z}⊆A", "y", "z");
		applyInclSetext(typeEnv, subseteq, "z∈A", "z");
		applyInclSetext(typeEnv, subseteq, "{x,y,z}⊆A", "x", "y", "z");
		final String subset = "{x,y,z}⊂A";
		applyInclSetext(typeEnv, subset, "x∈A", "x");
		applyInclSetext(typeEnv, subset, "{x,y}⊂A", "x", "y");
		applyInclSetext(typeEnv, subset, "{x,z}⊂A", "x", "z");
		applyInclSetext(typeEnv, subset, "y∈A", "y");
		applyInclSetext(typeEnv, subset, "{y,z}⊂A", "y", "z");
		applyInclSetext(typeEnv, subset, "z∈A", "z");
		applyInclSetext(typeEnv, subset, "{x,y,z}⊂A", "x", "y", "z");
	}

	private void applyInclSetext(final ITypeEnvironmentBuilder typeEnv,
			String predicate, String modifiedPredicate, String... expStr) {
		final Predicate pred = genPred(typeEnv, predicate);
		final Predicate modifiedpred = genPred(typeEnv, modifiedPredicate);
		Expression[] expressions = new Expression[expStr.length];
		for (int i = 0; i < expStr.length; i++) {
			expressions[i] = genExpr(typeEnv, expStr[i]);
		}
		final RelationalPredicate rPred = (RelationalPredicate) pred;
		final Rule.Hypothesis<RelationalPredicate> hyp = new Rule.Hypothesis<RelationalPredicate>(
				rPred, ff);
		final Rule.InclSetext inclSetext = new Rule.InclSetext(hyp, expressions);
		Assert.assertEquals(inclSetext.getConsequent(), modifiedpred);
	}

	@Test
	public void includBunion() {
		final ITypeEnvironmentBuilder typeEnv = mTypeEnvironment("Z=ℙ(ℤ)");
		final String subseteq = "A∪B∪C⊆Z";
		applyIncludBunion(typeEnv, subseteq, "A⊆Z", "A");
		applyIncludBunion(typeEnv, subseteq, "A∪B⊆Z", "A", "B");
		applyIncludBunion(typeEnv, subseteq, "A∪C⊆Z", "A", "C");
		applyIncludBunion(typeEnv, subseteq, "B⊆Z", "B");
		applyIncludBunion(typeEnv, subseteq, "B∪C⊆Z", "B", "C");
		applyIncludBunion(typeEnv, subseteq, "C⊆Z", "C");
		applyIncludBunion(typeEnv, subseteq, subseteq, "A", "B", "C");
		final String subset = "A∪B∪C⊂Z";
		applyIncludBunion(typeEnv, subset, "A⊂Z", "A");
		applyIncludBunion(typeEnv, subset, "A∪B⊂Z", "A", "B");
		applyIncludBunion(typeEnv, subset, "A∪C⊂Z", "A", "C");
		applyIncludBunion(typeEnv, subset, "B⊂Z", "B");
		applyIncludBunion(typeEnv, subset, "B∪C⊂Z", "B", "C");
		applyIncludBunion(typeEnv, subset, "C⊂Z", "C");
		applyIncludBunion(typeEnv, subset, subset, "A", "B", "C");
	}

	private void applyIncludBunion(final ITypeEnvironmentBuilder typeEnv,
			String predicate, String modifiedPredicate, String... expStr) {
		final Predicate pred = genPred(typeEnv, predicate);
		final Predicate modifiedpred = genPred(typeEnv, modifiedPredicate);
		Expression[] expressions = new Expression[expStr.length];
		for (int i = 0; i < expStr.length; i++) {
			expressions[i] = genExpr(typeEnv, expStr[i]);
		}
		final RelationalPredicate rPred = (RelationalPredicate) pred;
		final Rule.Hypothesis<RelationalPredicate> hyp = new Rule.Hypothesis<RelationalPredicate>(
				rPred, ff);
		final Rule.IncludBunion includBunion = new Rule.IncludBunion(hyp,
				expressions);
		Assert.assertEquals(includBunion.getConsequent(), modifiedpred);
	}

	@Test
	public void includOvr() {
		final ITypeEnvironmentBuilder typeEnv = mTypeEnvironment("f=ℤ↔ℤ");
		final String subseteq = "ghk⊆f";
		applyIncludOvr(typeEnv, subseteq, "k⊆f", "k");
		applyIncludOvr(typeEnv, subseteq, "hk⊆f", "hk");
		applyIncludOvr(typeEnv, subseteq, subseteq, "ghk");
		final String subset = "ghk⊂f";
		applyIncludOvr(typeEnv, subset, "k⊂f", "k");
		applyIncludOvr(typeEnv, subset, "hk⊂f", "hk");
		applyIncludOvr(typeEnv, subset, subset, "ghk");
	}

	private void applyIncludOvr(final ITypeEnvironmentBuilder typeEnv,
			String predicate, String modifiedPredicate, String expStr) {
		final Predicate pred = genPred(typeEnv, predicate);
		final Predicate modifiedpred = genPred(typeEnv, modifiedPredicate);
		Expression expression = genExpr(typeEnv, expStr);
		final RelationalPredicate rPred = (RelationalPredicate) pred;
		final Rule.Hypothesis<RelationalPredicate> hyp = new Rule.Hypothesis<RelationalPredicate>(
				rPred, ff);
		final Rule.IncludOvr includBunion = new Rule.IncludOvr(hyp, expression);
		Assert.assertEquals(includBunion.getConsequent(), modifiedpred);
	}

	@Test
	public void simpDomCprodLeft() {
		final ITypeEnvironmentBuilder typeEnv = mTypeEnvironment("A=ℙ(ℤ); B=ℙ(ℤ)");
		final String subseteq = "dom(A×B)⊆Z";
		applySimpDomCprodLeft(typeEnv, subseteq, "A⊆Z");
		final String subset = "dom(A×B)⊂Z";
		applySimpDomCprodLeft(typeEnv, subset, "A⊂Z");
	}

	private void applySimpDomCprodLeft(final ITypeEnvironmentBuilder typeEnv,
			String predicate, String modifiedPredicate) {
		final Predicate pred = genPred(typeEnv, predicate);
		final Predicate modifiedpred = genPred(typeEnv, modifiedPredicate);
		final RelationalPredicate rPred = (RelationalPredicate) pred;
		final Rule.Hypothesis<RelationalPredicate> hyp = new Rule.Hypothesis<RelationalPredicate>(
				rPred, ff);
		final Rule.SimpDomCProdLeft simpDom = new Rule.SimpDomCProdLeft(hyp);
		Assert.assertEquals(simpDom.getConsequent(), modifiedpred);
	}

	@Test
	public void simpDomCprodRight() {
		final ITypeEnvironmentBuilder typeEnv = mTypeEnvironment("A=ℙ(ℤ); B=ℙ(ℤ)");
		final String subseteq = "Z⊆dom(A×B)";
		applySimpDomCprodRight(typeEnv, subseteq, "Z⊆A");
		final String subset = "Z⊂dom(A×B)";
		applySimpDomCprodRight(typeEnv, subset, "Z⊂A");
	}

	private void applySimpDomCprodRight(final ITypeEnvironmentBuilder typeEnv,
			String predicate, String modifiedPredicate) {
		final Predicate pred = genPred(typeEnv, predicate);
		final Predicate modifiedpred = genPred(typeEnv, modifiedPredicate);
		final RelationalPredicate rPred = (RelationalPredicate) pred;
		final Rule.Hypothesis<RelationalPredicate> hyp = new Rule.Hypothesis<RelationalPredicate>(
				rPred, ff);
		final Rule.SimpDomCProdRight simpDom = new Rule.SimpDomCProdRight(hyp);
		Assert.assertEquals(simpDom.getConsequent(), modifiedpred);
	}

	@Test
	public void simpRanCprodLeft() {
		final ITypeEnvironmentBuilder typeEnv = mTypeEnvironment("A=ℙ(ℤ); B=ℙ(ℤ)");
		final String subseteq = "ran(A×B)⊆Z";
		applySimpRanCprodLeft(typeEnv, subseteq, "B⊆Z");
		final String subset = "ran(A×B)⊂Z";
		applySimpRanCprodLeft(typeEnv, subset, "B⊂Z");
	}

	private void applySimpRanCprodLeft(final ITypeEnvironmentBuilder typeEnv,
			String predicate, String modifiedPredicate) {
		final Predicate pred = genPred(typeEnv, predicate);
		final Predicate modifiedpred = genPred(typeEnv, modifiedPredicate);
		final RelationalPredicate rPred = (RelationalPredicate) pred;
		final Rule.Hypothesis<RelationalPredicate> hyp = new Rule.Hypothesis<RelationalPredicate>(
				rPred, ff);
		final Rule.SimpRanCProdLeft simpRan = new Rule.SimpRanCProdLeft(hyp);
		Assert.assertEquals(simpRan.getConsequent(), modifiedpred);
	}

	@Test
	public void simpRanCprodRight() {
		final ITypeEnvironmentBuilder typeEnv = mTypeEnvironment("A=ℙ(ℤ); B=ℙ(ℤ)");
		final String subseteq = "Z⊆ran(A×B)";
		applySimpRanCprodRight(typeEnv, subseteq, "Z⊆B");
		final String subset = "Z⊂ran(A×B)";
		applySimpRanCprodRight(typeEnv, subset, "Z⊂B");
	}

	private void applySimpRanCprodRight(final ITypeEnvironmentBuilder typeEnv,
			String predicate, String modifiedPredicate) {
		final Predicate pred = genPred(typeEnv, predicate);
		final Predicate modifiedpred = genPred(typeEnv, modifiedPredicate);
		final RelationalPredicate rPred = (RelationalPredicate) pred;
		final Rule.Hypothesis<RelationalPredicate> hyp = new Rule.Hypothesis<RelationalPredicate>(
				rPred, ff);
		final Rule.SimpRanCProdRight simpRan = new Rule.SimpRanCProdRight(hyp);
		Assert.assertEquals(simpRan.getConsequent(), modifiedpred);
	}

	@Test
	public void simpConvCprodLeft() {
		final ITypeEnvironmentBuilder typeEnv = mTypeEnvironment("A=ℙ(ℤ); B=ℙ(ℤ)");
		final String subseteq = "(A×B)∼⊆Z";
		applySimpConvCprodLeft(typeEnv, subseteq, "B×A⊆Z");
		final String subset = "(A×B)∼⊂Z";
		applySimpConvCprodLeft(typeEnv, subset, "B×A⊂Z");
	}

	private void applySimpConvCprodLeft(final ITypeEnvironmentBuilder typeEnv,
			String predicate, String modifiedPredicate) {
		final Predicate pred = genPred(typeEnv, predicate);
		final Predicate modifiedpred = genPred(typeEnv, modifiedPredicate);
		final RelationalPredicate rPred = (RelationalPredicate) pred;
		final Rule.Hypothesis<RelationalPredicate> hyp = new Rule.Hypothesis<RelationalPredicate>(
				rPred, ff);
		final Rule.SimpConvCProdLeft simpConv = new Rule.SimpConvCProdLeft(hyp);
		Assert.assertEquals(simpConv.getConsequent(), modifiedpred);
	}

	@Test
	public void simpConvCprodRight() {
		final ITypeEnvironmentBuilder typeEnv = mTypeEnvironment("A=ℙ(ℤ); B=ℙ(ℤ)");
		final String subseteq = "Z⊆(A×B)∼";
		applySimpConvCprodRight(typeEnv, subseteq, "Z⊆B×A");
		final String subset = "Z⊂(A×B)∼";
		applySimpConvCprodRight(typeEnv, subset, "Z⊂B×A");
	}

	private void applySimpConvCprodRight(final ITypeEnvironmentBuilder typeEnv,
			String predicate, String modifiedPredicate) {
		final Predicate pred = genPred(typeEnv, predicate);
		final Predicate modifiedpred = genPred(typeEnv, modifiedPredicate);
		final RelationalPredicate rPred = (RelationalPredicate) pred;
		final Rule.Hypothesis<RelationalPredicate> hyp = new Rule.Hypothesis<RelationalPredicate>(
				rPred, ff);
		final Rule.SimpConvCProdRight simpConv = new Rule.SimpConvCProdRight(
				hyp);
		Assert.assertEquals(simpConv.getConsequent(), modifiedpred);
	}

	@Test
	public void simpConvDomresLeft() {
		final ITypeEnvironmentBuilder typeEnv = mTypeEnvironment("A=ℙ(ℤ); f=ℤ↔ℤ");
		final String subseteq = "(A◁f)∼⊆g";
		applySimpConvDomresLeft(typeEnv, subseteq, "f∼▷A⊆g");
		final String subset = "(A◁f)∼⊂g";
		applySimpConvDomresLeft(typeEnv, subset, "f∼▷A⊂g");
	}

	private void applySimpConvDomresLeft(final ITypeEnvironmentBuilder typeEnv,
			String predicate, String modifiedPredicate) {
		final Predicate pred = genPred(typeEnv, predicate);
		final Predicate modifiedpred = genPred(typeEnv, modifiedPredicate);
		final RelationalPredicate rPred = (RelationalPredicate) pred;
		final Rule.Hypothesis<RelationalPredicate> hyp = new Rule.Hypothesis<RelationalPredicate>(
				rPred, ff);
		final Rule.SimpConvDomresLeft simp = new Rule.SimpConvDomresLeft(
				hyp);
		Assert.assertEquals(simp.getConsequent(), modifiedpred);
	}

	@Test
	public void simpConvDomresRight() {
		final ITypeEnvironmentBuilder typeEnv = mTypeEnvironment("A=ℙ(ℤ); f=ℤ↔ℤ");
		final String subseteq = "g⊆(A◁f)∼";
		applySimpConvDomresRight(typeEnv, subseteq, "g⊆f∼▷A");
		final String subset = "g⊂(A◁f)∼";
		applySimpConvDomresRight(typeEnv, subset, "g⊂f∼▷A");
	}

	private void applySimpConvDomresRight(final ITypeEnvironmentBuilder typeEnv,
			String predicate, String modifiedPredicate) {
		final Predicate pred = genPred(typeEnv, predicate);
		final Predicate modifiedpred = genPred(typeEnv, modifiedPredicate);
		final RelationalPredicate rPred = (RelationalPredicate) pred;
		final Rule.Hypothesis<RelationalPredicate> hyp = new Rule.Hypothesis<RelationalPredicate>(
				rPred, ff);
		final Rule.SimpConvDomresRight simp = new Rule.SimpConvDomresRight(
				hyp);
		Assert.assertEquals(simp.getConsequent(), modifiedpred);
	}

	@Test
	public void simpConvDomsubLeft() {
		final ITypeEnvironmentBuilder typeEnv = mTypeEnvironment("A=ℙ(ℤ); f=ℤ↔ℤ");
		final String subseteq = "(A⩤f)∼⊆g";
		applySimpConvDomsubLeft(typeEnv, subseteq, "f∼⩥A⊆g");
		final String subset = "(A⩤f)∼⊂g";
		applySimpConvDomsubLeft(typeEnv, subset, "f∼⩥A⊂g");
	}

	private void applySimpConvDomsubLeft(final ITypeEnvironmentBuilder typeEnv,
			String predicate, String modifiedPredicate) {
		final Predicate pred = genPred(typeEnv, predicate);
		final Predicate modifiedpred = genPred(typeEnv, modifiedPredicate);
		final RelationalPredicate rPred = (RelationalPredicate) pred;
		final Rule.Hypothesis<RelationalPredicate> hyp = new Rule.Hypothesis<RelationalPredicate>(
				rPred, ff);
		final Rule.SimpConvDomsubLeft simp = new Rule.SimpConvDomsubLeft(
				hyp);
		Assert.assertEquals(simp.getConsequent(), modifiedpred);
	}

	@Test
	public void simpConvDomsubRight() {
		final ITypeEnvironmentBuilder typeEnv = mTypeEnvironment("A=ℙ(ℤ); f=ℤ↔ℤ");
		final String subseteq = "g⊆(A⩤f)∼";
		applySimpConvDomsubRight(typeEnv, subseteq, "g⊆f∼⩥A");
		final String subset = "g⊂(A⩤f)∼";
		applySimpConvDomsubRight(typeEnv, subset, "g⊂f∼⩥A");
	}

	private void applySimpConvDomsubRight(final ITypeEnvironmentBuilder typeEnv,
			String predicate, String modifiedPredicate) {
		final Predicate pred = genPred(typeEnv, predicate);
		final Predicate modifiedpred = genPred(typeEnv, modifiedPredicate);
		final RelationalPredicate rPred = (RelationalPredicate) pred;
		final Rule.Hypothesis<RelationalPredicate> hyp = new Rule.Hypothesis<RelationalPredicate>(
				rPred, ff);
		final Rule.SimpConvDomsubRight simp = new Rule.SimpConvDomsubRight(
				hyp);
		Assert.assertEquals(simp.getConsequent(), modifiedpred);
	}

	@Test
	public void simpConvRanresLeft() {
		final ITypeEnvironmentBuilder typeEnv = mTypeEnvironment("A=ℙ(ℤ); f=ℤ↔ℤ");
		final String subseteq = "(f▷A)∼⊆g";
		applySimpConvRanresLeft(typeEnv, subseteq, "A◁f∼⊆g");
		final String subset = "(f▷A)∼⊂g";
		applySimpConvRanresLeft(typeEnv, subset, "A◁f∼⊂g");
	}

	private void applySimpConvRanresLeft(final ITypeEnvironmentBuilder typeEnv,
			String predicate, String modifiedPredicate) {
		final Predicate pred = genPred(typeEnv, predicate);
		final Predicate modifiedpred = genPred(typeEnv, modifiedPredicate);
		final RelationalPredicate rPred = (RelationalPredicate) pred;
		final Rule.Hypothesis<RelationalPredicate> hyp = new Rule.Hypothesis<RelationalPredicate>(
				rPred, ff);
		final Rule.SimpConvRanresLeft simp = new Rule.SimpConvRanresLeft(
				hyp);
		Assert.assertEquals(simp.getConsequent(), modifiedpred);
	}

	@Test
	public void simpConvRanresRight() {
		final ITypeEnvironmentBuilder typeEnv = mTypeEnvironment("A=ℙ(ℤ); f=ℤ↔ℤ");
		final String subseteq = "g⊆(f▷A)∼";
		applySimpConvRanresRight(typeEnv, subseteq, "g⊆A◁f∼");
		final String subset = "g⊂(f▷A)∼";
		applySimpConvRanresRight(typeEnv, subset, "g⊂A◁f∼");
	}

	private void applySimpConvRanresRight(final ITypeEnvironmentBuilder typeEnv,
			String predicate, String modifiedPredicate) {
		final Predicate pred = genPred(typeEnv, predicate);
		final Predicate modifiedpred = genPred(typeEnv, modifiedPredicate);
		final RelationalPredicate rPred = (RelationalPredicate) pred;
		final Rule.Hypothesis<RelationalPredicate> hyp = new Rule.Hypothesis<RelationalPredicate>(
				rPred, ff);
		final Rule.SimpConvRanresRight simp = new Rule.SimpConvRanresRight(
				hyp);
		Assert.assertEquals(simp.getConsequent(), modifiedpred);
	}

	@Test
	public void simpConvRansubLeft() {
		final ITypeEnvironmentBuilder typeEnv = mTypeEnvironment("A=ℙ(ℤ); f=ℤ↔ℤ");
		final String subseteq = "(f⩥A)∼⊆g";
		applySimpConvRansubLeft(typeEnv, subseteq, "A⩤f∼⊆g");
		final String subset = "(f⩥A)∼⊂g";
		applySimpConvRansubLeft(typeEnv, subset, "A⩤f∼⊂g");
	}

	private void applySimpConvRansubLeft(final ITypeEnvironmentBuilder typeEnv,
			String predicate, String modifiedPredicate) {
		final Predicate pred = genPred(typeEnv, predicate);
		final Predicate modifiedpred = genPred(typeEnv, modifiedPredicate);
		final RelationalPredicate rPred = (RelationalPredicate) pred;
		final Rule.Hypothesis<RelationalPredicate> hyp = new Rule.Hypothesis<RelationalPredicate>(
				rPred, ff);
		final Rule.SimpConvRansubLeft simp = new Rule.SimpConvRansubLeft(
				hyp);
		Assert.assertEquals(simp.getConsequent(), modifiedpred);
	}

	@Test
	public void simpConvRansubRight() {
		final ITypeEnvironmentBuilder typeEnv = mTypeEnvironment("A=ℙ(ℤ); f=ℤ↔ℤ");
		final String subseteq = "g⊆(f⩥A)∼";
		applySimpConvRansubRight(typeEnv, subseteq, "g⊆A⩤f∼");
		final String subset = "g⊂(f⩥A)∼";
		applySimpConvRansubRight(typeEnv, subset, "g⊂A⩤f∼");
	}

	private void applySimpConvRansubRight(final ITypeEnvironmentBuilder typeEnv,
			String predicate, String modifiedPredicate) {
		final Predicate pred = genPred(typeEnv, predicate);
		final Predicate modifiedpred = genPred(typeEnv, modifiedPredicate);
		final RelationalPredicate rPred = (RelationalPredicate) pred;
		final Rule.Hypothesis<RelationalPredicate> hyp = new Rule.Hypothesis<RelationalPredicate>(
				rPred, ff);
		final Rule.SimpConvRansubRight simp = new Rule.SimpConvRansubRight(
				hyp);
		Assert.assertEquals(simp.getConsequent(), modifiedpred);
	}

	@Test
	public void simpDomDomRanresPrj1Left() {
		final ITypeEnvironmentBuilder typeEnv = mTypeEnvironment("g=ℙ(ℤ)");
		final String subseteq = "dom(dom((prj1⦂ℤ×ℤ↔ℤ)▷f))⊆g";
		applySimpDomDomRanresPrj1Left(typeEnv, subseteq, "f⊆g");
		final String subset = "dom(dom((prj1⦂ℤ×ℤ↔ℤ)▷f))⊂g";
		applySimpDomDomRanresPrj1Left(typeEnv, subset, "f⊂g");
	}

	private void applySimpDomDomRanresPrj1Left(final ITypeEnvironmentBuilder typeEnv,
			String predicate, String modifiedPredicate) {
		final Predicate pred = genPred(typeEnv, predicate);
		final Predicate modifiedpred = genPred(typeEnv, modifiedPredicate);
		final RelationalPredicate rPred = (RelationalPredicate) pred;
		final Rule.Hypothesis<RelationalPredicate> hyp = new Rule.Hypothesis<RelationalPredicate>(
				rPred, ff);
		final Rule.SimpDomDomRanresPrj1Left simp = new Rule.SimpDomDomRanresPrj1Left(
				hyp);
		Assert.assertEquals(simp.getConsequent(), modifiedpred);
	}

	@Test
	public void simpDomDomRanresPrj1LefRight() {
		final ITypeEnvironmentBuilder typeEnv = mTypeEnvironment("g=ℙ(ℤ)");
		final String subseteq = "g⊆dom(dom((prj1⦂ℤ×ℤ↔ℤ)▷f))";
		applySimpDomDomRanresPrj1Right(typeEnv, subseteq, "g⊆f");
		final String subset = "g⊂dom(dom((prj1⦂ℤ×ℤ↔ℤ)▷f))";
		applySimpDomDomRanresPrj1Right(typeEnv, subset, "g⊂f");
	}

	private void applySimpDomDomRanresPrj1Right(final ITypeEnvironmentBuilder typeEnv,
			String predicate, String modifiedPredicate) {
		final Predicate pred = genPred(typeEnv, predicate);
		final Predicate modifiedpred = genPred(typeEnv, modifiedPredicate);
		final RelationalPredicate rPred = (RelationalPredicate) pred;
		final Rule.Hypothesis<RelationalPredicate> hyp = new Rule.Hypothesis<RelationalPredicate>(
				rPred, ff);
		final Rule.SimpDomDomRanresPrj1Right simp = new Rule.SimpDomDomRanresPrj1Right(
				hyp);
		Assert.assertEquals(simp.getConsequent(), modifiedpred);
	}

	@Test
	public void simpDomDomresLeft() {
		final ITypeEnvironmentBuilder typeEnv = mTypeEnvironment("A=ℙ(ℤ); f=ℤ↔ℤ");
		final String subseteq = "dom(A◁f)⊆B";
		applySimpDomDomresLeft(typeEnv, subseteq, "dom(f)∩A⊆B");
		final String subseteqId = "dom(A◁id)⊆B";
		applySimpDomDomresLeft(typeEnv, subseteqId, "A⊆B");
		final String subseteqPrj1 = "dom(f◁prj1)⊆g";
		applySimpDomDomresLeft(typeEnv, subseteqPrj1, "f⊆g");
		final String subseteqPrj2 = "dom(f◁prj2)⊆g";
		applySimpDomDomresLeft(typeEnv, subseteqPrj2, "f⊆g");
		final String subset = "dom(A◁f)⊂B";
		applySimpDomDomresLeft(typeEnv, subset, "dom(f)∩A⊂B");
	}

	private void applySimpDomDomresLeft(final ITypeEnvironmentBuilder typeEnv,
			String predicate, String modifiedPredicate) {
		final Predicate pred = genPred(typeEnv, predicate);
		final Predicate modifiedpred = genPred(typeEnv, modifiedPredicate);
		final RelationalPredicate rPred = (RelationalPredicate) pred;
		final Rule.Hypothesis<RelationalPredicate> hyp = new Rule.Hypothesis<RelationalPredicate>(
				rPred, ff);
		final Rule.SimpDomDomresLeft simp = new Rule.SimpDomDomresLeft(
				hyp);
		Assert.assertEquals(simp.getConsequent(), modifiedpred);
	}

	@Test
	public void simpDomDomresRight() {
		final ITypeEnvironmentBuilder typeEnv = mTypeEnvironment("A=ℙ(ℤ); f=ℤ↔ℤ");
		final String subseteq = "B⊆dom(A◁f)";
		applySimpDomDomresRight(typeEnv, subseteq, "B⊆dom(f)∩A");
		final String subseteqId = "B⊆dom(A◁id)";
		applySimpDomDomresRight(typeEnv, subseteqId, "B⊆A");
		final String subseteqPrj1 = "g⊆dom(f◁prj1)";
		applySimpDomDomresRight(typeEnv, subseteqPrj1, "g⊆f");
		final String subseteqPrj2 = "g⊆dom(f◁prj2)";
		applySimpDomDomresRight(typeEnv, subseteqPrj2, "g⊆f");
		final String subset = "B⊂dom(A◁f)";
		applySimpDomDomresRight(typeEnv, subset, "B⊂dom(f)∩A");
	}

	private void applySimpDomDomresRight(final ITypeEnvironmentBuilder typeEnv,
			String predicate, String modifiedPredicate) {
		final Predicate pred = genPred(typeEnv, predicate);
		final Predicate modifiedpred = genPred(typeEnv, modifiedPredicate);
		final RelationalPredicate rPred = (RelationalPredicate) pred;
		final Rule.Hypothesis<RelationalPredicate> hyp = new Rule.Hypothesis<RelationalPredicate>(
				rPred, ff);
		final Rule.SimpDomDomresRight simp = new Rule.SimpDomDomresRight(
				hyp);
		Assert.assertEquals(simp.getConsequent(), modifiedpred);
	}

	@Test
	public void simpDomDomsubLeft() {
		final ITypeEnvironmentBuilder typeEnv = mTypeEnvironment("A=ℙ(ℤ); f=ℤ↔ℤ");
		final String subseteq = "dom(A⩤f)⊆B";
		applySimpDomDomsubLeft(typeEnv, subseteq, "dom(f)∖A⊆B");
		final String subset = "dom(A⩤f)⊂B";
		applySimpDomDomsubLeft(typeEnv, subset, "dom(f)∖A⊂B");
	}

	private void applySimpDomDomsubLeft(final ITypeEnvironmentBuilder typeEnv,
			String predicate, String modifiedPredicate) {
		final Predicate pred = genPred(typeEnv, predicate);
		final Predicate modifiedpred = genPred(typeEnv, modifiedPredicate);
		final RelationalPredicate rPred = (RelationalPredicate) pred;
		final Rule.Hypothesis<RelationalPredicate> hyp = new Rule.Hypothesis<RelationalPredicate>(
				rPred, ff);
		final Rule.SimpDomDomsubLeft simp = new Rule.SimpDomDomsubLeft(
				hyp);
		Assert.assertEquals(simp.getConsequent(), modifiedpred);
	}

	@Test
	public void simpDomDomsubRight() {
		final ITypeEnvironmentBuilder typeEnv = mTypeEnvironment("A=ℙ(ℤ); f=ℤ↔ℤ");
		final String subseteq = "B⊆dom(A⩤f)";
		applySimpDomDomsubRight(typeEnv, subseteq, "B⊆dom(f)∖A");
		final String subset = "B⊂dom(A⩤f)";
		applySimpDomDomsubRight(typeEnv, subset, "B⊂dom(f)∖A");
	}

	private void applySimpDomDomsubRight(final ITypeEnvironmentBuilder typeEnv,
			String predicate, String modifiedPredicate) {
		final Predicate pred = genPred(typeEnv, predicate);
		final Predicate modifiedpred = genPred(typeEnv, modifiedPredicate);
		final RelationalPredicate rPred = (RelationalPredicate) pred;
		final Rule.Hypothesis<RelationalPredicate> hyp = new Rule.Hypothesis<RelationalPredicate>(
				rPred, ff);
		final Rule.SimpDomDomsubRight simp = new Rule.SimpDomDomsubRight(
				hyp);
		Assert.assertEquals(simp.getConsequent(), modifiedpred);
	}

	@Test
	public void simpDomRanresIdLeft() {
		final ITypeEnvironmentBuilder typeEnv = mTypeEnvironment("A=ℙ(ℤ); f=ℤ↔ℤ");
		final String subseteq = "dom(id▷f)⊆g";
		applySimpDomRanresIdLeft(typeEnv, subseteq, "f⊆g");
		final String subset = "dom(id▷f)⊂g";
		applySimpDomRanresIdLeft(typeEnv, subset, "f⊂g");
	}

	private void applySimpDomRanresIdLeft(final ITypeEnvironmentBuilder typeEnv,
			String predicate, String modifiedPredicate) {
		final Predicate pred = genPred(typeEnv, predicate);
		final Predicate modifiedpred = genPred(typeEnv, modifiedPredicate);
		final RelationalPredicate rPred = (RelationalPredicate) pred;
		final Rule.Hypothesis<RelationalPredicate> hyp = new Rule.Hypothesis<RelationalPredicate>(
				rPred, ff);
		final Rule.SimpDomRanresIdLeft simp = new Rule.SimpDomRanresIdLeft(
				hyp);
		Assert.assertEquals(simp.getConsequent(), modifiedpred);
	}

	@Test
	public void simpDomRanresIdRight() {
		final ITypeEnvironmentBuilder typeEnv = mTypeEnvironment("A=ℙ(ℤ); f=ℤ↔ℤ");
		final String subseteq = "g⊆dom(id▷f)";
		applySimpDomRanresIdRight(typeEnv, subseteq, "g⊆f");
		final String subset = "g⊂dom(id▷f)";
		applySimpDomRanresIdRight(typeEnv, subset, "g⊂f");
	}

	private void applySimpDomRanresIdRight(final ITypeEnvironmentBuilder typeEnv,
			String predicate, String modifiedPredicate) {
		final Predicate pred = genPred(typeEnv, predicate);
		final Predicate modifiedpred = genPred(typeEnv, modifiedPredicate);
		final RelationalPredicate rPred = (RelationalPredicate) pred;
		final Rule.Hypothesis<RelationalPredicate> hyp = new Rule.Hypothesis<RelationalPredicate>(
				rPred, ff);
		final Rule.SimpDomRanresIdRight simp = new Rule.SimpDomRanresIdRight(
				hyp);
		Assert.assertEquals(simp.getConsequent(), modifiedpred);
	}

	@Test
	public void simpRanDomRanresPrj2Left() {
		final ITypeEnvironmentBuilder typeEnv = mTypeEnvironment("f=ℙ(ℤ)");
		final String subseteq = "ran(dom((prj2⦂ℤ×ℤ↔ℤ)▷f))⊆g";
		applySimpRanDomRanresPrj2Left(typeEnv, subseteq, "f⊆g");
		final String subset = "ran(dom((prj2⦂ℤ×ℤ↔ℤ)▷f))⊂g";
		applySimpRanDomRanresPrj2Left(typeEnv, subset, "f⊂g");
	}

	private void applySimpRanDomRanresPrj2Left(final ITypeEnvironmentBuilder typeEnv,
			String predicate, String modifiedPredicate) {
		final Predicate pred = genPred(typeEnv, predicate);
		final Predicate modifiedpred = genPred(typeEnv, modifiedPredicate);
		final RelationalPredicate rPred = (RelationalPredicate) pred;
		final Rule.Hypothesis<RelationalPredicate> hyp = new Rule.Hypothesis<RelationalPredicate>(
				rPred, ff);
		final Rule.SimpRanDomRanresPrj2Left simp = new Rule.SimpRanDomRanresPrj2Left(
				hyp);
		Assert.assertEquals(simp.getConsequent(), modifiedpred);
	}

	@Test
	public void simpRanDomRanresPrj2Right() {
		final ITypeEnvironmentBuilder typeEnv = mTypeEnvironment("f=ℙ(ℤ)");
		final String subseteq = "g⊆ran(dom((prj2⦂ℤ×ℤ↔ℤ)▷f))";
		applySimpRanDomRanresPrj2Right(typeEnv, subseteq, "g⊆f");
		final String subset = "g⊂ran(dom((prj2⦂ℤ×ℤ↔ℤ)▷f))";
		applySimpRanDomRanresPrj2Right(typeEnv, subset, "g⊂f");
	}

	private void applySimpRanDomRanresPrj2Right(final ITypeEnvironmentBuilder typeEnv,
			String predicate, String modifiedPredicate) {
		final Predicate pred = genPred(typeEnv, predicate);
		final Predicate modifiedpred = genPred(typeEnv, modifiedPredicate);
		final RelationalPredicate rPred = (RelationalPredicate) pred;
		final Rule.Hypothesis<RelationalPredicate> hyp = new Rule.Hypothesis<RelationalPredicate>(
				rPred, ff);
		final Rule.SimpRanDomRanresPrj2Right simp = new Rule.SimpRanDomRanresPrj2Right(
				hyp);
		Assert.assertEquals(simp.getConsequent(), modifiedpred);
	}

	@Test
	public void simpRanDomresKxxLeft() {
		final ITypeEnvironmentBuilder typeEnv = mTypeEnvironment("A=ℙ(ℤ); f=ℤ↔ℤ");
		final String subseteqPrj1 = "ran(f◁prj1)⊆A";
		applySimpRanDomresKxxLeft(typeEnv, subseteqPrj1, "dom(f)⊆A");
		final String subset = "ran(f◁prj1)⊂A";
		applySimpRanDomresKxxLeft(typeEnv, subset, "dom(f)⊂A");
		final String subseteqId = "ran(A◁id)⊆B";
		applySimpRanDomresKxxLeft(typeEnv, subseteqId, "A⊆B");
		final String subseteqPrj2 = "ran(f◁prj2)⊆A";
		applySimpRanDomresKxxLeft(typeEnv, subseteqPrj2, "ran(f)⊆A");
	}

	private void applySimpRanDomresKxxLeft(final ITypeEnvironmentBuilder typeEnv,
			String predicate, String modifiedPredicate) {
		final Predicate pred = genPred(typeEnv, predicate);
		final Predicate modifiedpred = genPred(typeEnv, modifiedPredicate);
		final RelationalPredicate rPred = (RelationalPredicate) pred;
		final Rule.Hypothesis<RelationalPredicate> hyp = new Rule.Hypothesis<RelationalPredicate>(
				rPred, ff);
		final Rule.SimpRanDomresKxxLeft simp = new Rule.SimpRanDomresKxxLeft(
				hyp);
		Assert.assertEquals(simp.getConsequent(), modifiedpred);
	}

	@Test
	public void simpRanDomresKxxRight() {
		final ITypeEnvironmentBuilder typeEnv = mTypeEnvironment("A=ℙ(ℤ); f=ℤ↔ℤ");
		final String subseteqPrj1 = "A⊆ran(f◁prj1)";
		applySimpRanDomresKxxRight(typeEnv, subseteqPrj1, "A⊆dom(f)");
		final String subset = "A⊂ran(f◁prj1)";
		applySimpRanDomresKxxRight(typeEnv, subset, "A⊂dom(f)");
		final String subseteqId = "B⊆ran(A◁id)";
		applySimpRanDomresKxxRight(typeEnv, subseteqId, "B⊆A");
		final String subseteqPrj2 = "A⊆ran(f◁prj2)";
		applySimpRanDomresKxxRight(typeEnv, subseteqPrj2, "A⊆ran(f)");
	}

	private void applySimpRanDomresKxxRight(final ITypeEnvironmentBuilder typeEnv,
			String predicate, String modifiedPredicate) {
		final Predicate pred = genPred(typeEnv, predicate);
		final Predicate modifiedpred = genPred(typeEnv, modifiedPredicate);
		final RelationalPredicate rPred = (RelationalPredicate) pred;
		final Rule.Hypothesis<RelationalPredicate> hyp = new Rule.Hypothesis<RelationalPredicate>(
				rPred, ff);
		final Rule.SimpRanDomresKxxRight simp = new Rule.SimpRanDomresKxxRight(
				hyp);
		Assert.assertEquals(simp.getConsequent(), modifiedpred);
	}

	@Test
	public void simpRanRanresLeft() {
		final ITypeEnvironmentBuilder typeEnv = mTypeEnvironment("A=ℙ(ℤ); f=ℤ↔ℤ");
		final String subseteq = "ran(f▷A)⊆B";
		applySimpRanRanresLeft(typeEnv, subseteq, "ran(f)∩A⊆B");
		final String subset = "ran(f▷A)⊂B";
		applySimpRanRanresLeft(typeEnv, subset, "ran(f)∩A⊂B");
		final String subseteqId = "ran(id▷A)⊆B";
		applySimpRanRanresLeft(typeEnv, subseteqId, "A⊆B");
		final String subseteqPrj1 = "ran((prj1⦂ℤ×ℤ↔ℤ)▷A)⊆B";
		applySimpRanRanresLeft(typeEnv, subseteqPrj1, "A⊆B");
		final String subseteqPrj2 = "ran((prj2⦂ℤ×ℤ↔ℤ)▷A)⊆B";
		applySimpRanRanresLeft(typeEnv, subseteqPrj2, "A⊆B");
	}

	private void applySimpRanRanresLeft(final ITypeEnvironmentBuilder typeEnv,
			String predicate, String modifiedPredicate) {
		final Predicate pred = genPred(typeEnv, predicate);
		final Predicate modifiedpred = genPred(typeEnv, modifiedPredicate);
		final RelationalPredicate rPred = (RelationalPredicate) pred;
		final Rule.Hypothesis<RelationalPredicate> hyp = new Rule.Hypothesis<RelationalPredicate>(
				rPred, ff);
		final Rule.SimpRanRanresLeft simp = new Rule.SimpRanRanresLeft(
				hyp);
		Assert.assertEquals(simp.getConsequent(), modifiedpred);
	}

	@Test
	public void simpRanRanresRight() {
		final ITypeEnvironmentBuilder typeEnv = mTypeEnvironment("A=ℙ(ℤ); f=ℤ↔ℤ");
		final String subseteq = "B⊆ran(f▷A)";
		applySimpRanRanresRight(typeEnv, subseteq, "B⊆ran(f)∩A");
		final String subset = "B⊂ran(f▷A)";
		applySimpRanRanresRight(typeEnv, subset, "B⊂ran(f)∩A");
		final String subseteqId = "B⊆ran(id▷A)";
		applySimpRanRanresRight(typeEnv, subseteqId, "B⊆A");
		final String subseteqPrj1 = "B⊆ran((prj1⦂ℤ×ℤ↔ℤ)▷A)";
		applySimpRanRanresRight(typeEnv, subseteqPrj1, "B⊆A");
		final String subseteqPrj2 = "B⊆ran((prj2⦂ℤ×ℤ↔ℤ)▷A)";
		applySimpRanRanresRight(typeEnv, subseteqPrj2, "B⊆A");
	}

	private void applySimpRanRanresRight(final ITypeEnvironmentBuilder typeEnv,
			String predicate, String modifiedPredicate) {
		final Predicate pred = genPred(typeEnv, predicate);
		final Predicate modifiedpred = genPred(typeEnv, modifiedPredicate);
		final RelationalPredicate rPred = (RelationalPredicate) pred;
		final Rule.Hypothesis<RelationalPredicate> hyp = new Rule.Hypothesis<RelationalPredicate>(
				rPred, ff);
		final Rule.SimpRanRanresRight simp = new Rule.SimpRanRanresRight(
				hyp);
		Assert.assertEquals(simp.getConsequent(), modifiedpred);
	}

	@Test
	public void simpRanRansubLeft() {
		final ITypeEnvironmentBuilder typeEnv = mTypeEnvironment("A=ℙ(ℤ); f=ℤ↔ℤ");
		final String subseteq = "ran(f⩥A)⊆B";
		applySimpRanRansubLeft(typeEnv, subseteq, "ran(f)∖A⊆B");
		final String subset = "ran(f⩥A)⊂B";
		applySimpRanRansubLeft(typeEnv, subset, "ran(f)∖A⊂B");
	}

	private void applySimpRanRansubLeft(final ITypeEnvironmentBuilder typeEnv,
			String predicate, String modifiedPredicate) {
		final Predicate pred = genPred(typeEnv, predicate);
		final Predicate modifiedpred = genPred(typeEnv, modifiedPredicate);
		final RelationalPredicate rPred = (RelationalPredicate) pred;
		final Rule.Hypothesis<RelationalPredicate> hyp = new Rule.Hypothesis<RelationalPredicate>(
				rPred, ff);
		final Rule.SimpRanRansubLeft simp = new Rule.SimpRanRansubLeft(
				hyp);
		Assert.assertEquals(simp.getConsequent(), modifiedpred);
	}

	@Test
	public void simpRanRansubRight() {
		final ITypeEnvironmentBuilder typeEnv = mTypeEnvironment("A=ℙ(ℤ); f=ℤ↔ℤ");
		final String subseteq = "B⊆ran(f⩥A)";
		applySimpRanRansubRight(typeEnv, subseteq, "B⊆ran(f)∖A");
		final String subset = "B⊂ran(f⩥A)";
		applySimpRanRansubRight(typeEnv, subset, "B⊂ran(f)∖A");
	}

	private void applySimpRanRansubRight(final ITypeEnvironmentBuilder typeEnv,
			String predicate, String modifiedPredicate) {
		final Predicate pred = genPred(typeEnv, predicate);
		final Predicate modifiedpred = genPred(typeEnv, modifiedPredicate);
		final RelationalPredicate rPred = (RelationalPredicate) pred;
		final Rule.Hypothesis<RelationalPredicate> hyp = new Rule.Hypothesis<RelationalPredicate>(
				rPred, ff);
		final Rule.SimpRanRansubRight simp = new Rule.SimpRanRansubRight(
				hyp);
		Assert.assertEquals(simp.getConsequent(), modifiedpred);
	}

	@Test
	public void composition() {
		final ITypeEnvironmentBuilder typeEnv = mTypeEnvironment("A=ℙ(ℤ)");
		final String member = "x∈A";
		final String incl1EQ = "A⊆B";
		final String incl1 = "A⊂B";
		final String incl2EQ = "B⊆C";
		final String incl2 = "B⊂C";
		applyComposition(typeEnv, member, incl1EQ, "x∈B");
		applyComposition(typeEnv, member, incl1, "x∈B");
		applyComposition(typeEnv, incl1EQ, incl2EQ, "A⊆C");
		applyComposition(typeEnv, incl1EQ, incl2, "A⊂C");
		applyComposition(typeEnv, incl1, incl2EQ, "A⊂C");
		applyComposition(typeEnv, incl1, incl2, "A⊂C");
	}

	private void applyComposition(final ITypeEnvironmentBuilder typeEnv,
			String memberStr, String subsetStr, String composedStr) {
		final Predicate member = genPred(typeEnv, memberStr);
		final Predicate subset = genPred(typeEnv, subsetStr);
		final Predicate composed = genPred(typeEnv, composedStr);
		final RelationalPredicate rMemb = (RelationalPredicate) member;
		final RelationalPredicate rSub = (RelationalPredicate) subset;
		final Rule.Hypothesis<RelationalPredicate> memberRule = new Rule.Hypothesis<RelationalPredicate>(
				rMemb, ff);
		final Rule.Hypothesis<RelationalPredicate> subsetRule = new Rule.Hypothesis<RelationalPredicate>(
				rSub, ff);
		final Rule<RelationalPredicate> composition = rf.compose(memberRule,
				subsetRule);
		Assert.assertEquals(composition.getConsequent(), composed);
	}

	@Test
	public void compositionOvrIncl() {
		final ITypeEnvironmentBuilder typeEnv = mTypeEnvironment("a=ℤ↔ℤ");
		final String subseteq = "z⊆a";
		final String subset = "z⊂a";
		final String ovreq = "abc⊆h";
		final String ovr = "abc⊂h";
		applyCompositionOvrIncl(typeEnv, subseteq, ovreq, "zbc⊆h");
		applyCompositionOvrIncl(typeEnv, subseteq, ovr, "zbc⊂h");
		applyCompositionOvrIncl(typeEnv, subset, ovreq, "zbc⊆h");
		applyCompositionOvrIncl(typeEnv, subset, ovr, "zbc⊂h");
	}

	private void applyCompositionOvrIncl(final ITypeEnvironmentBuilder typeEnv,
			String inclStr, String ovrStr, String composedStr) {
		final Predicate incl = genPred(typeEnv, inclStr);
		final Predicate ovr = genPred(typeEnv, ovrStr);
		final Predicate composed = genPred(typeEnv, composedStr);
		final RelationalPredicate rIncl = (RelationalPredicate) incl;
		final RelationalPredicate rOvr = (RelationalPredicate) ovr;
		final Rule.Hypothesis<RelationalPredicate> inclRule = new Rule.Hypothesis<RelationalPredicate>(
				rIncl, ff);
		final Rule.Hypothesis<RelationalPredicate> inclOvrRule = new Rule.Hypothesis<RelationalPredicate>(
				rOvr, ff);
		final Rule.CompositionOvrIncl compositionOvrIncl = new Rule.CompositionOvrIncl(
				inclRule, inclOvrRule);
		Assert.assertEquals(compositionOvrIncl.getConsequent(), composed);
	}

	@Test
	public void compositionOvrCont() {
		final ITypeEnvironmentBuilder typeEnv = mTypeEnvironment("a=ℤ↔ℤ; x=ℤ×ℤ");
		final String memb = "x∈abc";
		final String ovreq = "h⊆abc";
		final String ovr = "h⊂abc";
		final String subseteq = "a⊆z";
		final String subset = "a⊂z";
		applyCompositionOvrCont(typeEnv, ovreq, subseteq, "h⊆zbc");
		applyCompositionOvrCont(typeEnv, ovr, subseteq, "h⊂zbc");
		applyCompositionOvrCont(typeEnv, memb, subseteq, "x∈zbc");
		applyCompositionOvrCont(typeEnv, ovreq, subset, "h⊆zbc");
		applyCompositionOvrCont(typeEnv, ovr, subset, "h⊂zbc");
		applyCompositionOvrCont(typeEnv, memb, subset, "x∈zbc");
	}

	private void applyCompositionOvrCont(final ITypeEnvironmentBuilder typeEnv,
			String ovrStr, String inclStr, String composedStr) {
		final Predicate incl = genPred(typeEnv, inclStr);
		final Predicate ovr = genPred(typeEnv, ovrStr);
		final Predicate composed = genPred(typeEnv, composedStr);
		final RelationalPredicate rIncl = (RelationalPredicate) incl;
		final RelationalPredicate rOvr = (RelationalPredicate) ovr;
		final Rule.Hypothesis<RelationalPredicate> inclRule = new Rule.Hypothesis<RelationalPredicate>(
				rIncl, ff);
		final Rule.Hypothesis<RelationalPredicate> inclOvrRule = new Rule.Hypothesis<RelationalPredicate>(
				rOvr, ff);
		final Rule.CompositionOvrCont compositionOvrCont = new Rule.CompositionOvrCont(
				inclOvrRule, inclRule);
		Assert.assertEquals(compositionOvrCont.getConsequent(), composed);
	}

	@Test
	public void compositionBunionIncl() {
		final ITypeEnvironmentBuilder typeEnv = mTypeEnvironment("Z=ℤ↔ℤ; A=ℤ↔ℤ");
		final String unioneq = "A∪B∪C⊆D";
		final String union = "A∪B∪C⊂D";
		final String subseteqA = "Z⊆A";
		final String subsetA = "Z⊂A";
		applyCompositionBunionIncl(typeEnv, subseteqA, unioneq, "Z∪B∪C⊆D");
		applyCompositionBunionIncl(typeEnv, subseteqA, union, "Z∪B∪C⊂D");
		applyCompositionBunionIncl(typeEnv, subsetA, unioneq, "Z∪B∪C⊆D");
		applyCompositionBunionIncl(typeEnv, subsetA, union, "Z∪B∪C⊂D");
		final String subseteqB = "Z⊆B";
		final String subsetB = "Z⊂B";
		applyCompositionBunionIncl(typeEnv, subseteqB, unioneq, "A∪Z∪C⊆D");
		applyCompositionBunionIncl(typeEnv, subseteqB, union, "A∪Z∪C⊂D");
		applyCompositionBunionIncl(typeEnv, subsetB, unioneq, "A∪Z∪C⊆D");
		applyCompositionBunionIncl(typeEnv, subsetB, union, "A∪Z∪C⊂D");
		final String subseteqC = "Z⊆C";
		final String subsetC = "Z⊂C";
		applyCompositionBunionIncl(typeEnv, subseteqC, unioneq, "A∪B∪Z⊆D");
		applyCompositionBunionIncl(typeEnv, subseteqC, union, "A∪B∪Z⊂D");
		applyCompositionBunionIncl(typeEnv, subsetC, unioneq, "A∪B∪Z⊆D");
		applyCompositionBunionIncl(typeEnv, subsetC, union, "A∪B∪Z⊂D");
	}

	private void applyCompositionBunionIncl(final ITypeEnvironmentBuilder typeEnv,
			String inclStr, String unionStr, String composedStr) {
		final Predicate incl = genPred(typeEnv, inclStr);
		final Predicate union = genPred(typeEnv, unionStr);
		final Predicate composed = genPred(typeEnv, composedStr);
		final RelationalPredicate rIncl = (RelationalPredicate) incl;
		final RelationalPredicate rUnion = (RelationalPredicate) union;
		final Rule.Hypothesis<RelationalPredicate> inclRule = new Rule.Hypothesis<RelationalPredicate>(
				rIncl, ff);
		final Rule.Hypothesis<RelationalPredicate> inclUnionRule = new Rule.Hypothesis<RelationalPredicate>(
				rUnion, ff);
		final Rule.CompositionBunionIncl compositionBunion = new Rule.CompositionBunionIncl(
				inclRule, inclUnionRule);
		Assert.assertEquals(compositionBunion.getConsequent(), composed);
	}

	@Test
	public void compositionBunionCont() {
		final ITypeEnvironmentBuilder typeEnv = mTypeEnvironment("Z=ℤ↔ℤ; A=ℤ↔ℤ; x=ℤ×ℤ");
		final String memb = "x∈B∪C∪D";
		final String unioneq = "A⊆B∪C∪D";
		final String union = "A⊂B∪C∪D";
		final String subseteqB = "B⊆Z";
		final String subsetB = "B⊂Z";
		applyCompositionBunionCont(typeEnv, unioneq, subseteqB, "A⊆Z∪C∪D");
		applyCompositionBunionCont(typeEnv, union, subseteqB, "A⊂Z∪C∪D");
		applyCompositionBunionCont(typeEnv, memb, subseteqB, "x∈Z∪C∪D");
		applyCompositionBunionCont(typeEnv, unioneq, subsetB, "A⊆Z∪C∪D");
		applyCompositionBunionCont(typeEnv, union, subsetB, "A⊂Z∪C∪D");
		applyCompositionBunionCont(typeEnv, memb, subsetB, "x∈Z∪C∪D");
		final String subseteqC = "C⊆Z";
		final String subsetC = "C⊂Z";
		applyCompositionBunionCont(typeEnv, unioneq, subseteqC, "A⊆B∪Z∪D");
		applyCompositionBunionCont(typeEnv, union, subseteqC, "A⊂B∪Z∪D");
		applyCompositionBunionCont(typeEnv, memb, subseteqC, "x∈B∪Z∪D");
		applyCompositionBunionCont(typeEnv, unioneq, subsetC, "A⊆B∪Z∪D");
		applyCompositionBunionCont(typeEnv, union, subsetC, "A⊂B∪Z∪D");
		applyCompositionBunionCont(typeEnv, memb, subsetC, "x∈B∪Z∪D");
		final String subseteqD = "D⊆Z";
		final String subsetD = "D⊂Z";
		applyCompositionBunionCont(typeEnv, unioneq, subseteqD, "A⊆B∪C∪Z");
		applyCompositionBunionCont(typeEnv, union, subseteqD, "A⊂B∪C∪Z");
		applyCompositionBunionCont(typeEnv, memb, subseteqD, "x∈B∪C∪Z");
		applyCompositionBunionCont(typeEnv, unioneq, subsetD, "A⊆B∪C∪Z");
		applyCompositionBunionCont(typeEnv, union, subsetD, "A⊂B∪C∪Z");
		applyCompositionBunionCont(typeEnv, memb, subsetD, "x∈B∪C∪Z");
	}

	private void applyCompositionBunionCont(final ITypeEnvironmentBuilder typeEnv,
			String inclStr, String unionStr, String composedStr) {
		final Predicate incl = genPred(typeEnv, inclStr);
		final Predicate union = genPred(typeEnv, unionStr);
		final Predicate composed = genPred(typeEnv, composedStr);
		final RelationalPredicate rIncl = (RelationalPredicate) incl;
		final RelationalPredicate rUnion = (RelationalPredicate) union;
		final Rule.Hypothesis<RelationalPredicate> inclRule = new Rule.Hypothesis<RelationalPredicate>(
				rIncl, ff);
		final Rule.Hypothesis<RelationalPredicate> inclUnionRule = new Rule.Hypothesis<RelationalPredicate>(
				rUnion, ff);
		final Rule.CompositionBunionCont compositionBunion = new Rule.CompositionBunionCont(
				inclUnionRule, inclRule);
		Assert.assertEquals(compositionBunion.getConsequent(), composed);
	}

	@Test
	public void compositionBinterIncl() {
		final ITypeEnvironmentBuilder typeEnv = mTypeEnvironment("Z=ℤ↔ℤ; A=ℤ↔ℤ");
		final String intereq = "A∩B∩C⊆D";
		final String inter = "A∩B∩C⊂D";
		final String subseteqA = "Z⊆A";
		final String subsetA = "Z⊂A";
		applyCompositionBinterIncl(typeEnv, subseteqA, intereq, "Z∩B∩C⊆D");
		applyCompositionBinterIncl(typeEnv, subseteqA, inter, "Z∩B∩C⊂D");
		applyCompositionBinterIncl(typeEnv, subsetA, intereq, "Z∩B∩C⊆D");
		applyCompositionBinterIncl(typeEnv, subsetA, inter, "Z∩B∩C⊂D");
		final String subseteqB = "Z⊆B";
		final String subsetB = "Z⊂B";
		applyCompositionBinterIncl(typeEnv, subseteqB, intereq, "A∩Z∩C⊆D");
		applyCompositionBinterIncl(typeEnv, subseteqB, inter, "A∩Z∩C⊂D");
		applyCompositionBinterIncl(typeEnv, subsetB, intereq, "A∩Z∩C⊆D");
		applyCompositionBinterIncl(typeEnv, subsetB, inter, "A∩Z∩C⊂D");
		final String subseteqC = "Z⊆C";
		final String subsetC = "Z⊂C";
		applyCompositionBinterIncl(typeEnv, subseteqC, intereq, "A∩B∩Z⊆D");
		applyCompositionBinterIncl(typeEnv, subseteqC, inter, "A∩B∩Z⊂D");
		applyCompositionBinterIncl(typeEnv, subsetC, intereq, "A∩B∩Z⊆D");
		applyCompositionBinterIncl(typeEnv, subsetC, inter, "A∩B∩Z⊂D");
	}

	private void applyCompositionBinterIncl(final ITypeEnvironmentBuilder typeEnv,
			String inclStr, String unionStr, String composedStr) {
		final Predicate incl = genPred(typeEnv, inclStr);
		final Predicate union = genPred(typeEnv, unionStr);
		final Predicate composed = genPred(typeEnv, composedStr);
		final RelationalPredicate rIncl = (RelationalPredicate) incl;
		final RelationalPredicate rInter = (RelationalPredicate) union;
		final Rule.Hypothesis<RelationalPredicate> inclRule = new Rule.Hypothesis<RelationalPredicate>(
				rIncl, ff);
		final Rule.Hypothesis<RelationalPredicate> inclInterRule = new Rule.Hypothesis<RelationalPredicate>(
				rInter, ff);
		final Rule.CompositionBinterIncl compositionBinter = new Rule.CompositionBinterIncl(
				inclRule, inclInterRule);
		Assert.assertEquals(compositionBinter.getConsequent(), composed);
	}

	@Test
	public void compositionBinterCont() {
		final ITypeEnvironmentBuilder typeEnv = mTypeEnvironment("Z=ℤ↔ℤ; A=ℤ↔ℤ; x=ℤ×ℤ");
		final String memb = "x∈B∩C∩D";
		final String intereq = "A⊆B∩C∩D";
		final String inter = "A⊂B∩C∩D";
		final String subseteqA = "B⊆Z";
		final String subsetA = "B⊂Z";
		applyCompositionBinterCont(typeEnv, intereq, subseteqA, "A⊆Z∩C∩D");
		applyCompositionBinterCont(typeEnv, inter, subseteqA, "A⊂Z∩C∩D");
		applyCompositionBinterCont(typeEnv, memb, subseteqA, "x∈Z∩C∩D");
		applyCompositionBinterCont(typeEnv, intereq, subsetA, "A⊆Z∩C∩D");
		applyCompositionBinterCont(typeEnv, inter, subsetA, "A⊂Z∩C∩D");
		applyCompositionBinterCont(typeEnv, memb, subsetA, "x∈Z∩C∩D");
		final String subseteqB = "C⊆Z";
		final String subsetB = "C⊂Z";
		applyCompositionBinterCont(typeEnv, intereq, subseteqB, "A⊆B∩Z∩D");
		applyCompositionBinterCont(typeEnv, inter, subseteqB, "A⊂B∩Z∩D");
		applyCompositionBinterCont(typeEnv, memb, subseteqB, "x∈B∩Z∩D");
		applyCompositionBinterCont(typeEnv, intereq, subsetB, "A⊆B∩Z∩D");
		applyCompositionBinterCont(typeEnv, inter, subsetB, "A⊂B∩Z∩D");
		applyCompositionBinterCont(typeEnv, memb, subsetB, "x∈B∩Z∩D");
		final String subseteqC = "D⊆Z";
		final String subsetC = "D⊂Z";
		applyCompositionBinterCont(typeEnv, intereq, subseteqC, "A⊆B∩C∩Z");
		applyCompositionBinterCont(typeEnv, inter, subseteqC, "A⊂B∩C∩Z");
		applyCompositionBinterCont(typeEnv, memb, subseteqC, "x∈B∩C∩Z");
		applyCompositionBinterCont(typeEnv, intereq, subsetC, "A⊆B∩C∩Z");
		applyCompositionBinterCont(typeEnv, inter, subsetC, "A⊂B∩C∩Z");
		applyCompositionBinterCont(typeEnv, memb, subsetC, "x∈B∩C∩Z");
	}

	private void applyCompositionBinterCont(final ITypeEnvironmentBuilder typeEnv,
			String interStr, String inclStr, String composedStr) {
		final Predicate incl = genPred(typeEnv, inclStr);
		final Predicate inter = genPred(typeEnv, interStr);
		final Predicate composed = genPred(typeEnv, composedStr);
		final RelationalPredicate rIncl = (RelationalPredicate) incl;
		final RelationalPredicate rInter = (RelationalPredicate) inter;
		final Rule.Hypothesis<RelationalPredicate> inclRule = new Rule.Hypothesis<RelationalPredicate>(
				rIncl, ff);
		final Rule.Hypothesis<RelationalPredicate> inclInterRule = new Rule.Hypothesis<RelationalPredicate>(
				rInter, ff);
		final Rule.CompositionBinterCont compositionBinter = new Rule.CompositionBinterCont(
				inclInterRule, inclRule);
		Assert.assertEquals(compositionBinter.getConsequent(), composed);
	}

	@Test
	public void compositionCProdLeftIncl() {
		final ITypeEnvironmentBuilder typeEnv = mTypeEnvironment("B=ℙ(ℤ); C=ℙ(ℤ)");
		final String subseteqCprod = "B×C⊆Z";
		final String subsetCprod = "B×C⊂Z";
		final String subseteq = "A⊆B";
		final String subset = "A⊂B";
		applyCompositionCProdLeftIncl(typeEnv, subseteq, subseteqCprod, "A×C⊆Z");
		applyCompositionCProdLeftIncl(typeEnv, subseteq, subsetCprod, "A×C⊂Z");
		applyCompositionCProdLeftIncl(typeEnv, subset, subseteqCprod, "A×C⊆Z");
		applyCompositionCProdLeftIncl(typeEnv, subset, subsetCprod, "A×C⊂Z");
	}

	private void applyCompositionCProdLeftIncl(final ITypeEnvironmentBuilder typeEnv,
			String inclStr, String inclCprodStr, String composedStr) {
		final Predicate incl = genPred(typeEnv, inclStr);
		final Predicate inclCprod = genPred(typeEnv, inclCprodStr);
		final Predicate composed = genPred(typeEnv, composedStr);
		final RelationalPredicate rIncl = (RelationalPredicate) incl;
		final RelationalPredicate rInclCprod = (RelationalPredicate) inclCprod;
		final Rule.Hypothesis<RelationalPredicate> inclRule = new Rule.Hypothesis<RelationalPredicate>(
				rIncl, ff);
		final Rule.Hypothesis<RelationalPredicate> inclCprodRule = new Rule.Hypothesis<RelationalPredicate>(
				rInclCprod, ff);
		final Rule.CompositionCProdLeftIncl compositionCprod = new Rule.CompositionCProdLeftIncl(
				inclRule, inclCprodRule);
		Assert.assertEquals(compositionCprod.getConsequent(), composed);
	}

	@Test
	public void compositionCProdRightIncl() {
		final ITypeEnvironmentBuilder typeEnv = mTypeEnvironment("B=ℙ(ℤ); C=ℙ(ℤ)");
		final String subseteqCprod = "B×C⊆Z";
		final String subsetCprod = "B×C⊂Z";
		final String subseteq = "A⊆C";
		final String subset = "A⊂C";
		applyCompositionCProdRightIncl(typeEnv, subseteq, subseteqCprod,
				"B×A⊆Z");
		applyCompositionCProdRightIncl(typeEnv, subseteq, subsetCprod, "B×A⊂Z");
		applyCompositionCProdRightIncl(typeEnv, subset, subseteqCprod, "B×A⊆Z");
		applyCompositionCProdRightIncl(typeEnv, subset, subsetCprod, "B×A⊂Z");
	}

	private void applyCompositionCProdRightIncl(final ITypeEnvironmentBuilder typeEnv,
			String inclStr, String inclCprodStr, String composedStr) {
		final Predicate incl = genPred(typeEnv, inclStr);
		final Predicate inclCprod = genPred(typeEnv, inclCprodStr);
		final Predicate composed = genPred(typeEnv, composedStr);
		final RelationalPredicate rIncl = (RelationalPredicate) incl;
		final RelationalPredicate rInclCprod = (RelationalPredicate) inclCprod;
		final Rule.Hypothesis<RelationalPredicate> inclRule = new Rule.Hypothesis<RelationalPredicate>(
				rIncl, ff);
		final Rule.Hypothesis<RelationalPredicate> inclCprodRule = new Rule.Hypothesis<RelationalPredicate>(
				rInclCprod, ff);
		final Rule.CompositionCProdRightIncl compositionCprod = new Rule.CompositionCProdRightIncl(
				inclRule, inclCprodRule);
		Assert.assertEquals(compositionCprod.getConsequent(), composed);
	}

	@Test
	public void compositionCProdLeftCont() {
		final ITypeEnvironmentBuilder typeEnv = mTypeEnvironment("B=ℙ(ℤ); C=ℙ(ℤ); x=ℤ×ℤ");
		final String membCprod = "x∈B×C";
		final String subseteqCprod = "Z⊆B×C";
		final String subsetCprod = "Z⊂B×C";
		final String subseteq = "B⊆A";
		final String subset = "B⊂A";
		applyCompositionCProdLeftInCont(typeEnv, subseteqCprod, subseteq,
				"Z⊆A×C");
		applyCompositionCProdLeftInCont(typeEnv, subsetCprod, subseteq, "Z⊂A×C");
		applyCompositionCProdLeftInCont(typeEnv, membCprod, subseteq, "x∈A×C");
		applyCompositionCProdLeftInCont(typeEnv, subseteqCprod, subset, "Z⊆A×C");
		applyCompositionCProdLeftInCont(typeEnv, subsetCprod, subset, "Z⊂A×C");
		applyCompositionCProdLeftInCont(typeEnv, membCprod, subset, "x∈A×C");
	}

	private void applyCompositionCProdLeftInCont(
			final ITypeEnvironmentBuilder typeEnv, String inclCprodStr,
			String inclStr, String composedStr) {
		final Predicate incl = genPred(typeEnv, inclStr);
		final Predicate inclCprod = genPred(typeEnv, inclCprodStr);
		final Predicate composed = genPred(typeEnv, composedStr);
		final RelationalPredicate rIncl = (RelationalPredicate) incl;
		final RelationalPredicate rInclCprod = (RelationalPredicate) inclCprod;
		final Rule.Hypothesis<RelationalPredicate> inclRule = new Rule.Hypothesis<RelationalPredicate>(
				rIncl, ff);
		final Rule.Hypothesis<RelationalPredicate> inclCprodRule = new Rule.Hypothesis<RelationalPredicate>(
				rInclCprod, ff);
		final Rule.CompositionCProdLeftCont compositionCprod = new Rule.CompositionCProdLeftCont(
				inclCprodRule, inclRule);
		Assert.assertEquals(compositionCprod.getConsequent(), composed);
	}

	@Test
	public void compositionCProdRightCont() {
		final ITypeEnvironmentBuilder typeEnv = mTypeEnvironment("B=ℙ(ℤ); C=ℙ(ℤ); x=ℤ×ℤ");
		final String membCprod = "x∈B×C";
		final String subseteqCprod = "Z⊆B×C";
		final String subsetCprod = "Z⊂B×C";
		final String subseteq = "C⊆A";
		final String subset = "C⊂A";
		applyCompositionCProdRightCont(typeEnv, subseteqCprod, subseteq,
				"Z⊆B×A");
		applyCompositionCProdRightCont(typeEnv, subsetCprod, subseteq, "Z⊂B×A");
		applyCompositionCProdRightCont(typeEnv, membCprod, subseteq, "x∈B×A");
		applyCompositionCProdRightCont(typeEnv, subseteqCprod, subset, "Z⊆B×A");
		applyCompositionCProdRightCont(typeEnv, subsetCprod, subset, "Z⊂B×A");
		applyCompositionCProdRightCont(typeEnv, membCprod, subset, "x∈B×A");
	}

	private void applyCompositionCProdRightCont(final ITypeEnvironmentBuilder typeEnv,
			String inclCprodStr, String inclStr, String composedStr) {
		final Predicate incl = genPred(typeEnv, inclStr);
		final Predicate inclCprod = genPred(typeEnv, inclCprodStr);
		final Predicate composed = genPred(typeEnv, composedStr);
		final RelationalPredicate rIncl = (RelationalPredicate) incl;
		final RelationalPredicate rInclCprod = (RelationalPredicate) inclCprod;
		final Rule.Hypothesis<RelationalPredicate> inclRule = new Rule.Hypothesis<RelationalPredicate>(
				rIncl, ff);
		final Rule.Hypothesis<RelationalPredicate> inclCprodRule = new Rule.Hypothesis<RelationalPredicate>(
				rInclCprod, ff);
		final Rule.CompositionCProdRightCont compositionCprod = new Rule.CompositionCProdRightCont(
				inclRule, inclCprodRule);
		Assert.assertEquals(compositionCprod.getConsequent(), composed);
	}

	@Test
	public void compositionSetminusLeftIncl() {
		final ITypeEnvironmentBuilder typeEnv = mTypeEnvironment("f=ℙ(ℤ)");
		final String setminusEQ = "f∖g⊆A";
		final String setminus = "f∖g⊂A";
		final String subseteq = "e⊆f";
		final String subset = "e⊂f";
		applyCompositionSetminusLeftIncl(typeEnv, setminusEQ, subseteq, "e∖g⊆A");
		applyCompositionSetminusLeftIncl(typeEnv, setminus, subseteq, "e∖g⊂A");
		applyCompositionSetminusLeftIncl(typeEnv, setminusEQ, subset, "e∖g⊆A");
		applyCompositionSetminusLeftIncl(typeEnv, setminus, subset, "e∖g⊂A");
	}

	private void applyCompositionSetminusLeftIncl(
			final ITypeEnvironmentBuilder typeEnv, String setminusStr, String inclStr,
			String composedStr) {
		final Predicate incl = genPred(typeEnv, inclStr);
		final Predicate setminus = genPred(typeEnv, setminusStr);
		final Predicate composed = genPred(typeEnv, composedStr);
		final RelationalPredicate rIncl = (RelationalPredicate) incl;
		final RelationalPredicate rSetminus = (RelationalPredicate) setminus;
		final Rule.Hypothesis<RelationalPredicate> inclRule = new Rule.Hypothesis<RelationalPredicate>(
				rIncl, ff);
		final Rule.Hypothesis<RelationalPredicate> setminusRule = new Rule.Hypothesis<RelationalPredicate>(
				rSetminus, ff);
		final Rule.CompositionSetminusLeftIncl composition = new Rule.CompositionSetminusLeftIncl(
				inclRule, setminusRule);
		Assert.assertEquals(composition.getConsequent(), composed);
	}

	@Test
	public void compositionSetminusRightIncl() {
		final ITypeEnvironmentBuilder typeEnv = mTypeEnvironment("g=ℙ(ℤ)");
		final String setminusEQ = "f∖g⊆A";
		final String setminus = "f∖g⊂A";
		final String subseteq = "g⊆h";
		final String subset = "g⊂h";
		applyCompositionSetminusRightIncl(typeEnv, setminusEQ, subseteq,
				"f∖h⊆A");
		applyCompositionSetminusRightIncl(typeEnv, setminus, subseteq, "f∖h⊂A");
		applyCompositionSetminusRightIncl(typeEnv, setminusEQ, subset, "f∖h⊆A");
		applyCompositionSetminusRightIncl(typeEnv, setminus, subset, "f∖h⊂A");
	}

	private void applyCompositionSetminusRightIncl(
			final ITypeEnvironmentBuilder typeEnv, String setminusStr, String inclStr,
			String composedStr) {
		final Predicate incl = genPred(typeEnv, inclStr);
		final Predicate setminus = genPred(typeEnv, setminusStr);
		final Predicate composed = genPred(typeEnv, composedStr);
		final RelationalPredicate rIncl = (RelationalPredicate) incl;
		final RelationalPredicate rSetminus = (RelationalPredicate) setminus;
		final Rule.Hypothesis<RelationalPredicate> inclRule = new Rule.Hypothesis<RelationalPredicate>(
				rIncl, ff);
		final Rule.Hypothesis<RelationalPredicate> setminusRule = new Rule.Hypothesis<RelationalPredicate>(
				rSetminus, ff);
		final Rule.CompositionSetminusRightIncl composition = new Rule.CompositionSetminusRightIncl(
				setminusRule, inclRule);
		Assert.assertEquals(composition.getConsequent(), composed);
	}

	@Test
	public void compositionSetminusLeftCont() {
		final ITypeEnvironmentBuilder typeEnv = mTypeEnvironment("f=ℙ(ℤ); x=ℤ");
		final String memb = "x∈f∖h";
		final String setminusEQ = "A⊆f∖h";
		final String setminus = "A⊂f∖h";
		final String subseteq = "f⊆g";
		final String subset = "f⊂g";
		applyCompositionSetminusLeftCont(typeEnv, setminusEQ, subseteq, "A⊆g∖h");
		applyCompositionSetminusLeftCont(typeEnv, setminus, subseteq, "A⊂g∖h");
		applyCompositionSetminusLeftCont(typeEnv, memb, subseteq, "x∈g∖h");
		applyCompositionSetminusLeftCont(typeEnv, setminusEQ, subset, "A⊆g∖h");
		applyCompositionSetminusLeftCont(typeEnv, setminus, subset, "A⊂g∖h");
		applyCompositionSetminusLeftCont(typeEnv, memb, subset, "x∈g∖h");
	}

	private void applyCompositionSetminusLeftCont(
			final ITypeEnvironmentBuilder typeEnv, String setminusStr, String inclStr,
			String composedStr) {
		final Predicate incl = genPred(typeEnv, inclStr);
		final Predicate setminus = genPred(typeEnv, setminusStr);
		final Predicate composed = genPred(typeEnv, composedStr);
		final RelationalPredicate rIncl = (RelationalPredicate) incl;
		final RelationalPredicate rSetminus = (RelationalPredicate) setminus;
		final Rule.Hypothesis<RelationalPredicate> inclRule = new Rule.Hypothesis<RelationalPredicate>(
				rIncl, ff);
		final Rule.Hypothesis<RelationalPredicate> setminusRule = new Rule.Hypothesis<RelationalPredicate>(
				rSetminus, ff);
		final Rule.CompositionSetminusLeftCont composition = new Rule.CompositionSetminusLeftCont(
				setminusRule, inclRule);
		Assert.assertEquals(composition.getConsequent(), composed);
	}

	@Test
	public void compositionSetminusRightCont() {
		final ITypeEnvironmentBuilder typeEnv = mTypeEnvironment("h=ℙ(ℤ); x=ℤ");
		final String memb = "x∈f∖h";
		final String setminusEQ = "A⊆f∖h";
		final String setminus = "A⊂f∖h";
		final String subseteq = "g⊆h";
		final String subset = "g⊂h";
		applyCompositionSetminusRightCont(typeEnv, setminusEQ, subseteq,
				"A⊆f∖g");
		applyCompositionSetminusRightCont(typeEnv, setminus, subseteq, "A⊂f∖g");
		applyCompositionSetminusRightCont(typeEnv, memb, subseteq, "x∈f∖g");
		applyCompositionSetminusRightCont(typeEnv, setminusEQ, subset, "A⊆f∖g");
		applyCompositionSetminusRightCont(typeEnv, setminus, subset, "A⊂f∖g");
		applyCompositionSetminusRightCont(typeEnv, memb, subset, "x∈f∖g");
	}

	private void applyCompositionSetminusRightCont(
			final ITypeEnvironmentBuilder typeEnv, String setminusStr, String inclStr,
			String composedStr) {
		final Predicate incl = genPred(typeEnv, inclStr);
		final Predicate setminus = genPred(typeEnv, setminusStr);
		final Predicate composed = genPred(typeEnv, composedStr);
		final RelationalPredicate rIncl = (RelationalPredicate) incl;
		final RelationalPredicate rSetminus = (RelationalPredicate) setminus;
		final Rule.Hypothesis<RelationalPredicate> inclRule = new Rule.Hypothesis<RelationalPredicate>(
				rIncl, ff);
		final Rule.Hypothesis<RelationalPredicate> setminusRule = new Rule.Hypothesis<RelationalPredicate>(
				rSetminus, ff);
		final Rule.CompositionSetminusRightCont composition = new Rule.CompositionSetminusRightCont(
				inclRule, setminusRule);
		Assert.assertEquals(composition.getConsequent(), composed);
	}

	@Test
	public void compositionRanresLeftIncl() {
		final ITypeEnvironmentBuilder typeEnv = mTypeEnvironment("f=ℤ↔ℤ");
		final String ranresEQ = "f▷B⊆g";
		final String ranres = "f▷B⊂g";
		final String subseteq = "e⊆f";
		final String subset = "e⊂f";
		applyCompositionRanresLeftIncl(typeEnv, ranresEQ, subseteq, "e▷B⊆g");
		applyCompositionRanresLeftIncl(typeEnv, ranres, subseteq, "e▷B⊂g");
		applyCompositionRanresLeftIncl(typeEnv, ranresEQ, subset, "e▷B⊆g");
		applyCompositionRanresLeftIncl(typeEnv, ranres, subset, "e▷B⊂g");
	}

	private void applyCompositionRanresLeftIncl(final ITypeEnvironmentBuilder typeEnv,
			String ranresStr, String inclStr, String composedStr) {
		final Predicate incl = genPred(typeEnv, inclStr);
		final Predicate ranres = genPred(typeEnv, ranresStr);
		final Predicate composed = genPred(typeEnv, composedStr);
		final RelationalPredicate rIncl = (RelationalPredicate) incl;
		final RelationalPredicate rRanres = (RelationalPredicate) ranres;
		final Rule.Hypothesis<RelationalPredicate> inclRule = new Rule.Hypothesis<RelationalPredicate>(
				rIncl, ff);
		final Rule.Hypothesis<RelationalPredicate> ranresRule = new Rule.Hypothesis<RelationalPredicate>(
				rRanres, ff);
		final Rule.CompositionRanresLeftIncl composition = new Rule.CompositionRanresLeftIncl(
				inclRule, ranresRule);
		Assert.assertEquals(composition.getConsequent(), composed);
	}

	@Test
	public void compositionRanresRightIncl() {
		final ITypeEnvironmentBuilder typeEnv = mTypeEnvironment("f=ℤ↔ℤ; B=ℙ(ℤ)");
		final String ranresEQ = "f▷B⊆g";
		final String ranres = "f▷B⊂g";
		final String subseteq = "A⊆B";
		final String subset = "A⊂B";
		applyCompositionRanresRightIncl(typeEnv, ranresEQ, subseteq, "f▷A⊆g");
		applyCompositionRanresRightIncl(typeEnv, ranres, subseteq, "f▷A⊂g");
		applyCompositionRanresRightIncl(typeEnv, ranresEQ, subset, "f▷A⊆g");
		applyCompositionRanresRightIncl(typeEnv, ranres, subset, "f▷A⊂g");
	}

	private void applyCompositionRanresRightIncl(
			final ITypeEnvironmentBuilder typeEnv, String ranresStr, String inclStr,
			String composedStr) {
		final Predicate incl = genPred(typeEnv, inclStr);
		final Predicate ranres = genPred(typeEnv, ranresStr);
		final Predicate composed = genPred(typeEnv, composedStr);
		final RelationalPredicate rIncl = (RelationalPredicate) incl;
		final RelationalPredicate rRanres = (RelationalPredicate) ranres;
		final Rule.Hypothesis<RelationalPredicate> inclRule = new Rule.Hypothesis<RelationalPredicate>(
				rIncl, ff);
		final Rule.Hypothesis<RelationalPredicate> ranresRule = new Rule.Hypothesis<RelationalPredicate>(
				rRanres, ff);
		final Rule.CompositionRanresRightIncl composition = new Rule.CompositionRanresRightIncl(
				inclRule, ranresRule);
		Assert.assertEquals(composition.getConsequent(), composed);
	}

	@Test
	public void compositionRanresLeftCont() {
		final ITypeEnvironmentBuilder typeEnv = mTypeEnvironment("g=ℤ↔ℤ; x=ℤ×ℤ");
		final String memb = "x∈g▷A";
		final String ranresEQ = "f⊆g▷A";
		final String ranres = "f⊂g▷A";
		final String subseteq = "g⊆h";
		final String subset = "g⊂h";
		applyCompositionRanresLeftCont(typeEnv, ranresEQ, subseteq, "f⊆h▷A");
		applyCompositionRanresLeftCont(typeEnv, ranres, subseteq, "f⊂h▷A");
		applyCompositionRanresLeftCont(typeEnv, memb, subseteq, "x∈h▷A");
		applyCompositionRanresLeftCont(typeEnv, ranresEQ, subset, "f⊆h▷A");
		applyCompositionRanresLeftCont(typeEnv, ranres, subset, "f⊂h▷A");
		applyCompositionRanresLeftCont(typeEnv, memb, subset, "x∈h▷A");
	}

	private void applyCompositionRanresLeftCont(final ITypeEnvironmentBuilder typeEnv,
			String ranresStr, String inclStr, String composedStr) {
		final Predicate incl = genPred(typeEnv, inclStr);
		final Predicate ranres = genPred(typeEnv, ranresStr);
		final Predicate composed = genPred(typeEnv, composedStr);
		final RelationalPredicate rIncl = (RelationalPredicate) incl;
		final RelationalPredicate rRanres = (RelationalPredicate) ranres;
		final Rule.Hypothesis<RelationalPredicate> inclRule = new Rule.Hypothesis<RelationalPredicate>(
				rIncl, ff);
		final Rule.Hypothesis<RelationalPredicate> ranresRule = new Rule.Hypothesis<RelationalPredicate>(
				rRanres, ff);
		final Rule.CompositionRanresLeftCont composition = new Rule.CompositionRanresLeftCont(
				ranresRule, inclRule);
		Assert.assertEquals(composition.getConsequent(), composed);
	}

	@Test
	public void compositionRanresRightCont() {
		final ITypeEnvironmentBuilder typeEnv = mTypeEnvironment("g=ℤ↔ℤ; B=ℙ(ℤ); x=ℤ×ℤ");
		final String memb = "x∈g▷A";
		final String ranresEQ = "f⊆g▷A";
		final String ranres = "f⊂g▷A";
		final String subseteq = "A⊆B";
		final String subset = "A⊂B";
		applyCompositionRanresRightCont(typeEnv, ranresEQ, subseteq, "f⊆g▷B");
		applyCompositionRanresRightCont(typeEnv, ranres, subseteq, "f⊂g▷B");
		applyCompositionRanresRightCont(typeEnv, memb, subseteq, "x∈g▷B");
		applyCompositionRanresRightCont(typeEnv, ranresEQ, subset, "f⊆g▷B");
		applyCompositionRanresRightCont(typeEnv, ranres, subset, "f⊂g▷B");
		applyCompositionRanresRightCont(typeEnv, memb, subset, "x∈g▷B");
	}

	private void applyCompositionRanresRightCont(
			final ITypeEnvironmentBuilder typeEnv, String ranresStr, String inclStr,
			String composedStr) {
		final Predicate incl = genPred(typeEnv, inclStr);
		final Predicate ranres = genPred(typeEnv, ranresStr);
		final Predicate composed = genPred(typeEnv, composedStr);
		final RelationalPredicate rIncl = (RelationalPredicate) incl;
		final RelationalPredicate rRanres = (RelationalPredicate) ranres;
		final Rule.Hypothesis<RelationalPredicate> inclRule = new Rule.Hypothesis<RelationalPredicate>(
				rIncl, ff);
		final Rule.Hypothesis<RelationalPredicate> ranresRule = new Rule.Hypothesis<RelationalPredicate>(
				rRanres, ff);
		final Rule.CompositionRanresRightCont composition = new Rule.CompositionRanresRightCont(
				ranresRule, inclRule);
		Assert.assertEquals(composition.getConsequent(), composed);
	}

	@Test
	public void compositionDomresLeftIncl() {
		final ITypeEnvironmentBuilder typeEnv = mTypeEnvironment("f=ℤ↔ℤ; B=ℙ(ℤ)");
		final String domresEQ = "B◁f⊆g";
		final String domres = "B◁f⊂g";
		final String subseteq = "A⊆B";
		final String subset = "A⊂B";
		applyCompositionDomresLeftInlc(typeEnv, domresEQ, subseteq, "A◁f⊆g");
		applyCompositionDomresLeftInlc(typeEnv, domres, subseteq, "A◁f⊂g");
		applyCompositionDomresLeftInlc(typeEnv, domresEQ, subset, "A◁f⊆g");
		applyCompositionDomresLeftInlc(typeEnv, domres, subset, "A◁f⊂g");
	}

	private void applyCompositionDomresLeftInlc(final ITypeEnvironmentBuilder typeEnv,
			String domresStr, String inclStr, String composedStr) {
		final Predicate incl = genPred(typeEnv, inclStr);
		final Predicate domres = genPred(typeEnv, domresStr);
		final Predicate composed = genPred(typeEnv, composedStr);
		final RelationalPredicate rIncl = (RelationalPredicate) incl;
		final RelationalPredicate rDomres = (RelationalPredicate) domres;
		final Rule.Hypothesis<RelationalPredicate> inclRule = new Rule.Hypothesis<RelationalPredicate>(
				rIncl, ff);
		final Rule.Hypothesis<RelationalPredicate> domresRule = new Rule.Hypothesis<RelationalPredicate>(
				rDomres, ff);
		final Rule.CompositionDomresLeftIncl composition = new Rule.CompositionDomresLeftIncl(
				inclRule, domresRule);
		Assert.assertEquals(composition.getConsequent(), composed);
	}

	@Test
	public void compositionDomresRightIncl() {
		final ITypeEnvironmentBuilder typeEnv = mTypeEnvironment("f=ℤ↔ℤ");
		final String domresEQ = "B◁f⊆g";
		final String domres = "B◁f⊂g";
		final String subseteq = "e⊆f";
		final String subset = "e⊂f";
		applyCompositionDomresRightIncl(typeEnv, domresEQ, subseteq, "B◁e⊆g");
		applyCompositionDomresRightIncl(typeEnv, domres, subseteq, "B◁e⊂g");
		applyCompositionDomresRightIncl(typeEnv, domresEQ, subset, "B◁e⊆g");
		applyCompositionDomresRightIncl(typeEnv, domres, subset, "B◁e⊂g");
	}

	private void applyCompositionDomresRightIncl(
			final ITypeEnvironmentBuilder typeEnv, String domresStr, String inclStr,
			String composedStr) {
		final Predicate incl = genPred(typeEnv, inclStr);
		final Predicate domres = genPred(typeEnv, domresStr);
		final Predicate composed = genPred(typeEnv, composedStr);
		final RelationalPredicate rIncl = (RelationalPredicate) incl;
		final RelationalPredicate rDomres = (RelationalPredicate) domres;
		final Rule.Hypothesis<RelationalPredicate> inclRule = new Rule.Hypothesis<RelationalPredicate>(
				rIncl, ff);
		final Rule.Hypothesis<RelationalPredicate> domresRule = new Rule.Hypothesis<RelationalPredicate>(
				rDomres, ff);
		final Rule.CompositionDomresRightIncl composition = new Rule.CompositionDomresRightIncl(
				inclRule, domresRule);
		Assert.assertEquals(composition.getConsequent(), composed);
	}

	@Test
	public void compositionDomresLeftCont() {
		final ITypeEnvironmentBuilder typeEnv = mTypeEnvironment("f=ℤ↔ℤ; B=ℙ(ℤ); x=ℤ×ℤ");
		final String memb = "x∈A◁g";
		final String domresEQ = "f⊆A◁g";
		final String domres = "f⊂A◁g";
		final String subseteq = "A⊆B";
		final String subset = "A⊂B";
		applyCompositionDomresLeftCont(typeEnv, domresEQ, subseteq, "f⊆B◁g");
		applyCompositionDomresLeftCont(typeEnv, domres, subseteq, "f⊂B◁g");
		applyCompositionDomresLeftCont(typeEnv, memb, subseteq, "x∈B◁g");
		applyCompositionDomresLeftCont(typeEnv, domresEQ, subset, "f⊆B◁g");
		applyCompositionDomresLeftCont(typeEnv, domres, subset, "f⊂B◁g");
		applyCompositionDomresLeftCont(typeEnv, memb, subset, "x∈B◁g");
	}

	private void applyCompositionDomresLeftCont(final ITypeEnvironmentBuilder typeEnv,
			String domresStr, String inclStr, String composedStr) {
		final Predicate incl = genPred(typeEnv, inclStr);
		final Predicate domres = genPred(typeEnv, domresStr);
		final Predicate composed = genPred(typeEnv, composedStr);
		final RelationalPredicate rIncl = (RelationalPredicate) incl;
		final RelationalPredicate rDomres = (RelationalPredicate) domres;
		final Rule.Hypothesis<RelationalPredicate> inclRule = new Rule.Hypothesis<RelationalPredicate>(
				rIncl, ff);
		final Rule.Hypothesis<RelationalPredicate> domresRule = new Rule.Hypothesis<RelationalPredicate>(
				rDomres, ff);
		final Rule.CompositionDomresLeftCont composition = new Rule.CompositionDomresLeftCont(
				domresRule, inclRule);
		Assert.assertEquals(composition.getConsequent(), composed);
	}

	@Test
	public void compositionDomresRightCont() {
		final ITypeEnvironmentBuilder typeEnv = mTypeEnvironment("g=ℤ↔ℤ; x=ℤ×ℤ");
		final String memb = "x∈A◁g";
		final String domresEQ = "f⊆A◁g";
		final String domres = "f⊂A◁g";
		final String subseteq = "g⊆h";
		final String subset = "g⊂h";
		applyCompositionDomresRightCont(typeEnv, domresEQ, subseteq, "f⊆A◁h");
		applyCompositionDomresRightCont(typeEnv, domres, subseteq, "f⊂A◁h");
		applyCompositionDomresRightCont(typeEnv, memb, subseteq, "x∈A◁h");
		applyCompositionDomresRightCont(typeEnv, domresEQ, subset, "f⊆A◁h");
		applyCompositionDomresRightCont(typeEnv, domres, subset, "f⊂A◁h");
		applyCompositionDomresRightCont(typeEnv, memb, subset, "x∈A◁h");
	}

	private void applyCompositionDomresRightCont(
			final ITypeEnvironmentBuilder typeEnv, String domresStr, String inclStr,
			String composedStr) {
		final Predicate incl = genPred(typeEnv, inclStr);
		final Predicate domres = genPred(typeEnv, domresStr);
		final Predicate composed = genPred(typeEnv, composedStr);
		final RelationalPredicate rIncl = (RelationalPredicate) incl;
		final RelationalPredicate rDomres = (RelationalPredicate) domres;
		final Rule.Hypothesis<RelationalPredicate> inclRule = new Rule.Hypothesis<RelationalPredicate>(
				rIncl, ff);
		final Rule.Hypothesis<RelationalPredicate> domresRule = new Rule.Hypothesis<RelationalPredicate>(
				rDomres, ff);
		final Rule.CompositionDomresRightCont composition = new Rule.CompositionDomresRightCont(
				domresRule, inclRule);
		Assert.assertEquals(composition.getConsequent(), composed);
	}

	@Test
	public void compositionRansubLeftIncl() {
		final ITypeEnvironmentBuilder typeEnv = mTypeEnvironment("f=ℤ↔ℤ");
		final String ransubEQ = "f⩥A⊆g";
		final String ransub = "f⩥A⊂g";
		final String subseteq = "e⊆f";
		final String subset = "e⊂f";
		applyCompositionRansubLeftIncl(typeEnv, ransubEQ, subseteq, "e⩥A⊆g");
		applyCompositionRansubLeftIncl(typeEnv, ransub, subseteq, "e⩥A⊂g");
		applyCompositionRansubLeftIncl(typeEnv, ransubEQ, subset, "e⩥A⊆g");
		applyCompositionRansubLeftIncl(typeEnv, ransub, subset, "e⩥A⊂g");
	}

	private void applyCompositionRansubLeftIncl(final ITypeEnvironmentBuilder typeEnv,
			String ransubStr, String inclStr, String composedStr) {
		final Predicate incl = genPred(typeEnv, inclStr);
		final Predicate ransub = genPred(typeEnv, ransubStr);
		final Predicate composed = genPred(typeEnv, composedStr);
		final RelationalPredicate rIncl = (RelationalPredicate) incl;
		final RelationalPredicate rRansub = (RelationalPredicate) ransub;
		final Rule.Hypothesis<RelationalPredicate> inclRule = new Rule.Hypothesis<RelationalPredicate>(
				rIncl, ff);
		final Rule.Hypothesis<RelationalPredicate> ransubRule = new Rule.Hypothesis<RelationalPredicate>(
				rRansub, ff);
		final Rule.CompositionRansubLeftIncl composition = new Rule.CompositionRansubLeftIncl(
				inclRule, ransubRule);
		Assert.assertEquals(composition.getConsequent(), composed);
	}

	@Test
	public void compositionRansubRightIncl() {
		final ITypeEnvironmentBuilder typeEnv = mTypeEnvironment("f=ℤ↔ℤ; B=ℙ(ℤ)");
		final String ransubEQ = "f⩥A⊆g";
		final String ransub = "f⩥A⊂g";
		final String subseteq = "A⊆B";
		final String subset = "A⊂B";
		applyCompositionRansubRightIncl(typeEnv, ransubEQ, subseteq, "f⩥B⊆g");
		applyCompositionRansubRightIncl(typeEnv, ransub, subseteq, "f⩥B⊂g");
		applyCompositionRansubRightIncl(typeEnv, ransubEQ, subset, "f⩥B⊆g");
		applyCompositionRansubRightIncl(typeEnv, ransub, subset, "f⩥B⊂g");
	}

	private void applyCompositionRansubRightIncl(
			final ITypeEnvironmentBuilder typeEnv, String ransubStr, String inclStr,
			String composedStr) {
		final Predicate incl = genPred(typeEnv, inclStr);
		final Predicate ransub = genPred(typeEnv, ransubStr);
		final Predicate composed = genPred(typeEnv, composedStr);
		final RelationalPredicate rIncl = (RelationalPredicate) incl;
		final RelationalPredicate rRansub = (RelationalPredicate) ransub;
		final Rule.Hypothesis<RelationalPredicate> inclRule = new Rule.Hypothesis<RelationalPredicate>(
				rIncl, ff);
		final Rule.Hypothesis<RelationalPredicate> ransubRule = new Rule.Hypothesis<RelationalPredicate>(
				rRansub, ff);
		final Rule.CompositionRansubRightIncl composition = new Rule.CompositionRansubRightIncl(
				ransubRule, inclRule);
		Assert.assertEquals(composition.getConsequent(), composed);
	}

	@Test
	public void compositionRansubLeftCont() {
		final ITypeEnvironmentBuilder typeEnv = mTypeEnvironment("g=ℤ↔ℤ; x=ℤ×ℤ");
		final String memb = "x∈g⩥A";
		final String ransubEQ = "f⊆g⩥A";
		final String ransub = "f⊂g⩥A";
		final String subseteq = "g⊆h";
		final String subset = "g⊂h";
		applyCompositionRansubLeftCont(typeEnv, ransubEQ, subseteq, "f⊆h⩥A");
		applyCompositionRansubLeftCont(typeEnv, ransub, subseteq, "f⊂h⩥A");
		applyCompositionRansubLeftCont(typeEnv, memb, subseteq, "x∈h⩥A");
		applyCompositionRansubLeftCont(typeEnv, ransubEQ, subset, "f⊆h⩥A");
		applyCompositionRansubLeftCont(typeEnv, ransub, subset, "f⊂h⩥A");
		applyCompositionRansubLeftCont(typeEnv, memb, subset, "x∈h⩥A");
	}

	private void applyCompositionRansubLeftCont(final ITypeEnvironmentBuilder typeEnv,
			String ransubStr, String inclStr, String composedStr) {
		final Predicate incl = genPred(typeEnv, inclStr);
		final Predicate ransub = genPred(typeEnv, ransubStr);
		final Predicate composed = genPred(typeEnv, composedStr);
		final RelationalPredicate rIncl = (RelationalPredicate) incl;
		final RelationalPredicate rRansub = (RelationalPredicate) ransub;
		final Rule.Hypothesis<RelationalPredicate> inclRule = new Rule.Hypothesis<RelationalPredicate>(
				rIncl, ff);
		final Rule.Hypothesis<RelationalPredicate> ransubRule = new Rule.Hypothesis<RelationalPredicate>(
				rRansub, ff);
		final Rule.CompositionRansubLeftCont composition = new Rule.CompositionRansubLeftCont(
				ransubRule, inclRule);
		Assert.assertEquals(composition.getConsequent(), composed);
	}

	@Test
	public void compositionRansubRightCont() {
		final ITypeEnvironmentBuilder typeEnv = mTypeEnvironment("g=ℤ↔ℤ; B=ℙ(ℤ); x=ℤ×ℤ");
		final String memb = "x∈g⩥B";
		final String ransubEQ = "f⊆g⩥B";
		final String ransub = "f⊂g⩥B";
		final String subseteq = "A⊆B";
		final String subset = "A⊂B";
		applyCompositionRansubRightCont(typeEnv, ransubEQ, subseteq, "f⊆g⩥A");
		applyCompositionRansubRightCont(typeEnv, ransub, subseteq, "f⊂g⩥A");
		applyCompositionRansubRightCont(typeEnv, memb, subseteq, "x∈g⩥A");
		applyCompositionRansubRightCont(typeEnv, ransubEQ, subset, "f⊆g⩥A");
		applyCompositionRansubRightCont(typeEnv, ransub, subset, "f⊂g⩥A");
		applyCompositionRansubRightCont(typeEnv, memb, subset, "x∈g⩥A");
	}

	private void applyCompositionRansubRightCont(
			final ITypeEnvironmentBuilder typeEnv, String ransubStr, String inclStr,
			String composedStr) {
		final Predicate incl = genPred(typeEnv, inclStr);
		final Predicate ransub = genPred(typeEnv, ransubStr);
		final Predicate composed = genPred(typeEnv, composedStr);
		final RelationalPredicate rIncl = (RelationalPredicate) incl;
		final RelationalPredicate rRansub = (RelationalPredicate) ransub;
		final Rule.Hypothesis<RelationalPredicate> inclRule = new Rule.Hypothesis<RelationalPredicate>(
				rIncl, ff);
		final Rule.Hypothesis<RelationalPredicate> ransubRule = new Rule.Hypothesis<RelationalPredicate>(
				rRansub, ff);
		final Rule.CompositionRansubRightCont composition = new Rule.CompositionRansubRightCont(
				ransubRule, inclRule);
		Assert.assertEquals(composition.getConsequent(), composed);
	}

	@Test
	public void compositionDomsubLeftIncl() {
		final ITypeEnvironmentBuilder typeEnv = mTypeEnvironment("f=ℤ↔ℤ; A=ℙ(ℤ)");
		final String domsubEQ = "A⩤f⊆g";
		final String domsub = "A⩤f⊂g";
		final String subseteq = "A⊆B";
		final String subset = "A⊂B";
		applyCompositionDomsubLeftIncl(typeEnv, domsubEQ, subseteq, "B⩤f⊆g");
		applyCompositionDomsubLeftIncl(typeEnv, domsub, subseteq, "B⩤f⊂g");
		applyCompositionDomsubLeftIncl(typeEnv, domsubEQ, subset, "B⩤f⊆g");
		applyCompositionDomsubLeftIncl(typeEnv, domsub, subset, "B⩤f⊂g");
	}

	private void applyCompositionDomsubLeftIncl(final ITypeEnvironmentBuilder typeEnv,
			String domsubStr, String inclStr, String composedStr) {
		final Predicate incl = genPred(typeEnv, inclStr);
		final Predicate domsub = genPred(typeEnv, domsubStr);
		final Predicate composed = genPred(typeEnv, composedStr);
		final RelationalPredicate rIncl = (RelationalPredicate) incl;
		final RelationalPredicate rDomsub = (RelationalPredicate) domsub;
		final Rule.Hypothesis<RelationalPredicate> inclRule = new Rule.Hypothesis<RelationalPredicate>(
				rIncl, ff);
		final Rule.Hypothesis<RelationalPredicate> domsubRule = new Rule.Hypothesis<RelationalPredicate>(
				rDomsub, ff);
		final Rule.CompositionDomsubLeftIncl composition = new Rule.CompositionDomsubLeftIncl(
				domsubRule, inclRule);
		Assert.assertEquals(composition.getConsequent(), composed);
	}

	@Test
	public void compositionDomsubRightIncl() {
		final ITypeEnvironmentBuilder typeEnv = mTypeEnvironment("f=ℤ↔ℤ");
		final String domsubEQ = "A⩤f⊆g";
		final String domsub = "A⩤f⊂g";
		final String subseteq = "e⊆f";
		final String subset = "e⊂f";
		applyCompositionDomsubRightIncl(typeEnv, domsubEQ, subseteq, "A⩤e⊆g");
		applyCompositionDomsubRightIncl(typeEnv, domsub, subseteq, "A⩤e⊂g");
		applyCompositionDomsubRightIncl(typeEnv, domsubEQ, subset, "A⩤e⊆g");
		applyCompositionDomsubRightIncl(typeEnv, domsub, subset, "A⩤e⊂g");
	}

	private void applyCompositionDomsubRightIncl(
			final ITypeEnvironmentBuilder typeEnv, String domsubStr, String inclStr,
			String composedStr) {
		final Predicate incl = genPred(typeEnv, inclStr);
		final Predicate domsub = genPred(typeEnv, domsubStr);
		final Predicate composed = genPred(typeEnv, composedStr);
		final RelationalPredicate rIncl = (RelationalPredicate) incl;
		final RelationalPredicate rDomsub = (RelationalPredicate) domsub;
		final Rule.Hypothesis<RelationalPredicate> inclRule = new Rule.Hypothesis<RelationalPredicate>(
				rIncl, ff);
		final Rule.Hypothesis<RelationalPredicate> domsubRule = new Rule.Hypothesis<RelationalPredicate>(
				rDomsub, ff);
		final Rule.CompositionDomsubRightIncl composition = new Rule.CompositionDomsubRightIncl(
				inclRule, domsubRule);
		Assert.assertEquals(composition.getConsequent(), composed);
	}

	@Test
	public void compositionDomsubLeftCont() {
		final ITypeEnvironmentBuilder typeEnv = mTypeEnvironment("f=ℤ↔ℤ; A=ℙ(ℤ); x=ℤ×ℤ");
		final String memb = "x∈B⩤g";
		final String domsubEQ = "f⊆B⩤g";
		final String domsub = "f⊂B⩤g";
		final String subseteq = "A⊆B";
		final String subset = "A⊂B";
		applyCompositionDomsubLeftCont(typeEnv, domsubEQ, subseteq, "f⊆A⩤g");
		applyCompositionDomsubLeftCont(typeEnv, domsub, subseteq, "f⊂A⩤g");
		applyCompositionDomsubLeftCont(typeEnv, memb, subseteq, "x∈A⩤g");
		applyCompositionDomsubLeftCont(typeEnv, domsubEQ, subset, "f⊆A⩤g");
		applyCompositionDomsubLeftCont(typeEnv, domsub, subset, "f⊂A⩤g");
		applyCompositionDomsubLeftCont(typeEnv, memb, subset, "x∈A⩤g");
	}

	private void applyCompositionDomsubLeftCont(final ITypeEnvironmentBuilder typeEnv,
			String domsubStr, String inclStr, String composedStr) {
		final Predicate incl = genPred(typeEnv, inclStr);
		final Predicate domsub = genPred(typeEnv, domsubStr);
		final Predicate composed = genPred(typeEnv, composedStr);
		final RelationalPredicate rIncl = (RelationalPredicate) incl;
		final RelationalPredicate rDomsub = (RelationalPredicate) domsub;
		final Rule.Hypothesis<RelationalPredicate> inclRule = new Rule.Hypothesis<RelationalPredicate>(
				rIncl, ff);
		final Rule.Hypothesis<RelationalPredicate> domsubRule = new Rule.Hypothesis<RelationalPredicate>(
				rDomsub, ff);
		final Rule.CompositionDomsubLeftCont composition = new Rule.CompositionDomsubLeftCont(
				inclRule, domsubRule);
		Assert.assertEquals(composition.getConsequent(), composed);
	}

	@Test
	public void compositionDomsubRightCont() {
		final ITypeEnvironmentBuilder typeEnv = mTypeEnvironment("g=ℤ↔ℤ; x=ℤ×ℤ");
		final String memb = "x∈A⩤g";
		final String domsubEQ = "f⊆A⩤g";
		final String domsub = "f⊂A⩤g";
		final String subseteq = "g⊆h";
		final String subset = "g⊂h";
		applyCompositionDomsubRightCont(typeEnv, domsubEQ, subseteq, "f⊆A⩤h");
		applyCompositionDomsubRightCont(typeEnv, domsub, subseteq, "f⊂A⩤h");
		applyCompositionDomsubRightCont(typeEnv, memb, subseteq, "x∈A⩤h");
		applyCompositionDomsubRightCont(typeEnv, domsubEQ, subset, "f⊆A⩤h");
		applyCompositionDomsubRightCont(typeEnv, domsub, subset, "f⊂A⩤h");
		applyCompositionDomsubRightCont(typeEnv, memb, subset, "x∈A⩤h");
	}

	private void applyCompositionDomsubRightCont(
			final ITypeEnvironmentBuilder typeEnv, String domsubStr, String inclStr,
			String composedStr) {
		final Predicate incl = genPred(typeEnv, inclStr);
		final Predicate domsub = genPred(typeEnv, domsubStr);
		final Predicate composed = genPred(typeEnv, composedStr);
		final RelationalPredicate rIncl = (RelationalPredicate) incl;
		final RelationalPredicate rDomsub = (RelationalPredicate) domsub;
		final Rule.Hypothesis<RelationalPredicate> inclRule = new Rule.Hypothesis<RelationalPredicate>(
				rIncl, ff);
		final Rule.Hypothesis<RelationalPredicate> domsubRule = new Rule.Hypothesis<RelationalPredicate>(
				rDomsub, ff);
		final Rule.CompositionDomsubRightCont composition = new Rule.CompositionDomsubRightCont(
				domsubRule, inclRule);
		Assert.assertEquals(composition.getConsequent(), composed);
	}

}
