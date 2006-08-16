package org.eventb.internal.ui.prover.hypothesisTactics;

import org.eclipse.swt.graphics.Image;
import org.eventb.core.seqprover.IProofTreeNode;
import org.eventb.core.seqprover.sequent.Hypothesis;
import org.eventb.core.seqprover.tactics.ITactic;
import org.eventb.ui.prover.IHypothesisTactic;

public class HypothesisTacticUI {

	private String ID;

	private Image image;

	private IHypothesisTactic tactic;

	private String hint;

	public HypothesisTacticUI(String ID, Image image, IHypothesisTactic tactic,
			String hint) {
		this.ID = ID;
		this.image = image;
		this.tactic = tactic;
		this.hint = hint;
	}

	public boolean isApplicable(IProofTreeNode node, Hypothesis hyp) {
		return tactic.isApplicable(node, hyp);
	}

	public ITactic getTactic(IProofTreeNode node, Hypothesis hyp,
			String[] inputs) {
		return tactic.getTactic(node, hyp, inputs);
	}

	public Image getImage() {
		return image;
	}

	public String getHint() {
		return hint;
	}
	
	public String getID() {
		return ID;
	}
	
}
