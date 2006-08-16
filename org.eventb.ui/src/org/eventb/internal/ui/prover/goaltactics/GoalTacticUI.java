package org.eventb.internal.ui.prover.goaltactics;

import org.eclipse.swt.graphics.Image;
import org.eventb.core.seqprover.IProofTreeNode;
import org.eventb.core.seqprover.tactics.ITactic;
import org.eventb.ui.prover.IGoalTactic;

public class GoalTacticUI {

	private String ID;

	private Image image;

	private IGoalTactic tactic;

	private String hint;

	public GoalTacticUI(String ID, Image image, IGoalTactic tactic, String hint) {
		this.ID = ID;
		this.image = image;
		this.tactic = tactic;
		this.hint = hint;
	}

	public boolean isApplicable(IProofTreeNode node) {
		return tactic.isApplicable(node);
	}

	public ITactic getTactic(IProofTreeNode node, String[] inputs) {
		return tactic.getTactic(node, inputs);
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
