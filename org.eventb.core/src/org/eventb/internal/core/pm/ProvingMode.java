package org.eventb.internal.core.pm;

import static org.eventb.core.seqprover.tactics.BasicTactics.loopOnAllPending;

import org.eventb.core.EventBPlugin;
import org.eventb.core.pm.IPostTacticRegistry;
import org.eventb.core.pm.IProvingMode;
import org.eventb.core.seqprover.IAutoTacticRegistry;
import org.eventb.core.seqprover.ITactic;
import org.eventb.core.seqprover.SequentProver;

@Deprecated
public class ProvingMode implements IProvingMode {

	private boolean postTacticEnabled = false;

	private ITactic postTactic;
	
	public ProvingMode() {
		IPostTacticRegistry postTacticRegistry = EventBPlugin.getDefault()
				.getPostTacticRegistry();
		setPostTactics(postTacticRegistry.getTacticIDs());
	}
	
	@Override
	@Deprecated
	public boolean isExpertMode() {
		return (getPostTactic() == null);
	}

	@Override
	@Deprecated
	public void setExpertMode(boolean mode) {
		setPostTacticEnable(mode);
	}

	@Override
	public void setPostTacticEnable(boolean enable) {
		postTacticEnabled = enable;
	}

	@Override
	public ITactic getPostTactic() {
		if (postTacticEnabled)
			return postTactic;
		else
			return null;
	}

	private ITactic composeTactics(ITactic ... list) {
		return loopOnAllPending(list);
	}

	@Override
	public void setPostTactics(String ... tacticIDs) {
		IAutoTacticRegistry tacticRegistry = SequentProver.getAutoTacticRegistry();
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
