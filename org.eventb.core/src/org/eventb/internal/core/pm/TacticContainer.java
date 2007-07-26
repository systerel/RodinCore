package org.eventb.internal.core.pm;

import static org.eventb.core.seqprover.tactics.BasicTactics.loopOnAllPending;

import java.util.ArrayList;

import org.eventb.core.ITacticContainer;
import org.eventb.core.ITacticContainerRegistry;
import org.eventb.core.seqprover.ITactic;
import org.eventb.core.seqprover.ITacticRegistry;
import org.eventb.core.seqprover.SequentProver;

public class TacticContainer implements ITacticContainer {

	private boolean enabled = false;

	private ITactic tactic;
	
	ITacticContainerRegistry registry;
	
	public TacticContainer(ITacticContainerRegistry registry) {
		this.registry = registry;
		setTactics(registry.getDefaultTacticIDs());
	}
	
	/* (non-Javadoc)
	 * @see org.eventb.core.ITacticContainer#setEnable(boolean)
	 */
	public void setEnable(boolean enable) {
		enabled = enable;
	}

	/* (non-Javadoc)
	 * @see org.eventb.core.ITacticContainer#isEnable()
	 */
	public boolean isEnable() {
		return enabled;
	}
	
	/* (non-Javadoc)
	 * @see org.eventb.core.ITacticContainer#getTactic()
	 */
	public ITactic getTactic() {
		return tactic;
	}

	protected ITactic composeTactics(ArrayList<ITactic> list) {
		return loopOnAllPending(list
				.toArray(new ITactic[list.size()]));
	}

	/* (non-Javadoc)
	 * @see org.eventb.core.ITacticContainer#setTactics(java.lang.String[])
	 */
	public void setTactics(String ... tacticIDs) {
		ITacticRegistry tacticRegistry = SequentProver.getTacticRegistry();
		ArrayList<ITactic> tactics = new ArrayList<ITactic>();
		for (String tacticID : tacticIDs) {
			if (tacticRegistry.isRegistered(tacticID)) {
				tactics.add(tacticRegistry.getTacticInstance(tacticID));
			}
		}
		tactic = composeTactics(tactics);
	}

	public ITacticContainerRegistry getTacticContainerRegistry() {
		return registry;
	}

}
