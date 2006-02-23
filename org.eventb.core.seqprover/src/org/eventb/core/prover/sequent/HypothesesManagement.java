package org.eventb.core.prover.sequent;

import java.util.Collections;
import java.util.List;
import java.util.Set;



public class HypothesesManagement {

	public enum ActionType {SELECT, DESELECT, HIDE, SHOW};

	public static ActionType fromString(String str)
	{
		if (str.compareToIgnoreCase("SELECT") == 0) return ActionType.SELECT;
		if (str.compareToIgnoreCase("DESELECT") == 0) return ActionType.DESELECT;
		if (str.compareToIgnoreCase("HIDE") == 0) return ActionType.HIDE;
		if (str.compareToIgnoreCase("SHOW") == 0) return ActionType.SHOW;
		return null;
	}
	
	
	public static class Action {
		
		private final ActionType type;
		private final Set<Hypothesis> hyps;
		
		public Action(ActionType type,Set<Hypothesis> hyps){
			this.type = type;
			this.hyps = hyps;
		}
		
		public Action(ActionType type,Hypothesis hyp){
			this.type = type;
			this.hyps = Collections.singleton(hyp);
		}
		
		
		
		public IProverSequent perform(IProverSequent S){
			switch(this.type){
			case SELECT: return S.selectHypotheses(this.hyps);
			case DESELECT: return S.deselectHypotheses(this.hyps);
			case HIDE: return S.hideHypotheses(this.hyps);
			case SHOW: return S.showHypotheses(this.hyps);
			default: throw new AssertionError(this);
			}
		}

		public Set<Hypothesis> getHyps() {
			return hyps;
		}

		public ActionType getType() {
			return type;
		}
		
		public String toString(){
			return type.toString();
		}
		
	}
	
	public static IProverSequent perform(List<Action> actions,IProverSequent S){
		if (actions == null) return S;
		IProverSequent result = S;
		for(Action action : actions){
			result = action.perform(result);
		}
		return result;
	}
}
