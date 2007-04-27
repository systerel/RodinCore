package org.eventb.internal.core.pm;

import static org.eventb.core.seqprover.tactics.BasicTactics.repeat;
import static org.eventb.core.seqprover.tactics.BasicTactics.composeOnAllPending;

import java.util.ArrayList;

import org.eventb.core.pm.IProvingMode;
import org.eventb.core.seqprover.ITactic;
import org.eventb.core.seqprover.ITacticRegistry;
import org.eventb.core.seqprover.SequentProver;

public class ProvingMode implements IProvingMode {

	private boolean expertMode = false;

	private ITactic postTactic;
	
	private ITactic [] tactics;
	
	public ProvingMode() {
		ITacticRegistry tacticRegistry = SequentProver.getTacticRegistry();
		String[] registeredIDs = tacticRegistry.getRegisteredIDs();
		int length = registeredIDs.length;
		tactics = new ITactic[length];
		for (int i = 0; i < length; ++i) {
			tactics[i] = tacticRegistry.getTacticInstance(registeredIDs[i]);
		}
		postTactic = composeTactics(tactics);
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

	public void setPostTactics(ArrayList<String> tacticIDs) {
		ITacticRegistry tacticRegistry = SequentProver.getTacticRegistry();
		int size = tacticIDs.size();
		tactics = new ITactic[size];
		int i = 0;
		for (String tacticID : tacticIDs) {
			tactics[i] = tacticRegistry.getTacticInstance(tacticID);
			++i;
		}
		postTactic = composeTactics(tactics);
	}

}
