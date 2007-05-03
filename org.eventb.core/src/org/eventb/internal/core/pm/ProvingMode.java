package org.eventb.internal.core.pm;

import static org.eventb.core.seqprover.tactics.BasicTactics.composeOnAllPending;
import static org.eventb.core.seqprover.tactics.BasicTactics.repeat;

import org.eventb.core.EventBPlugin;
import org.eventb.core.pm.IPostTacticRegistry;
import org.eventb.core.pm.IProvingMode;
import org.eventb.core.seqprover.ITactic;
import org.eventb.core.seqprover.ITacticRegistry;
import org.eventb.core.seqprover.SequentProver;

public class ProvingMode implements IProvingMode {

	private boolean expertMode = false;

	private ITactic postTactic;
	
	public ProvingMode() {
		IPostTacticRegistry postTacticRegistry = EventBPlugin.getDefault()
				.getPostTacticRegistry();
		setPostTactics(postTacticRegistry.getTacticIDs());
	}
	
	public boolean isExpertMode() {
		return expertMode;
	}

	public void setExpertMode(boolean mode) {
		expertMode = mode;
	}

	public ITactic getPostTactic() {
		return postTactic;
	}

	private ITactic composeTactics(ITactic ... list) {
		return repeat(composeOnAllPending(list));
	}

	public void setPostTactics(String ... tacticIDs) {
		ITacticRegistry tacticRegistry = SequentProver.getTacticRegistry();
		int size = tacticIDs.length;
		ITactic [] tactics = new ITactic[size];
		int i = 0;
		for (String tacticID : tacticIDs) {
			tactics[i] = tacticRegistry.getTacticInstance(tacticID);
			++i;
		}
		postTactic = composeTactics(tactics);
	}

}
