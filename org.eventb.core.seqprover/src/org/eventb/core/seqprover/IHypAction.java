/**
 * 
 */
package org.eventb.core.seqprover;

import java.util.Collection;

import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.Predicate;

/**
 * @author fmehta
 *
 */
public interface IHypAction {
	
	String getActionType();
	
	public interface ISelectionHypAction extends IHypAction{
		
		// Possible action types for implementors
		static String SELECT_ACTION_TYPE = "SELECT";
		static String DESELECT_ACTION_TYPE = "DESELECT";
		static String HIDE_ACTION_TYPE = "HIDE";
		static String SHOW_ACTION_TYPE = "SHOW";
		
		Collection<Predicate> getHyps();
	
	}
		
	public interface IForwardInfHypAction extends IHypAction{
		
		String ACTION_TYPE = "FORWARD_INF";
		Collection<Predicate> getHyps();
		FreeIdentifier[] getAddedFreeIdents();
		Collection<Predicate> getInferredHyps();
	
	}

}
