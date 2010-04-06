package org.eventb.core.pm;

import org.eventb.core.seqprover.ITactic;
import org.eventb.core.seqprover.autoTacticPreference.IAutoTacticPreference;

/**
 * @author htson
 * <p>
 * @deprecated use {@link IAutoTacticPreference} instead.
 * @since 1.0
 */
@Deprecated
public interface IProvingMode {
	
	/**
	 * Check if the post-tactic is enable.
	 * <p>
	 * 
	 * @return <code>true</code> if the post-tactic is enable,
	 *         <code>false</code> otherwise.
	 * @deprecated use ({@link #getPostTactic()} == null) instead.
	 */
	@Deprecated
	public boolean isExpertMode();

	/**
	 * Setting the mode for proving (enable/disable post-tactic).
	 * <p>
	 * 
	 * @param mode
	 *            <code>true</code> to enable the post-tactic,
	 *            <code>false</code> to disable the post-tactic.
	 * @deprecated used {@link #setPostTacticEnable(boolean)} instead.
	 */
	@Deprecated
	public void setExpertMode(boolean mode);

	/**
	 * Setting the mode for proving (enable/disable post-tactic)
	 * 
	 * @param enable
	 *            <code>true</code> to enable the post-tactic,
	 *            <code>false</code> to disable the post-tactic
	 */
	public void setPostTacticEnable(boolean enable);

	public ITactic getPostTactic();

	public void setPostTactics(String ... tacticIDs);
}