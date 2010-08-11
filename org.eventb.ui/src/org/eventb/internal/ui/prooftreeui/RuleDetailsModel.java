/*******************************************************************************
 * Copyright (c) 2010 Systerel and others. 
 *  
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - Initial API and implementation
 ******************************************************************************/
package org.eventb.internal.ui.prooftreeui;

import java.util.ArrayList;
import java.util.List;

import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.seqprover.IProofRule;

/**
 * Classes used to represent the rule displayed by the Rule Details view. It
 * stores the informations about the rules as a structure providing strings for
 * each detail to be shown.
 */
public class RuleDetailsModel {

	private final IProofRule rule;
	private RuleModelRoot root;

	public RuleDetailsModel(IProofRule rule, RuleModelRoot root) {
		this.rule = rule;
		this.root = root;
	}

	public RuleModelRoot getModelRoot() {
		return root;
	}

	public IProofRule getRule() {
		return rule;
	}

	public static abstract class RuleDetailsElement {

		private final String name;
		private List<RuleDetailsElement> children;
		private List<String> sequentH;
		private List<String> sequentG;

		public RuleDetailsElement(String name) {
			this.name = name;
		}

		public String getName() {
			return name;
		}

		public void setH(List<String> hypotheses) {
			sequentH = hypotheses;
		}

		public void setG(List<String> goals) {
			sequentG = goals;
		}

		public List<String> getH() {
			return sequentH;
		}

		public List<String> getG() {
			return sequentG;
		}

		public void setChildren(List<RuleDetailsElement> children) {
			this.children = children;
		}

		public List<RuleDetailsElement> getChildren() {
			return children;
		}
	}

	public static class RuleModelRoot extends RuleDetailsElement {

		private final int confidence;

		public RuleModelRoot(String name, int confidence) {
			super(name);
			this.confidence = confidence;
		}

		public String getConfidence() {
			return Integer.toString(confidence);
		}

	}

	public static class RuleModelAntecedent extends RuleDetailsElement {

		private List<String> addedIdentifiers;

		public RuleModelAntecedent(String name) {
			super(name);
		}

		public void setAddedIdentifiers(FreeIdentifier[] identifiers, int style) {
			final List<String> result = new ArrayList<String>();
			for (FreeIdentifier freeIdentifier : identifiers) {
				result.add(freeIdentifier.toString());
			}
			this.addedIdentifiers = result;
		}

		public void setAddedIdentifiers(List<String> addedIdentifiers) {
			this.addedIdentifiers = addedIdentifiers;
		}

		public List<String> getAddedIdentifiers() {
			return addedIdentifiers;
		}

	}

	public static class RuleModelAction extends RuleDetailsElement {

		public RuleModelAction(String name) {
			super(name);
		}

	}

}
