package org.eventb.core.prover;

import java.util.HashMap;
import java.util.Map;

import org.eventb.core.prover.reasoners.AllD;
import org.eventb.core.prover.reasoners.AllI;
import org.eventb.core.prover.reasoners.ConjE;
import org.eventb.core.prover.reasoners.ConjI;
import org.eventb.core.prover.reasoners.Contr;
import org.eventb.core.prover.reasoners.Contradiction;
import org.eventb.core.prover.reasoners.Cut;
import org.eventb.core.prover.reasoners.DisjE;
import org.eventb.core.prover.reasoners.DoCase;
import org.eventb.core.prover.reasoners.Eq;
import org.eventb.core.prover.reasoners.ExE;
import org.eventb.core.prover.reasoners.ExI;
import org.eventb.core.prover.reasoners.ExternalML;
import org.eventb.core.prover.reasoners.ExternalPP;
import org.eventb.core.prover.reasoners.Hyp;
import org.eventb.core.prover.reasoners.ImpE;
import org.eventb.core.prover.reasoners.ImpI;
import org.eventb.core.prover.reasoners.MngHyp;
import org.eventb.core.prover.reasoners.Review;
import org.eventb.core.prover.reasoners.RewriteGoal;
import org.eventb.core.prover.reasoners.RewriteHyp;
import org.eventb.core.prover.reasoners.Tautology;

public class ReasonerRegistry {
	
	private static Map<String,Reasoner> registry = new HashMap<String,Reasoner>();
	
	// Static initialization block for registry 
	static {
		Reasoner[] installedReasoners =	
		{
				// Add new reasoners here.
				new Hyp(),
				new Tautology(),
				new Contradiction(),
				new ConjI(),
				new Cut(),
				new DoCase(),
				new Contr(),
				new ConjE(),
				new DisjE(),
				new ImpI(),
				new ImpE(),
				new AllI(),
				new AllD(),
				new ExE(),
				new ExI(),
				new RewriteGoal(),
				new Eq(),
				new RewriteHyp(),
				new ExternalPP(),
				new ExternalML(),
				new Review(),
				new MngHyp()
		};
		
		for (Reasoner reasoner : installedReasoners)
		{
			// no duplicate ids
			assert ! registry.containsKey(reasoner.getReasonerID());
			registry.put(reasoner.getReasonerID(),reasoner);
			// System.out.println("Registered "+reasoner.getReasonerID()+" as "+reasoner);
		}
		
	}
	
	public static Reasoner getReasoner(String reasonerID){
		return registry.get(reasonerID);
	}
}
